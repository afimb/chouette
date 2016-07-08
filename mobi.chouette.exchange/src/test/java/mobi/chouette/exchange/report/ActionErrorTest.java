package mobi.chouette.exchange.report;

import java.io.PrintStream;

import mobi.chouette.common.Constant;
import mobi.chouette.exchange.report.ActionReporter.ERROR_CODE;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.codehaus.jettison.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ActionErrorTest implements Constant{
	@Test(groups = { "JsonGeneration" }, description = "Json generated" ,priority=105 )
	public void verifyJsonGeneration() throws Exception {
		
		
		ActionError actionError = new ActionError(ERROR_CODE.INTERNAL_ERROR, "action_error1");

		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(oStream);
		
		actionError.print(stream, new StringBuilder(), 1, true);
		String text = oStream.toString();
		JSONObject res = new JSONObject(text);
		
		
		// Test error code presence
		Assert.assertEquals(res.getString("code"), "INTERNAL_ERROR", "wrong action error code");
		
		// Test error description presence
		Assert.assertEquals(res.getString("description"), "action_error1", "wrong action error description");
	}
}
