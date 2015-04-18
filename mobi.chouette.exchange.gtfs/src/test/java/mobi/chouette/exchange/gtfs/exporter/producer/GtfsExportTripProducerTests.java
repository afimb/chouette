package mobi.chouette.exchange.gtfs.exporter.producer;

import java.sql.Time;

import mobi.chouette.exchange.gtfs.exporter.producer.mock.GtfsExporterMock;
import mobi.chouette.exchange.gtfs.model.GtfsStopTime;
import mobi.chouette.exchange.gtfs.model.GtfsTrip;
import mobi.chouette.exchange.gtfs.model.GtfsTrip.DirectionType;
import mobi.chouette.exchange.gtfs.model.GtfsTrip.WheelchairAccessibleType;
import mobi.chouette.exchange.gtfs.model.exporter.StopTimeExporter;
import mobi.chouette.exchange.gtfs.model.exporter.TripExporter;
import mobi.chouette.exchange.gtfs.model.importer.Context;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;


public class GtfsExportTripProducerTests 
{
   private GtfsExporterMock mock = new GtfsExporterMock();
   private GtfsTripProducer producer = new GtfsTripProducer(mock);
   private Context context = new Context();

   @Test(groups = { "Producers" }, description = "test trip with full data")
   public void verifyTripProducerWithFullData() throws Exception
   {
      
      mock.reset();

      ActionReport report = new ActionReport();
      VehicleJourney neptuneObject = buildNeptuneObject(true);

      producer.save(neptuneObject, "tm_01", report, "GTFS", "GTFS");
      Reporter.log("verifyTripProducerWithFullData");

      Assert.assertEquals(mock.getExportedTrips().size(), 1, "Trip should be returned");
      Assert.assertEquals(mock.getExportedStopTimes().size(), 4, "StopTimes should be returned");
      GtfsTrip gtfsObject = mock.getExportedTrips().get(0);
      Reporter.log(TripExporter.CONVERTER.to(context,gtfsObject));

      Assert.assertEquals(gtfsObject.getTripId(), "4321", "TripId must be correctly set");
      Assert.assertEquals(gtfsObject.getServiceId(), "tm_01", "ServiceId must be correctly set");
      Assert.assertEquals(gtfsObject.getRouteId(), "0123", "RouteId must be correctly set");
      Assert.assertEquals(gtfsObject.getTripShortName(), "456", "TripShortName must be correctly set");
      Assert.assertEquals(gtfsObject.getDirectionId(), DirectionType.Outbound, "DirectionId must be correctly set");
      Assert.assertEquals(gtfsObject.getTripHeadSign(), neptuneObject.getJourneyPattern().getPublishedName(), "TripHeadSign must be correctly set");
      Assert.assertNull(gtfsObject.getBlockId(), "BlockId must not be set");
      Assert.assertNull(gtfsObject.getShapeId(), "ShapeId must not be set");
      Assert.assertEquals(gtfsObject.getWheelchairAccessible(), WheelchairAccessibleType.Allowed, "WheelchairAccessible must be correctly set");
      Assert.assertNull(gtfsObject.getBikesAllowed(), "BikesAllowed must not be set");
      
      int i = 0;
      for (GtfsStopTime gtfsStopTime : mock.getExportedStopTimes())
      {
         Reporter.log(StopTimeExporter.CONVERTER.to(context,gtfsStopTime));
         Assert.assertEquals(gtfsStopTime.getTripId(), "4321", "TripId must be correctly set");
         Assert.assertEquals(gtfsStopTime.getStopId(), "SA"+i, "StopId must be correctly set");
         Assert.assertEquals(gtfsStopTime.getStopSequence(), Integer.valueOf(i*2), "StopSequence must be correctly set");
         if (i < 2) 
         {
            Assert.assertEquals(gtfsStopTime.getArrivalTime().getDay(), Integer.valueOf(0), "ArrivalTime must be today");
            Assert.assertEquals(gtfsStopTime.getDepartureTime().getDay(), Integer.valueOf(0), "DepartureTime must be today");
         }
         else if (i == 2)
         {
            Assert.assertEquals(gtfsStopTime.getArrivalTime().getDay(), Integer.valueOf(0), "ArrivalTime must be today");
            Assert.assertEquals(gtfsStopTime.getDepartureTime().getDay(), Integer.valueOf(1), "DepartureTime must be tomorrow");           
         }
         else
         {
            Assert.assertEquals(gtfsStopTime.getArrivalTime().getDay(), Integer.valueOf(1), "ArrivalTime must be tomorrow");
            Assert.assertEquals(gtfsStopTime.getDepartureTime().getDay(), Integer.valueOf(1), "DepartureTime must be tomorrow");           
         }
            
         i++;
      }

   }

   @Test(groups = { "Producers" }, description = "test trip with less data")
   public void verifyTripProducerWithLessData() throws Exception
   {
      
      mock.reset();

      ActionReport report = new ActionReport();
      VehicleJourney neptuneObject = buildNeptuneObject(false);

      producer.save(neptuneObject, "tm_01", report, "GTFS", "GTFS");
      Reporter.log("verifyTripProducerWithLessData");

      Assert.assertEquals(mock.getExportedTrips().size(), 1, "Trip should be returned");
      Assert.assertEquals(mock.getExportedStopTimes().size(), 4, "StopTimes should be returned");
      GtfsTrip gtfsObject = mock.getExportedTrips().get(0);
      Reporter.log(TripExporter.CONVERTER.to(context,gtfsObject));

      Assert.assertEquals(gtfsObject.getTripId(), "4321", "TripId must be correctly set");
      Assert.assertEquals(gtfsObject.getServiceId(), "tm_01", "ServiceId must be correctly set");
      Assert.assertEquals(gtfsObject.getRouteId(), "0123", "RouteId must be correctly set");
      Assert.assertNull(gtfsObject.getTripShortName(),  "TripShortName must not be set");
      Assert.assertEquals(gtfsObject.getDirectionId(), DirectionType.Outbound, "DirectionId must be correctly set");
      Assert.assertNull(gtfsObject.getTripHeadSign(),  "TripHeadSign must not be set");
      Assert.assertNull(gtfsObject.getBlockId(), "BlockId must not be set");
      Assert.assertNull(gtfsObject.getShapeId(), "ShapeId must not be set");
      Assert.assertEquals(gtfsObject.getWheelchairAccessible(), WheelchairAccessibleType.NoInformation, "WheelchairAccessible must be correctly set");
      Assert.assertNull(gtfsObject.getBikesAllowed(), "BikesAllowed must not be set");
      
      int i = 0;
      for (GtfsStopTime gtfsStopTime : mock.getExportedStopTimes())
      {
         Reporter.log(StopTimeExporter.CONVERTER.to(context,gtfsStopTime));
         Assert.assertEquals(gtfsStopTime.getTripId(), "4321", "TripId must be correctly set");
         Assert.assertEquals(gtfsStopTime.getStopId(), "SA"+i, "StopId must be correctly set");
         Assert.assertEquals(gtfsStopTime.getStopSequence(), Integer.valueOf(i*2), "StopSequence must be correctly set");
         if (i < 2) 
         {
            Assert.assertEquals(gtfsStopTime.getArrivalTime().getDay(), Integer.valueOf(0), "ArrivalTime must be today");
            Assert.assertEquals(gtfsStopTime.getDepartureTime().getDay(), Integer.valueOf(0), "DepartureTime must be today");
         }
         else if (i == 2)
         {
            Assert.assertEquals(gtfsStopTime.getArrivalTime().getDay(), Integer.valueOf(0), "ArrivalTime must be today");
            Assert.assertEquals(gtfsStopTime.getDepartureTime().getDay(), Integer.valueOf(1), "DepartureTime must be tomorrow");           
         }
         else
         {
            Assert.assertEquals(gtfsStopTime.getArrivalTime().getDay(), Integer.valueOf(1), "ArrivalTime must be tomorrow");
            Assert.assertEquals(gtfsStopTime.getDepartureTime().getDay(), Integer.valueOf(1), "DepartureTime must be tomorrow");           
         }
            
         i++;
      }

   }

   @Test(groups = { "Producers" }, description = "test trip wheelChair mapping")
   public void verifyTripProducerForWheelChairMapping() throws Exception
   {
      mock.reset();
      Reporter.log("verifyTripProducerForWheelChairMapping");

      ActionReport report = new ActionReport();
      VehicleJourney neptuneObject = buildNeptuneObject(true);
      neptuneObject.setMobilityRestrictedSuitability(Boolean.TRUE);

      producer.save(neptuneObject, "tm_01", report, "GTFS", "GTFS");

      Assert.assertEquals(mock.getExportedTrips().size(), 1, "Trip should be returned");
      GtfsTrip gtfsObject = mock.getExportedTrips().get(0);
      Reporter.log(TripExporter.CONVERTER.to(context,gtfsObject));
      Assert.assertEquals(gtfsObject.getWheelchairAccessible(), WheelchairAccessibleType.Allowed, "WheelchairAccessible must be correctly set");

      mock.reset();
      neptuneObject.setMobilityRestrictedSuitability(Boolean.FALSE);
      producer.save(neptuneObject, "tm_01", report, "GTFS", "GTFS");
      Assert.assertEquals(mock.getExportedTrips().size(), 1, "Trip should be returned");
      gtfsObject = mock.getExportedTrips().get(0);
      Reporter.log(TripExporter.CONVERTER.to(context,gtfsObject));
      Assert.assertEquals(gtfsObject.getWheelchairAccessible(), WheelchairAccessibleType.NoAllowed, "WheelchairAccessible must be correctly set");

      mock.reset();
      neptuneObject.setMobilityRestrictedSuitability(null);
      producer.save(neptuneObject, "tm_01", report, "GTFS", "GTFS");
      Assert.assertEquals(mock.getExportedTrips().size(), 1, "Trip should be returned");
      gtfsObject = mock.getExportedTrips().get(0);
      Reporter.log(TripExporter.CONVERTER.to(context,gtfsObject));
      Assert.assertEquals(gtfsObject.getWheelchairAccessible(), WheelchairAccessibleType.NoInformation, "WheelchairAccessible must be correctly set");
   }

  
   @Test(groups = { "Producers" }, description = "test trip Direction mapping")
   public void verifyTripProducerForDirectionMapping() throws Exception
   {
      mock.reset();
      Reporter.log("verifyTripProducerForDirectionMapping");

      ActionReport report = new ActionReport();
      VehicleJourney neptuneObject = buildNeptuneObject(true);
      Route r = neptuneObject.getRoute();
      r.setWayBack("A");
      producer.save(neptuneObject, "tm_01", report, "GTFS", "GTFS");

      Assert.assertEquals(mock.getExportedTrips().size(), 1, "Trip should be returned");
      GtfsTrip gtfsObject = mock.getExportedTrips().get(0);
      Assert.assertEquals(gtfsObject.getDirectionId(), DirectionType.Outbound, "DirectionId must be correctly set");

      mock.reset();
      r.setWayBack("R");
      producer.save(neptuneObject, "tm_01", report, "GTFS", "GTFS");
      Assert.assertEquals(mock.getExportedTrips().size(), 1, "Trip should be returned");
      gtfsObject = mock.getExportedTrips().get(0);
      Assert.assertEquals(gtfsObject.getDirectionId(), DirectionType.Inbound, "DirectionId must be correctly set");

      mock.reset();
      r.setWayBack(null);
      producer.save(neptuneObject, "tm_01", report, "GTFS", "GTFS");
      Assert.assertEquals(mock.getExportedTrips().size(), 1, "Trip should be returned");
      gtfsObject = mock.getExportedTrips().get(0);
      Assert.assertEquals(gtfsObject.getDirectionId(), DirectionType.Outbound, "DirectionId must be correctly set");
   }

   /**
    * @return
    */
   private VehicleJourney buildNeptuneObject(boolean full)
   {
      VehicleJourney neptuneObject = new VehicleJourney();
      neptuneObject.setObjectId("GTFS:VehicleJourney:4321");
      // if (full) neptuneObject.setName("name");
      if (full) neptuneObject.setNumber(Long.valueOf(456));
      if (full) neptuneObject.setMobilityRestrictedSuitability(Boolean.TRUE);
      JourneyPattern jp = new JourneyPattern();
      neptuneObject.setJourneyPattern(jp);
      Route route = new Route();
      neptuneObject.setRoute(route);
      if (full) jp.setPublishedName("jp name");
      Line line = new Line();
      line.setObjectId("GTFS:Line:0123");

      route.setLine(line);
      if (full) route.setWayBack("A");
      
      int h = 22;
      int m = 59;
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
         vjas.setArrivalTime(new Time(h,m,0));
         m = m + 2;
         if (m > 60)
         {
            m -= 60;
            h = (h+1) % 24;
         }
         vjas.setDepartureTime(new Time(h,m,0));
         m = m + 28;
         if (m > 60)
         {
            m -= 60;
            h = (h+1) % 24;
         }
         vjas.setVehicleJourney(neptuneObject);
      }
      return neptuneObject;
   }

}