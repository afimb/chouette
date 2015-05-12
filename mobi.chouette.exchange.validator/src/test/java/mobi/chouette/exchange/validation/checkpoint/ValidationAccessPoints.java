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
import mobi.chouette.dao.AccessPointDAO;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validator.JobDataTest;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.StopArea;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

@Log4j
public class ValidationAccessPoints extends AbstractTestValidation {
	private AccessPointCheckPoints checkPoint = new AccessPointCheckPoints();
	private ValidationParameters fullparameters;
	private AccessPoint bean1;
	private AccessPoint bean2;
	private List<AccessPoint> beansFor4 = new ArrayList<>();

	@EJB
	AccessPointDAO accessPointDao;

	@PersistenceContext(unitName = "referential")
	EntityManager em;

	@Inject
	UserTransaction utx;

	@Deployment
	public static WebArchive createDeployment() {

		WebArchive result;

		File[] files = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeAndTestDependencies()
				.resolve("mobi.chouette:mobi.chouette.exchange.validator:3.0.0").withTransitivity().asFile();

		result = ShrinkWrap.create(WebArchive.class, "test.war").addAsWebInfResource("postgres-ds.xml")
				.addAsLibraries(files).addClass(JobDataTest.class).addClass(AbstractTestValidation.class)
				.addAsResource(EmptyAsset.INSTANCE, "beans.xml");
		return result;

	}

	@BeforeGroups(groups = { "accessPoint" })
	public void init() {
		super.init();
		long id = 1;

		fullparameters = null;
		try {
			fullparameters = loadFullParameters();
			fullparameters.setCheckAccessPoint(1);

			StopArea area1 = new StopArea();
			area1.setId(id++);
			area1.setObjectId("test1:StopArea:1");
			area1.setName("test1");
			bean1 = new AccessPoint();
			bean1.setId(id++);
			bean1.setObjectId("test1:AccessPoint:1");
			bean1.setName("test1");
			bean1.setContainedIn(area1);
			StopArea area2 = new StopArea();
			area2.setId(id++);
			area2.setObjectId("test1:StopArea:2");
			area2.setName("test2");
			bean2 = new AccessPoint();
			bean2.setId(id++);
			bean2.setObjectId("test2:AccessPoint:1");
			bean2.setName("test2");
			bean2.setContainedIn(area2);

			beansFor4.add(bean1);
			beansFor4.add(bean2);
		} catch (Exception e) {
			fullparameters = null;
			e.printStackTrace();
		}

	}

	@Test(groups = { "accessPoint" }, description = "4-AccessPoint-1 no test", priority =1)
	public void verifyTest4_1_notest() throws Exception {
		// 4-AccessPoint-1 : check columns
		log.info(Color.BLUE + "4-AccessPoint-1 no test" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");
		context.put(VALIDATION_REPORT, new ValidationReport());

		fullparameters.setCheckAccessPoint(0);
		context.put(VALIDATION, fullparameters);
		ValidationData data = new ValidationData();
		data.getAccessPoints().addAll(beansFor4);
		context.put(VALIDATION_DATA, data);

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertTrue(report.findCheckPointByName("4-AccessPoint-1") == null,
				" report must not have item 4-AccessPoint-1");

		fullparameters.setCheckAccessPoint(1);
		context.put(VALIDATION_REPORT, new ValidationReport());

		checkPoint.validate(context, null);
		report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertTrue(report.findCheckPointByName("4-AccessPoint-1") != null,
				" report must have item 4-AccessPoint-1");
		Assert.assertEquals(report.findCheckPointByName("4-AccessPoint-1").getDetailCount(), 0,
				" checkpoint must have no detail");

	}

	@Test(groups = { "accessPoint" }, description = "4-AccessPoint-1 unicity", priority =2)
	public void verifyTest4_1_unique() throws Exception {
		// 4-AccessPoint-1 : check columns
		log.info(Color.BLUE + "4-AccessPoint-1 unicity" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");

		context.put(VALIDATION_REPORT, new ValidationReport());

		fullparameters.setCheckAccessPoint(1);
		fullparameters.getAccessPoint().getObjectId().setUnique(1);

		context.put(VALIDATION, fullparameters);
		ValidationData data = new ValidationData();
		data.getAccessPoints().addAll(beansFor4);
		context.put(VALIDATION_DATA, data);
		checkPoint.validate(context, null);
		fullparameters.getConnectionLink().getObjectId().setUnique(0);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);

		List<Detail> details = checkReportForTest4_1(report, "4-AccessPoint-1", 1);
		Detail detail = details.get(0);
		Assert.assertEquals(detail.getReferenceValue(), "ObjectId", "detail must refer column");
		Assert.assertEquals(detail.getValue(), bean2.getObjectId().split(":")[2], "detail must refer value");
	}


	@Test(groups = { "accessPoint" }, description = "3-AccessPoint-1", priority =3)
	public void verifyTest3_1() throws Exception {
		// 3-AccessPoint-1 : check if all access points have geolocalization
		importLines("model.zip", 7, 7, true);
		log.info(Color.BLUE + "3-AccessPoint-1" + Color.NORMAL);
		Context context = initValidatorContext();
		context.put(VALIDATION, fullparameters);
		context.put(VALIDATION_REPORT, new ValidationReport());
		Assert.assertNotNull(fullparameters, "no parameters for test");

		utx.begin();
		em.joinTransaction();

		List<AccessPoint> beans = accessPointDao.findAll();
		Assert.assertFalse(beans.isEmpty(), "No data for test");

		AccessPoint access1 = beans.get(0);
		access1.setLongLatType(null);
		access1.setLongitude(null);

		ValidationData data = new ValidationData();
		data.getAccessPoints().addAll(beans);
		context.put(VALIDATION_DATA, data);

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

		CheckPoint checkPointReport = report.findCheckPointByName("3-AccessPoint-1");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-AccessPoint-1 checkPoint");
		Assert.assertEquals(checkPointReport.getState(), CheckPoint.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(), CheckPoint.SEVERITY.ERROR,
				" checkPointReport must be on severity error");
		Assert.assertEquals(checkPointReport.getDetailCount(), 1, " checkPointReport must have 1 item");
		utx.rollback();

	}
	@Test(groups = { "accessPoint" }, description = "3-AccessPoint-2", priority =4)
	public void verifyTest3_2() throws Exception {
		// 3-AccessPoint-2 :  check distance of access points with different name
		log.info(Color.BLUE + "3-AccessPoint-2" + Color.NORMAL);
		Context context = initValidatorContext();
		context.put(VALIDATION, fullparameters);
		context.put(VALIDATION_REPORT, new ValidationReport());
		Assert.assertNotNull(fullparameters, "no parameters for test");

		utx.begin();
		em.joinTransaction();

		List<AccessPoint> beans = accessPointDao.findAll();
		Assert.assertFalse(beans.isEmpty(), "No data for test");

		AccessPoint access1 = null;
		AccessPoint access2 = null;
		for (AccessPoint accessPoint : beans) {
			if (accessPoint.getObjectId().equals("NINOXE:AccessPoint:6"))
				access1 = accessPoint;
			if (accessPoint.getObjectId().equals("NINOXE:AccessPoint:7"))
				access2 = accessPoint;
		}

		Assert.assertNotNull(access1,"missing validation data : NINOXE:AccessPoint:6");
		Assert.assertNotNull(access2,"missing validation data : NINOXE:AccessPoint:7");
		
		access1.setLongitude(access2.getLongitude());
		access1.setLatitude(access2.getLatitude());

		ValidationData data = new ValidationData();
		data.getAccessPoints().addAll(beans);
		context.put(VALIDATION_DATA, data);

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

		CheckPoint checkPointReport = report.findCheckPointByName("3-AccessPoint-2");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-AccessPoint-2 checkPoint");
		Assert.assertEquals(checkPointReport.getState(), CheckPoint.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(), CheckPoint.SEVERITY.WARNING,
				" checkPointReport must be on severity error");
		Assert.assertEquals(checkPointReport.getDetailCount(), 2, " checkPointReport must have 2 item");
		utx.rollback();

	}

	@Test(groups = { "accessPoint" }, description = "3-AccessPoint-3", priority =5)
	public void verifyTest3_3() throws Exception {
		// 3-AccessPoint-3 : check distance with parents
		log.info(Color.BLUE + "3-AccessPoint-2" + Color.NORMAL);
		Context context = initValidatorContext();
		context.put(VALIDATION, fullparameters);
		context.put(VALIDATION_REPORT, new ValidationReport());
		Assert.assertNotNull(fullparameters, "no parameters for test");

		utx.begin();
		em.joinTransaction();

		List<AccessPoint> beans = accessPointDao.findAll();
		Assert.assertFalse(beans.isEmpty(), "No data for test");

		fullparameters.setParentStopAreaDistanceMax( 50);

		ValidationData data = new ValidationData();
		data.getAccessPoints().addAll(beans);
		context.put(VALIDATION_DATA, data);

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

		CheckPoint checkPointReport = report.findCheckPointByName("3-AccessPoint-3");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-AccessPoint-3 checkPoint");
		Assert.assertEquals(checkPointReport.getState(), CheckPoint.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(), CheckPoint.SEVERITY.WARNING,
				" checkPointReport must be on severity error");
		Assert.assertEquals(checkPointReport.getDetailCount(), 1, " checkPointReport must have 1 item");
		utx.rollback();
	}


}