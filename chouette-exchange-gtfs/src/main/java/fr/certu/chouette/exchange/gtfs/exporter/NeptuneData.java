/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.gtfs.exporter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import lombok.Getter;
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
   List<Route> routes = new ArrayList<Route>();
   @Getter
   Set<Timetable> timetables = new HashSet<Timetable>();
   @Getter
   List<VehicleJourney> vehicleJourneys = new ArrayList<VehicleJourney>();
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
    */
   public void populateLines(List<Line> lines)
   {
      for (Line line : lines)
      {
         line.complete();
         if (line.getCompany() != null)
            companies.add(line.getCompany());
         if (line.getConnectionLinks() != null)
         {
            connectionLinks.addAll(line.getConnectionLinks());
         }
         if (line.getRoutes() != null)
         {
            for (Route route : line.getRoutes())
            {
               if (!"R".equals(route.getWayBack())
                     || route.getWayBackRouteId() == null)
               {
                  logger.info("route " + route.getObjectId() + " added");
                  routes.add(route);
               } else
               {
                  logger.info("route " + route.getObjectId()
                        + " bypassed, wayback is " + route.getWayBackRouteId());
               }
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
                     vehicleJourneys.add(vj);
                     for (Timetable timetable : vj.getTimetables())
                     {
                        timetables.add(timetable);
                     }
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
