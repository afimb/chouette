package mobi.chouette.exchange.regtopp.importer.index.v11;

import java.io.File;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.exchange.regtopp.importer.index.v11.TripByIndexingKey;
import mobi.chouette.exchange.regtopp.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppTripIndexTIX;

public class TripByIndexingKeyTest extends AbstractIndexTest {

	@Test(dependsOnMethods = { "setupImporter" })
	public void testValidation() throws Exception {
		FileContentParser fileContentParser = createUnderlyingFileParser(new File("src/test/data/fullsets/kolumbus_v12/R5001.tix"),
				new Class[] { RegtoppTripIndexTIX.class }, RegtoppException.ERROR.TIX_INVALID_FIELD_VALUE);
		TripByIndexingKey index = new TripByIndexingKey(new Context(), validationReporter, fileContentParser);
		for (AbstractRegtoppTripIndexTIX obj : index) {
			boolean validData = index.validate(obj, importer);
			Assert.assertTrue(validData, "Bean did not validate: " + obj);
		}

		Assert.assertEquals(index.getLength(), 17446);

		Assert.assertEquals(0, validationReporter.getExceptions().size());
	}

}
