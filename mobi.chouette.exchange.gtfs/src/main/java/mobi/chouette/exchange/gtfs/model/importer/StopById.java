package mobi.chouette.exchange.gtfs.model.importer;

import java.io.IOException;
import java.math.BigDecimal;

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
	protected GtfsStop build(GtfsIterator reader, Context context) {
		int i = 0;
		for (FIELDS field : FIELDS.values()) {
			array[i++] = getField(reader, field.name());
		}

		i = 0;
		int id = (int) context.get(Context.ID);
		bean.setId(id);
		bean.setStopId(STRING_CONVERTER.from(context, FIELDS.stop_id,
				array[i++], true));
		bean.setStopCode(STRING_CONVERTER.from(context, FIELDS.stop_code,
				array[i++], false));
		bean.setStopName(STRING_CONVERTER.from(context, FIELDS.stop_name,
				array[i++], true));
		bean.setStopDesc(STRING_CONVERTER.from(context, FIELDS.stop_desc,
				array[i++], false));
		bean.setStopLat(BigDecimal.valueOf(FLOAT_CONVERTER.from(context,
				FIELDS.stop_lat, array[i++], true)));
		bean.setStopLon(BigDecimal.valueOf(FLOAT_CONVERTER.from(context,
				FIELDS.stop_lon, array[i++], true)));
		bean.setZoneId(STRING_CONVERTER.from(context, FIELDS.zone_id,
				array[i++], false));
		bean.setStopUrl(URL_CONVERTER.from(context, FIELDS.stop_url,
				array[i++], false));
		bean.setLocationType(LOCATIONTYPE_CONVERTER.from(context,
				FIELDS.location_type, array[i++], LocationType.Stop, false));
		bean.setParentStation(STRING_CONVERTER.from(context,
				FIELDS.parent_station, array[i++], false));
		bean.setStopTimezone(TIMEZONE_CONVERTER.from(context,
				FIELDS.stop_timezone, array[i++], false));
		bean.setWheelchairBoarding(WHEELCHAIRBOARDINGTYPE_CONVERTER.from(
				context, FIELDS.wheelchair_boarding, array[i++],
				WheelchairBoardingType.NoInformation, false));

		bean.setAddressLine(STRING_CONVERTER.from(context, FIELDS.address_line,
				array[i++], false));
		bean.setLocality(STRING_CONVERTER.from(context, FIELDS.locality,
				array[i++], false));
		bean.setPostalCode(STRING_CONVERTER.from(context, FIELDS.postal_code,
				array[i++], false));

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
