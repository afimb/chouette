package mobi.chouette.exchange.netexprofile.jaxb;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.exchange.netexprofile.parser.xml.PublicactionDeliveryVersionAttributeReader;

public class PublicationDeliveryVersionAttributeReaderTest {
	
	@Test
	public void testGetProfileVersion() {
		File f = new File("Src/test/data/SK264.xml");
	
		String profileVersion = PublicactionDeliveryVersionAttributeReader.findPublicationDeliveryVersion(f);
		Assert.assertEquals(profileVersion, "1.04:NO-NeTEx-networktimetable:1.0");
	}
}
