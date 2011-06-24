package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import fr.certu.chouette.model.neptune.Period;
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
				timetable.addCalendarDay(getSqlDate(calendarDay));
			}
		}
		
		if(xmlTimetable.getPeriod() != null){
			for(chouette.schema.Period xmlPeriod : xmlTimetable.getPeriod()){
				timetable.addPeriod(new Period(getSqlDate(xmlPeriod.getStartOfPeriod()),getSqlDate(xmlPeriod.getStartOfPeriod())));
			}
		}
		
		xmlTimetable.getDayType();
		if(xmlTimetable.getVehicleJourneyId() != null){
			for(String vehicleJourneyId : xmlTimetable.getVehicleJourneyId()){
				timetable.addVehicleJourneyId(getNonEmptyTrimedString(vehicleJourneyId));
			}
		}
		
		return timetable;
	}

}
