package mobi.chouette.exchange.validation.checkpoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.parameters.TransportModeParameters;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.NeptuneUtil;

@Log4j
public class RouteCheckPoints extends AbstractValidation<Route> implements Validator<Route> {

	@Override
	public void validate(Context context, Route target) {
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		List<Route> beans = new ArrayList<>(data.getRoutes());
		ValidationParameters parameters = (ValidationParameters) context.get(VALIDATION);
		if (isEmpty(beans))
			return;
		// Monitor monitor =
		// MonitorFactory.start(this.getClass().getSimpleName());
		boolean sourceFile = context.get(SOURCE).equals(SOURCE_FILE);
		// init checkPoints : add here all defined check points for this kind of
		// object
		// 3-Route-1 : check if two successive stops are in same area
		// 3-Route-2 : check if two wayback routes are actually waybacks
		// 3-Route-3 : check distance between stops
		// 3-Route-4 : check identical routes
		// 3-Route-5 : check for potentially waybacks
		// 3-Route-6 : check if route has minimum 2 StopPoints
		// 3-Route-7 : check if route has minimum 1 JourneyPattern
		// 3-Route-8 : check if all stopPoints are used by journeyPatterns
		// 3-Route-9 : check if one journeyPattern uses all stopPoints

		initCheckPoint(context, ROUTE_1, SEVERITY.W);
		initCheckPoint(context, ROUTE_2, SEVERITY.W);
		initCheckPoint(context, ROUTE_3, SEVERITY.W);
		initCheckPoint(context, ROUTE_4, SEVERITY.W);
		initCheckPoint(context, ROUTE_5, SEVERITY.W);
		if (!sourceFile) {
			initCheckPoint(context, ROUTE_6, SEVERITY.E);
			initCheckPoint(context, ROUTE_7, SEVERITY.E);
			// checkPoint is applicable
			prepareCheckPoint(context, ROUTE_6);
			prepareCheckPoint(context, ROUTE_7);
		}
		initCheckPoint(context, ROUTE_8, SEVERITY.W);
		initCheckPoint(context, ROUTE_9, SEVERITY.W);

		boolean test4_1 = (parameters.getCheckRoute() != 0);
		if (test4_1) {
			initCheckPoint(context, L4_ROUTE_1, SEVERITY.E);
			prepareCheckPoint(context, L4_ROUTE_1);
		}

		// en cas d'erreur, on reporte autant de detail que de route en erreur
		for (int i = 0; i < beans.size(); i++) {
			Route route = beans.get(i);

			// 3-Route-1 : check if two successive stops are in same area
			check3Route1(context, route);

			// 3-Route-2 : check if two wayback routes are actually waybacks
			check3Route2(context, route);

			// 3-Route-3 : check distance between stops
			check3Route3(context, route, parameters);

			if (!sourceFile) {
				// 3-Route-6 : check if route has minimum 2 StopPoints
				check3Route6(context, route);

				// 3-Route-7 : check if route has minimum 1 JourneyPattern
				check3Route7(context, route);
			}

			// 3-Route-8 : check if all stopPoints are used by journeyPatterns
			check3Route8(context, route);

			// 3-Route-9 : check if one journeyPattern uses all stopPoints
			check3Route9(context, route);

			// 4-Route-1 : check columns constraints
			if (test4_1)
				check4Generic1(context, route, L4_ROUTE_1, parameters, log);

			for (int j = i + 1; j < beans.size(); j++) {
				// 3-Route-4 : check identical routes
				check3Route4(context, i, route, j, beans.get(j));

				// 3-Route-5 : check for potentially waybacks
				check3Route5(context, i, route, j, beans.get(j));
			}

		}
		// log.info(Color.CYAN + monitor.stop() + Color.NORMAL);
		return;
	}

	/**
	 * @param report
	 * @param route
	 * @param areas
	 */
	private void check3Route1(Context context, Route route) {
		// 3-Route-1 : check if two successive stops are in same area
		prepareCheckPoint(context, ROUTE_1);

		List<StopArea> areas = NeptuneUtil.getStopAreaOfRoute(route);
		for (int j = 1; j < areas.size(); j++) {
			if (areas.get(j - 1).equals(areas.get(j))) {
				// failure encountered, add route 1
				DataLocation location = buildLocation(context, route);
				DataLocation targetLocation = buildLocation(context, areas.get(j));

				ValidationReporter reporter = ValidationReporter.Factory.getInstance();
				reporter.addCheckPointReportError(context, ROUTE_1, location, null, null, targetLocation);
				break;
			}
		}

	}

	/**
	 * @param report
	 * @param route
	 * @param areas
	 */
	private void check3Route2(Context context, Route route) {
		// 3-Route-2 : check if two wayback routes are actually waybacks
		// test can be passed if route has wayback
		if (!hasOppositeRoute(route, log))
			return;

		List<StopArea> areas = NeptuneUtil.getStopAreaOfRoute(route);
		// test can be passed if areas exist and have parents
		if (areas.isEmpty())
			return;
		StopArea first = areas.get(0).getParent();
		StopArea last = areas.get(areas.size() - 1).getParent();
		if (first == null || last == null)
			return;
		Route routeWb = route.getOppositeRoute();
		List<StopArea> areasWb = NeptuneUtil.getStopAreaOfRoute(routeWb);
		// test can be passed if wayback areas exist and have parents
		if (!areasWb.isEmpty()) {
			StopArea firstWb = areasWb.get(0).getParent();
			StopArea lastWb = areasWb.get(areasWb.size() - 1).getParent();
			if (firstWb == null || lastWb == null)
				return;
			prepareCheckPoint(context, ROUTE_2);
			if (first.equals(lastWb) && last.equals(firstWb))
				return; // test ok
			// failure encountered, add route 1
			DataLocation location = buildLocation(context, route);

			DataLocation target1 = null;
			DataLocation target2 = null;
			if (!first.equals(lastWb)) {
				target1 = buildLocation(context, first);
				target2 = buildLocation(context, lastWb);

			} else {
				target1 = buildLocation(context, firstWb);
				target2 = buildLocation(context, last);

			}
			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			reporter.addCheckPointReportError(context, ROUTE_2, location, null, null, target1, target2);
		}
	}

	private void check3Route3(Context context, Route route, ValidationParameters parameters) {
		List<StopArea> areas = NeptuneUtil.getStopAreaOfRoute(route);
		if (isEmpty(areas))
			return;
		// 3-Route-3 : check distance between stops
		prepareCheckPoint(context, ROUTE_3);
		// find transportMode :
		String modeKey = route.getLine().getTransportModeName().toString();

		TransportModeParameters mode = getModeParameters(parameters, modeKey, log);
		if (mode == null) {
			log.error("no parameters for mode " + modeKey);
			mode = getModeParameters(parameters, MODE_OTHER, log);
			if (mode == null) {
				log.error("no parameters for mode " + MODE_OTHER);
				mode = modeDefault;
			}
		}
		double distanceMin = mode.getInterStopAreaDistanceMin();
		double distanceMax = mode.getInterStopAreaDistanceMax();

		for (int i = 1; i < areas.size(); i++) {
			StopArea firstArea = areas.get(i - 1);
			StopArea nextArea = areas.get(i);
			if (!firstArea.hasCoordinates() || !nextArea.hasCoordinates())
				continue;
			double distance = distance(firstArea, nextArea);
			if (distance < distanceMin) {
				DataLocation location = buildLocation(context, route);
				DataLocation target1 = buildLocation(context, firstArea);
				DataLocation target2 = buildLocation(context, nextArea);

				ValidationReporter reporter = ValidationReporter.Factory.getInstance();
				reporter.addCheckPointReportError(context, ROUTE_3, "1", location, Integer.toString((int) distance),
						Integer.toString((int) distanceMin), target1, target2);
				break; // do not check for oder stops in this route
			}
			if (distance > distanceMax) {
				DataLocation location = buildLocation(context, route);
				DataLocation target1 = buildLocation(context, firstArea);
				DataLocation target2 = buildLocation(context, nextArea);

				ValidationReporter reporter = ValidationReporter.Factory.getInstance();
				reporter.addCheckPointReportError(context, ROUTE_3, "2", location, Integer.toString((int) distance),
						Integer.toString((int) distanceMin), target1, target2);
				break; // do not check for oder stops in this route
			}
		}

	}

	private void check3Route4(Context context, int rank, Route route, int rank2, Route route2) {
		// 3-Route-4 : check identical routes
		if (isEmpty(route.getStopPoints()))
			return;
		prepareCheckPoint(context, ROUTE_4);
		List<StopArea> areas = NeptuneUtil.getStopAreaOfRoute(route);
		if (isEmpty(route2.getStopPoints()))
			return;

		List<StopArea> areas2 = NeptuneUtil.getStopAreaOfRoute(route2);
		// test can be passed if alternate route areas exist
		if (!areas2.isEmpty()) {
			if (areas.equals(areas2)) {
				// Improvement encountered, add route 1
				DataLocation location = buildLocation(context, route);
				DataLocation target = buildLocation(context, route2);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("routeId", route2.getObjectId());
				ValidationReporter reporter = ValidationReporter.Factory.getInstance();
				reporter.addCheckPointReportError(context, ROUTE_4, location, null, null, target);
			}
		}

	}

	/**
	 * @param report
	 * @param beans
	 * @param routeRank
	 * @param route
	 */
	private void check3Route5(Context context, int rank, Route route, int rankWb, Route routeWb) {
		// 3-Route-5 : check for potentially waybacks
		if (route.getOppositeRoute() != null)
			return;
		List<StopArea> areas = NeptuneUtil.getStopAreaOfRoute(route);
		// test can be passed if areas exist and have parents
		if (areas.isEmpty())
			return;
		StopArea first = areas.get(0).getParent();
		StopArea last = areas.get(areas.size() - 1).getParent();
		if (first == null || last == null)
			return;
		prepareCheckPoint(context, ROUTE_5);
		if (routeWb.getOppositeRoute() != null)
			return;
		List<StopArea> areasWb = NeptuneUtil.getStopAreaOfRoute(routeWb);
		// test can be passed if wayback areas exist and have parents
		if (!areasWb.isEmpty()) {
			StopArea firstWb = areasWb.get(0).getParent();
			StopArea lastWb = areasWb.get(areasWb.size() - 1).getParent();
			if (firstWb == null || lastWb == null)
				return;
			if (firstWb.equals(last) && lastWb.equals(first)) {
				// Improvement encountered
				DataLocation location = buildLocation(context, route);
				DataLocation target = buildLocation(context, routeWb);

				ValidationReporter reporter = ValidationReporter.Factory.getInstance();
				reporter.addCheckPointReportError(context, ROUTE_5, location, null, null, target);
			}
		}
	}

	/**
	 * @param report
	 * @param route
	 */
	private void check3Route6(Context context, Route route) {
		// 3-Route-6 : check if route has minimum 2 StopPoints
		if (isEmpty(route.getStopPoints()) || route.getStopPoints().size() < 2) {
			// failure encountered, add route 1
			DataLocation location = buildLocation(context, route);
			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			reporter.addCheckPointReportError(context, ROUTE_6, location);
		}
	}

	/**
	 * @param report
	 * @param route
	 */
	private void check3Route7(Context context, Route route) {
		// 3-Route-7 : check if route has minimum 1 JourneyPattern
		if (isEmpty(route.getJourneyPatterns())) {
			// failure encountered, add route 1
			DataLocation location = buildLocation(context, route);
			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			reporter.addCheckPointReportError(context, ROUTE_7, location);
		}
	}

	/**
	 * @param report
	 * @param route
	 */
	private void check3Route8(Context context, Route route) {
		// 3-Route-8 : check if all stopPoints are used by journeyPatterns
		if (isEmpty(route.getJourneyPatterns()))
			return;
		prepareCheckPoint(context, ROUTE_8);
		List<StopPoint> points = new ArrayList<StopPoint>(route.getStopPoints());
		for (Iterator<StopPoint> iterator = points.iterator(); iterator.hasNext();) {
			StopPoint stopPoint = iterator.next();
			if (stopPoint == null)
				iterator.remove();
		}
		for (JourneyPattern jp : route.getJourneyPatterns()) {
			points.removeAll(jp.getStopPoints());
			if (points.isEmpty())
				break; // useless to continue as soon as all points are found
		}
		if (!points.isEmpty()) {
			// failure encountered, add route 1
			DataLocation[] targets = new DataLocation[points.size()];

			int i = 0;
			for (StopPoint stopPoint : points) {
				targets[i++] = buildLocation(context, stopPoint.getContainedInStopArea());
			}
			DataLocation location = buildLocation(context, route);
			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			reporter.addCheckPointReportError(context, ROUTE_8, location, Integer.toString(points.size()), null,
					targets);
		}
	}

	/**
	 * @param report
	 * @param route
	 */
	private void check3Route9(Context context, Route route) {
		// 3-Route-9 : check if one journeyPattern uses all stopPoints
		if (isEmpty(route.getJourneyPatterns()))
			return;
		prepareCheckPoint(context, ROUTE_9);
		boolean found = false;
		int count = route.getStopPoints().size();
		for (JourneyPattern jp : route.getJourneyPatterns()) {
			if (jp.getStopPoints().size() == count) {
				found = true;
				break;
			}
		}
		if (!found) {
			// failure encountered, add route 1
			DataLocation location = buildLocation(context, route);
			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			reporter.addCheckPointReportError(context, ROUTE_9, location);
		}
	}

}
