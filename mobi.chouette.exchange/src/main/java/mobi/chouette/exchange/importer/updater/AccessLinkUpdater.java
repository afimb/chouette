package mobi.chouette.exchange.importer.updater;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import mobi.chouette.common.Context;
import mobi.chouette.dao.AccessPointDAO;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.util.NamingUtil;
import mobi.chouette.model.util.Referential;

@Stateless(name = AccessLinkUpdater.BEAN_NAME)
public class AccessLinkUpdater implements Updater<AccessLink> {

	public static final String BEAN_NAME = "AccessLinkUpdater";

	@EJB 
	private AccessPointDAO accessPointDAO;

	@Override
	public void update(Context context, AccessLink oldValue, AccessLink newValue) {

		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);

		Referential cache = (Referential) context.get(CACHE);

		if (newValue.getName() == null) {
			NamingUtil.setDefaultName(newValue);
		}

		if (newValue.getObjectId() != null
				&& !newValue.getObjectId().equals(oldValue.getObjectId())) {
			oldValue.setObjectId(newValue.getObjectId());
		}
		if (newValue.getObjectVersion() != null
				&& !newValue.getObjectVersion().equals(
						oldValue.getObjectVersion())) {
			oldValue.setObjectVersion(newValue.getObjectVersion());
		}
		if (newValue.getCreationTime() != null
				&& !newValue.getCreationTime().equals(
						oldValue.getCreationTime())) {
			oldValue.setCreationTime(newValue.getCreationTime());
		}
		if (newValue.getCreatorId() != null
				&& !newValue.getCreatorId().equals(oldValue.getCreatorId())) {
			oldValue.setCreatorId(newValue.getCreatorId());
		}
		if (newValue.getName() != null
				&& !newValue.getName().equals(oldValue.getName())) {
			oldValue.setName(newValue.getName());
		}
		if (newValue.getComment() != null
				&& !newValue.getComment().equals(oldValue.getComment())) {
			oldValue.setComment(newValue.getComment());
		}
		if (newValue.getLinkDistance() != null
				&& !newValue.getLinkDistance().equals(
						oldValue.getLinkDistance())) {
			oldValue.setLinkDistance(newValue.getLinkDistance());
		}
		if (newValue.getLiftAvailable() != null
				&& !newValue.getLiftAvailable().equals(
						oldValue.getLiftAvailable())) {
			oldValue.setLiftAvailable(newValue.getLiftAvailable());
		}
		if (newValue.getMobilityRestrictedSuitable() != null
				&& !newValue.getMobilityRestrictedSuitable().equals(
						oldValue.getMobilityRestrictedSuitable())) {
			oldValue.setMobilityRestrictedSuitable(newValue
					.getMobilityRestrictedSuitable());
		}
		if (newValue.getStairsAvailable() != null
				&& !newValue.getStairsAvailable().equals(
						oldValue.getStairsAvailable())) {
			oldValue.setStairsAvailable(newValue.getStairsAvailable());
		}
		if (newValue.getDefaultDuration() != null
				&& !newValue.getDefaultDuration().equals(
						oldValue.getDefaultDuration())) {
			oldValue.setDefaultDuration(newValue.getDefaultDuration());
		}
		if (newValue.getFrequentTravellerDuration() != null
				&& !newValue.getFrequentTravellerDuration().equals(
						oldValue.getFrequentTravellerDuration())) {
			oldValue.setFrequentTravellerDuration(newValue
					.getFrequentTravellerDuration());
		}
		if (newValue.getOccasionalTravellerDuration() != null
				&& !newValue.getOccasionalTravellerDuration().equals(
						oldValue.getOccasionalTravellerDuration())) {
			oldValue.setOccasionalTravellerDuration(newValue
					.getOccasionalTravellerDuration());
		}
		if (newValue.getMobilityRestrictedTravellerDuration() != null
				&& !newValue.getMobilityRestrictedTravellerDuration().equals(
						oldValue.getMobilityRestrictedTravellerDuration())) {
			oldValue.setMobilityRestrictedTravellerDuration(newValue
					.getMobilityRestrictedTravellerDuration());
		}
		if (newValue.getLinkType() != null
				&& !newValue.getLinkType().equals(oldValue.getLinkType())) {
			oldValue.setLinkType(newValue.getLinkType());
		}
		if (newValue.getIntUserNeeds() != null
				&& !newValue.getIntUserNeeds().equals(
						oldValue.getIntUserNeeds())) {
			oldValue.setIntUserNeeds(newValue.getIntUserNeeds());
		}
		if (newValue.getLinkOrientation() != null
				&& !newValue.getLinkOrientation().equals(
						oldValue.getLinkOrientation())) {
			oldValue.setLinkOrientation(newValue.getLinkOrientation());
		}

		// AccessPoint
		if (oldValue.getAccessPoint() == null) {
			String objectId = newValue.getAccessPoint().getObjectId();
			AccessPoint accessPoint = cache.getAccessPoints().get(objectId);
			if (accessPoint == null) {
				accessPoint = accessPointDAO.findByObjectId(objectId);
				if (accessPoint != null) {
					cache.getAccessPoints().put(objectId, accessPoint);
				}
			}

			if (accessPoint != null) {
				oldValue.setAccessPoint(accessPoint);
			}
		}

	}

}
