package fr.certu.chouette.exchange.gtfs.importer.producer;

import java.sql.Time;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime.DropOffType;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime.PickupType;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTime;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import fr.certu.chouette.plugin.exchange.tools.DbVehicleJourneyFactory;

@ContextConfiguration(locations = { "classpath:testContext.xml", "classpath*:chouetteContext.xml" })
public class GtfsVehicleJourneyAtStopProducerTests extends AbstractTestNGSpringContextTests
{
   DbVehicleJourneyFactory factory = new DbVehicleJourneyFactory("test", false);

   @Test(groups = { "Producers" }, description = "test stopTime with full data")
   public void verifyVehicleJourneyAtStopProducerFull() throws Exception
   {
      AbstractModelProducer.setPrefix("NINOXE");
      AbstractModelProducer.setIncrementalPrefix("");
      GtfsStopTime gtfsObject = new GtfsStopTime("t1", new GtfsTime(new Time(11, 30, 00), Integer.valueOf(0)), new GtfsTime(new Time(11, 31, 00),
            Integer.valueOf(0)), "Stop1", Integer.valueOf(5), "headSign", PickupType.Scheduled, DropOffType.Scheduled, Float.valueOf((float) 25.45));
      gtfsObject.setId(Integer.valueOf(123));
      VehicleJourneyAtStopProducer producer = new VehicleJourneyAtStopProducer();
      producer.setFactory(factory);
      VehicleJourneyAtStop neptuneObject = producer.produce(gtfsObject, null);
      Assert.assertEquals(neptuneObject.getId(), Long.valueOf(123), "id should be correctly set");
      Assert.assertEquals(neptuneObject.getStopPointId(), "Stop1", "StopPointId should be correctly set");
      Assert.assertEquals(neptuneObject.getOrder(), 5, "order should be correctly set");
      Assert.assertEquals(neptuneObject.getArrivalTime(), new Time(11, 30, 00), "Arrival time should be correctly set");
      Assert.assertEquals(neptuneObject.getDepartureTime(), new Time(11, 31, 00), "Departure time should be correctly set");

   }

}
