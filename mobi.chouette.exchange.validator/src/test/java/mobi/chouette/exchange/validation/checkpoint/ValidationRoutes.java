package mobi.chouette.exchange.validation.checkpoint;

import java.io.File;
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
import mobi.chouette.dao.LineDAO;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.CheckPointErrorReport;
import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.exchange.validator.DummyChecker;
import mobi.chouette.exchange.validator.JobDataTest;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
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
public class ValidationRoutes extends AbstractTestValidation {
	private RouteCheckPoints checkPoint = new RouteCheckPoints();
	private ValidationParameters fullparameters;
	private Route bean1;
	private Route bean2;
	private List<Route> beansFor4 = new ArrayList<>();

	@EJB 
	LineDAO lineDao;

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
				.addClass(ValidationRoutes.class);
		
		result = ShrinkWrap.create(EnterpriseArchive.class, "test.ear")
				.addAsLibraries(jars.toArray(new File[0]))
				.addAsModules(modules.toArray(new JavaArchive[0]))
				.addAsModule(testWar)
				.addAsResource(EmptyAsset.INSTANCE, "beans.xml");
		return result;
	}

	@BeforeGroups(groups = { "route" })
	public void init() {
		super.init();
		long id = 1;

		fullparameters = null;
		try {
			fullparameters = loadFullParameters();
			fullparameters.setCheckRoute(1);

			Line line = new Line();
			line.setId(id++);
			line.setObjectId("test1:Line:1");
			line.setName("test");
			bean1 = new Route();
			bean1.setId(id++);
			bean1.setObjectId("test1:Route:1");
			bean1.setName("test1");
			bean1.setLine(line);
			bean2 = new Route();
			bean2.setId(id++);
			bean2.setObjectId("test2:Route:1");
			bean2.setName("test2");
			bean2.setLine(line);

			beansFor4.add(bean1);
			beansFor4.add(bean2);
		} catch (Exception e) {
			fullparameters = null;
			e.printStackTrace();
		}

	}

	@Test(groups = { "route" }, description = "4-Route-1 no test", priority = 1)
	public void verifyTest4_1_notest() throws Exception {
		// 4-Route-1 : check columns
		log.info(Color.BLUE + "4-Route-1 no test" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");
		context.put(VALIDATION, fullparameters);
		context.put(VALIDATION_REPORT, new ValidationReport());

		fullparameters.setCheckRoute(0);
		ValidationData data = new ValidationData();
		data.getRoutes().addAll(beansFor4);
		context.put(VALIDATION_DATA, data);

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertTrue(report.findCheckPointReportByName("4-Route-1") == null, " report must not have item 4-Route-1");

		fullparameters.setCheckRoute(1);
		context.put(VALIDATION_REPORT, new ValidationReport());

		checkPoint.validate(context, null);
		report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertTrue(report.findCheckPointReportByName("4-Route-1") != null, " report must have item 4-Route-1");
		Assert.assertEquals(report.findCheckPointReportByName("4-Route-1").getCheckPointErrorCount(), 0,
				" checkpoint must have no detail");

	}

	@Test(groups = { "route" }, description = "4-Route-1 unicity", priority = 2)
	public void verifyTest4_1_unique() throws Exception {
		// 4-Route-1 : check columns
		log.info(Color.BLUE + "4-Route-1 unicity" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");

		context.put(VALIDATION, fullparameters);
		context.put(VALIDATION_REPORT, new ValidationReport());

		fullparameters.setCheckRoute(1);
		fullparameters.getRoute().getObjectId().setUnique(1);

		ValidationData data = new ValidationData();
		data.getRoutes().addAll(beansFor4);
		context.put(VALIDATION_DATA, data);

		checkPoint.validate(context, null);
		fullparameters.getRoute().getObjectId().setUnique(0);
		// unique
		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);

		List<CheckPointErrorReport> details = checkReportForTest(report, "4-Route-1", 3);
		for (CheckPointErrorReport detail : details) {
			Assert.assertEquals(detail.getReferenceValue(), "ObjectId", "detail must refer column");
			Assert.assertEquals(detail.getValue(), bean2.getObjectId().split(":")[2], "detail must refer value");
		}
	}

	@Test(groups = { "route" }, description = "3-Route-1", priority = 3)
	public void verifyTest3_1() throws Exception {
		// 3-Route-1 : check if two successive stops are in same area
		log.info(Color.BLUE + "3-Route-1" + Color.NORMAL);
		Context context = initValidatorContext();
		context.put(VALIDATION, fullparameters);
		context.put(VALIDATION_REPORT, new ValidationReport());

		Assert.assertNotNull(fullparameters, "no parameters for test");

		importLines("Ligne_OK.xml", 1, 1, true);

		utx.begin();
		em.joinTransaction();

		List<Line> beans = lineDao.findAll();
		Assert.assertFalse(beans.isEmpty(), "No data for test");
		Line line1 = beans.get(0);

		Route route1 = line1.getRoutes().get(0);
		route1.getStopPoints().get(1).setContainedInStopArea(route1.getStopPoints().get(0).getContainedInStopArea());

		ValidationData data = new ValidationData();
		context.put(VALIDATION_DATA, data);

		data.getRoutes().addAll(line1.getRoutes());

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

		CheckPointReport checkPointReport = report.findCheckPointReportByName("3-Route-1");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-Route-1 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(), CheckPointReport.SEVERITY.WARNING,
				" checkPointReport must be on level warning");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 1, " checkPointReport must have 1 item");

		String detailKey = "3-Route-1".replaceAll("-", "_").toLowerCase();
		List<CheckPointErrorReport> details = checkReportForTest(report,"3-Route-1",-1);
		for (CheckPointErrorReport detail : details) {
			Assert.assertTrue(detail.getKey().startsWith(detailKey),
					"details key should start with test key : expected " + detailKey + ", found : " + detail.getKey());
		}
		// check detail keys
		for (CheckPointErrorReport detail : details) {
			Assert.assertEquals(detail.getSource().getObjectId(), route1.getObjectId(),
					"route 1 must be source of error");
		}
		utx.rollback();
	}

	@Test(groups = { "route" }, description = "3-Route-2", priority = 4)
	public void verifyTest3_2() throws Exception {
		// 3-Route-2 : check if two wayback routes are actually waybacks
		log.info(Color.BLUE + "3-Route-2" + Color.NORMAL);
		Context context = initValidatorContext();
		context.put(VALIDATION, fullparameters);
		context.put(VALIDATION_REPORT, new ValidationReport());

		Assert.assertNotNull(fullparameters, "no parameters for test");

		importLines("Ligne_OK.xml", 1, 1, true);

		utx.begin();
		em.joinTransaction();

		List<Line> beans = lineDao.findAll();
		Assert.assertFalse(beans.isEmpty(), "No data for test");
		Line line1 = beans.get(0);

		Route route1 = line1.getRoutes().get(0);
		Route route2 = route1.getOppositeRoute();
		if (route2 == null) {
			route2 = line1.getRoutes().get(1);
			route1.setOppositeRoute(route2);
			route2.setOppositeRoute(route1);
		}

		StopArea area1 = route1.getStopPoints().get(1).getContainedInStopArea().getParent();
		StopArea area0 = route1.getStopPoints().get(0).getContainedInStopArea();
		area0.setParent(area1);

		ValidationData data = new ValidationData();
		context.put(VALIDATION_DATA, data);

		data.getRoutes().addAll(line1.getRoutes());

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

		CheckPointReport checkPointReport = report.findCheckPointReportByName("3-Route-2");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-Route-2 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(), CheckPointReport.SEVERITY.WARNING,
				" checkPointReport must be on level warning");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 2, " checkPointReport must have 2 item");
		String detailKey = "3-Route-2".replaceAll("-", "_").toLowerCase();
		List<CheckPointErrorReport> details = checkReportForTest(report,"3-Route-2",-1);
		for (CheckPointErrorReport detail : details) {
			Assert.assertTrue(detail.getKey().startsWith(detailKey),
					"details key should start with test key : expected " + detailKey + ", found : " + detail.getKey());
		}

		// check detail keys = route1 and route2 objectids
		boolean route1objectIdFound = false;
		boolean route2objectIdFound = false;
		for (CheckPointErrorReport detailReport : details) {

			if (detailReport.getSource().getObjectId().equals(route1.getObjectId()))
				route1objectIdFound = true;
			if (detailReport.getSource().getObjectId().equals(route2.getObjectId()))
				route2objectIdFound = true;
		}
		Assert.assertTrue(route1objectIdFound, "detail report must refer route 1");
		Assert.assertTrue(route2objectIdFound, "detail report must refer route 2");

		utx.rollback();

	}

	@Test(groups = { "route" }, description = "3-Route-3", priority = 5)
	public void verifyTest3_3() throws Exception {
		// 3-Route-3 : check distance between stops
		log.info(Color.BLUE + "3-Route-3" + Color.NORMAL);
		Context context = initValidatorContext();
		context.put(VALIDATION_REPORT, new ValidationReport());

		Assert.assertNotNull(fullparameters, "no parameters for test");

		importLines("Ligne_OK.xml", 1, 1, true);

		utx.begin();
		em.joinTransaction();

		List<Line> beans = lineDao.findAll();
		Assert.assertFalse(beans.isEmpty(), "No data for test");
		Line line1 = beans.get(0);

		line1.setTransportModeName(TransportModeNameEnum.Bus);
		Route route1 = line1.getRoutes().get(0);
		Route route2 = line1.getRoutes().get(1);

		StopArea area0 = route1.getStopPoints().get(0).getContainedInStopArea();
		double distanceMin = 10000000;
		double distanceMax = 0;
		for (int i = 1; i < route1.getStopPoints().size(); i++) {
			StopArea area1 = route1.getStopPoints().get(i).getContainedInStopArea();
			double distance = distance(area0, area1);
			if (distance > distanceMax)
				distanceMax = distance;
			if (distance < distanceMin)
				distanceMin = distance;
			area0 = area1;
		}
		area0 = route2.getStopPoints().get(0).getContainedInStopArea();
		for (int i = 1; i < route2.getStopPoints().size(); i++) {
			StopArea area1 = route2.getStopPoints().get(i).getContainedInStopArea();
			double distance = distance(area0, area1);
			if (distance > distanceMax)
				distanceMax = distance;
			if (distance < distanceMin)
				distanceMin = distance;
			area0 = area1;
		}

		fullparameters.getModeBus().setInterStopAreaDistanceMin((int) distanceMin + 10);
		fullparameters.getModeBus().setInterStopAreaDistanceMax((int) distanceMax - 10);
		context.put(VALIDATION, fullparameters);

		ValidationData data = new ValidationData();
		context.put(VALIDATION_DATA, data);

		data.getRoutes().addAll(line1.getRoutes());

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

		CheckPointReport checkPointReport = report.findCheckPointReportByName("3-Route-3");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-Route-3 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(), CheckPointReport.SEVERITY.WARNING,
				" checkPointReport must be on level warning");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 2, " checkPointReport must have 2 item");

		String detailKey = "3-Route-3".replaceAll("-", "_").toLowerCase();
		List<CheckPointErrorReport> details = checkReportForTest(report,"3-Route-3",-1);
		for (CheckPointErrorReport detail : details) {
			Assert.assertTrue(detail.getKey().startsWith(detailKey),
					"details key should start with test key : expected " + detailKey + ", found : " + detail.getKey());
		}
		// check detail keys
		boolean route1objectIdFound = false;
		boolean route2objectIdFound = false;
		for (CheckPointErrorReport detailReport : details) {

			if (detailReport.getSource().getObjectId().equals(route1.getObjectId()))
				route1objectIdFound = true;
			if (detailReport.getSource().getObjectId().equals(route2.getObjectId()))
				route2objectIdFound = true;
		}
		Assert.assertTrue(route1objectIdFound, "detail report must refer route 1");
		Assert.assertTrue(route2objectIdFound, "detail report must refer route 2");
		utx.rollback();

	}

	@Test(groups = { "route" }, description = "3-Route-4", priority = 6)
	public void verifyTest3_4() throws Exception {
		// 3-Route-4 : check identical routes
		log.info(Color.BLUE + "3-Route-4" + Color.NORMAL);
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

		Route route2 = new Route();
		route2.setLine(line1);

		route1.setObjectId("NINOXE:Route:original");
		route2.setObjectId("NINOXE:Route:copy");

		for (StopPoint point : route1.getStopPoints()) {
			StopPoint pointCopy = new StopPoint();
			pointCopy.setPosition(point.getPosition());
			pointCopy.setObjectId("NINOXE:StopPoint:copy" + point.getPosition());
			pointCopy.setContainedInStopArea(point.getContainedInStopArea());
			pointCopy.setRoute(route2);
		}

		context.put(VALIDATION, fullparameters);
		ValidationData data = new ValidationData();
		context.put(VALIDATION_DATA, data);
		data.getRoutes().addAll(line1.getRoutes());

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

		CheckPointReport checkPointReport = report.findCheckPointReportByName("3-Route-4");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-Route-4 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(), CheckPointReport.SEVERITY.WARNING,
				" checkPointReport must be on level warning");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 1, " checkPointReport must have 1 item");

		String detailKey = "3-Route-4".replaceAll("-", "_").toLowerCase();
		List<CheckPointErrorReport> details = checkReportForTest(report,"3-Route-4",-1);
		for (CheckPointErrorReport detail : details) {
			Assert.assertTrue(detail.getKey().startsWith(detailKey),
					"details key should start with test key : expected " + detailKey + ", found : " + detail.getKey());
		}
		boolean route1objectIdFound = false;
		boolean route2objectIdFound = false;
		for (CheckPointErrorReport detailReport : details) {

			if (detailReport.getSource().getObjectId().equals(route1.getObjectId()))
				route1objectIdFound = true;
			if (detailReport.getSource().getObjectId().equals(route2.getObjectId()))
				route2objectIdFound = true;
		}
		Assert.assertTrue(route1objectIdFound, "detail report must refer route 1");
		Assert.assertFalse(route2objectIdFound, "detail report must not refer route 2");
		utx.rollback();

	}

	@Test(groups = { "route" }, description = "3-Route-5", priority = 7)
	public void verifyTest3_5() throws Exception {
		// 3-Route-5 : check for potentially waybacks
		log.info(Color.BLUE + "3-Route-5" + Color.NORMAL);
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
		Route route2 = line1.getRoutes().get(1);
		route1.setObjectId("NINOXE:Route:first");
		route1.setOppositeRoute(null);
		route2.setObjectId("NINOXE:Route:second");
		route2.setOppositeRoute(null);

		context.put(VALIDATION, fullparameters);
		ValidationData data = new ValidationData();
		context.put(VALIDATION_DATA, data);
		data.getRoutes().addAll(line1.getRoutes());

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

		CheckPointReport checkPointReport = report.findCheckPointReportByName("3-Route-5");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-Route-5 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(), CheckPointReport.SEVERITY.WARNING,
				" checkPointReport must be on level warning");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 1, " checkPointReport must have 1 item");

		String detailKey = "3-Route-5".replaceAll("-", "_").toLowerCase();
		List<CheckPointErrorReport> details = checkReportForTest(report,"3-Route-5",-1);
		for (CheckPointErrorReport detail : details) {
			Assert.assertTrue(detail.getKey().startsWith(detailKey),
					"details key should start with test key : expected " + detailKey + ", found : " + detail.getKey());
		}
		boolean route1objectIdFound = false;
		boolean route2objectIdFound = false;
		for (CheckPointErrorReport detailReport : details) {

			if (detailReport.getSource().getObjectId().equals(route1.getObjectId()))
				route1objectIdFound = true;
			if (detailReport.getSource().getObjectId().equals(route2.getObjectId()))
				route2objectIdFound = true;
		}
		Assert.assertTrue(route1objectIdFound, "detail report must refer route 1");
		Assert.assertFalse(route2objectIdFound, "detail report must not refer route 2");
		utx.rollback();

	}

	@Test(groups = { "route" }, description = "3-Route-6", priority = 8)
	public void verifyTest3_6() throws Exception {
		// 3-Route-6 : check if route has minimum 2 StopPoints
		log.info(Color.BLUE + "3-Route-6" + Color.NORMAL);
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
		route1.getStopPoints().clear();

		route1.setObjectId("NINOXE:Route:first");

		context.put(VALIDATION, fullparameters);
		ValidationData data = new ValidationData();
		context.put(VALIDATION_DATA, data);
		data.getRoutes().addAll(line1.getRoutes());

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

		CheckPointReport checkPointReport = report.findCheckPointReportByName("3-Route-6");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-Route-6 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(), CheckPointReport.SEVERITY.ERROR,
				" checkPointReport must be on level warning");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 1, " checkPointReport must have 1 item");

		String detailKey = "3-Route-6".replaceAll("-", "_").toLowerCase();
		List<CheckPointErrorReport> details = checkReportForTest(report,"3-Route-6",-1);
		for (CheckPointErrorReport detail : details) {
			Assert.assertTrue(detail.getKey().startsWith(detailKey),
					"details key should start with test key : expected " + detailKey + ", found : " + detail.getKey());
		}
		boolean route1objectIdFound = false;
		for (CheckPointErrorReport detailReport : details) {

			if (detailReport.getSource().getObjectId().equals(route1.getObjectId()))
				route1objectIdFound = true;
		}
		Assert.assertTrue(route1objectIdFound, "detail report must refer route 1");
		utx.rollback();

	}

	@Test(groups = { "route" }, description = "3-Route-7", priority = 9)
	public void verifyTest3_7() throws Exception {
		// 3-Route-7 : check if route has minimum 1 JourneyPattern
		log.info(Color.BLUE + "3-Route-7" + Color.NORMAL);
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

		route1.getJourneyPatterns().clear();
		route1.setObjectId("NINOXE:Route:first");

		context.put(VALIDATION, fullparameters);
		ValidationData data = new ValidationData();
		context.put(VALIDATION_DATA, data);
		data.getRoutes().addAll(line1.getRoutes());

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

		CheckPointReport checkPointReport = report.findCheckPointReportByName("3-Route-7");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-Route-7 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(), CheckPointReport.SEVERITY.ERROR,
				" checkPointReport must be on level warning");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 1, " checkPointReport must have 1 item");

		String detailKey = "3-Route-7".replaceAll("-", "_").toLowerCase();
		List<CheckPointErrorReport> details = checkReportForTest(report,"3-Route-7",-1);
		for (CheckPointErrorReport detail : details) {
			Assert.assertTrue(detail.getKey().startsWith(detailKey),
					"details key should start with test key : expected " + detailKey + ", found : " + detail.getKey());
		}
		boolean route1objectIdFound = false;
		for (CheckPointErrorReport detailReport : details) {

			if (detailReport.getSource().getObjectId().equals(route1.getObjectId()))
				route1objectIdFound = true;
		}
		Assert.assertTrue(route1objectIdFound, "detail report must refer route 1");
		utx.rollback();

	}

	@Test(groups = { "route" }, description = "3-Route-8", priority = 10)
	public void verifyTest3_8() throws Exception {
		// 3-Route-8 : check if all stopPoints are used by journeyPatterns
		log.info(Color.BLUE + "3-Route-8" + Color.NORMAL);
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

		route1.getJourneyPatterns().get(0).removeStopPoint(route1.getJourneyPatterns().get(0).getStopPoints().get(0));
		route1.setObjectId("NINOXE:Route:first");

		context.put(VALIDATION, fullparameters);
		ValidationData data = new ValidationData();
		context.put(VALIDATION_DATA, data);
		data.getRoutes().addAll(line1.getRoutes());

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

		CheckPointReport checkPointReport = report.findCheckPointReportByName("3-Route-8");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-Route-8 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(), CheckPointReport.SEVERITY.WARNING,
				" checkPointReport must be on level warning");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 1, " checkPointReport must have 1 item");

		String detailKey = "3-Route-8".replaceAll("-", "_").toLowerCase();
		List<CheckPointErrorReport> details = checkReportForTest(report,"3-Route-8",-1);
		for (CheckPointErrorReport detail : details) {
			Assert.assertTrue(detail.getKey().startsWith(detailKey),
					"details key should start with test key : expected " + detailKey + ", found : " + detail.getKey());
		}
		boolean route1objectIdFound = false;
		for (CheckPointErrorReport detailReport : details) {

			if (detailReport.getSource().getObjectId().equals(route1.getObjectId()))
				route1objectIdFound = true;
		}
		Assert.assertTrue(route1objectIdFound, "detail report must refer route 1");
		utx.rollback();

	}

	@Test(groups = { "route" }, description = "3-Route-9", priority = 11)
	public void verifyTest3_9() throws Exception {
		// 3-Route-9 : check if one journeyPattern uses all stopPoints
		log.info(Color.BLUE + "3-Route-9" + Color.NORMAL);
		Context context = initValidatorContext();
		context.put(VALIDATION_REPORT, new ValidationReport());

		Assert.assertNotNull(fullparameters, "no parameters for test");

		importLines("3-Route-9.xml", 1, 1, true);

		utx.begin();
		em.joinTransaction();

		List<Line> beans = lineDao.findAll();
		Assert.assertFalse(beans.isEmpty(), "No data for test");
		Line line1 = beans.get(0);

		Route route1 = null;
		for (Route route : line1.getRoutes()) {
			if (route.getObjectId().equals("NINOXE:Route:15571498")) {
				route1 = route;
				break;
			}
		}
		Assert.assertNotNull(route1, "route NINOXE:Route:15571498 must be in 3-Route-9.xml");

		// route1.getJourneyPatterns().clear();
		route1.setObjectId("NINOXE:Route:first");

		context.put(VALIDATION, fullparameters);
		ValidationData data = new ValidationData();
		context.put(VALIDATION_DATA, data);
		data.getRoutes().addAll(line1.getRoutes());

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

		CheckPointReport checkPointReport = report.findCheckPointReportByName("3-Route-9");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-Route-9 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(), CheckPointReport.SEVERITY.WARNING,
				" checkPointReport must be on level warning");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 1, " checkPointReport must have 1 item");

		String detailKey = "3-Route-9".replaceAll("-", "_").toLowerCase();
		List<CheckPointErrorReport> details = checkReportForTest(report,"3-Route-9",-1);
		for (CheckPointErrorReport detail : details) {
			Assert.assertTrue(detail.getKey().startsWith(detailKey),
					"details key should start with test key : expected " + detailKey + ", found : " + detail.getKey());
		}
		boolean route1objectIdFound = false;
		for (CheckPointErrorReport detailReport : details) {
			log.warn(detailReport);
			if (detailReport.getSource().getObjectId().equals(route1.getObjectId()))
				route1objectIdFound = true;
		}
		Assert.assertTrue(route1objectIdFound, "detail report must refer route 1");
		utx.rollback();

	}

}
