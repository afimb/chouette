package mobi.chouette.exchange.gtfs.parser;

import java.util.Calendar;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.gtfs.Constant;
import mobi.chouette.exchange.gtfs.importer.GtfsParameters;
import mobi.chouette.exchange.gtfs.model.GtfsRoute;
import mobi.chouette.exchange.gtfs.model.importer.GtfsImporter;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.model.PTNetwork;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class GtfsParser implements Parser, Constant {

	private Referential referential;
	private GtfsImporter importer;
	private GtfsParameters configuration;
	private ValidationParameters validation;

	@Override
	public void parse(Context context) throws Exception {

		referential = (Referential) context.get(REFERENTIAL);
		importer = (GtfsImporter) context.get(IMPORTER);
		configuration = (GtfsParameters) context.get(CONFIGURATION);
		validation = (ValidationParameters) context.get(VALIDATION);

		// PTNetwork
		createPTNetwork(context);

		// Company
		Parser gtfsAgencyParser = ParserFactory.create(GtfsAgencyParser.class
				.getName());
		gtfsAgencyParser.parse(context);

		// StopArea
		Parser gtfsStopParser = ParserFactory.create(GtfsStopParser.class
				.getName());
		gtfsStopParser.parse(context);

		// ConnectionLink
		if (importer.hasTransferImporter()) {
			Parser gtfsTransferParser = ParserFactory
					.create(GtfsTransferParser.class.getName());
			gtfsTransferParser.parse(context);
		}

		// Timetable
		Parser gtfsCalendarParser = ParserFactory
				.create(GtfsCalendarParser.class.getName());
		gtfsCalendarParser.parse(context);

		// Line
		Parser gtfsRouteParser = ParserFactory.create(GtfsRouteParser.class
				.getName());
		for (GtfsRoute gtfsRoute : importer.getRouteById()) {

			// Route
			context.put(GTFS_ROUTE, gtfsRoute);
			gtfsRouteParser.parse(context);
		}

	}

	private void createPTNetwork(Context context) throws Exception {

		String prefix = configuration.getObjectIdPrefix();
		String objectId = prefix + ":" + PTNetwork.PTNETWORK_KEY + ":" + prefix;

		// PTnetwork
		PTNetwork ptNetwork = ObjectFactory.getPTNetwork(referential, objectId);

		// VersionDate mandatory
		ptNetwork.setVersionDate(Calendar.getInstance().getTime());

		// Name mandatory
		ptNetwork.setName(prefix);

		// Registration optional
		ptNetwork.setRegistrationNumber(prefix);

		// SourceName optional
		ptNetwork.setSourceName("GTFS");
	}

	static {
		ParserFactory.register(GtfsParser.class.getName(), new ParserFactory() {
			private GtfsParser instance = new GtfsParser();

			@Override
			protected Parser create() {
				return instance;
			}
		});
	}

}
