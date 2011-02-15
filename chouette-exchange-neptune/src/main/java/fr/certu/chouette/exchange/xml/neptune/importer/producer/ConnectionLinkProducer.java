package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import java.util.Date;

import chouette.schema.AccessibilitySuitabilityDetailsItem;
import chouette.schema.ConnectionLinkExtension;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.type.ConnectionLinkTypeEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;

public class ConnectionLinkProducer extends AbstractModelProducer<ConnectionLink, chouette.schema.ConnectionLink> {

	@Override
	public ConnectionLink produce(chouette.schema.ConnectionLink xmlConnectionLink) {
		ConnectionLink connectionLink= new ConnectionLink();
		
		// objectId, objectVersion, creatorId, creationTime
		populateFromCastorNeptune(connectionLink, xmlConnectionLink);
				
		// Name optional
		connectionLink.setName(getNonEmptyTrimedString(xmlConnectionLink.getName()));
		
		// Comment optional
		connectionLink.setComment(getNonEmptyTrimedString(xmlConnectionLink.getComment()));
		
		//StartOfLink mandatory
		connectionLink.setStartOfLinkId(getNonEmptyTrimedString(xmlConnectionLink.getStartOfLink()));
		
		//EndOfLink mandatory
		connectionLink.setEndOfLinkId(getNonEmptyTrimedString(xmlConnectionLink.getEndOfLink()));
		
		//LinkDistance optional
		connectionLink.setLinkDistance(xmlConnectionLink.getLinkDistance());
		
		// LiftAvailability optional
		connectionLink.setLiftAvailable(xmlConnectionLink.getLiftAvailability());
		
		// MobilityRestrictedSuitability optional
		connectionLink.setMobilityRestrictedSuitable(xmlConnectionLink.getMobilityRestrictedSuitability());
		
		// StairsAvailability optional
		connectionLink.setStairsAvailable(xmlConnectionLink.getStairsAvailability());
		
		// ConnectionLinkExtension optional
		ConnectionLinkExtension xmlConnectionLinkExtension = xmlConnectionLink.getConnectionLinkExtension();
		if(xmlConnectionLinkExtension != null){
			if(xmlConnectionLinkExtension.getAccessibilitySuitabilityDetails() != null){
				for(AccessibilitySuitabilityDetailsItem xmlAccessibilitySuitabilityDetailsItem : xmlConnectionLinkExtension.getAccessibilitySuitabilityDetails().getAccessibilitySuitabilityDetailsItem()){
					if(xmlAccessibilitySuitabilityDetailsItem.getUserNeedGroup() != null){
						try{
							connectionLink.addUserNeed(UserNeedEnum.fromValue(xmlAccessibilitySuitabilityDetailsItem.getUserNeedGroup().getChoiceValue().toString()));
						}
						catch (IllegalArgumentException e) 
						{
							// TODO: traiter le cas de non correspondance
						}
					}
				}
			}
		}
		
		// DefaultDuration optional
		if(xmlConnectionLink.getDefaultDuration() != null){
			connectionLink.setDefaultDuration(new Date(xmlConnectionLink.getDefaultDuration().toLong()));
		}
		
		// FrequentTravellerDuration optional
		if(xmlConnectionLink.getFrequentTravellerDuration() != null){
			connectionLink.setFrequentTravellerDuration(new Date(xmlConnectionLink.getFrequentTravellerDuration().toLong()));
		}
		
		// OccasionalTravellerDuration optional
		if(xmlConnectionLink.getOccasionalTravellerDuration() != null){
			connectionLink.setOccasionalTravellerDuration(new Date(xmlConnectionLink.getOccasionalTravellerDuration().toLong()));
		}
		
		// MobilityRestrictedTravellerDuration optional
		if(xmlConnectionLink.getMobilityRestrictedTravellerDuration() != null){
			connectionLink.setMobilityRestrictedTravellerDuration(new Date(xmlConnectionLink.getMobilityRestrictedTravellerDuration().toLong()));
		}
		
		// LinkType optional
		if(xmlConnectionLink.getLinkType() != null){
			try{
				connectionLink.setLinkType(ConnectionLinkTypeEnum.fromValue(xmlConnectionLink.getLinkType().value()));
			}
			catch (IllegalArgumentException e) 
			{
				// TODO: traiter le cas de non correspondance
			}
		}
				
		return connectionLink;
	}

}
