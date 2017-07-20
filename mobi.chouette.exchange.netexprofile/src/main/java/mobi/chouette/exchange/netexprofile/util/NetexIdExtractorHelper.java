package mobi.chouette.exchange.netexprofile.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.PositionalXMLReader;
import mobi.chouette.exchange.netexprofile.importer.util.IdVersion;

public class NetexIdExtractorHelper {
	public static List<IdVersion> collectEntityIdentificators(Context context, XPath xpath, Document dom, Set<String> ignorableElementNames)
			throws XPathExpressionException {
		return collectIdOrRefWithVersion(context, xpath, dom, "id", ignorableElementNames);
	}

	public static List<IdVersion> collectEntityReferences(Context context, XPath xpath, Document dom, Set<String> ignorableElementNames)
			throws XPathExpressionException {
		return collectIdOrRefWithVersion(context, xpath, dom, "ref", ignorableElementNames);
	}

	public static List<IdVersion> collectIdOrRefWithVersion(Context context, XPath xpath, Document dom, String attributeName, Set<String> ignorableElementNames)
			throws XPathExpressionException {
		StringBuilder filterClause = new StringBuilder();
		filterClause.append("//n:*[");
		if (ignorableElementNames != null) {
			for (String elementName : ignorableElementNames) {
				filterClause.append("not(name()='" + elementName + "') and ");
			}
		}
		filterClause.append("@" + attributeName + "]");

		NodeList nodes = (NodeList) xpath.evaluate(filterClause.toString(), dom, XPathConstants.NODESET);
		List<IdVersion> ids = new ArrayList<IdVersion>();
		int idCount = nodes.getLength();
		for (int i = 0; i < idCount; i++) {
			Node n = nodes.item(i);
			String elementName = n.getNodeName();
			String id = n.getAttributes().getNamedItem(attributeName).getNodeValue();
			String version = null;
			Node versionAttribute = n.getAttributes().getNamedItem("version");
			if (versionAttribute != null) {
				version = versionAttribute.getNodeValue();
			}
			ids.add(new IdVersion(id, version, elementName, (String) context.get(Constant.FILE_NAME),
					(Integer) n.getUserData(PositionalXMLReader.LINE_NUMBER_KEY_NAME), (Integer) n.getUserData(PositionalXMLReader.COLUMN_NUMBER_KEY_NAME)));

		}
		return ids;
	}
	
	


}
