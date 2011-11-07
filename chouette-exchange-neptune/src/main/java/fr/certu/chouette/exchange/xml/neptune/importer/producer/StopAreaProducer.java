package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import chouette.schema.AccessibilitySuitabilityDetailsItem;
import chouette.schema.StopAreaExtension;
import fr.certu.chouette.exchange.xml.neptune.importer.SharedImportedData;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;
import fr.certu.chouette.plugin.report.ReportItem;

public class StopAreaProducer extends AbstractModelProducer<StopArea,chouette.schema.StopArea>
{
	@Override
	public StopArea produce(chouette.schema.StopArea xmlStopArea,ReportItem report,SharedImportedData sharedData) 
	{
		StopArea stopArea = new StopArea();
		
		// objectId, objectVersion, creatorId, creationTime
		populateFromCastorNeptune(stopArea, xmlStopArea, report);
		StopArea sharedBean = sharedData.get(stopArea);
      if (sharedBean != null) return sharedBean;
      
		// AreaCentroid optional
		stopArea.setAreaCentroidId(getNonEmptyTrimedString(xmlStopArea.getCentroidOfArea()));
		
		// Name optional
		stopArea.setName(getNonEmptyTrimedString(xmlStopArea.getName()));
		
		// Comment optional
		stopArea.setComment(getNonEmptyTrimedString(xmlStopArea.getComment()));
		
		// BoundaryPoints [0..w] : out of Neptune scope
		
		// ContainedStopIds [1..w]
		for(String containedStopId : xmlStopArea.getContains()){
			stopArea.addContainedStopId(getNonEmptyTrimedString(containedStopId));
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
