package mobi.chouette.exchange.stopplace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.exchange.importer.updater.Updater;
import mobi.chouette.model.StopArea;

import org.apache.commons.collections.CollectionUtils;

@Log4j
public class StopAreaUpdateTask {

	private StopAreaDAO stopAreaDAO;

	private Updater<StopArea> stopAreaUpdater;

	private Context context;

	private StopAreaUpdateContext updateContext;


	private Map<String, StopArea> removedContainedStopAreas = new HashMap<>();

	public StopAreaUpdateTask(StopAreaDAO stopAreaDAO, Updater<StopArea> stopAreaUpdater, Context context, StopAreaUpdateContext updateContext) {
		this.stopAreaDAO = stopAreaDAO;
		this.stopAreaUpdater = stopAreaUpdater;
		this.context = context;
		this.updateContext = updateContext;
	}

	public void update() {
		updateContext.getInactiveStopAreaIds().stream().forEach(stopAreaId -> removeStopArea(stopAreaId));

		updateContext.getActiveStopAreas().forEach(sa -> createOrUpdate(sa));

		removedContainedStopAreas.values().forEach(containedStopArea -> removeContainedStopArea(containedStopArea));
	}


	private void createOrUpdate(StopArea stopArea) {
		try {
			StopArea existing = stopAreaDAO.findByObjectId(stopArea.getObjectId());
			if (existing == null) {
				createNewStopArea(stopArea);

			} else {
				updateExistingStopArea(stopArea, existing);
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to update stop place: " + e.getMessage(), e);
		}

	}

	private void updateExistingStopArea(StopArea stopArea, StopArea existing) throws Exception {
		log.debug("Updating existing StopArea : " + stopArea);

		Map<String, StopArea> existingContainedStopAreas = existing.getContainedStopAreas().stream().collect(Collectors.toMap(StopArea::getObjectId,
				Function.identity()));

		stopAreaUpdater.update(context, existing, stopArea);
		existing.getContainedStopAreas().clear();
		for (StopArea containedStopArea : new ArrayList<>(stopArea.getContainedStopAreas())) {

			StopArea existingContainedStopAreaForSameParent = existingContainedStopAreas.remove(containedStopArea.getObjectId());

			// Remove from removed collection to avoid moved contained stop area being deleted
			removedContainedStopAreas.remove(containedStopArea.getObjectId());

			if (existingContainedStopAreaForSameParent == null) {
				createOrMoveStopArea(existing, containedStopArea);

			} else {
				log.debug("Updating existing contained StopArea : " + stopArea);
				stopAreaUpdater.update(context, existingContainedStopAreaForSameParent, containedStopArea);
				stopAreaDAO.update(existingContainedStopAreaForSameParent);
			}
		}

		for (StopArea obsoleteStopArea : existingContainedStopAreas.values()) {
			registerRemovedContainedStopArea(obsoleteStopArea);
		}

		stopAreaDAO.update(existing);
	}

	private void createOrMoveStopArea(StopArea parent, StopArea stopArea) throws Exception {
		// Contained stop area with ID does not already exist for parent StopArea, but may exist for another. If so, move the existing contained stop area to new parent.
		StopArea existing = stopAreaDAO.findByObjectId(stopArea.getObjectId());
		if (existing != null) {
			log.info("Moving contained StopArea: " + stopArea + " to new parent : " + parent);
			existing.setDetached(true);
			updateExistingStopArea(stopArea, existing);
		} else {
			log.debug("Creating new contained StopArea: " + stopArea);
			stopArea.setParent(parent);
			createNewStopArea(stopArea);
		}
	}

	private void createNewStopArea(StopArea stopArea) throws Exception {
		log.debug("Creating new StopArea : " + stopArea);

		List<StopArea> containedStopAreas = new ArrayList<>();

		// Contained stops for new stop place might already exists and/or may be listed for removal because it has been removed from its previous owner
		if (!CollectionUtils.isEmpty(stopArea.getContainedStopAreas())) {
			containedStopAreas.addAll(stopArea.getContainedStopAreas());
			stopArea.getContainedStopAreas().clear();
		}

		stopAreaDAO.create(stopArea);

		for (StopArea containedStopArea : containedStopAreas) {
			// Remove from removed collection to avoid moved contained stop area being deleted
			removedContainedStopAreas.remove(containedStopArea.getObjectId());

			createOrMoveStopArea(stopArea, containedStopArea);
		}
	}

	private void removeStopArea(String objectId) {
		log.info("Deleting obsolete StopArea : " + objectId);

		StopArea stopArea = stopAreaDAO.findByObjectId(objectId);
		if (stopArea != null) {
			new ArrayList<>(stopArea.getContainedStopAreas()).forEach(containedStopArea -> registerRemovedContainedStopArea(containedStopArea));
			stopAreaDAO.delete(stopArea);
		} else {
			log.warn("Could not remove unknown stopArea: " + objectId);
		}

	}

	private void removeContainedStopArea(StopArea containedStopArea) {
		log.info("Deleting obsolete contained StopArea: " + containedStopArea.getObjectId());
		stopAreaDAO.delete(containedStopArea);
		if (containedStopArea.getContainedStopAreas() != null) {
			containedStopArea.getContainedStopAreas().forEach(grandChild -> removeContainedStopArea(grandChild));
		}
	}


	private void registerRemovedContainedStopArea(StopArea obsoleteStopArea) {
		StopArea oldParent = obsoleteStopArea.getParent();
		obsoleteStopArea.setParent(null);
		stopAreaDAO.update(oldParent);
		removedContainedStopAreas.put(obsoleteStopArea.getObjectId(), obsoleteStopArea);
	}

}
