/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.gtfs.exporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsAgencyProducer;
import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsExtendedStopProducer;
import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsRouteProducer;
import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsServiceProducer;
import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsStopProducer;
import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsTransferProducer;
import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsTripProducer;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.refactor.exporter.GtfsExporter;
import fr.certu.chouette.export.metadata.model.Metadata;
import fr.certu.chouette.export.metadata.model.NeptuneObjectPresenter;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;

/**
 *
 */
public class NeptuneData
{

   /**
    * @param lines
    * @param report
    * @param timeZone 
    */
   public void saveLines(List<Line> lines, GtfsExporter exporter, GtfsReport report, String prefix, String sharedPrefix, TimeZone timeZone, Metadata metadata)
   {
      Map<String, List<Timetable>> timetables = new HashMap<String, List<Timetable>>();
      Set<StopArea> physicalStops = new HashSet<StopArea>();
      Set<StopArea> commercialStops = new HashSet<StopArea>();
      Set<Company> companies = new HashSet<Company>();
      Set<ConnectionLink> connectionLinks = new HashSet<ConnectionLink>();
      GtfsServiceProducer calendarProducer = new GtfsServiceProducer(exporter);
      GtfsTripProducer tripProducer = new GtfsTripProducer(exporter);
      GtfsAgencyProducer agencyProducer = new GtfsAgencyProducer(exporter);
      GtfsStopProducer stopProducer = new GtfsStopProducer(exporter);
      GtfsRouteProducer routeProducer = new GtfsRouteProducer(exporter);
      GtfsTransferProducer transferProducer = new GtfsTransferProducer(exporter);
      boolean hasLines = false;
      for (Iterator<Line> lineIterator = lines.iterator(); lineIterator.hasNext();)
      {
         Line line = lineIterator.next();
         lineIterator.remove();
         line.complete();

         if (line.getRoutes() != null)
         {
            boolean hasVj = false;
            for (Route route : line.getRoutes())
            {
               for (JourneyPattern jp : route.getJourneyPatterns())
               {
                  for (VehicleJourney vj : jp.getVehicleJourneys())
                  {
                     String tmKey = calendarProducer.key(vj.getTimetables(), sharedPrefix);
                     if (tmKey != null)
                     {
                        if (tripProducer.save(vj, tmKey, report, prefix, sharedPrefix))
                        {
                           hasVj = true;
                           if (!timetables.containsKey(tmKey))
                           {
                              timetables.put(tmKey, new ArrayList<Timetable>(vj.getTimetables()));
                           }
                        }
                     }
                  } // vj loop
               } // jp loop
               if (hasVj)
               {
                  for (StopPoint point : route.getStopPoints())
                  {
                     StopArea area = point.getContainedInStopArea();
                     physicalStops.add(area);
                     if (area.getParent() != null && area.getParent().hasCoordinates())
                     {
                        commercialStops.add(area.getParent());
                     }
                  }
               }
            } // route loop
            if (hasVj)
            {
               routeProducer.save(line, report, prefix);
               hasLines = true;
               metadata.getResources().add(metadata.new Resource( 
                     NeptuneObjectPresenter.getName(line.getPtNetwork()), NeptuneObjectPresenter.getName(line)));
               if (line.getCompany() != null)
                  companies.add(line.getCompany());
               if (line.getConnectionLinks() != null)
               {
                  connectionLinks.addAll(line.getConnectionLinks());
               }
            }
         }
      }
      if (hasLines)
      {
         for (Iterator<StopArea> iterator = commercialStops.iterator(); iterator.hasNext();)
         {
            StopArea stop = iterator.next();
            if (!stopProducer.save(stop, report, sharedPrefix, null))
            {
               iterator.remove();
            }
            else
            {
               if (stop.hasCoordinates())
                  metadata.getSpatialCoverage().update(stop.getLongitude().doubleValue(), stop.getLatitude().doubleValue());
            }
         }
         for (StopArea stop : physicalStops)
         {
            stopProducer.save(stop, report, sharedPrefix, commercialStops);
            if (stop.hasCoordinates())
               metadata.getSpatialCoverage().update(stop.getLongitude().doubleValue(), stop.getLatitude().doubleValue());
         }
         // remove incomplete connectionlinks
         for (ConnectionLink link : connectionLinks)
         {
            if (!physicalStops.contains(link.getStartOfLink()) && !commercialStops.contains(link.getStartOfLink()))
            {
               continue;
            }
            else if (!physicalStops.contains(link.getEndOfLink()) && !commercialStops.contains(link.getEndOfLink()))
            {
               continue;
            }
            transferProducer.save(link, report, sharedPrefix);
         }

         for (Company company : companies)
         {
            agencyProducer.save(company, report, prefix, timeZone);
         }

         for (List<Timetable> tms : timetables.values())
         {
            calendarProducer.save(tms, report, sharedPrefix);
            for (Timetable tm : tms)
            {
               metadata.getTemporalCoverage().update(tm.getStartOfPeriod(), tm.getEndOfPeriod());
            }
         }

      }

   }

   public void saveStopAreas(List<StopArea> beans, GtfsExporter exporter, GtfsReport report, String sharedPrefix, Metadata metadata)
   {
      Set<StopArea> physicalStops = new HashSet<StopArea>();
      Set<StopArea> commercialStops = new HashSet<StopArea>();
      Set<ConnectionLink> connectionLinks = new HashSet<ConnectionLink>();
      GtfsExtendedStopProducer stopProducer = new GtfsExtendedStopProducer(exporter);
      GtfsTransferProducer transferProducer = new GtfsTransferProducer(exporter);
      metadata.setDescription("limited to stops and transfers");
      for (StopArea area : beans)
      {
         area.complete();
         if (area.getAreaType().equals(ChouetteAreaEnum.BoardingPosition) || area.getAreaType().equals(ChouetteAreaEnum.Quay))
         {
            if (area.hasCoordinates())
            {
               physicalStops.add(area);
               if (area.getConnectionLinks() != null)
                  connectionLinks.addAll(area.getConnectionLinks());

               if (area.getParent() != null && area.getParent().hasCoordinates())
               {
                  commercialStops.add(area.getParent());
                  if (area.getParent().getConnectionLinks() != null)
                     connectionLinks.addAll(area.getParent().getConnectionLinks());
               }
            }
         }

      }
      for (Iterator<StopArea> iterator = commercialStops.iterator(); iterator.hasNext();)
      {
         StopArea stop = iterator.next();
         if (!stopProducer.save(stop, report, sharedPrefix, null))
         {
            iterator.remove();
         }
         else
         {
            if (stop.hasCoordinates())
               metadata.getSpatialCoverage().update(stop.getLongitude().doubleValue(), stop.getLatitude().doubleValue());
         }
      }
      for (StopArea stop : physicalStops)
      {
         stopProducer.save(stop, report, sharedPrefix, commercialStops);
         if (stop.hasCoordinates())
            metadata.getSpatialCoverage().update(stop.getLongitude().doubleValue(), stop.getLatitude().doubleValue());
      }
      // remove incomplete connectionlinks
      for (ConnectionLink link : connectionLinks)
      {
         if (!physicalStops.contains(link.getStartOfLink()) && !commercialStops.contains(link.getStartOfLink()))
         {
            continue;
         }
         else if (!physicalStops.contains(link.getEndOfLink()) && !commercialStops.contains(link.getEndOfLink()))
         {
            continue;
         }
         transferProducer.save(link, report, sharedPrefix);
      }

   }

}
