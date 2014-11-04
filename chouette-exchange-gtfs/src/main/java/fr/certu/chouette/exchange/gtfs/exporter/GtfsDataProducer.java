/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.gtfs.exporter;

import java.util.Iterator;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsAgencyProducer;
import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsServiceProducer;
import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsExtendedStopProducer;
import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsRouteProducer;
import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsStopProducer;
import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsTransferProducer;
import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsTripProducer;
import fr.certu.chouette.exchange.gtfs.exporter.producer.IGtfsProducer;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReportItem;
import fr.certu.chouette.exchange.gtfs.model.GtfsAgency;
import fr.certu.chouette.exchange.gtfs.model.GtfsCalendar;
import fr.certu.chouette.exchange.gtfs.model.GtfsExtendedStop;
import fr.certu.chouette.exchange.gtfs.model.GtfsRoute;
import fr.certu.chouette.exchange.gtfs.model.GtfsStop;
import fr.certu.chouette.exchange.gtfs.model.GtfsTransfer;
import fr.certu.chouette.exchange.gtfs.model.GtfsTrip;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.Report.STATE;

/**
 * 
 */
public class GtfsDataProducer
{
   private static final Logger logger = Logger
         .getLogger(GtfsDataProducer.class);

   public GtfsData produceAll(NeptuneData neptuneData, TimeZone timeZone,
         GtfsReport report) throws GtfsExportException
   {
      GtfsData gtfsData = new GtfsData();
      return gtfsData;
   }

   public GtfsData produceStops(NeptuneData neptuneData, GtfsReport report)
         throws GtfsExportException
   {
      GtfsData gtfsData = new GtfsData();
      return gtfsData;
   }

}
