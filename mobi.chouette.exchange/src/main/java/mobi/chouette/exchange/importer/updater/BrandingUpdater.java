package mobi.chouette.exchange.importer.updater;

import javax.ejb.Stateless;

import mobi.chouette.common.Context;
import mobi.chouette.model.Branding;

@Stateless(name = BrandingUpdater.BEAN_NAME)
public class BrandingUpdater implements Updater<Branding> {

	public static final String BEAN_NAME = "BrandingUpdater";

	@Override
	public void update(Context context, Branding oldValue, Branding newValue) {

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

		if (newValue.getDescription() != null
				&& !newValue.getDescription().equals(oldValue.getDescription())) {
			oldValue.setDescription(newValue.getDescription());
		}

		if (newValue.getUrl() != null
				&& !newValue.getUrl().equals(oldValue.getUrl())) {
			oldValue.setUrl(newValue.getUrl());
		}

		if (newValue.getImage() != null
				&& !newValue.getImage().equals(oldValue.getImage())) {
			oldValue.setImage(newValue.getImage());
		}
	}
}
