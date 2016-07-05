package mobi.chouette.exchange.validation.report;

import java.io.PrintStream;

import mobi.chouette.exchange.validation.report.CheckPointReport.SEVERITY;
import mobi.chouette.exchange.validation.report.ValidationReporter.RESULT;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CheckPointReportTest {
	@Test(groups = { "JsonGeneration" }, description = "Json generated", priority = 104)
	public void verifyJsonGeneration() throws Exception {
		
		// checkpoint with no errors
		{
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(oStream);
			CheckPointReport checkPoint = new CheckPointReport("Neptune-Checkpoint-1", RESULT.OK, SEVERITY.WARNING);
			checkPoint.print(stream, 1, true);
			String text = oStream.toString();
			JSONObject res = new JSONObject(text);
			
			
			Assert.assertEquals(res.getString("test_id"), "Neptune-Checkpoint-1", "wrong checkpoint test_id");
			Assert.assertEquals(res.getString("level"), "Neptune", "wrong checkpoint phase");
			Assert.assertEquals(res.getString("type"), "Checkpoint", "wrong checkpoint type");
			Assert.assertEquals(res.getString("rank"), "1", "wrong checkpoint rank");
			Assert.assertEquals(res.getString("result"), "OK", "checkpoint result must be OK");
			Assert.assertEquals(res.getString("severity"), "WARNING", "checkpoint severity must be warning");
			Assert.assertEquals(res.getInt("check_point_error_count"), 0, "checkpoint should have no errors");
		}
		
		// checkpoint with one error
		{
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(oStream);
			CheckPointReport checkPoint = new CheckPointReport("Neptune-Checkpoint-1", RESULT.OK, SEVERITY.WARNING);
			checkPoint.addCheckPointError(0);
			checkPoint.print(stream, 1, true);
			String text = oStream.toString();
			JSONObject res = new JSONObject(text);
			
			
			Assert.assertEquals(res.getString("test_id"), "Neptune-Checkpoint-1", "wrong checkpoint test_id");
			Assert.assertEquals(res.getString("level"), "Neptune", "wrong checkpoint phase");
			Assert.assertEquals(res.getString("type"), "Checkpoint", "wrong checkpoint type");
			Assert.assertEquals(res.getString("rank"), "1", "wrong checkpoint rank");
			Assert.assertEquals(res.getString("result"), "NOK", "checkpoint result must be NOK");
			Assert.assertEquals(res.getString("severity"), "WARNING", "checkpoint severity must be warning");
			Assert.assertEquals(res.getInt("check_point_error_count"), 1, "checkpoint should have one error");
			
			JSONArray errors = res.getJSONArray("errors");
			Assert.assertEquals(errors.get(0), 0, "first error key must be 0");
		}
	}
}
