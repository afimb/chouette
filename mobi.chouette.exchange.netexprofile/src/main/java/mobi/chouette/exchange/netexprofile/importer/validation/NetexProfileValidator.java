package mobi.chouette.exchange.netexprofile.importer.validation;

import mobi.chouette.common.Context;

import java.util.Collection;

public interface NetexProfileValidator {

	void validate(Context context) throws Exception;
	
	void initializeCheckPoints(Context context);

	Collection<String> getSupportedProfiles();
	
	boolean isCommonFileValidator();
	
	void addExternalReferenceValidator(ExternalReferenceValidator v);
}
