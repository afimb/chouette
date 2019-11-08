package mobi.chouette.exchange.importer.updater;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import mobi.chouette.common.Context;
import mobi.chouette.dao.ScheduledStopPointDAO;
import mobi.chouette.model.RouteSection;
import mobi.chouette.model.ScheduledStopPoint;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Stateless(name = RouteSectionUpdater.BEAN_NAME)
public class RouteSectionUpdater implements Updater<RouteSection> {

	public static final String BEAN_NAME = "RouteSectionUpdater";


	@EJB(beanName = ScheduledStopPointUpdater.BEAN_NAME)
	private Updater<ScheduledStopPoint> scheduledStopPointUpdater;

	@EJB
	private ScheduledStopPointDAO scheduledStopPointDAO;

	@Override
	public void update(Context context, RouteSection oldValue, RouteSection newValue) throws Exception {
		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);
		Monitor monitor = MonitorFactory.start(BEAN_NAME);
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
		if (newValue.getFromScheduledStopPoint() != null
				&& !newValue.getFromScheduledStopPoint().equals(oldValue.getFromScheduledStopPoint())) {


			String scheduledStopPointId = newValue.getFromScheduledStopPoint().getObjectId();
			ScheduledStopPoint scheduledStopPoint = cache.getScheduledStopPoints().get(scheduledStopPointId);
			if (scheduledStopPoint == null) {
				scheduledStopPoint = scheduledStopPointDAO.findByObjectId(scheduledStopPointId);
				if (scheduledStopPoint != null) {
					cache.getScheduledStopPoints().put(scheduledStopPointId, scheduledStopPoint);
				}
			}
			if (scheduledStopPoint == null) {
				scheduledStopPoint = ObjectFactory.getScheduledStopPoint(cache, scheduledStopPointId);
			}
			oldValue.setFromScheduledStopPoint(scheduledStopPoint);

			scheduledStopPointUpdater.update(context, oldValue.getFromScheduledStopPoint(), newValue.getFromScheduledStopPoint());

		}
		if (newValue.getToScheduledStopPoint() != null
				&& !newValue.getToScheduledStopPoint().equals(oldValue.getToScheduledStopPoint())) {

			String scheduledStopPointId = newValue.getToScheduledStopPoint().getObjectId();
			ScheduledStopPoint scheduledStopPoint = cache.getScheduledStopPoints().get(scheduledStopPointId);
			if (scheduledStopPoint == null) {
				scheduledStopPoint = scheduledStopPointDAO.findByObjectId(scheduledStopPointId);
				if (scheduledStopPoint != null) {
					cache.getScheduledStopPoints().put(scheduledStopPointId, scheduledStopPoint);
				}
			}
			if (scheduledStopPoint == null) {
				scheduledStopPoint = ObjectFactory.getScheduledStopPoint(cache, scheduledStopPointId);
			}
			oldValue.setToScheduledStopPoint(scheduledStopPoint);

			scheduledStopPointUpdater.update(context, oldValue.getToScheduledStopPoint(), newValue.getToScheduledStopPoint());
		}
		if (newValue.getNoProcessing() != null
				&& !newValue.getNoProcessing().equals(oldValue.getNoProcessing())) {
			oldValue.setNoProcessing(newValue.getNoProcessing());
		}
		// Warning : JTS Geometry not protected from equals(null)
		if (oldValue.getInputGeometry() == null || (newValue.getInputGeometry() != null
				&& !newValue.getInputGeometry().equals(oldValue.getInputGeometry()))) {
			oldValue.setInputGeometry(newValue.getInputGeometry());
		}
		if (oldValue.getProcessedGeometry() == null || (newValue.getProcessedGeometry() != null
				&& !newValue.getProcessedGeometry().equals(oldValue.getProcessedGeometry()))) {
			oldValue.setProcessedGeometry(newValue.getProcessedGeometry());
		}
//		if (routeSectionDAO.findByObjectId(oldValue.getObjectId()) == null)
//			routeSectionDAO.create(oldValue);
//		else
//			routeSectionDAO.update(oldValue);
		monitor.stop();
	}
}
