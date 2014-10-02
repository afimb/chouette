package fr.certu.chouette.exchange.xml.neptune;

import java.util.ArrayList;
import java.util.List;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.ValidationReport;

@ContextConfiguration(locations = { "classpath:testContext.xml",
      "classpath*:chouetteContext.xml" })
public class ValidationTests extends AbstractTestNGSpringContextTests
{

   private IImportPlugin<Line> importLine = null;
   private String path = "src/test/resources/lignes_neptune_err/";

   @SuppressWarnings("unchecked")
   @Test(groups = { "validation" }, description = "test")
   @Parameters({ "description", "testFile", "mandatoryErrorTest", "status" })
   public void verifyValidation(String description, String testFile,
         String mandatoryErrorTest, String status) throws ChouetteException
   {

      importLine = (IImportPlugin<Line>) applicationContext
            .getBean("NeptuneLineImport");

      List<ParameterValue> parameters = new ArrayList<ParameterValue>();
      SimpleParameterValue simpleParameterValue = new SimpleParameterValue(
            "inputFile");
      simpleParameterValue.setFilepathValue(path + "/" + testFile);
      parameters.add(simpleParameterValue);

      ReportHolder ireport = new ReportHolder();
      ReportHolder vreport = new ReportHolder();

      List<Line> lines = importLine.doImport(parameters, ireport, vreport);

      Report importReport = ireport.getReport();
      ValidationReport valReport = (ValidationReport) vreport.getReport();
      // System.out.println(importReport.getLocalizedMessage());
      // printItems("",importReport.getItems());
      printReport(valReport);

      checkMandatoryTest(mandatoryErrorTest, valReport, status);

   }

   /**
    * @param mandatoryTest
    * @param importReport
    * @param valReport
    * @param state
    */
   private void checkMandatoryTest(String mandatoryTest,
         ValidationReport valReport, String status)
   {
      if (mandatoryTest.equals("NONE"))
      {
         for (ReportItem phase : valReport.getItems())
         {
            Assert.assertEquals(phase.getStatus().toString(), status,
                  phase.getMessageKey() + " must have status " + status);
         }
      } else
      {
         CheckPointReportItem foundItem = null;
         for (ReportItem phase : valReport.getItems())
         {
            for (ReportItem item : phase.getItems())
            {
               CheckPointReportItem cp = (CheckPointReportItem) item;
               if (cp.getMessageKey().equals(mandatoryTest))
               {
                  foundItem = cp;
                  break;
               }
            }
            if (foundItem != null)
               break;
         }
         Assert.assertNotNull(foundItem, mandatoryTest + " must be reported");
         Assert.assertEquals(foundItem.getStatus().toString(), status,
               mandatoryTest + " must have status " + status);
      }
   }

   private void printReport(Report report)
   {
      if (report == null)
      {
         Reporter.log("no report");
      } else
      {
         // Reporter.log(report.toJSON());

         Reporter.log(report.getStatus().name() + " : "
               + report.getLocalizedMessage());
         printItems("---", report.getItems());
      }
   }

   /**
    * @param indent
    * @param items
    */
   private void printItems(String indent, List<ReportItem> items)
   {
      if (items == null)
         return;
      for (ReportItem item : items)
      {
         if (item instanceof CheckPointReportItem)
         {
            CheckPointReportItem citem = (CheckPointReportItem) item;
            Reporter.log(indent + citem.getStatus().name() + " : "
                  + citem.getMessageKey());

         } else if (item instanceof DetailReportItem)
         {
            DetailReportItem ditem = (DetailReportItem) item;
            Reporter.log(indent + ditem.toJSON());
         } else
         {
            Reporter.log(indent + item.getStatus().name() + " : "
                  + item.getLocalizedMessage());
         }
         printItems(indent + "---", item.getItems());
      }

   }

}
