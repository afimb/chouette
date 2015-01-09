package fr.certu.chouette.validation.checkpoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.json.JSONObject;

import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.ICheckPointPlugin;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.ReportLocation;

@Log4j
public class LineCheckPoints extends AbstractValidation<Line> implements
ICheckPointPlugin<Line>
{

   @Setter
   private RouteCheckPoints routeCheckPoints;

   @Override
   public void check(List<Line> beans, JSONObject parameters,
         PhaseReportItem report, Map<String, Object> context)
   {
      // init checkPoints : add here all defined check points for this kind of
      // object
      initCheckPoint(report, LINE_1, CheckPointReportItem.SEVERITY.WARNING);
      initCheckPoint(report, LINE_2, CheckPointReportItem.SEVERITY.ERROR);

      // 3-Line-1 : check if two lines have same name
      // 3-Line-2 : check if line has routes
      // 4-Line-2 : check if line has valid transport mode
      // 4-Line-3 : check if line has one group and only one
      // 4-Line-4 : check if line has one route or one pair (inbound/outbound)

      boolean test4_1 = (parameters.optInt(CHECK_OBJECT+OBJECT_KEY.line.name(),0) != 0);
      boolean test4_2 = parameters.optInt(CHECK_ALLOWED_TRANSPORT_MODES,0) == 1;
      boolean test4_3 = parameters.optInt(CHECK_LINES_IN_GROUPS,0) == 1;
      boolean test4_4 = parameters.optInt(CHECK_LINE_ROUTES,0) == 1;

      if (beans.size() > 0)
      {
         // checkPoint is applicable
         prepareCheckPoint(report, LINE_2);
         if (test4_1)
         {
            initCheckPoint(report, L4_LINE_1, CheckPointReportItem.SEVERITY.ERROR);
            prepareCheckPoint(report, L4_LINE_1);
         }
         if (test4_2)
         {
            initCheckPoint(report, L4_LINE_2, CheckPointReportItem.SEVERITY.ERROR);
            prepareCheckPoint(report, L4_LINE_2);
         }
         if (test4_3)
         {
            initCheckPoint(report, L4_LINE_3, CheckPointReportItem.SEVERITY.ERROR);
            prepareCheckPoint(report, L4_LINE_3);
         }
         if (test4_4)
         {
            initCheckPoint(report, L4_LINE_4, CheckPointReportItem.SEVERITY.ERROR);
            prepareCheckPoint(report, L4_LINE_4);
         }

         // en cas d'erreur, on reporte autant de detail que de lignes en
         // erreur
         for (int i = 0; i < beans.size(); i++)
         {
            Line line1 = beans.get(i);
            // 3-Line-1 : check if two lines have same name
            check3Line1(beans, report, i, line1);
            // 3-Line-2 : check if line has routes
            check3Line2(report, line1);
            // 4-Line-1 : check columns constraints
            if (test4_1)
               check4Generic1(report,line1,L4_LINE_1,OBJECT_KEY.line,parameters,context,log );
            // 4-Line-2 : check if line has valid transportMode
            if (test4_2) check4Line2(report, line1, parameters);
            // 4-Line-3 : check if line has one group and only one
            if (test4_3) check4Line3(report, line1, parameters);
            // 4-Line-4 : check if line has one route or one pair (inbound/outbound)
            if (test4_4) check4Line4(report, line1, parameters);


            // forward on routes
            List<Route> routes = line1.getRoutes();
            if (routeCheckPoints != null)
               routeCheckPoints.check(routes, parameters, report, context);
         }
      }

   }


   /**
    * @param beans
    * @param report
    * @param lineRank
    * @param line1
    */
   private void check3Line1(List<Line> beans, PhaseReportItem report,
         int lineRank, Line line1)
   {
      if (beans.size() <= 1)
         return;
      boolean error_1 = false; // if true, add detail for this line
      if (line1.getPtNetwork() == null) return;
      prepareCheckPoint(report, LINE_1);
      for (int j = lineRank + 1; j < beans.size(); j++)
      {
         Line line2 = beans.get(j);
         if (line2.getPtNetwork() == null) continue;

         if (line2.getPtNetwork().equals(line1.getPtNetwork()))
         {
            if (line1.getName().equals(line2.getName())
                  && line1.getNumber().equals(line2.getNumber()))
            {
               // failure ! add only line2 location
               ReportLocation location = new ReportLocation(line2);
               Map<String, Object> map = new HashMap<String, Object>();
               map.put("name", line2.getName());
               map.put("number", line2.getNumber());
               map.put("networkName", line2.getPtNetwork().getName());
               map.put("networkId", line2.getPtNetwork().getObjectId());
               DetailReportItem detail = new DetailReportItem(LINE_1,
                     line2.getObjectId(), Report.STATE.WARNING, location, map);
               addValidationError(report, LINE_1, detail);

               error_1 = true; // to add detail for line1
            }
         }

      }
      if (error_1)
      {
         // failure encountered, add line 1
         ReportLocation location = new ReportLocation(line1);
         Map<String, Object> map = new HashMap<String, Object>();
         map.put("name", line1.getName());
         map.put("number", line1.getNumber());
         map.put("networkName", line1.getPtNetwork().getName());
         map.put("networkId", line1.getPtNetwork().getObjectId());
         DetailReportItem detail = new DetailReportItem(LINE_1,
               line1.getObjectId(), Report.STATE.WARNING, location, map);
         addValidationError(report, LINE_1, detail);
      }
   }

   /**
    * @param report
    * @param line1
    */
   private void check3Line2(PhaseReportItem report, Line line1)
   {
      if (isEmpty(line1.getRoutes()))
      {
         // failure encountered, add line 1
         ReportLocation location = new ReportLocation(line1);
         Map<String, Object> map = new HashMap<String, Object>();
         map.put("name", line1.getName());
         map.put("number", line1.getNumber());
         DetailReportItem detail = new DetailReportItem(LINE_2,
               line1.getObjectId(), Report.STATE.ERROR, location, map);
         addValidationError(report, LINE_2, detail);
      }
   }


   private void check4Line2(PhaseReportItem report, Line line1, JSONObject parameters)
   {
      if (getModeParameter(parameters, line1.getTransportModeName().name(), ALLOWED_TRANSPORT,log) != 1)
      {
         // failure encountered, add line 1
         ReportLocation location = new ReportLocation(line1);
         Map<String, Object> map = new HashMap<String, Object>();
         map.put("name", line1.getName());
         map.put("number", line1.getNumber());
         map.put("transportMode", line1.getTransportModeName().name());
         DetailReportItem detail = new DetailReportItem(L4_LINE_2,
               line1.getObjectId(), Report.STATE.ERROR, location, map);
         addValidationError(report, L4_LINE_2, detail);
      }

   }
   private void check4Line3(PhaseReportItem report, Line line1, JSONObject parameters)
   {
      if (line1.getGroupOfLines().size() == 0)
      {
         // failure encountered, add line 1
         ReportLocation location = new ReportLocation(line1);
         Map<String, Object> map = new HashMap<String, Object>();
         map.put("name", line1.getName());
         map.put("number", line1.getNumber());
         DetailReportItem detail = new DetailReportItem(L4_LINE_3+"_1",
               line1.getObjectId(), Report.STATE.ERROR, location, map);
         addValidationError(report, L4_LINE_3, detail);
      }
      else if (line1.getGroupOfLines().size() > 1)
      {
         // failure encountered, add line 1
         ReportLocation location = new ReportLocation(line1);
         Map<String, Object> map = new HashMap<String, Object>();
         map.put("name", line1.getName());
         map.put("number", line1.getNumber());
         DetailReportItem detail = new DetailReportItem(L4_LINE_3+"_2",
               line1.getObjectId(), Report.STATE.ERROR, location, map);
         addValidationError(report, L4_LINE_3, detail);
      }


   }
   private void check4Line4(PhaseReportItem report, Line line1, JSONObject parameters)
   {
      if (line1.getRoutes().size() == 1) return;
      if (line1.getRoutes().size() == 2) 
      {
         Route r1 = line1.getRoutes().get(0);
         Route r2 = line1.getRoutes().get(1);
         if (r1.getWayBackRoute() == r2 && r2.getWayBackRoute() == r1) return;
      }
      // failure encountered, add line 1
      ReportLocation location = new ReportLocation(line1);
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("name", line1.getName());
      map.put("number", line1.getNumber());
      map.put("routeCount", line1.getRoutes().size());
      if (line1.getRoutes().size() == 0)
      {
         DetailReportItem detail = new DetailReportItem(L4_LINE_4+"_1",
               line1.getObjectId(), Report.STATE.ERROR, location, map);
         addValidationError(report, L4_LINE_4, detail);
      }
      else
      {
         DetailReportItem detail = new DetailReportItem(L4_LINE_4+"_2",
               line1.getObjectId(), Report.STATE.ERROR, location, map);
         addValidationError(report, L4_LINE_4, detail);

      }
   }

}
