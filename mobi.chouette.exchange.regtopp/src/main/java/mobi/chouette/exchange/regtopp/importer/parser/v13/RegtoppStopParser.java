package mobi.chouette.exchange.regtopp.importer.parser.v13;

import static mobi.chouette.common.Constant.CONFIGURATION;
import static mobi.chouette.common.Constant.PARSER;
import static mobi.chouette.common.Constant.REFERENTIAL;

import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.parser.AbstractConverter;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.enums.StopType;
import mobi.chouette.exchange.regtopp.model.v13.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.v13.RegtoppStopPointSTP;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class RegtoppStopParser extends mobi.chouette.exchange.regtopp.importer.parser.v11.RegtoppStopParser {

	
	@Override
	public void parse(Context context) throws Exception {
		try {
			RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
			Referential referential = (Referential) context.get(REFERENTIAL);
			RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);
			String projection = configuration.getCoordinateProjection();

			Index<List<RegtoppStopPointSTP>> stopPointsByStopId = importer.getStopPointsByStopId();

			for (AbstractRegtoppStopHPL abstractStop : importer.getStopById()) {
				RegtoppStopHPL stop = (RegtoppStopHPL) abstractStop;
				if (stop.getType() == StopType.Stop) {
					String objectId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), StopArea.STOPAREA_KEY, stop.getStopId());
					
					StopArea stopArea = ObjectFactory.getStopArea(referential, objectId);
					stopArea.setName(stop.getFullName());
					stopArea.setRegistrationNumber(stop.getShortName());
					stopArea.setAreaType(ChouetteAreaEnum.StopPlace);

					convertAndSetCoordinates(stopArea, stop.getX(), stop.getY(), projection);

					List<RegtoppStopPointSTP> stopPoints = stopPointsByStopId.getValue(stop.getStopId());
					for (RegtoppStopPointSTP regtoppStopPoint : stopPoints) {
						String chouetteStopPointId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), StopArea.STOPAREA_KEY, regtoppStopPoint.getFullStopId());
						StopArea stopPoint = ObjectFactory.getStopArea(referential, chouetteStopPointId);

						convertAndSetCoordinates(stopPoint, regtoppStopPoint.getX(), regtoppStopPoint.getY(), projection);

						stopPoint.setName(regtoppStopPoint.getDescription());
						stopPoint.setRegistrationNumber(stopArea.getRegistrationNumber());
						stopPoint.setAreaType(ChouetteAreaEnum.BoardingPosition);

						stopPoint.setParent(stopArea);
					}

				} else {
					// TODO parse other node types (if really used, only Opplandstrafikk uses this)
					log.warn("Ignoring HPL stop of type Other: "+stop);
				}

			}
			
			
			
		} catch (Exception e) {
			log.error("Error parsing StopArea", e);
			throw e;
		}
	}


	
	static {
		ParserFactory.register(RegtoppStopParser.class.getName(), new ParserFactory() {
			@Override
			protected Parser create() {
				return new RegtoppStopParser();
			}
		});
	}



}
