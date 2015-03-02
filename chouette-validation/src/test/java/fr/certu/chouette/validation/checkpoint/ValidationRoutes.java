package fr.certu.chouette.validation.checkpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem.PHASE;
import fr.certu.chouette.validation.checkpoint.RouteCheckPoints;

@ContextConfiguration(locations = { "classpath:testContext.xml",
      "classpath*:chouetteContext.xml" })
public class ValidationRoutes extends AbstractTestValidation
{
   private RouteCheckPoints checkPoint;
   private JSONObject fullparameters;
   private Route bean1;
   private Route bean2;
   private List<Route> beansFor4 = new ArrayList<>();
   
   @BeforeGroups (groups = { "route" })
   public void init()
   {
      checkPoint = (RouteCheckPoints) applicationContext
            .getBean("routeCheckPoints");
      checkPoint.setJourneyPatternCheckPoints(null);
      
      long id = 1;

      fullparameters = null;
      try
      {
         fullparameters = new RuleParameterSet();
         fullparameters.put("check_route","1");

         bean1 = new Route();
         bean1.setId(id++);
         bean1.setObjectId("test1:Route:1");
         bean1.setName("test1");
         bean2 = new Route();
         bean2.setId(id++);
         bean2.setObjectId("test2:Route:1");
         bean2.setName("test2");
   
         beansFor4.add(bean1);
         beansFor4.add(bean2);
      } 
      catch (Exception e)
      {
         fullparameters = null;
         e.printStackTrace();
      }
      
   }
   
   @Test(groups = { "route" }, description = "4-Route-1 no test")
   public void verifyTest4_1_notest() throws ChouetteException
   {
      // 4-Route-1 : check columns
      Assert.assertNotNull(fullparameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      fullparameters.put("check_route","0");
      checkPoint.check(beansFor4, fullparameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertFalse(report.hasItem("4-Route-1"), " report must not have item 4-Route-1");

      fullparameters.put("check_route","1");
      report = new PhaseReportItem(PHASE.THREE);

      checkPoint.check(beansFor4, fullparameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertTrue(report.hasItem("4-Route-1"), " report must have item 4-Route-1");
      Assert.assertEquals(report.getItem("4-Route-1").getItems().size(), 0, " checkpoint must have no detail");

   }
   
   @Test(groups = { "route" }, description = "4-Route-1 unicity")
   public void verifyTest4_1_unique() throws ChouetteException
   {
      // 4-Route-1 : check columns
      Assert.assertNotNull(fullparameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      // unique
      JSONObject column = fullparameters.getJSONObject("route").getJSONObject("objectid");
      column.put("unique",1);

      checkPoint.check(beansFor4, fullparameters, report, new HashMap<String, Object>());
      report.refreshStatus();
      column.put("unique",0);

      DetailReportItem detail = checkReportForTest4_1(report,"4-Route-1",bean2.getObjectId());
      Assert.assertEquals(detail.getArgs().get("column"),"objectid","detail must refer column");
      Assert.assertEquals(detail.getArgs().get("value"),bean2.getObjectId().split(":")[2],"detail must refer value");
      Assert.assertEquals(detail.getArgs().get("alternateId"),bean1.getObjectId(),"detail must refer fisrt bean");
   }
	@SuppressWarnings("unchecked")
	@Test (groups = {"route"}, description = "3-Route-1" )
	public void verifyTest3_1() throws ChouetteException 
	{
		// 3-Route-1 : check if two successive stops are in same area		
		IImportPlugin<Line> importLine = (IImportPlugin<Line>) applicationContext.getBean("NeptuneLineImport");

		long id = 1;
		
		JSONObject parameters = null;
		try {
			parameters = new RuleParameterSet();
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(parameters,"no parameters for test");

		List<Line> beans = LineLoader.load(importLine, "src/test/data/Ligne_OK.xml");
		Assert.assertFalse(beans.isEmpty(),"No data for test");
		Line line1 = beans.get(0);
		
		// line1 is model;
		line1.setId(id++);
		
		for (Route route : line1.getRoutes()) 
		{
			route.setId(id++);
		}
		
		Route route1 = line1.getRoutes().get(0);
		route1.getStopPoints().get(1).setContainedInStopArea(route1.getStopPoints().get(0).getContainedInStopArea());

		PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

		checkPoint.check(line1.getRoutes(), parameters , report, new HashMap<String, Object>());
		report.refreshStatus();

		printReport(report);

		Assert.assertEquals(report.getStatus(), Report.STATE.WARNING," report must be on level warning");
		Assert.assertEquals(report.hasItems(), true," report must have items");
		boolean found = false;
		for (ReportItem item : report.getItems()) 
		{
			CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
			if (checkPointReport.getMessageKey().equals("3-Route-1"))
			{
				found = true;
				Assert.assertEquals(checkPointReport.getStatus(), Report.STATE.WARNING," checkPointReport must be on level warning");
				Assert.assertEquals(checkPointReport.hasItems(), true," checkPointReport must have items");
				Assert.assertEquals(checkPointReport.getItems().size(), 1," checkPointReport must have 1 item");

				//check detail keys = line1 and line2 objectids
				boolean route1objectIdFound = false;
				for (ReportItem ditem : checkPointReport.getItems()) 
				{
					DetailReportItem detailReport = (DetailReportItem) ditem;
					if (detailReport.getObjectId().equals(route1.getObjectId())) route1objectIdFound = true;
				}
				Assert.assertTrue(route1objectIdFound,"detail report must refer route 1");
			}
		}
		Assert.assertTrue(found,"report must contain a 3-Route-1 checkPoint");

	}
	

	@SuppressWarnings("unchecked")
	@Test (groups = {"route"}, description = "3-Route-2" )
	public void verifyTest3_2() throws ChouetteException 
	{
		// 3-Route-2 : check if two wayback routes are actually waybacks

		IImportPlugin<Line> importLine = (IImportPlugin<Line>) applicationContext.getBean("NeptuneLineImport");

		long id = 1;
		
		JSONObject parameters = null;
		try {
			parameters = new RuleParameterSet();
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(parameters,"no parameters for test");

		List<Line> beans = LineLoader.load(importLine, "src/test/data/Ligne_OK.xml");
		Assert.assertFalse(beans.isEmpty(),"No data for test");
		Line line1 = beans.get(0);
		
		// line1 is model;
		line1.setId(id++);
		
		for (Route route : line1.getRoutes()) 
		{
			route.setId(id++);
		}
		
		Route route1 = line1.getRoutes().get(0);
		Route route2 = route1.getWayBackRoute();
		if (route2 == null)
		{
			route2 = line1.getRoutes().get(1);
			route1.setWayBackRoute(route2);
			route2.setWayBackRoute(route1);
		}
		
		StopArea area1 = route1.getStopPoints().get(1).getContainedInStopArea().getParent();
        StopArea area0 = route1.getStopPoints().get(0).getContainedInStopArea();
        area0.setParent(area1);
		
		PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

		checkPoint.check(line1.getRoutes(), parameters , report, new HashMap<String, Object>());
		report.refreshStatus();

		printReport(report);

		Assert.assertEquals(report.getStatus(), Report.STATE.WARNING," report must be on level warning");
		Assert.assertEquals(report.hasItems(), true," report must have items");
		boolean found = false;
		for (ReportItem item : report.getItems()) 
		{
			CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
			if (checkPointReport.getMessageKey().equals("3-Route-2"))
			{
				found = true;
				Assert.assertEquals(checkPointReport.getStatus(), Report.STATE.WARNING," checkPointReport must be on level warning");
				Assert.assertEquals(checkPointReport.hasItems(), true," checkPointReport must have items");
				Assert.assertEquals(checkPointReport.getItems().size(), 2," checkPointReport must have 2 item");

				//check detail keys = route1 and route2 objectids
				boolean route1objectIdFound = false;
				boolean route2objectIdFound = false;
				for (ReportItem ditem : checkPointReport.getItems()) 
				{
					DetailReportItem detailReport = (DetailReportItem) ditem;
					if (detailReport.getObjectId().equals(route1.getObjectId())) route1objectIdFound = true;
					if (detailReport.getObjectId().equals(route2.getObjectId())) route2objectIdFound = true;
				}
				Assert.assertTrue(route1objectIdFound,"detail report must refer route 1");
				Assert.assertTrue(route2objectIdFound,"detail report must refer route 2");
			}
		}
		Assert.assertTrue(found,"report must contain a 3-Route-2 checkPoint");

	}

	@SuppressWarnings("unchecked")
	@Test (groups = {"route"}, description = "3-Route-3" )
	public void verifyTest3_3() throws ChouetteException 
	{
		// 3-Route-3 : check distance between stops 

		IImportPlugin<Line> importLine = (IImportPlugin<Line>) applicationContext.getBean("NeptuneLineImport");

		long id = 1;
		
		JSONObject parameters = null;
		try {
			parameters = new RuleParameterSet();
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(parameters,"no parameters for test");

		List<Line> beans = LineLoader.load(importLine, "src/test/data/Ligne_OK.xml");
		Assert.assertFalse(beans.isEmpty(),"No data for test");
		Line line1 = beans.get(0);
		
		// line1 is model;
		line1.setId(id++);
		
		for (Route route : line1.getRoutes()) 
		{
			route.setId(id++);
		}
		
		Route route1 = line1.getRoutes().get(0);
		Route route2 = line1.getRoutes().get(1);
		
        StopArea area0 = route1.getStopPoints().get(0).getContainedInStopArea();
        double distanceMin = 10000000;
        double distanceMax = 0;
        for (int i = 1; i < route1.getStopPoints().size(); i++)
        {
        	StopArea area1 = route1.getStopPoints().get(i).getContainedInStopArea();
        	double distance = distance(area0, area1);
        	if (distance > distanceMax) distanceMax = distance;
        	if (distance < distanceMin) distanceMin = distance;
        	area0 = area1;
        }
        area0 = route2.getStopPoints().get(0).getContainedInStopArea();
        for (int i = 1; i < route2.getStopPoints().size(); i++)
        {
        	StopArea area1 = route2.getStopPoints().get(i).getContainedInStopArea();
        	double distance = distance(area0, area1);
        	if (distance > distanceMax) distanceMax = distance;
        	if (distance < distanceMin) distanceMin = distance;
        	area0 = area1;
        }
		
        parameters.getJSONObject("mode_bus").put("inter_stop_area_distance_min",(int) distanceMin + 10);
        parameters.getJSONObject("mode_bus").put("inter_stop_area_distance_max",(int) distanceMax - 10);
		PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

		checkPoint.check(line1.getRoutes(), parameters , report, new HashMap<String, Object>());
		report.refreshStatus();

		printReport(report);

		Assert.assertEquals(report.getStatus(), Report.STATE.WARNING," report must be on level warning");
		Assert.assertEquals(report.hasItems(), true," report must have items");
		boolean found = false;
		for (ReportItem item : report.getItems()) 
		{
			CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
			if (checkPointReport.getMessageKey().equals("3-Route-3"))
			{
				found = true;
				Assert.assertEquals(checkPointReport.getStatus(), Report.STATE.WARNING," checkPointReport must be on level warning");
				Assert.assertEquals(checkPointReport.hasItems(), true," checkPointReport must have items");
				Assert.assertEquals(checkPointReport.getItems().size(), 2," checkPointReport must have 2 item");

				//check detail keys = route1 objectids
				boolean routeobjectIdFound = false;
				for (ReportItem ditem : checkPointReport.getItems()) 
				{
					DetailReportItem detailReport = (DetailReportItem) ditem;
					if (detailReport.getObjectId().equals(route1.getObjectId())) routeobjectIdFound = true;
					if (detailReport.getObjectId().equals(route2.getObjectId())) routeobjectIdFound = true;
				}
				Assert.assertTrue(routeobjectIdFound,"detail report must refer route 1 or 2 ");
			}
		}
		Assert.assertTrue(found,"report must contain a 3-Route-3 checkPoint");

	}
	
	
	@SuppressWarnings("unchecked")
	@Test (groups = {"route"}, description = "3-Route-4" )
	public void verifyTest3_4() throws ChouetteException 
	{
		// 3-Route-4 : check identical routes

		IImportPlugin<Line> importLine = (IImportPlugin<Line>) applicationContext.getBean("NeptuneLineImport");

		long id = 1;
		
		JSONObject parameters = null;
		try {
			parameters = new RuleParameterSet();
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(parameters,"no parameters for test");

		List<Line> beans = LineLoader.load(importLine, "src/test/data/Ligne_OK.xml");
		Assert.assertFalse(beans.isEmpty(),"No data for test");
		Line line1 = beans.get(0);
		
		// line1 is model;
		line1.setId(id++);
		
		for (Route route : line1.getRoutes()) 
		{
			route.setId(id++);
		}
		
		Route route1 = line1.getRoutes().get(0);
		
		Route route2 = new Route();
		route2.setId(id++);
		route2.setLine(line1);
		
		line1.addRoute(route2);
		
		route1.setObjectId("NINOXE:Route:original");
		route2.setObjectId("NINOXE:Route:copy");
		
		for (StopPoint point : route1.getStopPoints())
		{
			StopPoint pointCopy = new StopPoint();
			pointCopy.setPosition(point.getPosition());
			pointCopy.setObjectId("NINOXE:StopPoint:copy"+point.getPosition());
			pointCopy.setContainedInStopArea(point.getContainedInStopArea());
			route2.addStopPoint(pointCopy);
		}
		
		PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

		checkPoint.check(line1.getRoutes(), parameters , report, new HashMap<String, Object>());
		report.refreshStatus();

		printReport(report);

		Assert.assertEquals(report.getStatus(), Report.STATE.ERROR," report must be on level error"); // cause test 7 no JP
		Assert.assertEquals(report.hasItems(), true," report must have items");
		boolean found = false;
		for (ReportItem item : report.getItems()) 
		{
			CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
			if (checkPointReport.getMessageKey().equals("3-Route-4"))
			{
				found = true;
				Assert.assertEquals(checkPointReport.getStatus(), Report.STATE.WARNING," checkPointReport must be on level warning");
				Assert.assertEquals(checkPointReport.hasItems(), true," checkPointReport must have items");
				Assert.assertEquals(checkPointReport.getItems().size(), 1," checkPointReport must have 1 item");

				//check detail keys = route1 objectids
				boolean route1objectIdFound = false;
				for (ReportItem ditem : checkPointReport.getItems()) 
				{
					DetailReportItem detailReport = (DetailReportItem) ditem;
					if (detailReport.getObjectId().equals(route1.getObjectId())) route1objectIdFound = true;
				}
				Assert.assertTrue(route1objectIdFound,"detail report must refer route 1");
			}
		}
		Assert.assertTrue(found,"report must contain a 3-Route-4 checkPoint");

	}
	
	@SuppressWarnings("unchecked")
	@Test (groups = {"route"}, description = "3-Route-5" )
	public void verifyTest3_5() throws ChouetteException 
	{
		// 3-Route-5 : check for potentially waybacks

		IImportPlugin<Line> importLine = (IImportPlugin<Line>) applicationContext.getBean("NeptuneLineImport");

		long id = 1;
		
		JSONObject parameters = null;
		try {
			parameters = new RuleParameterSet();
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(parameters,"no parameters for test");

		List<Line> beans = LineLoader.load(importLine, "src/test/data/Ligne_OK.xml");
		Assert.assertFalse(beans.isEmpty(),"No data for test");
		Line line1 = beans.get(0);
		
		// line1 is model;
		line1.setId(id++);
		
		for (Route route : line1.getRoutes()) 
		{
			route.setId(id++);
		}
		
		Route route1 = line1.getRoutes().get(0);
		Route route2 = line1.getRoutes().get(1);
		route1.setObjectId("NINOXE:Route:first");
		route1.setWayBackRoute(null);
		route2.setObjectId("NINOXE:Route:second");
      route2.setWayBackRoute(null);
				
		PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

		checkPoint.check(line1.getRoutes(), parameters , report, new HashMap<String, Object>());
		report.refreshStatus();

		printReport(report);

		Assert.assertEquals(report.getStatus(), Report.STATE.WARNING," report must be on level warning"); 
		Assert.assertEquals(report.hasItems(), true," report must have items");
		boolean found = false;
		for (ReportItem item : report.getItems()) 
		{
			CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
			if (checkPointReport.getMessageKey().equals("3-Route-5"))
			{
				found = true;
				Assert.assertEquals(checkPointReport.getStatus(), Report.STATE.WARNING," checkPointReport must be on level warning");
				Assert.assertEquals(checkPointReport.hasItems(), true," checkPointReport must have items");
				Assert.assertEquals(checkPointReport.getItems().size(), 1," checkPointReport must have 1 item");

				//check detail keys = route1 objectids
				boolean route1objectIdFound = false;
				for (ReportItem ditem : checkPointReport.getItems()) 
				{
					DetailReportItem detailReport = (DetailReportItem) ditem;
					if (detailReport.getObjectId().equals(route1.getObjectId())) route1objectIdFound = true;
				}
				Assert.assertTrue(route1objectIdFound,"detail report must refer route 1");
			}
		}
		Assert.assertTrue(found,"report must contain a 3-Route-5 checkPoint");

	}

	
	@SuppressWarnings("unchecked")
	@Test (groups = {"route"}, description = "3-Route-6" )
	public void verifyTest3_6() throws ChouetteException 
	{
		// 3-Route-6 : check if route has minimum 2 StopPoints

		IImportPlugin<Line> importLine = (IImportPlugin<Line>) applicationContext.getBean("NeptuneLineImport");

		long id = 1;
		
		JSONObject parameters = null;
		try {
			parameters = new RuleParameterSet();
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(parameters,"no parameters for test");

		List<Line> beans = LineLoader.load(importLine, "src/test/data/Ligne_OK.xml");
		Assert.assertFalse(beans.isEmpty(),"No data for test");
		Line line1 = beans.get(0);
		
		// line1 is model;
		line1.setId(id++);
		
		for (Route route : line1.getRoutes()) 
		{
			route.setId(id++);
		}
		
		Route route1 = line1.getRoutes().get(0);
		while (route1.getStopPoints().size() > 1)
		{
			route1.removeStopPointAt(0);
		}
		
		route1.setObjectId("NINOXE:Route:first");
				
		PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

		checkPoint.check(line1.getRoutes(), parameters , report, new HashMap<String, Object>());
		report.refreshStatus();

		printReport(report);

		Assert.assertEquals(report.getStatus(), Report.STATE.ERROR," report must be on level error"); 
		Assert.assertEquals(report.hasItems(), true," report must have items");
		boolean found = false;
		for (ReportItem item : report.getItems()) 
		{
			CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
			if (checkPointReport.getMessageKey().equals("3-Route-6"))
			{
				found = true;
				Assert.assertEquals(checkPointReport.getStatus(), Report.STATE.ERROR," checkPointReport must be on level error");
				Assert.assertEquals(checkPointReport.hasItems(), true," checkPointReport must have items");
				Assert.assertEquals(checkPointReport.getItems().size(), 1," checkPointReport must have 1 item");

				//check detail keys = route1 objectids
				boolean route1objectIdFound = false;
				for (ReportItem ditem : checkPointReport.getItems()) 
				{
					DetailReportItem detailReport = (DetailReportItem) ditem;
					if (detailReport.getObjectId().equals(route1.getObjectId())) route1objectIdFound = true;
				}
				Assert.assertTrue(route1objectIdFound,"detail report must refer route 1");
			}
		}
		Assert.assertTrue(found,"report must contain a 3-Route-6 checkPoint");

	}

	
	@SuppressWarnings("unchecked")
	@Test (groups = {"route"}, description = "3-Route-7" )
	public void verifyTest3_7() throws ChouetteException 
	{
		// 3-Route-7 : check if route has minimum 1 JourneyPattern

		IImportPlugin<Line> importLine = (IImportPlugin<Line>) applicationContext.getBean("NeptuneLineImport");

		long id = 1;
		
		JSONObject parameters = null;
		try {
			parameters = new RuleParameterSet();
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(parameters,"no parameters for test");

		List<Line> beans = LineLoader.load(importLine, "src/test/data/Ligne_OK.xml");
		Assert.assertFalse(beans.isEmpty(),"No data for test");
		Line line1 = beans.get(0);
		
		// line1 is model;
		line1.setId(id++);
		
		for (Route route : line1.getRoutes()) 
		{
			route.setId(id++);
		}
		
		Route route1 = line1.getRoutes().get(0);
		
		route1.getJourneyPatterns().clear();
		route1.setObjectId("NINOXE:Route:first");
				
		PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

		checkPoint.check(line1.getRoutes(), parameters , report, new HashMap<String, Object>());
		report.refreshStatus();

		printReport(report);

		Assert.assertEquals(report.getStatus(), Report.STATE.ERROR," report must be on level error"); 
		Assert.assertEquals(report.hasItems(), true," report must have items");
		boolean found = false;
		for (ReportItem item : report.getItems()) 
		{
			CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
			if (checkPointReport.getMessageKey().equals("3-Route-7"))
			{
				found = true;
				Assert.assertEquals(checkPointReport.getStatus(), Report.STATE.ERROR," checkPointReport must be on level error");
				Assert.assertEquals(checkPointReport.hasItems(), true," checkPointReport must have items");
				Assert.assertEquals(checkPointReport.getItems().size(), 1," checkPointReport must have 1 item");

				//check detail keys = route1 objectids
				boolean route1objectIdFound = false;
				for (ReportItem ditem : checkPointReport.getItems()) 
				{
					DetailReportItem detailReport = (DetailReportItem) ditem;
					if (detailReport.getObjectId().equals(route1.getObjectId())) route1objectIdFound = true;
				}
				Assert.assertTrue(route1objectIdFound,"detail report must refer route 1");
			}
		}
		Assert.assertTrue(found,"report must contain a 3-Route-7 checkPoint");

	}

	
	@SuppressWarnings("unchecked")
	@Test (groups = {"route"}, description = "3-Route-8" )
	public void verifyTest3_8() throws ChouetteException 
	{
		// 3-Route-8 : check if all stopPoints are used by journeyPatterns

		IImportPlugin<Line> importLine = (IImportPlugin<Line>) applicationContext.getBean("NeptuneLineImport");

		long id = 1;
		
		JSONObject parameters = null;
		try {
			parameters = new RuleParameterSet();
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(parameters,"no parameters for test");

		List<Line> beans = LineLoader.load(importLine, "src/test/data/Ligne_OK.xml");
		Assert.assertFalse(beans.isEmpty(),"No data for test");
		Line line1 = beans.get(0);
		
		// line1 is model;
		line1.setId(id++);
		
		for (Route route : line1.getRoutes()) 
		{
			route.setId(id++);
		}
		
		Route route1 = line1.getRoutes().get(0);
		
		route1.getJourneyPatterns().get(0).removeStopPoint(route1.getJourneyPatterns().get(0).getStopPoints().get(0));
		route1.setObjectId("NINOXE:Route:first");
				
		PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

		checkPoint.check(line1.getRoutes(), parameters , report, new HashMap<String, Object>());
		report.refreshStatus();

		printReport(report);

		Assert.assertEquals(report.getStatus(), Report.STATE.WARNING," report must be on level warning"); 
		Assert.assertEquals(report.hasItems(), true," report must have items");
		boolean found = false;
		for (ReportItem item : report.getItems()) 
		{
			CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
			if (checkPointReport.getMessageKey().equals("3-Route-8"))
			{
				found = true;
				Assert.assertEquals(checkPointReport.getStatus(), Report.STATE.WARNING," checkPointReport must be on level warning");
				Assert.assertEquals(checkPointReport.hasItems(), true," checkPointReport must have items");
				Assert.assertEquals(checkPointReport.getItems().size(), 1," checkPointReport must have 1 item");

				//check detail keys = route1 objectids
				boolean route1objectIdFound = false;
				for (ReportItem ditem : checkPointReport.getItems()) 
				{
					DetailReportItem detailReport = (DetailReportItem) ditem;
					if (detailReport.getObjectId().equals(route1.getObjectId())) route1objectIdFound = true;
				}
				Assert.assertTrue(route1objectIdFound,"detail report must refer route 1");
			}
		}
		Assert.assertTrue(found,"report must contain a 3-Route-8 checkPoint");

	}

	
	@SuppressWarnings("unchecked")
	@Test (groups = {"route"}, description = "3-Route-9" )
	public void verifyTest3_9() throws ChouetteException 
	{
		// 3-Route-9 : check if one journeyPattern uses all stopPoints

		IImportPlugin<Line> importLine = (IImportPlugin<Line>) applicationContext.getBean("NeptuneLineImport");

		long id = 1;
		
		JSONObject parameters = null;
		try {
			parameters = new RuleParameterSet();
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(parameters,"no parameters for test");

		List<Line> beans = LineLoader.load(importLine, "src/test/data/3-Route-9.xml");
		Assert.assertFalse(beans.isEmpty(),"No data for test");
		Line line1 = beans.get(0);
		
		// line1 is model;
		line1.setId(id++);
		
		for (Route route : line1.getRoutes()) 
		{
			route.setId(id++);
		}
		
		Route route1 = line1.getRoutes().get(0);
		
		route1.getJourneyPatterns().clear();
		route1.setObjectId("NINOXE:Route:first");
				
		PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

		checkPoint.check(line1.getRoutes(), parameters , report, new HashMap<String, Object>());
		report.refreshStatus();

		printReport(report);

		Assert.assertEquals(report.getStatus(), Report.STATE.ERROR," report must be on level error"); 
		Assert.assertEquals(report.hasItems(), true," report must have items");
		boolean found = false;
		for (ReportItem item : report.getItems()) 
		{
			CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
			if (checkPointReport.getMessageKey().equals("3-Route-7"))
			{
				found = true;
				Assert.assertEquals(checkPointReport.getStatus(), Report.STATE.ERROR," checkPointReport must be on level error");
				Assert.assertEquals(checkPointReport.hasItems(), true," checkPointReport must have items");
				Assert.assertEquals(checkPointReport.getItems().size(), 1," checkPointReport must have 1 item");

				//check detail keys = route1 objectids
				boolean route1objectIdFound = false;
				for (ReportItem ditem : checkPointReport.getItems()) 
				{
					DetailReportItem detailReport = (DetailReportItem) ditem;
					if (detailReport.getObjectId().equals(route1.getObjectId())) route1objectIdFound = true;
				}
				Assert.assertTrue(route1objectIdFound,"detail report must refer route 1");
			}
		}
		Assert.assertTrue(found,"report must contain a 3-Route-7 checkPoint");

	}

}
