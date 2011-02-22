package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import java.util.ArrayList;
import java.util.List;

import org.exolab.castor.types.Duration;

import chouette.schema.AccessibilitySuitabilityDetails;
import chouette.schema.AccessibilitySuitabilityDetailsItem;
import chouette.schema.ConnectionLinkExtension;
import chouette.schema.UserNeedGroup;
import chouette.schema.types.ConnectionLinkTypeType;
import chouette.schema.types.EncumbranceEnumeration;
import chouette.schema.types.MedicalNeedEnumeration;
import chouette.schema.types.MobilityEnumeration;
import chouette.schema.types.PyschosensoryNeedEnumeration;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.type.ConnectionLinkTypeEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;

public class ConnectionLinkProducer extends AbstractCastorNeptuneProducer<chouette.schema.ConnectionLink, ConnectionLink> {

	@Override
	public chouette.schema.ConnectionLink produce(ConnectionLink connectionLink) {
		chouette.schema.ConnectionLink castorConnectionLink = new chouette.schema.ConnectionLink();
		
		//
		populateFromModel(castorConnectionLink, connectionLink);
		
		castorConnectionLink.setComment(connectionLink.getComment());
		castorConnectionLink.setName(connectionLink.getName());
		castorConnectionLink.setStartOfLink(getNonEmptyObjectId(connectionLink.getStartOfLink()));
		castorConnectionLink.setEndOfLink(getNonEmptyObjectId(connectionLink.getEndOfLink()));
		castorConnectionLink.setLinkDistance(connectionLink.getLinkDistance());
		castorConnectionLink.setMobilityRestrictedSuitability(connectionLink.isMobilityRestrictedSuitable());
		castorConnectionLink.setLiftAvailability(connectionLink.isLiftAvailable());
		castorConnectionLink.setStairsAvailability(connectionLink.isStairsAvailable());
		if(connectionLink.getDefaultDuration() != null){
			castorConnectionLink.setDefaultDuration(new Duration(connectionLink.getDefaultDuration().getTime()));
		}
		if(connectionLink.getFrequentTravellerDuration() != null){
			castorConnectionLink.setFrequentTravellerDuration(new Duration(connectionLink.getFrequentTravellerDuration().getTime()));
		}
		if(connectionLink.getOccasionalTravellerDuration() != null){
			castorConnectionLink.setOccasionalTravellerDuration(new Duration(connectionLink.getOccasionalTravellerDuration().getTime()));
		}
		if(connectionLink.getMobilityRestrictedTravellerDuration() != null){
			castorConnectionLink.setMobilityRestrictedTravellerDuration(new Duration(connectionLink.getMobilityRestrictedTravellerDuration().getTime()));
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
		connectionLinkExtension.setAccessibilitySuitabilityDetails(extractAccessibilitySuitabilityDetails(connectionLink.getUserNeeds()));
		castorConnectionLink.setConnectionLinkExtension(connectionLinkExtension);
		
		return castorConnectionLink;
	}
}
