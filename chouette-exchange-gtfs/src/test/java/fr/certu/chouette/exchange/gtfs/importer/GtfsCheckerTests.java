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
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
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

	private GtfsImporter importerLf = null;

	private GtfsChecker checker = new GtfsChecker();

	Path targetDirectory = null;
	Path targetDirectoryGood = null;
	Path targetDirectoryIndex = null;
	Path targetDirectoryLf = null;


	@BeforeGroups (groups = {"GtfsChecker"})
	public void initImporter() throws Exception
	{
		targetDirectory = Files.createTempDirectory("gtfs_import_");
		targetDirectoryGood = Files.createTempDirectory("gtfs_import_");
		targetDirectoryIndex = Files.createTempDirectory("gtfs_import_");
		targetDirectoryLf = Files.createTempDirectory("gtfs_import_");
		FileTool.uncompress("src/test/data/test_missing_data.zip", targetDirectory.toFile());
		FileTool.uncompress("src/test/data/test_gtfs.zip", targetDirectoryGood.toFile());
		FileTool.uncompress("src/test/data/test_missing_index.zip", targetDirectoryIndex.toFile());
		FileTool.uncompress("src/test/data/test_missing_lf.zip", targetDirectoryLf.toFile());

		importer = new GtfsImporter(targetDirectory.toString());
		importerGood = new GtfsImporter(targetDirectoryGood.toString());
		importerIndex = new GtfsImporter(targetDirectoryIndex.toString());
		importerLf = new GtfsImporter(targetDirectoryLf.toString());
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
		importerLf.dispose();
		FileUtils.deleteDirectory(targetDirectoryLf.toFile());
	}

	@Test(groups = { "GtfsChecker" }, description = "GtfsChecker should report missing mandatory data on files")
	public void verifyCheckFull() throws Exception
	{    
		Report report = new ExchangeReport(ExchangeReport.KEY.IMPORT,
				"GTFS");
		report.updateStatus(Report.STATE.OK);
		ReportItem zipReport = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_FILE, Report.STATE.OK, "test_missing_data.zip");
		report.addItem(zipReport);

		boolean ok = checker.check(importer, zipReport, true);
		Assert.assertFalse(ok, "checker should return false");

		printReport(report);

		checkReportCounts(zipReport,9,0,8,1);

	}

	private void checkReportCounts(Report report, int reportSize,int okSize, int errorSize, int warnSize)
	{
		List<ReportItem> items = report.getItems();
		Assert.assertEquals(items.size(), reportSize,"report must have expected item count ");
		int ok = 0;
		int err = 0; 
		int warn = 0;
		for (ReportItem reportItem : items) 
		{
			switch (reportItem.getStatus()) 
			{
			case ERROR:
				err++;
				break;
			case OK:
				ok++;
				break;
			case WARNING:
				warn++;
				break;
			default:
				break;
			}
		}
		Assert.assertEquals(ok, okSize,"report must have expected ok item count ");
		Assert.assertEquals(err, errorSize,"report must have expected error item count ");
		Assert.assertEquals(warn, warnSize,"report must have expected warn item count ");
	}

	@Test(groups = { "GtfsChecker" }, description = "GtfsChecker should report missing mandatory data on files")
	public void verifyCheckStops() throws Exception
	{    
		Report report = new ExchangeReport(ExchangeReport.KEY.IMPORT,
				"GTFS");
		report.updateStatus(Report.STATE.OK);
		ReportItem zipReport = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_FILE, Report.STATE.OK, "test_missing_data.zip");
		report.addItem(zipReport);

		boolean ok = checker.check(importer, zipReport, false);
		Assert.assertFalse(ok, "checker should return false");

		printReport(report);

		checkReportCounts(zipReport,2,0,2,0);

	}

	@Test(groups = { "GtfsChecker" }, description = "GtfsChecker should report missing mandatory data on files")
	public void verifyCheckFullOk() throws Exception
	{    
		Report report = new ExchangeReport(ExchangeReport.KEY.IMPORT,
				"GTFS");
		report.updateStatus(Report.STATE.OK);
		ReportItem zipReport = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_FILE, Report.STATE.OK, "test_gtfs.zip");
		report.addItem(zipReport);

		boolean ok = checker.check(importerGood, zipReport, true);
		Assert.assertTrue(ok, "checker should return true");

		printReport(report);

		checkReportCounts(zipReport,9,8,0,1);

	}

	@Test(groups = { "GtfsChecker" }, description = "GtfsChecker should report missing mandatory data on files")
	public void verifyCheckStopsOk() throws Exception
	{    
		Report report = new ExchangeReport(ExchangeReport.KEY.IMPORT,
				"GTFS");
		report.updateStatus(Report.STATE.OK);
		ReportItem zipReport = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_FILE, Report.STATE.OK, "test_gtfs.zip");
		report.addItem(zipReport);

		boolean ok = checker.check(importerGood, zipReport, false);
		Assert.assertTrue(ok, "checker should return true");

		printReport(report);

		checkReportCounts(zipReport,2,2,0,0);

	}

	@Test(groups = { "GtfsChecker" }, description = "GtfsChecker should report missing mandatory data on files")
	public void verifyCheckFullIndex() throws Exception
	{    
		Report report = new ExchangeReport(ExchangeReport.KEY.IMPORT,
				"GTFS");
		report.updateStatus(Report.STATE.OK);

		ReportItem zipReport = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_FILE, Report.STATE.OK, "test_missing_index.zip");
		report.addItem(zipReport);
		boolean ok = checker.check(importerIndex, zipReport, true);
		Assert.assertFalse(ok, "checker should return false");

		printReport(report);

		checkReportCounts(zipReport,9,1,8,0);

	}

	@Test(groups = { "GtfsChecker" }, description = "GtfsChecker should report missing mandatory data on files")
	public void verifyCheckStopsIndex() throws Exception
	{    
		Report report = new ExchangeReport(ExchangeReport.KEY.IMPORT,
				"GTFS");
		report.updateStatus(Report.STATE.OK);
		ReportItem zipReport = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_FILE, Report.STATE.OK, "test_missing_index.zip");
		report.addItem(zipReport);


		boolean ok = checker.check(importerIndex, zipReport, false);
		Assert.assertFalse(ok, "checker should return false");

		printReport(report);

		checkReportCounts(zipReport,2,0,2,0);


	}

	@Test(groups = { "GtfsChecker" }, description = "GtfsChecker should report missing linefeed on files")
	public void verifyCheckFullLf() throws Exception
	{    
		Report report = new ExchangeReport(ExchangeReport.KEY.IMPORT,
				"GTFS");
		report.updateStatus(Report.STATE.OK);
		ReportItem zipReport = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_FILE, Report.STATE.OK, "test_missing_lf.zip");
		report.addItem(zipReport);

		boolean ok = checker.check(importerLf, zipReport, true);
		Assert.assertFalse(ok, "checker should return false");

		printReport(report);

		checkReportCounts(zipReport,9,6,2,1);

	}


}
