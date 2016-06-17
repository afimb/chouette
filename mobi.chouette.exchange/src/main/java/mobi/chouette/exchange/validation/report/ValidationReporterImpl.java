package mobi.chouette.exchange.validation.report;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.model.DataLocation;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;

public class ValidationReporterImpl implements ValidationReporter, Constant{
	
	@Override
	public void addItemToValidationReport(Context context, String prefix,
			String name, int count, String... severities) {
		ValidationReport2 validationReport = (ValidationReport2) context.get(VALIDATION_REPORT);
		for (int i = 1; i <= count; i++) {
			String key = prefix + name + "-" + i;
			if (validationReport.findCheckPointReportByName(key) == null) {
				if (severities[i - 1].equals("W")) {
					validationReport.addCheckPointReport(
							new CheckPointReport(key, RESULT.UNCHECK, CheckPointReport.SEVERITY.WARNING));
				} else {
					validationReport.addCheckPointReport(
							new CheckPointReport(key, RESULT.UNCHECK, CheckPointReport.SEVERITY.ERROR));
				}
			}
		}
	}
	

	@Override
	public void addCheckPointReportError(Context context, String checkPointName,
			DataLocation location, String value, RESULT result) {
		addCheckPointReportError(context, checkPointName, null, location, value, null, result);
	}

	@Override
	public void addCheckPointReportError(Context context, String checkPointName,
			String detail, DataLocation location, String value, RESULT result) {
		addCheckPointReportError(context, checkPointName, detail, location, value, null, result);
	}

	@Override
	public void addCheckPointReportError(Context context, String checkPointName, String detail,
			DataLocation location, String value, String refValue, RESULT result) {
		ValidationReport2 validationReport = (ValidationReport2) context.get(VALIDATION_REPORT);
		Location detailLocation = new Location(location.getFilename(), location.getName(), location.getLineNumber(), location.getColumnNumber(), location.getObjectId());
		CheckPointReport checkPoint = validationReport.findCheckPointReportByName(checkPointName);
		
		if (checkPoint == null) throw new NullPointerException("unknown checkPointName "+checkPointName);
		checkPoint.setState(result);
		CheckPointErrorReport newCheckPointError;
		
		if(detail != null)
			newCheckPointError = new CheckPointErrorReport(checkPointName+"_"+detail, detailLocation, value);
		else
			newCheckPointError = new CheckPointErrorReport(checkPointName, detailLocation, value, refValue);
		
		validationReport.addCheckPointErrorReport(newCheckPointError);
		checkPoint.addCheckPointError(validationReport.getCheckPointErrors().indexOf(newCheckPointError));	
	}

	@Override
	public void addCheckPointReportError(Context context, String checkPointName,
			DataLocation[] locations, String value, RESULT result) {
		ValidationReport2 validationReport = (ValidationReport2) context.get(VALIDATION_REPORT);
		
		for (DataLocation location : locations) {
			Location detailLocation = new Location(location.getFilename(), location.getName(), location.getLineNumber(), location.getColumnNumber(), location.getObjectId());	
			CheckPointReport checkPoint = validationReport.findCheckPointReportByName(checkPointName);
			
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
		ValidationReport2 validationReport = (ValidationReport2) context.get(VALIDATION_REPORT);
		CheckPointReport checkPoint = validationReport.findCheckPointReportByName(checkpointName);
		report.addFileInfo(filenameInfo, FILE_STATE.OK);
		
		if (checkPoint.getState().equals(RESULT.UNCHECK))
			checkPoint.setState(RESULT.OK);
	}

	@Override
	public void prepareCheckPointReport(Context context, String checkPointName) {
		ValidationReport2 validationReport = (ValidationReport2) context.get(VALIDATION_REPORT);
		CheckPointReport checkPoint = validationReport.findCheckPointReportByName(checkPointName);
		if (checkPoint != null) {
			if (checkPoint.getState().equals(RESULT.UNCHECK))
				updateCheckPointReportState(context, checkPointName, RESULT.OK);
		}
	}

	@Override
	public void updateCheckPointReportState(Context context, String checkPointName, RESULT state) {
		ValidationReport2 validationReport = (ValidationReport2) context.get(VALIDATION_REPORT);
		CheckPointReport checkPoint = validationReport.findCheckPointReportByName(checkPointName);
		checkPoint.setState(state);
	}

	@Override
	public void clearValidationReport(Context context) {
		ValidationReport2 validationReport = (ValidationReport2) context.get(VALIDATION_REPORT);
		validationReport.setResult("NO_VALIDATION");
		validationReport.getCheckPoints().clear();
	}
}
