package mobi.chouette.exchange.validation.checkpoint;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.EJB;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.core.ChouetteException;
import mobi.chouette.dao.NetworkDAO;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validator.JobDataTest;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.Assert;
import org.testng.annotations.Test;

@Log4j
public class ValidationLines extends AbstractTestValidation
{
	private LineCheckPoints checkPoint = new LineCheckPoints();
	private SharedLineCheckPoints sharedCheckPoint = new SharedLineCheckPoints();
	private ValidationParameters fullparameters;
	private Line bean1;
	private Line bean2;
	private Line bean3;
	
	@EJB
	NetworkDAO networkDao;

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
		Assert.assertTrue(report.findCheckPointByName("4-Line-1") == null, " report must not have item 4-Line-1");

		fullparameters.setCheckLine(1);
		context.put(VALIDATION_REPORT, new ValidationReport());

		checkPoint.validate(context, null);
		report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertTrue(report.findCheckPointByName("4-Line-1") != null, " report must have item 4-Line-1");
		Assert.assertEquals(report.findCheckPointByName("4-Line-1").getDetailCount(), 0, " checkpoint must have no detail");

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

		Detail detail = checkReportForTest4_1(report,"4-Line-1",bean2.getObjectId());
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
		sharedCheckPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");
		
			CheckPoint checkPointReport = report.findCheckPointByName("3-Line-1");
			Assert.assertNotNull(checkPointReport, "report must contain a 3-Line-1 checkPoint");
			
				Assert.assertEquals(checkPointReport.getState(),
						CheckPoint.RESULT.NOK,
						" checkPointReport must be nok");
				Assert.assertEquals(checkPointReport.getSeverity(),
						CheckPoint.SEVERITY.WARNING,
						" checkPointReport must be on severity warning");
				Assert.assertEquals(checkPointReport.getDetailCount(), 2,
						" checkPointReport must have 2 item");

				for (Detail detail : checkPointReport.getDetails()) {
					log.warn(detail);
				}
				// check detail keys = line1 and line2 objectids
				boolean line1objectIdFound = false;
				boolean line2objectIdFound = false;
				boolean line3objectIdFound = false;
				for (Detail detailReport : checkPointReport.getDetails())
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

	/*	
	@SuppressWarnings("unchecked")
	@Test(groups = { "line" }, description = "3-Line-2")
	public void verifyTest3_2() throws ChouetteException
	{
		// 3-Line-2 : check if line has routes
		IImportPlugin<Line> importLine = (IImportPlugin<Line>) applicationContext
				.getBean("NeptuneLineImport");

		long id = 1;

		JSONObject parameters = null;
		try
		{
			parameters = new RuleParameterSet();
		} catch (JSONException | IOException e)
		{
			e.printStackTrace();
		}
		Assert.assertNotNull(parameters, "no parameters for test");

		List<Line> beans = LineLoader.load(importLine,
				"src/test/data/Ligne_OK.xml");
		Assert.assertFalse(beans.isEmpty(), "No data for test");
		Line line1 = beans.get(0);

		// line1 is model;
		line1.setId(id++);
		line1.setObjectId("NINOXE:Line:modelLine");

		line1.getRoutes().clear();

		PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

		checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
		report.refreshStatus();

		printReport(report);

		Assert.assertEquals(report.getStatus(), Report.STATE.ERROR,
				" report must be on level error");
		Assert.assertEquals(report.hasItems(), true, " report must have items");
		boolean found = false;
		for (ReportItem item : report.getItems())
		{
			CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
			if (checkPointReport.getMessageKey().equals("3-Line-2"))
			{
				found = true;
				Assert.assertEquals(checkPointReport.getStatus(),
						Report.STATE.ERROR,
						" checkPointReport must be on level error");
				Assert.assertEquals(checkPointReport.hasItems(), true,
						" checkPointReport must have items");
				Assert.assertEquals(checkPointReport.getItems().size(), 1,
						" checkPointReport must have 1 item");

				// check detail keys = line1 objectids
				boolean line1objectIdFound = false;
				for (ReportItem ditem : checkPointReport.getItems())
				{
					DetailReportItem detailReport = (DetailReportItem) ditem;
					if (detailReport.getObjectId().equals(line1.getObjectId()))
						line1objectIdFound = true;
				}
				Assert.assertTrue(line1objectIdFound,
						"detail report must refer line 1");
			}
		}
		Assert.assertTrue(found, "report must contain a 3-Line-2 checkPoint");

	}

	@SuppressWarnings("unchecked")
	@Test(groups = { "Line" }, description = "4-Line-2")
	public void verifyTest4_2() throws ChouetteException
	{
		// 4-Line-2 : check transport mode
		IImportPlugin<Line> importLine = (IImportPlugin<Line>) applicationContext
				.getBean("NeptuneLineImport");

		long id = 1;

		JSONObject parameters = null;
		try
		{
			parameters = new RuleParameterSet();
		} catch (JSONException | IOException e)
		{
			e.printStackTrace();
		}
		Assert.assertNotNull(parameters, "no parameters for test");

		List<Line> beans = LineLoader.load(importLine,
				"src/test/data/Ligne_OK.xml");
		Assert.assertFalse(beans.isEmpty(), "No data for test");
		Line line1 = beans.get(0);

		// line1 is model;
		line1.setId(id++);
		line1.setObjectId("NINOXE:Line:modelLine");

		parameters.remove("check_allowed_transport_modes");

		{ // check test not required when missing parameter
			PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

			checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
			report.refreshStatus();

			Assert.assertEquals(report.getStatus(), Report.STATE.OK,
					" report must be on level ok");
			Assert.assertEquals(report.hasItems(), true, " report must have items");
			boolean found = false;
			for (ReportItem item : report.getItems())
			{
				CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
				if (checkPointReport.getMessageKey().equals("4-Line-2"))
				{
					found = true;
				}
			}
			Assert.assertFalse(found, "report must not contain a 4-Line-2 checkPoint");
		}

		parameters.append("check_allowed_transport_modes", Integer.valueOf(0));

		{ // check test not required when check is false
			PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

			checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
			report.refreshStatus();

			Assert.assertEquals(report.getStatus(), Report.STATE.OK,
					" report must be on level ok");
			Assert.assertEquals(report.hasItems(), true, " report must have items");
			boolean found = false;
			for (ReportItem item : report.getItems())
			{
				CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
				if (checkPointReport.getMessageKey().equals("4-Line-2"))
				{
					found = true;
				}
			}
			Assert.assertFalse(found, "report must not contain a 4-Line-2 checkPoint");
		}

		parameters.put("check_allowed_transport_modes",Integer.valueOf(1));

		{ // check test not required when check is false
			PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

			checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
			report.refreshStatus();

			Assert.assertEquals(report.getStatus(), Report.STATE.OK,
					" report must be on level ok");
			Assert.assertEquals(report.hasItems(), true, " report must have items");
			boolean found = false;
			for (ReportItem item : report.getItems())
			{
				CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
				if (checkPointReport.getMessageKey().equals("4-Line-2"))
				{
					found = true;
				}
			}
			Assert.assertTrue(found, "report must contain a 4-Line-2 checkPoint");
		}

		JSONObject busParam = parameters.getJSONObject("mode_bus");
		busParam.put("allowed_transport", Integer.valueOf(0));
		line1.setTransportModeName(TransportModeNameEnum.Bus);
		{ // check test not required when check is false
			PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

			checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
			report.refreshStatus();

			Assert.assertEquals(report.getStatus(), Report.STATE.ERROR,
					" report must be on level error");
			Assert.assertEquals(report.hasItems(), true, " report must have items");
			boolean found = false;
			for (ReportItem item : report.getItems())
			{
				CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
				if (checkPointReport.getMessageKey().equals("4-Line-2"))
				{
					found = true;
					Assert.assertEquals(checkPointReport.getStatus(),
							Report.STATE.ERROR,
							" checkPointReport must be on level error");
					Assert.assertEquals(checkPointReport.hasItems(), true,
							" checkPointReport must have items");
					Assert.assertEquals(checkPointReport.getItems().size(), 1,
							" checkPointReport must have 1 item");

					// check detail keys = line1 objectids
					boolean line1objectIdFound = false;
					for (ReportItem ditem : checkPointReport.getItems())
					{
						DetailReportItem detailReport = (DetailReportItem) ditem;
						if (detailReport.getObjectId().equals(line1.getObjectId()))
							line1objectIdFound = true;
					}
					Assert.assertTrue(line1objectIdFound,
							"detail report must refer line 1");

				}
			}
			Assert.assertTrue(found, "report must contain a 4-Line-2 checkPoint");
		}


	}
	@SuppressWarnings("unchecked")
	@Test(groups = { "Line" }, description = "4-Line-3")
	public void verifyTest4_3() throws ChouetteException
	{
		// 4-Line-3 : check transport mode
		IImportPlugin<Line> importLine = (IImportPlugin<Line>) applicationContext
				.getBean("NeptuneLineImport");

		long id = 1;

		JSONObject parameters = null;
		try
		{
			parameters = new RuleParameterSet();
		} catch (JSONException | IOException e)
		{
			e.printStackTrace();
		}
		Assert.assertNotNull(parameters, "no parameters for test");

		List<Line> beans = LineLoader.load(importLine,
				"src/test/data/Ligne_OK.xml");
		Assert.assertFalse(beans.isEmpty(), "No data for test");
		Line line1 = beans.get(0);

		// line1 is model;
		line1.setId(id++);
		line1.setObjectId("NINOXE:Line:modelLine");

		parameters.remove("check_lines_in_groups");

		{ // check test not required when missing parameter
			PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

			checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
			report.refreshStatus();

			Assert.assertEquals(report.getStatus(), Report.STATE.OK,
					" report must be on level ok");
			Assert.assertEquals(report.hasItems(), true, " report must have items");
			Assert.assertFalse(report.hasItem("4-Line-3"),"report must not contain a 4-Line-3 checkPoint");
		}

		parameters.append("check_lines_in_groups", Integer.valueOf(0));

		{ // check test not required when check is false
			PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

			checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
			report.refreshStatus();

			Assert.assertEquals(report.getStatus(), Report.STATE.OK,
					" report must be on level ok");
			Assert.assertEquals(report.hasItems(), true, " report must have items");
			Assert.assertFalse(report.hasItem("4-Line-3"),"report must not contain a 4-Line-3 checkPoint");
		}

		parameters.put("check_lines_in_groups",Integer.valueOf(1));

		line1.addGroupOfLine(new GroupOfLine());

		{ // check test not required when check is false
			PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

			checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
			report.refreshStatus();
			printReport(report);

			Assert.assertEquals(report.getStatus(), Report.STATE.OK,
					" report must be on level ok");
			Assert.assertEquals(report.hasItems(), true, " report must have items");
			Assert.assertTrue(report.hasItem("4-Line-3"),"report must contain a 4-Line-3 checkPoint");
		}

		line1.getGroupOfLines().clear();
		{
			PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

			checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
			report.refreshStatus();

			Assert.assertEquals(report.getStatus(), Report.STATE.ERROR,
					" report must be on level error");
			Assert.assertEquals(report.hasItems(), true, " report must have items");
			Assert.assertTrue(report.hasItem("4-Line-3"),"report must contain a 4-Line-3 checkPoint");
			CheckPointReportItem checkPointReport = report.getItem("4-Line-3");
			Assert.assertEquals(checkPointReport.getStatus(),
					Report.STATE.ERROR,
					" checkPointReport must be on level error");
			Assert.assertEquals(checkPointReport.hasItems(), true,
					" checkPointReport must have items");
			Assert.assertEquals(checkPointReport.getItems().size(), 1,
					" checkPointReport must have 1 item");

			// check detail keys = line1 objectids
			boolean line1objectIdFound = false;
			for (ReportItem ditem : checkPointReport.getItems())
			{
				DetailReportItem detailReport = (DetailReportItem) ditem;
				if (detailReport.getObjectId().equals(line1.getObjectId()))
					line1objectIdFound = true;
			}
			Assert.assertTrue(line1objectIdFound,
					"detail report must refer line 1");
		}


	}

	@SuppressWarnings("unchecked")
	@Test(groups = { "Line" }, description = "4-Line-4")
	public void verifyTest4_4() throws ChouetteException
	{
		// 4-Line-4 : check routes
		IImportPlugin<Line> importLine = (IImportPlugin<Line>) applicationContext
				.getBean("NeptuneLineImport");

		long id = 1;

		JSONObject parameters = null;
		try
		{
			parameters = new RuleParameterSet();
		} catch (JSONException | IOException e)
		{
			e.printStackTrace();
		}
		Assert.assertNotNull(parameters, "no parameters for test");

		List<Line> beans = LineLoader.load(importLine,
				"src/test/data/Ligne_OK.xml");
		Assert.assertFalse(beans.isEmpty(), "No data for test");
		Line line1 = beans.get(0);

		// line1 is model;
		line1.setId(id++);
		line1.setObjectId("NINOXE:Line:modelLine");

		parameters.remove("check_line_routes");

		{ // check test not required when missing parameter
			PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

			checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
			report.refreshStatus();

			Assert.assertEquals(report.getStatus(), Report.STATE.OK,
					" report must be on level ok");
			Assert.assertEquals(report.hasItems(), true, " report must have items");
			Assert.assertFalse(report.hasItem("4-Line-4"),"report must not contain a 4-Line-4 checkPoint");
		}

		parameters.append("check_line_routes", Integer.valueOf(0));

		{ // check test not required when check is false
			PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

			checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
			report.refreshStatus();

			Assert.assertEquals(report.getStatus(), Report.STATE.OK,
					" report must be on level ok");
			Assert.assertEquals(report.hasItems(), true, " report must have items");
			Assert.assertFalse(report.hasItem("4-Line-4"),"report must not contain a 4-Line-4 checkPoint");
		}

		parameters.put("check_line_routes",Integer.valueOf(1));

		{ // check 2 routes in wayback
			PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

			checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
			report.refreshStatus();
			printReport(report);

			Assert.assertEquals(report.getStatus(), Report.STATE.OK,
					" report must be on level ok");
			Assert.assertEquals(report.hasItems(), true, " report must have items");
			Assert.assertTrue(report.hasItem("4-Line-4"),"report must contain a 4-Line-4 checkPoint");
		}

		line1.getRoutes().remove(1);
		{ // check 1 route
			PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

			checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
			report.refreshStatus();

			Assert.assertEquals(report.getStatus(), Report.STATE.OK,
					" report must be on level ok");
			Assert.assertEquals(report.hasItems(), true, " report must have items");
			Assert.assertTrue(report.hasItem("4-Line-4"),"report must contain a 4-Line-4 checkPoint");
		}

		line1.getRoutes().add(new Route());
		{
			PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

			checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
			report.refreshStatus();

			Assert.assertEquals(report.getStatus(), Report.STATE.ERROR,
					" report must be on level error");
			Assert.assertEquals(report.hasItems(), true, " report must have items");
			Assert.assertTrue(report.hasItem("4-Line-4"),"report must contain a 4-Line-4 checkPoint");
			CheckPointReportItem checkPointReport = report.getItem("4-Line-4");
			Assert.assertEquals(checkPointReport.getStatus(),
					Report.STATE.ERROR,
					" checkPointReport must be on level error");
			Assert.assertEquals(checkPointReport.hasItems(), true,
					" checkPointReport must have items");
			Assert.assertEquals(checkPointReport.getItems().size(), 1,
					" checkPointReport must have 1 item");

			// check detail keys = line1 objectids
			boolean line1objectIdFound = false;
			for (ReportItem ditem : checkPointReport.getItems())
			{
				DetailReportItem detailReport = (DetailReportItem) ditem;
				if (detailReport.getObjectId().equals(line1.getObjectId()))
					line1objectIdFound = true;
			}
			Assert.assertTrue(line1objectIdFound,
					"detail report must refer line 1");
		}


	}

*/
}
