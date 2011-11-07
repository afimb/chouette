package fr.certu.chouette.exchange.csv.exporter.producer;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;

public class TimetableProducer extends AbstractCSVNeptuneProducer<Timetable> {

	   public static final String            TIMETABLE_LABEL_TITLE = "Libellé du tableau de marche";
	   public static final String            ALIAS_TITLE           = "Alias";
	   public static final String            MONDAY_TITLE          = "Lundi (O/N)";
	   public static final String            TUESDAY_TITLE         = "Mardi (O/N)";
	   public static final String            WEDNESDAY_TITLE       = "Mercredi (O/N)";
	   public static final String            THURSDAY_TITLE        = "Jeudi (O/N)";
	   public static final String            FRIDAY_TITLE          = "Vendredi (O/N)";
	   public static final String            SATURDAY_TITLE        = "Samedi (O/N)";
	   public static final String            SUNDAY_TITLE          = "Dimanche (O/N)";
	   public static final String            START_DATE_TITLE      = "Date de début d'application";
	   public static final String            END_DATE_TITLE        = "Date de fin d'application";
	   public static final String            CALENDAR_DAY_TITLE    = "Jour d'application";

	   private static final String           YES_OPTION            = "O";
	   private static final String           NO_OPTION            = "N";
	   
	   private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	   
	@Override
	public List<String[]> produce(Timetable timetable) {
		List<String[]> csvLinesList = new ArrayList<String[]>();
		if (timetable.getName() != null && ! timetable.getName().isEmpty())
		   csvLinesList.add(createCSVLine(TIMETABLE_LABEL_TITLE, timetable.getName()));
		else
		   csvLinesList.add(createCSVLine(TIMETABLE_LABEL_TITLE, timetable.getComment()));
		csvLinesList.add(createCSVLine(ALIAS_TITLE, timetable.getComment()));
		List<DayTypeEnum> timetableDayTypes = timetable.getDayTypes();
		csvLinesList.add(createCSVLine(MONDAY_TITLE, getDayTypeString(timetableDayTypes, DayTypeEnum.MONDAY)));
		csvLinesList.add(createCSVLine(TUESDAY_TITLE, getDayTypeString(timetableDayTypes, DayTypeEnum.TUESDAY)));
		csvLinesList.add(createCSVLine(WEDNESDAY_TITLE, getDayTypeString(timetableDayTypes, DayTypeEnum.WEDNESDAY)));
		csvLinesList.add(createCSVLine(THURSDAY_TITLE, getDayTypeString(timetableDayTypes, DayTypeEnum.THURSDAY)));
		csvLinesList.add(createCSVLine(FRIDAY_TITLE, getDayTypeString(timetableDayTypes, DayTypeEnum.FRIDAY)));
		csvLinesList.add(createCSVLine(SATURDAY_TITLE, getDayTypeString(timetableDayTypes, DayTypeEnum.SATURDAY)));
		csvLinesList.add(createCSVLine(SUNDAY_TITLE, getDayTypeString(timetableDayTypes, DayTypeEnum.SUNDAY)));
		List<Period> timetablePeriods = timetable.getPeriods();
		csvLinesList.addAll(getPeriodsCSVLines(timetablePeriods));
		List<Date> timetableCalendarDays = timetable.getCalendarDays();
		csvLinesList.add(getCalendarDaysCSVLines(timetableCalendarDays));
		
		return csvLinesList;
	}
	
	String getDayTypeString(List<DayTypeEnum> timetableDayTypes, DayTypeEnum dayType){
		if(timetableDayTypes.contains(dayType)){
			return YES_OPTION;
		}
		return NO_OPTION;
	}
	
	List<String[]> getPeriodsCSVLines(List<Period> timetablePeriods){
		List<String[]> periodsCSVLines = new ArrayList<String[]>();
		String[] startDates = new String[TITLE_COLUMN+1+timetablePeriods.size()];
		periodsCSVLines.add(startDates);
		String[] endDates = new String[TITLE_COLUMN+1+timetablePeriods.size()];
		periodsCSVLines.add(endDates);
		
		startDates[TITLE_COLUMN] = START_DATE_TITLE;
		endDates[TITLE_COLUMN] = END_DATE_TITLE;
		
		int i = TITLE_COLUMN+1;
		for(Period timetablePeriod : timetablePeriods){
			startDates[i] = sdf.format(timetablePeriod.getStartDate());
			endDates[i] = sdf.format(timetablePeriod.getEndDate());
			i++;
		}
		
		return periodsCSVLines;
	}
	
	String[] getCalendarDaysCSVLines(List<Date> timetableCalendarDays){
		String[] calendarDaysCSVLine = new String[TITLE_COLUMN+1+timetableCalendarDays.size()];
		
		calendarDaysCSVLine[TITLE_COLUMN] = CALENDAR_DAY_TITLE;
		
		int i = TITLE_COLUMN+1;
		for(Date timetableCalendarDay : timetableCalendarDays){
			calendarDaysCSVLine[i] = sdf.format(timetableCalendarDay);
			i++;
		}
		
		return calendarDaysCSVLine;
	}
}
