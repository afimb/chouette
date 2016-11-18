package mobi.chouette.exchange.gtfs.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;
import mobi.chouette.exchange.gtfs.model.GtfsAgency;
import mobi.chouette.exchange.gtfs.model.importer.GtfsException;
import mobi.chouette.exchange.gtfs.model.importer.GtfsImporter;
import mobi.chouette.exchange.gtfs.model.importer.Index;
import mobi.chouette.exchange.gtfs.validation.Constant;
import mobi.chouette.exchange.gtfs.validation.GtfsValidationReporter;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.Validator;
import mobi.chouette.model.Company;
import mobi.chouette.exchange.gtfs.GtfsChouetteIdGenerator;
import mobi.chouette.exchange.gtfs.GtfsChouetteIdObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class GtfsAgencyParser extends GtfsChouetteIdGenerator implements Parser, Validator, Constant {

	@Override
	public void validate(Context context) throws Exception {
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		GtfsValidationReporter gtfsValidationReporter = (GtfsValidationReporter) context.get(GTFS_REPORTER);
		gtfsValidationReporter.getExceptions().clear();
		
		// log.info("validating agencies");
		// agency.txt
		if (importer.hasAgencyImporter()) { // the file "agency.txt" exists
			gtfsValidationReporter.reportSuccess(context, GTFS_1_GTFS_Common_1, GTFS_AGENCY_FILE);
			
			Index<GtfsAgency> parser = null;
			try { // Read and check the header line of the file "agency.txt"
				parser = importer.getAgencyById(); 
			} catch (Exception ex ) {
				// INVALID_HEADER_FILE_FORMAT, EMPTY_HEADER_FIELD, DUPLICATE_HEADER_FIELD, DUPLICATE_DEFAULT_KEY_FIELD
				// MISSING_FIELD, DUPLICATE_FIELD, INVALID_FILE_FORMAT, MISSING_FILE, SYSTEM,
				if (ex instanceof GtfsException) {
					gtfsValidationReporter.reportError(context, (GtfsException)ex, GTFS_AGENCY_FILE);
				} else {
					gtfsValidationReporter.throwUnknownError(context, ex, GTFS_AGENCY_FILE);
				}
			}
			
			gtfsValidationReporter.validateOkCSV(context, GTFS_AGENCY_FILE);
		
			if (parser == null) { // importer.getAgencyById() fails for any other reason
				gtfsValidationReporter.throwUnknownError(context, new Exception("Cannot instantiate AgencyById class"), GTFS_AGENCY_FILE);
			} else {
				gtfsValidationReporter.validate(context, GTFS_AGENCY_FILE, parser.getOkTests());
				gtfsValidationReporter.validateUnknownError(context);
			}
			
			if (!parser.getErrors().isEmpty()) {
				// EXTRA_SPACE_IN_HEADER_FIELD, HTML_TAG_IN_HEADER_FIELD, EXTRA_HEADER_FIELD, MISSING_REQUIRED_FIELDS
				gtfsValidationReporter.reportErrors(context, parser.getErrors(), GTFS_AGENCY_FILE);
				parser.getErrors().clear();
			}
			
			gtfsValidationReporter.validateOKGeneralSyntax(context, GTFS_AGENCY_FILE);
			
			if (parser.getLength() == 0) {
				gtfsValidationReporter.reportError(context, new GtfsException(GTFS_AGENCY_FILE, 1, null, GtfsException.ERROR.FILE_WITH_NO_ENTRY, null, null), GTFS_AGENCY_FILE);
			} else {
				gtfsValidationReporter.validate(context, GTFS_AGENCY_FILE, GtfsException.ERROR.FILE_WITH_NO_ENTRY);
			}
		
			// EXTRA_SPACE_IN_FIELD
			GtfsException fatalException = null;
			parser.setWithValidation(true);
			for (GtfsAgency bean : parser) { // Build the beans
				try {
					parser.validate(bean, importer);
				} catch (Exception ex) {
					if (ex instanceof GtfsException) {
						gtfsValidationReporter.reportError(context, (GtfsException)ex, GTFS_AGENCY_FILE);
					} else {
						gtfsValidationReporter.throwUnknownError(context, ex, GTFS_AGENCY_FILE);
					}
				}
				for(GtfsException ex : bean.getErrors()) {
					if (ex.isFatal())
						fatalException = ex;
				}
				gtfsValidationReporter.reportErrors(context, bean.getErrors(), GTFS_AGENCY_FILE);
				gtfsValidationReporter.validate(context, GTFS_AGENCY_FILE, bean.getOkTests());
			}
			parser.setWithValidation(false);
			if (fatalException != null)
				throw fatalException;
		} else { // the file "agency.txt" doesn't exist
			gtfsValidationReporter.reportError(context, new GtfsException(GTFS_AGENCY_FILE, 1, null, GtfsException.ERROR.MISSING_FILE, null, null), GTFS_AGENCY_FILE);
		}
	}
	
	@Override
	public void parse(Context context) throws Exception {

		Referential referential = (Referential) context.get(REFERENTIAL);
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		GtfsImportParameters configuration = (GtfsImportParameters) context.get(CONFIGURATION);

		for (GtfsAgency gtfsAgency : importer.getAgencyById()) {
			String objectId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), Company.COMPANY_KEY,
					gtfsAgency.getAgencyId(), log);
			Company company = GtfsChouetteIdObjectFactory.getCompany(referential, toChouetteId(objectId, "default_codespace"));
			convert(context, gtfsAgency, company);
		}
	}
	
	private void convert(Context context, GtfsAgency gtfsAgency, Company company) {
		company.setName(AbstractConverter.getNonEmptyTrimedString(gtfsAgency.getAgencyName()));
		company.setUrl(AbstractConverter.toString(gtfsAgency.getAgencyUrl()));
		company.setPhone(AbstractConverter.getNonEmptyTrimedString(gtfsAgency.getAgencyPhone()));
		String[] token = company.getChouetteId().getObjectId().split(":");
		company.setRegistrationNumber(token[2]);
		company.setTimeZone(AbstractConverter.toString(gtfsAgency.getAgencyTimezone()));
		company.setFilled(true);
// 		AbstractConverter.addLocation(context, "agency.txt", company.getChouetteId().getObjectId(), gtfsAgency.getId());
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
