package mobi.chouette.exchange.generic.exporter;

import java.io.IOException;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.CommandCancelledException;
import mobi.chouette.exchange.ProgressionCommand;
import mobi.chouette.exchange.exporter.AbstractExporterCommand;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.ActionReporter.ERROR_CODE;
import mobi.chouette.exchange.report.ReportConstant;
import mobi.chouette.persistence.hibernate.ContextHolder;

@Log4j
@Stateless(name = GenericExporterCommand.COMMAND)
public class GenericExporterCommand extends AbstractExporterCommand implements Command, Constant, ReportConstant {

	public static final String COMMAND = "GenericExporterCommand";
	
	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		ActionReporter reporter = ActionReporter.Factory.getInstance();

		// initialize reporting and progression
		ProgressionCommand progression = (ProgressionCommand) CommandFactory.create(initialContext,
				ProgressionCommand.class.getName());
		progression.initialize(context, 1);

		String currentTentant = ContextHolder.getContext();

		try {

			// read parameters
			Object configuration = context.get(CONFIGURATION);
			if (!(configuration instanceof GenericExportParameters)) {
				// fatal wrong parameters
				log.error("invalid parameters for generic export " + configuration.getClass().getName());
				reporter.setActionError(context, ERROR_CODE.INVALID_PARAMETERS,
						"invalid parameters for generic export " + configuration.getClass().getName());
				return ERROR;
			}

			GenericExportParameters parameters = (GenericExportParameters) configuration;
			if (parameters.getStartDate() != null && parameters.getEndDate() != null) {
				if (parameters.getStartDate().after(parameters.getEndDate())) {
					reporter.setActionError(context, ERROR_CODE.INVALID_PARAMETERS, "end date before start date");
					return ERROR;

				}
			}

			// TODO : Progression
			
			Command dataLoader =  CommandFactory.create(initialContext,
					GenericExportDataLoader.class.getName());
			dataLoader.execute(context);
			
			ContextHolder.setContext(parameters.getDestReferentialName());

			Command dataWriter =  CommandFactory.create(initialContext,
					GenericExportDataWriter.class.getName());
			dataWriter.execute(context);
			

			result = SUCCESS;

		} catch (CommandCancelledException e) {
			reporter.setActionError(context, ERROR_CODE.INTERNAL_ERROR, "Command cancelled");
			log.error(e.getMessage());
		} catch (Exception e) {
			reporter.setActionError(context, ERROR_CODE.INTERNAL_ERROR, "Fatal :" + e);
			log.error(e.getMessage(), e);
		} finally {
			progression.dispose(context);
			ContextHolder.setContext(currentTentant);
			log.info(Color.YELLOW + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.tools/" + COMMAND;
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
		CommandFactory.factories.put(GenericExporterCommand.class.getName(), new DefaultCommandFactory());
	}
}
