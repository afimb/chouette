package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import java.util.Date;

import fr.certu.chouette.model.neptune.TimeSlot;
import fr.certu.chouette.plugin.report.ReportItem;
/**
 * 
 * @author mamadou keira
 *
 */
public class TimeSlotProducer extends AbstractModelProducer<TimeSlot, chouette.schema.TimeSlot>{

	@Override
	public TimeSlot produce(chouette.schema.TimeSlot xmlTimeSlot, ReportItem report) {
		TimeSlot timeSlot = new TimeSlot();
		// objectId, objectVersion, creatorId, creationTime
		populateFromCastorNeptune(timeSlot, xmlTimeSlot, report);
		//beginningSlotTime mandatory
		timeSlot.setBeginningSlotTime(new Date(xmlTimeSlot.getBeginningSlotTime().toLong()));
		//endSlotTime mandatory
		timeSlot.setEndSlotTime(new Date(xmlTimeSlot.getEndSlotTime().toLong()));
		//firstDepartureTimeInSlot mandatory
		timeSlot.setFirstDepartureTimeInSlot(new Date(xmlTimeSlot.getFirstDepartureTimeInSlot().toLong()));
		//lastDepartureTimeInSlot mandatory
		timeSlot.setLastDepartureTimeInSlot(new Date(xmlTimeSlot.getLastDepartureTimeInSlot().toLong()));
		
		return timeSlot;
	}

}
