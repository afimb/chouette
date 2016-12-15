package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.CollectionUtil;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.importer.util.NetexObjectUtil;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.exchange.netexprofile.importer.validation.norway.RoutePointValidator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingAlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;
import mobi.chouette.model.util.Referential;
import org.apache.commons.collections.CollectionUtils;
import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;

@Log4j
public class PublicationDeliveryParser extends AbstractParser {

	@SuppressWarnings("unchecked")
	public void initCommonReferentials(Context context) throws Exception {
		// TODO find out when to to this null check and clearing, do we really need it, or can we just presume its available?
		NetexReferential referential = (NetexReferential) context.get(NETEX_REFERENTIAL);

		if (referential == null) {
			referential = new NetexReferential();
			context.put(NETEX_REFERENTIAL, referential);
		} else {
			referential.clear();
		}

		PublicationDeliveryStructure publicationDelivery = (PublicationDeliveryStructure) context.get(NETEX_COMMON_DATA);
		List<JAXBElement<? extends Common_VersionFrameStructure>> dataObjectFrames = publicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame();

		List<ResourceFrame> resourceFrames = NetexObjectUtil.getFrames(ResourceFrame.class, dataObjectFrames);

		if (CollectionUtils.isNotEmpty(resourceFrames)) {
			for (ResourceFrame resourceFrame : resourceFrames) {

				// parse organisations
				OrganisationsInFrame_RelStructure organisationsInFrameStruct = resourceFrame.getOrganisations();

				if (organisationsInFrameStruct != null) {
					context.put(NETEX_LINE_DATA_CONTEXT, organisationsInFrameStruct);
					OrganisationParser organisationParser = (OrganisationParser) ParserFactory.create(OrganisationParser.class.getName());
					organisationParser.parse(context);
				}
			}
		}

		List<SiteFrame> siteFrames = NetexObjectUtil.getFrames(SiteFrame.class, dataObjectFrames);

		if (CollectionUtils.isNotEmpty(siteFrames)) {
			for (SiteFrame siteFrame : siteFrames) {

				// parse stop places
				StopPlacesInFrame_RelStructure stopPlacesStruct = siteFrame.getStopPlaces();

				if (stopPlacesStruct != null) {
					context.put(NETEX_LINE_DATA_CONTEXT, stopPlacesStruct);
					StopPlaceParser stopPlaceParser = (StopPlaceParser) ParserFactory.create(StopPlaceParser.class.getName());
					stopPlaceParser.parse(context);
				}
			}
		}

		List<ServiceFrame> serviceFrames = NetexObjectUtil.getFrames(ServiceFrame.class, dataObjectFrames);

		if (CollectionUtils.isNotEmpty(siteFrames)) {
			for (ServiceFrame serviceFrame : serviceFrames) {

				// parse stop points
				ScheduledStopPointsInFrame_RelStructure scheduledStopPointStruct = serviceFrame.getScheduledStopPoints();

				if(scheduledStopPointStruct != null) {
					context.put(NETEX_LINE_DATA_CONTEXT, scheduledStopPointStruct);
					StopPointParser stopPointParser = (StopPointParser) ParserFactory.create(StopPointParser.class.getName());
					stopPointParser.initReferentials(context);
				}

				// parse stop assignments
				StopAssignmentsInFrame_RelStructure stopAssignmentsStructure = serviceFrame.getStopAssignments();

				if (stopAssignmentsStructure != null) {
					List<JAXBElement<? extends StopAssignment_VersionStructure>> stopAssignmentElements = stopAssignmentsStructure.getStopAssignment();

					for (JAXBElement<? extends StopAssignment_VersionStructure> stopAssignmentElement : stopAssignmentElements) {
						PassengerStopAssignment passengerStopAssignment = (PassengerStopAssignment) stopAssignmentElement.getValue();
						NetexObjectUtil.addPassengerStopAssignmentReference(referential, passengerStopAssignment.getId(), passengerStopAssignment);
					}
				}
			}
		}
	}

	@Override
	public void initReferentials(Context context) throws Exception {
		NetexReferential referential = (NetexReferential) context.get(NETEX_REFERENTIAL);
		if (referential == null) {
			referential = new NetexReferential();
			context.put(NETEX_REFERENTIAL, referential);
		} else {
			referential.clear();
		}

		// TODO the problem is when we are parsing a common file, the below statement have no meaning, either create a separate method for common deliveries or make code work for both cases
		PublicationDeliveryStructure publicationDelivery = (PublicationDeliveryStructure) context.get(NETEX_LINE_DATA_JAVA);
		List<JAXBElement<? extends Common_VersionFrameStructure>> topLevelFrame = publicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame();

		initResourceFrameRefs(context, referential, topLevelFrame);
		initSiteFrameRefs(context, referential, topLevelFrame);
		initServiceCalendarFrameRefs(referential, topLevelFrame);
		initServiceFrameRefs(context, referential, topLevelFrame);
		initTimetableFrameRefs(context, referential, topLevelFrame);
	}

	private void initResourceFrameRefs(Context context, NetexReferential referential, List<JAXBElement<? extends Common_VersionFrameStructure>> topLevelFrame)
			throws Exception {
		List<ResourceFrame> resourceFrames = getFrames(ResourceFrame.class, topLevelFrame);
		for (ResourceFrame resourceFrame : resourceFrames) {
			NetexObjectUtil.addResourceFrameReference(referential, resourceFrame.getId(), resourceFrame);

			// 1. initialize organisations
			OrganisationsInFrame_RelStructure organisationsInFrameStruct = resourceFrame.getOrganisations();
			context.put(NETEX_LINE_DATA_CONTEXT, organisationsInFrameStruct);
			OrganisationParser organisationParser = (OrganisationParser) ParserFactory.create(OrganisationParser.class.getName());
			organisationParser.initReferentials(context);
		}
	}

	private void initSiteFrameRefs(Context context, NetexReferential referential, List<JAXBElement<? extends Common_VersionFrameStructure>> topLevelFrame)
			throws Exception {
		List<SiteFrame> siteFrames = getFrames(SiteFrame.class, topLevelFrame);
		for (SiteFrame siteFrame : siteFrames) {
			NetexObjectUtil.addSiteFrameReference(referential, siteFrame.getId(), siteFrame);

			// 1. initialize stop places
			StopPlacesInFrame_RelStructure stopPlacesStruct = siteFrame.getStopPlaces();
			context.put(NETEX_LINE_DATA_CONTEXT, stopPlacesStruct);
			StopPlaceParser stopPlaceParser = (StopPlaceParser) ParserFactory.create(StopPlaceParser.class.getName());
			stopPlaceParser.initReferentials(context);
		}
	}

	private void initServiceCalendarFrameRefs(NetexReferential referential, List<JAXBElement<? extends Common_VersionFrameStructure>> topLevelFrame) {
		List<ServiceCalendarFrame> serviceCalendarFrames = getFrames(ServiceCalendarFrame.class, topLevelFrame);
		for (ServiceCalendarFrame serviceCalendarFrame : serviceCalendarFrames) {
			NetexObjectUtil.addServiceCalendarFrameReference(referential, serviceCalendarFrame.getId(), serviceCalendarFrame);

			DayTypesInFrame_RelStructure dayTypeStruct = serviceCalendarFrame.getDayTypes();
			List<JAXBElement<? extends DataManagedObjectStructure>> dayTypeStructElements = dayTypeStruct.getDayType_();

			for (JAXBElement<? extends DataManagedObjectStructure> dayTypeStructElement : dayTypeStructElements) {
				DayType dayType = (DayType) dayTypeStructElement.getValue();
				NetexObjectUtil.addDayTypeReference(referential, dayType.getId(), dayType);
			}
		}
	}

	private void initServiceFrameRefs(Context context, NetexReferential referential, List<JAXBElement<? extends Common_VersionFrameStructure>> topLevelFrame)
			throws Exception {
		List<ServiceFrame> serviceFrames = getFrames(ServiceFrame.class, topLevelFrame);
		for (ServiceFrame serviceFrame : serviceFrames) {
			NetexObjectUtil.addServiceFrameReference(referential, serviceFrame.getId(), serviceFrame);

			// 1. parse networks
			Network network = serviceFrame.getNetwork();
			context.put(NETEX_LINE_DATA_CONTEXT, network);
			NetworkParser networkParser = (NetworkParser) ParserFactory.create(NetworkParser.class.getName());
			networkParser.initReferentials(context);

			// 2. parse route points
			RoutePointValidator routePointValidator = (RoutePointValidator) ValidatorFactory.create(RoutePointValidator.class.getName(), context);
			RoutePointsInFrame_RelStructure routePointsStructure = serviceFrame.getRoutePoints();
			List<RoutePoint> routePoints = routePointsStructure.getRoutePoint();

			for (RoutePoint routePoint : routePoints) {
				String objectId = routePoint.getId();

				// 1. initialize stop point references
				List<String> stopPointIds = NetexObjectUtil.getStopPointRefsOfRoutePoint(routePoint);
				for (String stopPointId : stopPointIds) {
					routePointValidator.addStopPointReference(context, objectId, stopPointId);
				}
				NetexObjectUtil.addRoutePointReference(referential, routePoint.getId(), routePoint);
				routePointValidator.addObjectReference(context, routePoint);
			}

			// 3. parse routes
			RoutesInFrame_RelStructure routesInFrameStruct = serviceFrame.getRoutes();
			context.put(NETEX_LINE_DATA_CONTEXT, routesInFrameStruct);
			RouteParser routeParser = (RouteParser) ParserFactory.create(RouteParser.class.getName());
			routeParser.initReferentials(context);

			// 4. parse lines
			LinesInFrame_RelStructure linesInFrameStruct = serviceFrame.getLines();
			context.put(NETEX_LINE_DATA_CONTEXT, linesInFrameStruct);
			LineParser lineParser = (LineParser) ParserFactory.create(LineParser.class.getName());
			lineParser.initReferentials(context);

			// 5. parse stop assignments (connection between stop points and stop places)
			StopAssignmentsInFrame_RelStructure stopAssignmentsStructure = serviceFrame.getStopAssignments();
			if (stopAssignmentsStructure != null) {
				List<JAXBElement<? extends StopAssignment_VersionStructure>> stopAssignmentElements = stopAssignmentsStructure.getStopAssignment();
				for (JAXBElement<? extends StopAssignment_VersionStructure> stopAssignmentElement : stopAssignmentElements) {
					PassengerStopAssignment passengerStopAssignment = (PassengerStopAssignment) stopAssignmentElement.getValue();
					// TODO consider generating a more sophisticated id
					NetexObjectUtil.addPassengerStopAssignmentReference(referential, passengerStopAssignment.getId(), passengerStopAssignment);
				}
			}

			// 6. parse scheduled stop points
			ScheduledStopPointsInFrame_RelStructure scheduledStopPointStruct = serviceFrame.getScheduledStopPoints();
			if(scheduledStopPointStruct != null) {
				context.put(NETEX_LINE_DATA_CONTEXT, scheduledStopPointStruct);
				StopPointParser stopPointParser = (StopPointParser) ParserFactory.create(StopPointParser.class.getName());
				stopPointParser.initReferentials(context);
			}
			/*
			 * ScheduledStopPointParser scheduledStopPointParser = (ScheduledStopPointParser) ParserFactory.create(ScheduledStopPointParser.class.getName());
			 * scheduledStopPointParser.initReferentials(context);
			 */

			// 7. parse journey patterns
			JourneyPatternsInFrame_RelStructure journeyPatternStruct = serviceFrame.getJourneyPatterns();
			context.put(NETEX_LINE_DATA_CONTEXT, journeyPatternStruct);
			JourneyPatternParser journeyPatternParser = (JourneyPatternParser) ParserFactory.create(JourneyPatternParser.class.getName());
			journeyPatternParser.initReferentials(context);
		}
	}

	private void initTimetableFrameRefs(Context context, NetexReferential referential,
			List<JAXBElement<? extends Common_VersionFrameStructure>> topLevelFrame) {
		List<TimetableFrame> timetableFrames = getFrames(TimetableFrame.class, topLevelFrame);
		for (TimetableFrame timetableFrame : timetableFrames) {
			NetexObjectUtil.addTimetableFrameReference(referential, timetableFrame.getId(), timetableFrame);

			JourneysInFrame_RelStructure vehicleJourneysStruct = timetableFrame.getVehicleJourneys();
			List<Journey_VersionStructure> serviceJourneyStructs = vehicleJourneysStruct.getDatedServiceJourneyOrDeadRunOrServiceJourney();
			for (Journey_VersionStructure serviceJourneyStruct : serviceJourneyStructs) {
				ServiceJourney serviceJourney = (ServiceJourney) serviceJourneyStruct;
				// TODO consider generating a more sophisticated id
				NetexObjectUtil.addServiceJourneyReference(referential, serviceJourney.getId(), serviceJourney);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> getFrames(Class<T> clazz, List<JAXBElement<? extends Common_VersionFrameStructure>> compositeFrameOrCommonFrame) {
		List<T> foundFrames = new ArrayList<>();
		for (JAXBElement<? extends Common_VersionFrameStructure> frame : compositeFrameOrCommonFrame) {
			if (frame.getValue() instanceof CompositeFrame) {
				CompositeFrame compositeFrame = (CompositeFrame) frame.getValue();
				Frames_RelStructure frames = compositeFrame.getFrames();
				List<JAXBElement<? extends Common_VersionFrameStructure>> commonFrames = frames.getCommonFrame();
				for (JAXBElement<? extends Common_VersionFrameStructure> commonFrame : commonFrames) {
					T value = (T) commonFrame.getValue();
					if (value.getClass().equals(clazz)) {
						foundFrames.add(value);
					}
				}
			} else if (frame.getValue().equals(clazz)) {
				foundFrames.add((T) frame.getValue());
			}
		}
		return foundFrames;
	}

	// TODO we must find out the correct parsing order run
	@Override
	public void parse(Context context) throws Exception {
		@SuppressWarnings("unchecked")
		List<PublicationDeliveryStructure> commonData = (List<PublicationDeliveryStructure>) context.get(NETEX_COMMON_DATA);
		PublicationDeliveryStructure lineData = (PublicationDeliveryStructure) context.get(NETEX_LINE_DATA_JAVA);
		PublicationDeliveryStructure.DataObjects dataObjects = lineData.getDataObjects();
		List<JAXBElement<? extends Common_VersionFrameStructure>> compositeFrameOrCommonFrame = dataObjects.getCompositeFrameOrCommonFrame();

		Referential referential = (Referential) context.get(REFERENTIAL);
		NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);

		// TODO: find out how to handle common data frames, this is how it was done in previous version
		/*
		 * List<Object> foundFrames = new ArrayList<Object>(); if (commonData != null) { for (PublicationDeliveryStructure common : commonData) {
		 * foundFrames.addAll(findFrames(class1, common, null)); } }
		 */

		// Collection<ResourceFrame> resourceFrames = netexReferential.getResourceFrames().values();
		Parser organisationsParser = ParserFactory.create(OrganisationParser.class.getName());
		organisationsParser.parse(context);

		// Collection<SiteFrame> siteFrames = netexReferential.getSiteFrames().values();
		Parser stopPlaceParser = ParserFactory.create(StopPlaceParser.class.getName());
		stopPlaceParser.parse(context);

		// service frame

		Parser networkParser = ParserFactory.create(NetworkParser.class.getName());
		networkParser.parse(context);

		Parser lineParser = ParserFactory.create(LineParser.class.getName());
		lineParser.parse(context);

		Parser routeParser = ParserFactory.create(RouteParser.class.getName());
		routeParser.parse(context);

		Parser stopPointParser = ParserFactory.create(StopPointParser.class.getName());
		stopPointParser.parse(context);

		Parser journeyPatternParser = ParserFactory.create(JourneyPatternParser.class.getName());
		journeyPatternParser.parse(context);

		// timetable frame

		/*
		 * ValidityConditions_RelStructure validityConditions = frame.getValidityConditions();
		 * 
		 * if (validityConditions != null) { List<Object> availabilityConditionElements =
		 * validityConditions.getValidityConditionRefOrValidBetweenOrValidityCondition_(); // should iterate all availability conditions, for now only
		 * retrieving first occurrence
		 */
		/*
		 * for (JAXBElement<AvailabilityCondition> availabilityConditionElement : availabilityConditionElements) { AvailabilityCondition value =
		 * availabilityConditionElement.getValue(); }
		 *//*
			 * 
			 * // TODO: add more sophisticated check on zoneids and zoneoffsets here // how to connect the period to the right timetable instance? we can only
			 * get timetables by day type id if (availabilityConditionElements != null && availabilityConditionElements.size() > 0) { AvailabilityCondition
			 * availabilityCondition = ((JAXBElement<AvailabilityCondition>) availabilityConditionElements.get(0)).getValue(); OffsetDateTime fromDate =
			 * availabilityCondition.getFromDate(); OffsetDateTime toDate = availabilityCondition.getToDate(); Date startOfPeriod =
			 * ParserUtils.getSQLDate(fromDate.toString()); Date endOfPeriod = ParserUtils.getSQLDate(toDate.toString()); Period period = new
			 * Period(startOfPeriod, endOfPeriod); //timetable.addPeriod(period); } }
			 */

		Parser vehicleJourneyParser = ParserFactory.create(VehicleJourneyParser.class.getName());
		vehicleJourneyParser.parse(context);

		// Collection<ServiceCalendarFrame> serviceCalendarFrames = netexReferential.getServiceCalendarFrames().values();
		Parser dayTypeParser = ParserFactory.create(DayTypeParser.class.getName());
		dayTypeParser.parse(context);

		// TODO consider if this is the best place to sort the stop points connected to routes
		// maybe a post processor method could be useful for stuff like sorting, renaming, etc...
		referential.getRoutes().values().forEach(route -> {
			route.getStopPoints().sort((o1, o2) -> o1.getPosition().compareTo(o2.getPosition()));
		});

		// post processing
		updateBoardingAlighting(referential);
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
