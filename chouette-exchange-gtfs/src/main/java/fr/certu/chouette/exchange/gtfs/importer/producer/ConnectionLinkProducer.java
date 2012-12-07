package fr.certu.chouette.exchange.gtfs.importer.producer;

import java.util.Calendar;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.model.GtfsTransfer;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.type.ConnectionLinkTypeEnum;
import fr.certu.chouette.plugin.report.ReportItem;

public class ConnectionLinkProducer extends AbstractModelProducer<ConnectionLink, GtfsTransfer> 
{
    private static Logger logger = Logger.getLogger(ConnectionLinkProducer.class);
	@Override
	public ConnectionLink produce(GtfsTransfer gtfsTransfer,ReportItem report) 
	{

	   ConnectionLink link = new ConnectionLink();
		
	   link.setObjectId(composeObjectId( ConnectionLink.CONNECTIONLINK_KEY,gtfsTransfer.getFromStopId()+"_"+gtfsTransfer.getToStopId(),logger));
		
		link.setStartOfLinkId(gtfsTransfer.getFromStopId());
		
	    link.setEndOfLinkId(gtfsTransfer.getToStopId());

       link.setCreationTime(Calendar.getInstance().getTime());
       link.setLinkType(ConnectionLinkTypeEnum.OVERGROUND);
       if (gtfsTransfer.getMinTransferTime() != null)
          link.setDefaultDuration(gtfsTransfer.getMinTransferTime().getTime());
       if (gtfsTransfer.getTransferType().equals(GtfsTransfer.Type.FORBIDDEN))
       {
          link.setName("FORBIDDEN");
       }
       
		return link;
	}

}
