package mobi.chouette.exchange.report;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_STATE;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_TYPE;
import mobi.chouette.exchange.validation.report.CheckPointReport.SEVERITY;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@ToString
public class ObjectReport extends AbstractReport {

	@Getter
	private ActionReporter.OBJECT_TYPE type;

	@Getter
	@Setter
	private String description;

	@Getter
	private OBJECT_STATE status = OBJECT_STATE.OK;

	@Getter
	private Map<OBJECT_TYPE, Integer> stats = new HashMap<OBJECT_TYPE, Integer>();

	@Getter
	private IO_TYPE ioType;

	@Getter
	private List<ObjectError> errors = new ArrayList<ObjectError>();

	@Getter
	private List<Integer> checkPointErrorKeys = new ArrayList<Integer>();
	
	@Getter
	private List<Integer> checkPointWarningKeys = new ArrayList<Integer>();

	@Getter
	private int checkPointErrorCount = 0;

	@Getter
	private int checkPointWarningCount = 0;

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
	protected void addError(ObjectError error) {
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
			if (checkPointWarningCount < maxErrors) {
				checkPointWarningKeys.add(new Integer(checkPointErrorId));
				ret = true;
			}
			checkPointWarningCount++;
			break;

		default: // ERROR
			if (checkPointErrorCount < maxErrors) {
				checkPointErrorKeys.add(new Integer(checkPointErrorId));
				ret = true;
			}
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

				map.put(entry.getKey().toString().toLowerCase(), entry.getValue());
			}
		}

		if (!errors.isEmpty()) {
			JSONArray array = new JSONArray();
			object.put("errors", array);
			for (ObjectError error : errors) {
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

	@Override
	public void print(PrintStream out, StringBuilder ret , int level, boolean first) {
		ret.setLength(0);
		out.print(addLevel(ret, level).append('{'));
		out.print(toJsonString(ret, level + 1, "type", type.toString().toLowerCase(), true));
		out.print(toJsonString(ret, level + 1, "description", description, false));
		out.print(toJsonString(ret, level + 1, "objectid", objectId, false));
		out.print(toJsonString(ret, level + 1, "status", status, false));
		if (ioType != null)
			out.print(toJsonString(ret, level + 1, "io_type", ioType, false));
		if (!stats.isEmpty()) {
			printMap(out, ret, level + 1, "stats", stats, false);
		}
		if (!errors.isEmpty()) {
			printArray(out, ret, level + 1, "errors", errors, false);
		}
		List<Integer> lstErrorKeys = new ArrayList<Integer>();
		for(Integer numError: checkPointErrorKeys) {
			if(lstErrorKeys.size() < maxErrors)
				lstErrorKeys.add(numError);
			else
				break;
		}
		for(Integer numWarning: checkPointWarningKeys) {
			if(lstErrorKeys.size() < maxErrors)
				lstErrorKeys.add(numWarning);
			else
				break;
		}
		if (!lstErrorKeys.isEmpty())
			printIntArray(out, ret, level + 1, "check_point_errors", lstErrorKeys, false);

		out.print(toJsonString(ret, level + 1, "check_point_error_count", checkPointErrorCount, false));
		out.print(toJsonString(ret, level + 1, "check_point_warning_count", checkPointWarningCount, false));

		ret.setLength(0);
		out.print(addLevel(ret.append('\n'), level).append('}'));

	}
}
