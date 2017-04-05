package mobi.chouette.exchange.gtfs.parser;

import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;

public class AbstractConverterTest {
	@Test
	public void testCreateIdWithParsingDots() {
		GtfsImportParameters configuration = new GtfsImportParameters();
		configuration.setObjectIdPrefix("PRE");
		String id = AbstractConverter.composeObjectId(configuration, "VehicleJourney", "ABC.123", null);
	
		Assert.assertEquals(id, "ABC:VehicleJourney:123");
	}

	@Test
	public void testCreateIdWithoutParsingDots() {
		GtfsImportParameters configuration = new GtfsImportParameters();
		configuration.setSplitIdOnDot(false);
		configuration.setObjectIdPrefix("PRE");
		String id = AbstractConverter.composeObjectId(configuration, "VehicleJourney", "ABC.TestType.123", null);
	
		Assert.assertEquals(id, "PRE:VehicleJourney:ABC_TestType_123");
	}
}
