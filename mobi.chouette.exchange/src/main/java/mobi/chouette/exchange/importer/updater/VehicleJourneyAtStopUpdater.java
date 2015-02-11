package mobi.chouette.exchange.importer.updater;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.dao.StopPointDAO;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourneyAtStop;

@Log4j
@Stateless(name = VehicleJourneyAtStopUpdater.BEAN_NAME)
public class VehicleJourneyAtStopUpdater implements
		Updater<VehicleJourneyAtStop> {

	public static final String BEAN_NAME = "VehicleJourneyAtStopUpdater";

	@EJB
	private StopPointDAO stopPointDAO;

	@Override
	public void update(Context context, VehicleJourneyAtStop oldValue,
			VehicleJourneyAtStop newValue) {

		if (newValue.getConnectingServiceId() != null
				&& !newValue.getConnectingServiceId().equals(
						oldValue.getConnectingServiceId())) {
			oldValue.setConnectingServiceId(newValue.getConnectingServiceId());
		}
		if (newValue.getBoardingAlightingPossibility() != null
				&& !newValue.getBoardingAlightingPossibility().equals(
						oldValue.getBoardingAlightingPossibility())) {
			oldValue.setBoardingAlightingPossibility(newValue
					.getBoardingAlightingPossibility());
		}
		if (newValue.getArrivalTime() != null
				&& !newValue.getArrivalTime().equals(oldValue.getArrivalTime())) {
			oldValue.setArrivalTime(newValue.getArrivalTime());
		}
		if (newValue.getDepartureTime() != null
				&& !newValue.getDepartureTime().equals(
						oldValue.getDepartureTime())) {
			oldValue.setDepartureTime(newValue.getDepartureTime());
		}
		if (newValue.getWaitingTime() != null
				&& !newValue.getWaitingTime().equals(oldValue.getWaitingTime())) {
			oldValue.setWaitingTime(newValue.getWaitingTime());
		}
		if (newValue.getElapseDuration() != null
				&& !newValue.getElapseDuration().equals(
						oldValue.getElapseDuration())) {
			oldValue.setElapseDuration(newValue.getElapseDuration());
		}
		if (newValue.getHeadwayFrequency() != null
				&& !newValue.getHeadwayFrequency().equals(
						oldValue.getHeadwayFrequency())) {
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
