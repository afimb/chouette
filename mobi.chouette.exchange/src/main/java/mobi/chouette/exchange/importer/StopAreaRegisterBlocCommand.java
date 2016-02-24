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
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.exchange.importer.updater.StopAreaUpdater;
import mobi.chouette.exchange.importer.updater.Updater;
import mobi.chouette.exchange.importer.updater.UpdaterUtils;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
@Stateless(name = StopAreaRegisterBlocCommand.COMMAND)
public class StopAreaRegisterBlocCommand implements Command {

	public static final String COMMAND = "StopAreaRegisterBlocCommand";

	@EJB 
	private StopAreaDAO stopAreaDAO;

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

			Monitor monitorInit = MonitorFactory.start(COMMAND + ".init");
			context.put(OPTIMIZED, optimized);
			Collection<StopArea> areas = (Collection<StopArea>) context.get(AREA_BLOC);
			Referential cache = new Referential();
			context.put(CACHE, cache);
			Referential referential = (Referential) context.get(REFERENTIAL);

			initializeStopArea(cache, areas);
			initializeAccessLink(cache, referential.getAccessLinks().values());
			initializeAccessPoint(cache, referential.getAccessPoints().values());
			log.info(Color.CYAN + monitorInit.stop() + Color.NORMAL);
			Monitor monitorUpdate = MonitorFactory.start(COMMAND + ".update");

			for (StopArea newValue : areas) {
				StopArea oldValue = cache.getStopAreas().get(newValue.getObjectId());
				stopUpdater.update(context, oldValue, newValue);
				stopAreaDAO.create(oldValue);
			}
			log.info(Color.CYAN + monitorUpdate.stop() + Color.NORMAL);
			Monitor monitorFlush = MonitorFactory.start(COMMAND + ".flush");
			stopAreaDAO.flush();
			log.info(Color.CYAN + monitorFlush.stop() + Color.NORMAL);
			result = SUCCESS;
		} catch (Exception e) {
			log.error(e);
			throw e;
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}
		return result;

	}

	private void initializeStopArea(Referential cache, Collection<StopArea> list) {
		Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
		List<StopArea> objects = stopAreaDAO.findAll();// ByObjectId(objectIds);
		for (StopArea object : objects) {
			cache.getStopAreas().put(object.getObjectId(), object);
//			addParent(cache, object);
		}

		for (StopArea item : list) {
			addStopAreaIfMissing(cache, item);
		}
	}

	private void addParent(Referential cache, StopArea object) {
		if (object.getParent() != null) {
			if (!cache.getStopAreas().containsKey(object.getParent().getObjectId())) {
				cache.getStopAreas().put(object.getParent().getObjectId(), object.getParent());
				addParent(cache, object.getParent());
			}
		}
	}

	private void addStopAreaIfMissing(Referential cache, StopArea item) {
		StopArea object = cache.getStopAreas().get(item.getObjectId());
		if (object == null) {
			object = ObjectFactory.getStopArea(cache, item.getObjectId());
			if (item.getParent() != null && item.getParent().getAreaType() == null)
			{
				log.error("areatype missing for "+item.getParent());
				return;
			}
			if (item.getParent() != null) {
				addStopAreaIfMissing(cache, item.getParent());
			}
		}

	}

	private void initializeAccessPoint(Referential cache, Collection<AccessPoint> list) {
		if (list.isEmpty())
			return;
		Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
		List<AccessPoint> objects = accessPointDAO.findByObjectId(objectIds);
		for (AccessPoint object : objects) {
			cache.getAccessPoints().put(object.getObjectId(), object);
		}

		for (AccessPoint item : list) {
			AccessPoint object = cache.getAccessPoints().get(item.getObjectId());
			if (object == null) {
				object = ObjectFactory.getAccessPoint(cache, item.getObjectId());
			}
		}
	}

	private void initializeAccessLink(Referential cache, Collection<AccessLink> list) {
		if (list.isEmpty())
			return;
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
		CommandFactory.factories.put(StopAreaRegisterBlocCommand.class.getName(), new DefaultCommandFactory());
	}
}
