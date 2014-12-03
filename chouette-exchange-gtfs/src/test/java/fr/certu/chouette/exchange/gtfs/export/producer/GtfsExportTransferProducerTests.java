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
import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsTransferProducer;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsRoute;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTransfer;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTransfer.TransferType;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.StopArea;

@ContextConfiguration(locations = { "classpath:testContext.xml",
      "classpath*:chouetteContext.xml" })
public class GtfsExportTransferProducerTests extends
      AbstractTestNGSpringContextTests
{
   private static final Logger logger = Logger
         .getLogger(GtfsExportTransferProducerTests.class);

   private GtfsExporterMock mock = new GtfsExporterMock();

   @Test(groups = { "Producers" }, description = "test transfer with default duration")
   public void verifyTransferProducer1() throws ChouetteException
   {

      mock.reset();
      GtfsTransferProducer producer = new GtfsTransferProducer(mock);

      GtfsReport report = new GtfsReport(GtfsReport.KEY.EXPORT);
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

      producer.save(neptuneObject, report, "GTFS");
      GtfsTransfer gtfsObject = mock.getExportedTransfers().get(0);
      Reporter.log("verifyTransferProducer1");
      Reporter.log(gtfsObject.toString());

      Assert.assertEquals(gtfsObject.getFromStopId(), "start",
            "Start of link must be correctly set");
      Assert.assertEquals(gtfsObject.getToStopId(), "end",
            "End of link must be correctly set");
      Assert.assertEquals(gtfsObject.getMinTransferTime().toString(),
            "60", "transfer time must be correctly set");
      Assert.assertEquals(gtfsObject.getTransferType(), TransferType.Minimal,
            "transfer type must be MINIMAL");

   }

   @Test(groups = { "Producers" }, description = "test transfer without default duration")
   public void verifyTransferProducer2() throws ChouetteException
   {

      mock.reset();
      GtfsTransferProducer producer = new GtfsTransferProducer(mock);

      GtfsReport report = new GtfsReport(GtfsReport.KEY.EXPORT);
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

      producer.save(neptuneObject, report, "GTFS");
      GtfsTransfer gtfsObject = mock.getExportedTransfers().get(0);
      Reporter.log("verifyTransferProducer2");
      Reporter.log(gtfsObject.toString());

      Assert.assertEquals(gtfsObject.getFromStopId(), "start",
            "Start of link must be correctly set");
      Assert.assertEquals(gtfsObject.getToStopId(), "end",
            "End of link must be correctly set");
      Assert.assertNull(gtfsObject.getMinTransferTime(),
            "transfer time must be null");
      Assert.assertEquals(gtfsObject.getTransferType(), TransferType.Recommended,
            "transfer type must be RECOMMENDED");

   }

}
