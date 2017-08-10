package mobi.chouette.exchange.netexprofile.importer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import mobi.chouette.exchange.netexprofile.Constant;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;

public class NetexImporterTest {

	private NetexImporter importer = new NetexImporter();
	
	private File file = new File("src/test/data/minimal_publicationdelivery.xml");
	
	@Test
	public void testJavaParsingWithoutSkip() throws FileNotFoundException, SaxonApiException, IOException, SAXException, XMLStreamException, JAXBException {
		
		PublicationDeliveryStructure lineDeliveryStructure = importer.unmarshal(file, new HashSet<>());
		Assert.assertEquals(lineDeliveryStructure.getDataObjects().getCompositeFrameOrCommonFrame().size(),1,"Expected compsite frame");
		
	}

	@Test
	public void testJavaParsingWithSkip() throws FileNotFoundException, SaxonApiException, IOException, SAXException, XMLStreamException, JAXBException {
		
		HashSet<QName> elementsToSkip = new HashSet<>();
		elementsToSkip.add(new QName(Constant.NETEX_NAMESPACE, "CompositeFrame"));
		PublicationDeliveryStructure lineDeliveryStructure = importer.unmarshal(file, elementsToSkip);
		Assert.assertEquals(lineDeliveryStructure.getDataObjects().getCompositeFrameOrCommonFrame().size(),0,"Expected NO compsite frame");
		
	}


	@Test
	public void testXPathWithoutSkip() throws FileNotFoundException, SaxonApiException, IOException, SAXException, XMLStreamException, JAXBException {
		
		HashSet<QName> elementsToSkip = new HashSet<>();
		XdmNode node = importer.parseFileToXdmNode(file, elementsToSkip);
		XPathCompiler xPathCompiler = importer.getXPathCompiler();
		XPathSelector load = xPathCompiler.compile("/PublicationDelivery/dataObjects/CompositeFrame").load();
		load.setContextItem(node);
		XdmValue evaluate = load.evaluate();
		Assert.assertEquals(evaluate.size(), 1,"Expected compositeFrame");
		
	}

	@Test
	public void testXPathWithSkip() throws FileNotFoundException, SaxonApiException, IOException, SAXException, XMLStreamException, JAXBException {
		
		HashSet<QName> elementsToSkip = new HashSet<>();
		elementsToSkip.add(new QName(Constant.NETEX_NAMESPACE, "CompositeFrame"));
		XdmNode node = importer.parseFileToXdmNode(file, elementsToSkip);
		XPathCompiler xPathCompiler = importer.getXPathCompiler();
		XPathSelector load = xPathCompiler.compile("/PublicationDelivery/dataObjects/CompositeFrame").load();
		load.setContextItem(node);
		XdmValue evaluate = load.evaluate();
		Assert.assertEquals(evaluate.size(), 0,"Expected NO compositeFrame");
		
	}

}
