package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import chouette.schema.AccessibilitySuitabilityDetails;
import chouette.schema.ConnectionLinkExtension;
import chouette.schema.types.ConnectionLinkTypeType;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.type.ConnectionLinkTypeEnum;

public class ConnectionLinkProducer extends AbstractCastorNeptuneProducer<chouette.schema.ConnectionLink, ConnectionLink> {

	@Override
	public chouette.schema.ConnectionLink produce(ConnectionLink connectionLink) {
		chouette.schema.ConnectionLink castorConnectionLink = new chouette.schema.ConnectionLink();
		
		//
		populateFromModel(castorConnectionLink, connectionLink);
		
		castorConnectionLink.setComment(getNotEmptyString(connectionLink.getComment()));
		castorConnectionLink.setName(connectionLink.getName());
		castorConnectionLink.setStartOfLink(getNonEmptyObjectId(connectionLink.getStartOfLink()));
		castorConnectionLink.setEndOfLink(getNonEmptyObjectId(connectionLink.getEndOfLink()));
		castorConnectionLink.setLinkDistance(connectionLink.getLinkDistance());
		castorConnectionLink.setMobilityRestrictedSuitability(connectionLink.isMobilityRestrictedSuitable());
		castorConnectionLink.setLiftAvailability(connectionLink.isLiftAvailable());
		castorConnectionLink.setStairsAvailability(connectionLink.isStairsAvailable());
		if(connectionLink.getDefaultDuration() != null){
			castorConnectionLink.setDefaultDuration(toDuration(connectionLink.getDefaultDuration()));
		}
		if(connectionLink.getFrequentTravellerDuration() != null){
			castorConnectionLink.setFrequentTravellerDuration(toDuration(connectionLink.getFrequentTravellerDuration()));
		}
		if(connectionLink.getOccasionalTravellerDuration() != null){
			castorConnectionLink.setOccasionalTravellerDuration(toDuration(connectionLink.getOccasionalTravellerDuration()));
		}
		if(connectionLink.getMobilityRestrictedTravellerDuration() != null){
			castorConnectionLink.setMobilityRestrictedTravellerDuration(toDuration(connectionLink.getMobilityRestrictedTravellerDuration()));
		}
		
		try {
			ConnectionLinkTypeEnum linkType = connectionLink.getLinkType();
			if(linkType != null){
				castorConnectionLink.setLinkType(ConnectionLinkTypeType.fromValue(linkType.value()));
			}
		} catch (IllegalArgumentException e) {
			// TODO generate report
		}
		
		ConnectionLinkExtension connectionLinkExtension = new ConnectionLinkExtension();
		AccessibilitySuitabilityDetails details = extractAccessibilitySuitabilityDetails(connectionLink.getUserNeeds());
		if (details != null)
		{
			connectionLinkExtension.setAccessibilitySuitabilityDetails(details);
			castorConnectionLink.setConnectionLinkExtension(connectionLinkExtension);
		}
		
		return castorConnectionLink;
	}
}
