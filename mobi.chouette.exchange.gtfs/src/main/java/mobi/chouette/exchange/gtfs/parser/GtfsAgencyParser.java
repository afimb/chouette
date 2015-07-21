package mobi.chouette.exchange.gtfs.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.gtfs.importer.Constant;
import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;
import mobi.chouette.exchange.gtfs.model.GtfsAgency;
import mobi.chouette.exchange.gtfs.model.importer.GtfsException;
import mobi.chouette.exchange.gtfs.model.importer.GtfsImporter;
import mobi.chouette.exchange.gtfs.model.importer.Index;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.Validator;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileError;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.Company;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class GtfsAgencyParser implements Parser, Validator, Constant {

	@Override
	public void parse(Context context) throws Exception {

		Referential referential = (Referential) context.get(REFERENTIAL);
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		GtfsImportParameters configuration = (GtfsImportParameters) context.get(CONFIGURATION);

		for (GtfsAgency gtfsAgency : importer.getAgencyById()) {
			String objectId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), Company.COMPANY_KEY,
					gtfsAgency.getAgencyId(), log);
			Company company = ObjectFactory.getCompany(referential, objectId);
			convert(context, gtfsAgency, company);
		}
	}

	@Override
	public void validate(Context context) throws Exception {
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		ActionReport report = (ActionReport) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);
		// agency.txt
		if (importer.hasAgencyImporter()) {
			// Add to report
			report.addFileInfo(GTFS_AGENCY_FILE, FILE_STATE.OK);
		} else {
			// Add to report
			report.addFileInfo(GTFS_AGENCY_FILE, FILE_STATE.ERROR,
					new FileError(FileError.CODE.FILE_NOT_FOUND, "The file \"agency.txt\" must be provided (rule 1-GTFS-Agency-1)"));
			// Add to validation report checkpoint 1-GTFS-Agency-1
			validationReport.addDetail(GTFS_1_GTFS_Agency_1,
					new Location(GTFS_AGENCY_FILE, "agency-failure"),
					"The file \"agency.txt\" must be provided",
					CheckPoint.RESULT.NOK);
			// Stop parsing and render reports (1-GTFS-Agency-1 is fatal)
			throw new Exception("The file \"agency.txt\" must be provided");
		}
		
		Index<GtfsAgency> parser = null;
		try { // Read and check the header line of the file "agency.txt"
			parser = importer.getAgencyById();
		} catch (Exception ex ) {
			if (ex instanceof GtfsException) {
				switch ( ((GtfsException) ex).getError() ) {
				case INVALID_HEADER_FILE_FORMAT:
					report.addFileInfo(GTFS_AGENCY_FILE, FILE_STATE.ERROR,
							new FileError(FileError.CODE.INVALID_FORMAT,
									"The first line in file \"agency.txt\" must comply with CSV (rule 1-GTFS-CSV-10"));
					validationReport.addDetail(GTFS_1_GTFS_CSV_10,
							new Location(GTFS_AGENCY_FILE, "agency-failure", ((GtfsException) ex).getId()),
							"The first line in file \"agency.txt\" must comply with CSV",
							CheckPoint.RESULT.NOK);
					throw new Exception("The first line in file \"agency.txt\" must comply with CSV");
				case EMPTY_HEADER_FIELD:
					report.addFileInfo(GTFS_AGENCY_FILE, FILE_STATE.ERROR,
							new FileError(FileError.CODE.INVALID_FORMAT,
									"Header fields in file \"agency.txt\" could not be empty (rule 1-GTFS-CSV-11"));
					validationReport.addDetail(GTFS_1_GTFS_CSV_11,
							new Location(GTFS_AGENCY_FILE, "agency-failure", ((GtfsException) ex).getId()),
							"Header fields in file \"agency.txt\" could not be empty",
							CheckPoint.RESULT.NOK);
					throw new Exception("Header fields in file \"agency.txt\" could not be empty");
				case DUPLICATE_HEADER_FIELD:
					report.addFileInfo(GTFS_AGENCY_FILE, FILE_STATE.ERROR,
							new FileError(FileError.CODE.INVALID_FORMAT,
									"The header fields in file \"agency.txt\" could not be duplicated (rule 1-GTFS-CSV-12"));
					validationReport.addDetail(GTFS_1_GTFS_CSV_12,
							new Location(GTFS_AGENCY_FILE, "agency-failure", ((GtfsException) ex).getId()),
							"The header fields in file \"agency.txt\" could not be duplicated",
							CheckPoint.RESULT.NOK);
					throw new Exception("The header fields in file \"agency.txt\" could not be duplicated");
				case FILE_WITH_NO_ENTRY:
					report.addFileInfo(GTFS_AGENCY_FILE, FILE_STATE.ERROR,
							new FileError(FileError.CODE.INVALID_FORMAT,
									"The file \"agency.txt\" must contain at least one agency definition (rule 1-GTFS-Agency-11"));
					validationReport.addDetail(GTFS_1_GTFS_Agency_11,
							new Location(GTFS_AGENCY_FILE, "agency-failure", ((GtfsException) ex).getId()),
							"The file \"agency.txt\" must contain at least one agency definition",
							CheckPoint.RESULT.NOK);
					throw new Exception("The file \"agency.txt\" must contain at least one agency definition");
				case MISSING_FIELD: // 1-GTFS-Agency-2
					report.addFileInfo(GTFS_AGENCY_FILE, FILE_STATE.ERROR,
							new FileError(FileError.CODE.INVALID_FORMAT,
									"The file \"agency.txt\" must provide a non empty \"agency_id\" for each agency (rule 1-GTFS-Agency-2"));
					validationReport.addDetail(GTFS_1_GTFS_Agency_2,
							new Location(GTFS_AGENCY_FILE, "agency-failure", ((GtfsException) ex).getId()),
							"The file \"agency.txt\" must provide a non empty \"agency_id\" for each agency",
							CheckPoint.RESULT.NOK);
					throw new Exception("The file \"agency.txt\" must provide a non empty \"agency_id\" for each agency");
				case DUPLICATE_FIELD:
					report.addFileInfo(GTFS_AGENCY_FILE, FILE_STATE.ERROR,
							new FileError(FileError.CODE.INVALID_FORMAT, "The field \"agency_id\" must be unique (rule 1-GTFS-Agency-3)"));
					validationReport.addDetail(GTFS_1_GTFS_Agency_3,
							new Location(GTFS_AGENCY_FILE, "agency-failure", ((GtfsException) ex).getId(), ((GtfsException) ex).getField()),
							"The field \"agency_id\" must be unique",
							CheckPoint.RESULT.NOK);
					throw new Exception("The field \"agency_id\" must be unique");
				case INVALID_FILE_FORMAT:
					report.addFileInfo(GTFS_AGENCY_FILE, FILE_STATE.ERROR,
							new FileError(FileError.CODE.INVALID_FORMAT,
									"Line number "+((GtfsException) ex).getId()+" in file \"agency.txt\" must comply with CSV (rule 1-GTFS-CSV-13"));
					validationReport.addDetail(GTFS_1_GTFS_CSV_13,
							new Location(GTFS_AGENCY_FILE, "agency-failure", ((GtfsException) ex).getId()),
							"Line number "+((GtfsException) ex).getId()+" in file \"agency.txt\" must comply with CSV",
							CheckPoint.RESULT.NOK);
					throw new Exception("Line number "+((GtfsException) ex).getId()+" in file \"agency.txt\" must comply with CSV");
				case MISSING_REQUIRED_FIELDS:
					report.addFileInfo(GTFS_AGENCY_FILE, FILE_STATE.ERROR,
							new FileError(FileError.CODE.INVALID_FORMAT,
									"The fields \"agency_name\", \"agency_url\" and \"agency_timezone\" must be provided (rule 1-GTFS-Agency-4"));
					validationReport.addDetail(GTFS_1_GTFS_Agency_4,
							new Location(GTFS_AGENCY_FILE, "agency-failure", ((GtfsException) ex).getId()),
							"The fields \"agency_name\", \"agency_url\" and \"agency_timezone\" must be provided",
							CheckPoint.RESULT.NOK);
					throw new Exception("The fields \"agency_name\", \"agency_url\" and \"agency_timezone\" must be provided");
				case EXTRA_SPACE_IN_HEADER_FIELD: // Don't throw an exception at this level
					report.addFileInfo(GTFS_AGENCY_FILE, FILE_STATE.IGNORED,
							new FileError(FileError.CODE.INVALID_FORMAT,
									"Extra spaces in field names are not allowed (rule 1-GTFS-CSV-7"));
					validationReport.addDetail(GTFS_1_GTFS_CSV_7,
							new Location(GTFS_1_GTFS_CSV_7, "Extra spaces in field names are not allowed", ((GtfsException) ex).getId()),
							"Extra spaces in field names are not allowed",
							CheckPoint.RESULT.NOK);
					break;
				case HTML_TAG_IN_HEADER_FIELD:
					report.addFileInfo(GTFS_AGENCY_FILE, FILE_STATE.ERROR,
							new FileError(FileError.CODE.INVALID_FORMAT,
									"HTML tags in field names are not allowed (rule 1-GTFS-CSV-6"));
					validationReport.addDetail(GTFS_1_GTFS_CSV_6,
							new Location(GTFS_1_GTFS_CSV_6, "HTML tags in field names are not allowed", ((GtfsException) ex).getId()),
							"HTML tags in field names are not allowed",
							CheckPoint.RESULT.NOK);
				default:
					throwUnknownError(report, validationReport);
//				case MISSING_FILE: // THIS CAN NEVER OCCUR ! Already checked in importer.hasAgencyImporter()
//					report.addFileInfo(GTFS_AGENCY_FILE, FILE_STATE.ERROR,
//							new FileError(FileError.CODE.FILE_NOT_FOUND, "The file \"agency.txt\" must be provided (rule 1-GTFS-Agency-1)"));
//					validationReport.addDetail(GTFS_1_GTFS_Agency_1,
//							new Location(GTFS_AGENCY_FILE, "agency-failure"),
//							"The file \"agency.txt\" must be provided",
//							CheckPoint.RESULT.NOK);
//					throw new Exception("The file \"agency.txt\" must be provided");
//				
//				case INVALID_FORMAT: // THIS CAN NEVER OCCUR !
//				case MISSING_FOREIGN_KEY: // THIS CAN NEVER OCCUR !
//				case SYSTEM: // Problem while openning file \"agency.txt\"
//				case MISSING_REQUIRED_VALUES: // This cannot occur at this place
//					;
				}
			} else {
				throwUnknownError(report, validationReport);
			}
//			mobi.chouette.exchange.gtfs.model.importer.Context exceptionContext = new mobi.chouette.exchange.gtfs.model.importer.Context();
//			exceptionContext.put(mobi.chouette.exchange.gtfs.model.importer.Context.CODE, "1-GTFS-Agency-1");
//			exceptionContext.put(mobi.chouette.exchange.gtfs.model.importer.Context.ERROR, GtfsException.ERROR.MISSING_FILE);
//			GtfsException exception = new GtfsException(exceptionContext, ex);
//			AbstractConverter.populateFileError(new FileInfo(GTFS_AGENCY_FILE, FILE_STATE.ERROR), exception);
//			throw exception;
		}
		
		if (parser == null || parser.getLength() == 0) { // importer.getAgencyById() fails for any other reason
			throwUnknownError(report, validationReport);
//			mobi.chouette.exchange.gtfs.model.importer.Context exceptionContext = new mobi.chouette.exchange.gtfs.model.importer.Context();
//			exceptionContext.put(mobi.chouette.exchange.gtfs.model.importer.Context.CODE, "1-GTFS-Agency-2");
//			exceptionContext.put(mobi.chouette.exchange.gtfs.model.importer.Context.ERROR, GtfsException.ERROR.INVALID_FILE_FORMAT);
//			GtfsException exception = new GtfsException(exceptionContext);
//			AbstractConverter.populateFileError(new FileInfo(GTFS_AGENCY_FILE, FILE_STATE.ERROR), exception);
//			throw exception;
		}
		
		try {
			for (GtfsAgency bean : parser) { // calling ...
				// TODO general report for bean.getWarnings()
				parser.validate(bean, importer);
			}
		} catch (Exception ex) {
			if (ex instanceof GtfsException) {
				switch ( ((GtfsException) ex).getError() ) {
				case MISSING_REQUIRED_VALUES:
					;
				default:
					;
				}
			}
			AbstractConverter.populateFileError(new FileInfo(GTFS_AGENCY_FILE, FILE_STATE.ERROR), ex);
			throw ex;
		}
	}

	private void throwUnknownError(ActionReport report, ValidationReport validationReport) throws Exception {
		report.addFileInfo(GTFS_AGENCY_FILE, FILE_STATE.ERROR,
				new FileError(FileError.CODE.FILE_NOT_FOUND, "A problem occured while reading the file \"agency.txt\" (rule 1-GTFS-Agency-1)"));
		validationReport.addDetail(GTFS_1_GTFS_Agency_1,
				new Location(GTFS_AGENCY_FILE, "agency-failure"),
				"A problem occured while reading the file \"agency.txt\"",
				CheckPoint.RESULT.NOK);
		throw new Exception("A problem occured while reading the file \"agency.txt\"");
	}

	private void convert(Context context, GtfsAgency gtfsAgency, Company company) {

		company.setName(AbstractConverter.getNonEmptyTrimedString(gtfsAgency.getAgencyName()));
		company.setUrl(AbstractConverter.toString(gtfsAgency.getAgencyUrl()));
		company.setPhone(AbstractConverter.getNonEmptyTrimedString(gtfsAgency.getAgencyPhone()));
		String[] token = company.getObjectId().split(":");
		company.setRegistrationNumber(token[2]);
		company.setTimeZone(AbstractConverter.toString(gtfsAgency.getAgencyTimezone()));
		company.setFilled(true);
	}

	static {
		ParserFactory.register(GtfsAgencyParser.class.getName(), new ParserFactory() {
			@Override
			protected Parser create() {
				return new GtfsAgencyParser();
			}
		});
	}

}
