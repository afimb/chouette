package mobi.chouette.exchange.validation.report;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_TYPE;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.exchange.validation.report.CheckPointReport.SEVERITY;
import mobi.chouette.exchange.validation.report.DataLocation.Path;

@Log4j
public class ValidationReporterImpl implements ValidationReporter, Constant {

	@Override
	public void addItemToValidationReport(Context context, String key, String severity) {
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
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
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
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
	public void addCheckPointReportError(Context context, String checkPointName, String detail, DataLocation location) {
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
	public void addCheckPointReportError(Context context, String checkPointName, DataLocation location, String value,
			String refValue) {
		addCheckPointReportError(context, checkPointName, null, location, value, refValue);

	}

	@Override
	public void addCheckPointReportError(Context context, String checkPointName, String detail, DataLocation location,
			String value, String refValue) {
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		Location detailLocation = null;
		if (location != null)
			detailLocation = new Location(location);

		CheckPointReport checkPoint = validationReport.findCheckPointReportByName(checkPointName);

		if (checkPoint == null)
			throw new NullPointerException("unknown checkPointName " + checkPointName);
		checkPoint.setState(RESULT.NOK);
		CheckPointErrorReport newCheckPointError;

		if (detail != null)
			newCheckPointError = new CheckPointErrorReport(checkPointName, checkPointName + "_" + detail, detailLocation, value,
					refValue);
		else
			newCheckPointError = new CheckPointErrorReport(checkPointName, checkPointName, detailLocation, value, refValue);

		int index = validationReport.getCheckPointErrors().size();
		boolean checkPointAdded = checkPoint.addCheckPointError(index);

		boolean reportAdded = addReferencesToActionReport(context, location, index, checkPoint.getSeverity());

		if (checkPointAdded || reportAdded)
			validationReport.addCheckPointErrorReport(newCheckPointError);
	}

	@Override
	public void addCheckPointReportError(Context context, String checkPointName, DataLocation location, String value,
			String refValue, DataLocation... targetLocations) {
		addCheckPointReportError(context, checkPointName, null, location, value, refValue, targetLocations);
	}

	@Override
	public void addCheckPointReportError(Context context, String checkPointName, String detail, DataLocation location,
			String value, String refValue, DataLocation... targetLocations) {
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		Location detailLocation = null;
		if (location != null)
			detailLocation = new Location(location);

		CheckPointReport checkPoint = validationReport.findCheckPointReportByName(checkPointName);

		if (checkPoint == null)
			throw new NullPointerException("unknown checkPointName " + checkPointName);
		checkPoint.setState(RESULT.NOK);
		CheckPointErrorReport newCheckPointError;

		if (detail != null)
			newCheckPointError = new CheckPointErrorReport(checkPointName,checkPointName + "_" + detail, detailLocation, value,
					refValue);
		else
			newCheckPointError = new CheckPointErrorReport(checkPointName,checkPointName, detailLocation, value, refValue);

		if (targetLocations.length > 0) {
			for (DataLocation dataLocation : targetLocations) {
				Location targetLocation = new Location(dataLocation);
				newCheckPointError.getTargets().add(targetLocation);
			}
		}

		int index = validationReport.getCheckPointErrors().size();
		boolean checkPointAdded = checkPoint.addCheckPointError(index);

		boolean reportAdded = addReferencesToActionReport(context, location, index, checkPoint.getSeverity());

		if (checkPointAdded || reportAdded)
			validationReport.addCheckPointErrorReport(newCheckPointError);
	}

	private boolean addReferencesToActionReport(Context context, DataLocation location, int code, SEVERITY severity) {
		if (location == null)
			return false;
		boolean ret = false;
		ActionReporter reporter = ActionReporter.Factory.getInstance();
		if (location.getFilename() != null) {
			if (reporter.addValidationErrorToFileReport(context, location.getFilename(), code, severity))
				ret = true;
		}
		if (!location.getPath().isEmpty()) {
			DataLocation.Path object = location.getPath().get(location.getPath().size() - 1);
			OBJECT_TYPE type = getType(object);
			if (type != null) {
				if (reporter.addValidationErrorToObjectReport(context, object.getObjectId(), type, code, severity))
					ret = true;
			}
		}
		return ret;
	}

	private OBJECT_TYPE getType(Path object) {
		String name = object.getObjectClass().replaceAll("(.)(\\p{Upper})", "$1_$2").toUpperCase();
		try {
			return OBJECT_TYPE.valueOf(name);
		} catch (Exception ex) {
			log.error("unknown type " + object.getObjectClass());
			return null;
		}
	}

	protected static String toUnderscore(String camelcase) {

		return camelcase.replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
	}

//	private OBJECT_TYPE getType(NeptuneIdentifiedObject object) {
//		String name = object.getClass().getSimpleName();
//		name = name.replaceAll("(.)(\\p{Upper})", "$1_$2").toUpperCase();
//		try {
//			return OBJECT_TYPE.valueOf(name);
//		} catch (Exception ex) {
//			log.error("unknown type " + object.getClass().getSimpleName());
//			return null;
//		}
//	}

	@Override
	public void addCheckPointReportError(Context context, String checkPointName, DataLocation[] locations, String value) {
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);

		for (DataLocation location : locations) {
			Location detailLocation = new Location(location);
			CheckPointReport checkPoint = validationReport.findCheckPointReportByName(checkPointName);

			if (checkPoint == null)
				throw new NullPointerException("unknown checkPointName " + checkPointName);
			checkPoint.setState(RESULT.NOK);

			CheckPointErrorReport newCheckPointError = new CheckPointErrorReport(checkPointName, checkPointName, detailLocation, value);
			int index = validationReport.getCheckPointErrors().size();
			boolean checkPointAdded = checkPoint.addCheckPointError(index);

			boolean reportAdded = addReferencesToActionReport(context, location, index, checkPoint.getSeverity());

			if (checkPointAdded || reportAdded)
				validationReport.addCheckPointErrorReport(newCheckPointError);
		}

	}

	@Override
	public void reportSuccess(Context context, String checkpointName, String filenameInfo) {
		ActionReporter reporter = ActionReporter.Factory.getInstance();
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		CheckPointReport checkPoint = validationReport.findCheckPointReportByName(checkpointName);
		reporter.addFileReport(context, filenameInfo, IO_TYPE.INPUT);

		if (checkPoint == null)
			throw new IllegalArgumentException("Missing cheeckpoint " + checkpointName);

		if (checkPoint.getState().equals(RESULT.UNCHECK))
			checkPoint.setState(RESULT.OK);
	}

	@Override
	public void reportSuccess(Context context, String checkpointName) {
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		CheckPointReport checkPoint = validationReport.findCheckPointReportByName(checkpointName);

		if (checkPoint == null)
			throw new IllegalArgumentException("Missing checkpoint " + checkpointName);

		if (checkPoint.getState().equals(RESULT.UNCHECK))
			checkPoint.setState(RESULT.OK);
	}

	@Override
	public void prepareCheckPointReport(Context context, String checkPointName) {
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		CheckPointReport checkPoint = validationReport.findCheckPointReportByName(checkPointName);
		if (checkPoint != null) {
			if (checkPoint.getState().equals(RESULT.UNCHECK))
				updateCheckPointReportState(context, checkPointName, RESULT.OK);
		}
	}

	@Override
	public void updateCheckPointReportState(Context context, String checkPointName, RESULT state) {
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		CheckPointReport checkPoint = validationReport.findCheckPointReportByName(checkPointName);
		checkPoint.setState(state);
	}

	@Override
	public void clearValidationReport(Context context) {
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		validationReport.setResult(VALIDATION_RESULT.NO_PROCESSING);
		validationReport.getCheckPoints().clear();
		validationReport.getCheckPointErrors().clear();
	}

	@Override
	public void updateCheckPointReportSeverity(Context context, String checkPointName, SEVERITY severity) {
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		CheckPointReport checkPoint = validationReport.findCheckPointReportByName(checkPointName);
		if (checkPoint != null) {
			if (checkPoint.getSeverity().ordinal() < severity.ordinal())
				checkPoint.setSeverity(severity);
		}

	}


	@Override
	public boolean checkIfCheckPointExists(Context context, String checkPointName) {
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		CheckPointReport checkPoint = validationReport.findCheckPointReportByName(checkPointName);

		return (checkPoint != null);
	}


}
