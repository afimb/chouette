package mobi.chouette.exchange.gtfs.model.exporter;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.gtfs.model.GtfsAgency;
import mobi.chouette.exchange.gtfs.model.GtfsCalendar;
import mobi.chouette.exchange.gtfs.model.GtfsCalendarDate;
import mobi.chouette.exchange.gtfs.model.GtfsFrequency;
import mobi.chouette.exchange.gtfs.model.GtfsObject;
import mobi.chouette.exchange.gtfs.model.GtfsRoute;
import mobi.chouette.exchange.gtfs.model.GtfsStop;
import mobi.chouette.exchange.gtfs.model.GtfsStopTime;
import mobi.chouette.exchange.gtfs.model.GtfsTransfer;
import mobi.chouette.exchange.gtfs.model.GtfsTrip;
import mobi.chouette.exchange.gtfs.model.importer.Context;
import mobi.chouette.exchange.gtfs.model.importer.GtfsException;
import mobi.chouette.exchange.gtfs.model.importer.GtfsException.ERROR;

@Log4j
public class GtfsExporter implements GtfsExporterInterface {
	public static enum EXPORTER {
		AGENCY, CALENDAR, CALENDAR_DATE, FREQUENCY, ROUTE, STOP, STOP_TIME, TRANSFER, TRIP;
	}

	private String _path;
	private Map<String, Exporter<GtfsObject>> _map = new HashMap<String, Exporter<GtfsObject>>();

	public GtfsExporter(String path) {
		_path = path;
	}

	public void dispose() {
		for (Exporter exporter : _map.values()) {
			try {
				exporter.dispose();
			} catch (IOException e) {
				log.error(e);
			}
		}
		_map.clear();
	}

	public Exporter getExporter(String name, String path, Class clazz) {
		Exporter result = _map.get(name);

		if (result == null) {
			try {
				result = ExporterFactory.build(Paths.get(_path, path)
						.toString(), clazz.getName());
				_map.put(name, result);
			} catch (ClassNotFoundException | IOException e) {
				Context context = new Context();
				context.put(Context.PATH, _path);
				context.put(Context.ERROR, ERROR.SYSTEM);
				throw new GtfsException(context, e);
			}

		}
		return result;
	}

	public Exporter<GtfsAgency> getAgencyExporter() throws Exception {
		return getExporter(EXPORTER.AGENCY.name(), AgencyExporter.FILENAME,
				AgencyExporter.class);

	}

	public Exporter<GtfsCalendarDate> getCalendarDateExporter()
			throws Exception {
		return getExporter(EXPORTER.CALENDAR_DATE.name(),
				CalendarDateExporter.FILENAME, CalendarDateExporter.class);
	}

	public Exporter<GtfsCalendar> getCalendarExporter() throws Exception {
		return getExporter(EXPORTER.CALENDAR.name(), CalendarExporter.FILENAME,
				CalendarExporter.class);
	}

	public Exporter<GtfsFrequency> getFrequencyExporter() throws Exception {
		return getExporter(EXPORTER.FREQUENCY.name(),
				FrequencyExporter.FILENAME, FrequencyExporter.class);
	}

	public Exporter<GtfsRoute> getRouteExporter() throws Exception {
		return getExporter(EXPORTER.ROUTE.name(), RouteExporter.FILENAME,
				RouteExporter.class);
	}

	public Exporter<GtfsStop> getStopExporter() throws Exception {
		return getExporter(EXPORTER.STOP.name(), StopExporter.FILENAME,
				StopExporter.class);
	}

	public Exporter<GtfsStop> getStopExtendedExporter() throws Exception {
		return getExporter(EXPORTER.STOP.name(), StopExporter.FILENAME,
				StopExtendedExporter.class);
	}

	public Exporter<GtfsStopTime> getStopTimeExporter() throws Exception {
		return getExporter(EXPORTER.STOP_TIME.name(),
				StopTimeExporter.FILENAME, StopTimeExporter.class);
	}

	public Exporter<GtfsTransfer> getTransferExporter() throws Exception {
		return getExporter(EXPORTER.TRANSFER.name(), TransferExporter.FILENAME,
				TransferExporter.class);
	}

	public Exporter<GtfsTrip> getTripExporter() throws Exception {
		return getExporter(EXPORTER.TRIP.name(), TripExporter.FILENAME,
				TripExporter.class);
	}

}
