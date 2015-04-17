package mobi.chouette.exchange.neptune.exporter.producer;

import mobi.chouette.exchange.neptune.JsonExtension;
import mobi.chouette.model.StopArea;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

public class StopAreaProducerTests {

	private StopAreaProducer producer = new StopAreaProducer();

	@Test(groups = { "buildComment" }, description = "check empty comment and extensions")
	public void verifyBuildEmptyComment() throws Exception {
		StopArea stop = new StopArea();

		String xmlComment = producer.buildComment(stop, true);
		Assert.assertNull(xmlComment, "comment should be null");
	}

	@Test(groups = { "buildComment" }, description = "check normal comment without extensions")
	public void verifyBuildNormalComment() throws Exception {
		StopArea stop = new StopArea();
		stop.setComment("dummy comment");

		String xmlComment = producer.buildComment(stop, true);
		Reporter.log("comment = " + xmlComment);
		Assert.assertEquals(xmlComment, "dummy comment", "comment should be correctly built");
	}

	@Test(groups = { "buildComment" }, description = "check null comment with timezone")
	public void verifyBuildTimezoneComment() throws Exception {
		StopArea stop = new StopArea();
		stop.setTimeZone("Europe/Paris");

		String xmlComment = producer.buildComment(stop, true);
		Reporter.log("comment = " + xmlComment);
		Assert.assertEquals(xmlComment, "{\"time_zone\":\"Europe\\/Paris\"}", "comment should be correctly built");
	}

	@Test(groups = { "buildComment" }, description = "check null comment with url extension")
	public void verifyBuildUrlComment() throws Exception {
		StopArea stop = new StopArea();
		stop.setUrl("http://mystoparea.com");

		String xmlComment = producer.buildComment(stop, true);
		Reporter.log("comment = " + xmlComment);
		Assert.assertEquals(xmlComment, "{\"url\":\"http:\\/\\/mystoparea.com\"}", "comment should be correctly built");

	}

	@Test(groups = { "buildComment" }, description = "check comment with all extension")
	public void verifyBuildCompleteComment() throws Exception {
		StopArea stop = new StopArea();
		stop.setComment("dummy comment");
		stop.setUrl("http://mystoparea.com");
		stop.setTimeZone("Europe/Paris");
		String xmlComment = producer.buildComment(stop, true);
		Reporter.log("comment = " + xmlComment);

		Assert.assertTrue(xmlComment.startsWith("{"), "comment should start with {");
		Assert.assertTrue(xmlComment.endsWith("}"), "comment should end with }");
		Assert.assertTrue(xmlComment.contains(JsonExtension.COMMENT), "comment should contain url tag");
		Assert.assertTrue(xmlComment.contains(JsonExtension.URL_REF), "comment should contain url tag");
		Assert.assertTrue(xmlComment.contains(JsonExtension.TIME_ZONE), "comment should contain timezone tag");

	}

	@Test(groups = { "buildComment" }, description = "check comment with no extension asked")
	public void verifyBuildNoExtension() throws Exception {
		StopArea stop = new StopArea();
		stop.setComment("dummy comment");
		stop.setUrl("http://mystoparea.com");
		stop.setTimeZone("Europe/Paris");
		String xmlComment = producer.buildComment(stop, false);
		Reporter.log("comment = " + xmlComment);

		Assert.assertEquals(xmlComment, "dummy comment", "comment should be correctly built");

	}

}
