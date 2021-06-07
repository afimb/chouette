package mobi.chouette.exchange.netexprofile.exporter.writer;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.validation.norway.AbstractNorwayNetexProfileValidator;

public class AbstractNetexWriter  implements Constant {

    static final String NETEX_PROFILE_VERSION = AbstractNorwayNetexProfileValidator.EXPORT_PROFILE_ID;
    static final String DEFAULT_ZONE_ID = "Europe/Oslo";
    static final String DEFAULT_LANGUAGE_CODE = "no";
    static final String NSR_XMLNS = "NSR";
	static final String PARTICIPANT_REF_CONTENT = "RB";

    static final String CREATED = "created";
    static final String XMLNS = "Xmlns";
    static final String XMLNSURL = "XmlnsUrl";

    static void writeElement(XMLStreamWriter writer, String element, String value) {
        try {
            writer.writeStartElement(element);
            writer.writeCharacters(value);
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

}
