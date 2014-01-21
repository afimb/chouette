/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.validation.checkpoint;

import java.util.List;

import lombok.extern.log4j.Log4j;

import org.json.JSONObject;

import fr.certu.chouette.model.neptune.NeptuneLocalizedObject;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;

/**
 * @author michel
 *
 */
@Log4j
public abstract class AbstractValidation 
{
	// test keys
	protected static final String STOP_AREA_1 = "3-StopArea-1";
	protected static final String STOP_AREA_2 = "3-StopArea-2";
	protected static final String STOP_AREA_3 = "3-StopArea-3";
	protected static final String STOP_AREA_4 = "3-StopArea-4";
	protected static final String STOP_AREA_5 = "3-StopArea-5";
	protected static final String ACCESS_POINT_1 = "3-AccessPoint-1";
	protected static final String ACCESS_POINT_2 = "3-AccessPoint-2";
	protected static final String ACCESS_POINT_3 = "3-AccessPoint-3";
	protected static final String CONNECTION_LINK_1 = "3-ConnectionLink-1";
	protected static final String CONNECTION_LINK_2 = "3-ConnectionLink-2";
	protected static final String CONNECTION_LINK_3 = "3-ConnectionLink-3";
	protected static final String ACCESS_LINK_1 = "3-AccessLink-1";
	protected static final String ACCESS_LINK_2 = "3-AccessLink-2";
	protected static final String ACCESS_LINK_3 = "3-AccessLink-3";
	protected static final String LINE_1 = "3-Line-1";
	protected static final String LINE_2 = "3-Line-2";
	protected static final String ROUTE_1 = "3-Route-1";
	protected static final String ROUTE_2 = "3-Route-2";
	protected static final String ROUTE_3 = "3-Route-3";
	protected static final String ROUTE_4 = "3-Route-4";
	protected static final String ROUTE_5 = "3-Route-5";
	protected static final String ROUTE_6 = "3-Route-6";
	protected static final String ROUTE_7 = "3-Route-7";
	protected static final String ROUTE_8 = "3-Route-8";
	protected static final String ROUTE_9 = "3-Route-9";
	protected static final String JOURNEY_PATTERN_1 = "3-JourneyPattern-1";
	protected static final String VEHICLE_JOURNEY_1 = "3-VehicleJourney-1";
	protected static final String VEHICLE_JOURNEY_2 = "3-VehicleJourney-2";
	protected static final String VEHICLE_JOURNEY_3 = "3-VehicleJourney-3";
	protected static final String VEHICLE_JOURNEY_4 = "3-VehicleJourney-4";
	protected static final String VEHICLE_JOURNEY_5 = "3-VehicleJourney-5";
	protected static final String FACILITY_1 = "3-Facility-1";
	protected static final String FACILITY_2 = "3-Facility-2";

	// parameter keys
	protected static final String STOP_AREAS_AREA = "stop_areas_area";
	protected static final String INTER_STOP_AREA_DISTANCE_MIN = "inter_stop_area_distance_min";
	protected static final String PARENT_STOP_AREA_DISTANCE_MAX = "parent_stop_area_distance_max";
	protected static final String INTER_ACCESS_POINT_DISTANCE_MIN = "inter_access_point_distance_min";
	protected static final String INTER_CONNECTION_LINK_DISTANCE_MAX = "inter_connection_link_distance_max";
	protected static final String WALK_DEFAULT_SPEED_MAX = "walk_default_speed_max";
	protected static final String WALK_OCCASIONAL_TRAVELLER_SPEED_MAX = "walk_occasional_traveller_speed_max";
	protected static final String WALK_FREQUENT_TRAVELLER_SPEED_MAX = "walk_frequent_traveller_speed_max";
	protected static final String WALK_MOBILITY_RESTRICTED_TRAVELLER_SPEED_MAX = "walk_mobility_restricted_traveller_speed_max";
	protected static final String INTER_ACCESS_LINK_DISTANCE_MAX = "inter_access_link_distance_max";
	protected static final String INTER_STOP_DURATION_MAX = "inter_stop_duration_max";
	protected static final String FACILITY_STOP_AREA_DISTANCE_MAX = "facility_stop_area_distance_max";
	protected static final String MODE_PREFIX = "mode_";
	protected static final String MODE_OTHER = "mode_other";
	protected static final String INTER_STOP_AREA_DISTANCE_MAX = "inter_stop_area_distance_max";
	protected static final String SPEED_MAX = "speed_max";
	protected static final String SPEED_MIN = "speed_min";
	protected static final String INTER_STOP_DURATION_VARIATION_MAX = "inter_stop_duration_variation_max";

	protected static final JSONObject mode_default = new JSONObject(" {"+
			"\"inter_stop_area_distance_min\": 300, "+
			"\"inter_stop_area_distance_max\": 30000, "+
			"\"speed_max\": 40, "+
			"\"speed_min\": 10, "+
			"\"inter_stop_duration_variation_max\": 10 "+
			"}");


	/**
	 * create checkPoint entry with status uncheck
	 * 
	 * @param validationItem
	 * @param checkPointKey
	 * @param severity
	 */
	protected void initCheckPoint(PhaseReportItem validationItem, String checkPointKey, CheckPointReportItem.SEVERITY severity) 
	{
		int order = validationItem.hasItems()? validationItem.getItems().size() : 0  ;
		validationItem.addItem(new CheckPointReportItem(checkPointKey,order,Report.STATE.UNCHECK,severity));
	}

	/**
	 * pass checkpoint to ok if uncheck
	 * 
	 * @param validationReport
	 * @param checkPointKey
	 */
	protected void prepareCheckPoint(PhaseReportItem validationReport,String checkPointKey)
	{
		CheckPointReportItem checkPoint = validationReport.getItem(checkPointKey);
		if (!checkPoint.hasItems()) checkPoint.setStatus(Report.STATE.OK);
	}

	/**
	 * add a detail on a checkpoint 
	 * 
	 * @param validationReport
	 * @param checkPointKey
	 * @param item
	 */
	protected void addValidationError(PhaseReportItem validationReport,String checkPointKey,DetailReportItem item)
	{
		CheckPointReportItem checkPoint = validationReport.getItem(checkPointKey);
		checkPoint.addItem(item);

	}


	/**
	 * calculate distance on spheroid
	 * 
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	protected double distance(NeptuneLocalizedObject obj1,NeptuneLocalizedObject obj2)
	{
		double long1rad = Math.toRadians(obj1.getLongitude().doubleValue());
		double lat1rad = Math.toRadians(obj1.getLatitude().doubleValue());
		double long2rad = Math.toRadians(obj2.getLongitude().doubleValue());
		double lat2rad = Math.toRadians(obj2.getLatitude().doubleValue());

		double alpha = Math.cos(lat1rad)*Math.cos(lat2rad)*Math.cos(long2rad-long1rad) + Math.sin(lat1rad)*Math.sin(lat2rad);

		double distance = 6378. * Math.acos(alpha);

		return distance * 1000.;
	}

	protected boolean isEmpty(List<? extends Object> list)
	{
		return list == null || list.isEmpty();
	}

	protected String toUnderscore(String camelcase)
	{
		//		StringBuffer buffer = new StringBuffer();
		//		for (char car : camelcase.toCharArray()) 
		//		{
		//			if (Character.isUpperCase(car))
		//			{
		//				if (buffer.length() != 0) buffer.append("_");
		//				buffer.append(Character.toLowerCase(car));
		//			}
		//			else
		//			{
		//				buffer.append(car);
		//			}
		//		}

		return camelcase.replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
	}

	protected long getModeParameter(JSONObject parameters, String modeKey, String key)
	{
		// find transportMode : 
		modeKey = MODE_PREFIX+toUnderscore(modeKey);
		JSONObject mode = parameters.optJSONObject(modeKey);
		JSONObject modeOther = parameters.optJSONObject(MODE_OTHER);
		if (mode == null || !mode.has(key))
		{
			log.error("no parameter "+key+" for mode "+modeKey);
			mode = parameters.optJSONObject(MODE_OTHER);
			if (modeOther == null  || !modeOther.has(key)) 
			{
				log.error("no parameter "+key+" for mode "+MODE_OTHER);
				mode = mode_default;
			}
		}
		return mode.getLong(key);
	}
}
