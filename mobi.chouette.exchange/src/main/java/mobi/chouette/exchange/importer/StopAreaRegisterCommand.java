package mobi.chouette.exchange.importer;

import java.io.IOException;
import java.util.Collection;
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
import mobi.chouette.dao.AccessLinkDAO;
import mobi.chouette.dao.AccessPointDAO;
import mobi.chouette.dao.ConnectionLinkDAO;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.exchange.importer.updater.StopAreaUpdater;
import mobi.chouette.exchange.importer.updater.Updater;
import mobi.chouette.exchange.importer.updater.UpdaterUtils;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
@Stateless(name = StopAreaRegisterCommand.COMMAND)
public class StopAreaRegisterCommand implements Command {

	public static final String COMMAND = "StopAreaRegisterCommand";

	@EJB
	private StopAreaDAO stopAreaDAO;

	@EJB
	private ConnectionLinkDAO connectionLinkDAO;

	@EJB
	private AccessLinkDAO accessLinkDAO;

	@EJB
	private AccessPointDAO accessPointDAO;

	@EJB(beanName = StopAreaUpdater.BEAN_NAME)
	private Updater<StopArea> stopUpdater;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean execute(Context context) throws Exception {

		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			Boolean optimized = Boolean.TRUE;
			context.put(OPTIMIZED, optimized);

			Referential cache = new Referential();
			context.put(CACHE, cache);
			Referential referential = (Referential) context.get(REFERENTIAL);

			initializeStopArea(cache, referential.getStopAreas().values());
			initializeConnectionLink(cache, referential.getConnectionLinks()
					.values());
			initializeAccessLink(cache, referential.getAccessLinks().values());
			initializeAccessPoint(cache, referential.getAccessPoints().values());

			for (StopArea newValue : referential.getStopAreas().values()) {
				StopArea oldValue = cache.getStopAreas().get(newValue.getObjectId());
				stopUpdater.update(context, oldValue , newValue);
				stopAreaDAO.create(oldValue);
			}

			result = SUCCESS;
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
		log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		return result;
	}

	private void initializeStopArea(Referential cache, Collection<StopArea> list) {
		Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
		List<StopArea> objects = stopAreaDAO.findByObjectId(objectIds);
		for (StopArea object : objects) {
			cache.getStopAreas().put(object.getObjectId(), object);
		}

		for (StopArea item : list) {
			StopArea object = cache.getStopAreas().get(item.getObjectId());
			if (object == null) {
				object = ObjectFactory.getStopArea(cache, item.getObjectId());
			}
		}
	}

	private void initializeAccessPoint(Referential cache,
			Collection<AccessPoint> list) {
		Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
		List<AccessPoint> objects = accessPointDAO.findByObjectId(objectIds);
		for (AccessPoint object : objects) {
			cache.getAccessPoints().put(object.getObjectId(), object);
		}

		for (AccessPoint item : list) {
			AccessPoint object = cache.getAccessPoints()
					.get(item.getObjectId());
			if (object == null) {
				object = ObjectFactory
						.getAccessPoint(cache, item.getObjectId());
			}
		}
	}

	private void initializeAccessLink(Referential cache,
			Collection<AccessLink> list) {
		Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
		List<AccessLink> objects = accessLinkDAO.findByObjectId(objectIds);
		for (AccessLink object : objects) {
			cache.getAccessLinks().put(object.getObjectId(), object);
		}

		for (AccessLink item : list) {
			AccessLink object = cache.getAccessLinks().get(item.getObjectId());
			if (object == null) {
				object = ObjectFactory.getAccessLink(cache, item.getObjectId());
			}
		}
	}

	private void initializeConnectionLink(Referential cache,
			Collection<ConnectionLink> list) {
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
				log.error(e);
			}
			return result;
		}
	}

	static {
		CommandFactory.factories.put(StopAreaRegisterCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
