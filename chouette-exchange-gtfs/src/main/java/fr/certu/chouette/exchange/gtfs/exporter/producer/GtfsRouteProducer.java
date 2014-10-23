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
import fr.certu.chouette.plugin.report.Report.STATE;

/**
 * convert Timetable to Gtfs Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
public class GtfsRouteProducer extends AbstractProducer<GtfsRoute, Line>
{
   private static final Logger logger = Logger
         .getLogger(GtfsRouteProducer.class);

   @Override
   public List<GtfsRoute> produceAll(Line neptuneObject, GtfsReport report)
   {
      throw new UnsupportedOperationException("not yet implemented");
   }

   @Override
   public GtfsRoute produce(Line neptuneObject, GtfsReport report)
   {
      GtfsRoute route = new GtfsRoute();
      route.setRouteId(toGtfsId(neptuneObject.getObjectId()));
      route.setAgencyId(toGtfsId(neptuneObject.getCompany().getObjectId()));
      route.setRouteShortName(neptuneObject.getName());

      route.setRouteLongName(neptuneObject.getPublishedName());

      // Gtfs Route require short or long name
      if (isEmpty(route.getRouteShortName())
            && isEmpty(route.getRouteLongName()))
      {
         if (isEmpty(neptuneObject.getNumber()))
         {
            logger.error("no short or long name for route : "
                  + neptuneObject.getObjectId());
            GtfsReportItem item = new GtfsReportItem(
                  GtfsReportItem.KEY.MISSING_DATA, STATE.ERROR, "Route",
                  neptuneObject.getObjectId(), "Name or line number");
            report.addItem(item);
            return null;
         }
      }

      if (!isEmpty(neptuneObject.getComment()))
         route.setRouteDesc(neptuneObject.getComment());
      if (neptuneObject.getTransportModeName() != null)
      {
         switch (neptuneObject.getTransportModeName())
         {
         case Tramway:
            route.setRouteType(GtfsRoute.TRAM);
            break;
         case Trolleybus:
         case Coach:
         case Bus:
            route.setRouteType(GtfsRoute.BUS);
            break;
         case Val:
         case Metro:
            route.setRouteType(GtfsRoute.SUBWAY);
            break;
         case RapidTransit:
         case LocalTrain:
         case LongDistanceTrain:
         case Train:
            route.setRouteType(GtfsRoute.RAIL);
            break;
         case Ferry:
            route.setRouteType(GtfsRoute.FERRY);
            break;
         default:
            route.setRouteType(GtfsRoute.BUS);
         }
      } else
      {
         route.setRouteType(GtfsRoute.BUS);
      }
      return route;
   }

}
