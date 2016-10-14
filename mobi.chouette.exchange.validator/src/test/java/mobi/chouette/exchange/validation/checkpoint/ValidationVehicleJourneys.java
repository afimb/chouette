package mobi.chouette.exchange.validation.checkpoint;

import java.io.File;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.dao.JourneyPatternDAO;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.CheckPointErrorReport;
import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.exchange.validator.DummyChecker;
import mobi.chouette.exchange.validator.JobDataTest;
import mobi.chouette.model.JourneyFrequency;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.type.TransportModeNameEnum;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

@Log4j
public class ValidationVehicleJourneys extends AbstractTestValidation {
	private VehicleJourneyCheckPoints checkPoint = new VehicleJourneyCheckPoints();
	private ValidationParameters fullparameters;
	private VehicleJourney bean1;
	private VehicleJourney bean2;
	private List<VehicleJourney> beansFor4 = new ArrayList<>();

	@EJB 
	LineDAO lineDao;
	@EJB
	JourneyPatternDAO journeyPatternDao;

	@PersistenceContext(unitName = "referential")
	EntityManager em;

	@Inject
	UserTransaction utx;

	@Deployment
	public static EnterpriseArchive createDeployment() {

		EnterpriseArchive result;
		File[] files = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("mobi.chouette:mobi.chouette.exchange.validator").withTransitivity().asFile();
		List<File> jars = new ArrayList<>();
		List<JavaArchive> modules = new ArrayList<>();
		for (File file : files) {
			if (file.getName().startsWith("mobi.chouette.exchange"))
			{
				String name = file.getName().split("\\-")[0]+".jar";
				JavaArchive archive = ShrinkWrap
						  .create(ZipImporter.class, name)
						  .importFrom(file)
						  .as(JavaArchive.class);
				modules.add(archive);
			}
			else
			{
				jars.add(file);
			}
		}
		File[] filesDao = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("mobi.chouette:mobi.chouette.dao").withTransitivity().asFile();
		if (filesDao.length == 0) 
		{
			throw new NullPointerException("no dao");
		}
		for (File file : filesDao) {
			if (file.getName().startsWith("mobi.chouette.dao"))
			{
				String name = file.getName().split("\\-")[0]+".jar";
				
				JavaArchive archive = ShrinkWrap
						  .create(ZipImporter.class, name)
						  .importFrom(file)
						  .as(JavaArchive.class);
				modules.add(archive);
				if (!modules.contains(archive))
				   modules.add(archive);
			}
			else
			{
				if (!jars.contains(file))
				   jars.add(file);
			}
		}
		final WebArchive testWar = ShrinkWrap.create(WebArchive.class, "test.war").addAsWebInfResource("postgres-ds.xml")
				.addClass(DummyChecker.class)
				.addClass(JobDataTest.class)
				.addClass(AbstractTestValidation.class)
				.addClass(ValidationVehicleJourneys.class);
		
		result = ShrinkWrap.create(EnterpriseArchive.class, "test.ear")
				.addAsLibraries(jars.toArray(new File[0]))
				.addAsModules(modules.toArray(new JavaArchive[0]))
				.addAsModule(testWar)
				.addAsResource(EmptyAsset.INSTANCE, "beans.xml");
		return result;
	}

	@BeforeGroups(groups = { "vehicleJourney" })
	public void init() {
		super.init();

		long id = 1;

		fullparameters = null;
		try {
			fullparameters = loadFullParameters();
			fullparameters.setCheckVehicleJourney(1);

			Line line = new Line();
			line.setId(id++);
			line.setObjectId("test1:Line:1");
			line.setName("test");
			Route route = new Route();
			route.setId(id++);
			route.setObjectId("test1:Route:1");
			route.setName("test1");
			route.setLine(line);
			JourneyPattern jp = new JourneyPattern();
			jp.setId(id++);
			jp.setObjectId("test1:JourneyPattern:1");
			jp.setName("test1");
			jp.setRoute(route);
			bean1 = new VehicleJourney();
			bean1.setId(id++);
			bean1.setObjectId("test1:VehicleJourney:1");
			bean1.setPublishedJourneyName("test1");
			bean1.setJourneyPattern(jp);
			bean2 = new VehicleJourney();
			bean2.setId(id++);
			bean2.setObjectId("test2:VehicleJourney:1");
			bean2.setPublishedJourneyName("test2");
			bean2.setJourneyPattern(jp);
			
			beansFor4.add(bean1);
			beansFor4.add(bean2);
		} catch (Exception e) {
			fullparameters = null;
			e.printStackTrace();
		}

	}

	@Test(groups = { "vehicleJourney" }, description = "4-VehicleJourney-1 no test",priority=1)
	public void verifyTest4_1_notest() throws Exception {
		// 4-VehicleJourney-1 : check columns
		log.info(Color.BLUE + "4-VehicleJourney-1 no test" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");
		context.put(VALIDATION, fullparameters);
		context.put(VALIDATION_REPORT, new ValidationReport());

		fullparameters.setCheckVehicleJourney(0);
		ValidationData data = new ValidationData();
		data.getVehicleJourneys().addAll(beansFor4);
		context.put(VALIDATION_DATA, data);

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertTrue(report.findCheckPointReportByName("4-VehicleJourney-1") == null, " report must not have item 4-VehicleJourney-1");

		fullparameters.setCheckVehicleJourney(1);

		context.put(VALIDATION_REPORT, new ValidationReport());

		checkPoint.validate(context, null);
		report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertTrue(report.findCheckPointReportByName("4-VehicleJourney-1") != null, " report must have item 4-VehicleJourney-1");
		Assert.assertEquals(report.findCheckPointReportByName("4-VehicleJourney-1").getCheckPointErrorCount(), 0,
				" checkpoint must have no detail");

	}

	@Test(groups = { "vehicleJourney" }, description = "4-VehicleJourney-1 unicity",priority=2)
	public void verifyTest4_1_unique() throws Exception {
		// 4-VehicleJourney-1 : check columns
		log.info(Color.BLUE + "4-VehicleJourney-1 unicity" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");

		context.put(VALIDATION, fullparameters);
		context.put(VALIDATION_REPORT, new ValidationReport());

		fullparameters.setCheckVehicleJourney(1);
		fullparameters.getVehicleJourney().getObjectId().setUnique(1);

		ValidationData data = new ValidationData();
		data.getVehicleJourneys().addAll(beansFor4);
		context.put(VALIDATION_DATA, data);

		checkPoint.validate(context, null);
		fullparameters.getRoute().getObjectId().setUnique(0);
		// unique
		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);

		List<CheckPointErrorReport> details = checkReportForTest(report, "4-VehicleJourney-1", 3);
		for (CheckPointErrorReport detail : details) {
			Assert.assertEquals(detail.getReferenceValue(), "ObjectId", "detail must refer column");
			Assert.assertEquals(detail.getValue(), bean2.getObjectId().split(":")[2], "detail must refer value");
		}
	}

	@Test(groups = { "vehicleJourney" }, description = "3-VehicleJourney-1",priority=3)
	public void verifyTest3_1() throws Exception {
		// 3-VehicleJourney-1 : check if time progress correctly on each stop
		log.info(Color.BLUE + "3-VehicleJourney-1" + Color.NORMAL);
		Context context = initValidatorContext();
		context.put(VALIDATION_REPORT, new ValidationReport());

		Assert.assertNotNull(fullparameters, "no parameters for test");

		importLines("Ligne_OK.xml", 1, 1, true);

		utx.begin();
		em.joinTransaction();

		List<Line> beans = lineDao.findAll();
		Assert.assertFalse(beans.isEmpty(), "No data for test");
		Line line1 = beans.get(0);

		Route route1 = line1.getRoutes().get(0);
		route1.setObjectId("NINOXE:Route:checkedRoute");
		JourneyPattern jp1 = route1.getJourneyPatterns().get(0);
		jp1.setObjectId("NINOXE:JourneyPattern:checkedJP");
		VehicleJourney vj1 = jp1.getVehicleJourneys().get(0);
		vj1.setObjectId("NINOXE:VehicleJourney:checkedVJ");
		long maxDiffTime = 0;
		for (VehicleJourneyAtStop vjas : vj1.getVehicleJourneyAtStops()) {
			if (vjas.getArrivalTime().equals(vjas.getDepartureTime())) {
				vjas.getArrivalTime().setTime(vjas.getArrivalTime().getTime() - 60000);
			}
			long diffTime = Math.abs(diffTime(vjas.getArrivalTime(), vjas.getDepartureTime()));
			if (diffTime > maxDiffTime)
				maxDiffTime = diffTime;
		}
		ValidationData data = new ValidationData();
		data.getVehicleJourneys().addAll(jp1.getVehicleJourneys());
		fullparameters.setInterStopDurationMax((int) maxDiffTime - 30);
		context.put(VALIDATION_DATA, data);
		context.put(VALIDATION, fullparameters);

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");
		
		CheckPointReport checkPointReport = report.findCheckPointReportByName("3-VehicleJourney-1");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-VehicleJourney-1 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(), CheckPointReport.SEVERITY.WARNING,
				" checkPointReport must be on level warning");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 4, " checkPointReport must have 4 item");

		String detailKey = "3-VehicleJourney-1".replaceAll("-", "_").toLowerCase();
		List<CheckPointErrorReport> details = checkReportForTest(report,"3-VehicleJourney-1",-1);
		for (CheckPointErrorReport detail : details) {
			Assert.assertTrue(detail.getKey().startsWith(detailKey),
					"details key should start with test key : expected " + detailKey + ", found : " + detail.getKey());
		}
		// check detail keys
		for (CheckPointErrorReport detail : details) {
			Assert.assertEquals(detail.getSource().getObjectId(), vj1.getObjectId(),
					"vj 1 must be source of error");
		}

		utx.rollback();

	}

	@Test(groups = { "vehicleJourney" }, description = "3-VehicleJourney-2",priority=4)
	public void verifyTest3_2() throws Exception {
		// 3-VehicleJourney-2 : check speed progression
		log.info(Color.BLUE + "3-VehicleJourney-2" + Color.NORMAL);
		Context context = initValidatorContext();
		context.put(VALIDATION_REPORT, new ValidationReport());

		Assert.assertNotNull(fullparameters, "no parameters for test");

		importLines("Ligne_OK.xml", 1, 1, true);

		utx.begin();
		em.joinTransaction();

		List<Line> beans = lineDao.findAll();
		Assert.assertFalse(beans.isEmpty(), "No data for test");
		Line line1 = beans.get(0);

		// line1 is model;

		Route route1 = line1.getRoutes().get(0);
		route1.setObjectId("NINOXE:Route:checkedRoute");
		JourneyPattern jp1 = route1.getJourneyPatterns().get(0);
		jp1.setObjectId("NINOXE:JourneyPattern:checkedJP");

		VehicleJourney vj1 = jp1.getVehicleJourneys().get(0);
		vj1.setObjectId("NINOXE:VehicleJourney:checkedVJ");

		fullparameters.getModeBus().setSpeedMax( 10);
		fullparameters.getModeBus().setSpeedMin( 20);

		ValidationData data = new ValidationData();
		data.getVehicleJourneys().addAll(jp1.getVehicleJourneys());
		context.put(VALIDATION_DATA, data);
		context.put(VALIDATION, fullparameters);

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");
		

		CheckPointReport checkPointReport = report.findCheckPointReportByName("3-VehicleJourney-2");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-VehicleJourney-2 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(), CheckPointReport.SEVERITY.WARNING,
				" checkPointReport must be on level warning");
		
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 81, " checkPointReport must have 81 item");
		String detailKey = "3-VehicleJourney-2".replaceAll("-", "_").toLowerCase();
		List<CheckPointErrorReport> details = checkReportForTest(report,"3-VehicleJourney-2",-1);
		for (CheckPointErrorReport detail : details) {
			Assert.assertTrue(detail.getKey().startsWith(detailKey),
					"details key should start with test key : expected " + detailKey + ", found : " + detail.getKey());
		}
		utx.rollback();

	}

	@Test(groups = { "vehicleJourney" }, description = "3-VehicleJourney-3",priority=5)
	public void verifyTest3_3() throws Exception {
		// 3-VehicleJourney-3 : check if two journeys progress similarly
		log.info(Color.BLUE + "3-VehicleJourney-3" + Color.NORMAL);
		Context context = initValidatorContext();
		context.put(VALIDATION_REPORT, new ValidationReport());

		Assert.assertNotNull(fullparameters, "no parameters for test");

		importLines("Ligne_OK.xml", 1, 1, true);

		utx.begin();
		em.joinTransaction();

		List<Line> beans = lineDao.findAll();
		Assert.assertFalse(beans.isEmpty(), "No data for test");
		Line line1 = beans.get(0);

		// line1 is model;
		VehicleJourney vj1 = null;
		JourneyPattern jp1 = null;
		for (Route route : line1.getRoutes()) {
			for (JourneyPattern jp : route.getJourneyPatterns()) {
				for (VehicleJourney vj : jp.getVehicleJourneys()) {
					if (vj.getObjectId().equals("NINOXE:VehicleJourney:15627288")) {
						vj1 = vj;
						jp1 = jp;
					}
				}
			}
		}

		Assert.assertNotNull(jp1, "tested jp not found");
		Assert.assertNotNull(vj1, "tested vj not found");

		VehicleJourneyAtStop vjas1 = vj1.getVehicleJourneyAtStops().get(1);
		vjas1.getArrivalTime().setTime(vjas1.getArrivalTime().getTime() - 240000);

		fullparameters.getModeBus().setInterStopDurationVariationMax(220);

		ValidationData data = new ValidationData();
		data.getVehicleJourneys().addAll(jp1.getVehicleJourneys());
		context.put(VALIDATION_DATA, data);
		context.put(VALIDATION, fullparameters);

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");
		

		CheckPointReport checkPointReport = report.findCheckPointReportByName("3-VehicleJourney-3");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-VehicleJourney-3 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(), CheckPointReport.SEVERITY.WARNING,
				" checkPointReport must be on level warning");
		
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 1, " checkPointReport must have 1 item");
		String detailKey = "3-VehicleJourney-3".replaceAll("-", "_").toLowerCase();
		List<CheckPointErrorReport> details = checkReportForTest(report,"3-VehicleJourney-3",-1);
		for (CheckPointErrorReport detail : details) {
			Assert.assertTrue(detail.getKey().startsWith(detailKey),
					"details key should start with test key : expected " + detailKey + ", found : " + detail.getKey());
		}
		utx.rollback();

	}

	@Test(groups = { "vehicleJourney" }, description = "3-VehicleJourney-4",priority=6)
	public void verifyTest3_4() throws Exception {
		// 3-VehicleJourney-4 : check if each journey has minimum one timetable
		log.info(Color.BLUE + "3-VehicleJourney-3" + Color.NORMAL);
		Context context = initValidatorContext();
		context.put(VALIDATION_REPORT, new ValidationReport());

		Assert.assertNotNull(fullparameters, "no parameters for test");

		importLines("Ligne_OK.xml", 1, 1, true);

		utx.begin();
		em.joinTransaction();

		List<Line> beans = lineDao.findAll();
		Assert.assertFalse(beans.isEmpty(), "No data for test");
		Line line1 = beans.get(0);

		// line1 is model;

		Route route1 = line1.getRoutes().get(0);
		route1.setObjectId("NINOXE:Route:checkedRoute");
		JourneyPattern jp1 = route1.getJourneyPatterns().get(0);
		jp1.setObjectId("NINOXE:JourneyPattern:checkedJP");
		VehicleJourney vj1 = jp1.getVehicleJourneys().get(0);
		vj1.setObjectId("NINOXE:VehicleJourney:checkedVJ");

		vj1.getTimetables().clear();
		
		ValidationData data = new ValidationData();
		data.getVehicleJourneys().addAll(jp1.getVehicleJourneys());
		context.put(VALIDATION_DATA, data);
		context.put(VALIDATION, fullparameters);

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");
		

		CheckPointReport checkPointReport = report.findCheckPointReportByName("3-VehicleJourney-4");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-VehicleJourney-4 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(), CheckPointReport.SEVERITY.WARNING,
				" checkPointReport must be on level warning");
		
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 1, " checkPointReport must have 1 item");
		String detailKey = "3-VehicleJourney-4".replaceAll("-", "_").toLowerCase();
		List<CheckPointErrorReport> details = checkReportForTest(report,"3-VehicleJourney-4",-1);
		for (CheckPointErrorReport detail : details) {
			Assert.assertTrue(detail.getKey().startsWith(detailKey),
					"details key should start with test key : expected " + detailKey + ", found : " + detail.getKey());
		}
		utx.rollback();

	}
	
	@Test(groups = { "vehicleJourney" }, description = "3-VehicleJourney-6",priority=7)
	public void verifyTest3_6() throws Exception {
		// 3-VehicleJourney-6 : check if two journey frequencies are overlapping on same vehicle journey
		log.info(Color.BLUE + "3-VehicleJourney-6" + Color.NORMAL);
		
		
		importLines("Neptune_With_Frequencies.xml", 1, 1, true);
		
		Context context = initValidatorContext();
		context.put(VALIDATION_REPORT, new ValidationReport());

		Assert.assertNotNull(fullparameters, "no parameters for test");

		

		utx.begin();
		em.joinTransaction();

		
		JourneyPattern bean = journeyPatternDao.findByObjectId("ratp:JourneyPattern:1000252_00");
		Assert.assertFalse(bean == null, "No data for test");
		JourneyPattern jp1 = bean;
		jp1.setObjectId("NINOXE:JourneyPattern:checkedJP");
		
		
		ValidationData data = new ValidationData();
		data.getVehicleJourneys().addAll(jp1.getVehicleJourneys());
		context.put(VALIDATION_DATA, data);
		context.put(VALIDATION, fullparameters);

		checkPoint.validate(context, null);
		
		
		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");
		
		CheckPointReport checkPointReport = report.findCheckPointReportByName("3-VehicleJourney-6");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-VehicleJourney-6 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.OK, " checkPointReport must be ok");
		
		context.put(VALIDATION_REPORT, new ValidationReport());
		
		VehicleJourney vj1 = null;
		
		for(VehicleJourney vj: jp1.getVehicleJourneys()) {
			if(vj.getObjectId().equalsIgnoreCase("ratp:VehicleJourney:514572940997334-2-2")) {
				vj1 = vj;
				break;
			}
		}
		
		List<JourneyFrequency> listJF = vj1.getJourneyFrequencies();
		
		
		JourneyFrequency jf = new JourneyFrequency();
		jf.setFirstDepartureTime(listJF.get(0).getFirstDepartureTime());
		jf.setLastDepartureTime(listJF.get(0).getLastDepartureTime());
		listJF.add(jf);
		log.info("Test 3_6 : number of journey frequency : " + listJF.size());
		Time firstDepartureTime = listJF.get(0).getFirstDepartureTime();
		Time lastDepartureTime = listJF.get(0).getLastDepartureTime();
		Time fDTime = new Time((long) (firstDepartureTime.getTime() - 6000L));
		Time lDTime = new Time((long) (lastDepartureTime.getTime() + 6000L));
		

		
		listJF.get(1).setFirstDepartureTime(fDTime);
		listJF.get(1).setLastDepartureTime(lDTime);
		
		data.getVehicleJourneys().add(vj1);
		context.put(VALIDATION_DATA, data);
		context.put(VALIDATION, fullparameters);

		checkPoint.validate(context, null);

		ValidationReport report2 = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report2.getCheckPoints().size(), 0, " report must have items");
		
		CheckPointReport checkPointReport2 = report2.findCheckPointReportByName("3-VehicleJourney-6");
		Assert.assertNotNull(checkPointReport2, "report must contain a 3-VehicleJourney-6 checkPoint");

		Assert.assertEquals(checkPointReport2.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");

		utx.rollback();

	}
	
	@Test(groups = { "vehicleJourney" }, description = "3-VehicleJourney-7",priority=8)
	public void verifyTest3_7() throws Exception {
		// 3-VehicleJourney-7 : check if vehicle journey is included in its associated timeband
		log.info(Color.BLUE + "3-VehicleJourney-7" + Color.NORMAL);
		
		
		importLines("Neptune_With_Frequencies.xml", 1, 1, true);
		
		Context context = initValidatorContext();
		context.put(VALIDATION_REPORT, new ValidationReport());

		Assert.assertNotNull(fullparameters, "no parameters for test");

		

		utx.begin();
		em.joinTransaction();

		JourneyPattern bean = journeyPatternDao.findByObjectId("ratp:JourneyPattern:1000252_00");
		Assert.assertFalse(bean == null, "No data for test");
		JourneyPattern jp1 = bean;
		jp1.setObjectId("NINOXE:JourneyPattern:checkedJP");
		
		
		ValidationData data = new ValidationData();
		data.getVehicleJourneys().addAll(jp1.getVehicleJourneys());
		context.put(VALIDATION_DATA, data);
		context.put(VALIDATION, fullparameters);
		
		

		checkPoint.validate(context, null);
		
		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");
		
		CheckPointReport checkPointReport = report.findCheckPointReportByName("3-VehicleJourney-7");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-VehicleJourney-7 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.OK, " checkPointReport must be ok");
		
		context.put(VALIDATION_REPORT, new ValidationReport());
		
		
		VehicleJourney vj1 = null;
		
		for(VehicleJourney vj: jp1.getVehicleJourneys()) {
			if(vj.getObjectId().equalsIgnoreCase("ratp:VehicleJourney:514572940997334-2-2")) {
				vj1 = vj;
				break;
			}
		}
		
		List<JourneyFrequency> listJF = vj1.getJourneyFrequencies();
		
		
		JourneyFrequency jf = new JourneyFrequency();
		jf.setFirstDepartureTime(listJF.get(0).getFirstDepartureTime());
		jf.setLastDepartureTime(listJF.get(0).getLastDepartureTime());
		listJF.add(jf);
		
		Time firstDepartureTime = listJF.get(0).getTimeband().getStartTime();
		Time lastDepartureTime = listJF.get(0).getTimeband().getEndTime();
		
		Time fDTime = new Time((long) (firstDepartureTime.getTime() - 6000L));
		Time lDTime = new Time((long) (lastDepartureTime.getTime() + 6000L));
		
		listJF.get(0).setFirstDepartureTime(fDTime);
		listJF.get(0).setLastDepartureTime(lDTime);
		
		data.getVehicleJourneys().add(vj1);
		context.put(VALIDATION_DATA, data);
		context.put(VALIDATION, fullparameters);
		
		checkPoint.validate(context, null);
		
		Assert.assertNotNull(fullparameters, "no parameters for test");

		ValidationReport report2 = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report2.getCheckPoints().size(), 0, " report must have items");
		
		CheckPointReport checkPointReport2 = report2.findCheckPointReportByName("3-VehicleJourney-7");
		Assert.assertNotNull(checkPointReport2, "report must contain a 3-VehicleJourney-7 checkPoint");

		Assert.assertEquals(checkPointReport2.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");

		utx.rollback();

	}
	
	@Test(groups = { "vehicleJourney" }, description = "3-VehicleJourney-8",priority=9)
	public void verifyTest3_8() throws Exception {
		// 3-VehicleJourney-8 : check if some timesheet journey are included in frequency journeys
		log.info(Color.BLUE + "3-VehicleJourney-8" + Color.NORMAL);
		
		
		importLines("Neptune_With_Frequencies.xml", 1, 1, true);
		
		Context context = initValidatorContext();
		context.put(VALIDATION_REPORT, new ValidationReport());

		Assert.assertNotNull(fullparameters, "no parameters for test");

		

		utx.begin();
		em.joinTransaction();

		JourneyPattern bean = journeyPatternDao.findByObjectId("ratp:JourneyPattern:1000252_00");
		Assert.assertFalse(bean == null, "No data for test");
		JourneyPattern jp1 = bean;
		jp1.setObjectId("NINOXE:JourneyPattern:checkedJP");
		
		
		VehicleJourney vj1 = null;
		
		for(VehicleJourney vj: jp1.getVehicleJourneys()) {
			if(vj.getObjectId().equalsIgnoreCase("ratp:VehicleJourney:514572940997334-2-2")) {
				vj1 = vj;
				break;
			}
		}
		vj1.setObjectId("NINOXE:VehicleJourney:checkedVJ");
		VehicleJourney vj2 = null;
		
		for(VehicleJourney vj: jp1.getVehicleJourneys()) {
			if(vj.getObjectId().equalsIgnoreCase("ratp:VehicleJourney:514572940997334-2-1")) {
				vj2 = vj;
				break;
			}
		}
	
		vj2.setObjectId("NINOXE:VehicleJourney:checkedVJ2");
		
		
		ValidationData data = new ValidationData();
		data.getVehicleJourneys().addAll(jp1.getVehicleJourneys());
		context.put(VALIDATION_DATA, data);
		context.put(VALIDATION, fullparameters);

		checkPoint.validate(context, null);
		
		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");
		
		CheckPointReport checkPointReport = report.findCheckPointReportByName("3-VehicleJourney-8");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-VehicleJourney-8 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		
		
		List<VehicleJourney> filteredList = new ArrayList<VehicleJourney>();
		filteredList.add(vj1);
		filteredList.add(vj2);
		

		context.put(VALIDATION_REPORT, new ValidationReport());
		
		data.getVehicleJourneys().clear();
		data.getVehicleJourneys().addAll(filteredList);
		context.put(VALIDATION_DATA, data);
		context.put(VALIDATION, fullparameters);
		
		checkPoint.validate(context, null);
		
		
		Assert.assertNotNull(fullparameters, "no parameters for test");

		ValidationReport report2 = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report2.getCheckPoints().size(), 0, " report must have items");
		
		CheckPointReport checkPointReport2 = report2.findCheckPointReportByName("3-VehicleJourney-8");
		Assert.assertNotNull(checkPointReport2, "report must contain a 3-VehicleJourney-8 checkPoint");

		Assert.assertEquals(checkPointReport2.getState(), ValidationReporter.RESULT.OK, " checkPointReport must be ok");

		utx.rollback();

	}
	
	@Test(groups = { "vehicleJourney" }, description = "4-VehicleJourney-2",priority=10)
	public void verifyTest4_2() throws Exception {
		// 4-VehicleJourney-2 : check transport mode
		log.info(Color.BLUE +"4-VehicleJourney-2"+ Color.NORMAL);
		Context context = initValidatorContext();
		context.put(VALIDATION,fullparameters);

		Assert.assertNotNull(fullparameters, "no parameters for test");

		importLines("Ligne_OK.xml", 1, 1, true);

		utx.begin();
	    em.joinTransaction();
	    
		List<Line> beans = lineDao.findAll();
		Assert.assertFalse(beans.isEmpty(), "No data for test");
		Line line1 = beans.get(0);


		// line1 is model;
		line1.setObjectId("NINOXE:Line:modelLine");

		Route r1 = line1.getRoutes().get(0);
		JourneyPattern jp1 = r1.getJourneyPatterns().get(0);

		{ // check test not required when check is false
			ValidationData data = new ValidationData();
			context.put(VALIDATION_DATA, data);
			data.getVehicleJourneys().addAll(jp1.getVehicleJourneys());
			context.put(VALIDATION_REPORT, new ValidationReport());
			fullparameters.setCheckAllowedTransportModes(0);
			context.put(VALIDATION,fullparameters);
			checkPoint.validate(context, null);
			ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);

			Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

			CheckPointReport checkPointReport = report.findCheckPointReportByName("4-VehicleJourney-2");
			Assert.assertNull(checkPointReport, "report must not contain a 4-VehicleJourney-2 checkPoint");
		}



		{ // check test not required when mode is ok
			ValidationData data = new ValidationData();
			context.put(VALIDATION_DATA, data);
			data.getVehicleJourneys().addAll(jp1.getVehicleJourneys());
			context.put(VALIDATION_REPORT, new ValidationReport());
			fullparameters.setCheckAllowedTransportModes(1);
			fullparameters.getModeBus().setAllowedTransport(1);
			context.put(VALIDATION,fullparameters);
			checkPoint.validate(context, null);
			ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);

			Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

			CheckPointReport checkPointReport = report.findCheckPointReportByName("4-VehicleJourney-2");
			Assert.assertNotNull(checkPointReport, "report must contain a 4-VehicleJourney-2 checkPoint");
		}

		jp1.getVehicleJourneys().get(0).setTransportMode(TransportModeNameEnum.Bus);
		{ // check test not required when check is false
			ValidationData data = new ValidationData();
			context.put(VALIDATION_DATA, data);
			data.getVehicleJourneys().addAll(jp1.getVehicleJourneys());
			context.put(VALIDATION_REPORT, new ValidationReport());
			fullparameters.setCheckAllowedTransportModes(1);
			fullparameters.getModeBus().setAllowedTransport(0);
			context.put(VALIDATION,fullparameters);
			checkPoint.validate(context, null);
			ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);

			Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

			CheckPointReport checkPointReport = report.findCheckPointReportByName("4-VehicleJourney-2");
			Assert.assertNotNull(checkPointReport, "report must contain a 4-VehicleJourney-2 checkPoint");

			Assert.assertEquals(checkPointReport.getState(),
					ValidationReporter.RESULT.NOK,
					" checkPointReport must be nok");
			Assert.assertEquals(checkPointReport.getSeverity(),
					CheckPointReport.SEVERITY.ERROR,
					" checkPointReport must be on severity error");
			Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 1,
					" checkPointReport must have 1 item");
		}
		utx.rollback();

	}

}
