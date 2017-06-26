/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package mobi.chouette.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.model.type.OrganisationTypeEnum;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Chouette Company : a company providing public transport services.
 * <p/>
 * Neptune mapping : Company <br/>
 * Gtfs mapping : Agency <br/>
 */

@Entity
@Table(name = "companies")
@Cacheable
@NoArgsConstructor
@ToString(callSuper=true, exclude = { "lines" })
public class Company extends NeptuneIdentifiedObject {

	private static final long serialVersionUID = -8086291270595894778L;

	@Getter
	@Setter
	@GenericGenerator(name = "companies_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", 
		parameters = {
			@Parameter(name = "sequence_name", value = "companies_id_seq"),
			@Parameter(name = "increment_size", value = "10") })
	@GeneratedValue(generator = "companies_id_seq")
	@Id
	@Column(name = "id", nullable = false)
	protected Long id;

	/**
	 * Organisation type
	 *
	 * @param organisationType
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "organisation_type")
	private OrganisationTypeEnum organisationType;

	/**
	 * name
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "name")
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
	 * short name
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "short_name")
	private String shortName;

	/**
	 * set short name <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setShortName(String value) {
		shortName = StringUtils.abbreviate(value, 255);
	}

	/**
	 * legal name
	 *
	 * @return The actual value
	 */
	@Getter
	@Column(name = "legal_name")
	private String legalName;

	/**
	 * set legal name <br/>
	 * truncated to 255 characters if too long
	 *
	 * @param value
	 *            New value
	 */
	public void setLegalName(String value) {
		legalName = StringUtils.abbreviate(value, 255);
	}

	/**
	 * organizational unit
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "organizational_unit")
	private String organisationalUnit;

	/**
	 * set organizational unit <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setOrganisationalUnit(String value) {
		organisationalUnit = StringUtils.abbreviate(value, 255);

	}

	/**
	 * operating department name
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "operating_department_name")
	private String operatingDepartmentName;

	/**
	 * set operating department name <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setOperatingDepartmentName(String value) {
		operatingDepartmentName = StringUtils.abbreviate(value, 255);

	}

	/**
	 * organization code <br/>
	 * usually fixed by Transport Authority
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "code")
	private String code;

	/**
	 * set organization code <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setCode(String value) {
		code = StringUtils.abbreviate(value, 255);
	}

	/**
	 * phone number
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "phone")
	private String phone;

	/**
	 * set phone number <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setPhone(String value) {
		phone = StringUtils.abbreviate(value, 255);
	}

	/**
	 * fax number
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "fax")
	private String fax;

	/**
	 * set fax number <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setFax(String value) {
		fax = StringUtils.abbreviate(value, 255);
	}

	/**
	 * email
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "email")
	private String email;

	/**
	 * set email <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setEmail(String value) {
		email = StringUtils.abbreviate(value, 255);
	}

	/**
	 * public phone
	 *
	 * @return The actual value
	 */
	@Getter
	@Column(name = "public_phone")
	private String publicPhone;

	/**
	 * set public phone <br/>
	 * truncated to 255 characters if too long
	 *
	 * @param value
	 *            New value
	 */
	public void setPublicPhone(String value) {
		publicPhone = StringUtils.abbreviate(value, 255);
	}

	/**
	 * public email
	 *
	 * @return The actual value
	 */
	@Getter
	@Column(name = "public_email")
	private String publicEmail;

	/**
	 * set public email <br/>
	 * truncated to 255 characters if too long
	 *
	 * @param value
	 *            New value
	 */
	public void setPublicEmail(String value) {
		publicEmail = StringUtils.abbreviate(value, 255);
	}

	/**
	 * public url
	 *
	 * @return The actual value
	 */
	@Getter
	@Column(name = "public_url")
	private String publicUrl;

	/**
	 * set public url <br/>
	 * truncated to 255 characters if too long
	 *
	 * @param value
	 *            New value
	 */
	public void setPublicUrl(String value) {
		publicUrl = StringUtils.abbreviate(value, 255);
	}

	/**
	 * registration number
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "registration_number", unique = true)
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
	 * web site url
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "url")
	private String url;

	/**
	 * set web site url <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setUrl(String value) {
		url = StringUtils.abbreviate(value, 255);
	}

	/**
	 * default timezone
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "time_zone")
	private String timeZone;

	/**
	 * set default timezone <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setTimeZone(String value) {
		timeZone = StringUtils.abbreviate(value, 255);
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
	@OneToMany(mappedBy = "company")
	private List<Line> lines = new ArrayList<Line>(0);

}
