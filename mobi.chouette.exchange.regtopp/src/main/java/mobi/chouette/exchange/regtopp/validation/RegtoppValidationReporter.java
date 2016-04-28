package mobi.chouette.exchange.regtopp.validation;

import static mobi.chouette.common.Constant.MAIN_VALIDATION_REPORT;
import static mobi.chouette.common.Constant.REPORT;
import static mobi.chouette.exchange.regtopp.validation.Constant.REGTOPP_FILE;
import static mobi.chouette.exchange.regtopp.validation.Constant.REGTOPP_INVALID_FIELD_VALUE;
import static mobi.chouette.exchange.regtopp.validation.Constant.REGTOPP_INVALID_MANDATORY_ID_REFERENCE;
import static mobi.chouette.exchange.regtopp.validation.Constant.REGTOPP_INVALID_OPTIONAL_ID_REFERENCE;

import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileError;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.exchange.validation.report.ValidationReport;

@Log4j
public class RegtoppValidationReporter {

	@Getter
	private Set<RegtoppException> exceptions = new RegtoppExceptionsHashSet<RegtoppException>();

	public void dispose() {
		exceptions.clear();
		exceptions = null;
	}

	public void throwUnknownError(Context context, Exception ex, String filenameInfo) throws Exception {
		ActionReport report = (ActionReport) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);
		String name = filenameInfo;
		String checkPointName = checkPointName(name, RegtoppException.ERROR.SYSTEM);

		if (filenameInfo != null && filenameInfo.indexOf('.') > 0) {
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR, new FileError(FileError.CODE.FILE_NOT_FOUND,
					"A problem occured while reading the file \"" + filenameInfo + "\" (" + checkPointName + ") : " + ex.getMessage()));
			validationReport.addDetail(checkPointName, new Location(filenameInfo), ex.getMessage(), CheckPoint.RESULT.NOK);
			String message = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getName();
			log.error(ex, ex);
			throw new Exception("A problem occured while reading the file \"" + filenameInfo + "\" : " + message);
		}
	}

	public void reportErrors(Context context, Set<RegtoppException> errors, String filename) throws Exception {
		for (RegtoppException error : errors) {
			reportError(context, error, filename);
		}
	}

	public void reportError(Context context, RegtoppException ex, String filenameInfo) throws Exception {
		if (!exceptions.add(ex))
			return;
		ActionReport actionReport = (ActionReport) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);
		String checkPointName = "";
		String fieldName = "";

		// TODO improve messages below

		switch (ex.getError()) {
		case INVALID_MANDATORY_ID_REFERENCE:
			checkPointName = checkPointName(filenameInfo, RegtoppException.ERROR.INVALID_MANDATORY_ID_REFERENCE);
			addError(actionReport, filenameInfo,
					new FileError(FileError.CODE.INVALID_FORMAT, "Unreferenced " + ex.getField() + " (rule " + checkPointName + ")"));
			actionReport.addFileInfo(filenameInfo, FILE_STATE.ERROR);
			validationReport.addDetail(checkPointName, new Location(filenameInfo, fieldName, ex.getLineNumber()), ex.getValue(), ex.getField(),
					CheckPoint.RESULT.UNCHECK);
			break;
		case INVALID_OPTIONAL_ID_REFERENCE:
			checkPointName = checkPointName(filenameInfo, RegtoppException.ERROR.INVALID_OPTIONAL_ID_REFERENCE);

			addError(actionReport, filenameInfo,
					new FileError(FileError.CODE.INVALID_FORMAT, "Unreferenced " + ex.getField() + " (rule " + checkPointName + ")"));
			actionReport.addFileInfo(filenameInfo, FILE_STATE.ERROR);
			validationReport.addDetail(checkPointName, new Location(filenameInfo, fieldName, ex.getLineNumber()), ex.getValue(), ex.getField(),
					CheckPoint.RESULT.UNCHECK);
			break;
		case INVALID_FIELD_VALUE:
			checkPointName = checkPointName(filenameInfo, RegtoppException.ERROR.INVALID_FIELD_VALUE);
			addError(actionReport, filenameInfo,
					new FileError(FileError.CODE.INVALID_FORMAT, "Invalid value in field " + ex.getField() + " (rule " + checkPointName + ")"));
			actionReport.addFileInfo(filenameInfo, FILE_STATE.ERROR);
			validationReport.addDetail(checkPointName, new Location(filenameInfo, fieldName, ex.getLineNumber()), ex.getValue(), ex.getField(),
					CheckPoint.RESULT.UNCHECK);
			break;
		case SYSTEM:
			checkPointName = checkPointName(filenameInfo, RegtoppException.ERROR.SYSTEM);
			addError(actionReport, filenameInfo, new FileError(FileError.CODE.INVALID_FORMAT,
					"The first line in file \"" + filenameInfo + "\" must comply with CSV (rule " + checkPointName + ")"));
			actionReport.addFileInfo(filenameInfo, FILE_STATE.ERROR);
			validationReport.addDetail(checkPointName, new Location(filenameInfo, 1, -1), filenameInfo, CheckPoint.RESULT.NOK);
			break;
		default:
			break;
		}
	}

	private String checkPointName(String name, mobi.chouette.exchange.regtopp.validation.RegtoppException.ERROR errorName) {
		// name = capitalize(name);
		switch (errorName) {
		case SYSTEM:
			return REGTOPP_FILE;
		case INVALID_MANDATORY_ID_REFERENCE:
			return REGTOPP_INVALID_MANDATORY_ID_REFERENCE;
		case INVALID_OPTIONAL_ID_REFERENCE:
			return REGTOPP_INVALID_OPTIONAL_ID_REFERENCE;
		case INVALID_FIELD_VALUE:
			return REGTOPP_INVALID_FIELD_VALUE;
		default:
			return null;
		}
	}

	public void reportSuccess(Context context, String checkpointName, String filenameInfo) {
		ActionReport report = (ActionReport) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);

		report.addFileInfo(filenameInfo, FILE_STATE.OK);
		if (validationReport.findCheckPointByName(checkpointName).getState() == CheckPoint.RESULT.UNCHECK)
			validationReport.findCheckPointByName(checkpointName).setState(CheckPoint.RESULT.OK);
	}

	public void validate(Context context, String filenameInfo, Set<mobi.chouette.exchange.regtopp.validation.RegtoppException.ERROR> errorCodes) {
		if (errorCodes != null)
			for (mobi.chouette.exchange.regtopp.validation.RegtoppException.ERROR errorCode : errorCodes) {
				validate(context, filenameInfo, errorCode);
			}
	}

	public void validate(Context context, String filenameInfo, mobi.chouette.exchange.regtopp.validation.RegtoppException.ERROR errorCode) {
		String checkPointName = checkPointName(filenameInfo, errorCode);
		validate(context, filenameInfo, checkPointName);
	}

	public void validate(Context context, String filenameInfo, String checkPointName) {
		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);

		CheckPoint checkPoint = validationReport.findCheckPointByName(checkPointName);

		if (checkPoint != null)
			if (checkPoint.getState() == CheckPoint.RESULT.UNCHECK)
				checkPoint.setState(CheckPoint.RESULT.OK);

	}

	private void addError(ActionReport actionReport, String filename, FileError error) {
		List<FileInfo> files = actionReport.getFiles();
		for (FileInfo f : files) {
			if (f.getName().equals(filename)) {
				f.addError(error);
				break;
			}
		}

	}

}
