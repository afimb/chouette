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
   public List<GtfsStop> produceAll(StopArea area,GtfsReport report)
   {
      throw new UnsupportedOperationException("not yet implemented");
   }


   @Override
   public GtfsStop produce(StopArea neptuneObject,GtfsReport report)
   {
      GtfsStop stop = new GtfsStop();
      ChouetteAreaEnum chouetteAreaType = neptuneObject.getAreaType();
      if (chouetteAreaType.compareTo(ChouetteAreaEnum.BOARDINGPOSITION) == 0)
         stop.setLocationType(GtfsStop.STOP);
      else if(chouetteAreaType.compareTo(ChouetteAreaEnum.QUAY) == 0)
         stop.setLocationType(GtfsStop.STOP);
      else if(chouetteAreaType.compareTo(ChouetteAreaEnum.COMMERCIALSTOPPOINT) == 0)
         stop.setLocationType(GtfsStop.STATION);
      else if(chouetteAreaType.compareTo(ChouetteAreaEnum.STOPPLACE) == 0)
         stop.setLocationType(GtfsStop.STATION);
      else
         return null ; // ITL type not available
      stop.setStopId(neptuneObject.getObjectId());
      if (neptuneObject.getName() == null)
      {
         GtfsReportItem item = new GtfsReportItem(GtfsReportItem.KEY.MISSING_DATA, STATE.ERROR, "StopArea",neptuneObject.getObjectId(),"Name");
         report.addItem(item);
         return null;
      }
      stop.setStopName(neptuneObject.getName());

      if (neptuneObject.getAreaCentroid() == null)
      {
         GtfsReportItem item = new GtfsReportItem(GtfsReportItem.KEY.MISSING_DATA, STATE.ERROR, "StopArea",neptuneObject.getName(),"Longitude");
         report.addItem(item);
         return null;
      }
      if (neptuneObject.getAreaCentroid().getLatitude() == null)
      {
         GtfsReportItem item = new GtfsReportItem(GtfsReportItem.KEY.MISSING_DATA, STATE.ERROR, "StopArea",neptuneObject.getName(),"Latitude");
         report.addItem(item);
         return null;
      }
      stop.setStopLat(neptuneObject.getAreaCentroid().getLongitude());
      if (neptuneObject.getAreaCentroid().getLatitude() == null)
      {
         GtfsReportItem item = new GtfsReportItem(GtfsReportItem.KEY.MISSING_DATA, STATE.ERROR, "StopArea",neptuneObject.getName(),"Longitude");
         report.addItem(item);
         return null;
      }
      stop.setStopLon(neptuneObject.getAreaCentroid().getLongitude());
      stop.setStopCode(neptuneObject.getRegistrationNumber());
      stop.setStopDesc(neptuneObject.getComment());
      return stop;
   }


}
