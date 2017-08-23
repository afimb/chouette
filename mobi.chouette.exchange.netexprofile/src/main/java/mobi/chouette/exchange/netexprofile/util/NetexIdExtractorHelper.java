package mobi.chouette.exchange.netexprofile.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.xpath.XPathExpressionException;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.util.IdVersion;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;

public class NetexIdExtractorHelper {
	public static List<IdVersion> collectEntityIdentificators(Context context, XPathCompiler xpath, XdmNode dom, Set<String> ignorableElementNames)
			throws XPathExpressionException, SaxonApiException {
		return collectIdOrRefWithVersion(context, xpath, dom, "id", ignorableElementNames);
	}

	public static List<IdVersion> collectEntityReferences(Context context, XPathCompiler xpath, XdmNode dom, Set<String> ignorableElementNames)
			throws XPathExpressionException, SaxonApiException {
		return collectIdOrRefWithVersion(context, xpath, dom, "ref", ignorableElementNames);
	}

	public static List<IdVersion> collectIdOrRefWithVersion(Context context, XPathCompiler xpath, XdmNode dom, String attributeName, Set<String> ignorableElementNames)
			throws XPathExpressionException, SaxonApiException {
		StringBuilder filterClause = new StringBuilder();
		filterClause.append("//n:*[");
		if (ignorableElementNames != null) {
			for (String elementName : ignorableElementNames) {
				filterClause.append("not(local-name(.)='" + elementName + "') and ");
			}
		}
		filterClause.append("@" + attributeName + "]");

		XPathSelector selector = xpath.compile(filterClause.toString()).load();
		selector.setContextItem(dom);
		XdmValue nodes = selector.evaluate();

		
		List<IdVersion> ids = new ArrayList<IdVersion>();
		for (XdmItem item : nodes) {
			XdmNode n = (XdmNode)item;
			String elementName = n.getNodeName().getLocalName();
			
			String id = n.getAttributeValue(new QName(attributeName));
			String version = n.getAttributeValue(new QName("version"));;
			ids.add(new IdVersion(id, version, elementName, (String) context.get(Constant.FILE_NAME),
					n.getLineNumber(), (Integer) n.getColumnNumber()));

		}
		return ids;
	}
	
	


}
