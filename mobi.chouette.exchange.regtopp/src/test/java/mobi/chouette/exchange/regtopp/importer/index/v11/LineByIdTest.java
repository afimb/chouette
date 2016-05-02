package mobi.chouette.exchange.regtopp.importer.index.v11;

import java.io.File;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.exchange.regtopp.importer.index.v11.LineById;
import mobi.chouette.exchange.regtopp.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppLineLIN;

public class LineByIdTest extends AbstractIndexTest{
	

	@Test(dependsOnMethods = {"setupImporter"})
	public void testValidation() throws Exception {
		FileContentParser fileContentParser = createUnderlyingFileParser(new File("src/test/data/fullsets/kolumbus_v12/R5001.lin"), new Class[] {RegtoppLineLIN.class}, RegtoppException.ERROR.LIN_INVALID_FIELD_VALUE);
		LineById index = new LineById(new Context(), validationReporter,fileContentParser);
		for(RegtoppLineLIN obj : index) {
			boolean validData = index.validate(obj,importer);
			Assert.assertTrue(validData,"Bean did not validate: "+obj);
		}
		Assert.assertEquals(index.getLength(), 159);
		
		Assert.assertEquals(0, validationReporter.getExceptions().size());
	}

}
