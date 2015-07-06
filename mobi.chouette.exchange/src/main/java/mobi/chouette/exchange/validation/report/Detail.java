package mobi.chouette.exchange.validation.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import lombok.ToString;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@Data
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "key", "source", "targets", "value", "referenceValue" })
public class Detail {

	@XmlElement(name = "source")
	private Location source;

	@XmlElement(name = "target")
	private List<Location> targets = new ArrayList<>();

	@XmlElement(name = "error_id", required = true)
	private String key;

	@XmlElement(name = "error_value")
	private String value = "";

	@XmlElement(name = "reference_value")
	private String referenceValue = "";

	public Detail(String key, Location source) {
		setKey(key.replaceAll("-", "_").toLowerCase());
		this.source = source;

	}

	public Detail(String key, Location source, String value) {
		this(key, source);
		this.value = value;

	}

	public Detail(String key, Location source, String value, String refValue) {
		this(key, source, value);
		this.referenceValue = refValue;

	}

	public Detail(String key, Location source, Location... targets) {

		this(key, source);
		this.getTargets().addAll(Arrays.asList(targets));

	}

	public Detail(String key, Location source, String value, Location... targets) {
		this(key, source, value);
		this.getTargets().addAll(Arrays.asList(targets));

	}

	public Detail(String key, Location source, String value, String refValue, Location... targets) {
		this(key, source, value, refValue);
		this.getTargets().addAll(Arrays.asList(targets));

	}

	public JSONObject toJson() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("error_id", key);
		if (source != null) {
			object.put("source", source.toJson());
		}
		if (!targets.isEmpty()) {
			JSONArray array = new JSONArray();
			object.put("target", array);
			for (Location target : targets) {
				array.put(target.toJson());
			}
		}
		if (value != null) {
			object.put("error_value", value);
		}
		if (referenceValue != null) {
			object.put("reference_value", referenceValue);
		}
		return object;
	}

}
