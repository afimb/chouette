package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import java.util.ArrayList;
import java.util.List;

import chouette.schema.AccessibilitySuitabilityDetails;
import chouette.schema.AccessibilitySuitabilityDetailsItem;
import chouette.schema.LineExtension;
import chouette.schema.UserNeedGroup;
import chouette.schema.types.EncumbranceEnumeration;
import chouette.schema.types.MedicalNeedEnumeration;
import chouette.schema.types.MobilityEnumeration;
import chouette.schema.types.PyschosensoryNeedEnumeration;
import chouette.schema.types.TransportModeNameType;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;

public class LineProducer extends AbstractCastorNeptuneProducer<chouette.schema.Line, Line> {

	@Override
	public chouette.schema.Line produce(Line line) {
		chouette.schema.Line castorLine = new chouette.schema.Line();
		
		//
		populateFromModel(castorLine, line);
		
		castorLine.setComment(line.getComment());
		castorLine.setName(line.getName());
		castorLine.setNumber(line.getNumber());
		castorLine.setPublishedName(line.getPublishedName());
		PTNetwork ptNetwork = line.getPtNetwork();
		if(ptNetwork != null){
			castorLine.setPtNetworkIdShortcut(ptNetwork.getObjectId());
		}
		
		
		try {
			TransportModeNameEnum transportModeName = line.getTransportModeName();
			if(transportModeName != null){
				castorLine.setTransportModeName(TransportModeNameType.fromValue(transportModeName.value()));
			}
		} catch (IllegalArgumentException e) {
			// TODO generate report
		}
		
		castorLine.setRegistration(getRegistration(line.getRegistrationNumber()));
		if(line.getLineEnds() != null){
			castorLine.setLineEnd(line.getLineEnds());
		}
		castorLine.setRouteId(NeptuneIdentifiedObject.extractObjectIds(line.getRoutes()));
		
		LineExtension castorLineExtension = new LineExtension();
		castorLineExtension.setAccessibilitySuitabilityDetails(extractAccessibilitySuitabilityDetails(line.getUserNeeds()));
		castorLineExtension.setMobilityRestrictedSuitability(line.isMobilityRestrictedSuitable());
		// castorLineExtension.setStableId(stableId); ???
		castorLine.setLineExtension(castorLineExtension);
		
		return castorLine;
	}
	
	private static AccessibilitySuitabilityDetails extractAccessibilitySuitabilityDetails(List<UserNeedEnum> userNeeds){
		AccessibilitySuitabilityDetails details = new AccessibilitySuitabilityDetails();
		List<AccessibilitySuitabilityDetailsItem> detailsItems = new ArrayList<AccessibilitySuitabilityDetailsItem>();
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
		
		details.setAccessibilitySuitabilityDetailsItem(detailsItems);
		return details;
	}

}
