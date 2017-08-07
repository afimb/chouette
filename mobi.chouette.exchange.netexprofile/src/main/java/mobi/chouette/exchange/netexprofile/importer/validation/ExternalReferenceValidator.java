package mobi.chouette.exchange.netexprofile.importer.validation;

import java.util.Set;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.importer.util.IdVersion;

public interface ExternalReferenceValidator {

	/**
	 * Return a collection of IDs that this external reference validator could validate.
	 * @param context 
	 * 
	 * @return the IDs that were validated
	 */
	public Set<IdVersion> validateReferenceIds(Context context, Set<IdVersion> externalIdsToValidate);
	
	/**
	 * Returns the set of ids this external validator supports to validate
	 * @param references
	 * @return
	 */
	public Set<IdVersion> isOfSupportedTypes(Set<IdVersion> references);
	
}
