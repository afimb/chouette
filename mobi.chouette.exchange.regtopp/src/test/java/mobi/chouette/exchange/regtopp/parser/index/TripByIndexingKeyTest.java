package mobi.chouette.exchange.regtopp.parser.index;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.exchange.regtopp.importer.index.v11.TripByIndexingKey;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppTripIndexTIX;

public class TripByIndexingKeyTest extends AbstractIndexTest {

	@Test(dependsOnMethods = { "setupImporter" })
	public void testValidation() throws Exception {
		FileContentParser fileContentParser = createUnderlyingFileParser(new File("src/test/data/regtopp12kolumbus/R5001.tix"),
				new Class[] { RegtoppTripIndexTIX.class });
		TripByIndexingKey index = new TripByIndexingKey(validationReporter, fileContentParser);
		for (AbstractRegtoppTripIndexTIX obj : index) {
			boolean validData = index.validate(obj, importer);
			Assert.assertTrue(validData, "Bean did not validate: " + obj);
		}

		Assert.assertEquals(index.getLength(), 17446);

		Assert.assertEquals(0, validationReporter.getExceptions().size());
	}

}
