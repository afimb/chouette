package mobi.chouette.exchange.regtopp.importer.parser.v11;

import static mobi.chouette.common.Constant.CONFIGURATION;
import static mobi.chouette.common.Constant.PARSER;
import static mobi.chouette.common.Constant.REFERENTIAL;

import java.math.BigDecimal;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.parser.AbstractConverter;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppStopHPL;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.LongLatTypeEnum;
import mobi.chouette.model.util.Coordinate;
import mobi.chouette.model.util.CoordinateUtil;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class RegtoppStopParser implements Parser {

	@Override
	public void parse(Context context) throws Exception {
		try {

			RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
			Referential referential = (Referential) context.get(REFERENTIAL);
			RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);
			String projection = configuration.getCoordinateProjection();
			
			for (AbstractRegtoppStopHPL stop : importer.getStopById()) {
				String objectId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), StopArea.STOPAREA_KEY, stop.getFullStopId());

				StopArea stopArea = ObjectFactory.getStopArea(referential, objectId);
				stopArea.setName(stop.getFullName());
				stopArea.setRegistrationNumber(stop.getShortName());
				stopArea.setAreaType(ChouetteAreaEnum.BoardingPosition);

				convertAndSetCoordinates(stopArea, stop.getX(), stop.getY(), projection);
			}
		} catch (Exception e) {
			log.error("Error parsing StopArea", e);
			throw e;
		}
	}
	
	protected void convertAndSetCoordinates(StopArea stopArea, BigDecimal x, BigDecimal y, String projection) {
		Coordinate wgs84Coordinate = CoordinateUtil.transform(projection, Coordinate.WGS84,
				new Coordinate(x, y));

		stopArea.setLongitude(wgs84Coordinate.getY());
		stopArea.setLatitude(wgs84Coordinate.getX());
		stopArea.setLongLatType(LongLatTypeEnum.WGS84);

		// UTM coordinates
		stopArea.setX(x);
		stopArea.setY(y);
		stopArea.setProjectionType(projection);
		
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
