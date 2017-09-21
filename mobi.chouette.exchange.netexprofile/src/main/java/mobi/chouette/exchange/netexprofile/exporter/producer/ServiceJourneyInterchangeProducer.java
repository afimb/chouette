package mobi.chouette.exchange.netexprofile.exporter.producer;

import org.rutebanken.netex.model.ScheduledStopPointRefStructure;
import org.rutebanken.netex.model.ServiceJourneyInterchange;
import org.rutebanken.netex.model.VehicleJourneyRefStructure;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.ConversionUtil;
import mobi.chouette.model.Interchange;
import mobi.chouette.model.ScheduledStopPoint;
import mobi.chouette.model.VehicleJourney;

public class ServiceJourneyInterchangeProducer extends NetexProducer implements NetexEntityProducer<ServiceJourneyInterchange, Interchange> {

    @Override
    public ServiceJourneyInterchange produce(Context context, Interchange interchange) {
        ServiceJourneyInterchange netex = netexFactory.createServiceJourneyInterchange();

        NetexProducerUtils.populateId(interchange, netex);
        netex.setName(ConversionUtil.getMultiLingualString(interchange.getName()));
        netex.setPriority(ConversionUtil.asBigInteger(interchange.getPriority()));
        netex.setStaySeated(interchange.getStaySeated());
        netex.setAdvertised(interchange.getAdvertised());
        netex.setPlanned(interchange.getPlanned());
        netex.setGuaranteed(interchange.getGuaranteed());

        
        // Consumer stoppoint ref 
        ScheduledStopPoint consumerStopPoint = interchange.getConsumerStopPoint();
        ScheduledStopPointRefStructure consumerSSPRef = netexFactory.createScheduledStopPointRefStructure();
        NetexProducerUtils.populateReference(consumerStopPoint, consumerSSPRef, true);
        netex.setToVisitNumber(ConversionUtil.asBigInteger(interchange.getConsumerVisitNumber()));
        netex.setToPointRef(consumerSSPRef);

        // Consumer vehicle journey ref
        VehicleJourney consumerVehicleJourney = interchange.getConsumerVehicleJourney();
        VehicleJourneyRefStructure consumerVehicleRef = netexFactory.createVehicleJourneyRefStructure();
        NetexProducerUtils.populateReference(consumerVehicleJourney, consumerVehicleRef, true);
        netex.setToJourneyRef(consumerVehicleRef);

        // Find if interchange is within same line - if so use version reference
        boolean interchangeWithinSameLine = false;
        VehicleJourney feederVehicleJourney = interchange.getFeederVehicleJourney();
		if(feederVehicleJourney != null) {
			if(consumerVehicleJourney != null) {
				// Check if same line - if so they will both exist in the same file
				if(consumerVehicleJourney.getRoute().getLine() == feederVehicleJourney.getRoute().getLine()) {
					interchangeWithinSameLine = true;
				}        
			}
		}
		
       // Feeder stoppoint ref
		ScheduledStopPoint feederStopPoint = interchange.getFeederStopPoint();
		ScheduledStopPointRefStructure feederSSPRef = netexFactory.createScheduledStopPointRefStructure();
		
		if(feederStopPoint != null) {
            NetexProducerUtils.populateReference(feederStopPoint, feederSSPRef, interchangeWithinSameLine);
        } else {
        	feederSSPRef.setRef(interchange.getFeederStopPointObjectid());
        }
        netex.setFromVisitNumber(ConversionUtil.asBigInteger(interchange.getFeederVisitNumber()));
        netex.setFromPointRef(feederSSPRef);

        // Feeder vehicle journey ref
        VehicleJourneyRefStructure feederVehicleRef = netexFactory.createVehicleJourneyRefStructure();
		if(feederVehicleJourney != null) {
			NetexProducerUtils.populateReference(feederVehicleJourney, feederVehicleRef, interchangeWithinSameLine);
        } else {
            feederVehicleRef.setRef(interchange.getFeederVehicleJourneyObjectid());
        }
        netex.setFromJourneyRef(feederVehicleRef);
        
        
        return netex;
    }
}
