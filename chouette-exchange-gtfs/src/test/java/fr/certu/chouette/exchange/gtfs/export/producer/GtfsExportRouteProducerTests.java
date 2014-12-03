package fr.certu.chouette.exchange.gtfs.export.producer;

import org.apache.log4j.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.gtfs.export.producer.mock.GtfsExporterMock;
import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsRouteProducer;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsAgency;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsRoute;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.Line;

@ContextConfiguration(locations = { "classpath:testContext.xml",
      "classpath*:chouetteContext.xml" })
public class GtfsExportRouteProducerTests extends
      AbstractTestNGSpringContextTests
{
   private static final Logger logger = Logger
         .getLogger(GtfsExportRouteProducerTests.class);

   private GtfsExporterMock mock = new GtfsExporterMock();

   @Test(groups = { "Producers" }, description = "test route with both short and long name")
   public void verifyRouteProducerWithShortAndLongName()
         throws ChouetteException
   {
      mock.reset();
      GtfsRouteProducer producer = new GtfsRouteProducer(mock);

      GtfsReport report = new GtfsReport(GtfsReport.KEY.EXPORT);
      Line neptuneObject = new Line();
      neptuneObject.setObjectId("GTFS:Line:4321");
      neptuneObject.setName("lineName");
      neptuneObject.setNumber("lineNumber");
      neptuneObject.setPublishedName("publishedLineName");
      Company company = new Company();
      company.setObjectId("GTFS:Company:1234");
      company.setName("name");
      neptuneObject.setCompany(company);
      
      producer.save(neptuneObject, report, "GTFS");
      GtfsRoute gtfsObject = mock.getExportedRoutes().get(0);
      Reporter.log("verifyRouteProducerWithShortAndLongName");

      Assert.assertNotNull(gtfsObject, "Route should be returned");

      Assert.assertEquals(gtfsObject.getRouteShortName(), neptuneObject.getNumber(),
            "RouteShortName must be line Number");
      Assert.assertEquals(gtfsObject.getRouteLongName(),
            neptuneObject.getPublishedName(),
            "RouteLongName must be correctly set");

   }

   @Test(groups = { "Producers" }, description = "test route with no short name")
   public void verifyRouteProducerWithNoShortName() throws ChouetteException
   {
      mock.reset();
      GtfsRouteProducer producer = new GtfsRouteProducer(mock);

      GtfsReport report = new GtfsReport(GtfsReport.KEY.EXPORT);
      Line neptuneObject = new Line();
      neptuneObject.setObjectId("GTFS:Line:4321");
      neptuneObject.setName("lineNname");
      neptuneObject.setPublishedName("publishedLineName");
      Company company = new Company();
      company.setObjectId("GTFS:Company:1234");
      company.setName("name");
      neptuneObject.setCompany(company);
      producer.save(neptuneObject, report, "GTFS");
      GtfsRoute gtfsObject = mock.getExportedRoutes().get(0);
      Reporter.log("verifyRouteProducerWithNoShortName");

      Assert.assertNotNull(gtfsObject, "Route should be returned");
      Assert.assertEquals(gtfsObject.getRouteShortName(),neptuneObject.getName(),
            "RouteShortName must be line name");
      Assert.assertEquals(gtfsObject.getRouteLongName(),
            neptuneObject.getPublishedName(),
            "RouteLongName must be correctly set");

   }

   @Test(groups = { "Producers" }, description = "test route with no long name")
   public void verifyRouteProducerWithNoLongName() throws ChouetteException
   {
      mock.reset();
      GtfsRouteProducer producer = new GtfsRouteProducer(mock);

      GtfsReport report = new GtfsReport(GtfsReport.KEY.EXPORT);
      Line neptuneObject = new Line();
      neptuneObject.setObjectId("GTFS:Line:4321");
      neptuneObject.setNumber("lineNumber");
      Company company = new Company();
      company.setObjectId("GTFS:Company:1234");
      company.setName("name");
      neptuneObject.setCompany(company);
      producer.save(neptuneObject, report, "GTFS");
      GtfsRoute gtfsObject = mock.getExportedRoutes().get(0);
      Reporter.log("verifyRouteProducerWithNoLongName");

      Assert.assertNotNull(gtfsObject, "Route should be returned");
      Assert.assertEquals(gtfsObject.getRouteShortName(), neptuneObject.getNumber(),
            "RouteShortName must be line Number");
      Assert.assertNull(gtfsObject.getRouteLongName(),
            "RouteLongName must be empty");

   }
// expectedExceptions = { IllegalArgumentException.class }
   @Test(groups = { "Producers" }, description = "test route with no name")
   public void verifyRouteProducerWithNoName() throws ChouetteException
   {
      mock.reset();
      GtfsRouteProducer producer = new GtfsRouteProducer(mock);
      GtfsReport report = new GtfsReport(GtfsReport.KEY.EXPORT);

      Line neptuneObject = new Line();
      neptuneObject.setObjectId("GTFS:Line:4321");
      Company company = new Company();
      company.setObjectId("GTFS:Company:1234");
      company.setName("name");
      neptuneObject.setCompany(company);
      boolean state = producer.save(neptuneObject, report, "GTFS");
      Reporter.log("verifyRouteProducerWithNoName");
      Assert.assertFalse(state, "GTFS Route must not be produced");
 
   }


}
