package fr.certu.chouette.exchange.gtfs.export;

import java.util.ArrayList;
import java.util.List;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;

@ContextConfiguration(locations = { "classpath:testContext.xml",
      "classpath*:chouetteContext.xml" })
public class GtfsExportTests extends AbstractTestNGSpringContextTests
{
   @SuppressWarnings("unchecked")
   @Test(groups = { "export" }, description = "test export GTFS Line")
   public void verifyExportLines() throws ChouetteException
   {
      INeptuneManager<Line> lineManager;
      lineManager = (INeptuneManager<Line>) applicationContext
            .getBean("lineManager");
      List<ParameterValue> values = new ArrayList<ParameterValue>();
      SimpleParameterValue file = new SimpleParameterValue("inputFile");
      file.setFilepathValue("src/test/data/test_neptune.zip");
      values.add(file);
      SimpleParameterValue validate = new SimpleParameterValue("validate");
      validate.setBooleanValue(Boolean.TRUE);
      values.add(validate);
      ReportHolder reportHolder = new ReportHolder();
      ReportHolder reportHolder2 = new ReportHolder();
      List<Line> lines = lineManager.doImport(null, "NEPTUNE", values,
            reportHolder, reportHolder2);
      Assert.assertNotNull(lines, "lines can't be null");

      Report importReport = reportHolder.getReport();
      printItems("", importReport.getItems());

      List<ParameterValue> parameters = new ArrayList<ParameterValue>();
      SimpleParameterValue outputFile = new SimpleParameterValue("outputFile");
      outputFile.setFilepathValue("target/test/gtfs.zip");
      parameters.add(outputFile);
      SimpleParameterValue timeZone = new SimpleParameterValue("timeZone");
      timeZone.setStringValue("Europe/Paris");
      parameters.add(timeZone);

      ReportHolder exportReport = new ReportHolder();
      lineManager.doExport(null, lines, "GTFS", parameters, exportReport);
      Assert.assertNotNull(exportReport.getReport(), "report can't be null");
      Assert.assertEquals(exportReport.getReport().getStatus().name(), "OK",
            "report status should be ok");
      // printItems("",exportReport.getReport().getItems());

   }

   @SuppressWarnings("unchecked")
   @Test(groups = { "export" }, description = "test export GTFS StopAreas")
   public void verifyExportStopAreas() throws ChouetteException
   {
      INeptuneManager<Line> lineManager;
      lineManager = (INeptuneManager<Line>) applicationContext
            .getBean("lineManager");
      List<ParameterValue> values = new ArrayList<ParameterValue>();
      SimpleParameterValue file = new SimpleParameterValue("inputFile");
      file.setFilepathValue("src/test/data/test_neptune.zip");
      values.add(file);
      SimpleParameterValue validate = new SimpleParameterValue("validate");
      validate.setBooleanValue(Boolean.TRUE);
      values.add(validate);
      ReportHolder reportHolder = new ReportHolder();
      ReportHolder reportHolder2 = new ReportHolder();
      List<Line> lines = lineManager.doImport(null, "NEPTUNE", values,
            reportHolder, reportHolder2);
      Assert.assertNotNull(lines, "lines can't be null");

      List<StopArea> stops = new ArrayList<StopArea>();
      for (Line line : lines)
      {
         line.complete();
         for (StopArea stopArea : line.getStopAreas())
         {
            if (!stops.contains(stopArea))
               stops.add(stopArea);
         }
      }

      List<ParameterValue> parameters = new ArrayList<ParameterValue>();
      SimpleParameterValue outputFile = new SimpleParameterValue("outputFile");
      outputFile.setFilepathValue("target/test/gtfs.zip");
      parameters.add(outputFile);

      ReportHolder exportReport = new ReportHolder();
      INeptuneManager<StopArea> areaManager;
      areaManager = (INeptuneManager<StopArea>) applicationContext
            .getBean("stopAreaManager");
      areaManager.doExport(null, stops, "GTFS", parameters, exportReport);
      Assert.assertNotNull(exportReport.getReport(), "report can't be null");

      printItems("", exportReport.getReport().getItems());
      Assert.assertEquals(exportReport.getReport().getStatus().name(), "OK",
            "report status should be ok");

   }

   private void printItems(String indent, List<ReportItem> items)
   {
      if (items == null)
         return;
      for (ReportItem item : items)
      {
         System.out.println(indent + item.getStatus().name() + " : "
               + item.getLocalizedMessage());
         printItems(indent + "   ", item.getItems());
      }

   }

}
