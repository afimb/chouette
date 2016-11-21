package mobi.chouette.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.model.type.DayTypeEnum;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * Chouette Timetable
 * <p/>
 * Neptune mapping : Timetable <br/>
 * Gtfs mapping : service in calendar and calendar_dates <br/>
 */
@Entity
@Table(name = "time_tables")
@Cacheable
@NoArgsConstructor
@ToString(callSuper = true, exclude = { "vehicleJourneys" })
public class Timetable extends NeptuneIdentifiedObject {
	private static final long serialVersionUID = -1598554061982685113L;
	public static final long ONE_DAY = 3600000 * 24;

	@Getter
	@Setter
	@GenericGenerator(name = "time_tables_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "time_tables_id_seq"),
			@Parameter(name = "increment_size", value = "100") })
	@GeneratedValue(generator = "time_tables_id_seq")
	@Id
	@Column(name = "id", nullable = false)
	protected Long id;

	/**
	 * mapping day type with enumerations
	 */
	public static final DayTypeEnum[] dayTypeByInt = { DayTypeEnum.Sunday, DayTypeEnum.Monday, DayTypeEnum.Tuesday,
			DayTypeEnum.Wednesday, DayTypeEnum.Thursday, DayTypeEnum.Friday, DayTypeEnum.Saturday };

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
	 *            New value
	 */
	public void setComment(String value) {
		comment = StringUtils.abbreviate(value, 255);
	}

	/**
	 * version <br/>
	 * Note : should be rename as short name in next release
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
	 *            New value
	 */
	public void setVersion(String value) {
		version = StringUtils.abbreviate(value, 255);
	}

	/**
	 * day of week as bit mask
	 * 
	 * @param intDayTypes
	 *            New value
	 * @return The actual value
	 */

	@Getter
	@Setter
	@Column(name = "int_day_types")
	private Integer intDayTypes = 0;

	public List<DayTypeEnum> getDayTypes() {
		List<DayTypeEnum> result = new ArrayList<DayTypeEnum>();
		if (this.intDayTypes != null) {
			for (DayTypeEnum dayType : DayTypeEnum.values()) {
				int mask = 1 << dayType.ordinal();
				if ((this.intDayTypes & mask) == mask) {
					result.add(dayType);
				}
			}
		}
		return result;
	}

	public void setDayTypes(List<DayTypeEnum> arrayList) {
		int value = 0;
		for (DayTypeEnum dayType : arrayList) {
			int mask = 1 << dayType.ordinal();
			value |= mask;
		}
		this.intDayTypes = value;
	}

	/**
	 * add a dayType if not already present
	 * 
	 * @param dayType
	 */
	public void addDayType(DayTypeEnum dayType) {
		if (dayType != null) {
			int mask = 1 << dayType.ordinal();
			this.intDayTypes |= mask;
		}
	}

	/**
	 * remove a daytype
	 * 
	 * @param dayType
	 */
	public void removeDayType(DayTypeEnum dayType) {
		if (dayType != null) {
			int mask = 1 << dayType.ordinal();
			this.intDayTypes &= ~mask;
		}
	}

	/**
	 * first valid day in timetable
	 * 
	 * @param startOfPeriod
	 *            New value
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
	 *            New value
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
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ElementCollection(fetch = FetchType.EAGER)
	@Fetch(FetchMode.JOIN)
	@CollectionTable(name = "time_table_dates", joinColumns = @JoinColumn(name = "time_table_id"))
	@OrderColumn(name = "position", nullable = false)
	private List<CalendarDay> calendarDays = new ArrayList<CalendarDay>(0);

	/**
	 * list of periods
	 * 
	 * @param periods
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ElementCollection(fetch = FetchType.EAGER)
	@Fetch(FetchMode.JOIN)
	@CollectionTable(name = "time_table_periods", joinColumns = @JoinColumn(name = "time_table_id"))
	@OrderColumn(name = "position", nullable = false)
	private List<Period> periods = new ArrayList<Period>(0);

	/**
	 * list of vehicleJourneys
	 * 
	 * @param vehicleJourneys
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToMany(mappedBy = "timetables", fetch = FetchType.LAZY)
	private List<VehicleJourney> vehicleJourneys = new ArrayList<VehicleJourney>(0);

	/**
	 * add a day if not already present
	 * 
	 * @param calendarDay
	 */
	public void addCalendarDay(CalendarDay calendarDay) {
		if (calendarDays == null)
			calendarDays = new ArrayList<CalendarDay>();
		if (calendarDay != null && !calendarDays.contains(calendarDay)) {
			calendarDays.add(calendarDay);
		}
	}

	/**
	 * remove a day
	 * 
	 * @param calendarDay
	 */
	public void removeCalendarDay(CalendarDay calendarDay) {
		if (calendarDays == null)
			calendarDays = new ArrayList<CalendarDay>();
		if (calendarDay != null) {
			calendarDays.remove(calendarDay);
		}
	}

	/**
	 * add a period if not already present
	 * 
	 * @param period
	 */
	public void addPeriod(Period period) {
		if (periods == null)
			periods = new ArrayList<Period>();
		if (period != null && !periods.contains(period)) {
			periods.add(period);
		}

	}

	/**
	 * remove a period
	 * 
	 * @param period
	 */
	public void removePeriod(Period period) {
		if (periods == null)
			periods = new ArrayList<Period>();
		if (period != null) {
			periods.remove(period);
		}
	}

	/**
	 * add a vehicle journey if not already present
	 * 
	 * @param vehicleJourney
	 */
	public void addVehicleJourney(VehicleJourney vehicleJourney) {
		if (!getVehicleJourneys().contains(vehicleJourney)) {
			getVehicleJourneys().add(vehicleJourney);
		}
		if (!vehicleJourney.getTimetables().contains(this)) {
			vehicleJourney.getTimetables().add(this);
		}
	}

	/**
	 * remove a vehicle journey
	 * 
	 * @param vehicleJourney
	 */
	public void removeVehicleJourney(VehicleJourney vehicleJourney) {
		getVehicleJourneys().remove(vehicleJourney);
		vehicleJourney.getTimetables().remove(this);
	}

	/**
	 * build a bitwise dayType mask for filtering
	 * 
	 * @param dayTypes
	 *            a list of included day types
	 * @return binary mask for selected day types
	 */
	public static int buildDayTypeMask(List<DayTypeEnum> dayTypes) {
		int value = 0;
		if (dayTypes == null)
			return value;
		for (DayTypeEnum dayType : dayTypes) {
			value += buildDayTypeMask(dayType);
		}
		return value;
	}

	/**
	 * build a bitwise dayType mask for filtering
	 * 
	 * @param dayType
	 *            the dayType to filter
	 * @return binary mask for a day type
	 */
	public static int buildDayTypeMask(DayTypeEnum dayType) {
		return (int) Math.pow(2, dayType.ordinal());
	}

	/**
	 * get peculiar dates
	 * 
	 * @return a list of active dates and periods converted to dates if
	 *         exclusion present
	 */
	public List<Date> getEffectiveDates() {
		List<Date> ret = getPeculiarDates();
		if (!getExcludedDates().isEmpty())
		{
		for (Period period : periods) {
			List<Date> added = toDates(period);
			for (Date date : added) {
				if (!ret.contains(date)) ret.add(date);
			}
		}
		}
		Collections.sort(ret);
		return ret;
	}

	/**
	 * get peculiar dates
	 * 
	 * @return a list of active dates
	 */
	public List<Date> getPeculiarDates() {
		List<Date> ret = new ArrayList<>();
		for (CalendarDay day : getCalendarDays()) {
			if (day.getIncluded())
				ret.add(new Date(day.getDate().getTime()));
		}
		Collections.sort(ret);
		return ret;
	}

	/**
	 * get excluded dates
	 * 
	 * @return a list of excluded dates
	 */
	public List<Date> getExcludedDates() {
		List<Date> ret = new ArrayList<>();
		for (CalendarDay day : getCalendarDays()) {
			if (!day.getIncluded())
				ret.add(new Date(day.getDate().getTime()));
		}
		return ret;
	}

	/**
	 * check if a Timetable is active on a given date
	 * 
	 * @param aDay
	 * @return true if timetable is active on given date
	 */
	public boolean isActiveOn(final Date aDay) {
		if (getCalendarDays() != null) {
			CalendarDay includedDay = new CalendarDay(aDay, true);
			if (getCalendarDays().contains(includedDay))
				return true;
			CalendarDay excludedDay = new CalendarDay(aDay, false);
			if (getCalendarDays().contains(excludedDay))
				return false;
		}
		if (getIntDayTypes() != null && getIntDayTypes().intValue() != 0 && getPeriods() != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(aDay);

			int aDayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1; // zero on sunday
			int aDayOfWeekFlag = buildDayTypeMask(dayTypeByInt[aDayOfWeek]);
			if ((getIntDayTypes() & aDayOfWeekFlag) == aDayOfWeekFlag) {
				// check if day is in a period
				for (Period period : getPeriods()) {
					if (period.contains(aDay))
						return true;
				}
			}

		}
		return false;
	}

	public boolean isActiveBefore(final Date aDay) {
		return isActiveOnPeriod(getStartOfPeriod(), aDay);
	}

	public boolean isActiveAfter(final Date aDay) {
		return isActiveOnPeriod(aDay, getEndOfPeriod());
	}

	public boolean isActiveOnPeriod(final Date start, final Date end) {
		if(start == null || end == null) {
			return false;
		} else {
			Date day = new Date(start.getTime());
			while (day.before(end)) {
				if (isActiveOn(day))
					return true;
				day.setTime(day.getTime() + ONE_DAY);
	
			}
			return isActiveOn(end);
		}
	}

	/**
	 * calculate startOfPeriod and endOfPeriod form dates and periods
	 */
	public void computeLimitOfPeriods() {
		Date startOfPeriod = null;
		Date endOfPeriod = null;
		for (Period period : getPeriods()) {
			if (startOfPeriod == null || startOfPeriod.after(period.getStartDate())) {
				startOfPeriod = (Date) period.getStartDate().clone();
			}
			if (endOfPeriod == null || endOfPeriod.before(period.getEndDate())) {
				endOfPeriod = (Date) period.getEndDate().clone();
			}
		}
		// check DayType
		Calendar c = Calendar.getInstance();
		if (startOfPeriod != null && endOfPeriod != null) {
			while (startOfPeriod.before(endOfPeriod) && !isActiveOn(startOfPeriod)) {
				c.setTime(startOfPeriod);
				c.add(Calendar.DATE, 1);
				startOfPeriod.setTime(c.getTimeInMillis());
			}
			while (endOfPeriod.after(startOfPeriod) && !isActiveOn(endOfPeriod)) {
				c.setTime(endOfPeriod);
				c.add(Calendar.DATE, -1);
				endOfPeriod.setTime(c.getTimeInMillis());
			}
		}
		for (CalendarDay calendarDay : getCalendarDays()) {
			Date date = calendarDay.getDate();
			if (calendarDay.getIncluded()) {
				if (startOfPeriod == null || date.before(startOfPeriod))
					startOfPeriod = (Date) date.clone();
				if (endOfPeriod == null || date.after(endOfPeriod))
					endOfPeriod = (Date) date.clone();
			}
		}
		setStartOfPeriod(startOfPeriod);
		setEndOfPeriod(endOfPeriod);

	}

	/**
	 * return periods broken on excluded dates, for exports without date
	 * exclusion; one day periods are excluded (see getEffectiveDates
	 * 
	 * @return periods
	 */
	public List<Period> getEffectivePeriods() {
		List<Period> effectivePeriods = getRealPeriods();
		for (Iterator<Period> iterator = effectivePeriods.iterator(); iterator.hasNext();) {
			Period period = iterator.next();
			if (dateEquals(period.getStartDate(), period.getEndDate())) {
				// single date ; remove it
				iterator.remove();
			}
		}
		return effectivePeriods;
	}

	/**
	 * return copy of period or empty if excluding dates exists exclusion
	 * 
	 * @return periods
	 */
	private List<Period> getRealPeriods() {
		List<Date> dates = getExcludedDates();
		List<Period> effectivePeriods = new ArrayList<Period>();
		if (!dates.isEmpty())
			return effectivePeriods;
		// copy periods
		for (Period period : getPeriods()) {
			if (!effectivePeriods.contains(period))
				effectivePeriods.add(new Period(period.getStartDate(), period.getEndDate()));
		}

		Collections.sort(effectivePeriods);
		return effectivePeriods;
	}

	private boolean dateEquals(Date first, Date second) {
		long df = first.getTime() / 86400000;
		long ds = second.getTime() / 86400000;
		return df == ds;
	}

	private List<Date> toDates(Period period) {
		List<Date> dates = new ArrayList<>();

		List<Date> excluded = getExcludedDates();
		if (getIntDayTypes() != null && getIntDayTypes().intValue() != 0) {
			Calendar c = Calendar.getInstance();
			c.setTime(period.getStartDate());

			while (!c.getTime().after(period.getEndDate())) {
				int aDayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1; // zero on
																	// sunday
				int aDayOfWeekFlag = buildDayTypeMask(dayTypeByInt[aDayOfWeek]);
				if ((getIntDayTypes() & aDayOfWeekFlag) == aDayOfWeekFlag) {
					Date d = new Date(c.getTime().getTime());
					if (!excluded.contains(d))
					   dates.add(d);
				}
				c.add(Calendar.DATE, 1);
			}

		}
		return dates;
	}

}
