package mobi.chouette.exchange.importer.geometry;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Port to Java of Mark McClures Javascript PolylineEncoder :
 * http://facstaff.unca.edu/mcmcclur/GoogleMaps/EncodePolyline/decode.js
 */
public class PolylineDecoder {
	private static final double DEFAULT_PRECISION = 1E5;

	public List<Coordinate> decode(String encoded) {
		return decode(encoded, DEFAULT_PRECISION);
	}

	/**
	 * Precision should be something like 1E5 or 1E6. For OSRM routes found precision was 1E6, not the original default
	 * 1E5.
	 *
	 * @param encoded
	 * @param precision
	 * @return
	 */
	public List<Coordinate> decode(String encoded, double precision) {
		List<Coordinate> track = new ArrayList<>();
		int index = 0;
		int lat = 0, lng = 0;

		while (index < encoded.length()) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			Coordinate c = new Coordinate((double) lng / precision,(double) lat / precision);
			track.add(c);
		}
		return track;
	}

}