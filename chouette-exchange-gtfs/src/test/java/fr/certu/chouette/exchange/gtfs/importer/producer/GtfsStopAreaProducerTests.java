package fr.certu.chouette.exchange.gtfs.importer.producer;

import java.math.BigDecimal;
import java.net.URL;
import java.util.TimeZone;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStop;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStop.LocationType;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStop.WheelchairBoardingType;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;

@ContextConfiguration(locations = { "classpath:testContext.xml", "classpath*:chouetteContext.xml" })
public class GtfsStopAreaProducerTests extends AbstractTestNGSpringContextTests
{

   @Test(groups = { "Producers" }, description = "test stop with full data")
   public void verifyStopAreaProducerFromStop() throws Exception
   {
      AbstractModelProducer.setPrefix("NINOXE");
      AbstractModelProducer.setIncrementalPrefix("");
      GtfsStop gtfsObject = new GtfsStop("12","code", "name","comment", 
            BigDecimal.valueOf(45),BigDecimal.valueOf(3),"zone",new URL("http://www.test.com"), 
            LocationType.Stop ,"24",TimeZone.getTimeZone("Europe/Paris"), 
            WheelchairBoardingType.Allowed,"street","locality","zipcode");
      StopAreaProducer producer = new StopAreaProducer();
      StopArea neptuneObject = producer.produce(gtfsObject, null);
      Assert.assertEquals(neptuneObject.getObjectId(), "NINOXE:StopArea:12", "objectId should be correctly set");
      Assert.assertEquals(neptuneObject.getName(), "name", "name should be correctly set");
      Assert.assertEquals(neptuneObject.getUrl(), "http://www.test.com", "url should be correctly set");
      Assert.assertEquals(neptuneObject.getTimeZone(), "Europe/Paris", "timezone should be correctly set");
      Assert.assertEquals(neptuneObject.getRegistrationNumber(), "code", "registrationNumber should be correctly set");
      Assert.assertEquals(neptuneObject.getComment(), "comment", "comment should be correctly set");
      Assert.assertEquals(neptuneObject.getAreaType(), ChouetteAreaEnum.BoardingPosition, "areaType should be correctly set");
      Assert.assertEquals(neptuneObject.getLatitude(), BigDecimal.valueOf(45), "latitude should be correctly set");
      Assert.assertEquals(neptuneObject.getLongitude(), BigDecimal.valueOf(3), "longitude should be correctly set");
      Assert.assertEquals(neptuneObject.getLongLatType(), LongLatTypeEnum.WGS84, "longLatType should be correctly set");
      Assert.assertEquals(neptuneObject.getParentObjectId(), "24", "ParentId should be correctly set");
      Assert.assertTrue(neptuneObject.isMobilityRestrictedSuitable(),  "mobilityRestrictedSuitable should be correctly set");
      Assert.assertEquals(neptuneObject.getStreetName(), "street", "streetName should be correctly set");
      Assert.assertEquals(neptuneObject.getCityName(), "locality", "cityName should be correctly set");
      Assert.assertEquals(neptuneObject.getZipCode(), "zipcode", "zipCode should be correctly set");
   }

   @Test(groups = { "Producers" }, description = "test stop with less data")
   public void verifyStopAreaProducerFromLightStop() throws Exception
   {
      AbstractModelProducer.setPrefix("NINOXE");
      AbstractModelProducer.setIncrementalPrefix("");
      GtfsStop gtfsObject = new GtfsStop("12",null, "name",null, 
            BigDecimal.valueOf(45),BigDecimal.valueOf(3),null,null, 
            null ,null,null, 
            null, null ,null,null);
      StopAreaProducer producer = new StopAreaProducer();
      StopArea neptuneObject = producer.produce(gtfsObject, null);
      Assert.assertEquals(neptuneObject.getObjectId(), "NINOXE:StopArea:12", "objectId should be correctly set");
      Assert.assertEquals(neptuneObject.getName(), "name", "name should be correctly set");
      Assert.assertNull(neptuneObject.getUrl(), "url should be correctly set");
      Assert.assertNull(neptuneObject.getTimeZone(), "timezone should be correctly set");
      Assert.assertNull(neptuneObject.getRegistrationNumber(), "registrationNumber should be correctly set");
      Assert.assertNull(neptuneObject.getComment(), "comment should be correctly set");
      Assert.assertEquals(neptuneObject.getAreaType(), ChouetteAreaEnum.BoardingPosition, "areaType should be correctly set");
      Assert.assertEquals(neptuneObject.getLatitude(), BigDecimal.valueOf(45), "latitude should be correctly set");
      Assert.assertEquals(neptuneObject.getLongitude(), BigDecimal.valueOf(3), "longitude should be correctly set");
      Assert.assertEquals(neptuneObject.getLongLatType(), LongLatTypeEnum.WGS84, "longLatType should be correctly set");
      Assert.assertNull(neptuneObject.getParentObjectId(), "ParentId should be correctly set");
      Assert.assertFalse(neptuneObject.isMobilityRestrictedSuitable(),  "mobilityRestrictedSuitable should be correctly set");
      Assert.assertNull(neptuneObject.getStreetName(), "streetName should be correctly set");
      Assert.assertNull(neptuneObject.getCityName(), "cityName should be correctly set");
      Assert.assertNull(neptuneObject.getZipCode(), "zipCode should be correctly set");
   }

   @Test(groups = { "Producers" }, description = "test station with full data")
   public void verifyStopAreaProducerFromStation() throws Exception
   {
      AbstractModelProducer.setPrefix("NINOXE");
      AbstractModelProducer.setIncrementalPrefix("");
      GtfsStop gtfsObject = new GtfsStop("12","code", "name","comment", 
            BigDecimal.valueOf(45),BigDecimal.valueOf(3),"zone",new URL("http://www.test.com"), 
            LocationType.Station ,null,TimeZone.getTimeZone("Europe/Paris"), 
            WheelchairBoardingType.Allowed,"street","locality","zipcode");
      StopAreaProducer producer = new StopAreaProducer();
      StopArea neptuneObject = producer.produce(gtfsObject, null);
      Assert.assertEquals(neptuneObject.getObjectId(), "NINOXE:StopArea:12", "objectId should be correctly set");
      Assert.assertEquals(neptuneObject.getName(), "name", "name should be correctly set");
      Assert.assertEquals(neptuneObject.getUrl(), "http://www.test.com", "url should be correctly set");
      Assert.assertEquals(neptuneObject.getTimeZone(), "Europe/Paris", "timezone should be correctly set");
      Assert.assertEquals(neptuneObject.getRegistrationNumber(), "code", "registrationNumber should be correctly set");
      Assert.assertEquals(neptuneObject.getComment(), "comment", "comment should be correctly set");
      Assert.assertEquals(neptuneObject.getAreaType(), ChouetteAreaEnum.CommercialStopPoint, "areaType should be correctly set");
      Assert.assertEquals(neptuneObject.getLatitude(), BigDecimal.valueOf(45), "latitude should be correctly set");
      Assert.assertEquals(neptuneObject.getLongitude(), BigDecimal.valueOf(3), "longitude should be correctly set");
      Assert.assertEquals(neptuneObject.getLongLatType(), LongLatTypeEnum.WGS84, "longLatType should be correctly set");
      Assert.assertEquals(neptuneObject.getParentObjectId(), null, "ParentId should not be  set");
      Assert.assertTrue(neptuneObject.isMobilityRestrictedSuitable(),  "mobilityRestrictedSuitable should be correctly set");
      Assert.assertEquals(neptuneObject.getStreetName(), "street", "streetName should be correctly set");
      Assert.assertEquals(neptuneObject.getCityName(), "locality", "cityName should be correctly set");
      Assert.assertEquals(neptuneObject.getZipCode(), "zipcode", "zipCode should be correctly set");
   }

   
   @Test(groups = { "Producers" }, description = "test access with full data")
   public void verifyStopAreaProducerFromAccess() throws Exception
   {
      AbstractModelProducer.setPrefix("NINOXE");
      AbstractModelProducer.setIncrementalPrefix("");
      GtfsStop gtfsObject = new GtfsStop("12","code", "name","comment", 
            BigDecimal.valueOf(45),BigDecimal.valueOf(3),"zone",new URL("http://www.test.com"), 
            LocationType.Access ,"24",TimeZone.getTimeZone("Europe/Paris"), 
            WheelchairBoardingType.Allowed,"street","locality","zipcode");
      StopAreaProducer producer = new StopAreaProducer();
      StopArea neptuneObject = producer.produce(gtfsObject, null);
      Assert.assertNull(neptuneObject,  "access type is not produced");
   }

}
