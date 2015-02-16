/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package mobi.chouette.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;
import mobi.chouette.core.CoreExceptionCode;
import mobi.chouette.core.CoreRuntimeException;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.type.UserNeedEnum;
import mobi.chouette.model.util.ObjectIdTypes;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * Chouette Line : a group of Routes which is generally known to the public by a
 * similar name or number
 * <p/>
 * Neptune mapping : Line <br/>
 * Gtfs mapping : Line <br/>
 */
@Entity
@Table(name = "lines")
@NoArgsConstructor
@Log4j
@ToString(callSuper = true, exclude = { "routingConstraints" })
public class Line extends NeptuneIdentifiedObject implements ObjectIdTypes {
	private static final long serialVersionUID = -8086291270595894778L;

	@Getter
	@Setter
	@GenericGenerator(name = "lines_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", 
		parameters = {
			@Parameter(name = "sequence_name", value = "lines_id_seq"),
			@Parameter(name = "increment_size", value = "100") })
	@Id
	@GeneratedValue(generator = "lines_id_seq")
	@Column(name = "id", nullable = false)
	protected Long id;
	
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
	 * number or short name
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "number")
	private String number;

	/**
	 * set number <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setNumber(String value) {
		number = StringUtils.abbreviate(value, 255);
	}

	/**
	 * published name
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "published_name")
	private String publishedName;

	/**
	 * set published name <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setPublishedName(String value) {
		publishedName = StringUtils.abbreviate(value, 255);
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
	 * Transport mode
	 * 
	 * @param transportModeName
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "transport_mode_name")
	private TransportModeNameEnum transportModeName = TransportModeNameEnum.Other;

	/**
	 * mobility restriction indicator (such as wheel chairs) <br/>
	 * 
	 * <ul>
	 * <li>true if wheel chairs can use this line</li>
	 * <li>false if wheel chairs can't use this line</li>
	 * </ul>
	 * 
	 * @param mobilityRestrictedSuitable
	 *            New state for mobility restriction indicator
	 * @return The actual mobility restriction indicator
	 */
	@Getter
	@Setter
	@Column(name = "mobility_restricted_suitability")
	private Boolean mobilityRestrictedSuitable = false;

	/**
	 * coded user needs as binary map<br/>
	 * 
	 * use following methods for easier access :
	 * <ul>
	 * <li>getUserNeeds</li>
	 * <li>setUserNeeds</li>
	 * <li>addUserNeed</li>
	 * <li>addAllUserNeed</li>
	 * <li>removeUserNeed</li>
	 * </ul>
	 * 
	 * @param intUserNeeds
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "int_user_needs")
	private Integer intUserNeeds = 0;

	public List<UserNeedEnum> getUserNeeds() {
		List<UserNeedEnum> result = new ArrayList<UserNeedEnum>();
		for (UserNeedEnum userNeed : UserNeedEnum.values()) {
			int mask = 1 << userNeed.ordinal();
			if ((this.intUserNeeds & mask) == mask) {
				result.add(userNeed);
			}
		}
		return result;
	}

	public void setUserNeeds(List<UserNeedEnum> userNeeds) {
		int value = 0;
		for (UserNeedEnum userNeed : userNeeds) {
			int mask = 1 << userNeed.ordinal();
			value |= mask;
		}
		this.intUserNeeds = value;
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
	 * line drawing color <br/>
	 * should be used also on label background
	 * 
	 * @return The actual value in RRGGBB hexadecimal format
	 */
	@Getter
	@Column(name = "color", length = 6)
	private String color;

	/**
	 * set line drawing color <br/>
	 * truncated to 6 characters if too long
	 * 
	 * @param value
	 *            New value in RRGGBB hexadecimal format
	 */
	public void setColor(String value) {
		color = StringUtils.abbreviate(value, 6);
	}

	/**
	 * line text color
	 * 
	 * @return The actual value in RRGGBB hexadecimal format
	 */
	@Getter
	@Column(name = "text_color", length = 6)
	private String textColor;

	/**
	 * set line text color <br/>
	 * truncated to 6 characters if too long
	 * 
	 * @param value
	 *            New value in RRGGBB hexadecimal format
	 */
	public void setTextColor(String value) {
		textColor = StringUtils.abbreviate(value, 6);
	}

	/**
	 * network reference
	 * 
	 * @param ptNetwork
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@ManyToOne(cascade = { CascadeType.PERSIST })
	@JoinColumn(name = "network_id")
	private PTNetwork ptNetwork;

	/**
	 * set network
	 * 
	 * @param ptNetwork
	 */
	public void setPTNetwork(PTNetwork ptNetwork) {
		if (this.ptNetwork != null) {
			this.ptNetwork.getLines().remove(this);
		}
		this.ptNetwork = ptNetwork;
		if (ptNetwork != null) {
			ptNetwork.getLines().add(this);
		}
	}

	/**
	 * company reference
	 * 
	 * @param company
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToOne(cascade = { CascadeType.PERSIST })
	@JoinColumn(name = "company_id")
	private Company company;

	/**
	 * list of routes
	 * 
	 * @param routes
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@OneToMany(mappedBy = "line", cascade = { CascadeType.PERSIST })
	private List<Route> routes = new ArrayList<Route>(0);

	/**
	 * groups of lines reverse reference
	 * 
	 * @param groupOfLines
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToMany(cascade = { CascadeType.PERSIST })
	@JoinTable(name = "group_of_lines_lines", joinColumns = { @JoinColumn(name = "line_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "group_of_line_id", nullable = false, updatable = false) })
	private List<GroupOfLine> groupOfLines = new ArrayList<GroupOfLine>(0);

	/**
	 * routing constraints associations
	 * 
	 * @param routingConstraints
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToMany(mappedBy = "routingConstraintLines")
	private List<StopArea> routingConstraints = new ArrayList<StopArea>(0);

	// /**
	// * add a group of line
	// *
	// * @param groupOfLine
	// */
	// public void addGroupOfLine(GroupOfLine groupOfLine) {
	// if (groupOfLines == null)
	// groupOfLines = new ArrayList<GroupOfLine>();
	// if (!groupOfLines.contains(groupOfLine))
	// groupOfLines.add(groupOfLine);
	// }
	//
	// /**
	// * remove a group of line
	// *
	// * @param groupOfLine
	// */
	// public void removeGroupOfLine(GroupOfLine groupOfLine) {
	// if (groupOfLines == null)
	// groupOfLines = new ArrayList<GroupOfLine>();
	// if (groupOfLines.contains(groupOfLine))
	// groupOfLines.remove(groupOfLine);
	// }

	/* -------------------------------------- */

	/**
	 * add a routing constraint
	 * 
	 * @param routingConstraint
	 */
	public void addRoutingConstraint(StopArea routingConstraint) {
		if (routingConstraints == null)
			routingConstraints = new ArrayList<StopArea>();
		if (!routingConstraint.getAreaType().equals(ChouetteAreaEnum.ITL)) {
			// routingConstraint must be of ITL type
			throw new CoreRuntimeException(CoreExceptionCode.UNVALID_TYPE,
					routingConstraint.getAreaType().toString(), STOPAREA_KEY,
					"routingConstraints");
		}
		if (routingConstraint != null
				&& !routingConstraints.contains(routingConstraint))
			routingConstraints.add(routingConstraint);

	}

	/**
	 * remove a routing constraint
	 * 
	 * @param routingConstraint
	 */
	public void removeRoutingConstraint(StopArea routingConstraint) {
		if (routingConstraints == null)
			routingConstraints = new ArrayList<StopArea>();
		if (routingConstraint != null
				&& routingConstraints.contains(routingConstraint))
			routingConstraints.remove(routingConstraint);

	}

}
