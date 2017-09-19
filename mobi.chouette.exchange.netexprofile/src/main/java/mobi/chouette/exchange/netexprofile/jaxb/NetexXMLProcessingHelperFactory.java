package mobi.chouette.exchange.netexprofile.jaxb;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stax.StAXSource;
import javax.xml.validation.Schema;

import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.validation.NeTExValidator;
import org.rutebanken.netex.validation.NeTExValidator.NetexVersion;
import org.xml.sax.SAXException;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.model.NetexProfileVersion;
import mobi.chouette.exchange.netexprofile.parser.xml.PublicactionDeliveryVersionAttributeReader;
import mobi.chouette.exchange.netexprofile.parser.xml.SkippingXMLStreamReaderFactory;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.WhitespaceStrippingPolicy;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XdmNode;

@Log4j
public class NetexXMLProcessingHelperFactory {
	private static Map<NetexVersion, Schema> netexSchema = new HashMap<>();

	private static JAXBContext netexJaxBContext = null;

	private static Processor processor = new Processor(false);

	private static XPathCompiler xpathCompiler;

	public synchronized Schema getNetexSchema(NetexVersion version) throws SAXException, IOException {

		Schema schema = netexSchema.get(version);
		
		if (schema == null) {
			log.info("Initializing Netex schema version "+version+", this may take a few seconds");
			schema = new NeTExValidator(version).getSchema();
			netexSchema.put(version, schema);
		}

		return schema;
	}

	public NetexVersion detectNetexSchemaVersion(File f) {
		String profileVersion = PublicactionDeliveryVersionAttributeReader.findPublicationDeliveryVersion(f);
		String netexSchemaVersion = NetexProfileVersion.getSchemaVersion(profileVersion);

		if (netexSchemaVersion != null) {
			switch (netexSchemaVersion) {
			case "1.04":
				return NetexVersion.V1_0_4beta;
			case "1.07":
				return NetexVersion.V1_0_7;
			default:

			}
		}
		return null;
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

	public XdmNode parseFileToXdmNode(File file, Set<QName> elementsToSkip)
			throws SaxonApiException, FileNotFoundException, IOException, SAXException, XMLStreamException {

		DocumentBuilder builder = processor.newDocumentBuilder();
		builder.setLineNumbering(true);
		builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);

		XdmNode dom = builder
				.build(new StAXSource(SkippingXMLStreamReaderFactory.newXMLStreamReader(new BufferedInputStream(new FileInputStream(file)), elementsToSkip)));
		return dom;
	}

	public synchronized XPathCompiler getXPathCompiler() {

		if (xpathCompiler == null) {
			xpathCompiler = processor.newXPathCompiler();
			xpathCompiler.setCaching(true);
			xpathCompiler.declareNamespace("", Constant.NETEX_NAMESPACE); // Default
			xpathCompiler.declareNamespace("n", Constant.NETEX_NAMESPACE);
			xpathCompiler.declareNamespace("s", Constant.SIRI_NAMESPACE);
			xpathCompiler.declareNamespace("g", Constant.OPENGIS_NAMESPACE);
		}

		return xpathCompiler;
	}

	public Marshaller createFragmentMarshaller() throws JAXBException {
		JAXBContext netexJaxBContext = getNetexJaxBContext();
		Marshaller marshaller = netexJaxBContext.createMarshaller();

		marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.name());
		marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

		return marshaller;
	}

	public static IndentingXMLStreamWriter createXMLWriter(Path filePath) throws XMLStreamException, IOException {
		Writer bufferedWriter = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8, CREATE, APPEND);
		XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
		XMLStreamWriter xmlStreamWriter = outputFactory.createXMLStreamWriter(bufferedWriter);
		xmlStreamWriter.setDefaultNamespace(Constant.NETEX_NAMESPACE);

		IndentingXMLStreamWriter writer = new IndentingXMLStreamWriter(new EscapingXMLStreamWriter(xmlStreamWriter));

		return writer;
	}

}
