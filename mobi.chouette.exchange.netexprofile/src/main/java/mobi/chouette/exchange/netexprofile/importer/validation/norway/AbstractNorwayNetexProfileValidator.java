package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.DataManagedObjectStructure;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.importer.util.DataLocationHelper;
import mobi.chouette.exchange.netexprofile.importer.util.IdVersion;
import mobi.chouette.exchange.netexprofile.importer.validation.AbstractNetexProfileValidator;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XdmNode;

public abstract class AbstractNorwayNetexProfileValidator extends AbstractNetexProfileValidator {

	public static final String PROFILE_NORWAY_NETWORKTIMETABLE_104_10 = "1.04:NO-NeTEx-networktimetable:1.0";
	public static final String PROFILE_NORWAY_NETWORKTIMETABLE_104_11 = "1.04:NO-NeTEx-networktimetable:1.1";
	public static final String PROFILE_NORWAY_NETWORKTIMETABLE_107_11 = "1.07:NO-NeTEx-networktimetable:1.1";
	public static final String PROFILE_NORWAY_NETWORKTIMETABLE_108_11 = "1.08:NO-NeTEx-networktimetable:1.1";
	public static final String PROFILE_NORWAY_NETWORKTIMETABLE_108_12 = "1.08:NO-NeTEx-networktimetable:1.2";
	public static final String PROFILE_NORWAY_NETWORKTIMETABLE_108_13 = "1.08:NO-NeTEx-networktimetable:1.3";
	public static final String PROFILE_NORWAY_NETWORKTIMETABLE_109_13 = "1.09:NO-NeTEx-networktimetable:1.3";
	public static final String PROFILE_NORWAY_NETWORKTIMETABLE_110_13 = "1.10:NO-NeTEx-networktimetable:1.3";
	public static final String PROFILE_NORWAY_NETWORKTIMETABLE_111_13 = "1.11:NO-NeTEx-networktimetable:1.3";
	public static final String PROFILE_NORWAY_NETWORKTIMETABLE_112_13 = "1.12:NO-NeTEx-networktimetable:1.3";
	public static final String PROFILE_NORWAY_NETWORKTIMETABLE_113_13 = "1.13:NO-NeTEx-networktimetable:1.3";

	public static final String EXPORT_PROFILE_ID = PROFILE_NORWAY_NETWORKTIMETABLE_113_13; // Update when new profile version is implemented
	
	public static final String NSR_XMLNSURL = "http://www.rutebanken.org/ns/nsr";
	public static final String NSR_XMLNS = "NSR";

	public static final String _1_NETEX_REFERENCE_TO_ILLEGAL_ELEMENT = "1-NETEXPROFILE-ReferenceToIllegalElement";
	public static final String _1_NETEX_NOTICE_TEXT = "1-NETEXPROFILE-Notice-Text";
	public static final String _1_NETEX_NOTICE_ALTERNATIVE_TEXT_TEXT = "1-NETEXPROFILE-Notice-Alternative-Text-Text";
	public static final String _1_NETEX_NOTICE_ALTERNATIVE_TEXT_LANG = "1-NETEXPROFILE-Notice-Alternative-Text-Lang";
	public static final String _1_NETEX_NOTICE_ALTERNATIVE_TEXT_DUPLICATE_LANG = "1-NETEXPROFILE-Notice-Alternative-Text-Duplicate-Lang";

	public static final String _1_NETEX_NOTICE_ASSIGNMENTS_DUPLICATE = "1-NETEXPROFILE-NoticeAssignment-Duplicate";

	public static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CUSTOMER_SERVICE_CONTACT_DETAILS = "1-NETEXPROFILE-ResourceFrame-Organisations-Operator-CustomerServiceContactDetails";
	public static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CUSTOMER_SERVICE_CONTACT_DETAILS_URL = "1-NETEXPROFILE-ResourceFrame-Organisations-Operator-CustomerServiceContactDetails-Url";
	public static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CONTACT_DETAILS = "1-NETEXPROFILE-ResourceFrame-Organisations-Operator-ContactDetails";
	public static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CONTACT_DETAILS_URL_OR_PHONE_OR_EMAIL = "1-NETEXPROFILE-ResourceFrame-Organisations-Operator-ContactDetails-UrlOrPhoneOrEmail";
	public static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_LEGAL_NAME = "1-NETEXPROFILE-ResourceFrame-Organisations-Operator-LegalName";
	public static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_NAME = "1-NETEXPROFILE-ResourceFrame-Organisations-Operator-Name";
	public static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_COMPANY_NUMBER = "1-NETEXPROFILE-ResourceFrame-Organisations-Operator-CompanyNumber";
	public static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_CONTACT_DETAILS = "1-NETEXPROFILE-ResourceFrame-Organisations-Authority-ContactDetails";
	public static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_CONTACT_DETAILS_URL = "1-NETEXPROFILE-ResourceFrame-Organisations-Authority-ContactDetails-Url";
	public static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_LEGAL_NAME = "1-NETEXPROFILE-ResourceFrame-Organisations-Authority-LegalName";
	public static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_NAME = "1-NETEXPROFILE-ResourceFrame-Organisations-Authority-Name";
	public static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_COMPANY_NUMBER = "1-NETEXPROFILE-ResourceFrame-Organisations-Authority-CompanyNumber";
	public static final String _1_NETEX_RESOURCE_FRAME = "1-NETEXPROFILE-TimetableFrame";
	public static final String _1_NETEX_TIMETABLE_FRAME = "1-NETEXPROFILE-TimetableFrame";
	public static final String _1_NETEX_SERVICE_FRAME = "1-NETEXPROFILE-ServiceFrame";
	public static final String _1_NETEX_CODESPACE = "1-NETEXPROFILE-CompositeFrame-Codespace";
	public static final String _1_NETEX_SITE_FRAME = "1-NETEXPROFILE-SiteFrame";
	public static final String _1_NETEX_SERVICE_FRAME_GROUPOFLINES_OUTSIDE_NETWORK = "1-NETEXPROFILE-ServiceFrame-GroupOfLinesOutsideNetwork";
	public static final String _1_NETEX_SERVICE_FRAME_LINE = "1-NETEXPROFILE-ServiceFrame-Line";
	public static final String _1_NETEX_SERVICE_FRAME_LINE_NAME = "1-NETEXPROFILE-ServiceFrame-Line-Name";
	public static final String _1_NETEX_SERVICE_FRAME_NETWORK_AUTHORITY_REF = "1-NETEXPROFILE-ServiceFrame-Network-AutorityRef";
	public static final String _1_NETEX_SERVICE_FRAME_ROUTEPOINT_PROJECTION = "1-NETEXPROFILE-ServiceFrame-RoutePoint-Projection";
	public static final String _1_NETEX_SERVICE_FRAME_NETWORK_NAME = "1-NETEXPROFILE-ServiceFrame-Network-Name";
	public static final String _1_NETEX_SERVICE_FRAME_NETWORK_GROUPOFLINE_NAME = "1-NETEXPROFILE-ServiceFrame-Network-GroupOfLine-Name";
	public static final String _1_NETEX_SERVICE_FRAME_TIMING_POINTS = "1-NETEXPROFILE-ServiceFrame_TimingPoints";
	public static final String _1_NETEX_SERVICE_FRAME_JOURNEY_PATTERN = "1-NETEXPROFILE-ServiceFrame_JourneyPattern";
	public static final String _1_NETEX_SERVICE_FRAME_LINE_PUBLIC_CODE = "1-NETEXPROFILE-ServiceFrame-Line-PublicCode";
	public static final String _1_NETEX_SERVICE_FRAME_INVALID_TRANSPORTSUBMODE = "1-NETEXPROFILE-ServiceFrame-Line-IllegalTransportSubmode";
	public static final String _1_NETEX_SERVICE_FRAME_INVALID_TRANSPORTMODE = "1-NETEXPROFILE-ServiceFrame-Line-IllegalTransportMode";

	public static final String _1_NETEX_TIMETABLE_FRAME_VEHICLEJOURNEY_OPERATORREF_OR_LINE_OPREATORREF = "1-NETEXPROFILE-TimetableFrame-ServiceJourney-OperatorRef-Or-Line-OperatorRef";
	public static final String _1_NETEX_SERVICE_FRAME_JOURNEY_PATTERN_ROUTE_REF = "1-NETEXPROFILE-ServiceFrame_JourneyPattern_RouteRef";
	public static final String _1_NETEX_COMMON_SERVICE_FRAME_SERVICE_JOURNEY_PATTERN_MISSING_DESTINATIONDISPLAY = "1-NETEXPROFILE-ServiceFrame_JourneyPattern_First_StopPointInJourneyPattern_DestinationDisplayRef";
	public static final String _1_NETEX_COMMON_SERVICE_FRAME_SERVICE_JOURNEY_PATTERN_DESTINATIONDISPLAY_ON_LAST_STOP = "1-NETEXPROFILE-ServiceFrame_JourneyPattern_First_StopPointInJourneyPattern_DestinationDisplayRefOnLastStop";

	public static final String _1_NETEX_SERVICE_FRAME_FLEXBIBLE_LINE_FLEXIBLELINETYPE = "1-NETEXPROFILE-ServiceFrame_FlexibleLine_FlexibleLineType";
	public static final String _1_NETEX_SERVICE_FRAME_FLEXBIBLE_LINE_ILLEGAL_FLEXIBLELINETYPE = "1-NETEXPROFILE-ServiceFrame_FlexibleLine_IllegalFlexibleLineType";
	public static final String _1_NETEX_SERVICE_FRAME_FLEXBIBLE_LINE_ILLEGAL_BOOKINGMETHODS = "1-NETEXPROFILE-ServiceFrame_FlexibleLine_IllegalBookingMethods";
	public static final String _1_NETEX_SERVICE_FRAME_FLEXBIBLE_LINE_ILLEGAL_BOOKWHEN = "1-NETEXPROFILE-ServiceFrame_FlexibleLine_IllegalBookWhen";
	public static final String _1_NETEX_SERVICE_FRAME_FLEXBIBLE_LINE_ILLEGAL_BOOKINGACCESS = "1-NETEXPROFILE-ServiceFrame_FlexibleLine_IllegalBookingAccess";
	public static final String _1_NETEX_SERVICE_FRAME_FLEXBIBLE_LINE_ILLEGAL_BUYWHEN = "1-NETEXPROFILE-ServiceFrame_FlexibleLine_IllegalBuyWhen";

	public static final String _1_NETEX_SERVICE_FRAME_LINE_TRANSPORTMODE = "1-NETEXPROFILE-ServiceFrame_Line_TransportMode";
	public static final String _1_NETEX_SERVICE_FRAME_LINE_TRANSPORTSUBMODE = "1-NETEXPROFILE-ServiceFrame_Line_TransportSubmode";
	public static final String _1_NETEX_SERVICE_FRAME_LINE_GROUPOFLINES_OR_NETWORK = "1-NETEXPROFILE-ServiceFrame_Line_GroupOfLinesNetwork";
	public static final String _1_NETEX_SERVICE_FRAME_ROUTE_INDIRECTION = "1-NETEXPROFILE-ServiceFrame_Route";
	public static final String _1_NETEX_SERVICE_FRAME_ROUTE_NAME = "1-NETEXPROFILE-ServiceFrame_Route_Name";
	public static final String _1_NETEX_SERVICE_FRAME_ROUTE_LINEREF = "1-NETEXPROFILE-ServiceFrame_Route_LineRef";
	public static final String _1_NETEX_SERVICE_FRAME_ROUTE_DIRECTIONREF = "1-NETEXPROFILE-ServiceFrame_Route_DirectionRef";
	public static final String _1_NETEX_SERVICE_FRAME_ROUTE_POINTSINSEQUENCE = "1-NETEXPROFILE-ServiceFrame_Route_PointsInSequence";
	public static final String _1_NETEX_SERVICE_FRAME_ROUTE_POINTSINSEQUENCE_DUPLICATE_ORDER = "1-NETEXPROFILE-ServiceFrame_Route_PointsInSequence_Duplicate_Order";
	public static final String _1_NETEX_SERVICE_FRAME_DESTINATION_DISPLAY_FRONTTEXT = "1-NETEXPROFILE-ServiceFrame-DestinationDisplay-FrontText";
	public static final String _1_NETEX_SERVICE_FRAME_DESTINATION_DISPLAY_VIA_DESTINATIONDISPLAYREF = "1-NETEXPROFILE-ServiceFrame-DestinationDisplay-Via-DestinationDisplayRef";
	public static final String _1_NETEX_SERVICE_FRAME_STOP_WITHOUT_BOARDING_OR_ALIGHTING = "1-NETEXPROFILE-ServiceFrame-StopPointInJourneyPattern-NoBoardingNoAlighting";
	public static final String _1_NETEX_SERVICE_FRAME_STOP_WITH_REPEATING_DESTINATIONDISPLAYREF = "1-NETEXPROFILE-ServiceFrame-StopPointInJourneyPattern-Repeating-DestinationDisplayRef";
	public static final String _1_NETEX_SERVICE_FRAME_STOP_POINT_ILLEGAL_BOOKINGMETHODS = "1-NETEXPROFILE-ServiceFrame_StopPointInJourneyPattern_IllegalBookingMethods";
	public static final String _1_NETEX_SERVICE_FRAME_STOP_POINT_ILLEGAL_BOOKWHEN = "1-NETEXPROFILE-ServiceFrame_StopPointInJourneyPattern_IllegalBookWhen";
	public static final String _1_NETEX_SERVICE_FRAME_STOP_POINT_ILLEGAL_BOOKINGACCESS = "1-NETEXPROFILE-ServiceFrame_StopPointInJourneyPattern_IllegalBookingAccess";
	public static final String _1_NETEX_SERVICE_FRAME_STOP_POINT_ILLEGAL_BUYWHEN = "1-NETEXPROFILE-ServiceFrame_StopPointInJourneyPattern_IllegalBuyWhen";
	public static final String _1_NETEX_SERVICE_FRAME_PASSENGER_STOP_ASSIGNMENT_SCHEDULEDSTOPPOINTREF = "1-NETEXPROFILE-ServiceFrame-PassengerStopAssignment-ScheduledStopPointRef";
	public static final String _1_NETEX_SERVICE_FRAME_PASSENGER_STOP_ASSIGNMENT_QUAYREF = "1-NETEXPROFILE-ServiceFrame-PassengerStopAssignment-QuayRef";
	public static final String _1_NETEX_SERVICE_FRAME_PASSENGER_STOP_ASSIGNMENT_DUPLICATE = "1-NETEXPROFILE-ServiceFrame-PassengerStopAssignment-Duplicate";


	public static final String _1_NETEX_SERVICE_FRAME_SERVICE_LINK_TOPOINTREF = "1-NETEXPROFILE-ServiceFrame-ServiceLink-ToPointRef";
	public static final String _1_NETEX_SERVICE_FRAME_SERVICE_LINK_FROMPOINTREF = "1-NETEXPROFILE-ServiceFrame-ServiceLink-FromPointRef";
	public static final String _1_NETEX_SERVICE_FRAME_SERVICE_LINK_MISSING_POSITION_COORDINATES = "1-NETEXPROFILE-ServiceFrame-ServiceLink-Missing-Position-Coordinates";

	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY = "1-NETEXPROFILE-TimetableFrame-ServiceJourney";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_DAYTYPEREF = "1-NETEXPROFILE-TimetableFrame-ServiceJourney-DayTypeRef";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_DAYTYPEREF_AND_DATED_SERVICE_JOURNEY = "1-NETEXPROFILE-TimetableFrame-ServiceJourney-DayTypeRef-DatedServiceJourney";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIMES = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_TimetabledPassingTime";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_CALLS = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_Calls";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_MISSING_DEPARTURE_OR_ARRIVAL = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_TimetabledPassingTime_Missing_DepartureTimeArrivalTime";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_FIRST_DEPARTURE = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_TimetabledPassingTime_DepartureTime";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_LAST_ARRIVAL = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_TimetabledPassingTime_Last_ArrivalTime";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_SAME_VALUE = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_TimetabledPassingTime_Same_Value";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_ID = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_TimetabledPassingTime_Id";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_VERSION = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_TimetabledPassingTime_Version";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICEJOURNEY_JOURNEYPATTERN_REF = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_JourneyPatternRef";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_MISSING_PASSING_TIME = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_MissingTimetabledPassingTime";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_TRANSPORTMODE_OVERRIDE = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_TransportModeOverride";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_DUPLICATE_WITH_DIFFERENT_VERSION = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_DuplicateWithDifferentVersion";

	public static final String _1_NETEX_TIMETABLE_FRAME_DATED_SERVICE_JOURNEY_OPERATINGDAYREF = "1-NETEXPROFILE-TimetableFrame-DatedServiceJourney-OperatingDayRef";
	public static final String _1_NETEX_TIMETABLE_FRAME_DATED_SERVICE_JOURNEY_SERVICEJOURNEYREF = "1-NETEXPROFILE-TimetableFrame-DatedServiceJourney-ServiceJourneyRef";
	public static final String _1_NETEX_TIMETABLE_FRAME_DATED_SERVICE_JOURNEY_MULTIPLE_SERVICEJOURNEYREF = "1-NETEXPROFILE-TimetableFrame-DatedServiceJourney-Multiple-ServiceJourneyRef";
	public static final String _1_NETEX_TIMETABLE_FRAME_DATED_SERVICE_JOURNEY_DUPLICATE_WITH_DIFFERENT_VERSION = "1-NETEXPROFILE-TimetableFrame_DatedServiceJourney_DuplicateWithDifferentVersion" ;

	public static final String _1_NETEX_TIMETABLE_FRAME_DEAD_RUN_PASSING_TIMES = "1-NETEXPROFILE-TimetableFrame_DeadRun_TimetabledPassingTime";
	public static final String _1_NETEX_TIMETABLE_FRAME_DEAD_RUN_JOURNEYPATTERN_REF = "1-NETEXPROFILE-TimetableFrame_DeadRun_JourneyPatternRef";
	public static final String _1_NETEX_TIMETABLE_FRAME_DEAD_RUN_DAYTYPE_REF = "1-NETEXPROFILE-TimetableFrame-DeadRun-DayTypeRef";


	public static final String _1_NETEX_TIMETABLE_FRAME_FLEXIBLE_SERVICE_PROPERTIES_BOOKINGCONTACT = "1-NETEXPROFILE-TimetableFrame_FlexibleServiceProperties_BookingContact";
	public static final String _1_NETEX_TIMETABLE_FRAME_FLEXIBLE_SERVICE_PROPERTIES_BOOKINGMETHODS = "1-NETEXPROFILE-TimetableFrame_FlexibleServiceProperties_BookingMethods";
	public static final String _1_NETEX_TIMETABLE_FRAME_FLEXIBLE_SERVICE_PROPERTIES_ID = "1-NETEXPROFILE-TimetableFrame_FlexibleServiceProperties_Id";
	public static final String _1_NETEX_TIMETABLE_FRAME_FLEXIBLE_SERVICE_PROPERTIES_VERSION = "1-NETEXPROFILE-TimetableFrame_FlexibleServiceProperties_Version";
	public static final String _1_NETEX_TIMETABLE_FRAME_FLEXIBLE_SERVICE_PROPERTIES_ILLEGAL_BOOKINGMETHODS = "1-NETEXPROFILE-TimetableFrame_FlexibleServiceProperties_IllegalBookingMethods";
	public static final String _1_NETEX_TIMETABLE_FRAME_FLEXIBLE_SERVICE_PROPERTIES_BOOKWHEN = "1-NETEXPROFILE-TimetableFrame_FlexibleServiceProperties_BookWhen";
	public static final String _1_NETEX_TIMETABLE_FRAME_FLEXIBLE_SERVICE_PROPERTIES_ILLEGAL_BOOKWHEN = "1-NETEXPROFILE-TimetableFrame_FlexibleServiceProperties_IllegalBookWhen";
	public static final String _1_NETEX_TIMETABLE_FRAME_FLEXIBLE_SERVICE_PROPERTIES_ILLEGAL_BOOKINGACCESS = "1-NETEXPROFILE-TimetableFrame_FlexibleServiceProperties_IllegalBookingAccess";
	public static final String _1_NETEX_TIMETABLE_FRAME_FLEXIBLE_SERVICE_PROPERTIES_ILLEGAL_BUYWHEN = "1-NETEXPROFILE-TimetableFrame_FlexibleServiceProperties_IllegalBuyWhen";
	public static final String _1_NETEX_TIMETABLE_FRAME_FLEXIBLE_SERVICE_PROPERTIES_ILLEGAL_FLEXIBLESERVICETYPE = "1-NETEXPROFILE-TimetableFrame_FlexibleServiceProperties_IllegalFlexibleServiceType";

	public static final String _1_NETEX_TIMETABLE_FRAME_INTERCHANGE_PLANNED_AND_ADVERTISED = "1-NETEXPROFILE-TimetableFrame_Interchange_PlannedAndAdvertised";
	public static final String _1_NETEX_TIMETABLE_FRAME_INTERCHANGE_GUARANTEED_AND_MAX_WAIT_TIME_ZERO = "1-NETEXPROFILE-TimetableFrame_Interchange_GuaranteedAndMaxWaitTimeZero";
	public static final String _1_NETEX_TIMETABLE_FRAME_INTERCHANGE_MAX_WAIT_TIME_TOO_LONG = "1-NETEXPROFILE-TimetableFrame_Interchange_MaxWaitTimeTooLong";

	public static final String _1_NETEX_COMPOSITE_FRAME_VALIDITYCONDTITIONS = "1-NETEXPROFILE-CompositeFrame_ValidityConditions";
	public static final String _1_NETEX_VALIDITYCONDITIONS_ON_FRAMES_INSIDE_COMPOSITEFRAME = "1-NETEXPROFILE-ValidityConditionsOnFramesInsideCompositeFrame";
	public static final String _1_NETEX_NO_VALIDITYCONDITIONS_ON_FRAMES_OUTSIDE_COMPOSITEFRAME = "1-NETEXPROFILE-NoValidityConditionsOnServiceOrServiceCalendarOrTimetableFrame";
	public static final String _1_NETEX_MULTIPLE_FRAMES_OF_SAME_TYPE_WITHOUT_VALIDITYCONDITIONS = "1-NETEXPROFILE-NoValidityConditionsOnFrameTypeOccuringTwiceOrMore";
	
	public static final String _1_NETEX_COMMON_TIMETABLE_FRAME = "1-NETEXPROFILE-CommonFile-TimetableFrameNotAllowed";
	public static final String _1_NETEX_COMMON_SERVICE_FRAME_LINE = "1-NETEXPROFILE-CommonFile-ServiceFrame-LineNotAllowed";
	public static final String _1_NETEX_COMMON_SERVICE_FRAME_ROUTE = "1-NETEXPROFILE-CommonFile-ServiceFrame-RouteNotAllowed";
	public static final String _1_NETEX_COMMON_SERVICE_FRAME_SERVICE_JOURNEY_PATTERN = "1-NETEXPROFILE-CommonFile-ServiceFrame-JourneyPatternNotAllowed";

	public static final String _1_NETEX_SERVICE_CALENDAR_FRAME_DAYTYPE_NOT_ASSIGNED = "1-NETEXPROFILE-ServiceCalendarFrame-DayTypeWithoutAssignment";
	public static final String _1_NETEX_SERVICE_CALENDAR_FRAME_EMPTY_SERVICE_CALENDAR = "1-NETEXPROFILE-ServiceCalendarFrame-EmptyServiceCalendar";
	public static final String _1_NETEX_SERVICE_CALENDAR_FRAME_SERVICE_CALENDAR_FROMDATE = "1-NETEXPROFILE-ServiceCalendarFrame-ServiceCalendar_FromDate";
	public static final String _1_NETEX_SERVICE_CALENDAR_FRAME_SERVICE_CALENDAR_TODATE = "1-NETEXPROFILE-ServiceCalendarFrame-ServiceCalendar_ToDate";
	public static final String _1_NETEX_SERVICE_CALENDAR_FRAME_SERVICE_CALENDAR_FROMDATE_AFTER_TODATE = "1-NETEXPROFILE-ServiceCalendarFrame-ServiceCalendar_FromDateAfterToDate";

	public static final String _1_NETEX_VEHICLE_SHCEDULE_FRAME_BLOCK = "1-NETEXPROFILE-VehicleScheduleFrame_Block";
	public static final String _1_NETEX_VEHICLE_SHCEDULE_FRAME_BLOCK_JOURNEYS = "1-NETEXPROFILE-VehicleScheduleFrame_Block_journeys";
	public static final String _1_NETEX_VEHICLE_SHCEDULE_FRAME_BLOCK_DAYTYPES = "1-NETEXPROFILE-VehicleScheduleFrame_Block_dayTypes";
	
	protected static final String ID_STRUCTURE_REGEXP = "^([A-Z]{3}):([A-Za-z]*):([0-9A-Za-z_\\-]*)$";
	public static final String _1_NETEX_VALIDBETWEEN_INCOMPLETE = "1-NETEXPROFILE-ValidBetween_Incomplete";
	public static final String _1_NETEX_VALIDBETWEEN_TODATE_BEFORE_FROMDATE = "1-NETEXPROFILE-ValidBetween_FromDateAfterToDate";
	public static final String _1_NETEX_AVAILABILITYCONDITION_TODATE_BEFORE_FROMDATE = "1-NETEXPROFILE-AvailabilityCondition_FromDateAfterToDate";
	public static final String _1_NETEX_AVAILABILITYCONDITION_INCOMPLETE = "1-NETEXPROFILE-AvailabilityCondition_Incomplete";

	@Override
	public void addObjectReference(Context context, DataManagedObjectStructure object) {
	}

	@Override
	public void initializeCheckPoints(Context context) {

		addCheckpoints(context, _1_NETEX_DUPLICATE_IDS_ACROSS_LINE_AND_COMMON_FILES, "E");
		addCheckpoints(context, _1_NETEX_DUPLICATE_IDS_ACROSS_LINE_FILES, "E");
		addCheckpoints(context, _1_NETEX_MISSING_VERSION_ON_LOCAL_ELEMENTS, "E");
		addCheckpoints(context, _1_NETEX_MISSING_REFERENCE_VERSION_TO_LOCAL_ELEMENTS, "E");
		addCheckpoints(context, _1_NETEX_UNRESOLVED_REFERENCE_TO_COMMON_ELEMENTS, "E");
		addCheckpoints(context, _1_NETEX_INVALID_ID_STRUCTURE, "E");
		addCheckpoints(context, _1_NETEX_INVALID_ID_STRUCTURE_NAME, "E");
		addCheckpoints(context, _1_NETEX_UNAPPROVED_CODESPACE_DEFINED, "E");
		addCheckpoints(context, _1_NETEX_USE_OF_UNAPPROVED_CODESPACE, "E");
		addCheckpoints(context, _1_NETEX_REFERENCE_TO_ILLEGAL_ELEMENT, "E");
		addCheckpoints(context, _1_NETEX_UNRESOLVED_EXTERNAL_REFERENCE, "E");
		
		addCheckpoints(context, _1_NETEX_VALIDBETWEEN_INCOMPLETE, "E");
		addCheckpoints(context, _1_NETEX_VALIDBETWEEN_TODATE_BEFORE_FROMDATE, "E");
		addCheckpoints(context, _1_NETEX_AVAILABILITYCONDITION_INCOMPLETE, "E");
		addCheckpoints(context, _1_NETEX_AVAILABILITYCONDITION_TODATE_BEFORE_FROMDATE, "E");
		
		
		addCheckpoints(context, _1_NETEX_RESOURCE_FRAME, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME, "E");
		addCheckpoints(context, _1_NETEX_SITE_FRAME, "W");

		addCheckpoints(context, _1_NETEX_COMPOSITE_FRAME_VALIDITYCONDTITIONS, "E");
		addCheckpoints(context, _1_NETEX_VALIDITYCONDITIONS_ON_FRAMES_INSIDE_COMPOSITEFRAME, "W");
		addCheckpoints(context, _1_NETEX_MULTIPLE_FRAMES_OF_SAME_TYPE_WITHOUT_VALIDITYCONDITIONS, "E");
		addCheckpoints(context, _1_NETEX_NO_VALIDITYCONDITIONS_ON_FRAMES_OUTSIDE_COMPOSITEFRAME, "E");
		addCheckpoints(context, _1_NETEX_CODESPACE, "E");

		addCheckpoints(context, _1_NETEX_SERVICE_FRAME, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_LINE, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_LINE_NAME, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_GROUPOFLINES_OUTSIDE_NETWORK, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_NETWORK_AUTHORITY_REF, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_ROUTEPOINT_PROJECTION, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_NETWORK_GROUPOFLINE_NAME, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_NETWORK_NAME, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_FLEXBIBLE_LINE_FLEXIBLELINETYPE, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_FLEXBIBLE_LINE_ILLEGAL_BOOKINGACCESS, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_FLEXBIBLE_LINE_ILLEGAL_BOOKINGMETHODS, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_FLEXBIBLE_LINE_ILLEGAL_BOOKWHEN, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_FLEXBIBLE_LINE_ILLEGAL_BUYWHEN, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_FLEXBIBLE_LINE_ILLEGAL_FLEXIBLELINETYPE, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_STOP_POINT_ILLEGAL_BOOKINGACCESS, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_STOP_POINT_ILLEGAL_BOOKINGMETHODS, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_STOP_POINT_ILLEGAL_BOOKWHEN, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_STOP_POINT_ILLEGAL_BUYWHEN, "E");

		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_FLEXIBLE_SERVICE_PROPERTIES_ID, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_FLEXIBLE_SERVICE_PROPERTIES_VERSION, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_FLEXIBLE_SERVICE_PROPERTIES_BOOKWHEN, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_FLEXIBLE_SERVICE_PROPERTIES_BOOKINGMETHODS, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_FLEXIBLE_SERVICE_PROPERTIES_BOOKINGCONTACT, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_FLEXIBLE_SERVICE_PROPERTIES_ILLEGAL_BOOKINGACCESS, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_FLEXIBLE_SERVICE_PROPERTIES_ILLEGAL_BOOKINGMETHODS, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_FLEXIBLE_SERVICE_PROPERTIES_ILLEGAL_BOOKWHEN, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_FLEXIBLE_SERVICE_PROPERTIES_ILLEGAL_BUYWHEN, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_FLEXIBLE_SERVICE_PROPERTIES_ILLEGAL_FLEXIBLESERVICETYPE, "E");

		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_INTERCHANGE_PLANNED_AND_ADVERTISED, "W");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_INTERCHANGE_GUARANTEED_AND_MAX_WAIT_TIME_ZERO, "W");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_INTERCHANGE_MAX_WAIT_TIME_TOO_LONG, "W");

		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_LINE_PUBLIC_CODE, "W");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_VEHICLEJOURNEY_OPERATORREF_OR_LINE_OPREATORREF, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_LINE_TRANSPORTMODE, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_LINE_TRANSPORTSUBMODE, "W");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_LINE_GROUPOFLINES_OR_NETWORK, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_ROUTE_INDIRECTION, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_ROUTE_NAME, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_ROUTE_LINEREF, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_ROUTE_DIRECTIONREF, "W");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_ROUTE_POINTSINSEQUENCE, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_ROUTE_POINTSINSEQUENCE_DUPLICATE_ORDER, "W");

		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_TIMING_POINTS, "W");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_JOURNEY_PATTERN, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_JOURNEY_PATTERN_ROUTE_REF, "E");
		addCheckpoints(context, _1_NETEX_COMMON_SERVICE_FRAME_SERVICE_JOURNEY_PATTERN_MISSING_DESTINATIONDISPLAY, "E");
		addCheckpoints(context, _1_NETEX_COMMON_SERVICE_FRAME_SERVICE_JOURNEY_PATTERN_DESTINATIONDISPLAY_ON_LAST_STOP, "W");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_DESTINATION_DISPLAY_FRONTTEXT, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_DESTINATION_DISPLAY_VIA_DESTINATIONDISPLAYREF,"E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_STOP_WITHOUT_BOARDING_OR_ALIGHTING, "W");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_STOP_WITH_REPEATING_DESTINATIONDISPLAYREF, "E");
		
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_PASSENGER_STOP_ASSIGNMENT_SCHEDULEDSTOPPOINTREF, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_PASSENGER_STOP_ASSIGNMENT_QUAYREF, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_PASSENGER_STOP_ASSIGNMENT_DUPLICATE, "W");

		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_INVALID_TRANSPORTMODE, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_INVALID_TRANSPORTSUBMODE, "E");

		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_SERVICE_LINK_FROMPOINTREF, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_SERVICE_LINK_TOPOINTREF, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_SERVICE_LINK_MISSING_POSITION_COORDINATES, "E");

		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICEJOURNEY_JOURNEYPATTERN_REF, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIMES, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_CALLS, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_FIRST_DEPARTURE, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_MISSING_DEPARTURE_OR_ARRIVAL, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_LAST_ARRIVAL, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_SAME_VALUE, "W");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_ID, "W");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_VERSION, "W");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_DAYTYPEREF, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_DAYTYPEREF_AND_DATED_SERVICE_JOURNEY, "E");

		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_MISSING_PASSING_TIME, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_TRANSPORTMODE_OVERRIDE, "W");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_DUPLICATE_WITH_DIFFERENT_VERSION, "W");

		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_DATED_SERVICE_JOURNEY_OPERATINGDAYREF, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_DATED_SERVICE_JOURNEY_SERVICEJOURNEYREF, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_DATED_SERVICE_JOURNEY_MULTIPLE_SERVICEJOURNEYREF, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_DATED_SERVICE_JOURNEY_DUPLICATE_WITH_DIFFERENT_VERSION, "W");

		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_DEAD_RUN_JOURNEYPATTERN_REF, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_DEAD_RUN_DAYTYPE_REF, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_DEAD_RUN_PASSING_TIMES, "I");

		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_COMPANY_NUMBER, "I");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_NAME, "E");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_LEGAL_NAME, "I");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CONTACT_DETAILS, "W");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CONTACT_DETAILS_URL_OR_PHONE_OR_EMAIL, "W");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CUSTOMER_SERVICE_CONTACT_DETAILS, "W");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CUSTOMER_SERVICE_CONTACT_DETAILS_URL, "W");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_COMPANY_NUMBER, "I");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_NAME, "E");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_LEGAL_NAME, "I");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_CONTACT_DETAILS, "E");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_CONTACT_DETAILS_URL, "E");

		addCheckpoints(context, _1_NETEX_SERVICE_CALENDAR_FRAME_DAYTYPE_NOT_ASSIGNED, "W");
		addCheckpoints(context, _1_NETEX_SERVICE_CALENDAR_FRAME_EMPTY_SERVICE_CALENDAR, "W");
		addCheckpoints(context, _1_NETEX_SERVICE_CALENDAR_FRAME_SERVICE_CALENDAR_FROMDATE, "W");
		addCheckpoints(context, _1_NETEX_SERVICE_CALENDAR_FRAME_SERVICE_CALENDAR_TODATE, "W");
		addCheckpoints(context, _1_NETEX_SERVICE_CALENDAR_FRAME_SERVICE_CALENDAR_FROMDATE_AFTER_TODATE, "E");
		
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME, "E");;

		addCheckpoints(context, _1_NETEX_VEHICLE_SHCEDULE_FRAME_BLOCK, "E");
		addCheckpoints(context, _1_NETEX_VEHICLE_SHCEDULE_FRAME_BLOCK_JOURNEYS, "E");
		addCheckpoints(context, _1_NETEX_VEHICLE_SHCEDULE_FRAME_BLOCK_DAYTYPES, "E");

		// Common file specific checkpoints
		addCheckpoints(context, _1_NETEX_COMMON_TIMETABLE_FRAME, "E");
		addCheckpoints(context, _1_NETEX_COMMON_SERVICE_FRAME_LINE, "E");
		addCheckpoints(context, _1_NETEX_COMMON_SERVICE_FRAME_ROUTE, "E");
		addCheckpoints(context, _1_NETEX_COMMON_SERVICE_FRAME_SERVICE_JOURNEY_PATTERN, "E");

		addCheckpoints(context, _1_NETEX_NOTICE_TEXT, "E");
		addCheckpoints(context, _1_NETEX_NOTICE_ALTERNATIVE_TEXT_TEXT, "E");
		addCheckpoints(context, _1_NETEX_NOTICE_ALTERNATIVE_TEXT_LANG, "E");
		addCheckpoints(context, _1_NETEX_NOTICE_ALTERNATIVE_TEXT_DUPLICATE_LANG, "E");

		addCheckpoints(context, _1_NETEX_NOTICE_ASSIGNMENTS_DUPLICATE, "W");
	}

	private void addCheckpoints(Context context, String checkpointName, String error) {
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validationReporter.addItemToValidationReport(context, checkpointName, error);
	}

	protected void validateResourceFrame(Context context, XPathCompiler xpath, XdmNode dom, String subLevelPath) throws XPathExpressionException, SaxonApiException {
		XdmNode subLevel = dom;
		if (subLevelPath != null) {
			subLevel = (XdmNode)selectNode(subLevelPath, xpath, dom);
		}

		if (subLevel != null) {

			validateElementNotPresent(context, xpath, subLevel, "organisations/Operator[not(CompanyNumber) or normalize-space(CompanyNumber) = '']",
					_1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_COMPANY_NUMBER);
			validateElementNotPresent(context, xpath, subLevel, "organisations/Operator[not(Name) or normalize-space(Name) = '']", _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_NAME);
			validateElementNotPresent(context, xpath, subLevel, "organisations/Operator[not(LegalName) or normalize-space(LegalName) = '']",
					_1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_LEGAL_NAME);
			validateElementNotPresent(context, xpath, subLevel, "organisations/Operator[not(ContactDetails)]",
					_1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CONTACT_DETAILS);
			validateElementNotPresent(context, xpath, subLevel, "organisations/Operator/ContactDetails[(not(Email) or normalize-space(Email) = '') and (not(Phone) or normalize-space(Phone) = '') and (not(Url) or normalize-space(Url) = '')]",
					_1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CONTACT_DETAILS_URL_OR_PHONE_OR_EMAIL);
			validateElementNotPresent(context, xpath, subLevel, "organisations/Operator[not(CustomerServiceContactDetails)]",
					_1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CUSTOMER_SERVICE_CONTACT_DETAILS);
			validateElementNotPresent(context, xpath, subLevel, "organisations/Operator/CustomerServiceContactDetails[not(Url) or normalize-space(Url) = '']", _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CUSTOMER_SERVICE_CONTACT_DETAILS_URL);
			validateElementNotPresent(context, xpath, subLevel, "organisations/Authority[not(CompanyNumber) or normalize-space(CompanyNumber) = '']",
					_1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_COMPANY_NUMBER);
			validateElementNotPresent(context, xpath, subLevel, "organisations/Authority[not(Name) or normalize-space(Name) = '']",
					_1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_NAME);
			validateElementNotPresent(context, xpath, subLevel, "organisations/Authority[not(LegalName) or normalize-space(LegalName) = '']",
					_1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_LEGAL_NAME);
			validateElementNotPresent(context, xpath, subLevel, "organisations/Authority[not(ContactDetails)]",
					_1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_CONTACT_DETAILS);
			validateElementNotPresent(context, xpath, subLevel, "organisations/Authority/ContactDetails[not(Url) or not(starts-with(Url, 'http://') or (starts-with(Url, 'https://')) )]",
					_1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_CONTACT_DETAILS_URL);
		}
	}

	protected void validateServiceCalendarFrame(Context context, XPathCompiler xpath, XdmNode dom, String subLevelPath) throws XPathExpressionException, SaxonApiException {
		XdmNode subLevel = dom;
		if (subLevelPath != null) {
			subLevel = (XdmNode) selectNode(subLevelPath, xpath, dom);
		}

		if (subLevel != null) {
			validateElementNotPresent(context, xpath, subLevel, "//DayType[not(//DayTypeAssignment/DayTypeRef/@ref = @id)]",
					_1_NETEX_SERVICE_CALENDAR_FRAME_DAYTYPE_NOT_ASSIGNED);
			validateElementNotPresent(context, xpath, subLevel, "//ServiceCalendar[not(dayTypes) and not(dayTypeAssignments)]",
					_1_NETEX_SERVICE_CALENDAR_FRAME_EMPTY_SERVICE_CALENDAR);
			validateElementNotPresent(context, xpath, subLevel, "//ServiceCalendar[not(ToDate)]",
					_1_NETEX_SERVICE_CALENDAR_FRAME_SERVICE_CALENDAR_TODATE);
			validateElementNotPresent(context, xpath, subLevel, "//ServiceCalendar[not(FromDate)]",
					_1_NETEX_SERVICE_CALENDAR_FRAME_SERVICE_CALENDAR_FROMDATE);
			validateElementNotPresent(context, xpath, subLevel, "//ServiceCalendar[FromDate and ToDate and ToDate < FromDate]",
					_1_NETEX_SERVICE_CALENDAR_FRAME_SERVICE_CALENDAR_FROMDATE_AFTER_TODATE);
		}

	}


	public Collection<String> getSupportedProfiles() {
		return Arrays.asList(new String[] { PROFILE_NORWAY_NETWORKTIMETABLE_104_10,PROFILE_NORWAY_NETWORKTIMETABLE_104_11,
				PROFILE_NORWAY_NETWORKTIMETABLE_107_11, PROFILE_NORWAY_NETWORKTIMETABLE_108_11,  PROFILE_NORWAY_NETWORKTIMETABLE_108_12,
				PROFILE_NORWAY_NETWORKTIMETABLE_108_13, PROFILE_NORWAY_NETWORKTIMETABLE_109_13, PROFILE_NORWAY_NETWORKTIMETABLE_110_13, PROFILE_NORWAY_NETWORKTIMETABLE_111_13, PROFILE_NORWAY_NETWORKTIMETABLE_112_13, PROFILE_NORWAY_NETWORKTIMETABLE_113_13});
	}

	protected void verifyReferencesToCorrectEntityTypes(Context context, List<IdVersion> localRefs) {
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();

		Map<String, Set<String>> allowedSubstitutions = new HashMap<>();

		Set<String> groupOfLinesRefSubstitutions = new HashSet<>();
		groupOfLinesRefSubstitutions.add("Network");
		groupOfLinesRefSubstitutions.add("GroupOfLines");
		allowedSubstitutions.put("RepresentedByGroupRef", groupOfLinesRefSubstitutions);

		Set<String> inverseRouteRefSubstitutions = new HashSet<>();
		inverseRouteRefSubstitutions.add("Route");
		allowedSubstitutions.put("InverseRouteRef", inverseRouteRefSubstitutions);

		Set<String> projectedPointRefSubstitutions = new HashSet<>();
		projectedPointRefSubstitutions.add("ScheduledStopPoint");
		projectedPointRefSubstitutions.add("RoutePoint");
		projectedPointRefSubstitutions.add("TimingPoint");
		allowedSubstitutions.put("ProjectToPointRef", projectedPointRefSubstitutions);
		allowedSubstitutions.put("ProjectedPointRef", projectedPointRefSubstitutions);
		allowedSubstitutions.put("ToPointRef", projectedPointRefSubstitutions);
		allowedSubstitutions.put("FromPointRef", projectedPointRefSubstitutions);
		allowedSubstitutions.put("StartPointRef", projectedPointRefSubstitutions);
		allowedSubstitutions.put("EndPointRef", projectedPointRefSubstitutions);

		Set<String> noticedObjectRefSubstitutions = new HashSet<>();
		noticedObjectRefSubstitutions.add("Line");
		noticedObjectRefSubstitutions.add("FlexibleLine");
		noticedObjectRefSubstitutions.add("ServiceJourney");
		noticedObjectRefSubstitutions.add("JourneyPattern");
		noticedObjectRefSubstitutions.add("ServiceJourneyPattern");
		noticedObjectRefSubstitutions.add("StopPointInJourneyPattern");
		noticedObjectRefSubstitutions.add("TimetabledPassingTime");
		allowedSubstitutions.put("NoticedObjectRef", noticedObjectRefSubstitutions);

		Set<String> toAndFromJourneyRefSubstitutions = new HashSet<>();
		toAndFromJourneyRefSubstitutions.add("ServiceJourney");
		toAndFromJourneyRefSubstitutions.add("DatedServiceJourney");
		allowedSubstitutions.put("ToJourneyRef", toAndFromJourneyRefSubstitutions);
		allowedSubstitutions.put("FromJourneyRef", toAndFromJourneyRefSubstitutions);

		Set<String> vehicleScheduleJourneyRefSubstitutions = new HashSet<>(toAndFromJourneyRefSubstitutions);
		vehicleScheduleJourneyRefSubstitutions.add("VehicleJourney");
		vehicleScheduleJourneyRefSubstitutions.add("DeadRun");
		allowedSubstitutions.put("VehicleJourneyRef", vehicleScheduleJourneyRefSubstitutions);

		Set<String> serviceJourneyPatternRefSubstitutions = new HashSet<>();
		serviceJourneyPatternRefSubstitutions.add("ServiceJourneyPattern");
		allowedSubstitutions.put("JourneyPatternRef", serviceJourneyPatternRefSubstitutions);

		Set<String> lineRefSubstitutions = new HashSet<>();
		lineRefSubstitutions.add("FlexibleLine");
		allowedSubstitutions.put("LineRef", lineRefSubstitutions);

		Set<String> mainPartRefSubstitutions = new HashSet<>();
		mainPartRefSubstitutions.add("JourneyPart");
		allowedSubstitutions.put("MainPartRef", mainPartRefSubstitutions);

		Set<String> fromStopPointRefSubstitutions = new HashSet<>();
		fromStopPointRefSubstitutions.add("ScheduledStopPoint");
		allowedSubstitutions.put("FromStopPointRef", fromStopPointRefSubstitutions);

		Set<String> toStopPointRefSubstitutions = new HashSet<>();
		toStopPointRefSubstitutions.add("ScheduledStopPoint");
		allowedSubstitutions.put("ToStopPointRef", toStopPointRefSubstitutions);

		boolean foundErrors = false;

		for (IdVersion id : localRefs) {
			String referencingElement = id.getElementName();

			// TODO decomposing of Ids should be in a common class
			String[] idParts = StringUtils.split(id.getId(), ":");
			if (idParts.length == 3) {
				String referencedElement = idParts[1];

				// Dumb attemt first, must be of same type
				if (!(referencedElement + "Ref").equals(referencingElement) && !("Default" + referencedElement + "Ref").equals(referencingElement)) {
					Set<String> possibleSubstitutions = allowedSubstitutions.get(referencingElement);
					if (possibleSubstitutions != null) {
						if (possibleSubstitutions.contains(referencedElement)) {
							// Allowed substitution
							continue;
						}
					}

					foundErrors = true;
					validationReporter.addCheckPointReportError(context, _1_NETEX_REFERENCE_TO_ILLEGAL_ELEMENT, null, DataLocationHelper.findDataLocation(id),
							referencedElement, referencingElement);

				}
			} else {
				foundErrors = true;
				validationReporter.addCheckPointReportError(context, _1_NETEX_INVALID_ID_STRUCTURE, null, DataLocationHelper.findDataLocation(id),
						referencingElement, referencingElement);

			}

		}
		if (!foundErrors) {
			validationReporter.reportSuccess(context, _1_NETEX_REFERENCE_TO_ILLEGAL_ELEMENT);
		}
	}

	protected void validateServiceFrameCommonElements(Context context, XPathCompiler xpath, XdmNode subLevel) throws XPathExpressionException, SaxonApiException {
		validateElementNotPresent(context, xpath, subLevel, "Network[not(AuthorityRef)]", _1_NETEX_SERVICE_FRAME_NETWORK_AUTHORITY_REF);
		validateElementNotPresent(context, xpath, subLevel, "routePoints/RoutePoint[not(projections)]", _1_NETEX_SERVICE_FRAME_ROUTEPOINT_PROJECTION);
		validateElementNotPresent(context, xpath, subLevel, "Network[not(Name) or normalize-space(Name) = '']", _1_NETEX_SERVICE_FRAME_NETWORK_NAME);
		validateElementNotPresent(context, xpath, subLevel, "Network/groupsOfLines/GroupOfLines[not(Name)  or normalize-space(Name) = '']", _1_NETEX_SERVICE_FRAME_NETWORK_GROUPOFLINE_NAME);
		validateElementNotPresent(context, xpath, subLevel, "groupsOfLines", _1_NETEX_SERVICE_FRAME_GROUPOFLINES_OUTSIDE_NETWORK);
		validateElementNotPresent(context, xpath, subLevel, "timingPoints", _1_NETEX_SERVICE_FRAME_TIMING_POINTS);

		validateElementNotPresent(context, xpath, subLevel, "stopAssignments/PassengerStopAssignment[not(ScheduledStopPointRef)]", _1_NETEX_SERVICE_FRAME_PASSENGER_STOP_ASSIGNMENT_SCHEDULEDSTOPPOINTREF);
		validateElementNotPresent(context, xpath, subLevel, "stopAssignments/PassengerStopAssignment[not(QuayRef)]", _1_NETEX_SERVICE_FRAME_PASSENGER_STOP_ASSIGNMENT_QUAYREF);
		validateElementNotPresent(context, xpath, subLevel, "stopAssignments/PassengerStopAssignment[QuayRef/@ref = following-sibling::PassengerStopAssignment/QuayRef/@ref]", _1_NETEX_SERVICE_FRAME_PASSENGER_STOP_ASSIGNMENT_DUPLICATE);


		validateElementNotPresent(context, xpath, subLevel, "serviceLinks/ServiceLink[not(FromPointRef)]", _1_NETEX_SERVICE_FRAME_SERVICE_LINK_FROMPOINTREF);
		validateElementNotPresent(context, xpath, subLevel, "serviceLinks/ServiceLink[not(ToPointRef)]", _1_NETEX_SERVICE_FRAME_SERVICE_LINK_TOPOINTREF);
		validateElementNotPresent(context, xpath, subLevel, "serviceLinks/ServiceLink/projections/LinkSequenceProjection/g:LineString/g:posList[not(normalize-space(text()))]", _1_NETEX_SERVICE_FRAME_SERVICE_LINK_MISSING_POSITION_COORDINATES);
	}

	protected void validateNotices(Context context, XPathCompiler xpath, XdmNode subLevel) throws XPathExpressionException, SaxonApiException {
		validateElementNotPresent(context, xpath, subLevel, "//notices/Notice[not(Text) or normalize-space(Text/text()) = '']", _1_NETEX_NOTICE_TEXT);
		validateElementNotPresent(context, xpath, subLevel, "//notices/Notice/alternativeTexts/AlternativeText[not(Text) or normalize-space(Text/text()) = '']",
				_1_NETEX_NOTICE_ALTERNATIVE_TEXT_TEXT);
		validateElementNotPresent(context, xpath, subLevel, "//notices/Notice/alternativeTexts/AlternativeText/Text[not(@lang)]",
				_1_NETEX_NOTICE_ALTERNATIVE_TEXT_LANG);
		validateElementNotPresent(context, xpath, subLevel, "//notices/Notice/alternativeTexts/AlternativeText[Text/@lang = following-sibling::AlternativeText/Text/@lang or Text/@lang = preceding-sibling::AlternativeText/Text/@lang]",
				_1_NETEX_NOTICE_ALTERNATIVE_TEXT_DUPLICATE_LANG);
	}

	protected void validateCommonFrameConcepts(Context context, XPathCompiler xpath, XdmNode dom) throws XPathExpressionException, SaxonApiException {
		validateElementNotPresent(context, xpath, dom, ".[not(validityConditions)]", _1_NETEX_COMPOSITE_FRAME_VALIDITYCONDTITIONS);
		validateElementNotPresent(context, xpath, dom, "frames//validityConditions", _1_NETEX_VALIDITYCONDITIONS_ON_FRAMES_INSIDE_COMPOSITEFRAME);
		validateElementPresent(context, xpath, dom, "codespaces/Codespace[Xmlns = '" + NSR_XMLNS + "' and XmlnsUrl = '" + NSR_XMLNSURL + "']",
				_1_NETEX_CODESPACE);
		validateElementNotPresent(context, xpath, dom, "//ValidBetween[not(FromDate) and not(ToDate)]", _1_NETEX_VALIDBETWEEN_INCOMPLETE);
		validateElementNotPresent(context, xpath, dom, "//ValidBetween[FromDate and ToDate and ToDate < FromDate]", _1_NETEX_VALIDBETWEEN_TODATE_BEFORE_FROMDATE);
		validateElementNotPresent(context, xpath, dom, "//AvailabilityCondition[not(FromDate) and not(ToDate)]", _1_NETEX_AVAILABILITYCONDITION_INCOMPLETE);
		validateElementNotPresent(context, xpath, dom, "//AvailabilityCondition[FromDate and ToDate and ToDate < FromDate]", _1_NETEX_AVAILABILITYCONDITION_TODATE_BEFORE_FROMDATE);

		validateNotices(context, xpath, dom);
	}

}
