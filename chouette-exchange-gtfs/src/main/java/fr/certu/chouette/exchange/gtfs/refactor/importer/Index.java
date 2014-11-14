package fr.certu.chouette.exchange.gtfs.refactor.importer;


public interface Index<T> extends Iterable<T>
{
   void dispose();

   Iterable<String> keys();

   Iterable<T> values(String key);

   boolean containsKey(String key);

   T getValue(String key);

   boolean validate(T bean, GtfsImporter dao);

   int getLength();

}
