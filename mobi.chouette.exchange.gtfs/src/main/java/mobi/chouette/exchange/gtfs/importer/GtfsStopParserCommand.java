package mobi.chouette.exchange.gtfs.importer;

import java.io.IOException;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.gtfs.Constant;
import mobi.chouette.exchange.gtfs.model.importer.GtfsImporter;
import mobi.chouette.exchange.gtfs.parser.GtfsStopParser;
import mobi.chouette.exchange.gtfs.parser.GtfsTransferParser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_STATE;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_TYPE;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class GtfsStopParserCommand implements Command, Constant {

	public static final String COMMAND = "GtfsStopParserCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			Referential referential = (Referential) context.get(REFERENTIAL);
			GtfsImportParameters configuration = (GtfsImportParameters) context.get(CONFIGURATION);
			if (referential != null) {
				referential.clear(true);
			}

			GtfsImporter importer = (GtfsImporter) context.get(PARSER);

			// StopArea
			if (referential.getSharedStopAreas().isEmpty()) {
				GtfsStopParser gtfsStopParser = (GtfsStopParser) ParserFactory.create(GtfsStopParser.class.getName());
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
			if (configuration.getMaxDistanceForCommercial() > 0) {
				CommercialStopGenerator commercialStopGenerator = new CommercialStopGenerator();
				commercialStopGenerator.createCommercialStopPoints(context);
			}

			if (configuration.getMaxDistanceForConnectionLink() > 0) {
				ConnectionLinkGenerator connectionLinkGenerator = new ConnectionLinkGenerator();
				connectionLinkGenerator.createConnectionLinks(context);

			}

			addStats(context, referential);

			result = SUCCESS;
		} catch (Exception e) {
			log.error("[DSU] error : ", e);
			throw e;
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}
		return result;
	}

	private void addStats(Context context, Referential referential) {
		ActionReporter reporter = ActionReporter.Factory.getInstance();
		reporter.addObjectReport(context, "merged", OBJECT_TYPE.CONNECTION_LINK, "connection links", OBJECT_STATE.OK,
				IO_TYPE.INPUT);
		reporter.setStatToObjectReport(context, "merged", OBJECT_TYPE.CONNECTION_LINK, OBJECT_TYPE.CONNECTION_LINK,
				referential.getSharedConnectionLinks().size());
		reporter.addObjectReport(context, "merged", OBJECT_TYPE.STOP_AREA, "stop areas", OBJECT_STATE.OK, IO_TYPE.INPUT);
		reporter.setStatToObjectReport(context, "merged", OBJECT_TYPE.STOP_AREA, OBJECT_TYPE.STOP_AREA, referential
				.getSharedStopAreas().size());

	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new GtfsStopParserCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(GtfsStopParserCommand.class.getName(), new DefaultCommandFactory());
	}
}
