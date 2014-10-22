package fr.certu.chouette.exchange.gtfs.importer.producer;

import lombok.Setter;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import fr.certu.chouette.plugin.exchange.tools.DbVehicleJourneyFactory;
import fr.certu.chouette.plugin.report.Report;

/**
 * @author michel
 * 
 */
public class VehicleJourneyAtStopProducer extends
      AbstractModelProducer<VehicleJourneyAtStop, GtfsStopTime>
{
   @Setter
   private DbVehicleJourneyFactory factory;

   @Override
   public VehicleJourneyAtStop produce(GtfsStopTime gtfsStopTime, Report report)
   {
      VehicleJourneyAtStop vjas = factory.getNewVehicleJourneyAtStop();

      vjas.setId(Long.valueOf(gtfsStopTime.getId().longValue())); // preserve file line in vjas
      vjas.setStopPointId(gtfsStopTime.getStopId()); // preserve stopId in vjas
      vjas.setOrder(gtfsStopTime.getStopSequence()); // preserve stopSequence in vjas
      vjas.setArrivalTime(gtfsStopTime.getArrivalTime().getTime());
      vjas.setDepartureTime(gtfsStopTime.getDepartureTime().getTime());

      return vjas;
   }

}
