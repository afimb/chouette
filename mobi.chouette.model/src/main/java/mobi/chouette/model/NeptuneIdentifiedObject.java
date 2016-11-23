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

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.NaturalId;

/**
 * Abstract object used for all Identified Chouette Object
 * <p/>
 */
@SuppressWarnings("serial")
@MappedSuperclass
@EqualsAndHashCode(of = { "codeSpace", "technicalId" }, callSuper = false)
@ToString(callSuper = true)
public abstract class NeptuneIdentifiedObject extends NeptuneObject implements
		ObjectIdTypes {
	
	

	/**
	 * 	Id containing three fields from raw object id
	 */
	public ChouetteId getChouetteId() {
		return new ChouetteId(this.codeSpace, this.technicalId, this.shared);
	}
	
	public void setChouetteId(ChouetteId chouetteId) {
		this.codeSpace = chouetteId.getCodeSpace();
		this.technicalId = chouetteId.getTechnicalId();
		this.shared = chouetteId.isShared();
	}
	
	@NaturalId
	@Getter
	@Column(name = "codespace", nullable = false)
	private String codeSpace;

	public void setCodeSpace(String value) {
		codeSpace = StringUtils.abbreviate(value, 255);
	}
	
	@NaturalId
	@Getter
	@Column(name = "objectid", nullable = false)
	private String technicalId;

	public void setTechnicalId(String value) {
		technicalId = StringUtils.abbreviate(value, 255);
	}
	
	@Getter
	@Setter
	@Column(name = "shared", nullable = false)
	private boolean shared = false;
	
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
