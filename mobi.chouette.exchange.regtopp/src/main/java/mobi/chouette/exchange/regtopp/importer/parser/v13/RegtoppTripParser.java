package mobi.chouette.exchange.regtopp.importer.parser.v13;

import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.model.enums.TransportType;
import mobi.chouette.model.type.TransportModeNameEnum;

public class RegtoppTripParser extends mobi.chouette.exchange.regtopp.importer.parser.v12.RegtoppTripParser {

	@Override
	protected TransportModeNameEnum convertTypeOfService(TransportType typeOfService) {
		switch(typeOfService) {
		case LocalBus:
			return TransportModeNameEnum.Bus;
		case SchoolBus:
			return TransportModeNameEnum.Bus;
		case AirportExpressBus:
			return TransportModeNameEnum.Coach;
		case CarFerry:
			return TransportModeNameEnum.Ferry;
		case ExpressBoat:
			return TransportModeNameEnum.Waterborne;
		case AirportExpressTrain:
			return TransportModeNameEnum.Train;
		case SchoolBusRuter:
			return TransportModeNameEnum.Bus;
		case FlexibleBusRuter:
			return TransportModeNameEnum.Bus;
		case NightBusRuter:
			return TransportModeNameEnum.Bus;
		default:
			return super.convertTypeOfService(typeOfService);
		}
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
