/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.gtfs.exporter.producer;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.refactor.exporter.GtfsExporter;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTransfer;
import fr.certu.chouette.model.neptune.ConnectionLink;

/**
 * convert Timetable to Gtfs Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
public class GtfsTransferProducer extends AbstractProducer
{
   public GtfsTransferProducer(GtfsExporter exporter)
   {
      super(exporter);
   }
   private GtfsTransfer transfer = new GtfsTransfer();

   public boolean save(ConnectionLink neptuneObject, GtfsReport report,String prefix)
   {
      transfer.setFromStopId(toGtfsId(neptuneObject.getStartOfLink()
            .getObjectId(),prefix));
      transfer
            .setToStopId(toGtfsId(neptuneObject.getEndOfLink().getObjectId(),prefix));
      if (neptuneObject.getDefaultDuration() != null
            && neptuneObject.getDefaultDuration().getTime() > 1000)
      {
         transfer.setMinTransferTime(Integer.valueOf((int)(neptuneObject.getDefaultDuration().getTime()/1000)));
         transfer.setTransferType(GtfsTransfer.TransferType.Minimal);
      } else
      {
         transfer.setMinTransferTime(null);
         transfer.setTransferType(GtfsTransfer.TransferType.Recommended);
      }
      
      try
      {
         getExporter().getTransferExporter().export(transfer);
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
