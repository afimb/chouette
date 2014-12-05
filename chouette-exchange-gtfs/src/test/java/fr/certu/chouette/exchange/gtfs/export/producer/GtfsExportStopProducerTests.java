package fr.certu.chouette.exchange.gtfs.export.producer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.gtfs.export.producer.mock.GtfsExporterMock;
import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsStopProducer;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.refactor.exporter.StopExporter;
import fr.certu.chouette.exchange.gtfs.refactor.importer.Context;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStop;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStop.LocationType;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStop.WheelchairBoardingType;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;

@ContextConfiguration(locations = { "classpath:testContext.xml", "classpath*:chouetteContext.xml" })
public class GtfsExportStopProducerTests extends AbstractTestNGSpringContextTests
{
   private static final Logger logger = Logger.getLogger(GtfsExportStopProducerTests.class);
   private GtfsExporterMock mock = new GtfsExporterMock();
   private GtfsStopProducer producer = new GtfsStopProducer(mock);
   private Context context = new Context();

   @Test(groups = { "Producers" }, description = "test stops with full data")
   public void verifyStopProducerWithFullData() throws ChouetteException
   {
      mock.reset();

      GtfsReport report = new GtfsReport(GtfsReport.KEY.EXPORT);
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

      producer.save(neptuneObject, report, "GTFS", parents);
      GtfsStop gtfsObject = mock.getExportedStops().get(0);
      Reporter.log("verifyStopProducerWithFullData");
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

}
