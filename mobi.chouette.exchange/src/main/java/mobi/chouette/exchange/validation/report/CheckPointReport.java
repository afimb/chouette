package mobi.chouette.exchange.validation.report;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import mobi.chouette.exchange.report.AbstractReport;
import mobi.chouette.exchange.validation.report.ValidationReporter.RESULT;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;


@Data
@EqualsAndHashCode(callSuper=false)
@ToString
@XmlType(propOrder = { "name", "phase", "target", "rank", "severity", "state", "checkPointErrorCount", "checkPointErrorsKeys" })
public class CheckPointReport extends AbstractReport{

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

	protected boolean addCheckPointError(int checkPointErrorId) {
		boolean ret = false;
		if (maxByFile) {
			if (checkPointErrorCount < maxErrors) 
			{
				checkPointErrorsKeys.add(new Integer(checkPointErrorId));
				ret = true;
			}
		}
		checkPointErrorCount++;
		state = RESULT.NOK;
		return ret;
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

	@Override
	public void print(PrintStream out, StringBuilder ret , int level, boolean first) {
		ret.setLength(0);
		out.print(addLevel(ret,level).append('{'));
		out.print(toJsonString(ret,level+1,"test_id", name, true));
		out.print(toJsonString(ret,level+1,"level", phase, false));
		out.print(toJsonString(ret,level+1,"type", target, false));
		out.print(toJsonString(ret,level+1,"rank", rank, false));
		out.print(toJsonString(ret,level+1,"severity", severity, false));
		out.print(toJsonString(ret,level+1,"result", state, false));
		out.print(toJsonString(ret,level+1,"check_point_error_count", checkPointErrorCount, false));
		if (!checkPointErrorsKeys.isEmpty())
			printIntArray(out,ret, level+1,"errors",checkPointErrorsKeys, false);
		ret.setLength(0);
		out.print(addLevel(ret.append('\n'),level).append('}'));
		
	}

}
