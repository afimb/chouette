package mobi.chouette.exchange.importer;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.ConnectionLinkDAO;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.exchange.importer.updater.ConnectionLinkUpdater;
import mobi.chouette.exchange.importer.updater.Updater;
import mobi.chouette.exchange.importer.updater.UpdaterUtils;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
@Stateless(name = ConnectionLinkRegisterBlocCommand.COMMAND)
public class ConnectionLinkRegisterBlocCommand implements Command {

	public static final String COMMAND = "ConnectionLinkRegisterBlocCommand";

	@EJB 
	private StopAreaDAO stopAreaDAO;

	@EJB 
	private ConnectionLinkDAO connectionLinkDAO;

	@EJB(beanName = ConnectionLinkUpdater.BEAN_NAME)
	private Updater<ConnectionLink> connectionLinkUpdater;


	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean execute(Context context) throws Exception {

		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			Boolean optimized = Boolean.TRUE;
			
			Monitor monitorInit = MonitorFactory.start(COMMAND+".init");
			context.put(OPTIMIZED, optimized);
			Collection<ConnectionLink> connectionLinks = (Collection<ConnectionLink>) context.get(CONNECTION_LINK_BLOC);
			Referential cache = new Referential();
			context.put(CACHE, cache);
			initializeStopArea(cache, connectionLinks);
			initializeConnectionLink(cache, connectionLinks);
			log.info(Color.CYAN + monitorInit.stop() + Color.NORMAL);
			Monitor monitorUpdate = MonitorFactory.start(COMMAND+".update");

			for (ConnectionLink newValue : connectionLinks) {
				ConnectionLink oldValue = cache.getConnectionLinks().get(newValue.getObjectId());
				connectionLinkUpdater.update(context, oldValue , newValue);
				connectionLinkDAO.create(oldValue);
			}
			log.info(Color.CYAN + monitorUpdate.stop() + Color.NORMAL);
			Monitor monitorFlush = MonitorFactory.start(COMMAND + ".flush");
			connectionLinkDAO.flush();
			log.info(Color.CYAN + monitorFlush.stop() + Color.NORMAL);
			result = SUCCESS;
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
		finally
		{
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}
		return result;

	}

	private void initializeStopArea(Referential cache, Collection<ConnectionLink> list) {
		Collection<String> objectIds = new HashSet<>();
		for (ConnectionLink connectionLink : list) {
			objectIds.add(connectionLink.getStartOfLink().getObjectId());
			objectIds.add(connectionLink.getEndOfLink().getObjectId());
		}
		List<StopArea> objects = stopAreaDAO.findByObjectId(objectIds);
		for (StopArea object : objects) {
			cache.getStopAreas().put(object.getObjectId(), object);
		}

	}


	private void initializeConnectionLink(Referential cache,
			Collection<ConnectionLink> list) {
		if (list.isEmpty()) return; 
		Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
		List<ConnectionLink> objects = connectionLinkDAO
				.findByObjectId(objectIds);
		for (ConnectionLink object : objects) {
			cache.getConnectionLinks().put(object.getObjectId(), object);
		}

		for (ConnectionLink item : list) {
			ConnectionLink object = cache.getConnectionLinks().get(
					item.getObjectId());
			if (object == null) {
				object = ObjectFactory.getConnectionLink(cache,
						item.getObjectId());
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
		CommandFactory.factories.put(ConnectionLinkRegisterBlocCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
