package fr.certu.chouette.exchange.gtfs.export.producer;

import java.sql.Time;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsTransferProducer;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.model.GtfsTransfer;
import fr.certu.chouette.exchange.gtfs.model.GtfsTransfer.Type;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.plugin.report.ReportItem;

@ContextConfiguration(locations={"classpath:testContext.xml","classpath*:chouetteContext.xml"})
public class GtfsExportTransferProducerTests extends AbstractTestNGSpringContextTests
{
	private static final Logger logger = Logger.getLogger(GtfsExportTransferProducerTests.class);

	@Test (groups = {"Producers"}, description = "test transfer with default duration" )
	public void verifyTransferProducer1() throws ChouetteException 
	{
		

		GtfsTransferProducer producer = (GtfsTransferProducer) applicationContext.getBean("GtfsTransferExportProducer");

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
		neptuneObject.setDefaultDuration(defaultDuration );
		
		GtfsTransfer gtfsObject = producer.produce(neptuneObject , report );
		System.out.println("verifyTransferProducer1");
		System.out.println(gtfsObject);
		

		Assert.assertEquals(gtfsObject.getFromStopId(), "start","Start of link must be correctly set");
		Assert.assertEquals(gtfsObject.getToStopId(), "end","End of link must be correctly set");
		Assert.assertEquals(gtfsObject.getMinTransferTime().toString(), "00:01:00","transfer time must be correctly set");
		Assert.assertEquals(gtfsObject.getTransferType(), Type.MINIMAL,"transfer type must be MINIMAL");

	}

	@Test (groups = {"Producers"}, description = "test transfer without default duration" )
	public void verifyTransferProducer2() throws ChouetteException 
	{
		

		GtfsTransferProducer producer = (GtfsTransferProducer) applicationContext.getBean("GtfsTransferExportProducer");

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
		neptuneObject.setDefaultDuration(defaultDuration );
		
		GtfsTransfer gtfsObject = producer.produce(neptuneObject , report );
		System.out.println("verifyTransferProducer2");
		System.out.println(gtfsObject);
		

		Assert.assertEquals(gtfsObject.getFromStopId(), "start","Start of link must be correctly set");
		Assert.assertEquals(gtfsObject.getToStopId(), "end","End of link must be correctly set");
		Assert.assertNull(gtfsObject.getMinTransferTime(),"transfer time must be null");
		Assert.assertEquals(gtfsObject.getTransferType(), Type.RECOMMENDED,"transfer type must be RECOMMENDED");

	}


	private void printItems(String indent,List<ReportItem> items) 
	{
		if (items == null) return;
		for (ReportItem item : items) 
		{
			System.out.println(indent+item.getStatus().name()+" : "+item.getLocalizedMessage());
			printItems(indent+"   ",item.getItems());
		}

	}

}
