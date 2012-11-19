package fr.certu.chouette.exchange.gtfs.importer.producer;

import fr.certu.chouette.exchange.gtfs.model.GtfsStopTime;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import fr.certu.chouette.plugin.report.ReportItem;

/**
 * @author michel
 *
 */
public class VehicleJourneyAtStopProducer extends AbstractModelProducer<VehicleJourneyAtStop, GtfsStopTime> 
{

	@Override
	public VehicleJourneyAtStop produce(GtfsStopTime gtfsStopTime,ReportItem report) 
	{
		VehicleJourneyAtStop vjas = new VehicleJourneyAtStop();

		vjas.setArrivalTime(gtfsStopTime.getArrivalTime().getTime());
		vjas.setDepartureTime(gtfsStopTime.getDepartureTime().getTime());

		return vjas;
	}

}
