package mobi.chouette.model.util;

import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.ChouetteId;
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
//		object.getChouetteId().setObjectId("test:Network:1");
		ChouetteId chouetteId = new ChouetteId();
		chouetteId.setCodeSpace("test");
		chouetteId.setObjectId("1");
		object.setChouetteId(chouetteId);
		object.setName("test");
		Assert.assertEquals(NamingUtil.getName(object),object.getName(),"name should be returned");
		object.setName("");
		Assert.assertEquals(NamingUtil.getName(object),object.getChouetteId().getObjectId(),"objectId should be returned");
		object.setName(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getChouetteId().getObjectId(),"objectId should be returned");
	}
	@Test(groups = { "model" }, description = "company name")
	public void testCompanyName() throws Exception 
	{
		Company object = new Company();
//		object.getChouetteId().setObjectId("test:Company:1");
		ChouetteId chouetteId = new ChouetteId();
		chouetteId.setCodeSpace("test");
		chouetteId.setObjectId("1");
		object.setChouetteId(chouetteId);
		object.setName("test");
		Assert.assertEquals(NamingUtil.getName(object),object.getName(),"name should be returned");
		object.setName("");
		Assert.assertEquals(NamingUtil.getName(object),object.getChouetteId().getObjectId(),"objectId should be returned");
		object.setName(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getChouetteId().getObjectId(),"objectId should be returned");
	}
	@Test(groups = { "model" }, description = "GroupOfLine name")
	public void testGroupOfLineName() throws Exception 
	{
		GroupOfLine object = new GroupOfLine();
//		object.getChouetteId().setObjectId("test:GroupOfLine:1");
		ChouetteId chouetteId = new ChouetteId();
		chouetteId.setCodeSpace("test");
		chouetteId.setObjectId("1");
		object.setChouetteId(chouetteId);
		object.setName("test");
		Assert.assertEquals(NamingUtil.getName(object),object.getName(),"name should be returned");
		object.setName("");
		Assert.assertEquals(NamingUtil.getName(object),object.getChouetteId().getObjectId(),"objectId should be returned");
		object.setName(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getChouetteId().getObjectId(),"objectId should be returned");
	}
	@Test(groups = { "model" }, description = "StopArea name")
	public void testStopAreaName() throws Exception 
	{
		StopArea object = new StopArea();
//		object.getChouetteId().setObjectId("test:StopArea:1");
		ChouetteId chouetteId = new ChouetteId();
		chouetteId.setCodeSpace("test");
		chouetteId.setObjectId("1");
		object.setChouetteId(chouetteId);
		object.setName("test");
		Assert.assertEquals(NamingUtil.getName(object),object.getName(),"name should be returned");
		object.setName("");
		Assert.assertEquals(NamingUtil.getName(object),object.getChouetteId().getObjectId(),"objectId should be returned");
		object.setName(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getChouetteId().getObjectId(),"objectId should be returned");
	}
	@Test(groups = { "model" }, description = "AccessPoint name")
	public void testAccessPointName() throws Exception 
	{
		AccessPoint object = new AccessPoint();
//		object.getChouetteId().setObjectId("test:AccessPoint:1");
		ChouetteId chouetteId = new ChouetteId();
		chouetteId.setCodeSpace("test");
		chouetteId.setObjectId("1");
		object.setChouetteId(chouetteId);
		object.setName("test");
		Assert.assertEquals(NamingUtil.getName(object),object.getName(),"name should be returned");
		object.setName("");
		Assert.assertEquals(NamingUtil.getName(object),object.getChouetteId().getObjectId(),"objectId should be returned");
		object.setName(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getChouetteId().getObjectId(),"objectId should be returned");
	}
	@Test(groups = { "model" }, description = "AccessLink name")
	public void testAccessLinkName() throws Exception 
	{
		AccessLink object = new AccessLink();
//		object.getChouetteId().setObjectId("test:AccessLink:1");
		ChouetteId chouetteId = new ChouetteId();
		chouetteId.setCodeSpace("test");
		chouetteId.setObjectId("1");
		object.setChouetteId(chouetteId);
		object.setName("test");
		Assert.assertEquals(NamingUtil.getName(object),object.getName(),"name should be returned");
		object.setName("");
		Assert.assertEquals(NamingUtil.getName(object),object.getChouetteId().getObjectId(),"objectId should be returned");
		object.setName(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getChouetteId().getObjectId(),"objectId should be returned");
	}
	@Test(groups = { "model" }, description = "ConnectionLink name")
	public void testConnectionLinkName() throws Exception 
	{
		ConnectionLink object = new ConnectionLink();
//		object.getChouetteId().setObjectId("test:ConnectionLink:1");
		ChouetteId chouetteId = new ChouetteId();
		chouetteId.setCodeSpace("test");
		chouetteId.setObjectId("1");
		object.setChouetteId(chouetteId);
		object.setName("test");
		Assert.assertEquals(NamingUtil.getName(object),object.getName(),"name should be returned");
		object.setName("");
		Assert.assertEquals(NamingUtil.getName(object),object.getChouetteId().getObjectId(),"objectId should be returned");
		object.setName(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getChouetteId().getObjectId(),"objectId should be returned");
	}
	@Test(groups = { "model" }, description = "Timetable name")
	public void testTimetableName() throws Exception 
	{
		Timetable object = new Timetable();
//		object.getChouetteId().setObjectId("test:Timetable:1");
		ChouetteId chouetteId = new ChouetteId();
		chouetteId.setCodeSpace("test");
		chouetteId.setObjectId("1");
		object.setChouetteId(chouetteId);
		object.setComment("test");
		Assert.assertEquals(NamingUtil.getName(object),object.getComment(),"comment should be returned");
		object.setComment("");
		Assert.assertEquals(NamingUtil.getName(object),object.getChouetteId().getObjectId(),"objectId should be returned");
		object.setComment(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getChouetteId().getObjectId(),"objectId should be returned");
	}
	@Test(groups = { "model" }, description = "StopPoint name")
	public void testStopPointName() throws Exception 
	{
		StopPoint object = new StopPoint();
//		object.getChouetteId().setObjectId("test:StopPoint:1");
		ChouetteId chouetteId = new ChouetteId();
		chouetteId.setCodeSpace("test");
		chouetteId.setObjectId("1");
		object.setChouetteId(chouetteId);
		Assert.assertEquals(NamingUtil.getName(object),object.getChouetteId().getObjectId(),"objectId should be returned");
	}
	@Test(groups = { "model" }, description = "Line name")
	public void testLineName() throws Exception 
	{
		Line object = new Line();
//		object.getChouetteId().setObjectId("test:Line:1");
		ChouetteId chouetteId = new ChouetteId();
		chouetteId.setCodeSpace("test");
		chouetteId.setObjectId("1");
		object.setChouetteId(chouetteId);
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
		Assert.assertEquals(NamingUtil.getName(object),object.getChouetteId().getObjectId(),"objectId should be returned");
		object.setNumber(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getChouetteId().getObjectId(),"objectId should be returned");
	}
	@Test(groups = { "model" }, description = "Route name")
	public void testRouteName() throws Exception 
	{
		Route object = new Route();
//		object.getChouetteId().setObjectId("test:Route:1");
		ChouetteId chouetteId = new ChouetteId();
		chouetteId.setCodeSpace("test");
		chouetteId.setObjectId("1");
		object.setChouetteId(chouetteId);
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
		Assert.assertEquals(NamingUtil.getName(object),object.getChouetteId().getObjectId(),"objectId should be returned");
		object.setNumber(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getChouetteId().getObjectId(),"objectId should be returned");
	}
	@Test(groups = { "model" }, description = "JourneyPattern name")
	public void testJourneyPatternName() throws Exception 
	{
		JourneyPattern object = new JourneyPattern();
//		object.getChouetteId().setObjectId("test:Route:1");
		ChouetteId chouetteId = new ChouetteId();
		chouetteId.setCodeSpace("test");
		chouetteId.setObjectId("1");
		object.setChouetteId(chouetteId);
		object.setName("routeName");
		object.setPublishedName("publishedName");
		Assert.assertEquals(NamingUtil.getName(object),object.getName(),"name should be returned");
		object.setName("");
		Assert.assertEquals(NamingUtil.getName(object),object.getPublishedName(),"publishedName should be returned");
		object.setName(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getPublishedName(),"publishedName should be returned");
		object.setPublishedName("");
		Assert.assertEquals(NamingUtil.getName(object),object.getChouetteId().getObjectId(),"objectId should be returned");
		object.setPublishedName(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getChouetteId().getObjectId(),"objectId should be returned");
	}
	@Test(groups = { "model" }, description = "VehicleJourney name")
	public void testVehicleJourneyName() throws Exception 
	{
		VehicleJourney object = new VehicleJourney();
//		object.getChouetteId().setObjectId("test:VehicleJourney:1");
		ChouetteId chouetteId = new ChouetteId();
		chouetteId.setCodeSpace("test");
		chouetteId.setObjectId("1");
		object.setChouetteId(chouetteId);
		object.setPublishedJourneyName("test");
		object.setNumber(Long.valueOf(1));
		Assert.assertEquals(NamingUtil.getName(object),object.getPublishedJourneyName(),"PublishedJourneyName should be returned");
		object.setPublishedJourneyName("");
		Assert.assertEquals(NamingUtil.getName(object),object.getNumber().toString(),"number should be returned");
		object.setPublishedJourneyName(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getNumber().toString(),"number should be returned");
		object.setNumber(null);
		Assert.assertEquals(NamingUtil.getName(object),object.getChouetteId().getObjectId(),"objectId should be returned");
	}

}
