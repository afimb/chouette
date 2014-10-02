package fr.certu.chouette.validation;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTNetwork;
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

   @SuppressWarnings("unchecked")
   @Test(groups = { "line" }, description = "3-Line-1")
   public void verifyTest1() throws ChouetteException
   {
      // 3-Line-1 : check if two lines have same name
      LineCheckPoints checkPoint = (LineCheckPoints) applicationContext
            .getBean("lineCheckPoints");
      checkPoint.setRouteCheckPoints(null);
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

      checkPoint.check(beans, parameters, report);
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
   public void verifyTest2() throws ChouetteException
   {
      // 3-Line-2 : check if line has routes
      LineCheckPoints checkPoint = (LineCheckPoints) applicationContext
            .getBean("lineCheckPoints");
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

      checkPoint.check(beans, parameters, report);
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

}
