package fr.certu.chouette.service.validation.amivif.util;

import java.util.HashSet;
import java.util.Set;

import fr.certu.chouette.service.validation.amivif.Timetable;
import fr.certu.chouette.service.validation.amivif.TridentObject;
import fr.certu.chouette.service.validation.amivif.Timetable.Period;
import fr.certu.chouette.service.validation.amivif.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.amivif.commun.ValidationException;

public class TimetableProducer extends TridentObjectProducer {
    
    public TimetableProducer(ValidationException validationException) {
		super(validationException);
	}
    
	public Timetable getASG(amivif.schema.Timetable castorTimetable) {
		if(castorTimetable == null)
			return null;
		TridentObject tridentObject = super.getASG(castorTimetable);
		Timetable timetable = new Timetable();
		timetable.setTridentObject(tridentObject);
		
		// version optionnel
		timetable.setVersion(castorTimetable.getVersion());
		
		// period 0..w
		if (castorTimetable.getPeriod() != null)
			for (int i = 0; i < castorTimetable.getPeriod().length; i++)
				timetable.addPeriod(getASG(castorTimetable.getPeriod(i), timetable));
		
		// calendarDay 0..w
		if (castorTimetable.getCalendarDay() != null)
			for (int i = 0; i < castorTimetable.getCalendarDay().length; i++)
				timetable.addCalendarDay(castorTimetable.getCalendarDay(i).toDate());
		
		// dayType 0..w
		if (castorTimetable.getDayType() != null)
		{
			for (int i = 0; i < castorTimetable.getDayType().length; i++)
			{
				switch (castorTimetable.getDayType(i)) 
				{
					case FRIDAY:
						timetable.addDayType(Timetable.DayType.Friday);
						break;
					case MARKETDAY:
						timetable.addDayType(Timetable.DayType.MarketDay);
						break;
					case MONDAY:
						timetable.addDayType(Timetable.DayType.Monday);
						break;
					case PUBLICHOLLIDAY:
						timetable.addDayType(Timetable.DayType.PublicHolliday);
						break;
					case SATURDAY:
						timetable.addDayType(Timetable.DayType.Saturday);
						break;
					case SCHOOLHOLLIDAY:
						timetable.addDayType(Timetable.DayType.SchoolHolliday);
						break;
					case SUNDAY:
						timetable.addDayType(Timetable.DayType.Sunday);
						break;
					case THURSDAY:
						timetable.addDayType(Timetable.DayType.Thursday);
						break;
					case TUESDAY:
						timetable.addDayType(Timetable.DayType.Tuesday);
						break;
					case WEDNESDAY:
						timetable.addDayType(Timetable.DayType.Wednesday);
						break;
					case WEEKDAY:
						timetable.addDayType(Timetable.DayType.WeekDay);
						break;
					case WEEKEND:
						timetable.addDayType(Timetable.DayType.WeekEnd);
						break;
					default:
						getValidationException().add(TypeInvalidite.InvalidDayType_Timetable, "Le \"daytype\" ("+castorTimetable.getDayType(i).toString()+") de la \"timetable\" ("+castorTimetable.getObjectId()+") est invalid.");
				}
			}
		}
		
		// stopPointId 0..w
		Set<String> aSet = new HashSet<String>();
		if (castorTimetable.getStopPointId() != null)
			for (int i = 0; i < castorTimetable.getStopPointIdCount(); i++) {
				if (aSet.add(castorTimetable.getVehicleJourneyId(i))) {
					try {
						(new TridentObject()).new TridentId(castorTimetable.getStopPointId(i));
					}
					catch(NullPointerException e) {
						getValidationException().add(TypeInvalidite.NullTridentObjectLineEnd_Line, "Un \"objectId\" ne peut etre null.");
					}
					catch(IndexOutOfBoundsException e) {
						getValidationException().add(TypeInvalidite.InvalidTridentObject, "L'\"objectId\" "+castorTimetable.getStopPointId(i)+" est invalid.");
					}
					timetable.addStopPointId(castorTimetable.getStopPointId(i));
				}
				else
					getValidationException().add(TypeInvalidite.MultipleTridentObject, "La liste \"stopPointId\" de la \"Timetable\" contient plusieur fois le meme identifiant ("+castorTimetable.getStopPointId(i)+").");
			}
		
		// vehicleJourneyId 0..w
		aSet = new HashSet<String>();
		if (castorTimetable.getVehicleJourneyId() != null)
			for (int i = 0; i < castorTimetable.getVehicleJourneyIdCount(); i++) {
				if (aSet.add(castorTimetable.getVehicleJourneyId(i))) {
					try {
						(new TridentObject()).new TridentId(castorTimetable.getVehicleJourneyId(i));
					}
					catch(NullPointerException e) {
						getValidationException().add(TypeInvalidite.NullTridentObjectLineEnd_Line, "Un \"objectId\" ne peut etre null.");
					}
					catch(IndexOutOfBoundsException e) {
						getValidationException().add(TypeInvalidite.InvalidTridentObject, "L'\"objectId\" "+castorTimetable.getVehicleJourneyId(i)+" est invalid.");
					}
					timetable.addVehicleJourneyId(castorTimetable.getVehicleJourneyId(i));
				}
				else
					getValidationException().add(TypeInvalidite.MultipleTridentObject, "La liste \"vehicleJourneyId\" de la \"Timetable\" contient plusieur fois le meme identifiant ("+castorTimetable.getVehicleJourneyId(i)+").");
			}
		
		// comment optionnel
		timetable.setComment(castorTimetable.getComment());
		
		return timetable;
	}

	private Period getASG(amivif.schema.Period castorPeriod, Timetable timetable) {
		if (castorPeriod == null)
			return null;
		Period period = timetable.new Period();
		
		// startOfPeriod obligatoire
		if (castorPeriod.getStartOfPeriod() == null)
			getValidationException().add(TypeInvalidite.NoStartOfPeriod_Period, "Le \"startOfPeriod\" est indispensable dans une \"Period\" du \"Timetable\" ("+timetable.getObjectId().toString()+").");
		else
			period.setStartOfPeriod(castorPeriod.getStartOfPeriod().toDate());
		
		// endOfPeriod obligatoire
		if (castorPeriod.getEndOfPeriod() == null)
			getValidationException().add(TypeInvalidite.NoEndOfPeriod_Period, "Le \"endOfPeriod\" est indispensable dans une \"Period\" du \"Timetable\" ("+timetable.getObjectId().toString()+").");
		else
			period.setEndOfPeriod(castorPeriod.getEndOfPeriod().toDate());
		
		if (period.getStartOfPeriod().after(period.getEndOfPeriod()))
			getValidationException().add(TypeInvalidite.InvalidStartEndOfPeriod_Period, "Le \"startOfPeriod\" ("+period.getStartOfPeriod()+") est posterieur au \"endOfPeriod\" ("+period.getEndOfPeriod()+") dans une \"Period\" du \"Timetable\" ("+timetable.getObjectId().toString()+").");
		
		return period;
	}
}
