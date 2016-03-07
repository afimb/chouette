package mobi.chouette.exchange.regtopp.parser.index;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.exchange.regtopp.model.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.importer.parser.index.RouteById;

public class RouteByIdTest extends AbstractIndexTest{
	

	@Test(dependsOnMethods = {"setupImporter"})
	public void testValidation() throws Exception {
		FileContentParser fileContentParser = createUnderlyingFileParser(new File("src/test/data/regtopp12/R5001.tms"), new Class[] {RegtoppRouteTMS.class});
		RouteById index = new RouteById(validationReporter,fileContentParser);
		
		for(RegtoppRouteTMS obj : index) {
			boolean validData = index.validate(obj,importer);
			Assert.assertTrue(validData,"Bean did not validate: "+obj);
		}
		
		Assert.assertEquals(0, validationReporter.getExceptions().size());
	}

}
