package mobi.chouette.exchange.regtopp.parser.index;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.exchange.regtopp.importer.index.v11.LineById;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppLineLIN;

public class LineByIdTest extends AbstractIndexTest{
	

	@Test(dependsOnMethods = {"setupImporter"})
	public void testValidation() throws Exception {
		FileContentParser fileContentParser = createUnderlyingFileParser(new File("src/test/data/regtopp12kolumbus/R5001.lin"), new Class[] {RegtoppLineLIN.class});
		LineById index = new LineById(validationReporter,fileContentParser);
		for(RegtoppLineLIN obj : index) {
			boolean validData = index.validate(obj,importer);
			Assert.assertTrue(validData,"Bean did not validate: "+obj);
		}
		Assert.assertEquals(index.getLength(), 159);
		
		Assert.assertEquals(0, validationReporter.getExceptions().size());
	}

}
