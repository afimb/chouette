package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;

import org.rutebanken.netex.model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.importer.NetexprofileImportParameters;
import mobi.chouette.exchange.netexprofile.importer.util.IdVersion;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.exchange.netexprofile.importer.util.ProfileValidatorCodespace;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexProfileValidator;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexProfileValidatorFactory;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;

@Log4j
public class NorwayLineNetexProfileValidator extends AbstractValidator implements NetexProfileValidator {

	
	
	public static final String PROFILE_ID_1 = "1.04:NO-NeTEx-networktimetable:1.0";

	public static final String NSR_XMLNSURL = "http://www.rutebanken.org/ns/nsr";
	public static final String NSR_XMLNS = "NSR";

	private static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CUSTOMER_SERVICE_CONTACT_DETAILS = "1-NETEXPROFILE-ResourceFrame-Organisations-Operator-CustomerServiceContactDetails";
	private static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CONTACT_DETAILS = "1-NETEXPROFILE-ResourceFrame-Organisations-Operator-ContactDetails";
	private static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_LEGAL_NAME = "1-NETEXPROFILE-ResourceFrame-Organisations-Operator-LegalName";
	private static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_NAME = "1-NETEXPROFILE-ResourceFrame-Organisations-Operator-Name";
	private static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_COMPANY_NUMBER = "1-NETEXPROFILE-ResourceFrame-Organisations-Operator-CompanyNumber";
	private static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR = "1-NETEXPROFILE-ResourceFrame-Organisations-Operator";
	private static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY = "1-NETEXPROFILE-ResourceFrame-Organisations-Authority";
	private static final String _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS = "1-NETEXPROFILE-ResourceFrame-Organisations";

	private static final String _1_NETEX_TIMETABLE_FRAME = "1-NETEXPROFILE-TimetableFrame";
	private static final String _1_NETEX_SERVICE_CALENDAR_FRAME = "1-NETEXPROFILE-ServiceCalendarFrame";
	private static final String _1_NETEX_SERVICE_FRAME = "1-NETEXPROFILE-ServiceFrame";
	private static final String _1_NETEX_RESOURCE_FRAME = "1-NETEXPROFILE-ResourceFrame";
	private static final String _1_NETEX_CODESPACE = "1-NETEXPROFILE-CompositeFrame_Codespace";
	private static final String _1_NETEX_COMPOSITE_FRAME = "1-NETEXPROFILE-CompositeFrame";
	private static final String _1_NETEX_SITE_FRAME = "1-NETEXPROFILE-SiteFrame";
	private static final String _1_NETEX_SERVICE_FRAME_LINE = "1-NETEXPROFILE-ServiceFrame_Line";
	private static final String _1_NETEX_SERVICE_FRAME_NETWORK = "1-NETEXPROFILE-ServiceFrame_Network";

	private static final String _1_NETEX_SERVICE_FRAME_TIMING_POINTS = "1-NETEXPROFILE-ServiceFrame_TimingPoints";
	private static final String _1_NETEX_SERVICE_FRAME_SERVICE_JOURNEY_PATTERN = "1-NETEXPROFILE-ServiceFrame_ServiceJourneyPattern";
	private static final String _1_NETEX_SERVICE_FRAME_JOURNEY_PATTERN = "1-NETEXPROFILE-ServiceFrame_JourneyPattern";
	private static final String _1_NETEX_SERVICE_FRAME_JOURNEY_PATTERN_STOPPOINT_IN_JOURNEY_PATTERN = "1-NETEXPROFILE-ServiceFrame_JourneyPattern_StopPointInJourneyPattern";
	private static final String _1_NETEX_SERVICE_FRAME_LINE_PUBLIC_CODE = "1-NETEXPROFILE-ServiceFrame_Line_PublicCode";
	private static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY = "1-NETEXPROFILE-TimetableFrame_ServiceJourney";
	private static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_TRANSPORT_MODE = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_TransportMode";

	public static final String LOCAL_CONTEXT = "NetexPublicationDelivery";
	public static final String NAME = "NorwayLineNetexProfileValidator";
	public static final String PREFIX = "1-NETEXPROFILE-";

	private static final String FRAME_1 = "1-NETEXPROFILE-Frame-1";
	private static final String FRAME_2 = "1-NETEXPROFILE-Frame-2";
	private static final String FRAME_3 = "1-NETEXPROFILE-Frame-3";
	private static final String FRAME_4 = "1-NETEXPROFILE-Frame-4";
	private static final String FRAME_5 = "1-NETEXPROFILE-Frame-5";
	private static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIMES = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_TimetabledPassingTime";
	private static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_CALLS = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_Calls";
	private static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_FIRST_DEPARTURE = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_TimetabledPassingTime_First_DepartureTime";
	private static final String _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_LAST_ARRIVAL = "1-NETEXPROFILE-TimetableFrame_ServiceJourney_TimetabledPassingTime_Last_ArrivalTime";

	@Override
	public void addObjectReference(Context context, DataManagedObjectStructure object) {
	}

	@Override
	public void initializeCheckPoints(Context context) {
		// addItemToValidation(context, PREFIX, "Frame", 5, "E", "E", "E", "E", "E");

		addCheckpoints(context, _1_NETEX_DUPLICATE_IDS, "E");
		addCheckpoints(context, _1_NETEX_MISSING_VERSION_ON_LOCAL_ELEMENTS, "E");
		addCheckpoints(context, _1_NETEX_MISSING_REFERENCE_VERSION_TO_LOCAL_ELEMENTS, "E");
		addCheckpoints(context, _1_NETEX_UNRESOLVED_REFERENCE_TO_COMMON_ELEMENTS, "E");
		addCheckpoints(context, _1_NETEX_INVALID_ID_STRUCTURE, "E");
		addCheckpoints(context, _1_NETEX_UNAPPROVED_CODESPACE_DEFINED, "E");
		addCheckpoints(context, _1_NETEX_USE_OF_UNAPPROVED_CODESPACE, "E");

		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME, "E");

		addCheckpoints(context, _1_NETEX_SERVICE_CALENDAR_FRAME, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_LINE, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_NETWORK, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_LINE_PUBLIC_CODE, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_TIMING_POINTS, "W");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_SERVICE_JOURNEY_PATTERN, "W");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_JOURNEY_PATTERN, "E");
		addCheckpoints(context, _1_NETEX_SERVICE_FRAME_JOURNEY_PATTERN_STOPPOINT_IN_JOURNEY_PATTERN, "E");

		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_TRANSPORT_MODE, "W");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIMES, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_CALLS, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_FIRST_DEPARTURE, "E");
		addCheckpoints(context, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_LAST_ARRIVAL, "E");

		addCheckpoints(context, _1_NETEX_RESOURCE_FRAME, "E");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS, "E");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY, "E");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR, "E");

		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_COMPANY_NUMBER, "W");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_NAME, "E");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_LEGAL_NAME, "W");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CONTACT_DETAILS, "E");
		addCheckpoints(context, _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CUSTOMER_SERVICE_CONTACT_DETAILS, "E");

		addCheckpoints(context, _1_NETEX_COMPOSITE_FRAME, "E");
		addCheckpoints(context, _1_NETEX_SITE_FRAME, "W");
		addCheckpoints(context, _1_NETEX_CODESPACE, "E");
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
		Document dom = (Document) context.get(NETEX_LINE_DATA_DOM);
		NetexReferential referential = (NetexReferential) context.get(NETEX_REFERENTIAL);
		Set<ProfileValidatorCodespace> validCodespaces = (Set<ProfileValidatorCodespace>) context.get(NETEX_VALID_CODESPACES);

		// StopRegistryIdValidator stopRegisterValidator = new StopRegistryIdValidator();

		@SuppressWarnings("unchecked")
		Map<IdVersion, List<String>> commonIds = (Map<IdVersion, List<String>>) context.get(NETEX_COMMON_FILE_IDENTIFICATORS);

		Set<IdVersion> localIds = collectEntityIdentificators(context, xpath, dom);
		Set<IdVersion> localRefs = collectEntityReferences(context, xpath, dom);

		if(validCodespaces == null) {
			throw new RuntimeException("valid codespaces are empty - did you forget to include in context?");
		}

		// Add valid codespace for NSR
		validCodespaces.add(new ProfileValidatorCodespace(NorwayLineNetexProfileValidator.NSR_XMLNS, NorwayLineNetexProfileValidator.NSR_XMLNSURL));

		
		verifyAcceptedCodespaces(context, xpath, dom, validCodespaces);
		verifyIdStructure(context, localIds, commonIds, "^([A-Z]{3}):([A-Za-z]*):([0-9A-Za-z_\\-]*)$",validCodespaces);
		verifyNoDuplicatesWithCommonElements(context, localIds, commonIds);
		verifyUseOfVersionOnLocalElements(context, localIds);
		verifyUseOfVersionOnRefsToLocalElements(context, localIds, localRefs);
		verifyReferencesToCommonElements(context, localRefs, localIds, commonIds);

		validateElementPresent(context, xpath, dom, "/n:PublicationDelivery/n:dataObjects/n:CompositeFrame", "1", "No CompositeFrame",
				_1_NETEX_COMPOSITE_FRAME);
		validateElementPresent(context, xpath, dom, "/n:PublicationDelivery/n:dataObjects/n:CompositeFrame/n:codespaces/n:Codespace[n:Xmlns = '"
				+ NSR_XMLNS + "' and n:XmlnsUrl = '" + NSR_XMLNSURL + "']", "1", "NSR codespace missing", _1_NETEX_CODESPACE);
		validateElementPresent(context, xpath, dom, "/n:PublicationDelivery/n:dataObjects/n:CompositeFrame/n:frames/n:ResourceFrame", "1", "No ResourceFrame",
				_1_NETEX_RESOURCE_FRAME);
		validateElementPresent(context, xpath, dom, "/n:PublicationDelivery/n:dataObjects/n:CompositeFrame/n:frames/n:ServiceFrame", "1", "No ServiceFrame",
				_1_NETEX_SERVICE_FRAME);
		// TODO service calendar frame may be defined in common files
		// validateElementPresent(context, xpath, dom, "/n:PublicationDelivery/n:dataObjects/n:CompositeFrame/n:frames/n:ServiceCalendarFrame", "1",
		// "No ServiceCalendarFrame", _1_NETEX_SERVICE_CALENDAR_FRAME);
		validateElementPresent(context, xpath, dom, "/n:PublicationDelivery/n:dataObjects/n:CompositeFrame/n:frames/n:TimetableFrame", "1", "No TimetableFrame",
				_1_NETEX_TIMETABLE_FRAME);
		validateElementNotPresent(context, xpath, dom, "/n:PublicationDelivery/n:dataObjects/n:CompositeFrame/n:frames/n:SiteFrame", "1", "SiteFrame present",
				_1_NETEX_SITE_FRAME);

		validateResourceFrame(context, xpath, dom);

		validateServiceFrame(context, xpath, dom);
		validateTimetableFrame(context, xpath, dom);
		// validateResourceFrame(context,xpath,dom);

		// validateElementNotPresent(context, xpath, dom, "//n:SiteFrame/n:stopPlaces/n:StopPlace", "1", "Should not contain StopPlaces",
		// _2_NETEX_SITEFRAME_STOPPLACE);
		// validateExternalReferenceCorrect(context, xpath, dom, "//n:StopPlaceRef/@ref", stopRegisterValidator, _2_NETEX_STOPPLACE_REF);

		// TODO add profile validation elements based on external reference data (dom)

		// TODO consider check if frames present through xpath validation, before actual validation of frame
		// TODO add profile validation elements based on java codex
		// validateResourceFrame(context, referential);
		// validateSiteFrame(context, referential);
		// validateServiceFrame(context, referential);
		// validateServiceCalendarFrame(context, referential);
		// validateTimetableFrame(context, referential);

		return;
	}

	private void validateResourceFrame(Context context, XPath xpath, Document dom) throws XPathExpressionException {
		Node root = selectNode("/n:PublicationDelivery/n:dataObjects/n:CompositeFrame/n:frames/n:ResourceFrame", xpath, dom);
		if (root != null && root.hasChildNodes()) {
			validateElementPresent(context, xpath, root, "n:organisations", "1", "No organisations", _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS);
			validateElementPresent(context, xpath, root, "n:organisations/n:Authority", "1", "No Authority",
					_1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY);
			validateAtLeastElementPresent(context, xpath, root, "n:organisations/n:Operator", 1, "1", "No Operators",
					_1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR);
			NodeList operators = selectNodeSet("n:organisations/n:Operator", xpath, root);
			for (int i = 0; i < operators.getLength(); i++) {
				validateOperator(context, xpath, operators.item(i));
			}
		}
	}

	private void validateOperator(Context context, XPath xpath, Node node) throws XPathExpressionException {
		validateElementPresent(context, xpath, node, "n:CompanyNumber", "1", "No operator company number",
				_1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_COMPANY_NUMBER);
		validateElementPresent(context, xpath, node, "n:Name", "1", "No operator name", _1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_NAME);
		validateElementPresent(context, xpath, node, "n:LegalName", "1", "No operator legal name",
				_1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_LEGAL_NAME);
		validateElementPresent(context, xpath, node, "n:ContactDetails", "1", "No operator contact details",
				_1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CONTACT_DETAILS);
		validateElementPresent(context, xpath, node, "n:CustomerServiceContactDetails", "1", "No operator customer service contact details",
				_1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CUSTOMER_SERVICE_CONTACT_DETAILS);
	}

	private void validateServiceFrame(Context context, XPath xpath, Document dom) throws XPathExpressionException {
		Node root = selectNode("/n:PublicationDelivery/n:dataObjects/n:CompositeFrame/n:frames/n:ServiceFrame", xpath, dom);

		validateElementPresent(context, xpath, root, "n:Network", "1", "A line must belong to a network", _1_NETEX_SERVICE_FRAME_NETWORK);
		validateElementPresent(context, xpath, root, "n:lines/n:Line", "1", "One and only one Line in each file", _1_NETEX_SERVICE_FRAME_LINE);
		validateElementPresent(context, xpath, root, "n:lines/n:Line[1]/n:PublicCode", "1", "Lines must have PublicCode",
				_1_NETEX_SERVICE_FRAME_LINE_PUBLIC_CODE);
		validateElementNotPresent(context, xpath, root, "n:journeyPatterns/n:ServiceJourneyPattern", "1", "Preferred to use JourneyPattern",
				_1_NETEX_SERVICE_FRAME_SERVICE_JOURNEY_PATTERN);
		validateAtLeastElementPresent(context, xpath, root, "n:journeyPatterns/n:JourneyPattern | n:journeyPatterns/n:ServiceJourneyPattern", 1, "1",
				"JourneyPatterns present", _1_NETEX_SERVICE_FRAME_JOURNEY_PATTERN);
		validateElementNotPresent(context, xpath, root, "n:timingPoints", "1", "TimingPoints not used", _1_NETEX_SERVICE_FRAME_TIMING_POINTS);

	}

	private void validateTimetableFrame(Context context, XPath xpath, Document dom) throws XPathExpressionException {
		Node root = selectNode("/n:PublicationDelivery/n:dataObjects/n:CompositeFrame/n:frames/n:TimetableFrame", xpath, dom);

		validateAtLeastElementPresent(context, xpath, root, "n:vehicleJourneys/n:ServiceJourney", 1, "1", "At least one ServiceJourney must be presetn",
				_1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY);
		validateElementNotPresent(context, xpath, root, "n:vehicleJourneys/n:ServiceJourney[count(n:TransportMode) = 1]", "1",
				"ServiceJourney should not have TransportMode, set on Line instead", _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_TRANSPORT_MODE);
		validateElementNotPresent(context, xpath, root, "n:vehicleJourneys/n:ServiceJourney[count(n:passingTimes/n:TimetabledPassingTime) < 2]", "1",
				"ServiceJourney must have at least 2 TimetabledPassingTimes", _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIMES);
		validateElementNotPresent(context, xpath, root, "n:vehicleJourneys/n:ServiceJourney/n:calls", "1", "ServiceJourney calls not supported",
				_1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_CALLS);
		validateElementNotPresent(context, xpath, root,
				"n:vehicleJourneys/n:ServiceJourney[count(n:passingTimes/n:TimetabledPassingTime[1]/n:DepartureTime) = 0]", "1",
				"First stop must have a DepartureTime", _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_FIRST_DEPARTURE);
		validateElementNotPresent(context, xpath, root,
				"n:vehicleJourneys/n:ServiceJourney[count(n:passingTimes/n:TimetabledPassingTime[last()]/n:ArrivalTime) = 0]", "1",
				"Last stop must have an ArrivalTime", _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_LAST_ARRIVAL);

	}

	private void validateResourceFrame(Context context, NetexReferential referential) throws Exception {
		// TODO consider moving up one level in call hierarchy
		DataLocation dataLocation = new DataLocation((String) context.get(FILE_NAME));
		Collection<ResourceFrame> resourceFrames = referential.getResourceFrames().values();
		prepareCheckPoint(context, FRAME_1);

		if (isCollectionEmpty(resourceFrames)) {
			ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
			validationReporter.addCheckPointReportError(context, FRAME_1, "Missing mandatory frame : 'ResourceFrame'", dataLocation);
		} else {
			for (ResourceFrame resourceFrame : resourceFrames) {

				// validate data sources
				DataSourcesInFrame_RelStructure dataSourcesStruct = resourceFrame.getDataSources();
				if (dataSourcesStruct != null && !isCollectionEmpty(dataSourcesStruct.getDataSource())) {
					log.info("DataSources present");
					List<DataSource> dataSources = dataSourcesStruct.getDataSource();
					for (DataSource dataSource : dataSources) {
						// TODO validate data source instance
					}
				}

				// validate responsibility sets
				ResponsibilitySetsInFrame_RelStructure responsibilitySetsStruct = resourceFrame.getResponsibilitySets();
				if (responsibilitySetsStruct != null && !isCollectionEmpty(responsibilitySetsStruct.getResponsibilitySet())) {
					log.info("ResponsibilitySets present");
					List<ResponsibilitySet> responsibilitySets = responsibilitySetsStruct.getResponsibilitySet();
					for (ResponsibilitySet responsibilitySet : responsibilitySets) {
						// TODO validate responsibility set instance
					}
				}

				TypesOfValueInFrame_RelStructure typesOfValueStruct = resourceFrame.getTypesOfValue();
				if (typesOfValueStruct != null && !isCollectionEmpty(typesOfValueStruct.getValueSetOrTypeOfValue())) {
					log.info("TypesOfValues present");
					List<JAXBElement<? extends DataManagedObjectStructure>> typesOfValueElements = typesOfValueStruct.getValueSetOrTypeOfValue();
					for (JAXBElement<? extends DataManagedObjectStructure> typesOfValueElement : typesOfValueElements) {
						// TODO downcast and validate
					}
				}

				// validate organisations
				// TODO consider if this is a problem or not, we already have all organisations of all frames in referential right?
				// TODO this will probably cause same validation to occur multiple times, once per frame if more than one
				// TODO this is true for all validations where we already added refs to global referential
				// TODO in other words true for routes, lines, organisations, stoppoints etc...
				// TODO same goes for parsing
				Collection<Organisation> organisations = referential.getOrganisations().values();
				if (!isCollectionEmpty(organisations)) {
					log.info("Organisations present");
					// TODO consider adding the data to be validated in constant VALIDATION_DATA
					OrganisationValidator organisationValidator = (OrganisationValidator) ValidatorFactory.create(OrganisationValidator.class.getName(),
							context);
					organisationValidator.validate(context, null);
				}

				// validate groups of operators
				GroupsOfOperatorsInFrame_RelStructure groupsOfOperatorsStruct = resourceFrame.getGroupsOfOperators();
				if (groupsOfOperatorsStruct != null && !isCollectionEmpty(groupsOfOperatorsStruct.getGroupOfOperators())) {
					log.info("GroupsOfOperators present");
					List<GroupOfOperators> groupOfOperatorsList = groupsOfOperatorsStruct.getGroupOfOperators();
					for (GroupOfOperators groupOfOperators : groupOfOperatorsList) {
						// TODO validate group of operators instance
					}
				}

				// validate equipments
				EquipmentsInFrame_RelStructure equipmentsStruct = resourceFrame.getEquipments();
				if (equipmentsStruct != null && !isCollectionEmpty(equipmentsStruct.getEquipment())) {
					log.info("Equipments present");
					List<JAXBElement<? extends Equipment_VersionStructure>> equipmentElements = equipmentsStruct.getEquipment();
					for (JAXBElement<? extends Equipment_VersionStructure> equipmentElement : equipmentElements) {
						Equipment_VersionStructure equipment = equipmentElement.getValue();
						// TODO downcast and validate
					}
				}

				// validate vehichle types
				VehicleTypesInFrame_RelStructure vehicleTypesStruct = resourceFrame.getVehicleTypes();
				if (vehicleTypesStruct != null && !isCollectionEmpty(vehicleTypesStruct.getCompoundTrainOrTrainOrVehicleType())) {
					log.info("VehicleTypes present");
					List<VehicleType_VersionStructure> vehicleTypeStructList = vehicleTypesStruct.getCompoundTrainOrTrainOrVehicleType();
					for (VehicleType_VersionStructure vehicleTypeStruct : vehicleTypeStructList) {
						// TODO validate
					}
				}

				// validate vehicles
				VehiclesInFrame_RelStructure vehiclesStruct = resourceFrame.getVehicles();
				if (vehiclesStruct != null && !isCollectionEmpty(vehiclesStruct.getTrainElementOrVehicle())) {
					log.info("Vehicles present");
					List<DataManagedObjectStructure> vehicles = vehiclesStruct.getTrainElementOrVehicle();
					for (DataManagedObjectStructure vehicle : vehicles) {
						// TODO downcast and validate
					}
				}

				// validate schematic maps
				SchematicMapsInFrame_RelStructure schematicMapsStruct = resourceFrame.getSchematicMaps();
				if (schematicMapsStruct != null && !isCollectionEmpty(schematicMapsStruct.getSchematicMap())) {
					log.info("SchematicMaps present");
					List<SchematicMap> schematicMapList = schematicMapsStruct.getSchematicMap();
					for (SchematicMap schematicMap : schematicMapList) {
						// TODO validate
					}
				}

				// validate groups of entities
				GroupOfEntitiesInFrame_RelStructure groupsOfEntities = resourceFrame.getGroupsOfEntities();
				if (groupsOfEntities != null && !isCollectionEmpty(groupsOfEntities.getGeneralGroupOfEntities())) {
					log.info("GroupOfEntities present");
					List<GeneralGroupOfEntities> generalGroupOfEntities = groupsOfEntities.getGeneralGroupOfEntities();
					for (GeneralGroupOfEntities generalGroupOfEntity : generalGroupOfEntities) {
						// TODO downcast and validate
					}
				}
			}
		}
	}

	private void validateSiteFrame(Context context, NetexReferential referential) throws Exception {
		// TODO consider moving up one level in call hierarchy
		DataLocation dataLocation = new DataLocation((String) context.get(FILE_NAME));
		Collection<SiteFrame> siteFrames = referential.getSiteFrames().values();
		prepareCheckPoint(context, FRAME_2);

		if (isCollectionEmpty(siteFrames)) {
			ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
			validationReporter.addCheckPointReportError(context, FRAME_2, "Missing mandatory frame : 'SiteFrame'", dataLocation);
		} else {
			for (SiteFrame siteFrame : siteFrames) {
				// TODO add validation of site frame elements

				// validate topographic places
				TopographicPlacesInFrame_RelStructure topographicPlacesStruct = siteFrame.getTopographicPlaces();
				if (topographicPlacesStruct != null && !isCollectionEmpty(topographicPlacesStruct.getTopographicPlace())) {
					log.info("TopographicPlace present");
					List<TopographicPlace> topographicPlaces = topographicPlacesStruct.getTopographicPlace();
					for (TopographicPlace topographicPlace : topographicPlaces) {
						// TODO validate
					}
				}

				// validate addresses
				AddressesInFrame_RelStructure addressesStruct = siteFrame.getAddresses();
				if (addressesStruct != null && !isCollectionEmpty(addressesStruct.getAddress())) {
					log.info("Addresses present");
					List<JAXBElement<? extends Address_VersionStructure>> addressElements = addressesStruct.getAddress();
					for (JAXBElement<? extends Address_VersionStructure> addressElement : addressElements) {
						// TODO downcast and validate
						// Address_VersionStructure value = addressElement.getValue();
					}
				}

				// validate accesses
				AccessesInFrame_RelStructure accessesStruct = siteFrame.getAccesses();
				if (accessesStruct != null && !isCollectionEmpty(accessesStruct.getAccess())) {
					log.info("Accesses present");
					List<Access> accesses = accessesStruct.getAccess();
					for (Access access : accesses) {
						// TODO validate access instance
					}
				}

				// validate stop places
				StopPlacesInFrame_RelStructure stopPlacesStruct = siteFrame.getStopPlaces();
				if (stopPlacesStruct != null && !isCollectionEmpty(stopPlacesStruct.getStopPlace())) {
					log.info("StopPlaces present");
					List<StopPlace> stopPlaces = stopPlacesStruct.getStopPlace();
					for (StopPlace stopPlace : stopPlaces) {
						// TODO validate stop place
					}
				}

				// validate flexible stop places
				FlexibleStopPlacesInFrame_RelStructure flexibleStopPlacesStruct = siteFrame.getFlexibleStopPlaces();
				if (flexibleStopPlacesStruct != null && !isCollectionEmpty(flexibleStopPlacesStruct.getFlexibleStopPlace())) {
					log.info("FlexibleStopPlaces present");
					List<FlexibleStopPlace> flexibleStopPlaces = flexibleStopPlacesStruct.getFlexibleStopPlace();
					for (FlexibleStopPlace flexibleStopPlace : flexibleStopPlaces) {
						// TODO validate
					}
				}

				// validate points of interest
				PointsOfInterestInFrame_RelStructure pointsOfInterestStruct = siteFrame.getPointsOfInterest();
				if (pointsOfInterestStruct != null && !isCollectionEmpty(pointsOfInterestStruct.getPointOfInterest())) {
					log.info("PointsOfInterest present");
					List<PointOfInterest> pointOfInterestList = pointsOfInterestStruct.getPointOfInterest();
					for (PointOfInterest pointOfInterest : pointOfInterestList) {
						// TODO validate
					}
				}

				// validate parkings
				ParkingsInFrame_RelStructure parkingsStruct = siteFrame.getParkings();
				if (parkingsStruct != null && !isCollectionEmpty(parkingsStruct.getParking())) {
					log.info("Parkings present");
					List<Parking> parkings = parkingsStruct.getParking();
					for (Parking parking : parkings) {
						// TODO validate
					}
				}

				// validate navigation paths
				NavigationPathsInFrame_RelStructure navigationPathsStruct = siteFrame.getNavigationPaths();
				if (navigationPathsStruct != null && !isCollectionEmpty(navigationPathsStruct.getNavigationPath())) {
					log.info("NavigationPaths present");
					List<NavigationPath> navigationPaths = navigationPathsStruct.getNavigationPath();
					for (NavigationPath navigationPath : navigationPaths) {
						// TODO validate
					}
				}

				// validate site facility sets
				SiteFacilitySetsInFrame_RelStructure siteFacilitySetsStruct = siteFrame.getSiteFacilitySets();
				if (siteFacilitySetsStruct != null && !isCollectionEmpty(siteFacilitySetsStruct.getSiteFacilitySet())) {
					log.info("SiteFacilitySets present");
					List<SiteFacilitySet> siteFacilitySets = siteFacilitySetsStruct.getSiteFacilitySet();
					for (SiteFacilitySet siteFacilitySet : siteFacilitySets) {
						// TODO validate
					}
				}
			}
		}
	}

	private void validateServiceFrame(Context context, NetexReferential referential) throws Exception {
		// TODO consider moving up one level in call hierarchy
		DataLocation dataLocation = new DataLocation((String) context.get(FILE_NAME));
		Collection<ServiceFrame> serviceFrames = referential.getServiceFrames().values();
		prepareCheckPoint(context, FRAME_3);

		if (isCollectionEmpty(serviceFrames)) {
			ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
			validationReporter.addCheckPointReportError(context, FRAME_3, "Missing mandatory frame : 'ServiceFrame'", dataLocation);
		} else {
			for (ServiceFrame serviceFrame : serviceFrames) {
				// TODO validate frame elements

				// validate network
				Network network = serviceFrame.getNetwork();
				if (network != null) {
					log.info("Network present");
					// TODO consider validating this Network instance, or instance registered in referential
					NetworkValidator networkValidator = (NetworkValidator) ValidatorFactory.create(NetworkValidator.class.getName(), context);
					networkValidator.validate(context, network);
				}

				// validate route points
				Collection<RoutePoint> routePoints = referential.getRoutePoints().values();
				if (!isCollectionEmpty(routePoints)) {
					log.info("Route points present");
					RoutePointValidator routePointValidator = (RoutePointValidator) ValidatorFactory.create(RoutePointValidator.class.getName(), context);
					routePointValidator.validate(context, null);
				}

				// validate routes
				Collection<Route> routes = referential.getRoutes().values();
				if (!isCollectionEmpty(routes)) {
					log.info("Routes present");
					RouteValidator routeValidator = (RouteValidator) ValidatorFactory.create(RouteValidator.class.getName(), context);
					routeValidator.validate(context, null);
				}

				// validate flexiblePointProperties
				FlexiblePointProperties_RelStructure flexiblePointPropertiesStruct = serviceFrame.getFlexiblePointProperties();
				if (flexiblePointPropertiesStruct != null && !isCollectionEmpty(flexiblePointPropertiesStruct.getFlexiblePointProperties())) {
					log.info("FlexiblePointProperties present");
					List<FlexiblePointProperties> flexiblePointProperties = flexiblePointPropertiesStruct.getFlexiblePointProperties();
					for (FlexiblePointProperties flexiblePointProperty : flexiblePointProperties) {
						// TODO validate
					}
				}

				// validate flexibleLinkProperties
				FlexibleLinkProperties_RelStructure flexibleLinkPropertiesStruct = serviceFrame.getFlexibleLinkProperties();
				if (flexibleLinkPropertiesStruct != null && !isCollectionEmpty(flexibleLinkPropertiesStruct.getFlexibleLinkProperties())) {
					log.info("FlexibleLinkProperties present");
					List<FlexibleLinkProperties> flexibleLinkProperties = flexibleLinkPropertiesStruct.getFlexibleLinkProperties();
					for (FlexibleLinkProperties flexibleLinkProperty : flexibleLinkProperties) {
						// TODO validate
					}
				}

				// validate commonSections
				CommonSectionsInFrame_RelStructure commonSectionsStruct = serviceFrame.getCommonSections();
				if (commonSectionsStruct != null && !isCollectionEmpty(commonSectionsStruct.getCommonSection())) {
					log.info("CommonSections present");
					List<CommonSection> commonSections = commonSectionsStruct.getCommonSection();
					for (CommonSection commonSection : commonSections) {
						// TODO validate
					}
				}

				// validate lines
				Collection<Line> lines = referential.getLines().values();
				if (!isCollectionEmpty(lines)) {
					log.info("Lines present");
					LineValidator lineValidator = (LineValidator) ValidatorFactory.create(LineValidator.class.getName(), context);
					lineValidator.validate(context, null);
				}

				// validate groups of lines
				GroupsOfLinesInFrame_RelStructure groupsOfLinesStruct = serviceFrame.getGroupsOfLines();
				if (groupsOfLinesStruct != null && !isCollectionEmpty(groupsOfLinesStruct.getGroupOfLines())) {
					log.info("GroupsOfLines present");
					List<GroupOfLines> groupsOfLines = groupsOfLinesStruct.getGroupOfLines();
					for (GroupOfLines groupOfLines : groupsOfLines) {
						// TODO validate
					}
				}

				// validate destination displays
				DestinationDisplaysInFrame_RelStructure destinationDisplaysStruct = serviceFrame.getDestinationDisplays();
				if (destinationDisplaysStruct != null && !isCollectionEmpty(destinationDisplaysStruct.getDestinationDisplay())) {
					log.info("DestinationDisplays present");
					List<DestinationDisplay> destinationDisplays = destinationDisplaysStruct.getDestinationDisplay();
					for (DestinationDisplay destinationDisplay : destinationDisplays) {
						// TODO validate
					}
				}

				// validate scheduled stop points
				// TODO implement separate validator
				Collection<ScheduledStopPoint> scheduledStopPoints = referential.getScheduledStopPoints().values();
				if (!isCollectionEmpty(scheduledStopPoints)) {
					log.info("ScheduledStopPoints present");
					for (ScheduledStopPoint scheduledStopPoint : scheduledStopPoints) {
						// TODO validate
					}
				}

				// validate service patterns
				ServicePatternsInFrame_RelStructure servicePatternsStruct = serviceFrame.getServicePatterns();
				if (servicePatternsStruct != null && !isCollectionEmpty(servicePatternsStruct.getServicePatternOrJourneyPatternView())) {
					log.info("ServicePatterns present");
					List<Object> servicePatterns = servicePatternsStruct.getServicePatternOrJourneyPatternView();
					for (Object servicePattern : servicePatterns) {
						// TODO downcast and validate
					}
				}

				// validate tariff zones
				TariffZonesInFrame_RelStructure tariffZonesStruct = serviceFrame.getTariffZones();
				if (tariffZonesStruct != null && !isCollectionEmpty(tariffZonesStruct.getTariffZone())) {
					log.info("TariffZones present");
					List<TariffZone> tariffZones = tariffZonesStruct.getTariffZone();
					for (TariffZone tariffZone : tariffZones) {
						// TODO validate
					}
				}

				// validate stop assignments
				// TODO make type more generic in referential (StopAssignment)
				// TODO implement separate validator
				Collection<PassengerStopAssignment> stopAssignments = referential.getPassengerStopAssignments().values();
				if (!isCollectionEmpty(stopAssignments)) {
					log.info("PassengerStopAssignments present");
					for (PassengerStopAssignment stopAssignment : stopAssignments) {
						// TODO up-/downcast and validate
					}
				}

				// validate timing points
				TimingPointsInFrame_RelStructure timingPointsStruct = serviceFrame.getTimingPoints();
				if (timingPointsStruct != null && !isCollectionEmpty(timingPointsStruct.getTimingPoint())) {
					log.info("TimingPoints present");
					List<TimingPoint> timingPoints = timingPointsStruct.getTimingPoint();
					for (TimingPoint timingPoint : timingPoints) {
						// TODO validate
					}
				}

				// validate timing links
				TimingLinksInFrame_RelStructure timingLinksStruct = serviceFrame.getTimingLinks();
				if (timingLinksStruct != null && !isCollectionEmpty(timingLinksStruct.getTimingLink())) {
					log.info("TimingLinks present");
					List<TimingLink> timingLinks = timingLinksStruct.getTimingLink();
					for (TimingLink timingLink : timingLinks) {
						// TODO validate
					}
				}

				// validate journey patterns
				// TODO implement separate validator for journey patterns
				Collection<JourneyPattern_VersionStructure> journeyPatterns = referential.getJourneyPatterns().values();
				if (!isCollectionEmpty(journeyPatterns)) {
					log.info("JourneyPatterns present");
					for (JourneyPattern_VersionStructure journeyPattern : journeyPatterns) {
						// TODO validate
					}
				}

				// validate service exclusions
				ServiceExclusionsInFrame_RelStructure serviceExclusionsStruct = serviceFrame.getServiceExclusions();
				if (serviceExclusionsStruct != null && !isCollectionEmpty(serviceExclusionsStruct.getServiceExclusion())) {
					log.info("ServiceExclusions present");
					List<ServiceExclusion> serviceExclusions = serviceExclusionsStruct.getServiceExclusion();
					for (ServiceExclusion serviceExclusion : serviceExclusions) {
						// TODO validate
					}
				}

				// validate notices
				NoticesInFrame_RelStructure noticesStruct = serviceFrame.getNotices();
				if (noticesStruct != null && !isCollectionEmpty(noticesStruct.getNotice())) {
					log.info("Notices present");
					List<Notice> notices = noticesStruct.getNotice();
					for (Notice notice : notices) {
						// TODO validate
					}
				}

				// validate notice assignments
				NoticeAssignmentsInFrame_RelStructure noticeAssignmentsStruct = serviceFrame.getNoticeAssignments();
				if (noticeAssignmentsStruct != null && !isCollectionEmpty(noticeAssignmentsStruct.getNoticeAssignment_())) {
					log.info("NoticeAssignments present");
					List<JAXBElement<? extends DataManagedObjectStructure>> noticeAssignmentElements = noticeAssignmentsStruct.getNoticeAssignment_();
					for (JAXBElement<? extends DataManagedObjectStructure> noticeAssignmentElement : noticeAssignmentElements) {
						// TODO downcast and validate
					}
				}
			}
		}
	}

	private void validateServiceCalendarFrame(Context context, NetexReferential referential) {
		// TODO consider moving up one level in call hierarchy
		DataLocation dataLocation = new DataLocation((String) context.get(FILE_NAME));
		Collection<ServiceCalendarFrame> serviceCalendarFrames = referential.getServiceCalendarFrames().values();
		prepareCheckPoint(context, FRAME_4);

		if (isCollectionEmpty(serviceCalendarFrames)) {
			ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
			validationReporter.addCheckPointReportError(context, FRAME_4, "Missing mandatory frame : 'ServiceCalendarFrame'", dataLocation);
		} else {
			for (ServiceCalendarFrame serviceCalendarFrame : serviceCalendarFrames) {

				// validate service calendar
				ServiceCalendar serviceCalendar = serviceCalendarFrame.getServiceCalendar();
				if (serviceCalendar != null) {
					log.info("Service calendar present");
					// TODO validate
				}

				// validate day types
				// TODO implement separate validator
				Collection<DayType> dayTypes = referential.getDayTypes().values();
				if (!isCollectionEmpty(dayTypes)) {
					log.info("DayTypes present");
					for (DayType dayType : dayTypes) {
						// TODO validate
					}
				}

				// validate timebands
				TimebandsInFrame_RelStructure timebandsStruct = serviceCalendarFrame.getTimebands();
				if (timebandsStruct != null && !isCollectionEmpty(timebandsStruct.getTimeband())) {
					log.info("Timebands present");
					List<Timeband> timebands = timebandsStruct.getTimeband();
					for (Timeband timeband : timebands) {
						// TODO validate
					}
				}

				// validate operating days
				OperatingDaysInFrame_RelStructure operatingDaysStruct = serviceCalendarFrame.getOperatingDays();
				if (operatingDaysStruct != null && !isCollectionEmpty(operatingDaysStruct.getOperatingDay())) {
					log.info("OperatingDays present");
					List<OperatingDay> operatingDays = operatingDaysStruct.getOperatingDay();
					for (OperatingDay operatingDay : operatingDays) {
						// TODO validate
					}
				}

				// validate operating periods
				OperatingPeriodsInFrame_RelStructure operatingPeriodsStruct = serviceCalendarFrame.getOperatingPeriods();
				if (operatingPeriodsStruct != null && !isCollectionEmpty(operatingPeriodsStruct.getOperatingPeriodOrUicOperatingPeriod())) {
					log.info("OperatingPeriods present");
					List<OperatingPeriod_VersionStructure> operatingPeriods = operatingPeriodsStruct.getOperatingPeriodOrUicOperatingPeriod();
					for (OperatingPeriod_VersionStructure operatingPeriod : operatingPeriods) {
						// TODO downcast and validate
					}
				}

				// validate day type assignments
				DayTypeAssignmentsInFrame_RelStructure dayTypeAssignmentsStruct = serviceCalendarFrame.getDayTypeAssignments();
				if (dayTypeAssignmentsStruct != null && !isCollectionEmpty(dayTypeAssignmentsStruct.getDayTypeAssignment())) {
					log.info("DayTypeAssignments present");
					List<DayTypeAssignment> dayTypeAssignments = dayTypeAssignmentsStruct.getDayTypeAssignment();
					for (DayTypeAssignment dayTypeAssignment : dayTypeAssignments) {
						// TODO validate
					}
				}
			}
		}
	}

	private void validateTimetableFrame(Context context, NetexReferential referential) {
		// TODO consider moving up one level in call hierarchy
		DataLocation dataLocation = new DataLocation((String) context.get(FILE_NAME));
		Collection<TimetableFrame> timetableFrames = referential.getTimetableFrames().values();
		prepareCheckPoint(context, FRAME_5);

		if (isCollectionEmpty(timetableFrames)) {
			ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
			validationReporter.addCheckPointReportError(context, FRAME_5, "Missing mandatory frame : 'TimetableFrame'", dataLocation);
		} else {
			for (TimetableFrame timetableFrame : timetableFrames) {

				// validate bookingtimes/validityconditions, which one?
				ContainedAvailabilityConditions_RelStructure bookingTimesStruct = timetableFrame.getBookingTimes();
				if (bookingTimesStruct != null && !isCollectionEmpty(bookingTimesStruct.getAvailabilityCondition())) {
					log.info("ContainedAvailabilityConditions present");
					List<AvailabilityCondition> availabilityConditions = bookingTimesStruct.getAvailabilityCondition();
					for (AvailabilityCondition availabilityCondition : availabilityConditions) {
						// TODO validate
					}
				}
				// validate bookingtimes/validityconditions, which one?
				ValidityConditions_RelStructure validityConditionsStruct = timetableFrame.getValidityConditions();
				if (validityConditionsStruct != null
						&& !isCollectionEmpty(validityConditionsStruct.getValidityConditionRefOrValidBetweenOrValidityCondition_())) {
					log.info("ValidityConditions present");
					List<Object> validityConditions = validityConditionsStruct.getValidityConditionRefOrValidBetweenOrValidityCondition_();
					for (Object validityCondition : validityConditions) {
						// TODO downcast and validate
					}
				}

				// validate vehicle journeys
				// TODO implement separate validator
				Collection<ServiceJourney> serviceJourneys = referential.getServiceJourneys().values();
				if (!isCollectionEmpty(serviceJourneys)) {
					log.info("ServiceJourneys present");
					for (ServiceJourney serviceJourney : serviceJourneys) {
						// TODO validate
					}
				}

				// validate frequency groups
				FrequencyGroupsInFrame_RelStructure frequencyGroupsStruct = timetableFrame.getFrequencyGroups();
				if (frequencyGroupsStruct != null && !isCollectionEmpty(frequencyGroupsStruct.getHeadwayJourneyGroupOrRhythmicalJourneyGroup())) {
					log.info("FrequencyGroups present");
					List<JourneyFrequencyGroup_VersionStructure> frequencyGroups = frequencyGroupsStruct.getHeadwayJourneyGroupOrRhythmicalJourneyGroup();
					for (JourneyFrequencyGroup_VersionStructure frequencyGroup : frequencyGroups) {
						// TODO validate
					}
				}

				// validate groups of services
				GroupsOfServicesInFrame_RelStructure groupsOfServicesStruct = timetableFrame.getGroupsOfServices();
				if (groupsOfServicesStruct != null && !isCollectionEmpty(groupsOfServicesStruct.getGroupOfServices())) {
					log.info("GroupsOfServices present");
					List<GroupOfServices> groupsOfServices = groupsOfServicesStruct.getGroupOfServices();
					for (GroupOfServices groupOfServices : groupsOfServices) {
						// TODO validate
					}
				}

				// validate journey part couples
				JourneyPartCouplesInFrame_RelStructure journeyPartCouplesStruct = timetableFrame.getJourneyPartCouples();
				if (journeyPartCouplesStruct != null && !isCollectionEmpty(journeyPartCouplesStruct.getJourneyPartCouple())) {
					log.info("JourneyPartCouples present");
					List<JourneyPartCouple> journeyPartCouples = journeyPartCouplesStruct.getJourneyPartCouple();
					for (JourneyPartCouple journeyPartCouple : journeyPartCouples) {
						// TODO validate
					}
				}

				// validate coupled journeys
				CoupledJourneysInFrame_RelStructure coupledJourneysStruct = timetableFrame.getCoupledJourneys();
				if (coupledJourneysStruct != null && !isCollectionEmpty(coupledJourneysStruct.getCoupledJourney())) {
					log.info("CoupledJourneys present");
					List<CoupledJourney> coupledJourneys = coupledJourneysStruct.getCoupledJourney();
					for (CoupledJourney coupledJourney : coupledJourneys) {
						// TODO validate
					}
				}

				// validate service facility sets
				ServiceFacilitySetsInFrame_RelStructure serviceFacilitySetsStruct = timetableFrame.getServiceFacilitySets();
				if (serviceFacilitySetsStruct != null && !isCollectionEmpty(serviceFacilitySetsStruct.getServiceFacilitySet())) {
					log.info("ServiceFacilitySets present");
					List<ServiceFacilitySet> serviceFacilitySets = serviceFacilitySetsStruct.getServiceFacilitySet();
					for (ServiceFacilitySet serviceFacilitySet : serviceFacilitySets) {
						// TODO validate
					}
				}

				// validate flexible service properties
				FlexibleServicePropertiesInFrame_RelStructure flexibleServicePropertiesStruct = timetableFrame.getFlexibleServiceProperties();
				if (flexibleServicePropertiesStruct != null && !isCollectionEmpty(flexibleServicePropertiesStruct.getFlexibleServiceProperties())) {
					log.info("FlexibleServiceProperties present");
					List<FlexibleServiceProperties> flexibleServicePropertiesList = flexibleServicePropertiesStruct.getFlexibleServiceProperties();
					for (FlexibleServiceProperties flexibleServiceProperties : flexibleServicePropertiesList) {
						// TODO validate
					}
				}

				// validate journey meetings
				JourneyMeetingsInFrame_RelStructure journeyMeetingsStruct = timetableFrame.getJourneyMeetings();
				if (journeyMeetingsStruct != null && !isCollectionEmpty(journeyMeetingsStruct.getJourneyMeeting())) {
					log.info("JourneyMeetings present");
					List<JourneyMeeting> journeyMeetings = journeyMeetingsStruct.getJourneyMeeting();
					for (JourneyMeeting journeyMeeting : journeyMeetings) {
						// TODO validate
					}
				}

				// validate journey interchanges
				JourneyInterchangesInFrame_RelStructure journeyInterchangesStruct = timetableFrame.getJourneyInterchanges();
				if (journeyInterchangesStruct != null
						&& !isCollectionEmpty(journeyInterchangesStruct.getServiceJourneyPatternInterchangeOrServiceJourneyInterchange())) {
					log.info("JourneyInterchanges present");
					List<Interchange_VersionStructure> journeyInterchanges = journeyInterchangesStruct
							.getServiceJourneyPatternInterchangeOrServiceJourneyInterchange();
					for (Interchange_VersionStructure journeyInterchange : journeyInterchanges) {
						// TODO downcast and validate
					}
				}
			}
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
		return Arrays.asList(new String[] {
			PROFILE_ID_1	
		});
	}

}
