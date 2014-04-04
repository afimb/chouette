package fr.certu.chouette.validation;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem.PHASE;

@ContextConfiguration(locations = { "classpath:testContext.xml", "classpath*:chouetteContext.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class ValidationStopAreas extends AbstractTransactionalTestNGSpringContextTests
{

   @SuppressWarnings("unchecked")
   @BeforeMethod
   public void loadStopAreas() throws ChouetteException
   {

         INeptuneManager<Line> lineManager = (INeptuneManager<Line>) applicationContext.getBean("lineManager");
         List<Line> lines = lineManager.getAll(null);
         lineManager.removeAll(null, lines, true);

         INeptuneManager<StopArea> stopeManager = (INeptuneManager<StopArea>) applicationContext
               .getBean("stopAreaManager");
         List<StopArea> stops = stopeManager.getAll(null);
         stopeManager.removeAll(null, stops, true);

         INeptuneManager<PTNetwork> networkManager = (INeptuneManager<PTNetwork>) applicationContext
               .getBean("networkManager");
         List<PTNetwork> networks = networkManager.getAll(null);
         networkManager.removeAll(null, networks, true);

         INeptuneManager<Timetable> timetableManager = (INeptuneManager<Timetable>) applicationContext
               .getBean("timetableManager");
         List<Timetable> timetables = timetableManager.getAll(null);
         timetableManager.removeAll(null, timetables, true);

         INeptuneManager<Company> companyManager = (INeptuneManager<Company>) applicationContext
               .getBean("companyManager");
         List<Company> companies = companyManager.getAll(null);
         companyManager.removeAll(null, companies, true);

         IImportPlugin<Line> importLine = (IImportPlugin<Line>) applicationContext.getBean("NeptuneLineImport");

         JSONObject parameters = null;
         try
         {
            parameters = new RuleParameterSet();
         }
         catch (JSONException | IOException e)
         {
            e.printStackTrace();
         }
         Assert.assertNotNull(parameters, "no parameters for test");

         List<Line> beans = LineLoader.load(importLine, "src/test/data/model.zip");
         Assert.assertFalse(beans.isEmpty(), "No data for test");
         lineManager.saveAll(null, beans, true, true);

       
   }

   @SuppressWarnings("unchecked")
   @Test(groups = { "StopArea" }, description = "3-StopArea-1")
   public void verifyTest1() throws ChouetteException
   {
      // 3-StopArea-1 : check if all non ITL stopArea has geolocalization

      INeptuneManager<StopArea> stopAreaManager = (INeptuneManager<StopArea>) applicationContext
            .getBean("stopAreaManager");

      JSONObject parameters = null;
      try
      {
         parameters = new RuleParameterSet();
      }
      catch (JSONException | IOException e)
      {
         e.printStackTrace();
      }
      Assert.assertNotNull(parameters, "no parameters for test");

      List<StopArea> beans = stopAreaManager.getAll(null);
      Assert.assertFalse(beans.isEmpty(), "No data for test");

      StopArea area1 = beans.get(0);
      area1.setLongLatType(null);
      BigDecimal svLon = area1.getLongitude();
      area1.setLongitude(null);

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
      stopAreaManager.validate(null, beans, parameters, report, true);
      report.refreshStatus();

      AbstractValidation.printReport(report);

      Assert.assertEquals(report.getStatus(), Report.STATE.ERROR, " report must be on level error");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-StopArea-1"))
         {
            found = true;
            Assert.assertEquals(checkPointReport.getStatus(), Report.STATE.ERROR,
                  " checkPointReport must be on level error");
            Assert.assertEquals(checkPointReport.hasItems(), true, " checkPointReport must have items");
            Assert.assertEquals(checkPointReport.getItems().size(), 1, " checkPointReport must have 1 item");

         }
      }
      Assert.assertTrue(found, "report must contain a 3-StopArea-1 checkPoint");
      area1.setLongitude(svLon);
      area1.setLongLatType(LongLatTypeEnum.WGS84);

   }

   @SuppressWarnings("unchecked")
   @Test(groups = { "StopArea" }, description = "3-StopArea-2")
   public void verifyTest2() throws ChouetteException
   {
      // 3-StopArea-2 : check distance of stop areas with different name
      
      INeptuneManager<StopArea> stopAreaManager = (INeptuneManager<StopArea>) applicationContext
            .getBean("stopAreaManager");

      JSONObject parameters = null;
      try
      {
         parameters = new RuleParameterSet();
      }
      catch (JSONException | IOException e)
      {
         e.printStackTrace();
      }
      Assert.assertNotNull(parameters, "no parameters for test");

      List<StopArea> beans = stopAreaManager.getAll(null);
      Assert.assertFalse(beans.isEmpty(), "No data for test");

      StopArea area1 = null;
      for (StopArea stopArea : beans)
      {
         if (stopArea.getAreaType().equals(ChouetteAreaEnum.BoardingPosition))
         {
            area1 = stopArea;
            break;
         }
      }
      StopArea area2 = new StopArea();
      area2.setId(1000000L);
      area2.setObjectId("NINOXE:StopArea:1000000");
      area2.setName("Doublon " + area1.getName());
      area2.setAreaType(area1.getAreaType());
      area2.setLongLatType(area1.getLongLatType());
      area2.setLongitude(area1.getLongitude());
      area2.setLatitude(area1.getLatitude());
      beans.add(0, area2);

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
      stopAreaManager.validate(null, beans, parameters, report, true);
      report.refreshStatus();

      AbstractValidation.printReport(report);

      Assert.assertEquals(report.getStatus(), Report.STATE.WARNING, " report must be on level warning");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-StopArea-2"))
         {
            found = true;
            Assert.assertEquals(checkPointReport.getStatus(), Report.STATE.WARNING,
                  " checkPointReport must be on level warning");
            Assert.assertEquals(checkPointReport.hasItems(), true, " checkPointReport must have items");
            Assert.assertEquals(checkPointReport.getItems().size(), 1, " checkPointReport must have 1 item");

         }
      }
      Assert.assertTrue(found, "report must contain a 3-StopArea-2 checkPoint");
   }

   @SuppressWarnings("unchecked")
   @Test(groups = { "StopArea" }, description = "3-StopArea-3")
   public void verifyTest3() throws ChouetteException
   {

      // 3-StopArea-3 : check multiple occurrence of a stopArea

      INeptuneManager<StopArea> stopAreaManager = (INeptuneManager<StopArea>) applicationContext
            .getBean("stopAreaManager");

      JSONObject parameters = null;
      try
      {
         parameters = new RuleParameterSet();
      }
      catch (JSONException | IOException e)
      {
         e.printStackTrace();
      }
      Assert.assertNotNull(parameters, "no parameters for test");

      List<StopArea> beans = stopAreaManager.getAll(null);
      Assert.assertFalse(beans.isEmpty(), "No data for test");

      StopArea area1 = null;
      StopArea area2 = null;
      for (StopArea stopArea : beans)
      {
         if (stopArea.getObjectId().equals("NINOXE:StopArea:15568801")) // St
                                                                        // Paul
         {
            area1 = stopArea;
         }
         if (stopArea.getObjectId().equals("NINOXE:StopArea:15568802")) // place
                                                                        // de
                                                                        // verdun
         {
            area2 = stopArea;
         }
         if (area1 != null && area2 != null)
            break;

      }
      area2.setName(area1.getName());
      area2.setAreaType(area1.getAreaType());
      area2.setStreetName(area1.getStreetName());
      area2.setCountryCode(area1.getCountryCode());

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
      stopAreaManager.validate(null, beans, parameters, report, true);
      report.refreshStatus();

      AbstractValidation.printReport(report);

      Assert.assertEquals(report.getStatus(), Report.STATE.WARNING, " report must be on level warning");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-StopArea-3"))
         {
            found = true;
            Assert.assertEquals(checkPointReport.getStatus(), Report.STATE.WARNING,
                  " checkPointReport must be on level warning");
            Assert.assertEquals(checkPointReport.hasItems(), true, " checkPointReport must have items");
            Assert.assertEquals(checkPointReport.getItems().size(), 1, " checkPointReport must have 1 item");

         }
      }
      Assert.assertTrue(found, "report must contain a 3-StopArea-3 checkPoint");

   }

   @SuppressWarnings("unchecked")
   @Test(groups = { "StopArea" }, description = "3-StopArea-4")
   public void verifyTest4() throws ChouetteException
   {
      // 3-StopArea-4 : check localization in a region

      INeptuneManager<StopArea> stopAreaManager = (INeptuneManager<StopArea>) applicationContext
            .getBean("stopAreaManager");

      JSONObject parameters = null;
      try
      {
         parameters = new RuleParameterSet();
      }
      catch (JSONException | IOException e)
      {
         e.printStackTrace();
      }
      Assert.assertNotNull(parameters, "no parameters for test");

      List<StopArea> beans = stopAreaManager.getAll(null);
      Assert.assertFalse(beans.isEmpty(), "No data for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
      double minLat = 90;
      double maxLat = 0;
      double minLon = 180;
      double maxLon = -180;
      for (StopArea area : beans)
      {
         area.complete();
         if (area.getAreaType().equals(ChouetteAreaEnum.ITL))
            continue;
         if (area.getLatitude().doubleValue() > maxLat)
            maxLat = area.getLatitude().doubleValue();
         if (area.getLatitude().doubleValue() < minLat)
            minLat = area.getLatitude().doubleValue();
         if (area.getLongitude().doubleValue() > maxLon)
            maxLon = area.getLongitude().doubleValue();
         if (area.getLongitude().doubleValue() < minLon)
            minLon = area.getLongitude().doubleValue();
      }

      minLon = minLon + (maxLon - minLon) * 0.1;

      JSONArray array = new JSONArray();
      array.put(new JSONArray().put(minLon).put(minLat));
      array.put(new JSONArray().put(minLon).put(maxLat));
      array.put(new JSONArray().put(maxLon).put(maxLat));
      array.put(new JSONArray().put(maxLon).put(minLat));
      array.put(new JSONArray().put(minLon).put(minLat));

      parameters.put("stop_areas_area", array.toString());
      stopAreaManager.validate(null, beans, parameters, report, true);
      report.refreshStatus();

      AbstractValidation.printReport(report);

      Assert.assertEquals(report.getStatus(), Report.STATE.WARNING, " report must be on level warning");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-StopArea-4"))
         {
            found = true;
            Assert.assertEquals(checkPointReport.getStatus(), Report.STATE.WARNING,
                  " checkPointReport must be on level warning");
            Assert.assertEquals(checkPointReport.hasItems(), true, " checkPointReport must have items");
            Assert.assertEquals(checkPointReport.getItems().size(), 17, " checkPointReport must have 17 item");

         }
      }
      Assert.assertTrue(found, "report must contain a 3-StopArea-4 checkPoint");

   }

   @SuppressWarnings("unchecked")
   @Test(groups = { "StopArea" }, description = "3-StopArea-5")
   public void verifyTest5() throws ChouetteException
   {
      // 3-StopArea-5 : check distance with parents

      INeptuneManager<StopArea> stopAreaManager = (INeptuneManager<StopArea>) applicationContext
            .getBean("stopAreaManager");

      JSONObject parameters = null;
      try
      {
         parameters = new RuleParameterSet();
      }
      catch (JSONException | IOException e)
      {
         e.printStackTrace();
      }
      Assert.assertNotNull(parameters, "no parameters for test");

      List<StopArea> beans = stopAreaManager.getAll(null);
      Assert.assertFalse(beans.isEmpty(), "No data for test");

      parameters.put("parent_stop_area_distance_max", 300);

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
      stopAreaManager.validate(null, beans, parameters, report, true);
      report.refreshStatus();

      AbstractValidation.printReport(report);

      Assert.assertEquals(report.getStatus(), Report.STATE.WARNING, " report must be on level warning");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-StopArea-5"))
         {
            found = true;
            Assert.assertEquals(checkPointReport.getStatus(), Report.STATE.WARNING,
                  " checkPointReport must be on level warning");
            Assert.assertEquals(checkPointReport.hasItems(), true, " checkPointReport must have items");
            Assert.assertEquals(checkPointReport.getItems().size(), 2, " checkPointReport must have 2 item");

         }
      }
      Assert.assertTrue(found, "report must contain a 3-StopArea-5 checkPoint");

   }

}
