package mobi.chouette.exchange.netexprofile.parser;

import static mobi.chouette.common.Constant.REFERENTIAL;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.NetexprofileImportParameters;
import mobi.chouette.exchange.netexprofile.jaxb.NetexXMLProcessingHelperFactory;
import mobi.chouette.exchange.netexprofile.util.NetexReferential;
import mobi.chouette.model.DestinationDisplay;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.Referential;

public class DestinationDisplayParserTest {

	private NetexXMLProcessingHelperFactory netexImporter = new NetexXMLProcessingHelperFactory();

	private PublicationDeliveryParser parser = new PublicationDeliveryParser();

	@Test
	public void testWithDestinationDisplay() throws Exception {

		Referential referential = new Referential();

		parseIntoReferential(referential, "src/test/data/ServiceFrameWithDestinationDisplay.xml");

		StopPoint stopPoint1 = referential.getStopPoints().get("AVI:StopPoint:1263628001");
		Assert.assertNotNull(stopPoint1);

		DestinationDisplay destinationDisplayOriginal = stopPoint1.getDestinationDisplay();
		Assert.assertNotNull(destinationDisplayOriginal);
		
		Assert.assertEquals(destinationDisplayOriginal.getFrontText(), "10 Molde");
		Assert.assertEquals(destinationDisplayOriginal.getVias().size(), 1);
		Assert.assertEquals(destinationDisplayOriginal.getVias().get(0).getFrontText(), "Via");
		
		StopPoint stopPoint2 = referential.getStopPoints().get("AVI:StopPoint:1263628002");
		Assert.assertNotNull(stopPoint2);

		DestinationDisplay destinationDisplayNoNumber = stopPoint2.getDestinationDisplay();
		Assert.assertNotNull(destinationDisplayNoNumber);
		
		Assert.assertEquals(destinationDisplayNoNumber.getFrontText(), "Molde");
	
	}

	

	protected void parseIntoReferential(Referential referential, String netedFilePath)
			throws JAXBException, XMLStreamException, IOException, SAXException, Exception {
		Context context = new Context();
		context.put(REFERENTIAL, referential);

		NetexReferential netexReferential = new NetexReferential();
		context.put(Constant.NETEX_REFERENTIAL, netexReferential);

		context.put(Constant.NETEX_WITH_COMMON_DATA, false);
		context.put(Constant.CONFIGURATION, new NetexprofileImportParameters());

		PublicationDeliveryStructure pubDelivery = netexImporter.unmarshal(new File(netedFilePath), new HashSet<>());

		context.put(Constant.NETEX_DATA_JAVA, pubDelivery);

		parser.parse(context);
	}

}
