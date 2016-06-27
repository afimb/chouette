package mobi.chouette.exchange.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;
import lombok.ToString;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_STATE;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_TYPE;
import mobi.chouette.exchange.validation.report.CheckPointReport.SEVERITY;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@ToString
public class ObjectReport {
	public static final int maxErrors = 30;

	@XmlElement(name = "type", required = true)
	@Getter
	private ActionReporter.OBJECT_TYPE type;

	@XmlElement(name = "description", required = true)
	@Getter
	private String description;

	@XmlElement(name = "status", required = true)
	@Getter
	private OBJECT_STATE status = OBJECT_STATE.OK;

	@XmlElement(name = "stats", required = true)
	@Getter
	private Map<OBJECT_TYPE, Integer> stats = new HashMap<OBJECT_TYPE, Integer>();

	@XmlElement(name = "io_type")
	@Getter
	private IO_TYPE ioType;

	@XmlElement(name = "errors")
	@Getter
	private List<ObjectError2> errors = new ArrayList<ObjectError2>();

	@XmlElement(name = "checkpoint_errors")
	@Getter
	private List<Integer> checkPointErrorKeys = new ArrayList<Integer>();

	@XmlElement(name = "checkpoint_error_count")
	@Getter
	private int checkPointErrorCount = 0;

	@XmlElement(name = "checkpoint_warning_count")
	@Getter
	private int checkPointWarningCount = 0;

	@XmlElement(name = "objectid")
	@Getter
	private String objectId;

	protected ObjectReport(String objectId, OBJECT_TYPE type, String description, OBJECT_STATE status, IO_TYPE ioType) {
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
	protected void addError(ObjectError2 error) {
		status = OBJECT_STATE.ERROR;
		errors.add(error);
	}

	/**
	 * 
	 * @param checkPointErrorId
	 */
	protected boolean addCheckPointError(int checkPointErrorId, SEVERITY severity) {
		boolean ret = false;

		if (checkPointErrorCount + checkPointWarningCount < maxErrors) {
			checkPointErrorKeys.add(new Integer(checkPointErrorId));
			ret = true;
		}

		switch (severity) {
		case WARNING:
			checkPointWarningCount++;
			break;

		default: // ERROR
			checkPointErrorCount++;
			status = OBJECT_STATE.ERROR;
			break;
		}
		return ret;
	}

	/**
	 * Add stat to data type
	 * 
	 * @param type
	 * @param count
	 */
	protected void addStatTypeToObject(OBJECT_TYPE type, int count) {

		if (stats.containsKey(type)) {
			stats.put(type, new Integer(stats.get(type).intValue() + count));
		} else {
			stats.put(type, new Integer(count));
		}

	}

	/**
	 * set stat to data type 
	 * 
	 * @param type
	 * @param count
	 * @return previous value
	 */
	protected int setStatTypeToObject(OBJECT_TYPE type, int count) {

		int oldvalue = 0;
		if (stats.containsKey(type)) {
			oldvalue = stats.get(type).intValue();
		}
		stats.put(type, new Integer(count));
		return oldvalue;

	}

	public JSONObject toJson() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("type", type.toString().toLowerCase());
		object.put("description", description);
		object.put("objectid", objectId);
		object.put("status", status);
		if (ioType != null) {
			object.put("io_type", ioType);
		}
		if (!stats.isEmpty()) {
			JSONObject map = new JSONObject();
			object.put("stats", map);
			for (Entry<OBJECT_TYPE, Integer> entry : stats.entrySet()) {
				
				map.put(entry.getKey().toString().toLowerCase(),entry.getValue());
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
			object.put("check_point_errors", array);
			for (Integer value : checkPointErrorKeys) {
				array.put(value);
			}
		}

		object.put("check_point_error_count", checkPointErrorCount);
		object.put("check_point_warning_count", checkPointWarningCount);
		return object;
	}
}
