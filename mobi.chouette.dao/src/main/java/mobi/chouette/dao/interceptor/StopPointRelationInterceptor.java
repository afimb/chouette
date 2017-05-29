package mobi.chouette.dao.interceptor;

import lombok.extern.log4j.Log4j;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import javax.enterprise.inject.spi.CDI;
import java.io.Serializable;

/**
 * StopPoint and StopArea reside in separate schemas. This Interceptor enriches these entities with relations between them upon load.
 */
@Log4j
public class StopPointRelationInterceptor extends EmptyInterceptor {

    private StopAreaDAO stopAreaDAO;

    private static final String STOP_POINT_CONTAINED_IN_STOP_AREA_ID_PROPERTY = "containedInStopAreaObjectId";


    @Override
    public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        init();

        if (entity instanceof StopPoint) {

            StopPoint stopPoint = (StopPoint) entity;
            log.trace("On load StopPoint id: " + stopPoint.getId());
            String containedInStopAreaId = getProperty(STOP_POINT_CONTAINED_IN_STOP_AREA_ID_PROPERTY, propertyNames, state);

            if (containedInStopAreaId != null) {
                stopPoint.setContainedInStopArea(stopAreaDAO.findByObjectId(containedInStopAreaId));
            }

        }
        return super.onLoad(entity, id, state, propertyNames, types);
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

        if (entity instanceof StopPoint) {
            StopPoint stopPoint = ((StopPoint) entity);
            StopArea stopArea = stopPoint.getContainedInStopArea();
            if (stopArea != null) {
                log.debug("On save stoppoint with stop area");
                if (!stopArea.isSaved()) {
                    log.debug("Cascading persist of new stop area +" + stopArea.getObjectId() + "+ for created/updated stop point: " + stopPoint.getObjectId());
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
}
