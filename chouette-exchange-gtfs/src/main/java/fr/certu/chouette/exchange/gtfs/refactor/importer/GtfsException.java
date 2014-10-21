package fr.certu.chouette.exchange.gtfs.refactor.importer;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsObject;

public class GtfsException extends IllegalArgumentException
{

   private static final long serialVersionUID = 1L;

   public GtfsException(String message, GtfsObject bean)
   {
      this(message, bean, null);
   }

   public GtfsException(String message, GtfsObject bean, Throwable cause)
   {
      super();
   }

}
