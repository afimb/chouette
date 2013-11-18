package fr.certu.chouette.exchange.gtfs.export.producer;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsAgencyProducer;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.model.GtfsAgency;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.plugin.report.ReportItem;

@ContextConfiguration(locations={"classpath:testContext.xml","classpath*:chouetteContext.xml"})
public class GtfsExportAgencyProducerTests extends AbstractTestNGSpringContextTests
{
	private static final Logger logger = Logger.getLogger(GtfsExportAgencyProducerTests.class);

	@Test (groups = {"Producers"}, description = "test full company data" )
	public void verifyAgencyProducer1() throws ChouetteException 
	{

		GtfsAgencyProducer producer = (GtfsAgencyProducer) applicationContext.getBean("GtfsAgencyExportProducer");

		GtfsReport report = new GtfsReport(GtfsReport.KEY.EXPORT);
		Company neptuneObject = new Company();
		neptuneObject.setObjectId("GTFS:Company:1234");
		neptuneObject.setName("name");
		neptuneObject.setShortName("short");
		neptuneObject.setRegistrationNumber("1234");
		neptuneObject.setOrganisationalUnit("http://www.mywebsite.com");
		neptuneObject.setPhone("01 02 03 04 05");

		GtfsAgency gtfsObject = producer.produce(neptuneObject , report );
		System.out.println("verifyAgencyProducer1");
		System.out.println(GtfsAgency.header);
		System.out.println(gtfsObject.getCSVLine()+"\n");

		Assert.assertEquals(gtfsObject.getAgencyId(), toGtfsId(neptuneObject.getObjectId()),"agency id must be correcty set");
		Assert.assertEquals(gtfsObject.getAgencyName(),"name (short) (1234)", "agency name must be correcty set" );
		Assert.assertEquals(gtfsObject.getAgencyURL().toString(),"http://www.mywebsite.com", "agency url must be correcty set" );
		Assert.assertEquals(gtfsObject.getAgencyPhone(),"01 02 03 04 05", "agency phone must be correcty set" );

	}

	@Test (groups = {"Producers"}, description = "test medium company data" )
	public void verifyAgencyProducer2() throws ChouetteException 
	{

		GtfsAgencyProducer producer = (GtfsAgencyProducer) applicationContext.getBean("GtfsAgencyExportProducer");

		GtfsReport report = new GtfsReport(GtfsReport.KEY.EXPORT);
		Company neptuneObject = new Company();
		neptuneObject.setObjectId("GTFS:Company:1234");
		neptuneObject.setName("name");
		neptuneObject.setShortName("short");
		neptuneObject.setPhone("01 02 03 04 05");

		GtfsAgency gtfsObject = producer.produce(neptuneObject , report );
		System.out.println("verifyAgencyProducer2");
		System.out.println(GtfsAgency.header);
		System.out.println(gtfsObject.getCSVLine()+"\n");

		Assert.assertEquals(gtfsObject.getAgencyId(), toGtfsId(neptuneObject.getObjectId()),"agency id must be correcty set");
		Assert.assertEquals(gtfsObject.getAgencyName(),"name (short)", "agency name must be correcty set" );
		Assert.assertEquals(gtfsObject.getAgencyURL().toString(),"http://www.short.com", "agency url must be correcty set" );
		Assert.assertEquals(gtfsObject.getAgencyPhone(),"01 02 03 04 05", "agency phone must be correcty set" );

	}
	@Test (groups = {"Producers"}, description = "test loight company data" )
	public void verifyAgencyProducer3() throws ChouetteException 
	{

		GtfsAgencyProducer producer = (GtfsAgencyProducer) applicationContext.getBean("GtfsAgencyExportProducer");

		GtfsReport report = new GtfsReport(GtfsReport.KEY.EXPORT);
		Company neptuneObject = new Company();
		neptuneObject.setObjectId("GTFS:Company:1234");
		neptuneObject.setName("name");
		neptuneObject.setPhone("01 02 03 04 05");

		GtfsAgency gtfsObject = producer.produce(neptuneObject , report );
		System.out.println("verifyAgencyProducer3");
		System.out.println(GtfsAgency.header);
		System.out.println(gtfsObject.getCSVLine()+"\n");

		Assert.assertEquals(gtfsObject.getAgencyId(), toGtfsId(neptuneObject.getObjectId()),"agency id must be correcty set");
		Assert.assertEquals(gtfsObject.getAgencyName(),"name", "agency name must be correcty set" );
		Assert.assertEquals(gtfsObject.getAgencyURL().toString(),"http://www.null.com", "agency url must be correcty set" );
		Assert.assertEquals(gtfsObject.getAgencyPhone(),"01 02 03 04 05", "agency phone must be correcty set" );

	}

	protected String toGtfsId(String neptuneId)
	{
		String[] tokens = neptuneId.split(":");
		return tokens[2];
	}

//	private void printItems(String indent,List<ReportItem> items) 
//	{
//		if (items == null) return;
//		for (ReportItem item : items) 
//		{
//			System.out.println(indent+item.getStatus().name()+" : "+item.getLocalizedMessage());
//			printItems(indent+"   ",item.getItems());
//		}
//
//	}

}
