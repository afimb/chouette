package fr.certu.chouette.validation.checkpoint;

import java.io.IOException;
import java.util.List;

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
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem.PHASE;

@ContextConfiguration(locations = { "classpath:testContext.xml",
      "classpath*:chouetteContext.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class ValidationNominal extends
      AbstractTransactionalTestNGSpringContextTests
{

   @SuppressWarnings("unchecked")
   @BeforeMethod
   public void cleanData() throws Exception
   {
      // springTestContextPrepareTestInstance();

      INeptuneManager<Line> lineManager = (INeptuneManager<Line>) applicationContext
            .getBean("lineManager");
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

   }

   @SuppressWarnings("unchecked")
   @Test(groups = { "all" }, description = "3-all-ok")
   public void verifyTestOk() throws ChouetteException
   {
      // 3-all-1 : no warning nor error
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
      beans = lineManager.getAll(null);

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
      for (Line line : beans)
      {
         line.complete();
      }

      lineManager.validate(null, beans, parameters, report, null, true);
      report.refreshStatus();

      AbstractTestValidation.printReport(report);

      Assert.assertEquals(report.getStatus(), Report.STATE.OK,
            "report must be on level ok");
      Assert.assertEquals(report.hasItems(), true, "report must have items");
      for (ReportItem item : report.getItems())
      {
         CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
         if (checkPointReport.getMessageKey().equals("3-Route-5"))
         {
            Assert.assertEquals(
                  checkPointReport.getStatus(),
                  Report.STATE.UNCHECK,
                  "checkPointReport " + checkPointReport.getMessageKey()
                        + " must not be on level "
                        + checkPointReport.getStatus());
         } else
         {
            Assert.assertEquals(
                  checkPointReport.getStatus(),
                  Report.STATE.OK,
                  "checkPointReport " + checkPointReport.getMessageKey()
                        + " must not be on level "
                        + checkPointReport.getStatus());
         }
      }

   }

}
