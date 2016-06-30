package mobi.chouette.exchange.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import mobi.chouette.common.Constant;
import mobi.chouette.exchange.report.ActionReporter.FILE_STATE;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_TYPE;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@XmlRootElement(name = "action_report")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "progression", "result", "zip", "files", "lines", "stats", "failure", "objects", "collections" })
@Data
public class ActionReport2 implements Constant, ProgressionReport, Report {

	@XmlElement(name = "progression", required = true)
	private Progression progression = new Progression();

	@XmlElement(name = "result", required = true)
	private String result = ReportConstant.STATUS_OK;

	@XmlElement(name = "zip_files")
	private List<FileReport> zips = new ArrayList<>();

	@XmlElement(name = "files")
	private List<FileReport> files = new ArrayList<>();

	@XmlElement(name = "failure")
	private ActionError2 failure;

	@XmlElement(name = "objects")
	private Map<ActionReporter.OBJECT_TYPE, ObjectReport> objects = new HashMap<ActionReporter.OBJECT_TYPE, ObjectReport>();

	@XmlElement(name = "collections")
	private Map<ActionReporter.OBJECT_TYPE, ObjectCollectionReport> collections = new HashMap<ActionReporter.OBJECT_TYPE, ObjectCollectionReport>();

	/**
	 * Find file report from name
	 * 
	 * @param name
	 * @return
	 */
	protected FileReport findFileReport(String name) {
		for (FileReport fileReport : files) {
			if (fileReport.getName().equals(name))
				return fileReport;
		}
		return null;
	}

	/**
	 * Find file report from name
	 * 
	 * @param name
	 * @return
	 */
	protected FileReport findZipReport(String name) {
		for (FileReport fileReport : zips) {
			if (fileReport.getName().equals(name))
				return fileReport;
		}
		return null;
	}

	/**
	 * Find file report from name and state
	 * 
	 * @param name
	 * @param state
	 * @return
	 */
	protected FileReport findFileReport(String name, FILE_STATE state) {
		for (FileReport fileReport : files) {
			if (fileReport.getName().equals(name)
					&& (fileReport.getStatus().name().equals(state.name())
							|| FILE_STATE.OK.equals(fileReport.getStatus().name()) || FILE_STATE.OK
								.equals(state.name()))) {
				if (FILE_STATE.OK.equals(fileReport.getStatus().name()))
					fileReport.setStatus(state);
				return fileReport;
			}
		}
		return null;
	}

	/**
	 * set or unset error ; will set result to ERROR if error != null
	 * 
	 * @param error
	 */
	protected void setFailure(ActionError2 error) {
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
		if (!objects.containsKey(object.getType()))
			objects.put(object.getType(), object);
	}

	/**
	 * 
	 * @param collection
	 */
	protected void addObjectCollectionReport(ObjectCollectionReport collection) {
		if (!collections.containsKey(collection.getObjectType()))
			collections.put(collection.getObjectType(), collection);
	}

	/**
	 * 
	 * @param objectReport
	 */
	protected void addObjectReportToSpecificCollection(ObjectReport objectReport) {
		ObjectCollectionReport collection = collections.get(objectReport.getType());
		if (collection == null) {
			collection = new ObjectCollectionReport();
			collection.setObjectType(objectReport.getType());
			addObjectCollectionReport(collection);
		}
		collection.addObjectReport(objectReport);

	}

	/**
	 * 
	 * @param file
	 */
	protected void addFileReport(FileReport file) {
		files.add(file);
	}

	/**
	 * 
	 * @param file
	 */
	protected void addZipReport(FileReport file) {
		zips.add(file);
	}

	public JSONObject toJson() throws JSONException {
		JSONObject actionReport = new JSONObject();
		if (progression != null)
			actionReport.put("progression", progression.toJson());
		// "result","zip","files","lines","stats","failure"
		actionReport.put("result", result);
		if (!zips.isEmpty()) {
			JSONArray array = new JSONArray();
			actionReport.put("zip_files", array);
			for (FileReport file : zips) {
				array.put(file.toJson());
			}
		}

		if (failure != null)
			actionReport.put("failure", failure.toJson());

		if (!files.isEmpty()) {
			JSONArray array = new JSONArray();
			actionReport.put("files", array);
			for (FileReport file : files) {
				array.put(file.toJson());
			}
		}
		if (!objects.isEmpty()) {
			JSONArray array = new JSONArray();
			actionReport.put("objects", array);
			for (ObjectReport object : objects.values()) {
				array.put(object.toJson());
			}
		}

		if (!collections.isEmpty()) {
			JSONArray array = new JSONArray();
			actionReport.put("collections", array);
			for (ObjectCollectionReport collection : collections.values()) {
				array.put(collection.toJson());
			}
		}

		JSONObject object = new JSONObject();
		object.put("action_report", actionReport);
		return object;
	}

	@Override
	public boolean isEmpty() {
		// used to know if report has to be saved
		// Action Report has to be saved any time
		return false;
	}

	public ObjectReport findObjectReport(String objectId, OBJECT_TYPE type) {
		if (collections.containsKey(type))
		{
			ObjectCollectionReport collection = collections.get(type);
			for (ObjectReport objectReport : collection.getObjectReports()) {
				if (objectReport.getObjectId().equals(objectId)) return objectReport;
			}
		}
		else if (objects.containsKey(type))
		{
			return objects.get(type);
		}
		return null;
	}
}