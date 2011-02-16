package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

public class JourneyPatternProducer extends AbstractCastorNeptuneProducer<chouette.schema.JourneyPattern, JourneyPattern> {

	@Override
	public chouette.schema.JourneyPattern produce(JourneyPattern journeyPattern) {
		chouette.schema.JourneyPattern castorJourneyPattern = new chouette.schema.JourneyPattern();
		
		//
		populateFromModel(castorJourneyPattern, journeyPattern);
		
		castorJourneyPattern.setComment(journeyPattern.getComment());
		castorJourneyPattern.setName(journeyPattern.getName());
		castorJourneyPattern.setPublishedName(journeyPattern.getPublishedName());
		castorJourneyPattern.setDestination(journeyPattern.getDestination());
		castorJourneyPattern.setLineIdShortcut(journeyPattern.getLineIdShortcut()); //FIXME why not a model object ???
		castorJourneyPattern.setOrigin(journeyPattern.getOrigin());
		castorJourneyPattern.setRegistration(getRegistration(journeyPattern.getRegistrationNumber()));
		castorJourneyPattern.setRouteId(getNonEmptyObjectId(journeyPattern.getRoute()));
		castorJourneyPattern.setStopPointList(NeptuneIdentifiedObject.extractObjectIds(journeyPattern.getStopPoints()));
				
		return castorJourneyPattern;
	}

}
