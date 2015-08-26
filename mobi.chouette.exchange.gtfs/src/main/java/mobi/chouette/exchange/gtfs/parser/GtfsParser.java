package mobi.chouette.exchange.gtfs.parser;

import java.util.List;

import mobi.chouette.exchange.gtfs.importer.Constant;
import mobi.chouette.exchange.gtfs.model.importer.GtfsException;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileError;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.exchange.validation.report.ValidationReport;

public class GtfsParser implements Constant {

	protected void reportErrors(ActionReport report, ValidationReport validationReport, List<GtfsException> errors, String filename) throws Exception {
		for (GtfsException error : errors) {
			reportError(report, validationReport, error, filename);
		}
	}
	
	protected void reportError(ActionReport report, ValidationReport validationReport, GtfsException ex, String filename) throws Exception {
		switch ( ex.getError() ) {
		case INVALID_HEADER_FILE_FORMAT:
			report.addFileInfo(filename, FILE_STATE.ERROR,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"The first line in file \""+filename+"\" must comply with CSV (rule 1-GTFS-CSV-10"));
			validationReport.addDetail(GTFS_1_GTFS_CSV_10,
					new Location(filename, name(filename)+"-failure", ((GtfsException) ex).getId()),
					"The first line in file \""+filename+"\" must comply with CSV",
					CheckPoint.RESULT.NOK);
			throw new Exception("The first line in file \""+filename+"\" must comply with CSV");
		case EMPTY_HEADER_FIELD:
			report.addFileInfo(filename, FILE_STATE.ERROR,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Header fields in file \""+filename+"\" could not be empty (rule 1-GTFS-CSV-11"));
			validationReport.addDetail(GTFS_1_GTFS_CSV_11,
					new Location(filename, name(filename)+"-failure", ((GtfsException) ex).getId()),
					"Header fields in file \""+filename+"\" could not be empty",
					CheckPoint.RESULT.NOK);
			throw new Exception("Header fields in file \""+filename+"\" could not be empty");
		case DUPLICATE_HEADER_FIELD:
			report.addFileInfo(filename, FILE_STATE.ERROR,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"The header fields in file \""+filename+"\" could not be duplicated (rule 1-GTFS-CSV-12"));
			validationReport.addDetail(GTFS_1_GTFS_CSV_12,
					new Location(filename, name(filename)+"-failure", ((GtfsException) ex).getId()),
					"The header fields in file \""+filename+"\" could not be duplicated",
					CheckPoint.RESULT.NOK);
			throw new Exception("The header fields in file \""+filename+"\" could not be duplicated");
		case FILE_WITH_NO_ENTRY:
			report.addFileInfo(filename, FILE_STATE.ERROR,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"The file \""+filename+"\" must contain at least one agency definition (rule 1-GTFS-Agency-11"));
			validationReport.addDetail(GTFS_1_GTFS_Agency_11,
					new Location(filename, name(filename)+"-failure", ((GtfsException) ex).getId()),
					"The file \""+filename+"\" must contain at least one agency definition",
					CheckPoint.RESULT.NOK);
			throw new Exception("The file \""+filename+"\" must contain at least one agency definition");
		case MISSING_FIELD: // 1-GTFS-Agency-2
			report.addFileInfo(filename, FILE_STATE.ERROR,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"The file \""+filename+"\" must provide a non empty \"agency_id\" for each agency (rule 1-GTFS-Agency-2"));
			validationReport.addDetail(GTFS_1_GTFS_Agency_2,
					new Location(filename, name(filename)+"-failure", ((GtfsException) ex).getId()),
					"The file \""+filename+"\" must provide a non empty \"agency_id\" for each agency",
					CheckPoint.RESULT.NOK);
			throw new Exception("The file \""+filename+"\" must provide a non empty \"agency_id\" for each agency");
		case DUPLICATE_FIELD:
			report.addFileInfo(filename, FILE_STATE.ERROR,
					new FileError(FileError.CODE.INVALID_FORMAT, "The field \"agency_id\" must be unique (rule 1-GTFS-Agency-3)"));
			validationReport.addDetail(GTFS_1_GTFS_Agency_3,
					new Location(filename, name(filename)+"-failure", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"The field \"agency_id\" must be unique",
					CheckPoint.RESULT.NOK);
			throw new Exception("The field \"agency_id\" must be unique");
		case INVALID_FILE_FORMAT:
			report.addFileInfo(filename, FILE_STATE.ERROR,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Line number "+((GtfsException) ex).getId()+" in file \""+filename+"\" must comply with CSV (rule 1-GTFS-CSV-13"));
			validationReport.addDetail(GTFS_1_GTFS_CSV_13,
					new Location(filename, name(filename)+"-failure", ((GtfsException) ex).getId()),
					"Line number "+((GtfsException) ex).getId()+" in file \""+filename+"\" must comply with CSV",
					CheckPoint.RESULT.NOK);
			throw new Exception("Line number "+((GtfsException) ex).getId()+" in file \""+filename+"\" must comply with CSV");
		case MISSING_REQUIRED_FIELDS: // 1_GTFS_Agency_2, 1_GTFS_Agency_4, 1-GTFS-Stop-2, 1-GTFS-Route-2, 1-GTFS-StopTime-2, 1-GTFS-Trip-2, 1-GTFS-Frequency-1, 1-GTFS-Calendar-2, 1-GTFS-CalendarDate-2, 1-GTFS-Transfer-1 error
			report.addFileInfo(filename, FILE_STATE.ERROR,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"The fields \"agency_id\", \"agency_name\", \"agency_url\" and \"agency_timezone\" must be provided (rule 1-GTFS-Agency-4"));
			validationReport.addDetail(GTFS_1_GTFS_Agency_4,
					new Location(filename, name(filename)+"-failure", ((GtfsException) ex).getId()),
					"The fields \"agency_id\", \"agency_name\", \"agency_url\" and \"agency_timezone\" must be provided",
					CheckPoint.RESULT.NOK);
			throw new Exception("The fields \"agency_id\", \"agency_name\", \"agency_url\" and \"agency_timezone\" must be provided");
		case MISSING_REQUIRED_VALUES: // 1-GTFS-Agency-5 
			report.addFileInfo(filename, FILE_STATE.ERROR,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"The values \"agency_id\", \"agency_name\", \"agency_url\" and \"agency_timezone\" must be provided (rule 1-GTFS-Agency-4"));
			validationReport.addDetail(GTFS_1_GTFS_Agency_5,
					new Location(filename, name(filename)+"-failure", ((GtfsException) ex).getId()),
					"The values \"agency_id\", \"agency_name\", \"agency_url\" and \"agency_timezone\" must be provided",
					CheckPoint.RESULT.NOK);
			throw new Exception("The values \"agency_id\", \"agency_name\", \"agency_url\" and \"agency_timezone\" must be provided");
		case EXTRA_SPACE_IN_HEADER_FIELD: // Don't throw an exception at this level
			report.addFileInfo(filename, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Extra spaces in field names are not allowed (rule 1-GTFS-CSV-7"));
			validationReport.addDetail(GTFS_1_GTFS_CSV_7,
					new Location(GTFS_1_GTFS_CSV_7, "Extra spaces in field names are not allowed", ((GtfsException) ex).getId()),
					"Extra spaces in field names are not allowed",
					CheckPoint.RESULT.NOK);
			break;
		case HTML_TAG_IN_HEADER_FIELD:
			report.addFileInfo(filename, FILE_STATE.ERROR,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"HTML tags in field names are not allowed (rule 1-GTFS-CSV-6"));
			validationReport.addDetail(GTFS_1_GTFS_CSV_6,
					new Location(GTFS_1_GTFS_CSV_6, "HTML tags in field names are not allowed", ((GtfsException) ex).getId()),
					"HTML tags in field names are not allowed",
					CheckPoint.RESULT.NOK);
			break;
		case EXTRA_HEADER_FIELD: // 1_GTFS_Agency_10, 1_GTFS_Stop_11, 1-GTFS-Route-10, 1-GTFS-StopTime-12, 1-GTFS-Trip-8, 1-GTFS-Frequency-7, 1-GTFS-Calendar-14, 1-GTFS-CalendarDate-7, 1-GTFS-Transfer-6 info
			report.addFileInfo(filename, FILE_STATE.IGNORED,
					new FileError(FileError.CODE.INVALID_FORMAT,
							"Extra fields are provided (rule 1-GTFS-Agency-10"));
			validationReport.addDetail(GTFS_1_GTFS_Agency_10,
					new Location(GTFS_1_GTFS_Agency_10, "Extra fields are provided", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
					"Extra fields are provided",
					CheckPoint.RESULT.NOK);
			break;
		case INVALID_URL:// 1-GTFS-Agency-7  warning
			break;
		case INVALID_TIMEZONE:// 1-GTFS-Agency-6  warning
			break;
		case INVALID_FARE_URL:// 1-GTFS-Agency-9   warning
			break;
		case INVALID_LANG: // 1-GTFS-Agency-8   warning
			break;
			
		case INVALID_FORMAT:
			break;
		case MISSING_FILE:
			break;
		case MISSING_FOREIGN_KEY:
			break;
		case SYSTEM:
			break;
		default:
			break;

//		case MISSING_FILE: // THIS CAN NEVER OCCUR ! Already checked in importer.hasAgencyImporter()
//			report.addFileInfo(filename, FILE_STATE.ERROR,
//					new FileError(FileError.CODE.FILE_NOT_FOUND, "The file \""+filename+"\" must be provided (rule 1-GTFS-Agency-1)"));
//			validationReport.addDetail(GTFS_1_GTFS_Agency_1,
//					new Location(filename, name(filename)+"-failure"),
//					"The file \""+filename+"\" must be provided",
//					CheckPoint.RESULT.NOK);
//			throw new Exception("The file \""+filename+"\" must be provided");
//		
//		case INVALID_FORMAT: // THIS CAN NEVER OCCUR !
//		case MISSING_FOREIGN_KEY: // THIS CAN NEVER OCCUR !
//		case SYSTEM: // Problem while openning file \""+filename+"\"
//		case MISSING_REQUIRED_VALUES: // This cannot occur at this place
//			;
			
//		default:
//			throwUnknownError(report, validationReport);

		}
	}
	
	private String name(String filename) {
		if (filename != null) {
			if (filename.indexOf('.') > 0)
				filename = filename.substring(0, filename.lastIndexOf('.'));
			return filename;
		}
		return "";
	}
	
	protected void throwUnknownError(ActionReport report, ValidationReport validationReport, String filename) throws Exception {
		if (filename != null && filename.indexOf('.') > 0) {
			report.addFileInfo(filename, FILE_STATE.ERROR,
					new FileError(FileError.CODE.FILE_NOT_FOUND, "A problem occured while reading the file \""+filename+"\" (rule 1-GTFS-CSV-14)"));
			validationReport.addDetail(GTFS_1_GTFS_CSV_14,
					new Location(filename, filename.substring(0, filename.lastIndexOf('.'))+"-failure"),
					"A problem occured while reading the file \""+filename+"\"",
					CheckPoint.RESULT.NOK);
			throw new Exception("A problem occured while reading the file \""+filename+"\"");
		}
	}
}
