package mobi.chouette.exchange.importer.updater;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import mobi.chouette.common.Context;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Stateless(name = StopPointUpdater.BEAN_NAME)
public class StopPointUpdater implements Updater<StopPoint> {

	public static final String BEAN_NAME = "StopPointUpdater";

	@EJB 
	private StopAreaDAO stopAreaDAO;

	@EJB(beanName = StopAreaUpdater.BEAN_NAME)
	private Updater<StopArea> stopAreaUpdater;

	@Override
	public void update(Context context, StopPoint oldValue, StopPoint newValue)
			throws Exception {

		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);

		Referential cache = (Referential) context.get(CACHE);
		cache.getStopPoints().put(oldValue.getObjectId(), oldValue);

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

		// Boarding and alighting
		if (newValue.getForAlighting() != null
				&& !newValue.getForAlighting().equals(oldValue.getForAlighting())) {
			oldValue.setForAlighting(newValue.getForAlighting());
		}
		
		if (newValue.getForBoarding() != null
				&& !newValue.getForBoarding().equals(oldValue.getForBoarding())) {
			oldValue.setForBoarding(newValue.getForBoarding());
		}
		
		// StopArea
		if (newValue.getContainedInStopArea() == null) {
			oldValue.setContainedInStopArea(null);
		} else {

			String objectId = newValue.getContainedInStopArea().getObjectId();
			StopArea stopArea = cache.getStopAreas().get(objectId);
			if (stopArea == null) {
				stopArea = stopAreaDAO.findByObjectId(objectId);
				if (stopArea != null) {
					cache.getStopAreas().put(objectId, stopArea);
				}
			}

			if (stopArea == null) {
				stopArea = ObjectFactory.getStopArea(cache, objectId);
			}
			oldValue.setContainedInStopArea(stopArea);
			stopAreaUpdater.update(context, oldValue.getContainedInStopArea(),
					newValue.getContainedInStopArea());
		}

	}
}
