package mobi.chouette.exchange.importer.updater;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.model.PTNetwork;

@Log4j
@Stateless(name = PTNetworkUpdater.BEAN_NAME)
public class PTNetworkUpdater implements Updater<PTNetwork> {

	public static final String BEAN_NAME = "PTNetworkUpdater";

	@Override
	public void update(Context context, PTNetwork oldValue, PTNetwork newValue) {

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
		if (newValue.getVersionDate() != null
				&& !newValue.getVersionDate().equals(oldValue.getVersionDate())) {
			oldValue.setVersionDate(newValue.getVersionDate());
		}
		if (newValue.getDescription() != null
				&& !newValue.getDescription().equals(oldValue.getDescription())) {
			oldValue.setDescription(newValue.getDescription());
		}
		if (newValue.getRegistrationNumber() != null
				&& !newValue.getRegistrationNumber().equals(
						oldValue.getRegistrationNumber())) {
			oldValue.setRegistrationNumber(newValue.getRegistrationNumber());
		}
		if (newValue.getSourceType() != null
				&& !newValue.getSourceType().equals(oldValue.getSourceType())) {
			oldValue.setSourceType(newValue.getSourceType());
		}
		if (newValue.getSourceName() != null
				&& !newValue.getSourceName().equals(oldValue.getSourceName())) {
			oldValue.setSourceName(newValue.getSourceName());
		}
		if (newValue.getSourceIdentifier() != null
				&& !newValue.getSourceIdentifier().equals(
						oldValue.getSourceIdentifier())) {
			oldValue.setSourceIdentifier(newValue.getSourceIdentifier());
		}
	}

	static {
		UpdaterFactory.register(PTNetworkUpdater.class.getName(),
				new UpdaterFactory() {

					@Override
					protected <T> Updater<T> create(InitialContext context) {
						Updater result = null;
						try {
							result = (Updater) context
									.lookup("java:app/mobi.chouette.exchange/"
											+ BEAN_NAME);
						} catch (NamingException e) {
							log.error(e);
						}
						return result;
					}
				});
	}

}
