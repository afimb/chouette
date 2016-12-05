package mobi.chouette.exchange.netexprofile.importer;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.netexprofile.parser.xml.PredefinedSchemaListClasspathResourceResolver;

@Log4j
public class NetexImporter {
	private Schema netexSchema = null;

	private JAXBContext netexJaxBContext = null;

	public synchronized Schema getNetexSchema() throws SAXException, IOException {

		
		if (netexSchema == null) {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			factory.setResourceResolver(new PredefinedSchemaListClasspathResourceResolver("/netex_schema_list.txt"));

			Source schemaFile = new StreamSource(getClass().getResourceAsStream("/NeTEx-XML-1.04beta/schema/xsd/NeTEx_publication.xsd"));
			netexSchema = factory.newSchema(schemaFile);
		}

		return netexSchema;
	}

	public JAXBContext getNetexJaxBContext() throws JAXBException {
		if (netexJaxBContext == null) {
			netexJaxBContext = JAXBContext.newInstance("net.opengis.gml._3:org.rutebanken.netex.model:uk.org.siri.siri");
		}

		return netexJaxBContext;
	}

	public Document parseFileToDom(File f) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(f);

		return document;
	}

	@SuppressWarnings("unchecked")
	public PublicationDeliveryStructure unmarshal(File file) throws JAXBException {
		JAXBContext netexJaxBContext = getNetexJaxBContext();
		Unmarshaller createUnmarshaller = netexJaxBContext.createUnmarshaller();
		JAXBElement<PublicationDeliveryStructure> commonDeliveryStructure = (JAXBElement<PublicationDeliveryStructure>)
				createUnmarshaller.unmarshal(new StreamSource(file));
		return commonDeliveryStructure.getValue();
	}

	@SuppressWarnings("unchecked")
	public PublicationDeliveryStructure unmarshal(Document d) throws JAXBException {
		JAXBContext netexJaxBContext = getNetexJaxBContext();
		Unmarshaller createUnmarshaller = netexJaxBContext.createUnmarshaller();
		JAXBElement<PublicationDeliveryStructure> commonDeliveryStructure = (JAXBElement<PublicationDeliveryStructure>) createUnmarshaller
				.unmarshal(new DOMSource(d));
		return commonDeliveryStructure.getValue();
	}

}
