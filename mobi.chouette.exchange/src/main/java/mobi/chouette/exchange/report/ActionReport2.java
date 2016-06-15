package mobi.chouette.exchange.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@XmlRootElement(name = "action_report")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "progression", "result", "zip", "files", "lines", "stats", "failure", "objects", "collections" })
@Data
public class ActionReport2 {

	@XmlElement(name = "progression", required = true)
	private Progression progression = new Progression();

	@XmlElement(name = "result", required = true)
	private String result = ReportConstant.STATUS_OK;

	@XmlElement(name = "zip_file")
	private FileReport zip;

	@XmlElement(name = "files")
	private List<FileReport> files = new ArrayList<>();

	@XmlElement(name = "failure")
	private ActionError failure;
	
	
	@XmlElement(name = "objects")
	private List<ObjectReport> objects = new ArrayList<ObjectReport>();
	
	@XmlElement(name = "collections")
	private List<ObjectCollectionReport> collections = new ArrayList<ObjectCollectionReport>();

	/**
	 * set or unset error ; will set result to ERROR if error != null
	 * 
	 * @param error
	 */
	protected void setFailure(ActionError error) {
		if (error == null) {
			result = ReportConstant.STATUS_OK;
			failure = null;
		} else {
			result = ReportConstant.STATUS_ERROR;
			failure = error;
		}
	}
	
	/**
	 * 
	 * @param object
	 */
	protected void addObjectReport(ObjectReport object) {
		objects.add(object);	
	}
	
	/**
	 * 
	 * @param collection
	 */
	protected void addObjectCollectionReport(ObjectCollectionReport collection) {
		collections.add(collection);	
	}

	/**
	 * 
	 * @param file
	 */
	protected void addFileReport(FileReport file) {
		files.add(file);
	}
	
	
	public JSONObject toJson() throws JSONException {
		JSONObject actionReport = new JSONObject();
		actionReport.put("progression", progression.toJson());
		// "result","zip","files","lines","stats","failure"
		actionReport.put("result", result);
		if (zip != null)
			actionReport.put("zip_file", zip.toJson());
	
		if (failure != null)
			actionReport.put("failure", failure.toJson());
		
		if(!files.isEmpty()) {
			JSONArray array = new JSONArray();
			actionReport.put("files", array);
			for (FileReport file : files) {
				array.put(file.toJson());
			}
		}
		if(!objects.isEmpty()) {
			JSONArray array = new JSONArray();
			actionReport.put("objects", array);
			for (ObjectReport object : objects) {
				array.put(object.toJson());
			}
		}
		
		if(!collections.isEmpty()) {
			JSONArray array = new JSONArray();
			actionReport.put("collections", array);
			for (ObjectCollectionReport collection : collections) {
				array.put(collection.toJson());
			}
		}
		
		JSONObject object = new JSONObject();
		object.put("action_report", actionReport);
		return object;
	}
}
