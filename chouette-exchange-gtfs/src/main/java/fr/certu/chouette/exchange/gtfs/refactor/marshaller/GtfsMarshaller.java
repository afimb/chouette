package fr.certu.chouette.exchange.gtfs.refactor.marshaller;

import java.io.IOException;

public interface GtfsMarshaller<T>
{
   void dispose() throws IOException;

   void marshal(T bean) throws IOException;

   void marshal(String text) throws IOException;

}
