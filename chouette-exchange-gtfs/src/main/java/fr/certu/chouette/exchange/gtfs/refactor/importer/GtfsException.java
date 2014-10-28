package fr.certu.chouette.exchange.gtfs.refactor.importer;

import lombok.Getter;
import lombok.ToString;

@ToString
public class GtfsException extends RuntimeException
{
   private static final long serialVersionUID = 1L;

   public enum ERROR
   {
      MISSING_FIELD, INVALID_FORMAT, MISSING_FOREIGN_KEY, DUPLICATE_FIELD, SYSTEM
   }

   @Getter
   private String path;
   @Getter
   private Integer id;
   @Getter
   private String field;
   @Getter
   private ERROR error;

   @Getter
   private String code;

   @Getter
   private String value;

   public GtfsException(Context context)
   {
      this(context, null);
   }

   public GtfsException(Context context, Throwable cause)
   {
      super(cause);
      this.path = (String) context.get(Context.PATH);
      this.id = (Integer) context.get(Context.ID);
      this.field = (String) context.get(Context.FIELD);
      this.error = (ERROR) context.get(Context.ERROR);
      this.value = (String)context.get(Context.VALUE);
   }

   public GtfsException(String path, Integer id, String field, ERROR error,
         String code, String value)
   {
      this(path, id, field, error, code, value, null);
   }

   public GtfsException(String path, Integer id, String field, ERROR error,
         String code, String value, Throwable cause)
   {
      super(cause);
      this.path = path;
      this.id = id;
      this.field = field;
      this.error = error;
      this.code = code;
      this.value = value;
   }

}
