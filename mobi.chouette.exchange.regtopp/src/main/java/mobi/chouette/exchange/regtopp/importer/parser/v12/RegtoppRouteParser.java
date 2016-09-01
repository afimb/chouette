package mobi.chouette.exchange.regtopp.importer.parser.v12;

import static mobi.chouette.common.Constant.CONFIGURATION;
import static mobi.chouette.common.Constant.PARSER;
import static mobi.chouette.common.Constant.REFERENTIAL;

import java.util.List;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.RegtoppConstant;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.parser.ObjectIdCreator;
import mobi.chouette.exchange.regtopp.importer.parser.RouteKey;
import mobi.chouette.exchange.regtopp.importer.parser.v11.RegtoppStopParser;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppRouteTMS;
import mobi.chouette.model.Company;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.ObjectIdTypes;
import mobi.chouette.model.util.Referential;


public class RegtoppRouteParser extends mobi.chouette.exchange.regtopp.importer.parser.v11.RegtoppRouteParser {

	/*
	 * Validation rules of type III are checked at this step.
	 */
	// TODO. Rename this function "translate(Context context)" or "produce(Context context)", ...
	@Override
	public void parse(Context context) throws Exception {

		// Her tar vi allerede konsistenssjekkede data (ref validate-metode over) og bygger opp tilsvarende struktur i chouette.
		// Merk at import er linje-sentrisk, så man skal i denne klassen returnerer 1 line med x antall routes og stoppesteder, journeypatterns osv

		Referential referential = (Referential) context.get(REFERENTIAL);

		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
		RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);
		String calendarStartDate = (String) context.get(RegtoppConstant.CALENDAR_START_DATE);

		String chouetteLineId = ObjectIdCreator.createLineId(configuration, lineId, calendarStartDate);
		Line line = ObjectFactory.getLine(referential, chouetteLineId);
		List<Footnote> footnotes = line.getFootnotes();


		// Add routes and journey patterns
		Index<AbstractRegtoppRouteTMS> routeIndex = importer.getRouteIndex();

		for (AbstractRegtoppRouteTMS routeSegment : routeIndex) {
			if (lineId.equals(routeSegment.getLineId())) {

				// Add network
				Network ptNetwork = addNetwork(referential, configuration, routeSegment.getAdminCode());
				line.setNetwork(ptNetwork);

				// Add authority company
				Company company = addAuthority(referential, configuration, routeSegment.getAdminCode());
				line.setCompany(company);

				// TODO Add footnote to line
				addFootnote(routeSegment.getRemarkId(), null, footnotes, importer);

				// Create route
				RouteKey routeKey = new RouteKey(routeSegment.getLineId(), routeSegment.getDirection(), routeSegment.getRouteId(),calendarStartDate);
				Route route = createRoute(context, line, routeSegment.getDirection(), routeSegment.getRouteId(), routeSegment.getDestinationId(), routeKey);

				// Create journey pattern
				String chouetteJourneyPatternId = ObjectIdCreator.createJourneyPatternId(configuration,	routeKey);

				JourneyPattern journeyPattern = ObjectFactory.getJourneyPattern(referential, chouetteJourneyPatternId);
				journeyPattern.setRoute(route);
				journeyPattern.setPublishedName(route.getPublishedName());

				// Create stop point
				String chouetteStopPointId = ObjectIdCreator.createStopPointId(configuration,routeKey,routeSegment.getSequenceNumberStop());

				// Might return null if invalid stopPoint
				StopPoint stopPoint = createStopPoint(referential, context, routeSegment, chouetteStopPointId);

				if (stopPoint != null) {
					// Add stop point to journey pattern AND route (for now)
					journeyPattern.addStopPoint(stopPoint);
					route.getStopPoints().add(stopPoint);
				}
			}
		}

		sortStopPoints(referential);
		updateRouteNames(referential, configuration);
		linkOppositeRoutes(referential, configuration);

	}

	protected StopPoint createStopPoint(Referential referential, Context context, AbstractRegtoppRouteTMS routeSegment, String chouetteStopPointId)
			throws Exception {

		RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);

		StopPoint stopPoint = ObjectFactory.getStopPoint(referential, chouetteStopPointId);
		stopPoint.setPosition(Integer.parseInt(routeSegment.getSequenceNumberStop()));

		String chouetteStopAreaId = ObjectIdCreator.createStopAreaId(configuration, routeSegment.getStopId() + RegtoppStopParser.BOARDING_POSITION_ID_SUFFIX);

		StopArea stopArea = ObjectFactory.getStopArea(referential, chouetteStopAreaId);

		stopPoint.setContainedInStopArea(stopArea);

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
