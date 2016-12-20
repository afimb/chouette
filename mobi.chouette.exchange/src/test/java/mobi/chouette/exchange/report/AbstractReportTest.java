package mobi.chouette.exchange.report;

import mobi.chouette.common.Constant;
import mobi.chouette.exchange.report.DummyReport.DUMMY_ENUM;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbstractReportTest implements Constant {
	@Test(groups = {"JsonGeneration"}, description = "Json generated", priority = 104)
	public void verifyprintIntArray() throws Exception {
		AbstractReport dummyReport = new DummyReport();
		{
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(oStream);
			StringBuilder sb = new StringBuilder();
			List<Number> lstNumber = new ArrayList<Number>();
			lstNumber.add(new Integer(1));
			stream.print(dummyReport.addLevel(sb, 1).append('{'));
			dummyReport.printIntArray(stream, sb, 1, "int_array_test", lstNumber, true);
			stream.print(dummyReport.addLevel(sb.append('\n'), 1).append('}'));
			String text = oStream.toString();
			JSONObject res = new JSONObject(text);

			// Test json generation for number array with one value
			Assert.assertEquals(res.length(), 1, "Report must contains 1 entry");
			Assert.assertTrue(res.has("int_array_test"), "Report must contains entry int_array_test");
			Assert.assertEquals(res.get("int_array_test").toString(), "[1]");
		}
		{
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(oStream);
			StringBuilder sb = new StringBuilder();
			List<Number> lstNumber = new ArrayList<Number>();

			lstNumber.add(new Integer(2));
			lstNumber.add(new Integer(3));
			lstNumber.add(new Integer(4));

			stream.print(dummyReport.addLevel(sb, 1).append('{'));
			dummyReport.printIntArray(stream, sb, 1, "int_array_test", lstNumber, true);
			stream.print(dummyReport.addLevel(sb.append('\n'), 1).append('}'));
			String text = oStream.toString();
			JSONObject res = new JSONObject(text);

			// Test json generation for number array with one or multiples values
			Assert.assertEquals(res.length(), 1, "Report must contains 1 entry");
			Assert.assertTrue(res.has("int_array_test"), "Report must contains entry int_array_test");
			Assert.assertEquals(res.get("int_array_test").toString(), "[2,3,4]");
		}

		{
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(oStream);
			StringBuilder sb = new StringBuilder();
			List<Number> lstNumber = new ArrayList<Number>();

			lstNumber.add(new Integer(1));
			stream.print(dummyReport.addLevel(sb, 1).append('{'));
			dummyReport.printObject(stream, sb, 1, "first", dummyReport, true);

			dummyReport.printIntArray(stream, sb, 1, "int_array_test", lstNumber, false);
			stream.print(dummyReport.addLevel(sb.append('\n'), 1).append('}'));
			String text = oStream.toString();
			JSONObject res = new JSONObject(text);

			// Test json generation for array value with multiple entries
			Assert.assertEquals(res.length(), 2, "Report must contains 2 entries");
			Assert.assertTrue(res.has("int_array_test"), "Report must contains entry int_array_test");
			Assert.assertEquals(res.get("int_array_test").toString(), "[1]");

		}
	}

	@Test(groups = {"JsonGeneration"}, description = "Json generated", priority = 104)
	public void verifyprintNativeType() throws Exception {
		AbstractReport dummyReport = new DummyReport();
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		StringBuilder sb = new StringBuilder();
		PrintStream stream = new PrintStream(oStream);
		stream.print(dummyReport.addLevel(sb, 1).append('{'));
		dummyReport.printObject(stream, sb, 1, "first", dummyReport, true);
		stream.print(dummyReport.addLevel(sb.append('\n'), 1).append('}'));
		String text = oStream.toString();
		JSONObject res = new JSONObject(text);

		// Test json generation for native type with one entry
		Assert.assertEquals(res.length(), 1, "Report must contains 1 entry");

		JSONObject first = res.getJSONObject("first");
		Assert.assertNotNull(first);
		Assert.assertEquals(first.getString("chaine"), "dummy string");
		Assert.assertEquals((first.get("enum")).toString(), (DUMMY_ENUM.NO_DATA_FOUND).toString());
		Assert.assertEquals(first.getInt("number"), 1);
		Assert.assertEquals(first.getBoolean("boolean"), true);
	}


	@Test(groups = {"JsonGeneration"}, description = "Json generated", priority = 104)
	public void verifyprintArray() throws Exception {
		AbstractReport dummyReport = new DummyReport();
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(oStream);
		StringBuilder sb = new StringBuilder();
		List<DummyReportElement> lstDRE = new ArrayList<DummyReportElement>();
		lstDRE.add(new DummyReportElement(1, "dre1", true));
		lstDRE.add(new DummyReportElement(2, "dre2", false));
		lstDRE.add(new DummyReportElement(3, "dre3", true));
		lstDRE.add(new DummyReportElement(4, "dre4", false));
		stream.print(dummyReport.addLevel(sb, 1).append('{'));
		dummyReport.printArray(stream, sb, 1, "dummy_list", lstDRE, true);
		stream.print(dummyReport.addLevel(sb.append('\n'), 1).append('}'));
		String text = oStream.toString();
		JSONObject res = new JSONObject(text);
		Assert.assertEquals(res.length(), 1, "Report must contains 1 entry");

		JSONArray dummyJson = res.getJSONArray("dummy_list");
		Assert.assertNotNull(dummyJson);
		Assert.assertTrue(res.has("dummy_list"), "Report must contains entry dummy_list");

		Assert.assertEquals(((JSONObject) dummyJson.get(0)).get("id"), 1);
		Assert.assertEquals(((JSONObject) dummyJson.get(0)).get("chaine"), "dre1");
		Assert.assertEquals(((JSONObject) dummyJson.get(0)).get("ok"), true);

		Assert.assertEquals(((JSONObject) dummyJson.get(3)).get("id"), 4);
		Assert.assertEquals(((JSONObject) dummyJson.get(3)).get("chaine"), "dre4");
		Assert.assertEquals(((JSONObject) dummyJson.get(3)).get("ok"), false);
	}


	@Test(groups = {"JsonGeneration"}, description = "Json generated", priority = 104)
	public void verifyprintMap() throws Exception {
		AbstractReport dummyReport = new DummyReport();
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(oStream);
		StringBuilder sb = new StringBuilder();
		Map<Integer, DummyReportElement> mapDRE = new HashMap<Integer, DummyReportElement>();
		mapDRE.put(1, new DummyReportElement(1, "dre1", true));
		mapDRE.put(2, new DummyReportElement(2, "dre2", false));
		mapDRE.put(3, new DummyReportElement(3, "dre3", true));
		mapDRE.put(4, new DummyReportElement(4, "dre4", false));
		stream.print(dummyReport.addLevel(sb, 1).append('{'));
		dummyReport.printMap(stream, sb, 1, "dummy_map", mapDRE, true);
		stream.print(dummyReport.addLevel(sb.append('\n'), 1).append('}'));
		String text = oStream.toString();
		JSONObject res = new JSONObject(text);
		Assert.assertEquals(res.length(), 1, "Report must contains 1 entry");

		JSONObject dummyJson = res.getJSONObject("dummy_map");
		Assert.assertNotNull(dummyJson);
		Assert.assertTrue(res.has("dummy_map"), "Report must contains entry dummy_map");
		Assert.assertEquals(dummyJson.length(), 4, "Report must contains 4 entries");
	}

}
