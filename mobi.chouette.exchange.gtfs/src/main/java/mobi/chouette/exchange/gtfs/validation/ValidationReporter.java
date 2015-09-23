package mobi.chouette.exchange.gtfs.validation;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.gtfs.model.importer.GtfsException;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileError;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.exchange.validation.report.ValidationReport;

public class ValidationReporter implements Constant {

	@Getter
	private Set<GtfsException> exceptions = new HashSet<GtfsException>();
	
	public void throwUnknownError(Context context, Exception ex, String filenameInfo) throws Exception {
		ActionReport report = (ActionReport) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);
		String name = name(filenameInfo);
		String checkPointName = checkPointName(name, GtfsException.ERROR.SYSTEM);

		if (filenameInfo != null && filenameInfo.indexOf('.') > 0) {
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR,
					new FileError(FileError.CODE.FILE_NOT_FOUND, "A problem occured while reading the file \""+filenameInfo+"\" ("+checkPointName+") : "+ex.getMessage()));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, filenameInfo.substring(0, filenameInfo.lastIndexOf('.'))+"-failure"),
					"A problem occured while reading the file \""+filenameInfo+"\" : "+ex.getMessage(),
					CheckPoint.RESULT.NOK);
			throw new Exception("A problem occured while reading the file \""+filenameInfo+"\" : "+ex.getMessage());
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
		
		switch ( ex.getError() ) {
		case INVALID_HEADER_FILE_FORMAT:
			// 1-GTFS-CSV-2
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_HEADER_FILE_FORMAT);
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"The first line in file \""+filenameInfo+"\" must comply with CSV (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, name(filenameInfo)+"-failure", ((GtfsException) ex).getId()),
					"The first line in file \""+filenameInfo+"\" must comply with CSV",
					CheckPoint.RESULT.NOK);
			throw new Exception("The first line in file \""+filenameInfo+"\" must comply with CSV");
			
		case EMPTY_HEADER_FIELD:
			// 1-GTFS-CSV-3
			checkPointName = checkPointName(name, GtfsException.ERROR.EMPTY_HEADER_FIELD);
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Header fields in file \""+filenameInfo+"\" could not be empty (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, name(filenameInfo)+"-failure", ((GtfsException) ex).getId()),
					"Header fields in file \""+filenameInfo+"\" could not be empty",
					CheckPoint.RESULT.NOK);
			throw new Exception("Header fields in file \""+filenameInfo+"\" could not be empty");

		case DUPLICATE_HEADER_FIELD:
			// 1-GTFS-CSV-4
			checkPointName = checkPointName(name, GtfsException.ERROR.DUPLICATE_HEADER_FIELD);
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"The header fields in file \""+filenameInfo+"\" could not be duplicated (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, name(filenameInfo)+"-failure", ((GtfsException) ex).getId()),
					"The header fields in file \""+filenameInfo+"\" could not be duplicated",
					CheckPoint.RESULT.NOK);
			throw new Exception("The header fields in file \""+filenameInfo+"\" could not be duplicated");

		case INVALID_FILE_FORMAT:
			// 1-GTFS-CSV-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_FILE_FORMAT);
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Line number "+((GtfsException) ex).getId()+" in file \""+filenameInfo+"\" must comply with CSV (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, name(filenameInfo)+"-failure", ((GtfsException) ex).getId()),
					"Line number "+((GtfsException) ex).getId()+" in file \""+filenameInfo+"\" must comply with CSV",
					CheckPoint.RESULT.NOK);
			throw new Exception("Line number "+((GtfsException) ex).getId()+" in file \""+filenameInfo+"\" must comply with CSV");
			
		case HTML_TAG_IN_HEADER_FIELD:
			// 1-GTFS-CSV-6
			checkPointName = checkPointName(name, GtfsException.ERROR.HTML_TAG_IN_HEADER_FIELD);
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"HTML tags in field names are not allowed (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "HTML tags in field names are not allowed", ((GtfsException) ex).getId()),
					"HTML tags in field names are not allowed",
					CheckPoint.RESULT.NOK);
			break;
			
		case EXTRA_SPACE_IN_FIELD: // Don't throw an exception at this level
//			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
//					new FileError(FileError.CODE.INVALID_FORMAT,
//							"Extra spaces in field names are not allowed (rule 1-GTFS-CSV-7"));
//			validationReport.addDetail(GTFS_1_GTFS_CSV_7,
//					new Location(filenameInfo, "Extra spaces in field names are not allowed \""+((GtfsException) ex).getValue()+"\"", ((GtfsException) ex).getId()),
//					"Extra spaces in field names are not allowed \""+((GtfsException) ex).getValue()+"\"",
//					CheckPoint.RESULT.NOK);
//			break;
//
		case EXTRA_SPACE_IN_HEADER_FIELD: // Don't throw an exception at this level
			// 1-GTFS-CSV-7
			checkPointName = checkPointName(name, GtfsException.ERROR.EXTRA_SPACE_IN_HEADER_FIELD);
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Extra spaces in field names are not allowed (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Extra spaces in field names are not allowed \""+((GtfsException) ex).getValue()+"\"", ((GtfsException) ex).getId()),
					"Extra spaces in field names are not allowed \""+((GtfsException) ex).getValue()+"\"",
					CheckPoint.RESULT.NOK);
			break;
			
		case MISSING_FILE:
			// 1-GTFS-Common-1
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_FILE);
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR,
					new FileError(FileError.CODE.FILE_NOT_FOUND, "The file \""+filenameInfo+"\" must be provided (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, name(filenameInfo)+"-failure"),
					"The file \""+filenameInfo+"\" must be provided",
					CheckPoint.RESULT.NOK);
			throw new Exception("The file \""+filenameInfo+"\" must be provided");
				
		case MISSING_FILES:
			// 1-GTFS-Common-1-1
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_FILES);
			filenameInfo = "calendar.txt";
			filenameInfo2 = "calendar_dates.txt";
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR,
					new FileError(FileError.CODE.FILE_NOT_FOUND, "One of the files \""+filenameInfo+"\" or \""+filenameInfo2+"\"must be provided (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, name(filenameInfo)+"-failure"),
					"The file \""+filenameInfo+"\" must be provided",
					CheckPoint.RESULT.NOK);
			throw new Exception("The file \""+filenameInfo+"\" must be provided");
			
		case MISSING_OPTIONAL_FILE:
			// 1-GTFS-Common-1-2
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_OPTIONAL_FILE);
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.FILE_NOT_FOUND, "The file \""+filenameInfo+"\" is not provided (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, name(filenameInfo)+"-ignored"),
					"The file \""+filenameInfo+"\" is not provided",
					CheckPoint.RESULT.NOK);
			break;
			
		case UNUSED_FILE:
			// 1-GTFS-Common-1-3
			checkPointName = checkPointName(name, GtfsException.ERROR.UNUSED_FILE);
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.FILE_NOT_FOUND, "The file \""+filenameInfo+"\" will not be parsed (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, name(filenameInfo)+"-ignored"),
					"The file \""+filenameInfo+"\" will not be parsed",
					CheckPoint.RESULT.NOK);
			break;

		case FILE_WITH_NO_ENTRY: // 1-GTFS-Agency-11, 1-GTFS-Stop-12, 1-GTFS-Route-11 error
			// 1-GTFS-Common-2
			checkPointName = checkPointName(name, GtfsException.ERROR.FILE_WITH_NO_ENTRY);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"The file \""+filenameInfo+"\" must contain at least one "+fieldName+" definition (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, name(filenameInfo)+"-failure", ((GtfsException) ex).getId()),
					"The file \""+filenameInfo+"\" must contain at least one "+fieldName+" definition",
					CheckPoint.RESULT.NOK);
			throw new Exception("The file \""+filenameInfo+"\" must contain at least one "+fieldName+" definition");

		case FILES_WITH_NO_ENTRY: // 1-GTFS-Agency-11, 1-GTFS-Stop-12, 1-GTFS-Route-11 error
			// 1-GTFS-Common-2-1
			checkPointName = checkPointName(name, GtfsException.ERROR.FILES_WITH_NO_ENTRY);
			fieldName = ex.getField();
			filenameInfo = "calendar.txt";
			filenameInfo2 = "calendar_dates.txt";
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"One of the files \""+filenameInfo+"\" or \""+filenameInfo2+"\" must contain at least one "+fieldName+" definition (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, name(filenameInfo)+"-failure", ((GtfsException) ex).getId()),
					"One of the files \""+filenameInfo+"\" or \""+filenameInfo2+"\" must contain at least one "+fieldName+" definition",
					CheckPoint.RESULT.NOK);
			throw new Exception("One of the files \""+filenameInfo+"\" or \""+filenameInfo2+"\" must contain at least one "+fieldName+" definition");
		
		case DUPLICATE_FIELD:
			// 1-GTFS-Common-3
			checkPointName = checkPointName(name, GtfsException.ERROR.DUPLICATE_FIELD);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR,
					new FileError(FileError.CODE.INVALID_FORMAT, "The field \""+fieldName+"\" must be unique (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, name(filenameInfo)+"-failure", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"The field \""+fieldName+"\" must be unique",
					CheckPoint.RESULT.NOK);
			throw new Exception("The field \""+fieldName+"\" must be unique");
			
		case MISSING_REQUIRED_FIELDS: // 1_GTFS_Agency_2, 1_GTFS_Agency_4, 1-GTFS-Stop-2, 1-GTFS-Route-2, 1-GTFS-StopTime-2, 1-GTFS-Trip-2, 1-GTFS-Frequency-1, 1-GTFS-Calendar-2, 1-GTFS-CalendarDate-2, 1-GTFS-Transfer-1 error
			// 1-GTFS-Common-3-1
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_REQUIRED_FIELDS);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"The column \""+fieldName+"\" of file \""+filenameInfo+"\" must be provided (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, name+"-failure", ((GtfsException) ex).getId()),
					"The column \""+fieldName+"\" of file \""+filenameInfo+"\" must be provided",
					CheckPoint.RESULT.NOK);
			throw new Exception("The column \""+fieldName+"\" of file \""+filenameInfo+"\" must be provided");

		case MISSING_REQUIRED_FIELDS2:
			// 1-GTFS-Common-3-2
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_REQUIRED_FIELDS2);
			fieldName = ex.getField();
			fieldName2 = fieldName.replaceFirst("long", "short");
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"One of the fields \""+fieldName+"\" or \""+fieldName2+"\" must be provided (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, name+"-failure", ((GtfsException) ex).getId()),
					"One of the fields \""+fieldName+"\" or \""+fieldName2+"\" must be provided",
					CheckPoint.RESULT.NOK);
			break;				
			
		case MISSING_OPTIONAL_FIELD:
			// 1-GTFS-Common-3-3
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_OPTIONAL_FIELD);
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Optional field is not present (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Optional field is not present", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Optional field is not present",
					CheckPoint.RESULT.NOK);
			break;
			
		case EXTRA_HEADER_FIELD: // 1_GTFS_Agency_10, 1_GTFS_Stop_11, 1-GTFS-Route-10, 1-GTFS-StopTime-12, 1-GTFS-Trip-8, 1-GTFS-Frequency-7, 1-GTFS-Calendar-14, 1-GTFS-CalendarDate-7, 1-GTFS-Transfer-6 info
			// 1-GTFS-Common-3-4
			checkPointName = checkPointName(name, GtfsException.ERROR.EXTRA_HEADER_FIELD);
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Extra fields are provided (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Extra fields are provided", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Extra fields are provided",
					CheckPoint.RESULT.NOK);
			break;
		
		case MISSING_REQUIRED_VALUES:
			// 1-GTFS-Common-4 
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_REQUIRED_VALUES);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"The value \""+fieldName+"\" must be provided (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, name(filenameInfo)+"-failure", ((GtfsException) ex).getId()),
					"The value \""+fieldName+"\" must be provided",
					CheckPoint.RESULT.NOK);
			break;
			
		case MISSING_FIELD:
			// 1-GTFS-Common-4
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_FIELD);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"The file \""+filenameInfo+"\" must provide a non empty \""+name+"_id\" for each "+name+" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, name+"-failure", ((GtfsException) ex).getId()),
					"The file \""+filenameInfo+"\" must provide a non empty \""+fieldName+"\" for each "+name,
					CheckPoint.RESULT.NOK);
			throw new Exception("The file \""+filenameInfo+"\" must provide a non empty \""+fieldName+"\" for each "+name);
			
		case MISSING_REQUIRED_VALUES2:
			// 1-GTFS-Common-4-1 
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_REQUIRED_VALUES2);
			fieldName = ex.getField();
			fieldName2 = fieldName.replaceFirst("long", "short");
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"One of the valuess \""+fieldName+"\" or \""+fieldName2+"\" must be provided (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, name(filenameInfo)+"-failure", ((GtfsException) ex).getId()),
					"One of the valuess \""+fieldName+"\" or \""+fieldName2+"\" must be provided",
					CheckPoint.RESULT.NOK);
			break;
			
		case DUPLICATE_DEFAULT_KEY_FIELD:
			// 1-GTFS-Common-4-3
			checkPointName = checkPointName(name, GtfsException.ERROR.DUPLICATE_DEFAULT_KEY_FIELD);
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR,
					new FileError(FileError.CODE.INVALID_FORMAT, "At most only one Agency can have default value \"agency_id\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, name(filenameInfo)+"-failure", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"At most only one Agency can have default value \"agency_id\"",
					CheckPoint.RESULT.NOK);
			throw new Exception("At most only one Agency can have default value \"agency_id\"");
			
		case DEFAULT_VALUE:
				// 1-GTFS-Common-4-4
				checkPointName = checkPointName(name, GtfsException.ERROR.DEFAULT_VALUE);
				fieldName = ex.getField();
				report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
						new FileError(FileError.CODE.INVALID_FORMAT,
								"No default ids for agencies (rule "+checkPointName+")"));
				validationReport.addDetail(checkPointName,
						new Location(filenameInfo, "No default ids for agencies", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
						"No default ids for agencies",
						CheckPoint.RESULT.NOK);
				break;
				
				
		case MISSING_ARRIVAL_TIME:
			// 1-GTFS-Common-4-6
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_ARRIVAL_TIME);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Missing \""+fieldName+"\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Missing \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Missing \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;
				
		case MISSING_DEPARTURE_TIME:
			// 1-GTFS-Common-4-6
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_DEPARTURE_TIME);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Missing \""+fieldName+"\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Missing \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Missing \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;
		case MISSING_TRANSFER_TIME:
			// 1-GTFS-Common-4-6
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_TRANSFER_TIME);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Missing \""+fieldName+"\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Missing \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Missing \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;		

		case INVALID_COLOR:
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_COLOR);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"The value \""+fieldName+"\" is invalid (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, name(filenameInfo)+"-failure", ((GtfsException) ex).getId()),
					"The value \""+fieldName+"\" is invalid",
					CheckPoint.RESULT.NOK);
			break;
		
		case INVALID_COLOR_TEXT: // 1-GTFS-Route-9
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_COLOR_TEXT);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"The value \""+fieldName+"\" is invalid (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, name(filenameInfo)+"-failure", ((GtfsException) ex).getId()),
					"The value \""+fieldName+"\" is invalid",
					CheckPoint.RESULT.NOK);
			break;

		case INVALID_LAT:
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_LAT);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR,
					new FileError(FileError.CODE.INVALID_FORMAT, "The field \""+fieldName+"\" must have a valid value (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, name(filenameInfo)+"-failure", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"The field \""+fieldName+"\" must have a valid value",
					CheckPoint.RESULT.NOK);
			break;
		
		case INVALID_LON:
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_LON);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR,
					new FileError(FileError.CODE.INVALID_FORMAT, "The field \""+fieldName+"\" must have a valid value (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, name(filenameInfo)+"-failure", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"The field \""+fieldName+"\" must have a valid value",
					CheckPoint.RESULT.NOK);
			break;
			
		case INVALID_URL:// 1-GTFS-Agency-7, 1-GTFS-Stop-7  warning
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_URL);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid URL (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid URL", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid URL",
					CheckPoint.RESULT.NOK);
			break;
			
		case INVALID_LOCATION_TYPE: // 1-GTFS-Stop-8 error
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_LOCATION_TYPE);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid location type (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid location type", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid location type",
					CheckPoint.RESULT.NOK);
			break;
			
		case INVALID_TIMEZONE:// 1-GTFS-Agency-6, 1-GTFS-Stop-9  warning
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_TIMEZONE);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid time zone (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid time zone", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid time zone",
					CheckPoint.RESULT.NOK);
			break;
			
		case INVALID_WHEELCHAIR_BOARDING_TYPE: // 1-GTFS-Stop-10  warning
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_WHEELCHAIR_BOARDING_TYPE);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid weelchair boarding value (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid weelchair boarding value", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid weelchair boarding value",
					CheckPoint.RESULT.NOK);
			break;
		
		case INVALID_ROUTE_TYPE: // 1-GTFS-Route-6
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_ROUTE_TYPE);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid route type (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid route type", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid route type",
					CheckPoint.RESULT.NOK);
			break;
			
		case INVALID_EXCEPTION_TYPE: // 1-GTFS-CalendarDate-6
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_EXCEPTION_TYPE);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid exception type (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid exception type", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid exception type",
					CheckPoint.RESULT.NOK);
			break;

		case INVALID_DATE: // 1-GTFS-CalendarDate-5
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_DATE);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid date (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid date", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid date",
					CheckPoint.RESULT.NOK);
			break;
			
		case INVALID_STOP_SEQUENCE:// 1-GTFS-StopTime-6
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_STOP_SEQUENCE);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid \""+fieldName+"\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;
	
		case INVALID_ARRIVAL_TIME:// 1-GTFS-StopTime-7
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_ARRIVAL_TIME);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid \""+fieldName+"\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;

		case INVALID_DEPARTURE_TIME:// 1-GTFS-StopTime-8
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_DEPARTURE_TIME);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid \""+fieldName+"\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;
			
		case INVALID_PICKUP_TYPE: // 1-GTFS-StopTime-9
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_PICKUP_TYPE);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid \""+fieldName+"\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;
			
		case INVALID_DROP_OFF_TYPE: // 1-GTFS-StopTime-10
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_DROP_OFF_TYPE);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid \""+fieldName+"\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;
			
		case INVALID_SHAPE_DIST_TRAVELED: // 1-GTFS-StopTime-11
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_SHAPE_DIST_TRAVELED);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid \""+fieldName+"\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;
		
		case INVALID_TIMEPOINT: // 1-GTFS-StopTime-12
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_TIMEPOINT);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid \""+fieldName+"\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;
			
		case INVALID_DIRECTION: // 1-GTFS-Trip-5
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_DIRECTION);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid \""+fieldName+"\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;

		case INVALID_WHEELCHAIR_TYPE: // 1-GTFS-Trip-6
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_WHEELCHAIR_TYPE);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid \""+fieldName+"\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;
			
		case INVALID_BYKE_TYPE: // 1-GTFS-Trip-7
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_BYKE_TYPE);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid \""+fieldName+"\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;
			
		case INVALID_EXACT_TIMES_VALUE: // 1-GTFS-Frequency-6
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_EXACT_TIMES_VALUE);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid \""+fieldName+"\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;
			
		case INVALID_START_TIME: // 1-GTFS-Frequency-3
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_START_TIME);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid \""+fieldName+"\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;
			
		case INVALID_END_TIME: // 1-GTFS-Frequency-4
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_END_TIME);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid \""+fieldName+"\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;
			
		case INVALID_HEADWAY_SECS: // 1-GTFS-Frequency-5
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_HEADWAY_SECS);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid \""+fieldName+"\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;
			
		case INVALID_MONDAY_TYPE: // 1-GTFS-Calendar-5
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_MONDAY_TYPE);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid \""+fieldName+"\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;
			
		case INVALID_TUESDAY_TYPE: // 1-GTFS-Calendar-6
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_TUESDAY_TYPE);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid \""+fieldName+"\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;
			
		case INVALID_WEDNESDAY_TYPE: // 1-GTFS-Calendar-7
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_WEDNESDAY_TYPE);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid \""+fieldName+"\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;
			
		case INVALID_THURSDAY_TYPE: // 1-GTFS-Calendar-8
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_THURSDAY_TYPE);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid \""+fieldName+"\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;
			
		case INVALID_FRIDAY_TYPE: // 1-GTFS-Calendar-9
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_FRIDAY_TYPE);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid \""+fieldName+"\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;
			
		case INVALID_SATURDAY_TYPE: // 1-GTFS-Calendar-10
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_SATURDAY_TYPE);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid \""+fieldName+"\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;
			
		case INVALID_SUNDAY_TYPE: // 1-GTFS-Calendar-11
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_SUNDAY_TYPE);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid \""+fieldName+"\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;
			
		case INVALID_START_DATE: // 1-GTFS-Calendar-12
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_START_DATE);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid \""+fieldName+"\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;
			
		case INVALID_END_DATE: // 1-GTFS-Calendar-13
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_END_DATE);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid \""+fieldName+"\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;		

		case INVALID_TRANSFER_TYPE: // 1-GTFS-Transfer-3
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_TRANSFER_TYPE);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid \""+fieldName+"\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;		

		case INVALID_TRANSFER_TIME: // 1-GTFS-Transfer-5
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_TRANSFER_TIME);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid \""+fieldName+"\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;		

		case INVALID_SHAPE_POINT_SEQUENCE: // 1-GTFS-Shape-6
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_SHAPE_POINT_SEQUENCE);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid \""+fieldName+"\" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;

		case INVALID_FARE_URL:
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_FARE_URL);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid "+fieldName+" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;
		
		case INVALID_LANG:
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_LANG);
			fieldName = ex.getField();
			report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Invalid "+fieldName+" (rule "+checkPointName+")"));
			validationReport.addDetail(checkPointName,
					new Location(filenameInfo, "Invalid \""+fieldName+"\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Invalid \""+fieldName+"\"",
					CheckPoint.RESULT.NOK);
			break;
			
	case DUPLICATE_STOP_SEQUENCE:
		// 2-GTFS-Common-4
		checkPointName = checkPointName(name, GtfsException.ERROR.DUPLICATE_STOP_SEQUENCE);
		fieldName = ex.getField();
		report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
				new FileError(FileError.CODE.INVALID_FORMAT,
						"Duplicate \""+fieldName+"\" for the same \"tripid\" (rule "+checkPointName+")"));
		validationReport.addDetail(checkPointName,
				new Location(filenameInfo, "Duplicate \""+fieldName+"\" for the same \"tripid\"", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
				"Duplicate \""+fieldName+"\" for the same \"tripid\"",
				CheckPoint.RESULT.NOK);
		throw new Exception("Duplicate \""+fieldName+"\" for the same \"tripid\"");	
		
	case UNUSED_ID:
		// 2-GTFS-Common-2
		checkPointName = checkPointName(name, GtfsException.ERROR.UNUSED_ID);
		fieldName = ex.getField();
		report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
				new FileError(FileError.CODE.INVALID_FORMAT,
						"Unused "+fieldName+" (rule "+checkPointName+")"));
		validationReport.addDetail(checkPointName,
				new Location(filenameInfo, "Unused "+fieldName, ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
				"Unused "+fieldName,
				CheckPoint.RESULT.NOK);
		break;

	case DUPLICATE_DOUBLE_KEY:
		// 2-GTFS-Common-4
		checkPointName = checkPointName(name, GtfsException.ERROR.DUPLICATE_DOUBLE_KEY);
		fieldName = ex.getField();
		fieldName2 = "date";
		report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
				new FileError(FileError.CODE.INVALID_FORMAT,
						"Double service_id date (rule "+checkPointName+")"));
		validationReport.addDetail(checkPointName,
				new Location(filenameInfo, "Double service_id date", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
				"Double service_id date",
				CheckPoint.RESULT.NOK);
		break;
	
	case SHARED_VALUE:
		// 2-GTFS-Common-5
		checkPointName = checkPointName(name, GtfsException.ERROR.SHARED_VALUE);
		fieldName = ex.getField();
		fieldName2 = "route_long_name";
		report.addFileInfo(filenameInfo, FILE_STATE.IGNORED,
				new FileError(FileError.CODE.INVALID_FORMAT,
						"The two values "+fieldName+" and "+fieldName2+" cannot be the same (rule "+checkPointName+")"));
		validationReport.addDetail(checkPointName,
				new Location(filenameInfo, "The two values "+fieldName+" and "+fieldName2+" cannot be the same", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
				"The two values "+fieldName+" and "+fieldName2+" cannot be the same",
				CheckPoint.RESULT.NOK);
		break;

			
///////////////////////////////			

		case INVALID_FORMAT: // THIS CAN NEVER OCCUR !
		case MISSING_FOREIGN_KEY: // THIS CAN NEVER OCCUR !
		case SYSTEM: // THIS CAN NEVER OCCUR !
		default:
			break;
		}
	}

	private String checkPointName(String name, mobi.chouette.exchange.gtfs.model.importer.GtfsException.ERROR errorName) {
		name = capitalize(name);
		switch(errorName) {
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
			return GTFS_1_GTFS_Common_1_1;
		case MISSING_OPTIONAL_FILE:
			return GTFS_1_GTFS_Common_1_2;
		case UNUSED_FILE:
			return GTFS_1_GTFS_Common_1_3;
		case FILE_WITH_NO_ENTRY:
			return GTFS_1_GTFS_Common_2;
		case FILES_WITH_NO_ENTRY:
			return GTFS_1_GTFS_Common_2_1;
		case DUPLICATE_FIELD:
			return GTFS_1_GTFS_Common_3;
		case MISSING_REQUIRED_FIELDS:
			return GTFS_1_GTFS_Common_3_1;
		case MISSING_REQUIRED_FIELDS2:
			return GTFS_1_GTFS_Common_3_2;
		case MISSING_OPTIONAL_FIELD:
			return GTFS_1_GTFS_Common_3_3;
		case EXTRA_HEADER_FIELD:
			return GTFS_1_GTFS_Common_3_4;
		case MISSING_REQUIRED_VALUES:
		case MISSING_FIELD:
			return GTFS_1_GTFS_Common_4;
		case MISSING_REQUIRED_VALUES2:
			return GTFS_1_GTFS_Common_4_1;
		case DUPLICATE_DEFAULT_KEY_FIELD:
			return GTFS_1_GTFS_Common_4_3;
		case DEFAULT_VALUE:
			return GTFS_1_GTFS_Common_4_4;
		case MISSING_ARRIVAL_TIME:
		case MISSING_DEPARTURE_TIME:
		case MISSING_TRANSFER_TIME:
			return GTFS_1_GTFS_Common_4_6;
		case INVALID_FARE_URL:
		case INVALID_LANG:
		case INVALID_SHAPE_POINT_SEQUENCE:
		case INVALID_TRANSFER_TYPE:
		case INVALID_TRANSFER_TIME:
		case INVALID_MONDAY_TYPE:
		case INVALID_TUESDAY_TYPE:
		case INVALID_WEDNESDAY_TYPE:
		case INVALID_THURSDAY_TYPE:
		case INVALID_FRIDAY_TYPE:
		case INVALID_SATURDAY_TYPE:
		case INVALID_SUNDAY_TYPE:
		case INVALID_START_DATE:
		case INVALID_END_DATE:
		case INVALID_START_TIME:
		case INVALID_END_TIME:
		case INVALID_HEADWAY_SECS:
		case INVALID_EXACT_TIMES_VALUE:
		case INVALID_BYKE_TYPE:
		case INVALID_WHEELCHAIR_TYPE:
		case INVALID_DIRECTION:
		case INVALID_TIMEPOINT:
		case INVALID_SHAPE_DIST_TRAVELED:
		case INVALID_DROP_OFF_TYPE:
		case INVALID_PICKUP_TYPE:
		case INVALID_ARRIVAL_TIME:
		case INVALID_DEPARTURE_TIME:
		case INVALID_STOP_SEQUENCE:
		case INVALID_DATE:
		case INVALID_EXCEPTION_TYPE:
		case INVALID_COLOR:
		case INVALID_COLOR_TEXT:
		case INVALID_ROUTE_TYPE:
		case INVALID_LAT:
		case INVALID_LON:
		case INVALID_URL:
		case INVALID_LOCATION_TYPE:
		case INVALID_TIMEZONE:
		case INVALID_WHEELCHAIR_BOARDING_TYPE:
			return GTFS_1_GTFS_Common_5;
		case UNUSED_ID:
			return GTFS_2_GTFS_Common_2;
		case DUPLICATE_STOP_SEQUENCE:
		case DUPLICATE_DOUBLE_KEY:
			return GTFS_2_GTFS_Common_4;
		case SHARED_VALUE:
			return GTFS_2_GTFS_Common_5;
			
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
				name = (char)((int)c+(int)'A'-(int)('a')) + name;
			}
		}
		return name;
	}

	private String name(String filename) {
		if (filename != null) {
			if (filename.indexOf('.') > 0)
				filename = filename.substring(0, filename.lastIndexOf('.'));
			if (filename.endsWith("ies"))
				filename = filename.substring(0, filename.lastIndexOf('i'))+"y";
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
	
	public void reportFailure(Context context, String checkpointName, String filenameInfo1, String filenameInfo2) throws Exception {
		ActionReport report = (ActionReport) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);
		
		report.addFileInfo(filenameInfo1, FILE_STATE.ERROR,
				new FileError(FileError.CODE.FILE_NOT_FOUND, "At least one of the files \""+filenameInfo1+"\" and \""+filenameInfo2+"\" must be provided (rule "+checkpointName+")"));
		Location[] locations = new Location[2];
		locations[0] = new Location(filenameInfo1, name(filenameInfo1)+"-failure");
		locations[1] = new Location(filenameInfo2, name(filenameInfo2)+"-failure");
		validationReport.addDetail(checkpointName, locations,
				"At least one of the files \""+filenameInfo1+"\" and \""+filenameInfo2+"\" must be provided",
				CheckPoint.RESULT.NOK);
		// Stop parsing and render reports (1-GTFS-<X>-1 is fatal)
		throw new Exception("At least one of the files \""+filenameInfo1+"\" and \""+filenameInfo2+"\" must be provided");
	}

	public void validate(Context context, String filenameInfo, Set<mobi.chouette.exchange.gtfs.model.importer.GtfsException.ERROR> errorCodes) {
		if (errorCodes != null)
			for (mobi.chouette.exchange.gtfs.model.importer.GtfsException.ERROR errorCode : errorCodes) {
				validate(context, filenameInfo, errorCode);
			}
	}

	public void validate(Context context, String filenameInfo, mobi.chouette.exchange.gtfs.model.importer.GtfsException.ERROR errorCode) {
		String checkPointName = checkPointName(name(filenameInfo), errorCode);
		validate(context, filenameInfo, checkPointName);
	}
	
	public void validate(Context context, String filenameInfo, String checkPointName) {		
		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);
		
		CheckPoint checkPoint = validationReport.findCheckPointByName(checkPointName);
		
		if (checkPoint == null) {
			;
//			if ("agency.txt".equals(filenameInfo))
//				System.out.println("\t########### filenameInfo: "+filenameInfo+". ###### checkPointName: "+checkPointName);
		} else
			if (checkPoint.getState() == CheckPoint.RESULT.UNCHECK)
				checkPoint.setState(CheckPoint.RESULT.OK);
		
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
