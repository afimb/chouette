package mobi.chouette.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
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
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.UserNeedEnum;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * Chouette StopArea
 * <p/>
 * StopArea may be on 5 areaTypes :
 * <ul>
 * <li>BOARDINGPOSITION for physical stops on roads</li>
 * <li>QUAY for physical stops on rails</li>
 * <li>COMMERCIALSTOPPOINT to group physical stops</li>
 * <li>STOPPLACE to group commercials stops and stop places in bigger ones</li>
 * <li>ITL to group any other type for routing constraint purpose</li>
 * </ul>
 * theses objects have internal dependency rules :
 * <ol>
 * <li>only boarding positions and quays can have {@link StopPoint} children</li>
 * <li>boarding positions and quays cannot have {@link StopArea} children</li>
 * <li>commercial stop points can have only boarding position and quay children</li>
 * <li>stop places can have only stop place and commercial stop point children</li>
 * <li>routing constraint stops can't have routing constraint stops children</li>
 * </ol>
 * <p/>
 * Neptune mapping : ChouetteStopArea, AreaCentroid <br/>
 * Gtfs mapping : Stop (only for BoardingPosition, Quay and CommercialStopPoint
 * types) <br/>
 */

/**
 * @author dsuru
 * 
 */
@Entity
@Table(name = "stop_areas")
@Cacheable
@NoArgsConstructor
@ToString(callSuper = true, exclude = { "accessLinks", "accessPoints",
		"connectionEndLinks", "connectionStartLinks", "containedStopAreas",
		"containedStopPoints", "routingConstraintAreas", "routingConstraintLines" })
public class StopArea extends NeptuneLocalizedObject {
	private static final long serialVersionUID = 4548672479038099240L;

	@Getter
	@Setter
	@GenericGenerator(name = "stop_areas_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", 
		parameters = {
			@Parameter(name = "sequence_name", value = "stop_areas_id_seq"),
			@Parameter(name = "increment_size", value = "100") })
	@GeneratedValue(generator = "stop_areas_id_seq")
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
	 * area type
	 * 
	 * @param areaType
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "area_type", nullable = false)
	private ChouetteAreaEnum areaType;

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
	 * nearest topic name
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "nearest_topic_name")
	private String nearestTopicName;

	/**
	 * set nearest topic name <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setNearestTopicName(String value) {
		nearestTopicName = StringUtils.abbreviate(value, 255);

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
	 * timezone
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "time_zone")
	private String timeZone;

	/**
	 * set timezone <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setTimeZone(String value) {
		timeZone = StringUtils.abbreviate(value, 255);
	}

	/**
	 * fare code
	 * 
	 * @param fareCode
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "fare_code")
	private Integer fareCode;

	/**
	 * lift indicator <br/>
	 * 
	 * <ul>
	 * <li>true if a lift is available at this stop</li>
	 * <li>false if no lift is available at this stop</li>
	 * </ul>
	 * 
	 * @param liftAvailable
	 *            New state for lift indicator
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
	 * <li>true if wheel chairs can access this stop</li>
	 * <li>false if wheel chairs can't access this stop</li>
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
	 * stairs indicator <br/>
	 * 
	 * <ul>
	 * <li>true if a stairs are presents at this stop</li>
	 * <li>false if no stairs are presents at this stop</li>
	 * </ul>
	 * 
	 * @param stairsAvailable
	 *            New state for stairs indicator
	 * @return The actual stairs indicator
	 */
	@Getter
	@Setter
	@Column(name = "stairs_availability")
	private Boolean stairsAvailable = false;

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
	 * stop area parent<br/>
	 * unavailable for areaType = ITL
	 * 
	 * @return The actual value
	 */
	@Getter
	@ManyToOne(fetch = FetchType.LAZY ,cascade = { CascadeType.PERSIST})
	@JoinColumn(name = "parent_id")
	private StopArea parent;

	/**
	 * set stop area parent
	 * 
	 * @param stopArea
	 */
	public void setParent(StopArea stopArea) {
		if (this.parent != null) {
			this.parent.getContainedStopAreas().remove(this);
		}
		this.parent = stopArea;
		if (stopArea != null) {
			stopArea.getContainedStopAreas().add(this);
		}
	}

	/**
	 * lines concerned by routing constraints <br/>
	 * only for areaType = ITL
	 * 
	 * @param routingConstraintLines
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToMany
	@JoinTable(name = "routing_constraints_lines", joinColumns = { @JoinColumn(name = "stop_area_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "line_id", nullable = false, updatable = false) })
	private List<Line> routingConstraintLines = new ArrayList<Line>(0);

	/**
	 * stops grouped in a routing constraints <br/>
	 * only for areaType = ITL<br/>
	 * stops in this list can't be of ITL type
	 * 
	 * @param routingConstraintAreas
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToMany(cascade = { CascadeType.PERSIST})
	@JoinTable(name = "stop_areas_stop_areas", joinColumns = { @JoinColumn(name = "parent_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "child_id", nullable = false, updatable = false) })
	private List<StopArea> routingConstraintAreas = new ArrayList<StopArea>(0);

	/**
	 * stop area children<br/>
	 * unavailable for areaType = ITL, BoardingPosition and Quay
	 * 
	 * @param containedStopAreas
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@OneToMany(mappedBy = "parent",cascade = { CascadeType.PERSIST })
	private List<StopArea> containedStopAreas = new ArrayList<StopArea>(0);

	/**
	 * stop points children<br/>
	 * only for areaType = BoardingPosition and Quay
	 * 
	 * @param containedStopPoints
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@OneToMany(mappedBy = "containedInStopArea")
	private List<StopPoint> containedStopPoints = new ArrayList<StopPoint>(0);

	/**
	 * access links<br/>
	 * only for areaType != ITL
	 * 
	 * @param accessLinks
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@OneToMany(cascade = { CascadeType.PERSIST })
	@JoinColumn(name = "stop_area_id", updatable = false)
	private List<AccessLink> accessLinks = new ArrayList<AccessLink>(0);

	/**
	 * routeSections where this stop is at start position<br/>
	 * only for areaType == BoardingPosition or Quay
	 * 
	 * @param routeSectionDepartures
	 *            New value
	 * @return The actual value
	 * @since 3.2.0
	 */
	@Getter
	@Setter
	@OneToMany(cascade = { CascadeType.PERSIST })
	@JoinColumn(name = "departure_id") //, updatable = false)
	private List<RouteSection> routeSectionDepartures = new ArrayList<RouteSection>(
			0);

	/**
	 * routeSections where this stop is at end position<br/>
	 * only for areaType == BoardingPosition or Quay
	 * 
	 * @param routeSectionArrivals
	 *            New value
	 * @return The actual value
	 * @since 3.2.0
	 */
	@Getter
	@Setter
	@OneToMany(cascade = { CascadeType.PERSIST })
	@JoinColumn(name = "arrival_id") //, updatable = false)
	private List<RouteSection> routeSectionArrivals = new ArrayList<RouteSection>(
			0);

	/**
	 * connection links where this stop is at start position<br/>
	 * only for areaType != ITL
	 * 
	 * @param connectionStartLinks
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@OneToMany(cascade = { CascadeType.PERSIST })
	@JoinColumn(name = "departure_id") //, updatable = false)
	private List<ConnectionLink> connectionStartLinks = new ArrayList<ConnectionLink>(
			0);

	/**
	 * connection links where this stop is at end position<br/>
	 * only for areaType != ITL
	 * 
	 * @param connectionEndLinks
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@OneToMany(cascade = { CascadeType.PERSIST })
	@JoinColumn(name = "arrival_id") //, updatable = false)
	private List<ConnectionLink> connectionEndLinks = new ArrayList<ConnectionLink>(
			0);

	/**
	 * access points<br/>
	 * only for areaType != ITL
	 * 
	 * @param accessPoints
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@OneToMany(mappedBy = "containedIn", cascade = { CascadeType.PERSIST })
	private List<AccessPoint> accessPoints = new ArrayList<AccessPoint>(0);

	// /**
	// * add a child StopArea if not already present
	// *
	// * @param containedStopArea
	// */
	// public void addContainedStopArea(StopArea containedStopArea) {
	// if (containedStopAreas == null)
	// containedStopAreas = new ArrayList<StopArea>();
	// if (areaType.equals(ChouetteAreaEnum.BoardingPosition)
	// || areaType.equals(ChouetteAreaEnum.Quay)) {
	// // boarding positions or quays can't contains stop areas
	// throw new CoreRuntimeException(CoreExceptionCode.UNVALID_TYPE,
	// areaType.toString(), STOPAREA_KEY, "containedStopAreas");
	// }
	// if (areaType.equals(ChouetteAreaEnum.CommercialStopPoint)) {
	// // commercial stops can contains only boarding positions or quays
	// if (!containedStopArea.getAreaType().equals(
	// ChouetteAreaEnum.BoardingPosition)
	// && !containedStopArea.getAreaType().equals(
	// ChouetteAreaEnum.Quay)) {
	// throw new CoreRuntimeException(CoreExceptionCode.UNVALID_TYPE,
	// areaType.toString(), containedStopArea.getAreaType()
	// .toString(), "containedStopAreas");
	// }
	// } else if (areaType.equals(ChouetteAreaEnum.StopPlace)) {
	// // stop places can contains only stop places or commercial stops
	// if (!containedStopArea.getAreaType().equals(
	// ChouetteAreaEnum.StopPlace)
	// && !containedStopArea.getAreaType().equals(
	// ChouetteAreaEnum.CommercialStopPoint)) {
	// throw new CoreRuntimeException(CoreExceptionCode.UNVALID_TYPE,
	// areaType.toString(), containedStopArea.getAreaType()
	// .toString(), "containedStopAreas");
	// }
	// } else if (areaType.equals(ChouetteAreaEnum.ITL)) {
	// // restriction constraints can't contains restriction constraints
	// if (containedStopArea.getAreaType().equals(ChouetteAreaEnum.ITL)) {
	// throw new CoreRuntimeException(CoreExceptionCode.UNVALID_TYPE,
	// areaType.toString(), containedStopArea.getAreaType()
	// .toString(), "containedStopAreas");
	// }
	// // ITL relationship are stored in routingConstraintAreas
	// if (!routingConstraintAreas.contains(containedStopArea))
	// routingConstraintAreas.add(containedStopArea);
	// return;
	// }
	// if (!containedStopAreas.contains(containedStopArea)) {
	// containedStopAreas.add(containedStopArea);
	// containedStopArea.setParent(this);
	// }
	// }

	// /**
	// * remove a child StopArea
	// *
	// * @param containedStopArea
	// */
	// public void removeContainedStopArea(StopArea containedStopArea) {
	// if (areaType.equals(ChouetteAreaEnum.ITL)) {
	// if (routingConstraintAreas == null)
	// routingConstraintAreas = new ArrayList<StopArea>();
	// if (routingConstraintAreas.contains(containedStopArea))
	// routingConstraintAreas.remove(containedStopArea);
	//
	// } else {
	// if (containedStopAreas == null)
	// containedStopAreas = new ArrayList<StopArea>();
	// if (containedStopAreas.contains(containedStopArea)) {
	// containedStopAreas.remove(containedStopArea);
	// containedStopArea.setParent(null);
	// }
	// }
	// }

	// /**
	// * add a child StopPoint if not already present
	// *
	// * @param containedStopPoint
	// */
	// public void addContainedStopPoint(StopPoint containedStopPoint) {
	// if (containedStopPoints == null)
	// containedStopPoints = new ArrayList<StopPoint>();
	// if (!areaType.equals(ChouetteAreaEnum.BoardingPosition)
	// && !areaType.equals(ChouetteAreaEnum.Quay)) {
	// // only boarding positions and quays can contains stop points
	// throw new CoreRuntimeException(CoreExceptionCode.UNVALID_TYPE,
	// areaType.toString(), STOPPOINT_KEY, "containedStopPoints");
	// }
	// if (containedStopPoint == null
	// || containedStopPoints.contains(containedStopPoint))
	// return;
	// containedStopPoints.add(containedStopPoint);
	// containedStopPoint.setContainedInStopArea(this);
	// }

	// /**
	// * remove a child StopPoint
	// *
	// * @param containedStopPoint
	// */
	// public void removeContainedStopPoint(StopPoint containedStopPoint) {
	// if (containedStopPoints == null)
	// containedStopPoints = new ArrayList<StopPoint>();
	// if (containedStopPoints.contains(containedStopPoint))
	// containedStopPoints.remove(containedStopPoint);
	// }

	// /**
	// * add an accessLink if not already present
	// * <p>
	// * WARNING : no check on accessLink stopAreaLink validity
	// *
	// * @param accessLink
	// */
	// public void addAccessLink(AccessLink accessLink) {
	// if (accessLinks == null)
	// accessLinks = new ArrayList<AccessLink>();
	// if (accessLink != null && !accessLinks.contains(accessLink)) {
	// accessLinks.add(accessLink);
	// accessLink.setStopArea(this);
	// }
	// }
	//
	// /**
	// * remove an accessLink
	// *
	// * @param accessLink
	// */
	// public void removeAccessLink(AccessLink accessLink) {
	// if (accessLinks == null)
	// accessLinks = new ArrayList<AccessLink>();
	// if (accessLinks.contains(accessLink))
	// accessLinks.remove(accessLink);
	// }
	//
	// /**
	// * add an accessPoint if not already present
	// * <p>
	// *
	// * @param accessPoint
	// */
	// public void addAccessPoint(AccessPoint accessPoint) {
	// if (accessPoints == null)
	// accessPoints = new ArrayList<AccessPoint>();
	// if (accessPoint != null && !accessPoints.contains(accessPoint)) {
	// accessPoints.add(accessPoint);
	// }
	// }
	//
	// /**
	// * remove an accessPoint
	// *
	// * @param accessPoint
	// */
	// public void removeAccessPoint(AccessPoint accessPoint) {
	// if (accessPoints == null)
	// accessPoints = new ArrayList<AccessPoint>();
	// if (accessPoints.contains(accessPoint))
	// accessPoints.remove(accessPoint);
	// }


	/**
	 * add a line if not already present
	 * <p>
	 * stop
	 * 
	 * @param area
	 */
	public void addRoutingConstraintStopArea(StopArea area) {
		if (!routingConstraintAreas.contains(area))
			routingConstraintAreas.add(area);
	}

	/**
	 * remove a line
	 * 
	 * @param area
	 */
	public void removeRoutingConstraintArea(StopArea area) {
		if (routingConstraintAreas.contains(area))
			routingConstraintAreas.remove(area);
	}

}
