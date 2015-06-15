/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package mobi.chouette.model;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * basic class for every Chouette object
 */
@MappedSuperclass
@Access(AccessType.FIELD)
@ToString
public abstract class NeptuneObject implements Serializable {

	private static final long serialVersionUID = -1406542019260386319L;

	@Getter
	@Setter
	@Transient
	private boolean detached = false;

	/**
	 * database id <br/>
	 * null if not saved
	 * 
	 * @return The actual value
	 */
	public abstract Long getId();
	
	/**
	 * database id <br/>
	 * null if not saved
	 * 
	 * @param id
	 *            New value
	 */
	public abstract void setId(Long id);


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (getId() == null)
			return 0;
		return getId().hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NeptuneObject other = (NeptuneObject) obj;
		if (getId() == null)
			return (other.getId() == null);
		if (getId().equals(other.getId()))
			return false;
		return true;
	}

}
