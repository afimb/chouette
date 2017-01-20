package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;

import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.NetexprofileImportParameters;
import mobi.chouette.exchange.netexprofile.importer.util.DataLocationHelper;
import mobi.chouette.exchange.netexprofile.importer.util.IdVersion;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.exchange.netexprofile.importer.util.ProfileValidatorCodespace;
import mobi.chouette.exchange.netexprofile.importer.validation.AbstractNetexProfileValidator;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexProfileValidator;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexProfileValidatorFactory;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.report.ValidationReporter;

@Log4j
public class NorwayLineNetexProfileValidator extends AbstractNetexProfileValidator implements NetexProfileValidator {

	public static final String NAME = "NorwayLineNetexProfileValidator";

	public static final String PROFILE_ID_1 = "1.04:NO-NeTEx-networktimetable:1.0";

	public static final String NSR_XMLNSURL = "http://www.rutebanken.org/ns/nsr";
	public static final String NSR_XMLNS = "NSR";

	private static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CUSTOMER_SERVICE_CONTACT_DETAILS = "1-NETEXPROFILE-ResourceFrame-Organisations-Operator-CustomerServiceContactDetails";
	private static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CONTACT_DETAILS = "1-NETEXPROFILE-ResourceFrame-Organisations-Operator-ContactDetails";
	private static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_LEGAL_NAME = "1-NETEXPROFILE-ResourceFrame-Organisations-Operator-LegalName";
	private static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_NAME = "1-NETEXPROFILE-ResourceFrame-Organisations-Operator-Name";
	private static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_COMPANY_NUMBER = "1-NETEXPROFILE-ResourceFrame-Organisations-Operator-CompanyNumber";
	private static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_CONTACT_DETAILS = "1-NETEXPROFILE-ResourceFrame-Organisations-Authority-ContactDetails";
	private static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_LEGAL_NAME = "1-NETEXPROFILE-ResourceFrame-Organisations-Authority-LegalName";
	private static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_NAME = "1-NETEXPROFILE-ResourceFrame-Organisations-Authority-Name";
	private static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_COMPANY_NUMBER = "1-NETEXPROFILE-ResourceFrame-Organisations-Authority-CompanyNumber";

	private static final String _1_NETEX_RESOURCE_FRAME = "1-NETEXPROFILE-TimetableFrame";
	private static final String _1_NETEX_TIMETABLE_FRAME = "1-NETEXPROFILE-TimetableFrame";
	private static final String _1_NETEX_SERVICE_CALENDAR_FRAME = "1-NETEXPROFILE-ServiceCalendarFrame";
	private static final String _1_NETEX_SERVICE_FRAME = "1-NETEXPROFILE-ServiceFrame";
	private static final String _1_NETEX_CODESPACE = "1-NETEXPROFILE-CompositeFrame-Codespace";
	private static final String _1_NETEX_COMPOSITE_FRAME = "1-NETEXPROFILE-CompositeFrame";
	private static final String _1_NETEX_SITE_FRAME = "1-NETEXPROFILE-SiteFrame";
	private static final String _1_NETEX_SERVICE_FRAME_LINE = "1-NETEXPROFILE-ServiceFrame-Line";
	private static final String _1_NETEX_SERVICE_FRAME_NETWORK = "1-NETEXPROFILE-ServiceFrame-Network";

	private static final String _1_NETEX_SERVICE_FRAME_TIMING_POINTS = "1-NETEXPROFILE-ServiceFrame_TimingPoints";
	private static final String _1_NETEX_SERVICE_FRAME_SERVICE_JOURNEY_PATTERN = "1-NETEXPROFILE-ServiceFrame-ServiceJourneyPattern";
	private static final String _1_NETEX_SERVICE_FRAME_JOURNEY_PATTERN = "1-NETEXPROFILE-ServiceFrame_JourneyPattern";
	private static final String _1_NETEX_SERVICE_FRAME_LINE_PUBLIC_CODE = "1-NETEXPROFILE-ServiceFrame-Line-PublicCode";
	private static final String _1_NETEX_SERVICE_FRAME_JOURNEY_PATTERN_ROUTE_REF = "1-NETEXPROFILE-ServiceFrame_JourneyPattern_RouteRef";
	private static final String _1_NETEX_SERVICE_FRAME_LINE_TRANSPORTMODE = "1-NETEXPROFILE-ServiceFrame_Line_TransportMode";
	private static final String _1_NETEX_SERVICE_FRAME_ROUTE_INDIRECTION = "1-NETEXPROFILE-ServiceFrame_Route";
	private static final String _1_NETEX_SERVICE_FRAME_ROUTE_NAME = "1-NETEXPROFILE-ServiceFrame_Route_Name";
	private static final String _1_NETEX_SERVICE_FRAME_ROUTE_LINEREF = "1-NETEXPROFILE-ServiceFrame_Route_LineRef";
	private static final String _1_NETEX_SERVICE_FRAME_ROUTE_POINTSINSEQUENCE = "1-NETEXPROFILE-ServiceFrame_Route_PointsInSequence";

	private static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY = "1-NETEXPROFILE-TimetableFrame-ServiceJourney";
	private static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_TRANSPORT_MODE = "1-NETEXPROFILE-TimetableFrame-ServiceJourney-TransportMode";
	private static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIMES = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_TimetabledPassingTime";
	private static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_CALLS = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_Calls";
	private static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_FIRST_DEPARTURE = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_TimetabledPassingTime_First_DepartureTime";
	private static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_LAST_ARRIVAL = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_TimetabledPassingTime_Last_ArrivalTime";
	private static final String _1_NETEX_TIMETABLE_FRAME_SERVICEJOURNEY_JOURNEYPATTERN_REF = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_JourneyPatternRef";

	private static final String _1_NETEX_COMPOSITE_FRAME_VALIDITYCONDTITIONS = "1-NETEXPROFILE-CompositeFrame_ValidityConditions";
	private static final String _1_NETEX_VALIDITYCONDITIONS_ON_FRAMES_INSIDE_COMPOSITEFRAME = "1-NETEXPROFILE-ValidityConditionsOnFramesInsideCompositeFrame";

	private static final String _1_NETEX_NO_VALIDITYCONDITIONS_ON_FRAMES_OUTSIDE_COMPOSITEFRAME = "1-NETEXPROFILE-NoValidityConditionsOnServiceOrServiceCalendarOrTimetableFrame";
	private static final String _1_NETEX_MULTIPLE_FRAMES_OF_SAME_TYPE_WITHOUT_VALIDITYCONDITIONS = "1-NETEXPROFILE-NoValidityConditionsOnFrameTypeOccuringTwiceOrMore";
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
	}

	private void addCheckpoints(Context context, String checkpointName, String error) {
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validationReporter.addItemToValidationReport(context, checkpointName, error);
	}

	@Override
	public void validate(Context context) throws Exception {
		XPath xpath = (XPath) context.get(NETEX_LINE_DATA_XPATH);

		NetexprofileImportParameters configuration = (NetexprofileImportParameters) context.get(CONFIGURATION);
		PublicationDeliveryStructure lineDeliveryStructure = (PublicationDeliveryStructure) context.get(NETEX_LINE_DATA_JAVA);
		NetexReferential referential = (NetexReferential) context.get(NETEX_REFERENTIAL);

		Document dom = (Document) context.get(NETEX_LINE_DATA_DOM);
		@SuppressWarnings("unchecked")
		List<Document> commonDataDoms = (List<Document>) context.get(Constant.NETEX_COMMON_DATA_DOMS);

		@SuppressWarnings("unchecked")
		Set<ProfileValidatorCodespace> validCodespaces = (Set<ProfileValidatorCodespace>) context.get(NETEX_VALID_CODESPACES);
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);

		// StopRegistryIdValidator stopRegisterValidator = new StopRegistryIdValidator();

		@SuppressWarnings("unchecked")
		Map<IdVersion, List<String>> commonIds = (Map<IdVersion, List<String>>) context.get(NETEX_COMMON_FILE_IDENTIFICATORS);

		Set<IdVersion> localIds = collectEntityIdentificators(context, xpath, dom, new HashSet<>(Arrays.asList("Codespace")));
		Set<IdVersion> localRefs = collectEntityReferences(context, xpath, dom, null);

		for (IdVersion id : localIds) {
			data.getDataLocations().put(id.getId(), DataLocationHelper.findDataLocation(id));
		}

		// Null check, this is a bug if happens
		if (validCodespaces == null) {
			throw new RuntimeException("valid codespaces are empty - did you forget to include in context?");
		}

		// Add valid codespace for NSR
		validCodespaces.add(new ProfileValidatorCodespace(NorwayLineNetexProfileValidator.NSR_XMLNS, NorwayLineNetexProfileValidator.NSR_XMLNSURL));

		verifyAcceptedCodespaces(context, xpath, dom, validCodespaces);
		verifyIdStructure(context, localIds, commonIds, "^([A-Z]{3}):([A-Za-z]*):([0-9A-Za-z_\\-]*)$", validCodespaces);
		verifyNoDuplicatesWithCommonElements(context, localIds, commonIds);
		verifyNoDuplicatesAcrossLineFiles(context, localIds, new HashSet<>(Arrays.asList("ResourceFrame", "Network", "Authority", "Operator", "SiteFrame",
				"PointProjection", "RoutePoint", "StopPlace", "AvailabilityCondition")));
		verifyUseOfVersionOnLocalElements(context, localIds);
		verifyUseOfVersionOnRefsToLocalElements(context, localIds, localRefs);
		verifyReferencesToCommonElements(context, localRefs, localIds, commonIds);

		// Validate elements in common files
		for (Document commonDom : commonDataDoms) {
			NodeList resourceFrames = selectNodeSet("//n:ResourceFrame", xpath, commonDom);
			for (int i = 0; i < resourceFrames.getLength(); i++) {
				validateResourceFrame(context, xpath, resourceFrames.item(i), null);
			}

			NodeList serviceCalendarFrames = selectNodeSet("//n:ServiceCalendarFrame", xpath, commonDom);
			for (int i = 0; i < serviceCalendarFrames.getLength(); i++) {
				validateServiceCalendarFrame(context, xpath, serviceCalendarFrames.item(i), null);
			}
		}

		NodeList compositeFrames = selectNodeSet("/n:PublicationDelivery/n:dataObjects/n:CompositeFrame", xpath, dom);
		if (compositeFrames.getLength() > 0) {
			// Using composite frames
			for (int i = 0; i < compositeFrames.getLength(); i++) {
				validateCompositeFrame(context, xpath, compositeFrames.item(i));
			}
		} else {
			// Not using composite frames
			validateWithoutCompositeFrame(context, xpath, dom);

		}
		return;
	}

	protected void validateWithoutCompositeFrame(Context context, XPath xpath, Document dom) throws XPathExpressionException {
		// Validate that we have exactly one ResourceFrame
		validateElementPresent(context, xpath, dom, "/n:PublicationDelivery/n:dataObjects/n:ResourceFrame", _1_NETEX_RESOURCE_FRAME);
		validateResourceFrame(context, xpath, dom, "/n:PublicationDelivery/n:dataObjects/n:ResourceFrame");

		// Validate at least 1 ServiceFrame is present
		validateAtLeastElementPresent(context, xpath, dom, "/n:PublicationDelivery/n:dataObjects/n:ServiceFrame", 1, _1_NETEX_SERVICE_FRAME);
		NodeList serviceFrames = selectNodeSet("/n:PublicationDelivery/n:dataObjects/n:ServiceFrame", xpath, dom);
		for (int i = 0; i < serviceFrames.getLength(); i++) {
			validateServiceFrame(context, xpath, serviceFrames.item(i), null);
		}

		// Validate at least 1 TimetableFrame is present
		validateAtLeastElementPresent(context, xpath, dom, "/n:PublicationDelivery/n:dataObjects/n:TimetableFrame", 1, _1_NETEX_TIMETABLE_FRAME);
		NodeList timetableFrames = selectNodeSet("/n:PublicationDelivery/n:dataObjects/n:TimetableFrame", xpath, dom);
		for (int i = 0; i < timetableFrames.getLength(); i++) {
			validateTimetableFrame(context, xpath, timetableFrames.item(i), null);
		}

		// No siteframe allowed
		validateElementNotPresent(context, xpath, dom, "/n:PublicationDelivery/n:dataObjects/n:SiteFrame", _1_NETEX_SITE_FRAME);

		// Validate that at least one frame has validityConditions
		validateAtLeastElementPresent(context, xpath, dom,
				"/n:PublicationDelivery/n:dataObjects/n:ServiceFrame[n:validityConditions] | /n:PublicationDelivery/n:dataObjects/n:TimetableFrame[n:validityConditions] | /n:PublicationDelivery/n:dataObjects/n:ServiceCalendarFrame[n:validityConditions] ",
				1, _1_NETEX_NO_VALIDITYCONDITIONS_ON_FRAMES_OUTSIDE_COMPOSITEFRAME);

		// If more than one of a kind, all must have validity conditions
		validateElementNotPresent(context, xpath, dom, "//n:ServiceCalendarFrame[not(n:validityConditions) and count(//n:ServiceCalendarFrame) > 1]",
				_1_NETEX_MULTIPLE_FRAMES_OF_SAME_TYPE_WITHOUT_VALIDITYCONDITIONS);
		validateElementNotPresent(context, xpath, dom, "//n:ServiceFrame[not(n:validityConditions) and count(//n:ServiceFrame) > 1]",
				_1_NETEX_MULTIPLE_FRAMES_OF_SAME_TYPE_WITHOUT_VALIDITYCONDITIONS);
		validateElementNotPresent(context, xpath, dom, "//n:TimetableFrame[not(n:validityConditions) and count(//n:TimetableFrame) > 1]",
				_1_NETEX_MULTIPLE_FRAMES_OF_SAME_TYPE_WITHOUT_VALIDITYCONDITIONS);

	}

	private void validateCompositeFrame(Context context, XPath xpath, Node dom) throws XPathExpressionException {
		// Check that there are no overriding AvailabilityCondition which is identical to the one defined in the CompositeFrame
		validateElementPresent(context, xpath, dom, "n:validityConditions", _1_NETEX_COMPOSITE_FRAME_VALIDITYCONDTITIONS);
		validateElementNotPresent(context, xpath, dom, "n:frames//n:validityConditions", _1_NETEX_VALIDITYCONDITIONS_ON_FRAMES_INSIDE_COMPOSITEFRAME);

		validateElementPresent(context, xpath, dom, "n:codespaces/n:Codespace[n:Xmlns = '" + NSR_XMLNS + "' and n:XmlnsUrl = '" + NSR_XMLNSURL + "']",
				_1_NETEX_CODESPACE);

		Node resourceFrame = selectNode("n:frames/n:ResourceFrame", xpath, dom);
		if (resourceFrame != null) {
			validateResourceFrame(context, xpath, dom, null);
		}

		validateElementPresent(context, xpath, dom, "n:frames/n:ServiceFrame", _1_NETEX_SERVICE_FRAME);
		validateServiceFrame(context, xpath, dom, "n:frames/n:ServiceFrame");

		validateElementPresent(context, xpath, dom, "n:frames/n:TimetableFrame", _1_NETEX_TIMETABLE_FRAME);
		validateTimetableFrame(context, xpath, dom, "n:frames/n:TimetableFrame");

		Node serviceCalendarFrame = selectNode("n:frames/n:ServiceCalendarFrame", xpath, dom);
		if (serviceCalendarFrame != null) {
			validateServiceCalendarFrame(context, xpath, dom, null);
		}

		validateElementNotPresent(context, xpath, dom, "n:frames/n:SiteFrame", _1_NETEX_SITE_FRAME);

		// validateElementNotPresent(context, xpath, dom, "//n:SiteFrame/n:stopPlaces/n:StopPlace", "1", "Should not contain StopPlaces",
		// _2_NETEX_SITEFRAME_STOPPLACE);
		// validateExternalReferenceCorrect(context, xpath, dom, "//n:StopPlaceRef/@ref", stopRegisterValidator, _2_NETEX_STOPPLACE_REF);
	}

	private void validateResourceFrame(Context context, XPath xpath, Node dom, String subLevelPath) throws XPathExpressionException {
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

	private void validateServiceFrame(Context context, XPath xpath, Node dom, String subLevelPath) throws XPathExpressionException {
		Node subLevel = dom;
		if (subLevelPath != null) {
			subLevel = selectNode(subLevelPath, xpath, dom);
		}

		if (subLevel != null) {

			validateElementPresent(context, xpath, subLevel, "n:Network", _1_NETEX_SERVICE_FRAME_NETWORK);
			validateElementPresent(context, xpath, subLevel, "n:lines/n:Line", _1_NETEX_SERVICE_FRAME_LINE);
			validateElementNotPresent(context, xpath, subLevel, "n:lines/n:Line[not(n:PublicCode)]", _1_NETEX_SERVICE_FRAME_LINE_PUBLIC_CODE);
			validateElementNotPresent(context, xpath, subLevel, "n:lines/n:Line[not(n:TransportMode)]", _1_NETEX_SERVICE_FRAME_LINE_TRANSPORTMODE);
			validateElementNotPresent(context, xpath, subLevel, "n:lines/n:Line/n:routes/n:Route", _1_NETEX_SERVICE_FRAME_ROUTE_INDIRECTION);

			validateAtLeastElementPresent(context, xpath, subLevel, "n:routes/n:Route", 1, _1_NETEX_SERVICE_FRAME_ROUTE_INDIRECTION);
			validateElementNotPresent(context, xpath, subLevel, "n:routes/n:Route[not(n:Name)]", _1_NETEX_SERVICE_FRAME_ROUTE_NAME);
			validateElementNotPresent(context, xpath, subLevel, "n:routes/n:Route[not(n:LineRef)]", _1_NETEX_SERVICE_FRAME_ROUTE_LINEREF);
			validateElementNotPresent(context, xpath, subLevel, "n:routes/n:Route[not(n:pointsInSequence)]", _1_NETEX_SERVICE_FRAME_ROUTE_POINTSINSEQUENCE);

			validateElementNotPresent(context, xpath, subLevel, "n:journeyPatterns/n:ServiceJourneyPattern", _1_NETEX_SERVICE_FRAME_SERVICE_JOURNEY_PATTERN);
			validateAtLeastElementPresent(context, xpath, subLevel, "n:journeyPatterns/n:JourneyPattern | n:journeyPatterns/n:ServiceJourneyPattern", 1,
					_1_NETEX_SERVICE_FRAME_JOURNEY_PATTERN);
			validateElementNotPresent(context, xpath, subLevel,
					"n:journeyPatterns/n:ServiceJourneyPattern[not(n:RouteRef)] | n:journeyPatterns/n:JourneyPattern[not(n:RouteRef)]",
					_1_NETEX_SERVICE_FRAME_JOURNEY_PATTERN_ROUTE_REF);

			validateElementNotPresent(context, xpath, subLevel, "n:timingPoints", _1_NETEX_SERVICE_FRAME_TIMING_POINTS);
		}
	}

	private void validateTimetableFrame(Context context, XPath xpath, Node dom, String subLevelPath) throws XPathExpressionException {
		Node subLevel = dom;
		if (subLevelPath != null) {
			subLevel = selectNode(subLevelPath, xpath, dom);
		}

		if (subLevel != null) {

			validateAtLeastElementPresent(context, xpath, subLevel, "n:vehicleJourneys/n:ServiceJourney", 1, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY);
			validateElementNotPresent(context, xpath, subLevel, "n:vehicleJourneys/n:ServiceJourney[count(n:TransportMode) = 1]",
					_1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_TRANSPORT_MODE);
			validateElementNotPresent(context, xpath, subLevel, "n:vehicleJourneys/n:ServiceJourney[count(n:passingTimes/n:TimetabledPassingTime) < 2]",
					_1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIMES);
			validateElementNotPresent(context, xpath, subLevel, "n:vehicleJourneys/n:ServiceJourney/n:calls", _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_CALLS);
			validateElementNotPresent(context, xpath, subLevel,
					"n:vehicleJourneys/n:ServiceJourney[count(n:passingTimes/n:TimetabledPassingTime[1]/n:DepartureTime) = 0]",
					_1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_FIRST_DEPARTURE);
			validateElementNotPresent(context, xpath, subLevel,
					"n:vehicleJourneys/n:ServiceJourney[count(n:passingTimes/n:TimetabledPassingTime[last()]/n:ArrivalTime) = 0]",
					_1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_LAST_ARRIVAL);

			validateElementNotPresent(context, xpath, subLevel, "n:vehicleJourneys/n:ServiceJourney[not(n:JourneyPatternRef)]",
					_1_NETEX_TIMETABLE_FRAME_SERVICEJOURNEY_JOURNEYPATTERN_REF);

		}
	}

	private void validateServiceCalendarFrame(Context context, XPath xpath, Node dom, String subLevelPath) throws XPathExpressionException {
		Node subLevel = dom;
		if (subLevelPath != null) {
			subLevel = selectNode(subLevelPath, xpath, dom);
		}

		if (subLevel != null) {
			// TODO add validation logic for service calendar frames

		}

	}

	public static class DefaultValidatorFactory extends NetexProfileValidatorFactory {
		@Override
		protected NetexProfileValidator create(Context context) {
			NetexProfileValidator instance = (NetexProfileValidator) context.get(NAME);
			if (instance == null) {
				instance = new NorwayLineNetexProfileValidator();
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
	public Collection<String> getSupportedProfiles() {
		return Arrays.asList(new String[] { PROFILE_ID_1 });
	}

}
