package mobi.chouette.exchange.report;

import java.io.PrintStream;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProgressionTest {
	@Test(groups = { "JsonGeneration" }, description = "Json generated" ,priority=105 )
	public void verifyJsonGeneration() throws Exception {
		Progression progression = new Progression();
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(oStream);
		
		progression.print(stream, 1, true);
		String text = oStream.toString();
		JSONObject res = new JSONObject(text);
		
		// Test current_step presence
		Assert.assertEquals(res.getInt("current_step"), 0, "current step must be 0");
		// Test steps_count presence
		Assert.assertEquals(res.getInt("steps_count"), 3, "progression must have 3 steps");
		
		// Test steps existence
		JSONArray steps = res.getJSONArray("steps");
		
		// Test step list length
		Assert.assertEquals(steps.length(), 3, "progression step list must have 3 steps");
	}
}
