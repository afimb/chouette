/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.gtfs.exporter.producer;

import java.util.List;

import fr.certu.chouette.exchange.gtfs.model.GtfsRoute;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;

/**
 * convert Timetable to Gtfs Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
public class GtfsRouteProducer extends AbstractProducer<GtfsRoute, Route>
{

   @Override
   public List<GtfsRoute> produceAll(Route route)
   {
      throw new UnsupportedOperationException("not yet implemented");
   }


   @Override
   public GtfsRoute produce(Route neptuneObject)
   {
      GtfsRoute route = new GtfsRoute();
      route.setRouteId(neptuneObject.getObjectId());
      Line line = neptuneObject.getLine();
      route.setAgencyId(line.getCompany().getObjectId());
      route.setRouteShortName(line.getNumber());
      String routeLongName = "";
      if (line.getPublishedName() != null)
         routeLongName = line.getPublishedName();
      else
         routeLongName = line.getName();

      route.setRouteLongName(routeLongName);
      if (line.getComment() != null)
         route.setRouteDesc(line.getComment());
      if (line.getTransportModeName() != null)
      {
         switch (line.getTransportModeName())
         {
         case TRAMWAY:route.setRouteType(GtfsRoute.TRAM);break;
         case TROLLEYBUS:
         case COACH:
         case BUS : route.setRouteType(GtfsRoute.BUS);break;
         case METRO : route.setRouteType(GtfsRoute.SUBWAY);break;
         case LOCALTRAIN: 
         case LONGDISTANCETRAIN : 
         case TRAIN : route.setRouteType(GtfsRoute.RAIL);break;
         default: route.setRouteType(GtfsRoute.BUS);
         }
      }
      else
      {
         route.setRouteType(GtfsRoute.BUS);
      }
      return route;
   }


}
