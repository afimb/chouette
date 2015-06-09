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
		boolean result = validator.checkParameters(parameters,null);

		Assert.assertTrue(result, "check for good parameters");
		 result = validator.checkFilename("data.zip");

		Assert.assertTrue(result, "check for good  file name");
		 result = validator.checkFilename("data.txt");

		Assert.assertTrue(result, "check for good file name ");
	}

	@Test(groups = { "InputValidator" }, description = "test bad inputs")
	public void verifyBadInputs() throws Exception
	{
		GtfsImporterInputValidator validator = new GtfsImporterInputValidator();

		boolean result = validator.checkParameters(new BadParameters(),null);
		Assert.assertFalse(result, "check for parameter class");
		
		GtfsImportParameters parameters = new GtfsImportParameters();
		parameters.setObjectIdPrefix("GTFS");
		parameters.setReferencesType("bidon");
		result = validator.checkParameters(parameters,null);
		Assert.assertFalse(result, "check for wrong type");

		parameters.setReferencesType("line");
		parameters.setObjectIdPrefix(null);
		result = validator.checkParameters(parameters,null);
		Assert.assertFalse(result, "check for no object_id_prefix");

		parameters.setObjectIdPrefix("");
		result = validator.checkParameters(parameters,null);
		Assert.assertFalse(result, "check for empty object_id_prefix");

		parameters.setObjectIdPrefix("GTFS");
		result = validator.checkFilename(null);
		Assert.assertFalse(result, "check for filename");
	}


	private class BadParameters extends AbstractExportParameter
	{

	}

}
