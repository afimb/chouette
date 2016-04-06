package mobi.chouette.exchange.validation.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.exchange.validation.report.CheckPoint.RESULT;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@ToString
@XmlRootElement(name = "validation_report")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "result", "checkPoints" })
public class ValidationReport {

	@XmlElement(name = "result")
	@Getter
	@Setter
	private String result = "NO_VALIDATION";

	@XmlElement(name = "tests")
	 @Getter
	 @Setter
	private List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();

	@XmlTransient
	@Getter
	@Setter
	private boolean maxByFile = true;


	public CheckPoint findCheckPointByName(String name) {
		for (CheckPoint checkPoint : checkPoints) {
			if (checkPoint.getName().equals(name))
				return checkPoint;
		}
		return null;
	}

	public void checkResult() {
		result = checkPoints.isEmpty() ? "NO_VALIDATION" : "VALIDATION_PROCEDEED";
	}
	
	public void addCheckPoint(CheckPoint checkPoint)
	{
		checkPoint.setMaxByFile(maxByFile);
		checkPoints.add(checkPoint);
	}

	public void addAllCheckPoints(Collection<CheckPoint> list)
	{
		for (CheckPoint checkPoint : checkPoints) {
			checkPoint.setMaxByFile(maxByFile);
		}
		checkPoints.addAll(list);
	}

	public JSONObject toJson() throws JSONException {
		JSONObject validationReport = new JSONObject();
		validationReport.put("result", result);
		if (!checkPoints.isEmpty()) {
			JSONArray tests = new JSONArray();
			for (CheckPoint checkPoint : checkPoints) {
				tests.put(checkPoint.toJson());
			}
			validationReport.put("tests", tests);
		}
		JSONObject object = new JSONObject();
		object.put("validation_report", validationReport);
		return object;
	}

	public void addDetail(String checkPointName, Location location, String value, RESULT result) {
		CheckPoint checkPoint = findCheckPointByName(checkPointName);
		if (checkPoint == null) throw new NullPointerException("unknown checkPointName "+checkPointName);
		checkPoint.setState(result);
		checkPoint.addDetail(new Detail(checkPointName, location, value));
	}
	
	public void addDetail(String checkPointName, String detail, Location location, String value, RESULT result) {
		CheckPoint checkPoint = findCheckPointByName(checkPointName);
		if (checkPoint == null) throw new NullPointerException("unknown checkPointName "+checkPointName);
		checkPoint.setState(result);
		checkPoint.addDetail(new Detail(checkPointName+"_"+detail, location, value));
	}

	public void addDetail(String checkPointName, Location location, String value, String refValue, RESULT result) {
		CheckPoint checkPoint = findCheckPointByName(checkPointName);
		if (checkPoint == null) throw new NullPointerException("unknown checkPointName "+checkPointName);
		checkPoint.setState(result);
		checkPoint.addDetail(new Detail(checkPointName, location, value,refValue));
	}


	public void addDetail(String checkPointName, Location[] locations, String value, RESULT result) {
		CheckPoint checkPoint = findCheckPointByName(checkPointName);
		if (checkPoint == null) throw new NullPointerException("unknown checkPointName "+checkPointName);
		checkPoint.setState(result);
		for (Location location : locations)
			checkPoint.addDetail(new Detail(checkPointName, location, value));
	}
}
