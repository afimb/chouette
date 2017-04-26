package mobi.chouette.exchange.netexprofile.importer.validation;

import java.util.Set;

public interface ExternalReferenceValidator {

	/**
	 * Return a collection of IDs that this external reference validator could validate.
	 * 
	 * @return the IDs that were validated OK
	 */
	public Set<String> validateReferenceIds(Set<String> externalIdsToValidate);
	
}
