package fr.certu.chouette.validation.checkpoint;

import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.json.JSONObject;

import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;
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
 * <li>3-VehicleJourney-2 : check speed progression</li>
 * <li>3-VehicleJourney-3 : check if two journeys progress similarly</li>
 * <li>3-VehicleJourney-4 : check if each journey has minimum one timetable</li>
 * </ul>
 * 
 * 
 * @author michel
 *
 */
@Log4j
public class VehicleJourneyCheckPoints extends AbstractValidation implements ICheckPointPlugin<VehicleJourney>
{

	@Override
	public void check(List<VehicleJourney> beans, JSONObject parameters,
			PhaseReportItem report) 
	{
		if (isEmpty(beans)) return;
		// 3-VehicleJourney-1 : check if time progress correctly on each stop
		// 3-VehicleJourney-2 : check speed progression
		// 3-VehicleJourney-3 : check if two journeys progress similarly
		// 3-VehicleJourney-4 : check if each journey has minimum one timetable
		initCheckPoint(report, VEHICLE_JOURNEY_1, CheckPointReportItem.SEVERITY.WARNING);
		initCheckPoint(report, VEHICLE_JOURNEY_2, CheckPointReportItem.SEVERITY.WARNING);
		initCheckPoint(report, VEHICLE_JOURNEY_3, CheckPointReportItem.SEVERITY.WARNING);
		initCheckPoint(report, VEHICLE_JOURNEY_4, CheckPointReportItem.SEVERITY.WARNING);

		// checkPoint is applicable
		prepareCheckPoint(report, VEHICLE_JOURNEY_1);
		prepareCheckPoint(report, VEHICLE_JOURNEY_2);
		prepareCheckPoint(report, VEHICLE_JOURNEY_4);

		for (VehicleJourney vj: beans)
		{
			vj.sortVehicleJourneyAtStops(); // ensure scheduled times order
		}

		for (int i = 0; i < beans.size(); i++)
		{
			VehicleJourney vj = beans.get(i);

			// 3-VehicleJourney-1 : check if time progress correctly on each stop
			checkVehicleJourney1(report, vj, parameters);

			// 3-VehicleJourney-2 : check speed progression
			checkVehicleJourney2(report, vj, parameters);

			// 3-VehicleJourney-3 : check if two journeys progress similarly
			checkVehicleJourney3(report, beans, i, vj, parameters);

			// 3-VehicleJourney-4 : check if each journey has minimum one timetable
			checkVehicleJourney4(report, vj);

		}
	}


	private long diffTime(Time first, Time last) 
	{
		if (first == null || last == null) return Long.MIN_VALUE; // TODO
		long diff = last.getTime()/1000L - first.getTime() / 1000L;
		if (diff < 0) diff += 86400L; // step upon midnight : add one day in seconds  
		return diff;
	}

	private void checkVehicleJourney1(PhaseReportItem report, VehicleJourney vj, JSONObject parameters) 
	{
		// 3-VehicleJourney-1 : check if time progress correctly on each stop
		if (isEmpty(vj.getVehicleJourneyAtStops())) 
		{
			log.error("vehicleJourney "+vj.getObjectId()+" has no vehicleJourneyAtStop");
			return;
		}
		long maxDiffTime = parameters.optLong(INTER_STOP_DURATION_MAX,40);
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

	private void checkVehicleJourney2(PhaseReportItem report, VehicleJourney vj, JSONObject parameters) 
	{
		if (isEmpty(vj.getVehicleJourneyAtStops())) return;
		// 3-VehicleJourney-2 : check speed progression
		TransportModeNameEnum transportMode = getTransportMode(vj);
		long maxSpeed = getModeParameter(parameters,transportMode.toString(),SPEED_MAX); 
		long minSpeed = getModeParameter(parameters,transportMode.toString(),SPEED_MIN); 
		List<VehicleJourneyAtStop> vjasList = vj.getVehicleJourneyAtStops();
		for (int i = 1; i < vjasList.size(); i++) 
		{
			VehicleJourneyAtStop vjas0 = vjasList.get(i-1);
			VehicleJourneyAtStop vjas1 = vjasList.get(i);

			long diffTime =diffTime(vjas0.getDepartureTime(),vjas1.getArrivalTime());
			if (diffTime < 0) 
			{
				// chronologie inverse ou non définie
				ReportLocation location = new ReportLocation(vj);

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("firstStopRank", Integer.valueOf(i-1));
				map.put("firstStopName", vjas0.getStopPoint().getContainedInStopArea().getName());
				map.put("lastStopRank", Integer.valueOf(i));
				map.put("lastStopName", vjas1.getStopPoint().getContainedInStopArea().getName());

				DetailReportItem detail = new DetailReportItem(VEHICLE_JOURNEY_2+"_1",vj.getObjectId(), Report.STATE.WARNING, location,map);
				addValidationError(report, VEHICLE_JOURNEY_2, detail);
			}
			else
			{
				
				double distance = distance(vjas0.getStopPoint().getContainedInStopArea(),
						vjas1.getStopPoint().getContainedInStopArea());
				if (distance < 1) 
				{
					// arrêts superposés, vitesse non calculable
				}
				else
				{
					double speed = distance / (double) diffTime * 36 / 10 ; // (km/h)
					if (speed < minSpeed)
					{
						// trop lent
						ReportLocation location = new ReportLocation(vj);

						Map<String, Object> map = new HashMap<String, Object>();
						map.put("firstStopRank", Integer.valueOf(i-1));
						map.put("firstStopName", vjas0.getStopPoint().getContainedInStopArea().getName());
						map.put("lastStopRank", Integer.valueOf(i));
						map.put("lastStopName", vjas1.getStopPoint().getContainedInStopArea().getName());
						map.put("speed", Integer.valueOf((int) speed));
						map.put("speedLimit", Integer.valueOf((int) minSpeed));

						DetailReportItem detail = new DetailReportItem(VEHICLE_JOURNEY_2+"_2",vj.getObjectId(), Report.STATE.WARNING, location,map);
						addValidationError(report, VEHICLE_JOURNEY_2, detail);
					}
					else if (speed > maxSpeed)
					{
						// trop rapide
						ReportLocation location = new ReportLocation(vj);

						Map<String, Object> map = new HashMap<String, Object>();
						map.put("firstStopRank", Integer.valueOf(i-1));
						map.put("firstStopName", vjas0.getStopPoint().getContainedInStopArea().getName());
						map.put("lastStopRank", Integer.valueOf(i));
						map.put("lastStopName", vjas1.getStopPoint().getContainedInStopArea().getName());
						map.put("speed", Integer.valueOf((int) speed));
						map.put("speedLimit", Integer.valueOf((int) maxSpeed));

						DetailReportItem detail = new DetailReportItem(VEHICLE_JOURNEY_2+"_3",vj.getObjectId(), Report.STATE.WARNING, location,map);
						addValidationError(report, VEHICLE_JOURNEY_2, detail);
					}
				}
			}
		}


	}

	/**
	 * @param vj
	 * @return
	 */
	private TransportModeNameEnum getTransportMode(VehicleJourney vj) {
		TransportModeNameEnum transportMode = vj.getTransportMode();
		if (transportMode == null)
		{
			transportMode = vj.getRoute().getLine().getTransportModeName();
			if (transportMode == null) transportMode = TransportModeNameEnum.Other;
		}
		return transportMode;
	}

	private void checkVehicleJourney3(PhaseReportItem report,
			List<VehicleJourney> beans, int rank, VehicleJourney vj0, JSONObject parameters) 
	{
		if (isEmpty(vj0.getVehicleJourneyAtStops())) return;
		// 3-VehicleJourney-3 : check if two journeys progress similarly

		TransportModeNameEnum transportMode0 = getTransportMode(vj0);

		prepareCheckPoint(report, VEHICLE_JOURNEY_3);
		long maxDuration = getModeParameter(parameters,transportMode0.toString(),INTER_STOP_DURATION_VARIATION_MAX); 

		List<VehicleJourneyAtStop> vjas0 = vj0.getVehicleJourneyAtStops();
		for (int i = rank+1; i < beans.size(); i++)
		{
			VehicleJourney vj1 = beans.get(i);
			List<VehicleJourneyAtStop> vjas1 = vj1.getVehicleJourneyAtStops();
			if (vjas1.size() != vjas0.size())
			{
				// FATAL ERROR : 
				log.error("vehicleJourney "+vj1.getObjectId()+" has different vehicleJourneyAtStop count "+vjas1.size()+ " than vehicleJourney "+vj0.getObjectId());
				continue;
			}
			TransportModeNameEnum transportMode1 = getTransportMode(vj1);
			if (transportMode1.equals(transportMode0))
			{
				for (int j = 1; j < vjas0.size(); j++)
				{
					long duration0 = diffTime(vjas0.get(j-1).getDepartureTime(), vjas0.get(j).getArrivalTime());
					long duration1 = diffTime(vjas1.get(j-1).getDepartureTime(), vjas1.get(j).getArrivalTime());
					if (Math.abs(duration0-duration1) > maxDuration)
					{
						ReportLocation location = new ReportLocation(vj0);

						Map<String, Object> map = new HashMap<String, Object>();
						map.put("vehicleJourneyId", vj1.getObjectId());
						map.put("firstStopRank", Integer.valueOf(j-1));
						map.put("firstStopName", vjas0.get(j-1).getStopPoint().getContainedInStopArea().getName());
						map.put("lastStopRank", Integer.valueOf(j));
						map.put("lastStopName", vjas0.get(j).getStopPoint().getContainedInStopArea().getName());
						map.put("variation", Math.abs(duration0-duration1));
						map.put("maxVariation", maxDuration);

						DetailReportItem detail = new DetailReportItem(VEHICLE_JOURNEY_3,vj0.getObjectId(), Report.STATE.WARNING, location,map);
						addValidationError(report, VEHICLE_JOURNEY_3, detail);
					}
				}
			}
		}

	}

	private void checkVehicleJourney4(PhaseReportItem report, VehicleJourney vj) 
	{
		// 3-VehicleJourney-4 : check if each journey has minimum one timetable
		if (isEmpty(vj.getTimetables()))
		{
			ReportLocation location = new ReportLocation(vj);
			DetailReportItem detail = new DetailReportItem(VEHICLE_JOURNEY_4,vj.getObjectId(), Report.STATE.WARNING, location);
			addValidationError(report, VEHICLE_JOURNEY_4, detail);

		}

	}


}
