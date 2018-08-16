package mobi.chouette.common;

public class GeometryUtil {

	private static final double EARTH_RADIUS = 6371008.8;

	/**
	 * Convert a distance measured in central angle degrees to meters on earths surface
	 *
	 * @param angleDegrees distance in central angle degrees
	 * @return distance in meters
	 */
	public static final double convertFromAngleDegreesToMeters(double angleDegrees) {
		return angleDegrees * (Math.PI / 180) * EARTH_RADIUS;
	}


	private static final double toRad = 0.017453292519943; // degree/rad ratio

	/**
	 * lifted from computeHaversineFormula in RouteSectionCheckPoints
	 *
	 * @see http://mathforum.org/library/drmath/view/51879.html
	 */
	public static double calculateDistanceInMeters(double lon1, double lat1, double lon2, double lat2) {

		double lon1AsRad = lon1 * toRad;
		double lat1AsRad = lat1 * toRad;
		double lon2AsRad = lon2 * toRad;
		double lat2AsRad = lat2 * toRad;


		double dlon = Math.sin((lon2AsRad - lon1AsRad) / 2);
		double dlat = Math.sin((lat2AsRad - lat1AsRad) / 2);
		double a = (dlat * dlat) + Math.cos(lat1AsRad) * Math.cos(lat2AsRad)
				* (dlon * dlon);
		double c = 2. * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = EARTH_RADIUS * c;
		return d;
	}
}
