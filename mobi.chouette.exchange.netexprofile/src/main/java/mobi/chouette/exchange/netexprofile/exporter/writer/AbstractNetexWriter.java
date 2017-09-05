package mobi.chouette.exchange.netexprofile.exporter.writer;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import mobi.chouette.exchange.netexprofile.importer.validation.norway.AbstractNorwayNetexProfileValidator;

public class AbstractNetexWriter {

    static final String NETEX_PROFILE_VERSION = AbstractNorwayNetexProfileValidator.EXPORT_PROFILE_ID;
    static final String DEFAULT_ZONE_ID = "UTC";
    static final String DEFAULT_LANGUAGE_CODE = "no";
    static final String NSR_XMLNS = "NSR";
	static final String PARTICIPANT_REF_CONTENT = "RB";



    static final String VERSION = "version";
    static final String ID = "id";
    static final String CREATED = "created";
    static final String XMLNS = "Xmlns";
    static final String XMLNSURL = "XmlnsUrl";
    
    final static DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .optionalStart().appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true).optionalEnd()
            .optionalStart().appendPattern("XXXXX")
            .optionalEnd()
            .parseDefaulting(ChronoField.OFFSET_SECONDS, OffsetDateTime.now().getLong(ChronoField.OFFSET_SECONDS) ).toFormatter();

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
