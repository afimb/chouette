package mobi.chouette.exchange.regtopp.parser.index;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.exchange.regtopp.model.RegtoppFootnoteMRK;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.importer.parser.index.FootnoteById;

public class FootnoteByIdTest extends AbstractIndexTest{
	

	@Test(dependsOnMethods = {"setupImporter"})
	public void testValidation() throws Exception {
		FileContentParser fileContentParser = createUnderlyingFileParser(new File("src/test/data/regtopp12/R5001.mrk"), new Class[] {RegtoppFootnoteMRK.class});
		FootnoteById index = new FootnoteById(validationReporter,fileContentParser);
		for(RegtoppFootnoteMRK obj : index) {
			boolean validData = index.validate(obj,importer);
			Assert.assertTrue(validData,"Bean did not validate: "+obj);
		}
		
		Assert.assertEquals(0, validationReporter.getExceptions().size());
	}

}
