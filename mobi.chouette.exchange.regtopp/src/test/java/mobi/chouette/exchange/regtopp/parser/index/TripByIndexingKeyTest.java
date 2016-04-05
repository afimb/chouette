package mobi.chouette.exchange.regtopp.parser.index;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.exchange.regtopp.model.RegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.importer.parser.index.TripByIndexingKey;

public class TripByIndexingKeyTest extends AbstractIndexTest {

	@Test(dependsOnMethods = { "setupImporter" })
	public void testValidation() throws Exception {
		FileContentParser fileContentParser = createUnderlyingFileParser(new File("src/test/data/regtopp12kolumbus/R5001.tix"),
				new Class[] { RegtoppTripIndexTIX.class });
		TripByIndexingKey index = new TripByIndexingKey(validationReporter, fileContentParser);
		for (RegtoppTripIndexTIX obj : index) {
			boolean validData = index.validate(obj, importer);
			Assert.assertTrue(validData, "Bean did not validate: " + obj);
		}

		Assert.assertEquals(index.getLength(), 17446);

		Assert.assertEquals(0, validationReporter.getExceptions().size());
	}

}
