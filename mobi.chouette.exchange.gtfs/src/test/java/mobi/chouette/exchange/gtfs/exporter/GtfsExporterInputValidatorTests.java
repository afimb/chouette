package mobi.chouette.exchange.gtfs.exporter;

import java.util.Date;

import mobi.chouette.exchange.parameters.AbstractExportParameter;

import org.testng.Assert;
import org.testng.annotations.Test;

public class GtfsExporterInputValidatorTests 
{

	@SuppressWarnings("deprecation")
	@Test(groups = { "InputValidator" }, description = "test good inputs")
	public void verifyGoodInputs() throws Exception
	{
		GtfsExporterInputValidator validator = new GtfsExporterInputValidator();
		GtfsExportParameters parameters = new GtfsExportParameters();
		Date startDate = new Date(2015,04,20);
		Date endDate = new Date(2015,04,30);
		parameters.setStartDate(startDate);
		parameters.setEndDate(endDate);
		parameters.setReferencesType("line");
		parameters.setTimeZone("Europe/Paris");
		parameters.setObjectIdPrefix("GTFS");
		boolean result = validator.checkParameters(parameters,null);

		Assert.assertTrue(result, "check for good parameters");
	}

	@SuppressWarnings("deprecation")
	@Test(groups = { "InputValidator" }, description = "test bad inputs")
	public void verifyBadInputs() throws Exception
	{
		GtfsExporterInputValidator validator = new GtfsExporterInputValidator();

		boolean result = validator.checkParameters(new BadParameters(),null);
		Assert.assertFalse(result, "check for parameter class");
		
		GtfsExportParameters parameters = new GtfsExportParameters();
		Date startDate = new Date(2015,04,20);
		Date endDate = new Date(2015,04,30);
		parameters.setStartDate(endDate);
		parameters.setEndDate(startDate);
		parameters.setReferencesType("line");
		parameters.setTimeZone("Europe/Paris");
		parameters.setObjectIdPrefix("GTFS");
		result = validator.checkParameters(parameters,null);
		Assert.assertFalse(result, "check for bad dates");

		parameters.setStartDate(startDate);
		parameters.setEndDate(endDate);
		
		parameters.setReferencesType("bidon");
		result = validator.checkParameters(parameters,null);
		Assert.assertFalse(result, "check for wrong type");

		parameters.setReferencesType("line");
		parameters.setTimeZone(null);
		result = validator.checkParameters(parameters,null);
		Assert.assertFalse(result, "check for no timezone");

		parameters.setTimeZone("");
		result = validator.checkParameters(parameters,null);
		Assert.assertFalse(result, "check for empty timezone");

		parameters.setTimeZone("Europe/Paris");
		parameters.setObjectIdPrefix(null);
		result = validator.checkParameters(parameters,null);
		Assert.assertFalse(result, "check for no object_id_prefix");

		parameters.setObjectIdPrefix("");
		result = validator.checkParameters(parameters,null);
		Assert.assertFalse(result, "check for empty object_id_prefix");

		parameters.setObjectIdPrefix("GTFS");
		result = validator.checkFilename("data.zip");
		Assert.assertFalse(result, "check for no filename");
	}


	private class BadParameters extends AbstractExportParameter
	{

	}

}
