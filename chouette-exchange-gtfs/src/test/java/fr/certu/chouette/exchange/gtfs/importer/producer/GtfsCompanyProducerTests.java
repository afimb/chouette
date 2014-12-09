package fr.certu.chouette.exchange.gtfs.importer.producer;

import java.net.URL;
import java.util.TimeZone;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsAgency;
import fr.certu.chouette.model.neptune.Company;

@ContextConfiguration(locations = { "classpath:testContext.xml", "classpath*:chouetteContext.xml" })
public class GtfsCompanyProducerTests extends AbstractTestNGSpringContextTests
{

   @Test(groups = { "Producers" }, description = "test company with full data")
   public void verifyCompanyProducer1() throws Exception
   {
      AbstractModelProducer.setPrefix("NINOXE");
      AbstractModelProducer.setIncrementalPrefix("");
      GtfsAgency gtfsObject = new GtfsAgency("12", "name", new URL("http://www.test.com"), 
            TimeZone.getTimeZone("Europe/Paris"), "FR", "01 02 03 04 05", null);
      CompanyProducer producer = new CompanyProducer();
      Company neptuneObject = producer.produce(gtfsObject, null);
      Assert.assertEquals(neptuneObject.getObjectId(), "NINOXE:Company:12", "objectId should be correctly set");
      Assert.assertEquals(neptuneObject.getName(), "name", "name should be correctly set");
      Assert.assertEquals(neptuneObject.getUrl(), "http://www.test.com", "url should be correctly set");
      Assert.assertEquals(neptuneObject.getTimeZone(), "Europe/Paris", "timezone should be correctly set");
      Assert.assertEquals(neptuneObject.getPhone(), "01 02 03 04 05", "phone should be correctly set");
   }

}
