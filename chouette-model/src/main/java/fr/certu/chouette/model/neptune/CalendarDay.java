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

/**
 * peculiar date for Timetables
 */
@Embeddable
@NoArgsConstructor
public class CalendarDay implements Serializable, Comparable<CalendarDay>
{
   private static final long serialVersionUID = -1964071056103739954L;

   /**
    * date
    * 
    * @param date
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Column(name = "date")
   private Date date;

   /**
    * included or excluded date <br/>
    * <ul>
    * <li>true : if date is effective for this calendar (default value)</li>
    * <li>false : if date is to be excluded from periods definitions</li>
    * </ul>
    * 
    * @param included
    *           New value
    */
   @Setter
   @Column(name = "in_out")
   private Boolean included = Boolean.TRUE;
   
   /**
    * included or excluded date <br/>
    * <ul>
    * <li>true : if date is effective for this calendar (default value)</li>
    * <li>false : if date is to be excluded from periods definitions</li>
    * </ul>
    * 
    * @return The actual value
    */
   public Boolean getIncluded()
   {
	   // protection from missing migration
	   if (included == null) included = Boolean.TRUE;
	   return included;
   }

   /**
    * complete constructor
    * 
    * @param date
    *           date
    * @param included
    *           indicate if date is included or excluded on the timetable
    */
   public CalendarDay(Date date, boolean included)
   {
      this.date = date;
      this.included = Boolean.valueOf(included);
   }

   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("date = ").append(formatDate(date)).append(" included = ")
            .append(included);
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
      } else
      {
         return null;
      }
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((date == null) ? 0 : date.hashCode());
      result = prime
            * result
            + ((included == null) ? Boolean.TRUE.hashCode() : included
                  .hashCode());
      return result;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      CalendarDay other = (CalendarDay) obj;

      if (date == null)
      {
         if (other.date != null)
            return false;
      } else if (!date.equals(other.date))
         return false;

      if (included == null)
      {
         if (other.included != null)
            return false;
      } else if (!included.equals(other.included))
         return false;

      return true;
   }

   /* (non-Javadoc)
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   @Override
   public int compareTo(CalendarDay o)
   {
      return getDate().compareTo(o.getDate());
   }

}
