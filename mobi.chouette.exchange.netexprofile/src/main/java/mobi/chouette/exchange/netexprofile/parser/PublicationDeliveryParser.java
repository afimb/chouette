package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.util.NetexObjectUtil;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Route;
import mobi.chouette.model.*;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingAlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;
import mobi.chouette.model.util.Referential;
import org.apache.commons.collections.CollectionUtils;
import org.rutebanken.netex.model.*;
import org.rutebanken.netex.model.Network;

import javax.xml.bind.JAXBElement;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Log4j
public class PublicationDeliveryParser extends NetexParser implements Parser, Constant {

	static final String LOCAL_CONTEXT = "PublicationDelivery";
	static final String COMPOSITE_FRAME = "compositeFrame";
	static final String TIMETABLE_FRAME = "timetableFrame";
	static final String SERVICE_CALENDAR_FRAME = "serviceCalendarFrame";

	@Override
	public void parse(Context context) throws Exception {
		boolean isCommonDelivery = context.get(NETEX_WITH_COMMON_DATA) != null && context.get(NETEX_LINE_DATA_JAVA) == null;
		Referential referential = (Referential) context.get(REFERENTIAL);
        String contextKey = isCommonDelivery ? NETEX_COMMON_DATA_JAVA : NETEX_LINE_DATA_JAVA;
		PublicationDeliveryStructure publicationDelivery = (PublicationDeliveryStructure) context.get(contextKey);
		List<JAXBElement<? extends Common_VersionFrameStructure>> dataObjectFrames = publicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame();
		List<CompositeFrame> compositeFrames = NetexObjectUtil.getFrames(CompositeFrame.class, dataObjectFrames);

		if(compositeFrames.size() > 0) {
			
			// Parse inside a composite frame
			for(CompositeFrame compositeFrame : compositeFrames) {

				parseValidityConditionsInFrame(context, compositeFrame);

				List<JAXBElement<? extends Common_VersionFrameStructure>> frames = compositeFrame.getFrames().getCommonFrame();
				List<ResourceFrame> resourceFrames = NetexObjectUtil.getFrames(ResourceFrame.class, frames);
				List<ServiceFrame> serviceFrames = NetexObjectUtil.getFrames(ServiceFrame.class, frames);
				List<SiteFrame> siteFrames = NetexObjectUtil.getFrames(SiteFrame.class, frames);
				List<ServiceCalendarFrame> serviceCalendarFrames = NetexObjectUtil.getFrames(ServiceCalendarFrame.class, frames);

				// pre processing
				preParseReferentialDependencies(context, serviceFrames , isCommonDelivery);

				// normal processing
				parseResourceFrames(context, resourceFrames);
				parseSiteFrames(context, siteFrames);
				parseServiceFrames(context, serviceFrames , isCommonDelivery);
				parseServiceCalendarFrame(context, serviceCalendarFrames);

				if (!isCommonDelivery) {
					List<TimetableFrame> timetableFrames = NetexObjectUtil.getFrames(TimetableFrame.class, frames);
					parseTimetableFrames(context, timetableFrames);
				}
			}
		} else {

			// Not using composite frame
			List<ResourceFrame> resourceFrames = NetexObjectUtil.getFrames(ResourceFrame.class, dataObjectFrames);
			List<ServiceFrame> serviceFrames = NetexObjectUtil.getFrames(ServiceFrame.class, dataObjectFrames);
			List<SiteFrame> siteFrames = NetexObjectUtil.getFrames(SiteFrame.class, dataObjectFrames);
			List<ServiceCalendarFrame> serviceCalendarFrames = NetexObjectUtil.getFrames(ServiceCalendarFrame.class, dataObjectFrames);

			// pre processing
			preParseReferentialDependencies(context, serviceFrames, isCommonDelivery);

			// normal processing
			parseResourceFrames(context, resourceFrames);
			parseSiteFrames(context, siteFrames);
			parseServiceFrames(context, serviceFrames, isCommonDelivery);
			parseServiceCalendarFrame(context, serviceCalendarFrames);

			if (!isCommonDelivery) {
				List<TimetableFrame> timetableFrames = NetexObjectUtil.getFrames(TimetableFrame.class, dataObjectFrames);
				parseTimetableFrames(context, timetableFrames);
			}
		}

		// post processing
		sortStopPoints(referential);
		updateBoardingAlighting(referential);
	}

	private void preParseReferentialDependencies(Context context, List<ServiceFrame> serviceFrames, boolean isCommonDelivery) throws Exception {
		for (ServiceFrame serviceFrame : serviceFrames) {

			// pre parsing route points
			if (serviceFrame.getRoutePoints() != null) {
				context.put(NETEX_LINE_DATA_CONTEXT, serviceFrame.getRoutePoints());
				ParserFactory.create(RoutePointParser.class.getName()).parse(context);
			}

			// stop assignments
			if (serviceFrame.getStopAssignments() != null) {
				context.put(NETEX_LINE_DATA_CONTEXT, serviceFrame.getStopAssignments());
				ParserFactory.create(StopAssignmentParser.class.getName()).parse(context);
			}

			if (!isCommonDelivery) {
				// preparsing mandatory for stop places to parse correctly
				TariffZonesInFrame_RelStructure tariffZonesStruct = serviceFrame.getTariffZones();
				if (tariffZonesStruct != null) {
					context.put(NETEX_LINE_DATA_CONTEXT, tariffZonesStruct);
					StopPlaceParser stopPlaceParser = (StopPlaceParser) ParserFactory.create(StopPlaceParser.class.getName());
					stopPlaceParser.parse(context);
				}
			}
        }
    }

    private void parseResourceFrames(Context context, List<ResourceFrame> resourceFrames) throws Exception {
		for (ResourceFrame resourceFrame : resourceFrames) {
			OrganisationsInFrame_RelStructure organisationsInFrameStruct = resourceFrame.getOrganisations();
			if (organisationsInFrameStruct != null) {
				context.put(NETEX_LINE_DATA_CONTEXT, organisationsInFrameStruct);
				OrganisationParser organisationParser = (OrganisationParser) ParserFactory.create(OrganisationParser.class.getName());
				organisationParser.parse(context);
			}
		}
	}

	private void parseSiteFrames(Context context, List<SiteFrame> siteFrames) throws Exception {
		for (SiteFrame siteFrame : siteFrames) {
            StopPlacesInFrame_RelStructure stopPlacesStruct = siteFrame.getStopPlaces();
			if (stopPlacesStruct != null) {
				context.put(NETEX_LINE_DATA_CONTEXT, stopPlacesStruct);
				StopPlaceParser stopPlaceParser = (StopPlaceParser) ParserFactory.create(StopPlaceParser.class.getName());
				stopPlaceParser.parse(context);
			}
		}
	}

	private void parseServiceFrames(Context context, List<ServiceFrame> serviceFrames, boolean isCommonDelivery) throws Exception {
		for (ServiceFrame serviceFrame : serviceFrames) {
			if (!isCommonDelivery) {
				Network network = serviceFrame.getNetwork();
				context.put(NETEX_LINE_DATA_CONTEXT, network);
				NetworkParser networkParser = (NetworkParser) ParserFactory.create(NetworkParser.class.getName());
				networkParser.parse(context);

				LinesInFrame_RelStructure linesInFrameStruct = serviceFrame.getLines();
				context.put(NETEX_LINE_DATA_CONTEXT, linesInFrameStruct);
				LineParser lineParser = (LineParser) ParserFactory.create(LineParser.class.getName());
				lineParser.parse(context);

				RoutesInFrame_RelStructure routesInFrameStruct = serviceFrame.getRoutes();
				context.put(NETEX_LINE_DATA_CONTEXT, routesInFrameStruct);
				RouteParser routeParser = (RouteParser) ParserFactory.create(RouteParser.class.getName());
				routeParser.parse(context);
			}

			if (!isCommonDelivery) {
				JourneyPatternsInFrame_RelStructure journeyPatternStruct = serviceFrame.getJourneyPatterns();
				context.put(NETEX_LINE_DATA_CONTEXT, journeyPatternStruct);
				JourneyPatternParser journeyPatternParser = (JourneyPatternParser) ParserFactory.create(JourneyPatternParser.class.getName());
                journeyPatternParser.parse(context);

				TransfersInFrame_RelStructure connectionsStruct = serviceFrame.getConnections();
				if (connectionsStruct != null) {
					// TODO implement connection link parser
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void parseServiceCalendarFrame(Context context, List<ServiceCalendarFrame> serviceCalendarFrames) throws Exception {
		for (ServiceCalendarFrame serviceCalendarFrame : serviceCalendarFrames) {

			parseValidityConditionsInFrame(context, serviceCalendarFrame);

			// parse day type assignments

			if (serviceCalendarFrame.getDayTypeAssignments() != null) {
				context.put(NETEX_LINE_DATA_CONTEXT, serviceCalendarFrame.getDayTypeAssignments());
				ParserFactory.create(DayTypeAssignmentParser.class.getName()).parse(context);
			}

			ServiceCalendar serviceCalendar = serviceCalendarFrame.getServiceCalendar();

			// TODO consider removing this, because parsing is done in ServiceCalendarParser
			if (serviceCalendar != null && serviceCalendar.getDayTypeAssignments() != null) {
				context.put(NETEX_LINE_DATA_CONTEXT, serviceCalendar.getDayTypeAssignments());
				ParserFactory.create(DayTypeAssignmentParser.class.getName()).parse(context);
			}

			// parse service calendar if present

			if (serviceCalendar != null) {
				context.put(NETEX_LINE_DATA_CONTEXT, serviceCalendar);
				ParserFactory.create(ServiceCalendarParser.class.getName()).parse(context);
			}

			// parse day types

			if (serviceCalendarFrame.getDayTypes() != null) {
				context.put(NETEX_LINE_DATA_CONTEXT, serviceCalendarFrame.getDayTypes());
				ParserFactory.create(DayTypeParser.class.getName()).parse(context);
			} else {
				throw new RuntimeException("Only able to parse DayType elements for now");
			}
		}
	}

	private void parseTimetableFrames(Context context, List<TimetableFrame> timetableFrames) throws Exception {
		for (TimetableFrame timetableFrame : timetableFrames) {

			parseValidityConditionsInFrame(context, timetableFrame);

			JourneysInFrame_RelStructure vehicleJourneysStruct = timetableFrame.getVehicleJourneys();
			context.put(NETEX_LINE_DATA_CONTEXT, vehicleJourneysStruct);
			Parser serviceJourneyParser = ParserFactory.create(ServiceJourneyParser.class.getName());
			serviceJourneyParser.parse(context);
		}
	}

	private void parseValidityConditionsInFrame(Context context, Common_VersionFrameStructure frameStruct) throws Exception {
		if (frameStruct instanceof CompositeFrame) {
			parseValidityConditionsInFrame(context, COMPOSITE_FRAME, frameStruct);
		} else if (frameStruct instanceof TimetableFrame) {
			parseValidityConditionsInFrame(context, TIMETABLE_FRAME, frameStruct);
		} else if (frameStruct instanceof ServiceCalendarFrame) {
			parseValidityConditionsInFrame(context, SERVICE_CALENDAR_FRAME, frameStruct);
		}
	}

	private void parseValidityConditionsInFrame(Context context, String contextKey, Common_VersionFrameStructure frameStruct) throws Exception {
		if (frameStruct.getContentValidityConditions() != null) {
			ValidBetween validBetween = getValidBetween(frameStruct.getContentValidityConditions());
			if (validBetween != null) {
				addValidBetween(context, contextKey, validBetween);
			}
		} else if (frameStruct.getValidityConditions() != null) {
			ValidBetween validBetween = getValidBetween(frameStruct.getValidityConditions());
			if (validBetween != null) {
				addValidBetween(context, contextKey, validBetween);
			}
		} else if (CollectionUtils.isNotEmpty(frameStruct.getValidBetween())) {
			ValidBetween validBetween = getValidBetween(frameStruct.getValidBetween());
			if (validBetween != null) {
				addValidBetween(context, contextKey, validBetween);
			}
		}
	}

	// TODO add support for multiple validity conditions
	private void addValidBetween(Context context, String contextKey, ValidBetween validBetween) {
		Context localContext = getLocalContext(context, LOCAL_CONTEXT);
		localContext.put(contextKey, validBetween);
	}

	protected void sortStopPoints(Referential referential) {
		// Sort stopPoints on JourneyPattern
		Collection<JourneyPattern> journeyPatterns = referential.getJourneyPatterns().values();
		for (JourneyPattern jp : journeyPatterns) {
			List<StopPoint> stopPoints = jp.getStopPoints();
			stopPoints.sort(Comparator.comparing(StopPoint::getPosition));
			jp.setDepartureStopPoint(stopPoints.get(0));
			jp.setArrivalStopPoint(stopPoints.get(stopPoints.size() - 1));
		}

		// Sort stopPoints on route
		Collection<Route> routes = referential.getRoutes().values();
		for (Route r : routes) {
			List<StopPoint> stopPoints = r.getStopPoints();
			stopPoints.sort(Comparator.comparing(StopPoint::getPosition));
		}
	}

	private void updateBoardingAlighting(Referential referential) {

		for (Route route : referential.getRoutes().values()) {
			boolean invalidData = false;
			boolean usefullData = false;

			b1: for (JourneyPattern jp : route.getJourneyPatterns()) {
				for (VehicleJourney vj : jp.getVehicleJourneys()) {
					for (VehicleJourneyAtStop vjas : vj.getVehicleJourneyAtStops()) {
						if (!updateStopPoint(vjas)) {
							invalidData = true;
							break b1;
						}
					}
				}
			}
			if (!invalidData) {
				// check if every stoppoints were updated, complete missing ones to
				// normal; if all normal clean all
				for (StopPoint sp : route.getStopPoints()) {
					if (sp.getForAlighting() == null)
						sp.setForAlighting(AlightingPossibilityEnum.normal);
					if (sp.getForBoarding() == null)
						sp.setForBoarding(BoardingPossibilityEnum.normal);
				}
				for (StopPoint sp : route.getStopPoints()) {
					if (!sp.getForAlighting().equals(AlightingPossibilityEnum.normal)) {
						usefullData = true;
						break;
					}
					if (!sp.getForBoarding().equals(BoardingPossibilityEnum.normal)) {
						usefullData = true;
						break;
					}
				}

			}
			if (invalidData || !usefullData) {
				// remove useless informations
				for (StopPoint sp : route.getStopPoints()) {
					sp.setForAlighting(null);
					sp.setForBoarding(null);
				}
			}

		}
	}

	private boolean updateStopPoint(VehicleJourneyAtStop vjas) {
		StopPoint sp = vjas.getStopPoint();
		BoardingPossibilityEnum forBoarding = getForBoarding(vjas.getBoardingAlightingPossibility());
		AlightingPossibilityEnum forAlighting = getForAlighting(vjas.getBoardingAlightingPossibility());
		if (sp.getForBoarding() != null && !sp.getForBoarding().equals(forBoarding))
			return false;
		if (sp.getForAlighting() != null && !sp.getForAlighting().equals(forAlighting))
			return false;
		sp.setForBoarding(forBoarding);
		sp.setForAlighting(forAlighting);
		return true;
	}

	private AlightingPossibilityEnum getForAlighting(BoardingAlightingPossibilityEnum boardingAlightingPossibility) {
		if (boardingAlightingPossibility == null)
			return AlightingPossibilityEnum.normal;
		switch (boardingAlightingPossibility) {
		case BoardAndAlight:
			return AlightingPossibilityEnum.normal;
		case AlightOnly:
			return AlightingPossibilityEnum.normal;
		case BoardOnly:
			return AlightingPossibilityEnum.forbidden;
		case NeitherBoardOrAlight:
			return AlightingPossibilityEnum.forbidden;
		case BoardAndAlightOnRequest:
			return AlightingPossibilityEnum.request_stop;
		case AlightOnRequest:
			return AlightingPossibilityEnum.request_stop;
		case BoardOnRequest:
			return AlightingPossibilityEnum.normal;
		}
		return null;
	}

	private BoardingPossibilityEnum getForBoarding(BoardingAlightingPossibilityEnum boardingAlightingPossibility) {
		if (boardingAlightingPossibility == null)
			return BoardingPossibilityEnum.normal;
		switch (boardingAlightingPossibility) {
		case BoardAndAlight:
			return BoardingPossibilityEnum.normal;
		case AlightOnly:
			return BoardingPossibilityEnum.forbidden;
		case BoardOnly:
			return BoardingPossibilityEnum.normal;
		case NeitherBoardOrAlight:
			return BoardingPossibilityEnum.forbidden;
		case BoardAndAlightOnRequest:
			return BoardingPossibilityEnum.request_stop;
		case AlightOnRequest:
			return BoardingPossibilityEnum.normal;
		case BoardOnRequest:
			return BoardingPossibilityEnum.request_stop;
		}
		return null;
	}

	static {
		ParserFactory.register(PublicationDeliveryParser.class.getName(), new ParserFactory() {
			private PublicationDeliveryParser instance = new PublicationDeliveryParser();

			@Override
			protected Parser create() {
				return instance;
			}
		});
	}

}
