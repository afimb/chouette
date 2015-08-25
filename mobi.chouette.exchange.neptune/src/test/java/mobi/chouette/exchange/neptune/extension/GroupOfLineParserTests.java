package mobi.chouette.exchange.neptune.extension;

import mobi.chouette.exchange.neptune.JsonExtension;
import mobi.chouette.model.GroupOfLine;

import org.codehaus.jettison.json.JSONObject;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

public class GroupOfLineParserTests {

	private CommentExtension parser = new CommentExtension();

	@Test(groups = { "parseComment" }, description = "check empty comment and extensions")
	public void verifyBuildEmptyComment() throws Exception {
		GroupOfLine gline = new GroupOfLine();
		gline.setComment(null);

		parser.parseJsonComment(gline);
		Assert.assertNull(gline.getComment(), "comment should be null");
		Assert.assertNull(gline.getRegistrationNumber(), "registrationNumber should be null");
	}

	@Test(groups = { "parseComment" }, description = "check normal comment without extensions")
	public void verifyBuildNormalComment() throws Exception {
		String xmlComment = "dummy text";
		GroupOfLine gline = new GroupOfLine();
		gline.setComment(xmlComment);

		parser.parseJsonComment(gline);
		Assert.assertEquals(gline.getComment(), xmlComment, "comment should be filled");
		Assert.assertNull(gline.getRegistrationNumber(), "registrationNumber should be null");
	}

	@Test(groups = { "parseComment" }, description = "check null comment with registrationNUmber extension")
	public void verifyBuildRegistrationNumberComment() throws Exception {
		JSONObject jsonComment = new JSONObject();
		jsonComment.put(JsonExtension.REGISTRATION_NUMBER, "R_N");
		String xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		GroupOfLine gline = new GroupOfLine();
		gline.setComment(xmlComment);

		parser.parseJsonComment(gline);
		Assert.assertNull(gline.getComment(), "comment should be null");
		Assert.assertEquals(gline.getRegistrationNumber(), "R_N", "registrationNUmber should be set");
	}

	@Test(groups = { "parseComment" }, description = "check comment with all extension")
	public void verifyBuildCompleteComment() throws Exception {
		JSONObject jsonComment = new JSONObject();
		jsonComment.put(JsonExtension.REGISTRATION_NUMBER, "R_N");
		jsonComment.put(JsonExtension.COMMENT, "dummy text");

		String xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		GroupOfLine gline = new GroupOfLine();
		gline.setComment(xmlComment);

		parser.parseJsonComment(gline);
		Assert.assertEquals(gline.getComment(), "dummy text", "comment should be filled");
		Assert.assertEquals(gline.getRegistrationNumber(), "R_N", "registrationNUmber should be set");

	}

}
