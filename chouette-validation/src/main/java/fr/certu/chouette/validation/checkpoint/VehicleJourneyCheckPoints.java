package fr.certu.chouette.validation.checkpoint;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.ICheckPointPlugin;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.ReportLocation;

/**
 * check a group of coherent vehicle journeys (i.e. on the same journey pattern)
 * <ul>
 * <li>3-VehicleJourney-1 : check if time progress correctly on each stop</li>
 * <li>3-VehicleJourney-2 : check if time progress correctly along journey</li>
 * <li>3-VehicleJourney-3 : check speed progression</li>
 * <li>3-VehicleJourney-4 : check if two journeys progress similarly</li>
 * <li>3-VehicleJourney-5 : check if each journey has minimum one timetable</li>
 * </ul>
 * 
 * 
 * @author michel
 *
 */
public class VehicleJourneyCheckPoints extends AbstractValidation implements ICheckPointPlugin<VehicleJourney>
{

	@Override
	public void check(List<VehicleJourney> beans, JSONObject parameters,
			PhaseReportItem report) 
	{
		if (isEmpty(beans)) return;

		initCheckPoint(report, VEHICLE_JOURNEY_1, CheckPointReportItem.SEVERITY.WARNING);
		initCheckPoint(report, VEHICLE_JOURNEY_2, CheckPointReportItem.SEVERITY.WARNING);
		initCheckPoint(report, VEHICLE_JOURNEY_3, CheckPointReportItem.SEVERITY.WARNING);
		initCheckPoint(report, VEHICLE_JOURNEY_4, CheckPointReportItem.SEVERITY.WARNING);
		initCheckPoint(report, VEHICLE_JOURNEY_5, CheckPointReportItem.SEVERITY.WARNING);

		List<Double> distances = prepareDistance(beans.get(0).getJourneyPattern());
		if (distances.size() == 0) return;

		// checkPoint is applicable
		prepareCheckPoint(report, VEHICLE_JOURNEY_1);
		prepareCheckPoint(report, VEHICLE_JOURNEY_2);
		prepareCheckPoint(report, VEHICLE_JOURNEY_3);
		prepareCheckPoint(report, VEHICLE_JOURNEY_5);

		for (int i = 0; i < beans.size(); i++)
		{
			VehicleJourney vj = beans.get(i);

			// 3-VehicleJourney-1 : check if time progress correctly on each stop
			checkVehicleJourney1(report, vj, parameters);

			// 3-VehicleJourney-2 : check if time progress correctly along journey
			checkVehicleJourney2(report, vj, parameters, distances);

			// 3-VehicleJourney-3 : check speed progression
			checkVehicleJourney3(report, vj, parameters, distances);

			// 3-VehicleJourney-4 : check if two journeys progress similarly
			checkVehicleJourney4(report, beans, i, vj, parameters);

			// 3-VehicleJourney-5 : check if each journey has minimum one timetable
			checkVehicleJourney5(report, vj);

		}
	}

	/**
	 * calculate distance between each stops 
	 * 
	 * @param jp
	 * @return
	 */
	private List<Double> prepareDistance(JourneyPattern jp) 
	{
		List<Double> distances = new ArrayList<Double>();
		if (jp != null) 
		{
			List<StopPoint> stops = jp.getStopPoints();
			if (!isEmpty(stops))
			{
				StopArea first = stops.get(0).getContainedInStopArea();
				for (int i = 1; i < stops.size(); i++)
				{
					StopArea next = stops.get(i).getContainedInStopArea();
					double distance = distance(first, next);
					distances.add(Double.valueOf(distance));
				}
			}
		}
		return distances;
	}

	private long diffTime(Time first, Time last) 
	{
		if (first == null || last == null) return Long.MAX_VALUE; // TODO
		long diff = last.getTime()/1000L - first.getTime() / 1000L;
		if (diff < 0) diff += 86400L; // step upon midnight : add one day in seconds  
		return diff;
	}

	private void checkVehicleJourney1(PhaseReportItem report, VehicleJourney vj, JSONObject parameters) 
	{
		// 3-VehicleJourney-1 : check if time progress correctly on each stop
		if (isEmpty(vj.getVehicleJourneyAtStops())) return;
		long maxDiffTime = parameters.optLong("inter_stop_duration_max",40);
		List<VehicleJourneyAtStop> vjasList = vj.getVehicleJourneyAtStops();
		int rank = 0;
		for (VehicleJourneyAtStop vjas : vjasList) 
		{
			long diffTime = Math.abs(diffTime(vjas.getArrivalTime(),vjas.getDepartureTime()));
			if (diffTime > maxDiffTime)
			{
				ReportLocation location = new ReportLocation(vj);

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("stopRank", rank);
				map.put("stopName", vjas.getStopPoint().getContainedInStopArea().getName());
				map.put("diffTime", diffTime);
				map.put("maxDiffTime", maxDiffTime);

				DetailReportItem detail = new DetailReportItem(VEHICLE_JOURNEY_1,vj.getObjectId(), Report.STATE.WARNING, location,map);
				addValidationError(report, VEHICLE_JOURNEY_1, detail);
			}
		}

	}

	private void checkVehicleJourney2(PhaseReportItem report, VehicleJourney vj, JSONObject parameters, List<Double> distances) 
	{
		if (isEmpty(vj.getVehicleJourneyAtStops())) return;
		// 3-VehicleJourney-2 : check if time progress correctly along journey

		
	}

	private void checkVehicleJourney3(PhaseReportItem report, VehicleJourney vj, JSONObject parameters, List<Double> distances) 
	{
		if (isEmpty(vj.getVehicleJourneyAtStops())) return;
		// 3-VehicleJourney-3 : check speed progression
//		long maxSpeed = parameters.optLong("inter_stop_duration_max",40);
//		List<VehicleJourneyAtStop> vjasList = vj.getVehicleJourneyAtStops();
//		int rank = 0;
//		for (VehicleJourneyAtStop vjas : vjasList) 
//		{
//			long diffTime = Math.abs(diffTime(vjas.getArrivalTime(),vjas.getDepartureTime()));
//			if (diffTime > maxDiffTime)
//			{
//				ReportLocation location = new ReportLocation(vj);
//
//				Map<String, Object> map = new HashMap<String, Object>();
//				map.put("stopRank", rank);
//				map.put("stopName", vjas.getStopPoint().getContainedInStopArea().getName());
//				map.put("diffTime", diffTime);
//				map.put("maxDiffTime", maxDiffTime);
//
//				DetailReportItem detail = new DetailReportItem(VEHICLE_JOURNEY_1,vj.getObjectId(), Report.STATE.WARNING, location,map);
//				addValidationError(report, VEHICLE_JOURNEY_1, detail);
//			}
//		}
		

	}

	private void checkVehicleJourney4(PhaseReportItem report,
			List<VehicleJourney> beans, int i, VehicleJourney vj, JSONObject parameters) 
	{
		if (isEmpty(vj.getVehicleJourneyAtStops())) return;
		// 3-VehicleJourney-4 : check if two journeys progress similarly

		prepareCheckPoint(report, VEHICLE_JOURNEY_4);
	}

	private void checkVehicleJourney5(PhaseReportItem report, VehicleJourney vj) 
	{
		// 3-VehicleJourney-5 : check if each journey has minimum one timetable
		if (isEmpty(vj.getTimetables()))
		{
			ReportLocation location = new ReportLocation(vj);

			//			Map<String, Object> map = new HashMap<String, Object>();
			//			map.put("stopPointCount", pointCount);

			DetailReportItem detail = new DetailReportItem(VEHICLE_JOURNEY_5,vj.getObjectId(), Report.STATE.WARNING, location);
			addValidationError(report, VEHICLE_JOURNEY_5, detail);

		}

	}


}
