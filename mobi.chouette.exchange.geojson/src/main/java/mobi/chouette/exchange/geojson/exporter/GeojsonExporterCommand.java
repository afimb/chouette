package mobi.chouette.exchange.geojson.exporter;

import java.io.IOException;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

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
import mobi.chouette.exchange.exporter.AbstractExporterCommand;
import mobi.chouette.exchange.report.ActionError;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.ReportConstant;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
@Stateless(name = GeojsonExporterCommand.COMMAND)
public class GeojsonExporterCommand extends AbstractExporterCommand implements
		Command, Constant, ReportConstant {

	public static final String COMMAND = "GeojsonExporterCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		InitialContext initialContext = (InitialContext) context
				.get(INITIAL_CONTEXT);
		ActionReport report = (ActionReport) context.get(REPORT);

		// initialize reporting and progression
		ProgressionCommand progression = (ProgressionCommand) CommandFactory
				.create(initialContext, ProgressionCommand.class.getName());

		progression.initialize(context, 2);

		try {
			// read parameters
			Object configuration = context.get(CONFIGURATION);
			if (!(configuration instanceof GeojsonExportParameters)) {
				// fatal wrong parameters
				log.error("invalid parameters for geojson export "
						+ configuration.getClass().getName());
				report.setFailure(new ActionError(
						ActionError.CODE.INVALID_PARAMETERS,
						"invalid parameters for geojson export "
								+ configuration.getClass().getName()));
				return ERROR;
			}

			GeojsonExportParameters parameters = (GeojsonExportParameters) configuration;
			if (parameters.getStartDate() != null
					&& parameters.getEndDate() != null) {
				if (parameters.getStartDate().after(parameters.getEndDate())) {
					report.setFailure(new ActionError(
							ActionError.CODE.INVALID_PARAMETERS,
							"end date before start date"));
					return ERROR;

				}
			}
			ProcessingCommands commands = ProcessingCommandsFactory
					.create(GeojsonExporterProcessingCommands.class.getName());

			result = process(context, commands, progression, true, Mode.line);

		} catch (CommandCancelledException e) {
			report.setFailure(new ActionError(ActionError.CODE.INTERNAL_ERROR,
					"Command cancelled"));
			log.error(e.getMessage());
		} catch (Exception e) {
			report.setFailure(new ActionError(ActionError.CODE.INTERNAL_ERROR,
					"Fatal :" + e));
			log.error(e.getMessage(), e);
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
				String name = "java:app/mobi.chouette.exchange.geojson/"
						+ COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				log.error(e);
			}
			return result;
		}
	}

	static {
		CommandFactory.factories.put(GeojsonExporterCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
