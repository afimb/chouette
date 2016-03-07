package mobi.chouette.exchange.regtopp.importer.parser;

import mobi.chouette.exchange.parameters.AbstractExportParameter;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporterInputValidator;

import org.testng.Assert;
import org.testng.annotations.Test;

public class RegtoppImporterInputValidatorTests 
{

	@Test(groups = { "InputValidator" }, description = "test good inputs")
	public void verifyGoodInputs() throws Exception
	{
		RegtoppImporterInputValidator validator = new RegtoppImporterInputValidator();
		RegtoppImportParameters parameters = new RegtoppImportParameters();
		parameters.setObjectIdPrefix("Regtopp");
		
		boolean result = validator.checkParameters(parameters,null);
		Assert.assertTrue(result, "check for good parameters");

		result = validator.checkFilename("data.zip");
		Assert.assertTrue(result, "check for good file name");

		result = validator.checkFilename("data.txt");
		Assert.assertFalse(result, "txt files not allowed");
}

	@Test(groups = { "InputValidator" }, description = "test bad inputs")
	public void verifyBadInputs() throws Exception
	{
		RegtoppImporterInputValidator validator = new RegtoppImporterInputValidator();

		boolean result = validator.checkParameters(new BadParameters(),null);
		Assert.assertFalse(result, "check for parameter class");
		
		RegtoppImportParameters parameters = new RegtoppImportParameters();
		parameters.setObjectIdPrefix("Regtopp");
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

		parameters.setObjectIdPrefix("Regtopp");
		result = validator.checkFilename(null);
		Assert.assertFalse(result, "check for filename");
	}


	private class BadParameters extends AbstractExportParameter
	{

	}

}
