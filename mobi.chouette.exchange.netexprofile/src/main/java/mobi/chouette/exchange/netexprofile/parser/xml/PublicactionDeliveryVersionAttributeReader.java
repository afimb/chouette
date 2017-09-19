package mobi.chouette.exchange.netexprofile.parser.xml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import lombok.extern.log4j.Log4j;

import javax.xml.stream.events.Attribute;

@Log4j
public class PublicactionDeliveryVersionAttributeReader {
	public static String findPublicationDeliveryVersion(File f) {

		String versionAttribute = null;
		try {
			// First create a new XMLInputFactory
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			// Setup a new eventReader
			InputStream in = new FileInputStream(f);
			XMLEventReader eventReader = inputFactory.createXMLEventReader(new BufferedInputStream(in));
			// Read the XML document

			while (versionAttribute == null && eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();

				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();

					if (startElement.getName().getLocalPart().equals("PublicationDelivery")) {

						@SuppressWarnings("unchecked")
						Iterator<Attribute> attributes = event.asStartElement().getAttributes();
						while (attributes.hasNext()) {
							Attribute attribute = attributes.next();
							if (attribute.getName().toString().equals("version")) {
								versionAttribute = attribute.getValue();
							}
						}

					}

				}
			}
			eventReader.close();
			in.close();
		} catch (FileNotFoundException e) {
			log.error("Could not find file "+f.getAbsolutePath());
		} catch (XMLStreamException e) {
			log.error("Malformed xml",e);
		} catch (IOException e) {
			log.error("Error closing file",e);
		}

		return versionAttribute;
	}

}
