package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import chouette.schema.AccessibilitySuitabilityDetailsItem;
import chouette.schema.StopAreaExtension;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;
import fr.certu.chouette.plugin.report.ReportItem;

public class StopAreaProducer extends AbstractModelProducer<StopArea,chouette.schema.StopArea>
{
	@Override
	public StopArea produce(chouette.schema.StopArea xmlStopArea,ReportItem report) 
	{
		StopArea stopArea = new StopArea();
		
		// objectId, objectVersion, creatorId, creationTime
		populateFromCastorNeptune(stopArea, xmlStopArea, report);
		
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
				try{
					stopArea.setAreaType(ChouetteAreaEnum.fromValue(xmlStopAreaExtension.getAreaType().value()));
				}
				catch (IllegalArgumentException e) 
				{
					// TODO: traiter le cas de non correspondance
					e.printStackTrace();
				}
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
			stopArea.setRegistrationNumber(getRegistrationNumber(xmlStopAreaExtension.getRegistration(), report));
			
			//StairsAvailability optional
			stopArea.setStairsAvailable(xmlStopAreaExtension.getStairsAvailability());
			
			if(xmlStopAreaExtension.getAccessibilitySuitabilityDetails() != null){
				for(AccessibilitySuitabilityDetailsItem xmlAccessibilitySuitabilityDetailsItem : xmlStopAreaExtension.getAccessibilitySuitabilityDetails().getAccessibilitySuitabilityDetailsItem()){
					if(xmlAccessibilitySuitabilityDetailsItem.getUserNeedGroup() != null){
						try{
							stopArea.addUserNeed(UserNeedEnum.fromValue(xmlAccessibilitySuitabilityDetailsItem.getUserNeedGroup().getChoiceValue().toString()));
						}
						catch (IllegalArgumentException e) 
						{
							// TODO: traiter le cas de non correspondance
						}
					}
				}
			}
		}
		
		return stopArea;
	}

}
