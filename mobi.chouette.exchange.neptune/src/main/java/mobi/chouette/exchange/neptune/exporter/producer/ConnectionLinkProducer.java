package mobi.chouette.exchange.neptune.exporter.producer;

import java.util.ArrayList;
import java.util.List;

import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.type.ConnectionLinkTypeEnum;
import mobi.chouette.model.type.UserNeedEnum;

import org.trident.schema.trident.ChouettePTNetworkType;
import org.trident.schema.trident.ConnectionLinkExtensionType;
import org.trident.schema.trident.ConnectionLinkExtensionType.AccessibilitySuitabilityDetails;
import org.trident.schema.trident.ConnectionLinkTypeType;

import uk.org.ifopt.acsb.EncumbranceEnumeration;
import uk.org.ifopt.acsb.MedicalNeedEnumeration;
import uk.org.ifopt.acsb.MobilityEnumeration;
import uk.org.ifopt.acsb.PyschosensoryNeedEnumeration;
import uk.org.ifopt.acsb.UserNeedStructure;

public class ConnectionLinkProducer extends
		AbstractJaxbNeptuneProducer<ChouettePTNetworkType.ConnectionLink, ConnectionLink> {

	// @Override
	public ChouettePTNetworkType.ConnectionLink produce(ConnectionLink connectionLink, boolean addExtension) {
		ChouettePTNetworkType.ConnectionLink jaxbConnectionLink = tridentFactory
				.createChouettePTNetworkTypeConnectionLink();

		//
		populateFromModel(jaxbConnectionLink, connectionLink);

		jaxbConnectionLink.setComment(getNotEmptyString(connectionLink.getComment()));
		jaxbConnectionLink.setName(connectionLink.getName());
		if (connectionLink.getStartOfLink() != null)
			jaxbConnectionLink.setStartOfLink(connectionLink.getStartOfLink().getObjectId());
		if (connectionLink.getEndOfLink() != null)
			jaxbConnectionLink.setEndOfLink(connectionLink.getEndOfLink().getObjectId());
		jaxbConnectionLink.setLinkDistance(connectionLink.getLinkDistance());
		if (connectionLink.getMobilityRestrictedSuitable() != null) {
			jaxbConnectionLink.setMobilityRestrictedSuitability(connectionLink.getMobilityRestrictedSuitable()
					.booleanValue());
		}
		if (connectionLink.getLiftAvailable() != null) {
			jaxbConnectionLink.setLiftAvailability(connectionLink.getLiftAvailable().booleanValue());
		}
		if (connectionLink.getStairsAvailable() != null) {
			jaxbConnectionLink.setStairsAvailability(connectionLink.getStairsAvailable().booleanValue());
		}
		if (connectionLink.getDefaultDuration() != null) {
			jaxbConnectionLink.setDefaultDuration(toDuration(connectionLink.getDefaultDuration()));
		}
		if (connectionLink.getFrequentTravellerDuration() != null) {
			jaxbConnectionLink.setFrequentTravellerDuration(toDuration(connectionLink.getFrequentTravellerDuration()));
		}
		if (connectionLink.getOccasionalTravellerDuration() != null) {
			jaxbConnectionLink.setOccasionalTravellerDuration(toDuration(connectionLink
					.getOccasionalTravellerDuration()));
		}
		if (connectionLink.getMobilityRestrictedTravellerDuration() != null) {
			jaxbConnectionLink.setMobilityRestrictedTravellerDuration(toDuration(connectionLink
					.getMobilityRestrictedTravellerDuration()));
		}

		try {
			ConnectionLinkTypeEnum linkType = connectionLink.getLinkType();
			if (linkType != null) {
				jaxbConnectionLink.setLinkType(ConnectionLinkTypeType.fromValue(linkType.name()));
			}
		} catch (IllegalArgumentException e) {
			// TODO generate report
		}

		ConnectionLinkExtensionType connectionLinkExtension = tridentFactory.createConnectionLinkExtensionType();
		AccessibilitySuitabilityDetails details = extractAccessibilitySuitabilityDetails(connectionLink.getUserNeeds());
		if (details != null) {
			connectionLinkExtension.setAccessibilitySuitabilityDetails(details);
			jaxbConnectionLink.setConnectionLinkExtension(connectionLinkExtension);
		}

		return jaxbConnectionLink;
	}

	protected AccessibilitySuitabilityDetails extractAccessibilitySuitabilityDetails(List<UserNeedEnum> userNeeds) {
		AccessibilitySuitabilityDetails details = new AccessibilitySuitabilityDetails();
		List<UserNeedStructure> detailsItems = new ArrayList<UserNeedStructure>();
		if (userNeeds != null) {
			for (UserNeedEnum userNeed : userNeeds) {
				if (userNeed != null) {
					UserNeedStructure userNeedGroup = new UserNeedStructure();

					switch (userNeed.category()) {
					case ENCUMBRANCE:
						userNeedGroup.setEncumbranceNeed(EncumbranceEnumeration.fromValue(userNeed.value()));
						break;
					case MEDICAL:
						userNeedGroup.setMedicalNeed(MedicalNeedEnumeration.fromValue(userNeed.value()));
						break;
					case PSYCHOSENSORY:
						userNeedGroup.setPsychosensoryNeed(PyschosensoryNeedEnumeration.fromValue(userNeed.value()));
						break;
					case MOBILITY:
						userNeedGroup.setMobilityNeed(MobilityEnumeration.fromValue(userNeed.value()));
						break;
					default:
						throw new IllegalArgumentException("bad value of userNeed");
					}

					detailsItems.add(userNeedGroup);

				}
			}
		}

		if (detailsItems.isEmpty())
			return null;
		details.getMobilityNeedOrPsychosensoryNeedOrMedicalNeed().addAll(detailsItems);
		return details;
	}

}
