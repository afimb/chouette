package mobi.chouette.dao.interceptor;

import lombok.extern.log4j.Log4j;
import mobi.chouette.dao.StopPointDAO;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.persistence.hibernate.ContextHolder;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import javax.enterprise.inject.spi.CDI;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
            populateContainedStopPoints(stopArea, getProperty(STOP_AREA_OBJECT_ID_PROPERTY, propertyNames, state));

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

    /**
     * Populate stop area with contained stop points relevant for the current context. If no referential is set in the context the population is omitted.
     */
    private void populateContainedStopPoints(StopArea stopArea, String stopAreaObjectId) {
        if (ContextHolder.getContext() != null) {
            List<StopPoint> stopPoints = stopPointDAO.getStopPointsContainedInStopArea(stopAreaObjectId);

            List<StopPoint> notAlreadyInCollectionStopPoints = stopPoints.stream()
                                                                       .filter(stopPoint -> !alreadyExistingInStopAresCollection(stopArea, stopPoint))
                                                                       .collect(Collectors.toList());

            log.debug("Populated stopPoints for stop area: " + stopArea.getId() + ". New points: " + notAlreadyInCollectionStopPoints);
            stopArea.getContainedStopPoints().addAll(notAlreadyInCollectionStopPoints);
        }
    }

    private boolean alreadyExistingInStopAresCollection(StopArea stopArea, StopPoint stopPoint) {
        return stopArea.getContainedStopPoints().stream().anyMatch(existingStopPoint -> Objects.equals(existingStopPoint.getId(), stopPoint.getId()));
    }

    private void init() {
        if (stopPointDAO == null) {
            stopPointDAO = CDI.current().select(StopPointDAO.class).get();
        }
    }
}
