package fr.certu.chouette.validation.checkpoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import fr.certu.chouette.model.neptune.Facility;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.ICheckPointPlugin;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.ReportLocation;

public class FacilityCheckPoints extends AbstractValidation<Facility> implements
      ICheckPointPlugin<Facility>
{

   @Override
   public void check(List<Facility> beans, JSONObject parameters,
         PhaseReportItem report, Map<String, Object> context)
   {
      if (isEmpty(beans))
         return;
      // 3-Facility-1 : check localization (E)
      // 3-Facility-2 : check distance of facility with stop area ()

      initCheckPoint(report, FACILITY_1, CheckPointReportItem.SEVERITY.ERROR);
      initCheckPoint(report, FACILITY_2, CheckPointReportItem.SEVERITY.WARNING);
      prepareCheckPoint(report, FACILITY_1);
      prepareCheckPoint(report, FACILITY_2);

      for (int i = 0; i < beans.size(); i++)
      {
         Facility facility = beans.get(i);
         check3Facility1(report, facility);
         check3Facility2(report, facility, parameters);

      }
   }

   private void check3Facility1(PhaseReportItem report, Facility facility)
   {
      // 3-Facility-1 : check localization (E)
      if (!hasCoordinates(facility))
      {
         ReportLocation location = new ReportLocation(facility);
         Map<String, Object> map = new HashMap<String, Object>();
         map.put("name", facility.getName());
         DetailReportItem detail = new DetailReportItem(FACILITY_1,
               facility.getObjectId(), Report.STATE.ERROR, location, map);
         addValidationError(report, FACILITY_1, detail);
      }
   }

   private void check3Facility2(PhaseReportItem report, Facility facility,
         JSONObject parameters)
   {
      // 3-Facility-2 : check distance of facility with stop area ()
      if (!hasCoordinates(facility))
         return;
      if (facility.getContainedInStopArea() == null)
         return;
      StopArea area = facility.getContainedInStopArea();
      if (!hasCoordinates(area))
         return;
      long distanceMax = parameters.optLong(FACILITY_STOP_AREA_DISTANCE_MAX,
            300);
      double distance = distance(facility, area);
      if (distance > distanceMax)
      {
         ReportLocation location = new ReportLocation(facility);

         Map<String, Object> map = new HashMap<String, Object>();
         map.put("name", facility.getName());
         map.put("areaId", area.getObjectId());
         map.put("areaName", area.getName());
         map.put("distance", Integer.valueOf((int) distance));
         map.put("distanceLimit", Integer.valueOf((int) distanceMax));

         DetailReportItem detail = new DetailReportItem(FACILITY_2,
               facility.getObjectId(), Report.STATE.WARNING, location, map);
         addValidationError(report, FACILITY_2, detail);
      }

   }

}
