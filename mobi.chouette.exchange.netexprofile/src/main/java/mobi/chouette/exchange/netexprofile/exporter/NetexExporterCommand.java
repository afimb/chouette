package mobi.chouette.exchange.netexprofile.exporter;

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
import mobi.chouette.exchange.exporter.AbstractExporterCommand;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.ReportConstant;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;

@Log4j
@Stateless(name = NetexExporterCommand.COMMAND)
public class NetexExporterCommand extends AbstractExporterCommand implements Command, Constant, ReportConstant {

	public static final String COMMAND = "NetexExporterCommand";

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		ActionReporter actionReporter = ActionReporter.Factory.getInstance();

		// initialize reporting and progression
		ProgressionCommand progression = (ProgressionCommand) CommandFactory.create(initialContext,
				ProgressionCommand.class.getName());

		try {

			// read parameters
			Object configuration = context.get(CONFIGURATION);
			if (!(configuration instanceof NetexExportParameters)) {
				// fatal wrong parameters
				log.error("invalid parameters for netex export " + configuration.getClass().getName());
				actionReporter.setActionError(context, ActionReporter.ERROR_CODE.INVALID_PARAMETERS, "invalid parameters for netex export " + configuration.getClass().getName());
				return ERROR;
			}

			NetexExportParameters parameters = (NetexExportParameters) configuration;
			if (parameters.getStartDate() != null && parameters.getEndDate() != null) {
				if (parameters.getStartDate().after(parameters.getEndDate())) {
					actionReporter.setActionError(context, ActionReporter.ERROR_CODE.INVALID_PARAMETERS, "end date before start date");
					return ERROR;

				}
			}
			ProcessingCommands commands = ProcessingCommandsFactory.create(NetexExporterProcessingCommands.class
					.getName());

			result = process(context, commands, progression, true,Mode.line);

		} catch (CommandCancelledException e) {
			actionReporter.setActionError(context, ActionReporter.ERROR_CODE.INTERNAL_ERROR, "Command cancelled");
			log.error(e.getMessage());
		} catch (Exception e) {
			if (!COMMAND_CANCELLED.equals(e.getMessage())) {
				actionReporter.setActionError(context, ActionReporter.ERROR_CODE.INTERNAL_ERROR,  "Fatal :" + e);
				log.error(e.getMessage(), e);
			}
		} finally {
			progression.dispose(context);
			log.info(Color.YELLOW + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.netexprofile/" + COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				// try another way on test context
				String name = "java:module/" + COMMAND;
				try {
					result = (Command) context.lookup(name);
				} catch (NamingException e1) {
					log.error(e);
				}
			}
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NetexExporterCommand.class.getName(), new DefaultCommandFactory());
	}
}
