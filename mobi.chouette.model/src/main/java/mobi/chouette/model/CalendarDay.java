package mobi.chouette.model;

import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * peculiar date for Timetables
 */
@Embeddable
@NoArgsConstructor
@ToString
public class CalendarDay implements Serializable, Comparable<CalendarDay> {

	private static final long serialVersionUID = -1964071056103739954L;

	/**
	 * date
	 * 
	 * @param date
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "date")
	private Date date;

	/**
	 * included or excluded date <br/>
	 * <ul>
	 * <li>true : if date is effective for this calendar (default value)</li>
	 * <li>false : if date is to be excluded from periods definitions</li>
	 * </ul>
	 * 
	 * @param included
	 *            New value
	 */
	@Setter
	@Column(name = "in_out")
	private Boolean included = Boolean.TRUE;

	/**
	 * included or excluded date <br/>
	 * <ul>
	 * <li>true : if date is effective for this calendar (default value)</li>
	 * <li>false : if date is to be excluded from periods definitions</li>
	 * </ul>
	 * 
	 * @return The actual value
	 */
	public Boolean getIncluded() {
		// protection from missing migration
		if (included == null)
			included = Boolean.TRUE;
		return included;
	}

	/**
	 * complete constructor
	 * 
	 * @param date
	 *            date
	 * @param included
	 *            indicate if date is included or excluded on the timetable
	 */
	public CalendarDay(Date date, boolean included) {
		this.date = date;
		this.included = Boolean.valueOf(included);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(CalendarDay o) {
		return getDate().compareTo(o.getDate());
	}
	
	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
	@Override
	public String toString()
	{
		return (included? "I":"E")+format.format(date);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		return toString().equals(obj.toString());
	}
	
	

}
