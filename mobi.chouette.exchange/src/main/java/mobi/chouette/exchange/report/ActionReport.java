package mobi.chouette.exchange.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@XmlRootElement(name = "action_report")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "progression", "result", "zip", "files", "lines", "stats", "failure" })
@NoArgsConstructor
@Data
public class ActionReport {

	@XmlElement(name = "progression", required = true)
	private Progression progression = new Progression();

	@XmlElement(name = "result", required = true)
	private String result = ReportConstant.STATUS_OK;

	@XmlElement(name = "zip_file")
	private FileInfo zip;

	@XmlElement(name = "files")
	private List<FileInfo> files = new ArrayList<>();

	@XmlElement(name = "lines")
	private List<LineInfo> lines = new ArrayList<>();

	@XmlElement(name = "stats", required = true)
	private DataStats stats = new DataStats();

	@XmlElement(name = "failure")
	private ActionError failure;

	/**
	 * set or unset error ; will set result to ERROR if error != null
	 * 
	 * @param error
	 */
	public void setFailure(ActionError error) {
		if (error == null) {
			result = ReportConstant.STATUS_OK;
			failure = null;
		} else {
			result = ReportConstant.STATUS_ERROR;
			failure = error;
		}
	}

	public FileInfo findFileInfo(String name) {
		for (FileInfo fileInfo : files) {
			if (fileInfo.getName().equals(name))
				return fileInfo;
		}
		return null;
	}

	public LineInfo findLineInfo(String objectId) {
		for (LineInfo lineInfo : lines) {
			if (lineInfo.getObjectId().equals(objectId))
				return lineInfo;
		}
		return null;
	}

	public JSONObject toJson() throws JSONException {
		JSONObject actionReport = new JSONObject();
		actionReport.put("progression", progression.toJson());
		// "result","zip","files","lines","stats","failure"
		actionReport.put("result", result);
		if (zip != null)
			actionReport.put("zip_file", zip.toJson());
		if (!files.isEmpty()) {
			JSONArray array = new JSONArray();
			actionReport.put("files", array);
			for (FileInfo file : files) {
				array.put(file.toJson());
			}
		}
		if (!lines.isEmpty()) {
			JSONArray array = new JSONArray();
			actionReport.put("lines", array);
			for (LineInfo line : lines) {
				array.put(line.toJson());
			}
		}
		actionReport.put("stats", stats.toJson());
		if (failure != null)
			actionReport.put("failure", failure.toJson());
		JSONObject object = new JSONObject();
		object.put("action_report", actionReport);
		return object;
	}

}
