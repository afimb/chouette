package mobi.chouette.exchange.gtfs.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.gtfs.Constant;
import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;
import mobi.chouette.exchange.gtfs.model.GtfsAgency;
import mobi.chouette.exchange.gtfs.model.importer.GtfsImporter;
import mobi.chouette.exchange.gtfs.model.importer.Index;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.Validator;
import mobi.chouette.model.Company;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class GtfsAgencyParser implements Parser, Validator, Constant {

	private Referential referential;
	private GtfsImporter importer;
	private GtfsImportParameters configuration;

	@Override
	public void parse(Context context) throws Exception {

		referential = (Referential) context.get(REFERENTIAL);
		importer = (GtfsImporter) context.get(PARSER);
		configuration = (GtfsImportParameters) context.get(CONFIGURATION);

 
		for (GtfsAgency gtfsAgency : importer.getAgencyById()) {
			String objectId = AbstractConverter.composeObjectId(
					configuration.getObjectIdPrefix(), Company.COMPANY_KEY,
					gtfsAgency.getAgencyId(), log);
			Company company = ObjectFactory.getCompany(referential, objectId);
			convert(context, gtfsAgency, company);
		}
	}

	@Override
	public void validate(Context context) throws Exception {
		importer = (GtfsImporter) context.get(PARSER);
		
		// agency.txt
		Index<GtfsAgency> parser = importer.getAgencyById();
		for (GtfsAgency bean : parser) {
			parser.validate(bean, importer);
		}
	}

	public void convert(Context context, GtfsAgency gtfsAgency, Company company) {

		company.setName(AbstractConverter.getNonEmptyTrimedString(gtfsAgency
				.getAgencyName()));
		company.setUrl(AbstractConverter.toString(gtfsAgency.getAgencyUrl()));
		company.setPhone(AbstractConverter.getNonEmptyTrimedString(gtfsAgency
				.getAgencyPhone()));
		String[] token = company.getObjectId().split(":");
		company.setRegistrationNumber(token[2]);
		company.setTimeZone(AbstractConverter.toString(gtfsAgency
				.getAgencyTimezone()));
	}

	static {
		ParserFactory.register(GtfsAgencyParser.class.getName(),
				new ParserFactory() {
					private GtfsAgencyParser instance = new GtfsAgencyParser();

					@Override
					protected Parser create() {
						return instance;
					}
				});
	}

}
