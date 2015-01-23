package mobi.chouette.importer.updater;

import java.util.Collection;
import java.util.Comparator;

import javax.ejb.EJB;

import mobi.chouette.common.CollectionUtils;
import mobi.chouette.common.Pair;
import mobi.chouette.dao.CompanyDAO;
import mobi.chouette.dao.RouteDAO;
import mobi.chouette.dao.StopPointDAO;
import mobi.chouette.dao.TimetableDAO;
import mobi.chouette.dao.VehicleJourneyAtStopDAO;
import mobi.chouette.model.Company;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;

//@Log4j
public class VehicleJourneyUpdater implements Updater<VehicleJourney> {

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

	@Override
	public void update(VehicleJourney oldValue, VehicleJourney newValue)
			throws Exception {

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
		if (newValue.getComment() != null
				&& newValue.getComment().compareTo(oldValue.getComment()) != 0) {
			oldValue.setComment(newValue.getComment());
		}
		if (newValue.getServiceStatusValue() != null
				&& newValue.getServiceStatusValue().compareTo(
						oldValue.getServiceStatusValue()) != 0) {
			oldValue.setServiceStatusValue(newValue.getServiceStatusValue());
		}
		if (newValue.getTransportMode() != null
				&& newValue.getTransportMode().compareTo(
						oldValue.getTransportMode()) != 0) {
			oldValue.setTransportMode(newValue.getTransportMode());
		}
		if (newValue.getPublishedJourneyName() != null
				&& newValue.getPublishedJourneyName().compareTo(
						oldValue.getPublishedJourneyName()) != 0) {
			oldValue.setPublishedJourneyName(newValue.getPublishedJourneyName());
		}
		if (newValue.getPublishedJourneyIdentifier() != null
				&& newValue.getPublishedJourneyIdentifier().compareTo(
						oldValue.getPublishedJourneyIdentifier()) != 0) {
			oldValue.setPublishedJourneyIdentifier(newValue
					.getPublishedJourneyIdentifier());
		}
		if (newValue.getFacility() != null
				&& newValue.getFacility().compareTo(oldValue.getFacility()) != 0) {
			oldValue.setFacility(newValue.getFacility());
		}
		if (newValue.getVehicleTypeIdentifier() != null
				&& newValue.getVehicleTypeIdentifier().compareTo(
						oldValue.getVehicleTypeIdentifier()) != 0) {
			oldValue.setVehicleTypeIdentifier(newValue
					.getVehicleTypeIdentifier());
		}
		if (newValue.getNumber() != null
				&& newValue.getNumber().compareTo(oldValue.getNumber()) != 0) {
			oldValue.setNumber(newValue.getNumber());
		}

		if (newValue.getMobilityRestrictedSuitability() != null
				&& newValue.getMobilityRestrictedSuitability().compareTo(
						oldValue.getMobilityRestrictedSuitability()) != 0) {
			oldValue.setMobilityRestrictedSuitability(newValue
					.getMobilityRestrictedSuitability());
		}
		if (newValue.getFlexibleService() != null
				&& newValue.getFlexibleService().compareTo(
						oldValue.getFlexibleService()) != 0) {
			oldValue.setFlexibleService(newValue.getFlexibleService());
		}

		// Company
		if (oldValue.getCompany() == null
				|| !oldValue.getCompany().equals(newValue.getCompany())) {
			if (newValue.getCompany() == null) {
				oldValue.setCompany(null);
			} else {
				Company company = companyDAO.findByObjectId(newValue
						.getCompany().getObjectId());
				if (company != null) {
					oldValue.setCompany(company);
				}
			}
		}
		
		// Route
		if (oldValue.getRoute() == null
				|| !oldValue.getRoute().equals(newValue.getRoute())) {
			Route route = routeDAO.findByObjectId(newValue.getRoute()
					.getObjectId());
			if (route != null) {
				oldValue.setRoute(route);
			}
		}

		// VehicleJourneyAtStop
		Collection<VehicleJourneyAtStop> addedVehicleJourneyAtStop = CollectionUtils
				.substract(newValue.getVehicleJourneyAtStops(),
						oldValue.getVehicleJourneyAtStops(),
						VEHICLE_JOURNEY_AT_STOP_COMPARATOR);
		for (VehicleJourneyAtStop item : addedVehicleJourneyAtStop) {
			VehicleJourneyAtStop vehicleJourneyAtStop = new VehicleJourneyAtStop();
			StopPoint stopPoint = stopPointDAO.findByObjectId(item
					.getStopPoint().getObjectId());
			if (stopPoint != null) {
				vehicleJourneyAtStop.setStopPoint(stopPoint);
			}
			vehicleJourneyAtStop.setVehicleJourney(oldValue);
			vehicleJourneyAtStopDAO.create(vehicleJourneyAtStop);
		}

		Updater<VehicleJourneyAtStop> vehicleJourneyAtStopUpdater = UpdaterFactory
				.create(VehicleJourneyAtStopUpdater.class.getName());
		Collection<Pair<VehicleJourneyAtStop, VehicleJourneyAtStop>> modifiedVehicleJourneyAtStop = CollectionUtils
				.intersection(oldValue.getVehicleJourneyAtStops(),
						newValue.getVehicleJourneyAtStops(),
						VEHICLE_JOURNEY_AT_STOP_COMPARATOR);
		for (Pair<VehicleJourneyAtStop, VehicleJourneyAtStop> pair : modifiedVehicleJourneyAtStop) {
			vehicleJourneyAtStopUpdater.update(pair.getLeft(), pair.getRight());
		}

		// TODO remove ?
		Collection<VehicleJourneyAtStop> removedVehicleJourneyAtStop = CollectionUtils
				.substract(oldValue.getVehicleJourneyAtStops(),
						newValue.getVehicleJourneyAtStops(),
						VEHICLE_JOURNEY_AT_STOP_COMPARATOR);
		for (VehicleJourneyAtStop vehicleJourneyAtStop : removedVehicleJourneyAtStop) {
			vehicleJourneyAtStop.setVehicleJourney(null);
			vehicleJourneyAtStopDAO.delete(vehicleJourneyAtStop);
		}

		// Timetable
		Collection<Timetable> addedTimetable = CollectionUtils.substract(
				newValue.getTimetables(), oldValue.getTimetables(),
				NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Timetable item : addedTimetable) {
			Timetable timetable = timetableDAO.findByObjectId(item
					.getObjectId());
			if (timetable == null) {
				timetable = new Timetable();
				timetable.setObjectId(item.getObjectId());
				timetableDAO.create(timetable);
			}
			timetable.addVehicleJourney(oldValue);
		}

		Updater<Timetable> timetableUpdater = UpdaterFactory
				.create(TimetableUpdater.class.getName());
		Collection<Pair<Timetable, Timetable>> modifiedTimetable = CollectionUtils
				.intersection(oldValue.getTimetables(),
						newValue.getTimetables(),
						NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<Timetable, Timetable> pair : modifiedTimetable) {
			timetableUpdater.update(pair.getLeft(), pair.getRight());
		}

		// TODO remove ?
		Collection<Timetable> removedTimetable = CollectionUtils.substract(
				oldValue.getTimetables(), newValue.getTimetables(),
				NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Timetable timetable : removedTimetable) {
			timetable.removeVehicleJourney(oldValue);
		}

	}

	static {
		UpdaterFactory.register(VehicleJourneyUpdater.class.getName(),
				new UpdaterFactory() {
					private VehicleJourneyUpdater INSTANCE = new VehicleJourneyUpdater();

					@Override
					protected Updater<VehicleJourney> create() {
						return INSTANCE;
					}
				});
	}
}
