package mobi.chouette.dao.interceptor;

import lombok.extern.log4j.Log4j;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.dao.StopPointDAO;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.persistence.hibernate.ContextHolder;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import javax.enterprise.inject.spi.CDI;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * StopPoint and StopArea reside in separate schemas. This Interceptor enriches these entities with relations between them upon load.
 */
@Log4j
public class StopAreaRelationInterceptor extends EmptyInterceptor {


    private StopPointDAO stopPointDAO;


    @Override
    public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        init();

        if (entity instanceof StopArea) {

            StopArea stopArea = (StopArea) entity;
            log.warn("On load StopArea id: " + stopArea.getId());
            populateContainedStopPoints(stopArea);

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

    /**
     * Populate stop area with contained stop points relevant for the current context. If no referential is set in the context the population is omitted.
     */
    private void populateContainedStopPoints(StopArea stopArea) {
        if (ContextHolder.getContext() != null) {
            List<StopPoint> containedStopPoints = new ArrayList<>();
            containedStopPoints.addAll(stopPointDAO.getStopPointsContainedInStopArea(stopArea.getId()));
            log.info("Populated stopPoints for stop area: " + stopArea.getId() + ". Points: " + containedStopPoints);
            stopArea.getContainedStopPoints().addAll(containedStopPoints);
        }
    }


    private void init() {
        if (stopPointDAO == null) {
            stopPointDAO = CDI.current().select(StopPointDAO.class).get();
        }
    }
}
