package mobi.chouette.exchange.netexprofile.parser.xml;

import java.io.InputStream;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class SkippingXMLStreamReaderFactory {

	public static XMLStreamReader newXMLStreamReader(final InputStream is, final Set<QName> elementsToSkip) throws XMLStreamException {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader xmlStreamReader = factory.createXMLStreamReader(is);
		if (elementsToSkip.size() == 0) {
			return xmlStreamReader;
		} else {
			XMLStreamReader fileteredStreamReader = factory.createFilteredReader(xmlStreamReader, new StreamFilter() {

				private boolean accept = true;
				
				private QName rootSkipStart = null;
				
				@Override
				public boolean accept(XMLStreamReader reader) {
					if(reader.isStartElement() && accept && elementsToSkip.contains(reader.getName())) {
						if(rootSkipStart == null) {
							rootSkipStart = reader.getName();
						}
						accept = false;
						return false;
					} else if(reader.isEndElement() && !accept && elementsToSkip.contains(reader.getName())) {
						if(rootSkipStart.equals(reader.getName())) {
							rootSkipStart = null;
							accept = true;
							return false;
						}
					}
					return accept;
				}
			});

			return fileteredStreamReader;
		}
	}

}