package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import java.util.Date;

import chouette.schema.types.DayTypeType;
import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;

public class TimetableProducer extends AbstractCastorNeptuneProducer<chouette.schema.Timetable, Timetable> {

	@Override
	public chouette.schema.Timetable produce(Timetable timetable) {
		chouette.schema.Timetable castorTimetable = new chouette.schema.Timetable();
		
		//
		populateFromModel(castorTimetable, timetable);
		
		castorTimetable.setComment(getNotEmptyString(timetable.getComment()));
		castorTimetable.setVersion(timetable.getVersion());
		if(timetable.getCalendarDays() != null){
			for(Date calendarDay : timetable.getCalendarDays()){
				if(calendarDay != null){
					castorTimetable.addCalendarDay(new org.exolab.castor.types.Date(calendarDay));
				}
			}
		}
		if(timetable.getPeriods() != null){
			for(Period period : timetable.getPeriods()){
				if(period != null){
					chouette.schema.Period castorPeriod = new chouette.schema.Period();
					castorPeriod.setStartOfPeriod(new  org.exolab.castor.types.Date(period.getStartDate()));
					castorPeriod.setEndOfPeriod(new  org.exolab.castor.types.Date(period.getEndDate()));
					castorTimetable.addPeriod(castorPeriod);
				}
			}
		}
		if(timetable.getDayTypes() != null){
			for(DayTypeEnum dayType : timetable.getDayTypes()){
				if(dayType != null){
					try {
						castorTimetable.addDayType(DayTypeType.fromValue(dayType.value()));						
					} catch (IllegalArgumentException e) {
						// TODO: handle exception
					}
				}
			}
		}
		if(timetable.getVehicleJourneys() != null){
			for(VehicleJourney vehicleJourney : timetable.getVehicleJourneys()){
				castorTimetable.addVehicleJourneyId(getNonEmptyObjectId(vehicleJourney));
			}
		}
								
		return castorTimetable;
	}

}
