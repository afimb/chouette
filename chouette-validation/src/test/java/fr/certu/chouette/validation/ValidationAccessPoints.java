package fr.certu.chouette.validation;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem.PHASE;
import fr.certu.chouette.validation.checkpoint.AccessPointCheckPoints;

@ContextConfiguration(locations = { "classpath:testContext.xml",
      "classpath*:chouetteContext.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class ValidationAccessPoints extends AbstractValidation
{
   private AccessPointCheckPoints checkPoint;
   private JSONObject fullparameters;
   private AccessPoint bean1;
   private AccessPoint bean2;
   private List<AccessPoint> beansFor4 = new ArrayList<>();
   private INeptuneManager<AccessPoint> accessPointManager ;

   
   @SuppressWarnings("unchecked")
   @BeforeGroups (groups = { "accessPoint" })
   public void init()
   {
      accessPointManager = (INeptuneManager<AccessPoint>) applicationContext
            .getBean("accessPointManager");
      checkPoint = (AccessPointCheckPoints) applicationContext
            .getBean("accessPointCheckPoints");
      
      long id = 1;

      fullparameters = null;
      try
      {
         fullparameters = new RuleParameterSet();
         fullparameters.put("check_access_point","1");

         bean1 = new AccessPoint();
         bean1.setId(id++);
         bean1.setObjectId("test1:AccessPoint:1");
         bean1.setName("test1");
         bean1.setContainedIn(new StopArea());
         bean2 = new AccessPoint();
         bean2.setId(id++);
         bean2.setObjectId("test2:AccessPoint:1");
         bean2.setName("test2");
         bean2.setContainedIn(new StopArea());
   
         beansFor4.add(bean1);
         beansFor4.add(bean2);
      } 
      catch (Exception e)
      {
         fullparameters = null;
         e.printStackTrace();
      }
      
   }
   
   
   @Test(groups = { "accessPoint" }, description = "4-AccessPoint-1 no test")
   public void verifyTest4_1_notest() throws ChouetteException
   {
      // 4-AccessPoint-1 : check columns
      Assert.assertNotNull(fullparameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      fullparameters.put("check_access_point","0");
      checkPoint.check(beansFor4, fullparameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertFalse(report.hasItem("4-AccessPoint-1"), " report must not have item 4-AccessPoint-1");

      fullparameters.put("check_access_point","1");
      report = new PhaseReportItem(PHASE.THREE);

      checkPoint.check(beansFor4, fullparameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertTrue(report.hasItem("4-AccessPoint-1"), " report must have item 4-AccessPoint-1");
      Assert.assertEquals(report.getItem("4-AccessPoint-1").getItems().size(), 0, " checkpoint must have no detail");

   }
   
   @Test(groups = { "accessPoint" }, description = "4-AccessPoint-1 unicity")
   public void verifyTest4_1_unique() throws ChouetteException
   {
      // 4-AccessPoint-1 : check columns
      Assert.assertNotNull(fullparameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      // unique
      JSONObject column = fullparameters.getJSONObject("access_point").getJSONObject("objectid");
      column.put("unique",1);

      checkPoint.check(beansFor4, fullparameters, report, new HashMap<String, Object>());
      report.refreshStatus();
      column.put("unique",0);

      DetailReportItem detail = checkReportForTest4_1(report,"4-AccessPoint-1",bean2.getObjectId());
      Assert.assertEquals(detail.getArgs().get("column"),"objectid","detail must refer column");
      Assert.assertEquals(detail.getArgs().get("value"),bean2.getObjectId().split(":")[2],"detail must refer value");
      Assert.assertEquals(detail.getArgs().get("alternateId"),bean1.getObjectId(),"detail must refer fisrt bean");
   }


   @SuppressWarnings("unchecked")
   @BeforeGroups(groups = { "accessPoint" })
   public void loadStopAreas() throws ChouetteException
   {
      {
         IImportPlugin<Line> importLine = (IImportPlugin<Line>) applicationContext
               .getBean("NeptuneLineImport");
         INeptuneManager<Line> lineManager = (INeptuneManager<Line>) applicationContext
               .getBean("lineManager");

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
               "src/test/data/model.zip");
         Assert.assertFalse(beans.isEmpty(), "No data for test");
         lineManager.saveAll(null, beans, true, true);
      }

   }

   @Test(groups = { "accessPoint" }, description = "3-AccessPoint-1")
   public void verifyTest3_1() throws ChouetteException
   {
      // 3-AccessPoint-1 : check if all access points have geolocalization

      JSONObject parameters = null;
      try
      {
         parameters = new RuleParameterSet();
      } catch (JSONException | IOException e)
      {
         e.printStackTrace();
      }
      Assert.assertNotNull(parameters, "no parameters for test");

      List<AccessPoint> beans = accessPointManager.getAll(null);
      Assert.assertFalse(beans.isEmpty(), "No data for test");

      AccessPoint access1 = beans.get(0);
      access1.setLongLatType(null);
      BigDecimal svLon = access1.getLongitude();
      access1.setLongitude(null);

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      printReport(report);

      Assert.assertEquals(report.getStatus(), Report.STATE.ERROR,
            " report must be on level error");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-AccessPoint-1"))
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
      Assert.assertTrue(found,
            "report must contain a 3-AccessPoint-1 checkPoint");
      access1.setLongitude(svLon);
      access1.setLongLatType(LongLatTypeEnum.WGS84);

   }

   @Test(groups = { "accessPoint" }, description = "3-AccessPoint-2")
   public void verifyTest3_2() throws ChouetteException
   {
      // 3-AccessPoint-2 : check distance of access points with different name

      JSONObject parameters = null;
      try
      {
         parameters = new RuleParameterSet();
      } catch (JSONException | IOException e)
      {
         e.printStackTrace();
      }
      Assert.assertNotNull(parameters, "no parameters for test");

      List<AccessPoint> beans = accessPointManager.getAll(null);
      Assert.assertFalse(beans.isEmpty(), "No data for test");

      parameters.put("parent_stop_area_distance_max", 50);

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      AbstractValidation.printReport(report);

      Assert.assertEquals(report.getStatus(), Report.STATE.WARNING,
            " report must be on level warning");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-AccessPoint-3"))
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
      Assert.assertTrue(found,
            "report must contain a 3-AccessPoint-3 checkPoint");

   }

   @Test(groups = { "accessPoint" }, description = "3-AccessPoint-3")
   public void verifyTest3_3() throws ChouetteException
   {
      // 3-AccessPoint-3 : check distance with parents

      JSONObject parameters = null;
      try
      {
         parameters = new RuleParameterSet();
      } catch (JSONException | IOException e)
      {
         e.printStackTrace();
      }
      Assert.assertNotNull(parameters, "no parameters for test");

      List<AccessPoint> beans = accessPointManager.getAll(null);
      Assert.assertFalse(beans.isEmpty(), "No data for test");

      AccessPoint access1 = null;
      AccessPoint access2 = null;
      for (AccessPoint accessPoint : beans)
      {
         if (accessPoint.getObjectId().equals("NINOXE:AccessPoint:6"))
            access1 = accessPoint;
         if (accessPoint.getObjectId().equals("NINOXE:AccessPoint:7"))
            access2 = accessPoint;
      }

      access1.setLongitude(access2.getLongitude());
      access1.setLatitude(access2.getLatitude());

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      AbstractValidation.printReport(report);

      Assert.assertEquals(report.getStatus(), Report.STATE.WARNING,
            " report must be on level warning");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-AccessPoint-2"))
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
      Assert.assertTrue(found,
            "report must contain a 3-AccessPoint-2 checkPoint");

   }

}
