package fr.certu.chouette.exchange.gtfs.export.producer;

import org.apache.log4j.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsRouteProducer;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.model.GtfsRoute;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;

@ContextConfiguration(locations={"classpath:testContext.xml","classpath*:chouetteContext.xml"})
public class GtfsExportRouteProducerTests extends AbstractTestNGSpringContextTests
{
	private static final Logger logger = Logger.getLogger(GtfsExportRouteProducerTests.class);

	@Test (groups = {"Producers"}, description = "test route with both short and long name" )
	public void verifyRouteProducerWithShortAndLongName() throws ChouetteException 
	{
		GtfsRouteProducer producer = new GtfsRouteProducer();

		GtfsReport report = new GtfsReport(GtfsReport.KEY.EXPORT);
		Route neptuneObject = new Route();
		neptuneObject.setObjectId("GTFS:Route:1234");
		Line line = new Line();
		line.setObjectId("GTFS:Line:4321");
		line.addRoute(neptuneObject);
		line.setName("lineName");
		line.setNumber("lineNumber");
		line.setPublishedName("publishedLineName");
		Company company = new Company();
		company.setObjectId("GTFS:Company:1234");
		company.setName("name");
		line.setCompany(company);
		neptuneObject.setWayBack("A");
		GtfsRoute gtfsObject = producer.produce(neptuneObject , report );
		System.out.println("verifyRouteProducerWithShortAndLongName");

		Assert.assertNotNull(gtfsObject,"Route should be returned");
		System.out.println(GtfsRoute.header);
		System.out.println(gtfsObject.getCSVLine()+"\n");
		Assert.assertEquals(gtfsObject.getRouteShortName(), line.getNumber(),"RouteShortName must be line Number");
		Assert.assertEquals(gtfsObject.getRouteLongName(), line.getPublishedName()+" - Aller","RouteLongName must be correctly set");

	}

	@Test (groups = {"Producers"}, description = "test route with no short name" )
	public void verifyRouteProducerWithNoShortName() throws ChouetteException 
	{
		GtfsRouteProducer producer = new GtfsRouteProducer();

		GtfsReport report = new GtfsReport(GtfsReport.KEY.EXPORT);
		Route neptuneObject = new Route();
		neptuneObject.setObjectId("GTFS:Route:1234");
		Line line = new Line();
		line.setObjectId("GTFS:Line:4321");
		line.addRoute(neptuneObject);
		line.setName("lineName");
		line.setPublishedName("publishedLineName");
		Company company = new Company();
		company.setObjectId("GTFS:Company:1234");
		company.setName("name");
		line.setCompany(company);
		neptuneObject.setWayBack("A");
		GtfsRoute gtfsObject = producer.produce(neptuneObject , report );
		System.out.println("verifyRouteProducerWithNoShortName");

		Assert.assertNotNull(gtfsObject,"Route should be returned");
		System.out.println(GtfsRoute.header);
		System.out.println(gtfsObject.getCSVLine()+"\n");
		Assert.assertNull(gtfsObject.getRouteShortName(),"RouteShortName must be null");
		Assert.assertEquals(gtfsObject.getRouteLongName(), line.getPublishedName()+" - Aller","RouteLongName must be correctly set");

	}

	@Test (groups = {"Producers"}, description = "test route with no long name" )
	public void verifyRouteProducerWithNoLongName() throws ChouetteException 
	{
		GtfsRouteProducer producer = new GtfsRouteProducer();

		GtfsReport report = new GtfsReport(GtfsReport.KEY.EXPORT);
		Route neptuneObject = new Route();
		neptuneObject.setObjectId("GTFS:Route:1234");
		Line line = new Line();
		line.setObjectId("GTFS:Line:4321");
		line.addRoute(neptuneObject);
		line.setNumber("lineNumber");
		Company company = new Company();
		company.setObjectId("GTFS:Company:1234");
		company.setName("name");
		line.setCompany(company);
		neptuneObject.setWayBack("A");
		GtfsRoute gtfsObject = producer.produce(neptuneObject , report );
		System.out.println("verifyRouteProducerWithNoLongName");

		Assert.assertNotNull(gtfsObject,"Route should be returned");
		System.out.println(GtfsRoute.header);
		System.out.println(gtfsObject.getCSVLine()+"\n");
		Assert.assertEquals(gtfsObject.getRouteShortName(), line.getNumber(),"RouteShortName must be line Number");
		Assert.assertEquals(gtfsObject.getRouteLongName(),"", "RouteLongName must be empty");

	}

	@Test (groups = {"Producers"}, description = "test route with no name" )
	public void verifyRouteProducerWithNoName() throws ChouetteException 
	{
		GtfsRouteProducer producer = new GtfsRouteProducer();

		GtfsReport report = new GtfsReport(GtfsReport.KEY.EXPORT);
		Route neptuneObject = new Route();
		neptuneObject.setObjectId("GTFS:Route:1234");
		Line line = new Line();
		line.setObjectId("GTFS:Line:4321");
		line.addRoute(neptuneObject);
		Company company = new Company();
		company.setObjectId("GTFS:Company:1234");
		company.setName("name");
		line.setCompany(company);
		neptuneObject.setWayBack("A");
		GtfsRoute gtfsObject = producer.produce(neptuneObject , report );
		System.out.println("verifyRouteProducerWithNoName");
		System.out.println(gtfsObject);

		Assert.assertNull(gtfsObject, "GTFS Route must be null");

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
