package mobi.chouette.importer.updater;

import mobi.chouette.model.GroupOfLine;

public class GroupOfLineUpdater implements Updater<GroupOfLine> {

	@Override
	public void update(GroupOfLine oldValue, GroupOfLine newValue) {

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
		
		// TODO line list
	}

	static {
		UpdaterFactory.register(GroupOfLineUpdater.class.getName(),
				new UpdaterFactory() {
					private GroupOfLineUpdater INSTANCE = new GroupOfLineUpdater();

					@Override
					protected Updater<GroupOfLine> create() {
						return INSTANCE;
					}
				});
	}
}
