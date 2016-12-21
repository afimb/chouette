/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.gtfs.exporter.producer;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.TransportMode;
import mobi.chouette.exchange.TransportModeConverter;
import mobi.chouette.exchange.gtfs.GtfsTransportModeConverter;
import mobi.chouette.exchange.gtfs.model.GtfsRoute;
import mobi.chouette.exchange.gtfs.model.RouteTypeEnum;
import mobi.chouette.exchange.gtfs.model.exporter.GtfsExporterInterface;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.model.Line;

/**
 * convert Timetable to Gtfs Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
@Log4j
public class GtfsRouteProducer extends AbstractProducer implements Constant
{
   public GtfsRouteProducer(GtfsExporterInterface exporter)
   {
      super(exporter);
   }

   private GtfsRoute route = new GtfsRoute();

   public boolean save(Context context, Line neptuneObject,  String prefix, boolean keepOriginalId)
   {
	   AbstractParameter parameters = (AbstractParameter)context.get(CONFIGURATION);
	   TransportModeConverter tmc = (TransportModeConverter) context.get(TRANSPORT_MODE_CONVERTER);
	   GtfsTransportModeConverter gtmc = GtfsTransportModeConverter.getInstance();
      route.setRouteId(toGtfsId(neptuneObject.getChouetteId(), prefix,keepOriginalId));
      route.setAgencyId(toGtfsId(neptuneObject.getCompany().getChouetteId(), prefix, keepOriginalId));
      route.setRouteShortName(null);
      route.setRouteLongName(null);
      log.warn("GtfsRouteProducer default format : " + parameters.getDefaultFormat());
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
          log.warn("no naming data for line "+ neptuneObject.getCodeSpace() + " " + neptuneObject.getTechnicalId());
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

      if (neptuneObject.getTransportModeContainer() != null)
      {
    	  TransportMode neptuneObjectTransportMode = neptuneObject.getTransportModeContainer();
    	  if (!parameters.getDefaultFormat().equalsIgnoreCase("Gtfs")) {
    		  log.warn("Transport neptune mode : " + neptuneObjectTransportMode.getMode() + ":" + neptuneObjectTransportMode.getSubMode());
    		  if (tmc != null) {
    			  if (!(tmc instanceof GtfsTransportModeConverter))
    				  log.warn("Default transport mode converter is gtfs not neptune !!!");
    			  else
    				  log.warn("Default transport mode converter is neptune !!!");
    		  }
    		  TransportMode ptM = tmc.specificToGenericMode(neptuneObjectTransportMode);
    		  if (ptM != null) {
    			  log.warn("Transport pivot mode : " + ptM.getMode() + ":" + ptM.getSubMode());
    			  Integer code = gtmc.fromPivotTransportModeToCode(ptM);
	    		  if (code != null)
	    			  route.setRouteType(RouteTypeEnum.fromValue(code.intValue()));
    		  } else {
    			  log.warn("Conversion du mode de transport impossible");
    		  }
    	  } else {
    		  Integer code = gtmc.fromSpecificTransportModeToCode(neptuneObjectTransportMode);
    		  if (code != null)
    			  route.setRouteType(RouteTypeEnum.fromValue(code.intValue()));
    	  }
      }
  

      try
      {
         getExporter().getRouteExporter().export(route);
      }
      catch (Exception e)
      {
         log.warn("export failed for line "+ neptuneObject.getCodeSpace() + " " + neptuneObject.getTechnicalId(),e);
         return false;
      }

      return true;
   }
}
