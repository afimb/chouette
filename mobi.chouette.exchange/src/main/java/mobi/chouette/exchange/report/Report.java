package mobi.chouette.exchange.report;

import java.io.PrintStream;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public interface Report {

	JSONObject toJson() throws JSONException;
	
	boolean isEmpty();

	void print(PrintStream stream);
	
}
