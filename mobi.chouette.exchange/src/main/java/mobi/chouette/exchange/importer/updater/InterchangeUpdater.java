package mobi.chouette.exchange.importer.updater;

import javax.ejb.Stateless;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import mobi.chouette.common.Context;
import mobi.chouette.model.Interchange;

@Stateless(name = InterchangeUpdater.BEAN_NAME)
public class InterchangeUpdater implements Updater<Interchange> {

	public static final String BEAN_NAME = "InterchangeUpdater";

	@Override
	public void update(Context context, Interchange oldValue, Interchange newValue) {

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
		if (newValue.getPriority() != null
				&& !newValue.getPriority().equals(oldValue.getPriority())) {
			oldValue.setPriority(newValue.getPriority());
		}
		if (newValue.getPlanned() != null
				&& !newValue.getPlanned().equals(
						oldValue.getPlanned())) {
			oldValue.setPlanned(newValue.getPlanned());
		}
		if (newValue.getGuaranteed() != null
				&& !newValue.getGuaranteed().equals(
						oldValue.getGuaranteed())) {
			oldValue.setGuaranteed(newValue
					.getGuaranteed());
		}
		if (newValue.getAdvertised() != null
				&& !newValue.getAdvertised().equals(oldValue.getAdvertised())) {
			oldValue.setAdvertised(newValue.getAdvertised());
		}
		if (newValue.getStaySeated() != null
				&& !newValue.getStaySeated().equals(oldValue.getStaySeated())) {
			oldValue.setStaySeated(newValue.getStaySeated());
		}
		if (newValue.getMaximumWaitTime() != null
				&& !newValue.getMaximumWaitTime().equals(oldValue.getMaximumWaitTime())) {
			oldValue.setMaximumWaitTime(newValue.getMaximumWaitTime());
		}
		if (newValue.getMinimumTransferTime() != null
				&& !newValue.getMinimumTransferTime().equals(oldValue.getMinimumTransferTime())) {
			oldValue.setMinimumTransferTime(newValue.getMinimumTransferTime());
		}
		// Feeder vehicle journey
		if (newValue.getFeederVehicleJourney() != null
				&& !newValue.getFeederVehicleJourney().equals(oldValue.getFeederVehicleJourney())) {
			oldValue.setFeederVehicleJourney(newValue.getFeederVehicleJourney());
		}
		if (newValue.getFeederVehicleJourneyObjectid() != null
				&& !newValue.getFeederVehicleJourneyObjectid().equals(oldValue.getFeederVehicleJourneyObjectid())) {
			oldValue.setFeederVehicleJourneyObjectid(newValue.getFeederVehicleJourneyObjectid());
		}

		// Consumer vehicle journey
		if (newValue.getConsumerVehicleJourney() != null
				&& !newValue.getConsumerVehicleJourney().equals(oldValue.getConsumerVehicleJourney())) {
			oldValue.setConsumerVehicleJourney(newValue.getConsumerVehicleJourney());
		}
		if (newValue.getConsumerVehicleJourneyObjectid() != null
				&& !newValue.getConsumerVehicleJourneyObjectid().equals(oldValue.getConsumerVehicleJourneyObjectid())) {
			oldValue.setConsumerVehicleJourneyObjectid(newValue.getConsumerVehicleJourneyObjectid());
		}

		// Feeder stoppoint
		if (newValue.getFeederStopPoint() != null
				&& !newValue.getFeederStopPoint().equals(
						oldValue.getFeederStopPoint())) {
			oldValue.setFeederStopPoint(newValue.getFeederStopPoint());
		}
		if (newValue.getFeederStopPointObjectid() != null
				&& !newValue.getFeederStopPointObjectid().equals(
						oldValue.getFeederStopPointObjectid())) {
			oldValue.setFeederStopPointObjectid(newValue.getFeederStopPointObjectid());
		}
		if (newValue.getFeederVisitNumber() != null
				&& !newValue.getFeederVisitNumber().equals(
						oldValue.getFeederVisitNumber())) {
			oldValue.setFeederVisitNumber(newValue.getFeederVisitNumber());
		}
		
		// COnsumer stoppoint
		if (newValue.getConsumerStopPoint() != null
				&& !newValue.getConsumerStopPoint().equals(oldValue.getConsumerStopPoint())) {
			oldValue.setConsumerStopPoint(newValue.getConsumerStopPoint());
		}
		if (newValue.getConsumerStopPointObjectid() != null
				&& !newValue.getConsumerStopPointObjectid().equals(oldValue.getConsumerStopPointObjectid())) {
			oldValue.setConsumerStopPointObjectid(newValue.getConsumerStopPointObjectid());
		}
		if (newValue.getConsumerVisitNumber() != null
				&& !newValue.getConsumerVisitNumber().equals(oldValue.getConsumerVisitNumber())) {
			oldValue.setConsumerVisitNumber(newValue.getConsumerVisitNumber());
		}
	}

}
