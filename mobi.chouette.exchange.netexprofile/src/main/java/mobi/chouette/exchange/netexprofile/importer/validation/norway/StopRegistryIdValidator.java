package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.netexprofile.importer.validation.ExternalReferenceValidator;

import java.util.Collection;
import java.util.Collections;

@Log4j
public class StopRegistryIdValidator implements ExternalReferenceValidator {

	@Override
	public Collection<String> validateReferenceIds(Collection<String> externalIds) {

		for (String s : externalIds) {
			log.warn("Should validate id " + s + " externally, but not implemented");
		}

		return Collections.emptyList();
	}

}
