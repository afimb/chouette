package mobi.chouette.exchange.netexprofile.importer;

import java.io.IOException;

import javax.naming.InitialContext;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.CommandCancelledException;
import mobi.chouette.exchange.ProcessingCommands;
import mobi.chouette.exchange.ProcessingCommandsFactory;
import mobi.chouette.exchange.ProgressionCommand;
import mobi.chouette.exchange.importer.AbstractImporterCommand;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.report.ActionError;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.util.Referential;

@Log4j
public class NetexprofileImporterCommand extends AbstractImporterCommand implements Command, Constant {

	public static final String COMMAND = "NetextImporterCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = SUCCESS;
		Monitor monitor = MonitorFactory.start(COMMAND);

		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		ActionReport report = (ActionReport) context.get(REPORT);

		context.put(REFERENTIAL, new Referential());

		// initialize reporting and progression
		ProgressionCommand progression = (ProgressionCommand) CommandFactory.create(initialContext,
				ProgressionCommand.class.getName());

		try {

		Object configuration = context.get(CONFIGURATION);
		if (!(configuration instanceof NetexprofileImportParameters)) {
			// fatal wrong parameters
			log.error("invalid parameters for netex import " + configuration.getClass().getName());
			report.setFailure(new ActionError(ActionError.CODE.INVALID_PARAMETERS,
					"invalid parameters for netex import " + configuration.getClass().getName()));
			return false;
		}

		ProcessingCommands commands = ProcessingCommandsFactory.create(NetexImporterProcessingCommands.class.getName());
		result = process(context, commands, progression, true, Mode.line);

		} catch (CommandCancelledException e) {
			report.setFailure(new ActionError(ActionError.CODE.INTERNAL_ERROR, "Command cancelled"));
			log.error(e.getMessage());
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
			Command result = new NetexprofileImporterCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NetexprofileImporterCommand.class.getName(), new DefaultCommandFactory());
	}
}
