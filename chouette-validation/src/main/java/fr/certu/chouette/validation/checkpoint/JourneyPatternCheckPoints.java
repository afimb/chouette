package fr.certu.chouette.validation.checkpoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.json.JSONObject;

import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.ICheckPointPlugin;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.ReportLocation;

@Log4j
public class JourneyPatternCheckPoints extends AbstractValidation<JourneyPattern> implements
ICheckPointPlugin<JourneyPattern>
{
   @Setter
   private VehicleJourneyCheckPoints vehicleJourneyCheckPoints;

   @Override
   public void check(List<JourneyPattern> beans, JSONObject parameters,
         PhaseReportItem report, Map<String, Object> context)
   {
      if (isEmpty(beans))
         return;
      // init checkPoints : add here all defined check points for this kind of
      // object

      initCheckPoint(report, JOURNEY_PATTERN_1,
            CheckPointReportItem.SEVERITY.WARNING);
      boolean test4_1 = (parameters.optInt(CHECK_OBJECT+OBJECT_KEY.journey_pattern.name(),0) != 0);
      if (test4_1)
      {
         initCheckPoint(report, L4_JOURNEY_PATTERN_1, CheckPointReportItem.SEVERITY.ERROR);
         prepareCheckPoint(report, L4_JOURNEY_PATTERN_1);
      }

      // checkPoint is applicable
      for (int i = 0; i < beans.size(); i++)
      {
         JourneyPattern jp = beans.get(i);

         // 3-JourneyPattern-1 : check if two journey patterns use same stops
         check3JourneyPattern1(report, beans, i, jp);

         // 4-JourneyPattern-1 : check columns constraints
         if (test4_1)
            check4Generic1(report,jp,L4_JOURNEY_PATTERN_1,OBJECT_KEY.journey_pattern,parameters,context,log );

         if (vehicleJourneyCheckPoints != null)
            vehicleJourneyCheckPoints.check(jp.getVehicleJourneys(),
                  parameters, report, context);
      }

   }

   private void check3JourneyPattern1(PhaseReportItem report,
         List<JourneyPattern> beans, int jpRank, JourneyPattern jp)
   {
      // 3-JourneyPattern-1 : check if two journey patterns use same stops
      if (beans.size() <= 1)
         return;
      prepareCheckPoint(report, JOURNEY_PATTERN_1);
      int pointCount = jp.getStopPoints().size();
      for (int j = jpRank + 1; j < beans.size(); j++)
      {
         JourneyPattern jp2 = beans.get(j);
         if (pointCount != jp2.getStopPoints().size())
            continue;
         if (jp.getStopPoints().equals(jp2.getStopPoints()))
         {
            ReportLocation location = new ReportLocation(jp);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("stopPointCount", pointCount);
            map.put("journeyPatternId", jp2.getObjectId());

            DetailReportItem detail = new DetailReportItem(JOURNEY_PATTERN_1,
                  jp.getObjectId(), Report.STATE.WARNING, location, map);
            addValidationError(report, JOURNEY_PATTERN_1, detail);
         }
      }

   }

}
