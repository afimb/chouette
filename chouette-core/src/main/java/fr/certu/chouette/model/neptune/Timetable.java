package fr.certu.chouette.model.neptune;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;

/**
 * Neptune Timetable
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 */
public class Timetable extends NeptuneIdentifiedObject
{
   private static final long    serialVersionUID = -1598554061982685113L;

   // constant for persistence fields
   /**
    * name of comment attribute for {@link Filter} attributeName construction
    */
   public static final String   COMMENT          = "comment";
   /**
    * name of version attribute for {@link Filter} attributeName construction
    */
   public static final String   VERSION          = "version";
   /**
    * name of dayTypes attribute for {@link Filter} attributeName construction
    */
   public static final String   DAYTYPES_MASK    = "intDayTypes";
   /**
    * name of calendarDays attribute for {@link Filter} attributeName
    * construction
    */
   public static final String   CALENDARDAYS     = "calendarDays";
   /**
    * name of periods attribute for {@link Filter} attributeName construction
    */
   public static final String   PERIODS          = "periods";

   /**
    * comment <br/>
    * (import/export usage) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String               comment;
   @Getter
   @Setter
   private String               version;
   /**
    * List of dayTypes <br/>
    * this list is synchronized with intDayTypes at each update <br/>
    */
   private List<DayTypeEnum>    dayTypes;
   /**
    * intDayTypes <br/>
    * restricted to DAO usage
    */
   @Getter
   @Setter
   private Integer              intDayTypes;
   /**
    * individual calendar days affected to timetable <br/>
    * these days are not affected by dayType restrictions
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private List<Date>           calendarDays     = new ArrayList<Date>();
   /**
    * period of calendar affected to timetable<br/>
    * dayType restrictions affect every period <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private List<Period>         periods          = new ArrayList<Period>();

   /**
    * Neptune ObjectId of vehicleJourneys attached to this timetable <br/>
    * (import/export usage) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private List<String>         vehicleJourneyIds;
   /**
    * VehicleJourneys attached to this timetable <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private List<VehicleJourney> vehicleJourneys;

   /**
    * add a dayType if not already present
    * 
    * @param dayType
    */
   public void addDayType(DayTypeEnum dayType)
   {
      if (dayTypes == null)
         dayTypes = getDayTypes();
      if (dayType != null && !dayTypes.contains(dayType))
      {
         dayTypes.add(dayType);
         refreshIntDaytypes();
      }
   }

   /**
    * remove a daytype
    * 
    * @param dayType
    */
   public void removeDayType(DayTypeEnum dayType)
   {
      if (dayTypes == null)
         dayTypes = getDayTypes();
      if (dayType != null)
      {
         if (dayTypes.remove(dayType))
            refreshIntDaytypes();
      }
   }

   /**
    * add a day if not already present
    * 
    * @param calendarDay
    */
   public void addCalendarDay(Date calendarDay)
   {
      if (calendarDays == null)
         calendarDays = new ArrayList<Date>();
      if (calendarDay != null && !calendarDays.contains(calendarDay))
      {
         calendarDays.add(calendarDay);
      }
   }

   /**
    * remove a day
    * 
    * @param calendarDay
    */
   public void removeCalendarDay(Date calendarDay)
   {
      if (calendarDays == null)
         calendarDays = new ArrayList<Date>();
      if (calendarDay != null)
      {
         calendarDays.remove(calendarDay);
      }
   }

   /**
    * add a period if not already present
    * 
    * @param period
    */
   public void addPeriod(Period period)
   {
      if (periods == null)
         periods = new ArrayList<Period>();
      if (period != null && !periods.contains(period))
         periods.add(period);

   }

   /**
    * remove a period
    * 
    * @param period
    */
   public void removePeriod(Period period)
   {
      if (periods == null)
         periods = new ArrayList<Period>();
      if (period != null)
      {
         periods.remove(period);
      }
   }

   /**
    * remove a period at a specific rank
    * 
    * @param rank
    */
   public void removePeriod(int rank)
   {
      if (periods == null)
         periods = new ArrayList<Period>();
      if (rank >= 0 && rank < periods.size())
      {
         periods.remove(rank);
      }
   }

   /**
    * add a vehiclejourney Id
    * 
    * @param vehicleJourneyId
    */
   public void addVehicleJourneyId(String vehicleJourneyId)
   {
      if (vehicleJourneyIds == null)
         vehicleJourneyIds = new ArrayList<String>();
      vehicleJourneyIds.add(vehicleJourneyId);
   }

   /**
    * add a vehicle journey if not already present
    * 
    * @param vehicleJourney
    */
   public void addVehicleJourney(VehicleJourney vehicleJourney)
   {
      if (vehicleJourneys == null)
         vehicleJourneys = new ArrayList<VehicleJourney>();
      if (vehicleJourney != null && !vehicleJourneys.contains(vehicleJourney))
      {
         vehicleJourneys.add(vehicleJourney);
      }
   }

   /**
    * remove a vehicle journey
    * 
    * @param vehicleJourney
    */
   public void removeVehicleJourney(VehicleJourney vehicleJourney)
   {
      if (vehicleJourneys == null)
         vehicleJourneys = new ArrayList<VehicleJourney>();
      if (vehicleJourney != null && vehicleJourneys.contains(vehicleJourney))
      {
         vehicleJourneys.remove(vehicleJourney);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#toString(java.
    * lang.String, int)
    */
   @Override
   public String toString(String indent, int level)
   {
      StringBuilder sb = new StringBuilder(super.toString(indent, level));
      sb.append("\n").append(indent).append("  comment = ").append(comment);
      sb.append("\n").append(indent).append("  version = ").append(version);
      if (dayTypes != null)
      {
         sb.append("\n").append(indent).append(CHILD_ARROW).append("dayTypes");
         for (DayTypeEnum dayType : getDayTypes())
         {
            sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(dayType);
         }
      }
      if (calendarDays != null)
      {
         sb.append("\n").append(indent).append(CHILD_ARROW).append("calendarDays");
         for (Date calendarDay : getCalendarDays())
         {
            sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(formatDate(calendarDay));
         }
      }
      if (periods != null)
      {
         sb.append("\n").append(indent).append(CHILD_ARROW).append("periods");
         for (Period period : getPeriods())
         {
            sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(period);
         }
      }
      if (vehicleJourneyIds != null)
      {
         sb.append("\n").append(indent).append(CHILD_ARROW).append("vehicleJourneyIds");
         for (String vehicleJourneyId : getVehicleJourneyIds())
         {
            sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(vehicleJourneyId);
         }
      }
      if (level > 0)
      {
         int childLevel = level - 1;
         String childIndent = indent + CHILD_INDENT;
         childIndent = indent + CHILD_LIST_INDENT;
         if (vehicleJourneys != null)
         {
            sb.append("\n").append(indent).append(CHILD_ARROW).append("routes");
            for (VehicleJourney vehicleJourney : getVehicleJourneys())
            {
               sb.append("\n").append(indent).append(CHILD_LIST_ARROW)
                     .append(vehicleJourney.toString(childIndent, childLevel));
            }
         }
      }

      return sb.toString();
   }

   /**
    * format a date for toString usage
    * 
    * @param date
    * @return
    */
   private static String formatDate(Date date)
   {
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      if (date != null)
      {
         return dateFormat.format(date);
      }
      else
      {
         return null;
      }
   }

   /**
    * get the affected dayTypes
    * 
    * @return
    */
   public List<DayTypeEnum> getDayTypes()
   {
      if (dayTypes == null)
      {
         dayTypes = new ArrayList<DayTypeEnum>();
      }
      else
      {
         dayTypes.clear();
      }
      if (intDayTypes == null)
      {
         intDayTypes = 0;
      }

      DayTypeEnum[] dayTypeEnum = DayTypeEnum.values();
      for (DayTypeEnum dayType : dayTypeEnum)
      {
         int filterDayType = buildDayTypeMask(dayType);
         if (filterDayType == (intDayTypes.intValue() & filterDayType))
         {
            dayTypes.add(dayType);
         }
      }
      return this.dayTypes;
   }

   /**
    * set the dayTypes
    * 
    * @param dayTypes
    */
   public void setDayTypes(List<DayTypeEnum> dayTypes)
   {
      this.dayTypes = dayTypes;
      refreshIntDaytypes();
   }

   /**
    * synchronize intDayTypes with dayTypes list
    */
   private void refreshIntDaytypes()
   {
      if (this.dayTypes == null)
         this.dayTypes = new ArrayList<DayTypeEnum>();
      intDayTypes = buildDayTypeMask(this.dayTypes);
   }

   /**
    * build a bitwise dayType mask for filtering
    * 
    * @param dayTypes
    *           a list of included day types
    * @return
    */
   public static int buildDayTypeMask(List<DayTypeEnum> dayTypes)
   {
      int value = 0;
      if (dayTypes == null)
         return value;
      for (DayTypeEnum dayType : dayTypes)
      {
         value += buildDayTypeMask(dayType);
      }
      return value;
   }

   /**
    * build a bitwise dayType mask for filtering
    * 
    * @param dayType
    *           the dayType to filter
    * @return
    */
   public static int buildDayTypeMask(DayTypeEnum dayType)
   {
      return (int) Math.pow(2, dayType.ordinal());
   }
}
