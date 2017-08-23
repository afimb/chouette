package mobi.chouette.exchange.regtopp.importer;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.regtopp.RegtoppConstant;
import mobi.chouette.exchange.regtopp.importer.version.VersionHandler;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.model.util.Referential;

import javax.naming.InitialContext;
import java.io.IOException;

import static mobi.chouette.exchange.report.ActionReporter.OBJECT_TYPE;

@Log4j
public class RegtoppStopParserCommand implements Command {

	public static final String COMMAND = "RegtoppStopParserCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			Referential referential = (Referential) context.get(REFERENTIAL);
			RegtoppImportParameters parameters = (RegtoppImportParameters) context.get(CONFIGURATION);
			if (referential != null) {
				referential.clear(true);
			}

			VersionHandler versionHandler = (VersionHandler) context.get(RegtoppConstant.VERSION_HANDLER);

			// StopArea
			if (referential.getSharedStopAreas().isEmpty()) {
				Parser stopParser = versionHandler.createStopParser();
				stopParser.parse(context);
			}
			// Populate shared connection links
			if (parameters.isParseConnectionLinks() && referential.getSharedConnectionLinks().isEmpty()) {
				Parser connectionLinkParser = versionHandler.createConnectionLinkParser();
				connectionLinkParser.parse(context);
			}

			addStats(context, referential);

			result = SUCCESS;
		} catch (Exception e) {
			log.error("[DSU] error : ", e);
			throw e;
		}

		log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		return result;
	}

	private void addStats(Context context, Referential referential) {
		ActionReporter actionReporter = ActionReporter.Factory.getInstance();

		// global stats
		actionReporter.addObjectReport(context, "global", OBJECT_TYPE.CONNECTION_LINK, "connection links", ActionReporter.OBJECT_STATE.OK, IO_TYPE.INPUT);
		actionReporter.addObjectReport(context, "global", OBJECT_TYPE.STOP_AREA, "stop areas", ActionReporter.OBJECT_STATE.OK, IO_TYPE.INPUT);

		actionReporter.setStatToObjectReport(context, "global", OBJECT_TYPE.CONNECTION_LINK, OBJECT_TYPE.CONNECTION_LINK,
				referential.getSharedConnectionLinks().size());
		actionReporter.setStatToObjectReport(context, "global", OBJECT_TYPE.STOP_AREA, OBJECT_TYPE.STOP_AREA,
				referential.getSharedStopAreas().size());
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new RegtoppStopParserCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(RegtoppStopParserCommand.class.getName(), new DefaultCommandFactory());
	}
}
