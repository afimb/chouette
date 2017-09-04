package mobi.chouette.dao.interceptor;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.spi.CDI;

import lombok.extern.log4j.Log4j;
import mobi.chouette.dao.ScheduledStopPointDAO;
import mobi.chouette.dao.StopPointDAO;
import mobi.chouette.model.ScheduledStopPoint;
import mobi.chouette.model.StopArea;
import mobi.chouette.persistence.hibernate.ContextHolder;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

/**
 * StopPoint and StopArea reside in separate schemas. This Interceptor enriches these entities with relations between them upon load.
 */
@Log4j
public class StopAreaRelationInterceptor extends EmptyInterceptor {


    private ScheduledStopPointDAO scheduledStopPointDAO;

    private static final String STOP_AREA_OBJECT_ID_PROPERTY = "objectId";


    @Override
    public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        init();

        if (entity instanceof StopArea) {

            StopArea stopArea = (StopArea) entity;
            log.trace("On load StopArea id: " + stopArea.getId());

            String stopAreaObjectId = getProperty(STOP_AREA_OBJECT_ID_PROPERTY, propertyNames, state);

            List<ScheduledStopPoint> containedScheduledStopPointsProxy = (List<ScheduledStopPoint>) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{List.class}, new LazyLoadContainedScheduledStopPointsInvocationHandler(stopAreaObjectId));

            stopArea.setContainedScheduledStopPoints(containedScheduledStopPointsProxy);
        }

        return super.onLoad(entity, id, state, propertyNames, types);
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
        if (scheduledStopPointDAO == null) {
            scheduledStopPointDAO = CDI.current().select(ScheduledStopPointDAO.class).get();
        }
    }

    private class LazyLoadContainedScheduledStopPointsInvocationHandler implements InvocationHandler {

        private String stopAreaObjectId;

        private List<ScheduledStopPoint> target;

        public LazyLoadContainedScheduledStopPointsInvocationHandler(String stopAreaObjectId) {
            this.stopAreaObjectId = stopAreaObjectId;
        }

        /**
         * Populate stop area with contained stop points relevant for the current context. If no referential is set in the context the population is omitted.
         */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (target == null) {
                setTarget();
            }
            return method.invoke(target, args);
        }


        private synchronized void setTarget() {
            if (target == null) {
                if (ContextHolder.getContext() != null) {
                    log.debug("Lazy loading scheduled stop points for stop area: " + stopAreaObjectId);
                    target = scheduledStopPointDAO.getScheduledStopPointsContainedInStopArea(stopAreaObjectId);
                } else {
                    log.debug("Initialize empty scheduled stop point list for stop area outside context: " + stopAreaObjectId);
                    target = new ArrayList<>();
                }
            }
        }

    }
}
