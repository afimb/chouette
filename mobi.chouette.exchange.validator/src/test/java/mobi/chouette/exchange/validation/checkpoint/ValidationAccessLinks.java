package mobi.chouette.exchange.validation.checkpoint;

import java.io.File;
import java.math.BigDecimal;
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
import mobi.chouette.dao.AccessLinkDAO;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.CheckPointErrorReport;
import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.exchange.validator.DummyChecker;
import mobi.chouette.exchange.validator.JobDataTest;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.StopArea;

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
public class ValidationAccessLinks extends AbstractTestValidation {
	private AccessLinkCheckPoints checkPoint = new AccessLinkCheckPoints();
	private ValidationParameters fullparameters;
	private AccessLink bean1;
	private AccessLink bean2;
	private List<AccessLink> beansFor4 = new ArrayList<>();

	@EJB 
	AccessLinkDAO accessLinkDao;

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
				.addClass(ValidationAccessLinks.class);
		
		result = ShrinkWrap.create(EnterpriseArchive.class, "test.ear")
				.addAsLibraries(jars.toArray(new File[0]))
				.addAsModules(modules.toArray(new JavaArchive[0]))
				.addAsModule(testWar)
				.addAsResource(EmptyAsset.INSTANCE, "beans.xml");
		return result;
	}

	@BeforeGroups(groups = { "accessLink" })
	public void init() {
		super.init();
		long id = 1;

		fullparameters = null;
		try {
			fullparameters = loadFullParameters();
			fullparameters.setCheckAccessLink(1);

			StopArea contain = new StopArea();
			contain.setId(id++);
			contain.setObjectId("test1:AccessPoint:1");
			contain.setName("test1");

			AccessPoint point1 = new AccessPoint();
			point1.setId(id++);
			point1.setObjectId("test1:AccessPoint:1");
			point1.setName("test1");
			point1.setContainedIn(contain);
			bean1 = new AccessLink();
			bean1.setId(id++);
			bean1.setObjectId("test1:AccessLink:1");
			bean1.setAccessPoint(point1);
			bean1.setStopArea(contain);
			bean1.setName("test1");
			AccessPoint point2 = new AccessPoint();
			point2.setId(id++);
			point2.setObjectId("test1:AccessPoint:2");
			point2.setName("test2");
			point2.setContainedIn(contain);
			bean2 = new AccessLink();
			bean2.setId(id++);
			bean2.setObjectId("test2:AccessLink:1");
			bean2.setAccessPoint(point2);
			bean2.setStopArea(contain);
			bean2.setName("test2");

			beansFor4.add(bean1);
			beansFor4.add(bean2);
		} catch (Exception e) {
			fullparameters = null;
			e.printStackTrace();
		}

	}

	@Test(groups = { "accessLink" }, description = "4-AccessLink-1 no test", priority = 1)
	public void verifyTest4_1_notest() throws Exception {
		// 4-AccessLink-1 : check columns
		log.info(Color.BLUE + "4-AccessLink-1 no test" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");
		context.put(VALIDATION_REPORT, new ValidationReport());

		fullparameters.setCheckAccessLink(0);
		context.put(VALIDATION, fullparameters);
		ValidationData data = new ValidationData();
		data.getAccessLinks().addAll(beansFor4);
		context.put(VALIDATION_DATA, data);

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertTrue(report.findCheckPointReportByName("4-AccessLink-1") == null,
				" report must not have item 4-AccessLink-1");

		fullparameters.setCheckAccessLink(1);
		context.put(VALIDATION_REPORT, new ValidationReport());

		checkPoint.validate(context, null);
		report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertTrue(report.findCheckPointReportByName("4-AccessLink-1") != null,
				" report must have item 4-AccessLink-1");
		Assert.assertEquals(report.findCheckPointReportByName("4-AccessLink-1").getCheckPointErrorCount(), 0,
				" checkpoint must have no detail");

	}

	@Test(groups = { "accessLink" }, description = "4-AccessLink-1 unicity", priority = 2)
	public void verifyTest4_1_unique() throws Exception {
		// 4-AccessLink-1 : check columns
		log.info(Color.BLUE + "4-AccessLink-1 unicity" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");

		context.put(VALIDATION_REPORT, new ValidationReport());

		fullparameters.setCheckAccessLink(1);
		fullparameters.getAccessLink().getObjectId().setUnique(1);

		context.put(VALIDATION, fullparameters);
		ValidationData data = new ValidationData();
		data.getAccessLinks().addAll(beansFor4);
		context.put(VALIDATION_DATA, data);
		checkPoint.validate(context, null);
		fullparameters.getAccessLink().getObjectId().setUnique(0);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);

		List<CheckPointErrorReport> details = checkReportForTest(report, "4-AccessLink-1", 1);
		CheckPointErrorReport detail = details.get(0);
		Assert.assertEquals(detail.getReferenceValue(), "ObjectId", "detail must refer column");
		Assert.assertEquals(detail.getValue(), bean2.getObjectId().split(":")[2], "detail must refer value");
	}

	@Test(groups = { "accessLink" }, description = "3-AccessLink-1", priority = 3)
	public void verifyTest3_1() throws Exception {
		// 3-AccessLink-1 : check distance between ends of accessLink
		importLines("model.zip", 7, 7, true);
		log.info(Color.BLUE + "3-AccessLink-1" + Color.NORMAL);
		Context context = initValidatorContext();
		context.put(VALIDATION, fullparameters);
		context.put(VALIDATION_REPORT, new ValidationReport());
		Assert.assertNotNull(fullparameters, "no parameters for test");

		utx.begin();
		em.joinTransaction();

		List<AccessLink> beans = accessLinkDao.findAll();
		Assert.assertFalse(beans.isEmpty(), "No data for test");

		fullparameters.setInterAccessLinkDistanceMax(95);

		ValidationData data = new ValidationData();
		data.getAccessLinks().addAll(beans);
		context.put(VALIDATION_DATA, data);

		checkPoint.validate(context, null);
		fullparameters.setInterAccessLinkDistanceMax(120);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

		CheckPointReport checkPointReport = report.findCheckPointReportByName("3-AccessLink-1");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-AccessLink-1 checkPoint");
		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(), CheckPointReport.SEVERITY.WARNING,
				" checkPointReport must be on severity error");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 1, " checkPointReport must have 1 item");
		List<CheckPointErrorReport> details = checkReportForTest(report,"3-AccessLink-1",-1);
		String detailKey = "3-AccessLink-1".replaceAll("-", "_").toLowerCase();
		for (CheckPointErrorReport detail : details) {
			Assert.assertTrue(detail.getKey().startsWith(detailKey),
					"details key should start with test key : expected " + detailKey + ", found : " + detail.getKey());
		}
		utx.rollback();

	}

	@Test(groups = { "accessLink" }, description = "3-AccessLink-2", priority = 4)
	public void verifyTest3_2() throws Exception {
		// 3-AccessLink-2 : check distance of link against distance between
		// stops of accessLink

		log.info(Color.BLUE + "3-AccessLink-2" + Color.NORMAL);
		Context context = initValidatorContext();
		context.put(VALIDATION, fullparameters);
		context.put(VALIDATION_REPORT, new ValidationReport());
		Assert.assertNotNull(fullparameters, "no parameters for test");

		utx.begin();
		em.joinTransaction();

		List<AccessLink> beans = accessLinkDao.findAll();
		Assert.assertFalse(beans.isEmpty(), "No data for test");
		AccessLink link = beans.get(0);

		double distance = AbstractTestValidation.distance(link.getStopArea(), link.getAccessPoint());

		link.setLinkDistance(BigDecimal.valueOf(distance - 50));

		ValidationData data = new ValidationData();
		data.getAccessLinks().addAll(beans);
		context.put(VALIDATION_DATA, data);

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

		CheckPointReport checkPointReport = report.findCheckPointReportByName("3-AccessLink-2");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-AccessLink-2 checkPoint");
		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(), CheckPointReport.SEVERITY.WARNING,
				" checkPointReport must be on severity error");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 1, " checkPointReport must have 1 item");
		List<CheckPointErrorReport> details = checkReportForTest(report,"3-AccessLink-2",-1);
		String detailKey = "3-AccessLink-2".replaceAll("-", "_").toLowerCase();
		for (CheckPointErrorReport detail : details) {
			Assert.assertTrue(detail.getKey().startsWith(detailKey),
					"details key should start with test key : expected " + detailKey + ", found : " + detail.getKey());
		}
		utx.rollback();

	}

	@Test(groups = { "accessLink" }, description = "3-AccessLink-3", priority = 5)
	public void verifyTest3_3() throws Exception {
		// 3-AccessLink-3 : check speeds in accessLink
		log.info(Color.BLUE + "3-AccessLink-3" + Color.NORMAL);
		Context context = initValidatorContext();
		context.put(VALIDATION, fullparameters);
		context.put(VALIDATION_REPORT, new ValidationReport());
		Assert.assertNotNull(fullparameters, "no parameters for test");

		utx.begin();
		em.joinTransaction();

		List<AccessLink> beans = accessLinkDao.findAll();
		Assert.assertFalse(beans.isEmpty(), "No data for test");

		AccessLink link = null;
		for (AccessLink accessLink : beans) {
			if (accessLink.getObjectId().equals("NINOXE:AccessLink:7")) {
				link = accessLink;
				break;
			}
		}
		double distance = AbstractTestValidation.distance(link.getAccessPoint(), link.getStopArea());
		link.setLinkDistance(BigDecimal.valueOf(distance));
		link.getDefaultDuration().setTime(link.getDefaultDuration().getTime() - 150000);
		link.getOccasionalTravellerDuration().setTime(link.getDefaultDuration().getTime());
		link.getFrequentTravellerDuration().setTime(link.getDefaultDuration().getTime());
		link.getMobilityRestrictedTravellerDuration().setTime(link.getDefaultDuration().getTime());

		ValidationData data = new ValidationData();
		data.getAccessLinks().addAll(beans);
		context.put(VALIDATION_DATA, data);

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

		CheckPointReport checkPointReport = report.findCheckPointReportByName("3-AccessLink-3");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-AccessLink-3 checkPoint");
		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(), CheckPointReport.SEVERITY.WARNING,
				" checkPointReport must be on severity error");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 4, " checkPointReport must have 4 item");
		String detailKey = "3-AccessLink-3".replaceAll("-", "_").toLowerCase();
		List<CheckPointErrorReport> details = checkReportForTest(report,"3-AccessLink-3",-1);
		for (CheckPointErrorReport detail : details) {
			Assert.assertTrue(detail.getKey().startsWith(detailKey),
					"details key should start with test key : expected " + detailKey + ", found : " + detail.getKey());
		}
		utx.rollback();

	}

}
