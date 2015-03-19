package fr.certu.chouette.exchange.gtfs.importer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import fr.certu.chouette.exchange.gtfs.refactor.importer.GtfsException;
import fr.certu.chouette.exchange.gtfs.refactor.importer.GtfsImporter;
import fr.certu.chouette.exchange.gtfs.refactor.importer.Index;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsAgency;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendar;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendarDate;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsRoute;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStop;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime;
import fr.certu.chouette.plugin.exchange.tools.FileTool;

@ContextConfiguration(locations = { "classpath:testContext.xml",
      "classpath*:chouetteContext.xml" })
public class GtfsImportDataTests extends AbstractTestNGSpringContextTests
{   
   private GtfsImporter importer = null;
   
   Path targetDirectory = null;;

   
   @BeforeGroups (groups = {"GtfsImporter"})
   public void initImporter() throws Exception
   {
      targetDirectory = Files.createTempDirectory("gtfs_import_");
      FileTool.uncompress("src/test/data/test_missing_data.zip", targetDirectory.toFile());
      
      importer = new GtfsImporter(targetDirectory.toString());
   }

   
   @AfterGroups (groups = {"GtfsImporter"})
   public void releaseImporter() throws Exception
   {
      importer.dispose();
      FileUtils.deleteDirectory(targetDirectory.toFile());
   }

   @Test(groups = { "GtfsImporter" }, description = "GtfsImporter should report missing mandatory data on agency.txt")
   public void verifyMandatoryAgencyData() throws Exception
   {      
      int errors = 0;
      int ok = 0;
      for (Iterator<GtfsAgency> it =  importer.getAgencyById().iterator(); it.hasNext(); )
      {
         try
         {
            it.next();
            ok++;
         }
         catch (GtfsException e)
         {
            //logger.warn("missing data ",e);
            errors++;
         }
      }
      Assert.assertEquals(ok, 1, "only 1 agency must be loaded");
      Assert.assertEquals(errors, 4,"report must have errors ");
      
   }

   @Test(groups = { "GtfsImporter" }, description = "GtfsImporter should report missing mandatory data on stops.txt")
   public void verifyMandatoryStopsData() throws Exception
   {
      int errors = 0;
      int ok = 0;
      Index<GtfsStop> stops = importer.getStopById();
      for (Iterator<GtfsStop> it =  stops.iterator(); it.hasNext(); )
      {
         try
         {
            GtfsStop bean = it.next();
            // foreign key
            stops.validate(bean, importer);
            ok++;
         }
         catch (GtfsException e)
         {
            // logger.warn("missing data ",e);
            errors++;
         }
      }
      Assert.assertEquals(ok, 2, "only 2 stop must be loaded");
      Assert.assertEquals(errors, 4,"report must have errors ");
   }

   @Test(groups = { "GtfsImporter" }, description = "GtfsImporter should report missing mandatory data on calendars.txt")
   public void verifyMandatoryCalendarsData() throws Exception
   {
      int errors = 0;
      int ok = 0;
      for (Iterator<GtfsCalendar> it =  importer.getCalendarByService().iterator(); it.hasNext(); )
      {
         try
         {
            it.next();
            ok++;
         }
         catch (GtfsException e)
         {
            //logger.warn("missing data ",e);
            errors++;
         }
      }
      Assert.assertEquals(ok, 1, "only 1 calendar must be loaded");
      Assert.assertEquals(errors, 3,"report must have errors ");
   }

   @Test(groups = { "GtfsImporter" }, description = "GtfsImporter should report missing mandatory data on calendar_dates.txt")
   public void verifyMandatoryCalendarDatesData() throws Exception
   {
      int errors = 0;
      int ok = 0;
      for (Iterator<GtfsCalendarDate> it =  importer.getCalendarDateByService().iterator(); it.hasNext(); )
      {
         try
         {
            it.next();
            ok++;
         }
         catch (GtfsException e)
         {
            //logger.warn("missing data ",e);
            errors++;
         }
      }
      Assert.assertEquals(ok, 1, "only 1 calendarDate must be loaded");
      Assert.assertEquals(errors, 2,"report must have errors ");
   }

   @Test(groups = { "GtfsImporter" }, description = "GtfsImporter should report missing mandatory data on routes.txt")
   public void verifyMandatoryRoutesData() throws Exception
   {
      int errors = 0;
      int ok = 0;
      for (Iterator<GtfsRoute> it =  importer.getRouteById().iterator(); it.hasNext(); )
      {
         try
         {
            it.next();
            ok++;
         }
         catch (GtfsException e)
         {
            //logger.warn("missing data ",e);
            errors++;
         }
      }
      Assert.assertEquals(ok, 3, "only 3 Route must be loaded");
      Assert.assertEquals(errors, 2,"report must have errors ");
   }

   @Test(groups = { "GtfsImporter" }, description = "GtfsImporter should report missing mandatory data on stop_times.txt")
   public void verifyMandatoryStopTimesData() throws Exception
   {
      int errors = 0;
      int ok = 0;
      for (Iterator<GtfsStopTime> it =  importer.getStopTimeByTrip().iterator(); it.hasNext(); )
      {
         try
         {
            it.next();
            ok++;
         }
         catch (GtfsException e)
         {
            //logger.warn("missing data ",e);
            errors++;
         }
      }
      Assert.assertEquals(ok, 1, "only 1 stopTime must be loaded");
      Assert.assertEquals(errors, 5,"report must have errors ");
   }

   @Test(groups = { "GtfsImporter" }, description = "GtfsImporter should report missing mandatory data on transfers.txt")
   public void verifyMandatoryTransfersData() throws Exception
   {
//      Assert.assertEquals(data.getTransfers().size(), 1,
//            "only 1 transfer must be loaded");
//      Assert.assertEquals(report.getItems().size(), 3,
//            "report must have items ");
   }

   @Test(groups = { "GtfsImporter" }, description = "GtfsImporter should report missing mandatory data on trips.txt")
   public void verifyMandatoryTripsData() throws Exception
   {
//      Assert.assertEquals(data.getTrips().size(), 1,
//            "only 1 trip must be loaded");
//      Assert.assertEquals(report.getItems().size(), 4,
//            "report must have items ");
   }


}
