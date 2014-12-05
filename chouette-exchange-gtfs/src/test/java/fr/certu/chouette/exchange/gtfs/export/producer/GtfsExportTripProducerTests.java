package fr.certu.chouette.exchange.gtfs.export.producer;

import java.sql.Time;

import org.apache.log4j.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.gtfs.export.producer.mock.GtfsExporterMock;
import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsTripProducer;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTrip;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTrip.WheelchairAccessibleType;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;

@ContextConfiguration(locations = { "classpath:testContext.xml", "classpath*:chouetteContext.xml" })
public class GtfsExportTripProducerTests extends AbstractTestNGSpringContextTests
{
   private static final Logger logger = Logger.getLogger(GtfsExportTripProducerTests.class);

   private GtfsExporterMock mock = new GtfsExporterMock();
   private GtfsTripProducer producer = new GtfsTripProducer(mock);

   @Test(groups = { "Producers" }, description = "test trip with full data")
   public void verifyTripProducerWithFullData() throws ChouetteException
   {
      mock.reset();

      GtfsReport report = new GtfsReport(GtfsReport.KEY.EXPORT);
      VehicleJourney neptuneObject = new VehicleJourney();
      neptuneObject.setObjectId("GTFS:VehicleJourney:4321");
      neptuneObject.setName("name");
      neptuneObject.setNumber(Long.valueOf(456));
      neptuneObject.setMobilityRestrictedSuitability(Boolean.TRUE);
      JourneyPattern jp = new JourneyPattern();
      neptuneObject.setJourneyPattern(jp);
      Route route = new Route();
      neptuneObject.setRoute(route);
      jp.setPublishedName("jp name");
      Line line = new Line();
      line.setObjectId("GTFS:Line:0123");

      route.setLine(line);
      route.setWayBack("A");
      
      long arrivalTime = 3600 * 12 * 1000;
      long departureTime = arrivalTime + 60000;
      for (int i = 0; i < 4; i++)
      {
         StopPoint sp = new StopPoint();
         sp.setPosition(i*2);
         StopArea sa = new StopArea();
         sp.setObjectId("GTFS:StopPoint:SP"+i);
         sa.setObjectId("GTFS:StopPoint:SA"+i);
         sp.setContainedInStopArea(sa);
         VehicleJourneyAtStop vjas = new VehicleJourneyAtStop();
         vjas.setStopPoint(sp);
         vjas.setArrivalTime(new Time(arrivalTime));
         vjas.setDepartureTime(new Time(departureTime));
         arrivalTime += 120000 ;
         departureTime += 120000;
         neptuneObject.addVehicleJourneyAtStop(vjas);
      }

      producer.save(neptuneObject, "tm_01", report, "GTFS", "GTFS");

      Assert.assertEquals(mock.getExportedTrips().size(), 1, "Trip should be returned");
      Assert.assertEquals(mock.getExportedStopTimes().size(), 4, "StopTimes should be returned");
      GtfsTrip gtfsObject = mock.getExportedTrips().get(0);
      Reporter.log("verifyTripProducerWithFullData");

      Assert.assertEquals(gtfsObject.getTripId(), "4321", "TripId must be correctly set");
      Assert.assertEquals(gtfsObject.getServiceId(), "tm_01", "ServiceId must be correctly set");
      Assert.assertEquals(gtfsObject.getRouteId(), "0123", "RouteId must be correctly set");
      Assert.assertEquals(gtfsObject.getTripShortName(), "456", "TripShortName must be correctly set");
      Assert.assertEquals(gtfsObject.getTripHeadSign(), jp.getPublishedName(), "TripHeadSign must be correctly set");
      Assert.assertNull(gtfsObject.getBlockId(), "BlockId must not be set");
      Assert.assertNull(gtfsObject.getShapeId(), "ShapeId must not be set");
      Assert.assertEquals(gtfsObject.getWheelchairAccessible(), WheelchairAccessibleType.Allowed, "WheelchairAccessible must be correctly set");
      Assert.assertNull(gtfsObject.getBikesAllowed(), "BikesAllowed must not be set");
      
      int i = 0;
      for (GtfsStopTime gtfsStopTime : mock.getExportedStopTimes())
      {
         Assert.assertEquals(gtfsStopTime.getTripId(), "4321", "TripId must be correctly set");
         Assert.assertEquals(gtfsStopTime.getStopId(), "SA"+i, "StopId must be correctly set");
         Assert.assertEquals(gtfsStopTime.getStopSequence(), Integer.valueOf(i*2), "StopSequence must be correctly set");
         i++;
      }

   }

}