package mobi.chouette.exchange.gtfs.exporter.producer;

import mobi.chouette.core.ChouetteException;
import mobi.chouette.exchange.gtfs.exporter.producer.mock.GtfsExporterMock;
import mobi.chouette.exchange.gtfs.model.GtfsTransfer;
import mobi.chouette.exchange.gtfs.model.GtfsTransfer.TransferType;
import mobi.chouette.exchange.gtfs.model.exporter.TransferExporter;
import mobi.chouette.exchange.gtfs.model.importer.Context;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.Interchange;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.ScheduledStopPoint;
import mobi.chouette.model.SimpleObjectReference;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.util.ObjectIdTypes;

import org.apache.commons.lang.reflect.FieldUtils;
import org.joda.time.Duration;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

public class GtfsExportTransferProducerTests 
{

   private GtfsExporterMock mock = new GtfsExporterMock();
   GtfsTransferProducer producer = new GtfsTransferProducer(mock);
   private Context context = new Context();

   @Test(groups = { "Producers" }, description = "test transfer with default duration")
   public void verifyTransferProducer1() throws ChouetteException
   {

      mock.reset();

      ConnectionLink neptuneObject = new ConnectionLink();
      neptuneObject.setObjectId("GTFS:ConnectionLink:1234");
      StopArea startOfLink = new StopArea();
      startOfLink.setObjectId("GTFS:StopArea:start");
      neptuneObject.setStartOfLink(startOfLink);
      StopArea endOfLink = new StopArea();
      endOfLink.setObjectId("GTFS:StopArea:end");
      neptuneObject.setEndOfLink(endOfLink);
      neptuneObject.setDefaultDuration(Duration.millis(60000));

      producer.save(neptuneObject,  "GTFS",false);
      GtfsTransfer gtfsObject = mock.getExportedTransfers().get(0);
      Reporter.log("verifyTransferProducer1");
      Reporter.log(TransferExporter.CONVERTER.to(context,gtfsObject));

      Assert.assertEquals(gtfsObject.getFromStopId(), "start", "Start of link must be correctly set");
      Assert.assertEquals(gtfsObject.getToStopId(), "end", "End of link must be correctly set");
      Assert.assertEquals(gtfsObject.getMinTransferTime().toString(), "60", "transfer time must be correctly set");
      Assert.assertEquals(gtfsObject.getTransferType(), TransferType.Minimal, "transfer type must be MINIMAL");

   }

   @Test(groups = { "Producers" }, description = "test transfer without default duration")
   public void verifyTransferProducer2() throws ChouetteException
   {

      mock.reset();
      GtfsTransferProducer producer = new GtfsTransferProducer(mock);

      ConnectionLink neptuneObject = new ConnectionLink();
      neptuneObject.setObjectId("GTFS:ConnectionLink:1234");
      StopArea startOfLink = new StopArea();
      startOfLink.setObjectId("GTFS:StopArea:start");
      neptuneObject.setStartOfLink(startOfLink);
      StopArea endOfLink = new StopArea();
      endOfLink.setObjectId("GTFS:StopArea:end");
      neptuneObject.setEndOfLink(endOfLink);
      neptuneObject.setDefaultDuration(Duration.millis(500));

      producer.save(neptuneObject, "GTFS",false);
      GtfsTransfer gtfsObject = mock.getExportedTransfers().get(0);
      Reporter.log("verifyTransferProducer2");
      Reporter.log(TransferExporter.CONVERTER.to(context,gtfsObject));

      Assert.assertEquals(gtfsObject.getFromStopId(), "start", "Start of link must be correctly set");
      Assert.assertEquals(gtfsObject.getToStopId(), "end", "End of link must be correctly set");
      Assert.assertNull(gtfsObject.getMinTransferTime(), "transfer time must be null");
      Assert.assertEquals(gtfsObject.getTransferType(), TransferType.Recommended, "transfer type must be RECOMMENDED");

   }

   @Test(groups = { "Producers" }, description = "test transfer without default duration")
   public void verifyTransferProducerWithInterchange() throws ChouetteException, IllegalAccessException
   {

      mock.reset();
      GtfsTransferProducer producer = new GtfsTransferProducer(mock);

      StopArea feederSA = new StopArea();
      feederSA.setObjectId("GTFS:StopArea:start");
      StopPoint feederSP = new StopPoint();
      ScheduledStopPoint feederSSP = new ScheduledStopPoint();
      feederSSP.setObjectId("GTFS:" + ObjectIdTypes.SCHEDULED_STOP_POINT_KEY + ":start");
      feederSSP.setContainedInStopAreaRef(new SimpleObjectReference<>(feederSA));
      feederSP.setScheduledStopPoint(feederSSP);

      StopArea consumerSA = new StopArea();
      consumerSA.setObjectId("GTFS:StopArea:end");
      StopPoint consumerSP = new StopPoint();
      ScheduledStopPoint consumerSSP = new ScheduledStopPoint();
      consumerSSP.setObjectId("GTFS:" + ObjectIdTypes.SCHEDULED_STOP_POINT_KEY + ":end");
      consumerSSP.setContainedInStopAreaRef(new SimpleObjectReference<>(consumerSA));
      consumerSP.setScheduledStopPoint(consumerSSP);


      JourneyPattern feederJP = createLineStructure("GTFS:Line:feederLine");

      VehicleJourney feederVJ = new VehicleJourney();
      feederVJ.setObjectId("GTFS:VehicleJourney:feederJourney");
      feederVJ.setJourneyPattern(feederJP);


      JourneyPattern consumerJP = createLineStructure("GTFS:Line:consumerLine");
      VehicleJourney consumerVJ = new VehicleJourney();
      consumerVJ.setObjectId("GTFS:VehicleJourney:consumerJourney");
      consumerVJ.setJourneyPattern(consumerJP);


      Interchange interchange = new Interchange();

      // Set via reflection due to StopPoint and VehicleJourney is unset after set, but available after reload from database (cannot be transient)
      FieldUtils.writeField(interchange, "feederStopPoint",feederSSP, true);
      FieldUtils.writeField(interchange, "feederVehicleJourney",feederVJ, true);
      FieldUtils.writeField(interchange, "consumerStopPoint",consumerSSP, true);
      FieldUtils.writeField(interchange, "consumerVehicleJourney",consumerVJ, true);

      interchange.setFeederVehicleJourneyObjectid(feederVJ.getObjectId());
      interchange.setConsumerVehicleJourneyObjectid(consumerVJ.getObjectId());

      interchange.setGuaranteed(Boolean.TRUE);

      producer.save(interchange, "GTFS",false);
      GtfsTransfer gtfsObject = mock.getExportedTransfers().get(0);

      Reporter.log(TransferExporter.CONVERTER.to(context,gtfsObject));

      Assert.assertEquals(gtfsObject.getFromStopId(), "start");
      Assert.assertEquals(gtfsObject.getToStopId(), "end");
 //     Assert.assertEquals(gtfsObject.getFromRouteId(), "feederLine");
 //     Assert.assertEquals(gtfsObject.getToRouteId(), "consumerLine");
      Assert.assertEquals(gtfsObject.getFromTripId(), "feederJourney");
      Assert.assertEquals(gtfsObject.getToTripId(), "consumerJourney");
      Assert.assertNull(gtfsObject.getMinTransferTime(), "transfer time must be null");
      Assert.assertEquals(gtfsObject.getTransferType(), TransferType.Timed, "transfer type");

   }

protected JourneyPattern createLineStructure(String lineId) {
	Line feederLine = new Line();
      feederLine.setObjectId(lineId);

      Route feederRoute = new Route();
      feederRoute.setLine(feederLine);

      JourneyPattern feederJP = new JourneyPattern();
      feederJP.setRoute(feederRoute);
	return feederJP;
}



}
