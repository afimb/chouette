package fr.certu.chouette.exchange.gtfs.importer.producer;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTrip;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTrip.BikesAllowedType;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTrip.DirectionType;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTrip.WheelchairAccessibleType;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.plugin.exchange.tools.DbVehicleJourneyFactory;

@ContextConfiguration(locations = { "classpath:testContext.xml", "classpath*:chouetteContext.xml" })
public class GtfsVehicleJourneyProducerTests extends AbstractTestNGSpringContextTests
{
   DbVehicleJourneyFactory factory = new DbVehicleJourneyFactory("test", false);

   @Test(groups = { "Producers" }, description = "test trip with full data")
   public void verifyVehicleJourneyProducerFull() throws Exception
   {
      AbstractModelProducer.setPrefix("NINOXE");
      AbstractModelProducer.setIncrementalPrefix("");
      GtfsTrip gtfsObject = new GtfsTrip("r1", "s1","t1", "shortName", "headSign" ,
            DirectionType.Outbound, "b1" ,"shp1", 
            WheelchairAccessibleType.Allowed,BikesAllowedType.Allowed);
      VehicleJourneyProducer producer = new VehicleJourneyProducer();
      producer.setFactory(factory);
      VehicleJourney neptuneObject = producer.produce(gtfsObject, null);
      Assert.assertEquals(neptuneObject.getObjectId(), "NINOXE:VehicleJourney:t1", "objectId should be correctly set");
      Assert.assertEquals(neptuneObject.getPublishedJourneyName(), "shortName", "PublishedJourneyName should be correctly set");
      Assert.assertEquals(neptuneObject.getNumber(), Long.valueOf(0), "number should be correctly set");
      Assert.assertEquals(neptuneObject.getMobilityRestrictedSuitability(), Boolean.TRUE, "MobilityRestrictedSuitability should be correctly set");
      
   }

   @Test(groups = { "Producers" }, description = "test trip with number")
   public void verifyVehicleJourneyProducerNumber() throws Exception
   {
      AbstractModelProducer.setPrefix("NINOXE");
      AbstractModelProducer.setIncrementalPrefix("");
      GtfsTrip gtfsObject = new GtfsTrip("r1", "s1","t1", "1234", "headSign" ,
            DirectionType.Outbound, "b1" ,"shp1", 
            WheelchairAccessibleType.Allowed,BikesAllowedType.Allowed);
      VehicleJourneyProducer producer = new VehicleJourneyProducer();
      producer.setFactory(factory);
      VehicleJourney neptuneObject = producer.produce(gtfsObject, null);
      Assert.assertEquals(neptuneObject.getObjectId(), "NINOXE:VehicleJourney:t1", "objectId should be correctly set");
      Assert.assertEquals(neptuneObject.getNumber(), Long.valueOf(1234), "number should be correctly set");
      Assert.assertNull(neptuneObject.getPublishedJourneyName(), "PublishedJourneyName should be correctly set");
      Assert.assertEquals(neptuneObject.getMobilityRestrictedSuitability(), Boolean.TRUE, "MobilityRestrictedSuitability should be correctly set");
      
   }

   @Test(groups = { "Producers" }, description = "test trip with wheelChair conversion")
   public void verifyVehicleJourneyProducerWheelchair() throws Exception
   {
      AbstractModelProducer.setPrefix("NINOXE");
      AbstractModelProducer.setIncrementalPrefix("");
      GtfsTrip gtfsObject = new GtfsTrip("r1", "s1","t1", "1234", "headSign" ,
            DirectionType.Outbound, "b1" ,"shp1", 
            WheelchairAccessibleType.Allowed,BikesAllowedType.Allowed);
      VehicleJourneyProducer producer = new VehicleJourneyProducer();
      producer.setFactory(factory);
      VehicleJourney neptuneObject = producer.produce(gtfsObject, null);
      Assert.assertEquals(neptuneObject.getMobilityRestrictedSuitability(), Boolean.TRUE, "MobilityRestrictedSuitability should be correctly set");
      gtfsObject.setWheelchairAccessible(WheelchairAccessibleType.NoAllowed);
      neptuneObject = producer.produce(gtfsObject, null);
      Assert.assertEquals(neptuneObject.getMobilityRestrictedSuitability(), Boolean.FALSE, "MobilityRestrictedSuitability should be correctly set");
      gtfsObject.setWheelchairAccessible(WheelchairAccessibleType.NoInformation);
      neptuneObject = producer.produce(gtfsObject, null);
      Assert.assertNull(neptuneObject.getMobilityRestrictedSuitability(), "MobilityRestrictedSuitability should be correctly set");
      gtfsObject.setWheelchairAccessible(null);
      neptuneObject = producer.produce(gtfsObject, null);
      Assert.assertNull(neptuneObject.getMobilityRestrictedSuitability(), "MobilityRestrictedSuitability should be correctly set");

   }

}
