package mobi.chouette.exchange.importer.updater;

import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.CollectionUtil;
import mobi.chouette.common.Context;
import mobi.chouette.common.Pair;
import mobi.chouette.dao.DestinationDisplayDAO;
import mobi.chouette.dao.ScheduledStopPointDAO;
import mobi.chouette.dao.FootnoteDAO;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.DestinationDisplay;
import mobi.chouette.model.ScheduledStopPoint;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
@Stateless(name = StopPointUpdater.BEAN_NAME)
public class StopPointUpdater implements Updater<StopPoint> {

	public static final String BEAN_NAME = "StopPointUpdater";

	@EJB
	private ScheduledStopPointDAO scheduledStopPointDAO;

	@EJB
	private DestinationDisplayDAO destinationDisplayDAO;

	@EJB(beanName = ScheduledStopPointUpdater.BEAN_NAME)
	private Updater<ScheduledStopPoint> scheduledStopPointUpdater;

	@EJB(beanName = DestinationDisplayUpdater.BEAN_NAME)
	private Updater<DestinationDisplay> destinationDisplayUpdater;
	
	@EJB
	private FootnoteDAO footnoteDAO;

	@EJB(beanName = FootnoteUpdater.BEAN_NAME)
	private Updater<Footnote> footnoteUpdater;
	
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
		validationReporter.addItemToValidationReport(context, DATABASE_STOP_POINT_2, "E");
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		
		if (oldValue.isDetached()) {
			// object does not exist in database
			oldValue.setObjectId(newValue.getObjectId());
			oldValue.setObjectVersion(newValue.getObjectVersion());
			oldValue.setCreationTime(newValue.getCreationTime());
			oldValue.setCreatorId(newValue.getCreatorId());
			oldValue.setForAlighting(newValue.getForAlighting());
			oldValue.setForBoarding(newValue.getForBoarding());
			oldValue.setDestinationDisplay(newValue.getDestinationDisplay());
			oldValue.setDetached(false);
		} else {
			twoDatabaseStopPointTwoTest(validationReporter, context, oldValue, newValue, data);

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

		// Destination display
		if (newValue.getDestinationDisplay() == null) {
			oldValue.setDestinationDisplay(null);
		} else {
			String objectId = newValue.getDestinationDisplay().getObjectId();
			DestinationDisplay destinationDisplay = cache.getDestinationDisplays().get(objectId);
			if (destinationDisplay == null) {
				destinationDisplay = destinationDisplayDAO.findByObjectId(objectId);
				if (destinationDisplay != null) {
					cache.getDestinationDisplays().put(objectId, destinationDisplay);
				}
			}
			if (destinationDisplay == null) {
				destinationDisplay = ObjectFactory.getDestinationDisplay(cache, objectId);
			}
			oldValue.setDestinationDisplay(destinationDisplay);
			
			destinationDisplayUpdater.update(context, oldValue.getDestinationDisplay(), newValue.getDestinationDisplay());
		}

	
		
		updateFootnotes(context,oldValue,newValue,cache);
//		monitor.stop();
	}
	
	private void updateFootnotes(Context context, StopPoint oldValue, StopPoint newValue, Referential cache) throws Exception {
		Collection<Footnote> addedFootnote = CollectionUtil.substract(newValue.getFootnotes(),
				oldValue.getFootnotes(), NeptuneIdentifiedObjectComparator.INSTANCE);
		List<Footnote> footnotes = null;
		for (Footnote item : addedFootnote) {
			Footnote footnote = cache.getFootnotes().get(item.getObjectId());
			if (footnote == null) {
				if (footnotes == null) {
					footnotes = footnoteDAO.findByObjectId(UpdaterUtils.getObjectIds(addedFootnote));
					for (Footnote object : footnotes) {
						cache.getFootnotes().put(object.getObjectId(), object);
					}
				}
				footnote = cache.getFootnotes().get(item.getObjectId());
			}
			if (footnote == null) {
				footnote = ObjectFactory.getFootnote(cache, item.getObjectId());
			}
			oldValue.getFootnotes().add(footnote);
		}

		Collection<Pair<Footnote, Footnote>> modifiedFootnote = CollectionUtil.intersection(
				oldValue.getFootnotes(), newValue.getFootnotes(),
				NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<Footnote, Footnote> pair : modifiedFootnote) {
			footnoteUpdater.update(context, pair.getLeft(), pair.getRight());
		}

		Collection<Footnote> removedFootnote = CollectionUtil.substract(oldValue.getFootnotes(),
				newValue.getFootnotes(), NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Footnote Footnote : removedFootnote) {
			oldValue.getFootnotes().remove(Footnote);
		}

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
					validationReporter.addCheckPointReportError(context, DATABASE_STOP_POINT_2, data.getDataLocations().get(newSp.getObjectId()));
				else
					validationReporter.reportSuccess(context, DATABASE_STOP_POINT_2);
			}
		}
	}
	

}
