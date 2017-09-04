package mobi.chouette.exchange.netexprofile.parser;

import java.util.Collection;

import org.rutebanken.netex.model.Interchange_VersionStructure;
import org.rutebanken.netex.model.JourneyInterchangesInFrame_RelStructure;
import org.rutebanken.netex.model.ServiceJourneyInterchange;
import org.rutebanken.netex.model.StopPointInJourneyPattern;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.TimeUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.ConversionUtil;
import mobi.chouette.exchange.netexprofile.util.NetexReferential;
import mobi.chouette.model.Interchange;
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

				interchange.setPriority(ConversionUtil.asInteger(netexInterchange.getPriority()));
				interchange.setStaySeated(netexInterchange.isStaySeated());
				interchange.setPlanned(netexInterchange.isPlanned());
				interchange.setAdvertised(netexInterchange.isAdvertised());
				interchange.setGuaranteed(netexInterchange.isGuaranteed());

				interchange.setMaximumWaitTime(TimeUtil.toJodaDuration(netexInterchange.getMaximumWaitTime()));
				interchange.setMinimumTransferTime(TimeUtil.toJodaDuration(netexInterchange.getMinimumTransferTime()));

				// Parse journeys
				String feederVehicleJourneyObjectId = netexInterchange.getFromJourneyRef().getRef();
				VehicleJourney feederVehicleJourney = referential.getVehicleJourneys().get(feederVehicleJourneyObjectId);
				if (feederVehicleJourney != null) {
					interchange.setFeederVehicleJourney(feederVehicleJourney);
					feederVehicleJourney.getFeederInterchanges().add(interchange);
				} else {
					interchange.setFeederVehicleJourneyObjectid(feederVehicleJourneyObjectId);
				}

				String consumerVehicleJourneyObjectId = netexInterchange.getToJourneyRef().getRef();
				VehicleJourney consumerVehicleJourney = referential.getVehicleJourneys().get(consumerVehicleJourneyObjectId);
				if (consumerVehicleJourney != null) {
					interchange.setConsumerVehicleJourney(consumerVehicleJourney);
					consumerVehicleJourney.getConsumerInterchanges().add(interchange);
				} else {
					interchange.setConsumerVehicleJourneyObjectid(consumerVehicleJourneyObjectId);
				}

				// Parse stop points

				interchange.setFeederVisitNumber(ConversionUtil.asInteger(netexInterchange.getFromVisitNumber()));
				String feederScheduledStopPointObjectId = netexInterchange.getFromPointRef().getRef();
				if (feederVehicleJourney != null && feederVehicleJourney.getRoute().getLine().equals(consumerVehicleJourney.getRoute().getLine())) { // Within
					// same
					// line
					interchange.setFeederStopPointObjectid(feederScheduledStopPointObjectId);

				} else {
					log.error("Interchange is broken across Lines as there is no concept of ScheduledStopPoint implemented yet");
					interchange.setFeederStopPointObjectid(feederScheduledStopPointObjectId);
				}

				interchange.setConsumerVisitNumber(ConversionUtil.asInteger(netexInterchange.getToVisitNumber()));
				String consumerScheduledStopPointObjectId = netexInterchange.getToPointRef().getRef();
				interchange.setConsumerStopPointObjectid(consumerScheduledStopPointObjectId);

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
