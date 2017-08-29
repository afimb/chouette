package mobi.chouette.exchange.stopplace;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.dao.ReferentialDAO;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.dao.StopPointDAO;
import mobi.chouette.exchange.importer.updater.StopAreaUpdater;
import mobi.chouette.exchange.importer.updater.Updater;
import mobi.chouette.model.StopArea;
import mobi.chouette.persistence.hibernate.ContextHolder;

@Singleton(name = StopAreaUpdateService.BEAN_NAME)
@Log4j
public class StopAreaUpdateService {

	public static final String BEAN_NAME = "StopAreaUpdateService";

	@EJB
	private StopAreaDAO stopAreaDAO;

	@EJB(beanName = StopAreaUpdater.BEAN_NAME)
	private Updater<StopArea> stopAreaUpdater;

	@EJB
	private ReferentialDAO referentialDAO;

	@EJB
	private StopPointDAO stopPointDAO;

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void createOrUpdateStopAreas(Context context, StopAreaUpdateContext updateContext) {

		new StopAreaUpdateTask(stopAreaDAO, stopAreaUpdater, context, updateContext).update();

		updateContext.getMergedQuays().forEach((oldStopAreaId, newStopAreaId) -> updateStopAreaReference(oldStopAreaId, newStopAreaId));
	}

	@TransactionAttribute
	public void deleteStopArea(String objectId) {
		StopArea stopArea = stopAreaDAO.findByObjectId(objectId);
		if (stopArea != null) {
			cascadeDeleteStopArea(stopArea);
		} else {
			log.info("Ignored delete for unknown stop area: " + objectId);
		}
	}

	private void updateStopAreaReference(String oldStopAreaId, String newStopAreaId) {
		String orgContext = ContextHolder.getContext();
		try {
			for (String referential : referentialDAO.getReferentials()) {
				ContextHolder.setContext(referential);
				stopPointDAO.replaceContainedInStopAreaReference(oldStopAreaId, newStopAreaId);
			}
		} finally {
			ContextHolder.setContext(orgContext); // reset context
		}
	}

	private void cascadeDeleteStopArea(StopArea stopArea) {
		stopArea.getContainedStopAreas().forEach(child -> cascadeDeleteStopArea(child));
		stopAreaDAO.delete(stopArea);
		log.info("Deleted stop area: " + stopArea.getObjectId());
	}


}
