package mobi.chouette.exchange.netexprofile.parser;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;

import org.apache.commons.collections.CollectionUtils;
import org.rutebanken.netex.model.JourneyPattern_VersionStructure;
import org.rutebanken.netex.model.JourneyPatternsInFrame_RelStructure;
import org.rutebanken.netex.model.LinkInLinkSequence_VersionedChildStructure;
import org.rutebanken.netex.model.PointInLinkSequence_VersionedChildStructure;
import org.rutebanken.netex.model.ScheduledStopPointRefStructure;
import org.rutebanken.netex.model.ServiceLinkInJourneyPattern_VersionedChildStructure;
import org.rutebanken.netex.model.StopPointInJourneyPattern;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.TimeUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.BookingArrangement;
import mobi.chouette.model.DestinationDisplay;
import mobi.chouette.model.Route;
import mobi.chouette.model.ScheduledStopPoint;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;
import mobi.chouette.model.type.SectionStatusEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import org.apache.commons.collections.CollectionUtils;
import org.rutebanken.netex.model.BookingArrangementsStructure;
import org.rutebanken.netex.model.JourneyPattern_VersionStructure;
import org.rutebanken.netex.model.JourneyPatternsInFrame_RelStructure;
import org.rutebanken.netex.model.LinkInLinkSequence_VersionedChildStructure;
import org.rutebanken.netex.model.PointInLinkSequence_VersionedChildStructure;
import org.rutebanken.netex.model.ScheduledStopPointRefStructure;
import org.rutebanken.netex.model.ServiceLinkInJourneyPattern_VersionedChildStructure;
import org.rutebanken.netex.model.StopPointInJourneyPattern;

@Log4j
public class JourneyPatternParser extends NetexParser implements Parser, Constant {


	private ContactStructureParser contactStructureParser = new ContactStructureParser();

	@Override
	public void parse(Context context) throws Exception {
		Referential referential = (Referential) context.get(REFERENTIAL);
		JourneyPatternsInFrame_RelStructure journeyPatternStruct = (JourneyPatternsInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);

		for (JAXBElement<?> journeyPatternElement : journeyPatternStruct.getJourneyPattern_OrJourneyPatternView()) {
			JourneyPattern_VersionStructure netexJourneyPattern = (org.rutebanken.netex.model.JourneyPattern_VersionStructure) journeyPatternElement.getValue();

			mobi.chouette.model.JourneyPattern chouetteJourneyPattern = ObjectFactory.getJourneyPattern(referential, netexJourneyPattern.getId());

			chouetteJourneyPattern.setObjectVersion(NetexParserUtils.getVersion(netexJourneyPattern));

			String routeIdRef = netexJourneyPattern.getRouteRef().getRef();
			mobi.chouette.model.Route route = ObjectFactory.getRoute(referential, routeIdRef);
			chouetteJourneyPattern.setRoute(route);

			if (netexJourneyPattern.getName() != null) {
				chouetteJourneyPattern.setName(netexJourneyPattern.getName().getValue());
			} else {
				chouetteJourneyPattern.setName(route.getName());
			}

			if (netexJourneyPattern.getPrivateCode() != null) {
				chouetteJourneyPattern.setRegistrationNumber(netexJourneyPattern.getPrivateCode().getValue());
			}

			parseStopPointsInJourneyPattern(context, referential, netexJourneyPattern, chouetteJourneyPattern, route.getStopPoints());
			parseServiceLinksInJourneyPattern(referential, netexJourneyPattern, chouetteJourneyPattern);
			chouetteJourneyPattern.setFilled(true);
		}
	}

	private void parseServiceLinksInJourneyPattern(Referential referential, org.rutebanken.netex.model.JourneyPattern_VersionStructure netexJourneyPattern,
												   mobi.chouette.model.JourneyPattern chouetteJourneyPattern) {

		if (netexJourneyPattern.getLinksInSequence()==null || netexJourneyPattern.getLinksInSequence().getServiceLinkInJourneyPatternOrTimingLinkInJourneyPattern()==null) {
			return;
		}
		List<LinkInLinkSequence_VersionedChildStructure> linksInLinkSequence = netexJourneyPattern.getLinksInSequence()
				.getServiceLinkInJourneyPatternOrTimingLinkInJourneyPattern();

		for (LinkInLinkSequence_VersionedChildStructure linkInLinkSequence : linksInLinkSequence){
			if (linkInLinkSequence instanceof ServiceLinkInJourneyPattern_VersionedChildStructure) {

				ServiceLinkInJourneyPattern_VersionedChildStructure serviceLinkInJourneyPattern= (ServiceLinkInJourneyPattern_VersionedChildStructure) linkInLinkSequence;

				if (serviceLinkInJourneyPattern.getServiceLinkRef()!=null && serviceLinkInJourneyPattern.getServiceLinkRef().getRef()!=null){
					chouetteJourneyPattern.getRouteSections().add(ObjectFactory.getRouteSection(referential, serviceLinkInJourneyPattern.getServiceLinkRef().getRef()));
				}
			} else {
				log.warn("Got unexpected linkInLinkSequence element: " + linkInLinkSequence);
			}

		}

		if (chouetteJourneyPattern.hasCompleteRouteSections()) {
			chouetteJourneyPattern.setSectionStatus(SectionStatusEnum.Completed);
		}

	}

	private void parseStopPointsInJourneyPattern(Context context, Referential referential, org.rutebanken.netex.model.JourneyPattern_VersionStructure netexJourneyPattern,
			mobi.chouette.model.JourneyPattern chouetteJourneyPattern, List<StopPoint> routeStopPoints) throws Exception {

		List<PointInLinkSequence_VersionedChildStructure> pointsInLinkSequence = netexJourneyPattern.getPointsInSequence()
				.getPointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern();

		for (int i = 0; i < pointsInLinkSequence.size(); i++) {
			PointInLinkSequence_VersionedChildStructure pointInSequence = pointsInLinkSequence.get(i);
			StopPointInJourneyPattern pointInPattern = (StopPointInJourneyPattern) pointInSequence;

			StopPoint stopPointInJourneyPattern = ObjectFactory.getStopPoint(referential, pointInPattern.getId());
			ScheduledStopPointRefStructure scheduledStopPointRef = pointInPattern.getScheduledStopPointRef().getValue();

			ScheduledStopPoint scheduledStopPoint=ObjectFactory.getScheduledStopPoint(referential, scheduledStopPointRef.getRef());
			stopPointInJourneyPattern.setScheduledStopPoint(scheduledStopPoint);
			stopPointInJourneyPattern.setPosition(pointInPattern.getOrder().intValue());
			stopPointInJourneyPattern.setObjectVersion(NetexParserUtils.getVersion(pointInPattern.getVersion()));

			if (pointInPattern.isForAlighting() != null && !pointInPattern.isForAlighting()) {
				stopPointInJourneyPattern.setForAlighting(AlightingPossibilityEnum.forbidden);
			} else if (Boolean.TRUE.equals(pointInPattern.isRequestStop())) {
				stopPointInJourneyPattern.setForAlighting(AlightingPossibilityEnum.request_stop);
			} else {
				stopPointInJourneyPattern.setForAlighting(AlightingPossibilityEnum.normal);
			}

			if (pointInPattern.isForBoarding() != null && !pointInPattern.isForBoarding()) {
				stopPointInJourneyPattern.setForBoarding(BoardingPossibilityEnum.forbidden);
			} else if (Boolean.TRUE.equals(pointInPattern.isRequestStop())){
				stopPointInJourneyPattern.setForBoarding(BoardingPossibilityEnum.request_stop);
			} else {
				stopPointInJourneyPattern.setForBoarding(BoardingPossibilityEnum.normal);
			}

			chouetteJourneyPattern.addStopPoint(stopPointInJourneyPattern);
			stopPointInJourneyPattern.setRoute(chouetteJourneyPattern.getRoute());

			if (pointInPattern.getDestinationDisplayRef() != null) {
				String destinationDisplayId = pointInPattern.getDestinationDisplayRef().getRef();
				DestinationDisplay destinationDisplay = ObjectFactory.getDestinationDisplay(referential, destinationDisplayId);

				// HACK TODO HACK
				// Remove Line/PublicCode from DestinationDisplay if FrontText starts with it
				String lineNumber = referential.getLines().values().iterator().next().getNumber();
				if (destinationDisplay.getFrontText().startsWith(lineNumber + " ")) {
					String modifiedDestinationDisplayId = destinationDisplayId + "-NOLINENUMBER";
					DestinationDisplay modifiedDestinationDisplay = referential.getSharedDestinationDisplays().get(modifiedDestinationDisplayId);
					if (modifiedDestinationDisplay == null) {
						modifiedDestinationDisplay = ObjectFactory.getDestinationDisplay(referential, modifiedDestinationDisplayId);
						modifiedDestinationDisplay.setName(destinationDisplay.getName() == null ? "" : destinationDisplay.getName() + " (stripped number)");
						modifiedDestinationDisplay.setFrontText(destinationDisplay.getFrontText().substring(lineNumber.length() + 1));
						modifiedDestinationDisplay.setSideText(destinationDisplay.getSideText());
						modifiedDestinationDisplay.getVias().addAll(destinationDisplay.getVias());
					}
					stopPointInJourneyPattern.setDestinationDisplay(modifiedDestinationDisplay);
				} else {
					stopPointInJourneyPattern.setDestinationDisplay(destinationDisplay);
				}
			}

			if (pointInPattern.getBookingArrangements()!=null) {
				BookingArrangementsStructure netexBookingArrangement = pointInPattern.getBookingArrangements();
				BookingArrangement bookingArrangement = new BookingArrangement();
				if (netexBookingArrangement.getBookingNote() != null) {
					bookingArrangement.setBookingNote(netexBookingArrangement.getBookingNote().getValue());
				}
				bookingArrangement.setBookingAccess(NetexParserUtils.toBookingAccess(netexBookingArrangement.getBookingAccess()));
				bookingArrangement.setBookWhen(NetexParserUtils.toPurchaseWhen(netexBookingArrangement.getBookWhen()));
				bookingArrangement.setBuyWhen(netexBookingArrangement.getBuyWhen().stream().map(NetexParserUtils::toPurchaseMoment).collect(Collectors.toList()));
				bookingArrangement.setBookingMethods(netexBookingArrangement.getBookingMethods().stream().map(NetexParserUtils::toBookingMethod).collect(Collectors.toList()));
				bookingArrangement.setLatestBookingTime(TimeUtil.toJodaLocalTime(netexBookingArrangement.getLatestBookingTime()));
				bookingArrangement.setMinimumBookingPeriod(TimeUtil.toJodaDuration(netexBookingArrangement.getMinimumBookingPeriod()));

				bookingArrangement.setBookingContact(contactStructureParser.parse(netexBookingArrangement.getBookingContact()));

				stopPointInJourneyPattern.setBookingArrangement(bookingArrangement);
			}

			chouetteJourneyPattern.addStopPoint(stopPointInJourneyPattern);
		}

		List<StopPoint> patternStopPoints = chouetteJourneyPattern.getStopPoints();
		if (CollectionUtils.isNotEmpty(patternStopPoints)) {
			chouetteJourneyPattern.getStopPoints().sort(Comparator.comparingInt(StopPoint::getPosition));
			chouetteJourneyPattern.setDepartureStopPoint(patternStopPoints.get(0));
			chouetteJourneyPattern.setArrivalStopPoint(patternStopPoints.get(patternStopPoints.size() - 1));
		}

		Route chouetteRoute = chouetteJourneyPattern.getRoute();
		chouetteRoute.getStopPoints().forEach(stopPoint -> stopPoint.setPosition(chouetteRoute.getStopPoints().indexOf(stopPoint)));
		chouetteRoute.getStopPoints().sort(Comparator.comparingInt(StopPoint::getPosition));
		chouetteRoute.setFilled(true);

	}

	static {
		ParserFactory.register(JourneyPatternParser.class.getName(), new ParserFactory() {
			private JourneyPatternParser instance = new JourneyPatternParser();

			@Override
			protected Parser create() {
				return instance;
			}
		});
	}

}
