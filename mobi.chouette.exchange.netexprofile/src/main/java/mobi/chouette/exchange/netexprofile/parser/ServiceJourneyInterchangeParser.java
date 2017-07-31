package mobi.chouette.exchange.netexprofile.parser;

import org.rutebanken.netex.model.Interchange_VersionStructure;
import org.rutebanken.netex.model.JourneyInterchangesInFrame_RelStructure;
import org.rutebanken.netex.model.ServiceJourneyInterchange;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils;
import mobi.chouette.model.Interchange;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class ServiceJourneyInterchangeParser implements Parser, Constant {

	@Override
	public void parse(Context context) throws Exception {
		Referential referential = (Referential) context.get(REFERENTIAL);
		JourneyInterchangesInFrame_RelStructure organisationsInFrameStruct = (JourneyInterchangesInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);

		for (Interchange_VersionStructure interchangeVersionStructure : organisationsInFrameStruct
				.getServiceJourneyPatternInterchangeOrServiceJourneyInterchange()) {
			if (interchangeVersionStructure instanceof ServiceJourneyInterchange) {
				ServiceJourneyInterchange netexInterchange = (ServiceJourneyInterchange) interchangeVersionStructure;

				Interchange interchange = ObjectFactory.getInterchange(referential, netexInterchange.getId());
				interchange.setObjectVersion(NetexParserUtils.getVersion(netexInterchange));
				if (netexInterchange.getName() != null) {
					interchange.setName(netexInterchange.getName().getValue());
				}

				interchange.setPriority(NetexProducerUtils.asInteger(netexInterchange.getPriority()));
				interchange.setStaySeated(netexInterchange.isStaySeated());
				interchange.setPlanned(netexInterchange.isPlanned());
				interchange.setAdvertised(netexInterchange.isAdvertised());
				interchange.setGuaranteed(netexInterchange.isGuaranteed());

				interchange.setMaximumWaitTime(NetexProducerUtils.asTime(netexInterchange.getMaximumWaitTime()));
				interchange.setMinimumTransferTime(NetexProducerUtils.asTime(netexInterchange.getMinimumTransferTime()));

				String feederScheduledStopPointObjectId = netexInterchange.getFromPointRef().getRef();
				StopPoint feederStopPoint = referential.getStopPoints().get(feederScheduledStopPointObjectId);
				if (feederStopPoint != null) {
					interchange.setFeederStopPoint(feederStopPoint);
				} else {
					interchange.setFeederStopPointObjectid(feederScheduledStopPointObjectId);
				}

				String consumerScheduledStopPointObjectId = netexInterchange.getToPointRef().getRef();
				StopPoint consumerStopPoint = referential.getStopPoints().get(consumerScheduledStopPointObjectId);
				if (consumerStopPoint != null) {
					interchange.setFeederStopPoint(consumerStopPoint);
				} else {
					interchange.setFeederStopPointObjectid(consumerScheduledStopPointObjectId);
				}

				String feederVehicleJourneyObjectId = netexInterchange.getFromJourneyRef().getRef();
				VehicleJourney feederVehicleJourney = referential.getVehicleJourneys().get(feederVehicleJourneyObjectId);
				if (feederVehicleJourney != null) {
					interchange.setFeederVehicleJourney(feederVehicleJourney);
					feederVehicleJourney.getFeederInterchanges().add(interchange);
				} else {
					interchange.setFeederVehicleJourneyObjectid(feederVehicleJourneyObjectId);
				}

				String consumerVehicleJourneyObjectId = netexInterchange.getFromJourneyRef().getRef();
				VehicleJourney consumerVehicleJourney = referential.getVehicleJourneys().get(consumerVehicleJourneyObjectId);
				if (consumerVehicleJourney != null) {
					interchange.setConsumerVehicleJourney(consumerVehicleJourney);
					consumerVehicleJourney.getConsumerInterchanges().add(interchange);
				} else {
					interchange.setConsumerVehicleJourneyObjectid(consumerVehicleJourneyObjectId);
				}

			}
		}
	}


	static {
		ParserFactory.register(ServiceJourneyInterchangeParser.class.getName(), new ParserFactory() {
			private ServiceJourneyInterchangeParser instance = new ServiceJourneyInterchangeParser();

			@Override
			protected Parser create() {
				return instance;
			}
		});
	}

}
