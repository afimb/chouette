package mobi.chouette.exchange.stopplace;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.dao.StopPointDAO;
import mobi.chouette.dao.iev.ReferentialDAO;
import mobi.chouette.exchange.importer.updater.StopAreaUpdater;
import mobi.chouette.exchange.importer.updater.Updater;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.persistence.hibernate.ContextHolder;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Singleton(name = StopAreaUpdateService.BEAN_NAME)
@Log4j
public class StopAreaUpdateService {

    public static final String BEAN_NAME = "StopAreaUpdateService";

    @EJB
    private StopAreaDAO stopAreaDAO;

    @EJB
    private StopPointDAO stopPointDAO;

    @EJB
    private ReferentialDAO referentialDao;

    @EJB(beanName = StopAreaUpdater.BEAN_NAME)
    private Updater<StopArea> stopAreaUpdater;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void createOrUpdateStopAreas(Context context, Set<StopArea> createdOrUpdatedStopAreas, Set<String> removedStopAreas) {

        removedStopAreas.stream().forEach(stopAreaId -> removeStopArea(stopAreaId));
        createdOrUpdatedStopAreas.forEach(sa -> createOrUpdate(context, sa));
    }

    private void removeStopArea(String objectId) {
        log.info("Deleting obsolete StopArea (StopPlace) : " + objectId);

        StopArea stopArea = stopAreaDAO.findByObjectId(objectId);
        if (stopArea != null) {
            new ArrayList<>(stopArea.getContainedStopAreas()).forEach(quay -> removeQuay(quay));
            stopAreaDAO.delete(stopArea);
        } else {
            log.warn("Could not remove unknown stopArea: " + objectId);
        }

    }

    private void createOrUpdate(Context context, StopArea stopArea) {
        StopArea existing = stopAreaDAO.findByObjectId(stopArea.getObjectId());
        if (existing == null) {
            log.debug("Creating new StopArea(StopPlace) : " + stopArea);
            stopAreaDAO.create(stopArea);
        } else {
            log.debug("Updating existing StopArea (StopPlace) : " + stopArea);

            try {
                Map<String, StopArea> existingQuays = existing.getContainedStopAreas().stream().collect(Collectors.toMap(StopArea::getObjectId,
                        Function.identity()));

                stopAreaUpdater.update(context, existing, stopArea);
                existing.getContainedStopAreas().clear();
                for (StopArea quay : new ArrayList<>(stopArea.getContainedStopAreas())) {

                    StopArea existingQuayForSameStopPlace = existingQuays.remove(quay.getObjectId());


                    if (existingQuayForSameStopPlace == null) {

                        // Quay with ID does not already exist for this StopArea, but may exist for another. If so, remove the existing quay.
                        StopArea quayAlreadyExisting = stopAreaDAO.findByObjectId(quay.getObjectId());
                        if (quayAlreadyExisting != null) {
                            log.info("Moving StopArea (Quay) to new parent (StopPlace) : " + quay);
                            quayAlreadyExisting.setDetached(true);
                            stopAreaUpdater.update(context, quayAlreadyExisting, quay);
                            stopAreaDAO.update(quayAlreadyExisting);
                        } else {
                            log.info("Creating new StopArea (Quay) : " + quay);
                            quay.setParent(existing);
                            stopAreaDAO.create(quay);
                        }
                    } else {
                        log.debug("Updating existing StopArea (Quay) : " + stopArea);
                        stopAreaDAO.update(existingQuayForSameStopPlace);
                    }
                }

                for (StopArea obsoleteStopArea : existingQuays.values()) {
                    removeQuay(obsoleteStopArea);
                }

                stopAreaDAO.update(existing);
            } catch (Exception e) {
                throw new RuntimeException("Failed to update stop place: " + e.getMessage(), e);
            }
        }
    }

    private void removeQuay(StopArea obsoleteStopArea) {
        log.info("Deleting obsolete StopArea (Quay): " + obsoleteStopArea.getObjectId());

        try {
            for (String referential : referentialDao.getReferentials()) {
                ContextHolder.setContext(referential);

                List<StopPoint> containedStopPoints = stopPointDAO.getStopPointsContainedInStopArea(obsoleteStopArea.getId());
                if (containedStopPoints.size() > 0) {
                    log.info("Clearing references for " + containedStopPoints.size() + " stop points referring to obsolete StopArea (Quay): " + obsoleteStopArea.getObjectId());
                    containedStopPoints.forEach((stopPoint -> removeStopAreaReferenceFromStopPoint(stopPoint)));
                }
            }
        } finally {
            ContextHolder.setContext(null);
        }

        StopArea oldParent=obsoleteStopArea.getParent();
        obsoleteStopArea.setParent(null);
        stopAreaDAO.update(oldParent);
        stopAreaDAO.delete(obsoleteStopArea);
    }


    private void removeStopAreaReferenceFromStopPoint(StopPoint stopPoint) {
        stopPoint.setContainedInStopArea(null);
        stopPointDAO.update(stopPoint);
    }

}
