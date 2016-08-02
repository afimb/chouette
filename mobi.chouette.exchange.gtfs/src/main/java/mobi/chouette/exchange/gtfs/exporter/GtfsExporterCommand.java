package mobi.chouette.exchange.gtfs.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.CommandCancelledException;
import mobi.chouette.exchange.DaoReader;
import mobi.chouette.exchange.ProcessingCommands;
import mobi.chouette.exchange.ProcessingCommandsFactory;
import mobi.chouette.exchange.ProgressionCommand;
import mobi.chouette.exchange.exporter.AbstractExporterCommand;
import mobi.chouette.exchange.gtfs.Constant;
import mobi.chouette.exchange.parameters.AbstractExportParameter;
import mobi.chouette.exchange.report.ActionError;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.ReportConstant;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
@Stateless(name = GtfsExporterCommand.COMMAND)
public class GtfsExporterCommand extends AbstractExporterCommand implements Command, Constant, ReportConstant {

	public static final String COMMAND = "GtfsExporterCommand";
	@EJB DaoReader reader;
	
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);
		log.info("GtfsExporterCommand 1");
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		ActionReport report = (ActionReport) context.get(REPORT);
		log.info("GtfsExporterCommand 2");
		// initialize reporting and progression
		ProgressionCommand progression = (ProgressionCommand) CommandFactory.create(initialContext,
				ProgressionCommand.class.getName());
		log.info("GtfsExporterCommand 3");
		try {
			// read parameters
			Object configuration = context.get(CONFIGURATION);
			log.info("GtfsExporterCommand 4");
			if (!(configuration instanceof GtfsExportParameters)) {
				log.info("GtfsExporterCommand 4 error : wrong parameters");
				// fatal wrong parameters
				log.error("invalid parameters for gtfs export " + configuration.getClass().getName());
				report.setFailure(new ActionError(ActionError.CODE.INVALID_PARAMETERS,
						"invalid parameters for gtfs export " + configuration.getClass().getName()));
				return ERROR;
			}
			log.info("GtfsExporterCommand 5");
			GtfsExportParameters parameters = (GtfsExportParameters) configuration;

			String type = parameters.getReferencesType();
			// set default type
			if (type == null || type.isEmpty()) {
				// all lines
				type = "line";
				parameters.setIds(null);
			}
			type = type.toLowerCase();
			log.info("GtfsExporterCommand 6");
			// init
			boolean all = !(parameters.getReferencesType().equalsIgnoreCase("stop_area"));

				ProcessingCommands commands = ProcessingCommandsFactory
						.create(GtfsExporterProcessingCommands.class.getName());
				result = process(context, commands, progression, false,(all?Mode.line:Mode.stopareas));
			
				log.info("GtfsExporterCommand 7");
		} catch (CommandCancelledException e) {
			report.setFailure(new ActionError(ActionError.CODE.INTERNAL_ERROR, "Command cancelled"));
			log.error(e.getMessage());
		} catch (Exception e) {
			report.setFailure(new ActionError(ActionError.CODE.INTERNAL_ERROR, "Fatal :" + e));
			log.error(e.getMessage(), e);
		} finally {
			progression.dispose(context);
			log.info(Color.YELLOW + monitor.stop() + Color.NORMAL);
		}

		return result;
	}
	
	@Override
	public boolean process(Context context, ProcessingCommands commands, ProgressionCommand progression,
			boolean continueLineProcesingOnError, Mode mode) throws Exception {
		boolean result = ERROR;
		AbstractExportParameter parameters = (AbstractExportParameter) context.get(CONFIGURATION);
		ActionReport report = (ActionReport) context.get(REPORT);
		log.info("AbstractExporterCommand 1");
		// initialisation
		List<? extends Command> preProcessingCommands = commands.getPreProcessingCommands(context, true);
		progression.initialize(context, preProcessingCommands.size() + (mode.equals(Mode.line) ? 1 : 0));
		for (Command exportCommand : preProcessingCommands) {
			result = exportCommand.execute(context);
			if (!result) {
				log.info("AbstractExporterCommand not result loop 1");
				report.setFailure(new ActionError(ActionError.CODE.NO_DATA_FOUND, "no data selected"));
				progression.execute(context);
				return ERROR;
			}
			log.info("AbstractExporterCommand looping 2");
			progression.execute(context);
		}
		log.info("AbstractExporterCommand 3");
		if (mode.equals(Mode.line)) {
			log.info("AbstractExporterCommand 4");
			// get lines
			String type = parameters.getReferencesType();
			// set default type
			if (type == null || type.isEmpty()) {
				log.info("AbstractExporterCommand 4 bis");
				// all lines
				type = "line";
				parameters.setIds(null);
			}
			log.info("AbstractExporterCommand 5");
			type = type.toLowerCase();
			
			List<Long> ids = null;
			if (parameters.getIds() != null) {
				ids = new ArrayList<Long>(parameters.getIds());
			}
			log.info("AbstractExporterCommand 6");
			Set<Long> lines = reader.loadLines(type, ids);
			if (lines.isEmpty()) {
				log.info("AbstractExporterCommand 6 error");
				report.setFailure(new ActionError(ActionError.CODE.NO_DATA_FOUND, "no data selected"));
				//return ERROR;

			}
			log.info("AbstractExporterCommand 7");
			progression.execute(context);
			log.info("AbstractExporterCommand 8");
			// process lines
			List<? extends Command> lineProcessingCommands = commands.getLineProcessingCommands(context, true);
			progression.start(context, lines.size());
			log.info("AbstractExporterCommand 9");
			int lineCount = 0;
			// export each line
			for (Long line : lines) {
				context.put(LINE_ID, line);
				boolean exportFailed = false;
				for (Command exportCommand : lineProcessingCommands) {
					result = exportCommand.execute(context);
					if (!result) {
						log.info("AbstractExporterCommand 9 loop error 1");
						exportFailed = true;
						break;
					}
				}
				progression.execute(context);
				if (!exportFailed) {
					lineCount++;
				} else if (!continueLineProcesingOnError) {
					log.info("AbstractExporterCommand 9 loop error 2");
					report.setFailure(new ActionError(ActionError.CODE.INVALID_DATA, "unable to export data"));
					return ERROR;
				}
			}
			log.info("AbstractExporterCommand 10");
			// check if data where exported
//			if (lineCount == 0) {
//				log.info("AbstractExporterCommand 10 bis");
//				progression.terminate(context, 1);
//				report.setFailure(new ActionError(ActionError.CODE.NO_DATA_PROCEEDED, "no data exported"));
//				progression.execute(context);
//				//return ERROR;
//			}
		} else // stopareas
		{
			log.info("AbstractExporterCommand 11");
			// get stop info
			List<? extends Command> stopProcessingCommands = commands.getStopAreaProcessingCommands(context, true);
			progression.start(context, stopProcessingCommands.size());
			for (Command command : stopProcessingCommands) {
				result = command.execute(context);
				if (!result) {
					log.info("AbstractExporterCommand 11 loop error");
					return ERROR;
				}
				progression.execute(context);
			}
		}
		// post processing
		log.info("AbstractExporterCommand 12");
		List<? extends Command> postProcessingCommands = commands.getPostProcessingCommands(context, true);
		progression.terminate(context, postProcessingCommands.size());
		log.info("AbstractExporterCommand 13");
		for (Command exportCommand : postProcessingCommands) {
			result = exportCommand.execute(context);
			if (!result) {
				log.info("AbstractExporterCommand 13 loop error");
				if (report.getFailure() == null)
					report.setFailure(new ActionError(ActionError.CODE.NO_DATA_PROCEEDED, "no data exported"));
				//return ERROR;
			}
			progression.execute(context);
		}
		log.info("AbstractExporterCommand 14");
		return result;
	}
	
	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.gtfs/" + COMMAND;
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
		CommandFactory.factories.put(GtfsExporterCommand.class.getName(), new DefaultCommandFactory());
	}
}
