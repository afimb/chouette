package mobi.chouette.exchange.gtfs.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.gtfs.importer.Constant;
import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;
import mobi.chouette.exchange.gtfs.model.GtfsAgency;
import mobi.chouette.exchange.gtfs.model.importer.GtfsException;
import mobi.chouette.exchange.gtfs.model.importer.GtfsImporter;
import mobi.chouette.exchange.gtfs.model.importer.Index;
import mobi.chouette.exchange.gtfs.model.importer.IndexFactory;
import mobi.chouette.exchange.gtfs.model.importer.IndexImpl;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.Validator;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileError;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.Company;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class GtfsAgencyParser extends GtfsParser implements Parser, Validator, Constant {

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
		if (importer.hasAgencyImporter()) { // the file "agency.txt" exists ?
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
			parser = importer.getAgencyById(); // return new AgencyById("/.../agency.txt") { /** super(...) */
			//   IndexImpl<GtfsAgency>(_path = "/.../agency.txt", _key = "agency_id", _value = "default", _unique = true) {
			//     initialize() /** read the first line of file _path */
			//   }
			// }
		} catch (Exception ex ) {
			if (ex instanceof GtfsException) {
				reportError(report, validationReport, (GtfsException)ex, GTFS_AGENCY_FILE);
			} else {
				throwUnknownError(report, validationReport, GTFS_AGENCY_FILE);
			}
		}
		
		if (parser == null || parser.getLength() == 0) { // importer.getAgencyById() fails for any other reason
			throwUnknownError(report, validationReport, GTFS_AGENCY_FILE);
		}

		parser.getErrors().clear();
		
		try {
			for (GtfsAgency bean : parser) {
				parser.validate(bean, importer);
				reportErrors(report, validationReport, bean.getErrors(), GTFS_AGENCY_FILE);
			}
		} catch (Exception ex) {
			if (ex instanceof GtfsException) {
				reportError(report, validationReport, (GtfsException)ex, GTFS_AGENCY_FILE);
			} else {
				throwUnknownError(report, validationReport, GTFS_AGENCY_FILE);
			}
		}
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
