/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.gtfs.exporter.producer;

import java.util.List;

import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReportItem;
import fr.certu.chouette.exchange.gtfs.model.GtfsStop;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.plugin.report.Report.STATE;

/**
 * convert Timetable to Gtfs Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
public class GtfsStopProducer extends AbstractProducer<GtfsStop, StopArea>
{

   @Override
   public List<GtfsStop> produceAll(StopArea area, GtfsReport report)
   {
      throw new UnsupportedOperationException("not yet implemented");
   }

   @Override
   public GtfsStop produce(StopArea neptuneObject, GtfsReport report)
   {
      GtfsStop stop = new GtfsStop();
      ChouetteAreaEnum chouetteAreaType = neptuneObject.getAreaType();
      if (chouetteAreaType.compareTo(ChouetteAreaEnum.BoardingPosition) == 0)
         stop.setLocationType(GtfsStop.STOP);
      else if (chouetteAreaType.compareTo(ChouetteAreaEnum.Quay) == 0)
         stop.setLocationType(GtfsStop.STOP);
      else if (chouetteAreaType.compareTo(ChouetteAreaEnum.CommercialStopPoint) == 0)
         stop.setLocationType(GtfsStop.STATION);
      // else if(chouetteAreaType.compareTo(ChouetteAreaEnum.STOPPLACE) == 0)
      // stop.setLocationType(GtfsStop.STATION);
      else
         return null; // StopPlaces and ITL type not available
      stop.setStopId(toGtfsId(neptuneObject.getObjectId()));
      if (neptuneObject.getName() == null)
      {
         GtfsReportItem item = new GtfsReportItem(
               GtfsReportItem.KEY.MISSING_DATA, STATE.ERROR, "StopArea",
               neptuneObject.getObjectId(), "Name");
         report.addItem(item);
         return null;
      }
      stop.setStopName(neptuneObject.getName());

      if (neptuneObject.getLatitude() == null)
      {
         GtfsReportItem item = new GtfsReportItem(
               GtfsReportItem.KEY.MISSING_DATA, STATE.ERROR, "StopArea",
               neptuneObject.getName(), "Latitude");
         report.addItem(item);
         return null;
      }
      stop.setStopLat(neptuneObject.getLatitude());
      if (neptuneObject.getLongitude() == null)
      {
         GtfsReportItem item = new GtfsReportItem(
               GtfsReportItem.KEY.MISSING_DATA, STATE.ERROR, "StopArea",
               neptuneObject.getName(), "Longitude");
         report.addItem(item);
         return null;
      }
      stop.setStopLon(neptuneObject.getLongitude());
      stop.setStopCode(neptuneObject.getRegistrationNumber());
      stop.setStopDesc(neptuneObject.getComment());
      if (stop.getLocationType() == GtfsStop.STOP)
      {
         if (neptuneObject.getParent() != null)
         {
            stop.setParentStation(toGtfsId(neptuneObject.getParent()
                  .getObjectId()));
         }
      }
      return stop;
   }

}
