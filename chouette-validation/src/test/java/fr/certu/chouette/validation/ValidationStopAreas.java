package fr.certu.chouette.validation;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem.PHASE;
import fr.certu.chouette.validation.checkpoint.StopAreaCheckPoints;

@ContextConfiguration(locations = { "classpath:testContext.xml",
      "classpath*:chouetteContext.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class ValidationStopAreas extends
      AbstractValidation
{
   private StopAreaCheckPoints checkPoint;
   private JSONObject fullparameters;
   private StopArea bean1;
   private StopArea bean2;
   private StopArea bean3;
   private List<StopArea> beansFor4 = new ArrayList<>();
   private INeptuneManager<StopArea> stopAreaManager ;

   
   @SuppressWarnings("unchecked")
   @BeforeGroups (groups = { "stopArea" })
   public void init()
   {
      stopAreaManager = (INeptuneManager<StopArea>) applicationContext
            .getBean("stopAreaManager");
      checkPoint = (StopAreaCheckPoints) applicationContext
            .getBean("stopAreaCheckPoints");
      
      long id = 1;

      fullparameters = null;
      try
      {
         fullparameters = new RuleParameterSet();
         fullparameters.put("check_stop_area","1");

         bean1 = new StopArea();
         bean1.setId(id++);
         bean1.setObjectId("test1:StopArea:1");
         bean1.setName("test1");
         bean1.setAreaType(ChouetteAreaEnum.BoardingPosition);
         bean2 = new StopArea();
         bean2.setId(id++);
         bean2.setObjectId("test2:StopArea:1");
         bean2.setName("test2");
         bean2.setAreaType(ChouetteAreaEnum.BoardingPosition);
         bean3 = new StopArea();
         bean3.setId(id++);
         bean3.setObjectId("test2:StopArea:3");
         bean3.setName("test2");
         bean3.setAreaType(ChouetteAreaEnum.CommercialStopPoint);
   
         beansFor4.add(bean1);
         beansFor4.add(bean2);
      } 
      catch (Exception e)
      {
         fullparameters = null;
         e.printStackTrace();
      }
      
   }
   
   

   @SuppressWarnings("unchecked")
   @BeforeGroups(groups = { "stopArea" })
   public void loadStopAreas() throws ChouetteException
   {

      INeptuneManager<Line> lineManager = (INeptuneManager<Line>) applicationContext
            .getBean("lineManager");

      IImportPlugin<Line> importLine = (IImportPlugin<Line>) applicationContext
            .getBean("NeptuneLineImport");

      JSONObject parameters = null;
      try
      {
         parameters = new RuleParameterSet();
      } catch (JSONException | IOException e)
      {
         e.printStackTrace();
      }
      Assert.assertNotNull(parameters, "no parameters for test");

      List<Line> beans = LineLoader.load(importLine, "src/test/data/model.zip");
      Assert.assertFalse(beans.isEmpty(), "No data for test");
      lineManager.saveAll(null, beans, true, true);

   }

   @Test(groups = { "stopArea" }, description = "3-StopArea-1")
   public void verifyTest3_1() throws ChouetteException
   {
      // 3-StopArea-1 : check if all non ITL stopArea has geolocalization

      JSONObject parameters = null;
      try
      {
         parameters = new RuleParameterSet();
      } catch (JSONException | IOException e)
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
      stopAreaManager.validate(null, beans, parameters, report, null, true);
      report.refreshStatus();

      AbstractValidation.printReport(report);

      Assert.assertEquals(report.getStatus(), Report.STATE.ERROR,
            " report must be on level error");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-StopArea-1"))
         {
            found = true;
            Assert.assertEquals(checkPointReport.getStatus(),
                  Report.STATE.ERROR,
                  " checkPointReport must be on level error");
            Assert.assertEquals(checkPointReport.hasItems(), true,
                  " checkPointReport must have items");
            Assert.assertEquals(checkPointReport.getItems().size(), 1,
                  " checkPointReport must have 1 item");

         }
      }
      Assert.assertTrue(found, "report must contain a 3-StopArea-1 checkPoint");
      area1.setLongitude(svLon);
      area1.setLongLatType(LongLatTypeEnum.WGS84);

   }

   @Test(groups = { "stopArea" }, description = "3-StopArea-2")
   public void verifyTest3_2() throws ChouetteException
   {
      // 3-StopArea-2 : check distance of stop areas with different name

      JSONObject parameters = null;
      try
      {
         parameters = new RuleParameterSet();
      } catch (JSONException | IOException e)
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
      stopAreaManager.validate(null, beans, parameters, report, null, true);
      report.refreshStatus();

      AbstractValidation.printReport(report);

      Assert.assertEquals(report.getStatus(), Report.STATE.WARNING,
            " report must be on level warning");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-StopArea-2"))
         {
            found = true;
            Assert.assertEquals(checkPointReport.getStatus(),
                  Report.STATE.WARNING,
                  " checkPointReport must be on level warning");
            Assert.assertEquals(checkPointReport.hasItems(), true,
                  " checkPointReport must have items");
            Assert.assertEquals(checkPointReport.getItems().size(), 1,
                  " checkPointReport must have 1 item");

         }
      }
      Assert.assertTrue(found, "report must contain a 3-StopArea-2 checkPoint");
   }

   @Test(groups = { "stopArea" }, description = "3-StopArea-3")
   public void verifyTest3_3() throws ChouetteException
   {

      // 3-StopArea-3 : check multiple occurrence of a stopArea

      JSONObject parameters = null;
      try
      {
         parameters = new RuleParameterSet();
      } catch (JSONException | IOException e)
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
      stopAreaManager.validate(null, beans, parameters, report, null, true);
      report.refreshStatus();

      AbstractValidation.printReport(report);

      Assert.assertEquals(report.getStatus(), Report.STATE.WARNING,
            " report must be on level warning");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-StopArea-3"))
         {
            found = true;
            Assert.assertEquals(checkPointReport.getStatus(),
                  Report.STATE.WARNING,
                  " checkPointReport must be on level warning");
            Assert.assertEquals(checkPointReport.hasItems(), true,
                  " checkPointReport must have items");
            Assert.assertEquals(checkPointReport.getItems().size(), 1,
                  " checkPointReport must have 1 item");

         }
      }
      Assert.assertTrue(found, "report must contain a 3-StopArea-3 checkPoint");

   }

   @Test(groups = { "stopArea" }, description = "3-StopArea-4")
   public void verifyTest3_4() throws ChouetteException
   {
      // 3-StopArea-4 : check localization in a region

      JSONObject parameters = null;
      try
      {
         parameters = new RuleParameterSet();
      } catch (JSONException | IOException e)
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
      stopAreaManager.validate(null, beans, parameters, report, null, true);
      report.refreshStatus();

      AbstractValidation.printReport(report);

      Assert.assertEquals(report.getStatus(), Report.STATE.WARNING,
            " report must be on level warning");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-StopArea-4"))
         {
            found = true;
            Assert.assertEquals(checkPointReport.getStatus(),
                  Report.STATE.WARNING,
                  " checkPointReport must be on level warning");
            Assert.assertEquals(checkPointReport.hasItems(), true,
                  " checkPointReport must have items");
            Assert.assertEquals(checkPointReport.getItems().size(), 17,
                  " checkPointReport must have 17 item");

         }
      }
      Assert.assertTrue(found, "report must contain a 3-StopArea-4 checkPoint");

   }

   @Test(groups = { "stopArea" }, description = "3-StopArea-5")
   public void verifyTest3_5() throws ChouetteException
   {
      // 3-StopArea-5 : check distance with parents

      JSONObject parameters = null;
      try
      {
         parameters = new RuleParameterSet();
      } catch (JSONException | IOException e)
      {
         e.printStackTrace();
      }
      Assert.assertNotNull(parameters, "no parameters for test");

      List<StopArea> beans = stopAreaManager.getAll(null);
      Assert.assertFalse(beans.isEmpty(), "No data for test");

      parameters.put("parent_stop_area_distance_max", 300);

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
      stopAreaManager.validate(null, beans, parameters, report, null, true);
      report.refreshStatus();

      AbstractValidation.printReport(report);

      Assert.assertEquals(report.getStatus(), Report.STATE.WARNING,
            " report must be on level warning");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-StopArea-5"))
         {
            found = true;
            Assert.assertEquals(checkPointReport.getStatus(),
                  Report.STATE.WARNING,
                  " checkPointReport must be on level warning");
            Assert.assertEquals(checkPointReport.hasItems(), true,
                  " checkPointReport must have items");
            Assert.assertEquals(checkPointReport.getItems().size(), 2,
                  " checkPointReport must have 2 item");

         }
      }
      Assert.assertTrue(found, "report must contain a 3-StopArea-5 checkPoint");

   }

   @Test(groups = { "stopArea" }, description = "4-StopArea-1 no test")
   public void verifyTest4_1_notest() throws ChouetteException
   {
      // 4-StopArea-1 : check columns
      Assert.assertNotNull(fullparameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      fullparameters.put("check_stop_area","0");
      checkPoint.check(beansFor4, fullparameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertFalse(report.hasItem("4-StopArea-1"), " report must not have item 4-StopArea-1");

      fullparameters.put("check_stop_area","1");
      report = new PhaseReportItem(PHASE.THREE);

      checkPoint.check(beansFor4, fullparameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertTrue(report.hasItem("4-StopArea-1"), " report must have item 4-StopArea-1");
      Assert.assertEquals(report.getItem("4-StopArea-1").getItems().size(), 0, " checkpoint must have no detail");

   }
   
   @Test(groups = { "stopArea" }, description = "4-StopArea-1 unicity")
   public void verifyTest4_1_unique() throws ChouetteException
   {
      // 4-StopArea-1 : check columns
      Assert.assertNotNull(fullparameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      // unique
      JSONObject column = fullparameters.getJSONObject("stop_area").getJSONObject("objectid");
      column.put("unique",1);

      checkPoint.check(beansFor4, fullparameters, report, new HashMap<String, Object>());
      report.refreshStatus();
      column.put("unique",0);

      DetailReportItem detail = checkReportForTest4_1(report,"4-StopArea-1",bean2.getObjectId());
      Assert.assertEquals(detail.getArgs().get("column"),"objectid","detail must refer column");
      Assert.assertEquals(detail.getArgs().get("value"),bean2.getObjectId().split(":")[2],"detail must refer value");
      Assert.assertEquals(detail.getArgs().get("alternateId"),bean1.getObjectId(),"detail must refer fisrt bean");
   }

   @Test(groups = { "StopArea" }, description = "4-StopArea-2")
   public void verifyTest4_2() throws ChouetteException
   {
      // 4-StopArea-2 : check parent
      Assert.assertNotNull(fullparameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
      
      bean1.setParent(bean3);
      bean2.setParent(bean3);
      
      List<StopArea> list = new ArrayList<>(beansFor4);
      list.add(bean3);

      fullparameters.put("check_stop_parent","0");
      checkPoint.check(list, fullparameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertFalse(report.hasItem("4-StopArea-2"), " report must not have item 4-StopArea-2");

      fullparameters.put("check_stop_parent","1");
      report = new PhaseReportItem(PHASE.THREE);

      checkPoint.check(list, fullparameters, report, new HashMap<String, Object>());
      report.refreshStatus();
      fullparameters.put("check_stop_parent","0");

      Assert.assertTrue(report.hasItem("4-StopArea-2"), " report must have item 4-StopArea-2");
      Assert.assertEquals(report.getItem("4-StopArea-2").getItems().size(), 0, " checkpoint must have no detail");

      bean1.setParent(null);
      fullparameters.put("check_stop_parent","1");
      report = new PhaseReportItem(PHASE.THREE);

      checkPoint.check(list, fullparameters, report, new HashMap<String, Object>());
      report.refreshStatus();
      fullparameters.put("check_stop_parent","0");

      Assert.assertTrue(report.hasItem("4-StopArea-2"), " report must have item 4-StopArea-2");
      Assert.assertEquals(report.getItem("4-StopArea-2").getItems().size(), 1, " checkpoint must have one detail");

   }


}
