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
 * Period : peculiar date for Timetables
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 */
@Embeddable
@NoArgsConstructor
public class CalendarDay implements Serializable,Comparable<CalendarDay>
{
   private static final long serialVersionUID = -1964071056103739954L;

   @Getter
   @Setter
   @Column(name = "date")
   private Date date;

   @Setter
   @Column(name = "in_out")
   private Boolean included = Boolean.TRUE;
   
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

   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("date = ").append(formatDate(date)).append(" included = ").append(included);
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

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((date == null) ? 0 : date.hashCode());
      result = prime * result
            + ((included == null) ? Boolean.TRUE.hashCode() : included.hashCode());
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
      CalendarDay other = (CalendarDay) obj;
      
      if (date == null)
      {
         if (other.date != null)
            return false;
      }
      else if (!date.equals(other.date))
         return false;
      
      if (included == null)
      {
         if (other.included != null)
            return false;
      }
      else if (!included.equals(other.included))
         return false;
      
      return true;
   }

@Override
public int compareTo(CalendarDay o) 
{
	return getDate().compareTo(o.getDate());
}

}
