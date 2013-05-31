package fr.certu.chouette.exchange.gtfs.importer.producer;

import lombok.Setter;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.model.GtfsTrip;
import fr.certu.chouette.plugin.exchange.tools.DbVehicleJourney;
import fr.certu.chouette.plugin.exchange.tools.DbVehicleJourneyFactory;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.plugin.report.ReportItem;

public class VehicleJourneyProducer extends AbstractModelProducer<VehicleJourney, GtfsTrip> 
{
   private static Logger logger = Logger.getLogger(VehicleJourneyProducer.class);

   @Setter private DbVehicleJourneyFactory factory;

   @Override
   public VehicleJourney produce(GtfsTrip gtfsTrip,ReportItem report) 
   {
      VehicleJourney vehicleJourney = factory.getNewVehicleJourney();

      // objectId, objectVersion, creatorId, creationTime
      vehicleJourney.setObjectId(composeIncrementalObjectId( DbVehicleJourney.VEHICLEJOURNEY_KEY, gtfsTrip.getTripId(),logger));

      // JourneyPatternId optional
      // vehicleJourney.setJourneyPatternId(composeObjectId( JourneyPattern.JOURNEYPATTERN_KEY, gtfsTrip.getRouteId(),logger));

      // RouteId mandatory
      // vehicleJourney.setRouteId(composeObjectId( Route.ROUTE_KEY, gtfsTrip.getRouteId(),logger));

      return vehicleJourney;
   }

}
