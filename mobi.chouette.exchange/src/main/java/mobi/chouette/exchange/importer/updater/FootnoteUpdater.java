package mobi.chouette.exchange.importer.updater;

import javax.ejb.Stateless;

import mobi.chouette.common.Context;
import mobi.chouette.model.Footnote;

@Stateless(name = FootnoteUpdater.BEAN_NAME)
public class FootnoteUpdater implements Updater<Footnote> {

	public static final String BEAN_NAME = "FootnoteUpdater";

	@Override
	public void update(Context context, Footnote oldValue, Footnote newValue) {

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
		if (newValue.getCode() != null
				&& !newValue.getCode().equals(oldValue.getCode())) {
			oldValue.setCode(newValue.getCode());
		}

		if (newValue.getKey() != null
				&& !newValue.getKey().equals(oldValue.getKey())) {
			oldValue.setKey(newValue.getKey());
		}

		if (newValue.getLabel() != null
				&& !newValue.getLabel().equals(oldValue.getLabel())) {
			oldValue.setLabel(newValue.getLabel());
		}

	}

}
