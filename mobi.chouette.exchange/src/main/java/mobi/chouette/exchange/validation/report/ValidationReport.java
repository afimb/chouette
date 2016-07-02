package mobi.chouette.exchange.validation.report;

import java.io.PrintStream;
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
import mobi.chouette.exchange.report.AbstractReport;
import mobi.chouette.exchange.report.Report;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@ToString
@XmlRootElement(name = "validation_report")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "result", "checkPoints", "checkPointErrors" })
public class ValidationReport extends AbstractReport implements Report {

	@XmlElement(name = "result")
	@Getter
	@Setter
	private String result = "NO_VALIDATION";

	@XmlElement(name = "tests")
	@Getter
	@Setter
	private List<CheckPointReport> checkPoints = new ArrayList<CheckPointReport>();
	
	
	@XmlElement(name = "errors")
	@Getter
	@Setter
	private List<CheckPointErrorReport> checkPointErrors = new ArrayList<CheckPointErrorReport>();

	@XmlTransient
	@Getter
	@Setter
	private boolean maxByFile = true;


	protected void checkResult() {
		result = checkPoints.isEmpty() ? "NO_VALIDATION" : "VALIDATION_PROCEDEED";
	}
	
	public CheckPointReport findCheckPointReportByName(String name) {
		for (CheckPointReport checkPoint : checkPoints) {
			if (checkPoint.getName().equals(name))
				return checkPoint;
		}
		return null;
	}
	
	public CheckPointErrorReport findCheckPointReportErrorByKey(String key) {
		for (CheckPointErrorReport checkPointError : checkPointErrors) {
			if (checkPointError.getKey().equals(key))
				return checkPointError;
		}
		return null;
	}
	
	protected void addCheckPointReport(CheckPointReport checkPoint)
	{
		checkPoint.setMaxByFile(maxByFile);
		checkPoints.add(checkPoint);
	}
	
	protected void addCheckPointErrorReport(CheckPointErrorReport checkPointError)
	{
		checkPointErrors.add(checkPointError);
	}

	protected void addAllCheckPoints(Collection<CheckPointReport> list)
	{
		for (CheckPointReport checkPoint : checkPoints) {
			checkPoint.setMaxByFile(maxByFile);
		}
		checkPoints.addAll(list);
	}

	public JSONObject toJson() throws JSONException {
		result = checkPoints.isEmpty() ? "NO_VALIDATION" : "VALIDATION_PROCEDEED";
		JSONObject validationReport = new JSONObject();
		validationReport.put("result", result);
		if (!checkPoints.isEmpty()) {
			JSONArray tests = new JSONArray();
			for (CheckPointReport checkPoint : checkPoints) {
				tests.put(checkPoint.toJson());
			}
			validationReport.put("check_points", tests);
		}
		if (!checkPointErrors.isEmpty()) {
			JSONArray tests = new JSONArray();
			for (CheckPointErrorReport checkPointError : checkPointErrors) {
				tests.put(checkPointError.toJson());
			}
			validationReport.put("errors", tests);
		}
		JSONObject object = new JSONObject();
		object.put("validation_report", validationReport);
		return object;
	}
	
	public void print(PrintStream out)
	{
		print(out,1,true);
	}
	
	public void print(PrintStream out, int level, boolean first)
	{
		StringBuilder ret = new StringBuilder();
		level = 1;
		result = checkPoints.isEmpty() ? "NO_VALIDATION" : "VALIDATION_PROCEDEED";
		out.print("{\"validation_report\": {");
		out.print(toJsonString(ret,level,"result", result, true));
		if (!checkPoints.isEmpty())
			printArray(out,ret, level+1,"check_points",checkPoints, false);
		if (!checkPointErrors.isEmpty())
			printArray(out,ret, level+1,"errors",checkPointErrors, false);
		out.println("\n}}");
	}
	@Override
	public boolean isEmpty() {
		// used to know if report has to be saved
		// Validation Report has to be saved if checkPoints were defined
		
		return checkPoints.isEmpty();
	}
}
