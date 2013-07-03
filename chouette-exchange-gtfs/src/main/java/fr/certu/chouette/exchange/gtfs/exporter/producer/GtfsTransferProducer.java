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
import fr.certu.chouette.exchange.gtfs.model.GtfsTime;
import fr.certu.chouette.exchange.gtfs.model.GtfsTransfer;
import fr.certu.chouette.exchange.gtfs.model.GtfsTransfer.Type;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.plugin.report.Report.STATE;

/**
 * convert Timetable to Gtfs Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
public class GtfsTransferProducer extends AbstractProducer<GtfsTransfer, ConnectionLink>
{

	@Override
	public List<GtfsTransfer> produceAll(ConnectionLink link,GtfsReport report)
	{
		throw new UnsupportedOperationException("not yet implemented");
	}


	@Override
	public GtfsTransfer produce(ConnectionLink neptuneObject,GtfsReport report)
	{
		GtfsTransfer transfer = new GtfsTransfer();
		transfer.setFromStopId(toGtfsId(neptuneObject.getStartOfLink().getObjectId())) ;
		transfer.setToStopId(toGtfsId(neptuneObject.getEndOfLink().getObjectId())) ;
		if (neptuneObject.getDefaultDuration() != null && neptuneObject.getDefaultDuration().getTime() > 1000)
		{
			GtfsTime minTransferTime = new GtfsTime(neptuneObject.getDefaultDuration(),false);
			transfer.setMinTransferTime(minTransferTime );
			transfer.setTransferType(Type.MINIMAL);
		}
		else
		{
			transfer.setTransferType(Type.RECOMMENDED);
		}
		return transfer;
	}


}
