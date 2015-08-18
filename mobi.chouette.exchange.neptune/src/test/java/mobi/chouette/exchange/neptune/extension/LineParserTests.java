package mobi.chouette.exchange.neptune.extension;

import mobi.chouette.exchange.neptune.JsonExtension;
import mobi.chouette.model.Line;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

public class LineParserTests {

	private CommentExtension parser = new CommentExtension();

	@Test(groups = { "parseComment" }, description = "check empty comment and extensions")
	public void verifyBuildEmptyComment() throws Exception {
		String xmlComment = null;
		Line line = new Line();

		line.setComment(xmlComment);
		parser.parseJsonComment(line);
		Assert.assertNull(line.getComment(), "comment should be null");
		Assert.assertNull(line.getColor(), "color should be null");
		Assert.assertNull(line.getTextColor(), "textColor should be null");
		Assert.assertEquals(line.getFootnotes().size(), 0, "footnotes should be empty");
		Assert.assertNull(line.getFlexibleService(), "flexibleService should be null");
	}

	@Test(groups = { "parseComment" }, description = "check normal comment without extensions")
	public void verifyBuildNormalComment() throws Exception {
		String xmlComment = "dummy text";
		Line line = new Line();

		line.setComment(xmlComment);
		parser.parseJsonComment(line);
		Assert.assertEquals(line.getComment(), xmlComment, "comment should be filled");
		Assert.assertNull(line.getColor(), "color should be null");
		Assert.assertNull(line.getTextColor(), "textColor should be null");
		Assert.assertEquals(line.getFootnotes().size(), 0, "footnotes should be empty");
		Assert.assertNull(line.getFlexibleService(), "flexibleService should be null");
	}

	@Test(groups = { "parseComment" }, description = "check null comment with colors extension")
	public void verifyBuildColorsComment() throws Exception {
		JSONObject jsonComment = new JSONObject();
		jsonComment.put(JsonExtension.TEXT_COLOR, "ff23b3");
		jsonComment.put(JsonExtension.LINE_COLOR, "00ff00");
		String xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		Line line = new Line();

		line.setComment(xmlComment);
		parser.parseJsonComment(line);
		Assert.assertNull(line.getComment(), "comment should be null");
		Assert.assertEquals(line.getColor(), "00ff00", "color should be set");
		Assert.assertEquals(line.getTextColor(), "ff23b3", "textColor should be set");
		Assert.assertEquals(line.getFootnotes().size(), 0, "footnotes should be empty");
		Assert.assertNull(line.getFlexibleService(), "flexibleService should be null");
	}

	@Test(groups = { "parseComment" }, description = "check null comment with flexible service extension")
	public void verifyBuildFlexibleServiceComment() throws Exception {
		JSONObject jsonComment = new JSONObject();
		jsonComment.put(JsonExtension.FLEXIBLE_SERVICE, Boolean.TRUE);
		String xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		Line line = new Line();

		line.setComment(xmlComment);
		parser.parseJsonComment(line);
		Assert.assertNull(line.getComment(), "comment should be null");
		Assert.assertNull(line.getColor(), "color should be null");
		Assert.assertNull(line.getTextColor(), "textColor should be null");
		Assert.assertEquals(line.getFootnotes().size(), 0, "footnotes should be empty");
		Assert.assertEquals(line.getFlexibleService(), Boolean.TRUE, "flexibleService should be true");

		jsonComment = new JSONObject();
		jsonComment.put(JsonExtension.FLEXIBLE_SERVICE, Boolean.FALSE);
		xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);

		line.setComment(xmlComment);
		parser.parseJsonComment(line);
		Assert.assertEquals(line.getFlexibleService(), Boolean.FALSE, "flexibleService should be false");

	}

	@Test(groups = { "parseComment" }, description = "check null comment and footnotes extensions")
	public void verifyBuildFootnotesComment() throws Exception {
		JSONObject jsonComment = new JSONObject();
		JSONArray jsonFootnotes = new JSONArray();
		jsonComment.put(JsonExtension.FOOTNOTES, jsonFootnotes);
		{
			JSONObject note = new JSONObject();
			note.put(JsonExtension.KEY, "1");
			note.put(JsonExtension.CODE, "a");
			note.put(JsonExtension.LABEL, "zozo");
			jsonFootnotes.put(note);
		}
		String xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		Line line = new Line();

		line.setComment(xmlComment);
		parser.parseJsonComment(line);
		Assert.assertNull(line.getComment(), "comment should be null");
		Assert.assertNull(line.getColor(), "color should be null");
		Assert.assertNull(line.getTextColor(), "textColor should be null");
		Assert.assertEquals(line.getFootnotes().size(), 1, "footnotes should be filled");
		Assert.assertEquals(line.getFootnotes().get(0).getKey(), "1", "note key should be filled");
		Assert.assertEquals(line.getFootnotes().get(0).getCode(), "a", "note code should be filled");
		Assert.assertEquals(line.getFootnotes().get(0).getLabel(), "zozo", "note label should be filled");
		Assert.assertNull(line.getFlexibleService(), "flexibleService should be null");

		{
			JSONObject note = new JSONObject();
			note.put(JsonExtension.KEY, "2");
			note.put(JsonExtension.CODE, "b");
			note.put(JsonExtension.LABEL, "titi");
			jsonFootnotes.put(note);
		}
		xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		line = new Line();
		line.setComment(xmlComment);

		parser.parseJsonComment(line);
		Assert.assertEquals(line.getFootnotes().size(), 2, "footnotes should be filled");
		Assert.assertEquals(line.getFootnotes().get(0).getKey(), "1", "note key should be filled");
		Assert.assertEquals(line.getFootnotes().get(0).getCode(), "a", "note code should be filled");
		Assert.assertEquals(line.getFootnotes().get(0).getLabel(), "zozo", "note label should be filled");
		Assert.assertEquals(line.getFootnotes().get(1).getKey(), "2", "note key should be filled");
		Assert.assertEquals(line.getFootnotes().get(1).getCode(), "b", "note code should be filled");
		Assert.assertEquals(line.getFootnotes().get(1).getLabel(), "titi", "note label should be filled");
	}

	@Test(groups = { "parseComment" }, description = "check comment with all extension")
	public void verifyBuildCompleteComment() throws Exception {
		JSONObject jsonComment = new JSONObject();
		jsonComment.put(JsonExtension.FLEXIBLE_SERVICE, Boolean.TRUE);
		JSONArray jsonFootnotes = new JSONArray();
		jsonComment.put(JsonExtension.FOOTNOTES, jsonFootnotes);
		{
			JSONObject note = new JSONObject();
			note.put(JsonExtension.KEY, "1");
			note.put(JsonExtension.CODE, "a");
			note.put(JsonExtension.LABEL, "zozo");
			jsonFootnotes.put(note);
		}
		jsonComment.put(JsonExtension.TEXT_COLOR, "ff23b3");
		jsonComment.put(JsonExtension.LINE_COLOR, "00ff00");
		jsonComment.put(JsonExtension.COMMENT, "dummy text");

		String xmlComment = jsonComment.toString();
		Reporter.log("comment = " + xmlComment);
		Line line = new Line();
		line.setComment(xmlComment);

		parser.parseJsonComment(line);
		Assert.assertEquals(line.getComment(), "dummy text", "comment should be filled");
		Assert.assertEquals(line.getColor(), "00ff00", "color should be null");
		Assert.assertEquals(line.getTextColor(), "ff23b3", "textColor should be null");
		Assert.assertEquals(line.getFootnotes().size(), 1, "footnotes should be filled");
		Assert.assertEquals(line.getFootnotes().get(0).getKey(), "1", "note key should be filled");
		Assert.assertEquals(line.getFootnotes().get(0).getCode(), "a", "note code should be filled");
		Assert.assertEquals(line.getFootnotes().get(0).getLabel(), "zozo", "note label should be filled");
		Assert.assertEquals(line.getFlexibleService(), Boolean.TRUE, "flexibleService should be true");

	}

}
