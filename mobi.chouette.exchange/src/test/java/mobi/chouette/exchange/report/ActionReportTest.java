package mobi.chouette.exchange.report;

import java.io.PrintStream;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.report.ActionReporter.ERROR_CODE;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_STATE;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_TYPE;
import mobi.chouette.exchange.validation.report.ValidationReport;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ActionReportTest implements Constant{
	@Test(groups = { "JsonGeneration" }, description = "Json generated" ,priority=105 )
	public void verifyJsonGeneration() throws Exception {
		Context context = new Context();
		context.put(VALIDATION_REPORT, new ValidationReport());
		context.put(REPORT, new ActionReport());

		ActionReporter actionReporter = ActionReporter.Factory.getInstance();
		ActionReport actionReport = (ActionReport) context.get(REPORT);

		{
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(oStream);


		actionReport.print(stream);
		String text = oStream.toString();
		JSONObject res = new JSONObject(text);
		Assert.assertEquals(res.length(), 1 , "Report must contains 1 entry");
		Assert.assertTrue(res.has("action_report"), "Report must contains entry action_report");
		JSONObject arJson = res.getJSONObject("action_report");

		// Test progression presence
		JSONObject progression = arJson.getJSONObject("progression");
		Assert.assertNotNull(progression, "action report progression cannot be null");

		// Test result attribute
		Assert.assertEquals(arJson.get("result"), "OK", "action report result is not OK by default");
		}

		//Test zipFile add
		{
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(oStream);
			actionReporter.addZipReport(context, "zip1", IO_TYPE.INPUT);
			actionReport.print(stream);
			String text = oStream.toString();
			JSONObject res = new JSONObject(text);
			Assert.assertEquals(res.length(), 1 , "Report must contains 1 entry");
			Assert.assertTrue(res.has("action_report"), "Report must contains entry action_report");
			JSONObject arJson = res.getJSONObject("action_report");
			JSONArray zipFiles = arJson.getJSONArray("zip_files");
			Assert.assertEquals(((JSONObject)zipFiles.get(0)).getString("name"), "zip1", "action report must have a zip file named zip1");
		}

		//Test Failure add
		{
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(oStream);
			actionReporter.setActionError(context, ERROR_CODE.INVALID_FORMAT, "invalid format");
			actionReport.print(stream);
			String text = oStream.toString();
			JSONObject res = new JSONObject(text);
			Assert.assertEquals(res.length(), 1 , "Report must contains 1 entry");
			Assert.assertTrue(res.has("action_report"), "Report must contains entry action_report");
			JSONObject arJson = res.getJSONObject("action_report");
			JSONObject failure = arJson.getJSONObject("failure");
			Assert.assertNotNull(failure, "action report must contain one failure");
		}

		//Test Failure add
		{
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(oStream);
			actionReporter.setActionError(context, ERROR_CODE.INVALID_FORMAT, "invalid format");
			actionReport.print(stream);
			String text = oStream.toString();
			JSONObject res = new JSONObject(text);
			Assert.assertEquals(res.length(), 1 , "Report must contains 1 entry");
			Assert.assertTrue(res.has("action_report"), "Report must contains entry action_report");
			JSONObject arJson = res.getJSONObject("action_report");
			JSONObject failure = arJson.getJSONObject("failure");
			Assert.assertNotNull(failure, "action report must contain one failure");
		}

		//Test File report add
		{
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(oStream);
			actionReporter.addFileReport(context, "fileName", IO_TYPE.OUTPUT);
			actionReport.print(stream);
			String text = oStream.toString();
			JSONObject res = new JSONObject(text);
			Assert.assertEquals(res.length(), 1 , "Report must contains 1 entry");
			Assert.assertTrue(res.has("action_report"), "Report must contains entry action_report");
			JSONObject arJson = res.getJSONObject("action_report");
			JSONArray filesReport = arJson.getJSONArray("files");
			Assert.assertEquals(filesReport.length(), 1, "Action report must contain one file report");
		}

		//Test object report add
		{
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(oStream);
			actionReporter.addFileReport(context, "fileName", IO_TYPE.OUTPUT);
			actionReporter.addObjectReport(context, "object1", OBJECT_TYPE.ACCESS_POINT, "access point", OBJECT_STATE.WARNING, IO_TYPE.INPUT);
			actionReport.print(stream);
			String text = oStream.toString();
			JSONObject res = new JSONObject(text);
			Assert.assertEquals(res.length(), 1 , "Report must contains 1 entry");
			Assert.assertTrue(res.has("action_report"), "Report must contains entry action_report");
			JSONObject arJson = res.getJSONObject("action_report");

			JSONArray objectsReport = arJson.getJSONArray("objects");
			Assert.assertEquals(objectsReport.length(), 1, "Action report must contain one object report");
		}

		// Test object report collection add
		{
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(oStream);
			actionReporter.addFileReport(context, "fileName", IO_TYPE.OUTPUT);
			actionReporter.addObjectReport(context, "object1", OBJECT_TYPE.LINE, "line object", OBJECT_STATE.WARNING, IO_TYPE.INPUT);
			actionReport.print(stream);
			String text = oStream.toString();
			JSONObject res = new JSONObject(text);
			Assert.assertEquals(res.length(), 1 , "Report must contains 1 entry");
			Assert.assertTrue(res.has("action_report"), "Report must contains entry action_report");
			JSONObject arJson = res.getJSONObject("action_report");

			JSONArray objectCollectionsReport = arJson.getJSONArray("collections");
			JSONArray objects = ((JSONObject) objectCollectionsReport.get(0)).getJSONArray("objects");
			Assert.assertEquals(objects.length(), 1, "Action report must contain one object collection report");
		}
	}
}
