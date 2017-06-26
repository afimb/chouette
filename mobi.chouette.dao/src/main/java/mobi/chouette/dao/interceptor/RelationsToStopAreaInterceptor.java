package mobi.chouette.dao.interceptor;

import lombok.extern.log4j.Log4j;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.model.RouteSection;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.StopAreaImportModeEnum;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import javax.enterprise.inject.spi.CDI;

import java.io.Serializable;

/**
 * StopPoint and RouteSections reside in separate schemas from StopArea. This Interceptor enriches these entities with relations between them upon load.
 */
@Log4j
public class RelationsToStopAreaInterceptor extends EmptyInterceptor {

    private StopAreaDAO stopAreaDAO;

    private static final String STOP_POINT_CONTAINED_IN_STOP_AREA_ID_PROPERTY = "containedInStopAreaObjectId";
    private static final String DEPARTURE_STOP_AREA_ID_PROPERTY = "departureStopAreaObjectId";

    private static final String ARRIVAL_STOP_AREA_ID_PROPERTY = "arrivalStopAreaObjectId";

    @Override
    public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        init();

        if (entity instanceof StopPoint) {

            loadStopAreasForStopPoint((StopPoint) entity, state, propertyNames);

        } else if (entity instanceof RouteSection) {

            loadStopAreasForRouteSections((RouteSection) entity, state, propertyNames);
        }
        return super.onLoad(entity, id, state, propertyNames, types);
    }

    private void loadStopAreasForStopPoint(StopPoint entity, Object[] state, String[] propertyNames) {
        StopPoint stopPoint = entity;
        log.trace("On load StopPoint id: " + stopPoint.getId());
        String containedInStopAreaId = getProperty(STOP_POINT_CONTAINED_IN_STOP_AREA_ID_PROPERTY, propertyNames, state);

        if (stopPoint.getContainedInStopArea() == null && containedInStopAreaId != null) {
            stopPoint.setContainedInStopArea(stopAreaDAO.findByObjectId(containedInStopAreaId));
        }
    }

    private void loadStopAreasForRouteSections(RouteSection entity, Object[] state, String[] propertyNames) {
        RouteSection routeSection = entity;
        log.trace("On load RouteSection id: " + routeSection.getId());

        String arrivalStopAreaId = getProperty(ARRIVAL_STOP_AREA_ID_PROPERTY, propertyNames, state);
        if (entity.getArrival() == null && arrivalStopAreaId != null) {
            routeSection.setArrival(stopAreaDAO.findByObjectId(arrivalStopAreaId));
        }

        String departureStopAreaId = getProperty(DEPARTURE_STOP_AREA_ID_PROPERTY, propertyNames, state);
        if (entity.getDeparture() == null && departureStopAreaId != null) {
            routeSection.setDeparture(stopAreaDAO.findByObjectId(departureStopAreaId));
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

        if (entity instanceof StopPoint) {
            StopPoint stopPoint = ((StopPoint) entity);
            StopArea stopArea = stopPoint.getContainedInStopArea();
            if (stopArea != null) {
                if (stopArea.getId() == null && !stopArea.isDetached() && stopArea.getImportMode().shouldCreateMissingStopAreas()) {
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
