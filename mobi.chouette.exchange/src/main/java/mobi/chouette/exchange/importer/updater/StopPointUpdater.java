package mobi.chouette.exchange.importer.updater;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import mobi.chouette.common.Context;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.NeptuneUtil;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Stateless(name = StopPointUpdater.BEAN_NAME)
public class StopPointUpdater implements Updater<StopPoint> {

	public static final String BEAN_NAME = "StopPointUpdater";

	@EJB
	private StopAreaDAO stopAreaDAO;

	@EJB(beanName = StopAreaUpdater.BEAN_NAME)
	private Updater<StopArea> stopAreaUpdater;

	@Override
	public void update(Context context, StopPoint oldValue, StopPoint newValue) throws Exception {

		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);

//		Monitor monitor = MonitorFactory.start(BEAN_NAME);
		Referential cache = (Referential) context.get(CACHE);
		cache.getStopPoints().put(oldValue.getObjectId(), oldValue);
		
		// Database test init
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validationReporter.addItemToValidationReport(context, "2-", "StopPoint", 2, "E", "W");
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		
		twoStopPointTwoTest(validationReporter, context, oldValue, newValue, data);
		if (oldValue.isDetached()) {
			// object does not exist in database
			oldValue.setObjectId(newValue.getObjectId());
			oldValue.setObjectVersion(newValue.getObjectVersion());
			oldValue.setCreationTime(newValue.getCreationTime());
			oldValue.setCreatorId(newValue.getCreatorId());
			oldValue.setForAlighting(newValue.getForAlighting());
			oldValue.setForBoarding(newValue.getForBoarding());
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

			// Boarding and alighting
			if (newValue.getForAlighting() != null && !newValue.getForAlighting().equals(oldValue.getForAlighting())) {
				oldValue.setForAlighting(newValue.getForAlighting());
			}

			if (newValue.getForBoarding() != null && !newValue.getForBoarding().equals(oldValue.getForBoarding())) {
				oldValue.setForBoarding(newValue.getForBoarding());
			}
		}

		// StopArea
		twoStopPointThreeTest(validationReporter, context, oldValue.getContainedInStopArea(), newValue.getContainedInStopArea(), data);
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
//		monitor.stop();

	}
	
	
	/**
	 * Test 2-StopPoint-2
	 * @param validationReporter
	 * @param context
	 * @param oldSp
	 * @param newSp
	 */
	private void twoStopPointTwoTest(ValidationReporter validationReporter, Context context, StopPoint oldSp, StopPoint newSp, ValidationData data) {
		
		if(!(oldSp.equals(null) && newSp.equals(null))) {
			if(!oldSp.getPosition().equals(newSp.getPosition()))
				validationReporter.addCheckPointReportError(context, STOP_POINT_2, data.getDataLocations().get(newSp.getObjectId()));
			else
				validationReporter.reportSuccess(context, STOP_POINT_2);
		}
	}
	
	
	/**
	 * Test 2-StopPoint-3
	 * @param validationReporter
	 * @param context
	 * @param oldSp
	 * @param newSp
	 */
	private void twoStopPointThreeTest(ValidationReporter validationReporter, Context context, StopArea oldSA, StopArea newSA, ValidationData data) {
		if(!NeptuneUtil.sameValue(oldSA, newSA))
			validationReporter.addCheckPointReportError(context, STOP_POINT_3, data.getDataLocations().get(newSA.getObjectId()));
		else
			validationReporter.reportSuccess(context, STOP_POINT_3);
	}
}
