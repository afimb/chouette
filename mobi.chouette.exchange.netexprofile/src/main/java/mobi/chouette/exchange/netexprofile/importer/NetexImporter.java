package mobi.chouette.exchange.netexprofile.importer;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexProfileValidator;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexProfileValidatorFactory;
import mobi.chouette.exchange.netexprofile.importer.validation.norway.NorwayLineNetexProfileValidator;
import mobi.chouette.exchange.netexprofile.parser.xml.PredefinedSchemaListClasspathResourceResolver;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

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
import java.io.File;
import java.io.IOException;

@Log4j
public class NetexImporter {
	private Schema netexSchema = null;

	private JAXBContext netexJaxBContext = null;

	public Schema getNetexSchema() throws SAXException, IOException {

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

	public NetexProfileValidator getProfileValidator(Context context) throws Exception {
		log.warn("Profile validator selector not implemented, always returning NorwayLineNetexProfileValidator");

		NetexprofileImportParameters configuration = (NetexprofileImportParameters) context.get(Constant.CONFIGURATION);
		NetexProfileValidator profileValidator;

		switch (configuration.getProfileId()) {
		// TODO add correct ids. for now defaulting to NorwayLine
		case "norway-yadi-yadi":
		default:
			profileValidator = (NorwayLineNetexProfileValidator) NetexProfileValidatorFactory.create(
					NorwayLineNetexProfileValidator.class.getName(), context);
		}
		return profileValidator;
	}

}
