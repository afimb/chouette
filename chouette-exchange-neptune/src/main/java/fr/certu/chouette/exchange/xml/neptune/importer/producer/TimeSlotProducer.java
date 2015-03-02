package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import org.trident.schema.trident.TimeSlotType;

import fr.certu.chouette.exchange.xml.neptune.importer.Context;
import fr.certu.chouette.model.neptune.TimeSlot;

/**
 * 
 * @author mamadou keira
 * 
 */
public class TimeSlotProducer extends
      AbstractModelProducer<TimeSlot, TimeSlotType>
{

   @Override
   public TimeSlot produce(Context context, TimeSlotType xmlTimeSlot)
   {
      TimeSlot timeSlot = new TimeSlot();
      // objectId, objectVersion, creatorId, creationTime
      populateFromCastorNeptune(context, timeSlot, xmlTimeSlot);
      // beginningSlotTime mandatory
      timeSlot
            .setBeginningSlotTime(getTime(xmlTimeSlot.getBeginningSlotTime()));
      // endSlotTime mandatory
      timeSlot.setEndSlotTime(getTime(xmlTimeSlot.getEndSlotTime()));
      // firstDepartureTimeInSlot mandatory
      timeSlot.setFirstDepartureTimeInSlot(getTime(xmlTimeSlot
            .getFirstDepartureTimeInSlot()));
      // lastDepartureTimeInSlot mandatory
      timeSlot.setLastDepartureTimeInSlot(getTime(xmlTimeSlot
            .getLastDepartureTimeInSlot()));

      // TODO shared or unshared ???
      return timeSlot;
   }

}
