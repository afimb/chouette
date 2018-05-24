package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.importer.util.DataLocationHelper;
import mobi.chouette.exchange.netexprofile.importer.util.IdVersion;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexProfileValidator;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexProfileValidatorFactory;
import mobi.chouette.exchange.netexprofile.importer.validation.norway.StopPlaceRegistryIdValidator.DefaultExternalReferenceValidatorFactory;
import mobi.chouette.exchange.netexprofile.util.NetexIdExtractorHelper;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.model.Codespace;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import org.rutebanken.netex.model.BookingAccessEnumeration;
import org.rutebanken.netex.model.BookingMethodEnumeration;
import org.rutebanken.netex.model.FlexibleLineTypeEnumeration;
import org.rutebanken.netex.model.PurchaseMomentEnumeration;
import org.rutebanken.netex.model.PurchaseWhenEnumeration;

@Log4j
public class NorwayLineNetexProfileValidator extends AbstractNorwayNetexProfileValidator implements NetexProfileValidator {

	public static final String NAME = "NorwayLineNetexProfileValidator";
	
	private static final String[] validTransportModes = new String[] {
			"coach",
			"bus",
			"tram",
			"rail",
			"metro",
			"air",
			"water",
			"cableway",
			"funicular",
			"unknown"
	};
	
	
	private static final String[] validTransportSubModes = new String[] {
			"internationalCoach",
			"nationalCoach",
			"touristCoach",
			"airportLinkBus",
			"localTram",
			"cityTram",
			"international",
			"metro",
			"domesticFlight",
			"highSpeedPassengerService",
			"telecabin",
			"funicular",
			"expressBus",
			"interregionalRail",
			"helicopterService",
			"highSpeedVehicleService",
			"localBus",
			"local",
			"internationalFlight",
			"internationalCarFerry",
			"nightBus",
			"longDistance",
			"internationalPassengerFerry",
			"railReplacementBus",
			"nightRail",
			"airportLinkRail",
			"localCarFerry",
			"regionalBus",
			"regionalRail",
			"localPassengerFerry",
			"schoolBus",
			"touristRailway",
			"nationalCarFerry",
			"shuttleBus",
			"sightseeingService",
			"sightseeingBus"
	};

	private String validTransportModeString;
	private String validTransportSubModeString;

	private String validBookingAccessString = formatLegalEnumValues(BookingAccessEnumeration.PUBLIC.value(),
			BookingAccessEnumeration.AUTHORISED_PUBLIC.value(),
			BookingAccessEnumeration.STAFF.value());
	private String validBookingMethodString = formatLegalEnumValues(BookingMethodEnumeration.CALL_DRIVER.value(),
			BookingMethodEnumeration.CALL_OFFICE.value(),
			BookingMethodEnumeration.ONLINE.value(),
			BookingMethodEnumeration.PHONE_AT_STOP.value(),
			BookingMethodEnumeration.TEXT.value());
	private String validFlexibleLineTypeString = formatLegalEnumValues(FlexibleLineTypeEnumeration.CORRIDOR_SERVICE.value(),
			FlexibleLineTypeEnumeration.MAIN_ROUTE_WITH_FLEXIBLE_ENDS.value(),
			FlexibleLineTypeEnumeration.FLEXIBLE_AREAS_ONLY.value(),
			FlexibleLineTypeEnumeration.HAIL_AND_RIDE_SECTIONS.value(),
			FlexibleLineTypeEnumeration.FIXED_STOP_AREA_WIDE.value(),
			FlexibleLineTypeEnumeration.MIXED_FLEXIBLE.value(),
			FlexibleLineTypeEnumeration.MIXED_FLEXIBLE_AND_FIXED.value(),
			FlexibleLineTypeEnumeration.FIXED.value());
	private String validBookWhenString = formatLegalEnumValues(PurchaseWhenEnumeration.TIME_OF_TRAVEL_ONLY.value(),
			PurchaseWhenEnumeration.DAY_OF_TRAVEL_ONLY.value(),
			PurchaseWhenEnumeration.UNTIL_PREVIOUS_DAY.value(),
			PurchaseWhenEnumeration.ADVANCE_ONLY.value(),
			PurchaseWhenEnumeration.ADVANCE_AND_DAY_OF_TRAVEL.value());
	private String validBuyWhenString = formatLegalEnumValues(PurchaseMomentEnumeration.ON_RESERVATION.value(),
			PurchaseMomentEnumeration.BEFORE_BOARDING.value(),
			PurchaseMomentEnumeration.AFTER_BOARDING.value(),
			PurchaseMomentEnumeration.ON_CHECK_OUT.value());

	public NorwayLineNetexProfileValidator() {
		validTransportModeString = formatLegalEnumValues(validTransportModes);
		validTransportSubModeString = formatLegalEnumValues(validTransportSubModes);
	}

	private String formatLegalEnumValues(String... values) {
		return StringUtils.join(Arrays.asList(values).stream().map(e -> "'" + e + "'").collect(Collectors.toList()), ",");
	}

	@Override
	public void validate(Context context) throws Exception {
		XPathCompiler xpath = (XPathCompiler) context.get(NETEX_XPATH_COMPILER);

		XdmNode dom = (XdmNode) context.get(NETEX_DATA_DOM);

		@SuppressWarnings("unchecked")
		Set<Codespace> validCodespaces = (Set<Codespace>) context.get(NETEX_VALID_CODESPACES);
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);

		// StopPlaceRegistryIdValidator stopRegisterValidator = new StopPlaceRegistryIdValidator();

		@SuppressWarnings("unchecked")
		Map<IdVersion, List<String>> commonIds = (Map<IdVersion, List<String>>) context.get(NETEX_COMMON_FILE_IDENTIFICATORS);

		List<IdVersion> localIdList = NetexIdExtractorHelper.collectEntityIdentificators(context, xpath, dom, new HashSet<>(Arrays.asList("Codespace")));
		Set<IdVersion> localIds = new HashSet<>(localIdList);
		List<IdVersion> localRefs = NetexIdExtractorHelper.collectEntityReferences(context, xpath, dom, null);

		for (IdVersion id : localIds) {
			data.getDataLocations().put(id.getId(), DataLocationHelper.findDataLocation(id));
		}

		verifyAcceptedCodespaces(context, xpath, dom, validCodespaces);
		verifyIdStructure(context, localIds, ID_STRUCTURE_REGEXP, validCodespaces);
		verifyNoDuplicatesWithCommonElements(context, localIds, commonIds);

		verifyNoDuplicatesAcrossLineFiles(context, localIdList,
				new HashSet<>(Arrays.asList("ResourceFrame", "SiteFrame", "CompositeFrame", "TimetableFrame", "ServiceFrame", "ServiceCalendarFrame","RoutePoint","PointProjection","ScheduledStopPoint","PassengerStopAssignment","NoticeAssignment")));

		verifyUseOfVersionOnLocalElements(context, localIds);
		verifyUseOfVersionOnRefsToLocalElements(context, localIds, localRefs);
		verifyReferencesToCommonElements(context, localRefs, localIds, commonIds);
		verifyReferencesToCorrectEntityTypes(context, localRefs);
		verifyExternalRefs(context, localRefs, localIds,commonIds != null ? commonIds.keySet() : new HashSet<>());

		XdmValue compositeFrames = selectNodeSet("/PublicationDelivery/dataObjects/CompositeFrame", xpath, dom);
		if (compositeFrames.size() > 0) {
			// Using composite frames
			for (XdmItem compositeFrame : compositeFrames) {
				validateCompositeFrame(context, xpath, (XdmNode) compositeFrame);
			}
		} else {
			// Not using composite frames
			validateWithoutCompositeFrame(context, xpath, dom);

		}
		return;
	}

	protected void validateWithoutCompositeFrame(Context context, XPathCompiler xpath, XdmNode dom) throws XPathExpressionException, SaxonApiException {
		// Validate that we have exactly one ResourceFrame
		validateElementPresent(context, xpath, dom, "/PublicationDelivery/dataObjects/ResourceFrame", _1_NETEX_RESOURCE_FRAME);
		validateResourceFrame(context, xpath, dom, "/PublicationDelivery/dataObjects/ResourceFrame");

		// Validate at least 1 ServiceFrame is present
		validateAtLeastElementPresent(context, xpath, dom, "/PublicationDelivery/dataObjects/ServiceFrame", 1, _1_NETEX_SERVICE_FRAME);
		XdmValue serviceFrames = selectNodeSet("/PublicationDelivery/dataObjects/ServiceFrame", xpath, dom);
		for (XdmItem serviceFrame : serviceFrames) {
			validateServiceFrame(context, xpath, (XdmNode) serviceFrame, null);
		}

		// Validate at least 1 TimetableFrame is present
		validateAtLeastElementPresent(context, xpath, dom, "/PublicationDelivery/dataObjects/TimetableFrame", 1, _1_NETEX_TIMETABLE_FRAME);
		XdmValue timetableFrames = selectNodeSet("/PublicationDelivery/dataObjects/TimetableFrame", xpath, dom);
		for (XdmItem timetalbeFrame : timetableFrames) {
			validateTimetableFrame(context, xpath, (XdmNode) timetalbeFrame, null);
		}

		// No siteframe allowed
		validateElementNotPresent(context, xpath, dom, "/PublicationDelivery/dataObjects/SiteFrame", _1_NETEX_SITE_FRAME);

		// Validate that at least one frame has validityConditions
		validateAtLeastElementPresent(context, xpath, dom,
				"/PublicationDelivery/dataObjects/ServiceFrame[validityConditions] | /PublicationDelivery/dataObjects/TimetableFrame[validityConditions] | /PublicationDelivery/dataObjects/ServiceCalendarFrame[validityConditions] ",
				1, _1_NETEX_NO_VALIDITYCONDITIONS_ON_FRAMES_OUTSIDE_COMPOSITEFRAME);

		// If more than one of a kind, all must have validity conditions
		validateElementNotPresent(context, xpath, dom, "//ServiceCalendarFrame[not(validityConditions) and count(//ServiceCalendarFrame) > 1]",
				_1_NETEX_MULTIPLE_FRAMES_OF_SAME_TYPE_WITHOUT_VALIDITYCONDITIONS);
		validateElementNotPresent(context, xpath, dom, "//ServiceFrame[not(validityConditions) and count(//ServiceFrame) > 1]",
				_1_NETEX_MULTIPLE_FRAMES_OF_SAME_TYPE_WITHOUT_VALIDITYCONDITIONS);
		validateElementNotPresent(context, xpath, dom, "//TimetableFrame[not(validityConditions) and count(//TimetableFrame) > 1]",
				_1_NETEX_MULTIPLE_FRAMES_OF_SAME_TYPE_WITHOUT_VALIDITYCONDITIONS);

	}

	private void validateCompositeFrame(Context context,  XPathCompiler xpath, XdmNode dom) throws XPathExpressionException, SaxonApiException {

		validateCommonFrameConcepts(context, xpath, dom);
		
		XdmValue resourceFrames = selectNodeSet("frames/ResourceFrame", xpath, dom);
		for (XdmItem resourceFrame : resourceFrames) {
			validateResourceFrame(context, xpath, (XdmNode) resourceFrame, null);
		}

		validateAtLeastElementPresent(context, xpath, dom, "frames/ServiceFrame",1, _1_NETEX_SERVICE_FRAME);
		XdmValue serviceFrames = selectNodeSet("frames/ServiceFrame", xpath, dom);
		for (XdmItem serviceFrame : serviceFrames) {
			validateServiceFrame(context, xpath, (XdmNode) serviceFrame, null);
		}

		validateAtLeastElementPresent(context, xpath, dom, "frames/TimetableFrame",1, _1_NETEX_TIMETABLE_FRAME);
		XdmValue timetableFrames = selectNodeSet("frames/TimetableFrame", xpath, dom);
		for (XdmItem timetableFrame : timetableFrames) {
			validateTimetableFrame(context, xpath, (XdmNode) timetableFrame, null);
		}

		XdmValue serviceCalendarFrames = selectNodeSet("frames/ServiceCalendarFrame", xpath, dom);
		for (XdmItem serviceCalendarFrame : serviceCalendarFrames) {
			validateServiceCalendarFrame(context, xpath, (XdmNode) serviceCalendarFrame, null);
		}

		validateElementNotPresent(context, xpath, dom, "frames/SiteFrame", _1_NETEX_SITE_FRAME);
	}

	private void validateServiceFrame(Context context,  XPathCompiler xpath, XdmNode dom, String subLevelPath) throws XPathExpressionException, SaxonApiException {
		XdmNode subLevel = dom;
		if (subLevelPath != null) {
			subLevel = (XdmNode) selectNode(subLevelPath, xpath, dom);
		}

		if (subLevel != null) {

			validateServiceFrameCommonElements(context, xpath, subLevel);
			validateElementPresent(context, xpath, subLevel, "lines/*[self::Line or self::FlexibleLine]", _1_NETEX_SERVICE_FRAME_LINE);
			validateElementNotPresent(context, xpath, subLevel, "lines/*[self::Line or self::FlexibleLine][not(Name) or normalize-space(Name) = '']", _1_NETEX_SERVICE_FRAME_LINE_NAME);
			validateElementNotPresent(context, xpath, subLevel, "lines/*[self::Line or self::FlexibleLine][not(PublicCode) or normalize-space(PublicCode) = '']", _1_NETEX_SERVICE_FRAME_LINE_PUBLIC_CODE);
			validateElementNotPresent(context, xpath, subLevel, "lines/*[self::Line or self::FlexibleLine][not(TransportMode)]", _1_NETEX_SERVICE_FRAME_LINE_TRANSPORTMODE);
			validateElementNotPresent(context, xpath, subLevel, "lines/*[self::Line or self::FlexibleLine][not(TransportSubmode)]", _1_NETEX_SERVICE_FRAME_LINE_TRANSPORTSUBMODE);
			validateElementNotPresent(context, xpath, subLevel, "lines/*[self::Line or self::FlexibleLine]/routes/Route", _1_NETEX_SERVICE_FRAME_ROUTE_INDIRECTION);
			validateElementNotPresent(context, xpath, subLevel, "lines/*[self::Line or self::FlexibleLine][not(RepresentedByGroupRef)]",
					_1_NETEX_SERVICE_FRAME_LINE_GROUPOFLINES_OR_NETWORK);

			validateElementNotPresent(context, xpath, subLevel, "lines/FlexibleLine[not(BookingContact)]", _1_NETEX_SERVICE_FRAME_FLEXBIBLE_LINE_BOOKINGCONTACT);
			validateElementNotPresent(context, xpath, subLevel, "lines/FlexibleLine[not(BookWhen)]", _1_NETEX_SERVICE_FRAME_FLEXBIBLE_LINE_BOOKWHEN);
			validateElementNotPresent(context, xpath, subLevel, "lines/FlexibleLine[not(FlexibleLineType)]", _1_NETEX_SERVICE_FRAME_FLEXBIBLE_LINE_FLEXIBLELINETYPE);
			validateElementNotPresent(context, xpath, subLevel, "lines/FlexibleLine[not(BookingMethods)]", _1_NETEX_SERVICE_FRAME_FLEXBIBLE_LINE_BOOKINGMETHODS);


			validateElementNotPresent(context, xpath, subLevel, "lines/FlexibleLine/FlexibleLineType[not(. = ("+ validFlexibleLineTypeString +"))]",
					_1_NETEX_SERVICE_FRAME_FLEXBIBLE_LINE_ILLEGAL_FLEXIBLELINETYPE);
			validateElementNotPresent(context, xpath, subLevel, "lines/FlexibleLine/BookWhen[not(. = ("+ validBookWhenString +"))]",
					_1_NETEX_SERVICE_FRAME_FLEXBIBLE_LINE_ILLEGAL_BOOKWHEN);
			validateElementNotPresent(context, xpath, subLevel, "lines/FlexibleLine/BuyWhen[tokenize(.,' ')[not(. = ("+ validBuyWhenString +"))]]",
					_1_NETEX_SERVICE_FRAME_FLEXBIBLE_LINE_ILLEGAL_BUYWHEN);
			validateElementNotPresent(context, xpath, subLevel, "lines/FlexibleLine/BookingMethods[tokenize(.,' ')[not(. = ("+ validBookingMethodString +"))]]",
					_1_NETEX_SERVICE_FRAME_FLEXBIBLE_LINE_ILLEGAL_BOOKINGMETHODS);
			validateElementNotPresent(context, xpath, subLevel, "lines/FlexibleLine/BookingAccess[not(. = ("+ validBookingAccessString +"))]",
					_1_NETEX_SERVICE_FRAME_FLEXBIBLE_LINE_ILLEGAL_BOOKINGACCESS);

			validateAtLeastElementPresent(context, xpath, subLevel, "routes/Route", 1, _1_NETEX_SERVICE_FRAME_ROUTE_INDIRECTION);
			validateElementNotPresent(context, xpath, subLevel, "routes/Route[not(Name) or normalize-space(Name) = '']", _1_NETEX_SERVICE_FRAME_ROUTE_NAME);
			validateElementNotPresent(context, xpath, subLevel, "routes/Route[not(LineRef)]", _1_NETEX_SERVICE_FRAME_ROUTE_LINEREF);
			validateElementNotPresent(context, xpath, subLevel, "routes/Route[not(pointsInSequence)]", _1_NETEX_SERVICE_FRAME_ROUTE_POINTSINSEQUENCE);

	//		validateElementNotPresent(context, xpath, subLevel, "journeyPatterns/ServiceJourneyPattern", _1_NETEX_SERVICE_FRAME_SERVICE_JOURNEY_PATTERN);
			validateAtLeastElementPresent(context, xpath, subLevel, "journeyPatterns/JourneyPattern | journeyPatterns/ServiceJourneyPattern", 1,
					_1_NETEX_SERVICE_FRAME_JOURNEY_PATTERN);

			
			validateElementNotPresent(context, xpath, subLevel,
					"journeyPatterns/ServiceJourneyPattern[not(RouteRef)] | journeyPatterns/JourneyPattern[not(RouteRef)]",
					_1_NETEX_SERVICE_FRAME_JOURNEY_PATTERN_ROUTE_REF);

			validateElementNotPresent(context, xpath, subLevel, "//pointsInSequence/StopPointInJourneyPattern[1][not(DestinationDisplayRef)]",
					_1_NETEX_COMMON_SERVICE_FRAME_SERVICE_JOURNEY_PATTERN_MISSING_DESTINATIONDISPLAY);

			validateElementNotPresent(context, xpath, subLevel, "destinationDisplays/DestinationDisplay[not(FrontText) or normalize-space(FrontText) = '']",
					_1_NETEX_SERVICE_FRAME_DESTINATION_DISPLAY_FRONTTEXT);
			validateElementNotPresent(context, xpath, subLevel, "destinationDisplays/DestinationDisplay/vias/Via[not(DestinationDisplayRef)]",
					_1_NETEX_SERVICE_FRAME_DESTINATION_DISPLAY_VIA_DESTINATIONDISPLAYREF);
			validateElementNotPresent(context, xpath, subLevel, "//StopPointInJourneyPattern[ForAlighting = 'false' and ForBoarding = 'false']",
					_1_NETEX_SERVICE_FRAME_STOP_WITHOUT_BOARDING_OR_ALIGHTING);
			validateElementNotPresent(context, xpath, subLevel, "//StopPointInJourneyPattern[DestinationDisplayRef/@ref = preceding-sibling::StopPointInJourneyPattern[1]/DestinationDisplayRef/@ref and number(@order) >  number(preceding-sibling::StopPointInJourneyPattern[1]/@order)]",
					_1_NETEX_SERVICE_FRAME_STOP_WITH_REPEATING_DESTINATIONDISPLAYREF);

			validateElementNotPresent(context, xpath, subLevel, "lines/*[self::Line or self::FlexibleLine]/TransportMode[not(. = ("+ validTransportModeString +"))]",
					_1_NETEX_SERVICE_FRAME_INVALID_TRANSPORTMODE);
			validateElementNotPresent(context, xpath, subLevel, "lines/*[self::Line or self::FlexibleLine]/TransportSubmode/*[not(. = ("+ validTransportSubModeString +"))]",
					_1_NETEX_SERVICE_FRAME_INVALID_TRANSPORTSUBMODE);
			
			
		}
	}

	private void validateTimetableFrame(Context context, XPathCompiler xpath, XdmNode dom, String subLevelPath) throws XPathExpressionException, SaxonApiException {
		XdmNode subLevel = dom;
		if (subLevelPath != null) {
			subLevel = (XdmNode) selectNode(subLevelPath, xpath, dom);
		}

		if (subLevel != null) {

			validateAtLeastElementPresent(context, xpath, subLevel, "vehicleJourneys/ServiceJourney", 1, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY);
			validateElementNotPresent(context, xpath, subLevel, "vehicleJourneys/ServiceJourney/calls", _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_CALLS);

			validateElementNotPresent(context, xpath, subLevel, "vehicleJourneys/ServiceJourney[not(passingTimes)]",
					_1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIMES);
			validateElementNotPresent(context, xpath, subLevel,
					"vehicleJourneys/ServiceJourney/passingTimes/TimetabledPassingTime[not(DepartureTime) and not(ArrivalTime)]",
					_1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_MISSING_DEPARTURE_OR_ARRIVAL);
			validateElementNotPresent(context, xpath, subLevel,
					"vehicleJourneys/ServiceJourney[not(passingTimes/TimetabledPassingTime[1]/DepartureTime)]",
					_1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_FIRST_DEPARTURE);
			validateElementNotPresent(context, xpath, subLevel,
					"vehicleJourneys/ServiceJourney[count(passingTimes/TimetabledPassingTime[last()]/ArrivalTime) = 0]",
					_1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_LAST_ARRIVAL);
			validateElementNotPresent(context, xpath, subLevel, "//TimetabledPassingTime[DepartureTime = ArrivalTime]",
					_1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_SAME_VALUE);

			validateElementNotPresent(context, xpath, subLevel, "//TimetabledPassingTime[not(@id)]",
					_1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_ID);
			validateElementNotPresent(context, xpath, subLevel, "//TimetabledPassingTime[not(@version)]",
					_1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_VERSION);


			validateElementNotPresent(context, xpath, subLevel, "vehicleJourneys/ServiceJourney[not(JourneyPatternRef)]",
					_1_NETEX_TIMETABLE_FRAME_SERVICEJOURNEY_JOURNEYPATTERN_REF);
			validateElementNotPresent(context, xpath, subLevel, "vehicleJourneys/ServiceJourney[(TransportMode and not(TransportSubmode))  or (not(TransportMode) and TransportSubmode)]",
					_1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_TRANSPORTMODE_OVERRIDE);
			
			validateElementNotPresent(context, xpath, subLevel, "vehicleJourneys/ServiceJourney[not(OperatorRef) and not(//ServiceFrame/lines/*[self::Line or self::FlexibleLine]/OperatorRef)]", _1_NETEX_TIMETABLE_FRAME_VEHICLEJOURNEY_OPERATORREF_OR_LINE_OPREATORREF);

			validateElementNotPresent(context, xpath, subLevel, "vehicleJourneys/ServiceJourney[not(dayTypes/DayTypeRef)]", _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_DAYTYPEREF);

			validateElementNotPresent(context, xpath, subLevel, "for $a in vehicleJourneys/ServiceJourney return if(count(//ServiceFrame/journeyPatterns/*[@id = $a/JourneyPatternRef/@ref]/pointsInSequence/StopPointInJourneyPattern) != count($a/passingTimes/TimetabledPassingTime)) then $a else ()", _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_MISSING_PASSING_TIME);
			
			validateElementNotPresent(context, xpath, subLevel, "//ServiceJourney[@id = preceding-sibling::ServiceJourney/@id]", _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_DUPLICATE_WITH_DIFFERENT_VERSION);
			

		}
	}

	public static class DefaultValidatorFactory extends NetexProfileValidatorFactory {
		@Override
		protected NetexProfileValidator create(Context context) throws ClassNotFoundException {
			NetexProfileValidator instance = (NetexProfileValidator) context.get(NAME);
			if (instance == null) {
				instance = new NorwayLineNetexProfileValidator();

				// Shitty, should use inversion of control pattern and dependency injection
				if("true".equals(context.get("testng"))) {
					instance.addExternalReferenceValidator(new DummyStopReferentialIdValidator());
				} else {
					StopPlaceRegistryIdValidator stopRegistryValidator = (StopPlaceRegistryIdValidator) DefaultExternalReferenceValidatorFactory
							.create(StopPlaceRegistryIdValidator.class.getName(), context);
					instance.addExternalReferenceValidator(stopRegistryValidator);
				}
				
				instance.addExternalReferenceValidator(new ServiceJourneyInterchangeIgnorer());

				context.put(NAME, instance);
			}
			return instance;
		}
	}

	static {
		NetexProfileValidatorFactory.factories.put(NorwayLineNetexProfileValidator.class.getName(),
				new NorwayLineNetexProfileValidator.DefaultValidatorFactory());
	}

	@Override
	public boolean isCommonFileValidator() {
		return false;
	}

}
