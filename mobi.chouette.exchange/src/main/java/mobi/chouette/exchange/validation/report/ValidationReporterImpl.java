package mobi.chouette.exchange.validation.report;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.model.DataLocation;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;
import mobi.chouette.exchange.validation.report.CheckPointReport.RESULT;

public class ValidationReporterImpl implements ValidationReporter, Constant{

	@Override
	public void addItemToValidationReport(Context context, String prefix,
			String name, int count, String... severities) {
		ValidationReport2 validationReport = (ValidationReport2) context.get(VALIDATION_REPORT);
		for (int i = 1; i <= count; i++) {
			String key = prefix + name + "-" + i;
			if (findCheckPointReportByName(context, key) == null) {
				if (severities[i - 1].equals("W")) {
					validationReport.addCheckPointReport(
							new CheckPointReport(key, CheckPointReport.RESULT.UNCHECK, CheckPointReport.SEVERITY.WARNING));
				} else {
					validationReport.addCheckPointReport(
							new CheckPointReport(key, CheckPointReport.RESULT.UNCHECK, CheckPointReport.SEVERITY.ERROR));
				}
			}
		}
	}
	
	@Override
	public CheckPointReport findCheckPointReportByName(Context context, String name) {
		ValidationReport2 validationReport = (ValidationReport2) context.get(VALIDATION_REPORT);
		for (CheckPointReport checkPointReport : validationReport.getCheckPoints()) {
			if (checkPointReport.getName().equals(name))
				return checkPointReport;
		}
		return null;
	}

	@Override
	public void addCheckPointReportError(Context context, String checkPointName,
			DataLocation location, String value, RESULT result) {
		ValidationReport2 validationReport = (ValidationReport2) context.get(VALIDATION_REPORT);
		CheckPointReport checkPoint = findCheckPointReportByName(context, checkPointName);
		
		if (checkPoint == null) throw new NullPointerException("unknown checkPointName "+checkPointName);
		checkPoint.setState(result);
		
		Location errorLocation = new Location(location.getFilename(), location.getName(), location.getLineNumber(), location.getColumnNumber(), location.getObjectId());
		CheckPointErrorReport newCheckPointError = new CheckPointErrorReport(checkPointName, errorLocation, value);
		validationReport.addCheckPointErrorReport(newCheckPointError);
		checkPoint.addCheckPointError(validationReport.getCheckPointErrors().indexOf(newCheckPointError));
	}

	@Override
	public void addCheckPointReportError(Context context, String checkPointName,
			String detail, DataLocation location, String value, RESULT result) {
		ValidationReport2 validationReport = (ValidationReport2) context.get(VALIDATION_REPORT);
		Location detailLocation = new Location(location.getFilename(), location.getName(), location.getLineNumber(), location.getColumnNumber(), location.getObjectId());
		CheckPointReport checkPoint = findCheckPointReportByName(context, checkPointName);
		
		if (checkPoint == null) throw new NullPointerException("unknown checkPointName "+checkPointName);
		checkPoint.setState(result);
		
		CheckPointErrorReport newCheckPointError = new CheckPointErrorReport(checkPointName+"_"+detail, detailLocation, value);
		validationReport.addCheckPointErrorReport(newCheckPointError);
		checkPoint.addCheckPointError(validationReport.getCheckPointErrors().indexOf(newCheckPointError));
	}

	@Override
	public void addCheckPointReportError(Context context, String checkPointName,
			DataLocation location, String value, String refValue, RESULT result) {
		ValidationReport2 validationReport = (ValidationReport2) context.get(VALIDATION_REPORT);
		Location detailLocation = new Location(location.getFilename(), location.getName(), location.getLineNumber(), location.getColumnNumber(), location.getObjectId());
		CheckPointReport checkPoint = findCheckPointReportByName(context, checkPointName);
		
		if (checkPoint == null) throw new NullPointerException("unknown checkPointName "+checkPointName);
		checkPoint.setState(result);
		
		CheckPointErrorReport newCheckPointError = new CheckPointErrorReport(checkPointName, detailLocation, value, refValue);
		validationReport.addCheckPointErrorReport(newCheckPointError);
		checkPoint.addCheckPointError(validationReport.getCheckPointErrors().indexOf(newCheckPointError));	
	}

	@Override
	public void addCheckPointReportError(Context context, String checkPointName,
			DataLocation[] locations, String value, RESULT result) {
		ValidationReport2 validationReport = (ValidationReport2) context.get(VALIDATION_REPORT);
		
		for (DataLocation location : locations) {
			Location detailLocation = new Location(location.getFilename(), location.getName(), location.getLineNumber(), location.getColumnNumber(), location.getObjectId());	
			CheckPointReport checkPoint = findCheckPointReportByName(context, checkPointName);
			
			if (checkPoint == null) throw new NullPointerException("unknown checkPointName "+checkPointName);
			checkPoint.setState(result);
			
			CheckPointErrorReport newCheckPointError = new CheckPointErrorReport(checkPointName, detailLocation, value);
			validationReport.addCheckPointErrorReport(newCheckPointError);
			checkPoint.addCheckPointError(validationReport.getCheckPointErrors().indexOf(newCheckPointError));
		}
		
	}

	@Override
	public void reportSuccess(Context context, String checkpointName,
			String filenameInfo) {
		ActionReport report = (ActionReport) context.get(REPORT);
		CheckPointReport checkPoint = findCheckPointReportByName(context, checkpointName);
		report.addFileInfo(filenameInfo, FILE_STATE.OK);
		
		if (checkPoint.getState().equals(CheckPointReport.RESULT.UNCHECK))
			checkPoint.setState(CheckPointReport.RESULT.OK);
	}

	@Override
	public void prepareCheckPointReport(Context context, String checkPointKey) {
		CheckPointReport checkPoint = findCheckPointReportByName(context, checkPointKey);
		if (checkPoint != null) {
			if (checkPoint.getState().equals(CheckPointReport.RESULT.UNCHECK))
				updateCheckPointReportState(checkPoint, CheckPointReport.RESULT.OK);
		}
	}

	@Override
	public void updateCheckPointReportState(CheckPointReport checkPoint, RESULT state) {
		checkPoint.setState(state);
	}

	@Override
	public void clearValidationReport(Context context) {
		ValidationReport2 validationReport = (ValidationReport2) context.get(VALIDATION_REPORT);
		validationReport.setResult("NO_VALIDATION");
		validationReport.getCheckPoints().clear();
	}
}
