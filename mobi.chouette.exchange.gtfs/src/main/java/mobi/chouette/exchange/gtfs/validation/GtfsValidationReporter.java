package mobi.chouette.exchange.gtfs.validation;

//import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;
import mobi.chouette.exchange.gtfs.model.importer.AgencyById;
import mobi.chouette.exchange.gtfs.model.importer.CalendarByService;
import mobi.chouette.exchange.gtfs.model.importer.CalendarDateByService;
import mobi.chouette.exchange.gtfs.model.importer.GtfsException;
import mobi.chouette.exchange.gtfs.model.importer.GtfsExceptionsHashSet;
import mobi.chouette.exchange.gtfs.model.importer.RouteById;
import mobi.chouette.exchange.gtfs.model.importer.ShapeById;
import mobi.chouette.exchange.gtfs.model.importer.StopById;
import mobi.chouette.exchange.gtfs.model.importer.TransferByFromStop;
import mobi.chouette.exchange.gtfs.model.importer.TripById;
import mobi.chouette.exchange.gtfs.parser.AbstractConverter;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.ActionReporter.FILE_ERROR_CODE;
import mobi.chouette.exchange.report.ActionReporter.FILE_STATE;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.Company;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.Line;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
//import mobi.chouette.exchange.validation.report.ValidationReport2;

@Log4j
public class GtfsValidationReporter implements Constant {

	@Getter
	private Set<GtfsException> exceptions = new GtfsExceptionsHashSet<GtfsException>();

	public GtfsValidationReporter(Context context)
	{
//		ValidationReport2 validationReport = (ValidationReport2)context.get(VALIDATION_REPORT);
//		if (validationReport == null) {
//			validationReport = new ValidationReport2();
//			context.put(VALIDATION_REPORT, validationReport);
//		}
// 		validationReport.setMaxByFile(true);
		ValidationReporter reporter = ValidationReporter.Factory.getInstance();
		reporter.addItemToValidationReport(context, "1-GTFS-", "CSV", 7, "E","E","E","E","E","E","W");
		reporter.addItemToValidationReport(context, "1-GTFS-", "Common", 16, "E","E","W","W","E","E","W","E","E","W","W","E","E","W","E","E");
		reporter.addItemToValidationReport(context, "1-GTFS-", "Calendar", 2, "W","E");
		reporter.addItemToValidationReport(context, "1-GTFS-", "Route", 2, "E","E");

		reporter.addItemToValidationReport(context, "2-GTFS-", "Common", 4, "E","W","E","W");
		reporter.addItemToValidationReport(context, "2-GTFS-", "Stop", 4, "E","W","E","E");
		reporter.addItemToValidationReport(context, "2-GTFS-", "Route", 4, "W","W","W","W");
}
	
	public void dispose() {
		exceptions.clear();
		exceptions = null;
	}

	public void throwUnknownError(Context context, Exception ex, String filenameInfo) throws Exception {
		ActionReporter reporter = ActionReporter.Factory.getInstance();
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		String name = name(filenameInfo);
		String checkPointName = checkPointName(name, GtfsException.ERROR.SYSTEM);

		if (filenameInfo != null && filenameInfo.indexOf('.') > 0) {
			reporter.addFileErrorInReport(context, filenameInfo, FILE_ERROR_CODE.FILE_NOT_FOUND, "A problem occured while reading the file \"" + filenameInfo + "\" (" + checkPointName + ") : "
					+ ex.getMessage());
			validationReporter.addCheckPointReportError(context, checkPointName, new DataLocation(filenameInfo), ex.getMessage());
			String message = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getName();
			log.error(ex, ex);
			throw new Exception("A problem occured while reading the file \"" + filenameInfo + "\" : " + message);
		}
	}

	public void validateUnknownError(Context context) {
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validationReporter.reportSuccess(context, GTFS_1_GTFS_CSV_1);
	}

	public void reportErrors(Context context, String routeId,  Set<GtfsException> errors, String filename) throws Exception {
		for (GtfsException error : errors) {
			reportError(context, routeId , error, filename);
		}
	}
	
	private DataLocation buildDataLocation(Context context, DataLocation loc, String gtfsRouteId)
	{
		String fileName = loc.getFilename();
		if (fileName.equals(RouteById.FILENAME))
		{
			if (gtfsRouteId != null)
			{
				GtfsImportParameters configuration = (GtfsImportParameters) context.get(CONFIGURATION);
				String lineId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), Line.LINE_KEY,
						gtfsRouteId, log);
			    loc.getPath().add(loc.new Path(Line.class.getSimpleName(),lineId));
			}
		}
		else if (fileName.equals(AgencyById.FILENAME))
		{
			loc.getPath().add(loc.new Path(Company.class.getSimpleName(),""));
		}
		else if (fileName.equals(StopById.FILENAME))
		{
			loc.getPath().add(loc.new Path(StopArea.class.getSimpleName(),""));
			
		}
		else if (fileName.equals(TripById.FILENAME))
		{
			loc.getPath().add(loc.new Path(VehicleJourney.class.getSimpleName(),""));
			if (gtfsRouteId != null)
			{
				GtfsImportParameters configuration = (GtfsImportParameters) context.get(CONFIGURATION);
				String lineId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), Line.LINE_KEY,
						gtfsRouteId, log);
			    loc.getPath().add(loc.new Path(Line.class.getSimpleName(),lineId));	
			}
			
		}
		else if (fileName.equals(CalendarByService.FILENAME) || fileName.equals(CalendarDateByService.FILENAME))
		{
			loc.getPath().add(loc.new Path(Timetable.class.getSimpleName(),""));
			
		}
		else if (fileName.equals(ShapeById.FILENAME))
		{
			if (gtfsRouteId != null)
			{
				GtfsImportParameters configuration = (GtfsImportParameters) context.get(CONFIGURATION);
				String lineId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), Line.LINE_KEY,
						gtfsRouteId, log);
			    loc.getPath().add(loc.new Path(Line.class.getSimpleName(),lineId));	
			}
			
		}
		else if (fileName.equals(TransferByFromStop.FILENAME))
		{
			loc.getPath().add(loc.new Path(ConnectionLink.class.getSimpleName(),""));
			
		}
		return loc;
	}

	public void reportError(Context context, String routeId, GtfsException ex, String filenameInfo) throws Exception {
		if (!exceptions.add(ex))
			return;
		ActionReporter reporter = ActionReporter.Factory.getInstance();
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
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
			reporter.addFileErrorInReport(context, filenameInfo, FILE_ERROR_CODE.INVALID_FORMAT,
					"The first line in file \"" + filenameInfo + "\" must comply with CSV (rule " + checkPointName
							+ ")");
			validationReporter.addCheckPointReportError(context, checkPointName, new DataLocation(filenameInfo, 1, ex.getColumn()), filenameInfo);
			throw ex;
			// throw new Exception("The first line in file \"" + filenameInfo +
			// "\" must comply with CSV");

		case EMPTY_HEADER_FIELD:
			// 1-GTFS-CSV-3
			checkPointName = checkPointName(name, GtfsException.ERROR.EMPTY_HEADER_FIELD);
			reporter.addFileErrorInReport(context, filenameInfo, FILE_ERROR_CODE.INVALID_FORMAT,
					"Header fields in file \"" + filenameInfo + "\" could not be empty (rule " + checkPointName + ")");
			validationReporter.addCheckPointReportError(context, checkPointName, new DataLocation(filenameInfo, 1, ex.getColumn()), filenameInfo);
			throw ex;
			// throw new Exception("Header fields in file \"" + filenameInfo +
			// "\" could not be empty");

		case DUPLICATE_HEADER_FIELD:
			// 1-GTFS-CSV-4
			checkPointName = checkPointName(name, GtfsException.ERROR.DUPLICATE_HEADER_FIELD);
			reporter.addFileErrorInReport(context, filenameInfo, FILE_ERROR_CODE.INVALID_FORMAT,
					"The header fields in file \"" + filenameInfo + "\" could not be duplicated (rule "
							+ checkPointName + ")");
			validationReporter.addCheckPointReportError(context,checkPointName, new DataLocation(filenameInfo, 1, ex.getColumn()), filenameInfo);
			throw ex;
			// throw new Exception("The header fields in file \"" + filenameInfo
			// + "\" could not be duplicated");

		case INVALID_FILE_FORMAT:
			// 1-GTFS-CSV-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_FILE_FORMAT);
			reporter.addFileErrorInReport(context, filenameInfo, FILE_ERROR_CODE.INVALID_FORMAT,
					"Line number " + ex.getId() + " in file \"" + filenameInfo + "\" must comply with CSV (rule "
							+ checkPointName + ")");
			validationReporter.addCheckPointReportError(context,checkPointName, new DataLocation(filenameInfo, ex.getId(), ex.getColumn()),
					filenameInfo);
			throw ex;
			// throw new Exception("Line number " + ex.getId() + " in file \"" +
			// filenameInfo + "\" must comply with CSV");

		case HTML_TAG_IN_HEADER_FIELD:
			// 1-GTFS-CSV-6
			checkPointName = checkPointName(name, GtfsException.ERROR.HTML_TAG_IN_HEADER_FIELD);
			reporter.addFileErrorInReport(context, filenameInfo, FILE_ERROR_CODE.INVALID_FORMAT,
					"HTML tags in field names are not allowed (rule " + checkPointName + ")");
			validationReporter.addCheckPointReportError(context,checkPointName, new DataLocation(filenameInfo, ex.getId(), ex.getColumn()),
					filenameInfo);
			throw ex;
			// break;

		case EXTRA_SPACE_IN_FIELD: // Don't throw an exception at this level
		case EXTRA_SPACE_IN_HEADER_FIELD: // Don't throw an exception at this
											// level
			// 1-GTFS-CSV-7
			checkPointName = checkPointName(name, GtfsException.ERROR.EXTRA_SPACE_IN_HEADER_FIELD);
			validationReporter.addCheckPointReportError(context,checkPointName, new DataLocation(filenameInfo, ex.getId(), ex.getColumn()),
					ex.getValue(), ex.getField());
			break;

		case MISSING_FILE:
			// 1-GTFS-Common-1
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_FILE);
			reporter.addFileReport(context, filenameInfo, IO_TYPE.INPUT);
			reporter.addFileErrorInReport(context, filenameInfo, FILE_ERROR_CODE.FILE_NOT_FOUND,
					"The file \"" + filenameInfo + "\" must be provided (rule " + checkPointName + ")");
			validationReporter.addCheckPointReportError(context,checkPointName, new DataLocation(filenameInfo), filenameInfo);
			throw ex;
			// throw new Exception("The file \"" + filenameInfo +
			// "\" must be provided");

		case MISSING_FILES:
			// 1-GTFS-Common-1-1
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_FILES);
			filenameInfo = "calendar.txt";
			filenameInfo2 = "calendar_dates.txt";
			reporter.addFileReport(context, filenameInfo, IO_TYPE.INPUT);
			reporter.addFileErrorInReport(context, filenameInfo, FILE_ERROR_CODE.FILE_NOT_FOUND,
					"One of the files \"" + filenameInfo + "\" or \"" + filenameInfo2 + "\"must be provided (rule "
							+ checkPointName + ")");
			validationReporter.addCheckPointReportError(context,checkPointName, new DataLocation(filenameInfo), filenameInfo + "," + filenameInfo2);
			throw ex;
			// throw new Exception("The file \"" + filenameInfo +
			// "\" must be provided");

		case MISSING_OPTIONAL_FILE:
			// 1-GTFS-Common-1-2
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_OPTIONAL_FILE);
			reporter.addFileReport(context, filenameInfo, IO_TYPE.INPUT);
			reporter.setFileState(context, filenameInfo, IO_TYPE.INPUT, FILE_STATE.IGNORED);
			validationReporter.addCheckPointReportError(context,checkPointName, new DataLocation(filenameInfo), filenameInfo);
			break;

		case UNUSED_FILE:
			// 1-GTFS-Common-1-3
			checkPointName = checkPointName(name, GtfsException.ERROR.UNUSED_FILE);
			reporter.addFileReport(context, filenameInfo, IO_TYPE.INPUT);
			reporter.setFileState(context, filenameInfo, IO_TYPE.INPUT, FILE_STATE.IGNORED);
			validationReporter.addCheckPointReportError(context,checkPointName, new DataLocation(filenameInfo), filenameInfo);
			break;

		case FILE_WITH_NO_ENTRY: // 1-GTFS-Agency-11, 1-GTFS-Stop-12,
									// 1-GTFS-Route-11 error
			// 1-GTFS-Common-2
			checkPointName = checkPointName(name, GtfsException.ERROR.FILE_WITH_NO_ENTRY);
			reporter.addFileErrorInReport(context, filenameInfo, FILE_ERROR_CODE.INVALID_FORMAT,
					"The file \"" + filenameInfo + "\" must contain at least one entry (rule " + checkPointName + ")");
			validationReporter.addCheckPointReportError(context,checkPointName, new DataLocation(filenameInfo, ex.getId(), 0), "");
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
			reporter.addFileErrorInReport(context, filenameInfo, FILE_ERROR_CODE.INVALID_FORMAT,
					"One of the files \"" + filenameInfo + "\" or \"" + filenameInfo2
							+ "\" must contain at least one entry (rule " + checkPointName + ")");
			validationReporter.addCheckPointReportError(context,checkPointName, new DataLocation(filenameInfo, ex.getId(), 0), ""
					);
			throw ex;
			// throw new Exception("One of the files \"" + filenameInfo +
			// "\" or \"" + filenameInfo2 +
			// "\" must contain at least one entry");

		case DUPLICATE_FIELD:
			// 1-GTFS-Common-3
			checkPointName = checkPointName(name, GtfsException.ERROR.DUPLICATE_FIELD);
			fieldName = ex.getField();
			reporter.addFileErrorInReport(context, filenameInfo, FILE_ERROR_CODE.INVALID_FORMAT,
					"The field \"" + fieldName + "\" must be unique (rule " + checkPointName + ")");
			validationReporter.addCheckPointReportError(context,checkPointName, new DataLocation(filenameInfo, ex.getId(), ex.getColumn()),
					ex.getValue(), fieldName);
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
			reporter.addFileErrorInReport(context, filenameInfo, FILE_ERROR_CODE.INVALID_FORMAT,
					"The column \"" + fieldName + "\" of file \"" + filenameInfo + "\" must be provided (rule "
							+ checkPointName + ")");
			validationReporter.addCheckPointReportError(context,checkPointName, buildDataLocation(context,new DataLocation(filenameInfo, ex.getId(), ex.getColumn()),routeId),
					fieldName);
			throw ex;
			// throw new Exception("The column \"" + fieldName + "\" of file \""
			// + filenameInfo + "\" must be provided");

		case MISSING_REQUIRED_FIELDS2:
			// 1-GTFS-Common-3-2
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_REQUIRED_FIELDS2);
			fieldName = ex.getField();
			fieldName2 = fieldName.replaceFirst("long", "short");
			reporter.addFileErrorInReport(context, filenameInfo, FILE_ERROR_CODE.INVALID_FORMAT,
					"One of the fields \"" + fieldName + "\" or \"" + fieldName2 + "\" must be provided (rule "
							+ checkPointName + ")");
			validationReporter.addCheckPointReportError(context,checkPointName, new DataLocation(filenameInfo, ex.getId(), ex.getColumn()),
					fieldName + "," + fieldName2);
			break;

		case MISSING_OPTIONAL_FIELD:
			// 1-GTFS-Common-3-3
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_OPTIONAL_FIELD);
			reporter.addFileErrorInReport(context, filenameInfo, FILE_ERROR_CODE.INVALID_FORMAT,
					"Optional field is not present (rule " + checkPointName + ")");
			validationReporter.addCheckPointReportError(context,checkPointName, new DataLocation(filenameInfo, ex.getId(), ex.getColumn()),
					ex.getField());
			break;

		case EXTRA_HEADER_FIELD: // 1_GTFS_Agency_10, 1_GTFS_Stop_11,
									// 1-GTFS-Route-10, 1-GTFS-StopTime-12,
									// 1-GTFS-Trip-8, 1-GTFS-Frequency-7,
									// 1-GTFS-Calendar-14,
									// 1-GTFS-CalendarDate-7, 1-GTFS-Transfer-6
									// info
			// 1-GTFS-Common-3-4
			checkPointName = checkPointName(name, GtfsException.ERROR.EXTRA_HEADER_FIELD);
			validationReporter.addCheckPointReportError(context,checkPointName, new DataLocation(filenameInfo, ex.getId(), ex.getColumn()),
					ex.getField());
			break;

		case MISSING_REQUIRED_VALUES:
			// 1-GTFS-Common-4
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_REQUIRED_VALUES);
			fieldName = ex.getField();
			reporter.addFileErrorInReport(context, filenameInfo, FILE_ERROR_CODE.INVALID_FORMAT,
					"The value \"" + fieldName + "\" must be provided (rule " + checkPointName + ")");
			validationReporter.addCheckPointReportError(context,checkPointName, buildDataLocation(context,new DataLocation(filenameInfo, ex.getId(), ex.getColumn()),routeId),
					fieldName);
			break;

		case MISSING_FIELD:
			// 1-GTFS-Common-4
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_FIELD);
			fieldName = ex.getField();
			reporter.addFileErrorInReport(context, filenameInfo, FILE_ERROR_CODE.INVALID_FORMAT,
					"The file \"" + filenameInfo + "\" must provide a non empty \"" + name + "_id\" for each " + name
							+ " (rule " + checkPointName + ")");
			validationReporter.addCheckPointReportError(context,checkPointName, new DataLocation(filenameInfo, ex.getId(), ex.getColumn()),
					fieldName);
			throw ex;
			// throw new Exception("The file \"" + filenameInfo +
			// "\" must provide a non empty \"" + fieldName + "\" for each " +
			// name);

		case MISSING_REQUIRED_VALUES2:
			// 1-GTFS-Common-4-1
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_REQUIRED_VALUES2);
			fieldName = ex.getField();
			fieldName2 = fieldName.replaceFirst("long", "short");
			reporter.addFileErrorInReport(context, filenameInfo, FILE_ERROR_CODE.INVALID_FORMAT,
					"One of the values \"" + fieldName + "\" or \"" + fieldName2 + "\" must be provided (rule "
							+ checkPointName + ")");
			validationReporter.addCheckPointReportError(context,checkPointName, buildDataLocation(context,new DataLocation(filenameInfo, ex.getId(), ex.getColumn()),routeId),
					fieldName + "," + fieldName2);
			break;

		case ALL_DAYS_ARE_INVALID:
			// 1-GTFS-Calendar-1
			checkPointName = checkPointName(name, GtfsException.ERROR.ALL_DAYS_ARE_INVALID);
			fieldName = ex.getField();
			reporter.addFileErrorInReport(context, filenameInfo, FILE_ERROR_CODE.INVALID_FORMAT,
					"At least one day must be valid (rule " + checkPointName + ")");
			validationReporter.addCheckPointReportError(context,checkPointName, buildDataLocation(context,new DataLocation(filenameInfo, ex.getId(), ex.getColumn()),routeId),
					"At least one day must be valid");
			break;

		case DUPLICATE_DEFAULT_KEY_FIELD:
			// 1-GTFS-Common-4-3
			checkPointName = checkPointName(name, GtfsException.ERROR.DUPLICATE_DEFAULT_KEY_FIELD);
			reporter.addFileErrorInReport(context, filenameInfo, FILE_ERROR_CODE.INVALID_FORMAT,
					"At most only one Agency can have default value \"agency_id\" (rule " + checkPointName + ")");
			validationReporter.addCheckPointReportError(context,checkPointName, buildDataLocation(context,new DataLocation(filenameInfo, ex.getId(), ex.getColumn()),routeId),
					ex.getField());
			throw ex;

		case DEFAULT_VALUE:
			// 1-GTFS-Common-4-4
			checkPointName = checkPointName(name, GtfsException.ERROR.DEFAULT_VALUE);
			fieldName = ex.getField();
			validationReporter.addCheckPointReportError(context,checkPointName, buildDataLocation(context,new DataLocation(filenameInfo, ex.getId(), ex.getColumn()),routeId),
					fieldName);
			break;

		case MISSING_ARRIVAL_TIME:
			// 1-GTFS-Common-4-6
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_ARRIVAL_TIME);
			fieldName = ex.getField();
			validationReporter.addCheckPointReportError(context,checkPointName, "1", buildDataLocation(context,new DataLocation(filenameInfo, ex.getId(), ex.getColumn()),routeId),
					fieldName);
			break;

		case MISSING_DEPARTURE_TIME:
			// 1-GTFS-Common-4-6
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_DEPARTURE_TIME);
			fieldName = ex.getField();
			validationReporter.addCheckPointReportError(context,checkPointName, "2", buildDataLocation(context,new DataLocation(filenameInfo, ex.getId(), ex.getColumn()),routeId),
					fieldName);
			break;
		case MISSING_TRANSFER_TIME:
			// 1-GTFS-Common-4-6
			checkPointName = checkPointName(name, GtfsException.ERROR.MISSING_TRANSFER_TIME);
			fieldName = ex.getField();
			validationReporter.addCheckPointReportError(context,checkPointName, "3", buildDataLocation(context,new DataLocation(filenameInfo, ex.getId(), ex.getColumn()),routeId),
					fieldName);
			break;

		case START_DATE_AFTER_END_DATE:
			// 1-GTFS-Calendar-2
			checkPointName = checkPointName(name, GtfsException.ERROR.START_DATE_AFTER_END_DATE);
			fieldName = ex.getField();
			validationReporter.addCheckPointReportError(context,checkPointName, buildDataLocation(context,new DataLocation(filenameInfo, ex.getId(), ex.getColumn()),routeId),
					fieldName);
			break;

		case INVALID_FORMAT:
			// 1-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.INVALID_FORMAT);
			fieldName = ex.getField();
			value = ex.getValue();
			reporter.addFileErrorInReport(context, filenameInfo, FILE_ERROR_CODE.INVALID_FORMAT,
					"The value \"" + value + "\" is invalid for field " + fieldName + " (rule " + checkPointName + ")");
			validationReporter.addCheckPointReportError(context,checkPointName, buildDataLocation(context,new DataLocation(filenameInfo, ex.getId(), ex.getColumn()),routeId), value,
					fieldName);
			break;

		case DUPLICATE_STOP_SEQUENCE:
			// 2-GTFS-Common-4
			checkPointName = checkPointName(name, GtfsException.ERROR.DUPLICATE_STOP_SEQUENCE);
			fieldName = ex.getField();
			validationReporter.addCheckPointReportError(context,checkPointName,
					buildDataLocation(context,new DataLocation(filenameInfo, ex.getId(), ex.getColumn(), ex.getField()),routeId), ex.getValue(),
					ex.getField());
			throw ex;
			// throw new
			// Exception("Duplicate \""+fieldName+"\" for the same \"tripid\"");

		case UNREFERENCED_ID:
			// 2-GTFS-Common-1
			checkPointName = checkPointName(name, GtfsException.ERROR.UNREFERENCED_ID);
			fieldName = ex.getField();
			validationReporter.addCheckPointReportError(context,checkPointName, buildDataLocation(context,new DataLocation(filenameInfo, fieldName, ex.getId(),
					ex.getColumn(), ex.getCode()),routeId), ex.getValue(), ex.getField());
			break;

		case UNUSED_ID:
			// 2-GTFS-Common-2
			checkPointName = checkPointName(name, GtfsException.ERROR.UNUSED_ID);
			fieldName = ex.getField();
			validationReporter.addCheckPointReportError(context,checkPointName, buildDataLocation(context,new DataLocation(filenameInfo, ex.getId(), ex.getColumn()),routeId),
					ex.getValue(), fieldName);
			break;

		case DUPLICATE_DOUBLE_KEY:
			// 2-GTFS-Common-4
			checkPointName = checkPointName(name, GtfsException.ERROR.DUPLICATE_DOUBLE_KEY);
			fieldName = ex.getField();
			fieldName2 = "date";
			reporter.addFileErrorInReport(context, filenameInfo, FILE_ERROR_CODE.INVALID_FORMAT,
					"Duplicate (" + fieldName + ") values (rule " + checkPointName + ")");
			validationReporter.addCheckPointReportError(context,checkPointName,
					buildDataLocation(context,new DataLocation(filenameInfo, ex.getId(), ex.getColumn(), ex.getField()),routeId), ex.getValue(),
					ex.getField());
			throw ex;
			// break;

		case SHARED_VALUE:
			// 2-GTFS-Common-5
			checkPointName = checkPointName(name, GtfsException.ERROR.SHARED_VALUE);
			validationReporter.addCheckPointReportError(context,checkPointName,
					buildDataLocation(context,new DataLocation(filenameInfo, ex.getId(), ex.getColumn(), ex.getCode()),routeId), ex.getValue(), ex.getField());
			break;

		case BAD_REFERENCED_ID:
			// 2-GTFS-Stop-1
			checkPointName = checkPointName(name, GtfsException.ERROR.BAD_REFERENCED_ID);
			fieldName = ex.getField();
			validationReporter.addCheckPointReportError(context,checkPointName,
					buildDataLocation(context,new DataLocation(filenameInfo, ex.getId(), ex.getColumn(), ex.getCode()),routeId), ex.getValue());
			break;

		case NO_LOCATION_TYPE:
			// 2-GTFS-Stop-2
			checkPointName = checkPointName(name, GtfsException.ERROR.NO_LOCATION_TYPE);
			fieldName = ex.getField();
			validationReporter.addCheckPointReportError(context,checkPointName, buildDataLocation(context,new DataLocation(filenameInfo,
					filenameInfo, ex.getId(), ex.getColumn(), ex.getField()),routeId));
			break;

		case BAD_VALUE:
			// 2-GTFS-Stop-3
			checkPointName = checkPointName(name, GtfsException.ERROR.BAD_VALUE);
			fieldName = ex.getField();
			validationReporter.addCheckPointReportError(context,checkPointName,
					buildDataLocation(context,new DataLocation(filenameInfo, ex.getId(), ex.getColumn(), ex.getCode()),routeId), ex.getValue());
			throw ex;
			// break;

		case NO_PARENT_FOR_STATION:
			// 2-GTFS-Stop-4
			checkPointName = checkPointName(name, GtfsException.ERROR.NO_PARENT_FOR_STATION);
			fieldName = ex.getField();
			validationReporter.addCheckPointReportError(context,checkPointName,
					buildDataLocation(context,new DataLocation(filenameInfo, ex.getId(), ex.getColumn(), ex.getCode()),routeId), ex.getValue());
			break;

		case DUPLICATE_ROUTE_NAMES:
			// 2-GTFS-Route-5
			checkPointName = checkPointName(name, GtfsException.ERROR.DUPLICATE_ROUTE_NAMES);
			fieldName = ex.getField();
			validationReporter.addCheckPointReportError(context,checkPointName,
					buildDataLocation(context,new DataLocation(filenameInfo, ex.getId(), ex.getColumn(), ex.getCode()),routeId), ex.getValue());
			break;

		case CONTAINS_ROUTE_NAMES:
			// 2-GTFS-Route-8
			checkPointName = checkPointName(name, GtfsException.ERROR.CONTAINS_ROUTE_NAMES);
			fieldName = ex.getField();
			validationReporter.addCheckPointReportError(context,checkPointName,
					buildDataLocation(context,new DataLocation(filenameInfo, ex.getId(), ex.getColumn(), ex.getCode()),routeId), ex.getValue(),
					ex.getRefValue());
			break;

		case BAD_COLOR:
			// 2-GTFS-Route-9
			checkPointName = checkPointName(name, GtfsException.ERROR.BAD_COLOR);
			fieldName = ex.getField();
			validationReporter.addCheckPointReportError(context,checkPointName,
					buildDataLocation(context,new DataLocation(filenameInfo, ex.getId(), ex.getColumn(), ex.getCode()),routeId), "");
			break;

		case INVERSE_DUPLICATE_ROUTE_NAMES:
			// 2-GTFS-Route-11
			checkPointName = checkPointName(name, GtfsException.ERROR.INVERSE_DUPLICATE_ROUTE_NAMES);
			fieldName = ex.getField();
			validationReporter.addCheckPointReportError(context,checkPointName,
					buildDataLocation(context,new DataLocation(filenameInfo, ex.getId(), ex.getColumn(), ex.getCode()),routeId), ex.getValue());
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
		ActionReporter reporter = ActionReporter.Factory.getInstance();
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();

		reporter.setFileState(context, filenameInfo, IO_TYPE.INPUT,FILE_STATE.OK);
		validationReporter.reportSuccess(context, checkpointName);
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
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validationReporter.reportSuccess(context, checkPointName);

	}

//	public void updateValidationReport(Context context, String filename, String gtfsid, Line line) {
//		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
//		for (CheckPoint checkpoint : validationReport.getCheckPoints()) {
//			if (checkpoint.getDetailCount() > 0) {
//				for (Detail detail : checkpoint.getDetails()) {
//					if (detail.getSource() != null && detail.getSource().getFile() != null
//							&& filename.equals(detail.getSource().getFile().getFilename())) {
//						if (gtfsid.equals(detail.getSource().getObjectId())) {
//                              detail.getSource().setLine(new LineLocation(line));
//						}
//					}
//				}
//			}
//		}
//	}

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

	public void reportError(Context context, GtfsException gtfsException, String fileName) throws Exception {
		reportError(context,null,gtfsException,fileName);
		
	}

	public void reportErrors(Context context, Set<GtfsException> errors, String fileName) throws Exception {
		reportErrors(context,null,errors,fileName);
		
	}
}
