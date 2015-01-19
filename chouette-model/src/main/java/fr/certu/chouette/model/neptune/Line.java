/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.model.neptune;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;

/**
 * Chouette Line : a group of Routes which is generally known to the public 
 * by a similar name or number
 * <p/>
 * Neptune mapping : Line <br/>
 * Gtfs mapping : Line <br/>
 */
@Entity
@Table(name = "lines")
@NoArgsConstructor
@Log4j
public class Line extends NeptuneIdentifiedObject
{
   private static final long serialVersionUID = -8086291270595894778L;

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
    *           New value
    */
   public void setName(String value)
   {
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
    *           New value
    */
   public void setComment(String value)
   {
      comment = dataBaseSizeProtectedValue(value, "comment", log);
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
    *           New value
    */
   public void setNumber(String value)
   {
      number = dataBaseSizeProtectedValue(value,"number",log);
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
    *           New value
    */
   public void setPublishedName(String value)
   {
      publishedName = dataBaseSizeProtectedValue(value, "publishedName", log);
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
    *           New value
    */
   public void setRegistrationNumber(String value)
   {
      registrationNumber = dataBaseSizeProtectedValue(value,"registrationNumber",log);
   }

   /**
    * Transport mode
    * 
    * @param transportModeName
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Enumerated(EnumType.STRING)
   @Column(name = "transport_mode_name")
   private TransportModeNameEnum transportModeName;

   /**
    * mobility restriction indicator (such as wheel chairs) <br/>
    * 
    * <ul>
    * <li>true if wheel chairs can use this line</li>
    * <li>false if wheel chairs can't use this line</li>
    * </ul>
    * 
    * @param mobilityRestrictedSuitable
    *           New state for mobility restriction indicator
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
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Column(name = "int_user_needs")
   private Integer intUserNeeds = 0;

   /**
    * web site url
    * 
    * @return The actual value
    * 
    * @since 2.5.1
    */
   @Getter
   @Column(name = "url")
   private String url;
   /**
    * set  web site url <br/>
    * truncated to 255 characters if too long
    * 
    * @param value
    *           New value
    * 
    * @since 2.5.1
    */
   public void setUrl(String value)
   {
      url = dataBaseSizeProtectedValue(value,"url",log);
   }

   /**
    * line drawing color <br/>
    * should be used also on label background
    * 
    * @return The actual value in RRGGBB hexadecimal format
    * 
    * @since 2.5.1
    */
   @Getter
   @Column(name = "color", length = 6)
   private String color;

   /**
    * set  line drawing color <br/>
    * truncated to 6 characters if too long
    * 
    * @param value
    *           New value in RRGGBB hexadecimal format
    * 
    * @since 2.5.1
    */
   public void setColor(String value)
   {
      color = dataBaseSizeProtectedValue(value,"color",log);
   }

   /**
    * line text color
    * 
    * @return The actual value in RRGGBB hexadecimal format

    * @since 2.5.1
    */
   @Getter
   @Column(name = "text_color", length = 6)
   private String textColor;
   /**
    * set  line text color <br/>
    * truncated to 6 characters if too long
    * 
    * @param value
    *           New value in RRGGBB hexadecimal format
    *           
    * @since 2.5.1
    */
   public void setTextColor(String value)
   {
      textColor = dataBaseSizeProtectedValue(value,"textColor",log);
   }

   /**
    * network reference
    * 
    * @param ptNetwork
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @ManyToOne
   @JoinColumn(name = "network_id")
   private PTNetwork ptNetwork;

   /**
    * company reference
    * 
    * @param company
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @ManyToOne
   @JoinColumn(name = "company_id")
   private Company company;

   /**
    * list of routes
    * 
    * @param routes
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @OneToMany(mappedBy = "line", cascade = { CascadeType.PERSIST,
         CascadeType.MERGE })
   private List<Route> routes = new ArrayList<Route>(0);

   /**
    * groups of lines reverse reference
    * 
    * @param groupOfLines
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @ManyToMany
   @JoinTable(name = "group_of_lines_lines", joinColumns = { @JoinColumn(name = "line_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "group_of_line_id", nullable = false, updatable = false) })
   private List<GroupOfLine> groupOfLines = new ArrayList<GroupOfLine>(0);

   /**
    * routing constraints associations
    * 
    * @param routingConstraints
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @ManyToMany(mappedBy = "routingConstraintLines")
   private List<StopArea> routingConstraints = new ArrayList<StopArea>(0);

   /**
    * Neptune identification referring to the line's network <br/>
    * (import/export purpose)
    * 
    * @param ptNetworkIdShortcut
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Transient
   private String ptNetworkIdShortcut;
   /**
    * Neptune identification referring to the line's routes <br/>
    * (import/export purpose)
    * 
    * @param routeIds
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Transient
   private List<String> routeIds;
   /**
    * Neptune identification referring to the departures/arrivals stoppoints of
    * the line's JourneyPatterns<br/>
    * (import/export purpose)
    * 
    * @param lineEnds
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Transient
   private List<String> lineEnds;

   /**
    * The line's companies objects <br/>
    * shortcut reference of companies connected to line or to vehicle journeys of the line<br/>
    * (import/export purpose)
    * 
    * @param companies
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Transient
   private List<Company> companies;

   /**
    * List of the specific user needs available 
    */
   @Transient
   private List<UserNeedEnum> userNeeds;

   /**
    * The GroupOfLinesId of the line <br/>
    * (import/export purpose)
    * 
    * @param groupOfLineIds
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Transient
   private List<String> groupOfLineIds;

   /**
    * The optional RoutingConstraint objectIds of the line
    * <p>
    * RoutingConstraints are {@link StopArea} of {@link ChouetteAreaEnum}.ITL
    * areaType <br/>
    * (import/export purpose)
    * 
    * @param routingConstraintIds
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Transient
   private List<String> routingConstraintIds;
   
   /**
    * The optional Companies objectIds of the line
    * <p>
    * (import/export purpose)
    * 
    * @return The actual value
    */
   @Getter
   @Transient
   private List<String> companyIds = new ArrayList<>();


   /**
    * list of facilities (currently not saved) <br/>
    * (import purpose)
    * 
    * @param facilities
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Transient
   private List<Facility> facilities;

   /**
    * List of journeyPatterns filled only after complete() call<br/>
    * (export purpose)
    * 
    * @return The actual value
    */
   @Getter
   @Transient
   private List<JourneyPattern> journeyPatterns;

   /**
    * List of vehicleJourneys filled only after complete() call<br/>
    * (export purpose)
    * 
    * @return The actual value
    */
   @Getter
   @Transient
   private List<VehicleJourney> vehicleJourneys;

   /**
    * List of timetables filled only after complete() call<br/>
    * (export purpose)
    * 
    * @return The actual value
    */
   @Getter
   @Transient
   private List<Timetable> timetables;

   /**
    * List of stopPoints filled only after complete() call<br/>
    * (export purpose)
    * 
    * @return The actual value
    */
   @Getter
   @Transient
   private List<StopPoint> stopPoints;

   /**
    * List of stopAreas (excluded RoutingConstraints) filled only after
    * complete() call<br/>
    * (export purpose)
    * 
    * @return The actual value
    */
   @Getter
   @Transient
   private List<StopArea> stopAreas;

   /**
    * List of boardingPositions filled only after complete() call<br/>
    * (export purpose)
    * 
    * @return The actual value
    */
   @Getter
   @Transient
   private List<StopArea> boardingPositions;

   /**
    * List of quays filled only after complete() call<br/>
    * (export purpose)
    * 
    * @return The actual value
    */
   @Getter
   @Transient
   private List<StopArea> quays;

   /**
    * List of commercialStopPoints filled only after complete() call<br/>
    * (export purpose)
    * 
    * @return The actual value
    */
   @Getter
   @Transient
   private List<StopArea> commercialStopPoints;

   /**
    * List of stopPlaces filled only after complete() call<br/>
    * (export purpose)
    * 
    * @return The actual value
    */
   @Getter
   @Transient
   private List<StopArea> stopPlaces;

   /**
    * List of accessLinks filled only after complete() call<br/>
    * (export purpose)
    * 
    * @return The actual value
    */
   @Getter
   @Transient
   private List<AccessLink> accessLinks;

   /**
    * List of accessPoints filled only after complete() call<br/>
    * (export purpose)
    * 
    * @return The actual value
    */
   @Getter
   @Transient
   private List<AccessPoint> accessPoints;

   /**
    * List of connectionLinks filled only after complete() call<br/>
    * (export purpose)
    * 
    * @return The actual value
    */
   @Getter
   @Transient
   private List<ConnectionLink> connectionLinks;

   /**
    * add a group of line
    * 
    * @param groupOfLine
    */
   public void addGroupOfLine(GroupOfLine groupOfLine)
   {
      if (groupOfLines == null)
         groupOfLines = new ArrayList<GroupOfLine>();
      if (!groupOfLines.contains(groupOfLine))
         groupOfLines.add(groupOfLine);
   }

   /**
    * remove a group of line
    * 
    * @param groupOfLine
    */
   public void removeGroupOfLine(GroupOfLine groupOfLine)
   {
      if (groupOfLines == null)
         groupOfLines = new ArrayList<GroupOfLine>();
      if (groupOfLines.contains(groupOfLine))
         groupOfLines.remove(groupOfLine);
   }

   /**
    * add a group of line id
    * 
    * @param groupOfLineId
    */
   public void addGroupOfLineId(String groupOfLineId)
   {
      if (groupOfLineIds == null)
         groupOfLineIds = new ArrayList<String>();
      if (!groupOfLineIds.contains(groupOfLineId))
         groupOfLineIds.add(groupOfLineId);
   }

   /**
    * add a facility
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
    * add a userNeed value in userNeeds collection if not already present <br/>
    * intUserNeeds will be automatically synchronized 
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
      sb.append("\n").append(indent).append("  ptNetworkIdShortcut = ")
      .append(ptNetworkIdShortcut);
      sb.append("\n").append(indent).append("  number = ").append(number);
      sb.append("\n").append(indent).append("  publishedName = ")
      .append(publishedName);
      sb.append("\n").append(indent).append("  transportModeName = ")
      .append(transportModeName);
      sb.append("\n").append(indent).append("  registrationNumber = ")
      .append(registrationNumber);
      sb.append("\n").append(indent).append("  comment = ").append(comment);
      sb.append("\n").append(indent).append("  mobilityRestrictedSuitable = ")
      .append(mobilityRestrictedSuitable);
      sb.append("\n").append(indent).append("  routes count = ")
      .append(routes == null ? 0 : routes.size());
      sb.append("\n").append(indent).append("  routingConstraints count= ")
      .append(routingConstraints == null ? 0 : routingConstraints.size());
      if (lineEnds != null)
      {
         sb.append("\n").append(indent).append(CHILD_ARROW).append("lineEnds");
         for (String lineEnd : getLineEnds())
         {
            sb.append("\n").append(indent).append(CHILD_LIST_ARROW)
            .append(lineEnd);
         }
      }
      if (routeIds != null)
      {
         sb.append("\n").append(indent).append(CHILD_ARROW).append("routeIds");
         for (String routeid : getRouteIds())
         {
            sb.append("\n").append(indent).append(CHILD_LIST_ARROW)
            .append(routeid);
         }
      }
      if (userNeeds != null)
      {
         sb.append("\n").append(indent).append(CHILD_ARROW).append("userNeeds");
         for (UserNeedEnum userNeed : getUserNeeds())
         {
            sb.append("\n").append(indent).append(CHILD_LIST_ARROW)
            .append(userNeed);
         }
      }

      if (routingConstraintIds != null)
      {
         sb.append("\n").append(indent).append(CHILD_ARROW)
         .append("routingConstraintIds");
         for (String routingConstraintId : getRoutingConstraintIds())
         {
            sb.append("\n").append(indent).append(CHILD_LIST_ARROW)
            .append(routingConstraintId);
         }
      }

      if (level > 0)
      {
         int childLevel = level - 1;
         String childIndent = indent + CHILD_INDENT;
         if (getPtNetwork() != null)
         {
            sb.append("\n").append(indent).append(CHILD_ARROW)
            .append(ptNetwork.toString(childIndent, 0));
         }
         if (getCompany() != null)
         {
            sb.append("\n").append(indent).append(CHILD_ARROW)
            .append(company.toString(childIndent, 0));
         }
         childIndent = indent + CHILD_LIST_INDENT;
         if (getRoutes() != null)
         {
            sb.append("\n").append(indent).append(CHILD_ARROW).append("routes");
            for (Route route : getRoutes())
            {
               sb.append("\n").append(indent).append(CHILD_LIST_ARROW)
               .append(route.toString(childIndent, childLevel));
            }
         }
         if (routingConstraints != null)
         {
            sb.append("\n").append(indent).append(CHILD_ARROW)
            .append("routingConstraints");
            for (StopArea routing : getRoutingConstraints())
            {
               sb.append("\n").append(indent).append(CHILD_LIST_ARROW)
               .append(routing.toString(childIndent, childLevel));
            }
         }

      }

      return sb.toString();
   }

   /**
    * add a route to the line
    * 
    * @param route
    *           the route to be added
    */
   public void addRoute(Route route)
   {
      if (routes == null)
         routes = new ArrayList<Route>();
      if (routes.contains(route))
         return;
      if (route != null)
      {
         routes.add(route);
         route.setLine(this);
      }
   }

   /**
    * remove a route from the line
    * 
    * @param index
    *           of the route to be removed
    */
   public void removeRoute(int index)
   {
      if (routes == null)
         routes = new ArrayList<Route>();
      if (index < routes.size())
      {
         Route deleted = routes.remove(index);
         if (deleted != null)
            removeRouteId(deleted.getObjectId());
      }
   }

   /**
    * remove a route from the line
    * 
    * @param route
    *           the route to be removed
    */
   public void removeRoute(Route route)
   {
      if (routes == null)
         routes = new ArrayList<Route>();
      if (routes.remove(route))
      {
         removeRouteId(route.getObjectId());
      }
   }

   /**
    * remove a route from the line
    * 
    * @param routeId
    *           the route objectId to be removed
    */
   public void removeRoute(String routeId)
   {
      if (routes == null)
         routes = new ArrayList<Route>();
      for (Iterator<Route> iterator = routes.iterator(); iterator.hasNext();)
      {
         Route route = iterator.next();
         if (routeId.equals(route.getObjectId()))
         {
            removeRouteId(routeId);
            iterator.remove();
            break;
         }
      }
   }

   /**
    * add a routeid to the line
    * 
    * @param routeId
    *           the routeId to add
    */
   public void addRouteId(String routeId)
   {
      if (routeIds == null)
         routeIds = new ArrayList<String>();
      routeIds.add(routeId);
   }

   /**
    * remove a routeid to the line
    * 
    * @param routeId
    *           the routeId to add
    */
   public void removeRouteId(String routeId)
   {
      if (routeIds == null)
         routeIds = new ArrayList<String>();
      routeIds.remove(routeId);
   }

   /**
    * add a lienEndid to the line
    * 
    * @param lineEndId
    */
   public void addLineEnd(String lineEndId)
   {
      if (lineEnds == null)
         lineEnds = new ArrayList<String>();
      lineEnds.add(lineEndId);
   }

   /**
    * add a routing constraint
    * 
    * @param routingConstraint
    */
   public void addRoutingConstraint(StopArea routingConstraint)
   {
      if (routingConstraints == null)
         routingConstraints = new ArrayList<StopArea>();
      if (!routingConstraint.getAreaType().equals(ChouetteAreaEnum.ITL))
      {
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
   public void removeRoutingConstraint(StopArea routingConstraint)
   {
      if (routingConstraints == null)
         routingConstraints = new ArrayList<StopArea>();
      if (routingConstraint != null
            && routingConstraints.contains(routingConstraint))
         routingConstraints.remove(routingConstraint);

   }

   /**
    * add a routing constraint id
    * 
    * @param routingConstraintId
    */
   public void addRoutingConstraintId(String routingConstraintId)
   {
      if (routingConstraintIds == null)
         routingConstraintIds = new ArrayList<String>();
      if (routingConstraintId != null
            && !routingConstraintIds.contains(routingConstraintId))
         routingConstraintIds.add(routingConstraintId);

   }

   /**
    * remove a routing constraint id
    * 
    * @param routingConstraintId
    */
   public void removeRoutingConstraintId(String routingConstraintId)
   {
      if (routingConstraintIds == null)
         routingConstraintIds = new ArrayList<String>();
      if (routingConstraintId != null
            && routingConstraintIds.contains(routingConstraintId))
         routingConstraintIds.remove(routingConstraintId);

   }

   /**
    * return lineEndList built with PTLink relationship
    * <p/>
    * This method does not refresh lineEnds
    */
   public List<StopPoint> getLineEndList()
   {
      List<StopPoint> stopPoints = new ArrayList<StopPoint>();
      if (routes != null)
      {
         for (Route route : routes)
         {
            if (route.getPtLinks() != null)
            {
               Set<String> startStopPoints = new HashSet<String>();
               Set<String> endStopPoints = new HashSet<String>();
               for (PTLink link : route.getPtLinks())
               {
                  if (link.getStartOfLink() != null)
                     startStopPoints.add(link.getStartOfLink().getObjectId());
                  if (link.getEndOfLink() != null)
                     endStopPoints.add(link.getEndOfLink().getObjectId());
               }
               for (PTLink link : route.getPtLinks())
               {
                  StopPoint start = link.getStartOfLink();
                  if (start != null)
                  {
                     if (!endStopPoints.contains(start.getObjectId()))
                     {
                        stopPoints.add(start);
                     }
                  }
                  StopPoint end = link.getStartOfLink();
                  if (end != null)
                  {
                     if (!startStopPoints.contains(end.getObjectId()))
                     {
                        stopPoints.add(end);
                     }
                  }
               }
            }
         }
      }
      return stopPoints;
   }

   /**
    * return stopPointList built with JourneyPattern relationship
    * <p/>
    * This method does not refresh anything
    */
   public List<StopPoint> getStopPointList()
   {
      Set<StopPoint> stopPoints = new HashSet<StopPoint>();
      if (routes != null)
      {
         for (Route route : routes)
         {
            if (route.getJourneyPatterns() != null)
            {
               for (JourneyPattern jp : route.getJourneyPatterns())
               {
                  if (jp.getStopPoints() != null)
                  {
                     stopPoints.addAll(jp.getStopPoints());
                  }
               }
            }
         }
      }
      return Arrays.asList(stopPoints.toArray(new StopPoint[0]));
   }

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#clean()
    */
   @Override
   public boolean clean()
   {
      if (routes == null)
      {
         return false;
      }
      for (Iterator<Route> iterator = routes.iterator(); iterator.hasNext();)
      {
         Route route = iterator.next();
         if (route == null || !route.clean())
         {
            iterator.remove();
         }
      }
      if (routes.isEmpty())
      {
         return false;
      }
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#complete()
    */
   @Override
   public synchronized void complete()
   {
      if (isCompleted())
         return;
      super.complete();

      journeyPatterns = new ArrayList<JourneyPattern>();
      vehicleJourneys = new ArrayList<VehicleJourney>();
      timetables = new ArrayList<Timetable>();
      stopPoints = new ArrayList<StopPoint>();
      stopAreas = new ArrayList<StopArea>();
      boardingPositions = new ArrayList<StopArea>();
      quays = new ArrayList<StopArea>();
      commercialStopPoints = new ArrayList<StopArea>();
      stopPlaces = new ArrayList<StopArea>();
      accessLinks = new ArrayList<AccessLink>();
      accessPoints = new ArrayList<AccessPoint>();
      connectionLinks = new ArrayList<ConnectionLink>();

      Set<Timetable> timetableSet = new HashSet<Timetable>();
      Set<StopArea> stopAreaSet = new HashSet<StopArea>();
      Set<AccessPoint> accessPointSet = new HashSet<AccessPoint>();
      Set<ConnectionLink> connectionLinkSet = new HashSet<ConnectionLink>();

      // ptNetworkShortcut
      PTNetwork ptNetwork = getPtNetwork();
      if (ptNetwork != null)
      {
         setPtNetworkIdShortcut(ptNetwork.getObjectId());
      }

      // groupOfLine
      if (getGroupOfLines() != null)
      {
         for (GroupOfLine groupOfLine : getGroupOfLines())
         {
            addGroupOfLineId(groupOfLine.getObjectId());
            groupOfLine.addLineId(getObjectId());
         }

      }

      // lineEndIds, journeys and stopPoints
      List<Route> routes = getRoutes();
      if (routes != null)
      {
         for (Route route : routes)
         {
            route.complete();
            if (route.getPtLinks() != null)
            {
               Set<String> startStopPoints = new HashSet<String>();
               Set<String> endStopPoints = new HashSet<String>();
               for (PTLink link : route.getPtLinks())
               {
                  if (link.getStartOfLink() != null)
                     startStopPoints.add(link.getStartOfLink().getObjectId());
                  if (link.getEndOfLink() != null)
                     endStopPoints.add(link.getEndOfLink().getObjectId());
               }
               for (PTLink link : route.getPtLinks())
               {
                  StopPoint start = link.getStartOfLink();
                  if (start != null)
                  {
                     if (!endStopPoints.contains(start.getObjectId()))
                     {
                        addLineEnd(start.getObjectId());
                     }
                  }
                  StopPoint end = link.getEndOfLink();
                  if (end != null)
                  {
                     if (!startStopPoints.contains(end.getObjectId()))
                     {
                        addLineEnd(end.getObjectId());
                     }
                  }
               }
            }
            if (route.getJourneyPatterns() != null)
               journeyPatterns.addAll(route.getJourneyPatterns());
            if (route.getStopPoints() != null)
               stopPoints.addAll(route.getStopPoints());
         }
      }
      // collect vehiclejourneys
      for (JourneyPattern jp : journeyPatterns)
      {
         if (jp.getVehicleJourneys() != null)
         {
            vehicleJourneys.addAll(jp.getVehicleJourneys());
         }
      }
      // collect timetables
      for (VehicleJourney vj : vehicleJourneys)
      {
         if (vj.getTimetables() != null)
         {
            timetableSet.addAll(vj.getTimetables());
         }
      }
      timetables.addAll(timetableSet);
      // collect stopareas
      for (StopPoint sp : stopPoints)
      {
         sp.getContainedInStopArea().addContainedStopPoint(sp);
         stopAreaSet.addAll(extractStopAreaHierarchy(sp
               .getContainedInStopArea()));
      }

      // add routing constraints
      if (routingConstraints != null)
      {
         stopAreaSet.addAll(routingConstraints);
      }

      stopAreas.addAll(stopAreaSet);
      // sort stopArea and collect connectionLinks and accessLinks+points.
      for (StopArea area : stopAreaSet)
      {
         switch (area.getAreaType())
         {
         case BoardingPosition:
            boardingPositions.add(area);
            break;
         case Quay:
            quays.add(area);
            break;
         case CommercialStopPoint:
            commercialStopPoints.add(area);
            break;
         case StopPlace:
            stopPlaces.add(area);
            break;
         default:
            break;
         }
         if (area.getAccessLinks() != null)
            accessLinks.addAll(area.getAccessLinks());
         if (area.getConnectionLinks() != null)
            connectionLinkSet.addAll(area.getConnectionLinks());
      }
      connectionLinks.addAll(connectionLinkSet);
      // collect accessPoints
      for (AccessLink al : accessLinks)
      {
         accessPointSet.add(al.getAccessPoint());
      }
      accessPoints.addAll(accessPointSet);
   }

   /**
    * extract parent tree for physical Stop
    * 
    * @param stopArea
    *           physical stop to check
    * @param line
    *           line for routingConstraint relationship
    * @return stopareas
    */
   private List<StopArea> extractStopAreaHierarchy(StopArea stopArea)
   {
      List<StopArea> stopAreas = new ArrayList<StopArea>();
      if (stopArea != null)
      {
         if (!stopArea.getAreaType().equals(ChouetteAreaEnum.ITL))
         {
            stopAreas.add(stopArea);
            if (stopArea.getParent() != null)
            {
               stopArea.getParent().addContainedStopArea(stopArea);
               stopAreas.addAll(extractStopAreaHierarchy(stopArea.getParent()));
            }
         }
      }
      return stopAreas;
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.model.neptune.NeptuneObject#compareAttributes(fr.certu.chouette.model.neptune.NeptuneObject)
    */
   @Override
   public <T extends NeptuneObject> boolean compareAttributes(T anotherObject)
   {
      if (anotherObject instanceof Line)
      {
         Line another = (Line) anotherObject;
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
         if (!sameValue(this.getMobilityRestrictedSuitable(),
               another.getMobilityRestrictedSuitable()))
            return false;
         if (!sameValue(this.getNumber(), another.getNumber()))
            return false;
         if (!sameValue(this.getPublishedName(), another.getPublishedName()))
            return false;
         if (!sameValue(this.getRegistrationNumber(),
               another.getRegistrationNumber()))
            return false;
         if (!sameValue(this.getTransportModeName(),
               another.getTransportModeName()))
            return false;
         return true;
      } else
      {
         return false;
      }
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#toURL()
    */
   @Override
   public String toURL()
   {
      return "lines/" + getId();
   }

}
