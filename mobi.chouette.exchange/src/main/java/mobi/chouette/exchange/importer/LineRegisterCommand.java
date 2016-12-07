package mobi.chouette.exchange.importer;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.dao.VehicleJourneyDAO;
import mobi.chouette.exchange.importer.updater.LineOptimiser;
import mobi.chouette.exchange.importer.updater.LineUpdater;
import mobi.chouette.exchange.importer.updater.Updater;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.ActionReporter.ERROR_CODE;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_STATE;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_TYPE;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.model.Line;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.util.NamingUtil;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
@Stateless(name = LineRegisterCommand.COMMAND)
public class LineRegisterCommand implements Command {

	public static final String COMMAND = "LineRegisterCommand";

	@EJB
	private LineOptimiser optimiser;

	@EJB
	private LineDAO lineDAO;

	@EJB
	private VehicleJourneyDAO vehicleJourneyDAO;

	@EJB(beanName = LineUpdater.BEAN_NAME)
	private Updater<Line> lineUpdater;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean execute(Context context) throws Exception {

		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		if (!context.containsKey(OPTIMIZED)) {
			context.put(OPTIMIZED, Boolean.TRUE);
		}
		Boolean optimized = (Boolean) context.get(OPTIMIZED);
		Referential cache = new Referential();
		context.put(CACHE, cache);

		Referential referential = (Referential) context.get(REFERENTIAL);

		Line newValue = referential.getLines().values().iterator().next();
		log.info("register line : " + newValue.getObjectId() + " " + newValue.getName() + " vehicleJourney count = "
				+ referential.getVehicleJourneys().size());
		try {

			optimiser.initialize(cache, referential);
			//log.warn("Optimiser");
			Line oldValue = cache.getLines().get(newValue.getObjectId());
			//log.warn("Old value update line");
			lineUpdater.update(context, oldValue, newValue);
			//log.warn("value update");
			lineDAO.create(oldValue);
			//log.warn("create oldValue");
			lineDAO.flush(); // to prevent SQL error outside method
			//log.warn("flush");
			if (optimized) {
				//log.warn("optimised");
				Monitor wMonitor = MonitorFactory.start("prepareCopy");
				StringWriter buffer = new StringWriter(1024);
				final List<String> list = new ArrayList<String>(referential.getVehicleJourneys().keySet());
				//log.warn("optimised 2");
				for (VehicleJourney item : referential.getVehicleJourneys().values()) {
					//log.warn("optimised loop 1");
					VehicleJourney vehicleJourney = cache.getVehicleJourneys().get(item.getObjectId());
					//log.warn("optimised loop 2");
					List<VehicleJourneyAtStop> vehicleJourneyAtStops = item.getVehicleJourneyAtStops();
					//log.warn("optimised loop 3");
					
					if (vehicleJourney.getId() == null) {
						log.warn("vehicle journey object id : " + vehicleJourney.getObjectId());
						log.warn("journey pattern object id : " + vehicleJourney.getJourneyPattern().getObjectId());
						log.warn("journey pattern route object id : " + vehicleJourney.getJourneyPattern().getRoute().getObjectId());
						log.warn("route object id : " + vehicleJourney.getRoute().getObjectId());
						log.warn("line object id : " + vehicleJourney.getRoute().getLine().getObjectId());
					}
					for (VehicleJourneyAtStop vehicleJourneyAtStop : vehicleJourneyAtStops) {
						//log.warn("optimised loop loop 1");
						
			
						StopPoint stopPoint = cache.getStopPoints().get(
								vehicleJourneyAtStop.getStopPoint().getObjectId());
						//log.warn("optimised loop loop 2");
						
						
						write(buffer, vehicleJourney, stopPoint, vehicleJourneyAtStop);
						//log.warn("optimised loop loop 3");
					}
					//log.warn("optimised loop 4");
				}
				//log.warn("optimised 3");
				vehicleJourneyDAO.deleteChildren(list);
				//log.warn("optimised 4");
				context.put(BUFFER, buffer.toString());
				//log.warn("optimised 5");
				wMonitor.stop();
				log.warn("optimised 6");
			}

			result = SUCCESS;
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ActionReporter reporter = ActionReporter.Factory.getInstance();
			reporter.addObjectReport(context, newValue.getObjectId(), 
					OBJECT_TYPE.LINE, NamingUtil.getName(newValue), OBJECT_STATE.ERROR, IO_TYPE.INPUT);
			if (ex.getCause() != null) {
				Throwable e = ex.getCause();
				while (e.getCause() != null) {
					log.error(e.getMessage());
					e = e.getCause();
				}
				if (e instanceof SQLException) {
					e = ((SQLException) e).getNextException();
					reporter.addErrorToObjectReport(context, newValue.getObjectId(), OBJECT_TYPE.LINE, ERROR_CODE.WRITE_ERROR,  e.getMessage());
					
				} else {
					reporter.addErrorToObjectReport(context, newValue.getObjectId(), OBJECT_TYPE.LINE, ERROR_CODE.INTERNAL_ERROR,  e.getMessage());
				}
			} else {
				reporter.addErrorToObjectReport(context, newValue.getObjectId(), OBJECT_TYPE.LINE, ERROR_CODE.INTERNAL_ERROR,  ex.getMessage());
			}
			throw ex;
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
			
//			monitor = MonitorFactory.getTimeMonitor("LineOptimiser");
//			if (monitor != null)
//				log.info(Color.LIGHT_GREEN + monitor.toString() + Color.NORMAL);
//			monitor = MonitorFactory.getTimeMonitor(LineUpdater.BEAN_NAME);
//			if (monitor != null)
//				log.info(Color.LIGHT_GREEN + monitor.toString() + Color.NORMAL);
//			monitor = MonitorFactory.getTimeMonitor(GroupOfLineUpdater.BEAN_NAME);
//			if (monitor != null)
//				log.info(Color.LIGHT_GREEN + monitor.toString() + Color.NORMAL);
//			monitor = MonitorFactory.getTimeMonitor(CompanyUpdater.BEAN_NAME);
//			if (monitor != null)
//				log.info(Color.LIGHT_GREEN + monitor.toString() + Color.NORMAL);
//			monitor = MonitorFactory.getTimeMonitor(RouteUpdater.BEAN_NAME);
//			if (monitor != null)
//				log.info(Color.LIGHT_GREEN + monitor.toString() + Color.NORMAL);
//			monitor = MonitorFactory.getTimeMonitor(JourneyPatternUpdater.BEAN_NAME);
//			if (monitor != null)
//				log.info(Color.LIGHT_GREEN + monitor.toString() + Color.NORMAL);
//			monitor = MonitorFactory.getTimeMonitor(VehicleJourneyUpdater.BEAN_NAME);
//			if (monitor != null)
//				log.info(Color.LIGHT_GREEN + monitor.toString() + Color.NORMAL);
//			monitor = MonitorFactory.getTimeMonitor(StopPointUpdater.BEAN_NAME);
//			if (monitor != null)
//				log.info(Color.LIGHT_GREEN + monitor.toString() + Color.NORMAL);
//			monitor = MonitorFactory.getTimeMonitor(StopAreaUpdater.BEAN_NAME);
//			if (monitor != null)
//				log.info(Color.LIGHT_GREEN + monitor.toString() + Color.NORMAL);
//			monitor = MonitorFactory.getTimeMonitor(ConnectionLinkUpdater.BEAN_NAME);
//			if (monitor != null)
//				log.info(Color.LIGHT_GREEN + monitor.toString() + Color.NORMAL);
//			monitor = MonitorFactory.getTimeMonitor(TimetableUpdater.BEAN_NAME);
//			if (monitor != null)
//				log.info(Color.LIGHT_GREEN + monitor.toString() + Color.NORMAL);
//			monitor = MonitorFactory.getTimeMonitor("prepareCopy");
//			if (monitor != null)
//				log.info(Color.LIGHT_GREEN + monitor.toString() + Color.NORMAL);
		}
		return result;
	}

	protected void write(StringWriter buffer, VehicleJourney vehicleJourney, StopPoint stopPoint,
			VehicleJourneyAtStop vehicleJourneyAtStop) throws IOException {
		// The list of fields to synchronize with
		// VehicleJourneyAtStopUpdater.update(Context context,
		// VehicleJourneyAtStop oldValue,
		// VehicleJourneyAtStop newValue)
		log.warn("write 1");
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		log.warn("write 2");
		log.warn("VehicleJourney id : " + vehicleJourney.getId());
		buffer.write(vehicleJourney.getId().toString());
		log.warn("write 3");
		buffer.append(SEP);
		buffer.write(stopPoint.getId().toString());
		log.warn("write 4");
		buffer.append(SEP);
		if (vehicleJourneyAtStop.getArrivalTime() != null)
			buffer.write(timeFormat.format(vehicleJourneyAtStop.getArrivalTime()));
		else
			buffer.write(NULL);
		buffer.append(SEP);
		log.warn("write 5");
		if (vehicleJourneyAtStop.getDepartureTime() != null)
			buffer.write(timeFormat.format(vehicleJourneyAtStop.getDepartureTime()));
		else
			buffer.write(NULL);
		log.warn("write 6");
		buffer.append(SEP);
		buffer.write(Integer.toString(vehicleJourneyAtStop.getArrivalDayOffset()));
		buffer.append(SEP);
		log.warn("write 7");
		buffer.write(Integer.toString(vehicleJourneyAtStop.getDepartureDayOffset()));
		log.warn("write 8");
		
		buffer.append('\n');
		
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange/" + COMMAND;
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
		CommandFactory.factories.put(LineRegisterCommand.class.getName(), new DefaultCommandFactory());
	}
}
