package mobi.chouette.exchange.netexprofile.exporter;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.Line;

public class ExportedFilenamer {
	private static final String SPACE = " ";
	private static final String UNDERSCORE = "_";
	private static final String DASH = "-";

	public static String createSharedDataFilename(Context context) {
        NetexprofileExportParameters parameters = (NetexprofileExportParameters) context.get(Constant.CONFIGURATION);

        StringBuilder b = new StringBuilder();
        b.append(UNDERSCORE);
        b.append(parameters.getDefaultCodespacePrefix());
        b.append("_shared_data.xml");
        
        return b.toString();
	}
	
	public static String createLineFilename(Context context, Line line) {
        NetexprofileExportParameters parameters = (NetexprofileExportParameters) context.get(Constant.CONFIGURATION);

        StringBuilder b = new StringBuilder();
        b.append(parameters.getDefaultCodespacePrefix());
        b.append(UNDERSCORE);
        b.append(line.getObjectId().replaceAll(":", DASH));
        b.append(UNDERSCORE);
        if(line.getNumber() != null) {
        	b.append(line.getNumber().replaceAll(UNDERSCORE, DASH));
            b.append(UNDERSCORE);
        }
        if(line.getName() != null) {
        	b.append(line.getName());
        } else if (line.getPublishedName() != null) {
        	b.append(line.getPublishedName());
        }
       
        return b.toString().replaceAll("/", DASH).replace(SPACE, DASH).replaceAll("\\.",DASH) + ".xml";
	}
}
