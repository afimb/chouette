package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import java.util.HashSet;
import java.util.Set;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.importer.util.IdVersion;
import mobi.chouette.exchange.netexprofile.importer.validation.ExternalReferenceValidator;

public class ServiceJourneyInterchangeIgnorer implements ExternalReferenceValidator{

	@Override
	public Set<IdVersion> validateReferenceIds(Context context, Set<IdVersion> externalIdsToValidate) {

		// All references of supported type should be returned as validated
		
		Set<IdVersion> ignoredReferences = new HashSet<>(externalIdsToValidate);
		ignoredReferences.retainAll(isOfSupportedTypes(externalIdsToValidate));
		
		return ignoredReferences;
		
	}

	@Override
	public Set<IdVersion> isOfSupportedTypes(Set<IdVersion> references) {

		Set<IdVersion> supportedTypes =new HashSet<>();
		
		for(IdVersion ref : references) {
			if("ToJourneyRef".equals(ref.getElementName()) && ref.getId().contains("ServiceJourney")) {
				supportedTypes.add(ref);
			}
			else if("FromJourneyRef".equals(ref.getElementName()) && ref.getId().contains("ServiceJourney")) {
				supportedTypes.add(ref);
			}
		}
		
		return supportedTypes;
	}

}
