package mobi.chouette.exchange.gtfs.model.importer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import mobi.chouette.common.HTMLTagValidator;
import mobi.chouette.exchange.gtfs.model.GtfsAgency;
import mobi.chouette.exchange.gtfs.model.GtfsRoute;
import mobi.chouette.exchange.gtfs.model.GtfsStop;
import mobi.chouette.exchange.gtfs.model.GtfsStop.LocationType;
import mobi.chouette.exchange.gtfs.model.GtfsStop.WheelchairBoardingType;

public class StopById extends IndexImpl<GtfsStop> implements GtfsConverter {

	public static enum FIELDS {
		stop_id, stop_code, stop_name, stop_desc, stop_lat, stop_lon, zone_id, stop_url, location_type, parent_station, stop_timezone, wheelchair_boarding, address_line, locality, postal_code;
	};

	public static final String FILENAME = "stops.txt";
	public static final String KEY = FIELDS.stop_id.name();

	private GtfsStop bean = new GtfsStop();
	private String[] array = new String[FIELDS.values().length];
	private String _stopId = null;

	public StopById(String name) throws IOException {
		super(name, KEY);
	}
	
	@Override
	protected void checkRequiredFields(Map<String, Integer> fields) {
		for (String fieldName : fields.keySet()) {
			if (fieldName != null) {
				if (!fieldName.equals(fieldName.trim())) {
					// extra spaces in end fields are tolerated : 1-GTFS-CSV-7 warning
					getErrors().add(new GtfsException(_path, 1, getIndex(fieldName), fieldName.trim(), GtfsException.ERROR.EXTRA_SPACE_IN_HEADER_FIELD, null, fieldName));
				}
				
				if (HTMLTagValidator.validate(fieldName.trim())) {
					getErrors().add(new GtfsException(_path, 1, getIndex(fieldName), fieldName.trim(), GtfsException.ERROR.HTML_TAG_IN_HEADER_FIELD, null, null));
				}
				
				boolean fieldNameIsExtra = true;
				for (FIELDS field : FIELDS.values()) {
					if (fieldName.trim().equals(field.name())) {
						fieldNameIsExtra = false;
						break;
					}
				}
				if (fieldNameIsExtra) {
					// extra fields are tolerated : 1-GTFS-Stop-12 warning
					getErrors().add(new GtfsException(_path, 1, getIndex(fieldName), fieldName, GtfsException.ERROR.EXTRA_HEADER_FIELD, null, null));
				}
			}
		}

		// checks for ubiquitous header fields : 1-GTFS-Stop-2 error
		if ( fields.get(FIELDS.stop_id.name()) == null ||
				fields.get(FIELDS.stop_name.name()) == null ||
				fields.get(FIELDS.stop_lat.name()) == null ||
				fields.get(FIELDS.stop_lon.name()) == null) {
			
			if (fields.get(FIELDS.stop_id.name()) == null)
				throw new GtfsException(_path, 1, FIELDS.stop_id.name(), GtfsException.ERROR.MISSING_REQUIRED_FIELDS, null, null);
			
			String name = "";
			if (fields.get(FIELDS.stop_name.name()) == null)
				name = FIELDS.stop_name.name();
			else if (fields.get(FIELDS.stop_lat.name()) == null)
				name = FIELDS.stop_lat.name();
			else if (fields.get(FIELDS.stop_lon.name()) == null)
				name = FIELDS.stop_lon.name();
			getErrors().add(new GtfsException(_path, 1, name, GtfsException.ERROR.MISSING_REQUIRED_FIELDS, null, null));
		}
	}

	protected GtfsStop build(GtfsIterator reader, Context context) {
		int i = 0;
		for (FIELDS field : FIELDS.values()) {
			array[i++] = getField(reader, field.name());
		}

		i = 0;
		String value = null;
		int id = (int) context.get(Context.ID);
		clearBean();
		bean.setId(id);
		
		value = array[i++]; testExtraSpace(FIELDS.stop_id.name(), value, bean);
		bean.getOkTests().add(GtfsException.ERROR.EXTRA_SPACE_IN_FIELD);
		if (value != null && !value.trim().isEmpty()) {
			bean.setStopId(STRING_CONVERTER.from(context, FIELDS.stop_id, value, true));
		}
		
		value = array[i++]; testExtraSpace(FIELDS.stop_code.name(), value, bean);
		if (value != null && !value.trim().isEmpty()) {
			bean.setStopCode(STRING_CONVERTER.from(context, FIELDS.stop_code, value, false));
		}
		
		value = array[i++]; testExtraSpace(FIELDS.stop_name.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			if (withValidation)
				bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.stop_name.name()), FIELDS.stop_name.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			bean.getOkTests().add(GtfsException.ERROR.MISSING_REQUIRED_VALUES);
			bean.setStopName(STRING_CONVERTER.from(context, FIELDS.stop_name, value, true));
		}
		
		value = array[i++]; testExtraSpace(FIELDS.stop_desc.name(), value, bean);
		if (value != null && !value.trim().isEmpty()) {
			bean.setStopDesc(STRING_CONVERTER.from(context, FIELDS.stop_desc, value, false));
		}
		
		value = array[i++]; testExtraSpace(FIELDS.stop_lat.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			if (withValidation)
				bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.stop_lat.name()), FIELDS.stop_lat.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			bean.getOkTests().add(GtfsException.ERROR.MISSING_REQUIRED_VALUES);
			boolean validLat = true;
			try {
				double lat = Double.parseDouble(value);
				if (lat < -90 || lat > 90)
					validLat = false;
			} catch(Exception e) {
				validLat = false;
			}
			if (validLat) {
				bean.getOkTests().add(GtfsException.ERROR.INVALID_FORMAT);
				bean.setStopLat(BigDecimal.valueOf(FLOAT_CONVERTER.from(context, FIELDS.stop_lat, value, true)));
			} else {
				if (withValidation)
					bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.stop_lat.name()), FIELDS.stop_lat.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));
			}
		}
		
		value = array[i++]; testExtraSpace(FIELDS.stop_lon.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			if (withValidation)
				bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.stop_lon.name()), FIELDS.stop_lon.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			bean.getOkTests().add(GtfsException.ERROR.MISSING_REQUIRED_VALUES);
			boolean validLon = true;
			try {
				double lon = Double.parseDouble(value);
				if (lon < -180 || lon > 180)
					validLon = false;
			} catch(NumberFormatException e) {
				validLon = false;
			}
			if (validLon) {
				bean.getOkTests().add(GtfsException.ERROR.INVALID_FORMAT);
				bean.setStopLon(BigDecimal.valueOf(FLOAT_CONVERTER.from(context, FIELDS.stop_lon, value, true)));
			} else
				if (withValidation)
					bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.stop_lon.name()), FIELDS.stop_lon.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));
		}
		
		value = array[i++]; testExtraSpace(FIELDS.zone_id.name(), value, bean);
		if (value != null && !value.trim().isEmpty()) {
			bean.setZoneId(STRING_CONVERTER.from(context, FIELDS.zone_id, value, false));
		}
		
		value = array[i++]; testExtraSpace(FIELDS.stop_url.name(), value, bean);
		if (value != null && !value.trim().isEmpty()) {
			try {
				bean.setStopUrl(URL_CONVERTER.from(context, FIELDS.stop_url, value, false));
			} catch (GtfsException e) {
				// 1-GTFS-Stop-7 warning
				if (withValidation)
					bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.stop_url.name()), FIELDS.stop_url.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));	
			} finally {
				bean.getOkTests().add(GtfsException.ERROR.INVALID_FORMAT);
			}
		}
		
		value = array[i++]; testExtraSpace(FIELDS.location_type.name(), value, bean);
		if (value != null && !value.trim().isEmpty()) {
			boolean validLocType = true;
			try {
				int locType = Integer.parseInt(value);
				if (locType != 0 && locType != 1)
					validLocType = false;
			} catch(NumberFormatException e) {
				validLocType = false;
			}
			if (validLocType) {
				bean.getOkTests().add(GtfsException.ERROR.INVALID_FORMAT);
				bean.setLocationType(LOCATIONTYPE_CONVERTER.from(context, FIELDS.location_type, value, LocationType.Stop, false));
			} else {
				if (withValidation)
					bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.location_type.name()), FIELDS.location_type.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));				
			}
		}
		
		value = array[i++]; testExtraSpace(FIELDS.parent_station.name(), value, bean);
		if (value != null && !value.trim().isEmpty()) {
			bean.setParentStation(STRING_CONVERTER.from(context, FIELDS.parent_station, value, false));
		}
		
		value = array[i++]; testExtraSpace(FIELDS.stop_timezone.name(), value, bean);
		if (value != null && !value.trim().isEmpty()) {
			try {
				bean.setStopTimezone(TIMEZONE_CONVERTER.from(context,FIELDS.stop_timezone, value, false));
			} catch (GtfsException e) {
				// 1-GTFS-Stop-9  warning
				if (withValidation)
					bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.stop_timezone.name()), FIELDS.stop_timezone.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));	
			} finally {
				bean.getOkTests().add(GtfsException.ERROR.INVALID_FORMAT);
			}
		}
		
		value = array[i++]; testExtraSpace(FIELDS.wheelchair_boarding.name(), value, bean);
		if (value != null && !value.trim().isEmpty()) {
			boolean validWeelchairBoarding = true;
			try {
				int weelchairBoarding = Integer.parseInt(value);
				if (weelchairBoarding < 0 || weelchairBoarding > 2)
					validWeelchairBoarding = false;
			} catch(NumberFormatException e) {
				validWeelchairBoarding = false;
			}
			if (validWeelchairBoarding) {
				bean.setWheelchairBoarding(WHEELCHAIRBOARDINGTYPE_CONVERTER.from( context, FIELDS.wheelchair_boarding, value, WheelchairBoardingType.NoInformation, false));
			} else {
				bean.getOkTests().add(GtfsException.ERROR.INVALID_FORMAT);
				if (withValidation)
					bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.wheelchair_boarding.name()), FIELDS.wheelchair_boarding.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));				
			}
		}
		
		value = array[i++]; testExtraSpace(FIELDS.address_line.name(), value, bean);
		if (value != null && !value.trim().isEmpty()) {
			bean.setAddressLine(STRING_CONVERTER.from(context, FIELDS.address_line, value, false));
		}
		
		value = array[i++]; testExtraSpace(FIELDS.locality.name(), value, bean);
		if (value != null && !value.trim().isEmpty()) {
			bean.setLocality(STRING_CONVERTER.from(context, FIELDS.locality, value, false));
		}
		
		value = array[i++]; testExtraSpace(FIELDS.postal_code.name(), value, bean);
		if (value != null && !value.trim().isEmpty()) {
			bean.setPostalCode(STRING_CONVERTER.from(context, FIELDS.postal_code, value, false));
		}
		
		return bean;
	}

	@Override
	public boolean validate(GtfsStop bean, GtfsImporter dao) {
		boolean result = true;
		
		GtfsStop copy_bean = new GtfsStop(bean);
		String parentStationId = copy_bean.getParentStation();
		if (isPresent(parentStationId)) {

			// parentStation must reference a stop
			GtfsStop parent = dao.getStopById().getValue(parentStationId);
			if (parent == null) {
				result = false;
				bean.getErrors().add(new GtfsException(_path, copy_bean.getId(), getIndex(FIELDS.parent_station.name()), FIELDS.parent_station.name(), GtfsException.ERROR.UNREFERENCED_ID, copy_bean.getStopId(), parentStationId));
			} else if (copy_bean.getLocationType() == LocationType.Station) {
				result = false;
				bean.getErrors().add(new GtfsException(_path, copy_bean.getId(), getIndex(FIELDS.parent_station.name()), FIELDS.parent_station.name(), GtfsException.ERROR.NO_PARENT_FOR_STATION, copy_bean.getStopId(), parentStationId));
			} else {
				bean.getOkTests().add(GtfsException.ERROR.UNREFERENCED_ID);
				bean.getOkTests().add(GtfsException.ERROR.NO_PARENT_FOR_STATION);
			}
			
			// the stop parentStation is a station
			if (result) { // Stop, Station, Access
				if (LocationType.Station != parent.getLocationType()) {
					result = false;
					if (parent.getLocationType() != null)
					   bean.getErrors().add(new GtfsException(_path, copy_bean.getId(), getIndex(FIELDS.parent_station.name()), FIELDS.parent_station.name(), GtfsException.ERROR.BAD_REFERENCED_ID, copy_bean.getStopId(), Integer.toString(parent.getLocationType().ordinal())));
					else
					   bean.getErrors().add(new GtfsException(_path, copy_bean.getId(), getIndex(FIELDS.parent_station.name()), FIELDS.parent_station.name(), GtfsException.ERROR.BAD_REFERENCED_ID, copy_bean.getStopId(), "0"));
					   
				} else {
					bean.getOkTests().add(GtfsException.ERROR.BAD_REFERENCED_ID);
				}
			}
		}
		
		// stopId is used by a stop_time. OK: See GtfsTripParser.validateStopTimes(Context)
		
		// stopDesc != stopName
		boolean result2 = true;
		String stopName = copy_bean.getStopName();
		String stopDesc = copy_bean.getStopDesc();
		if (stopName != null && stopDesc != null) {
			if (stopName.equals(stopDesc)) {
				result2 = false;
				bean.getErrors().add(new GtfsException(_path, copy_bean.getId(), getIndex(FIELDS.stop_name.name()), FIELDS.stop_name.name(), GtfsException.ERROR.BAD_VALUE, copy_bean.getStopId(), stopName));
			} else {
				bean.getOkTests().add(GtfsException.ERROR.BAD_VALUE);
			}
		}
		result = result && result2;
		
		// stopUrl != GtfsAgency.agencyUrl
		boolean result3 = true;
		if (copy_bean.getStopUrl() != null) {
			for (GtfsAgency agency : (AgencyById)dao.getAgencyById()) {
				if (agency.getAgencyUrl() != null) {
					if (copy_bean.getStopUrl().equals(agency.getAgencyUrl())) {
						result3 = false;
						bean.getErrors().add(new GtfsException(_path, copy_bean.getId(), getIndex(FIELDS.stop_url.name()), FIELDS.stop_url.name()+","+AgencyById.FIELDS.agency_url.name(), GtfsException.ERROR.SHARED_VALUE,  copy_bean.getStopId(), copy_bean.getStopUrl().toString()));
						break;
					}
				}
			}
			for (GtfsRoute route : (RouteById)dao.getRouteById()) {
				if (route.getRouteUrl() != null) {
					if (copy_bean.getStopUrl().equals(route.getRouteUrl())) {
						result3 = false;
						bean.getErrors().add(new GtfsException(_path, copy_bean.getId(), getIndex(FIELDS.stop_url.name()), FIELDS.stop_url.name()+","+RouteById.FIELDS.route_url.name(), GtfsException.ERROR.SHARED_VALUE, copy_bean.getStopId(), copy_bean.getStopUrl().toString()));
						break;
					}
				}
			}
		}
		if (result3)
			bean.getOkTests().add(GtfsException.ERROR.SHARED_VALUE);
		result = result && result3;
		
		// stopUrl != routeUrl. OK: See RouteById.validate(GtfsRoute bean, GtfsImporter dao)
		
		// locationType is set for at least one stop. OK: See GtfsStopParser.validate(Context context)

		return result;
	}
	
	private void clearBean() {
		//bean.getErrors().clear();
		bean.setId(null);
		bean.setAddressLine(null);
		bean.setLocality(null);
		bean.setLocationType(null);
		bean.setParentStation(null);
		bean.setPostalCode(null);
		bean.setStopCode(null);
		bean.setStopDesc(null);
		bean.setStopId(null);
		bean.setStopLat(null);
		bean.setStopLon(null);
		bean.setStopName(null);
		bean.setStopTimezone(null);
		bean.setStopUrl(null);
		bean.setWheelchairBoarding(null);
		bean.setZoneId(null);
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(String name) throws IOException {
			return new StopById(name);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(StopById.class.getName(), factory);
	}
}
