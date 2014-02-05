package fr.certu.chouette.validation;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import lombok.extern.log4j.Log4j;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem.PHASE;

@ContextConfiguration(locations={"classpath:testContext.xml","classpath*:chouetteContext.xml"})
@TransactionConfiguration(transactionManager="transactionManager",defaultRollback=true)

@Log4j
public class ValidationAccessPoints extends AbstractTransactionalTestNGSpringContextTests
{

	@SuppressWarnings("unchecked")
	@BeforeGroups (groups = {"AccessPoint"})
	public void loadStopAreas() throws ChouetteException
	{
		{
		IImportPlugin<Line> importLine = (IImportPlugin<Line>) applicationContext.getBean("NeptuneLineImport");
		INeptuneManager<Line> lineManager = (INeptuneManager<Line>) applicationContext.getBean("lineManager");

		JSONObject parameters = null;
		try {
			parameters = new RuleParameterSet();
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(parameters,"no parameters for test");

		List<Line> beans = LineLoader.load(importLine, "src/test/data/model.zip");
		Assert.assertFalse(beans.isEmpty(),"No data for test");
		lineManager.saveAll(null, beans, true, true);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@Test (groups = {"AccessPoint"}, description = "3-AccessPoint-1" )
	public void verifyTest1() throws ChouetteException 
	{
		// 3-AccessPoint-1 : check if all access points have geolocalization

		INeptuneManager<AccessPoint> accessPointManager = (INeptuneManager<AccessPoint>) applicationContext.getBean("accessPointManager");

		JSONObject parameters = null;
		try {
			parameters = new RuleParameterSet();
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(parameters,"no parameters for test");

		List<AccessPoint> beans  = accessPointManager.getAll(null);
		Assert.assertFalse(beans.isEmpty(),"No data for test");
		
		for (AccessPoint accessPoint : beans) 
		{
			log.info("oid = "+accessPoint.getObjectId());
			log.info("name = "+accessPoint.getName());
		}

		AccessPoint access1 = beans.get(0);
		access1.setLongLatType(null);
		BigDecimal svLon = access1.getLongitude();
		access1.setLongitude(null);
		
		PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
		accessPointManager.validate(null, beans, parameters, report, true);
		report.refreshStatus();

		AbstractValidation.printReport(report);

		Assert.assertEquals(report.getStatus(), Report.STATE.ERROR," report must be on level error");
		Assert.assertEquals(report.hasItems(), true," report must have items");
		boolean found = false;
		for (ReportItem item : report.getItems()) 
		{
			CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
			if (checkPointReport.getMessageKey().equals("3-AccessPoint-1"))
			{
				found = true;
				Assert.assertEquals(checkPointReport.getStatus(), Report.STATE.ERROR," checkPointReport must be on level error");
				Assert.assertEquals(checkPointReport.hasItems(), true," checkPointReport must have items");
				Assert.assertEquals(checkPointReport.getItems().size(), 1," checkPointReport must have 1 item");

				
			}
		}
		Assert.assertTrue(found,"report must contain a 3-AccessPoint-1 checkPoint");
		access1.setLongitude(svLon);
		access1.setLongLatType(LongLatTypeEnum.WGS84);

	}

	
	@SuppressWarnings("unchecked")
	@Test (groups = {"AccessPoint"}, description = "3-AccessPoint-2" )
	public void verifyTest2() throws ChouetteException 
	{
		// 3-AccessPoint-2 : check distance of access points with different name

		INeptuneManager<AccessPoint> accessPointManager = (INeptuneManager<AccessPoint>) applicationContext.getBean("accessPointManager");

		JSONObject parameters = null;
		try {
			parameters = new RuleParameterSet();
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(parameters,"no parameters for test");

		List<AccessPoint> beans  = accessPointManager.getAll(null);
		Assert.assertFalse(beans.isEmpty(),"No data for test");
		
		parameters.put("parent_stop_area_distance_max",50);
		
		PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
		accessPointManager.validate(null, beans, parameters, report, true);
		report.refreshStatus();

		AbstractValidation.printReport(report);

		Assert.assertEquals(report.getStatus(), Report.STATE.WARNING," report must be on level warning");
		Assert.assertEquals(report.hasItems(), true," report must have items");
		boolean found = false;
		for (ReportItem item : report.getItems()) 
		{
			CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
			if (checkPointReport.getMessageKey().equals("3-AccessPoint-3"))
			{
				found = true;
				Assert.assertEquals(checkPointReport.getStatus(), Report.STATE.WARNING," checkPointReport must be on level warning");
				Assert.assertEquals(checkPointReport.hasItems(), true," checkPointReport must have items");
				Assert.assertEquals(checkPointReport.getItems().size(), 2," checkPointReport must have 2 item");

				
			}
		}
		Assert.assertTrue(found,"report must contain a 3-AccessPoint-3 checkPoint");

	}

	
	@SuppressWarnings("unchecked")
	@Test (groups = {"AccessPoint"}, description = "3-AccessPoint-3" )
	public void verifyTest3() throws ChouetteException 
	{
		// 3-AccessPoint-3 : check distance with parents 

		INeptuneManager<AccessPoint> accessPointManager = (INeptuneManager<AccessPoint>) applicationContext.getBean("accessPointManager");

		JSONObject parameters = null;
		try {
			parameters = new RuleParameterSet();
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(parameters,"no parameters for test");

		List<AccessPoint> beans  = accessPointManager.getAll(null);
		Assert.assertFalse(beans.isEmpty(),"No data for test");
		
		AccessPoint access1 = null;
		AccessPoint access2 = null;
		for (AccessPoint accessPoint : beans) 
		{
			if (accessPoint.getObjectId().equals("NINOXE:AccessPoint:6")) access1 = accessPoint;
			if (accessPoint.getObjectId().equals("NINOXE:AccessPoint:7")) access2 = accessPoint;
		}

		access1.setLongitude(access2.getLongitude());
		access1.setLatitude(access2.getLatitude());
		
		PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
		accessPointManager.validate(null, beans, parameters, report, true);
		report.refreshStatus();

		AbstractValidation.printReport(report);

		Assert.assertEquals(report.getStatus(), Report.STATE.WARNING," report must be on level warning");
		Assert.assertEquals(report.hasItems(), true," report must have items");
		boolean found = false;
		for (ReportItem item : report.getItems()) 
		{
			CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
			if (checkPointReport.getMessageKey().equals("3-AccessPoint-2"))
			{
				found = true;
				Assert.assertEquals(checkPointReport.getStatus(), Report.STATE.WARNING," checkPointReport must be on level warning");
				Assert.assertEquals(checkPointReport.hasItems(), true," checkPointReport must have items");
				Assert.assertEquals(checkPointReport.getItems().size(), 1," checkPointReport must have 1 item");

				
			}
		}
		Assert.assertTrue(found,"report must contain a 3-AccessPoint-2 checkPoint");

	}

	

}
