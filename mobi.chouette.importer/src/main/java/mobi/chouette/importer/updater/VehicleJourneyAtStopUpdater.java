package mobi.chouette.importer.updater;

import javax.ejb.EJB;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.dao.StopPointDAO;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourneyAtStop;

@Log4j
public class VehicleJourneyAtStopUpdater implements
		Updater<VehicleJourneyAtStop> {
	@EJB
	private StopPointDAO stopPointDAO;

	@Override
	public void update(Context context, VehicleJourneyAtStop oldValue,
			VehicleJourneyAtStop newValue) {

		if (newValue.getConnectingServiceId() != null
				&& newValue.getConnectingServiceId().compareTo(
						oldValue.getConnectingServiceId()) != 0) {
			oldValue.setConnectingServiceId(newValue.getConnectingServiceId());
		}
		if (newValue.getBoardingAlightingPossibility() != null
				&& newValue.getBoardingAlightingPossibility().compareTo(
						oldValue.getBoardingAlightingPossibility()) != 0) {
			oldValue.setBoardingAlightingPossibility(newValue
					.getBoardingAlightingPossibility());
		}
		if (newValue.getArrivalTime() != null
				&& newValue.getArrivalTime().compareTo(
						oldValue.getArrivalTime()) != 0) {
			oldValue.setArrivalTime(newValue.getArrivalTime());
		}
		if (newValue.getDepartureTime() != null
				&& newValue.getDepartureTime().compareTo(
						oldValue.getDepartureTime()) != 0) {
			oldValue.setDepartureTime(newValue.getDepartureTime());
		}
		if (newValue.getWaitingTime() != null
				&& newValue.getWaitingTime().compareTo(
						oldValue.getWaitingTime()) != 0) {
			oldValue.setWaitingTime(newValue.getWaitingTime());
		}
		if (newValue.getElapseDuration() != null
				&& newValue.getElapseDuration().compareTo(
						oldValue.getElapseDuration()) != 0) {
			oldValue.setElapseDuration(newValue.getElapseDuration());
		}
		if (newValue.getHeadwayFrequency() != null
				&& newValue.getHeadwayFrequency().compareTo(
						oldValue.getHeadwayFrequency()) != 0) {
			oldValue.setHeadwayFrequency(newValue.getHeadwayFrequency());
		}

		// StopPoint
		if (oldValue.getStopPoint() == null
				|| !oldValue.getStopPoint().equals(newValue.getStopPoint())) {
			StopPoint stopPoint = stopPointDAO.findByObjectId(newValue
					.getStopPoint().getObjectId());
			if (stopPoint != null) {
				oldValue.setStopPoint(stopPoint);
			}
		}
	}

	static {
		UpdaterFactory.register(VehicleJourneyAtStopUpdater.class.getName(),
				new UpdaterFactory() {
					private VehicleJourneyAtStopUpdater INSTANCE = new VehicleJourneyAtStopUpdater();

					@Override
					protected Updater<VehicleJourneyAtStop> create() {
						return INSTANCE;
					}
				});
	}

}
