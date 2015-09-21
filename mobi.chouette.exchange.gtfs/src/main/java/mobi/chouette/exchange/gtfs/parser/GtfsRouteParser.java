package mobi.chouette.exchange.gtfs.parser;


import java.awt.Color;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;
import mobi.chouette.exchange.gtfs.model.GtfsRoute;
import mobi.chouette.exchange.gtfs.model.importer.GtfsException;
import mobi.chouette.exchange.gtfs.model.importer.GtfsImporter;
import mobi.chouette.exchange.gtfs.model.importer.Index;
import mobi.chouette.exchange.gtfs.validation.Constant;
import mobi.chouette.exchange.gtfs.validation.ValidationReporter;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.Validator;
import mobi.chouette.model.Company;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class GtfsRouteParser implements Parser, Validator, Constant {

	@Getter
	@Setter
	private String gtfsRouteId;

	@Override
	public void validate(Context context) throws Exception {
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		ValidationReporter validationReporter = (ValidationReporter) context.get(GTFS_REPORTER);
		validationReporter.getExceptions().clear();
		
		// routes.txt
		if (importer.hasRouteImporter()) { // the file "routes.txt" exists ?
			validationReporter.reportSuccess(context, GTFS_1_GTFS_Common_1, GTFS_ROUTES_FILE);

			Index<GtfsRoute> parser = null;
			try { // Read and check the header line of the file "routes.txt"
				parser = importer.getRouteById(); 
			} catch (Exception ex ) {
				if (ex instanceof GtfsException) {
					validationReporter.reportError(context, (GtfsException)ex, GTFS_ROUTES_FILE);
				} else {
					validationReporter.throwUnknownError(context, ex, GTFS_ROUTES_FILE);
				}
			}

			validationReporter.validateOkCSV(context, GTFS_ROUTES_FILE);
		
			if (parser == null) { // importer.getRouteById() fails for any other reason
				validationReporter.throwUnknownError(context, new Exception("Cannot instantiate RouteById class"), GTFS_ROUTES_FILE);
			} else {
				validationReporter.validateUnknownError(context);
			}
			
			if (!parser.getErrors().isEmpty()) {
				validationReporter.reportErrors(context, parser.getErrors(), GTFS_ROUTES_FILE);
				parser.getErrors().clear();
			}
			
			validationReporter.validateOKGeneralSyntax(context, GTFS_ROUTES_FILE);
		
			if (parser.getLength() == 0) {
				validationReporter.reportError(context, new GtfsException(GTFS_ROUTES_FILE, 1, null, GtfsException.ERROR.FILE_WITH_NO_ENTRY, null, null), GTFS_ROUTES_FILE);
			} else {
				validationReporter.validate(context, GTFS_ROUTES_FILE, GtfsException.ERROR.FILE_WITH_NO_ENTRY);
			}
		
		for (GtfsRoute bean : parser) {
			try {
				parser.validate(bean, importer);
			} catch (Exception ex) {
				if (ex instanceof GtfsException) {
					validationReporter.reportError(context, (GtfsException)ex, GTFS_ROUTES_FILE);
				} else {
					validationReporter.throwUnknownError(context, ex, GTFS_ROUTES_FILE);
				}
			}
			validationReporter.reportErrors(context, bean.getErrors(), GTFS_ROUTES_FILE);
		}
		} else {
			validationReporter.reportError(context, new GtfsException(GTFS_ROUTES_FILE, 1, null, GtfsException.ERROR.MISSING_FILE, null, null), GTFS_ROUTES_FILE);
			//validationReporter.reportFailure(context, GTFS_1_GTFS_Route_1, GTFS_ROUTES_FILE);
		}
	}

	@Override
	public void parse(Context context) throws Exception {

		Referential referential = (Referential) context.get(REFERENTIAL);
		GtfsImportParameters configuration = (GtfsImportParameters) context.get(CONFIGURATION);
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);

		Index<GtfsRoute> routes = importer.getRouteById();
		GtfsRoute gtfsRoute = routes.getValue(gtfsRouteId);

		String lineId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), Line.LINE_KEY,
				gtfsRouteId, log);
		Line line = ObjectFactory.getLine(referential, lineId);
		convert(context, gtfsRoute, line);

		// PTNetwork
		String ptNetworkId = configuration.getObjectIdPrefix() + ":" + Network.PTNETWORK_KEY + ":"
				+ configuration.getObjectIdPrefix();
		Network ptNetwork = ObjectFactory.getPTNetwork(referential, ptNetworkId);
		line.setNetwork(ptNetwork);

		// Company
		if (gtfsRoute.getAgencyId() != null) {
			String companyId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(),
					Company.COMPANY_KEY, gtfsRoute.getAgencyId(), log);
			Company company = ObjectFactory.getCompany(referential, companyId);
			line.setCompany(company);
		}
		else if (!referential.getSharedCompanies().isEmpty())
		{
			Company company = referential.getSharedCompanies().values().iterator().next();
			line.setCompany(company);
		}

		// Route VehicleJourney VehicleJourneyAtStop , JourneyPattern ,StopPoint
		GtfsTripParser gtfsTripParser = (GtfsTripParser) ParserFactory.create(GtfsTripParser.class.getName());
		gtfsTripParser.setGtfsRouteId(gtfsRouteId);
		gtfsTripParser.parse(context);

	}

	protected void convert(Context context, GtfsRoute gtfsRoute, Line line) {

		line.setName(AbstractConverter.getNonEmptyTrimedString(gtfsRoute.getRouteLongName()));
		if (line.getName() == null)
			line.setName(AbstractConverter.getNonEmptyTrimedString(gtfsRoute.getRouteShortName()));

		line.setNumber(AbstractConverter.getNonEmptyTrimedString(gtfsRoute.getRouteShortName()));

		line.setPublishedName(AbstractConverter.getNonEmptyTrimedString(gtfsRoute.getRouteLongName()));

		if (line.getPublishedName() != null) {
			line.setName(line.getPublishedName());
		} else {
			line.setName(line.getNumber());
		}

		line.setTransportModeName(toTransportModeNameEnum(gtfsRoute.getRouteType()));

		String[] token = line.getObjectId().split(":");
		line.setRegistrationNumber(token[2]);
		line.setComment(gtfsRoute.getRouteDesc());
		line.setColor(toHexa(gtfsRoute.getRouteColor()));
		line.setTextColor(toHexa(gtfsRoute.getRouteTextColor()));
		line.setUrl(AbstractConverter.toString(gtfsRoute.getRouteUrl()));
		line.setFilled(true);
	}

	private TransportModeNameEnum toTransportModeNameEnum(GtfsRoute.RouteType type)
	{
		switch (type) {
		case Tram:
			return TransportModeNameEnum.Tramway;
		case Subway:
			return TransportModeNameEnum.Metro;
		case Rail:
			return TransportModeNameEnum.Train;
		case Bus:
			return TransportModeNameEnum.Bus;
		case Ferry:
			return TransportModeNameEnum.Ferry;
		case Cable:
			return TransportModeNameEnum.Other;
		case Gondola:
			return TransportModeNameEnum.Other;
		case Funicular:
			return TransportModeNameEnum.Other;
		default:
			return TransportModeNameEnum.Other;
		}
		
	}
	
	private String toHexa(Color color) {
		if (color == null)
			return null;
		String result = Integer.toHexString(color.getRGB());
		if (result.length() == 8)
			result = result.substring(2);
		while (result.length() < 6)
			result = "0" + result;
		return result;
	}

	static {
		ParserFactory.register(GtfsRouteParser.class.getName(), new ParserFactory() {
			@Override
			protected Parser create() {
				return new GtfsRouteParser();
			}
		});	
	}
}
