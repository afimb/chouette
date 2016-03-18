/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.validation.checkpoint;

import java.lang.reflect.Method;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.parameters.FieldParameters;
import mobi.chouette.exchange.validation.parameters.TransportModeParameters;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.parameters.ValidationParametersUtil;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.NeptuneIdentifiedObject;
import mobi.chouette.model.NeptuneLocalizedObject;
import mobi.chouette.model.Route;
import mobi.chouette.model.type.LongLatTypeEnum;
import mobi.chouette.model.util.NamingUtil;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

/**
 * @author michel
 * 
 */
public abstract class AbstractValidation<T extends NeptuneIdentifiedObject> implements Constant {

	public enum PATTERN_OPTION {
		free, num, alpha, upper, lower
	}

	// test keys
	protected static final String STOP_AREA_1 = "3-StopArea-1";
	protected static final String STOP_AREA_2 = "3-StopArea-2";
	protected static final String STOP_AREA_3 = "3-StopArea-3";
	protected static final String STOP_AREA_4 = "3-StopArea-4";
	protected static final String STOP_AREA_5 = "3-StopArea-5";
	protected static final String ACCESS_POINT_1 = "3-AccessPoint-1";
	protected static final String ACCESS_POINT_2 = "3-AccessPoint-2";
	protected static final String ACCESS_POINT_3 = "3-AccessPoint-3";
	protected static final String CONNECTION_LINK_1 = "3-ConnectionLink-1";
	protected static final String CONNECTION_LINK_2 = "3-ConnectionLink-2";
	protected static final String CONNECTION_LINK_3 = "3-ConnectionLink-3";
	protected static final String ACCESS_LINK_1 = "3-AccessLink-1";
	protected static final String ACCESS_LINK_2 = "3-AccessLink-2";
	protected static final String ACCESS_LINK_3 = "3-AccessLink-3";
	protected static final String LINE_1 = "3-Line-1";
	protected static final String LINE_2 = "3-Line-2";
	protected static final String ROUTE_1 = "3-Route-1";
	protected static final String ROUTE_2 = "3-Route-2";
	protected static final String ROUTE_3 = "3-Route-3";
	protected static final String ROUTE_4 = "3-Route-4";
	protected static final String ROUTE_5 = "3-Route-5";
	protected static final String ROUTE_6 = "3-Route-6";
	protected static final String ROUTE_7 = "3-Route-7";
	protected static final String ROUTE_8 = "3-Route-8";
	protected static final String ROUTE_9 = "3-Route-9";
	protected static final String ROUTE_10 = "3-Route-10";
	protected static final String JOURNEY_PATTERN_1 = "3-JourneyPattern-1";
	protected static final String VEHICLE_JOURNEY_1 = "3-VehicleJourney-1";
	protected static final String VEHICLE_JOURNEY_2 = "3-VehicleJourney-2";
	protected static final String VEHICLE_JOURNEY_3 = "3-VehicleJourney-3";
	protected static final String VEHICLE_JOURNEY_4 = "3-VehicleJourney-4";
	protected static final String FACILITY_1 = "3-Facility-1";
	protected static final String FACILITY_2 = "3-Facility-2";

	protected static final String L4_NETWORK_1 = "4-Network-1";
	protected static final String L4_COMPANY_1 = "4-Company-1";
	protected static final String L4_GROUP_OF_LINE_1 = "4-GroupOfLine-1";
	protected static final String L4_STOP_AREA_1 = "4-StopArea-1";
	protected static final String L4_STOP_AREA_2 = "4-StopArea-2";
	protected static final String L4_ACCESS_POINT_1 = "4-AccessPoint-1";
	protected static final String L4_ACCESS_LINK_1 = "4-AccessLink-1";
	protected static final String L4_CONNECTION_LINK_1 = "4-ConnectionLink-1";
	protected static final String L4_CONNECTION_LINK_2 = "4-ConnectionLink-2";
	protected static final String L4_TIME_TABLE_1 = "4-Timetable-1";
	protected static final String L4_LINE_1 = "4-Line-1";
	protected static final String L4_LINE_2 = "4-Line-2";
	protected static final String L4_LINE_3 = "4-Line-3";
	protected static final String L4_LINE_4 = "4-Line-4";
	protected static final String L4_ROUTE_1 = "4-Route-1";
	protected static final String L4_JOURNEY_PATTERN_1 = "4-JourneyPattern-1";
	protected static final String L4_VEHICLE_JOURNEY_1 = "4-VehicleJourney-1";
	protected static final String L4_VEHICLE_JOURNEY_2 = "4-VehicleJourney-2";

	// parameter keys
	protected static final String STOP_AREAS_AREA = "stop_areas_area";
	protected static final String INTER_STOP_AREA_DISTANCE_MIN = "inter_stop_area_distance_min";
	protected static final String PARENT_STOP_AREA_DISTANCE_MAX = "parent_stop_area_distance_max";
	protected static final String INTER_ACCESS_POINT_DISTANCE_MIN = "inter_access_point_distance_min";
	protected static final String INTER_CONNECTION_LINK_DISTANCE_MAX = "inter_connection_link_distance_max";
	protected static final String WALK_DEFAULT_SPEED_MAX = "walk_default_speed_max";
	protected static final String WALK_OCCASIONAL_TRAVELLER_SPEED_MAX = "walk_occasional_traveller_speed_max";
	protected static final String WALK_FREQUENT_TRAVELLER_SPEED_MAX = "walk_frequent_traveller_speed_max";
	protected static final String WALK_MOBILITY_RESTRICTED_TRAVELLER_SPEED_MAX = "walk_mobility_restricted_traveller_speed_max";
	protected static final String INTER_ACCESS_LINK_DISTANCE_MAX = "inter_access_link_distance_max";
	protected static final String INTER_STOP_DURATION_MAX = "inter_stop_duration_max";
	protected static final String FACILITY_STOP_AREA_DISTANCE_MAX = "facility_stop_area_distance_max";
	protected static final String MODE_PREFIX = "getMode";
	protected static final String MODE_OTHER = "Other";
	protected static final String INTER_STOP_AREA_DISTANCE_MAX = "inter_stop_area_distance_max";
	protected static final String SPEED_MAX = "speed_max";
	protected static final String SPEED_MIN = "speed_min";
	protected static final String INTER_STOP_DURATION_VARIATION_MAX = "inter_stop_duration_variation_max";

	// level 4 parameters
	protected static final String CHECK_ALLOWED_TRANSPORT_MODES = "check_allowed_transport_modes";
	protected static final String CHECK_LINES_IN_GROUPS = "check_lines_in_groups";
	protected static final String CHECK_LINE_ROUTES = "check_line_routes";
	protected static final String CHECK_STOP_PARENT = "check_stop_parent";
	protected static final String CHECK_CONNECTION_LINK_ON_PHYSICAL = "check_connection_link_on_physical";
	protected static final String ALLOWED_TRANSPORT = "allowed_transport";
	protected static final String CHECK_OBJECT = "check_";
	protected static final String UNIQUE = "unique";
	protected static final String PATTERN = "pattern";
	protected static final String MIN_SIZE = "min_size";
	protected static final String MAX_SIZE = "max_size";

	protected static final TransportModeParameters modeDefault = new TransportModeParameters(1, 300, 30000, 40, 10, 10);

	protected static final String DEFAULT_ENVELOPPE = "[[-5.2,42.25],[-5.2,51.1],[8.23,51.1],[8.23,42.25],[-5.2,42.25]]";
	private GeometryFactory geometryFactory;

	protected static void initCheckPoint(ValidationReport validationReport, String key, CheckPoint.SEVERITY severity) {
		// ValidationReport validationReport = (ValidationReport)
		// context.get(VALIDATION_REPORT);
		if (validationReport.findCheckPointByName(key) == null) {
			validationReport.getCheckPoints().add(new CheckPoint(key, CheckPoint.RESULT.UNCHECK, severity));
		}

		return;
	}

	/**
	 * pass checkpoint to ok if uncheck
	 * 
	 * @param validationReport
	 * @param checkPointKey
	 */
	protected static void prepareCheckPoint(ValidationReport validationReport, String checkPointKey) {
		CheckPoint checkPoint = validationReport.findCheckPointByName(checkPointKey);
		if (checkPoint == null) {
			checkPoint = validationReport.findCheckPointByName(checkPointKey);
		}
		if (checkPoint.getDetails().isEmpty())
			checkPoint.setState(CheckPoint.RESULT.OK);
	}

	/**
	 * add a detail on a checkpoint
	 * 
	 * @param validationReport
	 * @param checkPointKey
	 * @param item
	 */
	protected static void addValidationError(ValidationReport validationReport, String checkPointKey, Detail item) {
		CheckPoint checkPoint = validationReport.findCheckPointByName(checkPointKey);
		checkPoint.addDetail(item);

	}

	/**
	 * calculate distance on spheroid
	 * 
	 * return 0 if one object has no coordinate
	 * 
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	protected static double distance(NeptuneLocalizedObject obj1, NeptuneLocalizedObject obj2) {
		if (obj1.hasCoordinates() && obj2.hasCoordinates())
			return computeHaversineFormula(obj1, obj2);
		else
			return 0;
	}

	/**
	 * @see http://mathforum.org/library/drmath/view/51879.html
	 */
	private static double computeHaversineFormula(NeptuneLocalizedObject obj1, NeptuneLocalizedObject obj2) {

		double lon1 = Math.toRadians(obj1.getLongitude().doubleValue());
		double lat1 = Math.toRadians(obj1.getLatitude().doubleValue());
		double lon2 = Math.toRadians(obj2.getLongitude().doubleValue());
		double lat2 = Math.toRadians(obj2.getLatitude().doubleValue());

		final double R = 6371008.8;

		double dlon = lon2 - lon1;
		double dlat = lat2 - lat1;

		double a = Math.pow((Math.sin(dlat / 2)), 2) + Math.cos(lat1) * Math.cos(lat2)
				* Math.pow(Math.sin(dlon / 2), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = R * c;

		return d;
	}

	protected static boolean isEmpty(Collection<?> beans) {
		return beans == null || beans.isEmpty();
	}

	protected static boolean isEmpty(String string) {
		return string == null || string.isEmpty();
	}

	protected static String toUnderscore(String camelcase) {

		return camelcase.replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
	}

	protected static String toCamelCase(String underscore) {
		StringBuffer b = new StringBuffer();
		boolean underChar = false;
		for (char c : underscore.toCharArray()) {
			if (c == '_') {
				underChar = true;
				continue;
			}
			if (underChar)
				b.append(Character.toUpperCase(c));
			else
				b.append(c);
		}
		return b.toString();
	}

	protected static TransportModeParameters getModeParameters(ValidationParameters parameters, String mode, Logger log) {
		// find transportMode :
		String modeKey = MODE_PREFIX + mode;
		try {
			Method method = parameters.getClass().getMethod(modeKey);
			return (TransportModeParameters) method.invoke(parameters);
		} catch (Exception e) {
			log.error("unknown mode " + mode, e);
		}
		return modeDefault;
	}

	/**
	 * @param parameters
	 * @return
	 */
	protected Polygon getEnveloppe(ValidationParameters parameters) throws Exception {
		// validationPerimeter : default = France
		String perimeter = parameters.getStopAreasArea();
		if (perimeter == null || perimeter.isEmpty() || !perimeter.startsWith("[")) {
			perimeter = DEFAULT_ENVELOPPE;
		}

		// JsonReader jsonReader = Json.createReader(new
		// StringReader(perimeter));
		JSONArray array = new JSONArray(perimeter);
		List<Coordinate> listCoordinates = new ArrayList<Coordinate>();
		for (int i = 0; i < array.length(); i++) {
			JSONArray coords = array.getJSONArray(i);
			Coordinate coord = new Coordinate(coords.getDouble(0), coords.getDouble(1));
			listCoordinates.add(coord);
		}
		if (!listCoordinates.get(0).equals(listCoordinates.get(listCoordinates.size() - 1))) {
			listCoordinates.add(listCoordinates.get(0));
		}
		Coordinate[] coordinates = listCoordinates.toArray(new Coordinate[0]);
		LinearRing shell = getGeometryFactory().createLinearRing(coordinates);
		LinearRing[] holes = null;
		Polygon polygon = getGeometryFactory().createPolygon(shell, holes);
		return polygon;
	}

	private GeometryFactory getGeometryFactory() {
		if (geometryFactory == null) {
			PrecisionModel precisionModel = new PrecisionModel(PrecisionModel.maximumPreciseValue);
			geometryFactory = new GeometryFactory(precisionModel, LongLatTypeEnum.WGS84.getValue());
		}
		return geometryFactory;
	}

	protected Point buildPoint(NeptuneLocalizedObject obj) {
		double y1 = obj.getLatitude().doubleValue();
		double x1 = obj.getLongitude().doubleValue();
		Coordinate coordinate = new Coordinate(x1, y1);
		Point point = getGeometryFactory().createPoint(coordinate);
		return point;
	}

	/**
	 * @param report
	 * @param object
	 * @param duration
	 * @param distance
	 * @param maxDefaultSpeed
	 * @param testCode
	 * @param resultCode
	 */
	protected void checkLinkSpeed(Context context, ValidationReport report, NeptuneIdentifiedObject object,
			Time duration, double distance, int maxDefaultSpeed, String testCode, String resultCode) {
		if (duration != null) {
			long time = getTimeInSeconds(duration); // in seconds

			if (time > 0) {
				int speed = (int) (distance / (double) time * 36 / 10 + 0.5); // (km/h)

				if (speed > maxDefaultSpeed) {
					Location location = buildLocation(context, object);
					Detail detail = new Detail(testCode + resultCode, location, Integer.toString(speed),
							Integer.toString(maxDefaultSpeed));
					addValidationError(report, testCode, detail);
				}
			}
		}
	}

	protected long getTimeInSeconds(Time time) {
		TimeZone tz = TimeZone.getDefault();
		long millis = 0;
		millis = time.getTime() + tz.getRawOffset();
		return millis / 1000;
	}

	protected void check4Generic1(Context context, ValidationReport report, T object, String testName,
			ValidationParameters parameters, Logger log) {

		List<String> columnNames = ValidationParametersUtil.getFields(object);
		String objectKey = toUnderscore(object.getClass().getSimpleName());
		if (columnNames == null || columnNames.isEmpty()) {
			log.info("no columns parameters for " + object.getClass().getSimpleName());
			return;
		}

		for (String column : columnNames) {
			String javaAttribute = toCamelCase(column);
			Method getter = findGetter(object.getClass(), javaAttribute);

			if (getter == null) {
				log.error("unknown column " + column + " for " + object.getClass().getSimpleName());
				continue;
			}

			FieldParameters colParam = ValidationParametersUtil.getFieldParameters(parameters, object, column);
			if (colParam == null) {
				// no parameters for test , skipped
				return;
			}

			try {
				Object objVal = getter.invoke(object);
				String value = "";
				if (objVal != null) {
					if (objVal instanceof Time) {
						// use value in seconds
						Time t = (Time) objVal;
						value = Long.toString(t.getTime() / 1000);
					} else {
						value = objVal.toString();
					}
				}
				// if objectId : check only third part
				if (column.equalsIgnoreCase("objectid") && !value.isEmpty()) {
					value = value.split(":")[2];
				}
				// uniqueness ?
				if (colParam.getUnique() == 1) {
					check4Generic1Unique(context, report, object, testName, objectKey, column, value, log);
				}

				// pattern ?
				PATTERN_OPTION pattern_opt = PATTERN_OPTION.values()[colParam.getPattern()];

				check4Generic1Pattern(context, report, object, testName, column, value, pattern_opt, log);

				// min size ?
				if (colParam.getMinSize() != null && !colParam.getMinSize().isEmpty()) {
					check4Generic1MinSize(context, report, object, testName, column, colParam, objVal, value,
							pattern_opt, log);
				}

				// max_size ?
				if (colParam.getMaxSize() != null && !colParam.getMaxSize().isEmpty() && !value.isEmpty()) {
					check4Generic1MaxSize(context, report, object, testName, column, colParam, objVal, value,
							pattern_opt, log);
				}

			} catch (Exception e) {
				log.error("fail to check column " + column + " for " + objectKey, e);
			}
		}

	}

	/**
	 * @param report
	 * @param object
	 * @param testName
	 * @param column
	 * @param colParam
	 * @param objVal
	 * @param value
	 * @param pattern_opt
	 */
	private void check4Generic1MaxSize(Context context, ValidationReport report, T object, String testName,
			String column, FieldParameters colParam, Object objVal, String value, PATTERN_OPTION pattern_opt, Logger log) {
		int maxSize = Integer.parseInt(colParam.getMaxSize());
		if (maxSize != 0) {
			if (objVal instanceof Number || objVal instanceof Time || pattern_opt == PATTERN_OPTION.num) {
				// check numeric value
				long val = Long.parseLong(value);
				if (val > maxSize) {
					Location location = buildLocation(context, object);

					Detail detail = new Detail(testName + "_" + MAX_SIZE, location, value, column);
					addValidationError(report, testName, detail);
				}
			} else {
				// test string size
				if (value.length() > maxSize) {
					Location location = buildLocation(context, object);

					Detail detail = new Detail(testName + "_" + MAX_SIZE, location, value, column);
					addValidationError(report, testName, detail);
				}
			}
		}
	}

	/**
	 * @param report
	 * @param object
	 * @param testName
	 * @param column
	 * @param colParam
	 * @param objVal
	 * @param value
	 * @param pattern_opt
	 */
	private void check4Generic1MinSize(Context context, ValidationReport report, T object, String testName,
			String column, FieldParameters colParam, Object objVal, String value, PATTERN_OPTION pattern_opt, Logger log) {
		int minSize = Integer.parseInt(colParam.getMinSize());

		if (minSize > 0 && value.isEmpty()) {
			Location location = buildLocation(context, object);

			Detail detail = new Detail(testName + "_" + MIN_SIZE, location, value, column);
			addValidationError(report, testName, detail);
			return;
		}

		if (objVal instanceof Number || objVal instanceof Time || pattern_opt == PATTERN_OPTION.num) {
			// check numeric value
			long val = Long.parseLong(value);
			if (val < minSize) {
				Location location = buildLocation(context, object);

				Detail detail = new Detail(testName + "_" + MIN_SIZE, location, value, column);
				addValidationError(report, testName, detail);
			}
		} else {
			// test string size
			if (value.length() < minSize) {
				Location location = buildLocation(context, object);

				Detail detail = new Detail(testName + "_" + MIN_SIZE, location, value, column);
				addValidationError(report, testName, detail);
			}
		}
	}

	/**
	 * @param report
	 * @param object
	 * @param testName
	 * @param column
	 * @param value
	 * @param pattern_opt
	 */
	private void check4Generic1Pattern(Context context, ValidationReport report, T object, String testName,
			String column, String value, PATTERN_OPTION pattern_opt, Logger log) {
		if (!value.isEmpty()) {
			String regex = null;
			switch (pattern_opt) {
			case free: // no check
				break;
			case num: // numeric
				regex = "^[0-9]+$";
				break;
			case alpha: // alphabetic
				regex = "^[a-zA-Z]+$";
				break;
			case upper: // uppercase
				regex = "^[A-Z]+$";
				break;
			case lower: // lowercase
				regex = "^[a-z]+$";
				break;
			}
			if (regex != null) {
				if (!Pattern.matches(regex, value)) {
					Location location = buildLocation(context, object);
					Detail detail = new Detail(testName + "_" + PATTERN, location, value, column);
					addValidationError(report, testName, detail);
				}
			}
		}
	}

	/**
	 * @param report
	 * @param object
	 * @param testName
	 * @param objectKey
	 * @param context
	 * @param column
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	private void check4Generic1Unique(Context context, ValidationReport report, T object, String testName,
			String objectKey, String column, String value, Logger log) {
		String context_key = objectKey + "_" + column + "_" + UNIQUE;

		Map<String, Location> values = (Map<String, Location>) context.get(context_key);
		if (values == null) {
			values = new HashMap<>();
			context.put(context_key, values);
		}
		if (values.containsKey(value)) {
			Location location = buildLocation(context, object);

			Detail detail = new Detail(testName + "_" + UNIQUE, location, value, column, values.get(value));
			addValidationError(report, testName, detail);
		} else {
			values.put(value, buildLocation(context, object));
		}
	}

	/**
	 * @param class1
	 * @param attribute
	 * @return
	 * @throws Exception
	 */
	private Method findGetter(Class<? extends NeptuneIdentifiedObject> class1, String attribute) {
		String methodName = "get" + attribute;
		Method[] methods = class1.getMethods();
		Method accessor = null;
		for (Method method : methods) {
			if (method.getName().equalsIgnoreCase(methodName)) {
				accessor = method;
				break;
			}
		}
		return accessor;
	}

	/**
	 * check if 2 nullable values are equal
	 * 
	 * @param val1
	 * @param val2
	 * @return
	 */
	protected boolean checkEquals(Object val1, Object val2) {
		if (val1 == null)
			return val2 == null;
		return val1.equals(val2);
	}

	protected Location buildLocation(Context context, NeptuneIdentifiedObject object) {
		if (object.getId() != null)
			return new Location(object);
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		if (data == null) {
			return new Location(null, object);
		}
		Location loc = data.getFileLocations().get(object.getObjectId());
		if (loc == null) {
			return new Location(null, object);
		}
		if (NamingUtil.isEmpty(loc.getName())) {
			loc.setName(Location.buildName(object));
		}
		Location.addLineLocation(loc,object);
		return loc;
	}

	@SuppressWarnings("unused")
	protected boolean hasOppositeRoute(Route route, Logger log) {
		// protect tests from opposite_id invalid foreign key
		try {
			Route wayBack = route.getOppositeRoute();
			if (wayBack != null) {
				String o = wayBack.getObjectId();
				return true;
			}
		} catch (Exception ex) {

			log.error("problem with oppositeRoute foreign key ");
			// route.unsetOppositeRoute();
		}
		return false;
	}

}
