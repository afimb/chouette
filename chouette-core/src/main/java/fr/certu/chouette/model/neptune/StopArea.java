package fr.certu.chouette.model.neptune;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.core.CoreExceptionCode;
import fr.certu.chouette.core.CoreRuntimeException;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
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
   public static final String   PARENTSTOPAREA              = "parentStopArea";
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
   @Getter
   @Setter
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
    * List of parents
    * <br/><i>readable/writable</i>
    */
   @Getter
   @Setter
   private List<StopArea>       parents;
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
    * list of access links
    * <p>
    * links to access points
    * <br/><i>readable/writable</i>
    */
   @Getter
   @Setter
   private List<AccessLink>     accessLinks;

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
    * only for {@link ChouetteAreaEnum.ITL} StopAreas
    * <br/><i>readable/writable</i>
    */
   @Getter
   @Setter
   private List<Line>           routingConstraintLines;

   /**
    * line ids affected by this RoutingConstraint (for exchange purpose)
    * <p>
    * only for {@link ChouetteAreaEnum.ITL} StopAreas
    * <br/><i>readable/writable</i>
    */
   @Getter
   @Setter
   private List<String>           routingConstraintLineIds;

   /**
    * parentId for database v1.6 and less compatibility
    * 
    * @deprecated
    */
   @Setter
   private Long                 parentId;
   /**
    * main parent StopArea
    * 
    * @deprecated
    */
   private StopArea             parentStopArea;

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
    * add a parent StopArea if not already present
    * 
    * @param parent
    */
   public void addParent(StopArea parent)
   {
      if (parents == null)
         parents = new ArrayList<StopArea>();
      // TODO check area type compatibility

      if (!parents.contains(parent))
      {
         parents.add(parent);
         if (parentId == null)
            updateParentId();
      }
   }

   /**
    * remove a parent StopArea
    * 
    * @param parent
    */
   public void removeParent(StopArea parent)
   {
      if (parents == null)
         parents = new ArrayList<StopArea>();
      if (parents.contains(parent))
      {
         parents.remove(parent);
         if (parentId == parent.getId())
            updateParentId();
      }
   }

   /**
    * set parentId on first non-RestrictionConstraint parent id
    */
   private void updateParentId()
   {
      parentId = null;
      parentStopArea = null;
      if (parents != null)
      {
         for (StopArea parent : parents)
         {
            if (!parent.getAreaType().equals(ChouetteAreaEnum.ITL))
            {
               parentId = parent.getId();
               parentStopArea = parent;
               break;
            }
         }
      }
   }

   /**
    * get main parent stoparea id
    * <p>
    * maintained for backward compatibility with v1.6 database model
    * 
    * @deprecated see {@link getParents()}
    * @return
    */
   public Long getParentId()
   {
      if (parentId == null)
         updateParentId();
      return parentId;
   }

   /**
    * get main parent stoparea
    * <p>
    * maintained for backward compatibility with v1.6 database model
    * 
    * @deprecated see {@link getParents()}
    * @return
    */
   public StopArea getParentStopArea()
   {
      if (parentStopArea == null)
         updateParentId();
      return parentStopArea;
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
      }
      if (!containedStopAreas.contains(containedStopArea))
         containedStopAreas.add(containedStopArea);
   }

   /**
    * remove a child StopArea
    * 
    * @param containedStopArea
    */
   public void removeContainedStopArea(StopArea containedStopArea)
   {
      if (containedStopAreas == null)
         containedStopAreas = new ArrayList<StopArea>();
      if (containedStopAreas.contains(containedStopArea))
         containedStopAreas.remove(containedStopArea);
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
      if (!containedStopPoints.contains(containedStopPoint))
         containedStopPoints.add(containedStopPoint);
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
      if (!accessLinks.contains(accessLink))
         accessLinks.add(accessLink);
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
         if (parents != null)
         {
            for (StopArea parent : parents)
            {
               sb.append("\n").append(indent).append(CHILD_ARROW).append(parent.toString(childIndent, childLevel));
            }
         }

         childIndent = indent + CHILD_LIST_INDENT;
         if (connectionLinks != null)
         {
            sb.append("\n").append(indent).append(CHILD_ARROW).append("connectionLinks");
            for (ConnectionLink connectionLink : getConnectionLinks())
            {
               sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(connectionLink.toString(childIndent, 0));
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
      if (getParents() != null)
      {
         for (StopArea parent : getParents())
         {
            parent.complete();
         }
         
      }
      if (getAreaCentroid() != null)
      {
         AreaCentroid centroid = getAreaCentroid();
         if (centroid.getObjectId() == null)
         {
            centroid.setObjectId(getObjectId().replace(STOPAREA_KEY, AREACENTROID_KEY));
         }
         centroid.setCreationTime(getCreationTime());
         centroid.setObjectVersion(getObjectVersion());
         centroid.setContainedInStopArea(this);
         centroid.setContainedInStopAreaId(getObjectId());
         centroid.setName(getName());
         setAreaCentroidId(getAreaCentroid().getObjectId());
      }
      
      if (getRoutingConstraintLines() != null)
      {
         for (Line line : getRoutingConstraintLines())
         {
            addRoutingConstraintLineId(line.getObjectId());
         }
      }
      // TODO connectionlinks and accesslinks ? 
   }
}
