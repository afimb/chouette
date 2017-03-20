package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils;
import mobi.chouette.exchange.netexprofile.importer.util.NetexObjectUtil;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Route;
import mobi.chouette.model.*;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.ObjectIdTypes;
import mobi.chouette.model.util.Referential;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.*;
import org.rutebanken.netex.model.Line;
import org.rutebanken.netex.model.Network;

import javax.xml.bind.JAXBElement;
import java.util.*;

import static mobi.chouette.exchange.netexprofile.parser.NetexParserUtils.netexId;

@Log4j
public class PublicationDeliveryParser extends NetexParser implements Parser, Constant {

	static final String LOCAL_CONTEXT = "PublicationDelivery";
	static final String COMPOSITE_FRAME = "compositeFrame";
	static final String TIMETABLE_FRAME = "timetableFrame";
	static final String SERVICE_CALENDAR_FRAME = "serviceCalendarFrame";
	static final String TIMETABLE_ID = "timetableId";

	@Override
	public void parse(Context context) throws Exception {
		boolean isCommonDelivery = (boolean) context.get(NETEX_WITH_COMMON_DATA);
		Referential referential = (Referential) context.get(REFERENTIAL);
 		PublicationDeliveryStructure publicationDelivery = (PublicationDeliveryStructure) context.get(NETEX_DATA_JAVA);
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
				List<TimetableFrame> timetableFrames = NetexObjectUtil.getFrames(TimetableFrame.class, frames);

				// pre processing
				preParseReferentialDependencies(context, referential, serviceFrames, timetableFrames, isCommonDelivery);

				// normal processing
				parseResourceFrames(context, resourceFrames);
				parseSiteFrames(context, siteFrames);
				parseServiceFrames(context, serviceFrames , isCommonDelivery);
				parseServiceCalendarFrame(context, serviceCalendarFrames);

				if (!isCommonDelivery) {
					//List<TimetableFrame> timetableFrames = NetexObjectUtil.getFrames(TimetableFrame.class, frames);
					parseTimetableFrames(context, timetableFrames);
				}
			}
		} else {

			// Not using composite frame
			List<ResourceFrame> resourceFrames = NetexObjectUtil.getFrames(ResourceFrame.class, dataObjectFrames);
			List<ServiceFrame> serviceFrames = NetexObjectUtil.getFrames(ServiceFrame.class, dataObjectFrames);
			List<SiteFrame> siteFrames = NetexObjectUtil.getFrames(SiteFrame.class, dataObjectFrames);
			List<ServiceCalendarFrame> serviceCalendarFrames = NetexObjectUtil.getFrames(ServiceCalendarFrame.class, dataObjectFrames);
			List<TimetableFrame> timetableFrames = NetexObjectUtil.getFrames(TimetableFrame.class, dataObjectFrames);

			// pre processing
			preParseReferentialDependencies(context, referential, serviceFrames, timetableFrames, isCommonDelivery);

			// normal processing
			parseResourceFrames(context, resourceFrames);
			parseSiteFrames(context, siteFrames);
			parseServiceFrames(context, serviceFrames, isCommonDelivery);
			parseServiceCalendarFrame(context, serviceCalendarFrames);

			if (!isCommonDelivery) {
				//List<TimetableFrame> timetableFrames = NetexObjectUtil.getFrames(TimetableFrame.class, dataObjectFrames);
				parseTimetableFrames(context, timetableFrames);
			}
		}

		// post processing
		sortStopPoints(referential);
		updateBoardingAlighting(referential);
	}

	private void preParseReferentialDependencies(Context context, Referential referential, List<ServiceFrame> serviceFrames,
			List<TimetableFrame> timetableFrames, boolean isCommonDelivery) throws Exception {

		Line line = null;

		for (ServiceFrame serviceFrame : serviceFrames) {

			// pre parsing route points
			if (serviceFrame.getRoutePoints() != null) {
				context.put(NETEX_LINE_DATA_CONTEXT, serviceFrame.getRoutePoints());
				Parser routePointParser = ParserFactory.create(RoutePointParser.class.getName());
				routePointParser.parse(context);
			}

			// stop assignments
			if (serviceFrame.getStopAssignments() != null) {
				context.put(NETEX_LINE_DATA_CONTEXT, serviceFrame.getStopAssignments());
				Parser stopAssignmentParser = ParserFactory.create(StopAssignmentParser.class.getName());
				stopAssignmentParser.parse(context);
			}

			if (!isCommonDelivery) {
				if (line == null) {
					line = (Line) serviceFrame.getLines().getLine_().get(0).getValue();
				}

				// preparsing mandatory for stop places to parse correctly
				TariffZonesInFrame_RelStructure tariffZonesStruct = serviceFrame.getTariffZones();
				if (tariffZonesStruct != null) {
					context.put(NETEX_LINE_DATA_CONTEXT, tariffZonesStruct);
					StopPlaceParser stopPlaceParser = (StopPlaceParser) ParserFactory.create(StopPlaceParser.class.getName());
					stopPlaceParser.parse(context);
				}
			}
        }

		if (!isCommonDelivery) {
			Map<String, Set<String>> journeyDayTypeIdMap = new HashMap<>();

			for (TimetableFrame timetableFrame : timetableFrames) {
				for (Journey_VersionStructure journeyStruct : timetableFrame.getVehicleJourneys().getDatedServiceJourneyOrDeadRunOrServiceJourney()) {
					ServiceJourney serviceJourney = (ServiceJourney) journeyStruct;
					Set<String> dayTypeIds = new HashSet<>();

					for (JAXBElement<? extends DayTypeRefStructure> dayTypeRefStructElement : serviceJourney.getDayTypes().getDayTypeRef()) {
						dayTypeIds.add(dayTypeRefStructElement.getValue().getRef());
					}

					journeyDayTypeIdMap.put(serviceJourney.getId(), dayTypeIds);
				}
			}

			Set<String> processedIds = new HashSet<>();
			List<Set<String>> calendarGroups = new ArrayList<>();

			for (Map.Entry<String, Set<String>> entry1 : journeyDayTypeIdMap.entrySet()) {
				if (!processedIds.contains(entry1.getKey())) {
					Set<String> groupedJourneyIds = new HashSet<>();
					groupedJourneyIds.add(entry1.getKey());

					for (Map.Entry<String, Set<String>> entry2 : journeyDayTypeIdMap.entrySet()) {
						if (!entry1.getKey().equals(entry2.getKey())) {
							if (CollectionUtils.isEqualCollection(entry1.getValue(), entry2.getValue())) {
								groupedJourneyIds.add(entry2.getKey());
								processedIds.add(entry2.getKey());
							}
						}
					}
					calendarGroups.add(groupedJourneyIds);
					processedIds.add(entry1.getKey());
				}
			}

			assert line != null;
			String[] idParts = StringUtils.split(line.getId(), ":");
			String[] idSequence = NetexProducerUtils.generateIdSequence(calendarGroups.size());

			for (int i = 0; i < calendarGroups.size(); i++) {
				String timetableIdSuffix = idParts[2] + "-" + StringUtils.leftPad(idSequence[i], 2, "0");
				String timetableId = netexId(idParts[0], ObjectIdTypes.TIMETABLE_KEY, timetableIdSuffix);
				Timetable timetable = ObjectFactory.getTimetable(referential, timetableId);

				for (String journeyId : calendarGroups.get(i)) {
					addTimetableId(context, journeyId, timetable.getObjectId());
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

	private void parseServiceCalendarFrame(Context context, List<ServiceCalendarFrame> serviceCalendarFrames) throws Exception {
		for (ServiceCalendarFrame serviceCalendarFrame : serviceCalendarFrames) {

			parseValidityConditionsInFrame(context, serviceCalendarFrame);

			context.put(NETEX_LINE_DATA_CONTEXT, serviceCalendarFrame);
			Parser serviceCalendarParser = ParserFactory.create(ServiceCalendarParser.class.getName());
			serviceCalendarParser.parse(context);
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

	private void addValidBetween(Context context, String contextKey, ValidBetween validBetween) {
		Context localContext = getLocalContext(context, LOCAL_CONTEXT);

		if (localContext.containsKey(contextKey)) {
			localContext.replace(contextKey, validBetween);
		} else {
			localContext.put(contextKey, validBetween);
		}
	}

	private void addTimetableId(Context context, String objectId, String timetableId) {
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(TIMETABLE_ID, timetableId);
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
		BoardingPossibilityEnum forBoarding = NetexParserUtils.getForBoarding(vjas.getBoardingAlightingPossibility());
		AlightingPossibilityEnum forAlighting = NetexParserUtils.getForAlighting(vjas.getBoardingAlightingPossibility());
		if (sp.getForBoarding() != null && !sp.getForBoarding().equals(forBoarding))
			return false;
		if (sp.getForAlighting() != null && !sp.getForAlighting().equals(forAlighting))
			return false;
		sp.setForBoarding(forBoarding);
		sp.setForAlighting(forAlighting);
		return true;
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
