package fr.certu.chouette.exchange.gtfs.refactor.model;

import java.io.Serializable;
import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class CalendarDate extends GtfsObject implements Serializable
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

   public enum ExceptionType
   {
      Unknown, Added, Removed;
   }
}
