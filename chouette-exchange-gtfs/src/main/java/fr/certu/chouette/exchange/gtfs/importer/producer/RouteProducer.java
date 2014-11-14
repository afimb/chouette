package fr.certu.chouette.exchange.gtfs.importer.producer;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsRoute;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.plugin.report.Report;

public class RouteProducer extends AbstractModelProducer<Route, GtfsRoute>
{
   private static Logger logger = Logger.getLogger(RouteProducer.class);

   public Route produce(GtfsRoute gtfsRoute, Report report)
   {
      Route route = new Route();

      // objectId, objectVersion, creatorId, creationTime
      route.setObjectId(composeIncrementalObjectId(Route.ROUTE_KEY, gtfsRoute.getRouteId(), logger));

      route.setWayBack("A");

      return route;
   }

   public void update(Route route)
   {
      if (route.getName() == null)
      {
         if (!route.getStopPoints().isEmpty())
         {
            String first = route.getStopPoints().get(0).getContainedInStopArea().getName();
            String last = route.getStopPoints().get(route.getStopPoints().size() - 1).getContainedInStopArea().getName();
            route.setName(first + " -> " + last);
         }
      }
   }
}
