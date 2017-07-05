package mobi.chouette.exchange.netexprofile.parser;

import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.DestinationDisplay;
import org.rutebanken.netex.model.DestinationDisplayRefStructure;
import org.rutebanken.netex.model.DestinationDisplaysInFrame_RelStructure;
import org.rutebanken.netex.model.Via_VersionedChildStructure;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class DestinationDisplayParser implements Parser, Constant {

	@Override
	public void parse(Context context) throws Exception {
		Referential referential = (Referential) context.get(REFERENTIAL);
		DestinationDisplaysInFrame_RelStructure destinationDisplaysInFrameStruct = (DestinationDisplaysInFrame_RelStructure) context
				.get(NETEX_LINE_DATA_CONTEXT);

		for (DestinationDisplay netexDestinationDisplay : destinationDisplaysInFrameStruct.getDestinationDisplay()) {

			mobi.chouette.model.DestinationDisplay chouetteDestinationDisplay = ObjectFactory.getDestinationDisplay(referential,
					netexDestinationDisplay.getId());
			chouetteDestinationDisplay.setObjectVersion(NetexParserUtils.getVersion(netexDestinationDisplay));

			// TODO continue parsing

			if (StringUtils.trimToNull(netexDestinationDisplay.getName().getValue()) != null) {
				chouetteDestinationDisplay.setName(StringUtils.trimToNull(netexDestinationDisplay.getName().getValue()));
			}

			if (StringUtils.trimToNull(netexDestinationDisplay.getFrontText().getValue()) != null) {
				chouetteDestinationDisplay.setName(StringUtils.trimToNull(netexDestinationDisplay.getFrontText().getValue()));
			}

			if (StringUtils.trimToNull(netexDestinationDisplay.getSideText().getValue()) != null) {
				chouetteDestinationDisplay.setName(StringUtils.trimToNull(netexDestinationDisplay.getSideText().getValue()));
			}

			if (netexDestinationDisplay.getVias() != null && netexDestinationDisplay.getVias().getVia().size() > 0) {
				for (Via_VersionedChildStructure via : netexDestinationDisplay.getVias().getVia()) {
					DestinationDisplayRefStructure destinationDisplayRef = via.getDestinationDisplayRef();
					// Create referenced DestinationDisplay. Parent for loop is expected to populate values
					ObjectFactory.getDestinationDisplay(referential, destinationDisplayRef.getRef());
				}
			}

			chouetteDestinationDisplay.setFilled(true);
		}
	}

	static {
		ParserFactory.register(DestinationDisplayParser.class.getName(), new ParserFactory() {
			private DestinationDisplayParser instance = new DestinationDisplayParser();

			@Override
			protected Parser create() {
				return instance;
			}
		});
	}

}
