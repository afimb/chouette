package mobi.chouette.exchange.gtfs.exporter.producer;

import java.util.TimeZone;

import mobi.chouette.core.ChouetteException;
import mobi.chouette.exchange.gtfs.exporter.producer.mock.GtfsExporterMock;
import mobi.chouette.exchange.gtfs.model.GtfsAgency;
import mobi.chouette.exchange.gtfs.model.exporter.AgencyExporter;
import mobi.chouette.exchange.gtfs.model.importer.Context;
import mobi.chouette.model.Company;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

public class GtfsExportAgencyProducerTests 
{
   
   private GtfsExporterMock mock = new GtfsExporterMock();
   private Context context = new Context();
   private GtfsAgencyProducer producer = new GtfsAgencyProducer(mock);

   @Test(groups = { "Producers" }, description = "test full company data")
   public void verifyAgencyProducer1() throws ChouetteException
   {
      mock.reset();
      
      Company neptuneObject = new Company();
      neptuneObject.setObjectId("GTFS:Company:1234");
      neptuneObject.setName("name");
      neptuneObject.setShortName("short");
      neptuneObject.setRegistrationNumber("1234");
      neptuneObject.setUrl("http://www.mywebsite.com");
      neptuneObject.setPhone("01 02 03 04 05");

      producer.save(neptuneObject,  "GTFS", null,false);
      GtfsAgency gtfsObject = mock.getExportedAgencies().get(0);
      Reporter.log("verifyAgencyProducer1");
      Reporter.log(AgencyExporter.CONVERTER.to(context, gtfsObject));

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

      Company neptuneObject = new Company();
      neptuneObject.setObjectId("GTFS:Company:1234");
      neptuneObject.setName("name");
      neptuneObject.setShortName("short");
      neptuneObject.setPhone("01 02 03 04 05");

      producer.save(neptuneObject, "GTFS", null,false);
      GtfsAgency gtfsObject = mock.getExportedAgencies().get(0);
      Reporter.log("verifyAgencyProducer2");
      Reporter.log(AgencyExporter.CONVERTER.to(context, gtfsObject));

      Assert.assertEquals(gtfsObject.getAgencyId(),
            toGtfsId(neptuneObject.getObjectId()),
            "agency id must be correcty set");
      Assert.assertEquals(gtfsObject.getAgencyName(), "name",
            "agency name must be correcty set");
      Assert.assertEquals(gtfsObject.getAgencyUrl().toString(),
            "http://www.short.com", "agency url must be correcty set");
      Assert.assertEquals(gtfsObject.getAgencyPhone(), "01 02 03 04 05",
            "agency phone must be correcty set");
      Assert.assertEquals(gtfsObject.getAgencyTimezone(), TimeZone.getDefault(), "agency timezone must be correctly set");

   }

   @Test(groups = { "Producers" }, description = "test light company data")
   public void verifyAgencyProducer3() throws ChouetteException
   {

      mock.reset();

      Company neptuneObject = new Company();
      neptuneObject.setObjectId("GTFS:Company:1234");
      neptuneObject.setName("name");
      neptuneObject.setPhone("01 02 03 04 05");

      producer.save(neptuneObject, "GTFS", null,false);
      GtfsAgency gtfsObject = mock.getExportedAgencies().get(0);
      Reporter.log("verifyAgencyProducer3");
      Reporter.log(AgencyExporter.CONVERTER.to(context, gtfsObject));

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
   
   @Test(groups = { "Producers" }, description = "test timezone affectation")
   public void verifyAgencyProducer4() throws ChouetteException
   {

      mock.reset();

      Company neptuneObject = new Company();
      neptuneObject.setObjectId("GTFS:Company:1234");
      neptuneObject.setName("name");

      Reporter.log("verifyAgencyProducer4");
      producer.save(neptuneObject,  "GTFS", TimeZone.getTimeZone("America/Montreal"),false);
      GtfsAgency gtfsObject = mock.getExportedAgencies().get(0);
      Reporter.log(AgencyExporter.CONVERTER.to(context, gtfsObject));

      Assert.assertEquals(gtfsObject.getAgencyTimezone().getID(),"America/Montreal" ,
            "agency timezone must be correcty set");
      
      neptuneObject.setTimeZone("Europe/Paris");
      mock.reset();
      producer.save(neptuneObject, "GTFS", TimeZone.getTimeZone("America/Montreal"),false);
      gtfsObject = mock.getExportedAgencies().get(0);
      Reporter.log(AgencyExporter.CONVERTER.to(context, gtfsObject));
      Assert.assertEquals(gtfsObject.getAgencyTimezone().getID(),"Europe/Paris" ,
            "agency timezone must be correcty set");


   }
   

   protected String toGtfsId(String neptuneId)
   {
      String[] tokens = neptuneId.split(":");
      return tokens[2];
   }


}
