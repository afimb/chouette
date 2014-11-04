/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.gtfs.exporter.producer;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReportItem;
import fr.certu.chouette.exchange.gtfs.refactor.exporter.GtfsExporter;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsRoute;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.report.Report.STATE;

/**
 * convert Timetable to Gtfs Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
public class GtfsRouteProducer extends AbstractProducer
{
   public GtfsRouteProducer(GtfsExporter exporter)
   {
      super(exporter);
   }


   private static final Logger logger = Logger
         .getLogger(GtfsRouteProducer.class);
   
   private GtfsRoute route = new GtfsRoute();
   

   public boolean save(Line neptuneObject, GtfsReport report, String prefix)
   {
      route.setRouteId(toGtfsId(neptuneObject.getObjectId(),prefix));
      route.setAgencyId(toGtfsId(neptuneObject.getCompany().getObjectId(),prefix));
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
            return false;
         }
      }

      route.setRouteDesc(neptuneObject.getComment());
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
      } else
      {
         route.setRouteType(GtfsRoute.RouteType.Bus);
      }
      
      try
      {
         getExporter().getRouteExporter().export(route);
      }
      catch (Exception e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
         return false;
      }
      
      return true;
   }

}
