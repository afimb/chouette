package mobi.chouette.exchange.neptune.exporter.producer;

import mobi.chouette.exchange.neptune.JsonExtension;
import mobi.chouette.model.GroupOfLine;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

public class GroupOfLineProducerTests {

	private GroupOfLineProducer producer = new GroupOfLineProducer();

	@Test(groups = { "buildComment" }, description = "check empty comment and extensions")
	public void verifyBuildEmptyComment() throws Exception {
		GroupOfLine gline = new GroupOfLine();

		String xmlComment = producer.buildComment(gline, true);
		Assert.assertNull(xmlComment, "comment should be null");
	}

	@Test(groups = { "buildComment" }, description = "check normal comment without extensions")
	public void verifyBuildNormalComment() throws Exception {
		GroupOfLine gline = new GroupOfLine();
		gline.setComment("dummy comment");

		String xmlComment = producer.buildComment(gline, true);
		Reporter.log("comment = " + xmlComment);
		Assert.assertEquals(xmlComment, "dummy comment", "comment should be correctly built");
	}

	@Test(groups = { "buildComment" }, description = "check null comment with registration number")
	public void verifyBuildRegistrationNumberComment() throws Exception {
		GroupOfLine gline = new GroupOfLine();
		gline.setRegistrationNumber("R_N");

		String xmlComment = producer.buildComment(gline, true);
		Reporter.log("comment = " + xmlComment);
		Assert.assertEquals(xmlComment, "{\"registration_number\":\"R_N\"}", "comment should be correctly built");
	}


	@Test(groups = { "buildComment" }, description = "check comment with all extension")
	public void verifyBuildCompleteComment() throws Exception {
		GroupOfLine gline = new GroupOfLine();
		gline.setComment("dummy comment");
		gline.setRegistrationNumber("R_N");
		String xmlComment = producer.buildComment(gline, true);
		Reporter.log("comment = " + xmlComment);

		Assert.assertTrue(xmlComment.startsWith("{"), "comment should start with {");
		Assert.assertTrue(xmlComment.endsWith("}"), "comment should end with }");
		Assert.assertTrue(xmlComment.contains(JsonExtension.COMMENT), "comment should contain url tag");
		Assert.assertTrue(xmlComment.contains(JsonExtension.REGISTRATION_NUMBER), "comment should contain timezone tag");

	}

	@Test(groups = { "buildComment" }, description = "check comment with no extension asked")
	public void verifyBuildNoExtension() throws Exception {
		GroupOfLine gline = new GroupOfLine();
		gline.setComment("dummy comment");
		gline.setRegistrationNumber("R_N");
		String xmlComment = producer.buildComment(gline, false);
		Reporter.log("comment = " + xmlComment);

		Assert.assertEquals(xmlComment, "dummy comment", "comment should be correctly built");

	}

}
