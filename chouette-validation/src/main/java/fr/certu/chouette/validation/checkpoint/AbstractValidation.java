/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.validation.checkpoint;

import fr.certu.chouette.model.neptune.NeptuneLocalizedObject;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;

/**
 * @author michel
 *
 */
public abstract class AbstractValidation 
{
	
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
	protected static final String ROUTE_1 = "3-Route-1";
	protected static final String ROUTE_2 = "3-Route-2";
	protected static final String ROUTE_3 = "3-Route-3";
	protected static final String ROUTE_4 = "3-Route-4";
	protected static final String ROUTE_5 = "3-Route-5";
	protected static final String JOURNEY_PATTERN_1 = "3-JourneyPattern-1";
	protected static final String JOURNEY_PATTERN_2 = "3-JourneyPattern-2";
	protected static final String JOURNEY_PATTERN_3 = "3-JourneyPattern-3";
	protected static final String VEHICLE_JOURNEY_1 = "3-VehicleJourney-1";
	protected static final String VEHICLE_JOURNEY_2 = "3-VehicleJourney-2";
	protected static final String VEHICLE_JOURNEY_3 = "3-VehicleJourney-3";
	protected static final String VEHICLE_JOURNEY_4 = "3-VehicleJourney-4";
	protected static final String VEHICLE_JOURNEY_5 = "3-VehicleJourney-5";
	protected static final String FACILITY_1 = "3-Facility-1";
	protected static final String FACILITY_2 = "3-Facility-2";

	
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
		validationItem.addItem(new CheckPointReportItem(checkPointKey,order,Report.STATE.UNCHECK,CheckPointReportItem.SEVERITY.WARNING));
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

}
