package mobi.chouette.exchange.report;

import java.io.PrintStream;

import mobi.chouette.exchange.report.ActionReporter.ERROR_CODE;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_STATE;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_TYPE;
import mobi.chouette.exchange.validation.report.CheckPointReport.SEVERITY;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ObjectReportTest {
	@Test(groups = { "JsonGeneration" }, description = "Json generated" ,priority=105 )
	public void verifyJsonGeneration() throws Exception {
		ObjectReport objectReport = new ObjectReport("line1", OBJECT_TYPE.LINE, "line_1", OBJECT_STATE.OK,IO_TYPE.INPUT);
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(oStream);
		objectReport.addError(new ObjectError(ERROR_CODE.INTERNAL_ERROR, "object_error1"));
		objectReport.addCheckPointError(0, SEVERITY.ERROR);
		objectReport.addStatTypeToObject(OBJECT_TYPE.LINE, 1);
		objectReport.print(stream, new StringBuilder(), 1, true);
		String text = oStream.toString();
		JSONObject res;
		try {
			res = new JSONObject(text);
			// Test type presence
			Assert.assertEquals(res.getString("type"), "line", "wrong object report type");
			// Test description presence
			Assert.assertEquals(res.getString("description"), "line_1", "wrong object report description");
			// Test objectid presence
			Assert.assertEquals(res.getString("objectid"), "line1", "wrong object report objectid");
			// Test status presence
			Assert.assertEquals(res.getString("status"), "ERROR", "wrong object report status");
		
			// Test checkpoint error number
			Assert.assertEquals(res.getInt("check_point_error_count"), 1, "file report must not have errors");
			// Test checkpoint warning number
			Assert.assertEquals(res.getInt("check_point_warning_count"), 0, "file report must not have warnings");
			
			JSONObject stats = res.getJSONObject("stats");
			// Test stats presence
			Assert.assertEquals(stats.length(), 1, "object collection report must have one object stat");
			
			JSONArray errors = res.getJSONArray("errors");
			// Test errors presence
			Assert.assertEquals(((JSONObject)errors.get(0)).getString("description"), "object_error1", "wrong object report error name");
			
			JSONArray checkPointErrorKeys = res.getJSONArray("check_point_errors");
			// Test checkpoint error key presence
			Assert.assertEquals((checkPointErrorKeys.get(0)), 0, "checkpoint error key must be 0");
		} catch (JSONException e) {
			e.printStackTrace();
		}	
	}
}
