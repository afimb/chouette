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
import java.util.List;
import java.util.Set;

import lombok.Getter;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;

/**
 *
 */
public class NeptuneData
{
   @Getter
   List<Route>          routes          = new ArrayList<Route>();
   @Getter
   Set<Timetable>       timetables      = new HashSet<Timetable>();
   @Getter
   List<VehicleJourney> vehicleJourneys = new ArrayList<VehicleJourney>();
   @Getter
   Set<StopArea>        physicalStops   = new HashSet<StopArea>();
   @Getter
   Set<Company>         companies       = new HashSet<Company>();

   /**
    * @param lines
    */
   public void populate(List<Line> lines)
   {
      for (Line line : lines)
      {
         line.complete();
         if (line.getCompany() != null)
            companies.add(line.getCompany());
         if (line.getRoutes() != null)
         {
            for (Route route : line.getRoutes())
            {
               if (!"R".equals(route.getWayBack()))
                  routes.add(route);
               for (StopPoint point : route.getStopPoints())
               {
                  physicalStops.add(point.getContainedInStopArea());
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
   }

}
