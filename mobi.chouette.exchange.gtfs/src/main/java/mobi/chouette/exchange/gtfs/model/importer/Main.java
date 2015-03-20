package mobi.chouette.exchange.gtfs.model.importer;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.gtfs.model.GtfsAgency;
import mobi.chouette.exchange.gtfs.model.GtfsCalendar;
import mobi.chouette.exchange.gtfs.model.GtfsCalendarDate;
import mobi.chouette.exchange.gtfs.model.GtfsRoute;
import mobi.chouette.exchange.gtfs.model.GtfsStop;
import mobi.chouette.exchange.gtfs.model.GtfsStopTime;
import mobi.chouette.exchange.gtfs.model.GtfsTransfer;
import mobi.chouette.exchange.gtfs.model.GtfsTrip;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class Main {

	private int _count;
	private static final String PATH = "/opt/tmp/RENNES/";

	public static void main(String[] args) {
		BasicConfigurator.configure();
		Logger.getLogger("org.infinispan").setLevel(Level.WARN);
		Main main = new Main();

		Monitor monitor = MonitorFactory.start();
		main.test();
		log.debug("[DSU] total : " + monitor.stop());
	}

	@SuppressWarnings("rawtypes")
	private void parse(GtfsImporter dao, String name, String path, Class clazz) {

		try {

			Monitor monitor = MonitorFactory.start();
			Index parser = dao.getImporter(name, path, clazz);
			_count = 0;
			for (Object bean : parser) {
				System.out.println("[DSU] value : " + bean);
				// parser.validate(bean, dao);
				_count++;
			}

			log.debug("[DSU] get " + _count + " object " + monitor.stop());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private void todo() {
		GtfsImporter dao = new GtfsImporter(PATH);

		// routes.txt
		// parse(dao, GtfsImporter.INDEX.ROUTE_BY_ID.name(), RouteById.FILENAME,
		// RouteById.class);

		// trips.txt

		parse(dao, GtfsImporter.INDEX.TRIP_BY_ROUTE.name(), TripById.FILENAME,
				TripByRoute.class);

		dao.dispose();

	}

	@SuppressWarnings("unused")
	private void execute() {
		GtfsImporter dao = new GtfsImporter(PATH);

		// stop_times.txt
		parse(dao, GtfsImporter.INDEX.STOP_TIME_BY_TRIP.name(),
				StopTimeByTrip.FILENAME, StopTimeByTrip.class);

		// trips.txt
		parse(dao, GtfsImporter.INDEX.TRIP_BY_ID.name(), TripById.FILENAME,
				TripById.class);
		parse(dao, GtfsImporter.INDEX.TRIP_BY_ROUTE.name(), TripById.FILENAME,
				TripByRoute.class);
		// parse(dao, GtfsImporter.INDEX.TRIP_BY_SERVICE.name(),
		// TripById.FILENAME,
		// TripByService.class);

		// routes.txt
		parse(dao, GtfsImporter.INDEX.ROUTE_BY_ID.name(), RouteById.FILENAME,
				RouteById.class);

		// stops.txt
		parse(dao, GtfsImporter.INDEX.STOP_BY_ID.name(), StopById.FILENAME,
				StopById.class);

		// calendar.txt
		parse(dao, GtfsImporter.INDEX.CALENDAR_DATE_BY_SERVICE.name(),
				CalendarDateByService.FILENAME, CalendarDateByService.class);

		// calendar_dates.txt
		parse(dao, GtfsImporter.INDEX.CALENDAR_BY_SERVICE.name(),
				CalendarByService.FILENAME, CalendarByService.class);

		// transfers.txt
		parse(dao, GtfsImporter.INDEX.TRANSFER_BY_FROM_STOP.name(),
				TransferByFromStop.FILENAME, TransferByFromStop.class);

		// agency.txt
		parse(dao, GtfsImporter.INDEX.AGENCY_BY_ID.name(), AgencyById.FILENAME,
				AgencyById.class);

		// frequencies.txt
		if (dao.hasFrequencyImporter()) {
			parse(dao, GtfsImporter.INDEX.FREQUENCY_BY_TRIP.name(),
					FrequencyByTrip.FILENAME, FrequencyByTrip.class);
		}

		dao.dispose();

	}

	@SuppressWarnings("unused")
	private void test() {

		printMemory();

		GtfsImporter dao = new GtfsImporter(PATH);

		Map<String, GtfsStop> _map = new HashMap<String, GtfsStop>();

		Index<GtfsRoute> routes = dao.getRouteById();
		for (GtfsRoute route : routes) {
			// System.out.println(route);
			routes.validate(route, dao);

			Index<GtfsAgency> agencies = dao.getAgencyById();
			GtfsAgency agency = agencies.getValue(route.getAgencyId());
			agencies.validate(agency, dao);
			// System.out.println(agency);

			Index<GtfsTrip> trips = dao.getTripByRoute();
			for (GtfsTrip trip : trips.values(route.getRouteId())) {

				// System.out.println(trip);
				trips.validate(trip, dao);

				Index<GtfsStopTime> stopTimes = dao.getStopTimeByTrip();
				for (GtfsStopTime stopTime : stopTimes.values(trip.getTripId())) {
					// System.out.println(stopTime);
					stopTimes.validate(stopTime, dao);

					GtfsStop stop = _map.get(stopTime.getStopId());
					if (stop == null) {
						Index<GtfsStop> stops = dao.getStopById();
						stop = stops.getValue(stopTime.getStopId());
						stops.validate(stop, dao);
						GtfsStop clone = new GtfsStop(stop.getStopId(),
								stop.getStopCode(), stop.getStopName(),
								stop.getStopDesc(), stop.getStopLat(),
								stop.getStopLon(), stop.getZoneId(),
								stop.getStopUrl(), stop.getLocationType(),
								stop.getParentStation(),
								stop.getStopTimezone(),
								stop.getWheelchairBoarding(),
								stop.getAddressLine(), stop.getLocality(),
								stop.getPostalCode());
						_map.put(stop.getStopId(), clone);
					}
				}

				Index<GtfsCalendar> calendars = dao.getCalendarByService();
				for (GtfsCalendar calendar : calendars.values(trip
						.getServiceId())) {
					// System.out.println(calendar);
					calendars.validate(calendar, dao);
				}

				Index<GtfsCalendarDate> dates = dao.getCalendarDateByService();
				for (GtfsCalendarDate date : dates.values(trip.getServiceId())) {
					// System.out.println(date);
					dates.validate(date, dao);

				}

			}
		}

		if (dao.hasTransferImporter()) {
			Index<GtfsTransfer> transfers = dao.getTransferByFromStop();
			for (GtfsTransfer transfer : transfers) {
				transfers.validate(transfer, dao);
				GtfsStop from = dao.getStopById().getValue(
						transfer.getFromStopId());
				GtfsStop to = dao.getStopById()
						.getValue(transfer.getToStopId());
			}
		}
		printMemory();

		dao.dispose();

		printMemory();

	}

	public static void printMemory() {

		final int MB = 1024 * 1024;
		Runtime runtime = Runtime.getRuntime();

		runtime.gc();
		runtime.gc();
		runtime.gc();

		System.out.println("\n##### Heap utilization statistics [MB] #####");
		System.out.println("Used Memory:"
				+ (runtime.totalMemory() - runtime.freeMemory()) / MB);
		System.out.println("Free Memory:" + runtime.freeMemory() / MB);
		System.out.println("Total Memory:" + runtime.totalMemory() / MB);
		System.out.println("Max Memory:" + runtime.maxMemory() / MB);

	}
}
