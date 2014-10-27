package fr.certu.chouette.exchange.gtfs.refactor.importer;

import lombok.Getter;
import lombok.ToString;

@ToString
public class GtfsException extends RuntimeException
{
   private static final long serialVersionUID = 1L;

   public enum ERROR
   {
      MISSING_FIELD, INVALID_FORMAT, MISSING_FOREIGN_KEY, DUPLICATE_FIELD
   }

   @Getter
   private String path;
   @Getter
   private Integer id;
   @Getter
   private String field;
   @Getter
   private ERROR code;

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
      this.code = (ERROR) context.get(Context.CODE);
      this.value = context.get(Context.VALUE).toString();
   }

   public GtfsException(String path, Integer id, String field, ERROR code,
         String value)
   {
      super();
      this.path = path;
      this.id = id;
      this.field = field;
      this.code = code;
      this.value = value;
   }

   public GtfsException(String string)
   {
      // TODO Auto-generated constructor stub
   }

   public GtfsException(Exception e)
   {
      // TODO Auto-generated constructor stub
   }

}
