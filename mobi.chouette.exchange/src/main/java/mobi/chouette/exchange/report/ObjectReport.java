package mobi.chouette.exchange.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_STATE;
import mobi.chouette.model.Line;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ObjectReport {

	@XmlElement(name = "type",required=true)
	@Getter
	private String type;
	
	@XmlElement(name = "description",required=true)
	private String description;

	@XmlElement(name = "status",required=true)
	private OBJECT_STATE status = OBJECT_STATE.OK;
	
	@XmlElement(name = "stats",required=true)
	private Map<String, Integer> stats = new HashMap<String, Integer>();

	@XmlElement(name = "io_type")
	private IO_TYPE ioType;

	@XmlElement(name="errors")
	private List<ObjectError2> errors = new ArrayList<ObjectError2>();
	
	@XmlElement(name="checkpoint_errors")
	private List<Integer> checkPointErrorKeys = new ArrayList<Integer>();
	
	@XmlElement(name="checkpoint_error_count")
	private int checkPointErrorCount;
	
	@XmlElement(name="checkpoint_improvment_count")
	private int checkPointImprovmentCount;
	
	@XmlElement(name="objectid")
	private String objectId;
	
	protected ObjectReport(String objectId, String type, String description, OBJECT_STATE status, IO_TYPE ioType)
	{
		this.objectId = objectId;
		this.type = type;
		this.description = description;
		this.status = status;
		this.ioType = ioType;
	}
	
	/**
	 * add an error; status will be set to ERROR
	 * 
	 * @param error
	 */
	protected void addError(ObjectError2 error)
	{
		status = OBJECT_STATE.ERROR;
		errors.add(error);
	}
	
	/**
	 * 
	 * @param checkPointErrorId
	 */
	protected void addCheckPointError(int checkPointErrorId) {
		checkPointErrorKeys.add(new Integer(checkPointErrorId));		
		checkPointErrorCount++;
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
		object.put("type", type);
		object.put("description", description);
		object.put("objectid", objectId);
		object.put("status", status);
		if (ioType != null)
		{
			object.put("io_type",ioType);
		}
		if (!stats.isEmpty()) {
			JSONArray array = new JSONArray();
			object.put("stats", array);
			for (Entry<String, Integer> entry : stats.entrySet())
			{
				array.put(entry.getKey() + " - " + entry.getValue());
			}
		}
		
		if (!errors.isEmpty()) {
			JSONArray array = new JSONArray();
			object.put("errors", array);
			for (ObjectError2 error : errors) {
				array.put(error.toJson());
			}
		}
		
		if (!checkPointErrorKeys.isEmpty()) {
			JSONArray array = new JSONArray();
			object.put("checkPointerrorKeys", array);
			for (Integer value : checkPointErrorKeys) {
				array.put(value);
			}
		}
		
		object.put("errors number : ", checkPointErrorCount);
		object.put("improvments number : ", checkPointImprovmentCount);
		return object;
	}
}
