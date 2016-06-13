package mobi.chouette.exchange.netex.importer;

import java.nio.file.Paths;

import mobi.chouette.exchange.netex.importer.NetexImportParameters;
import mobi.chouette.exchange.netex.importer.NetexImporterInputValidator;
import mobi.chouette.exchange.parameters.AbstractExportParameter;

import org.testng.Assert;
import org.testng.annotations.Test;

public class NetexImporterInputValidatorTests {
	@Test(groups = { "InputValidator" }, description = "test good inputs")
	public void verifyGoodInputs() throws Exception
	{
		NetexImporterInputValidator validator = new NetexImporterInputValidator();
		NetexImportParameters parameters = new NetexImportParameters();
	
		boolean result = validator.checkParameters(parameters,null);

		Assert.assertTrue(result, "check for good parameters");
		 result = validator.checkFilename("data.zip");

		Assert.assertTrue(result, "check for good  file name");
		 result = validator.checkFilename("data.txt");

		Assert.assertTrue(result, "check for good file name ");
		
		result = validator.checkFile("good.xml", Paths.get("src/test/data/good.xml"), null);
		Assert.assertTrue(result, "check for good txt file");
		
		
		result = validator.checkFile("good.zip", Paths.get("src/test/data/good.zip"), null);
		Assert.assertTrue(result, "check for good zip file");
				
	}

	@Test(groups = { "InputValidator" }, description = "test bad inputs")
	public void verifyBadInputs() throws Exception
	{
		NetexImporterInputValidator validator = new NetexImporterInputValidator();

		boolean result = validator.checkParameters(new BadParameters(),null);
		Assert.assertFalse(result, "check for parameter class");
		
		NetexImportParameters parameters = new NetexImportParameters();
		
		result = validator.checkParameters(parameters,null);
		Assert.assertFalse(result, "check for wrong type");

		
		result = validator.checkParameters(parameters,null);
		Assert.assertFalse(result, "check for no object_id_prefix");

		
		result = validator.checkParameters(parameters,null);
		Assert.assertFalse(result, "check for empty object_id_prefix");

		
		result = validator.checkFilename(null);
		Assert.assertFalse(result, "check for filename");
		
		
		result = validator.checkFile("bad.zip", Paths.get("src/test/data/bad.zip"), null);
		Assert.assertFalse(result, "check for bad zip file");
		
	}


	private class BadParameters extends AbstractExportParameter
	{

	}
}
