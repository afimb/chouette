package mobi.chouette.model;

import java.math.BigDecimal;
import java.sql.Time;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;
import mobi.chouette.model.type.ConnectionLinkTypeEnum;

/**
 * Chouette ConnectionLink : relation between 2 StopAreas
 * <p/>
 * Neptune mapping : ConnectionLink <br/>
 * Gtfs mapping : transfer
 * 
 */

@Entity
@Table(name = "connection_links")
@NoArgsConstructor
@ToString
@Log4j
public class ConnectionLink extends NeptuneIdentifiedObject {
	private static final long serialVersionUID = 8490105295077539089L;

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
		name = dataBaseSizeProtectedValue(value, "name", log);
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
		comment = dataBaseSizeProtectedValue(value, "comment", log);
	}

	/**
	 * link length in meters
	 * 
	 * @param linkDistance
	 *            New value
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
	 * @param liftAvailable
	 *            New state for lift indicator
	 * @return The actual lift indicator
	 */
	@Getter
	@Setter
	@Column(name = "lift_availability")
	private boolean liftAvailable = false;

	/**
	 * mobility restriction indicator (such as wheel chairs) <br/>
	 * 
	 * <ul>
	 * <li>true if wheel chairs can follow this link</li>
	 * <li>false if wheel chairs can't follow this link</li>
	 * </ul>
	 * 
	 * @param mobilityRestrictedSuitable
	 *            New state for mobility restriction indicator
	 * @return The actual mobility restriction indicator
	 */
	@Getter
	@Setter
	@Column(name = "mobility_restricted_suitability")
	private boolean mobilityRestrictedSuitable = false;

	/**
	 * stairs indicator <br/>
	 * 
	 * <ul>
	 * <li>true if a stairs are presents on this link</li>
	 * <li>false if no stairs are presents on this link</li>
	 * </ul>
	 * 
	 * @param stairsAvailable
	 *            New state for stairs indicator
	 * @return The actual stairs indicator
	 */
	@Getter
	@Setter
	@Column(name = "stairs_availability")
	private boolean stairsAvailable = false;

	/**
	 * medium time to follow the link <br/>
	 * null if unknown
	 * 
	 * @param defaultDuration
	 *            New value
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
	 * @param frequentTravellerDuration
	 *            New value
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
	 * @param mobilityRestrictedTravellerDuration
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "mobility_restricted_traveller_duration")
	private Time mobilityRestrictedTravellerDuration;

	/**
	 * link type
	 * 
	 * @param linkType
	 *            New value
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
	 * @param intUserNeeds
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "int_user_needs")
	private Integer intUserNeeds = 0;

	/**
	 * first stop area connected to link
	 * 
	 * @param startOfLink
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "departure_id")
	private StopArea startOfLink;

	/**
	 * set startOfLink
	 * 
	 * @param stopArea
	 */
	public void setStartOfLink(StopArea stopArea) {
		if (this.startOfLink != null) {
			this.startOfLink.getConnectionStartLinks().remove(this);
		}
		this.startOfLink = stopArea;
		if (stopArea != null) {
			stopArea.getConnectionStartLinks().add(this);
		}
	}

	/**
	 * last stop area connected to link
	 * 
	 * @param endOfLink
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "arrival_id")
	private StopArea endOfLink;

	/**
	 * set stopArea
	 * 
	 * @param stopArea
	 */
	public void setEndOfLink(StopArea stopArea) {
		if (this.endOfLink != null) {
			this.endOfLink.getConnectionEndLinks().remove(this);
		}
		this.endOfLink = stopArea;
		if (stopArea != null) {
			stopArea.getConnectionEndLinks().add(this);
		}
	}

}
