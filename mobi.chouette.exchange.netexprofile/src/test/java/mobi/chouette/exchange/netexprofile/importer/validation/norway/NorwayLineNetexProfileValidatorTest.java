package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import static mobi.chouette.exchange.netexprofile.NetexTestUtils.createCodespace;
import static mobi.chouette.exchange.validation.report.ValidationReporter.RESULT.NOK;
import static mobi.chouette.exchange.validation.report.ValidationReporter.RESULT.UNCHECK;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.xpath.XPathFactoryConfigurationException;

import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.DuplicateIdCheckerCommand;
import mobi.chouette.exchange.netexprofile.importer.NetexImporter;
import mobi.chouette.exchange.netexprofile.importer.util.IdVersion;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexProfileValidator;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.report.CheckPointErrorReport;
import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.CheckPointReport.SEVERITY;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.exchange.validation.report.ValidationReporter.RESULT;
import mobi.chouette.model.Codespace;
import mobi.chouette.model.util.Referential;
import net.sf.saxon.s9api.XdmNode;

public class NorwayLineNetexProfileValidatorTest {

	@Test
	public void testValidateSimpleFile() throws Exception {
		NetexImporter importer = new NetexImporter();

		Context context = createContext(importer);

		ValidationReport vr = new ValidationReport();
		context.put(Constant.VALIDATION_REPORT, vr);

		Set<Codespace> validCodespaces = new HashSet<>();
		Codespace validCodespace = createCodespace(1L, "AVI", "http://www.rutebanken.org/ns/avi");
		validCodespaces.add(validCodespace);
		context.put(Constant.NETEX_VALID_CODESPACES, validCodespaces);

		File file = new File("src/test/data/WF739-201608311015.xml");
		XdmNode dom = importer.parseFileToXdmNode(file, new HashSet<>());
		PublicationDeliveryStructure lineDeliveryStructure = importer.unmarshal(file, new HashSet<>());

		context.put(Constant.NETEX_DATA_JAVA, lineDeliveryStructure);
		context.put(Constant.NETEX_DATA_DOM, dom);

		NetexProfileValidator validator = new NorwayLineNetexProfileValidator();
		validator.initializeCheckPoints(context);
		validator.validate(context);
		boolean valid = true;
		for (CheckPointReport cp : vr.getCheckPoints()) {
			if (cp.getState() == NOK) {
				System.err.println(cp);
				valid = false;
			}
		}

		// TODO add more checks here
		Assert.assertFalse(valid);
		;
	}

	@Test
	public void testValidateWithCommonFile() throws Exception {
		NetexImporter importer = new NetexImporter();
		Context context = createContext(importer);

		ValidationReport vr = new ValidationReport();
		context.put(Constant.VALIDATION_REPORT, vr);

		Referential referential = new Referential();
		context.put(Constant.REFERENTIAL, referential);

		Set<Codespace> validCodespaces = new HashSet<>();

		Codespace nsrCodespace = createCodespace(1L, "NSR", "http://www.rutebanken.org/ns/nsr");
		validCodespaces.add(nsrCodespace);

		Codespace avinorCodespace = createCodespace(2L, "AVI", "http://www.rutebanken.org/ns/avi");
		validCodespaces.add(avinorCodespace);

		context.put(Constant.NETEX_VALID_CODESPACES, validCodespaces);

		File file = new File("src/test/data/norway_line_commonfile/_avinor_common_elements.xml");
		XdmNode commonDom = importer.parseFileToXdmNode(file, new HashSet<>());
		PublicationDeliveryStructure commonStructure = importer.unmarshal(file, new HashSet<>());
		context.put(Constant.NETEX_DATA_JAVA, commonStructure);
		context.put(Constant.NETEX_DATA_DOM, commonDom);
		context.put(mobi.chouette.exchange.netexprofile.Constant.NETEX_COMMON_FILE_IDENTIFICATORS, new HashMap<IdVersion, List<String>>());

		NetexProfileValidator commonValidator = new NorwayCommonNetexProfileValidator();
		commonValidator.addExternalReferenceValidator(new DummyStopReferentialIdValidator());
		commonValidator.initializeCheckPoints(context);
		commonValidator.validate(context);
		boolean valid = true;
		for (CheckPointReport cp : vr.getCheckPoints()) {
			if (cp.getState() == NOK && cp.getSeverity() == SEVERITY.ERROR) {
				System.err.println(cp);
				valid = false;
			}
		}

		// TODO add more checks here
		Assert.assertTrue(valid, "Common file validation errors detected");

		DuplicateIdCheckerCommand duplicateChecker = new DuplicateIdCheckerCommand();
		duplicateChecker.execute(context);
		
		File lineFile = new File("src/test/data/norway_line_commonfile/Norwegian-DY121-Stavanger-Bergen.xml");
		XdmNode lineDom = importer.parseFileToXdmNode(lineFile, new HashSet<>());
		PublicationDeliveryStructure lineStructure = importer.unmarshal(lineFile, new HashSet<>());
		context.put(Constant.NETEX_DATA_JAVA, lineStructure);
		context.put(Constant.NETEX_DATA_DOM, lineDom);

		NetexProfileValidator validator = new NorwayLineNetexProfileValidator();
		validator.addExternalReferenceValidator(new DummyStopReferentialIdValidator());
		validator.initializeCheckPoints(context);
		validator.validate(context);

		for (CheckPointReport cp : vr.getCheckPoints()) {
			if (cp.getState() == NOK && cp.getSeverity() == SEVERITY.ERROR) {
				System.err.println(cp);
				valid = false;
			}
		}

		// TODO add more checks here
		Assert.assertTrue(valid, "Line file validation errors detected");
	}

	@Test
	public void testValidateSingleLineFileWithCompositeFrame() throws Exception {
		NetexImporter importer = new NetexImporter();

		Context context = createContext(importer);

		ValidationReport vr = new ValidationReport();
		context.put(Constant.VALIDATION_REPORT, vr);

		Set<Codespace> validCodespaces = new HashSet<>();
		Codespace validCodespace = createCodespace(1L, "AVI", "http://www.rutebanken.org/ns/avi");
		validCodespaces.add(validCodespace);
		context.put(Constant.NETEX_VALID_CODESPACES, validCodespaces);

	    File lineFile = new File("src/test/data/Profile_Error_SingleLineFileCompositeFrame.xml");
		XdmNode dom = importer.parseFileToXdmNode(lineFile, new HashSet<>());
		PublicationDeliveryStructure lineDeliveryStructure = importer.unmarshal(lineFile, new HashSet<>());

		context.put(Constant.NETEX_DATA_JAVA, lineDeliveryStructure);
		context.put(Constant.NETEX_DATA_DOM, dom);

		NetexProfileValidator validator = new NorwayLineNetexProfileValidator();
		validator.initializeCheckPoints(context);
		validator.validate(context);

		Map<String, ValidationReporter.RESULT> expectedResults = new HashMap<>();
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_REFERENCE_TO_ILLEGAL_ELEMENT, NOK);

		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CUSTOMER_SERVICE_CONTACT_DETAILS, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_CONTACT_DETAILS, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_LEGAL_NAME, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_NAME, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_OPERATOR_COMPANY_NUMBER, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_CONTACT_DETAILS, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_LEGAL_NAME, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_NAME, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEXPROFILE_RESOURCE_FRAME_ORGANISATIONS_AUTHORITY_COMPANY_NUMBER, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_RESOURCE_FRAME, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_TIMETABLE_FRAME, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SERVICE_FRAME, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_CODESPACE, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SITE_FRAME, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SERVICE_FRAME_GROUPOFLINES_OUTSIDE_NETWORK, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SERVICE_FRAME_LINE, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SERVICE_FRAME_NETWORK_AUTHORITY_REF, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SERVICE_FRAME_NETWORK_NAME, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SERVICE_FRAME_NETWORK_GROUPOFLINE_NAME, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SERVICE_FRAME_TIMING_POINTS, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SERVICE_FRAME_JOURNEY_PATTERN, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SERVICE_FRAME_LINE_PUBLIC_CODE, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_TIMETABLE_FRAME_VEHICLEJOURNEY_OPERATORREF_OR_LINE_OPREATORREF, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SERVICE_FRAME_JOURNEY_PATTERN_ROUTE_REF, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_COMMON_SERVICE_FRAME_SERVICE_JOURNEY_PATTERN_MISSING_DESTINATIONDISPLAY, NOK);

		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SERVICE_FRAME_LINE_TRANSPORTMODE, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SERVICE_FRAME_LINE_TRANSPORTSUBMODE, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SERVICE_FRAME_LINE_GROUPOFLINES_OR_NETWORK, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SERVICE_FRAME_ROUTE_INDIRECTION, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SERVICE_FRAME_ROUTE_NAME, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SERVICE_FRAME_ROUTE_LINEREF, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SERVICE_FRAME_ROUTE_POINTSINSEQUENCE, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SERVICE_FRAME_DESTINATION_DISPLAY_FRONTTEXT, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SERVICE_FRAME_DESTINATION_DISPLAY_VIA_DESTINATIONDISPLAYREF, NOK);

		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SERVICE_FRAME_STOP_WITHOUT_BOARDING_OR_ALIGHTING, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SERVICE_FRAME_PASSENGER_STOP_ASSIGNMENT_SCHEDULEDSTOPPOINTREF, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SERVICE_FRAME_PASSENGER_STOP_ASSIGNMENT_QUAYREF, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SERVICE_FRAME_INVALID_TRANSPORTMODE, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SERVICE_FRAME_INVALID_TRANSPORTSUBMODE, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY, NOK);
//		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_TRANSPORT_MODE, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIMES, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_CALLS, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_MISSING_DEPARTURE_OR_ARRIVAL, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_FIRST_DEPARTURE, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_LAST_ARRIVAL, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_SAME_VALUE, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_TIMETABLE_FRAME_SERVICEJOURNEY_JOURNEYPATTERN_REF, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_DAYTYPEREF, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_MISSING_PASSING_TIME, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_TRANSPORTMODE_OVERRIDE, NOK);
		
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_COMPOSITE_FRAME_VALIDITYCONDTITIONS, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_VALIDITYCONDITIONS_ON_FRAMES_INSIDE_COMPOSITEFRAME, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SERVICE_CALENDAR_FRAME_DAYTYPE_NOT_ASSIGNED, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SERVICE_CALENDAR_FRAME_EMPTY_SERVICE_CALENDAR, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SERVICE_CALENDAR_FRAME_SERVICE_CALENDAR_FROMDATE, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SERVICE_CALENDAR_FRAME_SERVICE_CALENDAR_TODATE, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SERVICE_CALENDAR_FRAME_SERVICE_CALENDAR_FROMDATE_AFTER_TODATE, NOK);
		
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_MISSING_VERSION_ON_LOCAL_ELEMENTS, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_MISSING_REFERENCE_VERSION_TO_LOCAL_ELEMENTS, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_UNRESOLVED_REFERENCE_TO_COMMON_ELEMENTS, UNCHECK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_INVALID_ID_STRUCTURE, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_INVALID_ID_STRUCTURE_NAME, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_UNAPPROVED_CODESPACE_DEFINED, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_USE_OF_UNAPPROVED_CODESPACE, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_UNRESOLVED_EXTERNAL_REFERENCE, NOK);

		// Common file specific checkpoints (NOT CHECKED HERE)
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_DUPLICATE_IDS_ACROSS_LINE_FILES, ValidationReporter.RESULT.OK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_COMMON_TIMETABLE_FRAME, UNCHECK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_COMMON_SERVICE_FRAME_LINE, UNCHECK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_COMMON_SERVICE_FRAME_ROUTE, UNCHECK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_COMMON_SERVICE_FRAME_SERVICE_JOURNEY_PATTERN, UNCHECK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_NO_VALIDITYCONDITIONS_ON_FRAMES_OUTSIDE_COMPOSITEFRAME, UNCHECK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_DUPLICATE_IDS_ACROSS_LINE_AND_COMMON_FILES, UNCHECK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_MULTIPLE_FRAMES_OF_SAME_TYPE_WITHOUT_VALIDITYCONDITIONS, UNCHECK);
		
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_VALIDBETWEEN_INCOMPLETE, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_VALIDBETWEEN_TODATE_BEFORE_FROMDATE, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_AVAILABILITYCONDITION_INCOMPLETE, NOK);
		expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_AVAILABILITYCONDITION_TODATE_BEFORE_FROMDATE, NOK);

		
		// expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_SCHEMA_VALIDATION_ERROR,UNCHECK);
		// expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_UNKNOWN_PROFILE,UNCHECK);
		
		//expectedResults.put(AbstractNorwayNetexProfileValidator._1_NETEX_DUPLICATE_IDS_ACROSS_COMMON_FILES,UNCHECK);

		verifyAllCheckpointsCovered(vr, expectedResults);

	}

	private void verifyAllCheckpointsCovered(ValidationReport vr, Map<String, ValidationReporter.RESULT> expectedResults) {
		for (Entry<String, RESULT> checkpointResult : expectedResults.entrySet()) {
			assertReported(vr, checkpointResult.getKey(), checkpointResult.getValue());
		}

		boolean covered = true;
		for (CheckPointReport cp : vr.getCheckPoints()) {
			if (!expectedResults.containsKey(cp.getName())) {
				System.err.println(cp.getName());
				covered = false;
			}
		}

		Assert.assertTrue(covered, "Not all checkpoints tested for");

	}

	private void assertReported(ValidationReport vr, String checkpointKey, ValidationReporter.RESULT result) {
		boolean found = false;

		for (CheckPointReport cp : vr.getCheckPoints()) {
			if (cp.getName().equals(checkpointKey)) {
				found = true;
				if (cp.getState() != result) {
					for (CheckPointErrorReport error : vr.getCheckPointErrors()) {
						if (error.getTestId().equals(cp.getName())) {
							System.err.println(error);
						}
					}
				}
				Assert.assertEquals(cp.getState(), result, "Checkpoint " + checkpointKey + " has wrong result");
			}
		}

		Assert.assertTrue(found, "Checkpoint not found in report: " + checkpointKey);

	}

	protected Context createContext(NetexImporter importer) throws XPathFactoryConfigurationException {
		Context context = new Context();
		context.put(Constant.IMPORTER, importer);

		ActionReport actionReport = new ActionReport();
		context.put(Constant.REPORT, actionReport);

		ValidationData data = new ValidationData();
		context.put(Constant.VALIDATION_DATA, data);

		context.put(Constant.NETEX_XPATH_COMPILER, importer.getXPathCompiler());
		return context;
	}

}
