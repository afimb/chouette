package mobi.chouette.exchange.netexprofile.importer.validation;

import javax.xml.xpath.XPathExpressionException;

import mobi.chouette.common.Context;

public interface NetexProfileValidator {

	void addCheckpoints(Context context);
	
	boolean validate(Context context) throws XPathExpressionException;

}
