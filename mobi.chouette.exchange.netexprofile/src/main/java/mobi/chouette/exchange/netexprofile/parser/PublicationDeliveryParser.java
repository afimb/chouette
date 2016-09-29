package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import no.rutebanken.netex.model.*;
import org.apache.commons.lang.StringUtils;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class PublicationDeliveryParser implements Parser, Constant {

	@Override
	public void parse(Context context) throws Exception {

		// Convert from NETEX java objects to Chouette objects

		PublicationDeliveryStructure lineData = (PublicationDeliveryStructure) context.get(NETEX_LINE_DATA_JAVA);
		@SuppressWarnings("unchecked")
		List<PublicationDeliveryStructure> commonData = (List<PublicationDeliveryStructure>) context
				.get(mobi.chouette.exchange.netexprofile.Constant.NETEX_COMMON_DATA);

		Referential referential = (Referential) context.get(REFERENTIAL);

		Map<String, Object> cachedNetexData = new HashMap<String, Object>();
		context.put(NETEX_LINE_DATA_ID_CONTEXT, cachedNetexData);

		for (Object frame : findFrames(ResourceFrame.class, lineData, commonData)) {
			parseResourceFrame(context, referential, lineData, commonData, (ResourceFrame) frame);
		}

		for (Object frame : findFrames(SiteFrame.class, lineData, commonData)) {
			parseSiteFrame(context, referential, lineData, commonData, (SiteFrame) frame);
		}

		for (Object frame : findFrames(ServiceCalendarFrame.class, lineData, commonData)) {
			parseServiceCalendarFrame(context, referential, lineData, commonData, (ServiceCalendarFrame) frame);
		}

		for (Object frame : findFrames(ServiceFrame.class, lineData, commonData)) {
			parseServiceFrame(context, referential, lineData, commonData, (ServiceFrame) frame);
		}

		for (Object frame : findFrames(TimetableFrame.class, lineData, commonData)) {
			parseTimetableFrame(context, referential, lineData, commonData, (TimetableFrame) frame);
		}

	}

	private List<Object> findFrames(Class class1, PublicationDeliveryStructure lineData, List<PublicationDeliveryStructure> commonData) {

		// TODO generify class

		List<Object> foundFrames = new ArrayList<Object>();
		if (commonData != null) {
			for (PublicationDeliveryStructure common : commonData) {
				foundFrames.addAll(findFrames(class1, common, null));
			}
		}

		for (JAXBElement<? extends Common_VersionFrameStructure> frame : lineData.getDataObjects().getCompositeFrameOrCommonFrame()) {
			if (frame.getValue() instanceof CompositeFrame) {
				CompositeFrame compositeFrame = (CompositeFrame) frame.getValue();
				Frames_RelStructure frames = compositeFrame.getFrames();
				List<JAXBElement<? extends Common_VersionFrameStructure>> commonFrames = frames.getCommonFrame();
				for (JAXBElement<? extends Common_VersionFrameStructure> commonFrame : commonFrames) {
					Common_VersionFrameStructure value = commonFrame.getValue();

					if (value.getClass().equals(class1)) {
						foundFrames.add(value);
					}
				}
			} else if (frame.getValue().equals(class1)) {
				foundFrames.add(frame.getValue());
			}
		}

		return foundFrames;
	}

	private void parseTimetableFrame(Context context, Referential referential, PublicationDeliveryStructure lineData,
			List<PublicationDeliveryStructure> commonData, TimetableFrame frame) throws Exception {
        context.put(NETEX_LINE_DATA_CONTEXT, frame);
        Parser timetableParser = ParserFactory.create(TimetableParser.class.getName());
        timetableParser.parse(context);
	}

	private void parseServiceCalendarFrame(Context context, Referential referential, PublicationDeliveryStructure lineData,
			List<PublicationDeliveryStructure> commonData, ServiceCalendarFrame serviceCalendarFrame)  throws Exception {
        DayTypesInFrame_RelStructure dayTypesStruct = serviceCalendarFrame.getDayTypes();
        context.put(NETEX_LINE_DATA_CONTEXT, dayTypesStruct);
        Parser dayTypeParser = ParserFactory.create(DayTypeParser.class.getName());
        dayTypeParser.parse(context);
	}

	// TODO: remove this parsing when NSR is in place
	// TODO: consider extracting this code into separate parser
	private void parseSiteFrame(Context context, Referential referential, PublicationDeliveryStructure lineData,
            List<PublicationDeliveryStructure> commonData, SiteFrame siteFrame) throws Exception {
		StopPlacesInFrame_RelStructure stopPlacesStruct = siteFrame.getStopPlaces();
		List<StopPlace> stopPlaces = stopPlacesStruct.getStopPlace();
		for(StopPlace stopPlace : stopPlaces) {
			StopArea stopArea = ObjectFactory.getStopArea(referential, stopPlace.getId());
			stopArea.setName(stopPlace.getName().getValue());
			stopArea.setRegistrationNumber(stopPlace.getShortName().getValue());
			stopArea.setAreaType(ChouetteAreaEnum.CommercialStopPoint);
			stopArea.setFilled(true);

			// TODO: add support for boarding positions, and connect to StopArea as parent
		}
	}

	private void parseServiceFrame(Context context, Referential referential, PublicationDeliveryStructure lineData,
			List<PublicationDeliveryStructure> commonData, ServiceFrame serviceFrame) throws Exception {
		// TODO: consider as method argument instead
		Map<String, Object> cachedNetexData = (Map<String, Object>) context.get(NETEX_LINE_DATA_ID_CONTEXT);

		Network network = serviceFrame.getNetwork();
		mobi.chouette.model.Network ptNetwork = ObjectFactory.getPTNetwork(referential, network.getId());
		ptNetwork.setName(network.getName().getValue());

		RoutePointsInFrame_RelStructure routePointsStructure = serviceFrame.getRoutePoints();
		List<RoutePoint> routePoints = routePointsStructure.getRoutePoint();

		for (RoutePoint routePoint : routePoints) {
			cachedNetexData.put(routePoint.getId(), routePoint);
		}

		RoutesInFrame_RelStructure routesStructure = serviceFrame.getRoutes();
		context.put(NETEX_LINE_DATA_CONTEXT, routesStructure);
		Parser routeParser = ParserFactory.create(RouteParser.class.getName());
		routeParser.parse(context);

		LinesInFrame_RelStructure linesStructure = serviceFrame.getLines();
		context.put(NETEX_LINE_DATA_CONTEXT, linesStructure);
		Parser lineParser = ParserFactory.create(LineParser.class.getName());
		lineParser.parse(context);

		StopAssignmentsInFrame_RelStructure stopAssignmentsStructure = serviceFrame.getStopAssignments();
		List<JAXBElement<? extends StopAssignment_VersionStructure>> stopAssignmentElements = stopAssignmentsStructure.getStopAssignment();
		for (JAXBElement<? extends StopAssignment_VersionStructure> stopAssignmentElement : stopAssignmentElements) {
			PassengerStopAssignment passengerStopAssignment = (PassengerStopAssignment) stopAssignmentElement.getValue();

			ScheduledStopPointRefStructure scheduledStopPointRef = passengerStopAssignment.getScheduledStopPointRef();
			StopPlaceRefStructure stopPlaceRef = passengerStopAssignment.getStopPlaceRef();

			if (scheduledStopPointRef != null && StringUtils.isNotEmpty(scheduledStopPointRef.getRef()) &&
					stopPlaceRef != null && StringUtils.isNotEmpty(stopPlaceRef.getRef())) {
				cachedNetexData.put(scheduledStopPointRef.getRef(), stopPlaceRef.getRef());
			}
		}

		ScheduledStopPointsInFrame_RelStructure scheduledStopPointsStructure = serviceFrame.getScheduledStopPoints();
		context.put(NETEX_LINE_DATA_CONTEXT, scheduledStopPointsStructure);
		Parser scheduledStopPointsParser = ParserFactory.create(ScheduledStopPointParser.class.getName());
		scheduledStopPointsParser.parse(context);

        JourneyPatternsInFrame_RelStructure journeyPatternsStructure = serviceFrame.getJourneyPatterns();
		context.put(NETEX_LINE_DATA_CONTEXT, journeyPatternsStructure);
        Parser journeyPatternParser = ParserFactory.create(JourneyPatternParser.class.getName());
        journeyPatternParser.parse(context);
	}

	private void parseResourceFrame(Context context, Referential referential, PublicationDeliveryStructure lineData,
			List<PublicationDeliveryStructure> commonData, ResourceFrame resourceFrame) throws Exception {
		OrganisationsInFrame_RelStructure organisationsStructure = resourceFrame.getOrganisations();
		context.put(NETEX_LINE_DATA_CONTEXT, organisationsStructure);
		Parser organisationsParser = ParserFactory.create(OrganisationParser.class.getName());
		organisationsParser.parse(context);
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
