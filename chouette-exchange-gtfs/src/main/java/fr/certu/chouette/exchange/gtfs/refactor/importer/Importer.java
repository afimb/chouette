package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.util.Iterator;

public interface Importer<T> extends Iterable<T>
{

   void dispose();

   Iterator<String> keyIterator();

   Iterator<T> valuesIterator(String key);

   boolean containsKey(String key);

   T getValue(String key);

   boolean validate(T bean, GtfsImporter dao);

}
