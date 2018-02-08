package mobi.chouette.exchange.importer;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import mobi.chouette.exchange.parameters.AbstractImportParameter;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.ActionReporter.ERROR_CODE;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_STATE;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_TYPE;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.util.NamingUtil;
import mobi.chouette.model.util.NeptuneUtil;
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

		AbstractImportParameter importParameter = (AbstractImportParameter) context.get(CONFIGURATION);

		Referential referential = (Referential) context.get(REFERENTIAL);

		Line newValue = referential.getLines().values().iterator().next();

		if (importParameter.isKeepObsoleteLines() || isLineIsValidInFuture(newValue)) {

			log.info("register line : " + newValue.getObjectId() + " " + newValue.getName()
					+ " vehicleJourney count = " + referential.getVehicleJourneys().size());
			try {

				optimiser.initialize(cache, referential);
				if (importParameter.isRouteMergeable()) {
					checkMergedRoutes(context);
				}

				Line oldValue = cache.getLines().get(newValue.getObjectId());
				lineUpdater.update(context, oldValue, newValue);
				lineDAO.create(oldValue);
				lineDAO.flush(); // to prevent SQL error outside method

				if (optimized) {
					Monitor wMonitor = MonitorFactory.start("prepareCopy");
					StringWriter buffer = new StringWriter(1024);
					final List<String> list = new ArrayList<String>(referential.getVehicleJourneys().keySet());
					for (VehicleJourney item : referential.getVehicleJourneys().values()) {
						VehicleJourney vehicleJourney = cache.getVehicleJourneys().get(item.getObjectId());
						if (vehicleJourney == null) {
							log.error("problem on saving vehicleJourneyAtStops for vehicleJourney " + item);
							log.error("journeyPattern =  " + item.getJourneyPattern());
							log.error("route =  " + item.getRoute());
							continue;
						}
						if (vehicleJourney.getId() == null) {
							log.error("problem on saving vehicleJourneyAtStops for vehicleJourney without id "
									+ vehicleJourney);
							log.error("new journeyPattern =  " + item.getJourneyPattern());
							log.error("new route =  " + item.getRoute());
							log.error("old journeyPattern =  " + vehicleJourney.getJourneyPattern());
							log.error("old route =  " + vehicleJourney.getRoute());
							continue;
						}

						List<VehicleJourneyAtStop> vehicleJourneyAtStops = item.getVehicleJourneyAtStops();
						for (VehicleJourneyAtStop vehicleJourneyAtStop : vehicleJourneyAtStops) {

							StopPoint stopPoint = cache.getStopPoints().get(
									vehicleJourneyAtStop.getStopPoint().getObjectId());

							write(buffer, vehicleJourney, stopPoint, vehicleJourneyAtStop);
						}
					}
					vehicleJourneyDAO.deleteChildren(list);
					context.put(BUFFER, buffer.toString());
					wMonitor.stop();
				}

				result = SUCCESS;
			} catch (Exception ex) {
				log.error(ex.getMessage());
				ActionReporter reporter = ActionReporter.Factory.getInstance();
				reporter.addObjectReport(context, newValue.getObjectId(), OBJECT_TYPE.LINE,
						NamingUtil.getName(newValue), OBJECT_STATE.ERROR, IO_TYPE.INPUT);
				if (ex.getCause() != null) {
					Throwable e = ex.getCause();
					while (e.getCause() != null) {
						log.error(e.getMessage());
						e = e.getCause();
					}
					if (e instanceof SQLException) {
						e = ((SQLException) e).getNextException();
						reporter.addErrorToObjectReport(context, newValue.getObjectId(), OBJECT_TYPE.LINE,
								ERROR_CODE.WRITE_ERROR, e.getMessage());

					} else {
						reporter.addErrorToObjectReport(context, newValue.getObjectId(), OBJECT_TYPE.LINE,
								ERROR_CODE.INTERNAL_ERROR, e.getMessage());
					}
				} else {
					reporter.addErrorToObjectReport(context, newValue.getObjectId(), OBJECT_TYPE.LINE,
							ERROR_CODE.INTERNAL_ERROR, ex.getMessage());
				}
				throw ex;
			} finally {
				log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);

				// monitor = MonitorFactory.getTimeMonitor("LineOptimiser");
				// if (monitor != null)
				// log.info(Color.LIGHT_GREEN + monitor.toString() +
				// Color.NORMAL);
				// monitor =
				// MonitorFactory.getTimeMonitor(LineUpdater.BEAN_NAME);
				// if (monitor != null)
				// log.info(Color.LIGHT_GREEN + monitor.toString() +
				// Color.NORMAL);
				// monitor =
				// MonitorFactory.getTimeMonitor(GroupOfLineUpdater.BEAN_NAME);
				// if (monitor != null)
				// log.info(Color.LIGHT_GREEN + monitor.toString() +
				// Color.NORMAL);
				// monitor =
				// MonitorFactory.getTimeMonitor(CompanyUpdater.BEAN_NAME);
				// if (monitor != null)
				// log.info(Color.LIGHT_GREEN + monitor.toString() +
				// Color.NORMAL);
				// monitor =
				// MonitorFactory.getTimeMonitor(RouteUpdater.BEAN_NAME);
				// if (monitor != null)
				// log.info(Color.LIGHT_GREEN + monitor.toString() +
				// Color.NORMAL);
				// monitor =
				// MonitorFactory.getTimeMonitor(JourneyPatternUpdater.BEAN_NAME);
				// if (monitor != null)
				// log.info(Color.LIGHT_GREEN + monitor.toString() +
				// Color.NORMAL);
				// monitor =
				// MonitorFactory.getTimeMonitor(VehicleJourneyUpdater.BEAN_NAME);
				// if (monitor != null)
				// log.info(Color.LIGHT_GREEN + monitor.toString() +
				// Color.NORMAL);
				// monitor =
				// MonitorFactory.getTimeMonitor(StopPointUpdater.BEAN_NAME);
				// if (monitor != null)
				// log.info(Color.LIGHT_GREEN + monitor.toString() +
				// Color.NORMAL);
				// monitor =
				// MonitorFactory.getTimeMonitor(StopAreaUpdater.BEAN_NAME);
				// if (monitor != null)
				// log.info(Color.LIGHT_GREEN + monitor.toString() +
				// Color.NORMAL);
				// monitor =
				// MonitorFactory.getTimeMonitor(ConnectionLinkUpdater.BEAN_NAME);
				// if (monitor != null)
				// log.info(Color.LIGHT_GREEN + monitor.toString() +
				// Color.NORMAL);
				// monitor =
				// MonitorFactory.getTimeMonitor(TimetableUpdater.BEAN_NAME);
				// if (monitor != null)
				// log.info(Color.LIGHT_GREEN + monitor.toString() +
				// Color.NORMAL);
				// monitor = MonitorFactory.getTimeMonitor("prepareCopy");
				// if (monitor != null)
				// log.info(Color.LIGHT_GREEN + monitor.toString() +
				// Color.NORMAL);
			}
		} else {
			log.info("skipping obsolete line : " + newValue.getObjectId());
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}
		return result;
	}

	private boolean isLineIsValidInFuture(Line line) {

		Date now = new Date();

		for (Route r : line.getRoutes()) {
			for (JourneyPattern jp : r.getJourneyPatterns()) {
				for (VehicleJourney vj : jp.getVehicleJourneys()) {
					for (Timetable t : vj.getTimetables()) {
						t.computeLimitOfPeriods();
						log.info("Checking " + t.getEndOfPeriod() + " against " + now);
						if (!t.getEndOfPeriod().before(now)) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	protected void write(StringWriter buffer, VehicleJourney vehicleJourney, StopPoint stopPoint,
			VehicleJourneyAtStop vehicleJourneyAtStop) throws IOException {
		// The list of fields to synchronize with
		// VehicleJourneyAtStopUpdater.update(Context context,
		// VehicleJourneyAtStop oldValue,
		// VehicleJourneyAtStop newValue)

		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		buffer.write(vehicleJourney.getId().toString());
		buffer.append(SEP);
		buffer.write(stopPoint.getId().toString());
		buffer.append(SEP);
		if (vehicleJourneyAtStop.getArrivalTime() != null)
			buffer.write(timeFormat.format(vehicleJourneyAtStop.getArrivalTime()));
		else
			buffer.write(NULL);
		buffer.append(SEP);
		if (vehicleJourneyAtStop.getDepartureTime() != null)
			buffer.write(timeFormat.format(vehicleJourneyAtStop.getDepartureTime()));
		else
			buffer.write(NULL);
		buffer.append(SEP);
		buffer.write(Integer.toString(vehicleJourneyAtStop.getArrivalDayOffset()));
		buffer.append(SEP);
		buffer.write(Integer.toString(vehicleJourneyAtStop.getDepartureDayOffset()));

		buffer.append('\n');

	}

	private void checkMergedRoutes(Context context) {
		Referential ref = (Referential) context.get(REFERENTIAL);
		Referential cache = (Referential) context.get(CACHE);
		// check if journey patterns can be remerged in existing routes not
		// built by new import
		for (JourneyPattern newJp : ref.getJourneyPatterns().values()) {
			JourneyPattern oldJp = cache.getJourneyPatterns().get(newJp.getObjectId());
			if (oldJp != null && !oldJp.isDetached()) {
				if (!oldJp.getRoute().getObjectId().equals(newJp.getRoute().getObjectId())) {
					// jp has changed route : see cases
					Route oldRoute = oldJp.getRoute();
					Route newRoute = oldJp.getRoute();
					if (oldRoute.getStopPoints().size() >= newRoute.getStopPoints().size()) {
						// oldRoute is good enough for jp, don't change it but
						// put new vehicle journeys on old route
						replaceOldRoute(cache, ref, newJp, oldJp);
					} else {
						// switch to new route, update old vehicle journeys
						setNewRoute(cache, ref, newJp, oldJp);
					}
				}
			}
		}

	}

	/**
	 * reaffect new version of JP to old saved route
	 * 
	 * @param cache
	 * @param ref
	 * @param newJp
	 * @param oldJp
	 */
	private void replaceOldRoute(Referential cache, Referential ref, JourneyPattern newJp, JourneyPattern oldJp) {
		// add saved route to cache if necessary
		Route oldRoute = oldJp.getRoute();
		addRouteToReferential(cache, oldRoute);
		addRouteToReferential(cache, oldRoute.getOppositeRoute());

		// create unsaved route
		Route route = ref.getRoutes().get(oldRoute.getObjectId());
		if (route == null) {
			route = new Route();
			route.copyAttributes(oldRoute);
			route.setLine(ref.getLines().values().iterator().next());
			// add stopPoints
			addStopsToRoute(ref, cache, route, oldRoute);
			ref.getRoutes().put(route.getObjectId(), route);
			if (oldRoute.getOppositeRoute() != null) {
				Route oppositeRoute = ref.getRoutes().get(oldRoute.getOppositeRoute().getObjectId());
				if (oppositeRoute == null) {
					oppositeRoute = new Route();
					oppositeRoute.copyAttributes(oldRoute.getOppositeRoute());
					oppositeRoute.setLine(ref.getLines().values().iterator().next());
					addStopsToRoute(ref, cache, oppositeRoute, oldRoute.getOppositeRoute());
					ref.getRoutes().put(oppositeRoute.getObjectId(), oppositeRoute);
				}
				route.setOppositeRoute(oppositeRoute);
			}
		}

		Route previousRoute = newJp.getRoute();
		newJp.setRoute(route);
		if (previousRoute.getJourneyPatterns().isEmpty()) {
			removeRoute(ref, previousRoute);
		}
		List<StopPoint> oldSps = new ArrayList<>();
		oldSps.addAll(newJp.getStopPoints());
		newJp.getStopPoints().clear();
		for (StopPoint stopPoint : oldSps) {
			StopPoint newSp = route.getStopPoints().get(stopPoint.getPosition());
			newJp.addStopPoint(newSp);
		}
		NeptuneUtil.refreshDepartureArrivals(newJp);
		for (VehicleJourney vj : newJp.getVehicleJourneys()) {
			vj.setRoute(route);
			int i = 0;
			for (VehicleJourneyAtStop vjas : vj.getVehicleJourneyAtStops()) {
				vjas.setStopPoint(newJp.getStopPoints().get(i));
				i++;
			}
		}
	}

	private void removeRoute(Referential ref, Route route) {
		log.info("remove unused route " + route.getObjectId());
		route.setLine(null);
		ref.getRoutes().remove(route.getObjectId());
		for (StopPoint point : route.getStopPoints()) {
			ref.getStopPoints().remove(point.getObjectId());
		}

	}

	private void addStopsToRoute(Referential ref, Referential cache, Route route, Route oldRoute) {
		for (StopPoint oldSp : oldRoute.getStopPoints()) {
			StopPoint sp = new StopPoint();
			sp.copyAttributes(oldSp);
			sp.setContainedInStopArea(getStopArea(ref, cache, oldSp));
			sp.setRoute(route);
			ref.getStopPoints().put(sp.getObjectId(), sp);
		}
	}

	private StopArea getStopArea(Referential ref, Referential cache, StopPoint oldSp) {
		StopArea dbStop = oldSp.getContainedInStopArea();
		if (!cache.getStopAreas().containsKey(dbStop.getObjectId())) {
			cache.getStopAreas().put(dbStop.getObjectId(), dbStop);
		}
		StopArea stop = ref.getStopAreas().get(dbStop.getObjectId());
		if (stop == null) {
			// add to ref
			stop = new StopArea();
			stop.copyAttributes(dbStop);
			ref.getStopAreas().put(stop.getObjectId(), stop);
		}
		return stop;
	}

	private void addRouteToReferential(Referential ref, Route route) {
		if (route == null)
			return;
		if (ref.getRoutes().containsKey(route.getObjectId()))
			return;
		ref.getRoutes().put(route.getObjectId(), route);
		for (StopPoint sp : route.getStopPoints()) {
			ref.getStopPoints().put(sp.getObjectId(), sp);
		}
	}

	private void setNewRoute(Referential cache, Referential ref, JourneyPattern newJp, JourneyPattern oldJp) {
		// set stopPoints for all vehicle journeys to new route
		// inject all old vehicle journeys in ref with new stoppoints
		for (VehicleJourney oldvj : oldJp.getVehicleJourneys()) {
			if (!cache.getVehicleJourneys().containsKey(oldvj.getObjectId())) {
				cache.getVehicleJourneys().put(oldvj.getObjectId(), oldvj);
			}
			// skip already updated vehicle journeys
			if (ref.getVehicleJourneys().containsKey(oldvj.getObjectId()))
				continue;
			VehicleJourney newVj = new VehicleJourney();
			newVj.copyAttributes(oldvj);
			newVj.setJourneyPattern(newJp);
			newVj.setRoute(newJp.getRoute());
			ref.getVehicleJourneys().put(newVj.getObjectId(), newVj);
			int i = 0;
			for (VehicleJourneyAtStop oldVjas : oldvj.getVehicleJourneyAtStops()) {
				VehicleJourneyAtStop newVjas = new VehicleJourneyAtStop();
				newVjas.copyAttributes(oldVjas);
				newVjas.setStopPoint(newJp.getStopPoints().get(i));
				i++;
			}
			ref.getVehicleJourneys().put(newVj.getObjectId(), newVj);
		}

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
