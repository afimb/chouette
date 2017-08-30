package mobi.chouette.exchange.regtopp.importer.parser.v12;

import lombok.extern.log4j.Log4j;
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
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDestinationDST;
import mobi.chouette.model.*;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import static mobi.chouette.common.Constant.*;

@Log4j
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
		if(line.getTransportModeName() == null) {
			line.setTransportModeName(TransportModeNameEnum.Other);
		}
		List<Footnote> footnotes = line.getFootnotes();


		// Add routes and journey patterns
		Index<AbstractRegtoppRouteTMS> routeIndex = importer.getRouteIndex();
		Index<RegtoppDestinationDST> destinationById = importer.getDestinationById();

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
				log.debug("Created chouette stop point ID: " + chouetteStopPointId);

				// Might return null if invalid stopPoint
				StopPoint stopPoint = createStopPoint(referential, context, routeSegment, chouetteStopPointId,destinationById);

				if (stopPoint != null) {
					// Add stop point to journey pattern AND route (for now)
					journeyPattern.addStopPoint(stopPoint);
					stopPoint.setRoute(route);
				}
			}
		}

		sortStopPointsAndAddDepartureDestinationDisplay(referential,configuration);
		updateRouteNames(referential, configuration);
		linkOppositeRoutes(referential, configuration);

	}

	protected StopPoint createStopPoint(Referential referential, Context context, AbstractRegtoppRouteTMS routeSegment, String chouetteStopPointId, Index<RegtoppDestinationDST> destinationById)
			throws Exception {

		RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);

		String chouetteStopAreaId = ObjectIdCreator.createQuayId(configuration, routeSegment.getStopId());
		log.debug("Created chouette stop area ID with suffix from route segment stop ID: " + chouetteStopAreaId);
		if(referential.getSharedStopAreas().containsKey(chouetteStopAreaId)) {
			log.debug("Referential contains key for created chouette stop area ID " + chouetteStopAreaId + " in shared stop areas.");
			StopArea stopArea = ObjectFactory.getStopArea(referential, chouetteStopAreaId);
			
			StopPoint stopPoint = ObjectFactory.getStopPoint(referential, chouetteStopPointId);
			stopPoint.setPosition(Integer.parseInt(routeSegment.getSequenceNumberStop()));
			
			appendDestinationDisplay(referential, routeSegment, destinationById, configuration, stopPoint);

			// NRP 1692 not sure about this, which id should we use for scheduled stop point
			ScheduledStopPoint scheduledStopPoint = ObjectFactory.getScheduledStopPoint(referential, chouetteStopPointId);
			stopPoint.setScheduledStopPoint(scheduledStopPoint);
			scheduledStopPoint.setContainedInStopArea(stopArea);
			return stopPoint;
		} else {
			log.warn("StopPoint "+chouetteStopPointId+" refers to unknown StopArea "+chouetteStopAreaId+" - skipping");
		}

		return null;
	}

	protected void appendDestinationDisplay(Referential referential, AbstractRegtoppRouteTMS routeSegment, Index<RegtoppDestinationDST> destinationById,
			RegtoppImportParameters configuration, StopPoint stopPoint) {
		RegtoppDestinationDST dst = destinationById.getValue(routeSegment.getDestinationId());
		if(dst != null) {
			DestinationDisplay destinationDisplay = ObjectFactory.getDestinationDisplay(referential, ObjectIdCreator.composeGenericObjectId(configuration.getObjectIdPrefix(), DestinationDisplay.DESTINATIONDISPLAY_KEY, routeSegment.getDestinationId()));
			if(!destinationDisplay.isFilled()) {
				String val = StringUtils.trimToNull(dst.getDestinationText());
				if(val != null) {
					destinationDisplay.setName(val);
					destinationDisplay.setFrontText(val);
					destinationDisplay.setFilled(true);
				}
			}
			stopPoint.setDestinationDisplay(destinationDisplay);
		}
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
