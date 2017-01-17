package mobi.chouette.exchange.generic.exporter;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.ejb3.annotation.TransactionTimeout;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.AccessLinkDAO;
import mobi.chouette.dao.ConnectionLinkDAO;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.exchange.importer.CleanRepositoryCommand;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.util.Referential;

@Log4j
@Stateless(name = GenericExportDataWriter.COMMAND)
public class GenericExportDataWriter implements Command {

	private static final int FLUSH_SIZE = 100;

	public static final String COMMAND = "GenericExporterDataWriter";

	@EJB
	private LineDAO lineDAO;

	@EJB
	private StopAreaDAO stopAreaDAO;

	@EJB
	private ConnectionLinkDAO connectionLinkDAO;

	@EJB
	private AccessLinkDAO accessLinkDAO;

	@PersistenceContext(unitName = "referential")
	private EntityManager em;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
	public boolean execute(Context context) throws Exception {
		if (!em.isJoinedToTransaction()) {
			throw new RuntimeException("No transaction");
		}

		List<Line> lineToTransfer = (List<Line>) context.get("LINES");

		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		Command cleanCommand = CommandFactory.create(initialContext, CleanRepositoryCommand.class.getName());
		log.info("Cleaning target dataspace");
		boolean cleanCommandResult = cleanCommand.execute(context);

		// Persist
		log.info("Starting to persist lines, count=" + lineToTransfer.size());

		context.put(OPTIMIZED, Boolean.TRUE);
		Referential referential = new Referential();

		Set<StopArea> stopAreas = new HashSet<>();
		for (Line l : lineToTransfer) {
			for (Route r : l.getRoutes()) {
				for (StopPoint sp : r.getStopPoints()) {
					StopArea sa = sp.getContainedInStopArea();

					if (sa != null) {
						// stopAreas.add(sa);

						referential.getStopAreas().put(sa.getObjectId(), sa);
						// referential.getSharedStopAreas().put(sa.getObjectId(),
						// sa);
						if (sa.getParent() != null) {
							// stopAreas.add(sa);
							referential.getStopAreas().put(sa.getParent().getObjectId(), sa.getParent());
							// referential.getSharedStopAreas().put(sa.getParent().getObjectId(),
							// sa.getParent());
						}
						for (ConnectionLink cle : sa.getConnectionEndLinks()) {
							if (cle.getEndOfLink() != null) {
								// stopAreas.add(cle.getEndOfLink());
								// referential.getStopAreas().put(sa.getObjectId(),
								// sa);
								referential.getStopAreas().put(cle.getEndOfLink().getObjectId(), cle.getEndOfLink());
							}
							if (cle.getStartOfLink() != null) {
								// stopAreas.add(cle.getStartOfLink());
								referential.getStopAreas().put(cle.getStartOfLink().getObjectId(),
										cle.getStartOfLink());
							}

							referential.getConnectionLinks().put(cle.getObjectId(), cle);
							// referential.getSharedConnectionLinks().put(cle.getObjectId(),
							// cle);
						}
						for (ConnectionLink cle : sa.getConnectionStartLinks()) {
							if (cle.getEndOfLink() != null) {
								referential.getStopAreas().put(cle.getEndOfLink().getObjectId(), cle.getEndOfLink());
							}
							if (cle.getStartOfLink() != null) {
								referential.getStopAreas().put(cle.getStartOfLink().getObjectId(),
										cle.getStartOfLink());
							}
							referential.getConnectionLinks().put(cle.getObjectId(), cle);
							// referential.getSharedConnectionLinks().put(cle.getObjectId(),
							// cle);
						}
						for (AccessLink cle : sa.getAccessLinks()) {
							if (cle.getStopArea() != null) {
								referential.getStopAreas().put(cle.getStopArea().getObjectId(), cle.getStopArea());
								// stopAreas.add(cle.getStopArea());
							}

							referential.getAccessLinks().put(cle.getObjectId(), cle);
							// referential.getSharedAccessLinks().put(cle.getObjectId(),
							// cle);
						}
					}
				}
			}
		}

		// context.put(REFERENTIAL, referential);
		// Command stopAreaRegister = CommandFactory.create(initialContext,
		// StopAreaRegisterCommand.class.getName());
		// stopAreaRegister.execute(context);

		log.info("Inserting " + referential.getStopAreas().size() + " stopareas");
		for (StopArea sa : referential.getStopAreas().values()) {
			stopAreaDAO.create(sa);
		}
		log.info("Flushing " + stopAreas.size() + " stopareas");
		stopAreaDAO.flush();

		log.info("Inserting " + referential.getConnectionLinks().size() + " connection links");
		for (ConnectionLink sa : referential.getConnectionLinks().values()) {
			connectionLinkDAO.create(sa);
		}
		log.info("Flushing " + referential.getConnectionLinks().size() + " connection links");
		connectionLinkDAO.flush();

		log.info("Inserting " + referential.getAccessLinks().size() + " access links");
		for (AccessLink sa : referential.getAccessLinks().values()) {
			accessLinkDAO.create(sa);
		}
		log.info("Flushing " + referential.getAccessLinks().size() + " access links");
		accessLinkDAO.flush();

		GenericImportParameters importParameters = new GenericImportParameters();
		importParameters.setKeepObsoleteLines(true);
		context.put(CONFIGURATION, importParameters);

		for (int i = 0; i < lineToTransfer.size(); i++) {
			Line line = lineToTransfer.get(i);
			log.info("Persisting line " + line.getObjectId() + " / " + line.getName());

			lineDAO.create(line);

			if (i % FLUSH_SIZE == 0) {
				log.info("Intermediary flush");
				lineDAO.flush();
				detachLineFromPersistenceContext(lineToTransfer, i, FLUSH_SIZE);
				log.info("Intermediary flush completed");
			}


			// referential.getLines().put(line.getObjectId(), line);
			// for(Route r : line.getRoutes()) {
			// for(JourneyPattern jp : r.getJourneyPatterns()) {
			// for(VehicleJourney vj : jp.getVehicleJourneys()) {
			// referential.getVehicleJourneys().put(vj.getObjectId(), vj);
			// }
			// }
			// }
			//
			//
			// Command register = CommandFactory.create(initialContext,
			// LineRegisterCommand.class.getName());
			// register.execute(context);
			//
			// Command copy = CommandFactory.create(initialContext,
			// CopyCommand.class.getName());
			// copy.execute(context);
			//
			// referential.getLines().clear();
			// referential.getVehicleJourneys().clear();

		}

		log.info("Final flush");
		lineDAO.flush();
		log.info("Final flush completed");

		// check if CopyCommands ended (with timeout to 5 minutes >
		// transaction timeout)
		// if (context.containsKey(COPY_IN_PROGRESS)) {
		// long timeout = 5;
		// TimeUnit unit = TimeUnit.MINUTES;
		// List<Future<Void>> futures = (List<Future<Void>>)
		// context.get(COPY_IN_PROGRESS);
		// for (Future<Void> future : futures) {
		// if (!future.isDone()) {
		// log.info("Waiting for CopyCommand");
		// future.get(timeout, unit);
		// }
		// }
		// }
		em.clear();

		return true;
	}

	private void detachLineFromPersistenceContext(List<Line> lineToTransfer, int i, int flushSize) {

		int start = i-flushSize;
		if(start < 0) {
			start = 0;
		}
		
		for (int x = start; x <= i; x++) {
			if (x < lineToTransfer.size()) {
				Line l = lineToTransfer.get(x);
				em.detach(l);
				for (Route r : l.getRoutes()) {
					em.detach(r);
					for(StopPoint sp : r.getStopPoints()) {
						em.detach(sp);
						sp.setContainedInStopArea(null);
					}
					r.getStopPoints().clear();
					
					for (JourneyPattern jp : r.getJourneyPatterns()) {
						em.detach(jp);
						for(StopPoint sp : jp.getStopPoints()) {
							em.detach(sp);
							sp.setContainedInStopArea(null);
							
						}
						jp.getStopPoints().clear();
						
						for (VehicleJourney vj : jp.getVehicleJourneys()) {
							em.detach(vj);
							for (VehicleJourneyAtStop vjs : vj.getVehicleJourneyAtStops()) {
								em.detach(vjs);
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

	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.generic/" + COMMAND;
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
		CommandFactory.factories.put(GenericExportDataWriter.class.getName(), new DefaultCommandFactory());
	}
}
