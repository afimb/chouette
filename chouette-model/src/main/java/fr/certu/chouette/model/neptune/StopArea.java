package fr.certu.chouette.model.neptune;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import fr.certu.chouette.core.CoreExceptionCode;
import fr.certu.chouette.core.CoreRuntimeException;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;

/**
 * Neptune StopArea
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
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 */

@Entity
@Table(name = "stop_areas")
@NoArgsConstructor
@Log4j
public class StopArea extends NeptuneLocalizedObject
{
   private static final long serialVersionUID = 4548672479038099240L;

   /**
    * name of comment attribute for {@link Filter} attributeName construction
    */
   public static final String COMMENT = "comment";
   /**
    * name of areaType attribute for {@link Filter} attributeName construction
    */
   public static final String AREA_TYPE = "areaType";
   /**
    * name of registrationNumber attribute for {@link Filter} attributeName
    * construction
    */
   public static final String REGISTRATION_NUMBER = "registrationNumber";
   /**
    * name of nearestTopicName attribute for {@link Filter} attributeName
    * construction
    */
   public static final String NEAREST_TOPIC_NAME = "nearestTopicName";
   /**
    * name of fareCode attribute for {@link Filter} attributeName construction
    */
   public static final String FARECODE = "fareCode";
   /**
    * name of areaCentroid attribute for {@link Filter} attributeName
    * construction <br/>
    * to be combined with {@Link AreaCentroid} constants
    */
   public static final String AREACENTROID = "areaCentroid";
   /**
    * name of parentStopArea attribute for {@link Filter} attributeName
    * construction
    */
   public static final String PARENTSTOPAREA = "parent";
   /**
    * name of containedStopAreas attribute for {@link Filter} attributeName
    * construction
    */
   public static final String CONTAINEDSTOPAREAS = "containedStopAreas";
   /**
    * name of containedStopPoints attribute for {@link Filter} attributeName
    * construction
    */
   public static final String CONTAINEDSTOPPOINTS = "containedStopPoints";

   /**
    * predefined filter to limit get on StopPlaces
    */
   // public static final Filter stopPlaceFilter =
   // Filter.getNewEqualsFilter(AREA_TYPE,
   // ChouetteAreaEnum.StopPlace);
   /**
    * predefined filter to limit get on CommercialStopPoints
    */
   // public static final Filter commercialStopPointFilter =
   // Filter.getNewEqualsFilter(AREA_TYPE,
   // ChouetteAreaEnum.CommercialStopPoint);
   /**
    * predefined filter to limit get on RestrictionConstraints
    */
   // public static final Filter restrictionConstraintFilter = Filter
   // .getNewEqualsFilter(AREA_TYPE, ChouetteAreaEnum.ITL);
   /**
    * predefined filter to limit get on BoardingPositions
    */
   // public static final Filter boardingPositionFilter =
   // Filter.getNewEqualsFilter(AREA_TYPE,
   // ChouetteAreaEnum.BoardingPosition);
   /**
    * predefined filter to limit get on Quays
    */
   // public static final Filter quayFilter =
   // Filter.getNewEqualsFilter(AREA_TYPE,
   // ChouetteAreaEnum.Quay);
   /**
    * predefined filter to limit get on PhysicalStops
    */
   // public static final Filter physicalStopsFilter =
   // Filter.getNewInFilter(AREA_TYPE, new ChouetteAreaEnum[] {
   // ChouetteAreaEnum.BoardingPosition, ChouetteAreaEnum.Quay });

   @Getter
   @Column(name = "name")
   private String name;

   @Getter
   @Column(name = "comment")
   private String comment;

   @Getter
   @Setter
   @Enumerated(EnumType.STRING)
   @Column(name = "area_type")
   private ChouetteAreaEnum areaType;

   @Getter
   @Column(name = "registration_number")
   private String registrationNumber;

   @Getter
   @Setter
   @Column(name = "nearest_topic_name")
   private String nearestTopicName;

   @Getter
   @Setter
   @Column(name = "fare_code")
   private Integer fareCode;

   @Getter
   @Setter
   @Column(name = "lift_availability")
   private boolean liftAvailable = false;

   @Getter
   @Setter
   @Column(name = "mobility_restricted_suitability")
   private boolean mobilityRestrictedSuitable = false;

   @Getter
   @Setter
   @Column(name = "stairs_availability")
   private boolean stairsAvailable = false;

   @Getter
   @Setter
   @Column(name = "int_user_needs")
   private Integer intUserNeeds = 0;

   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "parent_id")
   private StopArea parent;

   @Getter
   @Setter
   @ManyToMany
   @JoinTable(name = "routing_constraints_lines", joinColumns = { @JoinColumn(name = "stop_area_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "line_id", nullable = false, updatable = false) })
   private List<Line> routingConstraintLines = new ArrayList<Line>(0);

   @Getter
   @Setter
   @ManyToMany
   @JoinTable(name = "stop_areas_stop_areas", joinColumns = { @JoinColumn(name = "parent_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "child_id", nullable = false, updatable = false) })
   private List<StopArea> routingConstraintAreas = new ArrayList<StopArea>(0);

   @Getter
   @Setter
   @OneToMany(mappedBy = "parent")
   private List<StopArea> containedStopAreas = new ArrayList<StopArea>(0);

   @Getter
   @Setter
   @OneToMany(mappedBy = "containedInStopArea")
   private List<StopPoint> containedStopPoints = new ArrayList<StopPoint>(0);

   @Getter
   @Setter
   @OneToMany
   @JoinColumn(name = "stop_area_id", updatable = false)
   private List<AccessLink> accessLinks = new ArrayList<AccessLink>(0);

   @Getter
   @Setter
   @OneToMany
   @JoinColumn(name = "departure_id", updatable = false)
   private List<ConnectionLink> connectionStartLinks = new ArrayList<ConnectionLink>(
         0);

   @Getter
   @Setter
   @OneToMany
   @JoinColumn(name = "arrival_id", updatable = false)
   private List<ConnectionLink> connectionEndLinks = new ArrayList<ConnectionLink>(
         0);

   @Getter
   @Setter
   @OneToMany(mappedBy = "containedIn", cascade = { CascadeType.PERSIST,
         CascadeType.MERGE })
   private List<AccessPoint> accessPoints = new ArrayList<AccessPoint>(0);

   /**
    * AreaCentroid ObjectId for import/export purpose <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   @Transient
   private String areaCentroidId;

   /**
    * List of Children objectIds for import/export purpose
    * <p/>
    * may content StopPoint or StopArea ids but not mixed <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   @Transient
   private List<String> containedStopIds;

   /**
    * List of the specific user needs available <br/>
    * <i>readable/writable</i>
    */
   @Transient
   private List<UserNeedEnum> userNeeds;

   /**
    * list of connection links
    * <p>
    * links to others StopAreas (may be same one) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   @Transient
   private List<ConnectionLink> connectionLinks;

   /**
    * list of facilities <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   @Transient
   private List<Facility> facilities;

   /**
    * line ids affected by this RoutingConstraint (for exchange purpose)
    * <p>
    * only for {@link ChouetteAreaEnum}.ITL StopAreas <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   @Transient
   private List<String> routingConstraintLineIds;

   /**
    * non ITL parent StopArea ObjectID
    * 
    */
   @Getter
   @Setter
   @Transient
   private String parentObjectId;

   public void setRegistrationNumber(String value)
   {
      if (value != null && value.length() > 255)
      {
         log.warn("registrationNumber too long, truncated " + value);
         registrationNumber = value.substring(0, 255);
      } else
      {
         registrationNumber = value;
      }
   }

   public void setName(String value)
   {
      if (value != null && value.length() > 255)
      {
         log.warn("name too long, truncated " + value);
         name = value.substring(0, 255);
      } else
      {
         name = value;
      }
   }

   public void setComment(String value)
   {
      if (value != null && value.length() > 255)
      {
         log.warn("comment too long, truncated " + value);
         comment = value.substring(0, 255);
      } else
      {
         comment = value;
      }
   }

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
      if (areaType.equals(ChouetteAreaEnum.BoardingPosition)
            || areaType.equals(ChouetteAreaEnum.Quay))
      {
         // boarding positions or quays can't contains stop areas
         throw new CoreRuntimeException(CoreExceptionCode.UNVALID_TYPE,
               areaType.toString(), STOPAREA_KEY, "containedStopAreas");
      }
      if (areaType.equals(ChouetteAreaEnum.CommercialStopPoint))
      {
         // commercial stops can contains only boarding positions or quays
         if (!containedStopArea.getAreaType().equals(
               ChouetteAreaEnum.BoardingPosition)
               && !containedStopArea.getAreaType()
                     .equals(ChouetteAreaEnum.Quay))
         {
            throw new CoreRuntimeException(CoreExceptionCode.UNVALID_TYPE,
                  areaType.toString(), containedStopArea.getAreaType()
                        .toString(), "containedStopAreas");
         }
      } else if (areaType.equals(ChouetteAreaEnum.StopPlace))
      {
         // stop places can contains only stop places or commercial stops
         if (!containedStopArea.getAreaType()
               .equals(ChouetteAreaEnum.StopPlace)
               && !containedStopArea.getAreaType().equals(
                     ChouetteAreaEnum.CommercialStopPoint))
         {
            throw new CoreRuntimeException(CoreExceptionCode.UNVALID_TYPE,
                  areaType.toString(), containedStopArea.getAreaType()
                        .toString(), "containedStopAreas");
         }
      } else if (areaType.equals(ChouetteAreaEnum.ITL))
      {
         // restriction constraints can't contains restriction constraints
         if (containedStopArea.getAreaType().equals(ChouetteAreaEnum.ITL))
         {
            throw new CoreRuntimeException(CoreExceptionCode.UNVALID_TYPE,
                  areaType.toString(), containedStopArea.getAreaType()
                        .toString(), "containedStopAreas");
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

      } else
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
      if (!areaType.equals(ChouetteAreaEnum.BoardingPosition)
            && !areaType.equals(ChouetteAreaEnum.Quay))
      {
         // only boarding positions and quays can contains stop points
         throw new CoreRuntimeException(CoreExceptionCode.UNVALID_TYPE,
               areaType.toString(), STOPPOINT_KEY, "containedStopPoints");
      }
      if (containedStopPoint == null
            || containedStopPoints.contains(containedStopPoint))
         return;
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
         throw new CoreRuntimeException(CoreExceptionCode.UNVALID_TYPE,
               areaType.toString(), STOPAREA_KEY, "routingConstraintLines");
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
         throw new CoreRuntimeException(CoreExceptionCode.UNVALID_TYPE,
               areaType.toString(), STOPAREA_KEY, "routingConstraintLines");
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
         throw new CoreRuntimeException(CoreExceptionCode.UNVALID_TYPE,
               areaType.toString(), STOPAREA_KEY, "routingConstraintLineIds");
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
         throw new CoreRuntimeException(CoreExceptionCode.UNVALID_TYPE,
               areaType.toString(), STOPAREA_KEY, "routingConstraintLineIds");
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
         throw new CoreRuntimeException(CoreExceptionCode.UNVALID_TYPE,
               areaType.toString(), STOPAREA_KEY, "routingConstraintAreas");
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
         throw new CoreRuntimeException(CoreExceptionCode.UNVALID_TYPE,
               areaType.toString(), STOPAREA_KEY, "routingConstraintAreas");
      }
      if (routingConstraintAreas == null)
         routingConstraintAreas = new ArrayList<StopArea>();
      if (routingConstraintAreas.contains(area))
         routingConstraintAreas.remove(area);
   }

   /**
    * add a userNeed value in userNeeds collection if not already present <br/>
    * intUserNeeds will be automatically synchronized <br/>
    * <i>readable/writable</i>
    * 
    * @param userNeed
    *           the userNeed to add
    */
   public synchronized void addUserNeed(UserNeedEnum userNeed)
   {
      if (userNeeds == null)
         userNeeds = new ArrayList<UserNeedEnum>();
      if (!userNeeds.contains(userNeed))
      {
         userNeeds.add(userNeed);
         synchronizeUserNeeds();
      }
   }

   /**
    * add a collection of userNeed values in userNeeds collection if not already
    * present <br/>
    * intUserNeeds will be automatically synchronized
    * 
    * @param userNeedCollection
    *           the userNeeds to add
    */
   public synchronized void addAllUserNeed(
         Collection<UserNeedEnum> userNeedCollection)
   {
      if (userNeeds == null)
         userNeeds = new ArrayList<UserNeedEnum>();
      boolean added = false;
      for (UserNeedEnum userNeed : userNeedCollection)
      {
         if (!userNeeds.contains(userNeed))
         {
            userNeeds.add(userNeed);
            added = true;
         }
      }
      if (added)
      {
         synchronizeUserNeeds();
      }
   }

   /**
    * get UserNeeds list
    * 
    * @return userNeeds
    */
   public synchronized List<UserNeedEnum> getUserNeeds()
   {
      // synchronise userNeeds with intUserNeeds
      if (intUserNeeds == null)
      {
         userNeeds = null;
         return userNeeds;
      }

      userNeeds = new ArrayList<UserNeedEnum>();
      UserNeedEnum[] userNeedEnums = UserNeedEnum.values();
      for (UserNeedEnum userNeed : userNeedEnums)
      {
         int filtre = (int) Math.pow(2, userNeed.ordinal());
         if (filtre == (intUserNeeds.intValue() & filtre))
         {
            if (!userNeeds.contains(userNeed))
            {
               userNeeds.add(userNeed);
            }
         }
      }
      return userNeeds;
   }

   /**
    * set the userNeeds list <br/>
    * intUserNeeds will be automatically synchronized
    * 
    * @param userNeedEnums
    *           list of UserNeeds to set
    */
   public synchronized void setUserNeeds(List<UserNeedEnum> userNeedEnums)
   {
      userNeeds = userNeedEnums;

      synchronizeUserNeeds();
   }

   /**
    * synchronize intUserNeeds with userNeeds List content
    */
   private void synchronizeUserNeeds()
   {
      intUserNeeds = 0;
      if (userNeeds == null)
         return;

      for (UserNeedEnum userNeedEnum : userNeeds)
      {
         intUserNeeds += (int) Math.pow(2, userNeedEnum.ordinal());
      }
   }

   @Override
   public String toString(String indent, int level)
   {
      StringBuilder sb = new StringBuilder(super.toString(indent, level));
      sb.append("\n").append(indent).append("  areaCentroidId = ")
            .append(areaCentroidId);
      sb.append("\n").append(indent).append("  comment = ").append(comment);
      sb.append("\n").append(indent).append("  areaType = ").append(areaType);
      sb.append("\n").append(indent).append("  fareCode = ").append(fareCode);
      sb.append("\n").append(indent).append("  liftAvailable = ")
            .append(liftAvailable);
      sb.append("\n").append(indent).append("  mobilityRestrictedSuitable = ")
            .append(mobilityRestrictedSuitable);
      sb.append("\n").append(indent).append("  nearestTopicName = ")
            .append(nearestTopicName);
      sb.append("\n").append(indent).append("  registrationNumber = ")
            .append(registrationNumber);
      sb.append("\n").append(indent).append("  stairsAvailable = ")
            .append(stairsAvailable);

      if (getUserNeeds() != null)
      {
         sb.append("\n").append(indent).append(CHILD_ARROW).append("userNeeds");
         for (UserNeedEnum userNeed : getUserNeeds())
         {
            sb.append("\n").append(indent).append(CHILD_LIST_ARROW)
                  .append(userNeed);
         }
      }

      if (containedStopIds != null)
      {
         sb.append("\n").append(indent).append(CHILD_ARROW)
               .append("containedStopIds");
         for (String containedStopId : getContainedStopIds())
         {
            sb.append("\n").append(indent).append(CHILD_LIST_ARROW)
                  .append(containedStopId);
         }
      }

      if (level > 0)
      {
         int childLevel = level - 1;
         String childIndent = indent + CHILD_INDENT;
         if (parent != null)
         {
            sb.append("\n").append(indent).append(CHILD_ARROW)
                  .append(parent.toString(childIndent, childLevel));
         }

         childIndent = indent + CHILD_LIST_INDENT;
         if (connectionLinks != null && !connectionLinks.isEmpty())
         {
            sb.append("\n").append(indent).append(CHILD_ARROW)
                  .append("connectionLinks");
            for (ConnectionLink connectionLink : getConnectionLinks())
            {
               sb.append("\n").append(indent).append(CHILD_LIST_ARROW)
                     .append(connectionLink.toString(childIndent, 1));
            }
         }

         if (accessLinks != null && !accessLinks.isEmpty())
         {
            sb.append("\n").append(indent).append(CHILD_ARROW)
                  .append("accessLinks");
            for (AccessLink accessLink : accessLinks)
            {
               sb.append("\n").append(indent).append(CHILD_LIST_ARROW)
                     .append(accessLink.toString(childIndent, 1));
            }

         }

         if (areaType.equals(ChouetteAreaEnum.ITL))
         {
            sb.append("\n").append(indent).append(CHILD_ARROW)
                  .append("routingAreas");
            for (StopArea routingArea : routingConstraintAreas)
            {
               sb.append("\n").append(indent).append(CHILD_LIST_ARROW)
                     .append(routingArea.toString(childIndent, 0));
            }
            sb.append("\n").append(indent).append(CHILD_ARROW)
                  .append("routingLines");
            for (Line routingLine : routingConstraintLines)
            {
               sb.append("\n").append(indent).append(CHILD_LIST_ARROW)
                     .append(routingLine.toString(childIndent, 0));
            }

         }
      }

      return sb.toString();
   }

   @Override
   public void complete()
   {
      if (isCompleted())
         return;
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

      if (getRoutingConstraintLines() != null)
      {
         for (Line line : getRoutingConstraintLines())
         {
            addRoutingConstraintLineId(line.getObjectId());
         }
      }

      if (getRoutingConstraintAreas() != null)
      {
         for (StopArea child : getRoutingConstraintAreas())
         {
            addContainedStopId(child.getObjectId());
         }

      }

      // connectionlinks are not mapped by hibernate ; must fill with 2
      // collections
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

   @Override
   public <T extends NeptuneObject> boolean compareAttributes(T anotherObject)
   {
      if (anotherObject instanceof StopArea)
      {
         StopArea another = (StopArea) anotherObject;
         if (!sameValue(this.getObjectId(), another.getObjectId()))
            return false;
         if (!sameValue(this.getObjectVersion(), another.getObjectVersion()))
            return false;
         if (!sameValue(this.getName(), another.getName()))
            return false;
         if (!sameValue(this.getComment(), another.getComment()))
            return false;
         if (!sameValue(this.getIntUserNeeds(), another.getIntUserNeeds()))
            return false;
         if (!sameValue(this.getRegistrationNumber(),
               another.getRegistrationNumber()))
            return false;
         if (!sameValue(this.getCountryCode(), another.getCountryCode()))
            return false;
         if (!sameValue(this.getStreetName(), another.getStreetName()))
            return false;
         if (!sameValue(this.getLatitude(), another.getLatitude()))
            return false;
         if (!sameValue(this.getLongitude(), another.getLongitude()))
            return false;
         if (!sameValue(this.getLongLatType(), another.getLongLatType()))
            return false;
         if (!sameValue(this.getProjectionType(), another.getProjectionType()))
            return false;
         if (!sameValue(this.getX(), another.getX()))
            return false;
         if (!sameValue(this.getY(), another.getY()))
            return false;

         if (!sameValue(this.getAreaType(), another.getAreaType()))
            return false;
         if (!sameValue(this.getFareCode(), another.getFareCode()))
            return false;
         if (!sameValue(this.getNearestTopicName(),
               another.getNearestTopicName()))
            return false;
         return true;
      } else
      {
         return false;
      }
   }

   @Override
   public String toURL()
   {
      return "stop_areas/" + getId();
   }

}
