package mobi.chouette.exchange.gtfs.model.importer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import mobi.chouette.exchange.gtfs.model.GtfsStop;
import mobi.chouette.exchange.gtfs.model.GtfsStop.LocationType;
import mobi.chouette.exchange.gtfs.model.GtfsStop.WheelchairBoardingType;
import mobi.chouette.exchange.gtfs.model.importer.GtfsException.ERROR;

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
		// extra fields are tolerated : 1-GTFS-Stop-11 warning
		for (String fieldName : fields.keySet()) {
			if (fieldName != null) {
				boolean fieldNameIsExtra = true;
				for (FIELDS field : FIELDS.values()) {
					if (fieldName.trim().equals(field.name())) {
						fieldNameIsExtra = false;
						break;
					}
				}
				if (fieldNameIsExtra) {
					// add the warning to warnings
					Context context = new Context();
					context.put(Context.PATH, _path);
					context.put(Context.FIELD, fieldName);
					context.put(Context.ERROR, GtfsException.ERROR.EXTRA_HEADER_FIELD);
					getErrors().add(new GtfsException(context));
				}
			}
		}
		
		// checks for ubiquitous header fields : 1-GTFS-Stop-2 error
		if ( fields.get(FIELDS.stop_id.name()) == null ||
				fields.get(FIELDS.stop_name.name()) == null ||
				fields.get(FIELDS.stop_lat.name()) == null ||
				fields.get(FIELDS.stop_lon.name()) == null) {
			Context context = new Context();
			context.put(Context.PATH, _path);
			context.put(Context.ERROR, GtfsException.ERROR.MISSING_REQUIRED_FIELDS);
			getErrors().add(new GtfsException(context));
		}
	}

	@Override
	protected GtfsStop build(GtfsIterator reader, Context context) {
		int i = 0;
		for (FIELDS field : FIELDS.values()) {
			array[i++] = getField(reader, field.name());
		}

		i = 0;
		String value = null;
		int id = (int) context.get(Context.ID);
		bean.getErrors().clear();
		bean.setId(id);
		value = array[i++];
		bean.setStopId(STRING_CONVERTER.from(context, FIELDS.stop_id, value, true));
		value = array[i++];
		bean.setStopCode(STRING_CONVERTER.from(context, FIELDS.stop_code, value, false));
		value = array[i++];
		bean.setStopName(STRING_CONVERTER.from(context, FIELDS.stop_name, value, true));
		value = array[i++];
		bean.setStopDesc(STRING_CONVERTER.from(context, FIELDS.stop_desc, value, false));
		value = array[i++];
		bean.setStopLat(BigDecimal.valueOf(FLOAT_CONVERTER.from(context, FIELDS.stop_lat, value, true)));
		value = array[i++];
		bean.setStopLon(BigDecimal.valueOf(FLOAT_CONVERTER.from(context, FIELDS.stop_lon, value, true)));
		value = array[i++];
		bean.setZoneId(STRING_CONVERTER.from(context, FIELDS.zone_id, value, false));
		value = array[i++];
		bean.setStopUrl(URL_CONVERTER.from(context, FIELDS.stop_url, value, false));
		value = array[i++];
		bean.setLocationType(LOCATIONTYPE_CONVERTER.from(context, FIELDS.location_type, value, LocationType.Stop, false));
		value = array[i++];
		bean.setParentStation(STRING_CONVERTER.from(context, FIELDS.parent_station, value, false));
		value = array[i++];
		bean.setStopTimezone(TIMEZONE_CONVERTER.from(context, FIELDS.stop_timezone, value, false));
		value = array[i++];
		bean.setWheelchairBoarding(WHEELCHAIRBOARDINGTYPE_CONVERTER.from( context, FIELDS.wheelchair_boarding, value, WheelchairBoardingType.NoInformation, false));
		value = array[i++];
		bean.setAddressLine(STRING_CONVERTER.from(context, FIELDS.address_line, value, false));
		value = array[i++];
		bean.setLocality(STRING_CONVERTER.from(context, FIELDS.locality, value, false));
		value = array[i++];
		bean.setPostalCode(STRING_CONVERTER.from(context, FIELDS.postal_code, value, false));
		
		return bean;
	}

	@Override
	public boolean validate(GtfsStop bean, GtfsImporter dao) {
		boolean result = true;
		if (bean.getLocationType() == LocationType.Station
				&& bean.getParentStation() != null) {
			throw new GtfsException(getPath(), bean.getId(),
					FIELDS.parent_station.name(), ERROR.INVALID_FORMAT, "TODO",
					bean.getParentStation());
		}

		String stopId = bean.getParentStation();
		if (stopId != null && !stopId.equals(_stopId)) {
			if (stopId != null) {
				if (!containsKey(stopId)) {
					throw new GtfsException(getPath(), bean.getId(),
							FIELDS.stop_id.name(), ERROR.MISSING_FOREIGN_KEY,
							"TODO", bean.getStopId());

				}
				GtfsStop parent = getValue(stopId);
				if (parent == null
						|| parent.getLocationType() != LocationType.Station) {
					throw new GtfsException(getPath(), bean.getId(),
							FIELDS.parent_station.name(),
							ERROR.MISSING_FOREIGN_KEY, "TODO",
							bean.getParentStation());
				}
				_stopId = stopId;
			}
		}

		return result;
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
