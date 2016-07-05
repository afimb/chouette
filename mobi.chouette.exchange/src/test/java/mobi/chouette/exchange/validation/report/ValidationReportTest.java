package mobi.chouette.exchange.validation.report;

import java.io.PrintStream;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.report.ActionReport;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ValidationReportTest implements Constant{
	@Test(groups = { "JsonGeneration" }, description = "Json generated", priority = 104)
	public void verifyJsonGeneration() throws Exception {
		Context context = new Context();
		context.put(VALIDATION_REPORT, new ValidationReport());
		context.put(REPORT, new ActionReport());

		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		
		// location.setColumnNumber(1);
		// location.setFilename("filename");
		// location.setObjectId("1234");
		// location.setLineNumber(3);
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);

//		String result = "{\"validation_report\":{\"result\":\"VALIDATION_PROCEDEED\",\"check_points\":" +
//				"[{\"test_id\":\"Neptune-Checkpoint-1\",\"level\":\"Neptune\",\"type\":\"Checkpoint\"," +
//				"\"rank\":\"1\",\"severity\":\"WARNING\",\"result\":\"NOK\",\"check_point_error_count\":1,\"check_point_errors\":[0]}]," +
//				"\"errors\":[{\"test_id\":\"Neptune-Checkpoint-1\",\"error_id\":\"neptune_checkpoint_1\"," +
//				"\"source\":{\"file\":{\"filename\":\"filename\",\"line_number\":3,\"column_number\":1}," +
//				"\"objectid\":\"1234\",\"label\":\"\"},\"error_value\":\"test\"}]}}";
//		JSONObject array = new JSONObject(result);
		
		{
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(oStream);
			validationReport.print(stream);
			String text = oStream.toString();
			JSONObject res = new JSONObject(text);
			Assert.assertEquals(res.length(), 1 , "Report must contains 1 entry");
			Assert.assertTrue(res.has("validation_report"), "Report must contains entry validation_report");
			JSONObject vrJson = res.getJSONObject("validation_report");
			Assert.assertNotNull(vrJson, "validation_report json object must not be null");
			Assert.assertEquals(vrJson.getString("result"), "NO_VALIDATION", "validation_report is not empty");
		
		}
		validationReporter.addItemToValidationReport(context, "Neptune-", "Checkpoint", 1, "W");
		DataLocation location = new DataLocation("filename", 3, 1, "1234");
		validationReporter.addCheckPointReportError(context, "Neptune-Checkpoint-1", location, "test");
		{
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(oStream);
		validationReport.print(stream);
		String text = oStream.toString();
		JSONObject res = new JSONObject(text);
		Assert.assertEquals(res.length(), 1 , "Report must contains 1 entry");
		Assert.assertTrue(res.has("validation_report"), "Report must contains entry validation_report");
		JSONObject vrJson = res.getJSONObject("validation_report");
		Assert.assertNotNull(vrJson, "validation_report json object must not be null");
		Assert.assertEquals(vrJson.getString("result"), "VALIDATION_PROCEDEED", "validation_report is empty");
		
		JSONArray checkPointsJson = vrJson.getJSONArray("check_points");
		Assert.assertNotNull(checkPointsJson, "checkpoint list must not be null");
		
		Assert.assertEquals(((JSONObject)checkPointsJson.get(0)).getString("test_id"), "Neptune-Checkpoint-1", "wrong checkpoint test_id");
		Assert.assertEquals(((JSONObject)checkPointsJson.get(0)).getInt("check_point_error_count"), 1, "checkpoint must have one error");
		JSONArray checkPointErrorsKeyJson = ((JSONObject) checkPointsJson.get(0)).getJSONArray("errors");
		Assert.assertNotNull(checkPointErrorsKeyJson, "checkpoint error key list must not be null");
		Assert.assertEquals(checkPointErrorsKeyJson.length(), 1, "checkpoint must contain one error");
		Assert.assertEquals(checkPointErrorsKeyJson.get(0), 0, "wrong error key");
		
		JSONArray errorsArrayJson = vrJson.getJSONArray("errors");
		Assert.assertNotNull(errorsArrayJson, "errors array must not be null");
		Assert.assertEquals(((JSONObject)errorsArrayJson.get(0)).getString("test_id"), "Neptune-Checkpoint-1", "wrong error test_id");
		Assert.assertEquals(((JSONObject)errorsArrayJson.get(0)).getString("error_id"), "neptune_checkpoint_1", "wrong error error_id");
		}
		
//		Assert.assertEquals(validationReport.toJson().toString(), array.toString(), "Invalid validation report json");

	}

	@Test(groups = { "JsonGeneration" }, description = "Json string", priority = 105)
	public void verifyJsonString() throws Exception {
	
		ValidationReport report = new ValidationReport();
		StringBuilder ret = new StringBuilder();
		Assert.assertEquals(report.toJsonString(ret, 0, "toto", "a\\ \" /", true).toString(),"\n\"toto\": \"a\\\\ \\\" \\/\"");
	}
}
