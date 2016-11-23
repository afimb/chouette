package mobi.chouette.exchange.validation.report;

import java.io.PrintStream;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.report.ObjectReference.TYPE;
import mobi.chouette.model.JourneyPattern;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.testng.Assert;

public class ObjectReferenceTest implements Constant{
	{
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(oStream);
		JourneyPattern jp = new JourneyPattern();
		Context context = new Context();
		context.put(CHOUETTEID_GENERATOR, new DummyChouetteIdGenerator());
		DummyParameter dp = new DummyParameter();
		dp.setDefaultCodespace("DEFAULT_CODESPACE");
		context.put(PARAMETERS_FILE, dp);
		ObjectReference objectRef = new ObjectReference(context, jp);
		objectRef.print(stream, new StringBuilder(), 1, true);
		String text = oStream.toString();
		JSONObject res = null;
		try {
			res = new JSONObject(text);
			Assert.assertEquals(res.getString("type") , TYPE.journey_pattern, "wrong object reference type");
			Assert.assertEquals(res.getString("id") , jp.getChouetteId().getTechnicalId(), "wrong object referenced id");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
