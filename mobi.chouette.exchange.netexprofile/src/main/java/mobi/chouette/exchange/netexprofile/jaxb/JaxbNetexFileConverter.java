package mobi.chouette.exchange.netexprofile.jaxb;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.netexprofile.parser.xml.PredefinedSchemaListClasspathResourceResolver;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;

@Log4j
public class JaxbNetexFileConverter {

    private JAXBContext context = null;

    private Schema schema = null;

    private static JaxbNetexFileConverter instance = null;

    public static JaxbNetexFileConverter getInstance() throws Exception {
        if (instance == null) {
            instance = new JaxbNetexFileConverter();
        }
        return instance;
    }

    // TODO split up by context and schema init, and cache both instances in global context
    private JaxbNetexFileConverter() throws JAXBException, SAXException, URISyntaxException, IOException {
        //context = JAXBContext.newInstance(PublicationDeliveryStructure.class);
        context = JAXBContext.newInstance("net.opengis.gml._3:org.rutebanken.netex.model:uk.org.siri.siri");

        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setResourceResolver(new PredefinedSchemaListClasspathResourceResolver("/netex_schema_list.txt"));
        Source schemaFile = new StreamSource(getClass().getResourceAsStream("/NeTEx-XML-1.04beta/schema/xsd/NeTEx_publication.xsd"));
        schema = schemaFactory.newSchema(schemaFile);
    }

/*
    public Schema getNetexSchema() throws SAXException, IOException {
        if (netexSchema == null) {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            factory.setResourceResolver(new PredefinedSchemaListClasspathResourceResolver("/netex_schema_list.txt"));

            Source schemaFile = new StreamSource(getClass().getResourceAsStream("/NeTEx-XML-1.04beta/schema/xsd/NeTEx_publication.xsd"));
            netexSchema = factory.newSchema(schemaFile);
        }

        return netexSchema;
    }
*/

/*
    public JAXBContext getNetexJaxBContext() throws JAXBException {
        if (netexJaxBContext == null) {
            netexJaxBContext = JAXBContext.newInstance("net.opengis.gml._3:org.rutebanken.netex.model:uk.org.siri.siri");
        }
        return netexJaxBContext;
    }
*/

    public void write(JAXBElement<PublicationDeliveryStructure> rootObject, File file) throws JAXBException, IOException {
        write(rootObject, new FileOutputStream(file));
    }

    public void write(JAXBElement<PublicationDeliveryStructure> network, OutputStream stream) throws JAXBException, IOException {
        try {
            Marshaller marshaller = context.createMarshaller();
            marshaller.setSchema(schema);
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); // NOI18N
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setEventHandler(new JaxbNetexFileConverter.NetexValidationEventHandler());
            NamespacePrefixMapper mapper = new NetexNamespacePrefixMapper();
            marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", mapper);
            marshaller.marshal(network, stream);
        } finally {
            stream.close();
        }
    }

    private class NetexNamespacePrefixMapper extends NamespacePrefixMapper {
        private static final String NETEX_PREFIX = ""; // DEFAULT NAMESPACE
        private static final String NETEX_URI = "http://www.netex.org.uk/netex";

        private static final String GML_PREFIX = "gml";
        private static final String GML_URI = "http://www.opengis.net/gml/3.2";

        private static final String SIRI_PREFIX = "siri";
        private static final String SIRI_URI = "http://www.siri.org.uk/siri";

        @Override
        public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
            if (NETEX_URI.equals(namespaceUri)) {
                return NETEX_PREFIX;
            } else if (GML_URI.equals(namespaceUri)) {
                return GML_PREFIX;
            } else if (SIRI_URI.equals(namespaceUri)) {
                return SIRI_PREFIX;
            }
            return suggestion;
        }
    }

    private class NetexValidationEventHandler implements ValidationEventHandler {

        @Override
        public boolean handleEvent(ValidationEvent event) {
            switch (event.getSeverity()) {
                case ValidationEvent.FATAL_ERROR:
                    return false;
                case ValidationEvent.ERROR:
                case ValidationEvent.WARNING:
                    log.warn(event.getMessage());
                    break;
            }
            return false;
        }
    }

}
