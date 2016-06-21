package mobi.chouette.exchange.report;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public interface Report {

	JSONObject toJson() throws JSONException;

	boolean isEmpty();
	
}
