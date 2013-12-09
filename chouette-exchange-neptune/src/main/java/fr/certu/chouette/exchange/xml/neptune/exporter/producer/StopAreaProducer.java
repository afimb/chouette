package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.trident.schema.trident.ChouetteAreaType;
import org.trident.schema.trident.StopAreaExtensionType;
import org.trident.schema.trident.ChouettePTNetworkType.ChouetteArea;
import org.trident.schema.trident.StopAreaExtensionType.AccessibilitySuitabilityDetails;

import uk.org.ifopt.acsb.EncumbranceEnumeration;
import uk.org.ifopt.acsb.MedicalNeedEnumeration;
import uk.org.ifopt.acsb.MobilityEnumeration;
import uk.org.ifopt.acsb.PyschosensoryNeedEnumeration;
import uk.org.ifopt.acsb.UserNeedStructure;

import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;

public class StopAreaProducer extends AbstractJaxbNeptuneProducer<ChouetteArea.StopArea, StopArea> {

	@Override
	public ChouetteArea.StopArea produce(StopArea stopArea) {
		ChouetteArea.StopArea jaxbStopArea = tridentFactory.createChouettePTNetworkTypeChouetteAreaStopArea();

		//
		populateFromModel(jaxbStopArea, stopArea);

		jaxbStopArea.setComment(getNotEmptyString(stopArea.getComment()));
		jaxbStopArea.setName(stopArea.getName());
		
		// castorStopArea.setCentroidOfArea(getNonEmptyObjectId(stopArea.getAreaCentroid()));

		Set<String> containsList = new HashSet<String>();
		if (stopArea.getAreaType().equals(ChouetteAreaEnum.ITL))
		{
			containsList.addAll(NeptuneIdentifiedObject.extractObjectIds(stopArea.getRoutingConstraintAreas()));
		}
		else
		{
			containsList.addAll(NeptuneIdentifiedObject.extractObjectIds(stopArea.getContainedStopAreas()));
			containsList.addAll(NeptuneIdentifiedObject.extractObjectIds(stopArea.getContainedStopPoints()));			
		}
		jaxbStopArea.getContains().addAll(containsList);

		StopAreaExtensionType stopAreaExtension = tridentFactory.createStopAreaExtensionType();
		stopAreaExtension.setAccessibilitySuitabilityDetails(extractAccessibilitySuitabilityDetails(stopArea.getUserNeeds()));

		try 
		{
			ChouetteAreaEnum areaType = stopArea.getAreaType();
			if(areaType != null)
			{
				stopAreaExtension.setAreaType(ChouetteAreaType.fromValue(areaType.value()));
			}
		}
		catch (IllegalArgumentException e) 
		{
			// TODO generate report
		}

		stopAreaExtension.setNearestTopicName(getNotEmptyString(stopArea.getNearestTopicName()));
		stopAreaExtension.setRegistration(getRegistration(stopArea.getRegistrationNumber()));
		if (stopArea.getFareCode() != null)
			stopAreaExtension.setFareCode(stopArea.getFareCode());
		if (stopArea.isLiftAvailable())
			stopAreaExtension.setLiftAvailability(true);
		if (stopArea.isMobilityRestrictedSuitable())
			stopAreaExtension.setMobilityRestrictedSuitability(true);
		if (stopArea.isStairsAvailable())
			stopAreaExtension.setStairsAvailability(true);

		jaxbStopArea.setStopAreaExtension(stopAreaExtension );

		return jaxbStopArea;
	}
	
	protected AccessibilitySuitabilityDetails extractAccessibilitySuitabilityDetails(List<UserNeedEnum> userNeeds){
		AccessibilitySuitabilityDetails details = new AccessibilitySuitabilityDetails();
		List<UserNeedStructure> detailsItems = new ArrayList<UserNeedStructure>();
		if(userNeeds != null){
			for(UserNeedEnum userNeed : userNeeds){
				if(userNeed != null){
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

		if (detailsItems.isEmpty()) return null;
		details.getMobilityNeedOrPsychosensoryNeedOrMedicalNeed().addAll(detailsItems);
		return details;
	}

}
