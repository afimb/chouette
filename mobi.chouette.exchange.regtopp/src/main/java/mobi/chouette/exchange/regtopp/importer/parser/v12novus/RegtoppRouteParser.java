package mobi.chouette.exchange.regtopp.importer.parser.v12novus;

import static mobi.chouette.common.Constant.CONFIGURATION;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.importer.parser.AbstractConverter;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppRouteTMS;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.ObjectIdTypes;
import mobi.chouette.model.util.Referential;

@Log4j
public class RegtoppRouteParser extends mobi.chouette.exchange.regtopp.importer.parser.v12.RegtoppRouteParser {

	// Only change here from super class is that the stoppoint here uses both stopId and stopIdDeparture ("stoppunktsnummer");
	protected StopPoint createStopPoint(Referential referential, Context context, AbstractRegtoppRouteTMS routeSegment, String chouetteStopPointId)
			throws Exception {

		RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);

		StopPoint stopPoint = ObjectFactory.getStopPoint(referential, chouetteStopPointId);
		stopPoint.setPosition(Integer.parseInt(routeSegment.getSequenceNumberStop()));

		String regtoppId = routeSegment.getStopId() + routeSegment.getStopPointIdDeparture();
		String chouetteStopAreaId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.STOPAREA_KEY, regtoppId);

		if (referential.getSharedStopAreas().containsKey(chouetteStopAreaId)) {
			StopArea stopArea = ObjectFactory.getStopArea(referential, chouetteStopAreaId);

			stopPoint.setContainedInStopArea(stopArea);
		} else {
			
			String parentStopPlaceId = routeSegment.getStopId() ;
			String chouetteParentStopAreaId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.STOPAREA_KEY, parentStopPlaceId);
			
			if (referential.getSharedStopAreas().containsKey(chouetteParentStopAreaId)) {
				log.warn("StopPoint "+stopPoint+" is refering to non existent StopArea "+chouetteStopAreaId+". Luckily there is a parent StopArea with id "+chouetteParentStopAreaId);
				StopArea stopArea = ObjectFactory.getStopArea(referential, chouetteParentStopAreaId);
				stopPoint.setContainedInStopArea(stopArea);
			} else {
				log.error("Unable to link StopPoint "+stopPoint+ " to either a StopArea with identifier "+chouetteStopAreaId+" or the parent StopArea with id "+chouetteParentStopAreaId);
			}
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
