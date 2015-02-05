package mobi.chouette.exchange.gtfs.parser;

import java.awt.Color;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.gtfs.Constant;
import mobi.chouette.exchange.gtfs.importer.GtfsParameters;
import mobi.chouette.exchange.gtfs.model.GtfsAgency;
import mobi.chouette.exchange.gtfs.model.GtfsRoute;
import mobi.chouette.exchange.gtfs.model.importer.GtfsImporter;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.model.Company;
import mobi.chouette.model.Line;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class GtfsRouteParser implements Parser, Constant {

	private Referential referential;
	private GtfsImporter importer;
	private GtfsParameters configuration;

	@Override
	public void parse(Context context) throws Exception {

		referential = (Referential) context.get(REFERENTIAL);
		importer = (GtfsImporter) context.get(IMPORTER);
		configuration = (GtfsParameters) context.get(CONFIGURATION);

		GtfsRoute gtfsRoute = (GtfsRoute) context.get(GTFS_ROUTE);

		String lineId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), Line.LINE_KEY,
				gtfsRoute.getRouteId(), log);
		Line line = ObjectFactory.getLine(referential, lineId);
		convert(context, gtfsRoute, line);

		// Route
		Parser gtfsTripParser = ParserFactory.create(GtfsTripParser.class
				.getName());
		gtfsTripParser.parse(context);

	}

	protected void convert(Context context, GtfsRoute gtfsRoute, Line line) {

		Referential referential = (Referential) context.get(REFERENTIAL);

		// Name optional
		line.setName(AbstractConverter.getNonEmptyTrimedString(gtfsRoute
				.getRouteLongName()));
		if (line.getName() == null)
			line.setName(AbstractConverter.getNonEmptyTrimedString(gtfsRoute
					.getRouteShortName()));

		// Number optional
		line.setNumber(AbstractConverter.getNonEmptyTrimedString(gtfsRoute
				.getRouteShortName()));

		// PublishedName optional
		line.setPublishedName(AbstractConverter
				.getNonEmptyTrimedString(gtfsRoute.getRouteLongName()));

		// Name = route_long_name oder route_short_name
		if (line.getPublishedName() != null) {
			line.setName(line.getPublishedName());
		} else {
			line.setName(line.getNumber());
		}
		// TransportModeName optional
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

		// Registration optional
		String[] token = line.getObjectId().split(":");
		line.setRegistrationNumber(token[2]);

		// Comment optional
		line.setComment(gtfsRoute.getRouteDesc());

		// Company optional
		if (gtfsRoute.getAgencyId() != null) {

			String agencyId = (gtfsRoute.getAgencyId() != null) ? gtfsRoute.getAgencyId(): GtfsAgency.DEFAULT_ID;
			String companyId = AbstractConverter
					.getNonEmptyTrimedString(AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(),
							Company.COMPANY_KEY, gtfsRoute.getAgencyId(), log));
			Company company = ObjectFactory.getCompany(referential, companyId);

			line.setCompany(company);
		}

		line.setColor(toHexa(gtfsRoute.getRouteColor()));
		line.setTextColor(toHexa(gtfsRoute.getRouteTextColor()));
		line.setUrl(AbstractConverter.toString(gtfsRoute.getRouteUrl()));

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
		ParserFactory.register(GtfsRouteParser.class.getName(),
				new ParserFactory() {
					private GtfsRouteParser instance = new GtfsRouteParser();

					@Override
					protected Parser create() {
						return instance;
					}
				});
	}

}
