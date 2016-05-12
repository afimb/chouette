package mobi.chouette.exchange.neptune.importer;

import java.io.IOException;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.CommandCancelledException;
import mobi.chouette.exchange.ProcessingCommands;
import mobi.chouette.exchange.ProcessingCommandsFactory;
import mobi.chouette.exchange.ProgressionCommand;
import mobi.chouette.exchange.importer.AbstractImporterCommand;
import mobi.chouette.exchange.report.ActionError;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.ReportConstant;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 * execute use in context : 
 * <ul>
 * <li>INITIAL_CONTEXT</li>
 * <li>REPORT</li>
 * <li>CONFIGURATION</li>
 * </ul>
 * 
 * @author michel
 *
 */
@Log4j
public class NeptuneImporterCommand extends AbstractImporterCommand implements Command, Constant, ReportConstant {

	public static final String COMMAND = "NeptuneImporterCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);

		ActionReport report = (ActionReport) context.get(REPORT);
		
		// initialize reporting and progression
		ProgressionCommand progression = (ProgressionCommand) CommandFactory.create(initialContext,
				ProgressionCommand.class.getName());

        try {
		// read parameters
		Object configuration = context.get(CONFIGURATION);
		if (!(configuration instanceof NeptuneImportParameters)) {
			// fatal wrong parameters

			log.error("invalid parameters for neptune import " + configuration.getClass().getName());
			report.setFailure(new ActionError(ActionError.CODE.INVALID_PARAMETERS,"invalid parameters for neptune import " + configuration.getClass().getName()));
			return ERROR;
		}
		
		ProcessingCommands commands = ProcessingCommandsFactory.create(NeptuneImporterProcessingCommands.class.getName());
		result = process(context, commands, progression, true, Mode.line);
		

		} catch (CommandCancelledException e) {
			report.setFailure(new ActionError(ActionError.CODE.INTERNAL_ERROR, "Command cancelled"));
			log.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			report.setFailure(new ActionError(ActionError.CODE.INTERNAL_ERROR,"Fatal :" + e));

		} finally {
			progression.dispose(context);
			log.info(Color.YELLOW + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new NeptuneImporterCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NeptuneImporterCommand.class.getName(), new DefaultCommandFactory());
	}
}
