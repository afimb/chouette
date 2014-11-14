package fr.certu.chouette.exchange.gtfs.importer.producer;

import java.sql.Time;
import java.util.Calendar;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTransfer;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTransfer.TransferType;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.type.ConnectionLinkTypeEnum;
import fr.certu.chouette.plugin.report.Report;

public class ConnectionLinkProducer extends
      AbstractModelProducer<ConnectionLink, GtfsTransfer>
{
   private static Logger logger = Logger
         .getLogger(ConnectionLinkProducer.class);

   @Override
   public ConnectionLink produce(GtfsTransfer gtfsTransfer, Report report)
   {

      ConnectionLink link = new ConnectionLink();

      link.setObjectId(composeObjectId(ConnectionLink.CONNECTIONLINK_KEY,
            gtfsTransfer.getFromStopId() + "_" + gtfsTransfer.getToStopId(),
            logger));

      link.setStartOfLinkId(gtfsTransfer.getFromStopId());
      link.setEndOfLinkId(gtfsTransfer.getToStopId());

      link.setCreationTime(Calendar.getInstance().getTime());
      link.setLinkType(ConnectionLinkTypeEnum.Overground);
      if (gtfsTransfer.getMinTransferTime() != null)
         link.setDefaultDuration(new Time(gtfsTransfer.getMinTransferTime() *1000));
      if (gtfsTransfer.getTransferType().equals(TransferType.NoAllowed))
      {
         link.setName("FORBIDDEN");
      }

      return link;
   }

}
