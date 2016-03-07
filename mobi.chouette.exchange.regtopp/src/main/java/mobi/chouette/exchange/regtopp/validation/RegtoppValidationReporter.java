package mobi.chouette.exchange.regtopp.validation;

//import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppException;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppExceptionsHashSet;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileError;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.exchange.validation.report.ValidationReport;

@Log4j
public class RegtoppValidationReporter implements Constant {

	@Getter
	private Set<RegtoppException> exceptions = new RegtoppExceptionsHashSet<RegtoppException>();

	public void dispose() {
		exceptions.clear();
		exceptions = null;
	}

	public void throwUnknownError(Context context, Exception ex, String filenameInfo) throws Exception {
		ActionReport report = (ActionReport) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);
		String name = name(filenameInfo);
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

	public void validateUnknownError(Context context) {
		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);
		CheckPoint cp = validationReport.findCheckPointByName(REGTOPP_SYSTEM);
		if (cp.getState() == CheckPoint.RESULT.UNCHECK)
			cp.setState(CheckPoint.RESULT.OK);
	}

	public void reportErrors(Context context, Set<RegtoppException> errors, String filename) throws Exception {
		for (RegtoppException error : errors) {
			reportError(context, error, filename);
		}
	}

	public void reportError(Context context, RegtoppException ex, String filenameInfo) throws Exception {
		if (!exceptions.add(ex))
			return;
		ActionReport report = (ActionReport) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);
		String name = name(filenameInfo);
		String filenameInfo2 = "";
		String checkPointName = "";
		String fieldName = "";
		String fieldName2 = "";
		String value = "";

		// log.error(ex);

		switch (ex.getError()) {
		case SYSTEM:
			// 1-GTFS-CSV-2
			checkPointName = checkPointName(name, RegtoppException.ERROR.SYSTEM);
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR, new FileError(FileError.CODE.INVALID_FORMAT,
					"The first line in file \"" + filenameInfo + "\" must comply with CSV (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName, new Location(filenameInfo, 1, -1), filenameInfo, CheckPoint.RESULT.NOK);
			throw ex;
			// throw new Exception("The first line in file \"" + filenameInfo + "\" must comply with CSV");
		default:
			break;
		}
	}

	private String checkPointName(String name, mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppException.ERROR errorName) {
		name = capitalize(name);
		switch (errorName) {
		case SYSTEM:
			return REGTOPP_SYSTEM;

		default:
			return null;
		}
	}

	private String capitalize(String name) {
		// CSV, CalendarDate, StopTime
		if ("csv".equalsIgnoreCase(name))
			return "CSV";
		if ("calendar_date".equalsIgnoreCase(name))
			return "CalendarDate";
		if ("stop_time".equalsIgnoreCase(name))
			return "StopTime";
		if (name != null && !name.trim().isEmpty()) {
			name = name.trim();
			char c = name.charAt(0);
			if (c >= 'a' && c <= 'z') {
				name = name.substring(1);
				name = (char) ((int) c + (int) 'A' - (int) ('a')) + name;
			}
		}
		return name;
	}

	private String name(String filename) {
		if (filename != null) {
			if (filename.indexOf('.') > 0)
				filename = filename.substring(0, filename.lastIndexOf('.'));
			if (filename.endsWith("ies"))
				filename = filename.substring(0, filename.lastIndexOf('i')) + "y";
			if (filename.endsWith("s"))
				filename = filename.substring(0, filename.lastIndexOf('s'));
			return filename;
		}
		return "";
	}

	public void reportSuccess(Context context, String checkpointName, String filenameInfo) {
		ActionReport report = (ActionReport) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);

		report.addFileInfo(filenameInfo, FILE_STATE.OK);
		if (validationReport.findCheckPointByName(checkpointName).getState() == CheckPoint.RESULT.UNCHECK)
			validationReport.findCheckPointByName(checkpointName).setState(CheckPoint.RESULT.OK);
	}

	public void validate(Context context, String filenameInfo, Set<mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppException.ERROR> errorCodes) {
		if (errorCodes != null)
			for (mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppException.ERROR errorCode : errorCodes) {
				validate(context, filenameInfo, errorCode);
			}
	}

	public void validate(Context context, String filenameInfo, mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppException.ERROR errorCode) {
		String checkPointName = checkPointName(name(filenameInfo), errorCode);
		validate(context, filenameInfo, checkPointName);
	}

	public void validate(Context context, String filenameInfo, String checkPointName) {
		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);

		CheckPoint checkPoint = validationReport.findCheckPointByName(checkPointName);

		if (checkPoint != null)
			if (checkPoint.getState() == CheckPoint.RESULT.UNCHECK)
				checkPoint.setState(CheckPoint.RESULT.OK);

	}

}
