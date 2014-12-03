/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.gtfs.exporter.producer;

import lombok.extern.log4j.Log4j;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.refactor.exporter.GtfsExporterInterface;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsRoute;
import fr.certu.chouette.model.neptune.Line;

/**
 * convert Timetable to Gtfs Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
@Log4j
public class GtfsRouteProducer extends AbstractProducer
{
   public GtfsRouteProducer(GtfsExporterInterface exporter)
   {
      super(exporter);
   }

   private GtfsRoute route = new GtfsRoute();

   public boolean save(Line neptuneObject, GtfsReport report, String prefix)
   {
      route.setRouteId(toGtfsId(neptuneObject.getObjectId(), prefix));
      route.setAgencyId(toGtfsId(neptuneObject.getCompany().getObjectId(), prefix));
      route.setRouteShortName(null);
      route.setRouteLongName(null);
      if (isEmpty(neptuneObject.getNumber()))
      {
         route.setRouteShortName(neptuneObject.getName());
      }
      else
      {
         route.setRouteShortName(neptuneObject.getNumber());
      }

      if (isEmpty(neptuneObject.getPublishedName()))
      {
         route.setRouteLongName(neptuneObject.getName());
      }
      else
      {
         route.setRouteLongName(neptuneObject.getPublishedName());
      }
      if (isEmpty(route.getRouteShortName()) && isEmpty(route.getRouteLongName()))
      {
          log.warn("no naming data for line "+neptuneObject.getObjectId());
          return false;
      }
      if (!isEmpty(route.getRouteShortName()) && route.getRouteShortName().equals(route.getRouteLongName()))
      {
         route.setRouteLongName(null);
      }

      route.setRouteDesc(null);
      if (!isEmpty(neptuneObject.getComment()))
      {
         if (!neptuneObject.getComment().equals(route.getRouteShortName()) && !neptuneObject.getComment().equals(route.getRouteLongName()))
            route.setRouteDesc(neptuneObject.getComment());
      }

      route.setRouteColor(getColor(neptuneObject.getColor()));
      route.setRouteTextColor(getColor(neptuneObject.getTextColor()));
      route.setRouteUrl(getUrl(neptuneObject.getUrl()));

      if (neptuneObject.getTransportModeName() != null)
      {
         switch (neptuneObject.getTransportModeName())
         {
         case Tramway:
            route.setRouteType(GtfsRoute.RouteType.Tram);
            break;
         case Trolleybus:
         case Coach:
         case Bus:
            route.setRouteType(GtfsRoute.RouteType.Bus);
            break;
         case Val:
         case Metro:
            route.setRouteType(GtfsRoute.RouteType.Subway);
            break;
         case RapidTransit:
         case LocalTrain:
         case LongDistanceTrain:
         case Train:
            route.setRouteType(GtfsRoute.RouteType.Rail);
            break;
         case Ferry:
            route.setRouteType(GtfsRoute.RouteType.Ferry);
            break;
         default:
            route.setRouteType(GtfsRoute.RouteType.Bus);
         }
      }
      else
      {
         route.setRouteType(GtfsRoute.RouteType.Bus);
      }

      try
      {
         getExporter().getRouteExporter().export(route);
      }
      catch (Exception e)
      {
         log.warn("export failed for line "+neptuneObject.getObjectId(),e);
         return false;
      }

      return true;
   }
}
