package mobi.chouette.model;

import java.sql.Date;
import java.util.ArrayList;
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
@ToString(callSuper = true, exclude = {"vehicleJourneys" })
public class Timetable extends NeptuneIdentifiedObject {
	private static final long serialVersionUID = -1598554061982685113L;
	public static final long ONE_DAY = 3600000 * 24;

	@Getter
	@Setter
	@GenericGenerator(name = "time_tables_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "time_tables_id_seq"),
			@Parameter(name = "increment_size", value = "100") })
	@Id
	@GeneratedValue(generator = "time_tables_id_seq")
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
	@ElementCollection(fetch=FetchType.EAGER)
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
	@ElementCollection(fetch=FetchType.EAGER)
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
	@ManyToMany(mappedBy = "timetables")
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
		if (!vehicleJourney.getTimetables().contains(vehicleJourney)) {
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



}
