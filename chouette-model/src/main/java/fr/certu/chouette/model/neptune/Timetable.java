package fr.certu.chouette.model.neptune;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;

/**
 * Chouette Timetable
 * <p/>
 * Neptune mapping : Timetable <br/>
 * Gtfs mapping : service in calendar and calendar_dates <br/>
 */
@Entity
@Table(name = "time_tables")
@NoArgsConstructor
@Log4j
public class Timetable extends NeptuneIdentifiedObject
{
   private static final long serialVersionUID = -1598554061982685113L;
   private static final long ONE_DAY = 3600000 * 24;


   /**
    * mapping day type with enumerations
    */
   private static final DayTypeEnum[] dayTypeByInt = { DayTypeEnum.Sunday,
      DayTypeEnum.Monday, DayTypeEnum.Tuesday, DayTypeEnum.Wednesday,
      DayTypeEnum.Thursday, DayTypeEnum.Friday, DayTypeEnum.Saturday };

   /**
    * comment <br/>
    * Note : should be rename as name in next release
    * 
    * @return The actual value
    */
   @Getter
   @Column(name = "comment")
   private String comment;

   /**
    * set comment <br/>
    * truncated to 255 characters if too long
    * 
    * @param value
    *           New value
    */
   public void setComment(String value)
   {
      comment = dataBaseSizeProtectedValue(value, "comment", log);
   }

   /**
    * version <br/>
    * Note : should be rename as short name  in next release
    * 
    * @return The actual value
    */
   @Getter
   @Column(name = "version")
   private String version;
   /**
    * set version <br/>
    * truncated to 255 characters if too long
    * 
    * @param value
    *           New value
    */
   public void setVersion(String value)
   {
      version = dataBaseSizeProtectedValue(value,"version",log);
   }

   /**
    * day of week as bit mask
    * 
    * @param intDayTypes
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Column(name = "int_day_types")
   private Integer intDayTypes;

   /**
    * first valid day in timetable
    * 
    * @param startOfPeriod
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Column(name = "start_date")
   private Date startOfPeriod;

   /**
    * last valid day in timetable
    * 
    * @param endOfPeriod
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Column(name = "end_date")
   private Date endOfPeriod;

   /**
    * list of peculiar days
    * 
    * @param calendarDays
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @ElementCollection
   @CollectionTable(name = "time_table_dates", joinColumns = @JoinColumn(name = "time_table_id"))
   @OrderColumn(name = "position", nullable = false)
   private List<CalendarDay> calendarDays = new ArrayList<CalendarDay>(0);

   /**
    * list of periods
    * 
    * @param periods
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @ElementCollection
   @CollectionTable(name = "time_table_periods", joinColumns = @JoinColumn(name = "time_table_id"))
   @OrderColumn(name = "position", nullable = false)
   private List<Period> periods = new ArrayList<Period>(0);

   /**
    * list of vehicleJourneys
    * 
    * @param vehicleJourneys
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @ManyToMany(mappedBy = "timetables")
   private List<VehicleJourney> vehicleJourneys = new ArrayList<VehicleJourney>(
         0);

   /**
    * List of dayTypes <br/>
    * this list is synchronized with intDayTypes at each update <br/>
    */
   @Transient
   private List<DayTypeEnum> dayTypes;

   /**
    * Neptune ObjectId of vehicleJourneys attached to this timetable <br/>
    * (import/export purpose)
    * 
    * @param vehicleJourneyIds
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Transient
   private List<String> vehicleJourneyIds;

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
   public void addCalendarDay(CalendarDay calendarDay)
   {
      if (calendarDays == null)
         calendarDays = new ArrayList<CalendarDay>();
      if (calendarDay != null && !calendarDays.contains(calendarDay))
      {
         calendarDays.add(calendarDay);
      }
   }

   /**
    * add a list of Calendar days <br/>
    * will skip dates already presents
    * 
    * @param list
    */
   public void addCalendarDays(Collection<CalendarDay> list)
   {
      if (calendarDays == null)
         calendarDays = new ArrayList<CalendarDay>();
      for (CalendarDay calendarDay : list)
      {
         if (!calendarDays.contains(calendarDay))
            addCalendarDay(calendarDay);
      }
   }

   /**
    * remove a day
    * 
    * @param calendarDay
    */
   public void removeCalendarDay(CalendarDay calendarDay)
   {
      if (calendarDays == null)
         calendarDays = new ArrayList<CalendarDay>();
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

   /* (non-Javadoc)
    * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#toString(java.lang.String, int)
    */
   @Override
   public String toString(String indent, int level)
   {
      StringBuilder sb = new StringBuilder(super.toString(indent, level));
      sb.append("\n").append(indent).append("  comment = ").append(comment);
      sb.append("\n").append(indent).append("  version = ").append(version);
      sb.append("\n").append(indent).append("  startOfPeriod = ")
      .append(formatDate(startOfPeriod));
      sb.append("\n").append(indent).append("  endOfPeriod = ")
      .append(formatDate(endOfPeriod));
      if (dayTypes != null)
      {
         sb.append("\n").append(indent).append(CHILD_ARROW).append("dayTypes");
         for (DayTypeEnum dayType : getDayTypes())
         {
            sb.append("\n").append(indent).append(CHILD_LIST_ARROW)
            .append(dayType);
         }
      }
      if (calendarDays != null)
      {
         sb.append("\n").append(indent).append(CHILD_ARROW)
         .append("calendarDays");
         for (CalendarDay calendarDay : getCalendarDays())
         {
            sb.append("\n").append(indent).append(CHILD_LIST_ARROW)
            .append(calendarDay);
         }
      }
      if (periods != null)
      {
         sb.append("\n").append(indent).append(CHILD_ARROW).append("periods");
         for (Period period : getPeriods())
         {
            sb.append("\n").append(indent).append(CHILD_LIST_ARROW)
            .append(period);
         }
      }
      if (vehicleJourneyIds != null)
      {
         sb.append("\n").append(indent).append(CHILD_ARROW)
         .append("vehicleJourneyIds");
         for (String vehicleJourneyId : getVehicleJourneyIds())
         {
            sb.append("\n").append(indent).append(CHILD_LIST_ARROW)
            .append(vehicleJourneyId);
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
    * @return string formatted date
    */
   private static String formatDate(Date date)
   {
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      if (date != null)
      {
         return dateFormat.format(date);
      } else
      {
         return "undefined";
      }
   }

   /**
    * get the affected dayTypes
    * 
    * @return list of DayTypeEnum for intDayTypes value
    */
   public List<DayTypeEnum> getDayTypes()
   {
      if (dayTypes == null)
      {
         dayTypes = new ArrayList<DayTypeEnum>();
      } else
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
    * @return binary mask for selected day types
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
    * @return binary mask for a day type
    */
   public static int buildDayTypeMask(DayTypeEnum dayType)
   {
      return (int) Math.pow(2, dayType.ordinal());
   }

   /**
    * get peculiar dates
    * 
    * @return a list of active dates
    */
   public List<Date> getPeculiarDates()
   {
      List<Date> ret = new ArrayList<>();
      for (CalendarDay day : calendarDays)
      {
         if (day.getIncluded())
            ret.add(day.getDate());
      }
      return ret;
   }

   /**
    * get excluded dates
    * 
    * @return a list of excluded dates
    */
   public List<Date> getExcludedDates()
   {
      List<Date> ret = new ArrayList<>();
      for (CalendarDay day : calendarDays)
      {
         if (!day.getIncluded())
            ret.add(day.getDate());
      }
      return ret;
   }

   /**
    * check if a Timetable is active on a given date
    * 
    * @param aDay
    * @return true if timetable is active on given date
    */
   public boolean isActiveOn(Date aDay)
   {
      if (calendarDays != null)
      {
         CalendarDay includedDay = new CalendarDay(aDay, true);
         if (calendarDays.contains(includedDay))
            return true;
         CalendarDay excludedDay = new CalendarDay(aDay, false);
         if (calendarDays.contains(excludedDay))
            return false;
      }
      if (intDayTypes.intValue() != 0 && periods != null)
      {
         Calendar c = Calendar.getInstance();
         c.setTime(aDay);

         int aDayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1; // zero on sunday
         int aDayOfWeekFlag = buildDayTypeMask(dayTypeByInt[aDayOfWeek]);
         if ((intDayTypes & aDayOfWeekFlag) == aDayOfWeekFlag)
         {
            // check if day is in a period
            for (Period period : periods)
            {
               if (period.contains(aDay))
                  return true;
            }
         }

      }
      return false;
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#complete()
    */
   @Override
   public void complete()
   {
      if (isCompleted())
         return;
      super.complete();
      computeLimitOfPeriods();
   }

   /**
    * calculate startOfPeriod and endOfPeriod form dates and periods
    */
   public void computeLimitOfPeriods()
   {
      startOfPeriod = null;
      endOfPeriod = null;
      for (Period period : periods)
      {
         if (startOfPeriod == null
               || startOfPeriod.after(period.getStartDate()))
         {
            startOfPeriod = period.getStartDate();
         }
         if (endOfPeriod == null || endOfPeriod.before(period.getEndDate()))
         {
            endOfPeriod = period.getEndDate();
         }
      }
      // check DayType
      Calendar c = Calendar.getInstance();
      if (startOfPeriod != null && endOfPeriod != null)
      {
         while (startOfPeriod.before(endOfPeriod) && !isActiveOn(startOfPeriod))
         {
            c.setTime(startOfPeriod);
            c.add(Calendar.DATE, 1);
            startOfPeriod.setTime(c.getTimeInMillis());
         }
         while (endOfPeriod.after(startOfPeriod) && !isActiveOn(endOfPeriod))
         {
            c.setTime(endOfPeriod);
            c.add(Calendar.DATE, -1);
            endOfPeriod.setTime(c.getTimeInMillis());
         }
      }
      for (CalendarDay calendarDay : calendarDays)
      {
         Date date = calendarDay.getDate();
         if (calendarDay.getIncluded())
         {
            if (startOfPeriod == null || date.before(startOfPeriod))
               startOfPeriod = date;
            if (endOfPeriod == null || date.after(endOfPeriod))
               endOfPeriod = date;
         }
      }

   }

   /**
    * return periods broken on excluded dates, for exports without date
    * exclusion
    * 
    * @return periods
    */
   public List<Period> getEffectivePeriods()
   {
      List<Date> dates = getExcludedDates();
      List<Period> effectivePeriods = new ArrayList<Period>();
      // copy periods
      for (Period period : periods)
      {
         effectivePeriods.add(new Period(period.getStartDate(), period
               .getEndDate()));
      }
      if (!effectivePeriods.isEmpty())
      {
         for (Date aDay : dates)
         {
            // reduce or split periods around excluded date
            for (ListIterator<Period> iterator = effectivePeriods
                  .listIterator(); iterator.hasNext();)
            {
               Period period = iterator.next();
               if (period.getStartDate().equals(aDay))
               {
                  period.getStartDate().setTime(
                        period.getStartDate().getTime() + ONE_DAY);
                  if (period.getStartDate().after(period.getEndDate()))
                     iterator.remove();
               } else if (period.getEndDate().equals(aDay))
               {
                  period.getEndDate().setTime(
                        period.getEndDate().getTime() + ONE_DAY);
                  if (period.getStartDate().after(period.getEndDate()))
                     iterator.remove();
               } else if (period.contains(aDay))
               {
                  // split period
                  Period before = new Period(period.getStartDate(), new Date(
                        aDay.getTime() - ONE_DAY));
                  period.setStartDate(new Date(aDay.getTime() + ONE_DAY));
                  iterator.add(before);
               }

            }
         }
      }
      Collections.sort(effectivePeriods);
      return effectivePeriods;
   }


   /* (non-Javadoc)
    * @see fr.certu.chouette.model.neptune.NeptuneObject#compareAttributes(fr.certu.chouette.model.neptune.NeptuneObject)
    */
   @Override
   public <T extends NeptuneObject> boolean compareAttributes(T anotherObject)
   {
      if (anotherObject instanceof Timetable)
      {
         Timetable another = (Timetable) anotherObject;
         if (!sameValue(this.getObjectId(), another.getObjectId()))
            return false;
         if (!sameValue(this.getObjectVersion(), another.getObjectVersion()))
            return false;
         if (!sameValue(this.getComment(), another.getComment()))
            return false;
         if (!sameValue(this.getRegistrationNumber(),
               another.getRegistrationNumber()))
            return false;

         if (!sameValues(this.getDayTypes(), another.getDayTypes()))
            return false;
         if (!sameValues(this.getCalendarDays(), another.getCalendarDays()))
            return false;
         if (!sameValues(this.getPeriods(), another.getPeriods()))
            return false;
         return true;
      } else
      {
         return false;
      }
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#getName()
    */
   @Override
   public String getName()
   {
      return getComment();
   }
   
   /* (non-Javadoc)
    * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#setName(java.lang.String)
    */
   @Override
   public void setName(String name)
   {
      setComment(name);
   }
   
   /* (non-Javadoc)
    * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#toURL()
    */
   @Override
   public String toURL()
   {
      return "time_tables/" + getId();
   }


   /**
    * copy a timetable and its periods and calendar days<br/>
    * 
    * @return a copy
    */
   public Timetable copy()
   {
      Timetable tm = new Timetable();
      tm.setObjectId(getObjectId());
      tm.setObjectVersion(getObjectVersion());
      tm.setComment(getComment());
      tm.setVersion(getVersion());
      tm.setIntDayTypes(getIntDayTypes());
      tm.setPeriods(new ArrayList<Period>());
      for (Period period : getPeriods())
      {
         tm.addPeriod(new Period(period.getStartDate(),period.getEndDate()));
      }
      tm.setCalendarDays(new ArrayList<CalendarDay>());
      for (CalendarDay day : getCalendarDays())
      {
         tm.addCalendarDay(new CalendarDay(day.getDate(), day.getIncluded()));
      }
      return tm;
   }


}
