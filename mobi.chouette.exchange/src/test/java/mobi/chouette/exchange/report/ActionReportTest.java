package mobi.chouette.exchange.report;

import java.io.PrintStream;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.exchange.validation.report.CheckPointReport.SEVERITY;
import mobi.chouette.exchange.validation.report.ValidationReporter.RESULT;

import org.apache.commons.io.output.ByteArrayOutputStream;
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
	
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(oStream);
		
		actionReport.print(stream, 1, true);
		String text = oStream.toString();
		JSONObject res = new JSONObject(text);
		Assert.assertEquals(res.length(), 1 , "Report must contains 1 entry");
		Assert.assertTrue(res.has("action_report"), "Report must contains entry action_report");
		JSONObject vrJson = res.getJSONObject("action_report");
	
	}
}
