package mobi.chouette.exchange.validation.report;

import java.io.PrintStream;

import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LocationTest {
	@Test(groups = { "JsonGeneration" }, description = "Json generated", priority = 104)
	public void verifyJsonGeneration() throws Exception {
		
		// file not null
		{
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(oStream);
			Location location = new Location("fileName", 0, 0);
			location.print(stream, new StringBuilder(), 1, true);
			String text = oStream.toString();
			JSONObject res = new JSONObject(text);
			
			Assert.assertNotNull(res.getJSONObject("file") , "location file shall not be null");
		}
		
		// object id not null
		{
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(oStream);
			Location location = new Location("fileName", 0, 0, "neptune");
			location.print(stream, new StringBuilder(), 1, true);
			String text = oStream.toString();
			JSONObject res = new JSONObject(text);
			
			Assert.assertEquals(res.getString("objectid") , "neptune", "wrong location object id");
		}
		
		// location name not null
		{
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(oStream);
			Location location = new Location("fileName", "locationName", 0);
			location.print(stream, new StringBuilder(), 1, true);
			String text = oStream.toString();
			JSONObject res = new JSONObject(text);
			
			Assert.assertEquals(res.getString("label") , "locationName", "wrong location object name");
		}
		
		// object ref not null
		{
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(oStream);
			JourneyPattern jp = new JourneyPattern();
			Route route = new Route();
			route.setObjectId("route1");
			route.setName("rname");
			Line line = new Line();
			line.setObjectId("line1");
			line.setName("lname");
			route.setLine(line);
			jp.setRoute(route);
			Location location = new Location(jp);
			location.print(stream, new StringBuilder(), 1, true);
			String text = oStream.toString();
			JSONObject res = new JSONObject(text);
			JSONArray objectRef = res.getJSONArray("object_path");
			
			Assert.assertNotNull(objectRef.get(0), "journey pattern reference shall not be null");
		}
	}
}
