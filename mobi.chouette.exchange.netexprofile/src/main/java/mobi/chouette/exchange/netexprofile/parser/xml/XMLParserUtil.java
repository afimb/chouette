package mobi.chouette.exchange.netexprofile.parser.xml;

import javax.xml.stream.XMLInputFactory;

public final class XMLParserUtil {

    private XMLParserUtil() {
    }

    public static XMLInputFactory getSecureXmlInputFactory() {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
        return factory;
    }

}
