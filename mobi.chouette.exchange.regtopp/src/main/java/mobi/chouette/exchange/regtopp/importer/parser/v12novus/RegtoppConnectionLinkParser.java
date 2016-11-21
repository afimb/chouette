package mobi.chouette.exchange.regtopp.importer.parser.v12novus;

import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.importer.parser.ObjectIdCreator;

public class RegtoppConnectionLinkParser
		extends mobi.chouette.exchange.regtopp.importer.parser.v11.RegtoppConnectionLinkParser {

	@Override
	protected String createStopAreaIdForConnectionLink(RegtoppImportParameters configuration, String stopId) {
		// Regtopp 1.1D and 1.2 now using new parent stop structure with BOARDIN_POSITION_ID_SUFFIX appended. Not applicable for 1.2N and 1.3A
		return ObjectIdCreator.createStopAreaId(configuration,stopId);
	}

	static {
		ParserFactory.register(RegtoppConnectionLinkParser.class.getName(), new ParserFactory() {
			@Override
			protected Parser create() {
				return new RegtoppConnectionLinkParser();
			}
		});
	}

}
