package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.importer.util.DataLocationHelper;
import mobi.chouette.exchange.netexprofile.importer.util.IdVersion;
import mobi.chouette.exchange.netexprofile.importer.validation.AbstractNetexProfileValidator;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import java.util.*;

public abstract class AbstractNorwayNetexProfileValidator extends AbstractNetexProfileValidator {

	public static final String PROFILE_ID_1 = "1.04:NO-NeTEx-networktimetable:1.0";;

	public static final String NSR_XMLNSURL = "http://www.rutebanken.org/ns/nsr";
	public static final String NSR_XMLNS = "NSR";

	public static final String _1_NETEX_REFERENCE_TO_ILLEGAL_ELEMENT = "1-NETEXPROFILE-ReferenceToIllegalElement";

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
	public static final String _1_NETEX_SITE_FRAME = "1-NETEXPROFILE-SiteFrame";
	public static final String _1_NETEX_SERVICE_FRAME_GROUPOFLINES_OUTSIDE_NETWORK = "1-NETEXPROFILE-ServiceFrame-GroupOfLinesOutsideNetwork";
	public static final String _1_NETEX_SERVICE_FRAME_LINE = "1-NETEXPROFILE-ServiceFrame-Line";
	public static final String _1_NETEX_SERVICE_FRAME_NETWORK_AUTHORITY_REF = "1-NETEXPROFILE-ServiceFrame-Network-AutorityRef";
	public static final String _1_NETEX_SERVICE_FRAME_NETWORK_NAME = "1-NETEXPROFILE-ServiceFrame-Network-Name";
	public static final String _1_NETEX_SERVICE_FRAME_NETWORK_GROUPOFLINE_NAME = "1-NETEXPROFILE-ServiceFrame-Network-GroupOfLine-Name";
	public static final String _1_NETEX_SERVICE_FRAME_TIMING_POINTS = "1-NETEXPROFILE-ServiceFrame_TimingPoints";
	public static final String _1_NETEX_SERVICE_FRAME_SERVICE_JOURNEY_PATTERN = "1-NETEXPROFILE-ServiceFrame-ServiceJourneyPattern";
	public static final String _1_NETEX_SERVICE_FRAME_JOURNEY_PATTERN = "1-NETEXPROFILE-ServiceFrame_JourneyPattern";
	public static final String _1_NETEX_SERVICE_FRAME_LINE_PUBLIC_CODE = "1-NETEXPROFILE-ServiceFrame-Line-PublicCode";
	public static final String _1_NETEX_SERVICE_FRAME_LINE_OPERATOR_REF = "1-NETEXPROFILE-ServiceFrame-Line-OperatorRef";
	public static final String _1_NETEX_SERVICE_FRAME_JOURNEY_PATTERN_ROUTE_REF = "1-NETEXPROFILE-ServiceFrame_JourneyPattern_RouteRef";
	public static final String _1_NETEX_SERVICE_FRAME_JOURNEY_PATTERN_PASSENGERSTOPASSIGNMENT_QUAYREF = "1-NETEXPROFILE-ServiceFrame_JourneyPattern_PassengerStopAssignment_QuayRef";
	public static final String _1_NETEX_COMMON_SERVICE_FRAME_SERVICE_JOURNEY_PATTERN_MISSING_DESTINATIONDISPLAY = "1-NETEXPROFILE-ServiceFrame_JourneyPattern_First_StopPointInJourneyPattern_DestinationDisplayRef";
	
	public static final String _1_NETEX_SERVICE_FRAME_LINE_TRANSPORTMODE = "1-NETEXPROFILE-ServiceFrame_Line_TransportMode";
	public static final String _1_NETEX_SERVICE_FRAME_LINE_GROUPOFLINES_OR_NETWORK = "1-NETEXPROFILE-ServiceFrame_Line_GroupOfLinesNetwork";
	public static final String _1_NETEX_SERVICE_FRAME_ROUTE_INDIRECTION = "1-NETEXPROFILE-ServiceFrame_Route";
	public static final String _1_NETEX_SERVICE_FRAME_ROUTE_NAME = "1-NETEXPROFILE-ServiceFrame_Route_Name";
	public static final String _1_NETEX_SERVICE_FRAME_ROUTE_LINEREF = "1-NETEXPROFILE-ServiceFrame_Route_LineRef";
	public static final String _1_NETEX_SERVICE_FRAME_ROUTE_POINTSINSEQUENCE = "1-NETEXPROFILE-ServiceFrame_Route_PointsInSequence";
	public static final String _1_NETEX_SERVICE_FRAME_DESTINATION_DISPLAY_FRONTTEXT = "1-NETEXPROFILE-ServiceFrame-DestinationDisplay-FrontText";
	public static final String _1_NETEX_SERVICE_FRAME_STOP_WITHOUT_BOARDING_OR_ALIGHTING = "1-NETEXPROFILE-ServiceFrame-StopPointInJourneyPattern-NoBoardingNoAlighting";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY = "1-NETEXPROFILE-TimetableFrame-ServiceJourney";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_TRANSPORT_MODE = "1-NETEXPROFILE-TimetableFrame-ServiceJourney-TransportMode";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIMES = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_TimetabledPassingTime";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_CALLS = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_Calls";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_MISSING_DEPARTURE_OR_ARRIVAL = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_TimetabledPassingTime_Missing_DepartureTimeArrivalTime";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_FIRST_DEPARTURE = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_TimetabledPassingTime_DepartureTime";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_LAST_ARRIVAL = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_TimetabledPassingTime_Last_ArrivalTime";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_SAME_VALUE = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_TimetabledPassingTime_Same_Value";
	public static final String _1_NETEX_TIMETABLE_FRAME_SERVICEJOURNEY_JOURNEYPATTERN_REF = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_JourneyPatternRef";
	public static final String _1_NETEX_COMPOSITE_FRAME_VALIDITYCONDTITIONS = "1-NETEXPROFILE-CompositeFrame_ValidityConditions";
	public static final String _1_NETEX_VALIDITYCONDITIONS_ON_FRAMES_INSIDE_COMPOSITEFRAME = "1-NETEXPROFILE-ValidityConditionsOnFramesInsideCompositeFrame";
	public static final String _1_NETEX_NO_VALIDITYCONDITIONS_ON_FRAMES_OUTSIDE_COMPOSITEFRAME = "1-NETEXPROFILE-NoValidityConditionsOnServiceOrServiceCalendarOrTimetableFrame";
	public static final String _1_NETEX_MULTIPLE_FRAMES_OF_SAME_TYPE_WITHOUT_VALIDITYCONDITIONS = "1-NETEXPROFILE-NoValidityConditionsOnFrameTypeOccuringTwiceOrMore";
	public static final String _1_NETEX_COMMON_TIMETABLE_FRAME = "1-NETEXPROFILE-CommonFile-TimetableFrameNotAllowed";
	public static final String _1_NETEX_COMMON_SERVICE_FRAME_LINE = "1-NETEXPROFILE-CommonFile-ServiceFrame-LineNotAllowed";
	public static final String _1_NETEX_COMMON_SERVICE_FRAME_ROUTE = "1-NETEXPROFILE-CommonFile-ServiceFrame-RouteNotAllowed";
	public static final String _1_NETEX_COMMON_SERVICE_FRAME_SERVICE_JOURNEY_PATTERN = "1-NETEXPROFILE-CommonFile-ServiceFrame-JourneyPatternNotAllowed";

	protected static final String ID_STRUCTURE_REGEXP = "^([A-Z]{3}):([A-Za-z]*):([0-9A-Za-z_\\-]*)$";

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
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_GROUPOFLINES_OUTSIDE_NETWORK, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_NETWORK_AUTHORITY_REF, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_NETWORK_GROUPOFLINE_NAME, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_NETWORK_NAME, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_LINE_PUBLIC_CODE, "W");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_LINE_OPERATOR_REF, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_LINE_TRANSPORTMODE, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_LINE_GROUPOFLINES_OR_NETWORK, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_ROUTE_INDIRECTION, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_ROUTE_NAME, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_ROUTE_LINEREF, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_ROUTE_POINTSINSEQUENCE, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_TIMING_POINTS, "W");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_SERVICE_JOURNEY_PATTERN, "W");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_JOURNEY_PATTERN, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_JOURNEY_PATTERN_ROUTE_REF, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_JOURNEY_PATTERN_PASSENGERSTOPASSIGNMENT_QUAYREF, "W"); // Warn for now
		addCheckpoints(context, _1_NETEX_COMMON_SERVICE_FRAME_SERVICE_JOURNEY_PATTERN_MISSING_DESTINATIONDISPLAY, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_DESTINATION_DISPLAY_FRONTTEXT, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_STOP_WITHOUT_BOARDING_OR_ALIGHTING, "W");

		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_TRANSPORT_MODE, "W");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICEJOURNEY_JOURNEYPATTERN_REF, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIMES, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_CALLS, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_FIRST_DEPARTURE, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_MISSING_DEPARTURE_OR_ARRIVAL, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_LAST_ARRIVAL, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_SAME_VALUE, "W");

		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_COMPANY_NUMBER, "W");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_NAME, "E");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_LEGAL_NAME, "W");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CONTACT_DETAILS, "W");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CUSTOMER_SERVICE_CONTACT_DETAILS, "W");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_COMPANY_NUMBER, "W");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_NAME, "E");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_LEGAL_NAME, "W");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_CONTACT_DETAILS, "W");

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

	protected void verifyReferencesToCorrectEntityTypes(Context context, Set<IdVersion> localRefs) {
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
		allowedSubstitutions.put("ProjectToPointRef", projectedPointRefSubstitutions);
		allowedSubstitutions.put("ProjectedPointRef", projectedPointRefSubstitutions);
		allowedSubstitutions.put("ToPointRef", projectedPointRefSubstitutions);
		allowedSubstitutions.put("FromPointRef", projectedPointRefSubstitutions);

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

}
