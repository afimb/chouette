package mobi.chouette.exchange.netexprofile.parser;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.util.NetexReferential;

import org.rutebanken.netex.model.Direction;
import org.rutebanken.netex.model.DirectionsInFrame_RelStructure;

public class DirectionParser implements Parser, Constant {

	@Override
	public void parse(Context context) throws Exception {
		NetexReferential netexReferential = (NetexReferential) context.get(Constant.NETEX_REFERENTIAL);

		DirectionsInFrame_RelStructure directionsInFrame_RelStructure = (DirectionsInFrame_RelStructure) context
				.get(NETEX_LINE_DATA_CONTEXT);

		for (Direction direction : directionsInFrame_RelStructure.getDirection()) {
			netexReferential.getDirectionTypes().putIfAbsent(direction.getId(), direction.getDirectionType());
		}
	}

	static {
		ParserFactory.register(DirectionParser.class.getName(), new ParserFactory() {
			private DirectionParser instance = new DirectionParser();

			@Override
			protected Parser create() {
				return instance;
			}
		});
	}
}
