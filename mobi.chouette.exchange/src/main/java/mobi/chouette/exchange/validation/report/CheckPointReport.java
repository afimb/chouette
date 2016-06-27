package mobi.chouette.exchange.validation.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import lombok.ToString;
import mobi.chouette.exchange.validation.report.ValidationReporter.RESULT;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;


@Data
@ToString
@XmlType(propOrder = { "name", "phase", "target", "rank", "severity", "state", "checkPointErrorCount", "checkPointErrorsKeys" })
public class CheckPointReport {
	public static final int maxErrors = 30;

	public enum SEVERITY {
		WARNING, ERROR, IMPROVMENT
	};


	@XmlElement(name = "test_id", required = true)
	private String name;

	@XmlElement(name = "level", required = true)
	private String phase;

	@XmlElement(name = "object_type", required = true)
	private String target;

	@XmlElement(name = "rank", required = true)
	private String rank;

	@XmlElement(name = "severity", required = true)
	private SEVERITY severity;

	@XmlElement(name = "result", required = true)
	private RESULT state;

	@XmlElement(name = "check_point_error_count")
	private int checkPointErrorCount = 0;

	@XmlElement(name = "errors")
	private List<Integer> checkPointErrorsKeys = new ArrayList<Integer>();

	@XmlTransient
	private boolean maxByFile = true;

	protected CheckPointReport(String name, RESULT state, SEVERITY severity) {
		this.name = name;
		this.severity = severity;
		this.state = state;

		String[] token = name.split("\\-");
		if (token.length >= 4) {
			this.phase = token[0];
			this.target = token[2];
			this.rank = token[3];
		} else if (token.length == 3) {
			this.phase = token[0];
			this.target = token[1];
			this.rank = token[2];
		} else {
			throw new IllegalArgumentException("invalid name " + name);
		}
	}

	protected void addCheckPointError(int checkPointErrorId) {
		if (maxByFile) {
			if (checkPointErrorCount < maxErrors) 
				checkPointErrorsKeys.add(new Integer(checkPointErrorId));
		}
		checkPointErrorCount++;
		state = RESULT.NOK;
	}

	public JSONObject toJson() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("test_id", name);
		object.put("level", phase);
		object.put("type", target);
		object.put("rank", rank);
		object.put("severity", severity);
		object.put("result", state);
		object.put("check_point_error_count", checkPointErrorCount);
		if (checkPointErrorCount > 0) {
			JSONArray errors = new JSONArray();

			for (Integer errorKey : checkPointErrorsKeys) {
				errors.put(errorKey);
			}
			object.put("check_point_errors", errors);
		}

		return object;
	}
}
