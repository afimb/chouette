package mobi.chouette.exchange.regtopp.importer;

import java.io.IOException;

import javax.naming.InitialContext;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.ProcessingCommands;
import mobi.chouette.exchange.ProcessingCommandsFactory;
import mobi.chouette.exchange.ProgressionCommand;
import mobi.chouette.exchange.importer.AbstractImporterCommand;
import mobi.chouette.exchange.report.ActionError;
import mobi.chouette.exchange.report.ActionReport;

@Log4j
public class RegtoppImporterCommand extends AbstractImporterCommand implements Command {

	public static final String COMMAND = "RegtoppImporterCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);

		ProgressionCommand progression = (ProgressionCommand) CommandFactory.create(initialContext, ProgressionCommand.class.getName());
		ActionReport report = (ActionReport) context.get(REPORT);
		try {
			// check params
			Object configuration = context.get(CONFIGURATION);
			if (!(configuration instanceof RegtoppImportParameters)) {
				// fatal wrong parameters
				// log.error("invalid parameters for gtfs import " + configuration.getClass().getName());
				report.setFailure(
						new ActionError(ActionError.CODE.INVALID_PARAMETERS, "invalid parameters for regtopp import " + configuration.getClass().getName()));
				return ERROR;
			}

			RegtoppImportParameters parameters = (RegtoppImportParameters) configuration;
			boolean all = !"stop_area".equalsIgnoreCase(parameters.getReferencesType());

			ProcessingCommands commands = ProcessingCommandsFactory.create(RegtoppImporterProcessingCommands.class.getName());
			result = process(context, commands, progression, true, (all ? Mode.line : Mode.stopareas));

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			report.setFailure(new ActionError(ActionError.CODE.INTERNAL_ERROR, "Fatal :" + e));
		} finally {
			progression.dispose(context);
			log.info(Color.YELLOW + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new RegtoppImporterCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(RegtoppImporterCommand.class.getName(), new DefaultCommandFactory());
	}
}
