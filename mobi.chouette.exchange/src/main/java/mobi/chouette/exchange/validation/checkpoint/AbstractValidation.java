/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.validation.checkpoint;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.TestDescription;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.parameters.FieldParameters;
import mobi.chouette.exchange.validation.parameters.TransportModeParameters;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.parameters.ValidationParametersUtil;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.NeptuneIdentifiedObject;
import mobi.chouette.model.NeptuneLocalizedObject;
import mobi.chouette.model.Route;
import mobi.chouette.model.type.LongLatTypeEnum;
import mobi.chouette.model.util.NamingUtil;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.joda.time.Duration;
import org.joda.time.LocalTime;
import org.joda.time.Seconds;

/**
 * @author michel
 * 
 */
public abstract class AbstractValidation<T extends NeptuneIdentifiedObject> implements Constant {

	public enum PATTERN_OPTION {
		free, num, alpha, upper, lower
	}

	public enum SEVERITY {
		W, E
	}

	// test keys
	protected static final String STOP_POINT_1 = "rutebanken_3-StopPoint-1";
	protected static final String STOP_AREA_1 = "3-StopArea-1";
	protected static final String STOP_AREA_2 = "3-StopArea-2";
	protected static final String STOP_AREA_3 = "3-StopArea-3";
	protected static final String STOP_AREA_4 = "3-StopArea-4";
	protected static final String STOP_AREA_5 = "3-StopArea-5";
	protected static final String STOP_AREA_6 = "3-StopArea-6";
	protected static final String STOP_AREA_7 = "3-StopArea-7";
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
	protected static final String LINE_3 = "3-Line-3";
	protected static final String LINE_4 = "3-Line-4";
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
	protected static final String ROUTE_SECTION_1 = "3-RouteSection-1";
	protected static final String JOURNEY_PATTERN_1 = "3-JourneyPattern-1";
	protected static final String JOURNEY_PATTERN_2 = "3-JourneyPattern-2";
	protected static final String JOURNEY_PATTERN_3 = "3-JourneyPattern-3";
	protected static final String VEHICLE_JOURNEY_1 = "3-VehicleJourney-1";
	protected static final String VEHICLE_JOURNEY_2 = "3-VehicleJourney-2";
	protected static final String VEHICLE_JOURNEY_3 = "3-VehicleJourney-3";
	protected static final String VEHICLE_JOURNEY_4 = "3-VehicleJourney-4";
	protected static final String VEHICLE_JOURNEY_5 = "3-VehicleJourney-5";
	protected static final String VEHICLE_JOURNEY_6 = "3-VehicleJourney-6";
	protected static final String VEHICLE_JOURNEY_7 = "3-VehicleJourney-7";
	protected static final String VEHICLE_JOURNEY_8 = "3-VehicleJourney-8";
	protected static final String FACILITY_1 = "3-Facility-1";
	protected static final String FACILITY_2 = "3-Facility-2";
	protected static final String INTERCHANGE_1 = "3-Interchange-1";
	
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
	protected static final String L4_INTERCHANGE_1 = "4-Interchange-1";

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

	protected static final TransportModeParameters modeDefault = new TransportModeParameters(1, 300, 30000, 40, 10, 10, 20);

	protected static final String DEFAULT_ENVELOPPE = "[[-5.2,42.25],[-5.2,51.1],[8.23,51.1],[8.23,42.25],[-5.2,42.25]]";
	private GeometryFactory geometryFactory;
	private static List<TestDescription> testLevel3FileList = null;
	private static List<TestDescription> testLevel4FileList = null;
	private static List<TestDescription> testLevel3DatabaseList = null;
	private static List<TestDescription> testLevel4DatabaseList = null;
	
	
	protected static void initCheckPoint(Context context, String key, SEVERITY severity) {
		ValidationReporter reporter = ValidationReporter.Factory.getInstance();
		reporter.addItemToValidationReport(context, key, severity.toString());
	}

	protected static void initCheckPoint(Context context, String key, String detail, SEVERITY severity) {
		ValidationReporter reporter = ValidationReporter.Factory.getInstance();
		reporter.addItemToValidationReport(context, key+"_"+detail, severity.toString());
	}

	/**
	 * pass checkpoint to ok if uncheck
	 * 
	 * @param validationReport
	 * @param checkPointKey
	 */
	protected static void prepareCheckPoint(Context context, String checkPointKey) {
		ValidationReporter reporter = ValidationReporter.Factory.getInstance();
		reporter.prepareCheckPointReport(context, checkPointKey);
	}

	/**
	 * pass checkpoint to ok if uncheck
	 * 
	 * @param validationReport
	 * @param checkPointKey
	 */
	protected static void prepareCheckPoint(Context context, String checkPointKey, String detail) {
		ValidationReporter reporter = ValidationReporter.Factory.getInstance();
		reporter.prepareCheckPointReport(context, checkPointKey+"_"+detail);
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
		if (obj1.hasCoordinates() && obj2.hasCoordinates()) {
			return computeHaversineFormula(obj1, obj2);
		} else
			return 0;
	}

	protected static final double A = 111322.; // Length of a degree in meter on equator
	/**
	 * get distance for near objects (max 2kms)
	 * 
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	protected static double quickDistance(NeptuneLocalizedObject obj1, NeptuneLocalizedObject obj2) {

		double dlon = (obj2.getLongitude().doubleValue() - obj1.getLongitude().doubleValue()) * A;
		dlon *= Math.cos((obj2.getLatitude().doubleValue() + obj1.getLatitude().doubleValue())* toRad/2.);
		double dlat = (obj2.getLatitude().doubleValue() - obj1.getLatitude().doubleValue()) * A;
		double ret  = Math.sqrt(dlon * dlon + dlat * dlat);
		return ret;

	}
	
	/**
	 * get distance between two coordinates
	 * @param lat1
	 * @param lat2
	 * @param long1
	 * @param long2
	 * @return
	 */
	public static double quickDistanceFromCoordinates(Double lat1, Double lat2, Double long1, Double long2) {

		double dlon = (long2 - long1) * A;
		dlon *= Math.cos((lat2 + lat1)* toRad/2.);
		double dlat = (lat2 - lat1) * A;
		double ret  = Math.sqrt(dlon * dlon + dlat * dlat);
		return ret;

	}

	protected static final double R = 6371008.8; // Earth radius
	protected static final double toRad = 0.017453292519943; // degree/rad ratio

	/**
	 * @see http://mathforum.org/library/drmath/view/51879.html
	 */
	private static double computeHaversineFormula(NeptuneLocalizedObject obj1, NeptuneLocalizedObject obj2) {

		double lon1 = obj1.getLongitude().doubleValue() * toRad;
		double lat1 = obj1.getLatitude().doubleValue() * toRad;
		double lon2 = obj2.getLongitude().doubleValue() * toRad;
		double lat2 = obj2.getLatitude().doubleValue() * toRad;


		double dlon = Math.sin((lon2 - lon1)/ 2);
		double dlat = Math.sin((lat2 - lat1)/ 2 );
		double a = (dlat * dlat) + Math.cos(lat1) * Math.cos(lat2)
				* (dlon * dlon);
		double c = 2. * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
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

	public static TransportModeParameters getModeParameters(ValidationParameters parameters, String mode, Logger log) {
		// find transportMode :
		String modeKey = MODE_PREFIX + mode;
		try {
			Method method = parameters.getClass().getMethod(modeKey);
			return (TransportModeParameters) method.invoke(parameters);
		} catch (Exception e) {
			log.error("unknown mode " + mode, e);
		}
		return null; //modeDefault;
	}

	/**
	 * @param parameters
	 * @return
	 */
	public Polygon getEnveloppe(ValidationParameters parameters) throws Exception {
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
	protected void checkLinkSpeed(Context context, NeptuneIdentifiedObject object, Duration duration, double distance,
								  int maxDefaultSpeed, String testCode, String resultCode) {
		if (duration != null) {
			long time = duration.getStandardSeconds(); // in seconds

			if (time > 0) {
				int speed = (int) (distance / (double) time * 36 / 10 + 0.5); // (km/h)

				if (speed > maxDefaultSpeed) {
					ValidationReporter reporter = ValidationReporter.Factory.getInstance();

					DataLocation location = buildLocation(context, object);
					reporter.addCheckPointReportError(context, testCode, resultCode, location, Integer.toString(speed),
							Integer.toString(maxDefaultSpeed));
					// Detail detail = new Detail(testCode + resultCode,
					// location, Integer.toString(speed),
					// Integer.toString(maxDefaultSpeed));
					// addValidationError(report, testCode, detail);
				}
			}
		}
	}

	protected void check4Generic1(Context context, T object, String testName, ValidationParameters parameters,
			Logger log) {

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
					if (objVal instanceof LocalTime) {
						// use value in seconds
						LocalTime t = (LocalTime) objVal;
						value = Long.toString(Seconds.secondsBetween(new LocalTime(0, 0, 0), t).getSeconds());
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
					check4Generic1Unique(context, object, testName, objectKey, column, value, log);
				}

				// pattern ?
				PATTERN_OPTION pattern_opt = PATTERN_OPTION.values()[colParam.getPattern()];

				check4Generic1Pattern(context, object, testName, column, value, pattern_opt, log);

				// min size ?
				if (colParam.getMinSize() != null && !colParam.getMinSize().isEmpty()) {
					check4Generic1MinSize(context, object, testName, column, colParam, objVal, value, pattern_opt, log);
				}

				// max_size ?
				if (colParam.getMaxSize() != null && !colParam.getMaxSize().isEmpty() && !value.isEmpty()) {
					check4Generic1MaxSize(context, object, testName, column, colParam, objVal, value, pattern_opt, log);
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
	private void check4Generic1MaxSize(Context context, T object, String testName, String column,
			FieldParameters colParam, Object objVal, String value, PATTERN_OPTION pattern_opt, Logger log) {
		int maxSize = Integer.parseInt(colParam.getMaxSize());
		if (maxSize != 0) {
			if (objVal instanceof Number || objVal instanceof LocalTime || pattern_opt == PATTERN_OPTION.num) {
				// check numeric value
				long val = Long.parseLong(value);
				if (val > maxSize) {
					ValidationReporter reporter = ValidationReporter.Factory.getInstance();
					DataLocation location = buildLocation(context, object);
					reporter.addCheckPointReportError(context, testName, MAX_SIZE, location, value, column);
					// Location location = buildLocation(context, object);
					//
					// Detail detail = new Detail(testName + "_" + MAX_SIZE,
					// location, value, column);
					// addValidationError(report, testName, detail);
				}
			} else {
				// test string size
				if (value.length() > maxSize) {
					ValidationReporter reporter = ValidationReporter.Factory.getInstance();
					DataLocation location = buildLocation(context, object);
					reporter.addCheckPointReportError(context, testName, MAX_SIZE, location, value, column);
					// Location location = buildLocation(context, object);
					//
					// Detail detail = new Detail(testName + "_" + MAX_SIZE,
					// location, value, column);
					// addValidationError(report, testName, detail);
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
	private void check4Generic1MinSize(Context context, T object, String testName, String column,
			FieldParameters colParam, Object objVal, String value, PATTERN_OPTION pattern_opt, Logger log) {
		int minSize = Integer.parseInt(colParam.getMinSize());

		if (minSize > 0 && value.isEmpty()) {
			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			DataLocation location = buildLocation(context, object);
			reporter.addCheckPointReportError(context, testName, MIN_SIZE, location, value, column);
			// Location location = buildLocation(context, object);
			//
			// Detail detail = new Detail(testName + "_" + MIN_SIZE, location,
			// value, column);
			// addValidationError(report, testName, detail);
			return;
		}

		if (objVal instanceof Number || objVal instanceof LocalTime || pattern_opt == PATTERN_OPTION.num) {
			// check numeric value
			long val = Long.parseLong(value);
			if (val < minSize) {
				ValidationReporter reporter = ValidationReporter.Factory.getInstance();
				DataLocation location = buildLocation(context, object);
				reporter.addCheckPointReportError(context, testName, MIN_SIZE, location, value, column);
				// Location location = buildLocation(context, object);
				//
				// Detail detail = new Detail(testName + "_" + MIN_SIZE,
				// location, value, column);
				// addValidationError(report, testName, detail);
			}
		} else {
			// test string size
			if (value.length() < minSize) {
				ValidationReporter reporter = ValidationReporter.Factory.getInstance();
				DataLocation location = buildLocation(context, object);
				reporter.addCheckPointReportError(context, testName, MIN_SIZE, location, value, column);
				// Location location = buildLocation(context, object);
				//
				// Detail detail = new Detail(testName + "_" + MIN_SIZE,
				// location, value, column);
				// addValidationError(report, testName, detail);
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
	private void check4Generic1Pattern(Context context, T object, String testName, String column, String value,
			PATTERN_OPTION pattern_opt, Logger log) {
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
					ValidationReporter reporter = ValidationReporter.Factory.getInstance();
					DataLocation location = buildLocation(context, object);
					reporter.addCheckPointReportError(context, testName, PATTERN, location, value, column);
					// Location location = buildLocation(context, object);
					// Detail detail = new Detail(testName + "_" + PATTERN,
					// location, value, column);
					// addValidationError(report, testName, detail);
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
	private void check4Generic1Unique(Context context, T object, String testName, String objectKey, String column,
			String value, Logger log) {
		String context_key = objectKey + "_" + column + "_" + UNIQUE;

		Map<String, DataLocation> values = (Map<String, DataLocation>) context.get(context_key);
		if (values == null) {
			values = new HashMap<>();
			context.put(context_key, values);
		}
		if (values.containsKey(value)) {
			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			DataLocation location = buildLocation(context, object);
			reporter.addCheckPointReportError(context, testName, UNIQUE, location, value, column, values.get(value));
			// Location location = buildLocation(context, object);
			//
			// Detail detail = new Detail(testName + "_" + UNIQUE, location,
			// value, column, values.get(value));
			// addValidationError(report, testName, detail);
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

	protected DataLocation buildLocation(Context context, NeptuneIdentifiedObject object) {
		if (object.getId() != null)
			return new DataLocation(object);
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		if (data == null) {
			return new DataLocation(object);
		}
		DataLocation loc = data.getDataLocations().get(object.getObjectId());
		if (loc == null) {
			loc =  new DataLocation(object);
		}
		if (NamingUtil.isEmpty(loc.getName())) {
			loc.setName(DataLocation.buildName(object));
		}
		// DataLocation.addLineLocation(loc,object);
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
	
	public static List<TestDescription> getTestLevel3FileList() {
		if(testLevel3FileList == null) {
			testLevel3FileList = new ArrayList<TestDescription>();
			
			testLevel3FileList.add(new TestDescription(3, STOP_AREA_2, "WARNING"));
			testLevel3FileList.add(new TestDescription(3, STOP_AREA_3, "WARNING"));
			testLevel3FileList.add(new TestDescription(3, STOP_AREA_4, "WARNING"));
			testLevel3FileList.add(new TestDescription(3, STOP_AREA_5, "WARNING"));
			testLevel3FileList.add(new TestDescription(3, STOP_AREA_6, "WARNING"));
			testLevel3FileList.add(new TestDescription(3, STOP_AREA_7, "WARNING"));

			testLevel3FileList.add(new TestDescription(3, CONNECTION_LINK_1, "WARNING"));
			testLevel3FileList.add(new TestDescription(3, CONNECTION_LINK_2, "WARNING"));
			testLevel3FileList.add(new TestDescription(3, CONNECTION_LINK_3, "WARNING"));
			
			testLevel3FileList.add(new TestDescription(3, ACCESS_LINK_1, "WARNING"));
			testLevel3FileList.add(new TestDescription(3, ACCESS_LINK_2, "WARNING"));
			testLevel3FileList.add(new TestDescription(3, ACCESS_LINK_3, "WARNING"));
			
			testLevel3FileList.add(new TestDescription(3, LINE_1, "WARNING"));
			testLevel3FileList.add(new TestDescription(3, LINE_4, "WARNING"));

			testLevel3FileList.add(new TestDescription(3, ROUTE_1, "WARNING"));
			testLevel3FileList.add(new TestDescription(3, ROUTE_2, "WARNING"));
			testLevel3FileList.add(new TestDescription(3, ROUTE_3, "WARNING"));
			testLevel3FileList.add(new TestDescription(3, ROUTE_4, "WARNING"));
			testLevel3FileList.add(new TestDescription(3, ROUTE_5, "WARNING"));
			testLevel3FileList.add(new TestDescription(3, ROUTE_8, "WARNING"));
			testLevel3FileList.add(new TestDescription(3, ROUTE_9, "WARNING"));
			
			testLevel3FileList.add(new TestDescription(3, JOURNEY_PATTERN_1, "WARNING"));
			
			testLevel3FileList.add(new TestDescription(3, VEHICLE_JOURNEY_1, "WARNING"));
			testLevel3FileList.add(new TestDescription(3, VEHICLE_JOURNEY_2, "WARNING"));
			testLevel3FileList.add(new TestDescription(3, VEHICLE_JOURNEY_3, "WARNING"));
			testLevel3FileList.add(new TestDescription(3, VEHICLE_JOURNEY_4, "WARNING"));
			testLevel3FileList.add(new TestDescription(3, VEHICLE_JOURNEY_5, "WARNING"));
			testLevel3FileList.add(new TestDescription(3, VEHICLE_JOURNEY_6, "WARNING"));
			testLevel3FileList.add(new TestDescription(3, VEHICLE_JOURNEY_7, "WARNING"));
			testLevel3FileList.add(new TestDescription(3, VEHICLE_JOURNEY_8, "WARNING"));
			
			testLevel3FileList.add(new TestDescription(3, ROUTE_SECTION_1, "WARNING"));
			
//			testLevel3FileList.add(new TestDescription(3, FACILITY_1, "WARNING"));
//			testLevel3FileList.add(new TestDescription(3, FACILITY_2, "WARNING"));

			testLevel3FileList.add(new TestDescription(3, INTERCHANGE_1, "WARNING"));
}
		
		return testLevel3FileList;
	}
	
	public static List<TestDescription> getTestLevel4FileList() {
		if(testLevel4FileList == null) {
			testLevel4FileList = new ArrayList<TestDescription>();
			testLevel4FileList.add(new TestDescription(4, L4_LINE_2, "WARNING"));
			testLevel4FileList.add(new TestDescription(4, L4_VEHICLE_JOURNEY_2, "WARNING"));
		}
		
		return testLevel4FileList;
	}
	
	
	public static List<TestDescription> getTestLevel3DatabaseList() {
		if(testLevel3DatabaseList == null) {
			testLevel3DatabaseList = new ArrayList<TestDescription>();

			testLevel3DatabaseList.add(new TestDescription(3, STOP_POINT_1, "ERROR"));
			testLevel3DatabaseList.add(new TestDescription(3, STOP_AREA_1, "ERROR"));
			testLevel3DatabaseList.add(new TestDescription(3, STOP_AREA_2, "WARNING"));
			testLevel3DatabaseList.add(new TestDescription(3, STOP_AREA_3, "WARNING"));
			testLevel3DatabaseList.add(new TestDescription(3, STOP_AREA_4, "WARNING"));
			testLevel3DatabaseList.add(new TestDescription(3, STOP_AREA_5, "WARNING"));
			testLevel3DatabaseList.add(new TestDescription(3, STOP_AREA_6, "WARNING"));
			testLevel3DatabaseList.add(new TestDescription(3, STOP_AREA_7, "WARNING"));

			testLevel3DatabaseList.add(new TestDescription(3, ACCESS_POINT_1, "ERROR"));
			testLevel3DatabaseList.add(new TestDescription(3, ACCESS_POINT_2, "ERROR"));
			testLevel3DatabaseList.add(new TestDescription(3, ACCESS_POINT_3, "ERROR"));
			
			testLevel3DatabaseList.add(new TestDescription(3, CONNECTION_LINK_1, "WARNING"));
			testLevel3DatabaseList.add(new TestDescription(3, CONNECTION_LINK_2, "WARNING"));
			testLevel3DatabaseList.add(new TestDescription(3, CONNECTION_LINK_3, "WARNING"));
			
			testLevel3DatabaseList.add(new TestDescription(3, ACCESS_LINK_1, "WARNING"));
			testLevel3DatabaseList.add(new TestDescription(3, ACCESS_LINK_2, "WARNING"));
			testLevel3DatabaseList.add(new TestDescription(3, ACCESS_LINK_3, "WARNING"));
			
			testLevel3DatabaseList.add(new TestDescription(3, LINE_1, "WARNING"));
			testLevel3DatabaseList.add(new TestDescription(3, LINE_2, "ERROR"));
			testLevel3DatabaseList.add(new TestDescription(3, LINE_4, "WARNING"));

			testLevel3DatabaseList.add(new TestDescription(3, ROUTE_1, "WARNING"));
			testLevel3DatabaseList.add(new TestDescription(3, ROUTE_2, "WARNING"));
			testLevel3DatabaseList.add(new TestDescription(3, ROUTE_3, "WARNING"));
			testLevel3DatabaseList.add(new TestDescription(3, ROUTE_4, "WARNING"));
			testLevel3DatabaseList.add(new TestDescription(3, ROUTE_5, "WARNING"));
			testLevel3DatabaseList.add(new TestDescription(3, ROUTE_6, "ERROR"));
			testLevel3DatabaseList.add(new TestDescription(3, ROUTE_7, "ERROR"));
			testLevel3DatabaseList.add(new TestDescription(3, ROUTE_8, "WARNING"));
			testLevel3DatabaseList.add(new TestDescription(3, ROUTE_9, "WARNING"));
			
			testLevel3DatabaseList.add(new TestDescription(3, JOURNEY_PATTERN_1, "WARNING"));
			testLevel3DatabaseList.add(new TestDescription(3, JOURNEY_PATTERN_2, "ERROR"));
			
			
			testLevel3DatabaseList.add(new TestDescription(3, VEHICLE_JOURNEY_1, "WARNING"));
			testLevel3DatabaseList.add(new TestDescription(3, VEHICLE_JOURNEY_2, "WARNING"));
			testLevel3DatabaseList.add(new TestDescription(3, VEHICLE_JOURNEY_3, "WARNING"));
			testLevel3DatabaseList.add(new TestDescription(3, VEHICLE_JOURNEY_4, "WARNING"));
			testLevel3DatabaseList.add(new TestDescription(3, VEHICLE_JOURNEY_5, "WARNING"));
			testLevel3DatabaseList.add(new TestDescription(3, VEHICLE_JOURNEY_6, "WARNING"));
			testLevel3DatabaseList.add(new TestDescription(3, VEHICLE_JOURNEY_7, "WARNING"));
			testLevel3DatabaseList.add(new TestDescription(3, VEHICLE_JOURNEY_8, "WARNING"));
			
			testLevel3DatabaseList.add(new TestDescription(3, ROUTE_SECTION_1, "WARNING"));
			
//			testLevel3DatabaseList.add(new TestDescription(3, FACILITY_1, "WARNING"));
//			testLevel3DatabaseList.add(new TestDescription(3, FACILITY_2, "WARNING"));
			
			testLevel3DatabaseList.add(new TestDescription(3, INTERCHANGE_1, "WARNING"));
			
		}
		
		return testLevel3DatabaseList;
	}
	
	public static List<TestDescription> getTestLevel4DatabaseList() {
		if(testLevel4DatabaseList == null) {
			testLevel4DatabaseList = new ArrayList<TestDescription>();
			
			testLevel4DatabaseList.add(new TestDescription(4, L4_NETWORK_1, "ERROR"));
			testLevel4DatabaseList.add(new TestDescription(4, L4_COMPANY_1, "ERROR"));
			testLevel4DatabaseList.add(new TestDescription(4, L4_GROUP_OF_LINE_1, "ERROR"));
			
			testLevel4DatabaseList.add(new TestDescription(4, L4_LINE_1, "ERROR"));
			testLevel4DatabaseList.add(new TestDescription(4, L4_LINE_2, "WARNING"));
			testLevel4DatabaseList.add(new TestDescription(4, L4_LINE_3, "ERROR"));
			testLevel4DatabaseList.add(new TestDescription(4, L4_LINE_4, "ERROR"));
			
			testLevel4DatabaseList.add(new TestDescription(4, L4_STOP_AREA_1, "ERROR"));
			testLevel4DatabaseList.add(new TestDescription(4, L4_STOP_AREA_2, "ERROR"));
			
			testLevel4DatabaseList.add(new TestDescription(4, L4_CONNECTION_LINK_1, "ERROR"));
			testLevel4DatabaseList.add(new TestDescription(4, L4_CONNECTION_LINK_2, "ERROR"));
			
			testLevel4DatabaseList.add(new TestDescription(4, L4_ROUTE_1, "ERROR"));
			
			testLevel4DatabaseList.add(new TestDescription(4, L4_JOURNEY_PATTERN_1, "ERROR"));
			
			testLevel4DatabaseList.add(new TestDescription(4, L4_VEHICLE_JOURNEY_1, "ERROR"));
			testLevel4DatabaseList.add(new TestDescription(4, L4_VEHICLE_JOURNEY_2, "WARNING"));
			
			testLevel4DatabaseList.add(new TestDescription(4, L4_TIME_TABLE_1, "ERROR"));

			testLevel4DatabaseList.add(new TestDescription(4, L4_INTERCHANGE_1, "ERROR"));
}
		
		return testLevel4DatabaseList;
	}

}
