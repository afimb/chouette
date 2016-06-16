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

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@Data
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "objectType", "objectReports", "stats", "ioType" })
public class ObjectCollectionReport {
	@XmlElement(name = "type",required=true)
	private String objectType;
	
	@XmlElement(name = "object_reports")
	private List<ObjectReport> objectReports = new ArrayList<ObjectReport>();
	
	@XmlElement(name = "stats")
	private Map<String, Integer> stats = new HashMap<String, Integer>();
	
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
	 * @param type
	 */
	protected void addStatTypeToObject(String type) {
		if(stats.containsKey(type)) {
			stats.put(type, new Integer(stats.get(type).intValue() + 1));
		} else {
			stats.put(type, new Integer(1));
		}
	}
	
	public JSONObject toJson() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("type", objectType);
	
		JSONArray lines = new JSONArray();

		for (ObjectReport objectReport : objectReports) {
			lines.put(objectReport.toJson());
		}
		object.put("lines", lines);
		
		if (!stats.isEmpty()) {
			JSONArray array = new JSONArray();
			object.put("stats", array);
			for (Entry<String, Integer> entry : stats.entrySet())
			{
				array.put(entry.getKey() + " - " + entry.getValue());
			}
		}
		
		if(ioType != null)
			object.put("io_type", ioType);

		return object;
	}
}
