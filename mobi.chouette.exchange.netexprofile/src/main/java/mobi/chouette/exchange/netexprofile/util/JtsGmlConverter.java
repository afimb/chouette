package mobi.chouette.exchange.netexprofile.util;

import java.math.BigInteger;
import java.util.List;

import lombok.extern.log4j.Log4j;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import net.opengis.gml._3.DirectPositionListType;
import net.opengis.gml._3.LineStringType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Log4j
public class JtsGmlConverter {

	private static final Logger logger = LoggerFactory.getLogger(JtsGmlConverter.class);

	// 4324 = WGS84
	private static final int DEFAULT_SRID = 4326;

	private static GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), DEFAULT_SRID);

	public static LineString fromGmlToJts(LineStringType gml) {
		List<Double> values = gml.getPosList().getValue();
		CoordinateSequence coordinateSequence = convert(values);
		LineString jts = new LineString(coordinateSequence, geometryFactory);

		if (!StringUtils.isEmpty(gml.getSrsName())) {
			try {
				jts.setSRID(Integer.parseInt(gml.getSrsName()));
			} catch (NumberFormatException nfe) {
				log.warn("Failed to set SRID on linestring for illegal value: " + gml.getSrsName());
			}
		}

		return jts;
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
		gml.setSrsName("" + DEFAULT_SRID);

		return gml;
	}
}
