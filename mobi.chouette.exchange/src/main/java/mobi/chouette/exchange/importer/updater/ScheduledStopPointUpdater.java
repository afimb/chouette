package mobi.chouette.exchange.importer.updater;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.dao.DestinationDisplayDAO;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.ScheduledStopPoint;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.util.NeptuneUtil;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
@Stateless(name = ScheduledStopPointUpdater.BEAN_NAME)
public class ScheduledStopPointUpdater implements Updater<ScheduledStopPoint> {
	public static final String BEAN_NAME = "ScheduledStopPointUpdater";

	@EJB
	private StopAreaDAO stopAreaDAO;

	@EJB
	private DestinationDisplayDAO destinationDisplayDAO;

	@EJB(beanName = StopAreaUpdater.BEAN_NAME)
	private Updater<StopArea> stopAreaUpdater;

	@Override
	public void update(Context context, ScheduledStopPoint oldValue, ScheduledStopPoint newValue) throws Exception {

		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);

		Referential cache = (Referential) context.get(CACHE);
		cache.getScheduledStopPoints().put(oldValue.getObjectId(), oldValue);
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validationReporter.addItemToValidationReport(context, DATABASE_STOP_POINT_3, "W");
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);

		if (oldValue.isDetached()) {
			// object does not exist in database
			oldValue.setObjectId(newValue.getObjectId());
			oldValue.setObjectVersion(newValue.getObjectVersion());
			oldValue.setCreationTime(newValue.getCreationTime());
			oldValue.setCreatorId(newValue.getCreatorId());
			oldValue.setName(newValue.getName());

			oldValue.setDetached(false);
		} else {
			twoDatabaseStopPointThreeTest(validationReporter, context, oldValue.getContainedInStopArea(), newValue.getContainedInStopArea(), data);

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
			if (newValue.getName() != null && !newValue.getName().equals(oldValue.getName())) {
				oldValue.setName(newValue.getName());
			}

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

			if (!context.containsKey(AREA_BLOC))
			   stopAreaUpdater.update(context, oldValue.getContainedInStopArea(), newValue.getContainedInStopArea());
		}

	}


	/**
	 * Test 2-DATABASE-StopPoint-3
	 * @param validationReporter
	 * @param context
	 * @param oldSp
	 * @param newSp
	 */
	private void twoDatabaseStopPointThreeTest(ValidationReporter validationReporter, Context context, StopArea oldSA, StopArea newSA, ValidationData data) {
		if(!NeptuneUtil.sameValue(oldSA, newSA)) {
			if(validationReporter == null) {
				log.error("ValidationReporter (validationReporter) is null");
			}
			if(oldSA == null) {
				log.warn("StopArea (oldSA) is null");
			}
			if(newSA == null) {
				log.warn("StopArea (newSA) is null");
			}
			if(data == null) {
				log.error("ValidationData (data) is null");
			} else if(data.getDataLocations() == null) {
				log.error("ValidationData.getDataLocations() is null");
			}

			if(validationReporter != null && newSA != null && data != null && data.getDataLocations() != null) {
				validationReporter.addCheckPointReportError(context, DATABASE_STOP_POINT_3, data.getDataLocations().get(newSA.getObjectId()));
			}
		}
		else
			validationReporter.reportSuccess(context, DATABASE_STOP_POINT_3);
	}
}
