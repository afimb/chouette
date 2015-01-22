package mobi.chouette.importer.updater;

import java.util.Collection;

import javax.ejb.EJB;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.CollectionUtils;
import mobi.chouette.common.Pair;
import mobi.chouette.dao.StopPointDAO;
import mobi.chouette.dao.VehicleJourneyDAO;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;

@Log4j
public class JourneyPatternUpdater implements Updater<JourneyPattern> {

	@EJB
	private StopPointDAO stopPointDAO;

	@EJB
	private VehicleJourneyDAO vehicleJourneyDAO;

	@Override
	public void update(JourneyPattern oldValue, JourneyPattern newValue)
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
		if (newValue.getRegistrationNumber() != null
				&& newValue.getRegistrationNumber().compareTo(
						oldValue.getRegistrationNumber()) != 0) {
			oldValue.setRegistrationNumber(newValue.getRegistrationNumber());
		}
		if (newValue.getPublishedName() != null
				&& newValue.getPublishedName().compareTo(
						oldValue.getPublishedName()) != 0) {
			oldValue.setPublishedName(newValue.getPublishedName());
		}

		// StopPoint
		Collection<StopPoint> addedStopPoint = CollectionUtils.substract(
				newValue.getStopPoints(), oldValue.getStopPoints(),
				NeptuneIdentifiedObjectComparator.INSTANCE);
		for (StopPoint item : addedStopPoint) {
			StopPoint stopPoint = stopPointDAO.findByObjectId(item
					.getObjectId());
			if (stopPoint != null) {
				oldValue.addStopPoint(stopPoint);
			}
		}

		Collection<StopPoint> removedStopPoint = CollectionUtils.substract(
				oldValue.getStopPoints(), newValue.getStopPoints(),
				NeptuneIdentifiedObjectComparator.INSTANCE);
		for (StopPoint stopPoint : removedStopPoint) {
			oldValue.removeStopPoint(stopPoint);
		}

		// ArrivalStopPoint
		if (newValue.getArrivalStopPoint() == null) {
			oldValue.setArrivalStopPoint(null);
		} else if (!newValue.getArrivalStopPoint().equals(
				oldValue.getArrivalStopPoint())) {
			StopPoint stopPoint = stopPointDAO.findByObjectId(newValue
					.getArrivalStopPoint().getObjectId());
			if (stopPoint != null) {
				oldValue.setArrivalStopPoint(stopPoint);
			}
		}

		// DepartureStopPoint
		if (newValue.getDepartureStopPoint() == null) {
			oldValue.setDepartureStopPoint(null);
		} else if (!newValue.getDepartureStopPoint().equals(
				oldValue.getDepartureStopPoint())) {
			StopPoint stopPoint = stopPointDAO.findByObjectId(newValue
					.getDepartureStopPoint().getObjectId());
			if (stopPoint != null) {
				oldValue.setDepartureStopPoint(stopPoint);
			}
		}

		// VehicleJourney
		Collection<VehicleJourney> addedVehicleJourney = CollectionUtils
				.substract(newValue.getVehicleJourneys(),
						oldValue.getVehicleJourneys(),
						NeptuneIdentifiedObjectComparator.INSTANCE);
		for (VehicleJourney item : addedVehicleJourney) {
			VehicleJourney vehicleJourney = vehicleJourneyDAO
					.findByObjectId(item.getObjectId());
			if (vehicleJourney == null) {
				vehicleJourney = new VehicleJourney();
				vehicleJourney.setObjectId(item.getObjectId());
				vehicleJourney.setJourneyPattern(oldValue);
				vehicleJourneyDAO.create(vehicleJourney);
				log.debug("[DSU] create " + vehicleJourney.getObjectId());
			}
			vehicleJourney.setJourneyPattern(oldValue);
		}

		Updater<VehicleJourney> vehicleJourneyUpdater = UpdaterFactory
				.create(VehicleJourneyUpdater.class.getName());
		Collection<Pair<VehicleJourney, VehicleJourney>> modifiedVehicleJourney = CollectionUtils
				.intersection(oldValue.getVehicleJourneys(),
						newValue.getVehicleJourneys(),
						NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<VehicleJourney, VehicleJourney> pair : modifiedVehicleJourney) {
			vehicleJourneyUpdater.update(pair.getLeft(), pair.getRight());
			vehicleJourneyDAO.update(pair.getLeft());
			log.debug("[DSU] update " + pair.getLeft().getObjectId());
		}

		Collection<VehicleJourney> removedVehicleJourney = CollectionUtils
				.substract(oldValue.getVehicleJourneys(),
						newValue.getVehicleJourneys(),
						NeptuneIdentifiedObjectComparator.INSTANCE);
		for (VehicleJourney item : removedVehicleJourney) {
			item.setJourneyPattern(null);
			vehicleJourneyDAO.update(item);
			log.debug("[DSU] update " + item.getObjectId());
		}
	}

	static {
		UpdaterFactory.register(JourneyPatternUpdater.class.getName(),
				new UpdaterFactory() {
					private JourneyPatternUpdater INSTANCE = new JourneyPatternUpdater();

					@Override
					protected Updater<JourneyPattern> create() {
						return INSTANCE;
					}
				});
	}

}
