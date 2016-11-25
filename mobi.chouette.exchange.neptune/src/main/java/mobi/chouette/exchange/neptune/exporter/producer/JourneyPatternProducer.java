package mobi.chouette.exchange.neptune.exporter.producer;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.NeptuneChouetteIdGenerator;
import mobi.chouette.exchange.neptune.exporter.NeptuneExportParameters;
import mobi.chouette.exchange.neptune.exporter.util.NeptuneObjectUtil;
import mobi.chouette.model.JourneyPattern;

import org.trident.schema.trident.JourneyPatternType;

public class JourneyPatternProducer extends AbstractJaxbNeptuneProducer<JourneyPatternType, JourneyPattern> {

	// @Override
	public JourneyPatternType produce(Context context, JourneyPattern journeyPattern, boolean addExtension) {
		  NeptuneExportParameters parameters = (NeptuneExportParameters) context.get(CONFIGURATION);
		   NeptuneChouetteIdGenerator neptuneChouetteIdGenerator = (NeptuneChouetteIdGenerator) context.get(CHOUETTEID_GENERATOR);
		JourneyPatternType jaxbJourneyPattern = tridentFactory.createJourneyPatternType();

		//
		populateFromModel(context, jaxbJourneyPattern, journeyPattern);

		jaxbJourneyPattern.setComment(journeyPattern.getComment());
		jaxbJourneyPattern.setName(journeyPattern.getName());
		jaxbJourneyPattern.setPublishedName(journeyPattern.getPublishedName());
		if (journeyPattern.getArrivalStopPoint() != null)
			jaxbJourneyPattern.setDestination(neptuneChouetteIdGenerator.toSpecificFormatId(journeyPattern.getArrivalStopPoint().getChouetteId(), parameters.getDefaultCodespace(), journeyPattern.getArrivalStopPoint()));
		if (journeyPattern.getDepartureStopPoint() != null)
			jaxbJourneyPattern.setOrigin(neptuneChouetteIdGenerator.toSpecificFormatId(journeyPattern.getDepartureStopPoint().getChouetteId(), parameters.getDefaultCodespace(), journeyPattern.getDepartureStopPoint()));
		jaxbJourneyPattern.setRegistration(getRegistration(journeyPattern.getRegistrationNumber()));
		jaxbJourneyPattern.setRouteId(getNonEmptyObjectId(context, journeyPattern.getRoute()));
		jaxbJourneyPattern.getStopPointList()
				.addAll(neptuneChouetteIdGenerator.toListSpecificFormatId(NeptuneObjectUtil.extractObjectIds(journeyPattern.getStopPoints()), "default_codespace", journeyPattern));

		return jaxbJourneyPattern;
	}

}
