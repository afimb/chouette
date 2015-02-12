package mobi.chouette.exchange.importer.updater;

import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.CollectionUtils;
import mobi.chouette.common.Context;
import mobi.chouette.common.Pair;
import mobi.chouette.dao.AccessLinkDAO;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
@Stateless(name = AccessPointUpdater.BEAN_NAME)
public class AccessPointUpdater implements Updater<AccessPoint> {

	public static final String BEAN_NAME = "AccessPointUpdater";

	@EJB
	private AccessLinkDAO accessLinkDAO;

	@EJB(beanName = AccessLinkUpdater.BEAN_NAME)
	private Updater<AccessLink> accessLinkUpdater;

	@Override
	public void update(Context context, AccessPoint oldValue,
			AccessPoint newValue) throws Exception {

		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);

		Referential cache = (Referential) context.get(CACHE);

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

		if (newValue.getOpeningTime() != null
				&& !newValue.getOpeningTime().equals(oldValue.getOpeningTime())) {
			oldValue.setOpeningTime(newValue.getOpeningTime());
		}

		if (newValue.getClosingTime() != null
				&& !newValue.getClosingTime().equals(oldValue.getClosingTime())) {
			oldValue.setClosingTime(newValue.getClosingTime());
		}
		if (newValue.getType() != null
				&& !newValue.getType().equals(oldValue.getType())) {
			oldValue.setType(newValue.getType());
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

		// AccessLink
		Collection<AccessLink> addedAccessLink = CollectionUtils.substract(
				newValue.getAccessLinks(), oldValue.getAccessLinks(),
				NeptuneIdentifiedObjectComparator.INSTANCE);

		List<AccessLink> accessLinks = null;

		for (AccessLink item : addedAccessLink) {
			
			AccessLink accessLink = cache.getAccessLinks().get(
					item.getObjectId());
			if (accessLink == null) {
				if (accessLinks == null) {
					accessLinks = accessLinkDAO.load(addedAccessLink);
					for (AccessLink object : accessLinks) {
						cache.getAccessLinks().put(object.getObjectId(), object);
					}
				}
				accessLink = cache.getAccessLinks().get(item.getObjectId());
			}
			

			if (accessLink == null) {
				accessLink = ObjectFactory.getAccessLink(cache, item.getObjectId());
				// accessLink.setObjectId(item.getObjectId());
			}
			accessLink.setAccessPoint(oldValue);
		}

		// Updater<AccessLink> accessLinkUpdater = UpdaterFactory.create(
		// initialContext, AccessLinkUpdater.class.getName());
		Collection<Pair<AccessLink, AccessLink>> modifiedAccessLink = CollectionUtils
				.intersection(oldValue.getAccessLinks(),
						newValue.getAccessLinks(),
						NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<AccessLink, AccessLink> pair : modifiedAccessLink) {
			accessLinkUpdater.update(context, pair.getLeft(), pair.getRight());
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
