package mobi.chouette.exchange.stopplace;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.dao.ReferentialDAO;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.dao.StopPointDAO;
import mobi.chouette.exchange.importer.updater.StopAreaUpdater;
import mobi.chouette.exchange.importer.updater.Updater;
import mobi.chouette.model.StopArea;
import mobi.chouette.persistence.hibernate.ContextHolder;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Singleton(name = StopAreaUpdateService.BEAN_NAME)
@Log4j
public class StopAreaUpdateService {

    public static final String BEAN_NAME = "StopAreaUpdateService";

    @EJB
    private StopAreaDAO stopAreaDAO;

    @EJB(beanName = StopAreaUpdater.BEAN_NAME)
    private Updater<StopArea> stopAreaUpdater;

    @EJB
    private ReferentialDAO referentialDAO;

    @EJB
    private StopPointDAO stopPointDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void createOrUpdateStopAreas(Context context, Set<StopArea> createdOrUpdatedStopAreas, Set<String> removedStopAreas, Map<String, String> mergedQuays) {

        Map<String, StopArea> removedQuays = new HashMap<>();

        removedStopAreas.stream().forEach(stopAreaId -> removeStopArea(stopAreaId, removedQuays));
        createdOrUpdatedStopAreas.forEach(sa -> createOrUpdate(context, sa, removedQuays));

        removedQuays.values().forEach(quay -> removeQuay(quay));
        mergedQuays.forEach((oldStopAreaId, newStopAreaId) -> updateStopAreaReference(oldStopAreaId, newStopAreaId));
    }

    @TransactionAttribute
    public void deleteStopArea(String objectId) {
        StopArea stopArea = stopAreaDAO.findByObjectId(objectId);
        if (stopArea != null) {
            cascadeDeleteStopArea(stopArea);
        } else {
            log.info("Ignored delete for unknown stop area: " + objectId);
        }
    }

    private void updateStopAreaReference(String oldStopAreaId, String newStopAreaId) {
        String orgContext = ContextHolder.getContext();
        try {
            for (String referential : referentialDAO.getReferentials()) {
                ContextHolder.setContext(referential);
                stopPointDAO.replaceContainedInStopAreaReference(oldStopAreaId, newStopAreaId);
            }
        } finally {
            ContextHolder.setContext(orgContext); // reset context
        }
    }

    private void cascadeDeleteStopArea(StopArea stopArea) {
        stopArea.getContainedStopAreas().forEach(child -> cascadeDeleteStopArea(child));
        stopAreaDAO.delete(stopArea);
        log.info("Deleted stop area: " + stopArea.getObjectId());
    }

    private void removeStopArea(String objectId, Map<String, StopArea> removedQuays) {
        log.info("Deleting obsolete StopArea (StopPlace) : " + objectId);

        StopArea stopArea = stopAreaDAO.findByObjectId(objectId);
        if (stopArea != null) {
            new ArrayList<>(stopArea.getContainedStopAreas()).forEach(quay -> removeQuay(quay, removedQuays));
            stopAreaDAO.delete(stopArea);
        } else {
            log.warn("Could not remove unknown stopArea: " + objectId);
        }

    }

    private void removeQuay(StopArea quay) {
        log.info("Deleting obsolete StopArea (Quay): " + quay.getObjectId());
        stopAreaDAO.delete(quay);
    }

    private void createOrUpdate(Context context, StopArea stopArea, Map<String, StopArea> removedQuays) {
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

                    // Remove from removed collection to avoid moved quay being deleted
                    removedQuays.remove(quay.getObjectId());

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
                    removeQuay(obsoleteStopArea, removedQuays);
                }

                stopAreaDAO.update(existing);
            } catch (Exception e) {
                throw new RuntimeException("Failed to update stop place: " + e.getMessage(), e);
            }
        }
    }

    private void removeQuay(StopArea obsoleteStopArea, Map<String, StopArea> removedQuays) {
        StopArea oldParent = obsoleteStopArea.getParent();
        obsoleteStopArea.setParent(null);
        stopAreaDAO.update(oldParent);
        removedQuays.put(obsoleteStopArea.getObjectId(), obsoleteStopArea);
    }

}
