package mobi.chouette.exchange.neptune.exporter.producer;

import mobi.chouette.exchange.neptune.exporter.util.NeptuneObjectUtil;
import mobi.chouette.model.JourneyPattern;

import org.trident.schema.trident.JourneyPatternType;

public class JourneyPatternProducer extends AbstractJaxbNeptuneProducer<JourneyPatternType, JourneyPattern> {

	// @Override
	public JourneyPatternType produce(JourneyPattern journeyPattern, boolean addExtension) {
		JourneyPatternType jaxbJourneyPattern = tridentFactory.createJourneyPatternType();

		//
		populateFromModel(jaxbJourneyPattern, journeyPattern);

		jaxbJourneyPattern.setComment(journeyPattern.getComment());
		jaxbJourneyPattern.setName(journeyPattern.getName());
		jaxbJourneyPattern.setPublishedName(journeyPattern.getPublishedName());
		if (journeyPattern.getArrivalStopPoint() != null)
			jaxbJourneyPattern.setDestination(journeyPattern.getArrivalStopPoint().getChouetteId().getObjectId());
		if (journeyPattern.getDepartureStopPoint() != null)
			jaxbJourneyPattern.setOrigin(journeyPattern.getDepartureStopPoint().getChouetteId().getObjectId());
		jaxbJourneyPattern.setRegistration(getRegistration(journeyPattern.getRegistrationNumber()));
		jaxbJourneyPattern.setRouteId(getNonEmptyObjectId(journeyPattern.getRoute()));
		jaxbJourneyPattern.getStopPointList()
				.addAll(NeptuneObjectUtil.extractObjectIds(journeyPattern.getStopPoints()));

		return jaxbJourneyPattern;
	}

}
