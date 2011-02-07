package fr.certu.chouette.exchange.xml.neptune;

import chouette.schema.AccessibilitySuitabilityDetailsItem;
import chouette.schema.StopAreaExtension;
import fr.certu.chouette.filter.DetailLevelEnum;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;

public class StopAreaProducer extends AbstractModelProducer<StopArea,chouette.schema.StopArea>
{
	@Override
	public StopArea produce(chouette.schema.StopArea xmlStopArea)
	{
		StopArea stopArea = new StopArea();
		
		// objectId, objectVersion, creatorId, creationTime
		populateTridentObject(stopArea, xmlStopArea);
		
		// AreaCentroid optional
		stopArea.setAreaCentroidId(getNonEmptyTrimedString(xmlStopArea.getCentroidOfArea()));
		
		// Name optional
		stopArea.setName(getNonEmptyTrimedString(xmlStopArea.getName()));
		
		// Comment optional
		stopArea.setComment(getNonEmptyTrimedString(xmlStopArea.getComment()));
		
		// BoundaryPoints [0..w]
		for(String boundaryPoint : xmlStopArea.getBoundaryPoint()){
			stopArea.addBoundaryPoint(getNonEmptyTrimedString(boundaryPoint));
		}
		
		// ContainedStopIds [1..w]
		for(String containedStopAreaId : xmlStopArea.getContains()){
			stopArea.addContainedStopAreaId(getNonEmptyTrimedString(containedStopAreaId));
		}
		
		// StopAreaExtension optional
		StopAreaExtension xmlStopAreaExtension = xmlStopArea.getStopAreaExtension();
		if(xmlStopAreaExtension != null){
			// AreaType mandatory
			if(xmlStopAreaExtension.getAreaType() != null){
				stopArea.setAreaType(ChouetteAreaEnum.fromValue(xmlStopAreaExtension.getAreaType().value()));
			}
			
			// FareCode optional
			stopArea.setFareCode(xmlStopAreaExtension.getFareCode());
			
			// LiftAvailability optional
			stopArea.setLiftAvailable(xmlStopAreaExtension.getLiftAvailability());
			
			// MobilityRestrictedSuitability
			stopArea.setMobilityRestrictedSuitable(xmlStopAreaExtension.getMobilityRestrictedSuitability());
			
			// NearestTopicName optional
			stopArea.setNearestTopicName(getNonEmptyTrimedString(xmlStopAreaExtension.getNearestTopicName()));
			
			// RegistrationNumber optional
			stopArea.setRegistrationNumber(getRegistrationNumber(xmlStopAreaExtension.getRegistration()));
			
			//StairsAvailability optional
			stopArea.setStairsAvailable(xmlStopAreaExtension.getStairsAvailability());
			
			if(xmlStopAreaExtension.getAccessibilitySuitabilityDetails() != null){
				for(AccessibilitySuitabilityDetailsItem xmlAccessibilitySuitabilityDetailsItem : xmlStopAreaExtension.getAccessibilitySuitabilityDetails().getAccessibilitySuitabilityDetailsItem()){
					if(xmlAccessibilitySuitabilityDetailsItem.getUserNeedGroup() != null){
						stopArea.addUserNeed(UserNeedEnum.fromValue(xmlAccessibilitySuitabilityDetailsItem.getUserNeedGroup().getChoiceValue().toString()));
					}
				}
			}

		}
		
		stopArea.expand(DetailLevelEnum.ALL_DEPENDENCIES);
		return stopArea;
	}

}
