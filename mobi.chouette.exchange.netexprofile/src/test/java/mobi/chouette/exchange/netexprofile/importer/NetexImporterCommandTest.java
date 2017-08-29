package mobi.chouette.exchange.netexprofile.importer;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.CodespaceDAO;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.dao.VehicleJourneyDAO;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.DummyChecker;
import mobi.chouette.exchange.netexprofile.JobDataTest;
import mobi.chouette.exchange.netexprofile.NetexTestUtils;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.FileReport;
import mobi.chouette.exchange.report.ObjectCollectionReport;
import mobi.chouette.exchange.report.ObjectReport;
import mobi.chouette.exchange.report.ReportConstant;
import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.Codespace;
import mobi.chouette.model.DestinationDisplay;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.Interchange;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.StopAreaImportModeEnum;
import mobi.chouette.model.type.StopAreaTypeEnum;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.type.TransportSubModeNameEnum;
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
import org.joda.time.Duration;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import static mobi.chouette.exchange.netexprofile.NetexTestUtils.createCodespace;
import static org.testng.Assert.*;

@Log4j
public class NetexImporterCommandTest extends Arquillian implements Constant, ReportConstant {

	protected static InitialContext initialContext;

	@EJB
	private LineDAO lineDao;

	@EJB
	private VehicleJourneyDAO vehicleJourneyDao;

	@EJB
	private CodespaceDAO codespaceDao;

	@EJB
	private StopAreaDAO stopAreaDAO;

	@PersistenceContext(unitName = "referential")
	private EntityManager em;

	@Inject
	UserTransaction utx;

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

	private void clearCodespaceRecords() throws Exception {
		utx.begin();
		em.joinTransaction();
		log.info("Dumping old codespace records...");
		codespaceDao.deleteAll();
		codespaceDao.flush();
		utx.commit();
	}

	private void insertCodespaceRecords(List<Codespace> codespaces) throws Exception {
		utx.begin();
		em.joinTransaction();
		log.info("Inserting codespace records...");

		for (Codespace codespace : codespaces) {
			codespaceDao.create(codespace);
		}

		codespaceDao.flush();
		utx.commit();
		codespaceDao.clear();
	}

	@Test(groups = { "ImportLine" }, description = "Import Plugin should import file")
	public void codespaceReadTest() throws Exception {
		Context context = initImportContext();
		clearCodespaceRecords();

		insertCodespaceRecords(Arrays.asList(
				createCodespace(null, "NSR", "http://www.rutebanken.org/ns/nsr"),
				createCodespace(null, "AVI", "http://www.rutebanken.org/ns/avi"))
		);

		utx.begin();
		em.joinTransaction();

		List<Codespace> codespaces = codespaceDao.findAll();
		assertNotNull(codespaces, "Codespace list is null");
		assertEquals(codespaces.size(), 2, "No available codespaces");

		utx.rollback();
	}

	@Test(groups = { "ImportLine" }, description = "Import Plugin should import file")
	public void verifyImportLine() throws Exception {
		stopAreaDAO.truncate();
		StopArea existingStopArea=createExistingStopArea();

		importLine(StopAreaImportModeEnum.CREATE_OR_UPDATE);

		utx.begin();
		em.joinTransaction();

		Line line = lineDao.findByObjectId("AVI:Line:WF_TRD-MOL");
		assertNotNull(line, "Line not found");

		for (Route route : line.getRoutes()) {
			for (StopPoint stopPoint : route.getStopPoints()) {
				if (stopPoint.getObjectId().equals("AVI:StopPoint:1263628002")) {
					assertEquals(stopPoint.getContainedInStopArea().getObjectId(), existingStopArea.getObjectId());
					assertEquals(stopPoint.getContainedInStopArea().getName(), "Molde Lufthavn", "Expected name of existing stop area to be updated by import data");

					StopArea stopAreaParent = stopPoint.getContainedInStopArea().getParent();
					Assert.assertEquals(stopAreaParent.getStopAreaType(), StopAreaTypeEnum.Airport);
					Assert.assertEquals(stopAreaParent.getTransportModeName(), TransportModeNameEnum.Air);
					Assert.assertEquals(stopAreaParent.getTransportSubMode(), TransportSubModeNameEnum.InternationalFlight);
				} else {
					assertNotNull(stopPoint.getContainedInStopArea(), "Expected not existing stop area to have been created by import");
				}
			}
		}

		utx.rollback();
	}

	@Test(groups = { "ImportLine" }, description = "Import Plugin should import file")
	public void verifyImportLineWithStopAreaImportModeCreateNew() throws Exception {
		stopAreaDAO.truncate();
		StopArea existingStopArea=createExistingStopArea();

		importLine(StopAreaImportModeEnum.CREATE_NEW);

		utx.begin();
		em.joinTransaction();

		Line line = lineDao.findByObjectId("AVI:Line:WF_TRD-MOL");
		assertNotNull(line, "Line not found");

		for (Route route : line.getRoutes()) {
			for (StopPoint stopPoint : route.getStopPoints()) {
				if (stopPoint.getObjectId().equals("AVI:StopPoint:1263628002")) {
					assertEquals(stopPoint.getContainedInStopArea().getObjectId(), existingStopArea.getObjectId());
					assertEquals(stopPoint.getContainedInStopArea().getName(), existingStopArea.getName(), "Expected name of existing stop area to be unchanged by import");
				} else {
					assertNotNull(stopPoint.getContainedInStopArea(), "Expected not existing stop area to have been created by import");
				}
			}
		}

		utx.rollback();
	}

	@Test(groups = { "ImportLine" }, description = "Import Plugin should import file")
	public void verifyImportLineWithStopAreaImportModeReadOnly() throws Exception {
		stopAreaDAO.truncate();
		StopArea existingStopArea=createExistingStopArea();

		importLine(StopAreaImportModeEnum.READ_ONLY);

		utx.begin();
		em.joinTransaction();

		Line line = lineDao.findByObjectId("AVI:Line:WF_TRD-MOL");
		assertNotNull(line, "Line not found");

		for (Route route : line.getRoutes()) {
			for (StopPoint stopPoint : route.getStopPoints()) {
				if (stopPoint.getObjectId().equals("AVI:StopPoint:1263628002")) {
					assertEquals(stopPoint.getContainedInStopArea().getObjectId(), existingStopArea.getObjectId(), "Expected existing stop area to be matched correctly");
					assertEquals(stopPoint.getContainedInStopArea().getName(), existingStopArea.getName(), "Expected name of existing stop area to be unchanged by import");
				} else if (stopPoint.getObjectId().equals("AVI:StopPoint:1869688101")) {
					assertEquals(stopPoint.getContainedInStopArea().getObjectId(), existingStopArea.getObjectId(), "Expected existing stop area to be matched correctly");
				} else {
					assertNull(stopPoint.getContainedInStopArea(), "Did not expect stop areas to be created by import");
				}
			}
		}

		utx.rollback();
	}

	private StopArea createExistingStopArea(){
		StopArea stopArea=new StopArea();
		stopArea.setAreaType(ChouetteAreaEnum.BoardingPosition);
		stopArea.setObjectId("AVI:Quay:MOL");
		stopArea.setName("Name for stop area referenced in C_NETEX_1.xml");
		stopAreaDAO.create(stopArea);
		return stopArea;
	}

	private void importLine(StopAreaImportModeEnum stopAreaImportMode) throws Exception {

		Context context = initImportContext();
		clearCodespaceRecords();

		insertCodespaceRecords(Arrays.asList(
				createCodespace(null, "NSR", "http://www.rutebanken.org/ns/nsr"),
				createCodespace(null, "AVI", "http://www.rutebanken.org/ns/avi"))
		);

		NetexTestUtils.copyFile("C_NETEX_1.xml");

		JobDataTest jobData = (JobDataTest) context.get(JOB_DATA);
		jobData.setInputFilename("C_NETEX_1.xml");

		NetexprofileImporterCommand command = (NetexprofileImporterCommand) CommandFactory.create(initialContext, NetexprofileImporterCommand.class.getName());

		NetexprofileImportParameters configuration = (NetexprofileImportParameters) context.get(CONFIGURATION);
		configuration.setNoSave(false);
		configuration.setCleanRepository(true);
		configuration.setStopAreaImportMode(stopAreaImportMode);

		try {
			command.execute(context);
		} catch (Exception ex) {
			log.error("test failed", ex);
			throw ex;
		}

		ActionReport report = (ActionReport) context.get(REPORT);
		Reporter.log("report :" + report.toString(), true);
		dumpReports(context);

		assertEquals(report.getResult(), STATUS_OK, "fileValidationResult");
		assertEquals(report.getFiles().size(), 1, "file reported");
		assertNotNull(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE), "line reported");
		assertEquals(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports().size(), 1, "line reported");

		for (ObjectReport info : report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports()) {
			Reporter.log("report line :" + info.toString(), true);
			assertEquals(info.getStatus(), ActionReporter.OBJECT_STATE.OK, "line status");
		}

		NetexTestUtils.verifyValidationReport(context);


		utx.begin();
		Line line = lineDao.findByObjectId("AVI:Line:WF_TRD-MOL");
		Assert.assertNotNull(line);

		Set<Timetable> timetables = new HashSet<Timetable>();
		for(Route r : line.getRoutes()) {
			for(JourneyPattern jp : r.getJourneyPatterns()) {
				for(VehicleJourney vj : jp.getVehicleJourneys()) {
					timetables.addAll(vj.getTimetables());
				}
			}
		}
		utx.rollback();
		assertEquals(timetables.size(), 9, "num timetables");
	}

	@Test(groups = { "ImportLine" }, description = "Import Plugin should import file")
	public void verifyImportSingleLineWithOperatingPeriods() throws Exception {
		Context context = initImportContext();
		clearCodespaceRecords();

		insertCodespaceRecords(Arrays.asList(
				createCodespace(null, "NSR", "http://www.rutebanken.org/ns/nsr"),
				createCodespace(null, "AVI", "http://www.rutebanken.org/ns/avi"))
		);

		NetexTestUtils.copyFile("C_NETEX_6.xml");

		JobDataTest jobData = (JobDataTest) context.get(JOB_DATA);
		jobData.setInputFilename("C_NETEX_6.xml");

		NetexprofileImporterCommand command = (NetexprofileImporterCommand) CommandFactory.create(initialContext, NetexprofileImporterCommand.class.getName());

		NetexprofileImportParameters configuration = (NetexprofileImportParameters) context.get(CONFIGURATION);
		configuration.setNoSave(false);
		configuration.setCleanRepository(true);

		boolean result;
		try {
			result = command.execute(context);
		} catch (Exception ex) {
			log.error("test failed", ex);
			throw ex;
		}

		ActionReport report = (ActionReport) context.get(REPORT);
		Reporter.log("report :" + report.toString(), true);
		dumpReports(context);

		assertEquals(report.getResult(), STATUS_OK, "fileValidationResult");
		assertEquals(report.getFiles().size(), 1, "file reported");
		assertNotNull(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE), "line reported");
		assertEquals(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports().size(), 1, "line reported");

		for (ObjectReport info : report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports()) {
			Reporter.log("report line :" + info.toString(), true);
			assertEquals(info.getStatus(), ActionReporter.OBJECT_STATE.OK, "line status");
		}

		NetexTestUtils.verifyValidationReport(context);

		utx.begin();
		em.joinTransaction();

		Line line = lineDao.findByObjectId("AVI:Line:WF_TRD-MOL");
		
		Assert.assertEquals(line.getTransportModeName(), TransportModeNameEnum.Air);
		Assert.assertEquals(line.getTransportSubModeName(), TransportSubModeNameEnum.DomesticFlight);

		VehicleJourney j = vehicleJourneyDao.findByObjectId("AVI:ServiceJourney:WF538-01-18696881");
		Assert.assertNotNull(j);
		Assert.assertEquals(j.getTransportMode(), TransportModeNameEnum.Air);
		Assert.assertEquals(j.getTransportSubMode(), TransportSubModeNameEnum.HelicopterService);
		
		
		
		assertNotNull(line, "Line not found");

		int numVehicleJourneys = 0;
		int numJourneyPatterns = 0;
		int numTimetables = 0;

		for (Route route : line.getRoutes()) {
			assertNotEquals(route.getJourneyPatterns().size(), 0, "line routes must have journeyPattens");

			for (JourneyPattern jp : route.getJourneyPatterns()) {

				assertNotEquals(jp.getVehicleJourneys().size(), 0, " journeyPattern should have VehicleJourneys");

				for (VehicleJourney vj : jp.getVehicleJourneys()) {
					assertNotEquals(vj.getTimetables().size(), 0, " vehicleJourney should have timetables");
					List<Timetable> timetables = vj.getTimetables();

					for (Timetable timetable : timetables) {
						// All daytypes outside of validitycondition
						assertEquals(timetable.getCalendarDays().size(), 0);
						assertEquals(timetable.getPeriods().size(), 0);

						numTimetables++;
					}

					numVehicleJourneys++;
				}
				numJourneyPatterns++;
			}
		}

		assertEquals(numJourneyPatterns, 2, "number of journeyPatterns");
		assertEquals(numVehicleJourneys, 3, "number of vehicleJourneys");
		assertEquals(numTimetables, 14, "number of timetables");

		utx.rollback();
		assertTrue(result, "Importer command execution failed: " + report.getFailure());
	}

	@Test(groups = { "ImportLine" }, description = "Import Plugin should import file")
	public void verifyImportSingleLinesWithCommonsAndMixedDayTypes() throws Exception {
		Context context = initImportContext();
		clearCodespaceRecords();

		insertCodespaceRecords(Arrays.asList(
				createCodespace(null, "NSR", "http://www.rutebanken.org/ns/nsr"),
				createCodespace(null, "AVI", "http://www.rutebanken.org/ns/avi"))
		);

		NetexprofileImporterCommand command = (NetexprofileImporterCommand) CommandFactory.create(
				initialContext, NetexprofileImporterCommand.class.getName());

		NetexTestUtils.copyFile("avinor_single_line_common_mixed_day_types.zip");

		JobDataTest jobData = (JobDataTest) context.get(JOB_DATA);
		jobData.setInputFilename("avinor_single_line_common_mixed_day_types.zip");

		NetexprofileImportParameters configuration = (NetexprofileImportParameters) context.get(CONFIGURATION);
		configuration.setNoSave(false);
		configuration.setCleanRepository(true);

		boolean result;
		try {
			result = command.execute(context);
		} catch (Exception ex) {
			log.error("test failed", ex);
			throw ex;
		}

		ActionReport report = (ActionReport) context.get(REPORT);

		dumpReports(context);

		assertEquals(report.getResult(), STATUS_OK, "result");
		assertEquals(report.getFiles().size(), 2, "files reported");
		assertNotNull(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE), "lines reported");
		assertEquals(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports().size(), 1, "lines reported");

		for (ObjectReport info : report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports()) {
			Reporter.log("report lines :" + info.toString(), true);
			assertEquals(info.getStatus(), ActionReporter.OBJECT_STATE.OK, "lines status");
		}

		NetexTestUtils.verifyValidationReport(context);

		utx.begin();
		em.joinTransaction();

		Line line = lineDao.findByObjectId("AVI:Line:DY_OSL-BGO");
		assertNotNull(line, "Line not found");

		utx.rollback();

		assertTrue(result, "Importer command execution failed: " + report.getFailure());
	}

	@Test
	public void verifyImportSingleLineWithCommonDataAvinor() throws Exception {
		Context context = initImportContext();
		clearCodespaceRecords();

		insertCodespaceRecords(Arrays.asList(
				createCodespace(null, "NSR", "http://www.rutebanken.org/ns/nsr"),
				createCodespace(null, "AVI", "http://www.rutebanken.org/ns/avi"))
		);

		NetexprofileImporterCommand command = (NetexprofileImporterCommand) CommandFactory.create(
				initialContext, NetexprofileImporterCommand.class.getName());

		NetexTestUtils.copyFile("avinor_single_line_with_commondata.zip");

		JobDataTest jobData = (JobDataTest) context.get(JOB_DATA);
		jobData.setInputFilename("avinor_single_line_with_commondata.zip");

		NetexprofileImportParameters configuration = (NetexprofileImportParameters) context.get(CONFIGURATION);
		configuration.setNoSave(false);
		configuration.setCleanRepository(true);

		boolean result;
		try {
			result = command.execute(context);
		} catch (Exception ex) {
			log.error("test failed", ex);
			throw ex;
		}

		ActionReport report = (ActionReport) context.get(REPORT);

		dumpReports(context);

		assertEquals(report.getResult(), STATUS_OK, "result");
		assertEquals(report.getFiles().size(), 2, "files reported");
		assertNotNull(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE), "lines reported");
		assertEquals(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports().size(), 1, "lines reported");

		for (ObjectReport info : report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports()) {
			Reporter.log("report lines :" + info.toString(), true);
			assertEquals(info.getStatus(), ActionReporter.OBJECT_STATE.OK, "lines status");
		}

		NetexTestUtils.verifyValidationReport(context);

		utx.begin();
		em.joinTransaction();

		Line line = lineDao.findByObjectId("AVI:Line:WF_TRD-MOL");

		assertNotNull(line, "Line not found");
		assertNotNull(line.getNetwork(), "line must have a network");
		assertNotNull(line.getCompany(), "line must have a company");
		assertNotNull(line.getRoutes(), "line must have routes");
		assertEquals(line.getRoutes().size(), 2, "number of routes");

		Set<StopArea> bps = new HashSet<>();

		int numStopPoints = 0;
		int numVehicleJourneys = 0;
		int numJourneyPatterns = 0;

		for (Route route : line.getRoutes()) {
			assertNotNull(route.getName(), "No route name");
			assertNotEquals(route.getJourneyPatterns().size(), 0, "line routes must have journeyPattens");

			for (JourneyPattern jp : route.getJourneyPatterns()) {
				assertNotNull(jp.getName(), "No journeypattern name");
				assertNotEquals(jp.getStopPoints().size(), 0, "line journeyPattens must have stoppoints");

				for (StopPoint point : jp.getStopPoints()) {
					numStopPoints++;
					
					if(jp.getStopPoints().get(0).equals(point)) {
						Assert.assertNotNull(point.getDestinationDisplay());
					}
					
					if(point.getDestinationDisplay() != null && point.getDestinationDisplay().getObjectId().equals("AVI:DestinationDisplay:12636280")) {
						// Verify that it has a via
						DestinationDisplay destinationDisplay = point.getDestinationDisplay();
						Assert.assertEquals(destinationDisplay.getVias().size(),1);
						Assert.assertEquals(destinationDisplay.getVias().get(0).getObjectId(), "AVI:DestinationDisplay:18696881");
						
					}
					
					assertNotNull(point.getContainedInStopArea(), "stoppoints must have StopAreas");
					bps.add(point.getContainedInStopArea());
					assertNotNull(point.getForAlighting(), "no alighting info StopPoint=" + point);
					assertNotNull(point.getForBoarding(), "no boarding info StopPoint=" + point);
				}

				assertNotEquals(jp.getVehicleJourneys().size(), 0, " journeyPattern should have VehicleJourneys");

				for (VehicleJourney vj : jp.getVehicleJourneys()) {
					assertNotEquals(vj.getTimetables().size(), 0, " vehicleJourney should have timetables");
					assertEquals(vj.getVehicleJourneyAtStops().size(), jp.getStopPoints().size(), " vehicleJourney should have correct vehicleJourneyAtStop count");
					List<Timetable> timetables = vj.getTimetables();
					for (Timetable timetable : timetables) {
						assertNotNull(timetable.getStartOfPeriod());
						assertNotNull(timetable.getEndOfPeriod());
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
		assertTrue(result, "Importer command execution failed: " + report.getFailure());
	}

	@Test
	public void verifyImportMultipleLinesWithCommonDataAvinor() throws Exception {
		Context context = initImportContext();
		clearCodespaceRecords();

		insertCodespaceRecords(Arrays.asList(
				createCodespace(null, "NSR", "http://www.rutebanken.org/ns/nsr"),
				createCodespace(null, "AVI", "http://www.rutebanken.org/ns/avi"))
		);

		NetexprofileImporterCommand command = (NetexprofileImporterCommand) CommandFactory.create(
				initialContext, NetexprofileImporterCommand.class.getName());

		NetexTestUtils.copyFile("avinor_multiple_lines_with_commondata.zip");

		JobDataTest jobData = (JobDataTest) context.get(JOB_DATA);
		jobData.setInputFilename("avinor_multiple_lines_with_commondata.zip");

		NetexprofileImportParameters configuration = (NetexprofileImportParameters) context.get(CONFIGURATION);
		configuration.setNoSave(false);
		configuration.setCleanRepository(true);

		boolean result;
		try {
			result = command.execute(context);
		} catch (Exception ex) {
			log.error("test failed", ex);
			throw ex;
		}

		ActionReport report = (ActionReport) context.get(REPORT);

		dumpReports(context);

		assertEquals(report.getResult(), STATUS_OK, "result");
		assertEquals(report.getFiles().size(), 4, "files reported");
		assertNotNull(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE), "lines reported");
		assertEquals(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports().size(), 3, "lines reported");

		for (ObjectReport info : report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports()) {
			Reporter.log("report lines :" + info.toString(), true);
			assertEquals(info.getStatus(), ActionReporter.OBJECT_STATE.OK, "lines status");
		}

		NetexTestUtils.verifyValidationReport(context);

		utx.begin();
		em.joinTransaction();

		Collection<String> objectIds = Arrays.asList("AVI:Line:DY_TRD-TOS", "AVI:Line:SK_BOO-TOS", "AVI:Line:WF_SVG-FRO");
		List<Line> lines = lineDao.findByObjectId(objectIds);

		for (Line line : lines) {
			assertNotNull(line, "Line not found");
			assertNotNull(line.getNetwork(), "line must have a network");
			assertNotNull(line.getCompany(), "line must have a company");
			assertNotNull(line.getRoutes(), "line must have routes");

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
				assertNotNull(route.getName(), "No route name");
				assertNotEquals(route.getJourneyPatterns().size(), 0, "line routes must have journeyPattens");

				for (JourneyPattern jp : route.getJourneyPatterns()) {
					assertNotNull(jp.getName(), "No journeypattern name");
					assertNotEquals(jp.getStopPoints().size(), 0, "line journeyPattens must have stoppoints");

					for (StopPoint point : jp.getStopPoints()) {
						numStopPoints++;
						assertNotNull(point.getContainedInStopArea(), "stoppoints must have StopAreas");
						bps.add(point.getContainedInStopArea());
						assertNotNull(point.getForAlighting(), "no alighting info StopPoint=" + point);
						assertNotNull(point.getForBoarding(), "no boarding info StopPoint=" + point);
					}

					assertNotEquals(jp.getVehicleJourneys().size(), 0, " journeyPattern should have VehicleJourneys");

					for (VehicleJourney vj : jp.getVehicleJourneys()) {
						assertNotEquals(vj.getTimetables().size(), 0, " vehicleJourney should have timetables");
						assertEquals(vj.getVehicleJourneyAtStops().size(), jp.getStopPoints().size(), " vehicleJourney should have correct vehicleJourneyAtStop count");
						List<Timetable> timetables = vj.getTimetables();
						for(Timetable timetable : timetables) {
							assertNotNull(timetable.getStartOfPeriod());
							assertNotNull(timetable.getEndOfPeriod());
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
		assertTrue(result, "Importer command execution failed: " + report.getFailure());
	}

	@Test
	public void importMultipleGroupsOfLinesAvinor() throws Exception {
		Context context = initImportContext();
		clearCodespaceRecords();

		insertCodespaceRecords(Arrays.asList(
				createCodespace(null, "NSR", "http://www.rutebanken.org/ns/nsr"),
				createCodespace(null, "AVI", "http://www.rutebanken.org/ns/avi"))
		);

		NetexprofileImporterCommand command = (NetexprofileImporterCommand) CommandFactory.create(
				initialContext, NetexprofileImporterCommand.class.getName());

		NetexTestUtils.copyFile("avinor_multiple_groups_of_lines.zip");

		JobDataTest jobData = (JobDataTest) context.get(JOB_DATA);
		jobData.setInputFilename("avinor_multiple_groups_of_lines.zip");

		NetexprofileImportParameters configuration = (NetexprofileImportParameters) context.get(CONFIGURATION);
		configuration.setNoSave(false);
		configuration.setCleanRepository(true);

		boolean result;
		try {
			result = command.execute(context);
		} catch (Exception ex) {
			log.error("test failed", ex);
			throw ex;
		}

		ActionReport report = (ActionReport) context.get(REPORT);

		dumpReports(context);

		assertEquals(report.getResult(), STATUS_OK, "result");
		assertEquals(report.getFiles().size(), 13, "files reported");
		assertNotNull(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE), "lines reported");
		assertEquals(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports().size(), 12, "lines reported");

		for (ObjectReport info : report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports()) {
			Reporter.log("report lines :" + info.toString(), true);
			assertEquals(info.getStatus(), ActionReporter.OBJECT_STATE.OK, "lines status");
		}

		NetexTestUtils.verifyValidationReport(context);

		utx.begin();
		em.joinTransaction();

		// check FlyViking line
		Line vfLine = lineDao.findByObjectId("AVI:Line:VF_BOO-HFT");
		assertNotNull(vfLine, "Line not found");
		assertNotNull(vfLine.getNetwork(), "line must have a network");
		assertEquals(vfLine.getNetwork().getObjectId(),"AVI:Network:VF", "Network objectId is not correct");
		assertTrue(vfLine.getGroupOfLines().isEmpty(), "line must not have group of lines");

		// check SAS lines
		Collection<String> sasObjectIds = Arrays.asList("AVI:Line:SK_BGO-AES", "AVI:Line:SK_SVG-AES", "AVI:Line:SK_SVG-BGO");
		List<Line> sasLines = lineDao.findByObjectId(sasObjectIds);

		for (Line line : sasLines) {
			assertNotNull(line, "Line not found");
			assertNotNull(line.getNetwork(), "line must have a network");
			assertEquals(line.getNetwork().getObjectId(), "AVI:Network:SK", "Network objectId is not correct");
			assertTrue(!line.getGroupOfLines().isEmpty(), "line must have group of lines");
			assertTrue(line.getGroupOfLines().size() == 1, "line must belong to 1 group");
			assertEquals(line.getGroupOfLines().get(0).getObjectId(), "AVI:GroupOfLines:SK-VEST", "Line group objectId is not correct");
			assertEquals(line.getGroupOfLines().get(0).getName(), "SAS Vestlandet", "Line group name is not correct");
		}

		// check Wideroe lines in group north
		Collection<String> wfNorthObjectIds = Arrays.asList("AVI:Line:WF_TOS-ALF", "AVI:Line:WF_TOS-HFT", "AVI:Line:WF_TRD-EVE");
		List<Line> wfNorthLines = lineDao.findByObjectId(wfNorthObjectIds);

		for (Line line : wfNorthLines) {
			assertNotNull(line, "Line not found");
			assertNotNull(line.getNetwork(), "line must have a network");
			assertEquals(line.getNetwork().getObjectId(), "AVI:Network:WF", "Network objectId is not correct");
			assertTrue(!line.getGroupOfLines().isEmpty(), "line must have group of lines");
			assertTrue(line.getGroupOfLines().size() == 1, "line must belong to 1 group");
			assertEquals(line.getGroupOfLines().get(0).getObjectId(), "AVI:GroupOfLines:WF-NORD", "Line group objectId is not correct");
			assertEquals(line.getGroupOfLines().get(0).getName(), "Widerøe Nord-Norge", "Line group name is not correct");
		}

		// check Wideroe lines in group middle
		Collection<String> wfMiddleObjectIds = Arrays.asList("AVI:Line:WF_OSL-SDN", "AVI:Line:WF_TRD-MOL");
		List<Line> wfMiddleLines = lineDao.findByObjectId(wfMiddleObjectIds);

		for (Line line : wfMiddleLines) {
			assertNotNull(line, "Line not found");
			assertNotNull(line.getNetwork(), "line must have a network");
			assertEquals(line.getNetwork().getObjectId(), "AVI:Network:WF", "Network objectId is not correct");
			assertTrue(!line.getGroupOfLines().isEmpty(), "line must have group of lines");
			assertTrue(line.getGroupOfLines().size() == 1, "line must belong to 1 group");
			assertEquals(line.getGroupOfLines().get(0).getObjectId(), "AVI:GroupOfLines:WF-MIDT", "Line group objectId is not correct");
			assertEquals(line.getGroupOfLines().get(0).getName(), "Widerøe Midt-Norge", "Line group name is not correct");
		}

		// check Wideroe lines in group west
		Collection<String> wfWestObjectIds = Arrays.asList("AVI:Line:WF_BGO-AES", "AVI:Line:WF_BGO-HAU", "AVI:Line:WF_BGO-SVG");
		List<Line> wfWestLines = lineDao.findByObjectId(wfWestObjectIds);

		for (Line line : wfWestLines) {
			assertNotNull(line, "Line not found");
			assertNotNull(line.getNetwork(), "line must have a network");
			assertEquals(line.getNetwork().getObjectId(), "AVI:Network:WF", "Network objectId is not correct");
			assertTrue(!line.getGroupOfLines().isEmpty(), "line must have group of lines");
			assertTrue(line.getGroupOfLines().size() == 1, "line must belong to 1 group");
			assertEquals(line.getGroupOfLines().get(0).getObjectId(), "AVI:GroupOfLines:WF-VEST", "Line group objectId is not correct");
			assertEquals(line.getGroupOfLines().get(0).getName(), "Widerøe Vestlandet", "Line group name is not correct");
		}

		utx.rollback();
		assertTrue(result, "Importer command execution failed: " + report.getFailure());
	}

	@Test
	public void importSingleLineWithNotices() throws Exception {
		Context context = initImportContext();
		clearCodespaceRecords();

		insertCodespaceRecords(Arrays.asList(
				createCodespace(null, "NSR", "http://www.rutebanken.org/ns/nsr"),
				createCodespace(null, "AVI", "http://www.rutebanken.org/ns/avi"))
		);

		NetexprofileImporterCommand command = (NetexprofileImporterCommand) CommandFactory.create(
				initialContext, NetexprofileImporterCommand.class.getName());

		NetexTestUtils.copyFile("avinor_single_line_with_notices.zip");

		JobDataTest jobData = (JobDataTest) context.get(JOB_DATA);
		jobData.setInputFilename("avinor_single_line_with_notices.zip");

		NetexprofileImportParameters configuration = (NetexprofileImportParameters) context.get(CONFIGURATION);
		configuration.setNoSave(false);
		configuration.setCleanRepository(true);

		boolean result;
		try {
			result = command.execute(context);
		} catch (Exception ex) {
			log.error("test failed", ex);
			throw ex;
		}

		ActionReport report = (ActionReport) context.get(REPORT);

		dumpReports(context);

		assertEquals(report.getResult(), STATUS_OK, "result");
		assertEquals(report.getFiles().size(), 2, "files reported");
		assertNotNull(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE), "lines reported");
		assertEquals(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports().size(), 1, "lines reported");

		for (ObjectReport info : report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports()) {
			Reporter.log("report lines :" + info.toString(), true);
			assertEquals(info.getStatus(), ActionReporter.OBJECT_STATE.OK, "lines status");
		}

		NetexTestUtils.verifyValidationReport(context);

		utx.begin();
		em.joinTransaction();

		Line line = lineDao.findByObjectId("AVI:Line:WF_TRD-MOL");
		assertNotNull(line, "Line not found");
		assertNotNull(line.getFootnotes(), "line must have footnotes");
		assertEquals(line.getFootnotes().size(), 3, "number of footnotes");

		for (Route route : line.getRoutes()) {
			for (JourneyPattern journeyPattern : route.getJourneyPatterns()) {
				for (VehicleJourney vehicleJourney : journeyPattern.getVehicleJourneys()) {
					assertNotEquals(vehicleJourney.getFootnotes().size(), 0, " vehicleJourney should have footnotes");
					assertEquals(vehicleJourney.getFootnotes().size(), 1, "number of footnotes");

					Footnote footnote = vehicleJourney.getFootnotes().get(0);

					if (vehicleJourney.getObjectId().equals("AVI:ServiceJourney:3273336")) {
						assertEquals(footnote.getLabel(), "Sample notice text...1111", "Notice label is not correct");
						assertEquals(footnote.getLine(), line, "Line is not correct");
					} else if (vehicleJourney.getObjectId().equals("AVI:ServiceJourney:4598614")) {
						assertEquals(footnote.getLabel(), "Sample notice text...2222", "Notice label is not correct");
						assertEquals(footnote.getLine(), line, "Line is not correct");
					} else if (vehicleJourney.getObjectId().equals("AVI:ServiceJourney:3774199")) {
						assertEquals(footnote.getLabel(), "Sample notice text...3333", "Notice label is not correct");
						assertEquals(footnote.getLine(), line, "Line is not correct");
					}
				}
			}
		}

		utx.rollback();
		assertTrue(result, "Importer command execution failed: " + report.getFailure());
	}

	@Test
	public void importSingleLineWithSameLineInterchanges() throws Exception {
		Context context = initImportContext();
		clearCodespaceRecords();

		insertCodespaceRecords(Arrays.asList(
				createCodespace(null, "NSR", "http://www.rutebanken.org/ns/nsr"),
				createCodespace(null, "AVI", "http://www.rutebanken.org/ns/avi"))
		);

		NetexprofileImporterCommand command = (NetexprofileImporterCommand) CommandFactory.create(
				initialContext, NetexprofileImporterCommand.class.getName());

		NetexTestUtils.copyFile("avinor_single_line_with_interchanges.zip");

		JobDataTest jobData = (JobDataTest) context.get(JOB_DATA);
		jobData.setInputFilename("avinor_single_line_with_interchanges.zip");

		NetexprofileImportParameters configuration = (NetexprofileImportParameters) context.get(CONFIGURATION);
		configuration.setNoSave(false);
		configuration.setCleanRepository(true);

		boolean result;
		try {
			result = command.execute(context);
		} catch (Exception ex) {
			log.error("test failed", ex);
			throw ex;
		}

		ActionReport report = (ActionReport) context.get(REPORT);

		dumpReports(context);

		assertEquals(report.getResult(), STATUS_OK, "result");
		assertEquals(report.getFiles().size(), 2, "files reported");
		assertNotNull(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE), "lines reported");
		assertEquals(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports().size(), 1, "lines reported");

		for (ObjectReport info : report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports()) {
			Reporter.log("report lines :" + info.toString(), true);
			assertEquals(info.getStatus(), ActionReporter.OBJECT_STATE.OK, "lines status");
		}

		NetexTestUtils.verifyValidationReport(context);

		utx.begin();
		em.joinTransaction();

		VehicleJourney feederJourney = vehicleJourneyDao.findByObjectId("AVI:ServiceJourney:3273336");
		assertNotNull(feederJourney, "Feeder journey not found");
		VehicleJourney consumerJourney = vehicleJourneyDao.findByObjectId("AVI:ServiceJourney:4598614");
		assertNotNull(consumerJourney, "Consumer journey not found");

		
		
		assertEquals(feederJourney.getFeederInterchanges().size(), 1, " feederjourney should have feeder interchange");
		assertEquals(consumerJourney.getConsumerInterchanges().size(), 1, " consumerjourney should have consumer interchange");

		Interchange i = consumerJourney.getConsumerInterchanges().get(0);
		
		assertNotNull(i.getConsumerVehicleJourney());
		assertNotNull(i.getFeederVehicleJourney());
		assertNotNull(i.getConsumerStopPoint());
		assertNotNull(i.getFeederStopPoint());
		
		assertEquals(i.getStaySeated(),Boolean.FALSE);
		assertEquals(i.getPlanned(), Boolean.TRUE);
		assertEquals(i.getGuaranteed(),Boolean.FALSE);
		assertEquals(i.getAdvertised(),Boolean.TRUE);
		
		assertEquals(i.getMaximumWaitTime(), Duration.standardMinutes(30));
		assertNotNull(i.getName());
		Assert.assertNull(i.getMinimumTransferTime());
		
	

		utx.rollback();
		assertTrue(result, "Importer command execution failed: " + report.getFailure());
	}
	
	
	@Test
	public void importMultipleLinesWithInterchanges() throws Exception {
		Context context = initImportContext();
		clearCodespaceRecords();

		insertCodespaceRecords(Arrays.asList(
				createCodespace(null, "NSR", "http://www.rutebanken.org/ns/nsr"),
				createCodespace(null, "AVI", "http://www.rutebanken.org/ns/avi"))
		);

		NetexprofileImporterCommand command = (NetexprofileImporterCommand) CommandFactory.create(
				initialContext, NetexprofileImporterCommand.class.getName());

		NetexTestUtils.copyFile("avinor_multiple_line_with_interchanges.zip");

		JobDataTest jobData = (JobDataTest) context.get(JOB_DATA);
		jobData.setInputFilename("avinor_multiple_line_with_interchanges.zip");

		NetexprofileImportParameters configuration = (NetexprofileImportParameters) context.get(CONFIGURATION);
		configuration.setNoSave(false);
		configuration.setCleanRepository(true);

		boolean result;
		try {
			result = command.execute(context);
		} catch (Exception ex) {
			log.error("test failed", ex);
			throw ex;
		}

		ActionReport report = (ActionReport) context.get(REPORT);

		dumpReports(context);

		assertEquals(report.getResult(), STATUS_OK, "result");
		assertEquals(report.getFiles().size(), 3, "files reported");
		assertNotNull(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE), "lines reported");
		assertEquals(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports().size(), 2, "lines reported");

		for (ObjectReport info : report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports()) {
			Reporter.log("report lines :" + info.toString(), true);
			assertEquals(info.getStatus(), ActionReporter.OBJECT_STATE.OK, "lines status");
		}

		NetexTestUtils.verifyValidationReport(context);

		utx.begin();
		em.joinTransaction();

		VehicleJourney feederJourney = vehicleJourneyDao.findByObjectId("AVI:ServiceJourney:Feeder");
		assertNotNull(feederJourney, "Feeder journey not found");
		VehicleJourney consumerJourney = vehicleJourneyDao.findByObjectId("AVI:ServiceJourney:Consumer");
		assertNotNull(consumerJourney, "Consumer journey not found");

		
		
		assertEquals(feederJourney.getFeederInterchanges().size(), 1, " feederjourney should have feeder interchange");
		assertEquals(consumerJourney.getConsumerInterchanges().size(), 1, " consumerjourney should have consumer interchange");

		Interchange i = consumerJourney.getConsumerInterchanges().get(0);
		
		assertNotNull(i.getConsumerVehicleJourney());
		assertNotNull(i.getFeederVehicleJourney());
		assertNotNull(i.getConsumerStopPoint());
		//assertNotNull(i.getFeederStopPoint());
		log.error("TEST IS BROKEN UNTIL SCHEDULED STOPPOINT EXIST AS A CONCEPT IN CHOUETTE");
		
		assertEquals(i.getStaySeated(),Boolean.FALSE);
		assertEquals(i.getPlanned(), Boolean.TRUE);
		assertEquals(i.getGuaranteed(),Boolean.FALSE);
		assertEquals(i.getAdvertised(), Boolean.TRUE);

		assertEquals(i.getMaximumWaitTime(), Duration.standardMinutes(30));
		assertNotNull(i.getName());
		Assert.assertNull(i.getMinimumTransferTime());
		
	

		utx.rollback();
		assertTrue(result, "Importer command execution failed: " + report.getFailure());
	}
	
	

	@Test(enabled = false)
	public void verifyImportSingleLineWithCommonDataRuter() throws Exception {
		Context context = initImportContext();
		clearCodespaceRecords();

		insertCodespaceRecords(Arrays.asList(
				createCodespace(null, "NSR", "http://www.rutebanken.org/ns/nsr"),
				createCodespace(null, "RUT", "http://www.rutebanken.org/ns/ruter"))
		);

		NetexprofileImporterCommand command = (NetexprofileImporterCommand) CommandFactory.create(
				initialContext, NetexprofileImporterCommand.class.getName());

		NetexTestUtils.copyFile("ruter_single_line_210_with_commondata.zip");

		JobDataTest jobData = (JobDataTest) context.get(JOB_DATA);
		jobData.setInputFilename("ruter_single_line_210_with_commondata.zip");

		NetexprofileImportParameters configuration = (NetexprofileImportParameters) context.get(CONFIGURATION);
		configuration.setNoSave(false);
		configuration.setCleanRepository(true);

		boolean result;
		try {
			result = command.execute(context);
		} catch (Exception ex) {
			log.error("test failed", ex);
			throw ex;
		}

		ActionReport report = (ActionReport) context.get(REPORT);

		dumpReports(context);

		assertEquals(report.getResult(), STATUS_OK, "result");
		assertEquals(report.getFiles().size(), 2, "files reported");
		assertNotNull(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE), "lines reported");
		assertEquals(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports().size(), 1, "lines reported");

		for (ObjectReport info : report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports()) {
			Reporter.log("report lines :" + info.toString(), true);
			assertEquals(info.getStatus(), ActionReporter.OBJECT_STATE.OK, "lines status");
		}

		NetexTestUtils.verifyValidationReport(context);

		utx.begin();
		em.joinTransaction();

		Line line = lineDao.findByObjectId("RUT:Line:210");
		assertNotNull(line, "Line not found");

		utx.rollback();

		assertTrue(result, "Importer command execution failed: " + report.getFailure());
	}

	@Test(enabled = false)
	public void importMultipleLinesVKT() throws Exception {
		Context context = initImportContext();
		clearCodespaceRecords();

		insertCodespaceRecords(Arrays.asList(
				createCodespace(null, "NSR", "http://www.rutebanken.org/ns/nsr"),
				createCodespace(null, "VKT", "http://www.rutebanken.org/ns/vkt"))
		);

		JobDataTest jobData = (JobDataTest) context.get(JOB_DATA);
		String inputFileName = "NeTEx_VKT_r1.10.zip";

		NetexprofileImporterCommand command = (NetexprofileImporterCommand) CommandFactory.create(
				initialContext, NetexprofileImporterCommand.class.getName());

		NetexTestUtils.copyFile(inputFileName);
		jobData.setInputFilename(inputFileName);

		NetexprofileImportParameters configuration = (NetexprofileImportParameters) context.get(CONFIGURATION);
		configuration.setNoSave(false);
		configuration.setCleanRepository(true);

		boolean result;
		try {
			result = command.execute(context);
		} catch (Exception ex) {
			log.error("test failed", ex);
			throw ex;
		}

		ActionReport report = (ActionReport) context.get(REPORT);

		dumpReports(context);

		assertEquals(report.getResult(), STATUS_OK, "result");
		assertEquals(report.getFiles().size(), 3, "files reported");
		assertNotNull(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE), "lines reported");
		assertEquals(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports().size(), 2, "lines reported");

		for (ObjectReport info : report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports()) {
			Reporter.log("report lines :" + info.toString(), true);
			assertEquals(info.getStatus(), ActionReporter.OBJECT_STATE.OK, "lines status");
		}

		NetexTestUtils.verifyValidationReport(context);

		utx.begin();
		em.joinTransaction();

		Collection<String> objectIds = Arrays.asList("VKT:Line:1106", "VKT:Line:1107");
		List<Line> lines = lineDao.findByObjectId(objectIds);

		for (Line line : lines) {
			assertNotNull(line, "Line not found");
		}

		utx.rollback();
		assertTrue(result, "Importer command execution failed: " + report.getFailure());
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

			List<String> lines = new ArrayList<>();
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
