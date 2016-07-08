package mobi.chouette.exchange.validation.report;

import java.io.PrintStream;

import mobi.chouette.common.Constant;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CheckPointErrorReportTest implements Constant{
	@Test(groups = { "JsonGeneration" }, description = "Json generated", priority = 104)
	public void verifyJsonGeneration() throws Exception {
		
		// Error source and value not null
		{
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(oStream);
			Location location = new Location("filename", 3, 1, "1234");
			CheckPointErrorReport error = new CheckPointErrorReport("Neptune-Checkpoint-1", "neptune_checkpoint_1", location, "test");
			error.print(stream, new StringBuilder(), 1, true);
			String text = oStream.toString();
			JSONObject res = new JSONObject(text);
			
			
			Assert.assertEquals(res.getString("test_id"), "Neptune-Checkpoint-1", "wrong error test_id");
			Assert.assertEquals(res.getString("error_id"), "neptune_checkpoint_1", "wrong error error_id");
			Assert.assertNotNull(res.getJSONObject("source"), "source must not be null");
			Assert.assertNotNull(res.getString("error_value"), "value must not be null");
		}
		
		// Error targets and ref value not null
		{
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(oStream);
			Location locationTarget = new Location("filename2", 2, 6, "12345");
			CheckPointErrorReport error = new CheckPointErrorReport("Neptune-Checkpoint-1", "neptune_checkpoint_1", null, "", "ref_test", locationTarget);
			error.print(stream, new StringBuilder(), 1, true);
			String text = oStream.toString();
			JSONObject res = new JSONObject(text);
			
			
			Assert.assertEquals(res.getString("test_id"), "Neptune-Checkpoint-1", "wrong error test_id");
			Assert.assertEquals(res.getString("error_id"), "neptune_checkpoint_1", "wrong error error_id");
			
			JSONArray targets = res.getJSONArray("target");
			Assert.assertNotNull(targets, "targets must not be null");
			Assert.assertNotNull(res.getString("reference_value"), "reference value must not be null");
		}
	}
}
