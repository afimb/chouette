package mobi.chouette.exchange.validation.report;

import java.io.PrintStream;

import mobi.chouette.exchange.validation.report.ObjectReference.TYPE;
import mobi.chouette.model.JourneyPattern;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.testng.Assert;

public class ObjectReferenceTest {
	{
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(oStream);
		JourneyPattern jp = new JourneyPattern();
		ObjectReference objectRef = new ObjectReference(jp);
		objectRef.print(stream, new StringBuilder(), 1, true);
		String text = oStream.toString();
		JSONObject res = null;
		try {
			res = new JSONObject(text);
			Assert.assertEquals(res.getString("type") , TYPE.journey_pattern, "wrong object reference type");
			Assert.assertEquals(res.getString("id") , jp.getObjectId(), "wrong object referenced id");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
