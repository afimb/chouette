package mobi.chouette.exchange.neptune.extension;

import mobi.chouette.exchange.neptune.JsonExtension;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;

import org.codehaus.jettison.json.JSONObject;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

public class StopPointParserTests {

	private CommentExtension parser = new CommentExtension();

	@Test(groups = { "parseComment" }, description = "check empty comment and extensions")
	public void verifyBuildEmptyComment() throws Exception {
		String xmlComment = null;
		StopPoint point = new StopPoint();

		point.setComment(xmlComment);
		parser.parseJsonComment(point);
		Assert.assertNull(point.getForAlighting(), "forAlighting should be null");
		Assert.assertNull(point.getForBoarding(), "forBoarding should be null");
	}

	@Test(groups = { "parseComment" }, description = "check normal comment without extensions")
	public void verifyBuildNormalComment() throws Exception {
		String xmlComment = "dummy text";
		StopPoint point = new StopPoint();

		point.setComment(xmlComment);
		parser.parseJsonComment(point);
		Assert.assertNull(point.getForAlighting(), "forAlighting should be null");
		Assert.assertNull(point.getForBoarding(), "forBoarding should be null");
	}

	@Test(groups = { "parseComment" }, description = "check aligthing extension")
	public void verifyBuildAlightingComment() throws Exception {
		JSONObject jsonComment = new JSONObject();
		JSONObject jsonRC = new JSONObject();
		jsonRC.put(JsonExtension.ALIGHTING, "normal");
		jsonComment.put(JsonExtension.ROUTING_CONSTRAINTS, jsonRC);
		String xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		StopPoint point = new StopPoint();

		point.setComment(xmlComment);
		parser.parseJsonComment(point);
		Assert.assertEquals(point.getForAlighting(), AlightingPossibilityEnum.normal, "forAlighting should be filled");
		Assert.assertNull(point.getForBoarding(), "forBoarding should be null");

		jsonComment = new JSONObject();
		jsonRC = new JSONObject();
		jsonRC.put(JsonExtension.ALIGHTING, "forbidden");
		jsonComment.put(JsonExtension.ROUTING_CONSTRAINTS, jsonRC);
		xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		point = new StopPoint();

		point.setComment(xmlComment);
		parser.parseJsonComment(point);
		Assert.assertEquals(point.getForAlighting(), AlightingPossibilityEnum.forbidden,
				"forAlighting should be filled");
		Assert.assertNull(point.getForBoarding(), "forBoarding should be null");

		jsonComment = new JSONObject();
		jsonRC = new JSONObject();
		jsonRC.put(JsonExtension.ALIGHTING, "request_stop");
		jsonComment.put(JsonExtension.ROUTING_CONSTRAINTS, jsonRC);
		xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		point = new StopPoint();

		point.setComment(xmlComment);
		parser.parseJsonComment(point);
		Assert.assertEquals(point.getForAlighting(), AlightingPossibilityEnum.request_stop,
				"forAlighting should be filled");
		Assert.assertNull(point.getForBoarding(), "forBoarding should be null");

		jsonComment = new JSONObject();
		jsonRC = new JSONObject();
		jsonRC.put(JsonExtension.ALIGHTING, "is_flexible");
		jsonComment.put(JsonExtension.ROUTING_CONSTRAINTS, jsonRC);
		xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		point = new StopPoint();

		point.setComment(xmlComment);
		parser.parseJsonComment(point);
		Assert.assertEquals(point.getForAlighting(), AlightingPossibilityEnum.is_flexible,
				"forAlighting should be filled");
		Assert.assertNull(point.getForBoarding(), "forBoarding should be null");

		jsonComment = new JSONObject();
		jsonRC = new JSONObject();
		jsonRC.put(JsonExtension.ALIGHTING, "dummy");
		jsonComment.put(JsonExtension.ROUTING_CONSTRAINTS, jsonRC);
		xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		point = new StopPoint();

		point.setComment(xmlComment);
		parser.parseJsonComment(point);
		Assert.assertNull(point.getForAlighting(), "forAlighting should be null");
		Assert.assertNull(point.getForBoarding(), "forBoarding should be null");

	}

	@Test(groups = { "parseComment" }, description = "check boarding extension")
	public void verifyBuildBoardingComment() throws Exception {
		JSONObject jsonComment = new JSONObject();
		JSONObject jsonRC = new JSONObject();
		jsonRC.put(JsonExtension.BOARDING, "normal");
		jsonComment.put(JsonExtension.ROUTING_CONSTRAINTS, jsonRC);
		String xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		StopPoint point = new StopPoint();

		point.setComment(xmlComment);
		parser.parseJsonComment(point);
		Assert.assertNull(point.getForAlighting(), "forAlighting should be null");
		Assert.assertEquals(point.getForBoarding(), BoardingPossibilityEnum.normal, "forBoarding should be filled");

		jsonComment = new JSONObject();
		jsonRC = new JSONObject();
		jsonRC.put(JsonExtension.BOARDING, "forbidden");
		jsonComment.put(JsonExtension.ROUTING_CONSTRAINTS, jsonRC);
		xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		point = new StopPoint();

		point.setComment(xmlComment);
		parser.parseJsonComment(point);
		Assert.assertNull(point.getForAlighting(), "forAlighting should be null");
		Assert.assertEquals(point.getForBoarding(), BoardingPossibilityEnum.forbidden, "forBoarding should be filled");

		jsonComment = new JSONObject();
		jsonRC = new JSONObject();
		jsonRC.put(JsonExtension.BOARDING, "request_stop");
		jsonComment.put(JsonExtension.ROUTING_CONSTRAINTS, jsonRC);
		xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		point = new StopPoint();

		point.setComment(xmlComment);
		parser.parseJsonComment(point);
		Assert.assertNull(point.getForAlighting(), "forAlighting should be null");
		Assert.assertEquals(point.getForBoarding(), BoardingPossibilityEnum.request_stop,
				"forBoarding should be filled");

		jsonComment = new JSONObject();
		jsonRC = new JSONObject();
		jsonRC.put(JsonExtension.BOARDING, "is_flexible");
		jsonComment.put(JsonExtension.ROUTING_CONSTRAINTS, jsonRC);
		xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		point = new StopPoint();

		point.setComment(xmlComment);
		parser.parseJsonComment(point);
		Assert.assertNull(point.getForAlighting(), "forAlighting should be null");
		Assert.assertEquals(point.getForBoarding(), BoardingPossibilityEnum.is_flexible, "forBoarding should be filled");

		jsonComment = new JSONObject();
		jsonRC = new JSONObject();
		jsonRC.put(JsonExtension.BOARDING, "dummy");
		jsonComment.put(JsonExtension.ROUTING_CONSTRAINTS, jsonRC);
		xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		point = new StopPoint();

		point.setComment(xmlComment);
		parser.parseJsonComment(point);
		Assert.assertNull(point.getForAlighting(), "forAlighting should be null");
		Assert.assertNull(point.getForBoarding(), "forBoarding should be null");

	}

	@Test(groups = { "parseComment" }, description = "check complete extension")
	public void verifyBuildCompleteComment() throws Exception {
		JSONObject jsonComment = new JSONObject();
		JSONObject jsonRC = new JSONObject();
		jsonRC.put(JsonExtension.ALIGHTING, "forbidden");
		jsonRC.put(JsonExtension.BOARDING, "is_flexible");
		jsonComment.put(JsonExtension.ROUTING_CONSTRAINTS, jsonRC);
		String xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		StopPoint point = new StopPoint();

		point.setComment(xmlComment);
		parser.parseJsonComment(point);
		Assert.assertEquals(point.getForAlighting(), AlightingPossibilityEnum.forbidden,
				"forAlighting should be filled");
		Assert.assertEquals(point.getForBoarding(), BoardingPossibilityEnum.is_flexible, "forBoarding should be filled");

	}
}
