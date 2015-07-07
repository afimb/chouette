package mobi.chouette.exchange.importer;

import java.io.IOException;
import java.util.concurrent.Callable;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.VehicleJourneyDAO;
import mobi.chouette.persistence.hibernate.ContextHolder;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
@Stateless(name = CopyCommand.COMMAND)
public class CopyCommand implements Command {

	public static final String COMMAND = "CopyCommand";

	@EJB
	private VehicleJourneyDAO vehicleJourneyDAO;

	@Resource(lookup = "java:comp/DefaultManagedExecutorService")
	ManagedExecutorService executor;

	@Override
	public boolean execute(Context context) throws Exception {

		boolean result = ERROR;

		try {

			Boolean optimized = (Boolean) context.get(OPTIMIZED);
			if (optimized) {
				int retryCount = 0;
				if (context.containsKey(COPY_IN_PROGRESS)) 
					log.info("waiting for previous copy");
				while (context.containsKey(COPY_IN_PROGRESS) && retryCount < 1000) {
					Thread.sleep(300);
				}
				if (retryCount == 1000)
				{
					throw new Exception("time-out in waiting for end of previous copy");
				}
				log.info("starting new copy");

				context.put(COPY_IN_PROGRESS, Boolean.TRUE);
				CommandCallable callable = new CommandCallable();
				callable.buffer = (String) context.remove(BUFFER);
				callable.schema = ContextHolder.getContext();
				callable.context = context;
				executor.submit(callable);
			}

			result = SUCCESS;
		} catch (Exception e) {
			log.error(e);
			throw e;
		}

		return result;
	}

	private class CommandCallable implements Callable<Void> {
		private String buffer;
		private String schema;
		private Context context;

		@Override
		@TransactionAttribute(TransactionAttributeType.REQUIRED)
		public Void call() throws Exception {
			try {
				Monitor monitor = MonitorFactory.start(COMMAND);
				ContextHolder.setContext(schema);
				vehicleJourneyDAO.copy(buffer);
				log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
				ContextHolder.setContext(null);
				return null;
			} finally {
				context.remove(COPY_IN_PROGRESS);
			}
		}

	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {

			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange/" + COMMAND;
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
		CommandFactory.factories.put(CopyCommand.class.getName(), new DefaultCommandFactory());
	}
}
