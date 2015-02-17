package mobi.chouette.exchange.importer;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

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

@Stateless(name = CopyCommand.COMMAND)
@Log4j
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

				final String buffer = new String((String) context.get(BUFFER));
				final String schema = ContextHolder.getContext();
				Future<Void> future = executor.submit(new Callable<Void>() {

					@Override
					@TransactionAttribute(TransactionAttributeType.REQUIRED)
					public Void call() throws Exception {
						Monitor monitor = MonitorFactory.start(COMMAND);
						ContextHolder.setContext(schema);
						vehicleJourneyDAO.copy(buffer);
						log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
						ContextHolder.setContext(null);
						return null;
					}
				});
			}

			result = SUCCESS;
		} catch (Exception e) {
			log.error(e);
			throw e;
		}

		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {

			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange/" + COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				log.error(e);
			}
			return result;
		}
	}

	static {
		CommandFactory factory = new DefaultCommandFactory();
		CommandFactory.factories.put(CopyCommand.class.getName(), factory);
	}
}
