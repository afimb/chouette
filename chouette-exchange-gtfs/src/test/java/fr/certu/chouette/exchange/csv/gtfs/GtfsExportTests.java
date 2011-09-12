package fr.certu.chouette.exchange.csv.gtfs;

import org.apache.log4j.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

@ContextConfiguration(locations={"classpath:testContext.xml"})
public class GtfsExportTests extends AbstractTestNGSpringContextTests
{
	private static final Logger LOGGER = Logger.getLogger(GtfsExportTests.class);

}
