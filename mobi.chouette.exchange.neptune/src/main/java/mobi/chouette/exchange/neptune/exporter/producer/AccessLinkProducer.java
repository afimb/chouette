package mobi.chouette.exchange.neptune.exporter.producer;

import java.util.ArrayList;
import java.util.List;

import mobi.chouette.model.AccessLink;
import mobi.chouette.model.type.ConnectionLinkTypeEnum;
import mobi.chouette.model.type.LinkOrientationEnum;
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

public class AccessLinkProducer extends
AbstractJaxbNeptuneProducer<ChouettePTNetworkType.AccessLink, AccessLink>
{

	// @Override
	public ChouettePTNetworkType.AccessLink produce(AccessLink accessLink, boolean addExtension)
	{
		ChouettePTNetworkType.AccessLink jaxbAccessLink = tridentFactory
				.createChouettePTNetworkTypeAccessLink();

		//
		populateFromModel(jaxbAccessLink, accessLink);

		jaxbAccessLink.setComment(getNotEmptyString(accessLink.getComment()));
		jaxbAccessLink.setName(accessLink.getName());
		if (accessLink.getLinkOrientation().equals(LinkOrientationEnum.AccessPointToStopArea))
		{
			if (accessLink.getAccessPoint() != null)
				jaxbAccessLink.setStartOfLink(accessLink.getAccessPoint().getObjectId());
			if (accessLink.getStopArea() != null)
				jaxbAccessLink.setEndOfLink(accessLink.getStopArea().getObjectId());
		}
		else if (accessLink.getLinkOrientation().equals(LinkOrientationEnum.StopAreaToAccessPoint))
		{
			if (accessLink.getAccessPoint() != null)
				jaxbAccessLink.setEndOfLink(accessLink.getAccessPoint().getObjectId());
			if (accessLink.getStopArea() != null)
				jaxbAccessLink.setStartOfLink(accessLink.getStopArea().getObjectId());
		}

		jaxbAccessLink.setLinkDistance(accessLink.getLinkDistance());

		if (accessLink.getMobilityRestrictedSuitable() != null)
		{
			jaxbAccessLink.setMobilityRestrictedSuitability(accessLink.getMobilityRestrictedSuitable().booleanValue());
		}
		if (accessLink.getLiftAvailable() != null)
		{
			jaxbAccessLink.setLiftAvailability(accessLink.getLiftAvailable().booleanValue());
		}
		if (accessLink.getStairsAvailable() != null)
		{
			jaxbAccessLink.setStairsAvailability(accessLink.getStairsAvailable().booleanValue());
		}
		if (accessLink.getDefaultDuration() != null)
		{
			jaxbAccessLink.setDefaultDuration(toDuration(accessLink
					.getDefaultDuration()));
		}
		if (accessLink.getFrequentTravellerDuration() != null)
		{
			jaxbAccessLink.setFrequentTravellerDuration(toDuration(accessLink
					.getFrequentTravellerDuration()));
		}
		if (accessLink.getOccasionalTravellerDuration() != null)
		{
			jaxbAccessLink.setOccasionalTravellerDuration(toDuration(accessLink
					.getOccasionalTravellerDuration()));
		}
		if (accessLink.getMobilityRestrictedTravellerDuration() != null)
		{
			jaxbAccessLink
			.setMobilityRestrictedTravellerDuration(toDuration(accessLink
					.getMobilityRestrictedTravellerDuration()));
		}

		try
		{
			ConnectionLinkTypeEnum linkType = accessLink.getLinkType();
			if (linkType != null)
			{
				jaxbAccessLink.setLinkType(ConnectionLinkTypeType
						.fromValue(linkType.name()));
			}
		} catch (IllegalArgumentException e)
		{
			// TODO generate report
		}

		ConnectionLinkExtensionType connectionLinkExtension = tridentFactory
				.createConnectionLinkExtensionType();
		AccessibilitySuitabilityDetails details = extractAccessibilitySuitabilityDetails(accessLink
				.getUserNeeds());
		if (details != null)
		{
			connectionLinkExtension.setAccessibilitySuitabilityDetails(details);
			jaxbAccessLink.setConnectionLinkExtension(connectionLinkExtension);
		}

		return jaxbAccessLink;
	}

	protected AccessibilitySuitabilityDetails extractAccessibilitySuitabilityDetails(
			List<UserNeedEnum> userNeeds)
	{
		AccessibilitySuitabilityDetails details = new AccessibilitySuitabilityDetails();
		List<UserNeedStructure> detailsItems = new ArrayList<UserNeedStructure>();
		if (userNeeds != null)
		{
			for (UserNeedEnum userNeed : userNeeds)
			{
				if (userNeed != null)
				{
					UserNeedStructure userNeedGroup = new UserNeedStructure();

					switch (userNeed.category())
					{
					case ENCUMBRANCE:
						userNeedGroup.setEncumbranceNeed(EncumbranceEnumeration
								.fromValue(userNeed.value()));
						break;
					case MEDICAL:
						userNeedGroup.setMedicalNeed(MedicalNeedEnumeration
								.fromValue(userNeed.value()));
						break;
					case PSYCHOSENSORY:
						userNeedGroup
						.setPsychosensoryNeed(PyschosensoryNeedEnumeration
								.fromValue(userNeed.value()));
						break;
					case MOBILITY:
						userNeedGroup.setMobilityNeed(MobilityEnumeration
								.fromValue(userNeed.value()));
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
		details.getMobilityNeedOrPsychosensoryNeedOrMedicalNeed().addAll(
				detailsItems);
		return details;
	}

}
