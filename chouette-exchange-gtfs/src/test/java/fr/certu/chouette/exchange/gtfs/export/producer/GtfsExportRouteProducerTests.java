package fr.certu.chouette.exchange.gtfs.export.producer;

import org.apache.log4j.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsRouteProducer;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.model.GtfsRoute;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.Line;

@ContextConfiguration(locations = { "classpath:testContext.xml",
      "classpath*:chouetteContext.xml" })
public class GtfsExportRouteProducerTests extends
      AbstractTestNGSpringContextTests
{
   private static final Logger logger = Logger
         .getLogger(GtfsExportRouteProducerTests.class);

   @Test(groups = { "Producers" }, description = "test route with both short and long name")
   public void verifyRouteProducerWithShortAndLongName()
         throws ChouetteException
   {
      GtfsRouteProducer producer = new GtfsRouteProducer();

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
      GtfsRoute gtfsObject = producer.produce(neptuneObject, report);
      System.out.println("verifyRouteProducerWithShortAndLongName");

      Assert.assertNotNull(gtfsObject, "Route should be returned");
      System.out.println(GtfsRoute.header);
      System.out.println(gtfsObject.getCSVLine() + "\n");
      Assert.assertEquals(gtfsObject.getRouteShortName(), neptuneObject.getName(),
            "RouteShortName must be line Name");
      Assert.assertEquals(gtfsObject.getRouteLongName(),
            neptuneObject.getPublishedName(),
            "RouteLongName must be correctly set");

   }

   @Test(groups = { "Producers" }, description = "test route with no short name")
   public void verifyRouteProducerWithNoShortName() throws ChouetteException
   {
      GtfsRouteProducer producer = new GtfsRouteProducer();

      GtfsReport report = new GtfsReport(GtfsReport.KEY.EXPORT);
      Line neptuneObject = new Line();
      neptuneObject.setObjectId("GTFS:Line:4321");
      neptuneObject.setNumber("lineNumber");
      neptuneObject.setPublishedName("publishedLineName");
      Company company = new Company();
      company.setObjectId("GTFS:Company:1234");
      company.setName("name");
      neptuneObject.setCompany(company);
      GtfsRoute gtfsObject = producer.produce(neptuneObject, report);
      System.out.println("verifyRouteProducerWithNoShortName");

      Assert.assertNotNull(gtfsObject, "Route should be returned");
      System.out.println(GtfsRoute.header);
      System.out.println(gtfsObject.getCSVLine() + "\n");
      Assert.assertNull(gtfsObject.getRouteShortName(),
            "RouteShortName must be null");
      Assert.assertEquals(gtfsObject.getRouteLongName(),
            neptuneObject.getPublishedName(),
            "RouteLongName must be correctly set");

   }

   @Test(groups = { "Producers" }, description = "test route with no long name")
   public void verifyRouteProducerWithNoLongName() throws ChouetteException
   {
      GtfsRouteProducer producer = new GtfsRouteProducer();

      GtfsReport report = new GtfsReport(GtfsReport.KEY.EXPORT);
      Line neptuneObject = new Line();
      neptuneObject.setObjectId("GTFS:Line:4321");
      neptuneObject.setName("lineName");
      neptuneObject.setNumber("lineNumber");
      Company company = new Company();
      company.setObjectId("GTFS:Company:1234");
      company.setName("name");
      neptuneObject.setCompany(company);
      GtfsRoute gtfsObject = producer.produce(neptuneObject, report);
      System.out.println("verifyRouteProducerWithNoLongName");

      Assert.assertNotNull(gtfsObject, "Route should be returned");
      System.out.println(GtfsRoute.header);
      System.out.println(gtfsObject.getCSVLine() + "\n");
      Assert.assertEquals(gtfsObject.getRouteShortName(), neptuneObject.getName(),
            "RouteShortName must be line Name");
      Assert.assertNull(gtfsObject.getRouteLongName(),
            "RouteLongName must be empty");

   }

   @Test(groups = { "Producers" }, description = "test route with no name")
   public void verifyRouteProducerWithNoName() throws ChouetteException
   {
      GtfsRouteProducer producer = new GtfsRouteProducer();
      GtfsReport report = new GtfsReport(GtfsReport.KEY.EXPORT);

      Line neptuneObject = new Line();
      neptuneObject.setObjectId("GTFS:Line:4321");
      Company company = new Company();
      company.setObjectId("GTFS:Company:1234");
      company.setName("name");
      neptuneObject.setCompany(company);
      GtfsRoute gtfsObject = producer.produce(neptuneObject, report);
      System.out.println("verifyRouteProducerWithNoName");
      System.out.println(gtfsObject);

      Assert.assertNull(gtfsObject, "GTFS Route must be null");

   }

   // private void printItems(String indent,List<ReportItem> items)
   // {
   // if (items == null) return;
   // for (ReportItem item : items)
   // {
   // System.out.println(indent+item.getStatus().name()+" : "+item.getLocalizedMessage());
   // printItems(indent+"   ",item.getItems());
   // }
   //
   // }

}
