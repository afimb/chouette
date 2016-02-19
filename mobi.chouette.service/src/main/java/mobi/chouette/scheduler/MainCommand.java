package mobi.chouette.scheduler;

import java.io.IOException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.report.ActionError;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.ReportConstant;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.service.JobService;
import mobi.chouette.service.JobServiceManager;

@Log4j
@Stateless(name = MainCommand.COMMAND)
public class MainCommand implements Command, Constant {

	public static final String COMMAND = "MainCommand";

	@EJB
	JobServiceManager jobManager;

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public boolean execute(Context context) throws Exception {
		boolean result = false;

		// Long id = (Long) context.get(JOB_ID);
		// JobService jobService = jobManager.getJobService(id);
		JobService jobService = (JobService) context.get(JOB_DATA);
		try {
			// set job status to started
			// jobManager.start(jobService);
			context.put(CONFIGURATION, jobService.getActionParameter());
			ValidationParameters validationParameters = jobService.getValidationParameter();
			if (validationParameters != null)
			   context.put(VALIDATION, validationParameters);
			context.put(REPORT, new ActionReport());
			context.put(MAIN_VALIDATION_REPORT, new ValidationReport());

			String name = jobService.getCommandName();

			InitialContext ctx = (InitialContext) context.get(INITIAL_CONTEXT);
			Command command = CommandFactory.create(ctx, name);
			command.execute(context);

			ActionReport report = (ActionReport) context.get(REPORT);
			if (report.getResult().equals(ReportConstant.STATUS_ERROR)
					&& report.getFailure().getCode().equals(ActionError.CODE.INTERNAL_ERROR))
				jobManager.abort(jobService);
			else
				jobManager.terminate(jobService);

		} catch (javax.ejb.EJBTransactionRolledbackException ex) {
			log.warn("exception bypassed " + ex);
			// just ignore this exception
			ActionReport report = (ActionReport) context.get(REPORT);
			if (report.getResult().equals(ReportConstant.STATUS_ERROR)
					&& report.getFailure().getCode().equals(ActionError.CODE.INTERNAL_ERROR))
				jobManager.abort(jobService);
			else
				jobManager.terminate(jobService);

		} catch (Exception ex) {
			if (!COMMAND_CANCELLED.equals(ex.getMessage())) {
				log.error(ex,ex);
				jobManager.abort(jobService);
			}

		}
		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.service/" + COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				// try another way on test context
				String name = "java:module/" + COMMAND;
				try {
					result = (Command) context.lookup(name);
				} catch (NamingException e1) {
					log.error(e);
				}
			} catch (Exception e) {
				log.error(e);
			}
			return result;
		}
	}

	static {
		CommandFactory.factories.put(MainCommand.class.getName(), new DefaultCommandFactory());
	}
}
