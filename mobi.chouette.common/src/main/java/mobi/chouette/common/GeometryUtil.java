package mobi.chouette.common;

public class GeometryUtil {

	private static final int EARHT_RADIUS = 6378137;

	/**
	 * Convert a distance measured in central angle degrees to meters on earths surface
	 *
	 * @param angleDegrees distance in central angle degrees
	 * @return distance in meters
	 */
	public static final double convertFromAngleDegreesToMeters(double angleDegrees) {
		return angleDegrees * (Math.PI / 180) * EARHT_RADIUS;
	}
}
