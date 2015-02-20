package mobi.chouette.exchange.importer.updater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.CollectionUtils;
import mobi.chouette.common.Context;
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
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
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

	@EJB
	private CompanyDAO companyDAO;

	@EJB(beanName = CompanyUpdater.BEAN_NAME)
	private Updater<Company> companyUpdater;

	@EJB
	private RouteDAO routeDAO;

	@EJB
	private StopPointDAO stopPointDAO;

	@EJB
	private VehicleJourneyAtStopDAO vehicleJourneyAtStopDAO;

	@EJB
	private TimetableDAO timetableDAO;

	@EJB(beanName = TimetableUpdater.BEAN_NAME)
	private Updater<Timetable> timetableUpdater;

	@EJB(beanName = VehicleJourneyAtStopUpdater.BEAN_NAME)
	private Updater<VehicleJourneyAtStop> vehicleJourneyAtStopUpdater;

	@Override
	public void update(Context context, VehicleJourney oldValue,
			VehicleJourney newValue) throws Exception {

		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);

		Referential cache = (Referential) context.get(CACHE);
		cache.getVehicleJourneys().put(oldValue.getObjectId(), oldValue);

		boolean optimized = (Boolean) context.get(OPTIMIZED);

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
		if (newValue.getComment() != null
				&& !newValue.getComment().equals(oldValue.getComment())) {
			oldValue.setComment(newValue.getComment());
		}
		if (newValue.getServiceStatusValue() != null
				&& !newValue.getServiceStatusValue().equals(
						oldValue.getServiceStatusValue())) {
			oldValue.setServiceStatusValue(newValue.getServiceStatusValue());
		}
		if (newValue.getTransportMode() != null
				&& !newValue.getTransportMode().equals(
						oldValue.getTransportMode())) {
			oldValue.setTransportMode(newValue.getTransportMode());
		}
		if (newValue.getPublishedJourneyName() != null
				&& !newValue.getPublishedJourneyName().equals(
						oldValue.getPublishedJourneyName())) {
			oldValue.setPublishedJourneyName(newValue.getPublishedJourneyName());
		}
		if (newValue.getPublishedJourneyIdentifier() != null
				&& !newValue.getPublishedJourneyIdentifier().equals(
						oldValue.getPublishedJourneyIdentifier())) {
			oldValue.setPublishedJourneyIdentifier(newValue
					.getPublishedJourneyIdentifier());
		}
		if (newValue.getFacility() != null
				&& !newValue.getFacility().equals(oldValue.getFacility())) {
			oldValue.setFacility(newValue.getFacility());
		}
		if (newValue.getVehicleTypeIdentifier() != null
				&& !newValue.getVehicleTypeIdentifier().equals(
						oldValue.getVehicleTypeIdentifier())) {
			oldValue.setVehicleTypeIdentifier(newValue
					.getVehicleTypeIdentifier());
		}
		if (newValue.getNumber() != null
				&& !newValue.getNumber().equals(oldValue.getNumber())) {
			oldValue.setNumber(newValue.getNumber());
		}

		if (newValue.getMobilityRestrictedSuitability() != null
				&& !newValue.getMobilityRestrictedSuitability().equals(
						oldValue.getMobilityRestrictedSuitability())) {
			oldValue.setMobilityRestrictedSuitability(newValue
					.getMobilityRestrictedSuitability());
		}
		if (newValue.getFlexibleService() != null
				&& !newValue.getFlexibleService().equals(
						oldValue.getFlexibleService())) {
			oldValue.setFlexibleService(newValue.getFlexibleService());
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
			companyUpdater.update(context, oldValue.getCompany(),
					newValue.getCompany());
		}

		// Route
		if (oldValue.getRoute() == null
				|| !oldValue.getRoute().equals(newValue.getRoute())) {

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

			Collection<VehicleJourneyAtStop> addedVehicleJourneyAtStop = CollectionUtils
					.substract(newValue.getVehicleJourneyAtStops(),
							oldValue.getVehicleJourneyAtStops(),
							VEHICLE_JOURNEY_AT_STOP_COMPARATOR);

			final Collection<String> objectIds = new ArrayList<String>();
			for (VehicleJourneyAtStop vehicleJourneyAtStop : addedVehicleJourneyAtStop) {
				objectIds
						.add(vehicleJourneyAtStop.getStopPoint().getObjectId());
			}
			List<StopPoint> stopPoints = null;
			for (VehicleJourneyAtStop item : addedVehicleJourneyAtStop) {
				VehicleJourneyAtStop vehicleJourneyAtStop = ObjectFactory
						.getVehicleJourneyAtStop();

				StopPoint stopPoint = cache.getStopPoints().get(
						item.getStopPoint().getObjectId());
				if (stopPoint == null) {
					if (stopPoints == null) {
						stopPoints = stopPointDAO.findByObjectId(objectIds);
						for (StopPoint object : stopPoints) {
							cache.getStopPoints().put(object.getObjectId(),
									object);
						}
					}
					stopPoint = cache.getStopPoints().get(
							item.getStopPoint().getObjectId());
				}

				if (stopPoint != null) {
					vehicleJourneyAtStop.setStopPoint(stopPoint);
				}
				vehicleJourneyAtStop.setVehicleJourney(oldValue);
			}

			Collection<Pair<VehicleJourneyAtStop, VehicleJourneyAtStop>> modifiedVehicleJourneyAtStop = CollectionUtils
					.intersection(oldValue.getVehicleJourneyAtStops(),
							newValue.getVehicleJourneyAtStops(),
							VEHICLE_JOURNEY_AT_STOP_COMPARATOR);
			for (Pair<VehicleJourneyAtStop, VehicleJourneyAtStop> pair : modifiedVehicleJourneyAtStop) {
				vehicleJourneyAtStopUpdater.update(context, pair.getLeft(),
						pair.getRight());
			}

			Collection<VehicleJourneyAtStop> removedVehicleJourneyAtStop = CollectionUtils
					.substract(oldValue.getVehicleJourneyAtStops(),
							newValue.getVehicleJourneyAtStops(),
							VEHICLE_JOURNEY_AT_STOP_COMPARATOR);
			for (VehicleJourneyAtStop vehicleJourneyAtStop : removedVehicleJourneyAtStop) {
				vehicleJourneyAtStop.setVehicleJourney(null);
				vehicleJourneyAtStopDAO.delete(vehicleJourneyAtStop);
			}
		}

		// Timetable
		Collection<Timetable> addedTimetable = CollectionUtils.substract(
				newValue.getTimetables(), oldValue.getTimetables(),
				NeptuneIdentifiedObjectComparator.INSTANCE);

		List<Timetable> timetables = null;
		for (Timetable item : addedTimetable) {

			Timetable timetable = cache.getTimetables().get(item.getObjectId());
			if (timetable == null) {
				if (timetables == null) {
					timetables = timetableDAO.findByObjectId(UpdaterUtils
							.getObjectIds(addedTimetable));
					for (Timetable object : timetables) {
						cache.getTimetables().put(object.getObjectId(), object);
					}
				}
				timetable = cache.getTimetables().get(item.getObjectId());
			}

			if (timetable == null) {
				timetable = ObjectFactory.getTimetable(cache,
						item.getObjectId());
			}
			timetable.addVehicleJourney(oldValue);
		}

		Collection<Pair<Timetable, Timetable>> modifiedTimetable = CollectionUtils
				.intersection(oldValue.getTimetables(),
						newValue.getTimetables(),
						NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<Timetable, Timetable> pair : modifiedTimetable) {
			timetableUpdater.update(context, pair.getLeft(), pair.getRight());
		}

		Collection<Timetable> removedTimetable = CollectionUtils.substract(
				oldValue.getTimetables(), newValue.getTimetables(),
				NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Timetable timetable : removedTimetable) {
			timetable.removeVehicleJourney(oldValue);
		}

	}
}
