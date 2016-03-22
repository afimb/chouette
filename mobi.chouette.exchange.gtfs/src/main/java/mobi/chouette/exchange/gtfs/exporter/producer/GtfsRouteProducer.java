/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.gtfs.exporter.producer;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.gtfs.exporter.GtfsExportParameters;
import mobi.chouette.exchange.gtfs.model.GtfsRoute;
import mobi.chouette.exchange.gtfs.model.exporter.GtfsExporterInterface;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.Line;

/**
 * convert Timetable to Gtfs Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
@Log4j
public class GtfsRouteProducer extends AbstractProducer {

   private GtfsExportParameters parameters;

   public GtfsRouteProducer(GtfsExporterInterface exporter) {
      super(exporter);
   }

   private GtfsRoute route = new GtfsRoute();

   public GtfsRouteProducer(GtfsExporterInterface exporter, GtfsExportParameters parameters) {
      super(exporter);
      this.parameters = parameters;
   }

   public boolean save(Line neptuneObject, ActionReport report, String prefix)
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
    	  // long and short name must be different
         route.setRouteLongName(null);
      }
//      if (!isEmpty(route.getRouteShortName()) && !isEmpty(route.getRouteLongName()))
//      {
//    	  // long name should not contains short name
//    	 if (route.getRouteLongName().contains(route.getRouteShortName()))
//    	 {
//    		 route.setRouteLongName(route.getRouteLongName().replace(route.getRouteShortName(), ""));
//    	 }
//      }

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
         if ("standard".equals(parameters.getRouteTypeIdScheme())) {
            convertStandard(neptuneObject);
         } else if ("extended".equals(parameters.getRouteTypeIdScheme())) {
            convertExtended(neptuneObject);
         } else {
            throw new IllegalArgumentException("Invalid route type id scheme '" + parameters.getRouteTypeIdScheme() + "'");
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

   private void convertStandard(Line neptuneObject) {
      switch (neptuneObject.getTransportModeName()) {
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
            route.setRouteType(GtfsRoute.RouteType.Metro);
            break;
         case RapidTransit:
         case LocalTrain:
         case LongDistanceTrain:
         case Train:
            route.setRouteType(GtfsRoute.RouteType.Railway);
            break;
         case Ferry:
            route.setRouteType(GtfsRoute.RouteType.Ferry);
            break;
         default:
            route.setRouteType(GtfsRoute.RouteType.Bus);
      }
   }


   private void convertExtended(Line neptuneObject) {
      switch (neptuneObject.getTransportModeName()) {
         case PrivateVehicle:
            route.setRouteType(GtfsRoute.RouteType.SelfDrive);
            break;
         case Waterborne:
            route.setRouteType(GtfsRoute.RouteType.WaterTransport);
            break;
         case Taxi:
            route.setRouteType(GtfsRoute.RouteType.Taxi);
            break;
         case Air:
            route.setRouteType(GtfsRoute.RouteType.Air);
            break;
         case Tramway:
            route.setRouteType(GtfsRoute.RouteType.Tram);
            break;
         case Trolleybus:
            route.setRouteType(GtfsRoute.RouteType.TrolleyBus);
            break;
         case Coach:
            route.setRouteType(GtfsRoute.RouteType.Coach);
            break;
         case Bus:
            route.setRouteType(GtfsRoute.RouteType.Bus);
            break;
         case Val:
         case Metro:
            route.setRouteType(GtfsRoute.RouteType.Metro);
            break;
         case RapidTransit:
            route.setRouteType(GtfsRoute.RouteType.Metro);
            break;
         case LocalTrain:
            route.setRouteType(GtfsRoute.RouteType.SuburbanRailway);
            break;
         case LongDistanceTrain:
         case Train:
            route.setRouteType(GtfsRoute.RouteType.Railway);
            break;
         case Ferry:
            route.setRouteType(GtfsRoute.RouteType.Ferry);
            break;
         case Other:
            route.setRouteType(GtfsRoute.RouteType.Miscellaneous);
            break;
         default:
            route.setRouteType(GtfsRoute.RouteType.Bus);
      }
   }
}