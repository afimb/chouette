package mobi.chouette.importer.updater;

import mobi.chouette.model.StopArea;

public class StopAreaUpdater implements Updater<StopArea> {
	@Override
	public void update(StopArea oldValue, StopArea newValue) {
		// if new object unknown as ConnectionLink target, ignore update
		if (newValue.getAreaType() == null)
			return;

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
		if (newValue.getAreaType() != null
				&& newValue.getAreaType().compareTo(oldValue.getAreaType()) != 0) {
			oldValue.setAreaType(newValue.getAreaType());
		}
		if (newValue.getRegistrationNumber() != null
				&& newValue.getRegistrationNumber().compareTo(
						oldValue.getRegistrationNumber()) != 0) {
			oldValue.setRegistrationNumber(newValue.getRegistrationNumber());
		}
		if (newValue.getNearestTopicName() != null
				&& newValue.getNearestTopicName().compareTo(
						oldValue.getNearestTopicName()) != 0) {
			oldValue.setNearestTopicName(newValue.getNearestTopicName());
		}
		if (newValue.getFareCode() != null
				&& newValue.getFareCode().compareTo(oldValue.getFareCode()) != 0) {
			oldValue.setFareCode(newValue.getFareCode());
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
		if (newValue.getLongitude() != null
				&& newValue.getLongitude().compareTo(oldValue.getLongitude()) != 0) {
			oldValue.setLongitude(newValue.getLongitude());
		}
		if (newValue.getLatitude() != null
				&& newValue.getLatitude().compareTo(oldValue.getLatitude()) != 0) {
			oldValue.setLatitude(newValue.getLatitude());
		}
		if (newValue.getLongLatType() != null
				&& newValue.getLongLatType().compareTo(
						oldValue.getLongLatType()) != 0) {
			oldValue.setLongLatType(newValue.getLongLatType());
		}
		if (newValue.getX() != null
				&& newValue.getX().compareTo(oldValue.getX()) != 0) {
			oldValue.setX(newValue.getX());
		}
		if (newValue.getY() != null
				&& newValue.getY().compareTo(oldValue.getY()) != 0) {
			oldValue.setY(newValue.getY());
		}
		if (newValue.getProjectionType() != null
				&& newValue.getProjectionType().compareTo(
						oldValue.getProjectionType()) != 0) {
			oldValue.setProjectionType(newValue.getProjectionType());
		}
		if (newValue.getCountryCode() != null
				&& newValue.getCountryCode().compareTo(
						oldValue.getCountryCode()) != 0) {
			oldValue.setCountryCode(newValue.getCountryCode());
		}
		if (newValue.getStreetName() != null
				&& newValue.getStreetName().compareTo(oldValue.getStreetName()) != 0) {
			oldValue.setStreetName(newValue.getStreetName());
		}
		if (newValue.getUrl() != null
				&& newValue.getUrl().compareTo(oldValue.getUrl()) != 0) {
			oldValue.setUrl(newValue.getUrl());
		}
		if (newValue.getTimeZone() != null
				&& newValue.getTimeZone().compareTo(oldValue.getTimeZone()) != 0) {
			oldValue.setTimeZone(newValue.getTimeZone());
		}
		if (newValue.getIntUserNeeds() != null
				&& newValue.getIntUserNeeds().compareTo(
						oldValue.getIntUserNeeds()) != 0) {
			oldValue.setIntUserNeeds(newValue.getIntUserNeeds());
		}

	}

	static {
		UpdaterFactory.register(StopAreaUpdater.class.getName(),
				new UpdaterFactory() {
					private StopAreaUpdater INSTANCE = new StopAreaUpdater();

					@Override
					protected Updater<StopArea> create() {
						return INSTANCE;
					}
				});
	}

}
