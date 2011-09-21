package fr.certu.chouette.exchange.csv.neptune.importer.producer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import fr.certu.chouette.exchange.xml.neptune.exception.ExchangeException;
import fr.certu.chouette.exchange.xml.neptune.exception.ExchangeExceptionCode;
import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;
import fr.certu.chouette.plugin.report.ReportItem;

public class TimetableProducer extends AbstractModelProducer<Timetable> {

	public static final String TIMETABLE_LABEL_TITLE = "Libellé du tableau de marche";
	public static final String ALIAS_TITLE = "Alias";
	public static final String MONDAY_TITLE = "Lundi (O/N)";
	public static final String TUESDAY_TITLE = "Mardi (O/N)";
	public static final String WEDNESDAY_TITLE = "Mercredi (O/N)";
	public static final String THURSDAY_TITLE = "Jeudi (O/N)";
	public static final String FRIDAY_TITLE = "Vendredi (O/N)";
	public static final String SATURDAY_TITLE = "Samedi (O/N)";
	public static final String SUNDAY_TITLE = "Dimanche (O/N)";
	public static final String START_DATE_TITLE = "Date de début d'application";
	public static final String END_DATE_TITLE = "Date de fin d'application";
	public static final String CALENDAR_DAY_TITLE = "Jour d'application";

	public static final int TITLE_COLUMN = 7;
	private static final String YES_OPTION = "O";
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	

	
	@Override
	public Timetable produce(CSVReader csvReader, String[] firstLine){
		Timetable timetable = new Timetable();
		if(firstLine[TITLE_COLUMN].equals(TIMETABLE_LABEL_TITLE)){
			timetable.setName(firstLine[TITLE_COLUMN+1]);
		}
		else{
			return null;
		}
		try {
			timetable.setComment(loadStringParam(csvReader, ALIAS_TITLE));
			if(loadStringParam(csvReader, MONDAY_TITLE).equals(YES_OPTION)){
				timetable.addDayType(DayTypeEnum.MONDAY);
			}
			if(loadStringParam(csvReader, TUESDAY_TITLE).equals(YES_OPTION)){
				timetable.addDayType(DayTypeEnum.TUESDAY);
			}
			if(loadStringParam(csvReader, WEDNESDAY_TITLE).equals(YES_OPTION)){
				timetable.addDayType(DayTypeEnum.WEDNESDAY);
			}
			if(loadStringParam(csvReader, THURSDAY_TITLE).equals(YES_OPTION)){
				timetable.addDayType(DayTypeEnum.THURSDAY);
			}
			if(loadStringParam(csvReader, FRIDAY_TITLE).equals(YES_OPTION)){
				timetable.addDayType(DayTypeEnum.FRIDAY);
			}
			if(loadStringParam(csvReader, SATURDAY_TITLE).equals(YES_OPTION)){
				timetable.addDayType(DayTypeEnum.SATURDAY);
			}
			if(loadStringParam(csvReader, SUNDAY_TITLE).equals(YES_OPTION)){
				timetable.addDayType(DayTypeEnum.SUNDAY);
			}
			List<Date> startDates = loadDatesParam(csvReader, START_DATE_TITLE);
			List<Date> endDates = loadDatesParam(csvReader, START_DATE_TITLE);
			if(startDates.size() != endDates.size()){
				throw new ExchangeException(ExchangeExceptionCode.INVALID_CSV_FILE, "bad number of dates");
			}
			for(int i = 0 ; i<startDates.size() ; i++){
				Period period = new Period(startDates.get(i),endDates.get(i));
				timetable.addPeriod(period);
			}
			List<Date> calendarDays = loadDatesParam(csvReader, CALENDAR_DAY_TITLE);
			for(Date calendarDay : calendarDays){
				timetable.addCalendarDay(calendarDay);
			}
			
			
		} catch (ExchangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return timetable;
	}

	private String loadStringParam(CSVReader csvReader, String title) throws ExchangeException{
		String[] currentLine = null;
		try {
			currentLine = csvReader.readNext();
		} catch (IOException e) {
			throw new ExchangeException(ExchangeExceptionCode.INVALID_CSV_FILE, e);
		}
		if(currentLine[TITLE_COLUMN].equals(title)){
			return currentLine[TITLE_COLUMN+1];
		}
		else{
			throw new ExchangeException(ExchangeExceptionCode.INVALID_CSV_FILE,"Unable to read '"+title+"' in csv file");
		}
	}
	
	private List<Date> loadDatesParam(CSVReader csvReader, String title) throws ExchangeException{
		String[] currentLine = null;
		List<Date> dates = new ArrayList<Date>();
		try {
			currentLine = csvReader.readNext();
		} catch (IOException e) {
			throw new ExchangeException(ExchangeExceptionCode.INVALID_CSV_FILE, e);
		}
		if(currentLine[TITLE_COLUMN].equals(title)){
			try {
				for(int i = TITLE_COLUMN+1 ; i<currentLine.length ; i++){
					dates.add(new Date(DATE_FORMAT.parse(currentLine[i]).getTime()));
				}
			} catch (ParseException e) {
				throw new ExchangeException(ExchangeExceptionCode.INVALID_CSV_FILE,"Unable to read date :"+e.getMessage());
			}
		}
		else{
			throw new ExchangeException(ExchangeExceptionCode.INVALID_CSV_FILE,"Unable to read '"+title+"' in csv file");
		}
		return dates;
	}
}
