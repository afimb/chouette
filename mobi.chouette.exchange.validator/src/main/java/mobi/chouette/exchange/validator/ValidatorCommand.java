package mobi.chouette.exchange.validator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.CompanyDAO;
import mobi.chouette.dao.GroupOfLineDAO;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.dao.NetworkDAO;
import mobi.chouette.exchange.ProgressionCommand;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.LineInfo;
import mobi.chouette.exchange.report.LineStats;
import mobi.chouette.exchange.report.LineInfo.LINE_STATE;
import mobi.chouette.exchange.validation.DaoLineValidatorCommand;
import mobi.chouette.exchange.validation.DaoSharedDataValidatorCommand;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.model.Company;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
@Stateless(name = ValidatorCommand.COMMAND)
public class ValidatorCommand implements Command, Constant {

	public static final String COMMAND = "ValidatorCommand";

	@EJB
	private LineDAO lineDAO;

	@EJB
	private NetworkDAO ptNetworkDAO;

	@EJB
	private CompanyDAO companyDAO;

	@EJB
	private GroupOfLineDAO groupOfLineDAO;

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
			report.setFailure("invalid parameters for validation " + configuration.getClass().getName());
			progression.dispose(context);
			return ERROR;
		}

		ValidationParameters validationParameters = (ValidationParameters) context.get(VALIDATION);
		if (validationParameters == null)
		{
			ActionReport report = (ActionReport) context.get(REPORT);
			log.error("no validation parameters for validation ");
			report.setFailure("no validation parameters for validation ");
			progression.dispose(context);
			return ERROR;

		}

		ValidateParameters parameters = (ValidateParameters) configuration;

		String type = parameters.getReferencesType().toLowerCase();

		try {

			List<Object> ids = null;
			if (parameters.getIds() != null) {
				ids = new ArrayList<Object>(parameters.getIds());
			}

			Set<Line> lines = new HashSet<Line>();
			if (ids == null || ids.isEmpty()) {
				lines.addAll(lineDAO.findAll());
			} else {
				if (type.equals("line")) {
					lines.addAll(lineDAO.findAll(ids));
				} else if (type.equals("network")) {
					List<Network> list = ptNetworkDAO.findAll(ids);
					for (Network ptNetwork : list) {
						lines.addAll(ptNetwork.getLines());
					}
				} else if (type.equals("company")) {
					List<Company> list = companyDAO.findAll(ids);
					for (Company company : list) {
						lines.addAll(company.getLines());
					}
				} else if (type.equals("groupofline")) {
					List<GroupOfLine> list = groupOfLineDAO.findAll(ids);
					for (GroupOfLine groupOfLine : list) {
						lines.addAll(groupOfLine.getLines());
					}
				}
			}
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
				LineInfo lineInfo = new LineInfo();
				lineInfo.setName(line.getName() + " (" + line.getNumber() + ")");
				LineStats stats = new LineStats();
				stats.setJourneyPatternCount(data.getJourneyPatterns().size());
				stats.setRouteCount(data.getRoutes().size());
				stats.setVehicleJourneyCount(data.getVehicleJourneys().size());

				lineInfo.setStatus(LINE_STATE.OK);
				// merge lineStats to global ones
				LineStats globalStats = report.getStats();
				if (globalStats == null) {
					globalStats = new LineStats();
					report.setStats(globalStats);
				}
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
					LineStats globalStats = report.getStats();
					if (globalStats == null) {
						globalStats = new LineStats();
						report.setStats(globalStats);
					}
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
			report.setFailure("Fatal :" + e);
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
