package fr.certu.chouette.exchange.gtfs.refactor.validator;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime;
import fr.certu.chouette.exchange.gtfs.refactor.parser.GtfsException;

public interface StopTimeValidator extends GtfsValidator
{

   public static Validator<GtfsStopTime> STOPTIME_VALIDATOR = new Validator<GtfsStopTime>()
   {

      @Override
      public boolean validate(GtfsStopTime input)
      {
         throw new GtfsException("TODO", input);
      }

   };

}
