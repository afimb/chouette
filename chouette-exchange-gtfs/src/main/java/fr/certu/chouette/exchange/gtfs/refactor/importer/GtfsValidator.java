package fr.certu.chouette.exchange.gtfs.refactor.importer;

public interface GtfsValidator
{

   public abstract class Validator<T>
   {

      public abstract boolean validate(T input, GtfsImporter dao);

   }
}
