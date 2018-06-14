package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import java.util.HashSet;
import java.util.Set;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.importer.util.IdVersion;
import mobi.chouette.exchange.netexprofile.importer.validation.ExternalReferenceValidator;

import com.google.common.collect.Sets;

public class BlockJourneyReferencesIgnorerer implements ExternalReferenceValidator {

	private static final Set<String> JOURNEY_REF_TYPES = Sets.newHashSet("JourneyRef", "VehicleJourneyRef", "ServiceJourneyRef", "DeadRunRef");

	@Override
	public Set<IdVersion> validateReferenceIds(Context context, Set<IdVersion> externalIdsToValidate) {

		// All references of supported type should be returned as validated
		Set<IdVersion> ignoredReferences = new HashSet<>(externalIdsToValidate);
		ignoredReferences.retainAll(isOfSupportedTypes(externalIdsToValidate));

		return ignoredReferences;

	}

	@Override
	public Set<IdVersion> isOfSupportedTypes(Set<IdVersion> references) {
		Set<IdVersion> supportedTypes = new HashSet<>();
		for (IdVersion ref : references) {
			if (JOURNEY_REF_TYPES.contains(ref.getElementName()) && (ref.getId().contains("DeadRun") || ref.getId().contains("ServiceJourney")) && ref.getParentElementNames().contains("Block")) {
				supportedTypes.add(ref);
			}
		}

		return supportedTypes;
	}
}
