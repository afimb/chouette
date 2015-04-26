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
			if (referential != null) {
				referential.clear();
			}

			GtfsImporter importer = (GtfsImporter) context.get(PARSER);

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

		
			result = SUCCESS;
		} catch (Exception e) {
			log.error("[DSU] error : ", e);
			throw e;
		}
		
		log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		return result;
	}


	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new GtfsStopParserCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(GtfsStopParserCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
