package mobi.chouette.exchange.validation.report;

import lombok.Data;
import lombok.ToString;
import mobi.chouette.model.Line;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@Data
@ToString
public class LineLocation {

	private String objectId = "";

	private String name = "";

	public LineLocation(Line line) {
		this.objectId = line.getObjectId();
		this.name = Location.buildName(line);
	}

	public JSONObject toJson() throws JSONException {
		JSONObject object = new JSONObject();
		if (objectId != null)
			object.put("objectid", objectId);
		if (name != null)
			object.put("label", name);
		return object;
	}

}
