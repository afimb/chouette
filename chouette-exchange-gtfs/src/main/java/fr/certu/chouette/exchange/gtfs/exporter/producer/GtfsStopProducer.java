/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.gtfs.exporter.producer;

import java.util.Collection;
import java.util.TimeZone;

import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReportItem;
import fr.certu.chouette.exchange.gtfs.refactor.exporter.GtfsExporterInterface;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStop;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStop.WheelchairBoardingType;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.plugin.report.Report.STATE;

/**
 * convert Timetable to Gtfs Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
public class GtfsStopProducer extends AbstractProducer
{
   GtfsStop stop = new GtfsStop();


   public GtfsStopProducer(GtfsExporterInterface exporter)
   {
      super(exporter);
   }

   public boolean save(StopArea neptuneObject, GtfsReport report, String prefix, Collection<StopArea> validParents)
   {
      ChouetteAreaEnum chouetteAreaType = neptuneObject.getAreaType();
      if (chouetteAreaType.compareTo(ChouetteAreaEnum.BoardingPosition) == 0)
         stop.setLocationType(GtfsStop.LocationType.Stop);
      else if (chouetteAreaType.compareTo(ChouetteAreaEnum.Quay) == 0)
         stop.setLocationType(GtfsStop.LocationType.Stop);
      else if (chouetteAreaType.compareTo(ChouetteAreaEnum.CommercialStopPoint) == 0)
         stop.setLocationType(GtfsStop.LocationType.Station);
      // else if(chouetteAreaType.compareTo(ChouetteAreaEnum.STOPPLACE) == 0)
      // stop.setLocationType(GtfsStop.STATION);
      else
         return false; // StopPlaces and ITL type not available
      stop.setStopId(toGtfsId(neptuneObject.getObjectId(),prefix));
      if (neptuneObject.getName() == null)
      {
         GtfsReportItem item = new GtfsReportItem(
               GtfsReportItem.KEY.MISSING_DATA, STATE.ERROR, "StopArea",
               neptuneObject.getObjectId(), "Name");
         report.addItem(item);
         return false;
      }
      stop.setStopName(neptuneObject.getName());

      if (neptuneObject.getLatitude() == null)
      {
         GtfsReportItem item = new GtfsReportItem(
               GtfsReportItem.KEY.MISSING_DATA, STATE.ERROR, "StopArea",
               neptuneObject.getName(), "Latitude");
         report.addItem(item);
         return false;
      }
      stop.setStopLat(neptuneObject.getLatitude());
      if (neptuneObject.getLongitude() == null)
      {
         GtfsReportItem item = new GtfsReportItem(
               GtfsReportItem.KEY.MISSING_DATA, STATE.ERROR, "StopArea",
               neptuneObject.getName(), "Longitude");
         report.addItem(item);
         return false;
      }
      stop.setStopLon(neptuneObject.getLongitude());
      stop.setStopCode(neptuneObject.getRegistrationNumber());
      stop.setStopDesc(neptuneObject.getComment());
      stop.setStopUrl(getUrl(neptuneObject.getUrl()));
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

      stop.setParentStation(null);
      if (stop.getLocationType().equals(GtfsStop.LocationType.Stop))
      {
         if (neptuneObject.getParent() != null && validParents.contains(neptuneObject.getParent()))
         {
            stop.setParentStation(toGtfsId(neptuneObject.getParent()
                  .getObjectId(),prefix));
         }
      }
      
      if (neptuneObject.isMobilityRestrictedSuitable())
      {
         stop.setWheelchairBoarding(WheelchairBoardingType.Allowed);
      }
      else
      {
         // neptune stop does not distinct unknown and false !
         stop.setWheelchairBoarding(null);
      }
      
      try
      {
         getExporter().getStopExporter().export(stop);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         return false;
      }
      return true;
   }

}
