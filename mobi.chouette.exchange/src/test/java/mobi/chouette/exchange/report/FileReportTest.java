package mobi.chouette.exchange.report;

import java.io.PrintStream;

import mobi.chouette.exchange.report.ActionReporter.FILE_ERROR_CODE;
import mobi.chouette.exchange.report.ActionReporter.FILE_STATE;
import mobi.chouette.exchange.validation.report.CheckPointReport.SEVERITY;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FileReportTest {
	@Test(groups = { "JsonGeneration" }, description = "Json generated" ,priority=105 )
	public void verifyJsonGeneration() throws Exception {
		
		// File report with no errors
		{
			FileReport fileReport = new FileReport("file_report1",FILE_STATE.OK, IO_TYPE.INPUT);
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(oStream);
			
			fileReport.print(stream, 1, true);
			String text = oStream.toString();
			JSONObject res = new JSONObject(text);
			
			// Test file name presence
			Assert.assertEquals(res.getString("name"), "file_report1", "wrong file report name");
			// Test file name presence
			Assert.assertEquals(res.getString("status"), "OK", "wrong file report status");
			// Test file iotype presence
			Assert.assertEquals(res.getString("io_type"), "INPUT", "wrong file report io type");
					
			// Test checkpoint error number
			Assert.assertEquals(res.getInt("check_point_error_count"), 0, "file report must not have errors");
			
			// Test checkpoint warning number
			Assert.assertEquals(res.getInt("check_point_warning_count"), 0, "file report must not have warnings");
		}
		
		// File report with errors
		{
			FileReport fileReport = new FileReport("file_report1",FILE_STATE.OK, IO_TYPE.INPUT);
			fileReport.addError(new FileError(FILE_ERROR_CODE.FILE_NOT_FOUND, "file_error_1"));
			fileReport.addCheckPointError(0, SEVERITY.ERROR);
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(oStream);
			
			fileReport.print(stream, 1, true);
			String text = oStream.toString();
			JSONObject res = new JSONObject(text);
			
			// Test file name presence
			Assert.assertEquals(res.getString("name"), "file_report1", "wrong file report name");
			// Test file name presence
			Assert.assertEquals(res.getString("status"), "ERROR", "wrong file report status");
			// Test file iotype presence
			Assert.assertEquals(res.getString("io_type"), "INPUT", "wrong file report io type");
			
			JSONArray errors = res.getJSONArray("errors");
			JSONArray errorKeys = res.getJSONArray("check_point_errors");
			
			// Test checkpoint error number
			Assert.assertEquals(((JSONObject)errors.get(0)).getString("description"), "file_error_1", "file report must have one error");
			// Test checkpoint error keys presence
			Assert.assertEquals(errorKeys.get(0), 0, "file report must one checkpoint error key");
						
			// Test checkpoint error number
			Assert.assertEquals(res.getInt("check_point_error_count"), 1, "file report must not have errors");
			// Test checkpoint warning number
			Assert.assertEquals(res.getInt("check_point_warning_count"), 0, "file report must not have warnings");
		}
	}
}
