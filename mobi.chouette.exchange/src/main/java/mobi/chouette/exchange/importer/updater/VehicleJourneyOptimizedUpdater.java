package mobi.chouette.exchange.importer.updater;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Comparator;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
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

@Log4j
@Stateless(name = VehicleJourneyOptimizedUpdater.BEAN_NAME)
public class VehicleJourneyOptimizedUpdater implements Updater<VehicleJourney> {

	public static final String BEAN_NAME = "VehicleJourneyOptimizedUpdater";

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

		InitialContext initialContext = (InitialContext) context
				.get(INITIAL_CONTEXT);

		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);

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
			Company company = companyDAO.findByObjectId(newValue.getCompany()
					.getObjectId());
			if (company == null) {
				company = new Company();
				company.setObjectId(newValue.getCompany().getObjectId());
				//companyDAO.create(company);
			}
			oldValue.setCompany(company);
			Updater<Company> companyUpdater = UpdaterFactory.create(
					initialContext, CompanyUpdater.class.getName());
			companyUpdater.update(context, oldValue.getCompany(),
					newValue.getCompany());		
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
				// timetableDAO.create(timetable);
			}
			timetable.addVehicleJourney(oldValue);
		}

		Updater<Timetable> timetableUpdater = UpdaterFactory.create(
				initialContext, TimetableUpdater.class.getName());
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

					@Override
					protected <T> Updater<T> create(InitialContext context) {
						Updater result = null;
						try {
							result = (Updater) context
									.lookup("java:app/mobi.chouette.exchange/"
											+ BEAN_NAME);
						} catch (NamingException e) {
							log.error(e);
						}
						return result;
					}
				});
	}
}
