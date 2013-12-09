package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import org.trident.schema.trident.TimeSlotType;

import fr.certu.chouette.model.neptune.TimeSlot;

public class TimeSlotProducer extends AbstractJaxbNeptuneProducer<TimeSlotType, TimeSlot>
{
   @Override
   public TimeSlotType produce(TimeSlot timeSlot) {
	   TimeSlotType castorTimeSlot = tridentFactory.createTimeSlotType();

      //
      populateFromModel(castorTimeSlot, timeSlot);

      //beginningSlotTime mandatory
      castorTimeSlot.setBeginningSlotTime(toCalendar(timeSlot.getBeginningSlotTime()));
      //endSlotTime mandatory
      castorTimeSlot.setEndSlotTime(toCalendar(timeSlot.getEndSlotTime()));
      //firstDepartureTimeInSlot mandatory
      castorTimeSlot.setFirstDepartureTimeInSlot(toCalendar(timeSlot.getFirstDepartureTimeInSlot()));
      //lastDepartureTimeInSlot mandatory
      castorTimeSlot.setLastDepartureTimeInSlot(toCalendar(timeSlot.getLastDepartureTimeInSlot()));

      return castorTimeSlot;
   }

}
