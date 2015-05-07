package mobi.chouette.exchange.gtfs.importer;

import mobi.chouette.exchange.parameters.AbstractExportParameter;

import org.testng.Assert;
import org.testng.annotations.Test;

public class GtfsImporterInputValidatorTests 
{

	@Test(groups = { "InputValidator" }, description = "test good inputs")
	public void verifyGoodInputs() throws Exception
	{
		GtfsImporterInputValidator validator = new GtfsImporterInputValidator();
		GtfsImportParameters parameters = new GtfsImportParameters();
		parameters.setObjectIdPrefix("GTFS");
		boolean result = validator.check(parameters,null,"data.zip");

		Assert.assertTrue(result, "check for good parameters");
	}

	@Test(groups = { "InputValidator" }, description = "test bad inputs")
	public void verifyBadInputs() throws Exception
	{
		GtfsImporterInputValidator validator = new GtfsImporterInputValidator();

		boolean result = validator.check(new BadParameters(),null,"data.zip");
		Assert.assertFalse(result, "check for parameter class");
		
		GtfsImportParameters parameters = new GtfsImportParameters();
		parameters.setObjectIdPrefix("GTFS");
		parameters.setReferencesType("bidon");
		result = validator.check(parameters,null,"data.zip");
		Assert.assertFalse(result, "check for wrong type");

		parameters.setReferencesType("line");
		parameters.setObjectIdPrefix(null);
		result = validator.check(parameters,null,"data.zip");
		Assert.assertFalse(result, "check for no object_id_prefix");

		parameters.setObjectIdPrefix("");
		result = validator.check(parameters,null,"data.zip");
		Assert.assertFalse(result, "check for empty object_id_prefix");

		parameters.setObjectIdPrefix("GTFS");
		result = validator.check(parameters,null,null);
		Assert.assertFalse(result, "check for filename");
	}


	private class BadParameters extends AbstractExportParameter
	{

	}

}
