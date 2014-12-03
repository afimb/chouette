package fr.certu.chouette.exchange.gtfs.refactor.exporter;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsAgency;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendar;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendarDate;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsFrequency;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsRoute;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStop;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTransfer;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTrip;

public interface GtfsExporterInterface
{
   Exporter<GtfsAgency> getAgencyExporter() throws Exception;
   Exporter<GtfsCalendarDate> getCalendarDateExporter() throws Exception;
   Exporter<GtfsCalendar> getCalendarExporter() throws Exception;
   Exporter<GtfsFrequency> getFrequencyExporter() throws Exception;
   Exporter<GtfsRoute> getRouteExporter() throws Exception;
   Exporter<GtfsStop> getStopExporter() throws Exception;
   Exporter<GtfsStop> getStopExtendedExporter() throws Exception;
   Exporter<GtfsStopTime> getStopTimeExporter() throws Exception;
   Exporter<GtfsTransfer> getTransferExporter() throws Exception;
   Exporter<GtfsTrip> getTripExporter() throws Exception;
}
