package mobi.chouette.exchange.generic.importer;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

import javax.ejb.EJB;
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
import mobi.chouette.exchange.generic.LockManager;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.ActionReporter.ERROR_CODE;
import mobi.chouette.exchange.report.ReportConstant;
import mobi.chouette.persistence.hibernate.ContextHolder;
import mobi.chouette.service.JobServiceManager;

@Log4j
@Stateless(name = GenericImporterCommand.COMMAND)
public class GenericImporterCommand extends AbstractExporterCommand implements Command, Constant, ReportConstant {

	@EJB
	private JobServiceManager jobServiceManager;
	
	@EJB 
	private LockManager lockManager;
	
	public static final String COMMAND = "GenericImporterCommand";
	
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

		ReentrantLock lock = lockManager.getLock(currentTentant);
		try {

			// Wait for lock
			
			// Signal that the referential is ready for writing (no other jobs are trying to write to this referential)
			log.info("Ready to receive data into referential "+currentTentant);
			lock.lock();
			log.info("Data received into referential "+currentTentant+", finishing job");
			
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
			if(lock.isHeldByCurrentThread()) {
				lock.unlock();
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
		CommandFactory.factories.put(GenericImporterCommand.class.getName(), new DefaultCommandFactory());
	}
}
