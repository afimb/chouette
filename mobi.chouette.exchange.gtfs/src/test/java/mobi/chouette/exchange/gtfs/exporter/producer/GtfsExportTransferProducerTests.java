package mobi.chouette.exchange.gtfs.exporter.producer;

import java.sql.Time;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

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
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;

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
      Time defaultDuration = new Time(60000);
      neptuneObject.setDefaultDuration(defaultDuration);

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
      Time defaultDuration = new Time(500);
      neptuneObject.setDefaultDuration(defaultDuration);

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
   public void verifyTransferProducerWithInterchange() throws ChouetteException
   {

      mock.reset();
      GtfsTransferProducer producer = new GtfsTransferProducer(mock);

      StopArea feederSA = new StopArea();
      feederSA.setObjectId("GTFS:StopArea:start");
      StopPoint feederSP = new StopPoint();
      feederSP.setContainedInStopArea(feederSA);
      
      StopArea consumerSA = new StopArea();
      consumerSA.setObjectId("GTFS:StopArea:end");
      StopPoint consumerSP = new StopPoint();
      consumerSP.setContainedInStopArea(consumerSA);
      
      
      JourneyPattern feederJP = createLineStructure("GTFS:Line:feederLine");
      
      VehicleJourney feederVJ = new VehicleJourney();
      feederVJ.setObjectId("GTFS:VehicleJourney:feederJourney");
      feederVJ.setJourneyPattern(feederJP);
      
      
      JourneyPattern consumerJP = createLineStructure("GTFS:Line:consumerLine");
      VehicleJourney consumerVJ = new VehicleJourney();
      consumerVJ.setObjectId("GTFS:VehicleJourney:consumerJourney");
      consumerVJ.setJourneyPattern(consumerJP);

      
      Interchange interchange = new Interchange();
      interchange.setFeederStopPoint(feederSP);
      interchange.setFeederVehicleJourney(feederVJ);
      interchange.setConsumerStopPoint(consumerSP);
      interchange.setConsumerVehicleJourney(consumerVJ);
      interchange.setGuaranteed(Boolean.TRUE);
      
      producer.save(interchange, "GTFS",false);
      GtfsTransfer gtfsObject = mock.getExportedTransfers().get(0);
      
      Reporter.log(TransferExporter.CONVERTER.to(context,gtfsObject));

      Assert.assertEquals(gtfsObject.getFromStopId(), "start");
      Assert.assertEquals(gtfsObject.getToStopId(), "end");
      Assert.assertEquals(gtfsObject.getFromRouteId(), "feederLine");
      Assert.assertEquals(gtfsObject.getToRouteId(), "consumerLine");
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
