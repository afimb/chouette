package fr.certu.chouette.exchange.gtfs.importer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;

@ContextConfiguration(locations = { "classpath:testContext.xml",
      "classpath*:chouetteContext.xml" })
@SuppressWarnings("unchecked")
public class GtfsImportStopAreaTests extends AbstractTestNGSpringContextTests
{
   private static final Logger logger = Logger
         .getLogger(GtfsImportStopAreaTests.class);

   private IImportPlugin<StopArea> importStopArea = null;
   private String path = "src/test/data/";

   @Test(groups = { "ImportGTFS", "CheckParameters" }, description = "Get a bean from context")
   public void getBean()
   {
      importStopArea = (IImportPlugin<StopArea>) applicationContext
            .getBean("GtfsStopAreaImport");
   }

   @Test(groups = { "CheckParameters" }, description = "Import Plugin should reject wrong file extension", dependsOnMethods = { "getBean" }, expectedExceptions = { IllegalArgumentException.class })
   public void verifyCheckFileExtension() throws ChouetteException
   {
      List<ParameterValue> parameters = new ArrayList<ParameterValue>();
      SimpleParameterValue simpleParameterValue = new SimpleParameterValue(
            "inputFile");
      simpleParameterValue.setFilepathValue(path + "/dummyFile.tmp");
      parameters.add(simpleParameterValue);
      ReportHolder report = new ReportHolder();

      importStopArea.doImport(parameters, report, null);
      Assert.fail("expected exception not raised");
   }

   @Test(groups = { "CheckParameters" }, description = "Import Plugin should reject unknown parameter", dependsOnMethods = { "getBean" }, expectedExceptions = { IllegalArgumentException.class })
   public void verifyCheckUnknownParameter() throws ChouetteException
   {
      List<ParameterValue> parameters = new ArrayList<ParameterValue>();
      SimpleParameterValue simpleParameterValue = new SimpleParameterValue(
            "inputFile");
      simpleParameterValue.setFilepathValue(path + "/dummyFile.xml");
      parameters.add(simpleParameterValue);
      simpleParameterValue = new SimpleParameterValue("dummyParameter");
      simpleParameterValue.setStringValue("dummy value");
      parameters.add(simpleParameterValue);
      ReportHolder report = new ReportHolder();

      importStopArea.doImport(parameters, report, null);
      Assert.fail("expected exception not raised");
   }

   @Test(groups = { "CheckParameters" }, description = "Import Plugin should reject missing mandatory parameter", dependsOnMethods = { "getBean" }, expectedExceptions = { IllegalArgumentException.class })
   public void verifyCheckMandatoryParameter() throws ChouetteException
   {
      List<ParameterValue> parameters = new ArrayList<ParameterValue>();
      SimpleParameterValue simpleParameterValue = new SimpleParameterValue(
            "validate");
      simpleParameterValue.setBooleanValue(true);
      parameters.add(simpleParameterValue);
      ReportHolder report = new ReportHolder();

      importStopArea.doImport(parameters, report, null);
      Assert.fail("expected exception not raised");
   }

   @Test(groups = { "CheckParameters" }, description = "Import Plugin should reject wrong file type", dependsOnMethods = { "getBean" }, expectedExceptions = { IllegalArgumentException.class })
   public void verifyCheckFileType() throws ChouetteException
   {
      List<ParameterValue> parameters = new ArrayList<ParameterValue>();
      SimpleParameterValue simpleParameterValue = new SimpleParameterValue(
            "inputFile");
      simpleParameterValue.setFilepathValue(path + "/dummyFile.zip");
      parameters.add(simpleParameterValue);
      simpleParameterValue = new SimpleParameterValue("fileFormat");
      simpleParameterValue.setStringValue("txt");
      parameters.add(simpleParameterValue);
      ReportHolder report = new ReportHolder();

      importStopArea.doImport(parameters, report, null);
      Assert.fail("expected exception not raised");
   }

   @Test(groups = { "CheckParameters" }, description = "Import Plugin should reject file not found", dependsOnMethods = { "getBean" })
   public void verifyCheckinputFileExists() throws ChouetteException
   {
      List<ParameterValue> parameters = new ArrayList<ParameterValue>();
      SimpleParameterValue simpleParameterValue = new SimpleParameterValue(
            "inputFile");
      simpleParameterValue.setFilepathValue(path + "/dummyFile.tmp");
      parameters.add(simpleParameterValue);
      simpleParameterValue = new SimpleParameterValue("fileFormat");
      simpleParameterValue.setStringValue("zip");
      parameters.add(simpleParameterValue);
      simpleParameterValue = new SimpleParameterValue("objectIdPrefix");
      simpleParameterValue.setStringValue("GTFS");
      parameters.add(simpleParameterValue);
      ReportHolder report = new ReportHolder();

      List<StopArea> areas = importStopArea.doImport(parameters, report, null);
      Assert.assertNull(areas, "areas must be null");
      List<ReportItem> items = report.getReport().getItems();
      printReport(report.getReport());
      boolean found = false;
      for (ReportItem reportItem : items)
      {
         if (reportItem.getMessageKey().equals("FILE_ERROR"))
            found = true;
      }
      Assert.assertTrue(found, "FILE_ERROR must be found in report");

   }

   @Test(groups = { "CheckParameters" }, description = "Import Plugin should return format description", dependsOnMethods = { "getBean" })
   public void verifyFormatDescription()
   {
      FormatDescription description = importStopArea.getDescription();
      List<ParameterDescription> params = description
            .getParameterDescriptions();

      Assert.assertEquals(description.getName(), "GTFS");
      Assert.assertNotNull(params, "params should not be null");
      Assert.assertEquals(params.size(), 9, " params size must equal 9");
      logger.info("Description \n " + description.toString());
      Reporter.log("Description \n " + description.toString());

   }

   private void verifyCheckinputFileMissingEntry(String zipName)
         throws ChouetteException
   {
      List<ParameterValue> parameters = new ArrayList<ParameterValue>();
      SimpleParameterValue simpleParameterValue = new SimpleParameterValue(
            "inputFile");
      simpleParameterValue.setFilepathValue(path + "/" + zipName);
      parameters.add(simpleParameterValue);
      simpleParameterValue = new SimpleParameterValue("objectIdPrefix");
      simpleParameterValue.setStringValue("GTFS");
      parameters.add(simpleParameterValue);
      ReportHolder report = new ReportHolder();

      List<StopArea> areas = importStopArea.doImport(parameters, report, null);
      Assert.assertEquals(areas.size(), 0, "areas must be empty");
      List<ReportItem> items = report.getReport().getItems();
      printReport(report.getReport());
      boolean found = false;
      for (ReportItem reportItem : items)
      {
         if (reportItem.getMessageKey().equals("ZIP_MISSING_ENTRY"))
            found = true;
      }
      Assert.assertTrue(found, "ZIP_MISSING_ENTRY must be found in report");

   }

   @Test(groups = { "CheckFiles" }, description = "Import Plugin should reject file when missing stops.txt", dependsOnMethods = { "getBean" })
   public void verifyCheckinputFileMissingStops() throws ChouetteException
   {
      verifyCheckinputFileMissingEntry("test_gtfs_no_stop.zip");
   }

   @Test(groups = { "ImportStopArea" }, description = "Import Plugin should import file", dependsOnMethods = { "getBean" })
   public void verifyImportStopArea() throws ChouetteException
   {
      verifyImportStopArea(path + "/test_gtfs.zip", 12, 1);
      // try to clean data
      System.gc();
      // wait 1 second for next test
      try
      {
         Thread.sleep(1000);
      } catch (InterruptedException e)
      {
         e.printStackTrace();
      }
   }

   @Test(groups = { "ImportStopArea" }, description = "Import Plugin should accept bom marker", dependsOnMethods = { "getBean" })
   public void verifyImportStopAreaWithBOM() throws ChouetteException
   {
      verifyImportStopArea(path + "/test_gtfs_bom.zip", 12, 1);
      // try to clean data
      System.gc();
      // wait 1 second for next test
      try
      {
         Thread.sleep(1000);
      } catch (InterruptedException e)
      {
         e.printStackTrace();
      }
   }

   private List<StopArea> verifyImportStopArea(String file, int areaSize,
         int connectionLinkSize) throws ChouetteException
   {
      List<ParameterValue> parameters = new ArrayList<ParameterValue>();
      SimpleParameterValue simpleParameterValue = new SimpleParameterValue(
            "inputFile");
      simpleParameterValue.setFilepathValue(file);
      parameters.add(simpleParameterValue);
      simpleParameterValue = new SimpleParameterValue("objectIdPrefix");
      simpleParameterValue.setStringValue("GTFS");
      parameters.add(simpleParameterValue);

      ReportHolder report = new ReportHolder();

      List<StopArea> areas = importStopArea.doImport(parameters, report, null);

      printReport(report.getReport());

      Assert.assertNotNull(areas, "areas can't be null");
      Assert.assertEquals(areas.size(), areaSize, "areas size must equals "
            + areaSize);
      Set<ConnectionLink> clinks = new HashSet<ConnectionLink>();
      for (StopArea area : areas)
      {
         // comptage des objets :
         if (area.getConnectionLinks() != null)
         {
            clinks.addAll(area.getConnectionLinks());
         }
      }
      Assert.assertEquals(clinks.size(), connectionLinkSize,
            "connection links size must equals " + connectionLinkSize);

      return areas;
   }

   private void printReport(Report report)
   {
      if (report == null)
      {
         Reporter.log("no report");
      } else
      {
         Reporter.log(report.getStatus().name() + " : "
               + report.getLocalizedMessage());
         printItems("   ", report.getItems());
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
         Reporter.log(indent + item.getStatus().name() + " : "
               + item.getLocalizedMessage());
         printItems(indent + "   ", item.getItems());
      }

   }

}
