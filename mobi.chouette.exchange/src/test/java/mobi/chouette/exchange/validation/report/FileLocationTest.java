package mobi.chouette.exchange.validation.report;

import java.io.PrintStream;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.codehaus.jettison.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FileLocationTest {
	@Test(groups = { "JsonGeneration" }, description = "Json generated", priority = 104)
	public void verifyJsonGeneration() throws Exception {
		
		// lineNumber and columnNumber null
		{
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(oStream);
			FileLocation fileLocation = new FileLocation("fileName", 0, 0);
			fileLocation.print(stream, 1, true);
			String text = oStream.toString();
			JSONObject res = new JSONObject(text);
			
			Assert.assertEquals(res.getString("filename"), "fileName", "wrong file location name");
			Assert.assertEquals(res.getInt("line_number"), 0, "wrong file location line number");
			Assert.assertEquals(res.getInt("column_number"), 0, "wrong file location column number");
		}
		
		// lineNumber not null and columnNumber null
		{
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(oStream);
			FileLocation fileLocation = new FileLocation("fileName", 15, 0);
			fileLocation.print(stream, 1, true);
			String text = oStream.toString();
			JSONObject res = new JSONObject(text);
			
			Assert.assertEquals(res.getString("filename"), "fileName", "wrong file location name");
			Assert.assertEquals(res.getInt("line_number"), 15, "wrong file location line number");
			Assert.assertEquals(res.getInt("column_number"), 0, "wrong file location column number");
		}
		
		// lineNumber null and columnNumber not null
		{
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(oStream);
			FileLocation fileLocation = new FileLocation("fileName", 0, 10);
			fileLocation.print(stream, 1, true);
			String text = oStream.toString();
			JSONObject res = new JSONObject(text);
			
			Assert.assertEquals(res.getString("filename"), "fileName", "wrong file location name");
			Assert.assertEquals(res.getInt("line_number"), 0, "wrong file location line number");
			Assert.assertEquals(res.getInt("column_number"), 10, "wrong file location column number");
		}
	}
}
