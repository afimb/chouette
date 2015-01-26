/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package mobi.chouette.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;
import mobi.chouette.model.type.PTNetworkSourceTypeEnum;

import org.apache.commons.lang.StringUtils;

/**
 * Chouette Public Transport Network : a set of lines
 * <p/>
 * Neptune mapping : PTNetwork <br/>
 * Gtfs mapping : none
 */
@Entity
@Table(name = "networks")
@Cacheable
@NoArgsConstructor
@ToString
@Log4j
public class PTNetwork extends NeptuneIdentifiedObject {

	private static final long serialVersionUID = -8986371268064619423L;

	/**
	 * name
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "name", nullable = false)
	private String name;

	/**
	 * set name <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setName(String value) {
		name = StringUtils.abbreviate(value, 255);
	}

	/**
	 * comment
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
	 * version date
	 * 
	 * @param versionDate
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "version_date")
	private Date versionDate;

	/**
	 * description
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "description")
	private String description;

	/**
	 * set description <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setDescription(String value) {
		description = StringUtils.abbreviate(value, 255);
	}

	/**
	 * registration number
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "registration_number")
	private String registrationNumber;

	/**
	 * set registration number <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setRegistrationNumber(String value) {
		registrationNumber = StringUtils.abbreviate(value, 255);

	}

	/**
	 * source type
	 * 
	 * @param sourceType
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "source_type")
	private PTNetworkSourceTypeEnum sourceType;

	/**
	 * source name
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "source_name")
	private String sourceName;

	/**
	 * set source name <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setSourceName(String value) {
		sourceName = StringUtils.abbreviate(value, 255);
	}

	/**
	 * source identifier
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "source_identifier")
	private String sourceIdentifier;

	/**
	 * set source identifier <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setSourceIdentifier(String value) {
		sourceIdentifier = StringUtils.abbreviate(value, 255);

	}

	/**
	 * lines
	 * 
	 * @param lines
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@OneToMany(mappedBy = "ptNetwork")
	private List<Line> lines = new ArrayList<Line>(0);

}
