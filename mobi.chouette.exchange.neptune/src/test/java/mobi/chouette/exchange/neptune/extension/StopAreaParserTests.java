package mobi.chouette.exchange.neptune.extension;

import mobi.chouette.exchange.neptune.JsonExtension;
import mobi.chouette.model.StopArea;

import org.codehaus.jettison.json.JSONObject;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

public class StopAreaParserTests {

	private CommentExtension parser = new CommentExtension();

	@Test(groups = { "parseComment" }, description = "check empty comment and extensions")
	public void verifyBuildEmptyComment() throws Exception {
		String xmlComment = null;
		StopArea stop = new StopArea();
		stop.setComment(xmlComment);

		parser.parseJsonComment(stop);
		Assert.assertNull(stop.getComment(), "comment should be null");
		Assert.assertNull(stop.getUrl(), "url should be null");
		Assert.assertNull(stop.getTimeZone(), "timezone should be null");
	}

	@Test(groups = { "parseComment" }, description = "check normal comment without extensions")
	public void verifyBuildNormalComment() throws Exception {
		String xmlComment = "dummy text";
		StopArea stop = new StopArea();
		stop.setComment(xmlComment);

		parser.parseJsonComment(stop);
		Assert.assertEquals(stop.getComment(), xmlComment, "comment should be filled");
		Assert.assertNull(stop.getUrl(), "url should be null");
		Assert.assertNull(stop.getTimeZone(), "timezone should be null");
	}

	@Test(groups = { "parseComment" }, description = "check null comment with timezone extension")
	public void verifyBuildTimezoneComment() throws Exception {
		JSONObject jsonComment = new JSONObject();
		jsonComment.put(JsonExtension.TIME_ZONE, "Europe/Paris");
		String xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		StopArea stop = new StopArea();

		stop.setComment(xmlComment);
		parser.parseJsonComment(stop);
		Assert.assertNull(stop.getComment(), "comment should be null");
		Assert.assertNull(stop.getCityName(), "city name should be null");
		Assert.assertNull(stop.getZipCode(), "zip code should be null");
		Assert.assertEquals(stop.getTimeZone(), "Europe/Paris", "timezone should be set");
		Assert.assertNull(stop.getUrl(), "url should be null");
	}

	@Test(groups = { "parseComment" }, description = "check null comment with url extension")
	public void verifyBuildUrlComment() throws Exception {
		JSONObject jsonComment = new JSONObject();
		jsonComment.put(JsonExtension.URL_REF, "http://mystoparea.com");
		String xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		StopArea stop = new StopArea();

		stop.setComment(xmlComment);
		parser.parseJsonComment(stop);
		Assert.assertNull(stop.getComment(), "comment should be null");
		Assert.assertNull(stop.getCityName(), "city name should be null");
		Assert.assertNull(stop.getZipCode(), "zip code should be null");
		Assert.assertNull(stop.getTimeZone(), "timezone should be null");
		Assert.assertEquals(stop.getUrl(), "http://mystoparea.com", "url should be set");

	}

	@Test(groups = { "parseComment" }, description = "check null comment with zip code extension")
	public void verifyBuildZipCodeComment() throws Exception {
		JSONObject jsonComment = new JSONObject();
		jsonComment.put(JsonExtension.ZIP_CODE, "12345");
		String xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		StopArea stop = new StopArea();

		stop.setComment(xmlComment);
		parser.parseJsonComment(stop);
		Assert.assertNull(stop.getComment(), "comment should be null");
		Assert.assertNull(stop.getCityName(), "city name should be null");
		Assert.assertEquals(stop.getZipCode(),"12345", "zip code should be set");
		Assert.assertNull(stop.getTimeZone(), "timezone should be null");
		Assert.assertNull(stop.getUrl(), "url should be null");

	}

	@Test(groups = { "parseComment" }, description = "check null comment with city name extension")
	public void verifyBuildCityNameComment() throws Exception {
		JSONObject jsonComment = new JSONObject();
		jsonComment.put(JsonExtension.CITY_NAME, "Paris");
		String xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		StopArea stop = new StopArea();

		stop.setComment(xmlComment);
		parser.parseJsonComment(stop);
		Assert.assertNull(stop.getComment(), "comment should be null");
		Assert.assertEquals(stop.getCityName(),"Paris", "city name should be set");
		Assert.assertNull(stop.getZipCode(), "zip code should be null");
		Assert.assertNull(stop.getTimeZone(), "timezone should be null");
		Assert.assertNull(stop.getUrl(), "url should be null");

	}

	@Test(groups = { "parseComment" }, description = "check comment with all extension")
	public void verifyBuildCompleteComment() throws Exception {
		JSONObject jsonComment = new JSONObject();
		jsonComment.put(JsonExtension.TIME_ZONE, "Europe/Paris");
		jsonComment.put(JsonExtension.COMMENT, "dummy text");
		jsonComment.put(JsonExtension.URL_REF, "http://mystoparea.com");
		jsonComment.put(JsonExtension.CITY_NAME, "Paris");
		jsonComment.put(JsonExtension.ZIP_CODE, "12345");

		String xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		StopArea stop = new StopArea();

		stop.setComment(xmlComment);
		parser.parseJsonComment(stop);
		Assert.assertEquals(stop.getComment(), "dummy text", "comment should be filled");
		Assert.assertEquals(stop.getTimeZone(), "Europe/Paris", "timezone should be set");
		Assert.assertEquals(stop.getUrl(), "http://mystoparea.com", "url should be set");
		Assert.assertEquals(stop.getZipCode(),"12345", "zip code should be set");
		Assert.assertEquals(stop.getCityName(),"Paris", "city name should be set");

	}

}
