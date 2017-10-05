/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.gtfs.exporter.producer;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.gtfs.model.GtfsRoute;
import mobi.chouette.exchange.gtfs.model.RouteTypeEnum;
import mobi.chouette.exchange.gtfs.model.exporter.GtfsExporterInterface;
import mobi.chouette.model.Company;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.type.OrganisationTypeEnum;

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

   public boolean save(Line neptuneObject,  String prefix,boolean keepOriginalId, boolean useTPEGRouteTypes)
   {
      route.setRouteId(toGtfsId(neptuneObject.getObjectId(), prefix,keepOriginalId));
      Company c = neptuneObject.getCompany();

       String agencyId;
       if (c == null) {
           agencyId = neptuneObject.getNetwork().getObjectId();
       } else {
           // Use network->authority as agency if it is an authority
           if (!OrganisationTypeEnum.Authority.equals(c.getOrganisationType())) {
               Network network = neptuneObject.getNetwork();
               if (network != null && network.getCompany() != null) {
                   if (OrganisationTypeEnum.Authority.equals(network.getCompany().getOrganisationType())) {
                       c = network.getCompany();
                   }
               }

           }
           agencyId = c.getObjectId();
       }
       route.setAgencyId(toGtfsId(agencyId, prefix, keepOriginalId));
       route.setRouteShortName(null);
       route.setRouteLongName(null);
      
      route.setRouteShortName(neptuneObject.getNumber());
      
      if(!isEmpty(neptuneObject.getPublishedName())) {
    	  route.setRouteLongName(neptuneObject.getPublishedName());
      } else {
    	  route.setRouteLongName(neptuneObject.getName());
      }
      
//      if(isEmpty(neptuneObject.get))
//      if (isEmpty(neptuneObject.getNumber()))
//      {
//         route.setRouteShortName(neptuneObject.getName());
//      }
//      else
//      {
//         route.setRouteShortName(neptuneObject.getNumber());
//      }
//
//      if (isEmpty(neptuneObject.getPublishedName()))
//      {
//         route.setRouteLongName(neptuneObject.getName());
//      }
//      else
//      {
//         route.setRouteLongName(neptuneObject.getPublishedName());
//      }
//      
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
         if(useTPEGRouteTypes) {
        	 route.setRouteType(RouteTypeEnum.from(neptuneObject.getTransportModeName(), neptuneObject.getTransportSubModeName()));
         } else {
    	  
             switch (neptuneObject.getTransportModeName())
             {
             case Tram:
                route.setRouteType(RouteTypeEnum.Tram);
                break;
             case Metro:
                route.setRouteType(RouteTypeEnum.Subway);
                break;
             case Rail:
                route.setRouteType(RouteTypeEnum.Rail);
                break;
             case Water:
             case Ferry:
                route.setRouteType(RouteTypeEnum.Ferry);
                break;
             case Funicular:
            	 route.setRouteType(RouteTypeEnum.Funicular);
             case Cableway:
            	 route.setRouteType(RouteTypeEnum.Gondola);
             case TrolleyBus:
             case Coach:
             case Bus:
             default:
                route.setRouteType(RouteTypeEnum.Bus);
             }
         }
      
      }
      else
      {
         route.setRouteType(RouteTypeEnum.Bus);
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
