package mobi.chouette.exchange.transfer.exporter;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.dao.RouteSectionDAO;
import mobi.chouette.exchange.ProgressionCommand;
import mobi.chouette.exchange.importer.CleanRepositoryCommand;
import mobi.chouette.exchange.transfer.Constant;
import mobi.chouette.model.*;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

@Log4j
@Stateless(name = TransferExportDataWriter.COMMAND)
public class TransferExportDataWriter implements Command, Constant {

	private static final int FLUSH_SIZE = 100;

	public static final String COMMAND = "TransferExporterDataWriter";

	@EJB
	private LineDAO lineDAO;

	@EJB
	private RouteSectionDAO routeSectionDAO;

	@PersistenceContext(unitName = "referential")
	private EntityManager em;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@TransactionTimeout(value = 2, unit = TimeUnit.HOURS)
	public boolean execute(Context context) throws Exception {
		if (!em.isJoinedToTransaction()) {
			throw new RuntimeException("No transaction");
		}

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
		log.info("Starting to persist lines, count=" + lineToTransfer.size());

		// TODO collect StopArea, ConnectionLink, AccessLink and RouteSections into separate Sets
		Referential referential = new Referential();
		try {
			for (Line l : lineToTransfer) {
				for (Route r : l.getRoutes()) {
					for(JourneyPattern jp : r.getJourneyPatterns()) {
						for(RouteSection rs : jp.getRouteSections()) {
							referential.getRouteSections().putIfAbsent(rs.getObjectId(), rs);
						}
					}

				}
			}

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
				log.info("Persisting line " + line.getObjectId() + " / " + line.getName());

				lineDAO.create(line);
				progression.execute(context);

				if (i % FLUSH_SIZE == 0) {
					log.info("Intermediary flush");
					lineDAO.flush();
					// Remove most flushed objects from persistence context to ease garbage collection
					detachLineFromPersistenceContext(lineToTransfer, i, FLUSH_SIZE);
					log.info("Intermediary flush completed");
				}
			}

			log.info("Final flush");
			lineDAO.flush();
			log.info("Final flush completed");

			return true;
		} finally {
			em.clear();
			referential.clear(true);
			lineToTransfer.clear();
		}
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
						sp.setContainedInStopArea(null);
					}
					r.getStopPoints().clear();
					
					for (JourneyPattern jp : r.getJourneyPatterns()) {
						em.detach(jp);
						freedObjectCount++;
						for(StopPoint sp : jp.getStopPoints()) {
							em.detach(sp);
							freedObjectCount++;
							sp.setContainedInStopArea(null);
							
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
