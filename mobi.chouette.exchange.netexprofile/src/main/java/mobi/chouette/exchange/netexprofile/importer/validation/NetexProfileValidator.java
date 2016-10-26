package mobi.chouette.exchange.netexprofile.importer.validation;

import javax.xml.xpath.XPathExpressionException;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;

public interface NetexProfileValidator extends Constant {

	void addCheckpoints(Context context);
	
	boolean validate(Context context) throws Exception;

}
