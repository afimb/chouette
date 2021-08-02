package mobi.chouette.exchange.importer.updater;

import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import mobi.chouette.common.CollectionUtil;
import mobi.chouette.common.Context;
import mobi.chouette.common.Pair;
import mobi.chouette.dao.DeadRunDAO;
import mobi.chouette.dao.FootnoteDAO;
import mobi.chouette.dao.RouteSectionDAO;
import mobi.chouette.dao.StopPointDAO;
import mobi.chouette.dao.VehicleJourneyDAO;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.DeadRun;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.RouteSection;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.util.NeptuneUtil;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Stateless(name = JourneyPatternUpdater.BEAN_NAME)
public class JourneyPatternUpdater implements Updater<JourneyPattern> {

	public static final String BEAN_NAME = "JourneyPatternUpdater";

	@EJB
	private StopPointDAO stopPointDAO;

	@EJB
	private VehicleJourneyDAO vehicleJourneyDAO;

	@EJB
	private DeadRunDAO deadRunDAO;

	@EJB
	private RouteSectionDAO routeSectionDAO;

	@EJB(beanName = VehicleJourneyUpdater.BEAN_NAME)
	private Updater<VehicleJourney> vehicleJourneyUpdater;

	@EJB(beanName = DeadRunUpdater.BEAN_NAME)
	private Updater<DeadRun> deadRunUpdater;

	@EJB(beanName = RouteSectionUpdater.BEAN_NAME)
	private Updater<RouteSection> routeSectionUpdater;

	@EJB
	private FootnoteDAO footnoteDAO;

	@EJB(beanName = FootnoteUpdater.BEAN_NAME)
	private Updater<Footnote> footnoteUpdater;

	@Override
	public void update(Context context, JourneyPattern oldValue, JourneyPattern newValue) throws Exception {

		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);

//		Monitor monitor = MonitorFactory.start(BEAN_NAME);
		Referential cache = (Referential) context.get(CACHE);
		
		// Database test init
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validationReporter.addItemToValidationReport(context, DATABASE_JOURNEY_PATTERN_1, "E");
		validationReporter.addItemToValidationReport(context, DATABASE_VEHICLE_JOURNEY_1, "E");
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		
		if (oldValue.isDetached()) {
			// object does not exist in database
			oldValue.setObjectId(newValue.getObjectId());
			oldValue.setObjectVersion(newValue.getObjectVersion());
			oldValue.setCreationTime(newValue.getCreationTime());
			oldValue.setCreatorId(newValue.getCreatorId());
			oldValue.setName(newValue.getName());
			oldValue.setComment(newValue.getComment());
			oldValue.setRegistrationNumber(newValue.getRegistrationNumber());
			oldValue.setPublishedName(newValue.getPublishedName());
			oldValue.setSectionStatus(newValue.getSectionStatus());
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
			if (newValue.getName() != null && !newValue.getName().equals(oldValue.getName())) {
				oldValue.setName(newValue.getName());
			}
			if (newValue.getComment() != null && !newValue.getComment().equals(oldValue.getComment())) {
				oldValue.setComment(newValue.getComment());
			}
			if (newValue.getRegistrationNumber() != null
					&& !newValue.getRegistrationNumber().equals(oldValue.getRegistrationNumber())) {
				oldValue.setRegistrationNumber(newValue.getRegistrationNumber());
			}
			if (newValue.getPublishedName() != null && !newValue.getPublishedName().equals(oldValue.getPublishedName())) {
				oldValue.setPublishedName(newValue.getPublishedName());
			}

			if (newValue.getSectionStatus() != null && !newValue.getSectionStatus().equals(oldValue.getSectionStatus())) {
				oldValue.setSectionStatus(newValue.getSectionStatus());
			}
		}
		
		
		// RouteSections
		if (!newValue.getRouteSections().equals(oldValue.getRouteSections())) {
			oldValue.getRouteSections().clear();
			List<RouteSection> routeSections = null;
			for (RouteSection item : newValue.getRouteSections()) {
				RouteSection section = cache.getRouteSections().get(item.getObjectId());
				if (section == null) {
					if (routeSections == null) {
						routeSections = routeSectionDAO.findByObjectId(UpdaterUtils.getObjectIds(newValue.getRouteSections()));
						for (RouteSection object : routeSections) {
							cache.getRouteSections().put(object.getObjectId(), object);
						}
					}
					 section = cache.getRouteSections().get(item.getObjectId());
				}
				if (section == null) {
					section = ObjectFactory.getRouteSection(cache, item.getObjectId());
				}
				oldValue.getRouteSections().add(section);
			}
		}
		Collection<Pair<RouteSection, RouteSection>> modifiedRouteSection = CollectionUtil.intersection(
				oldValue.getRouteSections(), newValue.getRouteSections(), NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<RouteSection, RouteSection> pair : modifiedRouteSection) {
			routeSectionUpdater.update(context, pair.getLeft(), pair.getRight());
		}

		// StopPoint
		Collection<StopPoint> addedStopPoint = CollectionUtil.substract(newValue.getStopPoints(),
				oldValue.getStopPoints(), NeptuneIdentifiedObjectComparator.INSTANCE);

		List<StopPoint> stopPoints = null;
		for (StopPoint item : addedStopPoint) {

			StopPoint stopPoint = cache.getStopPoints().get(item.getObjectId());
			if (stopPoint == null) {
				if (stopPoints == null) {
					stopPoints = stopPointDAO.findByObjectId(UpdaterUtils.getObjectIds(addedStopPoint));
					for (StopPoint object : stopPoints) {
						cache.getStopPoints().put(object.getObjectId(), object);
					}
				}
				stopPoint = cache.getStopPoints().get(item.getObjectId());
			}

			if (stopPoint != null) {
				oldValue.addStopPoint(stopPoint);
			}
		}

		Collection<StopPoint> removedStopPoint = CollectionUtil.substract(oldValue.getStopPoints(),
				newValue.getStopPoints(), NeptuneIdentifiedObjectComparator.INSTANCE);
		for (StopPoint stopPoint : removedStopPoint) {
			oldValue.removeStopPoint(stopPoint);
		}

		// ArrivalStopPoint
		if (newValue.getArrivalStopPoint() == null) {
			oldValue.setArrivalStopPoint(null);
		} else if (!newValue.getArrivalStopPoint().equals(oldValue.getArrivalStopPoint())) {

			String objectId = newValue.getArrivalStopPoint().getObjectId();
			StopPoint stopPoint = cache.getStopPoints().get(objectId);
			if (stopPoint == null) {
				stopPoint = stopPointDAO.findByObjectId(objectId);
				if (stopPoint != null) {
					cache.getStopPoints().put(objectId, stopPoint);
				}
			}

			if (stopPoint != null) {
				oldValue.setArrivalStopPoint(stopPoint);
			}
		}

		// DepartureStopPoint
		if (newValue.getDepartureStopPoint() == null) {
			oldValue.setDepartureStopPoint(null);
		} else if (!newValue.getDepartureStopPoint().equals(oldValue.getDepartureStopPoint())) {

			String objectId = newValue.getDepartureStopPoint().getObjectId();
			StopPoint stopPoint = cache.getStopPoints().get(objectId);
			if (stopPoint == null) {
				stopPoint = stopPointDAO.findByObjectId(objectId);
				if (stopPoint != null) {
					cache.getStopPoints().put(objectId, stopPoint);
				}
			}

			if (stopPoint != null) {
				oldValue.setDepartureStopPoint(stopPoint);
			}
		}

		// VehicleJourney
		Collection<VehicleJourney> addedVehicleJourney = CollectionUtil.substract(newValue.getVehicleJourneys(),
				oldValue.getVehicleJourneys(), NeptuneIdentifiedObjectComparator.INSTANCE);

		List<VehicleJourney> vehicleJourneys = null;
		for (VehicleJourney item : addedVehicleJourney) {

			VehicleJourney vehicleJourney = cache.getVehicleJourneys().get(item.getObjectId());
			if (vehicleJourney == null) {
				if (vehicleJourneys == null) {
					vehicleJourneys = vehicleJourneyDAO.findByObjectId(UpdaterUtils.getObjectIds(addedVehicleJourney));
					for (VehicleJourney object : vehicleJourneys) {
						cache.getVehicleJourneys().put(object.getObjectId(), object);
					}
				}
				vehicleJourney = cache.getVehicleJourneys().get(item.getObjectId());
			}

			if (vehicleJourney == null) {
				vehicleJourney = ObjectFactory.getVehicleJourney(cache, item.getObjectId());
			}
			if(vehicleJourney.getJourneyPattern() != null) {
				twoDatabaseVehicleJourneyOneTest(validationReporter, context, vehicleJourney, item, data);
			} else {
				vehicleJourney.setJourneyPattern(oldValue);
			}
		}

		Collection<Pair<VehicleJourney, VehicleJourney>> modifiedVehicleJourney = CollectionUtil.intersection(
				oldValue.getVehicleJourneys(), newValue.getVehicleJourneys(),
				NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<VehicleJourney, VehicleJourney> pair : modifiedVehicleJourney) {
			vehicleJourneyUpdater.update(context, pair.getLeft(), pair.getRight());
		}

		// DeadRun
		Collection<DeadRun> addedDeadRun = CollectionUtil.substract(newValue.getDeadRuns(),
				oldValue.getDeadRuns(), NeptuneIdentifiedObjectComparator.INSTANCE);

		List<DeadRun> deadRuns = null;
		for (DeadRun item : addedDeadRun) {

			DeadRun deadRun = cache.getDeadRuns().get(item.getObjectId());
			if (deadRun == null) {
				if (deadRuns == null) {
					deadRuns = deadRunDAO.findByObjectId(UpdaterUtils.getObjectIds(addedDeadRun));
					for (DeadRun object : deadRuns) {
						cache.getDeadRuns().put(object.getObjectId(), object);
					}
				}
				deadRun = cache.getDeadRuns().get(item.getObjectId());
			}

			if (deadRun == null) {
				deadRun = ObjectFactory.getDeadRun(cache, item.getObjectId());
			}
			if(deadRun.getJourneyPattern() != null) {
				//TODO twoDatabaseDeadRunOneTest(validationReporter, context, deadRun, item, data);
			} else {
				deadRun.setJourneyPattern(oldValue);
			}
		}

		Collection<Pair<DeadRun, DeadRun>> modifiedDeadRun = CollectionUtil.intersection(
				oldValue.getDeadRuns(), newValue.getDeadRuns(),
				NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<DeadRun, DeadRun> pair : modifiedDeadRun) {
			deadRunUpdater.update(context, pair.getLeft(), pair.getRight());
		}




		updateFootnotes(context,oldValue,newValue,cache);
//		monitor.stop();
	}
	
	private void updateFootnotes(Context context, JourneyPattern oldValue, JourneyPattern newValue, Referential cache) throws Exception {
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
	 * Test 2-DATABASE-VehicleJourney-1
	 * @param validationReporter
	 * @param context
	 * @param oldVj
	 * @param newVj
	 */
	private void twoDatabaseVehicleJourneyOneTest(ValidationReporter validationReporter, Context context, VehicleJourney oldVj, VehicleJourney newVj, ValidationData data) {
		if(!NeptuneUtil.sameValue(oldVj.getJourneyPattern(), newVj.getJourneyPattern()))
			validationReporter.addCheckPointReportError(context, DATABASE_VEHICLE_JOURNEY_1, data.getDataLocations().get(newVj.getObjectId()));
		else
			validationReporter.reportSuccess(context, DATABASE_VEHICLE_JOURNEY_1);
	}
	
}
