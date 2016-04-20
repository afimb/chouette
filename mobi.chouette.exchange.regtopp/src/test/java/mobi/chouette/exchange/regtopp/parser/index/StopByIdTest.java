package mobi.chouette.exchange.regtopp.parser.index;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.exchange.regtopp.importer.index.v11.StopById;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppStopHPL;

public class StopByIdTest extends AbstractIndexTest{
	

	@Test(dependsOnMethods = {"setupImporter"})
	public void testValidation() throws Exception {
		FileContentParser fileContentParser = createUnderlyingFileParser(new File("src/test/data/regtopp12kolumbus/R5001.hpl"), new Class[] {RegtoppStopHPL.class});
		StopById index = new StopById(validationReporter,fileContentParser);
		for(RegtoppStopHPL obj : index) {
			boolean validData = index.validate(obj,importer);
			Assert.assertTrue(validData,"Bean did not validate: "+obj);
		}
		
		Assert.assertEquals(index.getLength(), 3950);
		Assert.assertEquals(0, validationReporter.getExceptions().size());
	}

}
