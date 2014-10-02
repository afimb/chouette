package fr.certu.chouette.validation.checkpoint;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.ICheckPointPlugin;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.ReportLocation;

public class ConnectionLinkCheckPoints extends AbstractValidation implements
      ICheckPointPlugin<ConnectionLink>
{

   @Override
   public void check(List<ConnectionLink> beans, JSONObject parameters,
         PhaseReportItem report)
   {
      if (isEmpty(beans))
         return;
      // init checkPoints : add here all defined check points for this kind of
      // object
      // 3-ConnectionLink-1 : check distance between stops of connectionLink
      // 3-ConnectionLink-2 : check distance of link against distance between
      // stops of connectionLink
      // 3-ConnectionLink-3 : check speeds in connectionLink
      initCheckPoint(report, CONNECTION_LINK_1,
            CheckPointReportItem.SEVERITY.WARNING);
      initCheckPoint(report, CONNECTION_LINK_2,
            CheckPointReportItem.SEVERITY.WARNING);
      initCheckPoint(report, CONNECTION_LINK_3,
            CheckPointReportItem.SEVERITY.WARNING);
      prepareCheckPoint(report, CONNECTION_LINK_1);
      prepareCheckPoint(report, CONNECTION_LINK_2);
      prepareCheckPoint(report, CONNECTION_LINK_3);

      for (int i = 0; i < beans.size(); i++)
      {
         ConnectionLink connectionLink = beans.get(i);
         checkConnectionLink1_2(report, connectionLink, parameters);
         checkConnectionLink3(report, connectionLink, parameters);

      }
   }

   private void checkConnectionLink1_2(PhaseReportItem report,
         ConnectionLink connectionLink, JSONObject parameters)
   {
      // 3-ConnectionLink-1 : check distance between stops of connectionLink
      StopArea start = connectionLink.getStartOfLink();
      StopArea end = connectionLink.getEndOfLink();
      if (!hasCoordinates(start) || !hasCoordinates(end))
         return;
      long distanceMax = parameters.optLong(INTER_CONNECTION_LINK_DISTANCE_MAX,
            400);

      double distance = distance(start, end);
      if (distance > distanceMax)
      {
         ReportLocation location = new ReportLocation(connectionLink);

         Map<String, Object> map = new HashMap<String, Object>();
         map.put("name", connectionLink.getName());
         map.put("startId", start.getObjectId());
         map.put("startName", start.getName());
         map.put("endId", end.getObjectId());
         map.put("endName", end.getName());
         map.put("distance", Integer.valueOf((int) distance));
         map.put("distanceLimit", Integer.valueOf((int) distanceMax));

         DetailReportItem detail = new DetailReportItem(CONNECTION_LINK_1,
               connectionLink.getObjectId(), Report.STATE.WARNING, location,
               map);
         addValidationError(report, CONNECTION_LINK_1, detail);
      } else
      {
         // 3-ConnectionLink-2 : check distance of link against distance
         // between stops of connectionLink
         if (connectionLink.getLinkDistance() != null
               && !connectionLink.getLinkDistance().equals(BigDecimal.ZERO))
         {
            if (distance > connectionLink.getLinkDistance().doubleValue())
            {
               ReportLocation location = new ReportLocation(connectionLink);

               Map<String, Object> map = new HashMap<String, Object>();
               map.put("name", connectionLink.getName());
               map.put("startId", start.getObjectId());
               map.put("startName", start.getName());
               map.put("endId", end.getObjectId());
               map.put("endName", end.getName());
               map.put("distance", Integer.valueOf((int) distance));
               map.put("linkDistance", Integer.valueOf(connectionLink
                     .getLinkDistance().intValue()));

               DetailReportItem detail = new DetailReportItem(
                     CONNECTION_LINK_2, connectionLink.getObjectId(),
                     Report.STATE.WARNING, location, map);
               addValidationError(report, CONNECTION_LINK_2, detail);
            }
         }
      }

   }

   private void checkConnectionLink3(PhaseReportItem report,
         ConnectionLink connectionLink, JSONObject parameters)
   {
      // 3-ConnectionLink-3 : check speeds in connectionLink
      double distance = 1; // meters
      if (connectionLink.getLinkDistance() != null
            && !connectionLink.getLinkDistance().equals(BigDecimal.ZERO))
      {
         distance = connectionLink.getLinkDistance().doubleValue();
      }
      int maxDefaultSpeed = parameters.optInt(WALK_DEFAULT_SPEED_MAX, 4); // km/h
      int maxFrequentSpeed = parameters.optInt(
            WALK_FREQUENT_TRAVELLER_SPEED_MAX, 5);
      int maxMobilitySpeed = parameters.optInt(
            WALK_MOBILITY_RESTRICTED_TRAVELLER_SPEED_MAX, 1);
      int maxOccasionalSpeed = parameters.optInt(
            WALK_OCCASIONAL_TRAVELLER_SPEED_MAX, 2);

      checkLinkSpeed(report, connectionLink,
            connectionLink.getDefaultDuration(), distance, maxDefaultSpeed,
            CONNECTION_LINK_3, "_1");
      checkLinkSpeed(report, connectionLink,
            connectionLink.getOccasionalTravellerDuration(), distance,
            maxOccasionalSpeed, CONNECTION_LINK_3, "_2");
      checkLinkSpeed(report, connectionLink,
            connectionLink.getFrequentTravellerDuration(), distance,
            maxFrequentSpeed, CONNECTION_LINK_3, "_3");
      checkLinkSpeed(report, connectionLink,
            connectionLink.getMobilityRestrictedTravellerDuration(), distance,
            maxMobilitySpeed, CONNECTION_LINK_3, "_4");

   }

}
