package mobi.chouette.exchange.importer.updater;

import javax.ejb.EJB;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;

@Log4j
public class StopPointUpdater implements Updater<StopPoint> {

	@EJB
	private StopAreaDAO stopAreaDAO;

	@Override
	public void update(Context context, StopPoint oldValue, StopPoint newValue)
			throws Exception {

		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);

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

		// StopArea
		if (newValue.getContainedInStopArea() == null) {
			oldValue.setContainedInStopArea(null);
		} else {
			StopArea stopArea = stopAreaDAO.findByObjectId(newValue
					.getContainedInStopArea().getObjectId());
			if (stopArea == null) {
				stopArea = new StopArea();
				stopArea.setObjectId(newValue.getContainedInStopArea()
						.getObjectId());
				stopAreaDAO.create(stopArea);
			}
			Updater<StopArea> stopAreaUpdater = UpdaterFactory
					.create(StopAreaUpdater.class.getName());
			stopAreaUpdater.update(null, oldValue.getContainedInStopArea(),
					newValue.getContainedInStopArea());
			oldValue.setContainedInStopArea(stopArea);
		}

	}

	static {
		UpdaterFactory.register(StopPointUpdater.class.getName(),
				new UpdaterFactory() {
					private StopPointUpdater INSTANCE = new StopPointUpdater();

					@Override
					protected Updater<StopPoint> create() {
						return INSTANCE;
					}
				});
	}

}
