package mobi.chouette.exchange.netexprofile.importer.validation;

import java.util.Collection;

import mobi.chouette.common.Context;

public interface NetexProfileValidator {

	void validate(Context context) throws Exception;
	
	void initializeCheckPoints(Context context);

	Collection<String> getSupportedProfiles();
	
	boolean isCommonFileValidator();

}
