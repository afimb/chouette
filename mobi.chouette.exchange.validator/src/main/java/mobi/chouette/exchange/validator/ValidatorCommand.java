package mobi.chouette.exchange.validator;

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
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.CommandCancelledException;
import mobi.chouette.exchange.DaoReader;
import mobi.chouette.exchange.ProcessingCommands;
import mobi.chouette.exchange.ProcessingCommandsFactory;
import mobi.chouette.exchange.ProgressionCommand;
import mobi.chouette.exchange.report.ActionError;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.DataStats;
import mobi.chouette.exchange.report.LineInfo;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.model.Line;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
@Stateless(name = ValidatorCommand.COMMAND)
public class ValidatorCommand implements Command, Constant {

	public static final String COMMAND = "ValidatorCommand";

	@EJB DaoReader reader;

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
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
			if (!(configuration instanceof ValidateParameters)) {
				// fatal wrong parameters
				log.error("invalid parameters for validation " + configuration.getClass().getName());
				report.setFailure(new ActionError(ActionError.CODE.INVALID_PARAMETERS,
						"invalid parameters for validation " + configuration.getClass().getName()));
				return ERROR;
			}

			ValidationParameters validationParameters = (ValidationParameters) context.get(VALIDATION);
			if (validationParameters == null) {
				log.error("no validation parameters for validation ");
				report.setFailure(new ActionError(ActionError.CODE.INVALID_PARAMETERS,
						"no validation parameters for validation "));
				return ERROR;

			}

			ValidateParameters parameters = (ValidateParameters) configuration;
			progression.initialize(context, 1);
			context.put(VALIDATION_DATA, new ValidationData());

			String type = parameters.getReferencesType();
			// set default type
			if (type == null || type.isEmpty()) {
				// all lines
				type = "line";
				parameters.setIds(null);
			}
			type = type.toLowerCase();

			ProcessingCommands commands = ProcessingCommandsFactory.create(ValidatorProcessingCommands.class.getName());

			result = process(context, commands, progression, false);


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

	private boolean process(Context context, ProcessingCommands commands, ProgressionCommand progression,
			boolean continueLineProcesingOnError) throws Exception {

		boolean result = ERROR;
		ValidateParameters parameters = (ValidateParameters) context.get(CONFIGURATION);
		ActionReport report = (ActionReport) context.get(REPORT);

		// initialisation
		List<? extends Command> preProcessingCommands = commands.getPreProcessingCommands(context, true);
		progression.initialize(context, preProcessingCommands.size()+1);
		for (Command exportCommand : preProcessingCommands) {
			result = exportCommand.execute(context);
			if (!result) {
				report.setFailure(new ActionError(ActionError.CODE.NO_DATA_FOUND,"no data selected"));
				progression.execute(context);
				return ERROR;		
			}
			progression.execute(context);
		}
		// get lines 
		String type = parameters.getReferencesType();
		// set default type 
		if (type == null || type.isEmpty() )
		{
			// all lines
			type = "line";
			parameters.setIds(null);
		}
		type=type.toLowerCase();

		List<Long> ids = null;
		if (parameters.getIds() != null) {
			ids = new ArrayList<Long>(parameters.getIds());
		}

		Set<Long> lines = reader.loadLines(type, ids);
		if (lines.isEmpty()) {
			report.setFailure(new ActionError(ActionError.CODE.NO_DATA_FOUND,"no data selected"));
			return ERROR;

		}
		progression.execute(context);
		// process lines
		progression.start(context, lines.size());
		int lineCount = 0;
		// export each line
		for (Long lineId : lines) {
			context.put(LINE_ID, lineId);
			boolean exportFailed = false;
			List<? extends Command> lineProcessingCommands = commands.getLineProcessingCommands(context, true);
			for (Command validateCommand : lineProcessingCommands) {
				result = validateCommand.execute(context);
				if (!result) {
					exportFailed = true;
					break;
				}
			}
			progression.execute(context);
			// TODO a mettre dans une commande dédiée
			ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
			Line line = data.getCurrentLine();
			LineInfo lineInfo = new LineInfo(line);
			DataStats stats = lineInfo.getStats();
			stats.setLineCount(1);
			stats.setJourneyPatternCount(data.getJourneyPatterns().size());
			stats.setRouteCount(data.getRoutes().size());
			stats.setVehicleJourneyCount(data.getVehicleJourneys().size());

			// merge lineStats to global ones
			DataStats globalStats = report.getStats();
			globalStats.setLineCount(globalStats.getLineCount() + stats.getLineCount());
			globalStats.setRouteCount(globalStats.getRouteCount() + stats.getRouteCount());
			globalStats.setVehicleJourneyCount(globalStats.getVehicleJourneyCount()
					+ stats.getVehicleJourneyCount());
			globalStats.setJourneyPatternCount(globalStats.getJourneyPatternCount()
					+ stats.getJourneyPatternCount());
			report.getLines().add(lineInfo);
			if (!exportFailed) 
			{
				lineCount ++;
			}
			else if (!continueLineProcesingOnError)
			{
				report.setFailure(new ActionError(ActionError.CODE.INVALID_DATA,"unable to export data"));
				return ERROR;
			}
		}
		// post processing
		
		// check if data where exported
		if (lineCount == 0) {
			progression.terminate(context, 1);
			report.setFailure(new ActionError(ActionError.CODE.NO_DATA_PROCEEDED,"no data exported"));
			progression.execute(context);
			return ERROR;		
		}
		
		List<? extends Command> postProcessingCommands = commands.getPostProcessingCommands(context, true);
		progression.terminate(context, postProcessingCommands.size());
		for (Command exportCommand : postProcessingCommands) {
			result = exportCommand.execute(context);
			if (!result) {
				if (report.getFailure() == null)
				   report.setFailure(new ActionError(ActionError.CODE.NO_DATA_PROCEEDED,"no data exported"));
				return ERROR;
			}
			progression.execute(context);
		}	
		// TODO a mettre dans une commande dédiée
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		DataStats globalStats = report.getStats();
		globalStats.setConnectionLinkCount(data.getConnectionLinks().size());
		globalStats.setAccessPointCount(data.getAccessPoints().size());
		globalStats.setStopAreaCount(data.getStopAreas().size());
		globalStats.setTimeTableCount(data.getTimetables().size());
		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.validator/" + COMMAND;
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
		CommandFactory.factories.put(ValidatorCommand.class.getName(), new DefaultCommandFactory());
	}
}
