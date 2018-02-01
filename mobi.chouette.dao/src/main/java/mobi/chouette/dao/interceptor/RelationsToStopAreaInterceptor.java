package mobi.chouette.dao.interceptor;

import java.io.Serializable;

import javax.enterprise.inject.spi.CDI;

import lombok.extern.log4j.Log4j;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.model.ObjectReference;
import mobi.chouette.model.ScheduledStopPoint;
import mobi.chouette.model.StopArea;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

/**
 * StopPoint and RouteSections reside in separate schemas from StopArea. This Interceptor enriches these entities with relations between them upon load.
 */
@Log4j
public class RelationsToStopAreaInterceptor extends EmptyInterceptor {

	private StopAreaDAO stopAreaDAO;

	private static final String STOP_POINT_CONTAINED_IN_STOP_AREA_ID_PROPERTY = "containedInStopAreaObjectId";

	@Override
	public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		init();

		if (entity instanceof ScheduledStopPoint) {
			loadStopAreasForScheduledStopPoint((ScheduledStopPoint) entity, state, propertyNames);
		}
		return super.onLoad(entity, id, state, propertyNames, types);
	}

	private void loadStopAreasForScheduledStopPoint(ScheduledStopPoint entity, Object[] state, String[] propertyNames) {
		ScheduledStopPoint scheduledStopPoint = entity;
		log.trace("On load StopPoint id: " + scheduledStopPoint.getId());
		String containedInStopAreaId = getProperty(STOP_POINT_CONTAINED_IN_STOP_AREA_ID_PROPERTY, propertyNames, state);

		if (!(scheduledStopPoint.getContainedInStopAreaRef() instanceof LazyLoadingStopAreaReference)) {
			scheduledStopPoint.setContainedInStopAreaRef(new LazyLoadingStopAreaReference(containedInStopAreaId));
		}
	}

	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		onCreateOrUpdate(entity);
		return super.onSave(entity, id, state, propertyNames, types);
	}

	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
		onCreateOrUpdate(entity);
		return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
	}

	private void onCreateOrUpdate(Object entity) {
		init();

		if (entity instanceof ScheduledStopPoint) {
			ScheduledStopPoint scheduledStopPoint = ((ScheduledStopPoint) entity);
			if (scheduledStopPoint.getContainedInStopAreaRef().isLoaded()) {
				StopArea stopArea = scheduledStopPoint.getContainedInStopAreaRef().getObject();
				if (stopArea != null && stopArea.getId() == null && !stopArea.isDetached() && stopArea.getImportMode().shouldCreateMissingStopAreas()) {
					log.debug("Cascading persist of new stop area " + stopArea.getObjectId() + " for created/updated stop point: " + scheduledStopPoint.getObjectId());
					stopAreaDAO.create(stopArea);
				}
			}

		}
	}


	private <T> T getProperty(String propertyName, String[] propertyNames, Object[] state) {
		for (int i = 0; i < propertyNames.length; i++) {
			if (propertyName.equals(propertyNames[i])) {
				return (T) state[i];
			}
		}
		throw new RuntimeException("Property not found: " + propertyName);
	}


	private void init() {
		if (stopAreaDAO == null) {
			stopAreaDAO = CDI.current().select(StopAreaDAO.class).get();
		}
	}

	private class LazyLoadingStopAreaReference implements ObjectReference<StopArea> {

		private String stopAreaObjectId;

		private StopArea target;

		private boolean loaded;

		public LazyLoadingStopAreaReference(String stopAreaObjectId) {
			this.stopAreaObjectId = stopAreaObjectId;
		}

		@Override
		public String getObjectId() {
			return stopAreaObjectId;
		}

		@Override
		public StopArea getObject() {
			if (!loaded) {
				setTarget();
			}
			return target;
		}

		@Override
		public boolean isLoaded() {
			return loaded;
		}

		private synchronized void setTarget() {
			if (!loaded) {
				if (stopAreaObjectId != null) {
					target = stopAreaDAO.findByObjectId(stopAreaObjectId);
				}
				loaded = true;
			}
		}
	}
}
