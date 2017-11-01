package mobi.chouette.exchange.importer.updater;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.dao.ScheduledStopPointDAO;
import mobi.chouette.model.RoutePoint;
import mobi.chouette.model.ScheduledStopPoint;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
@Stateless(name = RoutePointUpdater.BEAN_NAME)
public class RoutePointUpdater implements Updater<RoutePoint> {
	public static final String BEAN_NAME = "RoutePointUpdater";


	@EJB
	private ScheduledStopPointDAO scheduledStopPointDAO;

	@EJB(beanName = ScheduledStopPointUpdater.BEAN_NAME)
	private Updater<ScheduledStopPoint> scheduledStopPointUpdater;

	@Override
	public void update(Context context, RoutePoint oldValue, RoutePoint newValue) throws Exception {

		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);

//		Monitor monitor = MonitorFactory.start(BEAN_NAME);
		Referential cache = (Referential) context.get(CACHE);
		cache.getRoutePoints().put(oldValue.getObjectId(), oldValue);

		if (oldValue.isDetached()) {
			// object does not exist in database
			oldValue.setObjectId(newValue.getObjectId());
			oldValue.setObjectVersion(newValue.getObjectVersion());
			oldValue.setCreationTime(newValue.getCreationTime());
			oldValue.setCreatorId(newValue.getCreatorId());
			oldValue.setBoarderCrossing(newValue.getBoarderCrossing());
			oldValue.setName(newValue.getName());
			oldValue.setDetached(false);
		} else {

			if (newValue.getObjectId() != null && !newValue.getObjectId().equals(oldValue.getObjectId())) {
				oldValue.setObjectId(newValue.getObjectId());
			}
			if (newValue.getObjectVersion() != null && !newValue.getObjectVersion().equals(oldValue.getObjectVersion())) {
				oldValue.setObjectVersion(newValue.getObjectVersion());
			}
			if (newValue.getCreationTime() != null && !newValue.getCreationTime().equals(oldValue.getCreationTime())) {
				oldValue.setCreationTime(newValue.getCreationTime());
			}
			if (newValue.getCreatorId() != null && !newValue.getCreatorId().equals(oldValue.getCreatorId())) {
				oldValue.setCreatorId(newValue.getCreatorId());
			}

			if (newValue.getBoarderCrossing() != null && !newValue.getBoarderCrossing().equals(oldValue.getBoarderCrossing())) {
				oldValue.setBoarderCrossing(newValue.getBoarderCrossing());
			}

			if (newValue.getName() != null && !newValue.getName().equals(oldValue.getName())) {
				oldValue.setName(newValue.getName());
			}
		}

		String scheduledStopPointId = newValue.getScheduledStopPoint().getObjectId();
		ScheduledStopPoint scheduledStopPoint = cache.getScheduledStopPoints().get(scheduledStopPointId);
		if (scheduledStopPoint==null) {
			scheduledStopPoint = scheduledStopPointDAO.findByObjectId(scheduledStopPointId);
			if (scheduledStopPoint != null) {
				cache.getScheduledStopPoints().put(scheduledStopPointId, scheduledStopPoint);
			}
		}
		if (scheduledStopPoint == null) {
			scheduledStopPoint = ObjectFactory.getScheduledStopPoint(cache, scheduledStopPointId);
		}
		oldValue.setScheduledStopPoint(scheduledStopPoint);

		scheduledStopPointUpdater.update(context, oldValue.getScheduledStopPoint(), newValue.getScheduledStopPoint());

	}

}
