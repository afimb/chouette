package mobi.chouette.exchange.netexprofile.jaxb;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.netexprofile.parser.xml.PredefinedSchemaListClasspathResourceResolver;
import org.rutebanken.netex.model.Authority;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.OrganisationTypeEnumeration;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

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

    public void write(JAXBElement<PublicationDeliveryStructure> rootObject, File file) throws JAXBException, IOException, XMLStreamException {
        //write(rootObject, new FileOutputStream(file));
        //write(rootObject, new BufferedOutputStream(new FileOutputStream(file), 4096));
        write(new BufferedOutputStream(new FileOutputStream(file), 4096));
    }

    public void write(OutputStream stream) throws JAXBException, IOException, XMLStreamException {
        //File file = new File("/Users/swirzen/dev/temp/PublicationDelivery.xml");
        //BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file), 4096);

        XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
        //outputFactory.setProperty("javax.xml.stream.isRepairingNamespaces", Boolean.TRUE);
        //XMLStreamWriter streamWriter = outputFactory.createXMLStreamWriter(System.out, "UTF-8");
        XMLStreamWriter streamWriter = outputFactory.createXMLStreamWriter(stream, "UTF-8");
        //streamWriter.setNamespaceContext(namespaces);

        ObjectFactory objectFactory = new ObjectFactory();

        Authority nsrAuthority = objectFactory.createAuthority()
                .withVersion("1")
                .withId("AVI:Authority:NSR")
                .withOrganisationType(OrganisationTypeEnumeration.AUTHORITY);
        JAXBElement<Authority> nsrAuthorityJaxb = objectFactory.createAuthority(nsrAuthority);

        Authority avinorAuthority = objectFactory.createAuthority()
                .withVersion("")
                .withId("AVI:Authority:Avinor")
                .withOrganisationType(OrganisationTypeEnumeration.AUTHORITY);
        JAXBElement<Authority> avinorAuthorityJaxb = objectFactory.createAuthority(avinorAuthority);

        List<JAXBElement<Authority>> authorities = Arrays.asList(nsrAuthorityJaxb, avinorAuthorityJaxb);

        JAXBContext context = JAXBContext.newInstance(Authority.class);
        Marshaller marshaller = context.createMarshaller();

        NamespacePrefixMapper mapper = new NetexNamespacePrefixMapper();
        marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", mapper);

        //marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new PetascopeXmlNamespaceMapper());
        //marshaller.setProperty("jaxb.formatted.output", true);
        //marshaller.setProperty("jaxb.schemaLocation", "http://www.opengis.net/wcs/1.1 http://schemas.opengis.net/wcs/1.1.0/wcsGetCapabilities.xsd " + "http://www.opengis.net/wcs/1.1/ows http://schemas.opengis.net/wcs/1.1.0/owsGetCapabilities.xsd");

        //marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8");
        //marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

        streamWriter.writeStartDocument("UTF-8", "1.0");
        streamWriter.writeStartElement("PublicationDelivery");
        streamWriter.writeDefaultNamespace("http://www.netex.org.uk/netex");
        streamWriter.writeAttribute("version", "1.04:NO-NeTEx-networktimetable:1.0");

        streamWriter.writeStartElement("dataObjects");

        streamWriter.writeStartElement("CompositeFrame");
        streamWriter.writeAttribute("created", "2017-03-29T00:00:00.000Z");
        streamWriter.writeAttribute("version", "1");
        streamWriter.writeAttribute("id", "AVI:CompositeFrame:WF-TRD-MOL");

        streamWriter.writeStartElement("frames");
        streamWriter.writeStartElement("ResourceFrame");
        streamWriter.writeAttribute("version", "1");
        streamWriter.writeAttribute("id", "AVI:ResourceFrame:8356908");

        streamWriter.writeStartElement("organisations");

        for (JAXBElement<Authority> authority : authorities) {
            marshaller.marshal(authority, streamWriter);
            stream.flush();
        }

        streamWriter.writeEndElement(); // end organisations
        streamWriter.writeEndElement(); // end ResourceFrame
        streamWriter.writeEndElement(); // end frames
        streamWriter.writeEndElement(); // end CompositeFrame
        streamWriter.writeEndElement(); // end dataObjects
        streamWriter.writeEndElement(); // end PublicationDelivery

        streamWriter.writeEndDocument();
        streamWriter.flush();
        streamWriter.close();
    }

    public void write(JAXBElement<PublicationDeliveryStructure> network, OutputStream stream) throws JAXBException, IOException, XMLStreamException {
        try {
            Marshaller marshaller = context.createMarshaller();
            marshaller.setSchema(schema);
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); // NOI18N
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setEventHandler(new JaxbNetexFileConverter.NetexValidationEventHandler());
            NamespacePrefixMapper mapper = new NetexNamespacePrefixMapper();
            marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", mapper);
            //marshaller.marshal(network, stream);

            XMLStreamWriter streamWriter = XMLOutputFactory.newFactory().createXMLStreamWriter(new StringWriter());

            streamWriter.writeStartDocument("UTF-8", "1.0");
            streamWriter.writeStartElement("PublicationDelivery");
            streamWriter.writeDefaultNamespace("http://www.netex.org.uk/netex");
            streamWriter.writeAttribute("version", "1.04:NO-NeTEx-networktimetable:1.0");
            streamWriter.writeEndElement();
            streamWriter.writeEndDocument();
            streamWriter.close();
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
