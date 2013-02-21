package fr.certu.chouette.exchange.netex;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

public class NetexNamespaceContext implements NamespaceContext {

    @Override
    public String getNamespaceURI(String prefix) {
        if ("netex".equals(prefix)) {
            return "http://www.netex.org.uk/netex";
        } else if ("acsb".equals(prefix)) {
            return "http://www.ifopt.org.uk/acsb";
        } else if ("ifopt".equals(prefix)) {
            return "http://www.ifopt.org.uk/ifopt";
        } else if ("gml".equals(prefix)) {
            return "http://www.opengis.net/gml/3.2";
        } else if ("siri".equals(prefix)) {
            return "http://www.siri.org.uk/siri";
        }
        return null;
    }

    public String getPrefix(String namespaceURI) {
        return null;
    }

    public Iterator getPrefixes(String namespaceURI) {
        return null;
    }
}
