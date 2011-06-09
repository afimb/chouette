package fr.certu.chouette.model.neptune;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import fr.certu.chouette.filter.DetailLevelEnum;

public class TimeSlot extends NeptuneIdentifiedObject 
{
	private static final long serialVersionUID = 7510494886757866590L;

	 /**
     * Field beginningSlotTime.
     */
    @Getter @Setter private Date beginningSlotTime;

    /**
     * Field endSlotTime.
     */
    @Getter @Setter private Date endSlotTime;

    /**
     * Field firstDepartureTimeInSlot.
     */
    @Getter @Setter private Date firstDepartureTimeInSlot;

    /**
     * Field lastDepartureTimeInSlot.
     */
    @Getter @Setter private Date lastDepartureTimeInSlot;
	
//    @Override
//	public void expand(DetailLevelEnum level) 
//	{
//		// to avoid circular call check if level is already set according to this level
//		if (getLevel().ordinal() >= level.ordinal()) return;
//		super.expand(level);
//	}

}
