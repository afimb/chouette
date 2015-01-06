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
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem.PHASE;
import fr.certu.chouette.validation.checkpoint.ConnectionLinkCheckPoints;

@ContextConfiguration(locations = { "classpath:testContext.xml",
      "classpath*:chouetteContext.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class ValidationConnectionLinks extends AbstractValidation
{
   private ConnectionLinkCheckPoints checkPoint;
   private JSONObject fullparameters;
   private ConnectionLink bean1;
   private ConnectionLink bean2;
   private List<ConnectionLink> beansFor4 = new ArrayList<>();
   private INeptuneManager<ConnectionLink> connectionLinkManager ;

   
   @SuppressWarnings("unchecked")
   @BeforeGroups (groups = { "connectionLink" })
   public void init()
   {
      connectionLinkManager = (INeptuneManager<ConnectionLink>) applicationContext
            .getBean("connectionLinkManager");
      checkPoint = (ConnectionLinkCheckPoints) applicationContext
            .getBean("connectionLinkCheckPoints");
      
      long id = 1;

      fullparameters = null;
      try
      {
         fullparameters = new RuleParameterSet();
         fullparameters.put("check_connection_link","1");

         bean1 = new ConnectionLink();
         bean1.setId(id++);
         bean1.setObjectId("test1:ConnectionLink:1");
         bean1.setName("test1");
         bean2 = new ConnectionLink();
         bean2.setId(id++);
         bean2.setObjectId("test2:ConnectionLink:1");
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
   
   
   @Test(groups = { "connectionLink" }, description = "4-ConnectionLink-1 no test")
   public void verifyTest4_1_notest() throws ChouetteException
   {
      // 4-ConnectionLink-1 : check columns
      Assert.assertNotNull(fullparameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      fullparameters.put("check_connection_link","0");
      checkPoint.check(beansFor4, fullparameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertFalse(report.hasItem("4-ConnectionLink-1"), " report must not have item 4-ConnectionLink-1");

      fullparameters.put("check_connection_link","1");
      report = new PhaseReportItem(PHASE.THREE);

      checkPoint.check(beansFor4, fullparameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertTrue(report.hasItem("4-ConnectionLink-1"), " report must have item 4-ConnectionLink-1");
      Assert.assertEquals(report.getItem("4-ConnectionLink-1").getItems().size(), 0, " checkpoint must have no detail");

   }
   
   @Test(groups = { "connectionLink" }, description = "4-ConnectionLink-1 unicity")
   public void verifyTest4_1_unique() throws ChouetteException
   {
      // 4-ConnectionLink-1 : check columns
      Assert.assertNotNull(fullparameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      // unique
      JSONObject column = fullparameters.getJSONObject("connection_link").getJSONObject("objectid");
      column.put("unique",1);

      checkPoint.check(beansFor4, fullparameters, report, new HashMap<String, Object>());
      report.refreshStatus();
      column.put("unique",0);

      DetailReportItem detail = checkReportForTest4_1(report,"4-ConnectionLink-1",bean2.getObjectId());
      Assert.assertEquals(detail.getArgs().get("column"),"objectid","detail must refer column");
      Assert.assertEquals(detail.getArgs().get("value"),bean2.getObjectId().split(":")[2],"detail must refer value");
      Assert.assertEquals(detail.getArgs().get("alternateId"),bean1.getObjectId(),"detail must refer fisrt bean");
   }


   @SuppressWarnings("unchecked")
   @BeforeGroups (groups = { "connectionLink" })
   public void loadStopAreas() throws ChouetteException
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

      List<Line> beans = LineLoader.load(importLine, "src/test/data/model.zip");
      Assert.assertFalse(beans.isEmpty(), "No data for test");
      lineManager.saveAll(null, beans, true, true);

   }

   @Test(groups = { "connectionLink" }, description = "3-ConnectionLink-1")
   public void verifyTest3_1() throws ChouetteException
   {
      // 3-ConnectionLink-1 : check distance between stops of connectionLink

      JSONObject parameters = null;
      try
      {
         parameters = new RuleParameterSet();
      } catch (JSONException | IOException e)
      {
         e.printStackTrace();
      }
      Assert.assertNotNull(parameters, "no parameters for test");

      List<ConnectionLink> beans = connectionLinkManager.getAll(null);
      Assert.assertFalse(beans.isEmpty(), "No data for test");

      parameters.put("inter_connection_link_distance_max", 600);

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
      connectionLinkManager.validate(null, beans, parameters, report,null, true);
      report.refreshStatus();

      AbstractValidation.printReport(report);

      Assert.assertEquals(report.getStatus(), Report.STATE.WARNING,
            " report must be on level warning");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-ConnectionLink-1"))
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
            "report must contain a 3-ConnectionLink-1 checkPoint");

   }

   @Test(groups = { "connectionLink" }, description = "3-ConnectionLink-2")
   public void verifyTest3_2() throws ChouetteException
   {
      // 3-ConnectionLink-2 : check distance of link against distance between
      // stops of connectionLink

      JSONObject parameters = null;
      try
      {
         parameters = new RuleParameterSet();
      } catch (JSONException | IOException e)
      {
         e.printStackTrace();
      }
      Assert.assertNotNull(parameters, "no parameters for test");

      List<ConnectionLink> beans = connectionLinkManager.getAll(null);
      Assert.assertFalse(beans.isEmpty(), "No data for test");

      ConnectionLink link = beans.get(0);
      double distance = AbstractValidation.distance(link.getStartOfLink(),
            link.getEndOfLink());

      link.setLinkDistance(BigDecimal.valueOf(distance - 50));

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
      connectionLinkManager.validate(null, beans, parameters, report,null, true);
      report.refreshStatus();

      AbstractValidation.printReport(report);

      Assert.assertEquals(report.getStatus(), Report.STATE.WARNING,
            " report must be on level warning");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-ConnectionLink-2"))
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
            "report must contain a 3-ConnectionLink-2 checkPoint");

   }

   @Test(groups = { "connectionLink" }, description = "3-ConnectionLink-3")
   public void verifyTest3_3() throws ChouetteException
   {
      // 3-ConnectionLink-3 : check speeds in connectionLink

      JSONObject parameters = null;
      try
      {
         parameters = new RuleParameterSet();
      } catch (JSONException | IOException e)
      {
         e.printStackTrace();
      }
      Assert.assertNotNull(parameters, "no parameters for test");

      List<ConnectionLink> beans = connectionLinkManager.getAll(null);
      Assert.assertFalse(beans.isEmpty(), "No data for test");

      ConnectionLink link = null;
      for (ConnectionLink connectionLink : beans)
      {
         if (connectionLink.getObjectId().equals(
               "NINOXE:ConnectionLink:15627089"))
         {
            link = connectionLink;
            break;
         }
      }

      link.getDefaultDuration().setTime(
            link.getDefaultDuration().getTime() - 600000);
      link.getOccasionalTravellerDuration().setTime(
            link.getOccasionalTravellerDuration().getTime() - 800000);
      link.getFrequentTravellerDuration().setTime(
            link.getFrequentTravellerDuration().getTime() - 600000);
      link.getMobilityRestrictedTravellerDuration().setTime(
            link.getMobilityRestrictedTravellerDuration().getTime() - 900000);

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
      connectionLinkManager.validate(null, beans, parameters, report,null, true);
      report.refreshStatus();

      AbstractValidation.printReport(report);

      Assert.assertEquals(report.getStatus(), Report.STATE.WARNING,
            " report must be on level warning");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-ConnectionLink-3"))
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
            "report must contain a 3-ConnectionLink-3 checkPoint");

   }

}
