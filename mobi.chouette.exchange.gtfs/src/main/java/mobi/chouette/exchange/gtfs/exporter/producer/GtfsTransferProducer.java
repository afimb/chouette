/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.gtfs.exporter.producer;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.gtfs.model.GtfsTransfer;
import mobi.chouette.exchange.gtfs.model.exporter.GtfsExporterInterface;
import mobi.chouette.model.ConnectionLink;

/**
 * convert Timetable to Gtfs Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
@Log4j
public class GtfsTransferProducer extends AbstractProducer
{
   public GtfsTransferProducer(GtfsExporterInterface exporter)
   {
      super(exporter);
   }
   private GtfsTransfer transfer = new GtfsTransfer();

   public boolean save(ConnectionLink neptuneObject, String prefix, boolean keepOriginalId)
   {
      transfer.setFromStopId(toGtfsId(neptuneObject.getStartOfLink()
            .getObjectId(),prefix,keepOriginalId));
      transfer
            .setToStopId(toGtfsId(neptuneObject.getEndOfLink().getObjectId(),prefix, keepOriginalId));
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
          log.error("fail to produce transfer "+e.getClass().getName()+" "+e.getMessage());
         return false;
      }
      return true;
   }

}
