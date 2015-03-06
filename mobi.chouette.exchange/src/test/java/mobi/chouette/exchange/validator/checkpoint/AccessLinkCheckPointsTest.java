package mobi.chouette.exchange.validator.checkpoint;

import java.util.ArrayList;
import java.util.List;

import mobi.chouette.common.Context;
import mobi.chouette.core.ChouetteException;
import mobi.chouette.exchange.validator.ValidationData;
import mobi.chouette.exchange.validator.parameters.ValidationParameters;
import mobi.chouette.exchange.validator.report.Detail;
import mobi.chouette.exchange.validator.report.ValidationReport;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.StopArea;

import org.apache.log4j.BasicConfigurator;
import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;


public class AccessLinkCheckPointsTest extends
AbstractTestValidation
{
	private AccessLinkCheckPoints checkPoint;
	private ValidationParameters fullparameters = null;
	private ValidationParameters parameters = null;
	private AccessLink bean1;
	private AccessLink bean2;
	private List<AccessLink> beansFor4 = new ArrayList<>();

	private Context context ;
	private ValidationData data;

	@BeforeGroups (groups = { "accessLink" })
	public void init()
	{
		BasicConfigurator.configure();
		checkPoint = new AccessLinkCheckPoints();
		context = new Context();

		long id = 1;

		try
		{
			parameters = loadParameters();
			fullparameters = loadFullParameters();

			fullparameters.setCheckAccessLink(1);

			bean1 = new AccessLink();
			bean1.setId(id++);
			bean1.setObjectId("test1:AccessLink:1");
			bean1.setName("test1");
			bean2 = new AccessLink();
			bean2.setId(id++);
			bean2.setObjectId("test2:AccessLink:1");
			bean2.setName("test2");
			StopArea area1 = new StopArea();
			area1.setId(id++);
			area1.setObjectId("test1:StopArea:1");
			area1.setName("area1");
			bean1.setStopArea(area1);
			StopArea area2 = new StopArea();
			area2.setId(id++);
			area2.setObjectId("test1:StopArea:2");
			area2.setName("area2");
			bean2.setStopArea(area2);
			AccessPoint access1 = new AccessPoint();
			access1.setId(id++);
			access1.setObjectId("test1:AccessPoint:1");
			access1.setName("access1");
			bean1.setAccessPoint(access1);
			AccessPoint access2 = new AccessPoint();
			access2.setId(id++);
			access2.setObjectId("test1:AccessPoint:2");
			access2.setName("access2");
			bean2.setAccessPoint(access2);
			

			beansFor4.add(bean1);
			beansFor4.add(bean2);
			context.put(VALIDATION,fullparameters);
			data = new ValidationData();
			context.put(VALIDATION_DATA, data);
		} 
		catch (Exception e)
		{
			fullparameters = null;
			e.printStackTrace();
		}

	}


	@Test(groups = { "accessLink" }, description = "4-AccessLink-1 no test")
	public void verifyTest4_1_notest() throws ChouetteException
	{
		// 4-AccessLink-1 : check columns
		Assert.assertNotNull(fullparameters, "no parameters for test");

		ValidationReport report = new ValidationReport();
		context.put(VALIDATION_REPORT, report);
		data.getAccessLinks().clear();
		data.getAccessLinks().addAll(beansFor4);

		fullparameters.setCheckAccessLink(0);
		checkPoint.validate(context, null);

		Assert.assertNull(report.findCheckPointByName(AbstractValidation.L4_ACCESS_LINK_1), " report must not have item 4-AccessLink-1");

		fullparameters.setCheckAccessLink(1);
		report.getCheckPoints().clear();

		checkPoint.validate(context, null);

		Assert.assertNotNull(report.findCheckPointByName(AbstractValidation.L4_ACCESS_LINK_1), " report must have item 4-AccessLink-1");
		Assert.assertEquals(report.findCheckPointByName(AbstractValidation.L4_ACCESS_LINK_1).getDetails().size(), 0, " checkpoint must have no detail");

	}

	@Test(groups = { "accessLink" }, description = "4-AccessLink-1 unicity")
	public void verifyTest4_1_unique() throws ChouetteException
	{
		// 4-AccessLink-1 : check columns
		Assert.assertNotNull(fullparameters, "no parameters for test");

		ValidationReport report = new ValidationReport();
		context.put(VALIDATION_REPORT, report);
		data.getAccessLinks().clear();
		data.getAccessLinks().addAll(beansFor4);

		// unique
		fullparameters.getAccessLink().getObjectId().setUnique(1);

		checkPoint.validate(context, null);
		fullparameters.getAccessLink().getObjectId().setUnique(0);

		Detail detail = checkReportForTest4_1(report,AbstractValidation.L4_ACCESS_LINK_1,bean2.getObjectId());
		Assert.assertEquals(detail.getReferenceValue(),"ObjectId","detail must refer column");
		Assert.assertEquals(detail.getValue(),bean2.getObjectId().split(":")[2],"detail must refer value");
		Assert.assertEquals(detail.getTargets().get(0).getObjectId(),bean1.getObjectId(),"detail must refer first bean");
	}


//	@Test(groups = { "accessLink" }, description = "3-AccessLink-1")
//	public void verifyTest3_1() throws Exception
//	{
//		// 3-AccessLink-1 : check distance between ends of accessLink
//		Assert.assertNotNull(parameters, "no parameters for test");
//
//		List<AccessLink> beans = accessLinkManager.getAll(null);
//		Assert.assertFalse(beans.isEmpty(), "No data for test");
//
//		parameters.put("inter_access_link_distance_max", 50);
//
//		PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
//		checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
//		// accessLinkManager.validate(null, beans, parameters, report,null, true);
//		report.refreshStatus();
//
//		AbstractTestValidation.printReport(report);
//
//		Assert.assertEquals(report.getStatus(), Report.STATE.WARNING,
//				" report must be on level warning");
//		Assert.assertEquals(report.hasItems(), true, " report must have items");
//		boolean found = false;
//		for (ReportItem item : report.getItems())
//		{
//			CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
//			if (checkPointReport.getMessageKey().equals("3-AccessLink-1"))
//			{
//				found = true;
//				Assert.assertEquals(checkPointReport.getStatus(),
//						Report.STATE.WARNING,
//						" checkPointReport must be on level warning");
//				Assert.assertEquals(checkPointReport.hasItems(), true,
//						" checkPointReport must have items");
//				Assert.assertEquals(checkPointReport.getItems().size(), 2,
//						" checkPointReport must have 2 item");
//			}
//		}
//		Assert.assertTrue(found,
//				"report must contain a 3-AccessLink-1 checkPoint");
//
//	}
//
//	@Test(groups = { "accessLink" }, description = "3-AccessLink-2")
//	public void verifyTest3_2() throws ChouetteException
//	{
//		// 3-AccessLink-2 : check distance of link against distance between
//		// stops of accessLink
//
//		JSONObject parameters = null;
//		try
//		{
//			parameters = new RuleParameterSet();
//		} catch (JSONException | IOException e)
//		{
//			e.printStackTrace();
//		}
//		Assert.assertNotNull(parameters, "no parameters for test");
//
//		List<AccessLink> beans = accessLinkManager.getAll(null);
//		Assert.assertFalse(beans.isEmpty(), "No data for test");
//
//		AccessLink link = beans.get(0);
//
//		double distance = AbstractTestValidation.distance(link.getStopArea(),
//				link.getAccessPoint());
//
//		link.setLinkDistance(BigDecimal.valueOf(distance - 50));
//
//		PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
//		checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
//		report.refreshStatus();
//
//		AbstractTestValidation.printReport(report);
//
//		Assert.assertEquals(report.getStatus(), Report.STATE.WARNING,
//				" report must be on level warning");
//		Assert.assertEquals(report.hasItems(), true, " report must have items");
//		boolean found = false;
//		for (ReportItem item : report.getItems())
//		{
//			CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
//			if (checkPointReport.getMessageKey().equals("3-AccessLink-2"))
//			{
//				found = true;
//				Assert.assertEquals(checkPointReport.getStatus(),
//						Report.STATE.WARNING,
//						" checkPointReport must be on level warning");
//				Assert.assertEquals(checkPointReport.hasItems(), true,
//						" checkPointReport must have items");
//				Assert.assertEquals(checkPointReport.getItems().size(), 1,
//						" checkPointReport must have 1 item");
//			}
//		}
//		Assert.assertTrue(found,
//				"report must contain a 3-AccessLink-2 checkPoint");
//
//	}
//
//	@Test(groups = { "accessLink" }, description = "3-AccessLink-3")
//	public void verifyTest3_3() throws ChouetteException
//	{
//		// 3-AccessLink-3 : check speeds in accessLink
//
//		JSONObject parameters = null;
//		try
//		{
//			parameters = new RuleParameterSet();
//		} catch (JSONException | IOException e)
//		{
//			e.printStackTrace();
//		}
//		Assert.assertNotNull(parameters, "no parameters for test");
//
//		List<AccessLink> beans = accessLinkManager.getAll(null);
//		Assert.assertFalse(beans.isEmpty(), "No data for test");
//
//		AccessLink link = null;
//		for (AccessLink accessLink : beans)
//		{
//			if (accessLink.getObjectId().equals("NINOXE:AccessLink:7"))
//			{
//				link = accessLink;
//				break;
//			}
//		}
//		double distance = AbstractTestValidation.distance(link.getAccessPoint(),
//				link.getStopArea());
//		link.setLinkDistance(BigDecimal.valueOf(distance));
//		link.getDefaultDuration().setTime(
//				link.getDefaultDuration().getTime() - 150000);
//		link.getOccasionalTravellerDuration().setTime(
//				link.getDefaultDuration().getTime());
//		link.getFrequentTravellerDuration().setTime(
//				link.getDefaultDuration().getTime());
//		link.getMobilityRestrictedTravellerDuration().setTime(
//				link.getDefaultDuration().getTime());
//
//		PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
//		checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
//		report.refreshStatus();
//
//		AbstractTestValidation.printReport(report);
//
//		Assert.assertEquals(report.getStatus(), Report.STATE.WARNING,
//				" report must be on level warning");
//		Assert.assertEquals(report.hasItems(), true, " report must have items");
//		boolean found = false;
//		for (ReportItem item : report.getItems())
//		{
//			CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
//			if (checkPointReport.getMessageKey().equals("3-AccessLink-3"))
//			{
//				found = true;
//				Assert.assertEquals(checkPointReport.getStatus(),
//						Report.STATE.WARNING,
//						" checkPointReport must be on level warning");
//				Assert.assertEquals(checkPointReport.hasItems(), true,
//						" checkPointReport must have items");
//				Assert.assertEquals(checkPointReport.getItems().size(), 4,
//						" checkPointReport must have 4 items");
//			}
//		}
//		Assert.assertTrue(found,
//				"report must contain a 3-AccessLink-3 checkPoint");
//
//	}
//
//	@SuppressWarnings("unchecked")
//	@BeforeGroups(groups = { "accessLink" })
//	public void loadStopAreas() throws ChouetteException
//	{
//		{
//			IImportPlugin<Line> importLine = (IImportPlugin<Line>) applicationContext
//					.getBean("NeptuneLineImport");
//			INeptuneManager<Line> lineManager = (INeptuneManager<Line>) applicationContext
//					.getBean("lineManager");
//
//			List<Line> beans = LineLoader.load(importLine,
//					"src/test/data/model.zip");
//			Assert.assertFalse(beans.isEmpty(), "No data for test");
//			lineManager.saveAll(null, beans, true, true);
//		}
//
//	}
//

}
