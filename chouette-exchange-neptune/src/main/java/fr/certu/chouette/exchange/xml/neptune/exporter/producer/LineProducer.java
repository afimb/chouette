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
		castorLine.setPtNetworkIdShortcut(getNonEmptyObjectId(line.getPtNetwork()));
		
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
}
