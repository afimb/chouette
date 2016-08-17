package mobi.chouette.exchange.regtopp.importer.parser.v11;

import static mobi.chouette.common.Constant.CONFIGURATION;
import static mobi.chouette.common.Constant.PARSER;
import static mobi.chouette.common.Constant.REFERENTIAL;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.parser.ObjectIdCreator;
import mobi.chouette.exchange.regtopp.importer.parser.LineSpecificParser;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppPathwayGAV;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class RegtoppConnectionLinkParser extends LineSpecificParser {

	
	/*
	 * Validation rules of type III are checked at this step.
	 */
	// TODO. Rename this function "translate(Context context)" or "produce(Context context)", ...
	@SuppressWarnings("deprecation")
	@Override
	public void parse(Context context) throws Exception {

		Referential referential = (Referential) context.get(REFERENTIAL);
		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
		RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);

		if (importer.hasGAVImporter()) {
			Index<AbstractRegtoppPathwayGAV> routeIndex = importer.getPathwayByIndexingKey();

			for (AbstractRegtoppPathwayGAV pathway : routeIndex) {

				String chouetteStartStopAreaObjectId = ObjectIdCreator.createStopAreaId(configuration,pathway.getStopIdFrom());

				String chouetteEndStopAreaObjectId =ObjectIdCreator.createStopAreaId(configuration,pathway.getStopIdTo());

				
//				if(!referential.getSharedStopAreas().containsKey(chouetteStartStopAreaObjectId)) {
//					log.error("StopArea (ConnectionLink start) "+chouetteStartStopAreaObjectId+" does not exist in shipment");
//					// TODO report with validation reporter
//				} else if(!referential.getSharedStopAreas().containsKey(chouetteEndStopAreaObjectId)) {
//					// TODO report with validation reporter
//					log.error("StopArea (ConnectionLink end) "+chouetteEndStopAreaObjectId+" does not exist in shipment");
//				} else {
					StopArea startStopArea = ObjectFactory.getStopArea(referential, chouetteStartStopAreaObjectId);
					StopArea endStopArea = ObjectFactory.getStopArea(referential, chouetteEndStopAreaObjectId);

					String chouetteConnectionLinkId = ObjectIdCreator.createConnectionLinkId(configuration,pathway.getStopIdFrom() ,pathway.getStopIdTo());
					ConnectionLink connectionLink = ObjectFactory.getConnectionLink(referential, chouetteConnectionLinkId);
					connectionLink.setName(pathway.getStopIdFrom()+" -> "+pathway.getStopIdTo());
					connectionLink.setDefaultDuration(new java.sql.Time(0, pathway.getDuration(), 0));
					connectionLink.setComment(pathway.getDescription());
					connectionLink.setStartOfLink(startStopArea);
					connectionLink.setEndOfLink(endStopArea);
					
//				}
			}
		}

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
