package mobi.chouette.exchange.regtopp.parser.index;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.exchange.regtopp.importer.index.v11.DestinationById;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDestinationDST;

public class DestinationByIdTest extends AbstractIndexTest{
	

	@Test(dependsOnMethods = {"setupImporter"})
	public void testValidation() throws Exception {
		FileContentParser fileContentParser = createUnderlyingFileParser(new File("src/test/data/regtopp12kolumbus/R5001.dst"), new Class[] {RegtoppDestinationDST.class});
		DestinationById index = new DestinationById(validationReporter,fileContentParser);
		for(RegtoppDestinationDST obj : index) {
			boolean validData = index.validate(obj,importer);
			Assert.assertTrue(validData,"Bean did not validate: "+obj);
		}
		
		Assert.assertEquals(index.getLength(), 371);
		Assert.assertEquals(0, validationReporter.getExceptions().size());
	}

}
