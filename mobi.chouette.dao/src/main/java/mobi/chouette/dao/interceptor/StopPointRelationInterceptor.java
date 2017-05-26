package mobi.chouette.dao.interceptor;

import lombok.extern.log4j.Log4j;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.model.StopPoint;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import javax.enterprise.inject.spi.CDI;
import java.io.Serializable;
/**
 * StopPoint and StopArea reside in separate schemas. This Interceptor enriches these entities with relations between them upon load.
 */
@Log4j
public class StopPointRelationInterceptor  extends EmptyInterceptor {

    private StopAreaDAO stopAreaDAO;

    private static final String STOP_POINT_CONTAINED_IN_STOP_AREA_ID_PROPERTY = "containedInStopAreaId";


    @Override
    public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        init();

        if (entity instanceof StopPoint) {

            StopPoint stopPoint = (StopPoint) entity;
            log.warn("On load StopPoint id: "+ stopPoint.getId());
            Object containedInStopAreaId = getProperty(STOP_POINT_CONTAINED_IN_STOP_AREA_ID_PROPERTY, propertyNames, state);

            if (containedInStopAreaId != null) {
                log.info("Looking for stop area for containedInStopAreaId: " + containedInStopAreaId);
                stopPoint.setContainedInStopArea(stopAreaDAO.find(containedInStopAreaId));
            }

        }
        return super.onLoad(entity, id, state, propertyNames, types);
    }


    private Object getProperty(String propertyName, String[] propertyNames, Object[] state) {

        for (int i = 0; i < propertyNames.length; i++) {

            if (propertyName.equals(propertyNames[i])) {
                return state[i];
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
