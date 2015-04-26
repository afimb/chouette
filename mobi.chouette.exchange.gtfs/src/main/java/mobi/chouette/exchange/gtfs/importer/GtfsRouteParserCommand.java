package mobi.chouette.exchange.gtfs.importer;

import java.io.IOException;
import java.util.Calendar;

import javax.naming.InitialContext;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.gtfs.Constant;
import mobi.chouette.exchange.gtfs.model.importer.GtfsImporter;
import mobi.chouette.exchange.gtfs.parser.GtfsAgencyParser;
import mobi.chouette.exchange.gtfs.parser.GtfsCalendarParser;
import mobi.chouette.exchange.gtfs.parser.GtfsRouteParser;
import mobi.chouette.exchange.gtfs.parser.GtfsStopParser;
import mobi.chouette.exchange.gtfs.parser.GtfsTransferParser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.model.Network;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class GtfsRouteParserCommand implements Command, Constant {

	public static final String COMMAND = "GtfsRouteParserCommand";

	@Getter
	@Setter
	private String gtfsRouteId;

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		
		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			Referential referential = (Referential) context.get(REFERENTIAL);
			if (referential != null) {
				referential.clear();
			}

			GtfsImportParameters configuration = (GtfsImportParameters) context
					.get(CONFIGURATION);

			GtfsImporter importer = (GtfsImporter) context.get(PARSER);

			// PTNetwork
			if (referential.getSharedPTNetworks().isEmpty()) {
				createPTNetwork(referential, configuration);
			}
			
			// Company
			if (referential.getSharedCompanies().isEmpty()) {
				GtfsAgencyParser gtfsAgencyParser = (GtfsAgencyParser) ParserFactory
						.create(GtfsAgencyParser.class.getName());
				gtfsAgencyParser.parse(context);
			}

			// StopArea
			if (referential.getSharedStopAreas().isEmpty()) {
				GtfsStopParser gtfsStopParser = (GtfsStopParser) ParserFactory
						.create(GtfsStopParser.class.getName());
				gtfsStopParser.parse(context);
			}

			// ConnectionLink
			if (importer.hasTransferImporter()) {
				if (referential.getSharedConnectionLinks().isEmpty()) {
					GtfsTransferParser gtfsTransferParser = (GtfsTransferParser) ParserFactory
							.create(GtfsTransferParser.class.getName());
					gtfsTransferParser.parse(context);
				}
			}

			// Timetable
			if (referential.getSharedTimetables().isEmpty()) {
				GtfsCalendarParser gtfsCalendarParser = (GtfsCalendarParser) ParserFactory
						.create(GtfsCalendarParser.class.getName());
				gtfsCalendarParser.parse(context);
			}

			// TODO lazy loading for PTNetwork, Company, StopArea,
			// ConnectionLink

			
			// Line
			log.info("[DSU] parse route : " + gtfsRouteId);
			GtfsRouteParser gtfsRouteParser = (GtfsRouteParser) ParserFactory
					.create(GtfsRouteParser.class.getName());
			gtfsRouteParser.setGtfsRouteId(gtfsRouteId);
			gtfsRouteParser.parse(context);
		
			result = SUCCESS;
		} catch (Exception e) {
			log.error("[DSU] error : ", e);
			throw e;
		}
		
		log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		return result;
	}

	private Network createPTNetwork(Referential referential,
			GtfsImportParameters configuration) {
		String prefix = configuration.getObjectIdPrefix();
		String ptNetworkId = prefix + ":" + Network.PTNETWORK_KEY + ":"
				+ prefix;
		Network ptNetwork = ObjectFactory.getPTNetwork(referential,
				ptNetworkId);
		ptNetwork.setVersionDate(Calendar.getInstance().getTime());
		ptNetwork.setName(prefix);
		ptNetwork.setRegistrationNumber(prefix);
		ptNetwork.setSourceName("GTFS");
		return ptNetwork;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new GtfsRouteParserCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(GtfsRouteParserCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
