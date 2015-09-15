package mobi.chouette.exchange.gtfs.model.importer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import mobi.chouette.exchange.gtfs.model.GtfsAgency;
import mobi.chouette.exchange.gtfs.model.GtfsCalendar;
import mobi.chouette.exchange.gtfs.model.GtfsCalendarDate;
import mobi.chouette.exchange.gtfs.model.GtfsFrequency;
import mobi.chouette.exchange.gtfs.model.GtfsObject;
import mobi.chouette.exchange.gtfs.model.GtfsRoute;
import mobi.chouette.exchange.gtfs.model.GtfsShape;
import mobi.chouette.exchange.gtfs.model.GtfsStop;
import mobi.chouette.exchange.gtfs.model.GtfsStopTime;
import mobi.chouette.exchange.gtfs.model.GtfsTransfer;
import mobi.chouette.exchange.gtfs.model.GtfsTrip;
import mobi.chouette.exchange.gtfs.model.importer.GtfsException.ERROR;

public class GtfsImporter {
	public static enum INDEX {
		AGENCY_BY_ID, CALENDAR_BY_SERVICE, CALENDAR_DATE_BY_SERVICE, FREQUENCY_BY_TRIP, ROUTE_BY_ID, STOP_BY_ID, STOP_TIME_BY_TRIP, TRANSFER_BY_FROM_STOP, SHAPE_BY_ID, TRIP_BY_ID, TRIP_BY_ROUTE, TRIP_BY_SERVICE;
	}

	private String _path;
	private Map<String, Index<GtfsObject>> _map = new HashMap<String, Index<GtfsObject>>();

	public GtfsImporter(String path) {
		_path = path;
	}

	@SuppressWarnings("rawtypes")
	public void dispose() {
		for (Index importer : _map.values()) {
			importer.dispose();
		}
		_map.clear();
		_map = null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Index getImporter(String name, String path, Class clazz) {
		Index importer = _map.get(name);

		if (importer == null) {
			try {
				importer = IndexFactory.build(
						Paths.get(_path, path).toString(), clazz.getName());
				_map.put(name, importer);
			} catch (ClassNotFoundException | IOException e) {
				Context context = new Context();
				context.put(Context.PATH, _path);
				context.put(Context.ERROR, ERROR.SYSTEM);
				throw new GtfsException(context, e);
			}

		}
		return importer;
	}

	public boolean hasAgencyImporter() {
		return hasImporter(AgencyById.FILENAME);
	}

	public boolean hasCalendarImporter() {
		return hasImporter(CalendarByService.FILENAME);
	}

	public boolean hasCalendarDateImporter() {
		return hasImporter(CalendarDateByService.FILENAME);
	}

	public boolean hasFrequencyImporter() {
		return hasImporter(FrequencyByTrip.FILENAME);
	}

	public boolean hasRouteImporter() {
		return hasImporter(RouteById.FILENAME);
	}

	public boolean hasStopImporter() {
		return hasImporter(StopById.FILENAME);
	}

	public boolean hasStopTimeImporter() {
		return hasImporter(StopTimeByTrip.FILENAME);
	}

	public boolean hasTransferImporter() {
		return hasImporter(TransferByFromStop.FILENAME);
	}

	public boolean hasTripImporter() {
		return hasImporter(TripById.FILENAME);
	}
	
	public boolean hasShapeImporter() {
		return hasImporter(ShapeById.FILENAME);
	}

	private boolean hasImporter(String filename) {
		File f = new File(_path, filename);
		return f.exists();
	}

	@SuppressWarnings("unchecked")
	public Index<GtfsAgency> getAgencyById() {
		return getImporter(INDEX.AGENCY_BY_ID.name(), AgencyById.FILENAME,
				AgencyById.class);
	}

	@SuppressWarnings("unchecked")
	public Index<GtfsCalendar> getCalendarByService() {
		return getImporter(INDEX.CALENDAR_BY_SERVICE.name(),
				CalendarByService.FILENAME, CalendarByService.class);
	}

	@SuppressWarnings("unchecked")
	public Index<GtfsCalendarDate> getCalendarDateByService() {
		return getImporter(INDEX.CALENDAR_DATE_BY_SERVICE.name(),
				CalendarDateByService.FILENAME, CalendarDateByService.class);
	}

	@SuppressWarnings("unchecked")
	public Index<GtfsFrequency> getFrequencyByTrip() {
		return getImporter(INDEX.FREQUENCY_BY_TRIP.name(),
				FrequencyByTrip.FILENAME, FrequencyByTrip.class);
	}

	@SuppressWarnings("unchecked")
	public Index<GtfsRoute> getRouteById() {
		return getImporter(INDEX.ROUTE_BY_ID.name(), RouteById.FILENAME,
				RouteById.class);
	}

	@SuppressWarnings("unchecked")
	public Index<GtfsStop> getStopById() {
		return getImporter(INDEX.STOP_BY_ID.name(), StopById.FILENAME,
				StopById.class);
	}

	@SuppressWarnings("unchecked")
	public Index<GtfsStopTime> getStopTimeByTrip() {
		return getImporter(INDEX.STOP_TIME_BY_TRIP.name(),
				StopTimeByTrip.FILENAME, StopTimeByTrip.class);
	}

	@SuppressWarnings("unchecked")
	public Index<GtfsTransfer> getTransferByFromStop() {
		return getImporter(INDEX.TRANSFER_BY_FROM_STOP.name(),
				TransferByFromStop.FILENAME, TransferByFromStop.class);
	}

	@SuppressWarnings("unchecked")
	public Index<GtfsShape> getShapeById() {
		return getImporter(INDEX.SHAPE_BY_ID.name(), ShapeById.FILENAME,
				ShapeById.class);
	}

	@SuppressWarnings("unchecked")
	public Index<GtfsTrip> getTripById() {
		return getImporter(INDEX.TRIP_BY_ID.name(), TripById.FILENAME,
				TripById.class);
	}

	@SuppressWarnings("unchecked")
	public Index<GtfsTrip> getTripByRoute() {
		return getImporter(INDEX.TRIP_BY_ROUTE.name(), TripById.FILENAME,
				TripByRoute.class);
	}

	@SuppressWarnings("unchecked")
	public Index<GtfsTrip> getTripByService() {
		return getImporter(INDEX.TRIP_BY_SERVICE.name(), TripById.FILENAME,
				TripByRoute.class);
	}

}
