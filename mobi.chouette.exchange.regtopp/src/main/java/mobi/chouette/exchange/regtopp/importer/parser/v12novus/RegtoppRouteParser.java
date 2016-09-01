package mobi.chouette.exchange.regtopp.importer.parser.v12novus;

import static mobi.chouette.common.Constant.CONFIGURATION;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.importer.parser.ObjectIdCreator;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppRouteTMS;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class RegtoppRouteParser extends mobi.chouette.exchange.regtopp.importer.parser.v12.RegtoppRouteParser {

	// Only change here from super class is that the stoppoint here uses both stopId and stopIdDeparture ("stoppunktsnummer");
	protected StopPoint createStopPoint(Referential referential, Context context, AbstractRegtoppRouteTMS routeSegment, String chouetteStopPointId)
			throws Exception {

		RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);

		StopArea stopArea = null;
		StopPoint stopPoint = null;
		
		String chouetteStopAreaId = ObjectIdCreator.createStopAreaId(configuration,routeSegment.getStopId() + routeSegment.getStopPointIdDeparture());

		if (referential.getSharedStopAreas().containsKey(chouetteStopAreaId)) {
			stopArea = ObjectFactory.getStopArea(referential, chouetteStopAreaId);

		} else {

			String chouetteParentStopAreaId = ObjectIdCreator.createStopAreaId(configuration,routeSegment.getStopId());

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
			stopPoint.setContainedInStopArea(stopArea);

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
