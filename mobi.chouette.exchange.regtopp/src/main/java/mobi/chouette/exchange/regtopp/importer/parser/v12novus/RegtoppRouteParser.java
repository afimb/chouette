package mobi.chouette.exchange.regtopp.importer.parser.v12novus;

import static mobi.chouette.common.Constant.CONFIGURATION;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.parser.ObjectIdCreator;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDestinationDST;
import mobi.chouette.model.ScheduledStopPoint;
import mobi.chouette.model.SimpleObjectReference;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.ObjectIdTypes;
import mobi.chouette.model.util.Referential;

@Log4j
public class RegtoppRouteParser extends mobi.chouette.exchange.regtopp.importer.parser.v12.RegtoppRouteParser {

	// Only change here from super class is that the stoppoint here uses both stopId and stopIdDeparture ("stoppunktsnummer");
	@Override
	protected StopPoint createStopPoint(Referential referential, Context context, AbstractRegtoppRouteTMS routeSegment, String chouetteStopPointId, Index<RegtoppDestinationDST> destinationById, String calendarStartDate)
			throws Exception {

		RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);

		StopArea stopArea = null;
		StopPoint stopPoint = null;
		
		String chouetteStopAreaId;
		// Do not use empty stopppoint id if 00
		if("00".equals(routeSegment.getStopPointIdDeparture())) {
			 chouetteStopAreaId = ObjectIdCreator.createQuayId(configuration,routeSegment.getStopId());
		} else {
			 chouetteStopAreaId = ObjectIdCreator.createQuayId(configuration,routeSegment.getStopId() + routeSegment.getStopPointIdDeparture());
		}
		

		if (referential.getSharedStopAreas().containsKey(chouetteStopAreaId)) {
			stopArea = ObjectFactory.getStopArea(referential, chouetteStopAreaId);

		} else {

			String chouetteParentStopAreaId = ObjectIdCreator.createStopPlaceId(configuration,routeSegment.getStopId());

			if (referential.getSharedStopAreas().containsKey(chouetteParentStopAreaId)) {
				log.info("StopPoint " + chouetteStopPointId + " is refering to non existent StopArea " + chouetteStopAreaId
						+ ". Luckily there is a parent StopArea with id " + chouetteParentStopAreaId);
				stopArea = ObjectFactory.getStopArea(referential, chouetteParentStopAreaId);
			} else {
				log.warn("Unable to link StopPoint " + chouetteStopPointId + " to either a StopArea with identifier " + chouetteStopAreaId
						+ " or the parent StopArea with id " + chouetteParentStopAreaId+ ". Ignoring StopPoint");
				// TODO add validation reporter warning
			}
		}
		if (stopArea != null) {
			stopPoint = ObjectFactory.getStopPoint(referential, chouetteStopPointId);
			stopPoint.setPosition(Integer.parseInt(routeSegment.getSequenceNumberStop()));
			String scheduledStopPointId = chouetteStopPointId.replace(ObjectIdTypes.STOPPOINT_KEY, ObjectIdTypes.SCHEDULED_STOP_POINT_KEY);
			ScheduledStopPoint scheduledStopPoint = ObjectFactory.getScheduledStopPoint(referential, scheduledStopPointId);
			stopPoint.setScheduledStopPoint(scheduledStopPoint);
			scheduledStopPoint.setContainedInStopAreaRef(new SimpleObjectReference(stopArea));
			appendDestinationDisplay(referential, routeSegment, destinationById, configuration, stopPoint, calendarStartDate);

		}

		return stopPoint;
}

	static {
		ParserFactory.register(RegtoppRouteParser.class.getName(), new ParserFactory() {
			@Override
			protected Parser create() {
				return new RegtoppRouteParser();
			}
		});
	}

}
