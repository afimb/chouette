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
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;

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
public class ValidationJourneyPatterns extends AbstractTestValidation {
	private JourneyPatternCheckPoints checkPoint = new JourneyPatternCheckPoints();
	private ValidationParameters fullparameters;
	private JourneyPattern bean1;
	private JourneyPattern bean2;
	private List<JourneyPattern> beansFor4 = new ArrayList<>();

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
				.resolve("mobi.chouette:mobi.chouette.exchange.validation").withTransitivity().asFile();
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
				.addClass(ValidationJourneyPatterns.class);
		
		result = ShrinkWrap.create(EnterpriseArchive.class, "test.ear")
				.addAsLibraries(jars.toArray(new File[0]))
				.addAsModules(modules.toArray(new JavaArchive[0]))
				.addAsModule(testWar)
				.addAsResource(EmptyAsset.INSTANCE, "beans.xml");
		return result;
	}

	@BeforeGroups(groups = { "journeyPattern" })
	public void init() {
		super.init();

		long id = 1;

		fullparameters = null;
		try {
			fullparameters = loadFullParameters();
			fullparameters.setCheckJourneyPattern(1);

			Line line = new Line();
			line.setId(id++);
			line.setObjectId("test1:Line:1");
			line.setName("test");
			Route route = new Route();
			route.setId(id++);
			route.setObjectId("test1:Route:1");
			route.setName("test1");
			route.setLine(line);
			bean1 = new JourneyPattern();
			bean1.setId(id++);
			bean1.setObjectId("test1:JourneyPattern:1");
			bean1.setName("test1");
			bean1.setRoute(route);
			bean2 = new JourneyPattern();
			bean2.setId(id++);
			bean2.setObjectId("test2:JourneyPattern:1");
			bean2.setName("test2");
			bean2.setRoute(route);

			beansFor4.add(bean1);
			beansFor4.add(bean2);
		} catch (Exception e) {
			fullparameters = null;
			e.printStackTrace();
		}

	}

	@Test(groups = { "journeyPattern" }, description = "4-JourneyPattern-1 no test", priority = 1)
	public void verifyTest4_1_notest() throws Exception {
		// 4-JourneyPattern-1 : check columns
		log.info(Color.BLUE + "4-JourneyPattern-1 no test" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");
		context.put(VALIDATION, fullparameters);
		context.put(VALIDATION_REPORT, new ValidationReport());

		fullparameters.setCheckJourneyPattern(0);
		ValidationData data = new ValidationData();
		data.getJourneyPatterns().addAll(beansFor4);
		context.put(VALIDATION_DATA, data);

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertTrue(report.findCheckPointByName("4-JourneyPattern-1") == null, " report must not have item 4-JourneyPattern-1");

		fullparameters.setCheckJourneyPattern(1);

		context.put(VALIDATION_REPORT, new ValidationReport());

		checkPoint.validate(context, null);
		report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertTrue(report.findCheckPointByName("4-JourneyPattern-1") != null, " report must have item 4-JourneyPattern-1");
		Assert.assertEquals(report.findCheckPointByName("4-JourneyPattern-1").getDetailCount(), 0,
				" checkpoint must have no detail");

	}

	@Test(groups = { "journeyPattern" }, description = "4-JourneyPattern-1 unicity", priority = 2)
	public void verifyTest4_1_unique() throws Exception {
		// 4-JourneyPattern-1 : check columns
		log.info(Color.BLUE + "4-JourneyPattern-1 unicity" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");

		context.put(VALIDATION, fullparameters);
		context.put(VALIDATION_REPORT, new ValidationReport());

		fullparameters.setCheckJourneyPattern(1);
		fullparameters.getJourneyPattern().getObjectId().setUnique(1);

		ValidationData data = new ValidationData();
		data.getJourneyPatterns().addAll(beansFor4);
		context.put(VALIDATION_DATA, data);

		checkPoint.validate(context, null);
		fullparameters.getRoute().getObjectId().setUnique(0);
		// unique
		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);

		List<Detail> details = checkReportForTest4_1(report, "4-JourneyPattern-1", 3);
		for (Detail detail : details) {
			Assert.assertEquals(detail.getReferenceValue(), "ObjectId", "detail must refer column");
			Assert.assertEquals(detail.getValue(), bean2.getObjectId().split(":")[2], "detail must refer value");
		}
	}

	@Test(groups = { "journeyPattern" }, description = "3-JourneyPattern-1", priority = 3)
	public void verifyTest3_1() throws Exception {
		// 3-JourneyPattern-1 : check if two journey patterns use same stops
		log.info(Color.BLUE + "3-JourneyPattern-1" + Color.NORMAL);
		Context context = initValidatorContext();
		context.put(VALIDATION, fullparameters);
		context.put(VALIDATION_REPORT, new ValidationReport());

		Assert.assertNotNull(fullparameters, "no parameters for test");

		importLines("3-JourneyPattern-1.xml", 1, 1, true);

		utx.begin();
		em.joinTransaction();

		List<Line> beans = lineDao.findAll();
		Assert.assertFalse(beans.isEmpty(), "No data for test");
		Line line1 = beans.get(0);

		Route route1 = null;
		
		ValidationData data = new ValidationData();
		for (Route route : line1.getRoutes()) {
			data.getJourneyPatterns().addAll(route.getJourneyPatterns());
			if (route.getJourneyPatterns().size() == 2)
			{
				route1 = route;
			}
		}

		route1.setObjectId("NINOXE:Route:checkedRoute");
		JourneyPattern jp1 = route1.getJourneyPatterns().get(0);
		
		jp1.setObjectId("NINOXE:JourneyPattern:checkedJP");

		context.put(VALIDATION_DATA, data);

		data.getRoutes().addAll(line1.getRoutes());

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");
		
		CheckPoint checkPointReport = report.findCheckPointByName("3-JourneyPattern-1");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-JourneyPattern-1 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), CheckPoint.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(), CheckPoint.SEVERITY.WARNING,
				" checkPointReport must be on level warning");
		Assert.assertEquals(checkPointReport.getDetailCount(), 1, " checkPointReport must have 1 item");

		// check detail keys
		String detailKey = "3-JourneyPattern-1".replaceAll("-", "_").toLowerCase();
		for (Detail detail : checkPointReport.getDetails()) {
			Assert.assertTrue(detail.getKey().startsWith(detailKey),
					"details key should start with test key : expected " + detailKey + ", found : " + detail.getKey());
			Assert.assertEquals(detail.getSource().getObjectId(), jp1.getObjectId(),
					"jp 1 must be source of error");
		}
		utx.rollback();

	}

}
