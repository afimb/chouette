package mobi.chouette.exchange.report;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_TYPE;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"type", "objects", "stats"})
public class ObjectCollectionReport extends AbstractReport {
	@XmlElement(name = "type", required = true)
	private ActionReporter.OBJECT_TYPE objectType;

	@XmlElement(name = "objects")
	private List<ObjectReport> objectReports = new ArrayList<>();

	@XmlElement(name = "stats")
	private Map<OBJECT_TYPE, Integer> stats = new HashMap<OBJECT_TYPE, Integer>();

	/**
	 * @param object
	 */
	protected void addObjectReport(ObjectReport object) {
		if (findObjectReport(object.getObjectId()) == null)
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

		return object;
	}

	public ObjectReport findObjectReport(String objectId) {
		for (ObjectReport object : objectReports) {
			if (object.getObjectId().equals(objectId))
				return object;
		}
		return null;
	}

	@Override
	public void print(PrintStream out, StringBuilder ret, int level, boolean first) {
		ret.setLength(0);
		out.print(addLevel(ret, level).append('{'));
		out.print(toJsonString(ret, level + 1, "type", objectType.toString().toLowerCase(), true));
		if (!objectReports.isEmpty())
			printArray(out, ret, level + 1, "objects", objectReports, false);
		if (!stats.isEmpty()) {
			printMap(out, ret, level + 1, "stats", stats, false);
		}
		ret.setLength(0);
		out.print(addLevel(ret.append('\n'), level).append('}'));

	}
}
