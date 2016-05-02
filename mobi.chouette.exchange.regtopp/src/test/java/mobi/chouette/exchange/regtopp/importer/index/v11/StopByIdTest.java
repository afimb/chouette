package mobi.chouette.exchange.regtopp.importer.index.v11;

import java.io.File;

import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.exchange.regtopp.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppStopHPL;

public class StopByIdTest extends AbstractIndexTest{
	

	@Test(dependsOnMethods = {"setupImporter"})
	public void testValidation() throws Exception {
		FileContentParser fileContentParser = createUnderlyingFileParser(new File("src/test/data/fullsets/kolumbus_v12/R5001.hpl"), new Class[] {RegtoppStopHPL.class}, RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE);
		StopById index = new StopById(validationReporter,fileContentParser);
		for(AbstractRegtoppStopHPL obj : index) {
			boolean validData = index.validate(obj,importer);
			Assert.assertTrue(validData,"Bean did not validate: "+obj);
		}
		
		Assert.assertEquals(index.getLength(), 3950);
		Assert.assertEquals(0, validationReporter.getExceptions().size());
	}

}
