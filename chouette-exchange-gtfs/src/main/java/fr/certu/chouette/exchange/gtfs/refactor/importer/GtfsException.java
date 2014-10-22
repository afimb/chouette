package fr.certu.chouette.exchange.gtfs.refactor.importer;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsObject;

public class GtfsException extends IllegalArgumentException
{

   private static final long serialVersionUID = 1L;
   private GtfsObject _bean;

   public GtfsException()
   {
      super();
   }

   public GtfsException(String message, Throwable cause)
   {
      super(message, cause);

   }

   public GtfsException(String s)
   {
      super(s);
   }

   public GtfsException(Throwable cause)
   {
      super(cause);
   }

   public GtfsException(String message, GtfsObject bean)
   {
      this(message, bean, null);
   }

   public GtfsException(String message, GtfsObject bean, Throwable cause)
   {
      super(message, cause);
      _bean = bean;
   }

}
