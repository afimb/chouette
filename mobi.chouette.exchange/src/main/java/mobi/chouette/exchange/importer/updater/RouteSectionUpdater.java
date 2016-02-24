package mobi.chouette.exchange.importer.updater;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import mobi.chouette.common.Context;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.model.RouteSection;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.util.Referential;

@Stateless(name = RouteSectionUpdater.BEAN_NAME)
public class RouteSectionUpdater implements Updater<RouteSection> {

	public static final String BEAN_NAME = "RouteSectionUpdater";

	@EJB 
	private StopAreaDAO stopAreaDAO;

	@Override
	public void update(Context context, RouteSection oldValue, RouteSection newValue) throws Exception {
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
		if (newValue.getDistance() != null
				&& !newValue.getDistance().equals(oldValue.getDistance())) {
			oldValue.setDistance(newValue.getDistance());
		}
		if (newValue.getDeparture() != null
				&& !newValue.getDeparture().equals(oldValue.getDeparture())) {
			String objectId = newValue.getDeparture().getObjectId();
			StopArea departure = cache.getStopAreas().get(objectId);
			if (departure == null) {
				departure = stopAreaDAO.findByObjectId(objectId);
				if (departure != null) {
					cache.getStopAreas().put(objectId, departure);
				}
			}

			if (departure != null) {
				oldValue.setDeparture(departure);
			}
		}
		if (newValue.getArrival() != null
				&& !newValue.getArrival().equals(oldValue.getArrival())) {
			String objectId = newValue.getArrival().getObjectId();
			StopArea arrival = cache.getStopAreas().get(objectId);
			if (arrival == null) {
				arrival = stopAreaDAO.findByObjectId(objectId);
				if (arrival != null) {
					cache.getStopAreas().put(objectId, arrival);
				}
			}

			if (arrival != null) {
				oldValue.setArrival(arrival);
			}
		}
		if (newValue.getNoProcessing() != null
				&& !newValue.getNoProcessing().equals(oldValue.getNoProcessing())) {
			oldValue.setNoProcessing(newValue.getNoProcessing());
		}
		// Warning : JTS Geometry not protected from equals(null)
		if (oldValue.getInputGeometry() == null || ( newValue.getInputGeometry() != null
				&& !newValue.getInputGeometry().equals(oldValue.getInputGeometry()))) {
			oldValue.setInputGeometry(newValue.getInputGeometry());
		}
		if (oldValue.getProcessedGeometry() == null || ( newValue.getProcessedGeometry() != null
				&& !newValue.getProcessedGeometry().equals(oldValue.getProcessedGeometry()))) {
			oldValue.setProcessedGeometry(newValue.getProcessedGeometry());
		}
//		if (routeSectionDAO.findByObjectId(oldValue.getObjectId()) == null)
//			routeSectionDAO.create(oldValue);
//		else
//			routeSectionDAO.update(oldValue);
	}
}
