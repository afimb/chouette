package mobi.chouette.exchange.report;

import java.io.PrintStream;

import mobi.chouette.exchange.report.ActionReporter.OBJECT_STATE;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_TYPE;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ObjectCollectionReportTest {
	@Test(groups = { "JsonGeneration" }, description = "Json generated" ,priority=105 )
	public void verifyJsonGeneration() throws Exception {
		
		// Object report and stats null
		{
			ObjectCollectionReport objectCollectionReport = new ObjectCollectionReport();
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(oStream);
			objectCollectionReport.setObjectType(OBJECT_TYPE.LINE);
			objectCollectionReport.print(stream, 1, true);
			String text = oStream.toString();
			JSONObject res = new JSONObject(text);
			
			// Test type presence
			Assert.assertEquals(res.getString("type"), (OBJECT_TYPE.LINE).toString().toLowerCase(), "wrong object collection type");
		}
		
		// Object report and stats not null
		{
			ObjectCollectionReport objectCollectionReport = new ObjectCollectionReport();
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(oStream);
			objectCollectionReport.setObjectType(OBJECT_TYPE.LINE);
			objectCollectionReport.addObjectReport(new ObjectReport("line1", OBJECT_TYPE.LINE, "line_1", OBJECT_STATE.OK,IO_TYPE.INPUT));
			objectCollectionReport.addStatTypeToObject(OBJECT_TYPE.LINE, 1);
			objectCollectionReport.print(stream, 1, true);
			String text = oStream.toString();
			JSONObject res = new JSONObject(text);
			
			// Test type presence
			Assert.assertEquals(res.getString("type"), (OBJECT_TYPE.LINE).toString().toLowerCase(), "wrong object collection type");
			
			JSONArray objects = res.getJSONArray("objects");
			// Test objects presence
			Assert.assertEquals(objects.length(), 1, "object collection report must have one object report");
			
			JSONObject stats = res.getJSONObject("stats");
			// Test stats presence
			Assert.assertEquals(stats.length(), 1, "object collection report must have one object stat");
		}
	}
}
