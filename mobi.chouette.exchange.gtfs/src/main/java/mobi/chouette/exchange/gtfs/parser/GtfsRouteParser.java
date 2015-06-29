package mobi.chouette.exchange.gtfs.parser;

import java.awt.Color;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.gtfs.Constant;
import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;
import mobi.chouette.exchange.gtfs.model.GtfsRoute;
import mobi.chouette.exchange.gtfs.model.importer.GtfsImporter;
import mobi.chouette.exchange.gtfs.model.importer.Index;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.Validator;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;
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

		// routes.txt
		FileInfo file = new FileInfo(GTFS_ROUTES_FILE, FILE_STATE.OK);
		report.getFiles().add(file);
		try {
			Index<GtfsRoute> parser = importer.getRouteById();
			for (GtfsRoute bean : parser) {
				parser.validate(bean, importer);
			}
		} catch (Exception ex) {
			AbstractConverter.populateFileError(file, ex);
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
