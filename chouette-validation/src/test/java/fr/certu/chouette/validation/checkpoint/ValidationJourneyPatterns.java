package fr.certu.chouette.validation.checkpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem.PHASE;
import fr.certu.chouette.validation.checkpoint.JourneyPatternCheckPoints;

@ContextConfiguration(locations = { "classpath:testContext.xml",
      "classpath*:chouetteContext.xml" })
public class ValidationJourneyPatterns extends AbstractTestValidation
{
   private JourneyPatternCheckPoints checkPoint;
   private JSONObject fullparameters;
   private JourneyPattern bean1;
   private JourneyPattern bean2;
   private List<JourneyPattern> beansFor4 = new ArrayList<>();
   
   @BeforeGroups (groups = { "journeyPattern" })
   public void init()
   {
      checkPoint = (JourneyPatternCheckPoints) applicationContext
            .getBean("journeyPatternCheckPoints");
      checkPoint.setVehicleJourneyCheckPoints(null);
      
      long id = 1;

      fullparameters = null;
      try
      {
         fullparameters = new RuleParameterSet();
         fullparameters.put("check_journey_pattern","1");

         bean1 = new JourneyPattern();
         bean1.setId(id++);
         bean1.setObjectId("test1:JourneyPattern:1");
         bean1.setName("test1");
         bean2 = new JourneyPattern();
         bean2.setId(id++);
         bean2.setObjectId("test2:JourneyPattern:1");
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
   
   @Test(groups = { "journeyPattern" }, description = "4-JourneyPattern-1 no test")
   public void verifyTest4_1_notest() throws ChouetteException
   {
      // 4-JourneyPattern-1 : check columns
      Assert.assertNotNull(fullparameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      fullparameters.put("check_journey_pattern","0");
      checkPoint.check(beansFor4, fullparameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertFalse(report.hasItem("4-JourneyPattern-1"), " report must not have item 4-JourneyPattern-1");

      fullparameters.put("check_journey_pattern","1");
      report = new PhaseReportItem(PHASE.THREE);

      checkPoint.check(beansFor4, fullparameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertTrue(report.hasItem("4-JourneyPattern-1"), " report must have item 4-JourneyPattern-1");
      Assert.assertEquals(report.getItem("4-JourneyPattern-1").getItems().size(), 0, " checkpoint must have no detail");

   }
   
   @Test(groups = { "journeyPattern" }, description = "4-JourneyPattern-1 unicity")
   public void verifyTest4_1_unique() throws ChouetteException
   {
      // 4-JourneyPattern-1 : check columns
      Assert.assertNotNull(fullparameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      // unique
      JSONObject column = fullparameters.getJSONObject("journey_pattern").getJSONObject("objectid");
      column.put("unique",1);

      checkPoint.check(beansFor4, fullparameters, report, new HashMap<String, Object>());
      report.refreshStatus();
      column.put("unique",0);

      DetailReportItem detail = checkReportForTest4_1(report,"4-JourneyPattern-1",bean2.getObjectId());
      Assert.assertEquals(detail.getArgs().get("column"),"objectid","detail must refer column");
      Assert.assertEquals(detail.getArgs().get("value"),bean2.getObjectId().split(":")[2],"detail must refer value");
      Assert.assertEquals(detail.getArgs().get("alternateId"),bean1.getObjectId(),"detail must refer fisrt bean");
   }

   @SuppressWarnings("unchecked")
   @Test(groups = { "journeyPattern" }, description = "3-JourneyPattern-1")
   public void verifyTest3_1() throws ChouetteException
   {
      // 3-JourneyPattern-1 : check if two journey patterns use same stops
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
            "src/test/data/3-JourneyPattern-1.xml");
      Assert.assertFalse(beans.isEmpty(), "No data for test");
      Line line1 = beans.get(0);

      // line1 is model;
      line1.setId(id++);
      Route route1 = null;

      for (Route route : line1.getRoutes())
      {
         route.setId(id++);
         if (route.getJourneyPatterns().size() == 2)
            route1 = route;
         for (JourneyPattern jp : route.getJourneyPatterns())
         {
            jp.setId(id++);
         }
      }

      route1.setObjectId("NINOXE:Route:checkedRoute");
      JourneyPattern jp1 = route1.getJourneyPatterns().get(0);
      jp1.setObjectId("NINOXE:JourneyPattern:checkedJP");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      checkPoint.check(route1.getJourneyPatterns(), parameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      printReport(report);

      Assert.assertEquals(report.getStatus(), Report.STATE.WARNING,
            " report must be on level warning");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-JourneyPattern-1"))
         {
            found = true;
            Assert.assertEquals(checkPointReport.getStatus(),
                  Report.STATE.WARNING,
                  " checkPointReport must be on level warning");
            Assert.assertEquals(checkPointReport.hasItems(), true,
                  " checkPointReport must have items");
            Assert.assertEquals(checkPointReport.getItems().size(), 1,
                  " checkPointReport must have 1 item");

            // check detail keys = line1 and line2 objectids
            boolean objectIdFound = false;
            for (ReportItem ditem : checkPointReport.getItems())
            {
               DetailReportItem detailReport = (DetailReportItem) ditem;
               if (detailReport.getObjectId().equals(jp1.getObjectId()))
                  objectIdFound = true;
            }
            Assert.assertTrue(objectIdFound,
                  "detail report must refer JourneyPattern 1");
         }
      }
      Assert.assertTrue(found,
            "report must contain a 3-JourneyPattern-1 checkPoint");

   }

}
