package fr.certu.chouette.exchange.netex;

import fr.certu.chouette.exchange.netex.exporter.NetexFileWriter;
import java.util.Iterator;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import org.apache.log4j.Logger;

public class NetexNamespaceContext implements NamespaceContext {
    private static final Logger logger = Logger.getLogger(NetexFileWriter.class);
    
    @Override
    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("No prefix provided!");
        } else if (prefix.equals("netex")) {
            return "http://www.netex.org.uk/netex";    
        } else if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
            return "http://www.netex.org.uk/netex";
        } else if ("acsb".equals(prefix)) {
            return "http://www.ifopt.org.uk/acsb";
        } else if ("ifopt".equals(prefix)) {
            return "http://www.ifopt.org.uk/ifopt";
        } else if ("gml".equals(prefix)) {
            return "http://www.opengis.net/gml/3.2";
        } else if ("siri".equals(prefix)) {
            return "http://www.siri.org.uk/siri";
        } else {
            return XMLConstants.NULL_NS_URI;
        }        
    }
   
    @Override
    public String getPrefix(String namespaceURI) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public Iterator getPrefixes(String namespaceURI) {
        throw new IndexOutOfBoundsException();
    }
}
