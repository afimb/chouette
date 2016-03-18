package mobi.chouette.exchange.validation.report;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import lombok.ToString;
import mobi.chouette.model.Line;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@Data
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "objectId", "name" })
public class LineLocation {

	@XmlElement(name = "objectid")
	private String objectId = "";

	@XmlElement(name = "label")
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
