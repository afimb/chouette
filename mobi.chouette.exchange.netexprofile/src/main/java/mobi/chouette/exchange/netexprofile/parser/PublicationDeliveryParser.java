package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.importer.util.NetexFrameContext;
import mobi.chouette.exchange.netexprofile.importer.util.NetexObjectUtil;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
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
import java.util.Comparator;
import java.util.List;

@Log4j
public class PublicationDeliveryParser extends AbstractParser {

	@Override
	public void parse(Context context) throws Exception {
		boolean isCommonDelivery = context.get(NETEX_WITH_COMMON_DATA) != null && context.get(NETEX_LINE_DATA_JAVA) == null;
		Referential referential = (Referential) context.get(REFERENTIAL);
		NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);

		if (netexReferential == null) {
			netexReferential = new NetexReferential();
			context.put(NETEX_REFERENTIAL, netexReferential);
		} else {
			// clear all data from last line parsing session (not shared data)
			netexReferential.clear();
		}

		NetexFrameContext frameContext = (NetexFrameContext) context.get(NETEX_FRAME_CONTEXT);
		if (frameContext == null) {
			frameContext = new NetexFrameContext();
			context.put(NETEX_FRAME_CONTEXT, frameContext);
		} else {
			frameContext.clear();
		}

		String contextKey = isCommonDelivery ? NETEX_COMMON_DATA : NETEX_LINE_DATA_JAVA;
		PublicationDeliveryStructure publicationDelivery = (PublicationDeliveryStructure) context.get(contextKey);
		List<JAXBElement<? extends Common_VersionFrameStructure>> dataObjectFrames = publicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame();

		List<ResourceFrame> resourceFrames = NetexObjectUtil.getFrames(ResourceFrame.class, dataObjectFrames);
		if (CollectionUtils.isNotEmpty(resourceFrames)) {
			for (ResourceFrame resourceFrame : resourceFrames) {
				parseResourceFrame(context, resourceFrame);
			}
			// cache 1st occurrence of frame (presume 1 of each frame for now)
			frameContext.put(ResourceFrame.class, resourceFrames.get(0));
		}

		List<SiteFrame> siteFrames = NetexObjectUtil.getFrames(SiteFrame.class, dataObjectFrames);
		if (CollectionUtils.isNotEmpty(siteFrames)) {
			for (SiteFrame siteFrame : siteFrames) {
				parseSiteFrame(context, siteFrame);
			}
			// cache 1st occurrence of frame (presume 1 of each frame for now)
			frameContext.put(SiteFrame.class, siteFrames.get(0));
		}

		List<ServiceFrame> serviceFrames = NetexObjectUtil.getFrames(ServiceFrame.class, dataObjectFrames);
		if (CollectionUtils.isNotEmpty(serviceFrames)) {
			for (ServiceFrame serviceFrame : serviceFrames) {
				parseServiceFrame(context, serviceFrame, netexReferential, isCommonDelivery);
			}
			// cache 1st occurrence of frame (presume 1 of each frame for now)
			frameContext.put(ServiceFrame.class, serviceFrames.get(0));
		}

		List<ServiceCalendarFrame> serviceCalendarFrames = NetexObjectUtil.getFrames(ServiceCalendarFrame.class, dataObjectFrames);
		if (CollectionUtils.isNotEmpty(serviceCalendarFrames)) {
			for (ServiceCalendarFrame serviceCalendarFrame : serviceCalendarFrames) {
				parseServiceCalendarFrame(context, serviceCalendarFrame);
			}
			// cache 1st occurrence of frame (presume 1 of each frame for now)
			frameContext.put(ServiceCalendarFrame.class, serviceCalendarFrames.get(0));
		}

		if (!isCommonDelivery) {
			List<TimetableFrame> timetableFrames = NetexObjectUtil.getFrames(TimetableFrame.class, dataObjectFrames);
			if (CollectionUtils.isNotEmpty(timetableFrames)) {
				for (TimetableFrame timetableFrame : timetableFrames) {
					parseTimetableFrame(context, timetableFrame);
				}
				// cache 1st occurrence of frame (presume 1 of each frame for now)
				frameContext.put(TimetableFrame.class, timetableFrames.get(0));
			}
		}

		// post processing
		sortStopPointsOnRoutes(referential);
		updateBoardingAlighting(referential);
	}

	private void parseResourceFrame(Context context, ResourceFrame resourceFrame) throws Exception{
		OrganisationsInFrame_RelStructure organisationsInFrameStruct = resourceFrame.getOrganisations();
		if (organisationsInFrameStruct != null) {
			context.put(NETEX_LINE_DATA_CONTEXT, organisationsInFrameStruct);
			OrganisationParser organisationParser = (OrganisationParser) ParserFactory.create(OrganisationParser.class.getName());
			organisationParser.parse(context);
		}
	}

	private void parseSiteFrame(Context context, SiteFrame siteFrame) throws Exception{
		StopPlacesInFrame_RelStructure stopPlacesStruct = siteFrame.getStopPlaces();
		if (stopPlacesStruct != null) {
			context.put(NETEX_LINE_DATA_CONTEXT, stopPlacesStruct);
			StopPlaceParser stopPlaceParser = (StopPlaceParser) ParserFactory.create(StopPlaceParser.class.getName());
			stopPlaceParser.parse(context);
		}
	}

	private void parseServiceFrame(Context context, ServiceFrame serviceFrame, NetexReferential referential, boolean isCommonDelivery) throws Exception {
		if (!isCommonDelivery) {
			Network network = serviceFrame.getNetwork();
			//NetexObjectUtil.addNetworkReference(referential, network.getId(), network);
			context.put(NETEX_LINE_DATA_CONTEXT, network);
			NetworkParser networkParser = (NetworkParser) ParserFactory.create(NetworkParser.class.getName());
			networkParser.parse(context);

			// this is the mapping between points on a route to the actual stop points
			List<RoutePoint> routePoints = serviceFrame.getRoutePoints().getRoutePoint();
			for (RoutePoint routePoint : routePoints) {
				NetexObjectUtil.addRoutePointReference(referential, routePoint.getId(), routePoint);
			}

			RoutesInFrame_RelStructure routesInFrameStruct = serviceFrame.getRoutes();
			context.put(NETEX_LINE_DATA_CONTEXT, routesInFrameStruct);
			RouteParser routeParser = (RouteParser) ParserFactory.create(RouteParser.class.getName());
			routeParser.parse(context);

			LinesInFrame_RelStructure linesInFrameStruct = serviceFrame.getLines();
			context.put(NETEX_LINE_DATA_CONTEXT, linesInFrameStruct);
			LineParser lineParser = (LineParser) ParserFactory.create(LineParser.class.getName());
			lineParser.parse(context);
		}

		// parse stop assignments (connection between stop points and stop places)
		StopAssignmentsInFrame_RelStructure stopAssignmentsStructure = serviceFrame.getStopAssignments();
		List<JAXBElement<? extends StopAssignment_VersionStructure>> stopAssignmentElements = stopAssignmentsStructure.getStopAssignment();

		for (JAXBElement<? extends StopAssignment_VersionStructure> stopAssignmentElement : stopAssignmentElements) {
			PassengerStopAssignment passengerStopAssignment = (PassengerStopAssignment) stopAssignmentElement.getValue();
			NetexObjectUtil.addPassengerStopAssignmentReference(referential, passengerStopAssignment.getId(), passengerStopAssignment);
		}

		ScheduledStopPointsInFrame_RelStructure scheduledStopPointStruct = serviceFrame.getScheduledStopPoints();
		context.put(NETEX_LINE_DATA_CONTEXT, scheduledStopPointStruct);
		StopPointParser stopPointParser = (StopPointParser) ParserFactory.create(StopPointParser.class.getName());
		stopPointParser.parse(context);

		if (!isCommonDelivery) {
			JourneyPatternsInFrame_RelStructure journeyPatternStruct = serviceFrame.getJourneyPatterns();
			context.put(NETEX_LINE_DATA_CONTEXT, journeyPatternStruct);
			JourneyPatternParser journeyPatternParser = (JourneyPatternParser) ParserFactory.create(JourneyPatternParser.class.getName());
			journeyPatternParser.initReferentials(context);
		}
	}

	private void parseServiceCalendarFrame(Context context, ServiceCalendarFrame serviceCalendarFrame) throws Exception {
		DayTypesInFrame_RelStructure dayTypeStruct = serviceCalendarFrame.getDayTypes();
		context.put(NETEX_LINE_DATA_CONTEXT, dayTypeStruct);
		Parser dayTypeParser = ParserFactory.create(DayTypeParser.class.getName());
		dayTypeParser.parse(context);
	}

	private void parseTimetableFrame(Context context, TimetableFrame timetableFrame) throws Exception {
		JourneysInFrame_RelStructure vehicleJourneysStruct = timetableFrame.getVehicleJourneys();
		context.put(NETEX_LINE_DATA_CONTEXT, vehicleJourneysStruct);
		Parser vehicleJourneyParser = ParserFactory.create(VehicleJourneyParser.class.getName());
		vehicleJourneyParser.parse(context);
	}

	@Override
	public void initReferentials(Context context) throws Exception {
	}

	private void sortStopPointsOnRoutes(Referential referential) {
		referential.getRoutes().values().forEach(route -> route.getStopPoints()
				.sort(Comparator.comparing(StopPoint::getPosition)));
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
