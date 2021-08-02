package mobi.chouette.exchange.transfer.exporter;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.BlockDAO;
import mobi.chouette.dao.DeadRunDAO;
import mobi.chouette.dao.InterchangeDAO;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.dao.RouteSectionDAO;
import mobi.chouette.dao.ScheduledStopPointDAO;
import mobi.chouette.dao.TimetableDAO;
import mobi.chouette.dao.VehicleJourneyDAO;
import mobi.chouette.exchange.ProgressionCommand;
import mobi.chouette.exchange.importer.CleanRepositoryCommand;
import mobi.chouette.exchange.netexprofile.importer.UpdateReferentialLastUpdateTimestampCommand;
import mobi.chouette.exchange.transfer.Constant;
import mobi.chouette.model.Block;
import mobi.chouette.model.DeadRun;
import mobi.chouette.model.Interchange;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.RouteSection;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.util.Referential;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j
@Stateless(name = TransferExportDataWriter.COMMAND)
public class TransferExportDataWriter implements Command, Constant {

	private static final int FLUSH_SIZE = 50;

	public static final String COMMAND = "TransferExporterDataWriter";

	@EJB
	private LineDAO lineDAO;

	@EJB
	private BlockDAO blockDAO;

	@EJB
	private VehicleJourneyDAO vehicleJourneyDAO;

	@EJB
	private DeadRunDAO deadRunDAO;

	@EJB
	private TimetableDAO timetableDAO;

	@EJB
	private RouteSectionDAO routeSectionDAO;

	@EJB
	private InterchangeDAO interchangeDAO;

	@EJB
	private ScheduledStopPointDAO scheduledStopPointDAO;

	@PersistenceContext(unitName = "referential")
	private EntityManager em;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@TransactionTimeout(value = 2, unit = TimeUnit.HOURS)
	public boolean execute(Context context) throws Exception {
		if (!em.isJoinedToTransaction()) {
			throw new RuntimeException("No transaction");
		}

		List<Block> blocksToTransfer = (List<Block>) context.get(BLOCKS);
		List<Line> lineToTransfer = (List<Line>) context.get(LINES);
		ProgressionCommand progression = (ProgressionCommand) context.get(PROGRESSION);

		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		Command cleanCommand = CommandFactory.create(initialContext, CleanRepositoryCommand.class.getName());
		log.info("Cleaning target dataspace");
		boolean cleanCommandResult = cleanCommand.execute(context);
		if(!cleanCommandResult) {
			log.error("Error cleaning dataspace");
			return ERROR;
		}
		
		// Persist

		List<Interchange> interchanges=new ArrayList<>();
		Set<String> vehicleJourneyIds=new HashSet<>();

		Referential referential = new Referential();
		try {
			log.info("Starting to persist lines, count=" + lineToTransfer.size());

			for (Line l : lineToTransfer) {
				for (Route r : l.getRoutes()) {
					for(JourneyPattern jp : r.getJourneyPatterns()) {
						for(RouteSection rs : jp.getRouteSections()) {
							referential.getRouteSections().putIfAbsent(rs.getObjectId(), rs);
						}

						// Make sure interchanges do not point to vehicle journeys or stops in other lines (reset id to wipe object ref)
						for (VehicleJourney vj : jp.getVehicleJourneys()) {
							for (Interchange ci : vj.getConsumerInterchanges()) {
								clearInterchangeOjbectReferences(ci);
								interchanges.add(ci);
							}
							vj.getConsumerInterchanges().clear();
							vehicleJourneyIds.add(vj.getObjectId());
						}

					}

				}
			}
			
			List<Interchange> validInterchanges = interchanges.stream().filter(interchange -> isInterchangeValid(interchange, vehicleJourneyIds)).collect(Collectors.toList());
			log.info("Inserting " + validInterchanges.size() + " interchanges. Discarded " + (interchanges.size() - validInterchanges.size()) + " interchanges where consumer is invalid");
			validInterchanges.forEach(interchange -> interchangeDAO.create(interchange));

			log.info("Inserting " + referential.getRouteSections().size() + " route sections");
			for (RouteSection sa : referential.getRouteSections().values()) {
				routeSectionDAO.create(sa);
			}
			log.info("Flushing " + referential.getRouteSections().size() + " route sections");
			routeSectionDAO.flush();
			progression.execute(context);

			referential.clear(true);
			
			for (int i = 0; i < lineToTransfer.size(); i++) {
				Line line = lineToTransfer.get(i);
				log.info("Persisting transfered line " + line.getObjectId() + " / " + line.getName());

				lineDAO.create(line);
				progression.execute(context);

				if ((i + 1) % FLUSH_SIZE == 0) {
					log.info("Intermediary flush");
					lineDAO.flush();
					// Remove most flushed objects from persistence context to ease garbage collection
					detachLineFromPersistenceContext(lineToTransfer, i, FLUSH_SIZE);
					log.info("Intermediary flush completed");
				}
			}

			lineDAO.flush();
			log.info("Starting to persist blocks");
			for (Block block : blocksToTransfer) {
				if (log.isDebugEnabled()) {
					log.debug("Preparing block " + block.getObjectId());
				}

				// persist only the vehicle journeys that were effectively transferred, ignoring the others.
				List<VehicleJourney> persistentVehicleJourneys = vehicleJourneyDAO.findByObjectIdNoFlush(block.getVehicleJourneys().stream().map(vj -> vj.getObjectId()).collect(Collectors.toList()));
				block.setVehicleJourneys(persistentVehicleJourneys);

				// persist only the start points and end points that were effectively transferred, ignoring the others
				if(block.getStartPoint() != null) {
					block.setStartPoint(scheduledStopPointDAO.findByObjectId(block.getStartPoint().getObjectId()));
				}
				if(block.getEndPoint() != null) {
					block.setEndPoint(scheduledStopPointDAO.findByObjectId(block.getEndPoint().getObjectId()));
				}


				// persist only the deadRuns that were effectively transferred, ignoring the others.
				List<DeadRun> persistentDeadRuns = deadRunDAO.findByObjectIdNoFlush(block.getDeadRuns().stream().map(vj -> vj.getObjectId()).collect(Collectors.toList()));
				block.setDeadRuns(persistentDeadRuns);

				// reuse the timetables that were already created during the line transfer step,
				// the other timetables are tied only to blocks and are not persisted yet.
				List<Timetable> persistentTimetables = timetableDAO.findByObjectIdNoFlush(block.getTimetables().stream().map(tt -> tt.getObjectId()).collect(Collectors.toList()));
				block.getTimetables().removeAll(persistentTimetables);
				block.getTimetables().addAll(persistentTimetables);

			}
			log.info("Persisting blocks");
			// Persisting all blocks at once, for performance (batched INSERT in DB)
			blocksToTransfer.forEach(blockDAO::create);
			log.info("Flushing blocks");
			lineDAO.flush();

			log.info("Updating target referential last update timestamp");
			Command updateReferentialLastUpdateTimestampCommand = CommandFactory.create(initialContext, UpdateReferentialLastUpdateTimestampCommand.class.getName());
			updateReferentialLastUpdateTimestampCommand.execute(context);

			log.info("Final flush");
			lineDAO.flush();
			log.info("Final flush completed");

			return true;
		} finally {
			em.clear();
			referential.clear(true);
			lineToTransfer.clear();
			blocksToTransfer.clear();
		}
	}

	// If interchange consumer is within referential the journey must be among the journeys to be transferred for the interchange to be valid.
	// Unable to verify validity of inter-referential interchanges. To must be done in Level 2 validation.
	private boolean isInterchangeValid(Interchange i, Set<String> vehicleJourneyIds){
		if (i.getFeederVehicleJourneyObjectid().startsWith(i.objectIdPrefix())){
			return vehicleJourneyIds.contains(i.getFeederVehicleJourneyObjectid());
		}
		return true;
	}

	private void clearInterchangeOjbectReferences(Interchange fi) {
		fi.setConsumerStopPointObjectid(fi.getConsumerStopPointObjectid());
		fi.setConsumerVehicleJourneyObjectid(fi.getConsumerVehicleJourneyObjectid());
		fi.setFeederStopPointObjectid(fi.getFeederStopPointObjectid());
		fi.setFeederVehicleJourneyObjectid(fi.getFeederVehicleJourneyObjectid());
	}

	private void detachLineFromPersistenceContext(List<Line> lineToTransfer, int i, int flushSize) {

		int freedObjectCount = 0;
		
		int start = i-flushSize;
		if(start < 0) {
			start = 0;
		}
		
		for (int x = start; x <= i; x++) {
			if (x < lineToTransfer.size()) {
				Line l = lineToTransfer.get(x);
				em.detach(l);
				freedObjectCount++;
				for (Route r : l.getRoutes()) {
					em.detach(r);
					freedObjectCount++;
					for(StopPoint sp : r.getStopPoints()) {
						em.detach(sp);
						freedObjectCount++;
					}
					r.getStopPoints().clear();
					
					for (JourneyPattern jp : r.getJourneyPatterns()) {
						em.detach(jp);
						freedObjectCount++;
						for(StopPoint sp : jp.getStopPoints()) {
							em.detach(sp);
							freedObjectCount++;
							
						}
						jp.getStopPoints().clear();
						
						for (VehicleJourney vj : jp.getVehicleJourneys()) {
							em.detach(vj);
							freedObjectCount++;
							for (VehicleJourneyAtStop vjs : vj.getVehicleJourneyAtStops()) {
								em.detach(vjs);
								freedObjectCount++;
							}
							vj.getVehicleJourneyAtStops().clear();
						}
						jp.getVehicleJourneys().clear();
					}
					r.getJourneyPatterns().clear();
				}
				l.getRoutes().clear();
			}
		}
		log.info("Freed "+freedObjectCount+" objects for lines "+start+" to "+i);
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.transfer/" + COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				// try another way on test context
				String name = "java:module/" + COMMAND;
				try {
					result = (Command) context.lookup(name);
				} catch (NamingException e1) {
					log.error(e);
				}
			}
			return result;
		}
	}

	static {
		CommandFactory.factories.put(TransferExportDataWriter.class.getName(), new DefaultCommandFactory());
	}
}
