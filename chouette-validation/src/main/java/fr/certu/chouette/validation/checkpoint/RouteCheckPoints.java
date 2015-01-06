package fr.certu.chouette.validation.checkpoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.json.JSONObject;

import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.ICheckPointPlugin;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.ReportLocation;

@Log4j
public class RouteCheckPoints extends AbstractValidation<Route> implements
ICheckPointPlugin<Route>
{

   @Setter
   private JourneyPatternCheckPoints journeyPatternCheckPoints;

   @Override
   public void check(List<Route> beans, JSONObject parameters,
         PhaseReportItem report, Map<String, Object> context)
   {
      if (isEmpty(beans))
         return;
      // init checkPoints : add here all defined check points for this kind of
      // object
      // 3-Route-1 : check if two successive stops are in same area
      // 3-Route-2 : check if two wayback routes are actually waybacks
      // 3-Route-3 : check distance between stops
      // 3-Route-4 : check identical routes
      // 3-Route-5 : check for potentially waybacks
      // 3-Route-6 : check if route has minimum 2 StopPoints
      // 3-Route-7 : check if route has minimum 1 JourneyPattern
      // 3-Route-8 : check if all stopPoints are used by journeyPatterns
      // 3-Route-9 : check if one journeyPattern uses all stopPoints

      initCheckPoint(report, ROUTE_1, CheckPointReportItem.SEVERITY.WARNING);
      initCheckPoint(report, ROUTE_2, CheckPointReportItem.SEVERITY.WARNING);
      initCheckPoint(report, ROUTE_3, CheckPointReportItem.SEVERITY.WARNING);
      initCheckPoint(report, ROUTE_4, CheckPointReportItem.SEVERITY.WARNING);
      initCheckPoint(report, ROUTE_5, CheckPointReportItem.SEVERITY.WARNING);
      initCheckPoint(report, ROUTE_6, CheckPointReportItem.SEVERITY.ERROR);
      initCheckPoint(report, ROUTE_7, CheckPointReportItem.SEVERITY.ERROR);
      initCheckPoint(report, ROUTE_8, CheckPointReportItem.SEVERITY.WARNING);
      initCheckPoint(report, ROUTE_9, CheckPointReportItem.SEVERITY.WARNING);

      // checkPoint is applicable
      prepareCheckPoint(report, ROUTE_6);
      prepareCheckPoint(report, ROUTE_7);

      boolean test4_1 = (parameters.optInt(CHECK_OBJECT+OBJECT_KEY.route.name(),0) != 0);
      if (test4_1)
      {
         initCheckPoint(report, L4_ROUTE_1, CheckPointReportItem.SEVERITY.ERROR);
         prepareCheckPoint(report, L4_ROUTE_1);
      }

      // en cas d'erreur, on reporte autant de detail que de route en erreur
      for (int i = 0; i < beans.size(); i++)
      {
         Route route = beans.get(i);

         // 3-Route-1 : check if two successive stops are in same area
         check3Route1(report, route);

         // 3-Route-2 : check if two wayback routes are actually waybacks
         check3Route2(report, route);

         // 3-Route-3 : check distance between stops
         check3Route3(report, route, parameters);

         // 3-Route-6 : check if route has minimum 2 StopPoints
         check3Route6(report, route);

         // 3-Route-7 : check if route has minimum 1 JourneyPattern
         check3Route7(report, route);

         // 3-Route-8 : check if all stopPoints are used by journeyPatterns
         check3Route8(report, route);

         // 3-Route-9 : check if one journeyPattern uses all stopPoints
         check3Route9(report, route);

         // 4-Route-1 : check columns constraints
         if (test4_1)
            check4Generic1(report,route,L4_ROUTE_1,OBJECT_KEY.route,parameters,context,log );

         for (int j = i + 1; j < beans.size(); j++)
         {
            // 3-Route-4 : check identical routes
            check3Route4(report, i, route, j, beans.get(j));

            // 3-Route-5 : check for potentially waybacks
            check3Route5(report, i, route, j, beans.get(j));
         }
         // chain on journeyPatterns
         if (journeyPatternCheckPoints != null)
         {
            journeyPatternCheckPoints.check(route.getJourneyPatterns(),
                  parameters, report, context);
         }
      }
   }

   /**
    * @param report
    * @param route
    * @param areas
    */
   private void check3Route1(PhaseReportItem report, Route route)
   {
      // 3-Route-1 : check if two successive stops are in same area
      prepareCheckPoint(report, ROUTE_1);

      List<StopArea> areas = route.getStopAreas();
      for (int j = 1; j < areas.size(); j++)
      {
         if (areas.get(j - 1).equals(areas.get(j)))
         {
            // failure encountered, add route 1
            ReportLocation location = new ReportLocation(route);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("areaId", areas.get(j).getObjectId());
            map.put("areaName", areas.get(j).getName());
            DetailReportItem detail = new DetailReportItem(ROUTE_1,
                  route.getObjectId(), Report.STATE.WARNING, location, map);
            addValidationError(report, ROUTE_1, detail);
            break;
         }
      }

   }

   /**
    * @param report
    * @param route
    * @param areas
    */
   private void check3Route2(PhaseReportItem report, Route route)
   {
      // 3-Route-2 : check if two wayback routes are actually waybacks
      List<StopArea> areas = route.getStopAreas();
      // test can be passed if areas exist and have parents
      if (areas.isEmpty())
         return;
      StopArea first = areas.get(0).getParent();
      StopArea last = areas.get(areas.size() - 1).getParent();
      if (first == null || last == null)
         return;
      // test can be passed if route has wayback
      if (route.getWayBackRoute() != null)
      {
         Route routeWb = route.getWayBackRoute();
         List<StopArea> areasWb = routeWb.getStopAreas();
         // test can be passed if wayback areas exist and have parents
         if (!areasWb.isEmpty())
         {
            StopArea firstWb = areasWb.get(0).getParent();
            StopArea lastWb = areasWb.get(areasWb.size() - 1).getParent();
            if (firstWb == null || lastWb == null)
               return;
            prepareCheckPoint(report, ROUTE_2);
            if (first.equals(lastWb) && last.equals(firstWb))
               return; // test ok
            // failure encountered, add route 1
            ReportLocation location = new ReportLocation(route);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("routeId", routeWb.getObjectId());
            if (!first.equals(lastWb))
            {
               map.put("firstId", first.getObjectId());
               map.put("firstName", first.getName());
               map.put("lastId", lastWb.getObjectId());
               map.put("lastName", lastWb.getName());
            } else
            {
               map.put("firstId", firstWb.getObjectId());
               map.put("firstName", firstWb.getName());
               map.put("lastId", last.getObjectId());
               map.put("lastName", last.getName());
            }
            DetailReportItem detail = new DetailReportItem(ROUTE_2,
                  route.getObjectId(), Report.STATE.WARNING, location, map);
            addValidationError(report, ROUTE_2, detail);
         }

      }
   }

   private void check3Route3(PhaseReportItem report, Route route,
         JSONObject parameters)
   {
      List<StopArea> areas = route.getStopAreas();
      if (isEmpty(areas)) return;
      // 3-Route-3 : check distance between stops
      prepareCheckPoint(report, ROUTE_3);
      // find transportMode :
      String modeKey = route.getLine().getTransportModeName().toString();
      modeKey = MODE_PREFIX + toUnderscore(modeKey);
      JSONObject mode = parameters.optJSONObject(modeKey);
      if (mode == null)
      {
         log.error("no parameters for mode " + modeKey);
         mode = parameters.optJSONObject(MODE_OTHER);
         if (mode == null)
         {
            log.error("no parameters for mode " + MODE_OTHER);
            mode = mode_default;
         }
      }
      double distanceMin = mode.getLong(INTER_STOP_AREA_DISTANCE_MIN);
      double distanceMax = mode.getLong(INTER_STOP_AREA_DISTANCE_MAX);


      for (int i = 1; i < areas.size(); i++)
      {
         StopArea firstArea = areas.get(i - 1);
         StopArea nextArea = areas.get(i);
         if (!firstArea.hasCoordinates() || !nextArea.hasCoordinates())
            continue;
         double distance = distance(firstArea, nextArea);
         if (distance < distanceMin)
         {
            ReportLocation location = new ReportLocation(route);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("firstStop", firstArea.getName());
            map.put("firstStopRank", Integer.valueOf(i - 1));
            map.put("nextStop", nextArea.getName());
            map.put("nextStopRank", Integer.valueOf(i));
            map.put("orientation", "<");
            map.put("distance", Integer.valueOf((int) distance));
            map.put("distanceLimit", Integer.valueOf((int) distanceMin));

            DetailReportItem detail = new DetailReportItem(ROUTE_3,
                  route.getObjectId(), Report.STATE.WARNING, location, map);
            addValidationError(report, ROUTE_3, detail);
            break; // do not check for oder stops in this route
         }
         if (distance > distanceMax)
         {
            ReportLocation location = new ReportLocation(route);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("firstStop", firstArea.getName());
            map.put("firstStopRank", Integer.valueOf(i - 1));
            map.put("nextStop", nextArea.getName());
            map.put("nextStopRank", Integer.valueOf(i));
            map.put("orientation", ">");
            map.put("distance", Integer.valueOf((int) distance));
            map.put("distanceLimit", Integer.valueOf((int) distanceMax));

            DetailReportItem detail = new DetailReportItem(ROUTE_3,
                  route.getObjectId(), Report.STATE.WARNING, location, map);
            addValidationError(report, ROUTE_3, detail);
            break; // do not check for oder stops in this route
         }
      }

   }

   private void check3Route4(PhaseReportItem report, int rank, Route route,
         int rank2, Route route2)
   {
      // 3-Route-4 : check identical routes
      if (isEmpty(route.getStopPoints()))
         return;
      prepareCheckPoint(report, ROUTE_4);
      List<StopArea> areas = route.getStopAreas();
      if (isEmpty(route2.getStopPoints()))
         return;

      List<StopArea> areas2 = route2.getStopAreas();
      // test can be passed if alternate route areas exist
      if (!areas2.isEmpty())
      {
         if (areas.equals(areas2))
         {
            // Improvement encountered, add route 1
            ReportLocation location = new ReportLocation(route);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("routeId", route2.getObjectId());
            DetailReportItem detail = new DetailReportItem(ROUTE_4,
                  route.getObjectId(), Report.STATE.WARNING, location, map);
            addValidationError(report, ROUTE_4, detail);
         }
      }

   }

   /**
    * @param report
    * @param beans
    * @param routeRank
    * @param route
    */
   private void check3Route5(PhaseReportItem report, int rank, Route route,
         int rankWb, Route routeWb)
   {
      // 3-Route-5 : check for potentially waybacks
      if (route.getWayBackRoute() != null)
         return;
      List<StopArea> areas = route.getStopAreas();
      // test can be passed if areas exist and have parents
      if (areas.isEmpty())
         return;
      StopArea first = areas.get(0).getParent();
      StopArea last = areas.get(areas.size() - 1).getParent();
      if (first == null || last == null)
         return;
      prepareCheckPoint(report, ROUTE_5);
      if (routeWb.getWayBackRoute() != null)
         return;
      List<StopArea> areasWb = routeWb.getStopAreas();
      // test can be passed if wayback areas exist and have parents
      if (!areasWb.isEmpty())
      {
         StopArea firstWb = areasWb.get(0).getParent();
         StopArea lastWb = areasWb.get(areasWb.size() - 1).getParent();
         if (firstWb == null || lastWb == null)
            return;
         if (firstWb.equals(last) && lastWb.equals(first))
         {
            // Improvement encountered
            ReportLocation location = new ReportLocation(route);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("routeId", routeWb.getObjectId());
            DetailReportItem detail = new DetailReportItem(ROUTE_5,
                  route.getObjectId(), Report.STATE.WARNING, location, map);
            addValidationError(report, ROUTE_5, detail);
         }
      }
   }

   /**
    * @param report
    * @param route
    */
   private void check3Route6(PhaseReportItem report, Route route)
   {
      // 3-Route-6 : check if route has minimum 2 StopPoints
      if (isEmpty(route.getStopPoints()) || route.getStopPoints().size() < 2)
      {
         // failure encountered, add route 1
         ReportLocation location = new ReportLocation(route);
         DetailReportItem detail = new DetailReportItem(ROUTE_6,
               route.getObjectId(), Report.STATE.ERROR, location);
         addValidationError(report, ROUTE_6, detail);
      }
   }

   /**
    * @param report
    * @param route
    */
   private void check3Route7(PhaseReportItem report, Route route)
   {
      // 3-Route-7 : check if route has minimum 1 JourneyPattern
      if (isEmpty(route.getJourneyPatterns()))
      {
         // failure encountered, add route 1
         ReportLocation location = new ReportLocation(route);
         DetailReportItem detail = new DetailReportItem(ROUTE_7,
               route.getObjectId(), Report.STATE.ERROR, location);
         addValidationError(report, ROUTE_7, detail);
      }
   }

   /**
    * @param report
    * @param route
    */
   private void check3Route8(PhaseReportItem report, Route route)
   {
      // 3-Route-8 : check if all stopPoints are used by journeyPatterns
      if (isEmpty(route.getJourneyPatterns()))
         return;
      prepareCheckPoint(report, ROUTE_8);
      List<StopPoint> points = new ArrayList<StopPoint>(route.getStopPoints());
      for (JourneyPattern jp : route.getJourneyPatterns())
      {
         points.removeAll(jp.getStopPoints());
         if (points.isEmpty())
            break; // useless to continue as soon as all points are found
      }
      if (!points.isEmpty())
      {
         // failure encountered, add route 1
         Map<String, Object> map = new HashMap<String, Object>();
         map.put("count", points.size());
         String name = "";
         for (StopPoint stopPoint : points)
         {
            if (stopPoint.getContainedInStopArea() != null)
            {
               name += ", " + stopPoint.getContainedInStopArea().getName();
            }
         }
         map.put("names", name.substring(2));
         ReportLocation location = new ReportLocation(route);
         DetailReportItem detail = new DetailReportItem(ROUTE_8,
               route.getObjectId(), Report.STATE.WARNING, location, map);
         addValidationError(report, ROUTE_8, detail);
      }
   }

   /**
    * @param report
    * @param route
    */
   private void check3Route9(PhaseReportItem report, Route route)
   {
      // 3-Route-9 : check if one journeyPattern uses all stopPoints
      if (isEmpty(route.getJourneyPatterns()))
         return;
      prepareCheckPoint(report, ROUTE_9);
      boolean found = false;
      int count = route.getStopPoints().size();
      for (JourneyPattern jp : route.getJourneyPatterns())
      {
         if (jp.getStopPoints().size() == count)
         {
            found = true;
            break;
         }
      }
      if (!found)
      {
         // failure encountered, add route 1
         ReportLocation location = new ReportLocation(route);
         DetailReportItem detail = new DetailReportItem(ROUTE_9,
               route.getObjectId(), Report.STATE.WARNING, location);
         addValidationError(report, ROUTE_9, detail);
      }
   }

}
