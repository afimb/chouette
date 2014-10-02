package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import org.trident.schema.trident.TimeSlotType;

import fr.certu.chouette.model.neptune.TimeSlot;
import fr.certu.chouette.plugin.exchange.SharedImportedData;
import fr.certu.chouette.plugin.exchange.UnsharedImportedData;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;

/**
 * 
 * @author mamadou keira
 * 
 */
public class TimeSlotProducer extends
      AbstractModelProducer<TimeSlot, TimeSlotType>
{

   @Override
   public TimeSlot produce(String sourceFile, TimeSlotType xmlTimeSlot,
         ReportItem importReport, PhaseReportItem validationReport,
         SharedImportedData sharedData, UnsharedImportedData unshareableData)
   {
      TimeSlot timeSlot = new TimeSlot();
      // objectId, objectVersion, creatorId, creationTime
      populateFromCastorNeptune(timeSlot, xmlTimeSlot, importReport);
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

      return timeSlot;
   }

}
