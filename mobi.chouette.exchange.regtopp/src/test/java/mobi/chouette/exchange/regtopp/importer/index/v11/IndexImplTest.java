package mobi.chouette.exchange.regtopp.importer.index.v11;

import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.exchange.regtopp.importer.index.IndexImpl;

public class IndexImplTest {

	@Test 
	public void testIsNotNullString() {
		Assert.assertTrue(IndexImpl.isNull("0000"));
		Assert.assertTrue(IndexImpl.isNull("    "));
		Assert.assertTrue(IndexImpl.isNull(""));
		Assert.assertTrue(IndexImpl.isNotNull("0001"));
		Assert.assertTrue(IndexImpl.isNotNull("1"));
	}
	
}
