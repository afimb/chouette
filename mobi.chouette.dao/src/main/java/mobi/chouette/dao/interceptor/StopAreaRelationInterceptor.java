package mobi.chouette.dao.interceptor;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.spi.CDI;

import lombok.extern.log4j.Log4j;
import mobi.chouette.dao.StopPointDAO;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.persistence.hibernate.ContextHolder;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

/**
 * StopPoint and StopArea reside in separate schemas. This Interceptor enriches these entities with relations between them upon load.
 */
@Log4j
public class StopAreaRelationInterceptor extends EmptyInterceptor {


    private StopPointDAO stopPointDAO;

    private static final String STOP_AREA_OBJECT_ID_PROPERTY = "objectId";


    @Override
    public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        init();

        if (entity instanceof StopArea) {

            StopArea stopArea = (StopArea) entity;
            log.trace("On load StopArea id: " + stopArea.getId());

            String stopAreaObjectId = getProperty(STOP_AREA_OBJECT_ID_PROPERTY, propertyNames, state);

            List<StopPoint> containedStopPointsProxy = (List<StopPoint>) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{List.class}, new LazyLoadContainedStopPointsInvocationHandler(stopAreaObjectId));

            stopArea.setContainedStopPoints(containedStopPointsProxy);
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
        if (stopPointDAO == null) {
            stopPointDAO = CDI.current().select(StopPointDAO.class).get();
        }
    }

    private class LazyLoadContainedStopPointsInvocationHandler implements InvocationHandler {

        private String stopAreaObjectId;

        private List<StopPoint> target;

        public LazyLoadContainedStopPointsInvocationHandler(String stopAreaObjectId) {
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
                    log.debug("Lazy loading stop points for stop area: " + stopAreaObjectId);
                    target = stopPointDAO.getStopPointsContainedInStopArea(stopAreaObjectId);
                } else {
                    log.debug("Initialize empty stop point list for stop area outside context: " + stopAreaObjectId);
                    target = new ArrayList<>();
                }
            }
        }

    }
}
