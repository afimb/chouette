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


public class ValidationConnectionLinks extends AbstractTransactionalTestNGSpringContextTests
{

	@SuppressWarnings("unchecked")
	@BeforeGroups (groups = {"ConnectionLink"})
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
	@Test (groups = {"ConnectionLink"}, description = "3-ConnectionLink-1" )
	public void verifyTest1() throws ChouetteException 
	{
		// 3-ConnectionLink-1 : check distance between stops of connectionLink

		INeptuneManager<ConnectionLink> connectionLinkManager = (INeptuneManager<ConnectionLink>) applicationContext.getBean("connectionLinkManager");

		JSONObject parameters = null;
		try {
			parameters = new RuleParameterSet();
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(parameters,"no parameters for test");

		List<ConnectionLink> beans  = connectionLinkManager.getAll(null);
		Assert.assertFalse(beans.isEmpty(),"No data for test");

		parameters.put("inter_connection_link_distance_max", 600);

		PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
		connectionLinkManager.validate(null, beans, parameters, report, true);
		report.refreshStatus();

		AbstractValidation.printReport(report);

		Assert.assertEquals(report.getStatus(), Report.STATE.WARNING," report must be on level warning");
		Assert.assertEquals(report.hasItems(), true," report must have items");
		boolean found = false;
		for (ReportItem item : report.getItems()) 
		{
			CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
			if (checkPointReport.getMessageKey().equals("3-ConnectionLink-1"))
			{
				found = true;
				Assert.assertEquals(checkPointReport.getStatus(), Report.STATE.WARNING," checkPointReport must be on level warning");
				Assert.assertEquals(checkPointReport.hasItems(), true," checkPointReport must have items");
				Assert.assertEquals(checkPointReport.getItems().size(), 1," checkPointReport must have 1 item");
			}
		}
		Assert.assertTrue(found,"report must contain a 3-ConnectionLink-1 checkPoint");

	}


	@SuppressWarnings("unchecked")
	@Test (groups = {"ConnectionLink"}, description = "3-ConnectionLink-2" )
	public void verifyTest2() throws ChouetteException 
	{
		// 3-ConnectionLink-2 : check distance of link against distance between stops of connectionLink
		// 3-ConnectionLink-3 : check speeds in connectionLink

		INeptuneManager<ConnectionLink> connectionLinkManager = (INeptuneManager<ConnectionLink>) applicationContext.getBean("connectionLinkManager");

		JSONObject parameters = null;
		try {
			parameters = new RuleParameterSet();
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(parameters,"no parameters for test");

		List<ConnectionLink> beans  = connectionLinkManager.getAll(null);
		Assert.assertFalse(beans.isEmpty(),"No data for test");

		ConnectionLink link = beans.get(0);
		double distance = AbstractValidation.distance(link.getStartOfLink(), link.getEndOfLink());

		link.setLinkDistance(BigDecimal.valueOf(distance-50));

		PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
		connectionLinkManager.validate(null, beans, parameters, report, true);
		report.refreshStatus();

		AbstractValidation.printReport(report);

		Assert.assertEquals(report.getStatus(), Report.STATE.WARNING," report must be on level warning");
		Assert.assertEquals(report.hasItems(), true," report must have items");
		boolean found = false;
		for (ReportItem item : report.getItems()) 
		{
			CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
			if (checkPointReport.getMessageKey().equals("3-ConnectionLink-2"))
			{
				found = true;
				Assert.assertEquals(checkPointReport.getStatus(), Report.STATE.WARNING," checkPointReport must be on level warning");
				Assert.assertEquals(checkPointReport.hasItems(), true," checkPointReport must have items");
				Assert.assertEquals(checkPointReport.getItems().size(), 1," checkPointReport must have 1 item");
			}
		}
		Assert.assertTrue(found,"report must contain a 3-ConnectionLink-2 checkPoint");

	}


	@SuppressWarnings("unchecked")
	@Test (groups = {"ConnectionLink"}, description = "3-ConnectionLink-3" )
	public void verifyTest3() throws ChouetteException 
	{
		// 3-ConnectionLink-3 : check speeds in connectionLink

		INeptuneManager<ConnectionLink> connectionLinkManager = (INeptuneManager<ConnectionLink>) applicationContext.getBean("connectionLinkManager");

		JSONObject parameters = null;
		try {
			parameters = new RuleParameterSet();
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(parameters,"no parameters for test");

		List<ConnectionLink> beans  = connectionLinkManager.getAll(null);
		Assert.assertFalse(beans.isEmpty(),"No data for test");

		ConnectionLink link = beans.get(0);
		double distance = AbstractValidation.distance(link.getStartOfLink(), link.getEndOfLink());
		link.getDefaultDuration().setTime(link.getDefaultDuration().getTime() - 600000);
		link.getOccasionalTravellerDuration().setTime(link.getOccasionalTravellerDuration().getTime() - 800000);
		link.getFrequentTravellerDuration().setTime(link.getFrequentTravellerDuration().getTime() - 600000);
		link.getMobilityRestrictedTravellerDuration().setTime(link.getMobilityRestrictedTravellerDuration().getTime() - 900000);

		PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
		connectionLinkManager.validate(null, beans, parameters, report, true);
		report.refreshStatus();

		AbstractValidation.printReport(report);

		Assert.assertEquals(report.getStatus(), Report.STATE.WARNING," report must be on level warning");
		Assert.assertEquals(report.hasItems(), true," report must have items");
		boolean found = false;
		for (ReportItem item : report.getItems()) 
		{
			CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
			if (checkPointReport.getMessageKey().equals("3-ConnectionLink-3"))
			{
				found = true;
				Assert.assertEquals(checkPointReport.getStatus(), Report.STATE.WARNING," checkPointReport must be on level warning");
				Assert.assertEquals(checkPointReport.hasItems(), true," checkPointReport must have items");
				Assert.assertEquals(checkPointReport.getItems().size(), 4," checkPointReport must have 4 items");
			}
		}
		Assert.assertTrue(found,"report must contain a 3-ConnectionLink-3 checkPoint");

	}


}
