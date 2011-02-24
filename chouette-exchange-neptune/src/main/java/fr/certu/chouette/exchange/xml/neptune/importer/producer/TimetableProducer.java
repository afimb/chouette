package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import java.util.Date;

import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;
import fr.certu.chouette.plugin.report.ReportItem;

public class TimetableProducer extends AbstractModelProducer<Timetable, chouette.schema.Timetable> {

	@Override
	public Timetable produce(chouette.schema.Timetable xmlTimetable,ReportItem report) 
	{
		Timetable timetable= new Timetable();
		
		// objectId, objectVersion, creatorId, creationTime
		populateFromCastorNeptune(timetable, xmlTimetable, report);

		timetable.setComment(getNonEmptyTrimedString(xmlTimetable.getComment()));

		timetable.setVersion(getNonEmptyTrimedString(xmlTimetable.getVersion()));

		// DayType optional
		if (xmlTimetable.getDayType() != null)
		{
			for(chouette.schema.types.DayTypeType xmlDayType : xmlTimetable.getDayType())
			try
			{
				timetable.addDayType(DayTypeEnum.fromValue(xmlDayType.value()));
			}
			catch (IllegalArgumentException e) 
			{
				// TODO: traiter le cas de non correspondance
			}
		}
		
		// 
		if(xmlTimetable.getCalendarDay() != null){
			for(org.exolab.castor.types.Date calendarDay : xmlTimetable.getCalendarDay()){
				timetable.addCalendarDay(new Date(calendarDay.toLong()));
			}
		}
		
		if(xmlTimetable.getPeriod() != null){
			for(chouette.schema.Period xmlPeriod : xmlTimetable.getPeriod()){
				timetable.addPeriod(new Timetable.Period(new Date(xmlPeriod.getStartOfPeriod().toLong()),new Date(xmlPeriod.getStartOfPeriod().toLong())));
			}
		}
		
		xmlTimetable.getDayType();
		if(xmlTimetable.getVehicleJourneyId() != null){
			for(String vehicleJourneyId : xmlTimetable.getVehicleJourneyId()){
				timetable.addVehicleJourneyId(getNonEmptyTrimedString(vehicleJourneyId));
			}
		}
		timetable.setVersion(getNonEmptyTrimedString(xmlTimetable.getVersion()));
	
		
		return timetable;
	}

}
