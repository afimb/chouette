package fr.certu.chouette.validation.checkpoint;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;
import org.trident.schema.trident.ChouetteAreaType;

import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.ICheckPointPlugin;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.ReportLocation;

public class StopAreaCheckPoints extends AbstractValidation implements ICheckPointPlugin<StopArea>
{

	@Override
	public void check(List<StopArea> beans, JSONObject parameters,
			PhaseReportItem report) 
	{
		if (isEmpty(beans)) return;
		// init checkPoints : add here all defined check points for this kind of object
		// 3-StopArea-1 : check if all non ITL stopArea has geolocalization
		// 3-StopArea-2 : check distance of stop areas with different name
		// 3-StopArea-3 : check multiple occurrence of a stopArea 
		// 3-StopArea-4 : check localization in a region
		// 3-StopArea-5 : check distance with parents
		initCheckPoint(report, STOP_AREA_1, CheckPointReportItem.SEVERITY.ERROR);
		initCheckPoint(report, STOP_AREA_2, CheckPointReportItem.SEVERITY.WARNING);
		initCheckPoint(report, STOP_AREA_3, CheckPointReportItem.SEVERITY.WARNING);
		initCheckPoint(report, STOP_AREA_4, CheckPointReportItem.SEVERITY.WARNING);
		initCheckPoint(report, STOP_AREA_5, CheckPointReportItem.SEVERITY.WARNING);
		prepareCheckPoint(report, STOP_AREA_1);
		prepareCheckPoint(report, STOP_AREA_2);
		prepareCheckPoint(report, STOP_AREA_3);
		prepareCheckPoint(report, STOP_AREA_4);
		prepareCheckPoint(report, STOP_AREA_5);

		Polygon enveloppe = getEnveloppe(parameters);

		for (int i = 0; i < beans.size(); i++)
		{
			StopArea stopArea = beans.get(i);
			// no test for ITL
			if (stopArea.getAreaType().equals(ChouetteAreaType.ITL)) continue;
			checkStopArea1(report, stopArea);
			checkStopArea4(report, stopArea, enveloppe);
			checkStopArea5(report, stopArea, parameters);
			for (int j = i+1; j < beans.size(); j++)
			{
				checkStopArea2(report,  i, stopArea, j, beans.get(j), parameters);
				checkStopArea3(report,  i, stopArea, j, beans.get(j));
			}

		}
	}


	private void checkStopArea1(PhaseReportItem report, StopArea stopArea) 
	{
		// 3-StopArea-1 : check if all non ITL stopArea has geolocalization
		if (!hasCoordinates(stopArea)) 
		{
			ReportLocation location = new ReportLocation(stopArea);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", stopArea.getName());
			DetailReportItem detail = new DetailReportItem(STOP_AREA_1, stopArea.getObjectId(), Report.STATE.ERROR, location, map);
			addValidationError(report, STOP_AREA_1, detail);
		}
	}

	private void checkStopArea2(PhaseReportItem report, 
			int rank, StopArea stopArea, int rank2, StopArea stopArea2, JSONObject parameters) {
		// 3-StopArea-2 : check distance of stop areas with different name
		if (!hasCoordinates(stopArea)) return; 
		long distanceMin = parameters.optLong(INTER_STOP_AREA_DISTANCE_MIN,20);
		ChouetteAreaEnum type = stopArea.getAreaType();
		if (type.equals(ChouetteAreaEnum.BOARDINGPOSITION) || type.equals(ChouetteAreaEnum.QUAY))
		{
			if (!stopArea2.getAreaType().equals(type)) return;
			if (!hasCoordinates(stopArea2)) return;
			if (stopArea.getName().equals(stopArea2.getName())) return;
			double distance = distance(stopArea, stopArea2);
			if (distance < distanceMin)
			{
				ReportLocation location = new ReportLocation(stopArea);

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("name", stopArea.getName());
				map.put("areaId", stopArea2.getObjectId());
				map.put("areaName", stopArea2.getName());
				map.put("distance", Integer.valueOf((int) distance));
				map.put("distanceLimit", Integer.valueOf((int) distanceMin));

				DetailReportItem detail = new DetailReportItem(STOP_AREA_2,stopArea.getObjectId(), Report.STATE.WARNING, location,map);
				addValidationError(report, STOP_AREA_2, detail);
			}

		}

	}

	private void checkStopArea3(PhaseReportItem report, 
			int rank, StopArea stopArea, int rank2, StopArea stopArea2) 
	{
		// 3-StopArea-3 : check multiple occurrence of a stopArea  of same type
		if (!stopArea2.getAreaType().equals(stopArea.getAreaType())) return;
		// same name; same code; same address ...
		if (!stopArea.getName().equals(stopArea2.getName())) return;
		if (stopArea.getStreetName() != null && !stopArea.getStreetName().equals(stopArea2.getStreetName())) return;
		if (stopArea.getCountryCode() != null && !stopArea.getCountryCode().equals(stopArea2.getCountryCode())) return;
		Collection<Line> lines = getLines(stopArea);
		Collection<Line> lines2 = getLines(stopArea2);
		if (lines.containsAll(lines2) && lines2.containsAll(lines))
		{
			ReportLocation location = new ReportLocation(stopArea);

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", stopArea.getName());
			map.put("areaId", stopArea2.getObjectId());

			DetailReportItem detail = new DetailReportItem(STOP_AREA_3,stopArea.getObjectId(), Report.STATE.WARNING, location,map);
			addValidationError(report, STOP_AREA_3, detail);
		}

	}

	private void checkStopArea4(PhaseReportItem report, StopArea stopArea,
			Polygon enveloppe) 
	{
		// 3-StopArea-4 : check localization in a region
		if (!hasCoordinates(stopArea)) return; 
		Point p = buildPoint(stopArea);
		if (!enveloppe.contains(p))
		{
			ReportLocation location = new ReportLocation(stopArea);

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", stopArea.getName());

			DetailReportItem detail = new DetailReportItem(STOP_AREA_4,stopArea.getObjectId(), Report.STATE.WARNING, location,map);
			addValidationError(report, STOP_AREA_4, detail);
		}


	}

	private void checkStopArea5(PhaseReportItem report, StopArea stopArea,
			JSONObject parameters) 
	{
		// 3-StopArea-5 : check distance with parents
		if (!hasCoordinates(stopArea)) return; 
		long distanceMax = parameters.optLong(PARENT_STOP_AREA_DISTANCE_MAX,300);
		StopArea stopArea2 = stopArea.getParent();
		if (!hasCoordinates(stopArea2)) return; 
		double distance = distance(stopArea, stopArea2);
		if (distance > distanceMax)
		{
			ReportLocation location = new ReportLocation(stopArea);

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", stopArea.getName());
			map.put("parentId", stopArea2.getObjectId());
			map.put("ParentName", stopArea2.getName());
			map.put("distance", Integer.valueOf((int) distance));
			map.put("distanceLimit", Integer.valueOf((int) distanceMax));

			DetailReportItem detail = new DetailReportItem(STOP_AREA_5,stopArea.getObjectId(), Report.STATE.WARNING, location,map);
			addValidationError(report, STOP_AREA_5, detail);
		}
	}

	private Collection<Line> getLines(StopArea area)
	{
		Set<Line> lines = new HashSet<Line>();
		if (area.getAreaType().equals(ChouetteAreaEnum.BOARDINGPOSITION) || area.getAreaType().equals(ChouetteAreaEnum.QUAY))
		{
			for (StopPoint point : area.getContainedStopPoints())
			{
				lines.add(point.getRoute().getLine());
			}
		}
		else
		{
			for (StopArea child : area.getContainedStopAreas()) 
			{
				lines.addAll(getLines(child));
			}
		}
		return lines;
	}



}
