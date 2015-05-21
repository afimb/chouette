package mobi.chouette.exchange.validator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.ProgressionCommand;
import mobi.chouette.exchange.exporter.AbstractExporterCommand;
import mobi.chouette.exchange.report.ActionError;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.LineInfo;
import mobi.chouette.exchange.report.DataStats;
import mobi.chouette.exchange.validation.DaoLineValidatorCommand;
import mobi.chouette.exchange.validation.DaoSharedDataValidatorCommand;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.model.Line;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
@Stateless(name = ValidatorCommand.COMMAND)
public class ValidatorCommand extends AbstractExporterCommand implements Command, Constant {

	public static final String COMMAND = "ValidatorCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);

		// initialize reporting and progression
		ProgressionCommand progression = (ProgressionCommand) CommandFactory.create(initialContext,
				ProgressionCommand.class.getName());
		progression.initialize(context,1);

		context.put(VALIDATION_DATA, new ValidationData());

		// read parameters
		Object configuration = context.get(CONFIGURATION);
		if (!(configuration instanceof ValidateParameters)) {
			// fatal wrong parameters
			ActionReport report = (ActionReport) context.get(REPORT);
			log.error("invalid parameters for validation " + configuration.getClass().getName());
			report.setFailure(new ActionError(ActionError.CODE.INVALID_PARAMETERS,"invalid parameters for validation " + configuration.getClass().getName()));
			progression.dispose(context);
			return ERROR;
		}

		ValidationParameters validationParameters = (ValidationParameters) context.get(VALIDATION);
		if (validationParameters == null)
		{
			ActionReport report = (ActionReport) context.get(REPORT);
			log.error("no validation parameters for validation ");
			report.setFailure(new ActionError(ActionError.CODE.INVALID_PARAMETERS,"no validation parameters for validation "));
			progression.dispose(context);
			return ERROR;

		}

		ValidateParameters parameters = (ValidateParameters) configuration;

		String type = parameters.getReferencesType();
		// set default type 
		if (type == null || type.isEmpty() )
		{
			// all lines
			type = "line";
			parameters.setIds(null);
		}
		type=type.toLowerCase();

		try {

			List<Long> ids = null;
			if (parameters.getIds() != null) {
				ids = new ArrayList<Long>(parameters.getIds());
			}

			Set<Line> lines = loadLines(type, ids);
			progression.execute(context);
			progression.start(context, lines.size() + 1);
			Command validateLine = CommandFactory.create(initialContext, DaoLineValidatorCommand.class.getName());

			int lineCount = 0;
			for (Line line : lines) {
				context.put(LINE_ID, line.getId());
				progression.execute(context);
				boolean resLine = validateLine.execute(context);
				ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
				ActionReport report = (ActionReport) context.get(REPORT);
				LineInfo lineInfo = new LineInfo(line.getName() + " (" + line.getNumber() + ")");
				DataStats stats = lineInfo.getStats();
				stats.setLineCount(1);
				stats.setJourneyPatternCount(data.getJourneyPatterns().size());
				stats.setRouteCount(data.getRoutes().size());
				stats.setVehicleJourneyCount(data.getVehicleJourneys().size());

				// merge lineStats to global ones
				DataStats globalStats = report.getStats();
				globalStats.setLineCount(globalStats.getLineCount() + stats.getLineCount());
				globalStats.setRouteCount(globalStats.getRouteCount() + stats.getRouteCount());
				globalStats.setVehicleJourneyCount(globalStats.getVehicleJourneyCount() + stats.getVehicleJourneyCount());
				globalStats.setJourneyPatternCount(globalStats.getJourneyPatternCount() + stats.getJourneyPatternCount());
				report.getLines().add(lineInfo);

				if (resLine == SUCCESS) {
					lineCount++;
				}
			}

			if (lineCount > 0) {
				progression.execute(context);
				Command validateSharedData = CommandFactory.create(initialContext,
						DaoSharedDataValidatorCommand.class.getName());
				result = validateSharedData.execute(context);
				if (result)
				{
					ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
					ActionReport report = (ActionReport) context.get(REPORT);
					DataStats globalStats = report.getStats();
					globalStats.setConnectionLinkCount(data.getConnectionLinks().size());
					globalStats.setAccessPointCount(data.getAccessPoints().size());
					globalStats.setStopAreaCount(data.getStopAreas().size());
					globalStats.setTimeTableCount(data.getTimetables().size());
				}
			}

			// terminate : nothing to do
			progression.terminate(context,1);
			progression.execute(context);

		} catch (Exception e) {
			ActionReport report = (ActionReport) context.get(REPORT);
			report.setFailure(new ActionError(ActionError.CODE.INTERNAL_ERROR,"Fatal :" + e));
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
