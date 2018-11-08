package mobi.chouette.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Period : date period for Timetables
 */
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Period implements Serializable, Comparable<Period> {

	private static final long serialVersionUID = -1964071056103739954L;

	/**
	 * first date of period
	 * 
	 * @param startDate
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Column(name = "period_start")
	private Date startDate;
	
	public void setStartDate(Date date)
	{
		startDate=Timetable.toStableDate(date);
	}

	/**
	 * last date of period
	 * 
	 * @param endDate
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Column(name = "period_end")
	private Date endDate;
	
	public void setEndDate(Date date)
	{
		endDate=Timetable.toStableDate(date);
	}


	/**
	 * check if a date is included in period
	 * 
	 * @param aDay
	 * @return true if date is active in period
	 */
	public boolean contains(Date date) {
		Date aDay = Timetable.toStableDate(date);
		if (startDate == null || endDate == null)
			return false;
		if (aDay.equals(startDate))
			return true;
		if (aDay.equals(endDate))
			return true;
		return aDay.after(startDate) && aDay.before(endDate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Period period) {
		if (startDate == null && period.startDate == null)
			return 0;
		if (startDate == null)
			return -1;
		if (period.startDate == null)
			return 1;
		return startDate.compareTo(period.startDate);
	}

	public Period(Date startDate, Date endDate) {
		this.startDate = Timetable.toStableDate(startDate);
		this.endDate = Timetable.toStableDate(endDate);
	}

}
