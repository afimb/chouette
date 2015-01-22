package mobi.chouette.importer.updater;

import javax.ejb.EJB;

import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.StopArea;

public class ConnectionLinkUpdater implements Updater<ConnectionLink> {

	/*
	if (newValue.get != null
			&& newValue.get.compareTo(oldValue.get) != 0) {
		oldValue.set(newValue.get);
	}
	*/
	@EJB
	private StopAreaDAO stopAreaDAO;

	@Override
	public void update(ConnectionLink oldValue, ConnectionLink newValue) {
		
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
		if (newValue.getLinkDistance() != null
				&& newValue.getLinkDistance().compareTo(
						oldValue.getLinkDistance()) != 0) {
			oldValue.setLinkDistance(newValue.getLinkDistance());
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
		if (newValue.getDefaultDuration() != null
				&& newValue.getDefaultDuration().compareTo(
						oldValue.getDefaultDuration()) != 0) {
			oldValue.setDefaultDuration(newValue.getDefaultDuration());
		}
		if (newValue.getFrequentTravellerDuration() != null
				&& newValue.getFrequentTravellerDuration().compareTo(
						oldValue.getFrequentTravellerDuration()) != 0) {
			oldValue.setFrequentTravellerDuration(newValue
					.getFrequentTravellerDuration());
		}
		if (newValue.getOccasionalTravellerDuration() != null
				&& newValue.getOccasionalTravellerDuration().compareTo(
						oldValue.getOccasionalTravellerDuration()) != 0) {
			oldValue.setOccasionalTravellerDuration(newValue
					.getOccasionalTravellerDuration());
		}
		if (newValue.getMobilityRestrictedTravellerDuration() != null
				&& newValue.getMobilityRestrictedTravellerDuration().compareTo(
						oldValue.getMobilityRestrictedTravellerDuration()) != 0) {
			oldValue.setMobilityRestrictedTravellerDuration(newValue
					.getMobilityRestrictedTravellerDuration());
		}
		if (newValue.getLinkType() != null
				&& newValue.getLinkType().compareTo(oldValue.getLinkType()) != 0) {
			oldValue.setLinkType(newValue.getLinkType());
		}
		if (newValue.getIntUserNeeds() != null
				&& newValue.getIntUserNeeds().compareTo(
						oldValue.getIntUserNeeds()) != 0) {
			oldValue.setIntUserNeeds(newValue.getIntUserNeeds());
		}

		if (newValue.getStartOfLink() != null) {
			StopArea startOfLink = stopAreaDAO.findByObjectId(newValue.getStartOfLink()
					.getObjectId());
			if (startOfLink != null) {
				oldValue.setStartOfLink(startOfLink);
			}
		}

		if (newValue.getEndOfLink() != null) {
			StopArea endOfLink = stopAreaDAO.findByObjectId(newValue.getEndOfLink()
					.getObjectId());
			if (endOfLink != null) {
				oldValue.setEndOfLink(endOfLink);
			}
		}
	}

	static {
		UpdaterFactory.register(ConnectionLinkUpdater.class.getName(),
				new UpdaterFactory() {
					private ConnectionLinkUpdater INSTANCE = new ConnectionLinkUpdater();

					@Override
					protected Updater<ConnectionLink> create() {
						return INSTANCE;
					}
				});
	}

}
