package fr.certu.chouette.model.neptune;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import fr.certu.chouette.filter.Filter;

/**
 * Neptune TimeSlot a peroid for vehicleJOurney with frequency
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 */
@Entity
@Table(name = "time_slots")
@NoArgsConstructor
@Log4j
public class TimeSlot extends NeptuneIdentifiedObject
{
   private static final long serialVersionUID = 7510494886757866590L;

   /**
    * name of beginningSlotTime attribute for {@link Filter} attributeName
    * construction
    */
   public static final String BEGINNING_SLOTTIME = "beginningSlotTime";
   
   @Getter
   @Column(name = "name")
   private String name;
  
   @Getter
   @Setter
   @Column(name = "beginning_slot_time")
   private Time beginningSlotTime;

   @Getter
   @Setter
   @Column(name = "end_slot_time")
   private Time endSlotTime;

   @Getter
   @Setter
   @Column(name = "first_departure_time_in_slot")
   private Time firstDepartureTimeInSlot;

   @Getter
   @Setter
   @Column(name = "last_departure_time_in_slot")
   private Time lastDepartureTimeInSlot;

   
   public void setName(String value)
   {
      if (value != null && value.length() > 255)
      {
         log.warn("name too long, truncated " + value);
         name = value.substring(0, 255);
      }
      else
      {
         name = value;
      }
   }
   
   
   @Override
   public String toString(String indent, int level)
   {
      StringBuilder sb = new StringBuilder(super.toString(indent, level));
      sb.append("\n").append(indent).append("beginningSlotTime = ").append(formatDate(beginningSlotTime));
      sb.append("\n").append(indent).append("endSlotTime = ").append(formatDate(endSlotTime));
      sb.append("\n").append(indent).append("firstDepartureTimeInSlot = ").append(formatDate(firstDepartureTimeInSlot));
      sb.append("\n").append(indent).append("lastDepartureTimeInSlot = ").append(formatDate(lastDepartureTimeInSlot));
      return sb.toString();

   }

   /**
    * convert time to string for toString purpose
    * 
    * @param date
    * @return
    */
   private String formatDate(Date date)
   {
      DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
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
   public <T extends NeptuneObject> boolean compareAttributes(
         T anotherObject)
   {
      if (anotherObject instanceof TimeSlot)
      {
         TimeSlot another = (TimeSlot) anotherObject;
         if (!sameValue(this.getObjectId(), another.getObjectId()))
            return false;
         if (!sameValue(this.getObjectVersion(), another.getObjectVersion()))
            return false;
         if (!sameValue(this.getName(), another.getName()))
            return false;
         if (!sameValue(this.getRegistrationNumber(), another.getRegistrationNumber()))
            return false;

         if (!sameValue(this.getBeginningSlotTime(), another.getBeginningSlotTime()))
            return false;
         if (!sameValue(this.getEndSlotTime(), another.getEndSlotTime()))
            return false;
         if (!sameValue(this.getFirstDepartureTimeInSlot(), another.getFirstDepartureTimeInSlot()))
            return false;
         if (!sameValue(this.getLastDepartureTimeInSlot(), another.getLastDepartureTimeInSlot()))
            return false;
         return true;
      }
      else
      {
         return false;
      }
   }

   @Override
   public String toURL()
   {
      return null;
   }

}
