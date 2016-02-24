package mobi.chouette.exchange.importer.updater;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import mobi.chouette.common.Context;
import mobi.chouette.dao.StopPointDAO;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourneyAtStop;

@Stateless(name = VehicleJourneyAtStopUpdater.BEAN_NAME)
public class VehicleJourneyAtStopUpdater implements
		Updater<VehicleJourneyAtStop> {

	public static final String BEAN_NAME = "VehicleJourneyAtStopUpdater";

	@EJB 
	private StopPointDAO stopPointDAO;

	
	@Override
	public void update(Context context, VehicleJourneyAtStop oldValue,
			VehicleJourneyAtStop newValue) { 
		// The list of fields to sunchronize with LineRegisterCommand.write(StringWriter buffer, VehicleJourney vehicleJourney, StopPoint stopPoint,
		//    VehicleJourneyAtStop vehicleJourneyAtStop)

		if (newValue.getArrivalTime() != null
				&& !newValue.getArrivalTime().equals(oldValue.getArrivalTime())) {
			oldValue.setArrivalTime(newValue.getArrivalTime());
		}
		if (newValue.getDepartureTime() != null
				&& !newValue.getDepartureTime().equals(
						oldValue.getDepartureTime())) {
			oldValue.setDepartureTime(newValue.getDepartureTime());
		}
		
		// if (newValue.getElapseDuration() != null
		// 		&& !newValue.getElapseDuration().equals(
		// 				oldValue.getElapseDuration())) {
		// 	oldValue.setElapseDuration(newValue.getElapseDuration());
		// }
		// if (newValue.getHeadwayFrequency() != null
		// 		&& !newValue.getHeadwayFrequency().equals(
		// 				oldValue.getHeadwayFrequency())) {
		// 	oldValue.setHeadwayFrequency(newValue.getHeadwayFrequency());
		// }

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

}
