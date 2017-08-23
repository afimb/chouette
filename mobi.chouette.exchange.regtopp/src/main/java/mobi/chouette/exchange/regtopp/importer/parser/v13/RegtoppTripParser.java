package mobi.chouette.exchange.regtopp.importer.parser.v13;

import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.importer.parser.v11.TransportModePair;
import mobi.chouette.exchange.regtopp.model.enums.TransportType;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.type.TransportSubModeNameEnum;

public class RegtoppTripParser extends mobi.chouette.exchange.regtopp.importer.parser.v12.RegtoppTripParser {

	@Override
	protected TransportModePair convertTypeOfService(TransportType typeOfService) {

		TransportModePair pair = new TransportModePair();
		switch(typeOfService) {
		case LocalBus:
			pair.transportMode = TransportModeNameEnum.Bus;
			pair.subMode = TransportSubModeNameEnum.LocalBus;
			break;
		case SchoolBus:
			pair.transportMode = TransportModeNameEnum.Bus;
			pair.subMode = TransportSubModeNameEnum.SchoolBus;
			break;
		case AirportExpressBus:
			pair.transportMode = TransportModeNameEnum.Bus;
			pair.subMode = TransportSubModeNameEnum.AirportLinkBus;
			break;
		case CarFerry:
			pair.transportMode = TransportModeNameEnum.Water;
			//pair.subMode = TransportSubModeNameEnum.LocalCarFerry; // TODO used for both passenger and car
			break;
		case ExpressBoat:
			pair.transportMode = TransportModeNameEnum.Water;
			pair.subMode = TransportSubModeNameEnum.HighSpeedPassengerService;
			break;
		case AirportExpressTrain:
			pair.transportMode = TransportModeNameEnum.Rail;
			pair.subMode = TransportSubModeNameEnum.AirportLinkRail;  
			break;
		default:
			pair = super.convertTypeOfService(typeOfService);
		}
		
		return pair;
	}
	


	static {
		ParserFactory.register(RegtoppTripParser.class.getName(), new ParserFactory() {
			@Override
			protected Parser create() {
				return new RegtoppTripParser();
			}
		});
	}

}
