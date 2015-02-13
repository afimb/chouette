package mobi.chouette.exchange.importer;

import java.io.IOException;

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
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.exchange.importer.updater.LineUpdater;
import mobi.chouette.exchange.importer.updater.Updater;
import mobi.chouette.exchange.importer.updater.UpdaterFactory;
import mobi.chouette.model.Line;
import mobi.chouette.model.util.Referential;

@Stateless(name = RegisterCommand.COMMAND)
@Log4j
public class RegisterCommand implements Command {

	public static final String COMMAND = "RegisterCommand";

	@EJB
	private LineDAO lineDAO;
	
	@EJB(beanName=LineUpdater.BEAN_NAME)
	private Updater<Line> lineUpdater;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean execute(Context context) throws Exception {

		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			Referential cache  = new Referential();
			context.put(CACHE, cache);

			Referential referential = (Referential) context.get(REFERENTIAL);
			Line newValue = referential.getLines().values().iterator().next();
			log.info("[DSU] register line : " + newValue.getObjectId());

//			Updater<Line> lineUpdater = UpdaterFactory.create(initialContext,
//					LineUpdater.class.getName());
			Line oldValue = lineDAO.findByObjectId(newValue.getObjectId());
			if (oldValue == null) {
				oldValue = new Line();
				oldValue.setObjectId(newValue.getObjectId());

			}
			lineUpdater.update(context, oldValue, newValue);
			lineDAO.create(oldValue);
			
			result = SUCCESS;
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
		log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
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
		CommandFactory.factories.put(RegisterCommand.class.getName(), factory);
	}
}
