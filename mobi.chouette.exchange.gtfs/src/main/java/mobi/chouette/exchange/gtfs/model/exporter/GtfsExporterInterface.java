package mobi.chouette.exchange.gtfs.model.exporter;

import mobi.chouette.exchange.gtfs.model.GtfsAgency;
import mobi.chouette.exchange.gtfs.model.GtfsCalendar;
import mobi.chouette.exchange.gtfs.model.GtfsCalendarDate;
import mobi.chouette.exchange.gtfs.model.GtfsFrequency;
import mobi.chouette.exchange.gtfs.model.GtfsRoute;
import mobi.chouette.exchange.gtfs.model.GtfsShape;
import mobi.chouette.exchange.gtfs.model.GtfsStop;
import mobi.chouette.exchange.gtfs.model.GtfsStopTime;
import mobi.chouette.exchange.gtfs.model.GtfsTransfer;
import mobi.chouette.exchange.gtfs.model.GtfsTrip;

public interface GtfsExporterInterface {
	Exporter<GtfsAgency> getAgencyExporter() throws Exception;

	Exporter<GtfsCalendarDate> getCalendarDateExporter() throws Exception;

	Exporter<GtfsCalendar> getCalendarExporter() throws Exception;

	Exporter<GtfsFrequency> getFrequencyExporter() throws Exception;

	Exporter<GtfsRoute> getRouteExporter() throws Exception;

	Exporter<GtfsShape> getShapeExporter() throws Exception;

	Exporter<GtfsStop> getStopExporter() throws Exception;

	Exporter<GtfsStop> getStopExtendedExporter() throws Exception;

	Exporter<GtfsStopTime> getStopTimeExporter() throws Exception;

	Exporter<GtfsTransfer> getTransferExporter() throws Exception;

	Exporter<GtfsTrip> getTripExporter() throws Exception;
}
