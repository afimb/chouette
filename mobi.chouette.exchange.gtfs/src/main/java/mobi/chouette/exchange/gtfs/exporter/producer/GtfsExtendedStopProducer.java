/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.gtfs.exporter.producer;

import java.util.Collection;
import java.util.TimeZone;

import mobi.chouette.exchange.gtfs.model.GtfsStop;
import mobi.chouette.exchange.gtfs.model.GtfsStop.WheelchairBoardingType;
import mobi.chouette.exchange.gtfs.model.exporter.GtfsExporterInterface;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;

/**
 * convert Timetable to Gtfs Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
public class GtfsExtendedStopProducer extends
      AbstractProducer
{

   GtfsStop stop = new GtfsStop();
   public GtfsExtendedStopProducer(GtfsExporterInterface exporter)
   {
      super(exporter);
   }

   public boolean save(StopArea neptuneObject,  String prefix, Collection<StopArea> validParents, boolean keepOriginalId)
   {
      ChouetteAreaEnum chouetteAreaType = neptuneObject.getAreaType();
      if (chouetteAreaType.compareTo(ChouetteAreaEnum.BoardingPosition) == 0)
         stop.setLocationType(GtfsStop.LocationType.Stop);
      else if (chouetteAreaType.compareTo(ChouetteAreaEnum.Quay) == 0)
         stop.setLocationType(GtfsStop.LocationType.Stop);
      else if (chouetteAreaType.compareTo(ChouetteAreaEnum.CommercialStopPoint) == 0)
         stop.setLocationType(GtfsStop.LocationType.Station);
      else
         return false; // StopPlaces and ITL type not available
      stop.setStopId(toGtfsId(neptuneObject.getChouetteId(),prefix,keepOriginalId));
      if (neptuneObject.getName() == null)
      {
         return false;
      }
      stop.setStopName(neptuneObject.getName());

      if (neptuneObject.getLatitude() == null)
      {
         return false;
      }
      stop.setStopLat(neptuneObject.getLatitude());
      if (neptuneObject.getLongitude() == null)
      {
         return false;
      }
      stop.setStopLon(neptuneObject.getLongitude());
      stop.setStopCode(neptuneObject.getRegistrationNumber());
      stop.setStopDesc(neptuneObject.getComment());
      stop.setStopUrl(getUrl(neptuneObject.getUrl()));
      stop.setAddressLine(neptuneObject.getStreetName());
      stop.setLocality(neptuneObject.getCityName());
      stop.setPostalCode(neptuneObject.getZipCode());
      // manage stop_timezone
      stop.setStopTimezone(null);
      if (!isEmpty(neptuneObject.getTimeZone()))
      {
         TimeZone tz = TimeZone.getTimeZone(neptuneObject.getTimeZone());
         if (tz != null)
         {
            stop.setStopTimezone(tz);
         }
      }
      
      if (stop.getLocationType().equals(GtfsStop.LocationType.Stop))
      {
         if (neptuneObject.getParent() != null && validParents.contains(neptuneObject.getParent()))
         {
            stop.setParentStation(toGtfsId(neptuneObject.getParent()
                  .getChouetteId(),prefix,keepOriginalId));
         }
      }
      
      if (neptuneObject.getMobilityRestrictedSuitable() != null)
      {
    	  if (neptuneObject.getMobilityRestrictedSuitable())
             stop.setWheelchairBoarding(WheelchairBoardingType.Allowed);
    	  else
    		 stop.setWheelchairBoarding(WheelchairBoardingType.NoAllowed);
      }
      else
      {
         stop.setWheelchairBoarding(null);
      }
      
      try
      {
         getExporter().getStopExtendedExporter().export(stop);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         return false;
      }
      return true;
   }

}
