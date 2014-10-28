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
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
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
public class GtfsImportTests extends AbstractTestNGSpringContextTests
{
   private static final Logger logger = Logger.getLogger(GtfsImportTests.class);

   private IImportPlugin<Line> importLine = null;
   private String path = "src/test/data/";

   @Test(groups = { "ImportGTFS", "CheckParameters" }, description = "Get a bean from context")
   public void getBean()
   {
      importLine = (IImportPlugin<Line>) applicationContext
            .getBean("GtfsLineImport");
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

      importLine.doImport(parameters, report, null);
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

      importLine.doImport(parameters, report, null);
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

      importLine.doImport(parameters, report, null);
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

      importLine.doImport(parameters, report, null);
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

      List<Line> lines = importLine.doImport(parameters, report, null);
      Assert.assertNull(lines, "lines must be null");
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
      FormatDescription description = importLine.getDescription();
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

      List<Line> lines = importLine.doImport(parameters, report, null);
      Assert.assertEquals(lines.size(), 0, "lines must be empty");
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

   @Test(groups = { "CheckFiles" }, description = "Import Plugin should reject file when missing agency.txt", dependsOnMethods = { "getBean" })
   public void verifyCheckinputFileMissingAgency() throws ChouetteException
   {
      verifyCheckinputFileMissingEntry("test_gtfs_no_agency.zip");
   }

   @Test(groups = { "CheckFiles" }, description = "Import Plugin should reject file when missing routes.txt", dependsOnMethods = { "getBean" })
   public void verifyCheckinputFileMissingRoutes() throws ChouetteException
   {
      verifyCheckinputFileMissingEntry("test_gtfs_no_route.zip");
   }

   @Test(groups = { "CheckFiles" }, description = "Import Plugin should reject file when missing stops.txt", dependsOnMethods = { "getBean" })
   public void verifyCheckinputFileMissingStops() throws ChouetteException
   {
      verifyCheckinputFileMissingEntry("test_gtfs_no_stop.zip");
   }

   @Test(groups = { "CheckFiles" }, description = "Import Plugin should reject file when missing stop_times.txt", dependsOnMethods = { "getBean" })
   public void verifyCheckinputFileMissingStopTimes() throws ChouetteException
   {
      verifyCheckinputFileMissingEntry("test_gtfs_no_stop_time.zip");
   }

   @Test(groups = { "CheckFiles" }, description = "Import Plugin should reject file when missing trips.txt", dependsOnMethods = { "getBean" })
   public void verifyCheckinputFileMissingTrips() throws ChouetteException
   {
      verifyCheckinputFileMissingEntry("test_gtfs_no_trip.zip");
   }

   @Test(groups = { "CheckFiles" }, description = "Import Plugin should reject file when missing calendars.txt and calendar_dates", dependsOnMethods = { "getBean" })
   public void verifyCheckinputFileMissingCalendars() throws ChouetteException
   {
      verifyCheckinputFileMissingEntry("test_gtfs_no_calendar.zip");
   }

   @Test(groups = { "ImportLine" }, description = "Import Plugin should import file", dependsOnMethods = { "getBean" })
   public void verifyImportLine() throws ChouetteException
   {
      verifyImportLine(path + "/test_gtfs.zip", 2);
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

   @Test(groups = { "ImportLine" }, description = "Import Plugin should import file with three times a same stop on one journey", dependsOnMethods = { "getBean" })
   public void verifyImportLineWithThreeTimesAStopOnJourney()
         throws ChouetteException
   {
      verifyImportLine(path + "/test_gtfs_triple_stop.zip", 3);
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

   @Test(groups = { "ImportLine" }, description = "Import Plugin should import file with journey after midnight", dependsOnMethods = { "getBean" })
   public void verifyImportLineAfterMidnight() throws ChouetteException
   {
      List<Line> lines = verifyImportLine(path
            + "/test_gtfs_after_midnight.zip", 2);
      int afterMidnightVJ = 0;
      for (Line line : lines)
      {
         for (Route route : line.getRoutes())
         {
            for (JourneyPattern jp : route.getJourneyPatterns())
            {
               for (VehicleJourney vj : jp.getVehicleJourneys())
               {
                  for (Timetable tm : vj.getTimetables())
                  {
                     if (tm.getObjectId().endsWith(("_after_midnight")))
                     {
                        afterMidnightVJ++;
                     }
                  }
               }
            }
         }
         for (Timetable tm : line.getTimetables())
         {
            Reporter.log(tm.toString("\t", 0));
         }
      }

      Assert.assertEquals(afterMidnightVJ, 2,
            "import should produce 2 vehicle journeys after midnight");

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

   @Test(groups = { "ImportLine" }, description = "Import Plugin should set company when no agencyid set", dependsOnMethods = { "getBean" })
   public void verifyImportLineWithNoAgencyId() throws ChouetteException
   {
      verifyImportLine(path + "/test_gtfs_no_agencyid.zip", 2);
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

   @Test(groups = { "ImportLine" }, description = "Import Plugin should accept bom marker", dependsOnMethods = { "getBean" })
   public void verifyImportLineWithBOM() throws ChouetteException
   {
      verifyImportLine(path + "/test_gtfs_bom.zip", 2);
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

   private List<Line> verifyImportLine(String file, int routeSize)
         throws ChouetteException
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

      List<Line> lines = importLine.doImport(parameters, report, null);

      printReport(report.getReport());

      Assert.assertNotNull(lines, "lines can't be null");
      Assert.assertEquals(lines.size(), 1, "lines size must equals 1");
      for (Line line : lines)
      {
         // comptage des objets :
         Assert.assertNotNull(line.getPtNetwork(), "line must have a network");
         Assert.assertTrue(line.getGroupOfLines().isEmpty(),
               "line must have no groupOfLines");
         Assert.assertNotNull(line.getCompany(), "line must have a company");
         Assert.assertFalse(line.getRoutes().isEmpty(), "line must have routes");
         Assert.assertEquals(line.getRoutes().size(), routeSize,
               "line must have " + routeSize + " routes");
         Set<StopArea> bps = new HashSet<StopArea>();
         Set<StopArea> comms = new HashSet<StopArea>();

         for (Route route : line.getRoutes())
         {
            Assert.assertFalse(route.getJourneyPatterns().isEmpty(),
                  "line routes must have journeyPattens");
            for (JourneyPattern jp : route.getJourneyPatterns())
            {
               Assert.assertFalse(jp.getStopPoints().isEmpty(),
                     "line journeyPattens must have stoppoints");
               for (StopPoint point : jp.getStopPoints())
               {

                  Assert.assertNotNull(point.getContainedInStopArea(),
                        "stoppoints must have StopAreas");
                  bps.add(point.getContainedInStopArea());

                  Assert.assertNotNull(point.getContainedInStopArea()
                        .getParent(), "StopAreas must have parent : "
                        + point.getContainedInStopArea().getObjectId());
                  comms.add(point.getContainedInStopArea().getParent());
               }
            }
         }
         Assert.assertEquals(bps.size(), 8,
               "line must have 8 boarding positions");
         Assert.assertEquals(comms.size(), 4,
               "line must have 4 commercial stop points");

         Set<ConnectionLink> clinks = new HashSet<ConnectionLink>();

         for (StopArea comm : comms)
         {

            if (comm.getConnectionLinks() != null)
            {
               clinks.addAll(comm.getConnectionLinks());
            }
         }
         Assert.assertEquals(clinks.size(), 1,
               "line must have 1 connection link");
         for (ConnectionLink connectionLink : clinks)
         {
            Assert.assertNotNull(connectionLink.getDefaultDuration(),
                  "defaultDuration must not be null");
            long seconds = connectionLink.getDefaultDuration().getTime() / 1000;

            Assert.assertEquals(seconds, 240,
                  "line must have links duration of 4 minutes");
            Reporter.log(connectionLink.toString("\t", 1));

         }

         Reporter.log(line.toString("\t", 1));
      }

      return lines;
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
