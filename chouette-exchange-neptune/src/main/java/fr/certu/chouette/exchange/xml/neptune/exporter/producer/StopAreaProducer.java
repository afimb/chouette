package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import java.util.ArrayList;
import java.util.List;

import chouette.schema.AccessibilitySuitabilityDetails;
import chouette.schema.AccessibilitySuitabilityDetailsItem;
import chouette.schema.StopAreaExtension;
import chouette.schema.UserNeedGroup;
import chouette.schema.types.ChouetteAreaType;
import chouette.schema.types.EncumbranceEnumeration;
import chouette.schema.types.MedicalNeedEnumeration;
import chouette.schema.types.MobilityEnumeration;
import chouette.schema.types.PyschosensoryNeedEnumeration;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;

public class StopAreaProducer extends AbstractCastorNeptuneProducer<chouette.schema.StopArea, StopArea> {

	@Override
	public chouette.schema.StopArea produce(StopArea stopArea) {
		chouette.schema.StopArea castorStopArea = new chouette.schema.StopArea();
		
		//
		populateFromModel(castorStopArea, stopArea);
		
		castorStopArea.setComment(stopArea.getComment());
		castorStopArea.setName(stopArea.getName());
		if(stopArea.getBoundaryPoints() != null){
			castorStopArea.setBoundaryPoint(stopArea.getBoundaryPoints());
		}
		castorStopArea.setCentroidOfArea(getNonEmptyObjectId(stopArea.getAreaCentroid()));
		
		List<String> containsList = new ArrayList<String>();
		containsList.addAll(NeptuneIdentifiedObject.extractObjectIds(stopArea.getContainedStopAreas()));
		containsList.addAll(NeptuneIdentifiedObject.extractObjectIds(stopArea.getContainedStopPoints()));
		castorStopArea.setContains(containsList);
		
		StopAreaExtension stopAreaExtension = new StopAreaExtension();
		stopAreaExtension.setAccessibilitySuitabilityDetails(extractAccessibilitySuitabilityDetails(stopArea.getUserNeeds()));
		
		try {
			ChouetteAreaEnum areaType = stopArea.getAreaType();
			if(areaType != null){
				stopAreaExtension.setAreaType(ChouetteAreaType.fromValue(areaType.value()));
			}
		} catch (IllegalArgumentException e) {
			// TODO generate report
		}
		
		stopAreaExtension.setFareCode(stopArea.getFareCode());
		stopAreaExtension.setLiftAvailability(stopArea.isLiftAvailable());
		stopAreaExtension.setMobilityRestrictedSuitability(stopArea.isMobilityRestrictedSuitable());
		stopAreaExtension.setNearestTopicName(stopArea.getNearestTopicName());
		stopAreaExtension.setRegistration(getRegistration(stopArea.getRegistrationNumber()));
		stopAreaExtension.setStairsAvailability(stopArea.isStairsAvailable());
		
		castorStopArea.setStopAreaExtension(stopAreaExtension );
						
		return castorStopArea;
	}

	
	private static AccessibilitySuitabilityDetails extractAccessibilitySuitabilityDetails(List<UserNeedEnum> userNeeds){
		AccessibilitySuitabilityDetails details = new AccessibilitySuitabilityDetails();
		List<AccessibilitySuitabilityDetailsItem> detailsItems = new ArrayList<AccessibilitySuitabilityDetailsItem>();
		if(userNeeds != null){
			for(UserNeedEnum userNeed : userNeeds){
				if(userNeed != null){
					UserNeedGroup userNeedGroup = new UserNeedGroup();
					
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
					
					if(userNeedGroup.getChoiceValue() != null){
						AccessibilitySuitabilityDetailsItem item = new AccessibilitySuitabilityDetailsItem();
						item.setUserNeedGroup(userNeedGroup);
						detailsItems.add(item);
					}
				}
			}
		}
		
		details.setAccessibilitySuitabilityDetailsItem(detailsItems);
		return details;
	}
}
