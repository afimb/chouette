package mobi.chouette.exchange.netexprofile.parser;

import org.rutebanken.netex.model.Interchange_VersionStructure;
import org.rutebanken.netex.model.JourneyInterchangesInFrame_RelStructure;
import org.rutebanken.netex.model.ServiceJourneyInterchange;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.TimeUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.ConversionUtil;
import mobi.chouette.model.Interchange;
import mobi.chouette.model.ScheduledStopPoint;
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
				VehicleJourney feederVehicleJourney = ObjectFactory.getVehicleJourney(referential,feederVehicleJourneyObjectId);
				interchange.setFeederVehicleJourney(feederVehicleJourney);
				feederVehicleJourney.getFeederInterchanges().add(interchange);

				String consumerVehicleJourneyObjectId = netexInterchange.getToJourneyRef().getRef();
				VehicleJourney consumerVehicleJourney = ObjectFactory.getVehicleJourney(referential,consumerVehicleJourneyObjectId);
				interchange.setConsumerVehicleJourney(consumerVehicleJourney);
				consumerVehicleJourney.getConsumerInterchanges().add(interchange);

				// Parse stop points
				ScheduledStopPoint feederScheduledStopPoint = ObjectFactory.getScheduledStopPoint(referential, netexInterchange.getFromPointRef().getRef());
				interchange.setFeederStopPoint(feederScheduledStopPoint);
				interchange.setFeederVisitNumber(ConversionUtil.asInteger(netexInterchange.getFromVisitNumber()));
				
				ScheduledStopPoint consumerScheduledStopPoint = ObjectFactory.getScheduledStopPoint(referential, netexInterchange.getToPointRef().getRef());
				interchange.setConsumerStopPoint(consumerScheduledStopPoint);
				interchange.setConsumerVisitNumber(ConversionUtil.asInteger(netexInterchange.getToVisitNumber()));


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
