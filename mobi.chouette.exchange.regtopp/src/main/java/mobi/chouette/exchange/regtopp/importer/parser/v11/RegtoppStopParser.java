package mobi.chouette.exchange.regtopp.importer.parser.v11;

import static mobi.chouette.common.Constant.CONFIGURATION;
import static mobi.chouette.common.Constant.PARSER;
import static mobi.chouette.common.Constant.REFERENTIAL;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.parser.ObjectIdCreator;
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

	public static final String BOARDING_POSITION_ID_SUFFIX = "01";

	public static ChouetteAreaEnum PARENT_STOP_PLACE_TYPE = ChouetteAreaEnum.CommercialStopPoint;

	protected boolean shouldImportHPL(AbstractRegtoppStopHPL stop) {
		boolean shouldImport = true;

		if (stop.getFullName().toUpperCase().startsWith("MELD_")) {
			shouldImport = false;
		} else if (stop.getFullName().toUpperCase().startsWith("-- ")) {
			shouldImport = false;
		} else if (stop.getFullName().toUpperCase().startsWith("- ")) {
			shouldImport = false;
		} else if (stop.getFullName().toUpperCase().startsWith("- - ")) {
			shouldImport = false;
		} else if (stop.getFullName().toUpperCase().startsWith("AVLØSNING ")) {
			shouldImport = false;
		} else if (!StringUtils.isNumeric(stop.getStopId())) {
			shouldImport = false;
		}

		if(!shouldImport) {
			log.warn("Skipping HPL with id "+stop.getStopId()+" and name "+stop.getFullName()+ " due to ignoring rules");
		}
		
		return shouldImport;
	}

	@Override
	public void parse(Context context) throws Exception {
		try {

			RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
			Referential referential = (Referential) context.get(REFERENTIAL);
			RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);
			String projection = configuration.getCoordinateProjection();

			Map<String, StopArea> parentStopPlaceCache = new HashMap<String, StopArea>();

			for (AbstractRegtoppStopHPL stop : importer.getStopById()) {
				if (shouldImportHPL(stop)) {
					mapRegtoppStop(stop, configuration, referential, projection, parentStopPlaceCache);
				}
			}
			parentStopPlaceCache.clear();
			
		} catch (Exception e) {
			log.error("Error parsing StopArea", e);
			throw e;
		}
	}

	protected void mapRegtoppStop(AbstractRegtoppStopHPL stop, RegtoppImportParameters configuration,
			Referential referential, String projection, Map<String, StopArea> parentStopPlaceCache) {

		StopArea stopArea = parentStopPlaceCache.get(stop.getFullName());
		if (stopArea == null) {
			String stopPlaceObjectId = ObjectIdCreator.createStopAreaId(configuration, stop.getFullStopId());
			stopArea = ObjectFactory.getStopArea(referential, stopPlaceObjectId);
			stopArea.setName(stop.getFullName());
			// stopArea.setRegistrationNumber(stop.getShortName());
			stopArea.setAreaType(PARENT_STOP_PLACE_TYPE);
			convertAndSetCoordinates(stopArea, stop.getX(), stop.getY(), projection);

			parentStopPlaceCache.put(stop.getFullName(), stopArea);
		}

		String boardingPositionObjectId = ObjectIdCreator.createStopAreaId(configuration,
				stop.getFullStopId() + BOARDING_POSITION_ID_SUFFIX);
		StopArea boardingPosition = ObjectFactory.getStopArea(referential, boardingPositionObjectId);
		boardingPosition.setAreaType(ChouetteAreaEnum.BoardingPosition);
		convertAndSetCoordinates(boardingPosition, stop.getX(), stop.getY(), projection);
		boardingPosition.setName(stopArea.getName());

		boardingPosition.setParent(stopArea);
	}

	protected void convertAndSetCoordinates(StopArea stopArea, BigDecimal x, BigDecimal y, String projection) {

		if (BigDecimal.ZERO.equals(x) || BigDecimal.ZERO.equals(y)) {
			return;
		}

		BigDecimal _x = x;
		BigDecimal _y = y;

		// Some send values without decimals, adjus
		if (Coordinate.WGS84.equals(projection)) {

			// Switch coordinates
			_x = y;
			_y = x;

			while (Math.abs(_x.doubleValue()) >= 180) {
				_x = _x.divide(BigDecimal.TEN);
			}
			while (Math.abs(_y.doubleValue()) >= 90) {
				_y = _y.divide(BigDecimal.TEN);
			}

			stopArea.setLongitude(_y);
			stopArea.setLatitude(_x);

			if (log.isDebugEnabled()) {
				log.debug("Adjusted WGS84 coordinates from " + x + "->" + _x + " and " + y + "->" + _y);
			}

		} else {
			// Adjust coordinates for possible decimal places
			boolean valid = false;

			while (!valid) {
				Coordinate wgs84Coordinate = CoordinateUtil.transform(projection, Coordinate.WGS84,
						new Coordinate(_x, _y));
				if (Math.abs(wgs84Coordinate.getY().doubleValue()) >= 180
						|| Math.abs(wgs84Coordinate.getX().doubleValue()) >= 90) {
					// Bogus coordinates, divide by 10 and try again
					_x = _x.divide(BigDecimal.TEN);
					_y = _y.divide(BigDecimal.TEN);
				} else {
					valid = true;
					stopArea.setLongitude(wgs84Coordinate.getY());
					stopArea.setLatitude(wgs84Coordinate.getX());
				}
			}
		}

		stopArea.setLongLatType(LongLatTypeEnum.WGS84);

		// Original coordinates
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
