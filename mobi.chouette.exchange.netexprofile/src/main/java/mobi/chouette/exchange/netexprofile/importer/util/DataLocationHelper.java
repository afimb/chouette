package mobi.chouette.exchange.netexprofile.importer.util;

import org.w3c.dom.Node;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.PositionalXMLReader;
import mobi.chouette.exchange.validation.report.DataLocation;

public class DataLocationHelper {
	public static DataLocation findDataLocation(String filename, Node p) {

		Integer lineNumber = (Integer) p.getUserData(PositionalXMLReader.LINE_NUMBER_KEY_NAME);
		Integer columnNumber = (Integer) p.getUserData(PositionalXMLReader.COLUMN_NUMBER_KEY_NAME);
		
		Node idAttribute = null;
		if(p.hasAttributes()) {
			idAttribute = p.getAttributes().getNamedItem("id");
		}
		DataLocation d = new DataLocation(filename, lineNumber != null ? lineNumber : -1, columnNumber != null ? columnNumber : -1, idAttribute != null? idAttribute.getNodeValue() : null);

		return d;
	}

	public static DataLocation findDataLocation(Context context, Node p) {
		return findDataLocation((String) context.get(Constant.FILE_NAME), p);
	}
		

	public static DataLocation findDataLocation(IdVersion id) {
		DataLocation d = new DataLocation(id.getFilename(),id.getLineNumber(),id.getColumnNumber(),id.getId());
		return d;
	}

	public static DataLocation findDataLocation(String commonFileName, IdVersion id) {
		DataLocation d = new DataLocation(commonFileName,id.getLineNumber(),id.getColumnNumber(),id.getId());
		return d;
	}
}
