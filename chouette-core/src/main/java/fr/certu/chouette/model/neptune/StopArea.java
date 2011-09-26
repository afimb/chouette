package fr.certu.chouette.model.neptune;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;

public class StopArea extends NeptuneIdentifiedObject 
{
	private static final long serialVersionUID = 4548672479038099240L;
	// constant for persistence fields
	/**
	 * name of comment attribute for {@link Filter} attributeName construction
	 */
	public static final String COMMENT = "comment"; 
   /**
    * name of areaType attribute for {@link Filter} attributeName construction
    */
	public static final String AREA_TYPE = "areaType"; 
   /**
    * name of registrationNumber attribute for {@link Filter} attributeName construction
    */
	public static final String REGISTRATION_NUMBER = "registrationNumber"; 
   /**
    * name of nearestTopicName attribute for {@link Filter} attributeName construction
    */
	public static final String NEAREST_TOPIC_NAME = "nearestTopicName"; 
   /**
    * name of fareCode attribute for {@link Filter} attributeName construction
    */
	public static final String FARECODE ="fareCode"; 
   /**
    * name of areaCentroid attribute for {@link Filter} attributeName construction
    * <br/> to be combined with {@Link AreaCentroid} constants
    */
	public static final String AREACENTROID="areaCentroid"; 
   /**
    * name of parentStopArea attribute for {@link Filter} attributeName construction
    */
	public static final String PARENTSTOPAREA="parentStopArea"; 
   /**
    * name of containedStopAreas attribute for {@link Filter} attributeName construction
    */
	public static final String CONTAINEDSTOPAREAS="containedStopAreas"; 
   /**
    * name of containedStopPoints attribute for {@link Filter} attributeName construction
    */
	public static final String CONTAINEDSTOPPOINTS="containedStopPoints";
	
	/**
	 * predefined filter to limit get on StopPlaces
	 */
	public static final Filter stopPlaceFilter = Filter.getNewEqualsFilter(AREA_TYPE, ChouetteAreaEnum.STOPPLACE);
   /**
    * predefined filter to limit get on CommercialStopPoints
    */
   public static final Filter commercialStopPointFilter = Filter.getNewEqualsFilter(AREA_TYPE, ChouetteAreaEnum.COMMERCIALSTOPPOINT);
   /**
    * predefined filter to limit get on RestrictionConstraints
    */
   public static final Filter restrictionConstraintFilter = Filter.getNewEqualsFilter(AREA_TYPE, ChouetteAreaEnum.ITL);
   /**
    * predefined filter to limit get on BoardingPositions
    */
   public static final Filter boardingPositionFilter = Filter.getNewEqualsFilter(AREA_TYPE, ChouetteAreaEnum.BOARDINGPOSITION);
   /**
    * predefined filter to limit get on Quays
    */
   public static final Filter quayFilter = Filter.getNewEqualsFilter(AREA_TYPE, ChouetteAreaEnum.QUAY);
   /**
    * predefined filter to limit get on PhysicalStops
    */
   public static final Filter physicalStopsFilter = Filter.getNewOrFilter(boardingPositionFilter,quayFilter);
	
	@Getter @Setter private List<String> boundaryPoints;
	@Getter @Setter private String areaCentroidId;
	@Getter @Setter private AreaCentroid areaCentroid;
	@Getter @Setter private String comment;
	@Getter @Setter private List<String> containedStopIds;
	@Getter @Setter private List<StopArea> containedStopAreas;
	@Getter @Setter private List<StopPoint> containedStopPoints;
	@Getter @Setter private StopArea parentStopArea;
	@Getter @Setter private Long parentId;
	@Getter @Setter private ChouetteAreaEnum areaType;
	@Getter @Setter private Integer fareCode;
	@Getter @Setter private Boolean liftAvailable;
	@Getter @Setter private Boolean mobilityRestrictedSuitable;
	@Getter @Setter private Boolean stairsAvailable;
	@Getter @Setter private String nearestTopicName;
	@Getter @Setter private String registrationNumber;
	@Getter @Setter private List<UserNeedEnum> userNeeds;
	@Getter @Setter private List<ConnectionLink> connectionLinks;

	@Getter @Setter private List<AccessLink> accessLinks;
	//@Getter @Setter private List<RestrictionConstraint> restrictionConstraints;

    //    private static List<RestrictionConstraint> unvalidRestrictionConstraints;
	@Getter @Setter private List<Facility> facilities;
	
	// @Getter @Setter private List<RoutingConstraintOnLine> routingConstraintOnLines;

	public void addFacility(Facility facility)
	{
		if (facilities == null) facilities = new ArrayList<Facility>();
		if (!facilities.contains(facility)) facilities.add(facility);
	}
	
	public void removeFacility(Facility facility)
	{
		if( facilities == null) facilities = new ArrayList<Facility>();
		if (facilities.contains(facility)) facilities.remove(facility);
	}

	public void addBoundaryPoint(String boundaryPoint)
	{
		if (boundaryPoints == null) boundaryPoints = new ArrayList<String>();
		if (!boundaryPoints.contains(boundaryPoint)) boundaryPoints.add(boundaryPoint);
	}
	
	public void removeBoundaryPoint(String boundaryPoint)
	{
		if (boundaryPoints == null) boundaryPoints = new ArrayList<String>();
		if (boundaryPoints.contains(boundaryPoint)) boundaryPoints.remove(boundaryPoint);
	}

	public void addContainedStopId(String containedStopId)
	{
		if (containedStopIds == null) containedStopIds = new ArrayList<String>();
		if (!containedStopIds.contains(containedStopId))
			containedStopIds.add(containedStopId);
	}
	public void removeContainedStopId(String containedStopId)
	{
		if (containedStopIds == null) containedStopIds = new ArrayList<String>();
		if (containedStopIds.contains(containedStopId))
			containedStopIds.remove(containedStopId);
	}

	public void addContainedStopArea(StopArea containedStopArea)
	{
		if (containedStopAreas == null) containedStopAreas = new ArrayList<StopArea>();
		if (!containedStopAreas.contains(containedStopArea))
			containedStopAreas.add(containedStopArea);
	}
	
	public void removeContainedStopArea(StopArea containedStopArea)
	{
		if (containedStopAreas == null) containedStopAreas = new ArrayList<StopArea>();
		if (containedStopAreas.contains(containedStopArea))
			containedStopAreas.remove(containedStopArea);
	}

	public void addContainedStopPoint(StopPoint containedStopPoint)
	{
		if (containedStopPoints == null) containedStopPoints = new ArrayList<StopPoint>();
		if (!containedStopPoints.contains(containedStopPoint))
			containedStopPoints.add(containedStopPoint);
	}
	public void removeContainedStopPoint(StopPoint containedStopPoint)
	{
		if (containedStopPoints == null) containedStopPoints = new ArrayList<StopPoint>();
		if (containedStopPoints.contains(containedStopPoint))
			containedStopPoints.remove(containedStopPoint);
	}

	public void addUserNeed(UserNeedEnum userNeed)
	{
		if (userNeeds == null) userNeeds = new ArrayList<UserNeedEnum>();
		userNeeds.add(userNeed);
	}

	public void addConnectionLink(ConnectionLink connectionLink)
	{
		if (connectionLinks == null) connectionLinks = new ArrayList<ConnectionLink>();
		if (!connectionLinks.contains(connectionLink)) connectionLinks.add(connectionLink);
	}
	public void removeConnectionLink(ConnectionLink connectionLink)
	{
		if (connectionLinks == null) connectionLinks = new ArrayList<ConnectionLink>();
		if (connectionLinks.contains(connectionLink)) connectionLinks.remove(connectionLink);
	}

	public void addAccessLink(AccessLink accessLink){
		if (accessLinks == null) accessLinks = new ArrayList<AccessLink>();
		if (!accessLinks.contains(accessLink)) accessLinks.add(accessLink);
	}
	public void removeAccessLink(AccessLink accessLink){
		if (accessLinks == null) accessLinks = new ArrayList<AccessLink>();
		if (accessLinks.contains(accessLink)) accessLinks.remove(accessLink);
	}

	/*
	public void addRestrictionConstraint(RestrictionConstraint restrictionConstraint){
		if (restrictionConstraints == null) restrictionConstraints = new ArrayList<RestrictionConstraint>();
		restrictionConstraints.add(restrictionConstraint);
	}
        
        public static void addUnvalidRestrictionConstraint(RestrictionConstraint unvalidRestrictionConstraint) {
            if (unvalidRestrictionConstraints == null) unvalidRestrictionConstraints = new ArrayList<RestrictionConstraint>();
            unvalidRestrictionConstraints.add(unvalidRestrictionConstraint);
        }
        
        public static void setUnvalidRestrictionConstraints(List<RestrictionConstraint> tmpUnvalidRestrictionConstraints) {
            unvalidRestrictionConstraints = tmpUnvalidRestrictionConstraints;
        }
        
        public static List<RestrictionConstraint> getUnvalidRestrictionConstraints() {
            return unvalidRestrictionConstraints;
        }
        */
        

	@Override
	public String toString(String indent,int level)
	{
		StringBuilder sb = new StringBuilder(super.toString(indent,level));
		sb.append("\n").append(indent).append("  areaCentroidId = ").append(areaCentroidId);
		sb.append("\n").append(indent).append("  comment = ").append(comment);
		sb.append("\n").append(indent).append("  areaType = ").append(areaType);
		sb.append("\n").append(indent).append("  fareCode = ").append(fareCode);
		sb.append("\n").append(indent).append("  liftAvailable = ").append(liftAvailable);
		sb.append("\n").append(indent).append("  mobilityRestrictedSuitable = ").append(mobilityRestrictedSuitable);
		sb.append("\n").append(indent).append("  nearestTopicName = ").append(nearestTopicName);
		sb.append("\n").append(indent).append("  registrationNumber = ").append(registrationNumber);
		sb.append("\n").append(indent).append("  stairsAvailable = ").append(stairsAvailable);

		if(userNeeds != null){
			sb.append("\n").append(indent).append(CHILD_ARROW).append("userNeeds");
			for (UserNeedEnum userNeed : getUserNeeds())
			{
				sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(userNeed);
			}
		}

		if(boundaryPoints != null){
			sb.append("\n").append(indent).append(CHILD_ARROW).append("boundaryPoints");
			for (String boundaryPoint : getBoundaryPoints())
			{
				sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(boundaryPoint);
			}
		}

		if(containedStopIds != null){
			sb.append("\n").append(indent).append(CHILD_ARROW).append("containedStopIds");
			for (String containedStopId : getContainedStopIds())
			{
				sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(containedStopId);
			}
		}

		if (level > 0)
		{
			int childLevel = level -1;
			String childIndent = indent + CHILD_INDENT;
			if (areaCentroid != null) 
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append(areaCentroid.toString(childIndent,0));
			}
			if (parentStopArea != null)
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append(parentStopArea.toString(childIndent,childLevel));

			}
			childIndent = indent + CHILD_LIST_INDENT;
			if (connectionLinks != null)
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append("connectionLinks");
				for (ConnectionLink connectionLink : getConnectionLinks())
				{
					sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(connectionLink.toString(childIndent,0));
				}
			}
		}

		return sb.toString();
	}
	/*
	public List<String> extracAreaIdsFromRConstraint(){
		List<String> areaIds = new ArrayList<String>();
		if(restrictionConstraints != null){
			for (RestrictionConstraint constraint : restrictionConstraints) {
				if(constraint != null){
					String areaId = constraint.getAreaId();
					if(areaId != null){
						areaIds.add(areaId);
					}
				}
			}	
		}
		return areaIds;
	}
	*/
}
