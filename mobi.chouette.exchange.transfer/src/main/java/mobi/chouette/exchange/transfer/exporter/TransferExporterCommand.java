package mobi.chouette.exchange.transfer.exporter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.beanutils.BeanUtils;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JSONUtil;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.CommandCancelledException;
import mobi.chouette.exchange.ProgressionCommand;
import mobi.chouette.exchange.exporter.AbstractExporterCommand;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.ActionReporter.ERROR_CODE;
import mobi.chouette.exchange.transfer.LockManager;
import mobi.chouette.exchange.transfer.importer.TransferImportParameters;
import mobi.chouette.exchange.transfer.importer.JobParametersWrapper;
import mobi.chouette.exchange.report.ReportConstant;
import mobi.chouette.persistence.hibernate.ContextHolder;
import mobi.chouette.service.JobService;
import mobi.chouette.service.JobServiceManager;
import mobi.chouette.service.Parameters;

@Log4j
@Stateless(name = TransferExporterCommand.COMMAND)
public class TransferExporterCommand extends AbstractExporterCommand implements Command, Constant, ReportConstant {

	@EJB
	private JobServiceManager jobServiceManager;

	@EJB
	private LockManager synchronizer;

	public static final String COMMAND = "TransferExporterCommand";

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
		progression.initialize(context, 2);

		String currentTentant = ContextHolder.getContext();

		ReentrantLock lock = null;
		try {

			// read parameters
			Object configuration = context.get(CONFIGURATION);
			if (!(configuration instanceof TransferExportParameters)) {
				// fatal wrong parameters
				log.error("invalid parameters for transfer export " + configuration.getClass().getName());
				reporter.setActionError(context, ERROR_CODE.INVALID_PARAMETERS,
						"invalid parameters for transfer export " + configuration.getClass().getName());
				return ERROR;
			}

			TransferExportParameters parameters = (TransferExportParameters) configuration;
			if (parameters.getStartDate() != null && parameters.getEndDate() != null) {
				if (parameters.getStartDate().after(parameters.getEndDate())) {
					reporter.setActionError(context, ERROR_CODE.INVALID_PARAMETERS, "end date before start date");
					return ERROR;

				}

			}
			jobServiceManager.validateReferential(parameters.getDestReferentialName());

			// Obtain lock for destination referential
			lock = synchronizer.getLock(parameters.getDestReferentialName());

			// TODO : Progression

			Command dataLoader = CommandFactory.create(initialContext, TransferExportDataLoader.class.getName());
			dataLoader.execute(context);
			progression.execute(context);

			// Cancel existing jobs since this one is deleting all data
			for (JobService job : jobServiceManager.activeJobs()) {
				if (job.getReferential().equals(parameters.getDestReferentialName())) {
					jobServiceManager.cancel(job.getReferential(), job.getId());
				}
			}

			// Release lock
			lock.lock();

			TransferImportParameters importParameters = new TransferImportParameters();
			BeanUtils.copyProperties(importParameters, parameters);
			
			Map<String, InputStream> inputStreamsByName = new HashMap<>();
			
			inputStreamsByName.put("parameters.json",
					new ByteArrayInputStream(JSONUtil.toJSON(new JobParametersWrapper(importParameters)).getBytes()));
			JobService importJob = jobServiceManager.create(parameters.getDestReferentialName(), "importer", "transfer",
					inputStreamsByName);

			long maxWaitTime = 20000;
			long currWaitTime = 0;
			long pollDelay = 200;

			while (!lock.hasQueuedThreads() && currWaitTime < maxWaitTime) {
				log.info("Waiting for write job to obtain lock. Waitded for "+currWaitTime+"ms, will abort after "+maxWaitTime+"ms");
				Thread.sleep(pollDelay);
				currWaitTime += pollDelay;
			}
			if (lock.hasQueuedThreads()) {
				log.info("Write job has obtained lock");
				ContextHolder.setContext(parameters.getDestReferentialName());

				Command dataWriter = CommandFactory.create(initialContext, TransferExportDataWriter.class.getName());
				dataWriter.execute(context);
				progression.execute(context);

				result = SUCCESS;
			} else {
				// abort
				result = ERROR;
			}

		} catch (CommandCancelledException e) {
			reporter.setActionError(context, ERROR_CODE.INTERNAL_ERROR, "Command cancelled");
			log.error(e.getMessage());
		} catch (Exception e) {
			reporter.setActionError(context, ERROR_CODE.INTERNAL_ERROR, "Fatal :" + e);
			log.error(e.getMessage(), e);
		} finally {
			progression.dispose(context);
			ContextHolder.setContext(currentTentant);
			// Release lock
			if (lock != null) {
				if(lock.isHeldByCurrentThread()) {
					lock.unlock();
				}
			}
			log.info(Color.YELLOW + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.transfer/" + COMMAND;
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
		CommandFactory.factories.put(TransferExporterCommand.class.getName(), new DefaultCommandFactory());
	}
}
