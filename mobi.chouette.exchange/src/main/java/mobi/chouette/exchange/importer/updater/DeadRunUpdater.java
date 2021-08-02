package mobi.chouette.exchange.importer.updater;

import mobi.chouette.common.CollectionUtil;
import mobi.chouette.common.Context;
import mobi.chouette.common.Pair;
import mobi.chouette.dao.DeadRunAtStopDAO;
import mobi.chouette.dao.StopPointDAO;
import mobi.chouette.dao.TimetableDAO;
import mobi.chouette.model.DeadRun;
import mobi.chouette.model.DeadRunAtStop;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Stateless(name = DeadRunUpdater.BEAN_NAME)
public class DeadRunUpdater implements Updater<DeadRun> {

	public static final String BEAN_NAME = "DeadRunUpdater";

	private static final Comparator<DeadRunAtStop> DEAD_RUN_AT_STOP_COMPARATOR = new Comparator<DeadRunAtStop>() {
		@Override
		public int compare(DeadRunAtStop o1, DeadRunAtStop o2) {
			int result = -1;
			if (o1.getStopPoint() != null && o2.getStopPoint() != null) {
				result = (o1.getStopPoint().equals(o2.getStopPoint())) ? 0 : -1;
			}
			return result;
		}
	};

	@EJB
	private StopPointDAO stopPointDAO;

	@EJB
	private DeadRunAtStopDAO vehicleJourneyAtStopDAO;

	@EJB
	private TimetableDAO timetableDAO;


	@EJB(beanName = TimetableUpdater.BEAN_NAME)
	private Updater<Timetable> timetableUpdater;

	@EJB(beanName = DeadRunAtStopUpdater.BEAN_NAME)
	private Updater<DeadRunAtStop> deadRunAtStopUpdater;


	@Override
	public void update(Context context, DeadRun oldValue, DeadRun newValue) throws Exception {

		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);

//		Monitor monitor = MonitorFactory.start(BEAN_NAME);
		Referential cache = (Referential) context.get(CACHE);
		cache.getDeadRuns().put(oldValue.getObjectId(), oldValue);

		boolean optimized = (Boolean) context.get(OPTIMIZED);

		// TODO Database test init
		//ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		//validationReporter.addItemToValidationReport(context, DATABASE_VEHICLE_JOURNEY_2, "W");
		//ValidationData data = (ValidationData) context.get(VALIDATION_DATA);


		if (oldValue.isDetached()) {
			// object does not exist in database
			oldValue.setObjectId(newValue.getObjectId());
			oldValue.setObjectVersion(newValue.getObjectVersion());
			oldValue.setCreationTime(newValue.getCreationTime());
			oldValue.setCreatorId(newValue.getCreatorId());
			oldValue.setDetached(false);
		} else {
			//TODO twoDatabaseDeadRunTwoTest(validationReporter, context, oldValue.getCompany(), newValue.getCompany(), data);
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
		}

			// DeadRunAtStop
			if (!optimized) {

				Collection<DeadRunAtStop> addedDeadRunAtStop = CollectionUtil.substract(
						newValue.getDeadRunAtStops(), oldValue.getDeadRunAtStops(),
						DEAD_RUN_AT_STOP_COMPARATOR);

				final Collection<String> objectIds = new ArrayList<String>();
				for (DeadRunAtStop vehicleJourneyAtStop : addedDeadRunAtStop) {
					objectIds.add(vehicleJourneyAtStop.getStopPoint().getObjectId());
				}
				List<StopPoint> stopPoints = null;
				for (DeadRunAtStop item : addedDeadRunAtStop) {
					DeadRunAtStop vehicleJourneyAtStop = ObjectFactory.getDeadRunAtStop(cache, item.getObjectId());

					StopPoint stopPoint = cache.getStopPoints().get(item.getStopPoint().getObjectId());
					if (stopPoint == null) {
						if (stopPoints == null) {
							stopPoints = stopPointDAO.findByObjectId(objectIds);
							for (StopPoint object : stopPoints) {
								cache.getStopPoints().put(object.getObjectId(), object);
							}
						}
						stopPoint = cache.getStopPoints().get(item.getStopPoint().getObjectId());
					}

					if (stopPoint != null) {
						vehicleJourneyAtStop.setStopPoint(stopPoint);
					}
					vehicleJourneyAtStop.setDeadRun(oldValue);
				}

				Collection<Pair<DeadRunAtStop, DeadRunAtStop>> modifiedDeadRunAtStop = CollectionUtil
						.intersection(oldValue.getDeadRunAtStops(), newValue.getDeadRunAtStops(),
								DEAD_RUN_AT_STOP_COMPARATOR);
				for (Pair<DeadRunAtStop, DeadRunAtStop> pair : modifiedDeadRunAtStop) {
					deadRunAtStopUpdater.update(context, pair.getLeft(), pair.getRight());
				}

				Collection<DeadRunAtStop> removedDeadRunAtStop = CollectionUtil.substract(
						oldValue.getDeadRunAtStops(), newValue.getDeadRunAtStops(),
						DEAD_RUN_AT_STOP_COMPARATOR);
				for (DeadRunAtStop vehicleJourneyAtStop : removedDeadRunAtStop) {
					vehicleJourneyAtStop.setDeadRun(null);
					vehicleJourneyAtStopDAO.delete(vehicleJourneyAtStop);
				}
			}

			// Timetable
			Collection<Timetable> addedTimetable = CollectionUtil.substract(newValue.getTimetables(),
					oldValue.getTimetables(), NeptuneIdentifiedObjectComparator.INSTANCE);

			List<Timetable> timetables = null;
			for (Timetable item : addedTimetable) {

				Timetable timetable = cache.getTimetables().get(item.getObjectId());
				if (timetable == null) {
					if (timetables == null) {
						timetables = timetableDAO.findByObjectId(UpdaterUtils.getObjectIds(addedTimetable));
						for (Timetable object : timetables) {
							cache.getTimetables().put(object.getObjectId(), object);
						}
					}
					timetable = cache.getTimetables().get(item.getObjectId());
				}

				if (timetable == null) {
					timetable = ObjectFactory.getTimetable(cache, item.getObjectId());
				}
				timetable.addDeadRun(oldValue);
			}

			Collection<Pair<Timetable, Timetable>> modifiedTimetable = CollectionUtil.intersection(
					oldValue.getTimetables(), newValue.getTimetables(), NeptuneIdentifiedObjectComparator.INSTANCE);
			for (Pair<Timetable, Timetable> pair : modifiedTimetable) {
				timetableUpdater.update(context, pair.getLeft(), pair.getRight());
			}

			Collection<Timetable> removedTimetable = CollectionUtil.substract(oldValue.getTimetables(),
					newValue.getTimetables(), NeptuneIdentifiedObjectComparator.INSTANCE);
			for (Timetable timetable : removedTimetable) {
				timetable.removeDeadRun(oldValue);
			}
	}


	/**
	 * Test 2-DATABASE-DeadRun-2
	 * @param validationReporter
	 * @param context
	 * @param oldCompany
	 * @param newCompany
	 */
/*	private void twoDatabaseDeadRunTwoTest(ValidationReporter validationReporter, Context context, Company oldCompany,  Company newCompany, ValidationData data) {
		if(!NeptuneUtil.sameValue(oldCompany, newCompany))
			validationReporter.addCheckPointReportError(context, DATABASE_VEHICLE_JOURNEY_2, data.getDataLocations().get(newCompany.getObjectId()));
		else
			validationReporter.reportSuccess(context, DATABASE_VEHICLE_JOURNEY_2);
	}*/
}
