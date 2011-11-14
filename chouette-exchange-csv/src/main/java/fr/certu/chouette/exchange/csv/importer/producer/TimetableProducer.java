package fr.certu.chouette.exchange.csv.importer.producer;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import fr.certu.chouette.exchange.csv.exception.ExchangeException;
import fr.certu.chouette.exchange.csv.exception.ExchangeExceptionCode;
import fr.certu.chouette.exchange.csv.importer.ChouetteCsvReader;
import fr.certu.chouette.exchange.csv.importer.report.CSVReportItem;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;
import fr.certu.chouette.plugin.report.Report;

public class TimetableProducer extends AbstractModelProducer<Timetable>
{
   private static final Logger logger = Logger.getLogger(TimetableProducer.class);

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

   @Override
   public Timetable produce(ChouetteCsvReader csvReader, String[] firstLine, String objectIdPrefix, Report report) throws ExchangeException
   {
      Timetable timetable = new Timetable();
      if (firstLine[TITLE_COLUMN].equals(TIMETABLE_LABEL_TITLE))
      {
         timetable.setComment(firstLine[TITLE_COLUMN + 1]);
      }
      else
      {
         CSVReportItem reportItem = new CSVReportItem(CSVReportItem.KEY.MANDATORY_TAG, Report.STATE.ERROR, firstLine[TITLE_COLUMN] +"<>"+ TIMETABLE_LABEL_TITLE);
         report.addItem(reportItem);
         return null;
      }
      try
      {
         timetable.setName(loadStringParam(csvReader, ALIAS_TITLE));
         timetable.setObjectId(objectIdPrefix+":"+Timetable.TIMETABLE_KEY+":"+timetable.getName());
         if (!NeptuneIdentifiedObject.checkObjectId(timetable.getObjectId()))
         {
            CSVReportItem reportItem = new CSVReportItem(CSVReportItem.KEY.BAD_ID, Report.STATE.ERROR, timetable.getComment(), timetable.getObjectId());
            report.addItem(reportItem);
            return null;
         }
         if (loadStringParam(csvReader, MONDAY_TITLE).equals(YES_OPTION))
         {
            timetable.addDayType(DayTypeEnum.MONDAY);
         }
         if (loadStringParam(csvReader, TUESDAY_TITLE).equals(YES_OPTION))
         {
            timetable.addDayType(DayTypeEnum.TUESDAY);
         }
         if (loadStringParam(csvReader, WEDNESDAY_TITLE).equals(YES_OPTION))
         {
            timetable.addDayType(DayTypeEnum.WEDNESDAY);
         }
         if (loadStringParam(csvReader, THURSDAY_TITLE).equals(YES_OPTION))
         {
            timetable.addDayType(DayTypeEnum.THURSDAY);
         }
         if (loadStringParam(csvReader, FRIDAY_TITLE).equals(YES_OPTION))
         {
            timetable.addDayType(DayTypeEnum.FRIDAY);
         }
         if (loadStringParam(csvReader, SATURDAY_TITLE).equals(YES_OPTION))
         {
            timetable.addDayType(DayTypeEnum.SATURDAY);
         }
         if (loadStringParam(csvReader, SUNDAY_TITLE).equals(YES_OPTION))
         {
            timetable.addDayType(DayTypeEnum.SUNDAY);
         }
         List<Date> startDates = loadDatesParam(csvReader, START_DATE_TITLE);
         List<Date> endDates = loadDatesParam(csvReader, END_DATE_TITLE);
         if (startDates.size() != endDates.size())
         {
        	 logger.warn("the number of start dates is different of the number of end dates");
             CSVReportItem reportItem = new CSVReportItem(CSVReportItem.KEY.BAD_TIMETABLE_PERIODS, Report.STATE.WARNING);
             report.addItem(reportItem);
         }
         int size = Math.min(startDates.size(), endDates.size());
         for (int i = 0; i < size; i++)
         {
            Period period = new Period(startDates.get(i), endDates.get(i));
            timetable.addPeriod(period);
         }
         List<Date> calendarDays = loadDatesParam(csvReader, CALENDAR_DAY_TITLE);
         for (Date calendarDay : calendarDays)
         {
            timetable.addCalendarDay(calendarDay);
         }

      }
      catch (ExchangeException e)
      {
         logger.error("CSV reading failed",e);
         CSVReportItem reportItem = new CSVReportItem(CSVReportItem.KEY.MANDATORY_TAG, Report.STATE.ERROR, e.getLocalizedMessage());
         report.addItem(reportItem);
         return null;
      }
      CSVReportItem reportItem = new CSVReportItem(CSVReportItem.KEY.OK_TIMETABLE, Report.STATE.OK, timetable.getName(),timetable.getComment());
      report.addItem(reportItem);
      return timetable;
   }


   private List<Date> loadDatesParam(CSVReader csvReader, String title) throws ExchangeException
   {
      String[] currentLine = null;
      List<Date> dates = new ArrayList<Date>();
      try
      {
         currentLine = csvReader.readNext();
      }
      catch (IOException e)
      {
         throw new ExchangeException(ExchangeExceptionCode.INVALID_CSV_FILE, e);
      }
      if (currentLine[TITLE_COLUMN].equals(title))
      {
         for (int i = TITLE_COLUMN + 1; i < currentLine.length; i++)
         {
            Date date = getDateValue(i, currentLine, logger);
            if (date == null) break;
            dates.add(date);
         }
      }
      else
      {
         throw new ExchangeException(ExchangeExceptionCode.INVALID_CSV_FILE, "Unable to read '" + title
               + "' in csv file "+currentLine[TITLE_COLUMN]+" found");
      }
      return dates;
   }
}
