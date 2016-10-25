package mobi.chouette.exchange.neptune.importer;

import java.nio.file.Paths;

import mobi.chouette.exchange.parameters.AbstractExportParameter;

import org.testng.Assert;
import org.testng.annotations.Test;

public class NeptuneImporterInputValidatorTests 
{

	@Test(groups = { "InputValidator" }, description = "test good inputs")
	public void verifyGoodInputs() throws Exception
	{
		NeptuneImporterInputValidator validator = new NeptuneImporterInputValidator();
		NeptuneImportParameters parameters = new NeptuneImportParameters();
	
		boolean result = validator.checkParameters(parameters,null);
		Assert.assertTrue(result, "check for good parameters");
		
		result = validator.checkFilename("data.zip");
		Assert.assertTrue(result, "check for good  file name");
		
		result = validator.checkFilename("data.xml");
		Assert.assertTrue(result, "check for good file name ");
		
		result = validator.checkFile("good.xml", Paths.get("src/test/data/good.xml"), null);
		Assert.assertTrue(result, "check for good zip file");
		
		result = validator.checkFile("good.zip", Paths.get("src/test/data/good.zip"), null);
		Assert.assertTrue(result, "check for good zip file");
				
	}

	@Test(groups = { "InputValidator" }, description = "test bad inputs")
	public void verifyBadInputs() throws Exception
	{
		NeptuneImporterInputValidator validator = new NeptuneImporterInputValidator();

		boolean result = validator.checkParameters(new BadParameters(),null);
		Assert.assertFalse(result, "check for parameter class");
		
		NeptuneImportParameters parameters = new NeptuneImportParameters();
				
		result = validator.checkFilename(null);
		Assert.assertFalse(result, "check for filename");
		
		result = validator.checkFilename("data.txt");
		Assert.assertFalse(result, "check for filename");
		
		
		result = validator.checkFile("bad.zip", Paths.get("src/test/data/bad.zip"), parameters);
		Assert.assertFalse(result, "check for bad zip file");
		
	}


	private class BadParameters extends AbstractExportParameter
	{

	}

}
