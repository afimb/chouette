package mobi.chouette.exchange.netexprofile.importer.validation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

public class NetexNamespaceContext implements NamespaceContext {

	private Map<String, String> prefixToNamespace = new HashMap<String, String>();
	private Map<String, String> namespaceToPrefix = new HashMap<String, String>();

	public NetexNamespaceContext() {
		prefixToNamespace.put("n", "http://www.netex.org.uk/netex");
		prefixToNamespace.put("s", "http://www.siri.org.uk/siri");
		prefixToNamespace.put("g", "http://www.opengis.net/gml/3.2");

		namespaceToPrefix.put("http://www.netex.org.uk/netex", "n");
		namespaceToPrefix.put("http://www.siri.org.uk/siri", "s");
		namespaceToPrefix.put("http://www.opengis.net/gml/3.2", "g");
	}

	@Override
	public String getNamespaceURI(String prefix) {
		return prefixToNamespace.get(prefix);
	}

	@Override
	public String getPrefix(String namespaceURI) {
		return namespaceToPrefix.get(namespaceURI);
	}

	@Override
	public Iterator<String> getPrefixes(String namespaceURI) {
		return prefixToNamespace.keySet().iterator();
	}
}