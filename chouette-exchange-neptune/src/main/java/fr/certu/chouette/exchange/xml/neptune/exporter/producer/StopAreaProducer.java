package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import java.util.ArrayList;
import java.util.List;

import chouette.schema.StopAreaExtension;
import chouette.schema.types.ChouetteAreaType;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;

public class StopAreaProducer extends AbstractCastorNeptuneProducer<chouette.schema.StopArea, StopArea> {

	@Override
	public chouette.schema.StopArea produce(StopArea stopArea) {
		chouette.schema.StopArea castorStopArea = new chouette.schema.StopArea();

		//
		populateFromModel(castorStopArea, stopArea);

		castorStopArea.setComment(stopArea.getComment());
		castorStopArea.setName(stopArea.getName());
		if(stopArea.getBoundaryPoints() != null)
		{
			castorStopArea.setBoundaryPoint(stopArea.getBoundaryPoints());
		}
		castorStopArea.setCentroidOfArea(getNonEmptyObjectId(stopArea.getAreaCentroid()));

		List<String> containsList = new ArrayList<String>();
		containsList.addAll(NeptuneIdentifiedObject.extractObjectIds(stopArea.getContainedStopAreas()));
		containsList.addAll(NeptuneIdentifiedObject.extractObjectIds(stopArea.getContainedStopPoints()));
		castorStopArea.setContains(containsList);

		StopAreaExtension stopAreaExtension = new StopAreaExtension();
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

		if (stopArea.getFareCode() != null)
			stopAreaExtension.setFareCode(stopArea.getFareCode());
		if (stopArea.getLiftAvailable() != null)
			stopAreaExtension.setLiftAvailability(stopArea.getLiftAvailable());
		if (stopArea.getMobilityRestrictedSuitable() != null)
			stopAreaExtension.setMobilityRestrictedSuitability(stopArea.getMobilityRestrictedSuitable());
		stopAreaExtension.setNearestTopicName(stopArea.getNearestTopicName());
		stopAreaExtension.setRegistration(getRegistration(stopArea.getRegistrationNumber()));
		if (stopArea.getStairsAvailable() != null)
			stopAreaExtension.setStairsAvailability(stopArea.getStairsAvailable());

		castorStopArea.setStopAreaExtension(stopAreaExtension );

		return castorStopArea;
	}
}
