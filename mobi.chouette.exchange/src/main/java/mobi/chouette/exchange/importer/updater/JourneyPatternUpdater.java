package mobi.chouette.exchange.importer.updater;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.CollectionUtils;
import mobi.chouette.common.Context;
import mobi.chouette.common.Pair;
import mobi.chouette.dao.StopPointDAO;
import mobi.chouette.dao.VehicleJourneyDAO;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;

@Log4j
@Stateless(name = JourneyPatternUpdater.BEAN_NAME)
public class JourneyPatternUpdater implements Updater<JourneyPattern> {

	public static final String BEAN_NAME = "JourneyPatternUpdater";

	@EJB
	private StopPointDAO stopPointDAO;

	@EJB
	private VehicleJourneyDAO vehicleJourneyDAO;

	@Override
	public void update(Context context, JourneyPattern oldValue,
			JourneyPattern newValue) throws Exception {

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
		if (newValue.getRegistrationNumber() != null
				&& !newValue.getRegistrationNumber().equals(
						oldValue.getRegistrationNumber())) {
			oldValue.setRegistrationNumber(newValue.getRegistrationNumber());
		}
		if (newValue.getPublishedName() != null
				&& !newValue.getPublishedName().equals(
						oldValue.getPublishedName())) {
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
				vehicleJourneyDAO.create(vehicleJourney);
			}
			vehicleJourney.setJourneyPattern(oldValue);
		}

		Updater<VehicleJourney> vehicleJourneyUpdater = UpdaterFactory.create(
				initialContext, VehicleJourneyUpdater.class.getName());
		Collection<Pair<VehicleJourney, VehicleJourney>> modifiedVehicleJourney = CollectionUtils
				.intersection(oldValue.getVehicleJourneys(),
						newValue.getVehicleJourneys(),
						NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<VehicleJourney, VehicleJourney> pair : modifiedVehicleJourney) {
			vehicleJourneyUpdater.update(null, pair.getLeft(), pair.getRight());
		}

		// Collection<VehicleJourney> removedVehicleJourney = CollectionUtils
		// .substract(oldValue.getVehicleJourneys(),
		// newValue.getVehicleJourneys(),
		// NeptuneIdentifiedObjectComparator.INSTANCE);
		// for (VehicleJourney vehicleJourney : removedVehicleJourney) {
		// vehicleJourney.setJourneyPattern(null);
		// vehicleJourneyDAO.delete(vehicleJourney);
		// }
	}

	static {
		UpdaterFactory.register(LineUpdater.class.getName(),
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
