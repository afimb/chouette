/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.gtfs.exporter.producer;

import java.util.List;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReportItem;
import fr.certu.chouette.exchange.gtfs.model.GtfsRoute;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.plugin.report.Report.STATE;

/**
 * convert Timetable to Gtfs Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
public class GtfsRouteProducer extends AbstractProducer<GtfsRoute, Route>
{
   private static final Logger logger = Logger.getLogger(GtfsRouteProducer.class);

   @Override
   public List<GtfsRoute> produceAll(Route route,GtfsReport report)
   {
      throw new UnsupportedOperationException("not yet implemented");
   }


   @Override
   public GtfsRoute produce(Route neptuneObject,GtfsReport report)
   {
      GtfsRoute route = new GtfsRoute();
      route.setRouteId(toGtfsId(neptuneObject.getObjectId()));
      Line line = neptuneObject.getLine();
      route.setAgencyId(toGtfsId(line.getCompany().getObjectId()));
      route.setRouteShortName(line.getNumber());

      String routeLongName = "";
      String nameExtent = "";
      if (neptuneObject.getWayBackRouteId() == null)
      {
         if (neptuneObject.getPublishedName() != null)
         {
            routeLongName = neptuneObject.getPublishedName();
         }
         else if (neptuneObject.getName() != null)
         {
            routeLongName = neptuneObject.getName();
         }
         else
         {
            nameExtent = ("A".equals(neptuneObject.getWayBack())?" - Aller":" - Retour");
         }
      }
      if (routeLongName.isEmpty())
      {
         if (line.getPublishedName() != null)
            routeLongName = line.getPublishedName()+nameExtent;
         else if (line.getName() != null)
            routeLongName = line.getName()+nameExtent;
      }

      // Gtfs Route require short or long name 
      if (line.getNumber() == null && routeLongName.isEmpty())
      {
         logger.error("no short or long name for route : "+neptuneObject.getObjectId());
         GtfsReportItem item = new GtfsReportItem(GtfsReportItem.KEY.MISSING_DATA, STATE.ERROR, "Route",neptuneObject.getObjectId(),"Name or line number");
         report.addItem(item);
         return null;
      }

      route.setRouteLongName(routeLongName);
      if (line.getComment() != null)
         route.setRouteDesc(line.getComment());
      if (line.getTransportModeName() != null)
      {
         switch (line.getTransportModeName())
         {
         case Tramway:route.setRouteType(GtfsRoute.TRAM);break;
         case Trolleybus:
         case Coach:
         case Bus : route.setRouteType(GtfsRoute.BUS);break;
         case Val :
         case Metro : route.setRouteType(GtfsRoute.SUBWAY);break;
         case RapidTransit: 
         case LocalTrain: 
         case LongDistanceTrain : 
         case Train : route.setRouteType(GtfsRoute.RAIL);break;
         case Ferry : route.setRouteType(GtfsRoute.FERRY);break;
         case Waterborne : route.setRouteType(GtfsRoute.SUSPENDED_CAR);break;
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
