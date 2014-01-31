package fr.certu.chouette.validation.checkpoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.ICheckPointPlugin;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.ReportLocation;

public class AccessPointCheckPoints extends AbstractValidation implements ICheckPointPlugin<AccessPoint>
{

	@Override
	public void check(List<AccessPoint> beans, JSONObject parameters,
			PhaseReportItem report) 
	{
		if (isEmpty(beans)) return;
		// init checkPoints : add here all defined check points for this kind of object
		// 3-AccessPoint-1 : check if all access points have geolocalization
		// 3-AccessPoint-2 : check distance of access points with different name
		// 3-AccessPoint-3 : check distance with parents 
		initCheckPoint(report, ACCESS_POINT_1, CheckPointReportItem.SEVERITY.ERROR);
		initCheckPoint(report, ACCESS_POINT_2, CheckPointReportItem.SEVERITY.WARNING);
		initCheckPoint(report, ACCESS_POINT_3, CheckPointReportItem.SEVERITY.WARNING);
		prepareCheckPoint(report, ACCESS_POINT_1);
		prepareCheckPoint(report, ACCESS_POINT_2);
		prepareCheckPoint(report, ACCESS_POINT_3);

		for (int i = 0; i < beans.size(); i++)
		{
			AccessPoint accessPoint = beans.get(i);
			checkAccessPoint1(report, accessPoint);
			checkAccessPoint3(report, accessPoint, parameters);
			for (int j = i+1; j < beans.size(); j++)
			{
				checkAccessPoint2(report,  i, accessPoint, j, beans.get(j), parameters);
			}

		}

	}

	private void checkAccessPoint1(PhaseReportItem report,
			AccessPoint accessPoint) {
		// 3-AccessPoint-1 : check if all access points have geolocalization

		if (!hasCoordinates(accessPoint)) 
		{
			ReportLocation location = new ReportLocation(accessPoint);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", accessPoint.getName());
			map.put("areaId", accessPoint.getContainedIn().getObjectId());
			map.put("areaName", accessPoint.getContainedIn().getName());
			DetailReportItem detail = new DetailReportItem(ACCESS_POINT_1, accessPoint.getObjectId(), Report.STATE.ERROR, location, map);
			addValidationError(report, ACCESS_POINT_1, detail);
		}
	}

	private void checkAccessPoint2(PhaseReportItem report, int i,
			AccessPoint accessPoint, int j, AccessPoint accessPoint2,
			JSONObject parameters) {
		// 3-AccessPoint-2 : check distance of access points with different name
		if (!hasCoordinates(accessPoint)) return; 
		long distanceMin = parameters.optLong(INTER_ACCESS_POINT_DISTANCE_MIN,20);
		if (!hasCoordinates(accessPoint2)) return;
		if (accessPoint.getName().equals(accessPoint2.getName())) return;
		double distance = distance(accessPoint, accessPoint2);
		if (distance < distanceMin)
		{
			ReportLocation location = new ReportLocation(accessPoint);

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", accessPoint.getName());
			map.put("accessId", accessPoint2.getObjectId());
			map.put("accessName", accessPoint2.getName());
			map.put("distance", Integer.valueOf((int) distance));
			map.put("distanceLimit", Integer.valueOf((int) distanceMin));

			DetailReportItem detail = new DetailReportItem(ACCESS_POINT_2,accessPoint.getObjectId(), Report.STATE.WARNING, location,map);
			addValidationError(report, ACCESS_POINT_2, detail);
		}

	}

	private void checkAccessPoint3(PhaseReportItem report,
			AccessPoint accessPoint, JSONObject parameters) {
		// 3-AccessPoint-3 : check distance with parents 
		if (!hasCoordinates(accessPoint)) return; 
		long distanceMax = parameters.optLong(PARENT_STOP_AREA_DISTANCE_MAX,300);
		StopArea stopArea = accessPoint.getContainedIn();
		if (!hasCoordinates(stopArea)) return; 
		double distance = distance(accessPoint, stopArea);
		if (distance > distanceMax)
		{
			ReportLocation location = new ReportLocation(accessPoint);

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", accessPoint.getName());
			map.put("parentId", stopArea.getObjectId());
			map.put("ParentName", stopArea.getName());
			map.put("distance", Integer.valueOf((int) distance));
			map.put("distanceLimit", Integer.valueOf((int) distanceMax));

			DetailReportItem detail = new DetailReportItem(ACCESS_POINT_3,accessPoint.getObjectId(), Report.STATE.WARNING, location,map);
			addValidationError(report, ACCESS_POINT_3, detail);
		}

	}


}
