package mobi.chouette.exchange.netexprofile.parser.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class SkippingSAXSourceFactory {

	public static SAXSource newSAXSource(final InputStream is, Set<QName> elementsToSkip) throws IOException, SAXException {

		SAXParser parser;
		
		try {
			final SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			parser = factory.newSAXParser();

		} catch (final ParserConfigurationException e) {
			throw new RuntimeException("Can't create SAX parser / DOM builder.", e);
		}

		XMLReader xmlReader = parser.getXMLReader();
		xmlReader.setContentHandler(new SkippingElementContentHandler(xmlReader, elementsToSkip));
		
		return new SAXSource(xmlReader, new InputSource(is));
	}
	
}