package mobi.chouette.exchange.importer.updater;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;

@Log4j
@Stateless(name = StopPointUpdater.BEAN_NAME)
public class StopPointUpdater implements Updater<StopPoint> {

	public static final String BEAN_NAME = "StopPointUpdater";

	@EJB
	private StopAreaDAO stopAreaDAO;

	@Override
	public void update(Context context, StopPoint oldValue, StopPoint newValue)
			throws Exception {

		InitialContext initialContext = (InitialContext) context
				.get(INITIAL_CONTEXT);

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
				// stopAreaDAO.create(stopArea);
			}
			oldValue.setContainedInStopArea(stopArea);
			Updater<StopArea> stopAreaUpdater = UpdaterFactory.create(
					initialContext, StopAreaUpdater.class.getName());
			stopAreaUpdater.update(context, oldValue.getContainedInStopArea(),
					newValue.getContainedInStopArea());			
		}

	}

	static {
		UpdaterFactory.register(StopPointUpdater.class.getName(),
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
