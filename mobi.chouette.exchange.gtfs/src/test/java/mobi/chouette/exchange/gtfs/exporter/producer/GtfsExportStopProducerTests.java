package mobi.chouette.exchange.gtfs.exporter.producer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.core.ChouetteException;
import mobi.chouette.exchange.gtfs.exporter.producer.mock.GtfsExporterMock;
import mobi.chouette.exchange.gtfs.model.GtfsStop;
import mobi.chouette.exchange.gtfs.model.GtfsStop.LocationType;
import mobi.chouette.exchange.gtfs.model.GtfsStop.WheelchairBoardingType;
import mobi.chouette.exchange.gtfs.model.exporter.StopExporter;
import mobi.chouette.exchange.gtfs.model.importer.Context;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.LongLatTypeEnum;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;


public class GtfsExportStopProducerTests 
{
   private GtfsExporterMock mock = new GtfsExporterMock();
   private GtfsStopProducer producer = new GtfsStopProducer(mock);
   private Context context = new Context();

   @Test(groups = { "Producers" }, description = "test stop with full data")
   public void verifyStopProducerStopWithFullData() throws Exception
   {
      mock.reset();

      StopArea neptuneObject = new StopArea();
      neptuneObject.setObjectId("GTFS:StopArea:4321");
      neptuneObject.setName("physical point");
      neptuneObject.setAreaType(ChouetteAreaEnum.BoardingPosition);
      neptuneObject.setRegistrationNumber("1234");
      neptuneObject.setComment("comment");
      neptuneObject.setLatitude(BigDecimal.valueOf(45));
      neptuneObject.setLongitude(BigDecimal.valueOf(3));
      neptuneObject.setLongLatType(LongLatTypeEnum.WGS84);
      neptuneObject.setUrl("http://mystop.com");
      neptuneObject.setMobilityRestrictedSuitable(true);

      StopArea parent = new StopArea();
      parent.setObjectId("GTFS:StopArea:5678");
      List<StopArea> parents = new ArrayList<>();
      parents.add(parent);
      neptuneObject.setParent(parent);

      producer.save(neptuneObject,  "GTFS", parents,false);
      GtfsStop gtfsObject = mock.getExportedStops().get(0);
      Reporter.log("verifyStopProducerStopWithFullData");
      Reporter.log(StopExporter.CONVERTER.to(context, gtfsObject));

      Assert.assertEquals(gtfsObject.getStopId(), "4321", "StopId must be third part of objectid");
      Assert.assertEquals(gtfsObject.getStopCode(), neptuneObject.getRegistrationNumber(), "StopCode must be correctly set");
      Assert.assertEquals(gtfsObject.getStopName(), neptuneObject.getName(), "StopName must be correctly set");
      Assert.assertEquals(gtfsObject.getStopDesc(), neptuneObject.getComment(), "StopDesc must be correctly set");
      Assert.assertEquals(gtfsObject.getLocationType(), LocationType.Stop, "LocationType must be correctly set");
      Assert.assertEquals(gtfsObject.getStopLat(), neptuneObject.getLatitude(), "StopLat must be correctly set");
      Assert.assertEquals(gtfsObject.getStopLon(), neptuneObject.getLongitude(), "StopLon must be correctly set");
      Assert.assertEquals(gtfsObject.getParentStation(), "5678", "ParentStation must be correctly set");
      Assert.assertNotNull(gtfsObject.getStopUrl(), "StopUrl must be set");
      Assert.assertEquals(gtfsObject.getStopUrl().toString(), neptuneObject.getUrl(), "StopUrl must be correctly set");
      Assert.assertEquals(gtfsObject.getWheelchairBoarding(), WheelchairBoardingType.Allowed, "WheelchairBoarding must be correctly set");
      Assert.assertNull(gtfsObject.getAddressLine(), "AddressLine must not be set");
      Assert.assertNull(gtfsObject.getLocality(), "Locality must not be set");
      Assert.assertNull(gtfsObject.getPostalCode(), "PostalCode must not be set");
      Assert.assertNull(gtfsObject.getZoneId(), "ZoneId must not be set");
      Assert.assertNull(gtfsObject.getStopTimezone(), "StopTimezone must not be set");

   }
   @Test(groups = { "Producers" }, description = "test stop with less data")
   public void verifyStopProducerStopWithLessData() throws ChouetteException
   {
      mock.reset();

      StopArea neptuneObject = new StopArea();
      neptuneObject.setObjectId("GTFS:StopArea:4321");
      neptuneObject.setName("physical point");
      neptuneObject.setAreaType(ChouetteAreaEnum.BoardingPosition);
      neptuneObject.setLatitude(BigDecimal.valueOf(45));
      neptuneObject.setLongitude(BigDecimal.valueOf(3));
      neptuneObject.setLongLatType(LongLatTypeEnum.WGS84);
      neptuneObject.setMobilityRestrictedSuitable(null);

      StopArea parent = new StopArea();
      parent.setObjectId("GTFS:StopArea:5678");
      List<StopArea> parents = new ArrayList<>();
      parents.add(parent);

      producer.save(neptuneObject,  "GTFS", parents,false);
      GtfsStop gtfsObject = mock.getExportedStops().get(0);
      Reporter.log("verifyStopProducerStopWithLessData");
      Reporter.log(StopExporter.CONVERTER.to(context, gtfsObject));

      Assert.assertEquals(gtfsObject.getStopId(), "4321", "StopId must be third part of objectid");
      Assert.assertNull(gtfsObject.getStopCode(),  "StopCode must  must not be set");
      Assert.assertEquals(gtfsObject.getStopName(), neptuneObject.getName(), "StopName must be correctly set");
      Assert.assertNull(gtfsObject.getStopDesc(), "StopDesc must  must not be set");
      Assert.assertEquals(gtfsObject.getLocationType(), LocationType.Stop, "LocationType must be correctly set");
      Assert.assertEquals(gtfsObject.getStopLat(), neptuneObject.getLatitude(), "StopLat must be correctly set");
      Assert.assertEquals(gtfsObject.getStopLon(), neptuneObject.getLongitude(), "StopLon must be correctly set");
      Assert.assertNull(gtfsObject.getParentStation(),  "ParentStation must  must not be set");
      Assert.assertNull(gtfsObject.getStopUrl(), "StopUrl must not be set");
      Assert.assertNull(gtfsObject.getWheelchairBoarding(),  "WheelchairBoarding must not be set");
      Assert.assertNull(gtfsObject.getAddressLine(), "AddressLine must not be set");
      Assert.assertNull(gtfsObject.getLocality(), "Locality must not be set");
      Assert.assertNull(gtfsObject.getPostalCode(), "PostalCode must not be set");
      Assert.assertNull(gtfsObject.getZoneId(), "ZoneId must not be set");
      Assert.assertNull(gtfsObject.getStopTimezone(), "StopTimezone must not be set");

   }
   @Test(groups = { "Producers" }, description = "test station with full data")
   public void verifyStopProducerStationWithFullData() throws ChouetteException
   {
      mock.reset();

      StopArea neptuneObject = new StopArea();
      neptuneObject.setObjectId("GTFS:StopArea:4321");
      neptuneObject.setName("Commercial point");
      neptuneObject.setAreaType(ChouetteAreaEnum.CommercialStopPoint);
      neptuneObject.setRegistrationNumber("1234");
      neptuneObject.setComment("comment");
      neptuneObject.setLatitude(BigDecimal.valueOf(45));
      neptuneObject.setLongitude(BigDecimal.valueOf(3));
      neptuneObject.setLongLatType(LongLatTypeEnum.WGS84);
      neptuneObject.setUrl("http://mystop.com");
      neptuneObject.setMobilityRestrictedSuitable(true);
      neptuneObject.setTimeZone("Europe/Paris");

      StopArea parent = new StopArea();
      parent.setObjectId("GTFS:StopArea:5678");
      List<StopArea> parents = new ArrayList<>();
      parents.add(parent);
      neptuneObject.setParent(parent);

      producer.save(neptuneObject, "GTFS", parents,false);
      GtfsStop gtfsObject = mock.getExportedStops().get(0);
      Reporter.log("verifyStopProducerStationWithFullData");
      Reporter.log(StopExporter.CONVERTER.to(context, gtfsObject));

      Assert.assertEquals(gtfsObject.getStopId(), "4321", "StopId must be third part of objectid");
      Assert.assertEquals(gtfsObject.getStopCode(), neptuneObject.getRegistrationNumber(), "StopCode must be correctly set");
      Assert.assertEquals(gtfsObject.getStopName(), neptuneObject.getName(), "StopName must be correctly set");
      Assert.assertEquals(gtfsObject.getStopDesc(), neptuneObject.getComment(), "StopDesc must be correctly set");
      Assert.assertEquals(gtfsObject.getLocationType(), LocationType.Station, "LocationType must be correctly set");
      Assert.assertEquals(gtfsObject.getStopLat(), neptuneObject.getLatitude(), "StopLat must be correctly set");
      Assert.assertEquals(gtfsObject.getStopLon(), neptuneObject.getLongitude(), "StopLon must be correctly set");
      Assert.assertNull(gtfsObject.getParentStation(),  "ParentStation must not be set");
      Assert.assertNotNull(gtfsObject.getStopUrl(), "StopUrl must be set");
      Assert.assertEquals(gtfsObject.getStopUrl().toString(), neptuneObject.getUrl(), "StopUrl must be correctly set");
      Assert.assertEquals(gtfsObject.getWheelchairBoarding(), WheelchairBoardingType.Allowed, "WheelchairBoarding must be correctly set");
      Assert.assertNull(gtfsObject.getAddressLine(), "AddressLine must not be set");
      Assert.assertNull(gtfsObject.getLocality(), "Locality must not be set");
      Assert.assertNull(gtfsObject.getPostalCode(), "PostalCode must not be set");
      Assert.assertNull(gtfsObject.getZoneId(), "ZoneId must not be set");
      Assert.assertEquals(gtfsObject.getStopTimezone().getID(),"Europe/Paris", "StopTimezone must be correctly set");

   }
   
   @Test(groups = { "Producers" }, description = "test stop place")
   public void verifyStopProducerStopPlace() throws ChouetteException
   {
      mock.reset();

      StopArea neptuneObject = new StopArea();
      neptuneObject.setObjectId("GTFS:StopArea:4321");
      neptuneObject.setName("Stop place");
      neptuneObject.setAreaType(ChouetteAreaEnum.StopPlace);
      neptuneObject.setRegistrationNumber("1234");
      neptuneObject.setComment("comment");
      neptuneObject.setLatitude(BigDecimal.valueOf(45));
      neptuneObject.setLongitude(BigDecimal.valueOf(3));
      neptuneObject.setLongLatType(LongLatTypeEnum.WGS84);
      neptuneObject.setUrl("http://mystop.com");
      neptuneObject.setMobilityRestrictedSuitable(true);
      neptuneObject.setTimeZone("Europe/Paris");

      List<StopArea> parents = new ArrayList<>();

      Assert.assertFalse(producer.save(neptuneObject, "GTFS", parents,false));

   }

}
