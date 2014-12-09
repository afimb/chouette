package fr.certu.chouette.exchange.gtfs.importer.producer;

import java.awt.Color;
import java.net.URL;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsRoute;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsRoute.RouteType;
import fr.certu.chouette.model.neptune.Route;

@ContextConfiguration(locations = { "classpath:testContext.xml", "classpath*:chouetteContext.xml" })
public class GtfsRouteProducerTests extends AbstractTestNGSpringContextTests
{

   @Test(groups = { "Producers" }, description = "test Route ")
   public void verifyLineProducerFull() throws Exception
   {
      AbstractModelProducer.setPrefix("NINOXE");
      AbstractModelProducer.setIncrementalPrefix("");
      GtfsRoute gtfsObject = new GtfsRoute("12", "10", "shortName", "longName" ,
            "comment", RouteType.Subway ,new URL("http://www.test.com"), 
            Color.blue,Color.red);
      RouteProducer producer = new RouteProducer();
      Route neptuneObject = producer.produce(gtfsObject, null);
      Assert.assertEquals(neptuneObject.getObjectId(), "NINOXE:Route:12", "objectId should be correctly set");
      Assert.assertEquals(neptuneObject.getWayBack(), "A", "wayback should be correctly set");
      
   }



}
