package fr.certu.chouette.model.neptune;

import java.util.Date;

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

	 /**
     * Field beginningSlotTime.
    * <br/><i>readable/writable</i>
     */
    @Getter @Setter private Date beginningSlotTime;

    /**
     * Field endSlotTime.
    * <br/><i>readable/writable</i>
     */
    @Getter @Setter private Date endSlotTime;

    /**
     * Field firstDepartureTimeInSlot.
    * <br/><i>readable/writable</i>
     */
    @Getter @Setter private Date firstDepartureTimeInSlot;

    /**
     * Field lastDepartureTimeInSlot.
    * <br/><i>readable/writable</i>
     */
    @Getter @Setter private Date lastDepartureTimeInSlot;
	
}
