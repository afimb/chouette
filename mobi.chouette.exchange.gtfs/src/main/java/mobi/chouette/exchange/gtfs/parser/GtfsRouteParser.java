package mobi.chouette.exchange.gtfs.parser;

import java.awt.Color;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.gtfs.importer.Constant;
import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;
import mobi.chouette.exchange.gtfs.model.GtfsRoute;
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
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class GtfsRouteParser extends GtfsParser implements Parser, Validator, Constant {

	@Getter
	@Setter
	private String gtfsRouteId;

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

	@Override
	public void validate(Context context) throws Exception {
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		ActionReport report = (ActionReport) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);
		
		// routes.txt
		if (importer.hasRouteImporter()) {
			// Add to report
			report.addFileInfo(GTFS_ROUTES_FILE, FILE_STATE.OK);
		} else {
			// Add to report
			report.addFileInfo(GTFS_ROUTES_FILE, FILE_STATE.ERROR, new FileError(FileError.CODE.FILE_NOT_FOUND, "The file \"routes.txt\" must be provided (rule 1-GTFS-Route-1)"));
			// Add to validation report checkpoint 1-GTFS-Route-1
			validationReport.addDetail(GTFS_1_GTFS_Route_1, new Location(GTFS_ROUTES_FILE, "routes-failure"), "The file \"routes.txt\" must be provided", CheckPoint.RESULT.NOK);
			// Stop parsing and render reports (1-GTFS-Route-1 is fatal)
			throw new Exception("The file \"routes.txt\" must be provided");
		}

		Index<GtfsRoute> parser = null;
		try { // Read and check the header line of the file "routes.txt"
			parser = importer.getRouteById();
		} catch (Exception ex ) {
			if (ex instanceof GtfsException) {
				reportError(report, validationReport, (GtfsException)ex, GTFS_ROUTES_FILE);
			} else {
				throwUnknownError(report, validationReport, GTFS_ROUTES_FILE);
			}
		}
		
		if (parser == null || parser.getLength() == 0) { // importer.getRouteById() fails for any other reason
			throwUnknownError(report, validationReport, GTFS_ROUTES_FILE);
		}

		parser.getErrors().clear();
		try {
			for (GtfsRoute bean : parser) {
				reportErrors(report, validationReport, bean.getErrors(), GTFS_ROUTES_FILE);
				parser.validate(bean, importer);
			}
		} catch (Exception ex) {
			AbstractConverter.populateFileError(new FileInfo(GTFS_ROUTES_FILE, FILE_STATE.ERROR), ex);
			throw ex;
		}
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

		switch (gtfsRoute.getRouteType()) {
		case Tram:
			line.setTransportModeName(TransportModeNameEnum.Tramway);
			break;
		case Subway:
			line.setTransportModeName(TransportModeNameEnum.Metro);
			break;
		case Rail:
			line.setTransportModeName(TransportModeNameEnum.Train);
			break;
		case Bus:
			line.setTransportModeName(TransportModeNameEnum.Bus);
			break;
		case Ferry:
			line.setTransportModeName(TransportModeNameEnum.Ferry);
			break;
		case Cable:
			line.setTransportModeName(TransportModeNameEnum.Other);
			break;
		case Gondola:
			line.setTransportModeName(TransportModeNameEnum.Other);
			break;
		case Funicular:
			line.setTransportModeName(TransportModeNameEnum.Other);
			break;
		default:
			line.setTransportModeName(TransportModeNameEnum.Other);
			break;

		}

		String[] token = line.getObjectId().split(":");
		line.setRegistrationNumber(token[2]);
		line.setComment(gtfsRoute.getRouteDesc());
		line.setColor(toHexa(gtfsRoute.getRouteColor()));
		line.setTextColor(toHexa(gtfsRoute.getRouteTextColor()));
		line.setUrl(AbstractConverter.toString(gtfsRoute.getRouteUrl()));
		line.setFilled(true);
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
