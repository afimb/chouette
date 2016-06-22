package mobi.chouette.exchange.validation.report;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.model.DataLocation;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;
import mobi.chouette.exchange.validation.report.CheckPoint.RESULT;
import mobi.chouette.exchange.validation.report.CheckPointReport.SEVERITY;

public class ValidationReporterImpl implements ValidationReporter, Constant {
	
	@Override
	public void addItemToValidationReport(Context context, String key, String severity) {
		ValidationReport2 validationReport = (ValidationReport2) context.get(VALIDATION_REPORT);
		CheckPointReport checkPoint = validationReport.findCheckPointReportByName(key);
		if (checkPoint == null) {
			if (severity.equals("W")) {
				validationReport.addCheckPointReport(new CheckPointReport(key, RESULT.UNCHECK,
						CheckPointReport.SEVERITY.WARNING));
			} else {
				validationReport.addCheckPointReport(new CheckPointReport(key, RESULT.UNCHECK,
						CheckPointReport.SEVERITY.ERROR));
			}
		}
	}

	@Override
	public void addItemToValidationReport(Context context, String prefix, String name, int count, String... severities) {
		ValidationReport2 validationReport = (ValidationReport2) context.get(VALIDATION_REPORT);
		for (int i = 1; i <= count; i++) {
			String key = prefix + name + "-" + i;
			if (validationReport.findCheckPointReportByName(key) == null) {
				if (severities[i - 1].equals("W")) {
					validationReport.addCheckPointReport(new CheckPointReport(key, RESULT.UNCHECK,
							CheckPointReport.SEVERITY.WARNING));
				} else {
					validationReport.addCheckPointReport(new CheckPointReport(key, RESULT.UNCHECK,
							CheckPointReport.SEVERITY.ERROR));
				}
			}
		}
	}

	@Override
	public void addCheckPointReportError(Context context, String checkPointName,String detail, DataLocation location) {
		addCheckPointReportError(context, checkPointName, detail, location, null, null);
	}
	
	@Override
	public void addCheckPointReportError(Context context, String checkPointName, DataLocation location) {
		addCheckPointReportError(context, checkPointName, null, location, null, null);
	}
	
	@Override
	public void addCheckPointReportError(Context context, String checkPointName, DataLocation location, String value) {
		addCheckPointReportError(context, checkPointName, null, location, value, null);
	}

	@Override
	public void addCheckPointReportError(Context context, String checkPointName, String detail, DataLocation location,
			String value) {
		addCheckPointReportError(context, checkPointName, detail, location, value, null);
	}

	@Override
	public void addCheckPointReportError(Context context, String checkPointName, DataLocation location,
			String value, String refValue) {
		addCheckPointReportError(context, checkPointName, null, location, value, null);

	}
	@Override
	public void addCheckPointReportError(Context context, String checkPointName, String detail, DataLocation location,
			String value, String refValue) {
		ValidationReport2 validationReport = (ValidationReport2) context.get(VALIDATION_REPORT);
		Location detailLocation = null;
		if (location != null)
			detailLocation = new Location(location.getFilename(), location.getName(), location.getLineNumber(),
					location.getColumnNumber(), location.getObjectId());

		CheckPointReport checkPoint = validationReport.findCheckPointReportByName(checkPointName);

		if (checkPoint == null)
			throw new NullPointerException("unknown checkPointName " + checkPointName);
		checkPoint.setState(RESULT.NOK);
		CheckPointErrorReport newCheckPointError;

		if (detail != null )
			newCheckPointError = new CheckPointErrorReport(checkPointName + "_" + detail, detailLocation, value);
		else
			newCheckPointError = new CheckPointErrorReport(checkPointName, detailLocation, value, refValue);

		validationReport.addCheckPointErrorReport(newCheckPointError);
		checkPoint.addCheckPointError(validationReport.getCheckPointErrors().indexOf(newCheckPointError));
	}
	
	@Override
	public void addCheckPointReportError(Context context, String checkPointName, DataLocation location,
			String value, String refValue, DataLocation... targetLocations) {
		addCheckPointReportError(context, checkPointName, null, location, value, null,targetLocations);
	}
	
	@Override
	public void addCheckPointReportError(Context context, String checkPointName, String detail, DataLocation location,
			String value, String refValue, DataLocation... targetLocations) {
		ValidationReport2 validationReport = (ValidationReport2) context.get(VALIDATION_REPORT);
		Location detailLocation = null;
		if (location != null)
			detailLocation = new Location(location.getFilename(), location.getName(), location.getLineNumber(),
					location.getColumnNumber(), location.getObjectId());

		CheckPointReport checkPoint = validationReport.findCheckPointReportByName(checkPointName);

		if (checkPoint == null)
			throw new NullPointerException("unknown checkPointName " + checkPointName);
		checkPoint.setState(RESULT.NOK);
		CheckPointErrorReport newCheckPointError;

		if (detail != null)
			newCheckPointError = new CheckPointErrorReport(checkPointName + "_" + detail, detailLocation, value);
		else
			newCheckPointError = new CheckPointErrorReport(checkPointName, detailLocation, value, refValue);
		
		if (targetLocations.length > 0)
		{
			for (DataLocation dataLocation : targetLocations) {
				Location targetLocation = new Location(dataLocation.getFilename(), dataLocation.getName(), dataLocation.getLineNumber(),
						dataLocation.getColumnNumber(), dataLocation.getObjectId());
				newCheckPointError.getTargets().add(targetLocation);
			}
		}

		validationReport.addCheckPointErrorReport(newCheckPointError);
		checkPoint.addCheckPointError(validationReport.getCheckPointErrors().indexOf(newCheckPointError));
	}

	@Override
	public void addCheckPointReportError(Context context, String checkPointName, DataLocation[] locations, String value) {
		ValidationReport2 validationReport = (ValidationReport2) context.get(VALIDATION_REPORT);

		for (DataLocation location : locations) {
			Location detailLocation = new Location(location.getFilename(), location.getName(),
					location.getLineNumber(), location.getColumnNumber(), location.getObjectId());
			CheckPointReport checkPoint = validationReport.findCheckPointReportByName(checkPointName);

			if (checkPoint == null)
				throw new NullPointerException("unknown checkPointName " + checkPointName);
			checkPoint.setState(RESULT.NOK);

			CheckPointErrorReport newCheckPointError = new CheckPointErrorReport(checkPointName, detailLocation, value);
			validationReport.addCheckPointErrorReport(newCheckPointError);
			checkPoint.addCheckPointError(validationReport.getCheckPointErrors().indexOf(newCheckPointError));
		}

	}

	@Override
	public void reportSuccess(Context context, String checkpointName, String filenameInfo) {
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

	@Override
	public void updateCheckPointReportSeverity(Context context, String checkPointName,
			SEVERITY severity) {
		ValidationReport2 validationReport = (ValidationReport2) context.get(VALIDATION_REPORT);
		CheckPointReport checkPoint = validationReport.findCheckPointReportByName(checkPointName);
		if (checkPoint != null) {
			if (checkPoint.getSeverity().ordinal() < severity.ordinal())
				checkPoint.setSeverity(severity);
		}
		
	}

	@Override
	public boolean checkValidationReportValidity(Context context) {
		ValidationReport2 validationReport = (ValidationReport2) context.get(VALIDATION_REPORT);
		for (CheckPointReport checkPoint : validationReport.getCheckPoints()) {
			if (checkPoint.getSeverity().equals(SEVERITY.ERROR) && checkPoint.getState().equals(RESULT.NOK)) {
				return ERROR;
			}
		}
		return SUCCESS;
	}

	@Override
	public boolean checkIfCheckPointExists(Context context, String checkPointName) {
		ValidationReport2 validationReport = (ValidationReport2) context.get(VALIDATION_REPORT);
		CheckPointReport checkPoint = validationReport.findCheckPointReportByName(checkPointName);
		
		return (checkPoint != null);
	}

}
