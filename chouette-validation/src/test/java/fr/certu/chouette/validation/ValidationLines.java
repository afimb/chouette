package fr.certu.chouette.validation;

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
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem.PHASE;
import fr.certu.chouette.validation.checkpoint.LineCheckPoints;

@ContextConfiguration(locations = { "classpath:testContext.xml",
      "classpath*:chouetteContext.xml" })
public class ValidationLines extends AbstractValidation
{
   private LineCheckPoints checkPoint;
   private JSONObject fullparameters;
   private Line bean1;
   private Line bean2;
   private List<Line> beansFor4 = new ArrayList<>();
   
   @BeforeGroups (groups = { "line" })
   public void init()
   {
      checkPoint = (LineCheckPoints) applicationContext
            .getBean("lineCheckPoints");
      checkPoint.setRouteCheckPoints(null);

      long id = 1;

      fullparameters = null;
      try
      {
         fullparameters = new RuleParameterSet();
         fullparameters.put("check_line","1");

         bean1 = new Line();
         bean1.setId(id++);
         bean1.setObjectId("test1:Line:1");
         bean1.setName("test1");
         bean2 = new Line();
         bean2.setId(id++);
         bean2.setObjectId("test2:Line:1");
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
   
   @Test(groups = { "line" }, description = "4-Line-1 no test")
   public void verifyTest4_1_notest() throws ChouetteException
   {
      // 4-Line-1 : check columns
      Assert.assertNotNull(fullparameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      fullparameters.put("check_line","0");
      checkPoint.check(beansFor4, fullparameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertFalse(report.hasItem("4-Line-1"), " report must not have item 4-Line-1");

      fullparameters.put("check_line","1");
      report = new PhaseReportItem(PHASE.THREE);

      checkPoint.check(beansFor4, fullparameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertTrue(report.hasItem("4-Line-1"), " report must have item 4-Line-1");
      Assert.assertEquals(report.getItem("4-Line-1").getItems().size(), 0, " checkpoint must have no detail");

   }
   
   @Test(groups = { "line" }, description = "4-Line-1 unicity")
   public void verifyTest4_1_unique() throws ChouetteException
   {
      // 4-Line-1 : check columns
      Assert.assertNotNull(fullparameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      // unique
      JSONObject column = fullparameters.getJSONObject("line").getJSONObject("objectid");
      column.put("unique",1);

      checkPoint.check(beansFor4, fullparameters, report, new HashMap<String, Object>());
      report.refreshStatus();
      column.put("unique",0);

      DetailReportItem detail = checkReportForTest4_1(report,"4-Line-1",bean2.getObjectId());
      Assert.assertEquals(detail.getArgs().get("column"),"objectid","detail must refer column");
      Assert.assertEquals(detail.getArgs().get("value"),bean2.getObjectId().split(":")[2],"detail must refer value");
      Assert.assertEquals(detail.getArgs().get("alternateId"),bean1.getObjectId(),"detail must refer fisrt bean");
   }

   @SuppressWarnings("unchecked")
   @Test(groups = { "line" }, description = "3-Line-1")
   public void verifyTest3_1() throws ChouetteException
   {
      // 3-Line-1 : check if two lines have same name
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
      beans = LineLoader.load(importLine, "src/test/data/Ligne_OK.xml");
      Line line2 = beans.get(0);
      beans = LineLoader.load(importLine, "src/test/data/Ligne_OK.xml");
      Line line3 = beans.get(0);

      beans.add(line2);
      beans.add(line1);

      // line1 is model;
      line1.setId(id++);
      line1.setObjectId("NINOXE:Line:modelLine");
      // line2 has same name and same number on same network
      line2.setId(id++);
      line2.setObjectId("NINOXE:Line:wrongLine");
      // line3 has same name and same number on different network
      line3.setId(id++);
      line3.setObjectId("NINOXE:Line:goodLine");
      PTNetwork network2 = new PTNetwork();
      network2.setObjectId("NINOXE:GroupOfLine:testNetwork");
      network2.setName("test network");
      line3.setPtNetwork(network2);

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      printReport(report);

      Assert.assertEquals(report.getStatus(), Report.STATE.WARNING,
            " report must be on level warning");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-Line-1"))
         {
            found = true;
            Assert.assertEquals(checkPointReport.getStatus(),
                  Report.STATE.WARNING,
                  " checkPointReport must be on level warning");
            Assert.assertEquals(checkPointReport.hasItems(), true,
                  " checkPointReport must have items");
            Assert.assertEquals(checkPointReport.getItems().size(), 2,
                  " checkPointReport must have 2 item");

            // check detail keys = line1 and line2 objectids
            boolean line1objectIdFound = false;
            boolean line2objectIdFound = false;
            boolean line3objectIdFound = false;
            for (ReportItem ditem : checkPointReport.getItems())
            {
               DetailReportItem detailReport = (DetailReportItem) ditem;
               if (detailReport.getObjectId().equals(line1.getObjectId()))
                  line1objectIdFound = true;
               if (detailReport.getObjectId().equals(line2.getObjectId()))
                  line2objectIdFound = true;
               if (detailReport.getObjectId().equals(line3.getObjectId()))
                  line3objectIdFound = true;
            }
            Assert.assertTrue(line1objectIdFound,
                  "detail report must refer line 1");
            Assert.assertTrue(line2objectIdFound,
                  "detail report must refer line 2");
            Assert.assertFalse(line3objectIdFound,
                  "detail report must not refer line 3");
         }
      }
      Assert.assertTrue(found, "report must contain a 3-Line-1 checkPoint");

   }

   @SuppressWarnings("unchecked")
   @Test(groups = { "line" }, description = "3-Line-2")
   public void verifyTest3_2() throws ChouetteException
   {
      // 3-Line-2 : check if line has routes
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

      line1.getRoutes().clear();

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
         if (checkPointReport.getMessageKey().equals("3-Line-2"))
         {
            found = true;
            Assert.assertEquals(checkPointReport.getStatus(),
                  Report.STATE.ERROR,
                  " checkPointReport must be on level error");
            Assert.assertEquals(checkPointReport.hasItems(), true,
                  " checkPointReport must have items");
            Assert.assertEquals(checkPointReport.getItems().size(), 1,
                  " checkPointReport must have 1 item");

            // check detail keys = line1 objectids
            boolean line1objectIdFound = false;
            for (ReportItem ditem : checkPointReport.getItems())
            {
               DetailReportItem detailReport = (DetailReportItem) ditem;
               if (detailReport.getObjectId().equals(line1.getObjectId()))
                  line1objectIdFound = true;
            }
            Assert.assertTrue(line1objectIdFound,
                  "detail report must refer line 1");
         }
      }
      Assert.assertTrue(found, "report must contain a 3-Line-2 checkPoint");

   }
   
   @SuppressWarnings("unchecked")
   @Test(groups = { "Line" }, description = "3-Line-3")
   public void verifyTest3_3() throws ChouetteException
   {
      // 3-Line-4 : check transport mode
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
      
      { // check test not required when missing parameter
      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertEquals(report.getStatus(), Report.STATE.OK,
            " report must be on level ok");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-Line-3"))
         {
            found = true;
         }
      }
      Assert.assertFalse(found, "report must not contain a 3-Line-3 checkPoint");
      }
      
      parameters.append("check_allowed_transport_modes", Integer.valueOf(0));
      
      { // check test not required when check is false
      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertEquals(report.getStatus(), Report.STATE.OK,
            " report must be on level ok");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-Line-3"))
         {
            found = true;
         }
      }
      Assert.assertFalse(found, "report must not contain a 3-Line-3 checkPoint");
      }
      
      parameters.put("check_allowed_transport_modes",Integer.valueOf(1));
      
      { // check test not required when check is false
      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertEquals(report.getStatus(), Report.STATE.OK,
            " report must be on level ok");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-Line-3"))
         {
            found = true;
         }
      }
      Assert.assertTrue(found, "report must contain a 3-Line-3 checkPoint");
      }
 
      JSONObject busParam = parameters.getJSONObject("mode_bus");
      busParam.put("allowed_transport", Integer.valueOf(0));
      line1.setTransportModeName(TransportModeNameEnum.Bus);
      { // check test not required when check is false
      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertEquals(report.getStatus(), Report.STATE.WARNING,
            " report must be on level warning");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      boolean found = false;
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-Line-3"))
         {
            found = true;
            Assert.assertEquals(checkPointReport.getStatus(),
                  Report.STATE.WARNING,
                  " checkPointReport must be on level warning");
            Assert.assertEquals(checkPointReport.hasItems(), true,
                  " checkPointReport must have items");
            Assert.assertEquals(checkPointReport.getItems().size(), 1,
                  " checkPointReport must have 1 item");

            // check detail keys = line1 objectids
            boolean line1objectIdFound = false;
            for (ReportItem ditem : checkPointReport.getItems())
            {
               DetailReportItem detailReport = (DetailReportItem) ditem;
               if (detailReport.getObjectId().equals(line1.getObjectId()))
                  line1objectIdFound = true;
            }
            Assert.assertTrue(line1objectIdFound,
                  "detail report must refer line 1");
            
         }
      }
      Assert.assertTrue(found, "report must contain a 3-Line-3 checkPoint");
      }
      
      
   }

}
