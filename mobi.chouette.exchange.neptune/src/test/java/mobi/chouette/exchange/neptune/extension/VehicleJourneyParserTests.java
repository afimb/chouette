package mobi.chouette.exchange.neptune.extension;

import mobi.chouette.exchange.neptune.JsonExtension;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.VehicleJourney;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

public class VehicleJourneyParserTests {

	private CommentExtension parser = new CommentExtension();

	private Line line = null;

	private Route route;

	private Footnote buildFootnote(String key, Line line) {
		Footnote note = new Footnote();
		note.setKey(key);
		note.setCode("code" + key);
		note.setLabel("label" + key);
		note.setLine(line);
		return note;
	}

	private void addLine(VehicleJourney vj) {
		if (line == null) {
			line = new Line();

			line.getFootnotes().add(buildFootnote("1", line));
			line.getFootnotes().add(buildFootnote("2", line));
			line.getFootnotes().add(buildFootnote("3", line));
			line.getFootnotes().add(buildFootnote("4", line));

			route = new Route();
			route.setLine(line);
		}
		if (vj.getRoute() == null)
			vj.setRoute(route);
	}

	@Test(groups = { "parseComment" }, description = "check empty comment and extensions")
	public void verifyBuildEmptyComment() throws Exception {
		String xmlComment = null;
		VehicleJourney vj = new VehicleJourney();

		vj.setComment(xmlComment);
		addLine(vj);
		parser.parseJsonComment( vj);
		Assert.assertNull(vj.getComment(), "comment should be null");
		Assert.assertNull(vj.getFlexibleService(), "flexibleService should be null");
		Assert.assertNull(vj.getMobilityRestrictedSuitability(), "mobility should be null");
		Assert.assertEquals(vj.getFootnotes().size(), 0, "footnotes should be empty");

	}

	@Test(groups = { "parseComment" }, description = "check normal comment without extensions")
	public void verifyBuildNormalComment() throws Exception {
		String xmlComment = "dummy text";
		VehicleJourney vj = new VehicleJourney();

		vj.setComment(xmlComment);
		addLine(vj);
		parser.parseJsonComment( vj);
		Assert.assertEquals(vj.getComment(), "dummy text", "comment should be filled");
		Assert.assertNull(vj.getFlexibleService(), "flexibleService should be null");
		Assert.assertNull(vj.getMobilityRestrictedSuitability(), "mobility should be null");
		Assert.assertEquals(vj.getFootnotes().size(), 0, "footnotes should be empty");
	}

	@Test(groups = { "parseComment" }, description = "check null comment with flexible service extension")
	public void verifyBuildFlexibleServiceComment() throws Exception {
		JSONObject jsonComment = new JSONObject();
		jsonComment.put(JsonExtension.FLEXIBLE_SERVICE, Boolean.TRUE);
		String xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		VehicleJourney vj = new VehicleJourney();
		vj.setComment(xmlComment);
		addLine(vj);
		parser.parseJsonComment( vj);
		Assert.assertNull(vj.getComment(), "comment should be null");
		Assert.assertNull(vj.getMobilityRestrictedSuitability(), "mobility should be null");
		Assert.assertEquals(vj.getFootnotes().size(), 0, "footnotes should be empty");
		Assert.assertEquals(vj.getFlexibleService(), Boolean.TRUE, "flexibleService should be true");

		jsonComment = new JSONObject();
		jsonComment.put(JsonExtension.FLEXIBLE_SERVICE, Boolean.FALSE);
		xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		vj = new VehicleJourney();

		vj.setComment(xmlComment);
		addLine(vj);
		parser.parseJsonComment( vj);
		Assert.assertEquals(vj.getFlexibleService(), Boolean.FALSE, "flexibleService should be false");

	}

	@Test(groups = { "parseComment" }, description = "check null comment with mobility restricted suitability  extension")
	public void verifyBuildMobilityComment() throws Exception {
		JSONObject jsonComment = new JSONObject();
		jsonComment.put(JsonExtension.MOBILITY_RESTRICTION, Boolean.TRUE);
		String xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		VehicleJourney vj = new VehicleJourney();

		vj.setComment(xmlComment);
		addLine(vj);
		parser.parseJsonComment( vj);
		Assert.assertNull(vj.getComment(), "comment should be null");
		Assert.assertNull(vj.getFlexibleService(), "flexibleService should be null");
		Assert.assertEquals(vj.getFootnotes().size(), 0, "footnotes should be empty");
		Assert.assertEquals(vj.getMobilityRestrictedSuitability(), Boolean.TRUE, "mobility should be true");

		jsonComment = new JSONObject();
		jsonComment.put(JsonExtension.MOBILITY_RESTRICTION, Boolean.FALSE);
		xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		vj = new VehicleJourney();

		vj.setComment(xmlComment);
		addLine(vj);
		parser.parseJsonComment( vj);
		Assert.assertEquals(vj.getMobilityRestrictedSuitability(), Boolean.FALSE, "mobility should be false");

	}

	@Test(groups = { "parseComment" }, description = "check null comment and footnotes extensions")
	public void verifyBuildFootnotesComment() throws Exception {
		JSONObject jsonComment = new JSONObject();
		JSONArray jsonFootnotes = new JSONArray();
		jsonComment.put(JsonExtension.FOOTNOTE_REFS, jsonFootnotes);

		jsonFootnotes.put("1");

		String xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		VehicleJourney vj = new VehicleJourney();

		vj.setComment(xmlComment);
		addLine(vj);
		parser.parseJsonComment( vj);
		Assert.assertNull(vj.getComment(), "comment should be null");
		Assert.assertNull(vj.getFlexibleService(), "flexibleService should be null");
		Assert.assertNull(vj.getMobilityRestrictedSuitability(), "mobility should be null");
		Assert.assertEquals(vj.getFootnotes().size(), 1, "footnotes should be filled");
		Assert.assertEquals(vj.getFootnotes().get(0).getKey(), "1", "note key should be filled");

		jsonFootnotes.put("3");

		xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		vj = new VehicleJourney();

		vj.setComment(xmlComment);
		addLine(vj);
		parser.parseJsonComment( vj);
		Assert.assertEquals(vj.getFootnotes().size(), 2, "footnotes should be filled");
		Assert.assertEquals(vj.getFootnotes().get(0).getKey(), "1", "note key should be filled");
		Assert.assertEquals(vj.getFootnotes().get(1).getKey(), "3", "note key should be filled");
	}

	@Test(groups = { "parseComment" }, description = "check  comment with all extension")
	public void verifyBuildCompleteComment() throws Exception {
		JSONObject jsonComment = new JSONObject();
		jsonComment.put(JsonExtension.FLEXIBLE_SERVICE, Boolean.TRUE);
		JSONArray jsonFootnotes = new JSONArray();
		jsonComment.put(JsonExtension.FOOTNOTE_REFS, jsonFootnotes);
		jsonFootnotes.put("1");
		jsonFootnotes.put("3");
		jsonComment.put(JsonExtension.MOBILITY_RESTRICTION, Boolean.TRUE);
		jsonComment.put(JsonExtension.COMMENT, "dummy text");

		String xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		VehicleJourney vj = new VehicleJourney();

		vj.setComment(xmlComment);
		addLine(vj);
		parser.parseJsonComment( vj);
		Assert.assertEquals(vj.getComment(), "dummy text", "comment should be filled");
		Assert.assertEquals(vj.getFlexibleService(), Boolean.TRUE, "flexibleService should be true");
		Assert.assertEquals(vj.getMobilityRestrictedSuitability(), Boolean.TRUE, "mobility should be true");
		Assert.assertEquals(vj.getFootnotes().size(), 2, "footnotes should be filled");
		Assert.assertEquals(vj.getFootnotes().get(0).getKey(), "1", "note key should be filled");
		Assert.assertEquals(vj.getFootnotes().get(1).getKey(), "3", "note key should be filled");

	}

}
