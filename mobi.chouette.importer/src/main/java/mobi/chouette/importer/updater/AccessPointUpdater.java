package mobi.chouette.importer.updater;

import java.util.Collection;

import javax.ejb.EJB;

import mobi.chouette.common.CollectionUtils;
import mobi.chouette.common.Context;
import mobi.chouette.common.Pair;
import mobi.chouette.dao.AccessLinkDAO;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;

public class AccessPointUpdater implements Updater<AccessPoint> {

	@EJB
	private AccessLinkDAO accessLinkDAO;

	@Override
	public void update(Context context, AccessPoint oldValue,
			AccessPoint newValue) throws Exception {

		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);

		if (newValue.getObjectId() != null
				&& newValue.getObjectId().compareTo(oldValue.getObjectId()) != 0) {
			oldValue.setObjectId(newValue.getObjectId());
		}
		if (newValue.getObjectVersion() != null
				&& newValue.getObjectVersion().compareTo(
						oldValue.getObjectVersion()) != 0) {
			oldValue.setObjectVersion(newValue.getObjectVersion());
		}
		if (newValue.getCreationTime() != null
				&& newValue.getCreationTime().compareTo(
						oldValue.getCreationTime()) != 0) {
			oldValue.setCreationTime(newValue.getCreationTime());
		}
		if (newValue.getCreatorId() != null
				&& newValue.getCreatorId().compareTo(oldValue.getCreatorId()) != 0) {
			oldValue.setCreatorId(newValue.getCreatorId());
		}
		if (newValue.getName() != null
				&& newValue.getName().compareTo(oldValue.getName()) != 0) {
			oldValue.setName(newValue.getName());
		}
		if (newValue.getComment() != null
				&& newValue.getComment().compareTo(oldValue.getComment()) != 0) {
			oldValue.setComment(newValue.getComment());
		}

		if (newValue.getOpeningTime() != null
				&& newValue.getOpeningTime().compareTo(
						oldValue.getOpeningTime()) != 0) {
			oldValue.setOpeningTime(newValue.getOpeningTime());
		}

		if (newValue.getClosingTime() != null
				&& newValue.getClosingTime().compareTo(
						oldValue.getClosingTime()) != 0) {
			oldValue.setClosingTime(newValue.getClosingTime());
		}
		if (newValue.getType() != null
				&& newValue.getType().compareTo(oldValue.getType()) != 0) {
			oldValue.setType(newValue.getType());
		}

		if (newValue.getLiftAvailable() != null
				&& newValue.getLiftAvailable().compareTo(
						oldValue.getLiftAvailable()) != 0) {
			oldValue.setLiftAvailable(newValue.getLiftAvailable());
		}
		if (newValue.getMobilityRestrictedSuitable() != null
				&& newValue.getMobilityRestrictedSuitable().compareTo(
						oldValue.getMobilityRestrictedSuitable()) != 0) {
			oldValue.setMobilityRestrictedSuitable(newValue
					.getMobilityRestrictedSuitable());
		}
		if (newValue.getStairsAvailable() != null
				&& newValue.getStairsAvailable().compareTo(
						oldValue.getStairsAvailable()) != 0) {
			oldValue.setStairsAvailable(newValue.getStairsAvailable());
		}

		// AccessLink
		Collection<AccessLink> addedAccessLink = CollectionUtils.substract(
				newValue.getAccessLinks(), oldValue.getAccessLinks(),
				NeptuneIdentifiedObjectComparator.INSTANCE);
		for (AccessLink item : addedAccessLink) {
			AccessLink accessLink = accessLinkDAO.findByObjectId(item
					.getObjectId());
			if (accessLink == null) {
				accessLink = new AccessLink();
				accessLink.setObjectId(item.getObjectId());
				accessLinkDAO.create(accessLink);
			}
			accessLink.setAccessPoint(oldValue);
		}

		Updater<AccessLink> accessLinkUpdater = UpdaterFactory
				.create(AccessLinkUpdater.class.getName());
		Collection<Pair<AccessLink, AccessLink>> modifiedAccessLink = CollectionUtils
				.intersection(oldValue.getAccessLinks(),
						newValue.getAccessLinks(),
						NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<AccessLink, AccessLink> pair : modifiedAccessLink) {
			accessLinkUpdater.update(null, pair.getLeft(), pair.getRight());
		}

		// Collection<AccessLink> removedAccessLink = CollectionUtils.substract(
		// oldValue.getAccessLinks(), newValue.getAccessLinks(),
		// NeptuneIdentifiedObjectComparator.INSTANCE);
		// for (AccessLink accessLink : removedAccessLink) {
		// accessLink.setAccessPoint(null);
		// accessLinkDAO.delete(accessLink);
		// }

	}

	static {
		UpdaterFactory.register(AccessPointUpdater.class.getName(),
				new UpdaterFactory() {
					private AccessPointUpdater INSTANCE = new AccessPointUpdater();

					@Override
					protected Updater<AccessPoint> create() {
						return INSTANCE;
					}
				});
	}
}
