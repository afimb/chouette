package mobi.chouette.exchange.netexprofile.importer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.xml.sax.SAXException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.netexprofile.parser.xml.PredefinedSchemaListClasspathResourceResolver;
import mobi.chouette.exchange.netexprofile.parser.xml.SkippingXMLStreamReaderFactory;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.WhitespaceStrippingPolicy;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XdmNode;

@Log4j
public class NetexImporter {
	private static Schema netexSchema = null;

	private static JAXBContext netexJaxBContext = null;
	
	private static Processor processor = new Processor(false);
	
	private static XPathCompiler xpathCompiler;

	public synchronized Schema getNetexSchema() throws SAXException, IOException {

		if (netexSchema == null) {
			log.info("Initializing Netex schema, this may take a few seconds");
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			factory.setResourceResolver(new PredefinedSchemaListClasspathResourceResolver("/netex_schema_list.txt"));

			Source schemaFile = new StreamSource(getClass().getResourceAsStream("/NeTEx-XML-1.04beta/schema/xsd/NeTEx_publication.xsd"));
			netexSchema = factory.newSchema(schemaFile);
		}

		return netexSchema;
	}

	public synchronized JAXBContext getNetexJaxBContext() throws JAXBException {
		if (netexJaxBContext == null) {
			log.info("Initializing JAXBContext, this may take a few seconds");
			netexJaxBContext = JAXBContext.newInstance("net.opengis.gml._3:org.rutebanken.netex.model:uk.org.siri.siri");
		}

		return netexJaxBContext;
	}

	@SuppressWarnings("unchecked")
	public PublicationDeliveryStructure unmarshal(File file, Set<QName> elementsToSkip) throws JAXBException, XMLStreamException, IOException, SAXException {
		JAXBContext netexJaxBContext = getNetexJaxBContext();
		Unmarshaller createUnmarshaller = netexJaxBContext.createUnmarshaller();
		JAXBElement<PublicationDeliveryStructure> commonDeliveryStructure = (JAXBElement<PublicationDeliveryStructure>) createUnmarshaller
				.unmarshal(SkippingXMLStreamReaderFactory.newXMLStreamReader(new BufferedInputStream(new FileInputStream(file)), elementsToSkip));
		return commonDeliveryStructure.getValue();
	}

	public XdmNode parseFileToXdmNode(File file, Set<QName> elementsToSkip) throws SaxonApiException, FileNotFoundException, IOException, SAXException, XMLStreamException {
        
        DocumentBuilder builder = processor.newDocumentBuilder();
        builder.setLineNumbering(true);
        builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);
        
		XdmNode dom = builder.build(new StAXSource(SkippingXMLStreamReaderFactory.newXMLStreamReader(new BufferedInputStream(new FileInputStream(file)), elementsToSkip)));
		return dom;
	}
	
	public synchronized XPathCompiler getXPathCompiler() {
        
		if(xpathCompiler == null) {
			xpathCompiler = processor.newXPathCompiler();
	        
			xpathCompiler.declareNamespace("", "http://www.netex.org.uk/netex"); // Default
			xpathCompiler.declareNamespace("n", "http://www.netex.org.uk/netex");
			xpathCompiler.declareNamespace("s", "http://www.siri.org.uk/siri");
			xpathCompiler.declareNamespace("g", "http://www.opengis.net/gml/3.2");
		}
		
		return xpathCompiler;
	}

}
