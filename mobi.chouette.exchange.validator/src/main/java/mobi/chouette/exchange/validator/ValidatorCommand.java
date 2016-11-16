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
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_STATE;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_TYPE;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.model.Line;
import mobi.chouette.model.util.NamingUtil;

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
		ActionReporter reporter = ActionReporter.Factory.getInstance();

		// initialize reporting and progression
		ProgressionCommand progression = (ProgressionCommand) CommandFactory.create(initialContext,
				ProgressionCommand.class.getName());
		try {

			// read parameters
			Object configuration = context.get(CONFIGURATION);
			if (!(configuration instanceof ValidateParameters)) {
				// fatal wrong parameters
				log.error("invalid parameters for validation " + configuration.getClass().getName());
				reporter.setActionError(context, ActionReporter.ERROR_CODE.INVALID_PARAMETERS, 
						"invalid parameters for validation " + configuration.getClass().getName());
				return ERROR;
			}

			ValidationParameters validationParameters = (ValidationParameters) context.get(VALIDATION);
			if (validationParameters == null) {
				log.error("no validation parameters for validation ");
				reporter.setActionError(context, ActionReporter.ERROR_CODE.INVALID_PARAMETERS,
						"no validation parameters for validation ");
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
			reporter.setActionError(context, ActionReporter.ERROR_CODE.INTERNAL_ERROR, "Command cancelled");
			log.error(e.getMessage());
		} catch (Exception e) {
			reporter.setActionError(context, ActionReporter.ERROR_CODE.INTERNAL_ERROR, "Fatal :" + e);
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
		ActionReporter reporter = ActionReporter.Factory.getInstance();

		// initialisation
		List<? extends Command> preProcessingCommands = commands.getPreProcessingCommands(context, true);
		progression.initialize(context, preProcessingCommands.size()+1);
		for (Command command : preProcessingCommands) {
			result = command.execute(context);
			if (!result) {
				reporter.setActionError(context, ActionReporter.ERROR_CODE.NO_DATA_FOUND,"no data selected");
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
			reporter.setActionError(context, ActionReporter.ERROR_CODE.NO_DATA_FOUND,"no data selected");
			return ERROR;

		}
		progression.execute(context);
		// process lines
		progression.start(context, lines.size());
		int lineCount = 0;
		// export each line
		for (Long lineId : lines) {
			context.put(LINE_ID, lineId);
			boolean validateFailed = false;
			List<? extends Command> lineProcessingCommands = commands.getLineProcessingCommands(context, true);
			for (Command command : lineProcessingCommands) {
				result = command.execute(context);
				if (!result) {
					validateFailed = true;
					break;
				}
			}
			progression.execute(context);
			// TODO a mettre dans une commande dédiée
			ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
			Line line = data.getCurrentLine();
			reporter.addObjectReport(context, line.getChouetteId().getObjectId(), OBJECT_TYPE.LINE, NamingUtil.getName(line), OBJECT_STATE.OK, IO_TYPE.INPUT);
			reporter.setStatToObjectReport(context, line.getChouetteId().getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.LINE, 1);
			reporter.setStatToObjectReport(context, line.getChouetteId().getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.JOURNEY_PATTERN, data.getJourneyPatterns().size());
			reporter.setStatToObjectReport(context, line.getChouetteId().getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.ROUTE, data.getRoutes().size());
			reporter.setStatToObjectReport(context, line.getChouetteId().getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.VEHICLE_JOURNEY, data.getVehicleJourneys().size());

			if (!validateFailed) 
			{
				lineCount ++;
			}
			else if (!continueLineProcesingOnError)
			{
				reporter.setActionError(context, ActionReporter.ERROR_CODE.INVALID_DATA,"unable to validate data");
				return ERROR;
			}
		}
		// post processing
		
		// check if data where exported
		if (lineCount == 0) {
			progression.terminate(context, 1);
			reporter.setActionError(context, ActionReporter.ERROR_CODE.NO_DATA_PROCEEDED,"no data validated");
			progression.execute(context);
			return ERROR;		
		}
		
		List<? extends Command> postProcessingCommands = commands.getPostProcessingCommands(context, true);
		progression.terminate(context, postProcessingCommands.size());
		for (Command command : postProcessingCommands) {
			result = command.execute(context);
			if (!result) {
				if (!reporter.hasActionError(context))
				   reporter.setActionError(context, ActionReporter.ERROR_CODE.NO_DATA_PROCEEDED,"no data exported");
				return ERROR;
			}
			progression.execute(context);
		}	
		// TODO a mettre dans une commande dédiée
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		reporter.addObjectReport(context, "merged", OBJECT_TYPE.NETWORK, "networks", OBJECT_STATE.OK, IO_TYPE.INPUT);
		reporter.setStatToObjectReport(context, "merged", OBJECT_TYPE.NETWORK, OBJECT_TYPE.NETWORK, data.getNetworks().size());
		reporter.addObjectReport(context, "merged", OBJECT_TYPE.COMPANY, "companies", OBJECT_STATE.OK, IO_TYPE.INPUT);
		reporter.setStatToObjectReport(context, "merged", OBJECT_TYPE.COMPANY, OBJECT_TYPE.COMPANY, data.getCompanies().size());
		reporter.addObjectReport(context, "merged", OBJECT_TYPE.CONNECTION_LINK, "connection links", OBJECT_STATE.OK, IO_TYPE.INPUT);
		reporter.setStatToObjectReport(context, "merged", OBJECT_TYPE.CONNECTION_LINK, OBJECT_TYPE.CONNECTION_LINK, data.getConnectionLinks().size());
		reporter.addObjectReport(context, "merged", OBJECT_TYPE.ACCESS_POINT, "access points", OBJECT_STATE.OK, IO_TYPE.INPUT);
		reporter.setStatToObjectReport(context, "merged", OBJECT_TYPE.ACCESS_POINT, OBJECT_TYPE.ACCESS_POINT, data.getAccessPoints().size());
		reporter.addObjectReport(context, "merged", OBJECT_TYPE.STOP_AREA, "stop areas", OBJECT_STATE.OK, IO_TYPE.INPUT);
		reporter.setStatToObjectReport(context, "merged", OBJECT_TYPE.STOP_AREA, OBJECT_TYPE.STOP_AREA, data.getStopAreas().size());
		reporter.addObjectReport(context, "merged", OBJECT_TYPE.TIMETABLE, "calendars", OBJECT_STATE.OK, IO_TYPE.INPUT);
		reporter.setStatToObjectReport(context, "merged", OBJECT_TYPE.TIMETABLE, OBJECT_TYPE.TIMETABLE, data.getTimetables().size());
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
