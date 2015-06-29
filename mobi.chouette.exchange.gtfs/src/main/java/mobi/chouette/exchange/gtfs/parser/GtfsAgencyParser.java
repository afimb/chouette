package mobi.chouette.exchange.gtfs.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.gtfs.Constant;
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
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		
		// agency.txt
		if (importer.hasAgencyImporter()) {
			FileInfo file = new FileInfo(GTFS_AGENCY_FILE, FILE_STATE.OK);
			report.getFiles().add(file);
		} else {
			// 1-GTFS-Agency-1 The file agency.txt must be provided - fatal
			FileInfo file = new FileInfo(GTFS_AGENCY_FILE, FILE_STATE.ERROR);
			file.addError(new FileError(FileError.CODE.FILE_NOT_FOUND, "The file agency.txt must be provided (rule 1-GTFS-Agency-1)"));
			report.getFiles().add(file);
			////validationReport.getCheckPoints();
		}
		
		Index<GtfsAgency> parser = null;
		try {
			parser = importer.getAgencyById();
		} catch (Exception ex ) {
			// 1-GTFS-Agency-1 The file agency.txt must be provided - fatal
			//file.setStatus(FILE_STATE.ERROR);
			//file.addError(new FileError(FileError.CODE.FILE_NOT_FOUND, "1-GTFS-Agency-1: The file agency.txt must be provided"));
			mobi.chouette.exchange.gtfs.model.importer.Context exceptionContext = new mobi.chouette.exchange.gtfs.model.importer.Context();
			exceptionContext.put(mobi.chouette.exchange.gtfs.model.importer.Context.CODE, "1-GTFS-Agency-1");
			exceptionContext.put(mobi.chouette.exchange.gtfs.model.importer.Context.ERROR, GtfsException.ERROR.MISSING_FILE);
			GtfsException exception = new GtfsException(exceptionContext, ex);
			AbstractConverter.populateFileError(file, exception);
			throw exception;
		}
		if (parser == null || parser.getLength() == 0) {
			// 1-GTFS-Agency-2 There must exists at least one agency - fatal
			//file.setStatus(FILE_STATE.ERROR);
			//file.addError(new FileError(FileError.CODE.INVALID_FORMAT, "1-GTFS-Agency-2: There must exists at least one agency"));
			mobi.chouette.exchange.gtfs.model.importer.Context exceptionContext = new mobi.chouette.exchange.gtfs.model.importer.Context();
			exceptionContext.put(mobi.chouette.exchange.gtfs.model.importer.Context.CODE, "1-GTFS-Agency-2");
			exceptionContext.put(mobi.chouette.exchange.gtfs.model.importer.Context.ERROR, GtfsException.ERROR.INVALID_FILE_FORMAT);
			GtfsException exception = new GtfsException(exceptionContext);
			AbstractConverter.populateFileError(file, exception);
			throw exception;
		}
		try {
			for (GtfsAgency bean : parser) {
				parser.validate(bean, importer);
			}
		} catch (Exception ex) {
			AbstractConverter.populateFileError(file, ex);
			throw ex;
		}
	}

	public void convert(Context context, GtfsAgency gtfsAgency, Company company) {

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
