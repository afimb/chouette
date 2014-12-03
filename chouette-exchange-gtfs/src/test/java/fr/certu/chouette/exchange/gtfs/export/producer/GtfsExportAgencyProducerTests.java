package fr.certu.chouette.exchange.gtfs.export.producer;

import org.apache.log4j.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.gtfs.export.producer.mock.GtfsExporterMock;
import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsAgencyProducer;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsAgency;
import fr.certu.chouette.model.neptune.Company;

@ContextConfiguration(locations = { "classpath:testContext.xml",
      "classpath*:chouetteContext.xml" })
public class GtfsExportAgencyProducerTests extends
      AbstractTestNGSpringContextTests
{
   private static final Logger logger = Logger
         .getLogger(GtfsExportAgencyProducerTests.class);
   
   private GtfsExporterMock mock = new GtfsExporterMock();

   @Test(groups = { "Producers" }, description = "test full company data")
   public void verifyAgencyProducer1() throws ChouetteException
   {
      mock.reset();
      GtfsAgencyProducer producer = new GtfsAgencyProducer(mock);
      GtfsReport report = new GtfsReport(GtfsReport.KEY.EXPORT);
      Company neptuneObject = new Company();
      neptuneObject.setObjectId("GTFS:Company:1234");
      neptuneObject.setName("name");
      neptuneObject.setShortName("short");
      neptuneObject.setRegistrationNumber("1234");
      neptuneObject.setUrl("http://www.mywebsite.com");
      neptuneObject.setPhone("01 02 03 04 05");

      producer.save(neptuneObject, report, "GTFS", null);
      GtfsAgency gtfsObject = mock.getExportedAgencies().get(0);
      Reporter.log("verifyAgencyProducer1");

      Assert.assertEquals(gtfsObject.getAgencyId(),
            toGtfsId(neptuneObject.getObjectId()),
            "agency id must be correcty set");
      Assert.assertEquals(gtfsObject.getAgencyName(), "name",
            "agency name must be correcty set");
      Assert.assertEquals(gtfsObject.getAgencyUrl().toString(),
            "http://www.mywebsite.com", "agency url must be correcty set");
      Assert.assertEquals(gtfsObject.getAgencyPhone(), "01 02 03 04 05",
            "agency phone must be correcty set");

   }

   @Test(groups = { "Producers" }, description = "test medium company data")
   public void verifyAgencyProducer2() throws ChouetteException
   {

      mock.reset();
      GtfsAgencyProducer producer = new GtfsAgencyProducer(mock);

      GtfsReport report = new GtfsReport(GtfsReport.KEY.EXPORT);
      Company neptuneObject = new Company();
      neptuneObject.setObjectId("GTFS:Company:1234");
      neptuneObject.setName("name");
      neptuneObject.setShortName("short");
      neptuneObject.setPhone("01 02 03 04 05");

      producer.save(neptuneObject, report, "GTFS", null);
      GtfsAgency gtfsObject = mock.getExportedAgencies().get(0);
      Reporter.log("verifyAgencyProducer2");

      Assert.assertEquals(gtfsObject.getAgencyId(),
            toGtfsId(neptuneObject.getObjectId()),
            "agency id must be correcty set");
      Assert.assertEquals(gtfsObject.getAgencyName(), "name",
            "agency name must be correcty set");
      Assert.assertEquals(gtfsObject.getAgencyUrl().toString(),
            "http://www.short.com", "agency url must be correcty set");
      Assert.assertEquals(gtfsObject.getAgencyPhone(), "01 02 03 04 05",
            "agency phone must be correcty set");

   }

   @Test(groups = { "Producers" }, description = "test light company data")
   public void verifyAgencyProducer3() throws ChouetteException
   {

      mock.reset();
      GtfsAgencyProducer producer = new GtfsAgencyProducer(mock);

      GtfsReport report = new GtfsReport(GtfsReport.KEY.EXPORT);
      Company neptuneObject = new Company();
      neptuneObject.setObjectId("GTFS:Company:1234");
      neptuneObject.setName("name");
      neptuneObject.setPhone("01 02 03 04 05");

      producer.save(neptuneObject, report, "GTFS", null);
      GtfsAgency gtfsObject = mock.getExportedAgencies().get(0);
      Reporter.log("verifyAgencyProducer3");

      Assert.assertEquals(gtfsObject.getAgencyId(),
            toGtfsId(neptuneObject.getObjectId()),
            "agency id must be correcty set");
      Assert.assertEquals(gtfsObject.getAgencyName(), "name",
            "agency name must be correcty set");
      Assert.assertEquals(gtfsObject.getAgencyUrl().toString(),
            "http://www.null.com", "agency url must be correcty set");
      Assert.assertEquals(gtfsObject.getAgencyPhone(), "01 02 03 04 05",
            "agency phone must be correcty set");

   }

   protected String toGtfsId(String neptuneId)
   {
      String[] tokens = neptuneId.split(":");
      return tokens[2];
   }


}
