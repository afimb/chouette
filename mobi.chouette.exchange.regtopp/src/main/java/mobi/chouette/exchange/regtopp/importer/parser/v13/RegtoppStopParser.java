package mobi.chouette.exchange.regtopp.importer.parser.v13;

import static mobi.chouette.common.Constant.CONFIGURATION;
import static mobi.chouette.common.Constant.PARSER;
import static mobi.chouette.common.Constant.REFERENTIAL;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.parser.ObjectIdCreator;
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
				if (shouldImportHPL(abstractStop) && stop.getType() == StopType.Stop
						) {
					String objectId = ObjectIdCreator.createStopAreaId(configuration, stop.getStopId());

					StopArea stopArea = ObjectFactory.getStopArea(referential, objectId);
					stopArea.setName(StringUtils.trimToNull(stop.getFullName()));
					// stopArea.setRegistrationNumber(stop.getShortName());
					stopArea.setAreaType(PARENT_STOP_PLACE_TYPE);

					convertAndSetCoordinates(stopArea, stop.getX(), stop.getY(), projection);

					List<RegtoppStopPointSTP> stopPoints = stopPointsByStopId.getValue(stop.getStopId());
					if (stopPoints != null) {
						for (RegtoppStopPointSTP regtoppStopPoint : stopPoints) {
							String chouetteStopPointId = ObjectIdCreator.createStopAreaId(configuration,
									regtoppStopPoint.getFullStopId());
							StopArea boardingPosition = ObjectFactory.getStopArea(referential, chouetteStopPointId);

							convertAndSetCoordinates(boardingPosition, regtoppStopPoint.getX(), regtoppStopPoint.getY(),
									projection);
							boardingPosition.setAreaType(ChouetteAreaEnum.BoardingPosition);
							boardingPosition
									.setRegistrationNumber(StringUtils.trimToNull(regtoppStopPoint.getStopPointName()));

							if (stopArea.getName() != null) {
								// Use parent stop area name
								boardingPosition.setName(stopArea.getName());
							} else if (StringUtils.trimToNull(regtoppStopPoint.getDescription()) != null) {
								// If parent is empty, use stop point
								// description on both stop point and stop area
								boardingPosition.setName(StringUtils.trimToNull(regtoppStopPoint.getDescription()));
								stopArea.setName(boardingPosition.getName());
							}

							// if
							// (StringUtils.trimToNull(regtoppStopPoint.getDescription())
							// == null) {
							// stopPoint.setName(stopArea.getName());
							// log.warn("StopPoint with no description, using
							// HPL stop name instead: " + regtoppStopPoint);
							// } else {
							// stopPoint.setName(regtoppStopPoint.getDescription());
							// }
							// stopPoint.setRegistrationNumber(stopArea.getRegistrationNumber());

							boardingPosition.setParent(stopArea);
						}
					}

					if (stopArea.getName() == null) {
						// Fallback, must have name
						stopArea.setName("Noname");
					}

				} else {
					// TODO parse other node types (if really used, only Ruter
					// uses this)
					log.warn("Ignoring HPL stop of type Other: " + stop);
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
