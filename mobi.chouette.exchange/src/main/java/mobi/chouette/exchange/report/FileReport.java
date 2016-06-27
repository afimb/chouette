package mobi.chouette.exchange.report;

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

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "name", "status", "ioType", "errors", "checkPointErrorKeys", "checkPointErrorCount",
		"checkPointWarningCount" })
@Data
@EqualsAndHashCode(exclude = { "status", "errors" })
@NoArgsConstructor
public class FileReport {

	@XmlElement(name = "name", required = true)
	private String name;

	@XmlElement(name = "status", required = true)
	private FILE_STATE status;

	@XmlElement(name = "io_type", required = true)
	private IO_TYPE ioType;

	@XmlElement(name = "errors")
	private List<FileError2> errors = new ArrayList<>();

	@XmlElement(name = "checkpoint_errors")
	private List<Integer> checkPointErrorKeys = new ArrayList<Integer>();

	@XmlElement(name = "checkpoint_error_count")
	private Integer checkPointErrorCount;

	@XmlElement(name = "checkpoint_warning_count")
	private Integer checkPointWarningCount;

	protected void addError(FileError2 fileError2) {
		status = FILE_STATE.ERROR;
		errors.add(fileError2);
	}

	protected FileReport(String name, FILE_STATE state, IO_TYPE ioType) {
		this.name = name;
		this.status = state;
		this.ioType = ioType;
	}

	protected FileReport(String name, FILE_STATE state, IO_TYPE ioType, FileError2 fileError) {
		this(name, state, ioType);
		errors.add(fileError);
	}

	protected FileReport(String name, FILE_STATE state, IO_TYPE ioType, List<FileError2> fileErrors) {
		this(name, state, ioType);
		errors.addAll(fileErrors);
	}

	/**
	 * 
	 * @param checkPointErrorId
	 */
	protected void addCheckPointError(int checkPointErrorId) {
		checkPointErrorKeys.add(new Integer(checkPointErrorId));
		checkPointErrorCount++;
	}

	public JSONObject toJson() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("name", name);
		object.put("status", status);
		object.put("io_type", ioType);
		if (!errors.isEmpty()) {
			JSONArray array = new JSONArray();
			object.put("errors", array);
			for (FileError2 error : errors) {
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
		object.put("check_point_error_count : ", checkPointErrorCount);
		object.put("check_point_warning_count : ", checkPointWarningCount);

		return object;
	}

}
