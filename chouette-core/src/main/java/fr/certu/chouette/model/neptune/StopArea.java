package fr.certu.chouette.model.neptune;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.core.CoreExceptionCode;
import fr.certu.chouette.core.CoreRuntimeException;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;

/**
 * Neptune StopArea 
 * <p/>
 * StopArea may be on 5 areaTypes :
 * <ul>
 * <li>BOARDINGPOSITION for physical stops on roads</li>
 * <li>QUAY for physical stops on rails</li>
 * <li>COMMERCIALSTOPPOINT to group physical stops </li>
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
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 */
public class StopArea extends NeptuneIdentifiedObject
{
	private static final long    serialVersionUID            = 4548672479038099240L;
	// constant for persistence fields
	/**
	 * name of comment attribute for {@link Filter} attributeName construction
	 */
	public static final String   COMMENT                     = "comment";
	/**
	 * name of areaType attribute for {@link Filter} attributeName construction
	 */
	public static final String   AREA_TYPE                   = "areaType";
	/**
	 * name of registrationNumber attribute for {@link Filter} attributeName
	 * construction
	 */
	public static final String   REGISTRATION_NUMBER         = "registrationNumber";
	/**
	 * name of nearestTopicName attribute for {@link Filter} attributeName
	 * construction
	 */
	public static final String   NEAREST_TOPIC_NAME          = "nearestTopicName";
	/**
	 * name of fareCode attribute for {@link Filter} attributeName construction
	 */
	public static final String   FARECODE                    = "fareCode";
	/**
	 * name of areaCentroid attribute for {@link Filter} attributeName
	 * construction <br/>
	 * to be combined with {@Link AreaCentroid} constants
	 */
	public static final String   AREACENTROID                = "areaCentroid";
	/**
	 * name of parentStopArea attribute for {@link Filter} attributeName
	 * construction
	 */
	public static final String   PARENTSTOPAREA              = "parent";
	/**
	 * name of containedStopAreas attribute for {@link Filter} attributeName
	 * construction
	 */
	public static final String   CONTAINEDSTOPAREAS          = "containedStopAreas";
	/**
	 * name of containedStopPoints attribute for {@link Filter} attributeName
	 * construction
	 */
	public static final String   CONTAINEDSTOPPOINTS         = "containedStopPoints";

	public static final String LONGITUDE ="longitude"; 
	public static final String LATITUDE ="latitude"; 
	public static final String LONGLAT_TYPE="longLatType"; 
	public static final String COUNTRY_CODE="countryCode"; 
	public static final String STREET_NAME="streetName"; 
	public static final String X="x"; 
	public static final String Y="y"; 
	public static final String PROJECTION_TYPE="projectionType"; 

	/**
	 * predefined filter to limit get on StopPlaces
	 */
	public static final Filter   stopPlaceFilter             = Filter.getNewEqualsFilter(AREA_TYPE,
			ChouetteAreaEnum.STOPPLACE);
	/**
	 * predefined filter to limit get on CommercialStopPoints
	 */
	public static final Filter   commercialStopPointFilter   = Filter.getNewEqualsFilter(AREA_TYPE,
			ChouetteAreaEnum.COMMERCIALSTOPPOINT);
	/**
	 * predefined filter to limit get on RestrictionConstraints
	 */
	public static final Filter   restrictionConstraintFilter = Filter
			.getNewEqualsFilter(AREA_TYPE, ChouetteAreaEnum.ITL);
	/**
	 * predefined filter to limit get on BoardingPositions
	 */
	public static final Filter   boardingPositionFilter      = Filter.getNewEqualsFilter(AREA_TYPE,
			ChouetteAreaEnum.BOARDINGPOSITION);
	/**
	 * predefined filter to limit get on Quays
	 */
	public static final Filter   quayFilter                  = Filter.getNewEqualsFilter(AREA_TYPE,
			ChouetteAreaEnum.QUAY);
	/**
	 * predefined filter to limit get on PhysicalStops
	 */
	public static final Filter   physicalStopsFilter         = Filter.getNewInFilter(AREA_TYPE, new ChouetteAreaEnum[] {
			ChouetteAreaEnum.BOARDINGPOSITION, ChouetteAreaEnum.QUAY });

	/**
	 * AreaCentroid ObjectId for import/export purpose
	 * <br/><i>readable/writable</i>
	 */
	@Getter
	@Setter
	private String               areaCentroidId;
	/**
	 * AreaCentroid
	 */
	private AreaCentroid         areaCentroid;
	/**
	 * Comment
	 * <br/><i>readable/writable</i>
	 */
	@Getter
	@Setter
	private String               comment;
	/**
	 * List of Children objectIds for import/export purpose
	 * <p/>
	 * may content StopPoint or StopArea ids but not mixed
	 * <br/><i>readable/writable</i>
	 */
	@Getter
	@Setter
	private List<String>         containedStopIds;
	/**
	 * List of Children of StopArea type
	 * <br/><i>readable/writable</i>
	 */
	@Getter
	@Setter
	private List<StopArea>       containedStopAreas;
	/**
	 * List of Children of StopPoint type
	 * <br/><i>readable/writable</i>
	 */
	@Getter
	@Setter
	private List<StopPoint>      containedStopPoints;
	/**
	 * stopArea type
	 * <br/><i>readable/writable</i>
	 */
	@Getter
	@Setter
	private ChouetteAreaEnum     areaType;
	/**
	 * fare code
	 * <br/><i>readable/writable</i>
	 */
	@Getter
	@Setter
	private Integer              fareCode;
	/**
	 * lift availability
	 * <br/><i>readable/writable</i>
	 */
	@Getter
	@Setter
	private Boolean              liftAvailable;
	/**
	 * mobility restricted suitability
	 * <br/><i>readable/writable</i>
	 */
	@Getter
	@Setter
	private Boolean              mobilityRestrictedSuitable;
	/**
	 * stairs availability
	 * <br/><i>readable/writable</i>
	 */
	@Getter
	@Setter
	private Boolean              stairsAvailable;
	/**
	 * ????
	 * <br/><i>readable/writable</i>
	 */
	@Getter
	@Setter
	private String               nearestTopicName;
	/**
	 * registration number
	 * <br/><i>readable/writable</i>
	 */
	@Getter
	@Setter
	private String               registrationNumber;
	/**
	 * list of user needs
	 * <br/><i>readable/writable</i>
	 */
	@Getter
	@Setter
	private List<UserNeedEnum>   userNeeds;
	/**
	 * list of connection links
	 * <p>
	 * links to others StopAreas (may be same one)
	 * <br/><i>readable/writable</i>
	 */
	@Getter
	@Setter
	private List<ConnectionLink> connectionLinks;

	/**
	 * list of connection links where area is start (for hibernate purpose)
	 * <p>
	 * links to others StopAreas (may be same one) 
	 * <br/><i>readable/writable</i>
	 */
	@Getter
	@Setter
	private List<ConnectionLink> connectionStartLinks;

	/**
	 * list of connection links where area is end (for hibernate purpose)
	 * <p>
	 * links to others StopAreas (may be same one)
	 * <br/><i>readable/writable</i>
	 */
	@Getter
	@Setter
	private List<ConnectionLink> connectionEndLinks;
	/**
	 * list of access links
	 * <p>
	 * links to access points
	 * <br/><i>readable/writable</i>
	 */
	@Getter
	@Setter
	private List<AccessLink>     accessLinks;

	/**
	 * list of access points
	 * <p>
	 * access points contained in this stop area
	 * <br/><i>readable/writable</i>
	 */
	@Getter
	@Setter
	private List<AccessPoint>     accessPoints;
	
	/**
	 * list of facilities
	 * <br/><i>readable/writable</i>
	 */
	@Getter
	@Setter
	private List<Facility>       facilities;

	/**
	 * lines affected by this RoutingConstraint
	 * <p>
	 * only for {@link ChouetteAreaEnum}.ITL StopAreas
	 * <br/><i>readable/writable</i>
	 */
	@Getter
	@Setter
	private List<Line>           routingConstraintLines;

	/**
	 * line ids affected by this RoutingConstraint (for exchange purpose)
	 * <p>
	 * only for {@link ChouetteAreaEnum}.ITL StopAreas
	 * <br/><i>readable/writable</i>
	 */
	@Getter
	@Setter
	private List<String>           routingConstraintLineIds;

	/**
	 * stopareas affected by this RoutingConstraint
	 * <p>
	 * only for {@link ChouetteAreaEnum}.ITL StopAreas
	 * <br/><i>readable/writable</i>
	 */
	@Getter
	@Setter
	private List<StopArea>           routingConstraintAreas;


	/**
	 * non ITL parent StopArea
	 * 
	 */
	@Getter @Setter
	private StopArea             parent;

	/**
	 * non ITL parent StopArea ObjectID
	 * 
	 */
	@Getter @Setter
	private String             parentObjectId;

	/**
	 * Spatial Referential Type (actually only WGS84 is valid)  
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private LongLatTypeEnum longLatType;
	/**
	 * Latitude position of area 
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private BigDecimal latitude;
	/**
	 * Longitude position of area
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private BigDecimal longitude;
	/**
	 * address street name 
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private String streetName;
	/**
	 * address city or district code
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private String countryCode;

	/**
	 * x coordinate
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private BigDecimal x;
	/**
	 * y coordinate
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private BigDecimal y;
	/**
	 * projection system name (f.e. : epgs:27578)
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private String projectionType;

	/**
	 * add a facility if not already present
	 * 
	 * @param facility
	 */
	public void addFacility(Facility facility)
	{
		if (facilities == null)
			facilities = new ArrayList<Facility>();
		if (!facilities.contains(facility))
			facilities.add(facility);
	}

	/**
	 * remove a facility
	 * 
	 * @param facility
	 */
	public void removeFacility(Facility facility)
	{
		if (facilities == null)
			facilities = new ArrayList<Facility>();
		if (facilities.contains(facility))
			facilities.remove(facility);
	}

	/**
	 * add a child Stop objectId if not already present
	 * 
	 * @param containedStopId
	 */
	public void addContainedStopId(String containedStopId)
	{
		if (containedStopIds == null)
			containedStopIds = new ArrayList<String>();
		if (!containedStopIds.contains(containedStopId))
			containedStopIds.add(containedStopId);
	}

	/**
	 * remove a child Stop objectId
	 * 
	 * @param containedStopId
	 */
	public void removeContainedStopId(String containedStopId)
	{
		if (containedStopIds == null)
			containedStopIds = new ArrayList<String>();
		if (containedStopIds.contains(containedStopId))
			containedStopIds.remove(containedStopId);
	}


	/**
	 * add a child StopArea if not already present
	 * 
	 * @param containedStopArea
	 */
	public void addContainedStopArea(StopArea containedStopArea)
	{
		if (containedStopAreas == null)
			containedStopAreas = new ArrayList<StopArea>();
		if (areaType.equals(ChouetteAreaEnum.BOARDINGPOSITION) || areaType.equals(ChouetteAreaEnum.QUAY))
		{
			// boarding positions or quays can't contains stop areas
			throw new CoreRuntimeException(CoreExceptionCode.UNVALID_TYPE, areaType.toString(), STOPAREA_KEY,
					"containedStopAreas");
		}
		if (areaType.equals(ChouetteAreaEnum.COMMERCIALSTOPPOINT))
		{
			// commercial stops can contains only boarding positions or quays
			if (!containedStopArea.getAreaType().equals(ChouetteAreaEnum.BOARDINGPOSITION)
					&& !containedStopArea.getAreaType().equals(ChouetteAreaEnum.QUAY))
			{
				throw new CoreRuntimeException(CoreExceptionCode.UNVALID_TYPE, areaType.toString(), containedStopArea
						.getAreaType().toString(), "containedStopAreas");
			}
		}
		else if (areaType.equals(ChouetteAreaEnum.STOPPLACE))
		{
			// stop places can contains only stop places or commercial stops
			if (!containedStopArea.getAreaType().equals(ChouetteAreaEnum.STOPPLACE)
					&& !containedStopArea.getAreaType().equals(ChouetteAreaEnum.COMMERCIALSTOPPOINT))
			{
				throw new CoreRuntimeException(CoreExceptionCode.UNVALID_TYPE, areaType.toString(), containedStopArea
						.getAreaType().toString(), "containedStopAreas");
			}
		}
		else if (areaType.equals(ChouetteAreaEnum.ITL))
		{
			// restriction constraints can't contains restriction constraints
			if (containedStopArea.getAreaType().equals(ChouetteAreaEnum.ITL))
			{
				throw new CoreRuntimeException(CoreExceptionCode.UNVALID_TYPE, areaType.toString(), containedStopArea
						.getAreaType().toString(), "containedStopAreas");
			}
			// ITL relationship are stored in routingConstraintAreas
			if (!routingConstraintAreas.contains(containedStopArea))
				routingConstraintAreas.add(containedStopArea);
			return;
		}
		if (!containedStopAreas.contains(containedStopArea))
		{
			containedStopAreas.add(containedStopArea);
			containedStopArea.setParent(this);
		}
	}

	/**
	 * remove a child StopArea
	 * 
	 * @param containedStopArea
	 */
	public void removeContainedStopArea(StopArea containedStopArea)
	{
		if (areaType.equals(ChouetteAreaEnum.ITL))
		{
			if (routingConstraintAreas == null)
				routingConstraintAreas = new ArrayList<StopArea>();
			if (routingConstraintAreas.contains(containedStopArea))
				routingConstraintAreas.remove(containedStopArea);

		}
		else
		{
			if (containedStopAreas == null)
				containedStopAreas = new ArrayList<StopArea>();
			if (containedStopAreas.contains(containedStopArea))
			{
				containedStopAreas.remove(containedStopArea);
				containedStopArea.setParent(null);
			}
		}
	}

	/**
	 * add a child StopPoint if not already present
	 * 
	 * @param containedStopPoint
	 */
	public void addContainedStopPoint(StopPoint containedStopPoint)
	{
		if (containedStopPoints == null)
			containedStopPoints = new ArrayList<StopPoint>();
		if (!areaType.equals(ChouetteAreaEnum.BOARDINGPOSITION) && !areaType.equals(ChouetteAreaEnum.QUAY))
		{
			// only boarding positions and quays can contains stop points
			throw new CoreRuntimeException(CoreExceptionCode.UNVALID_TYPE, areaType.toString(), STOPPOINT_KEY,
					"containedStopPoints");
		}
		if (containedStopPoint ==  null || containedStopPoints.contains(containedStopPoint)) return;
		containedStopPoints.add(containedStopPoint);
		containedStopPoint.setContainedInStopArea(this);
	}

	/**
	 * remove a child StopPoint
	 * 
	 * @param containedStopPoint
	 */
	public void removeContainedStopPoint(StopPoint containedStopPoint)
	{
		if (containedStopPoints == null)
			containedStopPoints = new ArrayList<StopPoint>();
		if (containedStopPoints.contains(containedStopPoint))
			containedStopPoints.remove(containedStopPoint);
	}

	/**
	 * add a userNeed if not already present
	 * 
	 * @param userNeed
	 */
	public void addUserNeed(UserNeedEnum userNeed)
	{
		if (userNeeds == null)
			userNeeds = new ArrayList<UserNeedEnum>();
		if (!userNeeds.contains(userNeed))
			userNeeds.add(userNeed);
	}

	/**
	 * remove a userNeed
	 * 
	 * @param userNeed
	 */
	public void removeUserNeed(UserNeedEnum userNeed)
	{
		if (userNeeds == null)
			userNeeds = new ArrayList<UserNeedEnum>();
		if (userNeeds.contains(userNeed))
			userNeeds.remove(userNeed);
	}

	/**
	 * add a connectionLink if not already present
	 * <p>
	 * WARNING : no check on connectionLink startOfLink or endOfLink validity
	 * 
	 * @param connectionLink
	 */
	public void addConnectionLink(ConnectionLink connectionLink)
	{
		if (connectionLinks == null)
			connectionLinks = new ArrayList<ConnectionLink>();
		if (!connectionLinks.contains(connectionLink))
			connectionLinks.add(connectionLink);
	}

	/**
	 * remove a connection link
	 * 
	 * @param connectionLink
	 */
	public void removeConnectionLink(ConnectionLink connectionLink)
	{
		if (connectionLinks == null)
			connectionLinks = new ArrayList<ConnectionLink>();
		if (connectionLinks.contains(connectionLink))
			connectionLinks.remove(connectionLink);
	}

	/**
	 * add an accessLink if not already present
	 * <p>
	 * WARNING : no check on accessLink stopAreaLink validity
	 * 
	 * @param accessLink
	 */
	public void addAccessLink(AccessLink accessLink)
	{
		if (accessLinks == null)
			accessLinks = new ArrayList<AccessLink>();
		if (accessLink != null && !accessLinks.contains(accessLink))
		{
			accessLinks.add(accessLink);
			accessLink.setStopArea(this);
		}
	}

	/**
	 * remove an accessLink
	 * 
	 * @param accessLink
	 */
	public void removeAccessLink(AccessLink accessLink)
	{
		if (accessLinks == null)
			accessLinks = new ArrayList<AccessLink>();
		if (accessLinks.contains(accessLink))
			accessLinks.remove(accessLink);
	}
	/**
	 * add an accessPoint if not already present
	 * <p>
	 * 
	 * @param accessPoint
	 */
	public void addAccessPoint(AccessPoint accessPoint)
	{
		if (accessPoints == null)
			accessPoints = new ArrayList<AccessPoint>();
		if (accessPoint != null && !accessPoints.contains(accessPoint))
		{
			accessPoints.add(accessPoint);
		}
	}

	/**
	 * remove an accessPoint
	 * 
	 * @param accessPoint
	 */
	public void removeAccessPoint(AccessPoint accessPoint)
	{
		if (accessPoints == null)
			accessPoints = new ArrayList<AccessPoint>();
		if (accessPoints.contains(accessPoint))
			accessPoints.remove(accessPoint);
	}

	/**
	 * add a line if not already present
	 * <p>
	 * stop
	 * 
	 * @param line
	 */
	public void addRoutingConstraintLine(Line line)
	{
		if (!areaType.equals(ChouetteAreaEnum.ITL))
		{
			// only routing constraints can contains lines
			throw new CoreRuntimeException(CoreExceptionCode.UNVALID_TYPE, areaType.toString(), STOPAREA_KEY,
					"routingConstraintLines");
		}
		if (routingConstraintLines == null)
			routingConstraintLines = new ArrayList<Line>();
		if (!routingConstraintLines.contains(line))
			routingConstraintLines.add(line);
	}

	/**
	 * remove a line
	 * 
	 * @param line
	 */
	public void removeRoutingConstraintLine(Line line)
	{
		if (!areaType.equals(ChouetteAreaEnum.ITL))
		{
			// only routing constraints can contains lines
			throw new CoreRuntimeException(CoreExceptionCode.UNVALID_TYPE, areaType.toString(), STOPAREA_KEY,
					"routingConstraintLines");
		}
		if (routingConstraintLines == null)
			routingConstraintLines = new ArrayList<Line>();
		if (routingConstraintLines.contains(line))
			routingConstraintLines.remove(line);
	}

	/**
	 * add a line if not already present
	 * <p>
	 * stop
	 * 
	 * @param line
	 */
	public void addRoutingConstraintLineId(String lineId)
	{
		if (!areaType.equals(ChouetteAreaEnum.ITL))
		{
			// only routing constraints can contains lines
			throw new CoreRuntimeException(CoreExceptionCode.UNVALID_TYPE, areaType.toString(), STOPAREA_KEY,
					"routingConstraintLineIds");
		}
		if (routingConstraintLineIds == null)
			routingConstraintLineIds = new ArrayList<String>();
		if (!routingConstraintLineIds.contains(lineId))
			routingConstraintLineIds.add(lineId);
	}

	/**
	 * remove a line
	 * 
	 * @param line
	 */
	public void removeRoutingConstraintLineId(String lineId)
	{
		if (!areaType.equals(ChouetteAreaEnum.ITL))
		{
			// only routing constraints can contains lines
			throw new CoreRuntimeException(CoreExceptionCode.UNVALID_TYPE, areaType.toString(), STOPAREA_KEY,
					"routingConstraintLineIds");
		}
		if (routingConstraintLineIds == null)
			routingConstraintLineIds = new ArrayList<String>();
		if (routingConstraintLineIds.contains(lineId))
			routingConstraintLineIds.remove(lineId);
	}

	/**
	 * add a line if not already present
	 * <p>
	 * stop
	 * 
	 * @param line
	 */
	public void addRoutingConstraintLine(StopArea area)
	{
		if (!areaType.equals(ChouetteAreaEnum.ITL))
		{
			// only routing constraints can contains lines
			throw new CoreRuntimeException(CoreExceptionCode.UNVALID_TYPE, areaType.toString(), STOPAREA_KEY,
					"routingConstraintAreas");
		}
		if (routingConstraintAreas == null)
			routingConstraintAreas = new ArrayList<StopArea>();
		if (!routingConstraintAreas.contains(area))
			routingConstraintAreas.add(area);
	}

	/**
	 * remove a line
	 * 
	 * @param line
	 */
	public void removeRoutingConstraintArea(StopArea area)
	{
		if (!areaType.equals(ChouetteAreaEnum.ITL))
		{
			// only routing constraints can contains lines
			throw new CoreRuntimeException(CoreExceptionCode.UNVALID_TYPE, areaType.toString(), STOPAREA_KEY,
					"routingConstraintAreas");
		}
		if (routingConstraintAreas == null)
			routingConstraintAreas = new ArrayList<StopArea>();
		if (routingConstraintAreas.contains(area))
			routingConstraintAreas.remove(area);
	}


	/**
	 * returns areaCentroid
	 * <br/>
	 * <b>Note:</b>  areaCentroid is not persistent, 
	 * it is present only if complete() was called or set by import
	 *  
	 * @return areaCentroid
	 */
	public AreaCentroid getAreaCentroid() 
	{
		return areaCentroid;
	}
	/**
	 * setting areaCentroid will populate StopArea's attribute by copy from centroid one's  
	 * @param areaCentroid
	 */
	public void setAreaCentroid(AreaCentroid areaCentroid) 
	{  
		this.areaCentroid = areaCentroid;
		if (areaCentroid != null)
			areaCentroid.populateStopArea(this);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#toString(java.
	 * lang.String, int)
	 */
	@Override
	public String toString(String indent, int level)
	{
		StringBuilder sb = new StringBuilder(super.toString(indent, level));
		sb.append("\n").append(indent).append("  areaCentroidId = ").append(areaCentroidId);
		sb.append("\n").append(indent).append("  comment = ").append(comment);
		sb.append("\n").append(indent).append("  areaType = ").append(areaType);
		sb.append("\n").append(indent).append("  fareCode = ").append(fareCode);
		sb.append("\n").append(indent).append("  liftAvailable = ").append(liftAvailable);
		sb.append("\n").append(indent).append("  mobilityRestrictedSuitable = ").append(mobilityRestrictedSuitable);
		sb.append("\n").append(indent).append("  nearestTopicName = ").append(nearestTopicName);
		sb.append("\n").append(indent).append("  registrationNumber = ").append(registrationNumber);
		sb.append("\n").append(indent).append("  stairsAvailable = ").append(stairsAvailable);
		sb.append("\n").append(indent).append("  streetName = ").append(streetName);
		sb.append("\n").append(indent).append("  countryCode = ").append(countryCode);
		sb.append("\n").append(indent).append("  longLatType = ").append(longLatType);
		sb.append("\n").append(indent).append("  latitude = ").append(latitude);
		sb.append("\n").append(indent).append("  longitude = ").append(longitude);
		sb.append("\n").append(indent).append("  x = ").append(x);
		sb.append("\n").append(indent).append("  y = ").append(y);
		sb.append("\n").append(indent).append("  projection = ").append(projectionType);

		if (areaCentroid != null)
		{
			sb.append("\n").append(indent).append(CHILD_ARROW).append(areaCentroid.toString(indent + CHILD_INDENT, 0));
		}

		if (userNeeds != null)
		{
			sb.append("\n").append(indent).append(CHILD_ARROW).append("userNeeds");
			for (UserNeedEnum userNeed : getUserNeeds())
			{
				sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(userNeed);
			}
		}

		if (containedStopIds != null)
		{
			sb.append("\n").append(indent).append(CHILD_ARROW).append("containedStopIds");
			for (String containedStopId : getContainedStopIds())
			{
				sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(containedStopId);
			}
		}

		if (level > 0)
		{
			int childLevel = level - 1;
			String childIndent = indent + CHILD_INDENT;
			if (parent != null)
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append(parent.toString(childIndent, childLevel));
			}

			childIndent = indent + CHILD_LIST_INDENT;
			if (connectionLinks != null && !connectionLinks.isEmpty())
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append("connectionLinks");
				for (ConnectionLink connectionLink : getConnectionLinks())
				{
					sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(connectionLink.toString(childIndent, 1));
				}
			}
			
			if (accessLinks != null && !accessLinks.isEmpty())
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append("accessLinks");
				for (AccessLink accessLink : accessLinks)
				{
					sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(accessLink.toString(childIndent, 1));
				}
				
			}
			
			if (areaType.equals(ChouetteAreaEnum.ITL))
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append("routingAreas");
				for (StopArea routingArea : routingConstraintAreas)
				{
					sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(routingArea.toString(childIndent, 0));
				}
				sb.append("\n").append(indent).append(CHILD_ARROW).append("routingLines");
				for (Line routingLine : routingConstraintLines)
				{
					sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(routingLine.toString(childIndent, 0));
				}
				
			}
		}

		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#complete()
	 */
	@Override
	public void complete()
	{
		if (isCompleted()) return;
		super.complete();
		List<StopPoint> containsPoints = getContainedStopPoints();
		if (containsPoints != null && !containsPoints.isEmpty())
		{
			for (StopPoint child : containsPoints) 
			{
				addContainedStopId(child.getObjectId());
			}
		}
		List<StopArea> containsAreas = getContainedStopAreas();
		if (containsAreas != null && !containsAreas.isEmpty())
		{
			for (StopArea child : containsAreas) 
			{
				addContainedStopId(child.getObjectId());
			}
		}
		
		if (getParent() != null)
		{
			parentObjectId = parent.getObjectId();
			parent.complete();
		}
		// TODO ITL ? 
		if (!areaType.equals(ChouetteAreaEnum.ITL))
		{
			areaCentroid = new AreaCentroid(this);
			if (areaCentroid.getObjectId() == null)
			{
				areaCentroid.setObjectId(getObjectId().replace(STOPAREA_KEY, AREACENTROID_KEY));
			}
			areaCentroid.setCreationTime(getCreationTime());
			areaCentroid.setObjectVersion(getObjectVersion());
			areaCentroid.setContainedInStopAreaId(getObjectId());
			areaCentroid.setName(getName());
			setAreaCentroidId(getAreaCentroid().getObjectId());
		}

		if (getRoutingConstraintLines() != null) 
		{
			for (Line line : getRoutingConstraintLines())
			{
				addRoutingConstraintLineId(line.getObjectId());
			}
		}

		// connectionlinks are not mapped by hibernate ; must fill with 2 collections
		if (getConnectionLinks() == null)
		{
			connectionLinks = new ArrayList<ConnectionLink>();
			if (getConnectionStartLinks() != null)
			{
				connectionLinks.addAll(getConnectionStartLinks());
			}
			if (getConnectionEndLinks() != null)
			{
				// dont add link where start = end
				for (ConnectionLink link : getConnectionEndLinks())
				{
					if (!connectionLinks.contains(link))
						connectionLinks.add(link);
				}
			}
			for (ConnectionLink link : connectionLinks)
			{
				link.complete();
			}
		}

		if (getAccessLinks() != null)
		{
			for (AccessLink accessLink : getAccessLinks())
			{
				accessLink.complete();
			}
		}
	}
}
