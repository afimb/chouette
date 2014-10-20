package fr.certu.chouette.exchange.gtfs.refactor.parser;

public interface GtfsValidator
{

   public abstract class Validator<T>
   {

      public abstract boolean validate(T input, GtfsDao dao);

   }
}
