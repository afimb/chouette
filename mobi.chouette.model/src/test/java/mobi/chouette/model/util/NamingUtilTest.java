package mobi.chouette.model.util;

import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.Company;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;

import org.testng.Assert;
import org.testng.annotations.Test;

public class NamingUtilTest {
	@Test(groups = { "model" }, description = "network name")
	public void testNetworkName() throws Exception 
	{
		Network object = new Network();
		object.setObjectId("test:PtNetwork:1");
		object.setName("test");
		Assert.assertEquals(NamingUtil.getName(object),object.getName(),"name should be returned");
		object.setName("");
		Assert.assertEquals(NamingUtil.getName(object),object.getObjectId(),"objectId should be returned");
		object.setName(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getObjectId(),"objectId should be returned");
	}
	@Test(groups = { "model" }, description = "company name")
	public void testCompanyName() throws Exception 
	{
		Company object = new Company();
		object.setObjectId("test:Company:1");
		object.setName("test");
		Assert.assertEquals(NamingUtil.getName(object),object.getName(),"name should be returned");
		object.setName("");
		Assert.assertEquals(NamingUtil.getName(object),object.getObjectId(),"objectId should be returned");
		object.setName(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getObjectId(),"objectId should be returned");
	}
	@Test(groups = { "model" }, description = "GroupOfLine name")
	public void testGroupOfLineName() throws Exception 
	{
		GroupOfLine object = new GroupOfLine();
		object.setObjectId("test:GroupOfLine:1");
		object.setName("test");
		Assert.assertEquals(NamingUtil.getName(object),object.getName(),"name should be returned");
		object.setName("");
		Assert.assertEquals(NamingUtil.getName(object),object.getObjectId(),"objectId should be returned");
		object.setName(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getObjectId(),"objectId should be returned");
	}
	@Test(groups = { "model" }, description = "StopArea name")
	public void testStopAreaName() throws Exception 
	{
		StopArea object = new StopArea();
		object.setObjectId("test:StopArea:1");
		object.setName("test");
		Assert.assertEquals(NamingUtil.getName(object),object.getName(),"name should be returned");
		object.setName("");
		Assert.assertEquals(NamingUtil.getName(object),object.getObjectId(),"objectId should be returned");
		object.setName(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getObjectId(),"objectId should be returned");
	}
	@Test(groups = { "model" }, description = "AccessPoint name")
	public void testAccessPointName() throws Exception 
	{
		AccessPoint object = new AccessPoint();
		object.setObjectId("test:AccessPoint:1");
		object.setName("test");
		Assert.assertEquals(NamingUtil.getName(object),object.getName(),"name should be returned");
		object.setName("");
		Assert.assertEquals(NamingUtil.getName(object),object.getObjectId(),"objectId should be returned");
		object.setName(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getObjectId(),"objectId should be returned");
	}
	@Test(groups = { "model" }, description = "AccessLink name")
	public void testAccessLinkName() throws Exception 
	{
		AccessLink object = new AccessLink();
		object.setObjectId("test:AccessLink:1");
		object.setName("test");
		Assert.assertEquals(NamingUtil.getName(object),object.getName(),"name should be returned");
		object.setName("");
		Assert.assertEquals(NamingUtil.getName(object),object.getObjectId(),"objectId should be returned");
		object.setName(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getObjectId(),"objectId should be returned");
	}
	@Test(groups = { "model" }, description = "ConnectionLink name")
	public void testConnectionLinkName() throws Exception 
	{
		ConnectionLink object = new ConnectionLink();
		object.setObjectId("test:ConnectionLink:1");
		object.setName("test");
		Assert.assertEquals(NamingUtil.getName(object),object.getName(),"name should be returned");
		object.setName("");
		Assert.assertEquals(NamingUtil.getName(object),object.getObjectId(),"objectId should be returned");
		object.setName(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getObjectId(),"objectId should be returned");
	}
	@Test(groups = { "model" }, description = "Timetable name")
	public void testTimetableName() throws Exception 
	{
		Timetable object = new Timetable();
		object.setObjectId("test:Timetable:1");
		object.setComment("test");
		Assert.assertEquals(NamingUtil.getName(object),object.getComment(),"comment should be returned");
		object.setComment("");
		Assert.assertEquals(NamingUtil.getName(object),object.getObjectId(),"objectId should be returned");
		object.setComment(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getObjectId(),"objectId should be returned");
	}
	@Test(groups = { "model" }, description = "StopPoint name")
	public void testStopPointName() throws Exception 
	{
		StopPoint object = new StopPoint();
		object.setObjectId("test:StopPoint:1");
		Assert.assertEquals(NamingUtil.getName(object),object.getObjectId(),"objectId should be returned");
	}
	@Test(groups = { "model" }, description = "Line name")
	public void testLineName() throws Exception 
	{
		Line object = new Line();
		object.setObjectId("test:Line:1");
		object.setName("lineName");
		object.setPublishedName("publishedName");
		object.setNumber("number");
		Assert.assertEquals(NamingUtil.getName(object),object.getName(),"name should be returned");
		object.setName("");
		Assert.assertEquals(NamingUtil.getName(object),object.getPublishedName(),"publishedName should be returned");
		object.setName(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getPublishedName(),"publishedName should be returned");
		object.setPublishedName("");
		Assert.assertEquals(NamingUtil.getName(object),object.getNumber(),"number should be returned");
		object.setPublishedName(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getNumber(),"number should be returned");
		object.setNumber("");
		Assert.assertEquals(NamingUtil.getName(object),object.getObjectId(),"objectId should be returned");
		object.setNumber(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getObjectId(),"objectId should be returned");
	}
	@Test(groups = { "model" }, description = "Route name")
	public void testRouteName() throws Exception 
	{
		Route object = new Route();
		object.setObjectId("test:Route:1");
		object.setName("routeName");
		object.setPublishedName("publishedName");
		object.setNumber("number");
		Assert.assertEquals(NamingUtil.getName(object),object.getName(),"name should be returned");
		object.setName("");
		Assert.assertEquals(NamingUtil.getName(object),object.getPublishedName(),"publishedName should be returned");
		object.setName(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getPublishedName(),"publishedName should be returned");
		object.setPublishedName("");
		Assert.assertEquals(NamingUtil.getName(object),object.getNumber(),"number should be returned");
		object.setPublishedName(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getNumber(),"number should be returned");
		object.setNumber("");
		Assert.assertEquals(NamingUtil.getName(object),object.getObjectId(),"objectId should be returned");
		object.setNumber(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getObjectId(),"objectId should be returned");
	}
	@Test(groups = { "model" }, description = "JourneyPattern name")
	public void testJourneyPatternName() throws Exception 
	{
		JourneyPattern object = new JourneyPattern();
		object.setObjectId("test:Route:1");
		object.setName("routeName");
		object.setPublishedName("publishedName");
		Assert.assertEquals(NamingUtil.getName(object),object.getName(),"name should be returned");
		object.setName("");
		Assert.assertEquals(NamingUtil.getName(object),object.getPublishedName(),"publishedName should be returned");
		object.setName(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getPublishedName(),"publishedName should be returned");
		object.setPublishedName("");
		Assert.assertEquals(NamingUtil.getName(object),object.getObjectId(),"objectId should be returned");
		object.setPublishedName(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getObjectId(),"objectId should be returned");
	}
	@Test(groups = { "model" }, description = "VehicleJourney name")
	public void testVehicleJourneyName() throws Exception 
	{
		VehicleJourney object = new VehicleJourney();
		object.setObjectId("test:VehicleJourney:1");
		object.setPublishedJourneyName("test");
		object.setNumber(Long.valueOf(1));
		Assert.assertEquals(NamingUtil.getName(object),object.getPublishedJourneyName(),"PublishedJourneyName should be returned");
		object.setPublishedJourneyName("");
		Assert.assertEquals(NamingUtil.getName(object),object.getNumber().toString(),"number should be returned");
		object.setPublishedJourneyName(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getNumber().toString(),"number should be returned");
		object.setNumber(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getObjectId(),"objectId should be returned");
	}

}
