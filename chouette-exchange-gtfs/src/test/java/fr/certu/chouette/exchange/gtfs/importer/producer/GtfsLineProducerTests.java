package fr.certu.chouette.exchange.gtfs.importer.producer;

import java.awt.Color;
import java.net.URL;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsRoute;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsRoute.RouteType;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;

@ContextConfiguration(locations = { "classpath:testContext.xml", "classpath*:chouetteContext.xml" })
public class GtfsLineProducerTests extends AbstractTestNGSpringContextTests
{

   @Test(groups = { "Producers" }, description = "test line with full data")
   public void verifyLineProducerFull() throws Exception
   {
      AbstractModelProducer.setPrefix("NINOXE");
      AbstractModelProducer.setIncrementalPrefix("");
      GtfsRoute gtfsObject = new GtfsRoute("12", "10", "shortName", "longName" ,
            "comment", RouteType.Subway ,new URL("http://www.test.com"), 
            Color.blue,Color.red);
      LineProducer producer = new LineProducer();
      Line neptuneObject = producer.produce(gtfsObject, null);
      Assert.assertEquals(neptuneObject.getObjectId(), "NINOXE:Line:12", "objectId should be correctly set");
      Assert.assertEquals(neptuneObject.getComment(), "comment", "comment should be correctly set");
      Assert.assertEquals(neptuneObject.getUrl(), "http://www.test.com", "url should be correctly set");
      Assert.assertEquals(neptuneObject.getCompanyIds().size(), 1, "company id should be added");
      Assert.assertEquals(neptuneObject.getCompanyIds().get(0), "NINOXE:Company:10", "company id should be correctly set");
      Assert.assertEquals(neptuneObject.getNumber(), "shortName", "number should be correctly set");
      Assert.assertEquals(neptuneObject.getName(), "longName", "name should be correctly set");
      Assert.assertEquals(neptuneObject.getPublishedName(), "longName", "publishedName should be correctly set");
      Assert.assertEquals(neptuneObject.getTransportModeName(), TransportModeNameEnum.Metro, "TransportModeName should be correctly set");
      Assert.assertEquals(neptuneObject.getColor(), "0000ff", "color should be correctly set");
      Assert.assertEquals(neptuneObject.getTextColor(), "ff0000", "textColor should be correctly set");
      
   }

   @Test(groups = { "Producers" }, description = "test line with less data")
   public void verifyLineProducerLight() throws Exception
   {
      AbstractModelProducer.setPrefix("NINOXE");
      AbstractModelProducer.setIncrementalPrefix("");
      GtfsRoute gtfsObject = new GtfsRoute("12", null, "shortName", null ,
            null, RouteType.Subway ,null, 
            null,null);
      LineProducer producer = new LineProducer();
      Line neptuneObject = producer.produce(gtfsObject, null);
      Assert.assertEquals(neptuneObject.getObjectId(), "NINOXE:Line:12", "objectId should be correctly set");
      Assert.assertNull(neptuneObject.getComment(), "comment should be correctly set");
      Assert.assertNull(neptuneObject.getUrl(), "url should be correctly set");
      Assert.assertEquals(neptuneObject.getCompanyIds().size(), 0, "company id should be added");
      Assert.assertEquals(neptuneObject.getNumber(), "shortName", "number should be correctly set");
      Assert.assertEquals(neptuneObject.getName(), "shortName", "name should be correctly set");
      Assert.assertNull(neptuneObject.getPublishedName(), "publishedName should be correctly set");
      Assert.assertEquals(neptuneObject.getTransportModeName(), TransportModeNameEnum.Metro, "TransportModeName should be correctly set");
      Assert.assertNull(neptuneObject.getColor(), "color should be correctly set");
      Assert.assertNull(neptuneObject.getTextColor(), "textColor should be correctly set");
      
   }

   @Test(groups = { "Producers" }, description = "test line with no short name")
   public void verifyLineProducerLongName() throws Exception
   {
      AbstractModelProducer.setPrefix("NINOXE");
      AbstractModelProducer.setIncrementalPrefix("");
      GtfsRoute gtfsObject = new GtfsRoute("12", null,  null , "longName",
            null, RouteType.Subway ,null, 
            null,null);
      LineProducer producer = new LineProducer();
      Line neptuneObject = producer.produce(gtfsObject, null);
      Assert.assertEquals(neptuneObject.getObjectId(), "NINOXE:Line:12", "objectId should be correctly set");
      Assert.assertNull(neptuneObject.getComment(), "comment should be correctly set");
      Assert.assertNull(neptuneObject.getUrl(), "url should be correctly set");
      Assert.assertEquals(neptuneObject.getCompanyIds().size(), 0, "company id should be added");
      Assert.assertNull(neptuneObject.getNumber(),  "number should be correctly set");
      Assert.assertEquals(neptuneObject.getName(), "longName", "name should be correctly set");
      Assert.assertEquals(neptuneObject.getPublishedName(), "longName", "publishedName should be correctly set");
      Assert.assertEquals(neptuneObject.getTransportModeName(), TransportModeNameEnum.Metro, "TransportModeName should be correctly set");
      Assert.assertNull(neptuneObject.getColor(), "color should be correctly set");
      Assert.assertNull(neptuneObject.getTextColor(), "textColor should be correctly set");
      
   }
   
   @Test(groups = { "Producers" }, description = "test line transportMode conversion")
   public void verifyLineProducerTransportModes() throws Exception
   {
      AbstractModelProducer.setPrefix("NINOXE");
      AbstractModelProducer.setIncrementalPrefix("");
      GtfsRoute gtfsObject = new GtfsRoute("12", null,  null , "longName",
            null, RouteType.Tram ,null, 
            null,null);
      LineProducer producer = new LineProducer();
      Line neptuneObject = producer.produce(gtfsObject, null);
      Assert.assertEquals(neptuneObject.getTransportModeName(), TransportModeNameEnum.Tramway, "TransportModeName should be correctly set");
      gtfsObject.setRouteType(RouteType.Subway);
      neptuneObject = producer.produce(gtfsObject, null);
      Assert.assertEquals(neptuneObject.getTransportModeName(), TransportModeNameEnum.Metro, "TransportModeName should be correctly set");
      gtfsObject.setRouteType(RouteType.Rail);
      neptuneObject = producer.produce(gtfsObject, null);
      Assert.assertEquals(neptuneObject.getTransportModeName(), TransportModeNameEnum.Train, "TransportModeName should be correctly set");
      gtfsObject.setRouteType(RouteType.Bus);
      neptuneObject = producer.produce(gtfsObject, null);
      Assert.assertEquals(neptuneObject.getTransportModeName(), TransportModeNameEnum.Bus, "TransportModeName should be correctly set");
      gtfsObject.setRouteType(RouteType.Ferry);
      neptuneObject = producer.produce(gtfsObject, null);
      Assert.assertEquals(neptuneObject.getTransportModeName(), TransportModeNameEnum.Ferry, "TransportModeName should be correctly set");
      gtfsObject.setRouteType(RouteType.Cable);
      neptuneObject = producer.produce(gtfsObject, null);
      Assert.assertEquals(neptuneObject.getTransportModeName(), TransportModeNameEnum.Other, "TransportModeName should be correctly set");
      gtfsObject.setRouteType(RouteType.Gondola);
      neptuneObject = producer.produce(gtfsObject, null);
      Assert.assertEquals(neptuneObject.getTransportModeName(), TransportModeNameEnum.Other, "TransportModeName should be correctly set");
      gtfsObject.setRouteType(RouteType.Funicular);
      neptuneObject = producer.produce(gtfsObject, null);
      Assert.assertEquals(neptuneObject.getTransportModeName(), TransportModeNameEnum.Other, "TransportModeName should be correctly set");

   }


}
