package fr.certu.chouette.validation;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

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
import fr.certu.chouette.model.neptune.AccessLink;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem.PHASE;

@ContextConfiguration(locations={"classpath:testContext.xml","classpath*:chouetteContext.xml"})
@TransactionConfiguration(transactionManager="transactionManager",defaultRollback=true)
public class ValidationAccessLinks extends AbstractTransactionalTestNGSpringContextTests
{

	@SuppressWarnings("unchecked")
	@BeforeGroups (groups = {"AccessLink"})
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
	@Test (groups = {"AccessLink"}, description = "3-AccessLink-1" )
	public void verifyTest1() throws ChouetteException 
	{
		// 3-AccessLink-1 : check distance between ends of accessLink

		INeptuneManager<AccessLink> accessLinkManager = (INeptuneManager<AccessLink>) applicationContext.getBean("accessLinkManager");

		JSONObject parameters = null;
		try {
			parameters = new RuleParameterSet();
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(parameters,"no parameters for test");

		List<AccessLink> beans  = accessLinkManager.getAll(null);
		Assert.assertFalse(beans.isEmpty(),"No data for test");

		parameters.put("inter_access_link_distance_max", 50);

		PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
		accessLinkManager.validate(null, beans, parameters, report, true);
		report.refreshStatus();

		AbstractValidation.printReport(report);

		Assert.assertEquals(report.getStatus(), Report.STATE.WARNING," report must be on level warning");
		Assert.assertEquals(report.hasItems(), true," report must have items");
		boolean found = false;
		for (ReportItem item : report.getItems()) 
		{
			CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
			if (checkPointReport.getMessageKey().equals("3-AccessLink-1"))
			{
				found = true;
				Assert.assertEquals(checkPointReport.getStatus(), Report.STATE.WARNING," checkPointReport must be on level warning");
				Assert.assertEquals(checkPointReport.hasItems(), true," checkPointReport must have items");
				Assert.assertEquals(checkPointReport.getItems().size(), 2," checkPointReport must have 2 item");
			}
		}
		Assert.assertTrue(found,"report must contain a 3-AccessLink-1 checkPoint");

	}


	@SuppressWarnings("unchecked")
	@Test (groups = {"AccessLink"}, description = "3-AccessLink-2" )
	public void verifyTest2() throws ChouetteException 
	{
		// 3-AccessLink-2 : check distance of link against distance between stops of accessLink

		INeptuneManager<AccessLink> accessLinkManager = (INeptuneManager<AccessLink>) applicationContext.getBean("accessLinkManager");

		JSONObject parameters = null;
		try {
			parameters = new RuleParameterSet();
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(parameters,"no parameters for test");

		List<AccessLink> beans  = accessLinkManager.getAll(null);
		Assert.assertFalse(beans.isEmpty(),"No data for test");

		AccessLink link = beans.get(0);
		
		double distance = AbstractValidation.distance(link.getStopArea(), link.getAccessPoint());

		link.setLinkDistance(BigDecimal.valueOf(distance-50));

		PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
		accessLinkManager.validate(null, beans, parameters, report, true);
		report.refreshStatus();

		AbstractValidation.printReport(report);

		Assert.assertEquals(report.getStatus(), Report.STATE.WARNING," report must be on level warning");
		Assert.assertEquals(report.hasItems(), true," report must have items");
		boolean found = false;
		for (ReportItem item : report.getItems()) 
		{
			CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
			if (checkPointReport.getMessageKey().equals("3-AccessLink-2"))
			{
				found = true;
				Assert.assertEquals(checkPointReport.getStatus(), Report.STATE.WARNING," checkPointReport must be on level warning");
				Assert.assertEquals(checkPointReport.hasItems(), true," checkPointReport must have items");
				Assert.assertEquals(checkPointReport.getItems().size(), 1," checkPointReport must have 1 item");
			}
		}
		Assert.assertTrue(found,"report must contain a 3-AccessLink-2 checkPoint");

	}


	@SuppressWarnings("unchecked")
	@Test (groups = {"AccessLink"}, description = "3-AccessLink-3" )
	public void verifyTest3() throws ChouetteException 
	{
		// 3-AccessLink-3 : check speeds in accessLink

		INeptuneManager<AccessLink> accessLinkManager = (INeptuneManager<AccessLink>) applicationContext.getBean("accessLinkManager");

		JSONObject parameters = null;
		try {
			parameters = new RuleParameterSet();
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(parameters,"no parameters for test");

		List<AccessLink> beans  = accessLinkManager.getAll(null);
		Assert.assertFalse(beans.isEmpty(),"No data for test");

		AccessLink link = null; 
		for (AccessLink accessLink : beans) 
		{
			if (accessLink.getObjectId().equals("NINOXE:AccessLink:7"))
			{
				link = accessLink;
				break;
			}
		}
		double distance = AbstractValidation.distance(link.getAccessPoint(), link.getStopArea());
		link.setLinkDistance(BigDecimal.valueOf(distance));
		link.getDefaultDuration().setTime(link.getDefaultDuration().getTime() - 150000);
		link.getOccasionalTravellerDuration().setTime(link.getDefaultDuration().getTime());
		link.getFrequentTravellerDuration().setTime(link.getDefaultDuration().getTime());
		link.getMobilityRestrictedTravellerDuration().setTime(link.getDefaultDuration().getTime());

		PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
		accessLinkManager.validate(null, beans, parameters, report, true);
		report.refreshStatus();

		AbstractValidation.printReport(report);

		Assert.assertEquals(report.getStatus(), Report.STATE.WARNING," report must be on level warning");
		Assert.assertEquals(report.hasItems(), true," report must have items");
		boolean found = false;
		for (ReportItem item : report.getItems()) 
		{
			CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
			if (checkPointReport.getMessageKey().equals("3-AccessLink-3"))
			{
				found = true;
				Assert.assertEquals(checkPointReport.getStatus(), Report.STATE.WARNING," checkPointReport must be on level warning");
				Assert.assertEquals(checkPointReport.hasItems(), true," checkPointReport must have items");
				Assert.assertEquals(checkPointReport.getItems().size(), 4," checkPointReport must have 4 items");
			}
		}
		Assert.assertTrue(found,"report must contain a 3-AccessLink-3 checkPoint");

	}


}
