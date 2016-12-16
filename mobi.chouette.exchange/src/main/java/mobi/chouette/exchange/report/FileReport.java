package mobi.chouette.exchange.report;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import mobi.chouette.exchange.report.ActionReporter.FILE_STATE;
import mobi.chouette.exchange.validation.report.CheckPointReport.SEVERITY;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "name", "status", "ioType", "errors", "checkPointErrorKeys", "checkPointWarningKeys", "checkPointErrorCount",
		"checkPointWarningCount" })
@Data
@EqualsAndHashCode(exclude = { "status", "errors" }, callSuper=false)
@NoArgsConstructor
public class FileReport extends AbstractReport{

	@XmlElement(name = "name", required = true)
	private String name;

	@XmlElement(name = "status", required = true)
	private FILE_STATE status;

	@XmlElement(name = "io_type", required = true)
	private IO_TYPE ioType;

	@XmlElement(name = "errors")
	private List<FileError> errors = new ArrayList<>();
	
	
	@XmlElement(name = "check_point_errors")
	private List<Integer> checkPointErrorKeys = new ArrayList<Integer>();
	
	@XmlElement(name = "check_point_warnings")
	private List<Integer> checkPointWarningKeys = new ArrayList<Integer>();

	@XmlElement(name = "check_point_error_count")
	private int checkPointErrorCount = 0;

	@XmlElement(name = "check_point_warning_count")
	private int checkPointWarningCount = 0;

	protected void addError(FileError fileError2) {
		status = FILE_STATE.ERROR;
		errors.add(fileError2);
	}

	protected FileReport(String name, FILE_STATE state, IO_TYPE ioType) {
		this.name = name;
		this.status = state;
		this.ioType = ioType;
	}

	protected FileReport(String name, FILE_STATE state, IO_TYPE ioType, FileError fileError) {
		this(name, state, ioType);
		errors.add(fileError);
	}

	protected FileReport(String name, FILE_STATE state, IO_TYPE ioType, List<FileError> fileErrors) {
		this(name, state, ioType);
		errors.addAll(fileErrors);
	}

	/**
	 * 
	 * @param checkPointErrorId
	 * @param severity
	 */
	protected boolean addCheckPointError(int checkPointErrorId, SEVERITY severity) {
		boolean ret = false;

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
			status = FILE_STATE.ERROR;
			break;
		}
		return ret;
	}

	public JSONObject toJson() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("name", name);
		object.put("status", status);
		object.put("io_type", ioType);
		if (!errors.isEmpty()) {
			JSONArray array = new JSONArray();
			object.put("errors", array);
			for (FileError error : errors) {
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
		out.print(addLevel(ret,level).append('{'));
		out.print(toJsonString(ret,level+1,"name", name, true));
		out.print(toJsonString(ret,level+1,"status", status, false));
		out.print(toJsonString(ret,level+1,"io_type", ioType, false));
		
		if (!errors.isEmpty()) {
			printArray(out,ret, level+1,"errors",errors, false);
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
			printIntArray(out,ret, level+1,"check_point_errors",lstErrorKeys, false);
		out.print(toJsonString(ret,level+1,"check_point_error_count", checkPointErrorCount, false));
		out.print(toJsonString(ret,level+1,"check_point_warning_count", checkPointWarningCount, false));
		ret.setLength(0);
		out.print(addLevel(ret.append('\n'),level).append('}'));
	}

}
