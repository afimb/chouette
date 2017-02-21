package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import java.util.Arrays;
import java.util.Collection;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;

import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.w3c.dom.Node;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.importer.validation.AbstractNetexProfileValidator;
import mobi.chouette.exchange.validation.report.ValidationReporter;

public abstract class AbstractNorwayNetexProfileValidator extends AbstractNetexProfileValidator {

	public static final String PROFILE_ID_1 = "1.04:NO-NeTEx-networktimetable:1.0";;

	public static final String NSR_XMLNSURL = "http://www.rutebanken.org/ns/nsr";
	public static final String NSR_XMLNS = "NSR";
	public static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CUSTOMER_SERVICE_CONTACT_DETAILS = "1-NETEXPROFILE-ResourceFrame-Organisations-Operator-CustomerServiceContactDetails";
	public static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CONTACT_DETAILS = "1-NETEXPROFILE-ResourceFrame-Organisations-Operator-ContactDetails";
	public static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_LEGAL_NAME = "1-NETEXPROFILE-ResourceFrame-Organisations-Operator-LegalName";
	public static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_NAME = "1-NETEXPROFILE-ResourceFrame-Organisations-Operator-Name";
	public static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_COMPANY_NUMBER = "1-NETEXPROFILE-ResourceFrame-Organisations-Operator-CompanyNumber";
	public static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_CONTACT_DETAILS = "1-NETEXPROFILE-ResourceFrame-Organisations-Authority-ContactDetails";
	public static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_LEGAL_NAME = "1-NETEXPROFILE-ResourceFrame-Organisations-Authority-LegalName";
	public static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_NAME = "1-NETEXPROFILE-ResourceFrame-Organisations-Authority-Name";
	public static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_COMPANY_NUMBER = "1-NETEXPROFILE-ResourceFrame-Organisations-Authority-CompanyNumber";
	public static final String _1_NETEX_RESOURCE_FRAME = "1-NETEXPROFILE-TimetableFrame";
	public static final String _1_NETEX_TIMETABLE_FRAME = "1-NETEXPROFILE-TimetableFrame";
	public static final String _1_NETEX_SERVICE_CALENDAR_FRAME = "1-NETEXPROFILE-ServiceCalendarFrame";
	public static final String _1_NETEX_SERVICE_FRAME = "1-NETEXPROFILE-ServiceFrame";
	public static final String _1_NETEX_CODESPACE = "1-NETEXPROFILE-CompositeFrame-Codespace";
	public static final String _1_NETEX_COMPOSITE_FRAME = "1-NETEXPROFILE-CompositeFrame";
	public static final String _1_NETEX_SITE_FRAME = "1-NETEXPROFILE-SiteFrame";
	public static final String _1_NETEX_SERVICE_FRAME_LINE = "1-NETEXPROFILE-ServiceFrame-Line";
	public static final String _1_NETEX_SERVICE_FRAME_NETWORK = "1-NETEXPROFILE-ServiceFrame-Network";
	public static final String _1_NETEX_SERVICE_FRAME_TIMING_POINTS = "1-NETEXPROFILE-ServiceFrame_TimingPoints";
	public static final String _1_NETEX_SERVICE_FRAME_SERVICE_JOURNEY_PATTERN = "1-NETEXPROFILE-ServiceFrame-ServiceJourneyPattern";
	public static final String _1_NETEX_SERVICE_FRAME_JOURNEY_PATTERN = "1-NETEXPROFILE-ServiceFrame_JourneyPattern";
	public static final String _1_NETEX_SERVICE_FRAME_LINE_PUBLIC_CODE = "1-NETEXPROFILE-ServiceFrame-Line-PublicCode";
	public static final String _1_NETEX_SERVICE_FRAME_JOURNEY_PATTERN_ROUTE_REF = "1-NETEXPROFILE-ServiceFrame_JourneyPattern_RouteRef";
	public static final String _1_NETEX_SERVICE_FRAME_LINE_TRANSPORTMODE = "1-NETEXPROFILE-ServiceFrame_Line_TransportMode";
	public static final String _1_NETEX_SERVICE_FRAME_ROUTE_INDIRECTION = "1-NETEXPROFILE-ServiceFrame_Route";
	public static final String _1_NETEX_SERVICE_FRAME_ROUTE_NAME = "1-NETEXPROFILE-ServiceFrame_Route_Name";
	public static final String _1_NETEX_SERVICE_FRAME_ROUTE_LINEREF = "1-NETEXPROFILE-ServiceFrame_Route_LineRef";
	public static final String _1_NETEX_SERVICE_FRAME_ROUTE_POINTSINSEQUENCE = "1-NETEXPROFILE-ServiceFrame_Route_PointsInSequence";
	public static final String _1_NETEX_SERVICE_FRAME_DESTINATION_DISPLAY_FRONTTEXT = "1-NETEXPROFILE-ServiceFrame-DestinationDisplay-FrontText";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY = "1-NETEXPROFILE-TimetableFrame-ServiceJourney";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_TRANSPORT_MODE = "1-NETEXPROFILE-TimetableFrame-ServiceJourney-TransportMode";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIMES = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_TimetabledPassingTime";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_CALLS = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_Calls";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_FIRST_DEPARTURE = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_TimetabledPassingTime_First_DepartureTime";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_LAST_ARRIVAL = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_TimetabledPassingTime_Last_ArrivalTime";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICEJOURNEY_JOURNEYPATTERN_REF = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_JourneyPatternRef";
	public static final String _1_NETEX_COMPOSITE_FRAME_VALIDITYCONDTITIONS = "1-NETEXPROFILE-CompositeFrame_ValidityConditions";
	public static final String _1_NETEX_VALIDITYCONDITIONS_ON_FRAMES_INSIDE_COMPOSITEFRAME = "1-NETEXPROFILE-ValidityConditionsOnFramesInsideCompositeFrame";
	public static final String _1_NETEX_NO_VALIDITYCONDITIONS_ON_FRAMES_OUTSIDE_COMPOSITEFRAME = "1-NETEXPROFILE-NoValidityConditionsOnServiceOrServiceCalendarOrTimetableFrame";
	public static final String _1_NETEX_MULTIPLE_FRAMES_OF_SAME_TYPE_WITHOUT_VALIDITYCONDITIONS = "1-NETEXPROFILE-NoValidityConditionsOnFrameTypeOccuringTwiceOrMore";
	public static final String _1_NETEX_COMMON_TIMETABLE_FRAME = "1-NETEXPROFILE-CommonFile-TimetableFrameNotAllowed";
	public static final String _1_NETEX_COMMON_SERVICE_FRAME_LINE = "1-NETEXPROFILE-CommonFile-ServiceFrame-LineNotAllowed";
	public static final String _1_NETEX_COMMON_SERVICE_FRAME_ROUTE = "1-NETEXPROFILE-CommonFile-ServiceFrame-RouteNotAllowed";
	public static final String _1_NETEX_COMMON_SERVICE_FRAME_SERVICE_JOURNEY_PATTERN = "1-NETEXPROFILE-CommonFile-ServiceFrame-JourneyPatternNotAllowed";

	
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
		addCheckpoints(context, _1_NETEX_UNAPPROVED_CODESPACE_DEFINED, "E");
		addCheckpoints(context, _1_NETEX_USE_OF_UNAPPROVED_CODESPACE, "E");

		addCheckpoints(context, _1_NETEX_COMPOSITE_FRAME, "E");
		addCheckpoints(context, _1_NETEX_RESOURCE_FRAME, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME, "E");
		addCheckpoints(context, _1_NETEX_SITE_FRAME, "W");

		addCheckpoints(context, _1_NETEX_COMPOSITE_FRAME_VALIDITYCONDTITIONS, "E");
		addCheckpoints(context, _1_NETEX_VALIDITYCONDITIONS_ON_FRAMES_INSIDE_COMPOSITEFRAME, "E");
		addCheckpoints(context, _1_NETEX_MULTIPLE_FRAMES_OF_SAME_TYPE_WITHOUT_VALIDITYCONDITIONS, "E");
		
		addCheckpoints(context, _1_NETEX_CODESPACE, "E");

		// addCheckpoints(context, _1_NETEX_SERVICE_CALENDAR_FRAME, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_LINE, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_NETWORK, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_LINE_PUBLIC_CODE, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_LINE_TRANSPORTMODE, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_ROUTE_INDIRECTION, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_ROUTE_NAME, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_ROUTE_LINEREF, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_ROUTE_POINTSINSEQUENCE, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_TIMING_POINTS, "W");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_SERVICE_JOURNEY_PATTERN, "W");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_JOURNEY_PATTERN, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_JOURNEY_PATTERN_ROUTE_REF, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_DESTINATION_DISPLAY_FRONTTEXT, "E");

		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_TRANSPORT_MODE, "W");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICEJOURNEY_JOURNEYPATTERN_REF, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIMES, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_CALLS, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_FIRST_DEPARTURE, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_LAST_ARRIVAL, "E");

		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_COMPANY_NUMBER, "W");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_NAME, "E");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_LEGAL_NAME, "W");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CONTACT_DETAILS, "E");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CUSTOMER_SERVICE_CONTACT_DETAILS, "E");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_COMPANY_NUMBER, "W");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_NAME, "E");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_LEGAL_NAME, "W");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_CONTACT_DETAILS, "E");

		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME, "E");
		
		// Common file specific checkpoints
		addCheckpoints(context, _1_NETEX_COMMON_TIMETABLE_FRAME, "E");
		addCheckpoints(context, _1_NETEX_COMMON_SERVICE_FRAME_LINE, "E");
		addCheckpoints(context, _1_NETEX_COMMON_SERVICE_FRAME_ROUTE, "E");
		addCheckpoints(context, _1_NETEX_COMMON_SERVICE_FRAME_SERVICE_JOURNEY_PATTERN, "E");
		
	}

	private void addCheckpoints(Context context, String checkpointName, String error) {
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validationReporter.addItemToValidationReport(context, checkpointName, error);
	}

	
	protected void validateResourceFrame(Context context, XPath xpath, Node dom, String subLevelPath) throws XPathExpressionException {
		Node subLevel = dom;
		if (subLevelPath != null) {
			subLevel = selectNode(subLevelPath, xpath, dom);
		}
	
		if (subLevel != null) {
			Node organisations = selectNode("n:organisations", xpath, subLevel);
			if (organisations != null && organisations.hasChildNodes()) {
	
				validateElementNotPresent(context, xpath, organisations, "n:Operator[not(n:CompanyNumber)]",
						_1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_COMPANY_NUMBER);
				validateElementNotPresent(context, xpath, organisations, "n:Operator[not(n:Name)]", _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_NAME);
				validateElementNotPresent(context, xpath, organisations, "n:Operator[not(n:LegalName)]",
						_1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_LEGAL_NAME);
				validateElementNotPresent(context, xpath, organisations, "n:Operator[not(n:ContactDetails)]",
						_1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CONTACT_DETAILS);
				validateElementNotPresent(context, xpath, organisations, "n:Operator[not(n:CustomerServiceContactDetails)]",
						_1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CUSTOMER_SERVICE_CONTACT_DETAILS);
	
				validateElementNotPresent(context, xpath, organisations, "n:Authority[not(n:CompanyNumber)]",
						_1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_COMPANY_NUMBER);
				validateElementNotPresent(context, xpath, organisations, "n:Authority[not(n:Name)]",
						_1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_NAME);
				validateElementNotPresent(context, xpath, organisations, "n:Authority[not(n:LegalName)]",
						_1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_LEGAL_NAME);
				validateElementNotPresent(context, xpath, organisations, "n:Authority[not(n:ContactDetails)]",
						_1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_CONTACT_DETAILS);
			}
		}
	}

	protected void validateServiceCalendarFrame(Context context, XPath xpath, Node dom, String subLevelPath) throws XPathExpressionException {
		Node subLevel = dom;
		if (subLevelPath != null) {
			subLevel = selectNode(subLevelPath, xpath, dom);
		}
	
		if (subLevel != null) {
			// TODO add validation logic for service calendar frames
	
		}
	
	}

	public Collection<String> getSupportedProfiles() {
		return Arrays.asList(new String[] { PROFILE_ID_1 });
	}

	

}