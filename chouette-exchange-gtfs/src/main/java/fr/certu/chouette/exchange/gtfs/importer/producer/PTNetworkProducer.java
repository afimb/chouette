package fr.certu.chouette.exchange.gtfs.importer.producer;

import java.util.Calendar;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.model.GtfsNetwork;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.plugin.report.ReportItem;

public class PTNetworkProducer extends AbstractModelProducer<PTNetwork, GtfsNetwork> 
{
	private static Logger logger = Logger.getLogger(PTNetworkProducer.class);
	/* (non-Javadoc)
	 * @see fr.certu.chouette.exchange.gtfs.importer.producer.IModelProducer#produce(java.lang.String, fr.certu.chouette.exchange.gtfs.model.GtfsBean, fr.certu.chouette.plugin.report.ReportItem)
	 */
	@Override
	public PTNetwork produce(GtfsNetwork gtfsNetwork,ReportItem report) 
	{
		if (gtfsNetwork == null) return null;
		PTNetwork ptNetwork = new PTNetwork();
		
		ptNetwork.setObjectId(composeObjectId(PTNetwork.PTNETWORK_KEY, getPrefix(),logger));

		// VersionDate mandatory
		ptNetwork.setVersionDate(Calendar.getInstance().getTime());
				
		// Name mandatory
		ptNetwork.setName(gtfsNetwork.getName());
		
		// Registration optional
		ptNetwork.setRegistrationNumber(getPrefix());
		
		// SourceName optional
		ptNetwork.setSourceName("GTFS");
		
		
		return ptNetwork;
	}

}
