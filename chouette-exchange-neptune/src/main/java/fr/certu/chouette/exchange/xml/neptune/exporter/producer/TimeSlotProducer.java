package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import fr.certu.chouette.model.neptune.TimeSlot;

public class TimeSlotProducer extends AbstractCastorNeptuneProducer<chouette.schema.TimeSlot, TimeSlot>
{
   @Override
   public chouette.schema.TimeSlot produce(TimeSlot timeSlot) {
      chouette.schema.TimeSlot castorTimeSlot = new chouette.schema.TimeSlot();

      //
      populateFromModel(castorTimeSlot, timeSlot);

      //beginningSlotTime mandatory
      castorTimeSlot.setBeginningSlotTime(toCastorTime(timeSlot.getBeginningSlotTime()));
      //endSlotTime mandatory
      castorTimeSlot.setEndSlotTime(toCastorTime(timeSlot.getEndSlotTime()));
      //firstDepartureTimeInSlot mandatory
      castorTimeSlot.setFirstDepartureTimeInSlot(toCastorTime(timeSlot.getFirstDepartureTimeInSlot()));
      //lastDepartureTimeInSlot mandatory
      castorTimeSlot.setLastDepartureTimeInSlot(toCastorTime(timeSlot.getLastDepartureTimeInSlot()));

      return castorTimeSlot;
   }

}
