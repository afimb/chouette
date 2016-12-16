package mobi.chouette.exchange.gtfs.exporter.producer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.core.ChouetteException;
import mobi.chouette.exchange.gtfs.exporter.producer.mock.GtfsExporterMock;
import mobi.chouette.exchange.gtfs.model.GtfsStop;
import mobi.chouette.exchange.gtfs.model.GtfsStop.LocationType;
import mobi.chouette.exchange.gtfs.model.GtfsStop.WheelchairBoardingType;
import mobi.chouette.exchange.gtfs.model.exporter.StopExtendedExporter;
import mobi.chouette.exchange.gtfs.model.importer.Context;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.LongLatTypeEnum;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

public class GtfsExportExtendedStopProducerTests
{

   private GtfsExporterMock mock = new GtfsExporterMock();
   private GtfsExtendedStopProducer producer = new GtfsExtendedStopProducer(mock);
   private Context context = new Context();


   /**
    * @throws ChouetteException
    */
   @Test(groups = { "Producers" }, description = "test stops with full data and extensions")
   public void verifyExtendedStopProducerWithFullData() throws Exception
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
      neptuneObject.setStreetName("Rue du Louvre");
      neptuneObject.setZipCode("75001");
      neptuneObject.setCityName("Paris");
      
      StopArea parent = new StopArea();
      parent.setObjectId("GTFS:StopArea:5678");
      List<StopArea> parents = new ArrayList<>();
      parents.add(parent);
      neptuneObject.setParent(parent);

      producer.save(neptuneObject,  "GTFS", parents,false);
      GtfsStop gtfsObject = mock.getExportedStops().get(0);
      Reporter.log("verifyExtendedStopProducerWithFullData");
      Reporter.log(StopExtendedExporter.CONVERTER.to(context, gtfsObject));

      Assert.assertEquals(gtfsObject.getStopId(), "4321", "StopId must be third part of objectid");
      Assert.assertEquals(gtfsObject.getStopCode(), neptuneObject.getRegistrationNumber(), "StopCode must be correctly set");
      Assert.assertEquals(gtfsObject.getStopName(), neptuneObject.getName(), "StopName must be correctly set");
      Assert.assertEquals(gtfsObject.getStopDesc(), neptuneObject.getComment(), "StopDesc must be correctly set");
      Assert.assertEquals(gtfsObject.getLocationType(), LocationType.Stop, "LocationType must be correctly set");
      Assert.assertEquals(gtfsObject.getStopLat(), neptuneObject.getLatitude(), "StopLat must be correctly set");
      Assert.assertEquals(gtfsObject.getStopLon(), neptuneObject.getLongitude(), "StopLon must be correctly set");
      Assert.assertEquals(gtfsObject.getParentStation(), "5678", "ParentStation must be correctly set");
      Assert.assertNotNull(gtfsObject.getStopUrl(),  "StopUrl must be set");
      Assert.assertEquals(gtfsObject.getStopUrl().toString(), neptuneObject.getUrl(), "StopUrl must be correctly set");
      Assert.assertEquals(gtfsObject.getWheelchairBoarding(), WheelchairBoardingType.Allowed, "WheelchairBoarding must be correctly set");
      Assert.assertEquals(gtfsObject.getAddressLine(),neptuneObject.getStreetName(),  "AddressLine must be correctly set");
      Assert.assertEquals(gtfsObject.getLocality(),neptuneObject.getCityName(),  "Locality must be correctly set");
      Assert.assertEquals(gtfsObject.getPostalCode(),neptuneObject.getZipCode(),  "PostalCode must be correctly set");
      Assert.assertNull(gtfsObject.getZoneId(),  "ZoneId must not be set");
      Assert.assertNull(gtfsObject.getStopTimezone(),  "StopTimezone must not be set");

   }

}
