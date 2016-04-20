package mobi.chouette.exchange.regtopp.parser.index;

import java.io.File;
import java.util.Iterator;

import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.exchange.regtopp.importer.index.v11.UniqueLinesByTripIndex;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppTripIndexTIX;

public class UniqueLinesByTripIndexTest extends AbstractIndexTest {

	@Test(dependsOnMethods = { "setupImporter" })
	public void testValidation() throws Exception {
		FileContentParser fileContentParser = createUnderlyingFileParser(new File("src/test/data/regtopp12kolumbus/R5001.tix"),
				new Class[] { RegtoppTripIndexTIX.class });
		UniqueLinesByTripIndex index = new UniqueLinesByTripIndex(validationReporter, fileContentParser);
		
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
