package fr.certu.chouette.exchange.gtfs.importer;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import fr.certu.chouette.plugin.exchange.report.ExchangeReport;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;

@ContextConfiguration(locations={"classpath:testContext.xml","classpath*:chouetteContext.xml"})
public class GtfsImportDataTests extends AbstractTestNGSpringContextTests
{
   private String path="src/test/data/";


   @Test (groups = {"GtfsData"}, description = "GtfsData should report missing mandatory data on agency.txt")
   public void verifyMandatoryAgencyData() throws Exception
   {
	   Report report = new ExchangeReport(ExchangeReport.KEY.IMPORT, "GTFS");
	   GtfsData data = new GtfsData("test", "tmp", false);
	   FileInputStream input = new FileInputStream(new File(path,"agency_missing_data.txt"));
	   data.loadAgencies(input, report);
	   printReport(report);    
	   Assert.assertEquals(data.getAgencies().size(), 1,"only 1 agency must be loaded");
	   Assert.assertEquals(report.getItems().size(), 4,"report must have items ");
   }


   @Test (groups = {"GtfsData"}, description = "GtfsData should report missing mandatory data on stops.txt")
   public void verifyMandatoryStopsData() throws Exception
   {
	   Report report = new ExchangeReport(ExchangeReport.KEY.IMPORT, "GTFS");
	   GtfsData data = new GtfsData("test", "tmp", false);
	   FileInputStream input = new FileInputStream(new File(path,"stops_missing_data.txt"));
	   data.loadStops(input, report);
	   printReport(report);    
	   Assert.assertEquals(data.getStops().size(), 1,"only 1 stop must be loaded");
	   Assert.assertEquals(report.getItems().size(), 5,"report must have items ");
   }

   @Test (groups = {"GtfsData"}, description = "GtfsData should report missing mandatory data on calendars.txt")
   public void verifyMandatoryCalendarsData() throws Exception
   {
	   Report report = new ExchangeReport(ExchangeReport.KEY.IMPORT, "GTFS");
	   GtfsData data = new GtfsData("test", "tmp", false);
	   FileInputStream input = new FileInputStream(new File(path,"calendars_missing_data.txt"));
	   data.loadCalendars(input, report);
	   printReport(report);    
	   Assert.assertEquals(data.getCalendars().size(), 1,"only 1 calendar must be loaded");
	   Assert.assertEquals(report.getItems().size(), 6,"report must have items ");
   }

   @Test (groups = {"GtfsData"}, description = "GtfsData should report missing mandatory data on calendar_dates.txt")
   public void verifyMandatoryCalendarDatesData() throws Exception
   {
	   Report report = new ExchangeReport(ExchangeReport.KEY.IMPORT, "GTFS");
	   GtfsData data = new GtfsData("test", "tmp", false);
	   FileInputStream input = new FileInputStream(new File(path,"calendar_dates_missing_data.txt"));
	   data.loadCalendarDates(input, report);
	   printReport(report);    
	   Assert.assertEquals(data.getCalendarDates().size(), 1,"only 1 calendar_date must be loaded");
	   Assert.assertEquals(report.getItems().size(), 4,"report must have items ");
   }

   @Test (groups = {"GtfsData"}, description = "GtfsData should report missing mandatory data on routes.txt")
   public void verifyMandatoryRoutesData() throws Exception
   {
	   Report report = new ExchangeReport(ExchangeReport.KEY.IMPORT, "GTFS");
	   GtfsData data = new GtfsData("test", "tmp", false);
	   FileInputStream input = new FileInputStream(new File(path,"routes_missing_data.txt"));
	   data.loadRoutes(input, report);
	   printReport(report);    
	   Assert.assertEquals(data.getRoutes().size(), 3,"only 3 routes must be loaded");
	   Assert.assertEquals(report.getItems().size(), 3,"report must have items ");
   }

   @Test (groups = {"GtfsData"}, description = "GtfsData should report missing mandatory data on stop_times.txt")
   public void verifyMandatoryStopTimesData() throws Exception
   {
	   Report report = new ExchangeReport(ExchangeReport.KEY.IMPORT, "GTFS");
	   GtfsData data = new GtfsData("test", "tmp", false);
	   FileInputStream input = new FileInputStream(new File(path,"stop_times_missing_data.txt"));
	   data.loadStopTimes(input, report);
	   printReport(report);    
	   Assert.assertEquals(data.getStopTimes().size(), 1,"only 1 stop_time must be loaded");
	   Assert.assertEquals(report.getItems().size(), 6,"report must have items ");
   }

   @Test (groups = {"GtfsData"}, description = "GtfsData should report missing mandatory data on transfers.txt")
   public void verifyMandatoryTransfersData() throws Exception
   {
	   Report report = new ExchangeReport(ExchangeReport.KEY.IMPORT, "GTFS");
	   GtfsData data = new GtfsData("test", "tmp", false);
	   FileInputStream input = new FileInputStream(new File(path,"transfers_missing_data.txt"));
	   data.loadTransfers(input, report);
	   printReport(report);    
	   Assert.assertEquals(data.getTransfers().size(), 1,"only 1 transfer must be loaded");
	   Assert.assertEquals(report.getItems().size(), 3,"report must have items ");
   }

   @Test (groups = {"GtfsData"}, description = "GtfsData should report missing mandatory data on trips.txt")
   public void verifyMandatoryStopData() throws Exception
   {
	   Report report = new ExchangeReport(ExchangeReport.KEY.IMPORT, "GTFS");
	   GtfsData data = new GtfsData("test", "tmp", false);
	   FileInputStream input = new FileInputStream(new File(path,"trips_missing_data.txt"));
	   data.loadTrips(input, report);
	   printReport(report);    
	   Assert.assertEquals(data.getTrips().size(), 1,"only 1 trip must be loaded");
	   Assert.assertEquals(report.getItems().size(), 4,"report must have items ");
   }


   private void printReport(Report report)
   {
      if (report == null)
      {
         Reporter.log("no report");
      }
      else
      {
         Reporter.log(report.getStatus().name()+" : "+report.getLocalizedMessage());
         printItems("   ",report.getItems());
      }
   }

   /**
    * @param indent
    * @param items
    */
   private void printItems(String indent,List<ReportItem> items) 
   {
      if (items == null) return;
      for (ReportItem item : items) 
      {
         Reporter.log(indent+item.getStatus().name()+" : "+item.getLocalizedMessage());
         printItems(indent+"   ",item.getItems());
      }

   }


}
