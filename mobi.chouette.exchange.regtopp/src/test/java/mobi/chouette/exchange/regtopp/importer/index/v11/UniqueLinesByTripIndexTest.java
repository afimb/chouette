package mobi.chouette.exchange.regtopp.importer.index.v11;

import java.io.File;
import java.util.Iterator;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.exchange.regtopp.importer.index.v11.UniqueLinesByTripIndex;
import mobi.chouette.exchange.regtopp.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppTripIndexTIX;

public class UniqueLinesByTripIndexTest extends AbstractIndexTest {

	@Test(dependsOnMethods = { "setupImporter" })
	public void testValidation() throws Exception {
		FileContentParser fileContentParser = createUnderlyingFileParser(new File("src/test/data/fullsets/kolumbus_v12/R5001.tix"),
				new Class[] { RegtoppTripIndexTIX.class }, RegtoppException.ERROR.TIX_INVALID_FIELD_VALUE);
		UniqueLinesByTripIndex index = new UniqueLinesByTripIndex(new Context(), validationReporter, fileContentParser);
		
		Iterator<String> keys = index.keys();
		
		int counter = 0;
		
		while(keys.hasNext()) {
			Assert.assertNotNull(keys.next());
			counter++;
		}
		
		Assert.assertEquals(counter, 159,"Number of unique lines");

		Assert.assertEquals(0, validationReporter.getExceptions().size());
	}

}
