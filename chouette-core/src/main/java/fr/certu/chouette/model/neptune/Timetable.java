package fr.certu.chouette.model.neptune;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;

/**
 * Neptune Timetable
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 */
@Entity
@Table(name = "time_tables")
@NoArgsConstructor
@Log4j
public class Timetable extends NeptuneIdentifiedObject
{
	private static final long serialVersionUID = -1598554061982685113L;
	private static final long ONE_DAY = 3600000*24;

	// constant for persistence fields
	/**
	 * name of comment attribute for {@link Filter} attributeName construction
	 */
	public static final String COMMENT = "comment";
	/**
	 * name of version attribute for {@link Filter} attributeName construction
	 */
	public static final String VERSION = "version";
	/**
	 * name of dayTypes attribute for {@link Filter} attributeName construction
	 */
	public static final String DAYTYPES_MASK = "intDayTypes";
	/**
	 * name of calendarDays attribute for {@link Filter} attributeName
	 * construction
	 */
	public static final String CALENDARDAYS = "calendarDays";
	/**
	 * name of periods attribute for {@link Filter} attributeName construction
	 */
	public static final String PERIODS = "periods";

	/**
	 * mapping day type with enumerations
	 */
	private static final DayTypeEnum[] dayTypeByInt = { DayTypeEnum.Sunday,
		DayTypeEnum.Monday,
		DayTypeEnum.Tuesday,
		DayTypeEnum.Wednesday,
		DayTypeEnum.Thursday,
		DayTypeEnum.Friday,
		DayTypeEnum.Saturday };

	@Getter
	@Column(name = "comment")
	private String comment;

	@Getter
	@Setter
	@Column(name = "version")
	private String version;

	@Getter
	@Setter
	@Column(name = "int_day_types")
	private Integer intDayTypes;

	@Getter
	@Setter
	@Column(name = "start_date")
	private Date startOfPeriod;

	@Getter
	@Setter
	@Column(name = "end_date")
	private Date endOfPeriod;

	@Getter
	@Setter
	@ElementCollection
	@CollectionTable(name = "time_table_dates", joinColumns = @JoinColumn(name = "time_table_id"))
	@OrderColumn(name = "position", nullable = false)
	private List<CalendarDay> calendarDays = new ArrayList<CalendarDay>(0);

	@Getter
	@Setter
	@ElementCollection
	@CollectionTable(name = "time_table_periods", joinColumns = @JoinColumn(name = "time_table_id"))
	@OrderColumn(name = "position", nullable = false)
	private List<Period> periods = new ArrayList<Period>(0);

	@Getter
	@Setter
	@ManyToMany(mappedBy = "timetables")
	private List<VehicleJourney> vehicleJourneys = new ArrayList<VehicleJourney>(0);

	/**
	 * List of dayTypes <br/>
	 * this list is synchronized with intDayTypes at each update <br/>
	 */
	@Transient
	private List<DayTypeEnum> dayTypes;

	/**
	 * Neptune ObjectId of vehicleJourneys attached to this timetable <br/>
	 * (import/export usage) <br/>
	 * <i>readable/writable</i>
	 */
	@Getter
	@Setter
	@Transient
	private List<String> vehicleJourneyIds;

	public void setComment(String value)
	{
		if (value != null && value.length() > 255)
		{
			log.warn("comment too long, truncated " + value);
			comment = value.substring(0, 255);
		}
		else
		{
			comment = value;
		}
	}

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

	@Override
	public String toString(String indent, int level)
	{
		StringBuilder sb = new StringBuilder(super.toString(indent, level));
		sb.append("\n").append(indent).append("  comment = ").append(comment);
		sb.append("\n").append(indent).append("  version = ").append(version);
		sb.append("\n").append(indent).append("  startOfPeriod = ").append(formatDate(startOfPeriod));
		sb.append("\n").append(indent).append("  endOfPeriod = ").append(formatDate(endOfPeriod));
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
			for (CalendarDay calendarDay : getCalendarDays())
			{
				sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(calendarDay);
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
			return "undefined";
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

	public List<Date> getPeculiarDates()
	{
		List<Date> ret = new ArrayList<>();
		for (CalendarDay day : calendarDays) 
		{
			if (day.getIncluded()) ret.add(day.getDate());
		}
		return ret;
	}
	
	public List<Date> getExcludedDates()
	{
		List<Date> ret = new ArrayList<>();
		for (CalendarDay day : calendarDays) 
		{
			if (!day.getIncluded()) ret.add(day.getDate());
		}
		return ret;
	}
	
	/**
	 * check if a Timetable is active on a given date
	 * 
	 * @param aDay
	 * @return
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
			if (startOfPeriod == null || startOfPeriod.after(period.getStartDate()))
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
	 * return periods broken on excluded dates, for exports without date exclusion
	 * 
	 * @return
	 */
	public List<Period> getEffectivePeriods()
	{
		List<Date> dates = getExcludedDates();
		List<Period> effectivePeriods = new ArrayList<Period>();
		// copy periods
		for (Period period : periods) 
		{
			effectivePeriods.add(new Period(period.getStartDate(),period.getEndDate()));
		}
		if (!effectivePeriods.isEmpty())
		{
			for (Date aDay : dates) 
			{
				// reduce or split periods around excluded date
				for (ListIterator<Period> iterator = effectivePeriods.listIterator(); iterator
						.hasNext();) 
				{
					Period period = iterator.next();
					if (period.getStartDate().equals(aDay))
					{
						period.getStartDate().setTime(period.getStartDate().getTime()+ONE_DAY);
						if (period.getStartDate().after(period.getEndDate())) iterator.remove();
					}
					else if (period.getEndDate().equals(aDay))
					{
						period.getEndDate().setTime(period.getEndDate().getTime()+ONE_DAY);							
						if (period.getStartDate().after(period.getEndDate())) iterator.remove();
					}
					else if (period.contains(aDay))
					{
						// split period
						Period before = new Period(period.getStartDate(),new Date(aDay.getTime()-ONE_DAY));
						period.setStartDate(new Date(aDay.getTime()+ONE_DAY));
						iterator.add(before);
					}
					
				}
			}
		}
		Collections.sort(effectivePeriods);
		return effectivePeriods;
	}

	@Override
	public <T extends NeptuneObject> boolean compareAttributes(
			T anotherObject)
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
			if (!sameValue(this.getRegistrationNumber(), another.getRegistrationNumber()))
				return false;

			if (!sameValues(this.getDayTypes(), another.getDayTypes()))
				return false;
			if (!sameValues(this.getCalendarDays(), another.getCalendarDays()))
				return false;
			if (!sameValues(this.getPeriods(), another.getPeriods()))
				return false;
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public String toURL()
	{
		return "time_tables/" + getId();
	}

}
