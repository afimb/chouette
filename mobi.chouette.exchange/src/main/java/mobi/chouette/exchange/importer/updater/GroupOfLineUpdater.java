package mobi.chouette.exchange.importer.updater;

import javax.ejb.Stateless;

import mobi.chouette.common.Context;
import mobi.chouette.model.GroupOfLine;

@Stateless(name = GroupOfLineUpdater.BEAN_NAME)
public class GroupOfLineUpdater implements Updater<GroupOfLine> {

	public static final String BEAN_NAME = "GroupOfLineUpdater";

	@Override
	public void update(Context context, GroupOfLine oldValue,
			GroupOfLine newValue) {

		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);

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
	}
}
