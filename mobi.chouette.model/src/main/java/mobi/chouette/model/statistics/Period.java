package mobi.chouette.model.statistics;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;

@XmlRootElement(name = "period")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "from", "to" })

@Getter
public class Period implements Comparable<Period> {

	private Date from;
	private Date to;

	public Period(Date from, Date to) {
		super();
		this.from = from;
		this.to = to;
	}

	public boolean isEmpty() {
		return from == null || to == null;
	}
	
	@Override
	public int compareTo(Period o) {
		if (o == null) {
			return -1;
		}

		int f = from.compareTo(o.from);
		if (f == 0) {
			f = to.compareTo(o.to);
		}

		return f;
	}

	@Override
	public String toString() {
		return "Period [from=" + from + ", to=" + to + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Period other = (Period) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		return true;
	}

}