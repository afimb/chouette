package fr.certu.chouette.exchange.gtfs.refactor.validator;

import fr.certu.chouette.exchange.gtfs.refactor.model.StopTime;
import fr.certu.chouette.exchange.gtfs.refactor.parser.GtfsException;

public interface StopTimeValidator extends GtfsValidator
{

   public static Validator<StopTime> STOPTIME_VALIDATOR = new Validator<StopTime>()
   {

      @Override
      public boolean validate(StopTime input)
      {
         throw new GtfsException("TODO", input);
      }

   };

}
