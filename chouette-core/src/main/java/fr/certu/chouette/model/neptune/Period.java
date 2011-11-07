package fr.certu.chouette.model.neptune;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.sql.Date;

import fr.certu.chouette.filter.Filter;

import lombok.Getter;
import lombok.Setter;

/**
 * Period : date period for Timetables
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 */
public class Period implements Serializable
{
   private static final long serialVersionUID = -1964071056103739954L;

   // constant for persistence fields
   /**
    * name of startDate attribute for {@link Filter} attributeName construction
    */
   public static final String    START_DATE                    = "startDate";
   /**
    * name of endDate attribute for {@link Filter} attributeName construction
    */
   public static final String    END_DATE                    = "endDate";

   /**
    * start of period <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private Date              startDate;
   /**
    * end of period <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private Date              endDate;

   /**
    * default constructor
    */
   public Period()
   {

   }

   /**
    * complete constructor
    * 
    * @param startDate
    *           start of period
    * @param endDate
    *           end of period
    */
   public Period(Date startDate, Date endDate)
   {
      this.startDate = startDate;
      this.endDate = endDate;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("start = ").append(formatDate(startDate)).append(" end = ").append(formatDate(endDate));
      return sb.toString();
   }

   /**
    * format date for toString
    * 
    * @param date
    *           date
    * @return string representation
    */
   private static String formatDate(Date date)
   {
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      if (date != null)
      {
         return dateFormat.format(date);
      }
      else
      {
         return null;
      }
   }
}
