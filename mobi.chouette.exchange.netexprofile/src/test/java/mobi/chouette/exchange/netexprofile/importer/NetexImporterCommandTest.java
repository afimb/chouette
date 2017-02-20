package mobi.chouette.exchange.netexprofile.importer;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.dao.RouteDAO;
import mobi.chouette.dao.VehicleJourneyDAO;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.DummyChecker;
import mobi.chouette.exchange.netexprofile.JobDataTest;
import mobi.chouette.exchange.netexprofile.NetexTestUtils;
import mobi.chouette.exchange.report.*;
import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.*;
import mobi.chouette.model.util.Referential;
import mobi.chouette.persistence.hibernate.ContextHolder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

import static org.testng.Assert.assertEquals;

@Log4j
public class NetexImporterCommandTest extends Arquillian implements Constant, ReportConstant {

	protected static InitialContext initialContext;

	@EJB
	LineDAO lineDao;

	@EJB
	RouteDAO routeDao;

	@EJB
	VehicleJourneyDAO vjDao;

	@PersistenceContext(unitName = "referential")
	EntityManager em;

	@Inject
	UserTransaction utx;

	// @Deployment
	public static WebArchive createDeploymentOld() {

		WebArchive result;

		File[] files = Maven.resolver().loadPomFromFile("pom.xml").resolve("mobi.chouette:mobi.chouette.exchange.netexprofile:3.4.0-SNAPSHOT",
				"mobi.chouette:mobi.chouette.dao:3.4.0-SNAPSHOT", "mobi.chouette:mobi.chouette.exchange:3.4.0-SNAPSHOT").withTransitivity().asFile();

		result = ShrinkWrap.create(WebArchive.class, "test.war").addAsWebInfResource("postgres-ds.xml").addAsLibraries(files).addClass(DummyChecker.class)
				.addClass(NetexTestUtils.class).addClass(JobDataTest.class).addAsResource(EmptyAsset.INSTANCE, "beans.xml");
		return result;

	}

	@Deployment
	public static EnterpriseArchive createDeployment() {
		EnterpriseArchive result;
		File[] files = Maven.resolver().loadPomFromFile("pom.xml").resolve("mobi.chouette:mobi.chouette.exchange.netexprofile").withTransitivity().asFile();

		List<File> jars = new ArrayList<>();
		List<JavaArchive> modules = new ArrayList<>();

		for (File file : files) {
			if (file.getName().startsWith("mobi.chouette.exchange")) {
				String name = file.getName().split("\\-")[0] + ".jar";
				JavaArchive archive = ShrinkWrap.create(ZipImporter.class, name).importFrom(file).as(JavaArchive.class);
				modules.add(archive);
			} else {
				jars.add(file);
			}
		}

		File[] filesDao = Maven.resolver().loadPomFromFile("pom.xml").resolve("mobi.chouette:mobi.chouette.dao").withTransitivity().asFile();

		if (filesDao.length == 0) {
			throw new NullPointerException("no dao");
		}

		for (File file : filesDao) {
			if (file.getName().startsWith("mobi.chouette.dao")) {
				String name = file.getName().split("\\-")[0] + ".jar";
				JavaArchive archive = ShrinkWrap.create(ZipImporter.class, name).importFrom(file).as(JavaArchive.class);
				modules.add(archive);

				if (!modules.contains(archive))
					modules.add(archive);
			} else {
				if (!jars.contains(file))
					jars.add(file);
			}
		}

		final WebArchive testWar = ShrinkWrap.create(WebArchive.class, "test.war").addAsWebInfResource("postgres-ds.xml")
				.addClass(NetexImporterCommandTest.class).addClass(NetexTestUtils.class).addClass(DummyChecker.class).addClass(JobDataTest.class);

		result = ShrinkWrap.create(EnterpriseArchive.class, "test.ear").addAsLibraries(jars.toArray(new File[0]))
				.addAsModules(modules.toArray(new JavaArchive[0])).addAsModule(testWar).addAsResource(EmptyAsset.INSTANCE, "beans.xml");
		return result;
	}

	protected void init() {
		Locale.setDefault(Locale.ENGLISH);
		if (initialContext == null) {
			try {
				initialContext = new InitialContext();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
	}

	protected Context initImportContext() {
		init();
		ContextHolder.setContext("chouette_gui"); // set tenant schema

		Context context = new Context();
		context.put(INITIAL_CONTEXT, initialContext);
		context.put(REPORT, new ActionReport());
		context.put(VALIDATION_REPORT, new ValidationReport());
		NetexprofileImportParameters configuration = new NetexprofileImportParameters();
		context.put(CONFIGURATION, configuration);
		configuration.setName("name");
		configuration.setUserName("userName");
		configuration.setNoSave(true);
		configuration.setCleanRepository(true);
		configuration.setOrganisationName("organisation");
		configuration.setReferentialName("test");
		JobDataTest jobData = new JobDataTest();
		context.put(JOB_DATA, jobData);
		jobData.setPathName("target/referential/test");
		File f = new File("target/referential/test");
		if (f.exists())
			try {
				FileUtils.deleteDirectory(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		f.mkdirs();
		jobData.setReferential("chouette_gui");
		jobData.setAction(IMPORTER);
		jobData.setType("netexprofile");
		context.put("testng", "true");
		context.put(OPTIMIZED, Boolean.FALSE);
		return context;

	}

	@Test(enabled = true, groups = { "ImportLine" }, description = "Import Plugin should import file")
	public void verifyImportLine() throws Exception {
		Context context = initImportContext();
		NetexTestUtils.copyFile("C_NETEX_1.xml");

		JobDataTest jobData = (JobDataTest) context.get(JOB_DATA);
		jobData.setInputFilename("C_NETEX_1.xml");

		NetexprofileImporterCommand command = (NetexprofileImporterCommand) CommandFactory.create(initialContext, NetexprofileImporterCommand.class.getName());

		NetexprofileImportParameters configuration = (NetexprofileImportParameters) context.get(CONFIGURATION);
		configuration.setNoSave(false);
		configuration.setCleanRepository(true);
		configuration.setValidCodespaces("AVI,http://www.rutebanken.org/ns/avi");

		try {
			command.execute(context);
		} catch (Exception ex) {
			log.error("test failed", ex);
			throw ex;
		}

		// dumpReports(context);

		ActionReport report = (ActionReport) context.get(REPORT);
		Reporter.log("report :" + report.toString(), true);
		dumpReports(context);

		Assert.assertEquals(report.getResult(), STATUS_OK, "fileValidationResult");
		Assert.assertEquals(report.getFiles().size(), 1, "file reported");
		Assert.assertNotNull(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE), "line reported");
		Assert.assertEquals(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports().size(), 1, "line reported");

		for (ObjectReport info : report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports()) {
			Reporter.log("report line :" + info.toString(), true);
			Assert.assertEquals(info.getStatus(), ActionReporter.OBJECT_STATE.OK, "line status");
		}

		NetexTestUtils.checkLine(context);

		Referential referential = (Referential) context.get(REFERENTIAL);
		Assert.assertNotEquals(referential.getTimetables(), 0, "timetables");
		Assert.assertNotEquals(referential.getSharedTimetables(), 0, "shared timetables");

		// line should be saved
		utx.begin();
		em.joinTransaction();
		Line line = lineDao.findByObjectId("AVI:Line:WF-TRD-MOL");
		Assert.assertNotNull(line, "Line not found");

		utx.rollback();
	}

	@Test(enabled = true)
	public void verifyImportSingleLineWithCommonDataAvinor() throws Exception {
		Context context = initImportContext();
		NetexprofileImporterCommand command = (NetexprofileImporterCommand) CommandFactory.create(
				initialContext, NetexprofileImporterCommand.class.getName());

		NetexTestUtils.copyFile("avinor_single_line_with_commondata.zip");

		JobDataTest jobData = (JobDataTest) context.get(JOB_DATA);
		jobData.setInputFilename("avinor_single_line_with_commondata.zip");

		NetexprofileImportParameters configuration = (NetexprofileImportParameters) context.get(CONFIGURATION);
		configuration.setNoSave(false);
		configuration.setCleanRepository(true);
		configuration.setValidCodespaces("AVI,http://www.rutebanken.org/ns/avi");

		boolean result;
		try {
			result = command.execute(context);
		} catch (Exception ex) {
			log.error("test failed", ex);
			throw ex;
		}

		ActionReport report = (ActionReport) context.get(REPORT);

		dumpReports(context);

		Assert.assertEquals(report.getResult(), STATUS_OK, "result");
		Assert.assertEquals(report.getFiles().size(), 2, "files reported");
		Assert.assertNotNull(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE), "lines reported");
		Assert.assertEquals(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports().size(), 1, "lines reported");

		for (ObjectReport info : report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports()) {
			Reporter.log("report lines :" + info.toString(), true);
			Assert.assertEquals(info.getStatus(), ActionReporter.OBJECT_STATE.OK, "lines status");
		}

		// lines should be saved
		utx.begin();
		em.joinTransaction();

		Line line = lineDao.findByObjectId("AVI:Line:WF_TRD-MOL");

		Assert.assertNotNull(line, "Line not found");
		Assert.assertNotNull(line.getNetwork(), "line must have a network");
		Assert.assertNotNull(line.getCompany(), "line must have a company");
		Assert.assertNotNull(line.getRoutes(), "line must have routes");
		assertEquals(line.getRoutes().size(), 2, "number of routes");

		Set<StopArea> bps = new HashSet<>();

		int numStopPoints = 0;
		int numVehicleJourneys = 0;
		int numJourneyPatterns = 0;

		for (Route route : line.getRoutes()) {
			Assert.assertNotNull(route.getName(), "No route name");
			Assert.assertNotEquals(route.getJourneyPatterns().size(), 0, "line routes must have journeyPattens");

			for (JourneyPattern jp : route.getJourneyPatterns()) {
				Assert.assertNotNull(jp.getName(), "No journeypattern name");
				Assert.assertNotEquals(jp.getStopPoints().size(), 0, "line journeyPattens must have stoppoints");

				for (StopPoint point : jp.getStopPoints()) {
					numStopPoints++;
					Assert.assertNotNull(point.getContainedInStopArea(), "stoppoints must have StopAreas");
					bps.add(point.getContainedInStopArea());
					Assert.assertNotNull(point.getForAlighting(), "no alighting info StopPoint=" + point);
					Assert.assertNotNull(point.getForBoarding(), "no boarding info StopPoint=" + point);
				}

				Assert.assertNotEquals(jp.getVehicleJourneys().size(), 0, " journeyPattern should have VehicleJourneys");

				for (VehicleJourney vj : jp.getVehicleJourneys()) {
					Assert.assertNotEquals(vj.getTimetables().size(), 0, " vehicleJourney should have timetables");
					assertEquals(vj.getVehicleJourneyAtStops().size(), jp.getStopPoints().size(), " vehicleJourney should have correct vehicleJourneyAtStop count");
					List<Timetable> timetables = vj.getTimetables();
					for (Timetable timetable : timetables) {
						Assert.assertNotNull(timetable.getStartOfPeriod());
						Assert.assertNotNull(timetable.getEndOfPeriod());
					}

					numVehicleJourneys++;
				}
				numJourneyPatterns++;
			}
		}

		assertEquals(numJourneyPatterns, 2, "number of journeyPatterns");
		assertEquals(numVehicleJourneys, 3, "number of vehicleJourneys");
		assertEquals(numStopPoints, 4, "number of stopPoints in journeyPattern");
		assertEquals(bps.size(), 2, "number boarding positions");

		utx.rollback();
		Assert.assertTrue(result, "Importer command execution failed: " + report.getFailure());
	}

	@Test(enabled = true)
	public void verifyImportMultipleLinesWithCommonDataAvinor() throws Exception {
		Context context = initImportContext();
		NetexprofileImporterCommand command = (NetexprofileImporterCommand) CommandFactory.create(
				initialContext, NetexprofileImporterCommand.class.getName());

		NetexTestUtils.copyFile("avinor_multiple_lines_with_commondata.zip");

		JobDataTest jobData = (JobDataTest) context.get(JOB_DATA);
		jobData.setInputFilename("avinor_multiple_lines_with_commondata.zip");

		NetexprofileImportParameters configuration = (NetexprofileImportParameters) context.get(CONFIGURATION);
		configuration.setNoSave(false);
		configuration.setCleanRepository(true);
		configuration.setValidCodespaces("AVI,http://www.rutebanken.org/ns/avi");

		boolean result;
		try {
			result = command.execute(context);
		} catch (Exception ex) {
			log.error("test failed", ex);
			throw ex;
		}

		ActionReport report = (ActionReport) context.get(REPORT);

		dumpReports(context);

		Assert.assertEquals(report.getResult(), STATUS_OK, "result");
		Assert.assertEquals(report.getFiles().size(), 4, "files reported");
		Assert.assertNotNull(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE), "lines reported");
		Assert.assertEquals(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports().size(), 3, "lines reported");

		for (ObjectReport info : report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports()) {
			Reporter.log("report lines :" + info.toString(), true);
			Assert.assertEquals(info.getStatus(), ActionReporter.OBJECT_STATE.OK, "lines status");
		}

		// lines should be saved
		utx.begin();
		em.joinTransaction();

		Collection<String> objectIds = Arrays.asList("AVI:Line:DY_TRD-TOS", "AVI:Line:SK_BOO-TOS", "AVI:Line:WF_SVG-FRO");
		List<Line> lines = lineDao.findByObjectId(objectIds);

		for (Line line : lines) {
			Assert.assertNotNull(line, "Line not found");
			Assert.assertNotNull(line.getNetwork(), "line must have a network");
			Assert.assertNotNull(line.getCompany(), "line must have a company");
			Assert.assertNotNull(line.getRoutes(), "line must have routes");

			if (line.getObjectId().equals("AVI:Line:DY_TRD-TOS")) {
				assertEquals(line.getRoutes().size(), 2, "number of routes");
			} else {
				assertEquals(line.getRoutes().size(), 1, "number of routes");
			}

			Set<StopArea> bps = new HashSet<>();

			int numStopPoints = 0;
			int numVehicleJourneys = 0;
			int numJourneyPatterns = 0;

			for (Route route : line.getRoutes()) {
				Assert.assertNotNull(route.getName(), "No route name");
				Assert.assertNotEquals(route.getJourneyPatterns().size(), 0, "line routes must have journeyPattens");

				for (JourneyPattern jp : route.getJourneyPatterns()) {
					Assert.assertNotNull(jp.getName(), "No journeypattern name");
					Assert.assertNotEquals(jp.getStopPoints().size(), 0, "line journeyPattens must have stoppoints");

					for (StopPoint point : jp.getStopPoints()) {
						numStopPoints++;
						Assert.assertNotNull(point.getContainedInStopArea(), "stoppoints must have StopAreas");
						bps.add(point.getContainedInStopArea());
						Assert.assertNotNull(point.getForAlighting(), "no alighting info StopPoint=" + point);
						Assert.assertNotNull(point.getForBoarding(), "no boarding info StopPoint=" + point);
					}

					Assert.assertNotEquals(jp.getVehicleJourneys().size(), 0, " journeyPattern should have VehicleJourneys");

					for (VehicleJourney vj : jp.getVehicleJourneys()) {
						Assert.assertNotEquals(vj.getTimetables().size(), 0, " vehicleJourney should have timetables");
						assertEquals(vj.getVehicleJourneyAtStops().size(), jp.getStopPoints().size(), " vehicleJourney should have correct vehicleJourneyAtStop count");
						List<Timetable> timetables = vj.getTimetables();
						for(Timetable timetable : timetables) {
							Assert.assertNotNull(timetable.getStartOfPeriod());
							Assert.assertNotNull(timetable.getEndOfPeriod());
						}

						numVehicleJourneys++;
					}
					numJourneyPatterns++;
				}
			}

			if (line.getObjectId().equals("AVI:Line:DY_TRD-TOS")) {
				assertEquals(numJourneyPatterns, 2, "number of journeyPatterns");
				assertEquals(numVehicleJourneys, 2, "number of vehicleJourneys");
				assertEquals(numStopPoints, 4, "number of stopPoints in journeyPattern");
				assertEquals(bps.size(), 2, "number boarding positions");
			} else {
				assertEquals(numJourneyPatterns, 1, "number of journeyPatterns");
				assertEquals(numVehicleJourneys, 1, "number of vehicleJourneys");
				assertEquals(numStopPoints, 2, "number of stopPoints in journeyPattern");
				assertEquals(bps.size(), 2, "number boarding positions");
			}
		}

		utx.rollback();
		Assert.assertTrue(result, "Importer command execution failed: " + report.getFailure());
	}

	@Test(enabled = false)
	public void verifyImportSingleLineWithCommonDataRuter() throws Exception {
		Context context = initImportContext();
		NetexprofileImporterCommand command = (NetexprofileImporterCommand) CommandFactory.create(
				initialContext, NetexprofileImporterCommand.class.getName());

		NetexTestUtils.copyFile("ruter_single_line_295_with_commondata.zip");

		JobDataTest jobData = (JobDataTest) context.get(JOB_DATA);
		jobData.setInputFilename("ruter_single_line_295_with_commondata.zip");

		NetexprofileImportParameters configuration = (NetexprofileImportParameters) context.get(CONFIGURATION);
		configuration.setNoSave(false);
		configuration.setCleanRepository(true);
		configuration.setValidCodespaces("RUT,http://www.rutebanken.org/ns/ruter");

		boolean result;
		try {
			result = command.execute(context);
		} catch (Exception ex) {
			log.error("test failed", ex);
			throw ex;
		}

		ActionReport report = (ActionReport) context.get(REPORT);

		dumpReports(context);

		Assert.assertEquals(report.getResult(), STATUS_OK, "result");
		Assert.assertEquals(report.getFiles().size(), 2, "files reported");
		Assert.assertNotNull(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE), "lines reported");
		Assert.assertEquals(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports().size(), 1, "lines reported");

		for (ObjectReport info : report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports()) {
			Reporter.log("report lines :" + info.toString(), true);
			Assert.assertEquals(info.getStatus(), ActionReporter.OBJECT_STATE.OK, "lines status");
		}

		utx.begin();
		em.joinTransaction();

		Line line = lineDao.findByObjectId("AVI:Line:WF_TRD-MOL");
		Assert.assertNotNull(line, "Line not found");

		utx.rollback();

		Assert.assertTrue(result, "Importer command execution failed: " + report.getFailure());
	}

	private void assertGlobalLines(ActionReport report, int lines) {
		assertEquals(report.findObjectReport("global", ActionReporter.OBJECT_TYPE.LINE).getStats().get(ActionReporter.OBJECT_TYPE.LINE).intValue(), lines,
				"lines reported");
	}

	private void assertLine(ActionReport report, ActionReporter.OBJECT_STATE state) {
		assertEquals(report.getObjects().get(ActionReporter.OBJECT_TYPE.LINE).getStatus(), state);
	}

	private void assertStats(ActionReport report, int lines, int routes) {
		assertGlobalLines(report, lines);
		assertGlobalRoutes(report, routes);
	}

	private void assertGlobalRoutes(ActionReport report, int routes) {
		assertEquals(report.findObjectReport("global", ActionReporter.OBJECT_TYPE.ROUTE).getStats().get(ActionReporter.OBJECT_TYPE.ROUTE).intValue(), routes,
				"routes reported");
	}

	private void assertFiles(ActionReport report, int files) {
		assertEquals(report.getFiles().size(), files, "files reported");
	}

	private void assertStatus(ActionReport report, String status) {
		assertEquals(report.getResult(), status, "fileValidationResult");
	}

	private void assertValidationReport(ValidationReport validationReport, int expectedOk, int expectedNok, int expectedUncheck) {
		assertWarningOrOk(validationReport.getResult());
		int actualOk = 0;
		int actualNok = 0;
		int actualUncheck = 0;
		for (CheckPointReport checkPointReport : validationReport.getCheckPoints()) {
			if (checkPointReport.getState().equals(ValidationReporter.RESULT.OK)) {
				actualOk++;
			} else if (checkPointReport.getState().equals(ValidationReporter.RESULT.NOK)) {
				actualNok++;
			} else if (checkPointReport.getState().equals(ValidationReporter.RESULT.UNCHECK)) {
				actualUncheck++;
			}
		}
		assertEquals(actualOk, expectedOk, "ok");
		assertEquals(actualNok, expectedNok, "nok");
		assertEquals(actualUncheck, expectedUncheck, "uncheck");
	}

	private void assertWarningOrOk(ValidationReporter.VALIDATION_RESULT result) {
		if (result.equals(ValidationReporter.VALIDATION_RESULT.ERROR) || result.equals(ValidationReporter.VALIDATION_RESULT.NO_PROCESSING)) {
			throw new AssertionError("Validation failed. Got " + result);
		}
	}

	private void assertActionReport(ActionReport report, String status, int files, int lines) {
		assertEquals(report.getResult(), status, "fileValidationResult");
		assertEquals(report.getFiles().size(), files, "file reported");
		assertEquals(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports().size(), lines, "line reported");
	}

	public void dumpReports(Context context) {
		ActionReport actionReport = (ActionReport) context.get(REPORT);

		ReflectionToStringBuilder builder = new ReflectionToStringBuilder(actionReport, ToStringStyle.MULTI_LINE_STYLE, null, null, true, true);
		builder.setExcludeFieldNames(new String[] { "files", "lines" });
		System.out.println(builder.toString());
		for (FileReport fileReport : actionReport.getFiles()) {
			String toString = ToStringBuilder.reflectionToString(fileReport, ToStringStyle.SHORT_PREFIX_STYLE, true);
			if (fileReport.getStatus() == ActionReporter.FILE_STATE.ERROR) {
				logError(toString);
			} else {
				logOk(System.out, toString);
			}
		}
		Collection<ObjectCollectionReport> collections = actionReport.getCollections().values();

		for (ObjectCollectionReport report : collections) {
			for (ObjectReport object : report.getObjectReports()) {
				String toString = ToStringBuilder.reflectionToString(object, ToStringStyle.SHORT_PREFIX_STYLE, true);
				if (object.getStatus() == ActionReporter.OBJECT_STATE.ERROR) {
					logError(toString);
				} else {
					logOk(System.out, toString);
				}
			}
		}

		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		ReflectionToStringBuilder validationBuilder = new ReflectionToStringBuilder(validationReport, ToStringStyle.SHORT_PREFIX_STYLE, null, null, true, true);
		validationBuilder.setExcludeFieldNames(new String[] { "checkPoints" });
		System.out.println(validationBuilder.toString());

		for (CheckPointReport object : validationReport.getCheckPoints()) {
			ReflectionToStringBuilder checkpointBuilder = new ReflectionToStringBuilder(object, ToStringStyle.SHORT_PREFIX_STYLE, null, null, true, true);
			checkpointBuilder.setExcludeFieldNames(new String[] { "details" });
			String checkpointAsString = checkpointBuilder.toString();

			List<String> lines = new ArrayList<String>();
			lines.add(checkpointAsString);

			if (object.getState() == ValidationReporter.RESULT.NOK) {
				logError(lines.toArray(new String[0]));
			} else {
				logOk(System.out, lines.toArray(new String[0]));
			}
		}
	}

	private void logError(String... data) {
		logOk(System.err, data);
	}

	private void logOk(PrintStream stream, String... data) {
		for (String d : data) {
			stream.println(d);
		}

	}

}
