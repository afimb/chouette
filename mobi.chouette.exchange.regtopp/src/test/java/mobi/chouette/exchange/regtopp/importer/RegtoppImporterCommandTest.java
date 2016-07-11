package mobi.chouette.exchange.regtopp.importer;

import static mobi.chouette.exchange.report.ReportConstant.STATUS_OK;
import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.common.Context;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.dao.RouteDAO;
import mobi.chouette.dao.VehicleJourneyDAO;
import mobi.chouette.exchange.regtopp.DummyChecker;
import mobi.chouette.exchange.regtopp.JobDataTest;
import mobi.chouette.exchange.regtopp.RegtoppTestUtils;
import mobi.chouette.exchange.regtopp.importer.version.RegtoppVersion;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.DataStats;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;
import mobi.chouette.exchange.report.LineInfo;
import mobi.chouette.exchange.report.LineInfo.LINE_STATE;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.CheckPoint.RESULT;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.persistence.hibernate.ContextHolder;

public class RegtoppImporterCommandTest extends Arquillian implements mobi.chouette.common.Constant {

	private InitialContext initialContext;

	private void init() {
		if (initialContext == null) {
			try {
				initialContext = new InitialContext();

			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
	}

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

	@Deployment
	public static WebArchive createDeploymentOld() {

		WebArchive result;

		File[] files = Maven.resolver().loadPomFromFile("pom.xml").resolve("mobi.chouette:mobi.chouette.exchange.regtopp", "mobi.chouette:mobi.chouette.dao")
				.withTransitivity().asFile();

		result = ShrinkWrap.create(WebArchive.class, "test.war").addAsWebInfResource("postgres-ds.xml").addAsLibraries(files).addClass(DummyChecker.class)
				.addClass(RegtoppTestUtils.class).addClass(JobDataTest.class).addAsResource(EmptyAsset.INSTANCE, "beans.xml");
		return result;

	}

	protected Context initImportContext() {
		init();
		ContextHolder.setContext("chouette_gui"); // set tenant schema

		Context context = new Context();
		context.put(INITIAL_CONTEXT, initialContext);
		context.put(REPORT, new ActionReport());
		context.put(MAIN_VALIDATION_REPORT, new ValidationReport());
		RegtoppImportParameters configuration = new RegtoppImportParameters();
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
		jobData.setType("regtopp");
		context.put("testng", "true");
		context.put(OPTIMIZED, Boolean.FALSE);
		return context;

	}

	@Test
	public void importRegtoppKolumbusLine2306() throws Exception {
		// Prepare context
		Context context = initImportContext();

		File f = new File("src/test/data/lineextracts/kolumbus_line2306.zip");
		File dest = new File("target/referential/test");
		FileUtils.copyFileToDirectory(f, dest);
		JobDataTest job = (JobDataTest) context.get(JOB_DATA);
		job.setInputFilename(f.getName());

		RegtoppImporterCommand command = (RegtoppImporterCommand) CommandFactory.create(initialContext, RegtoppImporterCommand.class.getName());

		RegtoppImportParameters parameters = (RegtoppImportParameters) context.get(CONFIGURATION);
		parameters.setObjectIdPrefix("TST");
		parameters.setReferencesType("line");
		parameters.setNoSave(false);
		parameters.setVersion(RegtoppVersion.R12);
		parameters.setCoordinateProjection("EPSG:32632");
		parameters.setCharsetEncoding(RegtoppParameterGuesser.REGTOPP_DEFAULT_ENCODING);

		boolean result = command.execute(context);
		dumpReports(context);

		ActionReport report = (ActionReport) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);
		assertActionReport(report, STATUS_OK, 8, 1);
		assertStats(report.getStats(), 1, 3);
		assertLine(report.getLines().get(0), LineInfo.LINE_STATE.OK);
		assertValidationReport(validationReport, "VALIDATION_PROCEDEED", 14, 1, 40); // typo in chouette

		// line should be saved
		utx.begin();
		em.joinTransaction();
		Line line = lineDao.findByObjectId("TST:Line:2306");

		Assert.assertNotNull(line, "Line not found");
		Assert.assertNotNull(line.getNetwork(), "line must have a network");
		Assert.assertNotNull(line.getCompany(), "line must have a company");
		Assert.assertNotNull(line.getRoutes(), "line must have routes");
		assertEquals(line.getRoutes().size(), 3, "number of routes");
		Set<StopArea> bps = new HashSet<StopArea>();

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
					assertEquals(vj.getVehicleJourneyAtStops().size(), jp.getStopPoints().size(),
							" vehicleJourney should have correct vehicleJourneyAtStop count");
					numVehicleJourneys++;
				}
				numJourneyPatterns++;
			}
		}

		assertEquals(numJourneyPatterns, 3, "number of journeyPatterns");
		assertEquals(numVehicleJourneys, 12, "number of vehicleJourneys");
		assertEquals(numStopPoints, 137, "number of stopPoints in journeyPattern");
		assertEquals(bps.size(), 90, "number boarding positions");

		// Check opposite routes
		Route outbound = routeDao.findByObjectId("TST:Route:2306102");
		Route inbound = routeDao.findByObjectId("TST:Route:2306201");

		Assert.assertNotNull(outbound, "Outbound route not found");
		Assert.assertNotNull(inbound, "Inbound route not found");

		Assert.assertNotNull(outbound.getOppositeRoute(), "Oppsite route to outbound not found");
		Assert.assertNotNull(inbound.getOppositeRoute(), "Oppsite route to inbound not found");

		Assert.assertTrue(outbound.getOppositeRoute().equals(inbound), "Opposite route incorrect");

		utx.rollback();

		Assert.assertTrue(result, "Importer command execution failed: " + report.getFailure());
	}

	@Test
	public void importRegtoppKolumbusLine5560FootnotesByLine() throws Exception {
		// Prepare context
		Context context = initImportContext();

		File f = new File("src/test/data/lineextracts/kolumbus_line5560.zip");
		File dest = new File("target/referential/test");
		FileUtils.copyFileToDirectory(f, dest);
		JobDataTest job = (JobDataTest) context.get(JOB_DATA);
		job.setInputFilename(f.getName());

		RegtoppImporterCommand command = (RegtoppImporterCommand) CommandFactory.create(initialContext, RegtoppImporterCommand.class.getName());

		RegtoppImportParameters parameters = (RegtoppImportParameters) context.get(CONFIGURATION);
		parameters.setObjectIdPrefix("TST");
		parameters.setReferencesType("line");
		parameters.setNoSave(false);
		parameters.setVersion(RegtoppVersion.R12);
		parameters.setCoordinateProjection("EPSG:32632");
		parameters.setCharsetEncoding(RegtoppParameterGuesser.REGTOPP_DEFAULT_ENCODING);

		command.execute(context);
		dumpReports(context);

		ActionReport report = (ActionReport) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);
		assertActionReport(report, STATUS_OK, 8, 1);
		assertStats(report.getStats(), 1, 23);
		assertLine(report.getLines().get(0), LineInfo.LINE_STATE.OK);
		assertValidationReport(validationReport, "VALIDATION_PROCEDEED", 15, 0, 40); // typo in chouette

		utx.begin();
		em.joinTransaction();
		Line line = lineDao.findByObjectId("TST:Line:5560");
		assertEquals(line.getTransportModeName(), TransportModeNameEnum.Ferry);

		Assert.assertNotNull(line, "Line not found");

		// Check footnotes
		Assert.assertNotNull(line.getFootnotes(), "No footnote lists");
		assertEquals(line.getFootnotes().size(), 3, "number of line footnotes");

		utx.rollback();

	}

	@Test
	public void importRegtoppAtBLine0076FootnotesByVehicleJourney() throws Exception {
		// Prepare context
		Context context = initImportContext();

		File f = new File("src/test/data/lineextracts/atb_line0076.zip");
		File dest = new File("target/referential/test");
		FileUtils.copyFileToDirectory(f, dest);
		JobDataTest job = (JobDataTest) context.get(JOB_DATA);
		job.setInputFilename(f.getName());

		RegtoppImporterCommand command = (RegtoppImporterCommand) CommandFactory.create(initialContext, RegtoppImporterCommand.class.getName());

		RegtoppImportParameters parameters = (RegtoppImportParameters) context.get(CONFIGURATION);
		parameters.setObjectIdPrefix("TST");
		parameters.setReferencesType("line");
		parameters.setNoSave(false);
		parameters.setVersion(RegtoppVersion.R12);
		parameters.setCoordinateProjection("EPSG:32632");
		parameters.setCharsetEncoding(RegtoppParameterGuesser.REGTOPP_DEFAULT_ENCODING);

		command.execute(context);
		dumpReports(context);

		ActionReport report = (ActionReport) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);
		assertActionReport(report, STATUS_OK, 8, 1);
		assertStats(report.getStats(), 1, 9);
		assertLine(report.getLines().get(0), LineInfo.LINE_STATE.OK);
		assertValidationReport(validationReport, "VALIDATION_PROCEDEED", 14, 1, 40); // typo in chouette

		// line should be saved
		utx.begin();
		em.joinTransaction();
		Line line = lineDao.findByObjectId("TST:Line:0076");

		Assert.assertNotNull(line, "Line not found");

		// Check footnotes
		Assert.assertNotNull(line.getFootnotes(), "No footnote lists");
		assertEquals(line.getFootnotes().size(), 1, "number of line footnotes");

		// Find vehicle journey
		VehicleJourney vehicleJourney = vjDao.findByObjectId("TST:VehicleJourney:00760015");
		Assert.assertNotNull(vehicleJourney, "VehicleJourney not found");
		List<Footnote> footnotes = vehicleJourney.getFootnotes();
		Assert.assertNotNull(footnotes, "footnotes list null");
		assertEquals(footnotes.size(), 1, "Expected 1 footnote");
		assertEquals(footnotes.get(0).getCode(), "027");

		utx.rollback();

	}

	@Test
	public void importRegtoppAtbLineD2Nortura0096() throws Exception {
		// Prepare context
		Context context = initImportContext();

		File f = new File("src/test/data/lineextracts/atb_line0098.zip");
		File dest = new File("target/referential/test");
		FileUtils.copyFileToDirectory(f, dest);
		JobDataTest job = (JobDataTest) context.get(JOB_DATA);
		job.setInputFilename(f.getName());

		RegtoppImporterCommand command = (RegtoppImporterCommand) CommandFactory.create(initialContext, RegtoppImporterCommand.class.getName());

		RegtoppImportParameters parameters = (RegtoppImportParameters) context.get(CONFIGURATION);
		parameters.setObjectIdPrefix("TST");
		parameters.setReferencesType("line");
		parameters.setNoSave(false);
		parameters.setVersion(RegtoppVersion.R12);
		parameters.setCoordinateProjection("EPSG:32632");
		parameters.setCharsetEncoding(RegtoppParameterGuesser.REGTOPP_DEFAULT_ENCODING);

		boolean result = command.execute(context);
		dumpReports(context);

		ActionReport report = (ActionReport) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);
		assertActionReport(report, STATUS_OK, 8, 1);
		assertStats(report.getStats(), 1, 3);
		assertLine(report.getLines().get(0), LineInfo.LINE_STATE.OK);
		assertValidationReport(validationReport, "VALIDATION_PROCEDEED", 13, 2, 40); // typo in chouette

		// Reporter.log("report line :" + report.getLines().get(0).toString(), true);
		// Assert.assertEquals(report.getLines().get(0).getStatus(), LINE_STATE.OK, "line status");
		// RegtoppTestUtils.checkLine(context);
		//
		// Referential referential = (Referential) context.get(REFERENTIAL);
		// Assert.assertNotEquals(referential.getTimetables(),0, "timetables" );
		// Assert.assertNotEquals(referential.getSharedTimetables(),0, "shared timetables" );

		// line should be saved
		utx.begin();
		em.joinTransaction();
		Line line = lineDao.findByObjectId("TST:Line:0098");

		Assert.assertNotNull(line, "Line not found");
		Assert.assertNotNull(line.getNetwork(), "line must have a network");
		Assert.assertNotNull(line.getCompany(), "line must have a company");
		Assert.assertNotNull(line.getRoutes(), "line must have routes");
		assertEquals(line.getRoutes().size(), 3, "number of routes");
		Set<StopArea> bps = new HashSet<StopArea>();

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

				}
				Assert.assertNotEquals(jp.getVehicleJourneys().size(), 0, " journeyPattern should have VehicleJourneys");
				for (VehicleJourney vj : jp.getVehicleJourneys()) {
					Assert.assertNotEquals(vj.getTimetables().size(), 0, " vehicleJourney should have timetables");
					assertEquals(vj.getVehicleJourneyAtStops().size(), jp.getStopPoints().size(),
							" vehicleJourney should have correct vehicleJourneyAtStop count");
					numVehicleJourneys++;
				}
				for (StopPoint point : route.getStopPoints()) {
					Assert.assertNotNull(point.getContainedInStopArea(), "stoppoints must have StopAreas");
					bps.add(point.getContainedInStopArea());
				}
				numJourneyPatterns++;
			}
		}

		assertEquals(numJourneyPatterns, 3, "number of journeyPatterns");
		assertEquals(numVehicleJourneys, 3, "number of vehicleJourneys");
		assertEquals(numStopPoints, 63, "number of stopPoints in journeyPattern");
		assertEquals(bps.size(), 48, "number boarding positions");

		utx.rollback();

		if (!result) {
			System.out.println(ToStringBuilder.reflectionToString(report, ToStringStyle.MULTI_LINE_STYLE));
			System.out.println(validationReport);

		}

		Assert.assertTrue(result, "Importer command execution failed: " + report.getFailure());
	}

	@Test
	public void importRegtoppOpplandstrafikkLine5001StopAreaParent() throws Exception {
		// Prepare context
		Context context = initImportContext();

		File f = new File("src/test/data/lineextracts/ot_line5001.zip");
		File dest = new File("target/referential/test");
		FileUtils.copyFileToDirectory(f, dest);
		JobDataTest job = (JobDataTest) context.get(JOB_DATA);
		job.setInputFilename(f.getName());

		RegtoppImporterCommand command = (RegtoppImporterCommand) CommandFactory.create(initialContext, RegtoppImporterCommand.class.getName());

		RegtoppImportParameters parameters = (RegtoppImportParameters) context.get(CONFIGURATION);
		parameters.setObjectIdPrefix("TST");
		parameters.setReferencesType("line");
		parameters.setNoSave(false);
		parameters.setVersion(RegtoppVersion.R12N);
		parameters.setCoordinateProjection("EPSG:32632");
		parameters.setCharsetEncoding(RegtoppParameterGuesser.REGTOPP_DEFAULT_ENCODING);

		command.execute(context);
		dumpReports(context);

		ActionReport report = (ActionReport) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);

		assertActionReport(report, STATUS_OK, 8, 1);
		assertStats(report.getStats(), 1, 6);
		assertLine(report.getLines().get(0), LineInfo.LINE_STATE.OK);
		assertValidationReport(validationReport, "VALIDATION_PROCEDEED", 14, 1, 40); // typo in chouette

		// line should be saved
		utx.begin();
		em.joinTransaction();
		Line line = lineDao.findByObjectId("TST:Line:5001");

		Assert.assertNotNull(line, "Line not found");

		// Random journey pattern
		JourneyPattern journeyPattern = line.getRoutes().get(0).getJourneyPatterns().get(0);

		StopPoint departureStopPoint = journeyPattern.getDepartureStopPoint();

		StopArea containedInStopArea = departureStopPoint.getContainedInStopArea();
		Assert.assertNotNull(containedInStopArea, "No stop area on stop point");
		assertEquals(containedInStopArea.getAreaType(), ChouetteAreaEnum.BoardingPosition);
		StopArea parent = containedInStopArea.getParent();
		Assert.assertNotNull(parent);
		assertEquals(parent.getAreaType(), ChouetteAreaEnum.StopPlace);

		utx.rollback();

	}

	@Test
	public void importRegtoppTromsLine0002Regtopp11D() throws Exception {
		// Prepare context
		Context context = initImportContext();

		File f = new File("src/test/data/lineextracts/troms_line0002.zip");
		File dest = new File("target/referential/test");
		FileUtils.copyFileToDirectory(f, dest);
		JobDataTest job = (JobDataTest) context.get(JOB_DATA);
		job.setInputFilename(f.getName());

		RegtoppImporterCommand command = (RegtoppImporterCommand) CommandFactory.create(initialContext, RegtoppImporterCommand.class.getName());

		RegtoppImportParameters parameters = (RegtoppImportParameters) context.get(CONFIGURATION);
		parameters.setObjectIdPrefix("TST");
		parameters.setReferencesType("line");
		parameters.setNoSave(false);
		parameters.setVersion(RegtoppVersion.R11D);
		parameters.setCoordinateProjection("EPSG:32632");
		parameters.setCharsetEncoding(RegtoppParameterGuesser.REGTOPP_DEFAULT_ENCODING);

		command.execute(context);

		dumpReports(context);

		ActionReport report = (ActionReport) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);
		assertActionReport(report, STATUS_OK, 8, 1);
		assertStats(report.getStats(), 1, 6);
		assertLine(report.getLines().get(0), LineInfo.LINE_STATE.OK);
		// TODO line below must be verified
		assertValidationReport(validationReport, "VALIDATION_PROCEDEED", 12, 0, 43); // typo in chouette

		System.out.println(ToStringBuilder.reflectionToString(report, ToStringStyle.MULTI_LINE_STYLE, true));

		// line should be saved
		utx.begin();
		em.joinTransaction();
		Line line = lineDao.findByObjectId("TST:Line:0002");

		Assert.assertNotNull(line, "Line not found");
		Assert.assertNotNull(line.getNetwork(), "line must have a network");
		Assert.assertNotNull(line.getCompany(), "line must have a company");
		Assert.assertNotNull(line.getRoutes(), "line must have routes");
		assertEquals(line.getRoutes().size(), 6, "number of routes");

		Route route0002139 = routeDao.findByObjectId("TST:Route:0002139");
		Assert.assertNotNull(route0002139);
		List<JourneyPattern> journeyPatterns = route0002139.getJourneyPatterns();
		assertEquals(journeyPatterns.size(), 1);
		assertEquals(journeyPatterns.get(0).getStopPoints().size(), 5);
		assertEquals(journeyPatterns.get(0).getVehicleJourneys().size(), 18);

		utx.rollback();

	}

	@Test
	public void importRegtoppRuterLine0030Regtopp13A() throws Exception {
		// Prepare context
		Context context = initImportContext();

		File f = new File("src/test/data/lineextracts/ruter_line0030.zip");
		File dest = new File("target/referential/test");
		FileUtils.copyFileToDirectory(f, dest);
		JobDataTest job = (JobDataTest) context.get(JOB_DATA);
		job.setInputFilename(f.getName());

		RegtoppImporterCommand command = (RegtoppImporterCommand) CommandFactory.create(initialContext, RegtoppImporterCommand.class.getName());

		RegtoppImportParameters parameters = (RegtoppImportParameters) context.get(CONFIGURATION);
		parameters.setObjectIdPrefix("TST");
		parameters.setReferencesType("line");
		parameters.setNoSave(false);
		parameters.setVersion(RegtoppVersion.R13A);
		parameters.setCoordinateProjection("EPSG:32632");
		parameters.setCharsetEncoding(RegtoppParameterGuesser.REGTOPP_DEFAULT_ENCODING);

		command.execute(context);
		dumpReports(context);

		// line should be saved
		utx.begin();
		em.joinTransaction();
		Line line = lineDao.findByObjectId("TST:Line:0030");

		// 1119 Vehicle Journeys
		// 120 stopPlace
		// 210 boardingPosition
		// 21 JourneyPatterns / Routes
		// 1064 StopPoints

		Assert.assertNotNull(line, "Line not found");
		Assert.assertNotNull(line.getNetwork(), "line must have a network");
		Assert.assertNotNull(line.getCompany(), "line must have a company");
		Assert.assertNotNull(line.getRoutes(), "line must have routes");

		int numVehicleJourneys = 0;
		int numStopPoints = 0;

		Set<StopArea> boardingPositions = new HashSet<StopArea>();

		for (Route r : line.getRoutes()) {
			numStopPoints += r.getStopPoints().size();
			for (JourneyPattern jp : r.getJourneyPatterns()) {
				numVehicleJourneys += jp.getVehicleJourneys().size();

				for (StopPoint p : jp.getStopPoints()) {
					StopArea stopArea = p.getContainedInStopArea();

					Assert.assertEquals(stopArea.getAreaType(), ChouetteAreaEnum.BoardingPosition, "stoppoint does not refer to boarding position");
					boardingPositions.add(stopArea);
					Assert.assertNotNull(stopArea.getParent(), "parent is null");
					Assert.assertEquals(stopArea.getParent().getAreaType(), ChouetteAreaEnum.StopPlace,
							"stoppoint -> stoparea -> parent is not of type stopPlace");

				}
			}
		}

		Assert.assertEquals(line.getRoutes().size(), 6, "routes");
		Assert.assertEquals(numVehicleJourneys, 453, "vehicleJourneys");
		Assert.assertEquals(boardingPositions.size(), 66, "numBoardingPositions");
		Assert.assertEquals(numStopPoints, 151, "numStopPoints");

		// Route route0002139 = routeDao.findByObjectId("TST:Route:0002139");
		// Assert.assertNotNull(route0002139);
		// List<JourneyPattern> journeyPatterns = route0002139.getJourneyPatterns();
		// Assert.assertEquals(journeyPatterns.size(), 1);
		// Assert.assertEquals(journeyPatterns.get(0).getStopPoints().size(), 5);
		// Assert.assertEquals(journeyPatterns.get(0).getVehicleJourneys().size(), 2);

		utx.rollback();

	}

	private void assertLine(LineInfo lineInfo, LineInfo.LINE_STATE lineState) {
		assertEquals(lineInfo.getStatus(), lineState);
	}

	private void assertValidationReportOk(ValidationReport validationReport, String result) {
		validationReport.checkResult();
		assertEquals(validationReport.getResult(), result);
		for (CheckPoint checkPoint : validationReport.getCheckPoints()) {
			assertEquals(checkPoint.getState(), CheckPoint.RESULT.OK);
		}
	}

	private void assertValidationReport(ValidationReport validationReport, String result, int expectedOk, int expectedNok, int expectedUncheck) {
		validationReport.checkResult();
		assertEquals(validationReport.getResult(), result);
		int actualOk = 0;
		int actualNok = 0;
		int actualUncheck = 0;
		for (CheckPoint checkPoint : validationReport.getCheckPoints()) {
			if (checkPoint.getState().equals(CheckPoint.RESULT.OK)) {
				actualOk++;
			} else if (checkPoint.getState().equals(CheckPoint.RESULT.NOK)) {
				actualNok++;
			} else if (checkPoint.getState().equals(CheckPoint.RESULT.UNCHECK)) {
				actualUncheck++;
			}
		}
		assertEquals(actualOk, expectedOk);
		assertEquals(actualNok, expectedNok);
		assertEquals(actualUncheck, expectedUncheck);
	}

	private void assertStats(DataStats stats, int lines, int routes) {
		assertEquals(stats.getLineCount(), lines, "lines reported in stats");
		assertEquals(stats.getRouteCount(), routes, "routes reported in stats");
	}

	private void assertActionReport(ActionReport report, String status, int files, int lines) {
		assertEquals(report.getResult(), status, "result");
		assertEquals(report.getFiles().size(), files, "file reported");
		assertEquals(report.getLines().size(), lines, "line reported");
	}

	public void dumpReports(Context context) {
		ActionReport actionReport = (ActionReport) context.get(REPORT);
	
		ReflectionToStringBuilder builder = new ReflectionToStringBuilder(actionReport, ToStringStyle.MULTI_LINE_STYLE, null, null, true, true);
		builder.setExcludeFieldNames(new String[] { "files", "lines" });
		System.out.println(builder.toString());
		for (FileInfo object : actionReport.getFiles()) {
			String toString = ToStringBuilder.reflectionToString(object, ToStringStyle.SHORT_PREFIX_STYLE, true);
			if(object.getStatus() == FILE_STATE.ERROR) {
				logError(toString);
			} else {
				logOk(System.out,toString);
			}
		}
		for (LineInfo object : actionReport.getLines()) {
			String toString = ToStringBuilder.reflectionToString(object, ToStringStyle.SHORT_PREFIX_STYLE, true);
			if(object.getStatus() == LINE_STATE.ERROR) {
				logError(toString);
			} else {
				logOk(System.out,toString);
			}
		}

		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);
		ReflectionToStringBuilder validationBuilder = new ReflectionToStringBuilder(validationReport, ToStringStyle.SHORT_PREFIX_STYLE, null, null, true, true);
		validationBuilder.setExcludeFieldNames(new String[] { "checkPoints" });
		System.out.println(validationBuilder.toString());
	
		for (CheckPoint object : validationReport.getCheckPoints()) {
			ReflectionToStringBuilder checkpointBuilder = new ReflectionToStringBuilder(object, ToStringStyle.SHORT_PREFIX_STYLE, null, null, true, true);
			checkpointBuilder.setExcludeFieldNames(new String[] { "details" });
			String checkpointAsString = checkpointBuilder.toString();
		
			List<String> lines = new ArrayList<String>();
			lines.add(checkpointAsString);
			for(Detail d : object.getDetails()) {
				lines.add("   "+ToStringBuilder.reflectionToString(d, ToStringStyle.SHORT_PREFIX_STYLE, true));
			}
			
			if(object.getState() == RESULT.NOK) {
				logError(lines.toArray(new String[0]));
			} else {
				logOk(System.out,lines.toArray(new String[0]));
			}
		}
	}
	
	private void logError(String... data) {
		logOk(System.err,data);
	}
	
	private void logOk(PrintStream stream, String... data) {
		for(String d : data) {
			stream.println(d);
		}
		
	}

}
