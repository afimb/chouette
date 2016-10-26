package mobi.chouette.exchange.netexprofile.importer.validation;

import java.util.Collection;

public interface ExternalReferenceValidator {
	
	public Collection<String> validateReferenceIds(Collection<String> externalIds);
}
