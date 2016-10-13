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
import mobi.chouette.dao.ConnectionLinkDAO;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.CheckPointErrorReport;
import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.exchange.validator.DummyChecker;
import mobi.chouette.exchange.validator.JobDataTest;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;

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
public class ValidationConnectionLinks extends AbstractTestValidation {
	private ConnectionLinkCheckPoints checkPoint = new ConnectionLinkCheckPoints();
	private ValidationParameters fullparameters;
	private ConnectionLink bean1;
	private ConnectionLink bean2;
	private List<ConnectionLink> beansFor4 = new ArrayList<>();

	@EJB 
	ConnectionLinkDAO connectionLinkDao;

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
				.addClass(ValidationConnectionLinks.class);
		
		result = ShrinkWrap.create(EnterpriseArchive.class, "test.ear")
				.addAsLibraries(jars.toArray(new File[0]))
				.addAsModules(modules.toArray(new JavaArchive[0]))
				.addAsModule(testWar)
				.addAsResource(EmptyAsset.INSTANCE, "beans.xml");
		return result;
	}

	@BeforeGroups(groups = { "connectionLink" })
	public void init() {
		super.init();

		long id = 1;

		fullparameters = null;
		try {
			fullparameters = loadFullParameters();
			fullparameters.setCheckConnectionLink(1);

			bean1 = new ConnectionLink();
			bean1.setId(id++);
			bean1.setObjectId("test1:ConnectionLink:1");
			bean1.setName("test1");
			bean2 = new ConnectionLink();
			bean2.setId(id++);
			bean2.setObjectId("test2:ConnectionLink:1");
			bean2.setName("test2");

			beansFor4.add(bean1);
			beansFor4.add(bean2);
		} catch (Exception e) {
			fullparameters = null;
			e.printStackTrace();
		}

	}

	@Test(groups = { "connectionLink" }, description = "3-ConnectionLink-1", priority = 1)
	public void verifyTest3_1() throws Exception {
		// 3-ConnectionLink-1 : check distance between stops of connectionLink
		importLines("model.zip", 7, 7, true);
		log.info(Color.BLUE + "3-ConnectionLink-1" + Color.NORMAL);
		Context context = initValidatorContext();
		context.put(VALIDATION, fullparameters);
		context.put(VALIDATION_REPORT, new ValidationReport());
		Assert.assertNotNull(fullparameters, "no parameters for test");

		utx.begin();
		em.joinTransaction();

		List<ConnectionLink> beans = connectionLinkDao.findAll();
		Assert.assertFalse(beans.isEmpty(), "No data for test");

		fullparameters.setInterConnectionLinkDistanceMax(600);

		ValidationData data = new ValidationData();
		data.getConnectionLinks().addAll(beans);
		context.put(VALIDATION_DATA, data);

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

		CheckPointReport checkPointReport = report.findCheckPointReportByName("3-ConnectionLink-1");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-ConnectionLink-1 checkPoint");
		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(), CheckPointReport.SEVERITY.WARNING,
				" checkPointReport must be on severity error");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 1, " checkPointReport must have 1 item");
		String detailKey = "3-ConnectionLink-1".replaceAll("-", "_").toLowerCase();
		List<CheckPointErrorReport> details = checkReportForTest(report,"3-ConnectionLink-1",-1);
		for (CheckPointErrorReport detail : details) {
			Assert.assertTrue(detail.getKey().startsWith(detailKey),
					"details key should start with test key : expected " + detailKey + ", found : " + detail.getKey());
		}
		utx.rollback();

	}

	@Test(groups = { "connectionLink" }, description = "3-ConnectionLink-2", priority = 2)
	public void verifyTest3_2() throws Exception {
		// 3-ConnectionLink-2 : check distance of link against distance between
		// stops of connectionLink
		log.info(Color.BLUE + "3-ConnectionLink-2" + Color.NORMAL);
		Context context = initValidatorContext();
		context.put(VALIDATION, fullparameters);
		context.put(VALIDATION_REPORT, new ValidationReport());
		Assert.assertNotNull(fullparameters, "no parameters for test");

		utx.begin();
		em.joinTransaction();

		List<ConnectionLink> beans = connectionLinkDao.findAll();
		Assert.assertFalse(beans.isEmpty(), "No data for test");

		ConnectionLink link = beans.get(0);
		double distance = AbstractTestValidation.distance(link.getStartOfLink(), link.getEndOfLink());

		link.setLinkDistance(BigDecimal.valueOf(distance - 50));

		ValidationData data = new ValidationData();
		data.getConnectionLinks().addAll(beans);
		context.put(VALIDATION_DATA, data);

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

		CheckPointReport checkPointReport = report.findCheckPointReportByName("3-ConnectionLink-2");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-ConnectionLink-2 checkPoint");
		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(), CheckPointReport.SEVERITY.WARNING,
				" checkPointReport must be on severity error");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 1, " checkPointReport must have 1 item");
		String detailKey = "3-ConnectionLink-2".replaceAll("-", "_").toLowerCase();
		List<CheckPointErrorReport> details = checkReportForTest(report,"3-ConnectionLink-2",-1);
		for (CheckPointErrorReport detail : details) {
			Assert.assertTrue(detail.getKey().startsWith(detailKey),
					"details key should start with test key : expected " + detailKey + ", found : " + detail.getKey());
		}
		utx.rollback();

	}

	@Test(groups = { "connectionLink" }, description = "3-ConnectionLink-3", priority = 3)
	public void verifyTest3_3() throws Exception {
		// 3-ConnectionLink-3 : check speeds in connectionLink
		log.info(Color.BLUE + "3-ConnectionLink-3" + Color.NORMAL);
		Context context = initValidatorContext();
		context.put(VALIDATION, fullparameters);
		context.put(VALIDATION_REPORT, new ValidationReport());
		Assert.assertNotNull(fullparameters, "no parameters for test");

		utx.begin();
		em.joinTransaction();

		List<ConnectionLink> beans = connectionLinkDao.findAll();
		Assert.assertFalse(beans.isEmpty(), "No data for test");

		ConnectionLink link = null;
		for (ConnectionLink connectionLink : beans) {
			if (connectionLink.getObjectId().equals("NINOXE:ConnectionLink:15627089")) {
				link = connectionLink;
				break;
			}
		}

		link.getDefaultDuration().setTime(link.getDefaultDuration().getTime() - 600000);
		link.getOccasionalTravellerDuration().setTime(link.getOccasionalTravellerDuration().getTime() - 800000);
		link.getFrequentTravellerDuration().setTime(link.getFrequentTravellerDuration().getTime() - 600000);
		link.getMobilityRestrictedTravellerDuration().setTime(
				link.getMobilityRestrictedTravellerDuration().getTime() - 900000);

		ValidationData data = new ValidationData();
		data.getConnectionLinks().addAll(beans);
		context.put(VALIDATION_DATA, data);

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

		CheckPointReport checkPointReport = report.findCheckPointReportByName("3-ConnectionLink-3");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-ConnectionLink-3 checkPoint");
		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(), CheckPointReport.SEVERITY.WARNING,
				" checkPointReport must be on severity error");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 4, " checkPointReport must have 4 item");
		String detailKey = "3-ConnectionLink-3".replaceAll("-", "_").toLowerCase();
		List<CheckPointErrorReport> details = checkReportForTest(report,"3-ConnectionLink-3",-1);
		for (CheckPointErrorReport detail : details) {
			Assert.assertTrue(detail.getKey().startsWith(detailKey),
					"details key should start with test key : expected " + detailKey + ", found : " + detail.getKey());
		}
		utx.rollback();

	}

	@Test(groups = { "connectionLink" }, description = "4-ConnectionLink-1 no test", priority = 4)
	public void verifyTest4_1_notest() throws Exception {
		// 4-ConnectionLink-1 : check columns
		log.info(Color.BLUE + "4-ConnectionLink-1 no test" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");
		context.put(VALIDATION_REPORT, new ValidationReport());

		fullparameters.setCheckConnectionLink(0);
		context.put(VALIDATION, fullparameters);
		ValidationData data = new ValidationData();
		data.getConnectionLinks().addAll(beansFor4);
		context.put(VALIDATION_DATA, data);

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertTrue(report.findCheckPointReportByName("4-ConnectionLink-1") == null,
				" report must not have item 4-ConnectionLink-1");


		fullparameters.setCheckConnectionLink(1);
		context.put(VALIDATION_REPORT, new ValidationReport());

		checkPoint.validate(context, null);
		report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertTrue(report.findCheckPointReportByName("4-ConnectionLink-1") != null, " report must have item 4-ConnectionLink-1");
		Assert.assertEquals(report.findCheckPointReportByName("4-ConnectionLink-1").getCheckPointErrorCount(), 0,
				" checkpoint must have no detail");

	}

	@Test(groups = { "connectionLink" }, description = "4-ConnectionLink-1 unicity", priority = 5)
	public void verifyTest4_1_unique() throws Exception {
		// 4-ConnectionLink-1 : check columns
		log.info(Color.BLUE + "4-ConnectionLink-1 unicity" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");

		context.put(VALIDATION_REPORT, new ValidationReport());

		fullparameters.setCheckConnectionLink(1);
		fullparameters.getConnectionLink().getObjectId().setUnique(1);

		context.put(VALIDATION, fullparameters);
		ValidationData data = new ValidationData();
		data.getConnectionLinks().addAll(beansFor4);
		context.put(VALIDATION_DATA, data);
		checkPoint.validate(context, null);
		fullparameters.getConnectionLink().getObjectId().setUnique(0);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);

		List<CheckPointErrorReport> details = checkReportForTest(report, "4-ConnectionLink-1", 1);
		CheckPointErrorReport detail = details.get(0);
		Assert.assertEquals(detail.getReferenceValue(), "ObjectId", "detail must refer column");
		Assert.assertEquals(detail.getValue(), bean2.getObjectId().split(":")[2], "detail must refer value");
	}

	@Test(groups = { "connectionLink" }, description = "4-ConnectionLink-2", priority = 6)
	public void verifyTest4_2() throws Exception {
		// 4-connectionLink-2 : check targets
		log.info(Color.BLUE + "4-ConnectionLink-2" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");
		context.put(VALIDATION_REPORT, new ValidationReport());


		StopArea start = new StopArea();
		start.setId((long) 1);
		start.setObjectId("test:StopArea:1");
		start.setAreaType(ChouetteAreaEnum.StopPlace);
		StopArea end = new StopArea();
		end.setId((long) 2);
		end.setObjectId("test:StopArea:1");
		end.setAreaType(ChouetteAreaEnum.Quay);
		bean1.setStartOfLink(start);
		bean1.setEndOfLink(end);

		List<ConnectionLink> list = new ArrayList<>();
		list.add(bean1);

		fullparameters.setCheckConnectionLinkOnPhysical(0);
		context.put(VALIDATION, fullparameters);
		ValidationData data = new ValidationData();
		data.getConnectionLinks().addAll(list);
		context.put(VALIDATION_DATA, data);
		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertTrue(report.findCheckPointReportByName("4-ConnectionLink-2") == null,
				" report must not have item 4-ConnectionLink-2");

		fullparameters.setCheckConnectionLinkOnPhysical(1);
		start.setAreaType(ChouetteAreaEnum.BoardingPosition);
		context.put(VALIDATION, fullparameters);
		context.put(VALIDATION_REPORT, new ValidationReport());
		checkPoint.validate(context, null);
		fullparameters.setCheckConnectionLinkOnPhysical(0);

		report = (ValidationReport) context.get(VALIDATION_REPORT);
		CheckPointReport checkPointReport = report.findCheckPointReportByName("4-ConnectionLink-2");
		Assert.assertNotNull(checkPointReport, "report must contain a 4-ConnectionLink-2 checkPoint");
		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.OK, " checkPointReport must be ok");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 0, " checkPointReport must have 0 item");

		start.setAreaType(ChouetteAreaEnum.CommercialStopPoint);
		fullparameters.setCheckConnectionLinkOnPhysical(1);
		context.put(VALIDATION, fullparameters);
		context.put(VALIDATION_REPORT, new ValidationReport());
		checkPoint.validate(context, null);
		fullparameters.setCheckConnectionLinkOnPhysical(0);

		report = (ValidationReport) context.get(VALIDATION_REPORT);
		checkPointReport = report.findCheckPointReportByName("4-ConnectionLink-2");
		Assert.assertNotNull(checkPointReport, "report must contain a 4-ConnectionLink-2 checkPoint");
		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 1, " checkPointReport must have 0 item");

	}

}
