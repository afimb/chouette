package fr.certu.chouette.validation;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem.PHASE;
import fr.certu.chouette.validation.checkpoint.VehicleJourneyCheckPoints;

@ContextConfiguration(locations = { "classpath:testContext.xml",
      "classpath*:chouetteContext.xml" })
public class ValidationVehicleJourneys extends AbstractValidation
{

   @SuppressWarnings("unchecked")
   @Test(groups = { "vehicleJourney" }, description = "3-VehicleJourney-1")
   public void verifyTest1() throws ChouetteException
   {
      // 3-VehicleJourney-1 : check if time progress correctly on each stop

      VehicleJourneyCheckPoints checkPoint = (VehicleJourneyCheckPoints) applicationContext
            .getBean("vehicleJourneyCheckPoints");
      IImportPlugin<Line> importLine = (IImportPlugin<Line>) applicationContext
            .getBean("NeptuneLineImport");

      long id = 1;

      JSONObject parameters = null;
      try
      {
         parameters = new RuleParameterSet();
      } catch (JSONException | IOException e)
      {
         e.printStackTrace();
      }
      Assert.assertNotNull(parameters, "no parameters for test");

      List<Line> beans = LineLoader.load(importLine,
            "src/test/data/Ligne_OK.xml");
      Assert.assertFalse(beans.isEmpty(), "No data for test");
      Line line1 = beans.get(0);

      // line1 is model;
      line1.setId(id++);

      for (Route route : line1.getRoutes())
      {
         route.setId(id++);
         for (JourneyPattern jp : route.getJourneyPatterns())
         {
            jp.setId(id++);
            for (VehicleJourney vj : jp.getVehicleJourneys())
            {
               vj.setId(id++);
            }
         }
      }

      Route route1 = line1.getRoutes().get(0);
      route1.setObjectId("NINOXE:Route:checkedRoute");
      JourneyPattern jp1 = route1.getJourneyPatterns().get(0);
      jp1.setObjectId("NINOXE:JourneyPattern:checkedJP");
      VehicleJourney vj1 = jp1.getVehicleJourneys().get(0);
      vj1.setObjectId("NINOXE:VehicleJourney:checkedVJ");
      long maxDiffTime = 0;
      for (VehicleJourneyAtStop vjas : vj1.getVehicleJourneyAtStops())
      {
         if (vjas.getArrivalTime().equals(vjas.getDepartureTime()))
         {
            vjas.getArrivalTime().setTime(
                  vjas.getArrivalTime().getTime() - 60000);
         }
         long diffTime = Math.abs(diffTime(vjas.getArrivalTime(),
               vjas.getDepartureTime()));
         if (diffTime > maxDiffTime)
            maxDiffTime = diffTime;
      }
      parameters.put("inter_stop_duration_max", (int) maxDiffTime - 30);

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      checkPoint.check(jp1.getVehicleJourneys(), parameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      printReport(report);

      Assert.assertEquals(report.getStatus(), Report.STATE.WARNING,
            " report must be on level warning");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-VehicleJourney-1"))
         {
            found = true;
            Assert.assertEquals(checkPointReport.getStatus(),
                  Report.STATE.WARNING,
                  " checkPointReport must be on level warning");
            Assert.assertEquals(checkPointReport.hasItems(), true,
                  " checkPointReport must have items");
            Assert.assertEquals(checkPointReport.getItems().size(), 4,
                  " checkPointReport must have 4 items");

            // check detail keys
            boolean objectIdFound = false;
            for (ReportItem ditem : checkPointReport.getItems())
            {
               DetailReportItem detailReport = (DetailReportItem) ditem;
               if (detailReport.getObjectId().equals(vj1.getObjectId()))
                  objectIdFound = true;
            }
            Assert.assertTrue(objectIdFound,
                  "detail report must refer VehicleJourney 1");
         }
      }
      Assert.assertTrue(found,
            "report must contain a 3-VehicleJourney-1 checkPoint");

   }

   @SuppressWarnings("unchecked")
   @Test(groups = { "vehicleJourney" }, description = "3-VehicleJourney-2")
   public void verifyTest2() throws ChouetteException
   {
      // 3-VehicleJourney-2 : check speed progression

      VehicleJourneyCheckPoints checkPoint = (VehicleJourneyCheckPoints) applicationContext
            .getBean("vehicleJourneyCheckPoints");
      IImportPlugin<Line> importLine = (IImportPlugin<Line>) applicationContext
            .getBean("NeptuneLineImport");

      long id = 1;

      JSONObject parameters = null;
      try
      {
         parameters = new RuleParameterSet();
      } catch (JSONException | IOException e)
      {
         e.printStackTrace();
      }
      Assert.assertNotNull(parameters, "no parameters for test");

      List<Line> beans = LineLoader.load(importLine,
            "src/test/data/Ligne_OK.xml");
      Assert.assertFalse(beans.isEmpty(), "No data for test");
      Line line1 = beans.get(0);

      // line1 is model;
      line1.setId(id++);

      for (Route route : line1.getRoutes())
      {
         route.setId(id++);
         for (JourneyPattern jp : route.getJourneyPatterns())
         {
            jp.setId(id++);
            for (VehicleJourney vj : jp.getVehicleJourneys())
            {
               vj.setId(id++);
            }
         }
      }

      Route route1 = line1.getRoutes().get(0);
      route1.setObjectId("NINOXE:Route:checkedRoute");
      JourneyPattern jp1 = route1.getJourneyPatterns().get(0);
      jp1.setObjectId("NINOXE:JourneyPattern:checkedJP");

      VehicleJourney vj1 = jp1.getVehicleJourneys().get(0);
      vj1.setObjectId("NINOXE:VehicleJourney:checkedVJ");

      parameters.getJSONObject("mode_bus").put("speed_max", 10);
      parameters.getJSONObject("mode_bus").put("speed_min", 20);

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      checkPoint.check(jp1.getVehicleJourneys(), parameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      printReport(report);

      Assert.assertEquals(report.getStatus(), Report.STATE.WARNING,
            " report must be on level warning");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-VehicleJourney-2"))
         {
            found = true;
            Assert.assertEquals(checkPointReport.getStatus(),
                  Report.STATE.WARNING,
                  " checkPointReport must be on level warning");
            Assert.assertEquals(checkPointReport.hasItems(), true,
                  " checkPointReport must have items");
            Assert.assertEquals(checkPointReport.getItems().size(), 50,
                  " checkPointReport must have 50 items");

            // check detail keys
            boolean objectIdFound = false;
            for (ReportItem ditem : checkPointReport.getItems())
            {
               DetailReportItem detailReport = (DetailReportItem) ditem;
               if (detailReport.getObjectId().equals(vj1.getObjectId()))
                  objectIdFound = true;
            }
            Assert.assertTrue(objectIdFound,
                  "detail report must refer VehicleJourney 1");
         }
      }
      Assert.assertTrue(found,
            "report must contain a 3-VehicleJourney-2 checkPoint");

   }

   @SuppressWarnings("unchecked")
   @Test(groups = { "vehicleJourney" }, description = "3-VehicleJourney-3")
   public void verifyTest3() throws ChouetteException
   {
      // 3-VehicleJourney-3 : check if two journeys progress similarly

      VehicleJourneyCheckPoints checkPoint = (VehicleJourneyCheckPoints) applicationContext
            .getBean("vehicleJourneyCheckPoints");
      IImportPlugin<Line> importLine = (IImportPlugin<Line>) applicationContext
            .getBean("NeptuneLineImport");

      long id = 1;

      JSONObject parameters = null;
      try
      {
         parameters = new RuleParameterSet();
      } catch (JSONException | IOException e)
      {
         e.printStackTrace();
      }
      Assert.assertNotNull(parameters, "no parameters for test");

      List<Line> beans = LineLoader.load(importLine,
            "src/test/data/Ligne_OK.xml");
      Assert.assertFalse(beans.isEmpty(), "No data for test");
      Line line1 = beans.get(0);

      // line1 is model;
      line1.setId(id++);
      VehicleJourney vj1 = null;
      JourneyPattern jp1 = null;
      for (Route route : line1.getRoutes())
      {
         route.setId(id++);
         for (JourneyPattern jp : route.getJourneyPatterns())
         {
            jp.setId(id++);
            for (VehicleJourney vj : jp.getVehicleJourneys())
            {
               vj.setId(id++);
               vj.sortVehicleJourneyAtStops();
               if (vj.getObjectId().equals("NINOXE:VehicleJourney:15627288"))
               {
                  vj1 = vj;
                  jp1 = jp;
               }
            }
         }
      }

      Assert.assertNotNull(jp1, "tested jp not found");
      Assert.assertNotNull(vj1, "tested vj not found");

      VehicleJourneyAtStop vjas1 = vj1.getVehicleJourneyAtStops().get(1);
      vjas1.getArrivalTime().setTime(vjas1.getArrivalTime().getTime() - 240000);

      parameters.getJSONObject("mode_bus").put(
            "inter_stop_duration_variation_max", 220);

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      checkPoint.check(jp1.getVehicleJourneys(), parameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      printReport(report);

      Assert.assertEquals(report.getStatus(), Report.STATE.WARNING,
            " report must be on level warning");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-VehicleJourney-3"))
         {
            found = true;
            Assert.assertEquals(checkPointReport.getStatus(),
                  Report.STATE.WARNING,
                  " checkPointReport must be on level warning");
            Assert.assertEquals(checkPointReport.hasItems(), true,
                  " checkPointReport must have items");
            Assert.assertEquals(checkPointReport.getItems().size(), 26,
                  " checkPointReport must have 26 items");

            // check detail keys
            boolean objectIdFound = false;
            for (ReportItem ditem : checkPointReport.getItems())
            {
               DetailReportItem detailReport = (DetailReportItem) ditem;
               if (detailReport.getObjectId().equals(vj1.getObjectId()))
                  objectIdFound = true;
            }
            Assert.assertTrue(objectIdFound,
                  "detail report must refer VehicleJourney 1");
         }
      }
      Assert.assertTrue(found,
            "report must contain a 3-VehicleJourney-3 checkPoint");

   }

   @SuppressWarnings("unchecked")
   @Test(groups = { "vehicleJourney" }, description = "3-VehicleJourney-4")
   public void verifyTest4() throws ChouetteException
   {
      // 3-VehicleJourney-4 : check if each journey has minimum one timetable

      VehicleJourneyCheckPoints checkPoint = (VehicleJourneyCheckPoints) applicationContext
            .getBean("vehicleJourneyCheckPoints");
      IImportPlugin<Line> importLine = (IImportPlugin<Line>) applicationContext
            .getBean("NeptuneLineImport");

      long id = 1;

      JSONObject parameters = null;
      try
      {
         parameters = new RuleParameterSet();
      } catch (JSONException | IOException e)
      {
         e.printStackTrace();
      }
      Assert.assertNotNull(parameters, "no parameters for test");

      List<Line> beans = LineLoader.load(importLine,
            "src/test/data/Ligne_OK.xml");
      Assert.assertFalse(beans.isEmpty(), "No data for test");
      Line line1 = beans.get(0);

      // line1 is model;
      line1.setId(id++);

      for (Route route : line1.getRoutes())
      {
         route.setId(id++);
         for (JourneyPattern jp : route.getJourneyPatterns())
         {
            jp.setId(id++);
            for (VehicleJourney vj : jp.getVehicleJourneys())
            {
               vj.setId(id++);
            }
         }
      }

      Route route1 = line1.getRoutes().get(0);
      route1.setObjectId("NINOXE:Route:checkedRoute");
      JourneyPattern jp1 = route1.getJourneyPatterns().get(0);
      jp1.setObjectId("NINOXE:JourneyPattern:checkedJP");
      VehicleJourney vj1 = jp1.getVehicleJourneys().get(0);
      vj1.setObjectId("NINOXE:VehicleJourney:checkedVJ");

      vj1.getTimetables().clear();

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      checkPoint.check(jp1.getVehicleJourneys(), parameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      printReport(report);

      Assert.assertEquals(report.getStatus(), Report.STATE.WARNING,
            " report must be on level warning");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-VehicleJourney-4"))
         {
            found = true;
            Assert.assertEquals(checkPointReport.getStatus(),
                  Report.STATE.WARNING,
                  " checkPointReport must be on level warning");
            Assert.assertEquals(checkPointReport.hasItems(), true,
                  " checkPointReport must have items");
            Assert.assertEquals(checkPointReport.getItems().size(), 1,
                  " checkPointReport must have 1 item");

            // check detail keys
            boolean objectIdFound = false;
            for (ReportItem ditem : checkPointReport.getItems())
            {
               DetailReportItem detailReport = (DetailReportItem) ditem;
               if (detailReport.getObjectId().equals(vj1.getObjectId()))
                  objectIdFound = true;
            }
            Assert.assertTrue(objectIdFound,
                  "detail report must refer VehicleJourney 1");
         }
      }
      Assert.assertTrue(found,
            "report must contain a 3-VehicleJourney-4 checkPoint");

   }
   
   @SuppressWarnings("unchecked")
   @Test(groups = { "vehicleJourney" }, description = "3-VehicleJourney-5")
   public void verifyTest5() throws ChouetteException
   {
      // 3-VehicleJourney-5 : check number bounds

      VehicleJourneyCheckPoints checkPoint = (VehicleJourneyCheckPoints) applicationContext
            .getBean("vehicleJourneyCheckPoints");
      IImportPlugin<Line> importLine = (IImportPlugin<Line>) applicationContext
            .getBean("NeptuneLineImport");

      long id = 1;

      JSONObject parameters = null;
      try
      {
         parameters = new RuleParameterSet();
      } catch (JSONException | IOException e)
      {
         e.printStackTrace();
      }
      Assert.assertNotNull(parameters, "no parameters for test");

      List<Line> beans = LineLoader.load(importLine,
            "src/test/data/Ligne_OK.xml");
      Assert.assertFalse(beans.isEmpty(), "No data for test");
      Line line1 = beans.get(0);

      // line1 is model;
      line1.setId(id++);
      line1.setObjectId("NINOXE:Line:modelLine");
      
      parameters.remove("vehicle_journey_number_min");
      parameters.remove("vehicle_journey_number_max");
      Route r1 = line1.getRoutes().get(0);
      JourneyPattern jp1 = r1.getJourneyPatterns().get(0);
      Long i = Long.valueOf(1);
      for (VehicleJourney vj : jp1.getVehicleJourneys())
      {
         vj.setNumber(i);
         i = i + 1;
      }
      
      { // check test not required when missing parameter
      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      checkPoint.check(jp1.getVehicleJourneys(), parameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertEquals(report.getStatus(), Report.STATE.OK,
            " report must be on level ok");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-VehicleJourney-5"))
         {
            found = true;
         }
      }
      Assert.assertFalse(found, "report must not contain a 3-VehicleJourney-5 checkPoint");
      }
      
      parameters.append("vehicle_journey_number_min","0");
      parameters.append("vehicle_journey_number_max","0");
      
      { // check test not required when check is false
      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      checkPoint.check(jp1.getVehicleJourneys(), parameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertEquals(report.getStatus(), Report.STATE.OK,
            " report must be on level ok");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-VehicleJourney-5"))
         {
            found = true;
         }
      }
      Assert.assertFalse(found, "report must not contain a 3-VehicleJourney-5 checkPoint");
      }
      
      parameters.put("vehicle_journey_number_min","0");
      parameters.put("vehicle_journey_number_max","100");
      
      { // check test required
      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      checkPoint.check(jp1.getVehicleJourneys(), parameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertEquals(report.getStatus(), Report.STATE.OK,
            " report must be on level ok");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-VehicleJourney-5"))
         {
            found = true;
         }
      }
      Assert.assertTrue(found, "report must contain a 3-VehicleJourney-5 checkPoint");
      }
 
      parameters.put("vehicle_journey_number_min","2");
      parameters.put("vehicle_journey_number_max","100");
      
      { // check test number present
      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
      jp1.getVehicleJourneys().get(0).setNumber(null);
      
      checkPoint.check(jp1.getVehicleJourneys(), parameters, report, new HashMap<String, Object>());
      jp1.getVehicleJourneys().get(0).setNumber(Long.valueOf(1));
      report.refreshStatus();
      printReport(report);

      Assert.assertEquals(report.getStatus(), Report.STATE.WARNING,
            " report must be on level warning");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-VehicleJourney-5"))
         {
            found = true;
            Assert.assertEquals(checkPointReport.getStatus(),
                  Report.STATE.WARNING,
                  " checkPointReport must be on level warning");
            Assert.assertEquals(checkPointReport.hasItems(), true,
                  " checkPointReport must have items");
            Assert.assertEquals(checkPointReport.getItems().size(), 1,
                  " checkPointReport must have 1 item");

            // check detail keys = vj1 objectids
            boolean objectIdFound = false;
            boolean detailsFound = false;
            for (ReportItem ditem : checkPointReport.getItems())
            {
               DetailReportItem detailReport = (DetailReportItem) ditem;
               if (detailReport.getMessageKey().equals("detail_3_vehiclejourney_5_1"))  detailsFound = true;
               if (detailReport.getObjectId().equals(jp1.getVehicleJourneys().get(0).getObjectId()))
                  objectIdFound = true;
            }
            Assert.assertTrue(detailsFound,
                  "detail report must be on message detail_3_vehiclejourney_5_1");
            Assert.assertTrue(objectIdFound,
                  "detail report must refer vj 1");
            
         }
      }
      Assert.assertTrue(found, "report must contain a 3-VehicleJourney-5 checkPoint");
      }

      { // check test number out of bounds
      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      checkPoint.check(jp1.getVehicleJourneys(), parameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertEquals(report.getStatus(), Report.STATE.WARNING,
            " report must be on level warning");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-VehicleJourney-5"))
         {
            found = true;
            Assert.assertEquals(checkPointReport.getStatus(),
                  Report.STATE.WARNING,
                  " checkPointReport must be on level warning");
            Assert.assertEquals(checkPointReport.hasItems(), true,
                  " checkPointReport must have items");
            Assert.assertEquals(checkPointReport.getItems().size(), 1,
                  " checkPointReport must have 1 item");

            // check detail keys = vj1 objectids
            boolean objectIdFound = false;
            boolean detailsFound = false;
            for (ReportItem ditem : checkPointReport.getItems())
            {
               DetailReportItem detailReport = (DetailReportItem) ditem;
               if (detailReport.getMessageKey().equals("detail_3_vehiclejourney_5_2"))  detailsFound = true;
               if (detailReport.getObjectId().equals(jp1.getVehicleJourneys().get(0).getObjectId()))
                  objectIdFound = true;
            }
            Assert.assertTrue(detailsFound,
                  "detail report must be on message detail_3_vehiclejourney_5_2");
            Assert.assertTrue(objectIdFound,
                  "detail report must refer vj 1");
            
         }
      }
      Assert.assertTrue(found, "report must contain a 3-VehicleJourney-5 checkPoint");
      }
      
      { // check test number duplicated
      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
      jp1.getVehicleJourneys().get(0).setNumber(Long.valueOf(2));
      
      checkPoint.check(jp1.getVehicleJourneys(), parameters, report, new HashMap<String, Object>());
      jp1.getVehicleJourneys().get(0).setNumber(Long.valueOf(1));
      report.refreshStatus();

      Assert.assertEquals(report.getStatus(), Report.STATE.WARNING,
            " report must be on level warning");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-VehicleJourney-5"))
         {
            found = true;
            Assert.assertEquals(checkPointReport.getStatus(),
                  Report.STATE.WARNING,
                  " checkPointReport must be on level warning");
            Assert.assertEquals(checkPointReport.hasItems(), true,
                  " checkPointReport must have items");
            Assert.assertEquals(checkPointReport.getItems().size(), 1,
                  " checkPointReport must have 1 item");

            // check detail keys = vj1 objectids
            boolean objectIdFound = false;
            boolean detailsFound = false;
            for (ReportItem ditem : checkPointReport.getItems())
            {
               DetailReportItem detailReport = (DetailReportItem) ditem;
               if (detailReport.getMessageKey().equals("detail_3_vehiclejourney_5_3"))  detailsFound = true;
               if (detailReport.getObjectId().equals(jp1.getVehicleJourneys().get(1).getObjectId()))
                  objectIdFound = true;
            }
            Assert.assertTrue(detailsFound,
                  "detail report must be on message detail_3_vehiclejourney_5_3");
            Assert.assertTrue(objectIdFound,
                  "detail report must refer vj 2");
            
         }
      }
      Assert.assertTrue(found, "report must contain a 3-VehicleJourney-5 checkPoint");
      }
      
   }

   
   @SuppressWarnings("unchecked")
   @Test(groups = { "vehicleJourney" }, description = "3-VehicleJourney-6")
   public void verifyTest6() throws ChouetteException
   {
      // 3-VehicleJourney-6 : check transport mode

      VehicleJourneyCheckPoints checkPoint = (VehicleJourneyCheckPoints) applicationContext
            .getBean("vehicleJourneyCheckPoints");
      IImportPlugin<Line> importLine = (IImportPlugin<Line>) applicationContext
            .getBean("NeptuneLineImport");

      long id = 1;

      JSONObject parameters = null;
      try
      {
         parameters = new RuleParameterSet();
      } catch (JSONException | IOException e)
      {
         e.printStackTrace();
      }
      Assert.assertNotNull(parameters, "no parameters for test");

      List<Line> beans = LineLoader.load(importLine,
            "src/test/data/Ligne_OK.xml");
      Assert.assertFalse(beans.isEmpty(), "No data for test");
      Line line1 = beans.get(0);

      // line1 is model;
      line1.setId(id++);
      line1.setObjectId("NINOXE:Line:modelLine");
      
      parameters.remove("check_allowed_transport_modes");
      Route r1 = line1.getRoutes().get(0);
      JourneyPattern jp1 = r1.getJourneyPatterns().get(0);
      
      { // check test not required when missing parameter
      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      checkPoint.check(jp1.getVehicleJourneys(), parameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertEquals(report.getStatus(), Report.STATE.OK,
            " report must be on level ok");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-VehicleJourney-6"))
         {
            found = true;
         }
      }
      Assert.assertFalse(found, "report must not contain a 3-VehicleJourney-6 checkPoint");
      }
      
      parameters.append("check_allowed_transport_modes", Integer.valueOf(0));
      
      { // check test not required when check is false
      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      checkPoint.check(jp1.getVehicleJourneys(), parameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertEquals(report.getStatus(), Report.STATE.OK,
            " report must be on level ok");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-VehicleJourney-6"))
         {
            found = true;
         }
      }
      Assert.assertFalse(found, "report must not contain a 3-VehicleJourney-6 checkPoint");
      }
      
      parameters.put("check_allowed_transport_modes",Integer.valueOf(1));
      
      { // check test not required when check is false
      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      checkPoint.check(jp1.getVehicleJourneys(), parameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertEquals(report.getStatus(), Report.STATE.OK,
            " report must be on level ok");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-VehicleJourney-6"))
         {
            found = true;
         }
      }
      Assert.assertTrue(found, "report must contain a 3-VehicleJourney-6 checkPoint");
      }
 
      JSONObject busParam = parameters.getJSONObject("mode_bus");
      busParam.put("allowed_transport", Integer.valueOf(0));
      jp1.getVehicleJourneys().get(0).setTransportMode(TransportModeNameEnum.Bus);
      { // check test not required when check is false
      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      checkPoint.check(jp1.getVehicleJourneys(), parameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertEquals(report.getStatus(), Report.STATE.WARNING,
            " report must be on level warning");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-VehicleJourney-6"))
         {
            found = true;
            Assert.assertEquals(checkPointReport.getStatus(),
                  Report.STATE.WARNING,
                  " checkPointReport must be on level warning");
            Assert.assertEquals(checkPointReport.hasItems(), true,
                  " checkPointReport must have items");
            Assert.assertEquals(checkPointReport.getItems().size(), 1,
                  " checkPointReport must have 1 item");

            // check detail keys = vj1 objectids
            boolean line1objectIdFound = false;
            for (ReportItem ditem : checkPointReport.getItems())
            {
               DetailReportItem detailReport = (DetailReportItem) ditem;
               if (detailReport.getObjectId().equals(jp1.getVehicleJourneys().get(0).getObjectId()))
                  line1objectIdFound = true;
            }
            Assert.assertTrue(line1objectIdFound,
                  "detail report must refer vj 1");
            
         }
      }
      Assert.assertTrue(found, "report must contain a 3-VehicleJourney-6 checkPoint");
      }
      
      
   }

}
