package fr.certu.chouette.model.neptune;

import java.io.Serializable;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import fr.certu.chouette.filter.Filter;

/**
 * Period : date period for Timetables
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 */
@Embeddable
@NoArgsConstructor
public class Period implements Serializable
{
   private static final long serialVersionUID = -1964071056103739954L;

   // constant for persistence fields
   /**
    * name of startDate attribute for {@link Filter} attributeName construction
    */
   public static final String START_DATE = "startDate";
   /**
    * name of endDate attribute for {@link Filter} attributeName construction
    */
   public static final String END_DATE = "endDate";

   @Getter
   @Setter
   @Column(name = "period_start")
   private Date startDate;

   @Getter
   @Setter
   @Column(name = "period_end")
   private Date endDate;

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

   /**
    * check if a date is included in period
    * 
    * @param aDay
    * @return
    */
   public boolean contains(Date aDay)
   {
      if (startDate == null || endDate == null)
         return false;
      if (aDay.equals(startDate))
         return true;
      if (aDay.equals(endDate))
         return true;
      return aDay.after(startDate) && aDay.before(endDate);
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
      result = prime * result
            + ((startDate == null) ? 0 : startDate.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Period other = (Period) obj;
      if (endDate == null)
      {
         if (other.endDate != null)
            return false;
      }
      else if (!endDate.equals(other.endDate))
         return false;
      if (startDate == null)
      {
         if (other.startDate != null)
            return false;
      }
      else if (!startDate.equals(other.startDate))
         return false;
      return true;
   }

}
