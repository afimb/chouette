package fr.certu.chouette.exchange.gtfs.refactor.model;

import java.io.Serializable;
import java.sql.Date;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import fr.certu.chouette.exchange.gtfs.refactor.exporter.CalendarDateExporter;

//@ToString(callSuper=true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class GtfsCalendarDate extends GtfsObject implements Serializable
{

   private static final long serialVersionUID = 1L;

   @Getter
   @Setter
   private String serviceId;

   @Getter
   @Setter
   private Date date;

   @Getter
   @Setter
   private ExceptionType exceptionType;

   @Override
   public String toString()
   {
      return id + ":" + CalendarDateExporter.CONVERTER.to(this);
   }

   public enum ExceptionType
   {
      Unknown, Added, Removed;
   }
}
