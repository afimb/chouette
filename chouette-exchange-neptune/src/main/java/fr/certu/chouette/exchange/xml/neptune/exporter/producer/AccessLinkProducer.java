package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import chouette.schema.AccessibilitySuitabilityDetails;
import chouette.schema.ConnectionLinkExtension;
import chouette.schema.types.ConnectionLinkTypeType;
import fr.certu.chouette.model.neptune.AccessLink;
import fr.certu.chouette.model.neptune.type.ConnectionLinkTypeEnum;

public class AccessLinkProducer extends AbstractCastorNeptuneProducer<chouette.schema.AccessLink, AccessLink> {

	@Override
	public chouette.schema.AccessLink produce(AccessLink accessLink) {
		chouette.schema.AccessLink castorAccessLink = new chouette.schema.AccessLink();
		
		//
		populateFromModel(castorAccessLink, accessLink);
		 
		castorAccessLink.setComment(getNotEmptyString(accessLink.getComment()));
		castorAccessLink.setName(accessLink.getName());
		castorAccessLink.setStartOfLink(accessLink.getStartOfLinkId());
		castorAccessLink.setEndOfLink(accessLink.getEndOfLinkId());
		castorAccessLink.setLinkDistance(accessLink.getLinkDistance());
		castorAccessLink.setMobilityRestrictedSuitability(accessLink.isMobilityRestrictedSuitable());
		castorAccessLink.setLiftAvailability(accessLink.isLiftAvailable());
		castorAccessLink.setStairsAvailability(accessLink.isStairsAvailable());
		if(accessLink.getDefaultDuration() != null){
			castorAccessLink.setDefaultDuration(toDuration(accessLink.getDefaultDuration()));
		}
		if(accessLink.getFrequentTravellerDuration() != null){
			castorAccessLink.setFrequentTravellerDuration(toDuration(accessLink.getFrequentTravellerDuration()));
		}
		if(accessLink.getOccasionalTravellerDuration() != null){
			castorAccessLink.setOccasionalTravellerDuration(toDuration(accessLink.getOccasionalTravellerDuration()));
		}
		if(accessLink.getMobilityRestrictedTravellerDuration() != null){
			castorAccessLink.setMobilityRestrictedTravellerDuration(toDuration(accessLink.getMobilityRestrictedTravellerDuration()));
		}
		
		try {
			ConnectionLinkTypeEnum linkType = accessLink.getLinkType();
			if(linkType != null){
				castorAccessLink.setLinkType(ConnectionLinkTypeType.fromValue(linkType.value()));
			}
		} catch (IllegalArgumentException e) {
			// TODO generate report
		}
		
		ConnectionLinkExtension connectionLinkExtension = new ConnectionLinkExtension();
		AccessibilitySuitabilityDetails details = extractAccessibilitySuitabilityDetails(accessLink.getUserNeeds());
		if (details != null)
		{
			connectionLinkExtension.setAccessibilitySuitabilityDetails(details);
			castorAccessLink.setConnectionLinkExtension(connectionLinkExtension);
		}
		
		return castorAccessLink;
	}
}
