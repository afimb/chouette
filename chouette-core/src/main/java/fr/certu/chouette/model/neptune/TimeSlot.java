package fr.certu.chouette.model.neptune;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.certu.chouette.filter.Filter;

import lombok.Getter;
import lombok.Setter;

/**
 * Neptune TimeSlot a peroid for vehicleJOurney with frequency
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 */
public class TimeSlot extends NeptuneIdentifiedObject 
{
	private static final long serialVersionUID = 7510494886757866590L;

   // TODO constant for persistence fields
   /**
    * name of beginningSlotTime attribute for {@link Filter} attributeName construction
    */
   public static final String    BEGINNING_SLOTTIME                    = "beginningSlotTime";
	 /**
     * Field beginningSlotTime.
    * <br/><i>readable/writable</i>
     */
    @Getter @Setter private Time beginningSlotTime;

    /**
     * Field endSlotTime.
    * <br/><i>readable/writable</i>
     */
    @Getter @Setter private Time endSlotTime;

    /**
     * Field firstDepartureTimeInSlot.
    * <br/><i>readable/writable</i>
     */
    @Getter @Setter private Time firstDepartureTimeInSlot;

    /**
     * Field lastDepartureTimeInSlot.
    * <br/><i>readable/writable</i>
     */
    @Getter @Setter private Time lastDepartureTimeInSlot;
	
    @Override
    public String toString(String indent,int level)
    {
       StringBuilder sb = new StringBuilder(super.toString(indent,level));
       sb.append("\n").append(indent).append("beginningSlotTime = ").append(formatDate(beginningSlotTime));
       sb.append("\n").append(indent).append("endSlotTime = ").append(formatDate(endSlotTime));
       sb.append("\n").append(indent).append("firstDepartureTimeInSlot = ").append(formatDate(firstDepartureTimeInSlot));
       sb.append("\n").append(indent).append("lastDepartureTimeInSlot = ").append(formatDate(lastDepartureTimeInSlot));
       return sb.toString();

    }
    
    /**
     * convert time to string for toString purpose 
     * @param date 
     * @return
     */
    private String formatDate(Date date)
    {
       DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
       if(date != null)
       {
          return dateFormat.format(date);
       }
       else
       {
          return null;
       }
    }

}
