package mobi.chouette.exchange.netexprofile.parser;

import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.commons.collections.CollectionUtils;
import org.rutebanken.netex.model.Common_VersionFrameStructure;
import org.rutebanken.netex.model.CompositeFrame;
import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.rutebanken.netex.model.DestinationDisplaysInFrame_RelStructure;
import org.rutebanken.netex.model.JourneyInterchangesInFrame_RelStructure;
import org.rutebanken.netex.model.JourneyPatternsInFrame_RelStructure;
import org.rutebanken.netex.model.JourneysInFrame_RelStructure;
import org.rutebanken.netex.model.LinesInFrame_RelStructure;
import org.rutebanken.netex.model.Network;
import org.rutebanken.netex.model.Notice;
import org.rutebanken.netex.model.NoticeAssignment;
import org.rutebanken.netex.model.OrganisationsInFrame_RelStructure;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.ResourceFrame;
import org.rutebanken.netex.model.RoutesInFrame_RelStructure;
import org.rutebanken.netex.model.ScheduledStopPointsInFrame_RelStructure;
import org.rutebanken.netex.model.ServiceCalendarFrame;
import org.rutebanken.netex.model.ServiceFrame;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.StopPlacesInFrame_RelStructure;
import org.rutebanken.netex.model.TariffZonesInFrame_RelStructure;
import org.rutebanken.netex.model.TimetableFrame;
import org.rutebanken.netex.model.ValidBetween;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.ConversionUtil;
import mobi.chouette.exchange.netexprofile.importer.NetexprofileImportParameters;
import mobi.chouette.exchange.netexprofile.util.NetexObjectUtil;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

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
		NetexprofileImportParameters configuration = (NetexprofileImportParameters) context.get(CONFIGURATION);
		PublicationDeliveryStructure publicationDelivery = (PublicationDeliveryStructure) context.get(NETEX_DATA_JAVA);
		List<JAXBElement<? extends Common_VersionFrameStructure>> dataObjectFrames = publicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame();
		List<CompositeFrame> compositeFrames = NetexObjectUtil.getFrames(CompositeFrame.class, dataObjectFrames);

		if (compositeFrames.size() > 0) {

			// parse composite frame elements
			for (CompositeFrame compositeFrame : compositeFrames) {

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

				if (configuration.isParseSiteFrames()) {
					parseSiteFrames(context, siteFrames);
				}
				parseServiceFrames(context, serviceFrames, isCommonDelivery);
				parseServiceCalendarFrame(context, serviceCalendarFrames);

				if (!isCommonDelivery) {
					parseTimetableFrames(context, timetableFrames);
				}
			}
		} else {

			// no composite frame present
			List<ResourceFrame> resourceFrames = NetexObjectUtil.getFrames(ResourceFrame.class, dataObjectFrames);
			List<ServiceFrame> serviceFrames = NetexObjectUtil.getFrames(ServiceFrame.class, dataObjectFrames);
			List<SiteFrame> siteFrames = NetexObjectUtil.getFrames(SiteFrame.class, dataObjectFrames);
			List<ServiceCalendarFrame> serviceCalendarFrames = NetexObjectUtil.getFrames(ServiceCalendarFrame.class, dataObjectFrames);
			List<TimetableFrame> timetableFrames = NetexObjectUtil.getFrames(TimetableFrame.class, dataObjectFrames);

			// pre processing
			preParseReferentialDependencies(context, referential, serviceFrames, timetableFrames, isCommonDelivery);

			// normal processing
			parseResourceFrames(context, resourceFrames);
			if (configuration.isParseSiteFrames()) {
				parseSiteFrames(context, siteFrames);
			}
			parseServiceFrames(context, serviceFrames, isCommonDelivery);
			parseServiceCalendarFrame(context, serviceCalendarFrames);

			if (!isCommonDelivery) {
				parseTimetableFrames(context, timetableFrames);
			}
		}

		// post processing
		// sortStopPoints(referential);
		// updateBoardingAlighting(referential);
	}

	private void preParseReferentialDependencies(Context context, Referential referential, List<ServiceFrame> serviceFrames,
			List<TimetableFrame> timetableFrames, boolean isCommonDelivery) throws Exception {

		org.rutebanken.netex.model.Line line = null;

		for (ServiceFrame serviceFrame : serviceFrames) {

			// pre parsing route points
			// if (serviceFrame.getRoutePoints() != null) {
			// context.put(NETEX_LINE_DATA_CONTEXT, serviceFrame.getRoutePoints());
			// Parser routePointParser = ParserFactory.create(RoutePointParser.class.getName());
			// routePointParser.parse(context);
			// }

			// stop assignments
			if (serviceFrame.getStopAssignments() != null) {
				context.put(NETEX_LINE_DATA_CONTEXT, serviceFrame.getStopAssignments());
				Parser stopAssignmentParser = ParserFactory.create(StopAssignmentParser.class.getName());
				stopAssignmentParser.parse(context);
			}

			if (!isCommonDelivery) {
				if (line == null) {
					line = (org.rutebanken.netex.model.Line) serviceFrame.getLines().getLine_().get(0).getValue();
					context.put(PARSING_CONTEXT_LINE_ID, line.getId());
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
			// Map<String, Set<String>> journeyDayTypeIdMap = new HashMap<>();
			//
			// for (TimetableFrame timetableFrame : timetableFrames) {
			// for (Journey_VersionStructure journeyStruct : timetableFrame.getVehicleJourneys().getDatedServiceJourneyOrDeadRunOrServiceJourney()) {
			// ServiceJourney serviceJourney = (ServiceJourney) journeyStruct;
			// Set<String> dayTypeIds = new HashSet<>();
			//
			// for (JAXBElement<? extends DayTypeRefStructure> dayTypeRefStructElement : serviceJourney.getDayTypes().getDayTypeRef()) {
			// dayTypeIds.add(dayTypeRefStructElement.getValue().getRef());
			// }
			//
			// journeyDayTypeIdMap.put(serviceJourney.getId(), dayTypeIds);
			// }
			// }
			//
			// Set<String> processedIds = new HashSet<>();
			// List<Set<String>> calendarGroups = new ArrayList<>();
			//
			// for (Map.Entry<String, Set<String>> entry1 : journeyDayTypeIdMap.entrySet()) {
			// if (!processedIds.contains(entry1.getKey())) {
			// Set<String> groupedJourneyIds = new HashSet<>();
			// groupedJourneyIds.add(entry1.getKey());
			//
			// for (Map.Entry<String, Set<String>> entry2 : journeyDayTypeIdMap.entrySet()) {
			// if (!entry1.getKey().equals(entry2.getKey())) {
			// if (CollectionUtils.isEqualCollection(entry1.getValue(), entry2.getValue())) {
			// groupedJourneyIds.add(entry2.getKey());
			// processedIds.add(entry2.getKey());
			// }
			// }
			// }
			// calendarGroups.add(groupedJourneyIds);
			// processedIds.add(entry1.getKey());
			// }
			// }
			//
			// assert line != null;
			// String[] idParts = StringUtils.split(line.getId(), ":");
			// String[] idSequence = NetexProducerUtils.generateIdSequence(calendarGroups.size());
			//
			// for (int i = 0; i < calendarGroups.size(); i++) {
			// String timetableIdSuffix = idParts[2] + "-" + StringUtils.leftPad(idSequence[i], 2, "0");
			// String timetableId = netexId(idParts[0], ObjectIdTypes.TIMETABLE_KEY, timetableIdSuffix);
			// Timetable timetable = ObjectFactory.getTimetable(referential, timetableId);
			//
			// for (String journeyId : calendarGroups.get(i)) {
			// addTimetableId(context, journeyId, timetable.getObjectId());
			// }
			// }
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

			if (serviceFrame.getNetwork() != null) {
				Network network = serviceFrame.getNetwork();
				context.put(NETEX_LINE_DATA_CONTEXT, network);
				NetworkParser networkParser = (NetworkParser) ParserFactory.create(NetworkParser.class.getName());
				networkParser.parse(context);
			}

			if (serviceFrame.getDestinationDisplays() != null) {
				DestinationDisplaysInFrame_RelStructure destinationDisplaysInFrameStruct = serviceFrame.getDestinationDisplays();
				context.put(NETEX_LINE_DATA_CONTEXT, destinationDisplaysInFrameStruct);
				DestinationDisplayParser destinationDisplayParser = (DestinationDisplayParser) ParserFactory.create(DestinationDisplayParser.class.getName());
				destinationDisplayParser.parse(context);

			}
			if (serviceFrame.getNotices() != null) {
				for (Notice notice : serviceFrame.getNotices().getNotice()) {
					parseNotice(context, notice);
				}
			}

			if(serviceFrame.getNoticeAssignments() != null) {
				for(JAXBElement<? extends DataManagedObjectStructure> assingment : serviceFrame.getNoticeAssignments().getNoticeAssignment_()) {
					NoticeAssignment a = (NoticeAssignment) assingment.getValue();
					parseNoticeAssignment(context, a);
				}
			}

			if (serviceFrame.getScheduledStopPoints() != null) {
				ScheduledStopPointsInFrame_RelStructure scheduledStopPointsInFrameStruct = serviceFrame.getScheduledStopPoints();
				context.put(NETEX_LINE_DATA_CONTEXT, scheduledStopPointsInFrameStruct);
				ScheduledStopPointParser scheduledStopPointParser = (ScheduledStopPointParser) ParserFactory.create(ScheduledStopPointParser.class.getName());
				scheduledStopPointParser.parse(context);

			}



			if (!isCommonDelivery) {
				LinesInFrame_RelStructure linesInFrameStruct = serviceFrame.getLines();
				if (linesInFrameStruct != null) {
					context.put(NETEX_LINE_DATA_CONTEXT, linesInFrameStruct);
					LineParser lineParser = (LineParser) ParserFactory.create(LineParser.class.getName());
					lineParser.parse(context);
				}
				RoutesInFrame_RelStructure routesInFrameStruct = serviceFrame.getRoutes();
				if (routesInFrameStruct != null) {
					context.put(NETEX_LINE_DATA_CONTEXT, routesInFrameStruct);
					RouteParser routeParser = (RouteParser) ParserFactory.create(RouteParser.class.getName());
					routeParser.parse(context);
				}
				JourneyPatternsInFrame_RelStructure journeyPatternStruct = serviceFrame.getJourneyPatterns();
				if (journeyPatternStruct != null) {
					context.put(NETEX_LINE_DATA_CONTEXT, journeyPatternStruct);
					JourneyPatternParser journeyPatternParser = (JourneyPatternParser) ParserFactory.create(JourneyPatternParser.class.getName());
					journeyPatternParser.parse(context);
				}
			}
		}
	}

	private void parseServiceCalendarFrame(Context context, List<ServiceCalendarFrame> serviceCalendarFrames) throws Exception {
		for (ServiceCalendarFrame serviceCalendarFrame : serviceCalendarFrames) {

			parseValidityConditionsInFrame(context, serviceCalendarFrame);

			context.put(NETEX_LINE_DATA_CONTEXT, serviceCalendarFrame);
			Parser serviceCalendarParser = ParserFactory.create(ServiceCalendarFrameParser.class.getName());
			serviceCalendarParser.parse(context);
		}
	}

	private void parseTimetableFrames(Context context, List<TimetableFrame> timetableFrames) throws Exception {
		for (TimetableFrame timetableFrame : timetableFrames) {

			parseValidityConditionsInFrame(context, timetableFrame);

			if (timetableFrame.getNotices() != null) {
				for (Notice notice : timetableFrame.getNotices().getNotice()) {
					parseNotice(context, notice);
				}
			}

			if (timetableFrame.getNoticeAssignments() != null) {
				for (JAXBElement<? extends DataManagedObjectStructure> assingment : timetableFrame.getNoticeAssignments().getNoticeAssignment_()) {
					NoticeAssignment a = (NoticeAssignment) assingment.getValue();
					parseNoticeAssignment(context, a);
				}
			}

			JourneysInFrame_RelStructure vehicleJourneysStruct = timetableFrame.getVehicleJourneys();
			context.put(NETEX_LINE_DATA_CONTEXT, vehicleJourneysStruct);
			Parser serviceJourneyParser = ParserFactory.create(ServiceJourneyParser.class.getName());
			serviceJourneyParser.parse(context);

			JourneyInterchangesInFrame_RelStructure journeyInterchangesStruct = timetableFrame.getJourneyInterchanges();
			if (journeyInterchangesStruct != null) {
				context.put(NETEX_LINE_DATA_CONTEXT, journeyInterchangesStruct);
				Parser serviceInterchangeParser = ParserFactory.create(ServiceJourneyInterchangeParser.class.getName());
				serviceInterchangeParser.parse(context);
			}

		}
	}

	private void parseNoticeAssignment(Context context, NoticeAssignment assignment) {
		Referential referential = (Referential) context.get(REFERENTIAL);

		Footnote footnote = ObjectFactory.getFootnote(referential, assignment.getNoticeRef().getRef());
		String noticedObject = assignment.getNoticedObjectRef().getRef();

		if (noticedObject.contains(":Line:")) {
			Line line = ObjectFactory.getLine(referential, noticedObject);
			line.getFootnotes().add(footnote);
		} else if (noticedObject.contains(":VehicleJourney:") || noticedObject.contains(":ServiceJourney:")) {
			VehicleJourney vehicleJourney = ObjectFactory.getVehicleJourney(referential, noticedObject);
			vehicleJourney.getFootnotes().add(footnote);
		} else if (noticedObject.contains(":JourneyPattern:") || noticedObject.contains(":ServicePattern:")) {
			JourneyPattern journeyPattern = ObjectFactory.getJourneyPattern(referential, noticedObject);
			journeyPattern.getFootnotes().add(footnote);
		} else if (noticedObject.contains(":StopPointInJourneyPattern:")) {
			StopPoint stopPointInJourneyPattern = ObjectFactory.getStopPoint(referential, noticedObject);
			stopPointInJourneyPattern.getFootnotes().add(footnote);
		} else if (noticedObject.contains(":TimetabledPassingTime:")) {
			log.error("NoticedObjectRef for TimetabledPassingTime (VehicleJourneyAtStop) not implemented");
		} else {
			log.warn("Unsupported NoticedObjectRef type: " + noticedObject);
		}
	}

	private void parseNotice(Context context, Notice notice) {
		Referential referential = (Referential) context.get(REFERENTIAL);

		Footnote footnote = ObjectFactory.getFootnote(referential, notice.getId());
		footnote.setLabel(ConversionUtil.getValue(notice.getText()));
		footnote.setCode(notice.getPublicCode());
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
