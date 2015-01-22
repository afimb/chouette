package mobi.chouette.importer.updater;

import mobi.chouette.model.PTNetwork;

public class PTNetworkUpdater implements Updater<PTNetwork> {
	@Override
	public void update(PTNetwork oldValue, PTNetwork newValue) {

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
		if (newValue.getVersionDate() != null
				&& newValue.getVersionDate().compareTo(
						oldValue.getVersionDate()) != 0) {
			oldValue.setVersionDate(newValue.getVersionDate());
		}
		if (newValue.getDescription() != null
				&& newValue.getDescription().compareTo(
						oldValue.getDescription()) != 0) {
			oldValue.setDescription(newValue.getDescription());
		}
		if (newValue.getRegistrationNumber() != null
				&& newValue.getRegistrationNumber().compareTo(
						oldValue.getRegistrationNumber()) != 0) {
			oldValue.setRegistrationNumber(newValue.getRegistrationNumber());
		}
		if (newValue.getSourceType() != null
				&& newValue.getSourceType().compareTo(oldValue.getSourceType()) != 0) {
			oldValue.setSourceType(newValue.getSourceType());
		}
		if (newValue.getSourceName() != null
				&& newValue.getSourceName().compareTo(oldValue.getSourceName()) != 0) {
			oldValue.setSourceName(newValue.getSourceName());
		}
		if (newValue.getSourceIdentifier() != null
				&& newValue.getSourceIdentifier().compareTo(
						oldValue.getSourceIdentifier()) != 0) {
			oldValue.setSourceIdentifier(newValue.getSourceIdentifier());
		}

		// TODO line list
	}

	static {
		UpdaterFactory.register(PTNetworkUpdater.class.getName(),
				new UpdaterFactory() {
					private PTNetworkUpdater INSTANCE = new PTNetworkUpdater();

					@Override
					protected Updater<PTNetwork> create() {
						return INSTANCE;
					}
				});
	}

}
