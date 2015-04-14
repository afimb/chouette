package mobi.chouette.exchange.neptune.exporter.producer;

import mobi.chouette.exchange.neptune.model.TimeSlot;

import org.trident.schema.trident.TimeSlotType;


public class TimeSlotProducer extends
      AbstractJaxbNeptuneProducer<TimeSlotType, TimeSlot>
{
   //@Override
   public TimeSlotType produce(TimeSlot timeSlot, boolean addExtension)
   {
      TimeSlotType castorTimeSlot = tridentFactory.createTimeSlotType();

      //
      populateFromModel(castorTimeSlot, timeSlot);

      // beginningSlotTime mandatory
      castorTimeSlot.setBeginningSlotTime(toCalendar(timeSlot
            .getBeginningSlotTime()));
      // endSlotTime mandatory
      castorTimeSlot.setEndSlotTime(toCalendar(timeSlot.getEndSlotTime()));
      // firstDepartureTimeInSlot mandatory
      castorTimeSlot.setFirstDepartureTimeInSlot(toCalendar(timeSlot
            .getFirstDepartureTimeInSlot()));
      // lastDepartureTimeInSlot mandatory
      castorTimeSlot.setLastDepartureTimeInSlot(toCalendar(timeSlot
            .getLastDepartureTimeInSlot()));

      return castorTimeSlot;
   }

}
