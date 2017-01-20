package mobi.chouette.exchange.importer.updater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import mobi.chouette.common.CollectionUtil;
import mobi.chouette.common.Context;
import mobi.chouette.common.Pair;
import mobi.chouette.dao.CompanyDAO;
import mobi.chouette.dao.JourneyFrequencyDAO;
import mobi.chouette.dao.RouteDAO;
import mobi.chouette.dao.StopPointDAO;
import mobi.chouette.dao.TimebandDAO;
import mobi.chouette.dao.TimetableDAO;
import mobi.chouette.dao.VehicleJourneyAtStopDAO;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.Company;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.JourneyFrequency;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timeband;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.util.NeptuneUtil;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Stateless(name = VehicleJourneyUpdater.BEAN_NAME)
public class VehicleJourneyUpdater implements Updater<VehicleJourney> {

	public static final String BEAN_NAME = "VehicleJourneyUpdater";

	private static final Comparator<VehicleJourneyAtStop> VEHICLE_JOURNEY_AT_STOP_COMPARATOR = new Comparator<VehicleJourneyAtStop>() {
		@Override
		public int compare(VehicleJourneyAtStop o1, VehicleJourneyAtStop o2) {
			int result = -1;
			if (o1.getStopPoint() != null && o2.getStopPoint() != null) {
				result = (o1.getStopPoint().equals(o2.getStopPoint())) ? 0 : -1;
			}
			return result;
		}
	};

	private static final Comparator<JourneyFrequency> JOURNEY_FREQUENCY_COMPARATOR = new Comparator<JourneyFrequency>() {
		@Override
		public int compare(JourneyFrequency o1, JourneyFrequency o2) {
			int result = 1;
			if (o1.getTimeband() != null && o2.getTimeband() != null) {
				if (o1.getTimeband().equals(o2.getTimeband()))
					result = 0;
				else if (o1.getTimeband().getStartTime() == null || o1.getTimeband().getEndTime() == null)
					result = -1;
				else if (o1.getTimeband().getEndTime().before(o2.getTimeband().getStartTime())
						|| o1.getTimeband().getEndTime().equals(o2.getTimeband().getStartTime()))
					result = -1;
			}
			return result;
		}
	};

	@EJB(beanName = CompanyUpdater.BEAN_NAME)
	private Updater<Company> companyUpdater;

	@EJB
	private CompanyDAO companyDAO;

	@EJB
	private RouteDAO routeDAO;

	@EJB
	private StopPointDAO stopPointDAO;

	@EJB
	private VehicleJourneyAtStopDAO vehicleJourneyAtStopDAO;

	@EJB
	private TimetableDAO timetableDAO;

	@EJB
	private TimebandDAO timebandDAO;

	@EJB
	private JourneyFrequencyDAO journeyFrequencyDAO;

	@EJB(beanName = TimetableUpdater.BEAN_NAME)
	private Updater<Timetable> timetableUpdater;

	@EJB(beanName = VehicleJourneyAtStopUpdater.BEAN_NAME)
	private Updater<VehicleJourneyAtStop> vehicleJourneyAtStopUpdater;

	@EJB(beanName = JourneyFrequencyUpdater.BEAN_NAME)
	private Updater<JourneyFrequency> journeyFrequencyUpdater;

	@EJB(beanName = FootnoteUpdater.BEAN_NAME)
	private Updater<Footnote> footnoteUpdater;

	@Override
	public void update(Context context, VehicleJourney oldValue, VehicleJourney newValue) throws Exception {

		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);

//		Monitor monitor = MonitorFactory.start(BEAN_NAME);
		Referential cache = (Referential) context.get(CACHE);
		cache.getVehicleJourneys().put(oldValue.getObjectId(), oldValue);

		boolean optimized = (Boolean) context.get(OPTIMIZED);
		
		// Database test init
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validationReporter.addItemToValidationReport(context, DATABASE_VEHICLE_JOURNEY_2, "W");
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
				
				
		if (oldValue.isDetached()) {
			// object does not exist in database
			oldValue.setObjectId(newValue.getObjectId());
			oldValue.setObjectVersion(newValue.getObjectVersion());
			oldValue.setCreationTime(newValue.getCreationTime());
			oldValue.setCreatorId(newValue.getCreatorId());
			oldValue.setComment(newValue.getComment());
			oldValue.setTransportMode(newValue.getTransportMode());
			oldValue.setPublishedJourneyName(newValue.getPublishedJourneyName());
			oldValue.setPublishedJourneyIdentifier(newValue.getPublishedJourneyIdentifier());
			oldValue.setFacility(newValue.getFacility());
			oldValue.setVehicleTypeIdentifier(newValue.getVehicleTypeIdentifier());
			oldValue.setNumber(newValue.getNumber());
			oldValue.setMobilityRestrictedSuitability(newValue.getMobilityRestrictedSuitability());
			oldValue.setFlexibleService(newValue.getFlexibleService());
			oldValue.setJourneyCategory(newValue.getJourneyCategory());
			oldValue.setDetached(false);
		} else {
			twoDatabaseVehicleJourneyTwoTest(validationReporter, context, oldValue.getCompany(), newValue.getCompany(), data);
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
			if (newValue.getComment() != null && !newValue.getComment().equals(oldValue.getComment())) {
				oldValue.setComment(newValue.getComment());
			}
			if (newValue.getTransportMode() != null && !newValue.getTransportMode().equals(oldValue.getTransportMode())) {
				oldValue.setTransportMode(newValue.getTransportMode());
			}
			if (newValue.getPublishedJourneyName() != null
					&& !newValue.getPublishedJourneyName().equals(oldValue.getPublishedJourneyName())) {
				oldValue.setPublishedJourneyName(newValue.getPublishedJourneyName());
			}
			if (newValue.getPublishedJourneyIdentifier() != null
					&& !newValue.getPublishedJourneyIdentifier().equals(oldValue.getPublishedJourneyIdentifier())) {
				oldValue.setPublishedJourneyIdentifier(newValue.getPublishedJourneyIdentifier());
			}
			if (newValue.getFacility() != null && !newValue.getFacility().equals(oldValue.getFacility())) {
				oldValue.setFacility(newValue.getFacility());
			}
			if (newValue.getVehicleTypeIdentifier() != null
					&& !newValue.getVehicleTypeIdentifier().equals(oldValue.getVehicleTypeIdentifier())) {
				oldValue.setVehicleTypeIdentifier(newValue.getVehicleTypeIdentifier());
			}
			if (newValue.getNumber() != null && !newValue.getNumber().equals(oldValue.getNumber())) {
				oldValue.setNumber(newValue.getNumber());
			}

			if (newValue.getMobilityRestrictedSuitability() != null
					&& !newValue.getMobilityRestrictedSuitability().equals(oldValue.getMobilityRestrictedSuitability())) {
				oldValue.setMobilityRestrictedSuitability(newValue.getMobilityRestrictedSuitability());
			}
			if (newValue.getFlexibleService() != null
					&& !newValue.getFlexibleService().equals(oldValue.getFlexibleService())) {
				oldValue.setFlexibleService(newValue.getFlexibleService());
			}
			if (newValue.getJourneyCategory() != null
					&& !newValue.getJourneyCategory().equals(oldValue.getJourneyCategory())) {
				oldValue.setJourneyCategory(newValue.getJourneyCategory());
			}
		}

		// Company
		if (newValue.getCompany() == null) {
			oldValue.setCompany(null);
		} else {
			String objectId = newValue.getCompany().getObjectId();
			Company company = cache.getCompanies().get(objectId);
			if (company == null) {
				company = companyDAO.findByObjectId(objectId);
				if (company != null) {
					cache.getCompanies().put(objectId, company);
				}
			}

			if (company == null) {
				company = ObjectFactory.getCompany(cache, objectId);
			}
			oldValue.setCompany(company);
			companyUpdater.update(context, oldValue.getCompany(), newValue.getCompany());
		}

		// Route
		if (oldValue.getRoute() == null || !oldValue.getRoute().equals(newValue.getRoute())) {

			String objectId = newValue.getRoute().getObjectId();
			Route route = cache.getRoutes().get(objectId);
			if (route == null) {
				route = routeDAO.findByObjectId(objectId);
				if (route != null) {
					cache.getRoutes().put(objectId, route);
				}
			}

			if (route != null) {
				oldValue.setRoute(route);
			}
		}

		// VehicleJourneyAtStop
		if (!optimized) {

			Collection<VehicleJourneyAtStop> addedVehicleJourneyAtStop = CollectionUtil.substract(
					newValue.getVehicleJourneyAtStops(), oldValue.getVehicleJourneyAtStops(),
					VEHICLE_JOURNEY_AT_STOP_COMPARATOR);

			final Collection<String> objectIds = new ArrayList<String>();
			for (VehicleJourneyAtStop vehicleJourneyAtStop : addedVehicleJourneyAtStop) {
				objectIds.add(vehicleJourneyAtStop.getStopPoint().getObjectId());
			}
			List<StopPoint> stopPoints = null;
			for (VehicleJourneyAtStop item : addedVehicleJourneyAtStop) {
				VehicleJourneyAtStop vehicleJourneyAtStop = ObjectFactory.getVehicleJourneyAtStop();

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
				vehicleJourneyAtStop.setVehicleJourney(oldValue);
			}

			Collection<Pair<VehicleJourneyAtStop, VehicleJourneyAtStop>> modifiedVehicleJourneyAtStop = CollectionUtil
					.intersection(oldValue.getVehicleJourneyAtStops(), newValue.getVehicleJourneyAtStops(),
							VEHICLE_JOURNEY_AT_STOP_COMPARATOR);
			for (Pair<VehicleJourneyAtStop, VehicleJourneyAtStop> pair : modifiedVehicleJourneyAtStop) {
				vehicleJourneyAtStopUpdater.update(context, pair.getLeft(), pair.getRight());
			}

			Collection<VehicleJourneyAtStop> removedVehicleJourneyAtStop = CollectionUtil.substract(
					oldValue.getVehicleJourneyAtStops(), newValue.getVehicleJourneyAtStops(),
					VEHICLE_JOURNEY_AT_STOP_COMPARATOR);
			for (VehicleJourneyAtStop vehicleJourneyAtStop : removedVehicleJourneyAtStop) {
				vehicleJourneyAtStop.setVehicleJourney(null);
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
			timetable.addVehicleJourney(oldValue);
		}

		Collection<Pair<Timetable, Timetable>> modifiedTimetable = CollectionUtil.intersection(
				oldValue.getTimetables(), newValue.getTimetables(), NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<Timetable, Timetable> pair : modifiedTimetable) {
			timetableUpdater.update(context, pair.getLeft(), pair.getRight());
		}

		Collection<Timetable> removedTimetable = CollectionUtil.substract(oldValue.getTimetables(),
				newValue.getTimetables(), NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Timetable timetable : removedTimetable) {
			timetable.removeVehicleJourney(oldValue);
		}

		// journey frequency
		/* if (!optimized) */{
			Collection<JourneyFrequency> addedJourneyFrequency = CollectionUtil.substract(
					newValue.getJourneyFrequencies(), oldValue.getJourneyFrequencies(), JOURNEY_FREQUENCY_COMPARATOR);
			final Collection<String> objectIds = new ArrayList<String>();
			for (JourneyFrequency journeyFrequency : addedJourneyFrequency) {
				objectIds.add(journeyFrequency.getTimeband().getObjectId());
			}
			List<Timeband> timebands = null;
			for (JourneyFrequency item : addedJourneyFrequency) {
				JourneyFrequency journeyFrequency = new JourneyFrequency();
				Timeband timeband = cache.getTimebands().get(item.getTimeband().getObjectId());
				if (timeband == null) {
					if (timebands == null) {
						timebands = timebandDAO.findByObjectId(objectIds);
						for (Timeband object : timebands) {
							cache.getTimebands().put(object.getObjectId(), object);
						}
					}
					timeband = cache.getTimebands().get(item.getTimeband().getObjectId());
				}
				if (timeband != null) {
					journeyFrequency.setTimeband(timeband);
				}
				journeyFrequency.setVehicleJourney(oldValue);
			}

			Collection<Pair<JourneyFrequency, JourneyFrequency>> modifiedJourneyFrequency = CollectionUtil
					.intersection(oldValue.getJourneyFrequencies(), newValue.getJourneyFrequencies(),
							JOURNEY_FREQUENCY_COMPARATOR);
			for (Pair<JourneyFrequency, JourneyFrequency> pair : modifiedJourneyFrequency) {
				journeyFrequencyUpdater.update(context, pair.getLeft(), pair.getRight());
			}

			Collection<JourneyFrequency> removedJourneyFrequency = CollectionUtil.substract(
					oldValue.getJourneyFrequencies(), newValue.getJourneyFrequencies(), JOURNEY_FREQUENCY_COMPARATOR);
			for (JourneyFrequency journeyFrequency : removedJourneyFrequency) {
				journeyFrequency.setVehicleJourney(null);
				journeyFrequencyDAO.delete(journeyFrequency);
			}
		}
		// Footnotes
		// This is the new list of footnotes
		List<Footnote> footnotes = new ArrayList<Footnote>();
		
		// Compare at 'code' attribute
		Comparator<Footnote> footnoteCodeCompatator = new Comparator<Footnote>() {
			@Override
			public int compare(Footnote o1, Footnote o2) {
				return o2.getCode().compareTo(o1.getCode());
			}
		};
		
		// Find added footnotes
		Collection<Footnote> addedFootnotes = CollectionUtil.substract(
				newValue.getFootnotes(), oldValue.getFootnotes(),
				footnoteCodeCompatator);
		
		// add all new footnotes
		footnotes.addAll(addedFootnotes);
		
		// Find modified footnotes
		Collection<Pair<Footnote, Footnote>> modifiedFootnotes = CollectionUtil
				.intersection(oldValue.getFootnotes(),
						newValue.getFootnotes(),
						footnoteCodeCompatator);
		for (Pair<Footnote, Footnote> pair : modifiedFootnotes) {
			footnoteUpdater.update(context, pair.getLeft(), pair.getRight());
			footnotes.add(pair.getLeft());
		}
		
		for(Footnote f : footnotes) {
			Line line = cache.getLines().get(f.getLine().getObjectId());
			f.setLine(line);
		}
		
		oldValue.setFootnotes(footnotes);
//		monitor.stop();
	}
	
	
	
	/**
	 * Test 2-DATABASE-VehicleJourney-2
	 * @param validationReporter
	 * @param context
	 * @param oldCompany
	 * @param newCompany
	 */
	private void twoDatabaseVehicleJourneyTwoTest(ValidationReporter validationReporter, Context context, Company oldCompany,  Company newCompany, ValidationData data) {
		if(!NeptuneUtil.sameValue(oldCompany, newCompany))
			validationReporter.addCheckPointReportError(context, DATABASE_VEHICLE_JOURNEY_2, data.getDataLocations().get(newCompany.getObjectId()));
		else
			validationReporter.reportSuccess(context, DATABASE_VEHICLE_JOURNEY_2);
	}
}
