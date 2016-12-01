package mobi.chouette.exchange.validation.checkpoint;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.core.ChouetteException;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.CheckPointErrorReport;
import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.exchange.validator.DummyChecker;
import mobi.chouette.exchange.validator.JobDataTest;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
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
import org.testng.annotations.Test;

@Log4j
public class ValidationLines extends AbstractTestValidation
{
	private LineCheckPoints checkPoint = new LineCheckPoints();
	private SharedLineCheckPoints sharedCheckPointReport = new SharedLineCheckPoints();
	private ValidationParameters fullparameters;
	private Line bean1;
	private Line bean2;
	private Line bean3;

	@EJB 
	LineDAO lineDao;

    @PersistenceContext (unitName = "referential")
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
				.addClass(ValidationLines.class);
		
		result = ShrinkWrap.create(EnterpriseArchive.class, "test.ear")
				.addAsLibraries(jars.toArray(new File[0]))
				.addAsModules(modules.toArray(new JavaArchive[0]))
				.addAsModule(testWar)
				.addAsResource(EmptyAsset.INSTANCE, "beans.xml");
		return result;
	}

	@Override
	public void init()
	{
		super.init();
		long id = 1;

		fullparameters = null;
		try
		{
			fullparameters = loadFullParameters();
			fullparameters.setCheckLine(1);

			bean1 = new Line();
			bean1.setId(id++);
			bean1.setObjectId("test1:Line:1");
			bean1.setName("test1");
			bean2 = new Line();
			bean2.setId(id++);
			bean2.setObjectId("test2:Line:1");
			bean2.setName("test2");
			bean3 = new Line();
			bean3.setId(id++);
			bean3.setObjectId("test3:Line:1");
			bean3.setName("test3");

		} 
		catch (Exception e)
		{
			fullparameters = null;
			e.printStackTrace();
		}

	}

	@Test(groups = { "line" }, description = "4-Line-1 no test",priority=1)
	public void verifyTest4_1_notest() throws ChouetteException
	{
		// 4-Line-1 : check columns
		log.info(Color.BLUE +"4-Line-1 no test"+ Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");
		context.put(VALIDATION,fullparameters);
		context.put(VALIDATION_REPORT, new ValidationReport());

		fullparameters.setCheckLine(0);
		ValidationData data = new ValidationData();
		data.setCurrentLine(bean1);
		context.put(VALIDATION_DATA, data);

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertTrue(report.findCheckPointReportByName("4-Line-1") == null, " report must not have item 4-Line-1");

		fullparameters.setCheckLine(1);
		context.put(VALIDATION_REPORT, new ValidationReport());

		checkPoint.validate(context, null);
		report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertTrue(report.findCheckPointReportByName("4-Line-1") != null, " report must have item 4-Line-1");
		Assert.assertEquals(report.findCheckPointReportByName("4-Line-1").getCheckPointErrorCount(), 0, " checkpoint must have no detail");

	}

	@Test(groups = { "line" }, description = "4-Line-1 unicity",priority=2)
	public void verifyTest4_1_unique() throws ChouetteException
	{
		// 4-Line-1 : check columns
		log.info(Color.BLUE +"4-Line-1 unicity"+ Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");

		context.put(VALIDATION,fullparameters);
		context.put(VALIDATION_REPORT, new ValidationReport());

		fullparameters.getLine().getObjectId().setUnique(1);
		ValidationData data = new ValidationData();
		context.put(VALIDATION_DATA, data);

		data.setCurrentLine(bean1);
		checkPoint.validate(context, null);
		data.setCurrentLine(bean2);
		checkPoint.validate(context, null);
		fullparameters.getLine().getObjectId().setUnique(0);
		// unique
		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);

		List<CheckPointErrorReport> details = checkReportForTest(report,"4-Line-1",1);
		CheckPointErrorReport detail = details.get(0);
		Assert.assertEquals(detail.getReferenceValue(),"ObjectId","detail must refer column");
		Assert.assertEquals(detail.getValue(),bean2.getObjectId().split(":")[2],"detail must refer value");
		Assert.assertEquals(detail.getSource().getObjectId(),bean2.getObjectId(),"detail must refer second bean as source");
		Assert.assertEquals(detail.getTargets().get(0).getObjectId(),bean1.getObjectId(),"detail must refer fisrt bean as target");
	}


	@Test(groups = { "line" }, description = "3-Line-1",priority=3)
	public void verifyTest3_1() throws Exception
	{
		// 3-Line-1 : check if two lines have same name
		log.info(Color.BLUE +"3-Line-1"+ Color.NORMAL);
		Context context = initValidatorContext();

		Assert.assertNotNull(fullparameters, "no parameters for test");

		bean1.setObjectId("NINOXE:Line:modelLine");
		bean2.setObjectId("NINOXE:Line:wrongLine");
		bean3.setObjectId("NINOXE:Line:goodLine");

		Network network1 = new Network();
		network1.setId(1L);
		network1.setObjectId("NINOXE:GroupOfLine:testNetwork1");
		network1.setName("test network1");
		bean1.setNetwork(network1);
		bean2.setNetwork(network1);
		Network network2 = new Network();
		network2.setId(2L);
		network2.setObjectId("NINOXE:GroupOfLine:testNetwork2");
		network2.setName("test network2");
		bean3.setNetwork(network2);

		bean1.setName("line");
		bean2.setName("line");
		bean3.setName("line");

		context.put(VALIDATION,fullparameters);
		context.put(VALIDATION_REPORT, new ValidationReport());

		ValidationData data = new ValidationData();
		context.put(VALIDATION_DATA, data);

		Set<Line> lines = new HashSet<>();
		lines.add(bean1);
		lines.add(bean2);
		lines.add(bean3);
		data.setLines(lines);
		sharedCheckPointReport.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

		CheckPointReport checkPointReport = report.findCheckPointReportByName("3-Line-1");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-Line-1 checkPoint");

		Assert.assertEquals(checkPointReport.getState(),
				ValidationReporter.RESULT.NOK,
				" checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(),
				CheckPointReport.SEVERITY.WARNING,
				" checkPointReport must be on severity warning");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 2,
				" checkPointReport must have 2 item");

		List<CheckPointErrorReport> details = checkReportForTest(report,"3-Line-1",-1);
		for (CheckPointErrorReport detail : details) {
			log.warn(detail);
		}
		String detailKey = "3-Line-1".replaceAll("-", "_").toLowerCase();
		for (CheckPointErrorReport detail : details) {
			Assert.assertTrue(detail.getKey().startsWith(detailKey),
					"details key should start with test key : expected " + detailKey + ", found : " + detail.getKey());
		}
		// check detail keys = line1 and line2 objectids
		boolean line1objectIdFound = false;
		boolean line2objectIdFound = false;
		boolean line3objectIdFound = false;
		for (CheckPointErrorReport detailReport : details)
		{
			if (detailReport.getSource().getObjectId().equals(bean1.getObjectId()))
				line1objectIdFound = true;
			if (detailReport.getSource().getObjectId().equals(bean2.getObjectId()))
				line2objectIdFound = true;
			if (detailReport.getSource().getObjectId().equals(bean3.getObjectId()))
				line3objectIdFound = true;
		}
		Assert.assertTrue(line1objectIdFound,
				"detail report must refer line 1");
		Assert.assertTrue(line2objectIdFound,
				"detail report must refer line 2");
		Assert.assertFalse(line3objectIdFound,
				"detail report must not refer line 3");
	}

	@Test(groups = { "line" }, description = "3-Line-2",priority=4)
	public void verifyTest3_2() throws Exception
	{
		// 3-Line-2 : check if line has routes
		log.info(Color.BLUE +"3-Line-2"+ Color.NORMAL);
		Context context = initValidatorContext();
		context.put(VALIDATION,fullparameters);
		context.put(VALIDATION_REPORT, new ValidationReport());

		Assert.assertNotNull(fullparameters, "no parameters for test");

		importLines("Ligne_OK.xml", 1, 1, true);

		utx.begin();
	    em.joinTransaction();
	    
		List<Line> beans = lineDao.findAll();
		Assert.assertFalse(beans.isEmpty(), "No data for test");
		Line line1 = beans.get(0);

		// line1 is model;
		line1.setObjectId("NINOXE:Line:modelLine");

		line1.getRoutes().clear();

		ValidationData data = new ValidationData();
		context.put(VALIDATION_DATA, data);

		data.setCurrentLine(line1);
		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

		CheckPointReport checkPointReport = report.findCheckPointReportByName("3-Line-2");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-Line-2 checkPoint");

		Assert.assertEquals(checkPointReport.getState(),
				ValidationReporter.RESULT.NOK,
				" checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(),
				CheckPointReport.SEVERITY.ERROR,
				" checkPointReport must be on severity error");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 1,
				" checkPointReport must have 1 item");

		String detailKey = "3-Line-2".replaceAll("-", "_").toLowerCase();
		List<CheckPointErrorReport> details = checkReportForTest(report,"3-Line-2",-1);
		for (CheckPointErrorReport detail : details) {
			Assert.assertTrue(detail.getKey().startsWith(detailKey),
					"details key should start with test key : expected " + detailKey + ", found : " + detail.getKey());
		}
		for (CheckPointErrorReport detail : details) {
			log.warn(detail);
			Assert.assertEquals(detail.getSource().getObjectId(),line1.getObjectId(), "line must be source of error");
		}

		utx.rollback();

	}

	@Test(groups = { "Line" }, description = "4-Line-2",priority=5)
	public void verifyTest4_2() throws Exception
	{
		// 4-Line-2 : check transport mode
		log.info(Color.BLUE +"4-Line-2"+ Color.NORMAL);
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
		ValidationData data = new ValidationData();
		context.put(VALIDATION_DATA, data);
		data.setCurrentLine(line1);

		{ // check test not required when check is false
			context.put(VALIDATION_REPORT, new ValidationReport());
			fullparameters.setCheckAllowedTransportModes(0);
			context.put(VALIDATION,fullparameters);
			checkPoint.validate(context, null);
			ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);

			Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

			CheckPointReport checkPointReport = report.findCheckPointReportByName("4-Line-2");
			Assert.assertNull(checkPointReport, "report must not contain a 4-Line-2 checkPoint");

		}

		{ // check test required when check is true
			context.put(VALIDATION_REPORT, new ValidationReport());
			fullparameters.setCheckAllowedTransportModes(1);
			context.put(VALIDATION,fullparameters);
			checkPoint.validate(context, null);
			ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
			
			Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

			CheckPointReport checkPointReport = report.findCheckPointReportByName("4-Line-2");
			Assert.assertNotNull(checkPointReport, "report must contain a 4-Line-2 checkPoint");
		}

		{ // check test detect invalid transport mode
			line1.setTransportModeName(TransportModeNameEnum.Bus);
			context.put(VALIDATION_REPORT, new ValidationReport());
			fullparameters.getModeBus().setAllowedTransport(0);
			context.put(VALIDATION,fullparameters);
			checkPoint.validate(context, null);
			
			ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
			
			Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

			CheckPointReport checkPointReport = report.findCheckPointReportByName("4-Line-2");
			Assert.assertNotNull(checkPointReport, "report must contain a 4-Line-2 checkPoint");

			Assert.assertEquals(checkPointReport.getState(),
					ValidationReporter.RESULT.NOK,
					" checkPointReport must be nok");
			Assert.assertEquals(checkPointReport.getSeverity(),
					CheckPointReport.SEVERITY.ERROR,
					" checkPointReport must be on severity error");
			Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 1,
					" checkPointReport must have 1 item");

			List<CheckPointErrorReport> details = checkReportForTest(report,"4-Line-2",-1);
			for (CheckPointErrorReport detail : details) {
				log.warn(detail);
				Assert.assertEquals(detail.getSource().getObjectId(),line1.getObjectId(), "line must be source of error");
			}
		}
		utx.rollback();

	}
	
	@Test(groups = { "Line" }, description = "4-Line-3",priority=6)
	public void verifyTest4_3() throws Exception
	{
		// 4-Line-3 : check transport mode
		log.info(Color.BLUE +"4-Line-3"+ Color.NORMAL);
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
		ValidationData data = new ValidationData();
		context.put(VALIDATION_DATA, data);
		data.setCurrentLine(line1);
		
		{ // check test not required when check is false
			context.put(VALIDATION_REPORT, new ValidationReport());
			fullparameters.setCheckLinesInGroups(0);
			context.put(VALIDATION,fullparameters);
			checkPoint.validate(context, null);
			ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);

			Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

			CheckPointReport checkPointReport = report.findCheckPointReportByName("4-Line-3");
			Assert.assertNull(checkPointReport, "report must not contain a 4-Line-3 checkPoint");
		}



		GroupOfLine grp = new GroupOfLine();
		{ // check test not required when check is false
			context.put(VALIDATION_REPORT, new ValidationReport());
			grp.addLine(line1);
			fullparameters.setCheckLinesInGroups(1);
			context.put(VALIDATION,fullparameters);
			checkPoint.validate(context, null);
			ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);

			Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

			CheckPointReport checkPointReport = report.findCheckPointReportByName("4-Line-3");
			Assert.assertNotNull(checkPointReport, "report must  contain a 4-Line-3 checkPoint");
		}

		{
			context.put(VALIDATION_REPORT, new ValidationReport());
			grp.removeLine(line1);
			line1.getGroupOfLines().clear();
			fullparameters.setCheckLinesInGroups(1);
			context.put(VALIDATION,fullparameters);
			checkPoint.validate(context, null);
			ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);

			Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

			CheckPointReport checkPointReport = report.findCheckPointReportByName("4-Line-3");
			Assert.assertNotNull(checkPointReport, "report must  contain a 4-Line-3 checkPoint");
			Assert.assertEquals(checkPointReport.getState(),
					ValidationReporter.RESULT.NOK,
					" checkPointReport must be nok");
			Assert.assertEquals(checkPointReport.getSeverity(),
					CheckPointReport.SEVERITY.ERROR,
					" checkPointReport must be on severity error");
			Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 1,
					" checkPointReport must have 1 item");

			List<CheckPointErrorReport> details = checkReportForTest(report,"4-Line-3",-1);
			for (CheckPointErrorReport detail : details) {
				log.warn(detail);
				Assert.assertEquals(detail.getSource().getObjectId(),line1.getObjectId(), "line must be source of error");
			}
		}
		utx.rollback();


	}
	
	@Test(groups = { "Line" }, description = "4-Line-4",priority=7)
	public void verifyTest4_4() throws Exception
	{
		// 4-Line-4 : check routes
		log.info(Color.BLUE +"4-Line-4"+ Color.NORMAL);
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
		ValidationData data = new ValidationData();
		context.put(VALIDATION_DATA, data);
		data.setCurrentLine(line1);



		{ // check test not required when check is false
			context.put(VALIDATION_REPORT, new ValidationReport());
			fullparameters.setCheckLineRoutes(0);
			context.put(VALIDATION,fullparameters);
			checkPoint.validate(context, null);
			ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);

			Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

			CheckPointReport checkPointReport = report.findCheckPointReportByName("4-Line-4");
			Assert.assertNull(checkPointReport, "report must not contain a 4-Line-4 checkPoint");
		}

		{ // check 2 routes in wayback
			context.put(VALIDATION_REPORT, new ValidationReport());
			fullparameters.setCheckLineRoutes(1);
			context.put(VALIDATION,fullparameters);
			checkPoint.validate(context, null);
			ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
			
			Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

			CheckPointReport checkPointReport = report.findCheckPointReportByName("4-Line-4");
			Assert.assertNotNull(checkPointReport, "report must contain a 4-Line-4 checkPoint");
		}

		line1.getRoutes().remove(1);
		{ // check 1 route
			context.put(VALIDATION_REPORT, new ValidationReport());
			fullparameters.setCheckLineRoutes(1);
			context.put(VALIDATION,fullparameters);
			checkPoint.validate(context, null);
			ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
			
			Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

			CheckPointReport checkPointReport = report.findCheckPointReportByName("4-Line-4");
			Assert.assertNotNull(checkPointReport, "report must contain a 4-Line-4 checkPoint");
		}

		line1.getRoutes().add(new Route());
		{
			context.put(VALIDATION_REPORT, new ValidationReport());
			fullparameters.setCheckLineRoutes(1);
			context.put(VALIDATION,fullparameters);
			checkPoint.validate(context, null);
			
			ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
			
			Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

			CheckPointReport checkPointReport = report.findCheckPointReportByName("4-Line-4");
			Assert.assertNotNull(checkPointReport, "report must contain a 4-Line-4 checkPoint");

			Assert.assertEquals(checkPointReport.getState(),
					ValidationReporter.RESULT.NOK,
					" checkPointReport must be nok");
			Assert.assertEquals(checkPointReport.getSeverity(),
					CheckPointReport.SEVERITY.ERROR,
					" checkPointReport must be on severity error");
			Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 1,
					" checkPointReport must have 1 item");
			List<CheckPointErrorReport> details = checkReportForTest(report,"4-Line-4",-1);
			for (CheckPointErrorReport detail : details) {
				log.warn(detail);
				Assert.assertEquals(detail.getSource().getObjectId(),line1.getObjectId(), "line must be source of error");
			}
		}

		utx.rollback();
	}

}
