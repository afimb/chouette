/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package mobi.chouette.model;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.model.util.ObjectIdTypes;

import org.hibernate.annotations.NaturalId;

/**
 * Abstract object used for all Identified Chouette Object
 * <p/>
 */
@SuppressWarnings("serial")
@MappedSuperclass
@EqualsAndHashCode(of = { "chouetteId" }, callSuper = false)
@ToString(callSuper = true)
public abstract class NeptuneIdentifiedObject extends NeptuneObject implements
		ObjectIdTypes {

	/**
	 * 	Embedded id containing three fields from raw object id
	 */
	
	@Embedded
	@Getter
	@Setter
    private ChouetteId chouetteId;

	/**
	 * object version
	 * 
	 * @param objectVersion
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "object_version")
	protected Integer objectVersion = 1;

	/**
	 * creation time
	 * 
	 * @param creationTime
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "creation_time")
	protected Date creationTime = new Date();

	/**
	 * creator id
	 * 
	 * @param creatorId
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "creator_id")
	protected String creatorId;

	@Getter
	@Setter
	@Transient
	private boolean saved = false;

	@Getter
	@Setter
	@Transient
	private boolean isFilled = false;

	/**
	 * to be overrided; facility to check registration number on any object
	 * 
	 * @return null : when object has no registration number
	 */
	public String getRegistrationNumber() {
		return null;
	}

}
