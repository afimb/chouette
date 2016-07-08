package mobi.chouette.exchange.report;

import java.io.PrintStream;
import mobi.chouette.exchange.report.ActionReporter.FILE_ERROR_CODE;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.codehaus.jettison.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FileErrorTest {
	@Test(groups = { "JsonGeneration" }, description = "Json generated" ,priority=105 )
	public void verifyJsonGeneration() throws Exception {
		FileError fileError = new FileError(FILE_ERROR_CODE.FILE_NOT_FOUND, "file_error1");
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(oStream);
		
		fileError.print(stream, new StringBuilder(), 1, true);
		String text = oStream.toString();
		JSONObject res = new JSONObject(text);
		
		// Test error code presence
		Assert.assertEquals(res.getString("code"), "FILE_NOT_FOUND", "wrong file error code");
		// Test error description presence
		Assert.assertEquals(res.getString("description"), "file_error1", "wrong file error description");
	}
}
