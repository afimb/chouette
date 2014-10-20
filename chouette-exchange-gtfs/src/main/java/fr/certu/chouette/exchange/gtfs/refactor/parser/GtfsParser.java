package fr.certu.chouette.exchange.gtfs.refactor.parser;

import java.util.Iterator;

public interface GtfsParser<T> extends Iterable<T>
{

   void dispose();

   Iterator<String> keyIterator();

   Iterator<T> valuesIterator(String key);

   boolean containsKey(String key);

   T getValue(String key);

   boolean validate(T bean, GtfsDao dao);

}
