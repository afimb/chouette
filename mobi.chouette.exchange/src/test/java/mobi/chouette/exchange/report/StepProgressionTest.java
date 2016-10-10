package mobi.chouette.exchange.report;

import java.io.PrintStream;

import mobi.chouette.exchange.report.StepProgression.STEP;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.codehaus.jettison.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

public class StepProgressionTest {
	@Test(groups = { "JsonGeneration" }, description = "Json generated" ,priority=105 )
	public void verifyJsonGeneration() throws Exception {
		StepProgression stepProgression = new StepProgression(STEP.INITIALISATION, 1, 0);
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(oStream);
		
		stepProgression.print(stream, new StringBuilder(), 1, true);
		String text = oStream.toString();
		JSONObject res = new JSONObject(text);
		
		// Test step type
		Assert.assertEquals(res.getString("step"), "INITIALISATION", "step must be INITIALISATION");
		// Test total value
		Assert.assertEquals(res.getInt("total"), 1, "total step number must be 1");
		// Test realized value
		Assert.assertEquals(res.getInt("realized"), 0, "no step has been realized yet");
	}
}
