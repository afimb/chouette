/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.gtfs.exporter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsAgencyProducer;
import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsRouteProducer;
import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsServiceProducer;
import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsStopProducer;
import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsTransferProducer;
import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsTripProducer;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.refactor.exporter.GtfsExporter;
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
   private static final Logger logger = Logger.getLogger(NeptuneData.class);
   @Getter
   Map<String,List<Timetable>> timetables = new HashMap<String,List<Timetable>>();
   @Getter
   Set<StopArea> physicalStops = new HashSet<StopArea>();
   @Getter
   Set<StopArea> commercialStops = new HashSet<StopArea>();
   @Getter
   Set<Company> companies = new HashSet<Company>();
   @Getter
   Set<ConnectionLink> connectionLinks = new HashSet<ConnectionLink>();

   /**
    * @param lines
    * @param report 
    */
   public void populateLines(List<Line> lines, GtfsExporter exporter, GtfsReport report)
   {
      GtfsServiceProducer calendarProducer = new GtfsServiceProducer(exporter);
      GtfsTripProducer tripProducer = new GtfsTripProducer(exporter);
      GtfsAgencyProducer agencyProducer = new GtfsAgencyProducer(exporter);
      GtfsStopProducer stopProducer = new GtfsStopProducer(exporter);
      GtfsRouteProducer routeProducer = new GtfsRouteProducer(exporter);
      GtfsTransferProducer transferProducer = new GtfsTransferProducer(exporter);      
      for (Iterator<Line> lineIterator = lines.iterator(); lineIterator.hasNext();)
      {
         Line line = lineIterator.next();
         lineIterator.remove();
         line.complete();
         if (line.getCompany() != null)
            companies.add(line.getCompany());
         if (line.getConnectionLinks() != null)
         {
            connectionLinks.addAll(line.getConnectionLinks());
         }
         
         boolean ok = routeProducer.save(line, report, "");
         if (!ok) continue;
         if (line.getRoutes() != null)
         {
            for (Route route : line.getRoutes())
            {
               for (StopPoint point : route.getStopPoints())
               {
                  StopArea area = point.getContainedInStopArea();
                  physicalStops.add(area);
                  if (area.getParent() != null
                        && area.getParent().hasCoordinates())
                  {
                     commercialStops.add(area.getParent());
                  }
               }
               for (JourneyPattern jp : route.getJourneyPatterns())
               {
                  for (VehicleJourney vj : jp.getVehicleJourneys())
                  {
                     // TODO : refactor
//                     vehicleJourneys.add(vj);
//                     for (Timetable timetable : vj.getTimetables())
//                     {
//                        timetables.add(timetable);
//                     }
                  }
               }
            }
         }
      }
      // remove incomplete connectionlinks
      for (Iterator<ConnectionLink> iterator = connectionLinks.iterator(); iterator
            .hasNext();)
      {
         ConnectionLink link = iterator.next();
         if (!physicalStops.contains(link.getStartOfLink())
               && !commercialStops.contains(link.getStartOfLink()))
         {
            iterator.remove();
         } else if (!physicalStops.contains(link.getEndOfLink())
               && !commercialStops.contains(link.getEndOfLink()))
         {
            iterator.remove();
         }
      }

   }

   public void populateStopAreas(List<StopArea> beans)
   {
      for (StopArea area : beans)
      {
         area.complete();
         if (area.getAreaType().equals(ChouetteAreaEnum.BoardingPosition)
               || area.getAreaType().equals(ChouetteAreaEnum.Quay))
         {
            if (area.hasCoordinates())
            {
               physicalStops.add(area);
               if (area.getConnectionLinks() != null)
                  connectionLinks.addAll(area.getConnectionLinks());

               if (area.getParent() != null
                     && area.getParent().hasCoordinates())
               {
                  commercialStops.add(area.getParent());
                  if (area.getParent().getConnectionLinks() != null)
                     connectionLinks.addAll(area.getParent()
                           .getConnectionLinks());
               }
            }
         }

      }
      // remove incomplete connectionlinks
      for (Iterator<ConnectionLink> iterator = connectionLinks.iterator(); iterator
            .hasNext();)
      {
         ConnectionLink link = iterator.next();
         if (!physicalStops.contains(link.getStartOfLink())
               && !commercialStops.contains(link.getStartOfLink()))
         {
            logger.info("missing start link for " + link.getObjectId());
            iterator.remove();
         } else if (!physicalStops.contains(link.getEndOfLink())
               && !commercialStops.contains(link.getEndOfLink()))
         {
            logger.info("missing end link for " + link.getObjectId());
            iterator.remove();
         }
      }

   }

}
