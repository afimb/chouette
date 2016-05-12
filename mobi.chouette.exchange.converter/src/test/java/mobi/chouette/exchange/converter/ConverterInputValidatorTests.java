package mobi.chouette.exchange.converter;

import mobi.chouette.exchange.gtfs.exporter.GtfsExportParameters;
import mobi.chouette.exchange.neptune.exporter.NeptuneExportParameters;
import mobi.chouette.exchange.neptune.importer.NeptuneImportParameters;
import mobi.chouette.exchange.netex.exporter.NetexExportParameters;
import mobi.chouette.exchange.parameters.AbstractExportParameter;
import mobi.chouette.exchange.parameters.AbstractImportParameter;
import mobi.chouette.exchange.parameters.AbstractParameter;

import org.apache.log4j.BasicConfigurator;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ConverterInputValidatorTests 
{

	static {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	@Test(groups = { "InputValidator" }, description = "test good inputs")
	public void verifyGoodInputs() throws Exception
	{
		ConverterInputValidator validator = new ConverterInputValidator();
		ConvertParameters parameters = new ConvertParameters();
		parameters.setImportConfiguration(new NeptuneImportParameters());
		parameters.setExportConfiguration(new NetexExportParameters());
		boolean result = validator.checkParameters(parameters,null);
		Assert.assertTrue(result, "check for good parameters");
		
		result = validator.checkFilename("data.zip");
		Assert.assertTrue(result, "check for good file name");
	}

	@Test(groups = { "InputValidator" }, description = "test bad inputs")
	public void verifyBadInputs() throws Exception
	{
		ConverterInputValidator validator = new ConverterInputValidator();

		boolean result = validator.checkParameters(new BadParameters(),null);
		Assert.assertFalse(result, "check for parameter class");
		
		ConvertParameters parameters = new ConvertParameters();
		result = validator.checkParameters(parameters,null);
		Assert.assertFalse(result, "check for missing import parameters");

		parameters.setImportConfiguration(new BadImportParameters());
		result = validator.checkParameters(parameters,null);
		Assert.assertFalse(result, "check for missing export parameters");

		parameters.setExportConfiguration(new BadExportParameters());
		result = validator.checkParameters(parameters,null);
		Assert.assertFalse(result, "check for bad import parameters");

		parameters.setImportConfiguration(new NeptuneImportParameters());
		result = validator.checkParameters(parameters,null);
		Assert.assertFalse(result, "check for bad export parameters");

		parameters.setExportConfiguration(new NeptuneExportParameters());
		result = validator.checkParameters(parameters,null);
		Assert.assertFalse(result, "check for same format");
		parameters.setExportConfiguration(new GtfsExportParameters());
		result = validator.checkParameters(parameters,null);
		Assert.assertFalse(result, "check for error in specific parameters");

		result = validator.checkFilename(null);
		Assert.assertFalse(result, "check for missing filename");
		result = validator.checkFilename("test.rar");
		Assert.assertFalse(result, "check for filename type ");
	}


	private class BadParameters extends AbstractParameter
	{

	}
	
	private class BadExportParameters extends AbstractExportParameter
	{

	}
	private class BadImportParameters extends AbstractImportParameter
	{

	}

}
