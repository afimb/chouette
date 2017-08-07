package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.importer.util.IdVersion;
import mobi.chouette.exchange.netexprofile.importer.validation.ExternalReferenceValidator;

public class DummyStopReferentialIdValidator implements ExternalReferenceValidator {

	public DummyStopReferentialIdValidator() {
	}

	@Override
	public Set<IdVersion> validateReferenceIds(Context context, Set<IdVersion> externalIds) {

		// Return all of supported type
		return new HashSet<IdVersion>();
	}

	@Override
	public Set<IdVersion> isOfSupportedTypes(Set<IdVersion> externalIds) {
		return externalIds.stream().filter(e -> e.getId().contains(":Quay:") || e.getId().contains(":StopPlace:")).collect(Collectors.toSet());
	}

}
