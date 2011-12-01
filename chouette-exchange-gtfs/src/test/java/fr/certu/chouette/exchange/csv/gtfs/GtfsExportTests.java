package fr.certu.chouette.exchange.csv.gtfs;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;

@ContextConfiguration(locations={"classpath:testContext.xml","classpath*:chouetteContext.xml"})
public class GtfsExportTests extends AbstractTestNGSpringContextTests
{
   private static final Logger logger = Logger.getLogger(GtfsExportTests.class);

   @SuppressWarnings("unchecked")
   @Test (groups = {"validation"}, description = "test" )
   public void verifyValidation() throws ChouetteException 
   {
      INeptuneManager<Line> lineManager;
      lineManager = (INeptuneManager<Line>) applicationContext.getBean("lineManager");
      List<ParameterValue> values = new ArrayList<ParameterValue>();
      SimpleParameterValue file = new SimpleParameterValue("inputFile");
      file.setFilepathValue("src/test/data/test.zip");
      values.add(file);
      SimpleParameterValue validate = new SimpleParameterValue("validate");
      validate.setBooleanValue(Boolean.TRUE);
      values.add(validate);
      ReportHolder reportHolder = new ReportHolder();
      List<Line> lines = lineManager.doImport(null, "NEPTUNE", values, reportHolder );

      Report importReport = reportHolder.getReport();
      printItems("",importReport.getItems());

      List<ParameterValue> parameters = new ArrayList<ParameterValue>();
      SimpleParameterValue outputFile = new SimpleParameterValue("outputFile");
      outputFile.setFilepathValue("target/test/gtfs.zip");
      parameters.add(outputFile);
      SimpleParameterValue timeZone = new SimpleParameterValue("timeZone");
      timeZone.setStringValue("Europe/Paris");
      parameters.add(timeZone);

      ReportHolder exportReport = new ReportHolder();
      lineManager.doExport(null  , lines, "GTFS", parameters, exportReport );

      printItems("",exportReport.getReport().getItems());
      
   }
   
   
   private void printItems(String indent,List<ReportItem> items) 
   {
      if (items == null) return;
      for (ReportItem item : items) 
      {
         System.out.println(indent+item.getStatus().name()+" : "+item.getLocalizedMessage());
         printItems(indent+"   ",item.getItems());
      }

   }

}
