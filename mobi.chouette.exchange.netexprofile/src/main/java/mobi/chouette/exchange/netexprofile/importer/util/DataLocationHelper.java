package mobi.chouette.exchange.netexprofile.importer.util;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.validation.report.DataLocation;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;

public class DataLocationHelper {
	public static DataLocation findDataLocation(IdVersion id) {
		DataLocation d = new DataLocation(id.getFilename(),id.getLineNumber(),id.getColumnNumber(),id.getId());
		return d;
	}

	public static DataLocation findDataLocation(String commonFileName, IdVersion id) {
		DataLocation d = new DataLocation(commonFileName,id.getLineNumber(),id.getColumnNumber(),id.getId());
		return d;
	}

	public static DataLocation findDataLocation(Context context, XdmItem item) {
		return findDataLocation(context, (XdmNode)item);
	}
	
	public static DataLocation findDataLocation(Context context, XdmNode item) {
		String filename = (String) context.get(Constant.FILE_NAME);
		int lineNumber = item.getLineNumber();
		int columnNumber = item.getColumnNumber();
		
		String attributeValue = item.getAttributeValue(new QName("id"));
		DataLocation d = new DataLocation(filename,  lineNumber ,  columnNumber , attributeValue);

		return d;
	}
}
