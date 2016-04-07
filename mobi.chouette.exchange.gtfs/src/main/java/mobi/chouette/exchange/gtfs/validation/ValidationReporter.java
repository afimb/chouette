package mobi.chouette.exchange.gtfs.validation;

//import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.gtfs.model.importer.GtfsException;
import mobi.chouette.exchange.gtfs.model.importer.GtfsExceptionsHashSet;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileError;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.LineLocation;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.Line;

@Log4j
public class ValidationReporter implements Constant {

	@Getter
	private Set<GtfsException> exceptions = new GtfsExceptionsHashSet<GtfsException>();

	public void dispose() {
		exceptions.clear();
		exceptions = null;
	}

	public void throwUnknownError(Context context, Exception ex, String filenameInfo) throws Exception {
		ActionReport report = (ActionReport) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);
		String name = name(filenameInfo);
		String checkPointName = checkPointName(name, GtfsException.ERROR.SYSTEM);

		if (filenameInfo != null && filenameInfo.indexOf('.') > 0) {
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR, new FileError(FileError.CODE.FILE_NOT_FOUND,
					"A problem occured while reading the file \"" + filenameInfo + "\" (" + checkPointName + ") : "
							+ ex.getMessage()));
			validationReport.addDetail(checkPointName, new Location(filenameInfo), ex.getMessage(),
					CheckPoint.RESULT.NOK);
			String message = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getName();
			log.error(ex, ex);
			throw new Exception("A problem occured while reading the file \"" + filenameInfo + "\" : " + message);
		}
	}

	public void validateUnknownError(Context context) {
		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);
		CheckPoint cp = validationReport.findCheckPointByName(GTFS_1_GTFS_CSV_1);
		if (cp.getState() == CheckPoint.RESULT.UNCHECK)
			cp.setState(CheckPoint.RESULT.OK);
	}

	public void reportErrors(Context context, Set<GtfsException> errors, String filename) throws Exception {
		for (GtfsException error : errors) {
			reportError(context, error, filename);
		}
	}

	public void reportError(Context context, GtfsException ex, String filenameInfo) throws Exception {
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
		case INVALID_HEADER_FILE_FORMAT:
			// 1-GTFS-CSV-2
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_HEADER_FILE_FORMAT);
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR, new FileError(FileError.CODE.INVALID_FORMAT,
					"The first line in file \"" + filenameInfo + "\" must comply with CSV (rule " + checkPointName
							+ ")"));
			validationReport.addDetail(checkPointName, new Location(filenameInfo, 1, ex.getColumn()), filenameInfo,
					CheckPoint.RESULT.NOK);
			throw ex;
			// throw new Exception("The first line in file \"" + filenameInfo +
			// "\" must comply with CSV");

		case EMPTY_HEADER_FIELD:
			// 1-GTFS-CSV-3
			checkPointName = checkPointName(name, GtfsException.ERROR.EMPTY_HEADER_FIELD);
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR, new FileError(FileError.CODE.INVALID_FORMAT,
					"Header fields in file \"" + filenameInfo + "\" could not be empty (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName, new Location(filenameInfo, 1, ex.getColumn()), filenameInfo,
					CheckPoint.RESULT.NOK);
			throw ex;
			// throw new Exception("Header fields in file \"" + filenameInfo +
			// "\" could not be empty");

		case DUPLICATE_HEADER_FIELD:
			// 1-GTFS-CSV-4
			checkPointName = checkPointName(name, GtfsException.ERROR.DUPLICATE_HEADER_FIELD);
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR, new FileError(FileError.CODE.INVALID_FORMAT,
					"The header fields in file \"" + filenameInfo + "\" could not be duplicated (rule "
							+ checkPointName + ")"));
			validationReport.addDetail(checkPointName, new Location(filenameInfo, 1, ex.getColumn()), filenameInfo,
					CheckPoint.RESULT.NOK);
			throw ex;
			// throw new Exception("The header fields in file \"" + filenameInfo
			// + "\" could not be duplicated");

		case INVALID_FILE_FORMAT:
			// 1-GTFS-CSV-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_FILE_FORMAT);
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR, new FileError(FileError.CODE.INVALID_FORMAT,
					"Line number " + ex.getId() + " in file \"" + filenameInfo + "\" must comply with CSV (rule "
							+ checkPointName + ")"));
			validationReport.addDetail(checkPointName, new Location(filenameInfo, ex.getId(), ex.getColumn()),
					filenameInfo, CheckPoint.RESULT.NOK);
			throw ex;
			// throw new Exception("Line number " + ex.getId() + " in file \"" +
			// filenameInfo + "\" must comply with CSV");

		case HTML_TAG_IN_HEADER_FIELD:
			// 1-GTFS-CSV-6
			checkPointName = checkPointName(name, GtfsException.ERROR.HTML_TAG_IN_HEADER_FIELD);
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR, new FileError(FileError.CODE.INVALID_FORMAT,
					"HTML tags in field names are not allowed (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName, new Location(filenameInfo, ex.getId(), ex.getColumn()),
					filenameInfo, CheckPoint.RESULT.NOK);
			throw ex;
			// break;

		case EXTRA_SPACE_IN_FIELD: // Don't throw an exception at this level
			// report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
			// new FileError(FileError.CODE.INVALID_FORMAT,
			// "Extra spaces in field names are not allowed (rule 1-GTFS-CSV-7"));
			// validationReport.addDetail(GTFS_1_GTFS_CSV_7,
			// new Location(filenameInfo,
			// "Extra spaces in field names are not allowed \""+ex.getValue()+"\"",
			// ex.getId()),
			// "Extra spaces in field names are not allowed \""+ex.getValue()+"\"",
			// CheckPoint.RESULT.NOK);
			// break;
			//
		case EXTRA_SPACE_IN_HEADER_FIELD: // Don't throw an exception at this
											// level
			// 1-GTFS-CSV-7
			checkPointName = checkPointName(name, GtfsException.ERROR.EXTRA_SPACE_IN_HEADER_FIELD);
			report.addFileInfo(
					filenameInfo,
					FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Extra spaces in field names are not allowed, at file " + filenameInfo + " for value "
									+ ex.getValue() + " at line " + ex.getId() + " (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName, new Location(filenameInfo, ex.getId(), ex.getColumn()),
					ex.getValue(), ex.getField(), CheckPoint.RESULT.NOK);
			break;

		case MISSING_FILE:
			// 1-GTFS-Common-1
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_FILE);
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR, new FileError(FileError.CODE.FILE_NOT_FOUND,
					"The file \"" + filenameInfo + "\" must be provided (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName, new Location(filenameInfo), filenameInfo, CheckPoint.RESULT.NOK);
			throw ex;
			// throw new Exception("The file \"" + filenameInfo +
			// "\" must be provided");

		case MISSING_FILES:
			// 1-GTFS-Common-1-1
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_FILES);
			filenameInfo = "calendar.txt";
			filenameInfo2 = "calendar_dates.txt";
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR, new FileError(FileError.CODE.FILE_NOT_FOUND,
					"One of the files \"" + filenameInfo + "\" or \"" + filenameInfo2 + "\"must be provided (rule "
							+ checkPointName + ")"));
			validationReport.addDetail(checkPointName, new Location(filenameInfo), filenameInfo + "," + filenameInfo2,
					CheckPoint.RESULT.NOK);
			throw ex;
			// throw new Exception("The file \"" + filenameInfo +
			// "\" must be provided");

		case MISSING_OPTIONAL_FILE:
			// 1-GTFS-Common-1-2
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_OPTIONAL_FILE);
			// report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
			// new FileError(FileError.CODE.FILE_NOT_FOUND,
			// "The file \""+filenameInfo+"\" is not provided (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName, new Location(filenameInfo), filenameInfo, CheckPoint.RESULT.NOK);
			break;

		case UNUSED_FILE:
			// 1-GTFS-Common-1-3
			checkPointName = checkPointName(name, GtfsException.ERROR.UNUSED_FILE);
			// report.addFileInfo(filenameInfo, FILE_STATE.IGNORED, new
			// FileError(FileError.CODE.FILE_NOT_FOUND,
			// "The file \"" + filenameInfo + "\" will not be parsed (rule " +
			// checkPointName + ")"));
			validationReport.addDetail(checkPointName, new Location(filenameInfo), filenameInfo, CheckPoint.RESULT.NOK);
			break;

		case FILE_WITH_NO_ENTRY: // 1-GTFS-Agency-11, 1-GTFS-Stop-12,
									// 1-GTFS-Route-11 error
			// 1-GTFS-Common-2
			checkPointName = checkPointName(name, GtfsException.ERROR.FILE_WITH_NO_ENTRY);
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR, new FileError(FileError.CODE.INVALID_FORMAT,
					"The file \"" + filenameInfo + "\" must contain at least one entry (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName, new Location(filenameInfo, ex.getId(), 0), "",
					CheckPoint.RESULT.NOK);
			throw ex;
			// throw new Exception("The file \"" + filenameInfo +
			// "\" must contain at least one entry");

		case FILES_WITH_NO_ENTRY: // 1-GTFS-Agency-11, 1-GTFS-Stop-12,
									// 1-GTFS-Route-11 error
			// 1-GTFS-Common-2-1
			checkPointName = checkPointName(name, GtfsException.ERROR.FILES_WITH_NO_ENTRY);
			fieldName = ex.getField();
			filenameInfo = "calendar.txt";
			filenameInfo2 = "calendar_dates.txt";
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR, new FileError(FileError.CODE.INVALID_FORMAT,
					"One of the files \"" + filenameInfo + "\" or \"" + filenameInfo2
							+ "\" must contain at least one entry (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName, new Location(filenameInfo, ex.getId(), 0), "",
					CheckPoint.RESULT.NOK);
			throw ex;
			// throw new Exception("One of the files \"" + filenameInfo +
			// "\" or \"" + filenameInfo2 +
			// "\" must contain at least one entry");

		case DUPLICATE_FIELD:
			// 1-GTFS-Common-3
			checkPointName = checkPointName(name, GtfsException.ERROR.DUPLICATE_FIELD);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR, new FileError(FileError.CODE.INVALID_FORMAT,
					"The field \"" + fieldName + "\" must be unique (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName, new Location(filenameInfo, ex.getId(), ex.getColumn()),
					ex.getValue(), fieldName, CheckPoint.RESULT.NOK);
			throw ex;
			// throw new Exception("The field \"" + fieldName +
			// "\" must be unique");

		case MISSING_REQUIRED_FIELDS: // 1_GTFS_Agency_2, 1_GTFS_Agency_4,
										// 1-GTFS-Stop-2, 1-GTFS-Route-2,
										// 1-GTFS-StopTime-2, 1-GTFS-Trip-2,
										// 1-GTFS-Frequency-1,
										// 1-GTFS-Calendar-2,
										// 1-GTFS-CalendarDate-2,
										// 1-GTFS-Transfer-1 error
			// 1-GTFS-Common-3-1
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_REQUIRED_FIELDS);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR, new FileError(FileError.CODE.INVALID_FORMAT,
					"The column \"" + fieldName + "\" of file \"" + filenameInfo + "\" must be provided (rule "
							+ checkPointName + ")"));
			validationReport.addDetail(checkPointName, new Location(filenameInfo, ex.getId(), ex.getColumn()),
					fieldName, CheckPoint.RESULT.NOK);
			throw ex;
			// throw new Exception("The column \"" + fieldName + "\" of file \""
			// + filenameInfo + "\" must be provided");

		case MISSING_REQUIRED_FIELDS2:
			// 1-GTFS-Common-3-2
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_REQUIRED_FIELDS2);
			fieldName = ex.getField();
			fieldName2 = fieldName.replaceFirst("long", "short");
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR, new FileError(FileError.CODE.INVALID_FORMAT,
					"One of the fields \"" + fieldName + "\" or \"" + fieldName2 + "\" must be provided (rule "
							+ checkPointName + ")"));
			validationReport.addDetail(checkPointName, new Location(filenameInfo, ex.getId(), ex.getColumn()),
					fieldName + "," + fieldName2, CheckPoint.RESULT.NOK);
			break;

		case MISSING_OPTIONAL_FIELD:
			// 1-GTFS-Common-3-3
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_OPTIONAL_FIELD);
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED, new FileError(FileError.CODE.INVALID_FORMAT,
					"Optional field is not present (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName, new Location(filenameInfo, ex.getId(), ex.getColumn()),
					ex.getField(), CheckPoint.RESULT.NOK);
			break;

		case EXTRA_HEADER_FIELD: // 1_GTFS_Agency_10, 1_GTFS_Stop_11,
									// 1-GTFS-Route-10, 1-GTFS-StopTime-12,
									// 1-GTFS-Trip-8, 1-GTFS-Frequency-7,
									// 1-GTFS-Calendar-14,
									// 1-GTFS-CalendarDate-7, 1-GTFS-Transfer-6
									// info
			// 1-GTFS-Common-3-4
			checkPointName = checkPointName(name, GtfsException.ERROR.EXTRA_HEADER_FIELD);
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED, new FileError(FileError.CODE.INVALID_FORMAT,
					"Extra fields are provided (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName, new Location(filenameInfo, ex.getId(), ex.getColumn()),
					ex.getField(), CheckPoint.RESULT.NOK);
			break;

		case MISSING_REQUIRED_VALUES:
			// 1-GTFS-Common-4
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_REQUIRED_VALUES);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR, new FileError(FileError.CODE.INVALID_FORMAT,
					"The value \"" + fieldName + "\" must be provided (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName, new Location(filenameInfo, ex.getId(), ex.getColumn()),
					fieldName, CheckPoint.RESULT.NOK);
			break;

		case MISSING_FIELD:
			// 1-GTFS-Common-4
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_FIELD);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR, new FileError(FileError.CODE.INVALID_FORMAT,
					"The file \"" + filenameInfo + "\" must provide a non empty \"" + name + "_id\" for each " + name
							+ " (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName, new Location(filenameInfo, ex.getId(), ex.getColumn()),
					fieldName, CheckPoint.RESULT.NOK);
			throw ex;
			// throw new Exception("The file \"" + filenameInfo +
			// "\" must provide a non empty \"" + fieldName + "\" for each " +
			// name);

		case MISSING_REQUIRED_VALUES2:
			// 1-GTFS-Common-4-1
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_REQUIRED_VALUES2);
			fieldName = ex.getField();
			fieldName2 = fieldName.replaceFirst("long", "short");
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR, new FileError(FileError.CODE.INVALID_FORMAT,
					"One of the values \"" + fieldName + "\" or \"" + fieldName2 + "\" must be provided (rule "
							+ checkPointName + ")"));
			validationReport.addDetail(checkPointName, new Location(filenameInfo, ex.getId(), ex.getColumn()),
					fieldName + "," + fieldName2, CheckPoint.RESULT.NOK);
			break;

		case ALL_DAYS_ARE_INVALID:
			// 1-GTFS-Calendar-1
			checkPointName = checkPointName(name, GtfsException.ERROR.ALL_DAYS_ARE_INVALID);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR, new FileError(FileError.CODE.INVALID_FORMAT,
					"At least one day must be valid (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName, new Location(filenameInfo, ex.getId(), ex.getColumn()),
					"At least one day must be valid", CheckPoint.RESULT.NOK);
			break;

		case DUPLICATE_DEFAULT_KEY_FIELD:
			// 1-GTFS-Common-4-3
			checkPointName = checkPointName(name, GtfsException.ERROR.DUPLICATE_DEFAULT_KEY_FIELD);
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR, new FileError(FileError.CODE.INVALID_FORMAT,
					"At most only one Agency can have default value \"agency_id\" (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName, new Location(filenameInfo, ex.getId(), ex.getColumn()),
					ex.getField(), CheckPoint.RESULT.NOK);
			throw ex;
			// throw new
			// Exception("At most only one Agency can have default value \"agency_id\"");

		case DEFAULT_VALUE:
			// 1-GTFS-Common-4-4
			checkPointName = checkPointName(name, GtfsException.ERROR.DEFAULT_VALUE);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED, new FileError(FileError.CODE.INVALID_FORMAT,
					"No default ids for agencies (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName, new Location(filenameInfo, ex.getId(), ex.getColumn()),
					fieldName, CheckPoint.RESULT.NOK);
			break;

		case MISSING_ARRIVAL_TIME:
			// 1-GTFS-Common-4-6
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_ARRIVAL_TIME);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED, new FileError(FileError.CODE.INVALID_FORMAT,
					"Missing \"" + fieldName + "\" (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName, "1", new Location(filenameInfo, ex.getId(), ex.getColumn()),
					fieldName, CheckPoint.RESULT.NOK);
			break;

		case MISSING_DEPARTURE_TIME:
			// 1-GTFS-Common-4-6
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_DEPARTURE_TIME);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED, new FileError(FileError.CODE.INVALID_FORMAT,
					"Missing \"" + fieldName + "\" (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName, "2", new Location(filenameInfo, ex.getId(), ex.getColumn()),
					fieldName, CheckPoint.RESULT.NOK);
			break;
		case MISSING_TRANSFER_TIME:
			// 1-GTFS-Common-4-6
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_TRANSFER_TIME);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED, new FileError(FileError.CODE.INVALID_FORMAT,
					"Missing \"" + fieldName + "\" (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName, "3", new Location(filenameInfo, ex.getId(), ex.getColumn()),
					fieldName, CheckPoint.RESULT.NOK);
			break;

		case START_DATE_AFTER_END_DATE:
			// 1-GTFS-Calendar-2
			checkPointName = checkPointName(name, GtfsException.ERROR.START_DATE_AFTER_END_DATE);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED, new FileError(FileError.CODE.INVALID_FORMAT,
					"StartDate cannot be after EndDate (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName, new Location(filenameInfo, ex.getId(), ex.getColumn()),
					fieldName, CheckPoint.RESULT.NOK);
			break;

		case INVALID_FORMAT:
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_FORMAT);
			fieldName = ex.getField();
			value = ex.getValue();
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR, new FileError(FileError.CODE.INVALID_FORMAT,
					"The value \"" + value + "\" is invalid for field " + fieldName + " (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName, new Location(filenameInfo, ex.getId(), ex.getColumn()), value,
					fieldName, CheckPoint.RESULT.NOK);
			break;

		case DUPLICATE_STOP_SEQUENCE:
			// 2-GTFS-Common-4
			checkPointName = checkPointName(name, GtfsException.ERROR.DUPLICATE_STOP_SEQUENCE);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED, new FileError(FileError.CODE.INVALID_FORMAT,
					"Duplicate (" + fieldName + ") values (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, ex.getId(), ex.getColumn(), ex.getField()), ex.getValue(),
					ex.getField(), CheckPoint.RESULT.NOK);
			throw ex;
			// throw new
			// Exception("Duplicate \""+fieldName+"\" for the same \"tripid\"");

		case UNREFERENCED_ID:
			// 2-GTFS-Common-1
			checkPointName = checkPointName(name, GtfsException.ERROR.UNREFERENCED_ID);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED, new FileError(FileError.CODE.INVALID_FORMAT,
					"Unreferenced " + fieldName + " (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName, new Location(filenameInfo, fieldName, ex.getId(),
					ex.getColumn(), ex.getCode()), ex.getValue(), ex.getField(), CheckPoint.RESULT.NOK);
			break;

		case UNUSED_ID:
			// 2-GTFS-Common-2
			checkPointName = checkPointName(name, GtfsException.ERROR.UNUSED_ID);
			fieldName = ex.getField();
			report.addFileInfo(ex.getPath(), FILE_STATE.IGNORED, new FileError(FileError.CODE.INVALID_FORMAT,
					"Unused \"" + fieldName + "\" (" + ex.getValue() + ") in file \"" + ex.getPath() + "\" at line \""
							+ ex.getId() + "\" (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName, new Location(filenameInfo, ex.getId(), ex.getColumn()),
					ex.getValue(), fieldName, CheckPoint.RESULT.NOK);
			break;

		case DUPLICATE_DOUBLE_KEY:
			// 2-GTFS-Common-4
			checkPointName = checkPointName(name, GtfsException.ERROR.DUPLICATE_DOUBLE_KEY);
			fieldName = ex.getField();
			fieldName2 = "date";
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED, new FileError(FileError.CODE.INVALID_FORMAT,
					"Duplicate (" + fieldName + ") values (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, ex.getId(), ex.getColumn(), ex.getField()), ex.getValue(),
					ex.getField(), CheckPoint.RESULT.NOK);
			throw ex;
			// break;

		case SHARED_VALUE:
			// 2-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.SHARED_VALUE);
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED, new FileError(FileError.CODE.INVALID_FORMAT,
					"The two values " + ex.getField() + " cannot be the same (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, ex.getId(), ex.getColumn(), ex.getCode()), ex.getValue(), ex.getField(),
					CheckPoint.RESULT.NOK);
			break;

		case BAD_REFERENCED_ID:
			// 2-GTFS-Stop-1
			checkPointName = checkPointName(name, GtfsException.ERROR.BAD_REFERENCED_ID);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED, new FileError(FileError.CODE.INVALID_FORMAT,
					"The parent stop must be a station (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, ex.getId(), ex.getColumn(), ex.getCode()), ex.getValue(),
					CheckPoint.RESULT.NOK);
			break;

		case NO_LOCATION_TYPE:
			// 2-GTFS-Stop-2
			checkPointName = checkPointName(name, GtfsException.ERROR.NO_LOCATION_TYPE);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED, new FileError(FileError.CODE.INVALID_FORMAT,
					"Column location_type cannot be empty for all stops (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName, new Location(filenameInfo,
					"Column location_type cannot be empty for all stops", ex.getId(), ex.getColumn(), ex.getField()),
					"Column location_type cannot be empty for all stops", CheckPoint.RESULT.NOK);
			break;

		case BAD_VALUE:
			// 2-GTFS-Stop-3
			checkPointName = checkPointName(name, GtfsException.ERROR.BAD_VALUE);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED, new FileError(FileError.CODE.INVALID_FORMAT,
					"stop_name and stop_desc must be different (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, ex.getId(), ex.getColumn(), ex.getCode()), ex.getValue(),
					CheckPoint.RESULT.NOK);
			throw ex;
			// break;

		case NO_PARENT_FOR_STATION:
			// 2-GTFS-Stop-4
			checkPointName = checkPointName(name, GtfsException.ERROR.NO_PARENT_FOR_STATION);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED, new FileError(FileError.CODE.INVALID_FORMAT,
					"Stations can't contain other stations (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, ex.getId(), ex.getColumn(), ex.getCode()), ex.getValue(),
					CheckPoint.RESULT.NOK);
			break;

		case DUPLICATE_ROUTE_NAMES:
			// 2-GTFS-Route-5
			checkPointName = checkPointName(name, GtfsException.ERROR.DUPLICATE_ROUTE_NAMES);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED, new FileError(FileError.CODE.INVALID_FORMAT,
					"The couple short_name, long_name must be unique (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, ex.getId(), ex.getColumn(), ex.getCode()), ex.getValue(),
					CheckPoint.RESULT.NOK);
			break;

		case CONTAINS_ROUTE_NAMES:
			// 2-GTFS-Route-8
			checkPointName = checkPointName(name, GtfsException.ERROR.CONTAINS_ROUTE_NAMES);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED, new FileError(FileError.CODE.INVALID_FORMAT,
					"The long_name cannot contains the short_name (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, ex.getId(), ex.getColumn(), ex.getCode()), ex.getValue(),
					ex.getRefValue(), CheckPoint.RESULT.NOK);
			break;

		case BAD_COLOR:
			// 2-GTFS-Route-9
			checkPointName = checkPointName(name, GtfsException.ERROR.BAD_COLOR);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED, new FileError(FileError.CODE.INVALID_FORMAT,
					"Poor visibility between text and background colors (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, ex.getId(), ex.getColumn(), ex.getCode()), "", CheckPoint.RESULT.NOK);
			break;

		case INVERSE_DUPLICATE_ROUTE_NAMES:
			// 2-GTFS-Route-11
			checkPointName = checkPointName(name, GtfsException.ERROR.INVERSE_DUPLICATE_ROUTE_NAMES);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED, new FileError(FileError.CODE.INVALID_FORMAT,
					"The set short_name, long_name must be unique (rule " + checkPointName + ")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, ex.getId(), ex.getColumn(), ex.getCode()), ex.getValue(),
					CheckPoint.RESULT.NOK);
			break;

		case MISSING_FOREIGN_KEY: // THIS CAN NEVER OCCUR !
		case SYSTEM: // THIS CAN NEVER OCCUR !
		default:
			break;
		}
	}

	private String checkPointName(String name, mobi.chouette.exchange.gtfs.model.importer.GtfsException.ERROR errorName) {
		name = capitalize(name);
		switch (errorName) {
		case SYSTEM:
			return GTFS_1_GTFS_CSV_1;
		case INVALID_HEADER_FILE_FORMAT:
			return GTFS_1_GTFS_CSV_2;
		case EMPTY_HEADER_FIELD:
			return GTFS_1_GTFS_CSV_3;
		case DUPLICATE_HEADER_FIELD:
			return GTFS_1_GTFS_CSV_4;
		case INVALID_FILE_FORMAT:
			return GTFS_1_GTFS_CSV_5;
		case HTML_TAG_IN_HEADER_FIELD:
			return GTFS_1_GTFS_CSV_6;
		case EXTRA_SPACE_IN_FIELD:
		case EXTRA_SPACE_IN_HEADER_FIELD:
			return GTFS_1_GTFS_CSV_7;
		case MISSING_FILE:
			return GTFS_1_GTFS_Common_1;
		case MISSING_FILES:
			return GTFS_1_GTFS_Common_2;
		case MISSING_OPTIONAL_FILE:
			return GTFS_1_GTFS_Common_3;
		case UNUSED_FILE:
			return GTFS_1_GTFS_Common_4;
		case FILE_WITH_NO_ENTRY:
			return GTFS_1_GTFS_Common_5;
		case FILES_WITH_NO_ENTRY:
			return GTFS_1_GTFS_Common_6;
		case DUPLICATE_FIELD:
			return GTFS_1_GTFS_Common_8;
		case MISSING_REQUIRED_FIELDS:
			return GTFS_1_GTFS_Common_9;
		case MISSING_REQUIRED_FIELDS2:
			return GTFS_1_GTFS_Route_1;
		case MISSING_OPTIONAL_FIELD:
			return GTFS_1_GTFS_Common_10;
		case EXTRA_HEADER_FIELD:
			return GTFS_1_GTFS_Common_11;
		case MISSING_REQUIRED_VALUES:
		case MISSING_FIELD:
			return GTFS_1_GTFS_Common_12;
		case MISSING_REQUIRED_VALUES2:
			return GTFS_1_GTFS_Route_2;
		case ALL_DAYS_ARE_INVALID:
			return GTFS_1_GTFS_Calendar_1;
		case DUPLICATE_DEFAULT_KEY_FIELD:
			return GTFS_1_GTFS_Common_13;
		case DEFAULT_VALUE:
			return GTFS_1_GTFS_Common_14;
		case MISSING_ARRIVAL_TIME:
		case MISSING_DEPARTURE_TIME:
		case MISSING_TRANSFER_TIME:
			return GTFS_1_GTFS_Common_15;
		case START_DATE_AFTER_END_DATE:
			return GTFS_1_GTFS_Calendar_2;
		case INVALID_FORMAT:
			return GTFS_1_GTFS_Common_16;
		case UNREFERENCED_ID:
			return GTFS_2_GTFS_Common_1;
		case UNUSED_ID:
			return GTFS_2_GTFS_Common_2;
		case DUPLICATE_STOP_SEQUENCE:
		case DUPLICATE_DOUBLE_KEY:
			return GTFS_2_GTFS_Common_3;
		case SHARED_VALUE:
			return GTFS_2_GTFS_Common_4;

		case BAD_REFERENCED_ID:
			return GTFS_2_GTFS_Stop_1;
		case NO_LOCATION_TYPE:
			return GTFS_2_GTFS_Stop_2;
		case BAD_VALUE:
			return GTFS_2_GTFS_Stop_3;
		case NO_PARENT_FOR_STATION:
			return GTFS_2_GTFS_Stop_4;
		case DUPLICATE_ROUTE_NAMES:
			return GTFS_2_GTFS_Route_1;
		case CONTAINS_ROUTE_NAMES:
			return GTFS_2_GTFS_Route_2;
		case BAD_COLOR:
			return GTFS_2_GTFS_Route_3;
		case INVERSE_DUPLICATE_ROUTE_NAMES:
			return GTFS_2_GTFS_Route_4;

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

	public void validate(Context context, String filenameInfo,
			Set<mobi.chouette.exchange.gtfs.model.importer.GtfsException.ERROR> errorCodes) {
		if (errorCodes != null)
			for (mobi.chouette.exchange.gtfs.model.importer.GtfsException.ERROR errorCode : errorCodes) {
				validate(context, filenameInfo, errorCode);
			}
	}

	public void validate(Context context, String filenameInfo,
			mobi.chouette.exchange.gtfs.model.importer.GtfsException.ERROR errorCode) {
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

	public void updateValidationReport(Context context, String filename, String gtfsid, Line line) {
		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);
		for (CheckPoint checkpoint : validationReport.getCheckPoints()) {
			if (checkpoint.getDetailCount() > 0) {
				for (Detail detail : checkpoint.getDetails()) {
					if (detail.getSource() != null && detail.getSource().getFile() != null
							&& filename.equals(detail.getSource().getFile().getFilename())) {
						if (gtfsid.equals(detail.getSource().getObjectId())) {
                              detail.getSource().setLine(new LineLocation(line));
						}
					}
				}
			}
		}
	}

	public void validateOkCSV(Context context, String filenameInfo) {
		validate(context, filenameInfo, GtfsException.ERROR.INVALID_HEADER_FILE_FORMAT);
		validate(context, filenameInfo, GtfsException.ERROR.EMPTY_HEADER_FIELD);
		validate(context, filenameInfo, GtfsException.ERROR.DUPLICATE_HEADER_FIELD);
		validate(context, filenameInfo, GtfsException.ERROR.DUPLICATE_DEFAULT_KEY_FIELD);
		validate(context, filenameInfo, GtfsException.ERROR.MISSING_FIELD);
		validate(context, filenameInfo, GtfsException.ERROR.DUPLICATE_FIELD);
		validate(context, filenameInfo, GtfsException.ERROR.INVALID_FILE_FORMAT);
		validate(context, filenameInfo, GtfsException.ERROR.MISSING_FILE);
		validate(context, filenameInfo, GtfsException.ERROR.SYSTEM);
	}

	public void validateOKGeneralSyntax(Context context, String filenameInfo) {
		validate(context, filenameInfo, GtfsException.ERROR.EXTRA_SPACE_IN_HEADER_FIELD);
		validate(context, filenameInfo, GtfsException.ERROR.HTML_TAG_IN_HEADER_FIELD);
		validate(context, filenameInfo, GtfsException.ERROR.EXTRA_HEADER_FIELD);
		validate(context, filenameInfo, GtfsException.ERROR.MISSING_REQUIRED_FIELDS);
	}
}
