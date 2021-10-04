package mobi.chouette.exchange.netexprofile.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import net.opengis.gml._3.DirectPositionListType;
import net.opengis.gml._3.DirectPositionType;
import net.opengis.gml._3.LineStringType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Log4j
public class JtsGmlConverter {

	private static final Logger logger = LoggerFactory.getLogger(JtsGmlConverter.class);

	private static final String DEFAULT_SRID_NAME = "WGS84";
	private static final String DEFAULT_SRID_AS_STRING = "4326";
	private static final int DEFAULT_SRID_AS_INT = 4326;


	private static GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), DEFAULT_SRID_AS_INT);

	public static LineString fromGmlToJts(LineStringType gml) {
		List<Double> coordinateList;
		DirectPositionListType posList = gml.getPosList();
		if (posList != null && !posList.getValue().isEmpty()) {
			coordinateList = posList.getValue();
		} else {
			if (gml.getPosOrPointProperty() != null && !gml.getPosOrPointProperty().isEmpty()) {
				coordinateList = new ArrayList<>();
				for (Object o : gml.getPosOrPointProperty()) {
					if (o instanceof DirectPositionType) {
						DirectPositionType directPositionType = (DirectPositionType) o;
						coordinateList.addAll(directPositionType.getValue());
					} else {
						log.warn("Unrecognized class (" + o.getClass() + ") for PosOrPointProperty for gmlString: " + gml.getId());
						return null;
					}
				}
			} else {
				log.warn("LineString without posList or PosOrPointProperty: " + gml.getId());
				return null;
			}
		}
		if(coordinateList.isEmpty()) {
			log.warn("LineString without coordinates: " + gml.getId());
			return null;
		}
		if(coordinateList.size() < 4) {
			log.warn("LineString with less than 2 pairs of coordinates (found " + coordinateList.size() + " individual coordinate values): " + gml.getId());
			return null;
		}
		CoordinateSequence coordinateSequence = convert(coordinateList);
		LineString jts = new LineString(coordinateSequence, geometryFactory);
		assignSRID(gml, jts);

		return jts;
	}

	/**
	 * Assign an SRID to the LineString based on the provided Spatial Reference System name.
	 * The LineString is expected to be based on the WGS84 spatial reference system (SRID=4326).
	 * If srsName is not set, the SRID defaults to 4326 (default value set by the {@link GeometryFactory}).
	 * If srsName is set to either "4326" or "WGS84", the SRID defaults to 4326.
	 * If srsName is set to another value, an attempt is made to parse it as a SRID.
	 * If srsName is not parseable as a SRID, then the SRID defaults to 4326.
	 **/
	private static void assignSRID(LineStringType gml, LineString jts) {
		String srsName = gml.getSrsName();
		if (!StringUtils.isEmpty(srsName) && !DEFAULT_SRID_NAME.equals(srsName) && !DEFAULT_SRID_AS_STRING.equals(srsName)) {
			log.warn("The LineString " + gml.getId() + " is not based on the WGS84 Spatial Reference System. SRID in use: " + srsName);
			try {
				jts.setSRID(Integer.parseInt(srsName));
			} catch (NumberFormatException nfe) {
				log.warn("Ignoring SRID on linestring" + gml.getId() + " for illegal value: " + srsName);
			}
		}
	}


	public static CoordinateSequence convert(List<Double> values) {
		Coordinate[] coordinates = new Coordinate[values.size() / 2];
		int coordinateIndex = 0;
		for (int index = 0; index < values.size(); index += 2) {
			Coordinate coordinate = new Coordinate(values.get(index + 1), values.get(index));
			coordinates[coordinateIndex++] = coordinate;
		}
		return new CoordinateArraySequence(coordinates);
	}


	public static LineStringType fromJtsToGml(LineString jts, String id) {

		LineStringType gml = new LineStringType();

		DirectPositionListType directPositionListType = new DirectPositionListType();

		if (jts.getCoordinates() != null) {
			logger.trace("Converting coordinates {}", jts.getCoordinates());
			List<Double> positions = directPositionListType.getValue();
			for (Coordinate coordinate : jts.getCoordinates()) {
				positions.add(coordinate.y);
				positions.add(coordinate.x);

			}
			directPositionListType.setCount(BigInteger.valueOf(positions.size()));
			directPositionListType.setSrsDimension(BigInteger.valueOf(2L));
		}
		gml.setPosList(directPositionListType);
		gml.setId(id);
		gml.setSrsDimension(BigInteger.valueOf(2L));
		gml.setSrsName(DEFAULT_SRID_AS_STRING);

		return gml;
	}
}
