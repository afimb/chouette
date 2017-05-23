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


    private StopAreaDAO stopAreaDAO;


    private StopPointDAO stopPointDAO;

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

        } else if (entity instanceof StopArea) {

            StopArea stopArea = (StopArea) entity;
            log.warn("On load StopArea id: "+ stopArea.getId());
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

    private void populateContainedStopPoints(StopArea stopArea) {
        List<StopPoint> containedStopPoints = new ArrayList<>();

        String originalContext = ContextHolder.getContext();
        try {
            log.warn("OrgContext: "+originalContext);

//            log.info("Found referentials: "+ referentialDAO.getReferentials());
//            // Find stoppoints with ref to stop area in all schemas??!
//            for (String referential : referentialDAO.getReferentials()) {
//                ContextHolder.setContext(referential);
//                log.info("Looking for stop points for referential: " + referential);
                containedStopPoints.addAll(stopPointDAO.getStopPointsContainedInStopArea(stopArea.getId()));
//            }
        } finally {
            ContextHolder.setContext(originalContext);
        }



        log.info("Populated stopPoints for stop area: " + stopArea.getId() + ". Points: " + containedStopPoints);
        stopArea.getContainedStopPoints().addAll(containedStopPoints);
    }


    private void init() {
        if (stopAreaDAO == null) {
            stopAreaDAO = CDI.current().select(StopAreaDAO.class).get();
            stopPointDAO = CDI.current().select(StopPointDAO.class).get();
        }
    }
}
