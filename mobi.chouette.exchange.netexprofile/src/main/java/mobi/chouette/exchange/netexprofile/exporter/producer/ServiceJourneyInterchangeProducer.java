package mobi.chouette.exchange.netexprofile.exporter.producer;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.netexId;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.SCHEDULED_STOP_POINT;

import org.rutebanken.netex.model.ServiceJourneyInterchange;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.ConversionUtil;
import mobi.chouette.model.Interchange;
import mobi.chouette.model.ScheduledStopPoint;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;

public class ServiceJourneyInterchangeProducer extends NetexProducer implements NetexEntityProducer<ServiceJourneyInterchange, Interchange> {

    @Override
    public ServiceJourneyInterchange produce(Context context, Interchange interchange) {
        ServiceJourneyInterchange netex = netexFactory.createServiceJourneyInterchange();

        netex.setVersion(interchange.getObjectVersion() > 0 ? String.valueOf(interchange.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);

        netex.setId(interchange.getObjectId());

        
        netex.setName(ConversionUtil.getMLString(interchange.getName()));
        netex.setPriority(ConversionUtil.asBigInteger(interchange.getPriority()));
        netex.setStaySeated(interchange.getStaySeated());
        netex.setAdvertised(interchange.getAdvertised());
        netex.setPlanned(interchange.getPlanned());
        netex.setGuaranteed(interchange.getGuaranteed());

        
        // Consumer stoppoint ref 
        ScheduledStopPoint consumerStopPoint = interchange.getConsumerStopPoint();
		if(consumerStopPoint != null) {
            String stopPointIdSuffix = consumerStopPoint.getContainedInStopArea().objectIdSuffix();
            String stopPointIdRef = netexId(consumerStopPoint.objectIdPrefix(), SCHEDULED_STOP_POINT, stopPointIdSuffix);
            netex.setToPointRef(netexFactory.createScheduledStopPointRefStructure().withRef(stopPointIdRef));
        } else {
            netex.setToPointRef(netexFactory.createScheduledStopPointRefStructure().withRef(interchange.getConsumerStopPointObjectid()));
        }

        netex.setToVisitNumber(ConversionUtil.asBigInteger(interchange.getConsumerVisitNumber()));

        // Consumer vehicle journey ref
        VehicleJourney consumerVehicleJourney = interchange.getConsumerVehicleJourney();
		if(consumerVehicleJourney != null) {
            String consumerVehicleJourneyVersion = consumerVehicleJourney.getObjectVersion() > 0 ? String.valueOf(consumerVehicleJourney.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION;
            netex.setToJourneyRef(netexFactory.createVehicleJourneyRefStructure().withRef(consumerVehicleJourney.getObjectId()).withVersion(consumerVehicleJourneyVersion));
        } else {
            netex.setToJourneyRef(netexFactory.createVehicleJourneyRefStructure().withRef(interchange.getConsumerVehicleJourneyObjectid()));
        }


        
       // Feeder stoppoint ref
		ScheduledStopPoint feederStopPoint = interchange.getFeederStopPoint();
		if(feederStopPoint != null) {
            String stopPointIdSuffix = feederStopPoint.getContainedInStopArea().objectIdSuffix();
            String stopPointIdRef = netexId(feederStopPoint.objectIdPrefix(), SCHEDULED_STOP_POINT, stopPointIdSuffix);
            netex.setFromPointRef(netexFactory.createScheduledStopPointRefStructure().withRef(stopPointIdRef));
        } else {
            netex.setFromPointRef(netexFactory.createScheduledStopPointRefStructure().withRef(interchange.getFeederStopPointObjectid()));
        }
        netex.setFromVisitNumber(ConversionUtil.asBigInteger(interchange.getFeederVisitNumber()));

        // Feeder vehicle journey ref
		String feederVehicleJourneyVersion = null;
        VehicleJourney feederVehicleJourney = interchange.getFeederVehicleJourney();
		if(feederVehicleJourney != null) {
			if(consumerVehicleJourney != null) {
				// Check if same line - if so they will both exist in the same file
				if(consumerVehicleJourney.getRoute().getLine() == feederVehicleJourney.getRoute().getLine()) {
					feederVehicleJourneyVersion = feederVehicleJourney.getObjectVersion() > 0 ? String.valueOf(feederVehicleJourney.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION;
				}
			}
            netex.setFromJourneyRef(netexFactory.createVehicleJourneyRefStructure().withRef(feederVehicleJourney.getObjectId()).withVersion(feederVehicleJourneyVersion));
        } else {
            netex.setFromJourneyRef(netexFactory.createVehicleJourneyRefStructure().withRef(interchange.getFeederVehicleJourneyObjectid()));
        }
        
        
        return netex;
    }
}
