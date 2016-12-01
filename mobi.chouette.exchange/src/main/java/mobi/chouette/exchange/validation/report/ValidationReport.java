package mobi.chouette.exchange.validation.report;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.exchange.report.AbstractReport;
import mobi.chouette.exchange.report.Report;
import mobi.chouette.exchange.validation.report.CheckPointReport.SEVERITY;
import mobi.chouette.exchange.validation.report.ValidationReporter.VALIDATION_RESULT;

@ToString
public class ValidationReport extends AbstractReport implements Report {

	@Getter
	@Setter
	private VALIDATION_RESULT result = VALIDATION_RESULT.NO_PROCESSING;

	@Getter
	@Setter
	private List<CheckPointReport> checkPoints = new ArrayList<CheckPointReport>();

	@Getter
	@Setter
	private List<CheckPointErrorReport> checkPointErrors = new ArrayList<CheckPointErrorReport>();

	@Getter
	@Setter
	private boolean maxByFile = true;
	
	@Getter
	@Setter
	private Date date = new Date(0);


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

	protected void addCheckPointReport(CheckPointReport checkPoint) {
		checkPoint.setMaxByFile(maxByFile);
		checkPoints.add(checkPoint);
		if (result.ordinal() < VALIDATION_RESULT.OK.ordinal())
			result = VALIDATION_RESULT.OK;
	}

	protected void addCheckPointErrorReport(CheckPointErrorReport checkPointError) {
		checkPointErrors.add(checkPointError);
		if (result != VALIDATION_RESULT.ERROR) {
			CheckPointReport checkPoint = findCheckPointReportByName(checkPointError.getTestId());
			if (checkPoint.getSeverity().equals(SEVERITY.WARNING)) {
				if (result.ordinal() < VALIDATION_RESULT.WARNING.ordinal())
					result = VALIDATION_RESULT.WARNING;
			} else {
				result = VALIDATION_RESULT.ERROR;
			}
		}
	}

	protected void addAllCheckPoints(Collection<CheckPointReport> list) {
		for (CheckPointReport checkPoint : checkPoints) {
			checkPoint.setMaxByFile(maxByFile);
		}
		checkPoints.addAll(list);
		if (result.ordinal() < VALIDATION_RESULT.OK.ordinal())
			result = VALIDATION_RESULT.OK;
	}


	@Override
	public void print(PrintStream out) {
		print(out, new StringBuilder(), 1, true);
	}

	@Override
	public void print(PrintStream out, StringBuilder ret , int level, boolean first) {
		ret.setLength(0);
		level = 1;
		out.print("{\"validation_report\": {");
		out.print(toJsonString(ret, level, "result", result, true));
		if (!checkPoints.isEmpty())
			printArray(out, ret, level + 1, "check_points", checkPoints, false);
		if (!checkPointErrors.isEmpty())
			printArray(out, ret, level + 1, "errors", checkPointErrors, false);
		out.println("\n}}");
	}

	@Override
	public boolean isEmpty() {
		// used to know if report has to be saved
		// Validation Report has to be saved if checkPoints were defined

		return checkPoints.isEmpty();
	}
}
