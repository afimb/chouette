package mobi.chouette.exchange.geojson.exporter;

import java.nio.file.Paths;
import java.util.Date;

import mobi.chouette.exchange.geojson.exporter.GeojsonExportParameters;
import mobi.chouette.exchange.geojson.exporter.GeojsonExporterInputValidator;
import mobi.chouette.exchange.parameters.AbstractExportParameter;

import org.testng.Assert;
import org.testng.annotations.Test;

public class GeojsonExporterInputValidatorTests {
	@SuppressWarnings("deprecation")
	@Test(groups = { "InputValidator" }, description = "test good inputs")
	public void verifyGoodInputs() throws Exception
	{
		GeojsonExporterInputValidator validator = new GeojsonExporterInputValidator();
		GeojsonExportParameters parameters = new GeojsonExportParameters();
		Date startDate = new Date(2015,04,20);
		Date endDate = new Date(2015,04,30);
		parameters.setStartDate(startDate);
		parameters.setEndDate(endDate);
		parameters.setReferencesType("line");
		boolean result = validator.checkParameters(parameters,null);

		Assert.assertTrue(result, "check for good parameters");
	}

	@SuppressWarnings("deprecation")
	@Test(groups = { "InputValidator" }, description = "test bad inputs")
	public void verifyBadInputs() throws Exception
	{
		GeojsonExporterInputValidator validator = new GeojsonExporterInputValidator();

		boolean result = validator.checkParameters(new BadParameters(),null);
		Assert.assertFalse(result, "check for parameter class");
		
		GeojsonExportParameters parameters = new GeojsonExportParameters();
		Date startDate = new Date(2015,04,20);
		Date endDate = new Date(2015,04,30);
		parameters.setStartDate(endDate);
		parameters.setEndDate(startDate);
		parameters.setReferencesType("line");
		result = validator.checkParameters(parameters,null);
		Assert.assertFalse(result, "check for bad dates");

		parameters.setStartDate(startDate);
		parameters.setEndDate(endDate);
		
		parameters.setReferencesType("bidon");
		result = validator.checkParameters(parameters,null);
		Assert.assertFalse(result, "check for wrong type");

		parameters.setReferencesType("line");
		result = validator.checkParameters(parameters,null);
		Assert.assertFalse(result, "check for no timezone");

		result = validator.checkParameters(parameters,null);
		Assert.assertFalse(result, "check for empty timezone");

		result = validator.checkParameters(parameters,null);
		Assert.assertFalse(result, "check for no object_id_prefix");

		result = validator.checkParameters(parameters,null);
		Assert.assertFalse(result, "check for empty object_id_prefix");

		result = validator.checkFilename("data.zip");
		Assert.assertFalse(result, "check for no filename");
		
		result = validator.checkFile("", Paths.get("bidon"), null);
		Assert.assertFalse(result, "check for bad zip file");
	}


	private class BadParameters extends AbstractExportParameter
	{

	}
}
