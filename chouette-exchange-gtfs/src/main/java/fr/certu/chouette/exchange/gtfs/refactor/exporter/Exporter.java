package fr.certu.chouette.exchange.gtfs.refactor.exporter;

import java.io.IOException;

public interface Exporter<T>
{
   void dispose() throws IOException;

   void export(T bean) throws IOException;

   void export(String text) throws IOException;

}
