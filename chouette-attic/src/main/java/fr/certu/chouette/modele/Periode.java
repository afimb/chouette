package fr.certu.chouette.modele;

import java.util.Date;

import chouette.schema.Period;

public class Periode {
	
	public Date debut;
	public Date fin;

	public Periode() {
		debut = new Date();
		fin = new Date();
	}

	public Date getDebut() {
		return debut;
	}

	public void setDebut(Date debut) {
		this.debut = debut;
	}

	public Date getFin() {
		return fin;
	}

	public void setFin(Date fin) {
		this.fin = fin;
	}
	
	public Period getPeriod()
	{
		Period period = new Period();
		if ( debut==null)
			period.setStartOfPeriod( null);
		else
			period.setStartOfPeriod( new org.exolab.castor.types.Date( debut));
			
		if ( fin==null)
			period.setEndOfPeriod( null);
		else
			period.setEndOfPeriod( new org.exolab.castor.types.Date( fin));
		
		return period;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((debut == null) ? 0 : debut.hashCode());
		result = PRIME * result + ((fin == null) ? 0 : fin.hashCode());
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
		final Periode other = (Periode) obj;
		if (debut == null) {
			if (other.debut != null)
				return false;
		} else if (!debut.equals(other.debut))
			return false;
		if (fin == null) {
			if (other.fin != null)
				return false;
		} else if (!fin.equals(other.fin))
			return false;
		return true;
	}
	
}
