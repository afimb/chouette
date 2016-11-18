package mobi.chouette.exchange.importer.updater;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import mobi.chouette.common.Context;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.ChouetteId;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.NeptuneUtil;
import mobi.chouette.exchange.ChouetteIdObjectFactory;
import mobi.chouette.model.util.Referential;

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
		cache.getStopPoints().put(oldValue.getChouetteId(), oldValue);
		
		// Database test init
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validationReporter.addItemToValidationReport(context, DATABASE_STOP_POINT_2, "E");
		validationReporter.addItemToValidationReport(context, DATABASE_STOP_POINT_3, "W");
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		
		if (oldValue.isDetached()) {
			// object does not exist in database
			oldValue.getChouetteId().setObjectId(newValue.getChouetteId().getObjectId());
			oldValue.setObjectVersion(newValue.getObjectVersion());
			oldValue.setCreationTime(newValue.getCreationTime());
			oldValue.setCreatorId(newValue.getCreatorId());
			oldValue.setForAlighting(newValue.getForAlighting());
			oldValue.setForBoarding(newValue.getForBoarding());
			oldValue.setDetached(false);
		} else {
			twoDatabaseStopPointTwoTest(validationReporter, context, oldValue, newValue, data);
			twoDatabaseStopPointThreeTest(validationReporter, context, oldValue.getContainedInStopArea(), newValue.getContainedInStopArea(), data);
			if (newValue.getChouetteId().getObjectId() != null && !newValue.getChouetteId().getObjectId().equals(oldValue.getChouetteId().getObjectId())) {
				oldValue.getChouetteId().setObjectId(newValue.getChouetteId().getObjectId());
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
		
		if (newValue.getContainedInStopArea() == null) {
			oldValue.setContainedInStopArea(null);
		} else {
			String codeSpace = newValue.getContainedInStopArea().getChouetteId().getCodeSpace();
			String objectId = newValue.getContainedInStopArea().getChouetteId().getObjectId();
			ChouetteId chouetteId = newValue.getContainedInStopArea().getChouetteId();
			StopArea stopArea = cache.getStopAreas().get(objectId);
			if (stopArea == null) {
				stopArea = stopAreaDAO.findByChouetteId(codeSpace, objectId);
				if (stopArea != null) {
					cache.getStopAreas().put(chouetteId, stopArea);
				}
			}

			if (stopArea == null) {
				stopArea = ChouetteIdObjectFactory.getStopArea(cache, chouetteId);
			}
			
			oldValue.setContainedInStopArea(stopArea);

			if (!context.containsKey(AREA_BLOC))
			   stopAreaUpdater.update(context, oldValue.getContainedInStopArea(), newValue.getContainedInStopArea());
		}
//		monitor.stop();

	}
	
	
	/**
	 * Test 2-DATABASE-StopPoint-2
	 * @param validationReporter
	 * @param context
	 * @param oldSp
	 * @param newSp
	 */
	private void twoDatabaseStopPointTwoTest(ValidationReporter validationReporter, Context context, StopPoint oldSp, StopPoint newSp, ValidationData data) {
		if(oldSp !=null && newSp != null) {
			if(oldSp.getPosition() != null && newSp.getPosition() != null) {
				if(!oldSp.getPosition().equals(newSp.getPosition()))
					validationReporter.addCheckPointReportError(context, DATABASE_STOP_POINT_2, data.getDataLocations().get(newSp.getChouetteId().getObjectId()));
				else
					validationReporter.reportSuccess(context, DATABASE_STOP_POINT_2);
			}
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
		if(!NeptuneUtil.sameValue(oldSA, newSA))
			validationReporter.addCheckPointReportError(context, DATABASE_STOP_POINT_3, data.getDataLocations().get(newSA.getChouetteId().getObjectId()));
		else
			validationReporter.reportSuccess(context, DATABASE_STOP_POINT_3);
	}
}
