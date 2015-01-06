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
import fr.certu.chouette.model.neptune.AccessLink;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem.PHASE;
import fr.certu.chouette.validation.checkpoint.AccessLinkCheckPoints;

@ContextConfiguration(locations = { "classpath:testContext.xml",
      "classpath*:chouetteContext.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class ValidationAccessLinks extends
      AbstractValidation
{
   private AccessLinkCheckPoints checkPoint;
   private JSONObject fullparameters;
   private AccessLink bean1;
   private AccessLink bean2;
   private List<AccessLink> beansFor4 = new ArrayList<>();
   private INeptuneManager<AccessLink> accessLinkManager ;

   
   @SuppressWarnings("unchecked")
   @BeforeGroups (groups = { "accessLink" })
   public void init()
   {
      accessLinkManager = (INeptuneManager<AccessLink>) applicationContext
            .getBean("accessLinkManager");
      checkPoint = (AccessLinkCheckPoints) applicationContext
            .getBean("accessLinkCheckPoints");
      
      long id = 1;

      fullparameters = null;
      try
      {
         fullparameters = new RuleParameterSet();
         fullparameters.put("check_access_link","1");

         bean1 = new AccessLink();
         bean1.setId(id++);
         bean1.setObjectId("test1:AccessLink:1");
         bean1.setName("test1");
         bean2 = new AccessLink();
         bean2.setId(id++);
         bean2.setObjectId("test2:AccessLink:1");
         bean2.setName("test2");
   
         beansFor4.add(bean1);
         beansFor4.add(bean2);
      } 
      catch (Exception e)
      {
         fullparameters = null;
         e.printStackTrace();
      }
      
   }
   
   
   @Test(groups = { "accessLink" }, description = "4-AccessLink-1 no test")
   public void verifyTest4_1_notest() throws ChouetteException
   {
      // 4-AccessLink-1 : check columns
      Assert.assertNotNull(fullparameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      fullparameters.put("check_access_link","0");
      checkPoint.check(beansFor4, fullparameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertFalse(report.hasItem("4-AccessLink-1"), " report must not have item 4-AccessLink-1");

      fullparameters.put("check_access_link","1");
      report = new PhaseReportItem(PHASE.THREE);

      checkPoint.check(beansFor4, fullparameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertTrue(report.hasItem("4-AccessLink-1"), " report must have item 4-AccessLink-1");
      Assert.assertEquals(report.getItem("4-AccessLink-1").getItems().size(), 0, " checkpoint must have no detail");

   }
   
   @Test(groups = { "accessLink" }, description = "4-AccessLink-1 unicity")
   public void verifyTest4_1_unique() throws ChouetteException
   {
      // 4-AccessLink-1 : check columns
      Assert.assertNotNull(fullparameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      // unique
      JSONObject column = fullparameters.getJSONObject("access_link").getJSONObject("objectid");
      column.put("unique",1);

      checkPoint.check(beansFor4, fullparameters, report, new HashMap<String, Object>());
      report.refreshStatus();
      column.put("unique",0);

      DetailReportItem detail = checkReportForTest4_1(report,"4-AccessLink-1",bean2.getObjectId());
      Assert.assertEquals(detail.getArgs().get("column"),"objectid","detail must refer column");
      Assert.assertEquals(detail.getArgs().get("value"),bean2.getObjectId().split(":")[2],"detail must refer value");
      Assert.assertEquals(detail.getArgs().get("alternateId"),bean1.getObjectId(),"detail must refer fisrt bean");
   }


   @Test(groups = { "accessLink" }, description = "3-AccessLink-1")
   public void verifyTest3_1() throws ChouetteException
   {
      // 3-AccessLink-1 : check distance between ends of accessLink


      JSONObject parameters = null;
      try
      {
         parameters = new RuleParameterSet();
      } catch (JSONException | IOException e)
      {
         e.printStackTrace();
      }
      Assert.assertNotNull(parameters, "no parameters for test");

      List<AccessLink> beans = accessLinkManager.getAll(null);
      Assert.assertFalse(beans.isEmpty(), "No data for test");

      parameters.put("inter_access_link_distance_max", 50);

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      // accessLinkManager.validate(null, beans, parameters, report,null, true);
      report.refreshStatus();

      AbstractValidation.printReport(report);

      Assert.assertEquals(report.getStatus(), Report.STATE.WARNING,
            " report must be on level warning");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-AccessLink-1"))
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
            "report must contain a 3-AccessLink-1 checkPoint");

   }

   @Test(groups = { "accessLink" }, description = "3-AccessLink-2")
   public void verifyTest3_2() throws ChouetteException
   {
      // 3-AccessLink-2 : check distance of link against distance between
      // stops of accessLink

      JSONObject parameters = null;
      try
      {
         parameters = new RuleParameterSet();
      } catch (JSONException | IOException e)
      {
         e.printStackTrace();
      }
      Assert.assertNotNull(parameters, "no parameters for test");

      List<AccessLink> beans = accessLinkManager.getAll(null);
      Assert.assertFalse(beans.isEmpty(), "No data for test");

      AccessLink link = beans.get(0);

      double distance = AbstractValidation.distance(link.getStopArea(),
            link.getAccessPoint());

      link.setLinkDistance(BigDecimal.valueOf(distance - 50));

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
         if (checkPointReport.getMessageKey().equals("3-AccessLink-2"))
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
            "report must contain a 3-AccessLink-2 checkPoint");

   }

   @Test(groups = { "accessLink" }, description = "3-AccessLink-3")
   public void verifyTest3_3() throws ChouetteException
   {
      // 3-AccessLink-3 : check speeds in accessLink

      JSONObject parameters = null;
      try
      {
         parameters = new RuleParameterSet();
      } catch (JSONException | IOException e)
      {
         e.printStackTrace();
      }
      Assert.assertNotNull(parameters, "no parameters for test");

      List<AccessLink> beans = accessLinkManager.getAll(null);
      Assert.assertFalse(beans.isEmpty(), "No data for test");

      AccessLink link = null;
      for (AccessLink accessLink : beans)
      {
         if (accessLink.getObjectId().equals("NINOXE:AccessLink:7"))
         {
            link = accessLink;
            break;
         }
      }
      double distance = AbstractValidation.distance(link.getAccessPoint(),
            link.getStopArea());
      link.setLinkDistance(BigDecimal.valueOf(distance));
      link.getDefaultDuration().setTime(
            link.getDefaultDuration().getTime() - 150000);
      link.getOccasionalTravellerDuration().setTime(
            link.getDefaultDuration().getTime());
      link.getFrequentTravellerDuration().setTime(
            link.getDefaultDuration().getTime());
      link.getMobilityRestrictedTravellerDuration().setTime(
            link.getDefaultDuration().getTime());

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
         if (checkPointReport.getMessageKey().equals("3-AccessLink-3"))
         {
            found = true;
            Assert.assertEquals(checkPointReport.getStatus(),
                  Report.STATE.WARNING,
                  " checkPointReport must be on level warning");
            Assert.assertEquals(checkPointReport.hasItems(), true,
                  " checkPointReport must have items");
            Assert.assertEquals(checkPointReport.getItems().size(), 4,
                  " checkPointReport must have 4 items");
         }
      }
      Assert.assertTrue(found,
            "report must contain a 3-AccessLink-3 checkPoint");

   }
   
   @SuppressWarnings("unchecked")
   @BeforeGroups(groups = { "accessLink" })
   public void loadStopAreas() throws ChouetteException
   {
      {
         IImportPlugin<Line> importLine = (IImportPlugin<Line>) applicationContext
               .getBean("NeptuneLineImport");
         INeptuneManager<Line> lineManager = (INeptuneManager<Line>) applicationContext
               .getBean("lineManager");

         List<Line> beans = LineLoader.load(importLine,
               "src/test/data/model.zip");
         Assert.assertFalse(beans.isEmpty(), "No data for test");
         lineManager.saveAll(null, beans, true, true);
      }

   }


}
