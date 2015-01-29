package mobi.chouette.exchange.importer.updater;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Comparator;

import javax.ejb.EJB;

import mobi.chouette.common.CollectionUtils;
import mobi.chouette.common.Constant;
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

//@Log4j
public class VehicleJourneyOptimizedUpdater implements Updater<VehicleJourney> {

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
	public void update(Context context, VehicleJourney oldValue,
			VehicleJourney newValue) throws Exception {

		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);

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
		if (newValue.getCompany() == null) {
			oldValue.setCompany(null);
		} else {
			Company company = companyDAO.findByObjectId(newValue.getCompany()
					.getObjectId());
			if (company == null) {
				company = new Company();
				company.setObjectId(newValue.getCompany().getObjectId());
				companyDAO.create(company);
			}
			Updater<Company> companyUpdater = UpdaterFactory
					.create(CompanyUpdater.class.getName());
			companyUpdater.update(null, oldValue.getCompany(),
					newValue.getCompany());
			oldValue.setCompany(company);
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
		ByteArrayOutputStream out = (ByteArrayOutputStream) context
				.get(Constant.BUFFER);
		if (out == null) {
			out = new ByteArrayOutputStream(1024);
			context.put(Constant.BUFFER, out);
		}
		BufferedWriter buffer = new BufferedWriter(new OutputStreamWriter(out));

		Collection<VehicleJourneyAtStop> addedVehicleJourneyAtStop = CollectionUtils
				.substract(newValue.getVehicleJourneyAtStops(),
						oldValue.getVehicleJourneyAtStops(),
						VEHICLE_JOURNEY_AT_STOP_COMPARATOR);
		for (VehicleJourneyAtStop item : addedVehicleJourneyAtStop) {
			StopPoint stopPoint = stopPointDAO.findByObjectId(item
					.getStopPoint().getObjectId());
			if (stopPoint != null) {
				write(buffer, oldValue, stopPoint, item);
			}
		}

		Collection<Pair<VehicleJourneyAtStop, VehicleJourneyAtStop>> modifiedVehicleJourneyAtStop = CollectionUtils
				.intersection(oldValue.getVehicleJourneyAtStops(),
						newValue.getVehicleJourneyAtStops(),
						VEHICLE_JOURNEY_AT_STOP_COMPARATOR);
		for (Pair<VehicleJourneyAtStop, VehicleJourneyAtStop> pair : modifiedVehicleJourneyAtStop) {
			write(buffer, oldValue, pair.getLeft().getStopPoint(),
					pair.getRight());
		}
		buffer.flush();

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
			timetableUpdater.update(null, pair.getLeft(), pair.getRight());
		}

		Collection<Timetable> removedTimetable = CollectionUtils.substract(
				oldValue.getTimetables(), newValue.getTimetables(),
				NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Timetable timetable : removedTimetable) {
			timetable.removeVehicleJourney(oldValue);
		}

	}

	private void write(BufferedWriter buffer, VehicleJourney vehicleJourney,
			StopPoint stopPoint, VehicleJourneyAtStop vehicleJourneyAtStop)
			throws IOException {
		buffer.write("" + vehicleJourney.getId());
		buffer.write(Constant.SEP);
		buffer.write("" + stopPoint.getId());
		buffer.write(Constant.SEP);
		buffer.write("" + vehicleJourneyAtStop.getConnectingServiceId());
		buffer.write(Constant.SEP);
		buffer.write(""
				+ vehicleJourneyAtStop.getBoardingAlightingPossibility());
		buffer.write(Constant.SEP);
		buffer.write("" + vehicleJourneyAtStop.getArrivalTime());
		buffer.write(Constant.SEP);
		buffer.write("" + vehicleJourneyAtStop.getDepartureTime());
		buffer.write(Constant.SEP);
		buffer.write("" + vehicleJourneyAtStop.getWaitingTime());
		buffer.write(Constant.SEP);
		buffer.write("" + vehicleJourneyAtStop.getElapseDuration());
		buffer.write(Constant.SEP);
		buffer.write("" + vehicleJourneyAtStop.getHeadwayFrequency());
		buffer.write('\n');
	}

	static {
		UpdaterFactory.register(VehicleJourneyOptimizedUpdater.class.getName(),
				new UpdaterFactory() {
					private VehicleJourneyOptimizedUpdater INSTANCE = new VehicleJourneyOptimizedUpdater();

					@Override
					protected Updater<VehicleJourney> create() {
						return INSTANCE;
					}
				});
	}
}
