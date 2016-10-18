package mobi.chouette.exchange.report;

import java.io.PrintStream;

import mobi.chouette.exchange.report.ActionReporter.ERROR_CODE;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.codehaus.jettison.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ObjectErrorTest {
	@Test(groups = { "JsonGeneration" }, description = "Json generated" ,priority=105 )
	public void verifyJsonGeneration() throws Exception {
		ObjectError objectError = new ObjectError(ERROR_CODE.INTERNAL_ERROR, "object_error1");
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(oStream);
		
		objectError.print(stream, new StringBuilder(), 1, true);
		String text = oStream.toString();
		JSONObject res = new JSONObject(text);
		
		// Test error code presence
		Assert.assertEquals(res.getString("code"), "INTERNAL_ERROR", "wrong object error code");
		// Test error description presence
		Assert.assertEquals(res.getString("description"), "object_error1", "wrong object error description");
	}
}
