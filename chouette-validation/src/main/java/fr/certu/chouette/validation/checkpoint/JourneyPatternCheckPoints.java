package fr.certu.chouette.validation.checkpoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Setter;

import org.json.JSONObject;

import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.ICheckPointPlugin;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.ReportLocation;

public class JourneyPatternCheckPoints extends AbstractValidation implements ICheckPointPlugin<JourneyPattern>
{
	@Setter private VehicleJourneyCheckPoints vehicleJourneyCheckPoints;

	@Override
	public void check(List<JourneyPattern> beans, JSONObject parameters,
			PhaseReportItem report) 
	{
		if (isEmpty(beans)) return;
		// init checkPoints : add here all defined check points for this kind of object

		initCheckPoint(report, JOURNEY_PATTERN_1, CheckPointReportItem.SEVERITY.WARNING);

		// checkPoint is applicable
		for (int i = 0; i < beans.size(); i++)
		{
			JourneyPattern jp = beans.get(i);

			// 3-JourneyPattern-1 : check if two journey patterns use same stops
			checkJourneyPattern1(report, beans, i, jp);
			
			vehicleJourneyCheckPoints.check(jp.getVehicleJourneys(), parameters, report);
		}


	}

	private void checkJourneyPattern1(PhaseReportItem report,
			List<JourneyPattern> beans, int jpRank, JourneyPattern jp) 
	{
		// 3-JourneyPattern-1 : check if two journey patterns use same stops
		if (beans.size() <= 1) return;
		prepareCheckPoint(report, JOURNEY_PATTERN_1);
		int pointCount = jp.getStopPoints().size();
		for (int j = 0; j < beans.size() ; j++)
		{
			if (j != jpRank)
			{
				JourneyPattern jp2 = beans.get(j);
				if (pointCount != jp2.getStopPoints().size()) continue;
				if (jp.getStopPoints().equals(jp2.getStopPoints()))
				{
					ReportLocation location = new ReportLocation(jp);

					Map<String, Object> map = new HashMap<String, Object>();
					map.put("stopPointCount", pointCount);

					DetailReportItem detail = new DetailReportItem(JOURNEY_PATTERN_1,jp.getObjectId(), Report.STATE.WARNING, location,map);
					addValidationError(report, JOURNEY_PATTERN_1, detail);
				}
			}
		}


	}


}
