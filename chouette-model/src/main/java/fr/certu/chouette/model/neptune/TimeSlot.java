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

/**
 * Chouette TimeSlot a peroid for vehicleJourney with frequency
 * <p>
 * Note : this object will be remove soon because it is too close to Neptune
 * model and will not be adaptable to GTFS or NeTEx formats
 * <p/>
 * Neptune mapping : TimeSlot <br/>
 * Gtfs mapping :none <br/>
 */
@Entity
@Table(name = "time_slots")
@NoArgsConstructor
@Log4j
public class TimeSlot extends NeptuneIdentifiedObject
{
   private static final long serialVersionUID = 7510494886757866590L;

   /**
    * name
    * 
    * @return The actual value
    */
   @Getter
   @Column(name = "name")
   private String name;

   /**
    * set name <br/>
    * truncated to 255 characters if too long
    * 
    * @param value
    *           New value
    */
   public void setName(String value)
   {
      name = dataBaseSizeProtectedValue(value, "name", log);
   }

   /**
    * start of slot
    * 
    * @param beginningSlotTime
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Column(name = "beginning_slot_time")
   private Time beginningSlotTime;

   /**
    * end of slot
    * 
    * @param endSlotTime
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Column(name = "end_slot_time")
   private Time endSlotTime;

   /**
    * first departure of journey in slot
    * 
    * @param firstDepartureTimeInSlot
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Column(name = "first_departure_time_in_slot")
   private Time firstDepartureTimeInSlot;

   /**
    * last departure of journey in slot
    * 
    * @param lastDepartureTimeInSlot
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Column(name = "last_departure_time_in_slot")
   private Time lastDepartureTimeInSlot;

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#toString(java.
    * lang.String, int)
    */
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
    * @return formated date
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

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.model.neptune.NeptuneObject#compareAttributes(fr.certu
    * .chouette.model.neptune.NeptuneObject)
    */
   @Override
   public <T extends NeptuneObject> boolean compareAttributes(T anotherObject)
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

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#toURL()
    */
   @Override
   public String toURL()
   {
      return null;
   }

}
