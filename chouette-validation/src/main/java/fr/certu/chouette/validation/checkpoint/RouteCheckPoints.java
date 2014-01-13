package fr.certu.chouette.validation.checkpoint;

import java.util.List;

import lombok.extern.log4j.Log4j;

import org.json.JSONObject;

import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.ICheckPointPlugin;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.ReportLocation;

@Log4j
public class RouteCheckPoints extends AbstractValidation implements ICheckPointPlugin<Route>
{

	@Override
	public void check(List<Route> beans, JSONObject parameters,
			PhaseReportItem report) 
	{
		// init checkPoints : add here all defined check points for this kind of object
		// 3-Route-1 : check if two successive stops are in same area
		// 3-Route-2 : check if two wayback routes are actually waybacks
		// 3-Route-3 : check distance between stops 
		// 3-Route-4 : check identical routes
		// 3-Route-5 : check for potentially waybacks
		// 3-Route-6 : check if route has minimum 2 StopPoints
		// 3-Route-7 : check if route has minimum 1 JourneyPattern

		initCheckPoint(report, ROUTE_1, CheckPointReportItem.SEVERITY.WARNING);
		initCheckPoint(report, ROUTE_2, CheckPointReportItem.SEVERITY.WARNING);
		initCheckPoint(report, ROUTE_3, CheckPointReportItem.SEVERITY.WARNING);
		initCheckPoint(report, ROUTE_4, CheckPointReportItem.SEVERITY.WARNING);
		initCheckPoint(report, ROUTE_5, CheckPointReportItem.SEVERITY.WARNING);
		initCheckPoint(report, ROUTE_6, CheckPointReportItem.SEVERITY.ERROR);
		initCheckPoint(report, ROUTE_7, CheckPointReportItem.SEVERITY.ERROR);

		if (beans.size() > 0)
		{
			// checkPoint is applicable
			prepareCheckPoint(report, ROUTE_6);
			prepareCheckPoint(report, ROUTE_7);

			// en cas d'erreur, on reporte autant de detail que de route en erreur
			for (int i = 0; i < beans.size(); i++)
			{
				Route route = beans.get(i);

				// 3-Route-1 : check if two successive stops are in same area
				checkRoute1(report, route);

				// 3-Route-2 : check if two wayback routes are actually waybacks
				checkRoute2(report, route);

				// 3-Route-3 : check distance between stops 
				checkRoute3(report, route, parameters);

				// 3-Route-4 : check identical routes
				checkRoute4(report, beans, i, route);

				// 3-Route-5 : check for potentially waybacks
				checkRoute5(report, beans, i, route);

				// 3-Route-6 : check if route has minimum 2 StopPoints
				checkRoute6(report, route);

				// 3-Route-7 : check if route has minimum 1 JourneyPattern
				checkRoute7(report, route);

			}
		}

	}

	/**
	 * @param report
	 * @param route
	 * @param areas
	 */
	private void checkRoute1(PhaseReportItem report, Route route) 
	{
		prepareCheckPoint(report, ROUTE_1);

		List<StopArea> areas = route.getStopAreas();
		for (int j = 1; j < areas.size(); j++)
		{
			if (areas.get(j-1).equals(areas.get(j)))
			{
				// failure encountered, add route 1
				ReportLocation location = new ReportLocation(route);
				DetailReportItem detail = new DetailReportItem(ROUTE_1, route.getObjectId(), Report.STATE.WARNING, location);
				addValidationError(report, ROUTE_1, detail);
				break;
			}
		}

	}
	/**
	 * @param report
	 * @param route
	 * @param areas
	 */
	private void checkRoute2(PhaseReportItem report, Route route) 
	{
		List<StopArea> areas = route.getStopAreas();
		// test can be passed if areas exist and have parents
		if (areas.isEmpty()) return;
		StopArea first = areas.get(0).getParent();
		StopArea last = areas.get(areas.size()-1).getParent();
		if (first == null || last == null) return;
		// test can be passed if route has wayback
		if (route.getWayBackRoute() != null)
		{
			Route routeWb = route.getWayBackRoute();
			List<StopArea> areasWb = routeWb.getStopAreas();
			// test can be passed if wayback areas exist and have parents
			if (!areasWb.isEmpty())
			{
				StopArea firstWb = areasWb.get(0).getParent();
				StopArea lastWb = areasWb.get(areasWb.size()-1).getParent();
				if (firstWb == null || lastWb == null) return;
				prepareCheckPoint(report, ROUTE_2);
				if (first.equals(lastWb) && last.equals(firstWb)) return; // test ok
				// failure encountered, add route 1
				ReportLocation location = new ReportLocation(route);
				DetailReportItem detail = new DetailReportItem(ROUTE_2,route.getObjectId(), Report.STATE.WARNING, location);
				addValidationError(report, ROUTE_2, detail);
			}

		}
	}

	private void checkRoute3(PhaseReportItem report, Route route, JSONObject parameters) 
	{
		// check distance 
		prepareCheckPoint(report, ROUTE_3);
		// find transportMode : 
		String modeKey = route.getLine().getTransportModeName().toString();
		modeKey = MODE_PREFIX+toUnderscore(modeKey);
		JSONObject mode = parameters.optJSONObject(modeKey);
		if (mode == null)
		{
			log.error("no parameters for mode "+modeKey);
			mode = parameters.optJSONObject(MODE_OTHER);
			if (mode == null) 
			{
				log.error("no parameters for mode "+MODE_OTHER);
				mode = mode_default;
			}
		}
		double distanceMin = mode.getLong(INTER_STOP_AREA_DISTANCE_MIN);
		double distanceMax = mode.getLong(INTER_STOP_AREA_DISTANCE_MAX);

		List<StopArea> areas = route.getStopAreas();
		
		for (int i = 1; i < areas.size(); i++)
		{
			StopArea firstArea = areas.get(i-1);
			StopArea nextArea = areas.get(i);
			double distance = distance(firstArea, nextArea);
			if (distance < distanceMin) 
			{
				
			}
			if (distance > distanceMax)
			{
				
			}
		}
		

	}


	private void checkRoute4(PhaseReportItem report, List<Route> beans, int routeRank,
			Route route)
	{
		if (beans.size() <= 1) return;
		if (isEmpty(route.getStopPoints())) return;
		prepareCheckPoint(report, ROUTE_4);
		List<StopArea> areas = route.getStopAreas();
		for (int j = 0; j < beans.size() ; j++)
		{
			if (j != routeRank)
			{
				Route route2 = beans.get(j);
				if (isEmpty(route2.getStopPoints())) continue;
				List<StopArea> areas2 = route2.getStopAreas();
				// test can be passed if alternate route areas exist 
				if (!areas2.isEmpty())
				{
					if (areas.equals(areas2))
					{
						// Improvement encountered, add route 1 
						ReportLocation location = new ReportLocation(route);
						DetailReportItem detail = new DetailReportItem( ROUTE_4, route.getObjectId(), Report.STATE.WARNING, location);
						addValidationError(report, ROUTE_4, detail);
						break;
					}
				}
			}
		}

	}


	/**
	 * @param report 
	 * @param beans
	 * @param routeRank
	 * @param route
	 */
	private void checkRoute5(PhaseReportItem report, List<Route> beans, int routeRank, Route route) 
	{
		if (route.getWayBackRoute() != null) return;
		if (beans.size() <= 1) return;
		List<StopArea> areas = route.getStopAreas();
		// test can be passed if areas exist and have parents
		if (areas.isEmpty()) return;
		StopArea first = areas.get(0).getParent();
		StopArea last = areas.get(areas.size()-1).getParent();
		if (first == null || last == null) return;
		prepareCheckPoint(report, ROUTE_5);

		for (int j = 0; j < beans.size() ; j++)
		{
			if (j != routeRank)
			{
				Route routeWb = beans.get(j);
				if (routeWb.getWayBackRoute() !=  null) continue;
				List<StopArea> areasWb = routeWb.getStopAreas();
				// test can be passed if wayback areas exist and have parents
				if (!areasWb.isEmpty())
				{
					StopArea firstWb = areasWb.get(0).getParent();
					StopArea lastWb = areasWb.get(areasWb.size()-1).getParent();
					if (firstWb == null || lastWb == null) continue;
					if (firstWb.equals(last) && lastWb.equals(first))
					{
						// Improvement encountered, add route 1 
						ReportLocation location = new ReportLocation(route);
						DetailReportItem detail = new DetailReportItem(ROUTE_5, route.getObjectId(), Report.STATE.WARNING, location);
						addValidationError(report, ROUTE_5, detail);
						break;
					}
				}
			}
		}

	}


	/**
	 * @param report
	 * @param route
	 */
	private void checkRoute6(PhaseReportItem report, Route route) {
		if (isEmpty(route.getStopPoints()) || route.getStopPoints().size() < 2 )
		{
			// failure encountered, add route 1
			ReportLocation location = new ReportLocation(route);
			DetailReportItem detail = new DetailReportItem(ROUTE_6, route.getObjectId(), Report.STATE.WARNING, location);
			addValidationError(report, ROUTE_6, detail);					
		}
	}


	/**
	 * @param report
	 * @param route
	 */
	private void checkRoute7(PhaseReportItem report, Route route) {
		if (isEmpty(route.getJourneyPatterns()))
		{
			// failure encountered, add route 1
			ReportLocation location = new ReportLocation(route);
			DetailReportItem detail = new DetailReportItem(ROUTE_7, route.getObjectId(), Report.STATE.WARNING, location);
			addValidationError(report, ROUTE_7, detail);					
		}
	}




}
