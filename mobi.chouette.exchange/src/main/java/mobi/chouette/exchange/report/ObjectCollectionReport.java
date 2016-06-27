package mobi.chouette.exchange.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import lombok.ToString;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_TYPE;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@Data
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "objectType", "objectReports", "stats", "ioType" })
public class ObjectCollectionReport {
	@XmlElement(name = "type", required = true)
	private ActionReporter.OBJECT_TYPE objectType;

	@XmlElement(name = "object_reports")
	private List<ObjectReport> objectReports = new ArrayList<ObjectReport>();

	@XmlElement(name = "stats")
	private Map<ActionReporter.OBJECT_TYPE, Integer> stats = new HashMap<ActionReporter.OBJECT_TYPE, Integer>();

	@XmlElement(name = "io_type")
	private IO_TYPE ioType;

	/**
	 * 
	 * @param object
	 */
	protected void addObjectReport(ObjectReport object) {
		objectReports.add(object);
	}

	/**
	 * Add stat to data type
	 * 
	 * @param type
	 */
	protected void addStatTypeToObject(ActionReporter.OBJECT_TYPE type, int count) {
		if (stats.containsKey(type)) {
			stats.put(type, new Integer(stats.get(type).intValue() + count));
		} else {
			stats.put(type, new Integer(count));
		}
	}

	public JSONObject toJson() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("type", objectType.toString().toLowerCase());

		JSONArray objects = new JSONArray();

		for (ObjectReport objectReport : objectReports) {
			objects.put(objectReport.toJson());
		}
		object.put("objects", objects);

		if (!stats.isEmpty()) {
			JSONObject map = new JSONObject();
			object.put("stats", map);
			for (Entry<OBJECT_TYPE, Integer> entry : stats.entrySet()) {

				map.put(entry.getKey().toString().toLowerCase(), entry.getValue());
			}
		}

		if (ioType != null)
			object.put("io_type", ioType);

		return object;
	}

	public ObjectReport findObjectReport(String objectId) {
		for (ObjectReport object : objectReports) {
			if (object.getObjectId().equals(objectId))
				return object;
		}
		return null;
	}
}
