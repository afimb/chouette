package mobi.chouette.model;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.model.type.ConnectionLinkTypeEnum;
import mobi.chouette.model.type.LinkOrientationEnum;
import mobi.chouette.model.type.UserNeedEnum;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * Chouette AccessLink : relation between an AccessPoint and a StopArea
 * <p/>
 * Neptune mapping : PTAccessLink <br/>
 * Gtfs mapping : none <br/>
 * 
 */
@Entity
@Table(name = "access_links")
@NoArgsConstructor
@ToString
public class AccessLink extends NeptuneIdentifiedObject {

	private static final long serialVersionUID = 7835556134861322471L;

	
	@Getter
	@Setter
	@GenericGenerator(name = "access_links_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", 
		parameters = {
			@Parameter(name = "sequence_name", value = "access_links_id_seq"),
			@Parameter(name = "increment_size", value = "100") })
	@GeneratedValue(generator = "access_links_id_seq")
	@Id
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
	 * link length in meters
	 * 
	 * @param linkDistance New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "link_distance")
	private BigDecimal linkDistance;

	/**
	 * lift indicator <br/>
	 * 
	 * <ul>
	 * <li>true if a lift is available on this link</li>
	 * <li>false if no lift is available on this link</li>
	 * </ul>
	 * 
	 * @param liftAvailable New state for lift indicator
	 * @return The actual lift indicator
	 */
	@Getter
	@Setter
	@Column(name = "lift_availability")
	private Boolean liftAvailable = false;

	/**
	 * mobility restriction indicator (such as wheel chairs) <br/>
	 * 
	 * <ul>
	 * <li>true if wheel chairs can follow this link</li>
	 * <li>false if wheel chairs can't follow this link</li>
	 * </ul>
	 * 
	 * @param mobilityRestrictedSuitable New state for mobility restriction indicator
	 * @return The actual mobility restriction indicator
	 */
	@Getter
	@Setter
	@Column(name = "mobility_restricted_suitability")
	private Boolean mobilityRestrictedSuitable = false;

	/**
	 * stairs indicator <br/>
	 * 
	 * <ul>
	 * <li>true if a stairs are presents on this link</li>
	 * <li>false if no stairs are presents on this link</li>
	 * </ul>
	 * 
	 * @param stairsAvailable New state for stairs indicator
	 * @return The actual stairs indicator
	 */
	@Getter
	@Setter
	@Column(name = "stairs_availability")
	private Boolean stairsAvailable = false;

	/**
	 * medium time to follow the link <br/>
	 * null if unknown
	 * 
	 * @param defaultDuration New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "default_duration")
	private Time defaultDuration;

	/**
	 * time to follow the link for a frequent traveller <br/>
	 * null if unknown
	 * 
	 * @param frequentTravellerDuration New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "frequent_traveller_duration")
	private Time frequentTravellerDuration;

	/**
	 * time to follow the link for an occasional traveller <br/>
	 * null if unknown
	 * 
	 * @param occasionalTravellerDuration
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "occasional_traveller_duration")
	private Time occasionalTravellerDuration;

	/**
	 * time to follow the link for a traveller with mobility restriction <br/>
	 * null if unknown
	 * 
	 * @param mobilityRestrictedTravellerDuration New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "mobility_restricted_traveller_duration")
	private Time mobilityRestrictedTravellerDuration;

	/**
	 * link type
	 * 
	 * @param linkType New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "link_type")
	private ConnectionLinkTypeEnum linkType;

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
	 * @param intUserNeeds New value
	 * @return The actual value
	 */

	@Getter
	@Setter
	@Column(name = "int_user_needs")
	private Integer intUserNeeds = 0;

	/**
	 * return UserNeeds as Enum list
	 * 
	 * @return UserNeeds
	 */
	public List<UserNeedEnum> getUserNeeds() {
		List<UserNeedEnum> result = new ArrayList<UserNeedEnum>();
		if (intUserNeeds == null) return result;
		for (UserNeedEnum userNeed : UserNeedEnum.values()) {
			int mask = 1 << userNeed.ordinal();
			if ((this.intUserNeeds & mask) == mask) {
				result.add(userNeed);
			}
		}
		return result;
	}

	/**
	 * update UserNeeds as Enum list
	 * 
	 * @param userNeeds
	 */
	public void setUserNeeds(List<UserNeedEnum> userNeeds) {
		int value = 0;
		for (UserNeedEnum userNeed : userNeeds) {
			int mask = 1 << userNeed.ordinal();
			value |= mask;
		}
		this.intUserNeeds = value;
	}

	/**
	 * access link orientation
	 * 
	 * @param linkOrientation
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "link_orientation")
	private LinkOrientationEnum linkOrientation;

	/**
	 * access point connected to link
	 * 
	 * @return The actual value
	 */
	@Getter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "access_point_id")
	private AccessPoint accessPoint;

	/**
	 * set access point connected to link
	 * 
	 * @param accessPoint New value
	 */
	public void setAccessPoint(AccessPoint accessPoint) {
		if (this.accessPoint != null) {
			this.accessPoint.getAccessLinks().remove(this);
		}
		this.accessPoint = accessPoint;
		if (accessPoint != null) {
			accessPoint.getAccessLinks().add(this);
		}
	}

	/**
	 * stop area connected to link
	 * 
	 * @return The actual value
	 */
	@Getter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stop_area_id")
	private StopArea stopArea;

	/**
	 * set stop area connected to link
	 * 
	 * @param stopArea New value
	 */
	public void setStopArea(StopArea stopArea) {
		if (this.stopArea != null) {
			this.stopArea.getAccessLinks().remove(this);
		}
		this.stopArea = stopArea;
		if (stopArea != null) {
			stopArea.getAccessLinks().add(this);
		}
	}

}
