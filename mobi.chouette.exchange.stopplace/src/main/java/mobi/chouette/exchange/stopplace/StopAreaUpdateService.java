package mobi.chouette.exchange.stopplace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.dao.ReferentialDAO;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.dao.StopPointDAO;
import mobi.chouette.exchange.importer.updater.StopAreaUpdater;
import mobi.chouette.exchange.importer.updater.Updater;
import mobi.chouette.model.StopArea;
import mobi.chouette.persistence.hibernate.ContextHolder;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;

@Singleton(name = StopAreaUpdateService.BEAN_NAME)
@Log4j
public class StopAreaUpdateService {

    private static final int DELETE_UNUSED_BATCH_SIZE = 1000;

    public static final String BEAN_NAME = "StopAreaUpdateService";

    @EJB
    private StopAreaDAO stopAreaDAO;

    @EJB(beanName = StopAreaUpdater.BEAN_NAME)
    private Updater<StopArea> stopAreaUpdater;

    @EJB
    private ReferentialDAO referentialDAO;

    @EJB
    private StopPointDAO stopPointDAO;


    @TransactionAttribute
    public void createOrUpdateStopAreas(Context context, Set<StopArea> createdOrUpdatedStopAreas, Set<String> removedStopAreas) {

        Map<String, StopArea> removedQuays = new HashMap<>();

        removedStopAreas.stream().forEach(stopAreaId -> removeStopArea(stopAreaId, removedQuays));

        createdOrUpdatedStopAreas.forEach(sa -> createOrUpdate(context, sa, removedQuays));

        removedQuays.values().forEach(quay -> removeQuay(quay));
    }

    /**
     * Update stop area references in seperate transaction in order to iterate over all referentials
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateStopAreaReferences(Map<String, Set<String>> replacementMap) {
        replacementMap.forEach((newStopAreaId,oldStopAreaIds) -> stopPointDAO.replaceContainedInStopAreaReferences(oldStopAreaIds,newStopAreaId));
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

    @TransactionAttribute
    public void deleteUnusedStopAreas() {
        List<String> referentials = referentialDAO.getReferentials();
        Set<String> boardingPositionObjectIds = new HashSet<>(stopAreaDAO.getBoardingPositionObjectIds());

        log.debug("Total no of boarding positions: " + boardingPositionObjectIds.size());

        for (String referential : referentials) {
            ContextHolder.setContext(referential);
            List<String> inUseBoardingPositionsForReferential = stopPointDAO.getAllStopAreaObjectIds();
            boardingPositionObjectIds.removeAll(inUseBoardingPositionsForReferential);
            log.debug("Removed: " + inUseBoardingPositionsForReferential.size() + " in use boarding positions for referential: " +
                    referential + ". Potentially not used boarding positions left: " + boardingPositionObjectIds.size());
        }

        if (boardingPositionObjectIds.size() > 0) {
            log.info("Found " + boardingPositionObjectIds.size() + " unused boarding positions. Deleting stop areas where all quays are unused");

            if (boardingPositionObjectIds.size() > DELETE_UNUSED_BATCH_SIZE) {
                Lists.partition(new ArrayList<>(boardingPositionObjectIds), DELETE_UNUSED_BATCH_SIZE).forEach(batch -> deleteBatchOfUnusedStopAreas(batch));
            } else {
                deleteBatchOfUnusedStopAreas(boardingPositionObjectIds);
            }

        }
        log.info("Finished deleting unused stop areas");
    }

    private void deleteBatchOfUnusedStopAreas(Collection<String> unusedBoardingPositionObjectIds) {
        List<StopArea> unusedStopAreas = stopAreaDAO.findByObjectId(unusedBoardingPositionObjectIds).stream()
                .map(boardingPosition -> boardingPosition.getParent())
                .distinct()
                .filter(stop -> stop != null)
                .filter(stop -> stop.getContainedStopAreas().stream().allMatch(boardingPosition -> unusedBoardingPositionObjectIds.contains(boardingPosition.getObjectId())))
                .peek(stop -> log.debug("Deleting unused stop area: " + stop)).collect(Collectors.toList());


        unusedStopAreas.forEach(stop -> stop.getContainedStopAreas().forEach(boardingPosition -> stopAreaDAO.delete(boardingPosition)));
        unusedStopAreas.forEach(stop -> stopAreaDAO.delete(stop));

        log.info("Deleted " + unusedStopAreas.size() + " unused stop areas");
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
        try {
            StopArea existing = stopAreaDAO.findByObjectId(stopArea.getObjectId());
            if (existing == null) {
                createNewStopArea(context, stopArea, removedQuays);

            } else {
                updateExistingStopArea(context, stopArea, removedQuays, existing);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to update stop place: " + e.getMessage(), e);
        }

    }

    private void updateExistingStopArea(Context context, StopArea stopArea, Map<String, StopArea> removedQuays, StopArea existing) throws Exception {
        log.debug("Updating existing StopArea (StopPlace) : " + stopArea);

        Map<String, StopArea> existingQuays = existing.getContainedStopAreas().stream().collect(Collectors.toMap(StopArea::getObjectId,
                Function.identity()));

        stopAreaUpdater.update(context, existing, stopArea);
        existing.getContainedStopAreas().clear();
        for (StopArea quay : new ArrayList<>(stopArea.getContainedStopAreas())) {

            StopArea existingQuayForSameStopPlace = existingQuays.remove(quay.getObjectId());

            // Remove from removed collection to avoid moved quay being deleted
            removedQuays.remove(quay.getObjectId());

            if (existingQuayForSameStopPlace == null) {
                createOrMoveQuay(context, existing, quay);


            } else {
                log.debug("Updating existing StopArea (Quay) : " + stopArea);
                stopAreaDAO.update(existingQuayForSameStopPlace);
            }
        }

        for (StopArea obsoleteStopArea : existingQuays.values()) {
            removeQuay(obsoleteStopArea, removedQuays);
        }

        stopAreaDAO.update(existing);
    }

    private void createOrMoveQuay(Context context, StopArea parentStopPlace, StopArea quay) throws Exception {
        // Quay with ID does not already exist for this StopArea, but may exist for another. If so, move the existing quay.
        StopArea quayAlreadyExisting = stopAreaDAO.findByObjectId(quay.getObjectId());
        if (quayAlreadyExisting != null) {
            log.info("Moving StopArea (Quay) to new parent (StopPlace) : " + quay);
            quayAlreadyExisting.setDetached(true);
            stopAreaUpdater.update(context, quayAlreadyExisting, quay);
            stopAreaDAO.update(quayAlreadyExisting);
        } else {
            log.info("Creating new StopArea (Quay) : " + quay);
            quay.setParent(parentStopPlace);
            stopAreaDAO.create(quay);
        }
    }

    private void createNewStopArea(Context context, StopArea stopArea, Map<String, StopArea> removedQuays) throws Exception {
        log.debug("Creating new StopArea(StopPlace) : " + stopArea);

        List<StopArea> quays = new ArrayList<>();


        // Quays for new stop place might already exists and/or may be listed for removal because it has been removed from its previous owner
        if (!CollectionUtils.isEmpty(stopArea.getContainedStopAreas())) {
            quays.addAll(stopArea.getContainedStopAreas());
            stopArea.getContainedStopAreas().clear();
        }

        stopAreaDAO.create(stopArea);

        for (StopArea quay : quays) {
            // Remove from removed collection to avoid moved quay being deleted
            removedQuays.remove(quay.getObjectId());

            createOrMoveQuay(context, stopArea, quay);
        }
    }

    private void removeQuay(StopArea obsoleteStopArea, Map<String, StopArea> removedQuays) {
        StopArea oldParent = obsoleteStopArea.getParent();
        obsoleteStopArea.setParent(null);
        stopAreaDAO.update(oldParent);
        removedQuays.put(obsoleteStopArea.getObjectId(), obsoleteStopArea);
    }

}
