package fr.certu.chouette.exchange.gtfs.importer.producer;

import lombok.Setter;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTrip;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.plugin.exchange.tools.DbVehicleJourney;
import fr.certu.chouette.plugin.exchange.tools.DbVehicleJourneyFactory;
import fr.certu.chouette.plugin.report.Report;

public class VehicleJourneyProducer extends AbstractModelProducer<VehicleJourney, GtfsTrip>
{
   private static Logger logger = Logger.getLogger(VehicleJourneyProducer.class);

   @Setter
   private DbVehicleJourneyFactory factory;

   @Override
   public VehicleJourney produce(GtfsTrip gtfsTrip, Report report)
   {
      VehicleJourney vehicleJourney = factory.getNewVehicleJourney();

      // objectId, objectVersion, creatorId, creationTime
      vehicleJourney.setObjectId(composeIncrementalObjectId(DbVehicleJourney.VEHICLEJOURNEY_KEY, gtfsTrip.getTripId(), logger));

      if (gtfsTrip.getTripShortName() != null)
      {
         try
         {
            vehicleJourney.setNumber(Long.parseLong(gtfsTrip.getTripShortName()));
         }
         catch (NumberFormatException e)
         {
            vehicleJourney.setNumber(Long.valueOf(0));
            vehicleJourney.setPublishedJourneyName(gtfsTrip.getTripShortName());
         }
      }

      if (gtfsTrip.getWheelchairAccessible() != null)
      {
         switch (gtfsTrip.getWheelchairAccessible())
         {
         case NoInformation:
            vehicleJourney.setMobilityRestrictedSuitability(null);
            break;
         case NoAllowed:
            vehicleJourney.setMobilityRestrictedSuitability(Boolean.FALSE);
            break;
         case Allowed:
            vehicleJourney.setMobilityRestrictedSuitability(Boolean.TRUE);
            break;
         }
      }

      return vehicleJourney;
   }

}
