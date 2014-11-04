package fr.certu.chouette.exchange.gtfs.importer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import fr.certu.chouette.exchange.gtfs.refactor.importer.GtfsImporter;
import fr.certu.chouette.plugin.exchange.report.ExchangeReport;
import fr.certu.chouette.plugin.exchange.tools.FileTool;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;

@ContextConfiguration(locations = { "classpath:testContext.xml",
      "classpath*:chouetteContext.xml" })
public class GtfsCheckerTests extends AbstractGtfsTest
{   
   private GtfsImporter importer = null;

   private GtfsImporter importerGood = null;
   
   private GtfsImporter importerIndex = null;

   private GtfsChecker checker = new GtfsChecker();
   
   Path targetDirectory = null;
   Path targetDirectoryGood = null;
   Path targetDirectoryIndex = null;

   
   @BeforeGroups (groups = {"GtfsChecker"})
   public void initImporter() throws Exception
   {
      targetDirectory = Files.createTempDirectory("gtfs_import_");
      targetDirectoryGood = Files.createTempDirectory("gtfs_import_");
      targetDirectoryIndex = Files.createTempDirectory("gtfs_import_");
      FileTool.uncompress("src/test/data/test_missing_data.zip", targetDirectory.toFile());
      FileTool.uncompress("src/test/data/test_gtfs.zip", targetDirectoryGood.toFile());
      FileTool.uncompress("src/test/data/test_missing_index.zip", targetDirectoryIndex.toFile());
      
      importer = new GtfsImporter(targetDirectory.toString());
      importerGood = new GtfsImporter(targetDirectoryGood.toString());
      importerIndex = new GtfsImporter(targetDirectoryIndex.toString());
   }

   
   @AfterGroups (groups = {"GtfsChecker"})
   public void releaseImporter() throws Exception
   {
      importer.dispose();
      FileUtils.deleteDirectory(targetDirectory.toFile());
      importerGood.dispose();
      FileUtils.deleteDirectory(targetDirectoryGood.toFile());
      importerIndex.dispose();
      FileUtils.deleteDirectory(targetDirectoryIndex.toFile());
   }

   @Test(groups = { "GtfsChecker" }, description = "GtfsChecker should report missing mandatory data on files")
   public void verifyCheckFull() throws Exception
   {    
      Report report = new ExchangeReport(ExchangeReport.KEY.IMPORT,
            "GTFS");
      report.updateStatus(Report.STATE.OK);
      
      boolean ok = checker.check(importer, report, true);
      Assert.assertFalse(ok, "checker should return false");
      
      printReport(report);
      
      List<ReportItem> items = report.getItems();
      Assert.assertEquals(items.size(), 9,"report must have 9 items ");
      
   }

   @Test(groups = { "GtfsChecker" }, description = "GtfsChecker should report missing mandatory data on files")
   public void verifyCheckStops() throws Exception
   {    
      Report report = new ExchangeReport(ExchangeReport.KEY.IMPORT,
            "GTFS");
      report.updateStatus(Report.STATE.OK);
      
      boolean ok = checker.check(importer, report, false);
      Assert.assertFalse(ok, "checker should return false");
      
      printReport(report);
      
      List<ReportItem> items = report.getItems();
      Assert.assertEquals(items.size(), 2,"report must have 2 items ");
      
   }

   @Test(groups = { "GtfsChecker" }, description = "GtfsChecker should report missing mandatory data on files")
   public void verifyCheckFullOk() throws Exception
   {    
      Report report = new ExchangeReport(ExchangeReport.KEY.IMPORT,
            "GTFS");
      report.updateStatus(Report.STATE.OK);
      
      boolean ok = checker.check(importerGood, report, true);
      Assert.assertTrue(ok, "checker should return true");
      
      printReport(report);
      
      List<ReportItem> items = report.getItems();
      Assert.assertEquals(items.size(), 1,"report must have 1 item ");
      
   }

   @Test(groups = { "GtfsChecker" }, description = "GtfsChecker should report missing mandatory data on files")
   public void verifyCheckStopsOk() throws Exception
   {    
      Report report = new ExchangeReport(ExchangeReport.KEY.IMPORT,
            "GTFS");
      report.updateStatus(Report.STATE.OK);
      
      boolean ok = checker.check(importerGood, report, false);
      Assert.assertTrue(ok, "checker should return true");
      
      printReport(report);
      
      List<ReportItem> items = report.getItems();
      Assert.assertNull(items,"report must have 0 items ");
      
   }

   @Test(groups = { "GtfsChecker" }, description = "GtfsChecker should report missing mandatory data on files")
   public void verifyCheckFullIndex() throws Exception
   {    
      Report report = new ExchangeReport(ExchangeReport.KEY.IMPORT,
            "GTFS");
      report.updateStatus(Report.STATE.OK);
      
      boolean ok = checker.check(importerIndex, report, true);
      Assert.assertFalse(ok, "checker should return false");
      
      printReport(report);
      
      List<ReportItem> items = report.getItems();
      Assert.assertEquals(items.size(), 8,"report must have 8 items ");
      
   }

   @Test(groups = { "GtfsChecker" }, description = "GtfsChecker should report missing mandatory data on files")
   public void verifyCheckStopsIndex() throws Exception
   {    
      Report report = new ExchangeReport(ExchangeReport.KEY.IMPORT,
            "GTFS");
      report.updateStatus(Report.STATE.OK);
      
      boolean ok = checker.check(importerIndex, report, false);
      Assert.assertFalse(ok, "checker should return false");
      
      printReport(report);
      
      List<ReportItem> items = report.getItems();
      Assert.assertEquals(items.size(), 2,"report must have 2 items ");
      
   }

}
