package fr.certu.chouette.validation.checkpoint;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.json.JSONObject;

import fr.certu.chouette.model.neptune.AccessLink;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.ICheckPointPlugin;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.ReportLocation;

@Log4j
public class AccessLinkCheckPoints extends AbstractValidation<AccessLink> implements
ICheckPointPlugin<AccessLink>
{

   @Override
   public void check(List<AccessLink> beans, JSONObject parameters,
         PhaseReportItem report, Map<String,Object> context)
   {
      if (isEmpty(beans))
         return;
      // init checkPoints : add here all defined check points for this kind of
      // object
      // 3-AccessLink-1 : check distance between ends of AccessLink
      // 3-AccessLink-2 : check distance of link against distance between ends
      // of AccessLink
      // 3-AccessLink-3 : check speeds in AccessLink
      initCheckPoint(report, ACCESS_LINK_1,
            CheckPointReportItem.SEVERITY.WARNING);
      initCheckPoint(report, ACCESS_LINK_2,
            CheckPointReportItem.SEVERITY.WARNING);
      initCheckPoint(report, ACCESS_LINK_3,
            CheckPointReportItem.SEVERITY.WARNING);
      prepareCheckPoint(report, ACCESS_LINK_1);
      prepareCheckPoint(report, ACCESS_LINK_2);
      prepareCheckPoint(report, ACCESS_LINK_3);
      boolean test4_1 = (parameters.optInt(CHECK_OBJECT+OBJECT_KEY.access_link.name(),0) != 0);
      if (test4_1)
      {
         initCheckPoint(report, L4_ACCESSLINK_1, CheckPointReportItem.SEVERITY.ERROR);
         prepareCheckPoint(report, L4_ACCESSLINK_1);
      }


      for (int i = 0; i < beans.size(); i++)
      {
         AccessLink accessLink = beans.get(i);
         check3AccessLink1_2(report, accessLink, parameters);
         check3AccessLink3(report, accessLink, parameters);

         // 4-AccessLink-1 : check columns constraints
         if (test4_1)
            check4Generic1(report,accessLink,L4_ACCESSLINK_1,OBJECT_KEY.access_link,parameters,context,log );


      }
   }

   private void check3AccessLink1_2(PhaseReportItem report,
         AccessLink accessLink, JSONObject parameters)
   {
      // 3-AccessLink-1 : check distance between stops of accessLink
      StopArea start = accessLink.getStopArea();
      AccessPoint end = accessLink.getAccessPoint();
      if (start == null || end == null) return;
      if (!hasCoordinates(start) || !hasCoordinates(end))
         return;
      long distanceMax = parameters
            .optLong(INTER_ACCESS_LINK_DISTANCE_MAX, 400);

      double distance = distance(start, end);
      if (distance > distanceMax)
      {
         ReportLocation location = new ReportLocation(accessLink);

         Map<String, Object> map = new HashMap<String, Object>();
         map.put("name", accessLink.getName());
         map.put("startId", start.getObjectId());
         map.put("startName", start.getName());
         map.put("endId", end.getObjectId());
         map.put("endName", end.getName());
         map.put("distance", Integer.valueOf((int) distance));
         map.put("distanceLimit", Integer.valueOf((int) distanceMax));

         DetailReportItem detail = new DetailReportItem(ACCESS_LINK_1,
               accessLink.getObjectId(), Report.STATE.WARNING, location, map);
         addValidationError(report, ACCESS_LINK_1, detail);
      } else
      {
         // 3-AccessLink-2 : check distance of link against distance between
         // stops of accessLink
         if (accessLink.getLinkDistance() != null
               && !accessLink.getLinkDistance().equals(BigDecimal.ZERO))
         {
            if (distance > accessLink.getLinkDistance().doubleValue())
            {
               ReportLocation location = new ReportLocation(accessLink);

               Map<String, Object> map = new HashMap<String, Object>();
               map.put("name", accessLink.getName());
               map.put("startId", start.getObjectId());
               map.put("startName", start.getName());
               map.put("endId", end.getObjectId());
               map.put("endName", end.getName());
               map.put("distance", Integer.valueOf((int) distance));
               map.put("linkDistance",
                     Integer.valueOf(accessLink.getLinkDistance().intValue()));

               DetailReportItem detail = new DetailReportItem(ACCESS_LINK_2,
                     accessLink.getObjectId(), Report.STATE.WARNING, location,
                     map);
               addValidationError(report, ACCESS_LINK_2, detail);
            }
         }
      }

   }

   private void check3AccessLink3(PhaseReportItem report, AccessLink accessLink,
         JSONObject parameters)
   {
      // 3-AccessLink-3 : check speeds in accessLink
      double distance = 1; // meters
      if (accessLink.getLinkDistance() != null
            && !accessLink.getLinkDistance().equals(BigDecimal.ZERO))
      {
         distance = accessLink.getLinkDistance().doubleValue();
      }
      int maxDefaultSpeed = parameters.optInt(WALK_DEFAULT_SPEED_MAX, 4); // km/h
      int maxFrequentSpeed = parameters.optInt(
            WALK_FREQUENT_TRAVELLER_SPEED_MAX, 5);
      int maxMobilitySpeed = parameters.optInt(
            WALK_MOBILITY_RESTRICTED_TRAVELLER_SPEED_MAX, 1);
      int maxOccasionalSpeed = parameters.optInt(
            WALK_OCCASIONAL_TRAVELLER_SPEED_MAX, 2);

      checkLinkSpeed(report, accessLink, accessLink.getDefaultDuration(),
            distance, maxDefaultSpeed, ACCESS_LINK_3, "_1");
      checkLinkSpeed(report, accessLink,
            accessLink.getOccasionalTravellerDuration(), distance,
            maxOccasionalSpeed, ACCESS_LINK_3, "_2");
      checkLinkSpeed(report, accessLink,
            accessLink.getFrequentTravellerDuration(), distance,
            maxFrequentSpeed, ACCESS_LINK_3, "_3");
      checkLinkSpeed(report, accessLink,
            accessLink.getMobilityRestrictedTravellerDuration(), distance,
            maxMobilitySpeed, ACCESS_LINK_3, "_4");

   }

}
