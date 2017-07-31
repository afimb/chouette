package mobi.chouette.exchange.netexprofile.exporter.producer;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.getMLString;

import org.rutebanken.netex.model.ServiceJourneyInterchange;

import mobi.chouette.common.Context;
import mobi.chouette.model.Interchange;

public class ServiceJourneyInterchangeProducer extends NetexProducer implements NetexEntityProducer<ServiceJourneyInterchange, Interchange> {

    @Override
    public ServiceJourneyInterchange produce(Context context, Interchange interchange) {
        ServiceJourneyInterchange netex = netexFactory.createServiceJourneyInterchange();

        netex.setVersion(interchange.getObjectVersion() > 0 ? String.valueOf(interchange.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);

        netex.setId(interchange.getObjectId());

        
        netex.setName(getMLString(interchange.getName()));
        netex.setPriority(NetexProducerUtils.asBigInteger(interchange.getPriority()));
        netex.setStaySeated(interchange.getStaySeated());
        netex.setAdvertised(interchange.getAdvertised());
        netex.setPlanned(interchange.getPlanned());
        netex.setGuaranteed(interchange.getGuaranteed());

        // TODO must add version attribute if local to this file
        netex.setFromPointRef(netexFactory.createScheduledStopPointRefStructure().withRef(interchange.getFeederStopPointObjectid()));
        netex.setFromJourneyRef(netexFactory.createVehicleJourneyRefStructure().withRef(interchange.getFeederVehicleJourneyObjectid()));
        netex.setFromVisitNumber(NetexProducerUtils.asBigInteger(interchange.getFeederVisitNumber()));
        
        netex.setToPointRef(netexFactory.createScheduledStopPointRefStructure().withRef(interchange.getConsumerStopPointObjectid()));
        netex.setToJourneyRef(netexFactory.createVehicleJourneyRefStructure().withRef(interchange.getConsumerVehicleJourneyObjectid()));
        netex.setToVisitNumber(NetexProducerUtils.asBigInteger(interchange.getConsumerVisitNumber()));
        
        return netex;
    }
}
