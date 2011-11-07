package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import fr.certu.chouette.exchange.xml.neptune.importer.SharedImportedData;
import fr.certu.chouette.model.neptune.TimeSlot;
import fr.certu.chouette.plugin.report.ReportItem;
/**
 * 
 * @author mamadou keira
 *
 */
public class TimeSlotProducer extends AbstractModelProducer<TimeSlot, chouette.schema.TimeSlot>{

	@Override
	public TimeSlot produce(chouette.schema.TimeSlot xmlTimeSlot, ReportItem report,SharedImportedData sharedData) {
		TimeSlot timeSlot = new TimeSlot();
		// objectId, objectVersion, creatorId, creationTime
		populateFromCastorNeptune(timeSlot, xmlTimeSlot, report);
		//beginningSlotTime mandatory
		timeSlot.setBeginningSlotTime(getTime(xmlTimeSlot.getBeginningSlotTime()));
		//endSlotTime mandatory
		timeSlot.setEndSlotTime(getTime(xmlTimeSlot.getEndSlotTime()));
		//firstDepartureTimeInSlot mandatory
		timeSlot.setFirstDepartureTimeInSlot(getTime(xmlTimeSlot.getFirstDepartureTimeInSlot()));
		//lastDepartureTimeInSlot mandatory
		timeSlot.setLastDepartureTimeInSlot(getTime(xmlTimeSlot.getLastDepartureTimeInSlot()));
		
		return timeSlot;
	}

}
