/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.model.neptune;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.type.PTDirectionEnum;

/**
 * Neptune Route
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 */
public class Route extends NeptuneIdentifiedObject
{
   private static final long    serialVersionUID = -2249654966081042738L;
   // constant for persistence fields
   /**
    * name of comment attribute for {@link Filter} attributeName construction
    */
   public static final String    COMMENT                    = "comment";
   /**
    * name of oppositeRouteId attribute for {@link Filter} attributeName construction
    */
   public static final String    OPPOSITE_ROUTE_ID           = "oppositeRouteId";
   /**
    * name of line attribute for {@link Filter} attributeName construction
    */
   public static final String    LINE           = "line";
   /**
    * name of number attribute for {@link Filter} attributeName construction
    */
   public static final String    NUMBER           = "number";
   /**
    * name of publishedName attribute for {@link Filter} attributeName construction
    */
   public static final String    PUBLISHED_NAME           = "publishedName";
   /**
    * name of direction attribute for {@link Filter} attributeName construction
    */
   public static final String    DIRECTION           = "direction";
   /**
    * name of wayBack attribute for {@link Filter} attributeName construction
    */
   public static final String    WAYBACK           = "wayBack";

   /**
    * Database foreign key referring to the route's wayback route<br/>
    * Meaningless after import action <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private Long                 oppositeRouteId;                         // FK
   /**
    * Database foreign key referring to the route's line<br/>
    * Meaningless after import action <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private Long                 lineId;                                  // FK

   @Getter
   @Setter
   private Line                 line;
   /**
    * Public name for travelers <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String               publishedName;                           // BD
   /**
    * Number of the route (characters) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String               number;                                  // BD
   /**
    * Direction (geographical, clockwise or logical) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private PTDirectionEnum      direction;                               // BD
   /**
    * Comment <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String               comment;                                 // BD
   /**
    * A logical direction (French extension)
    * <ul>
    * <li>A for Outward (Aller)</li>
    * <li>R for Return (Retour)</li>
    * </ul>
    * <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String               wayBack;                                 // BD
   /**
    * Neptune identification referring to the wayBackRoute of the route<br/>
    * Meaningless after database read (see oppositeRouteId) <br/>
    * Changes have no effect on database <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String               wayBackRouteId;
   /**
    * Neptune identification referring to the JourneyPatterns of the route<br/>
    * Meaningless after database read (see journeyPatterns) <br/>
    * Changes have no effect on database <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private List<String>         journeyPatternIds;
   /**
    * The route's journey patterns objects <br/>
    * <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private List<JourneyPattern> journeyPatterns;                         // FK
   // inverse
   // manquante
   // à
   // ajouter
   /**
    * Neptune identification referring to the PTLinks of the route<br/>
    * Meaningless after database read (see ptLinks) <br/>
    * Changes have no effect on database <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private List<String>         ptLinkIds;
   /**
    * The route's ptLink objects <br/>
    * <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private List<PTLink>         ptLinks;                                 // Table
   // +
   // FK
   // inverse
   /**
    * The route's stopPoints objects <br/>
    * <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private List<StopPoint>      stopPoints;

   public Route()
   {
      journeyPatterns = new ArrayList<JourneyPattern>();
      ptLinks = new ArrayList<PTLink>();
      stopPoints = new ArrayList<StopPoint>();
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
      sb.append("\n").append(indent).append("oppositeRouteId = ").append(oppositeRouteId);
      sb.append("\n").append(indent).append("lineId = ").append(lineId);
      sb.append("\n").append(indent).append("publishedName = ").append(publishedName);
      sb.append("\n").append(indent).append("number = ").append(number);
      sb.append("\n").append(indent).append("direction = ").append(direction);
      sb.append("\n").append(indent).append("comment = ").append(comment);
      sb.append("\n").append(indent).append("wayBack = ").append(wayBack);

      if (journeyPatternIds != null)
      {
         sb.append("\n").append(indent).append(CHILD_ARROW).append("journeyPatternIds");
         for (String journeyPatternId : journeyPatternIds)
         {
            sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(journeyPatternId);
         }
      }
      if (ptLinkIds != null)
      {
         sb.append("\n").append(indent).append(CHILD_ARROW).append("ptLinkIds");
         for (String ptLinkid : ptLinkIds)
         {
            sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(ptLinkid);
         }
      }
      if (level > 0)
      {
         int childLevel = level - 1;
         String childIndent = indent + CHILD_LIST_INDENT;
         if (journeyPatterns != null)
         {
            sb.append("\n").append(indent).append(CHILD_ARROW).append("journey patterns");
            for (JourneyPattern journeyPattern : journeyPatterns)
            {
               sb.append("\n").append(indent).append(CHILD_LIST_ARROW)
               .append(journeyPattern.toString(childIndent, childLevel));
            }
         }
         if (ptLinks != null)
         {
            sb.append("\n").append(indent).append(CHILD_ARROW).append("pt links");
            for (PTLink ptLink : ptLinks)
            {
               sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(ptLink.toString(childIndent, childLevel));
            }
         }
         if (stopPoints != null)
         {
            sb.append("\n").append(indent).append(CHILD_ARROW).append("stopPoints");
            for (StopPoint stopPoint : stopPoints)
            {
               if (stopPoint != null)
                  sb.append("\n").append(indent).append(CHILD_LIST_ARROW)
                  .append(stopPoint.toString(childIndent, childLevel));
               else
                  sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append("null stopPoint !!");

            }
         }
      }

      return sb.toString();
   }

   /**
    * add a journeyPatternId to list only if not already present
    * 
    * @param journeyPatternId
    */
   public void addJourneyPatternId(String journeyPatternId)
   {
      if (journeyPatternIds == null)
         journeyPatternIds = new ArrayList<String>();
      if (!journeyPatternIds.contains(journeyPatternId))
         journeyPatternIds.add(journeyPatternId);
   }

   /**
    * add a journeyPattern to list only if not already present
    * 
    * @param journeyPattern
    */
   public void addJourneyPattern(JourneyPattern journeyPattern)
   {
      if (journeyPatterns == null)
         journeyPatterns = new ArrayList<JourneyPattern>();
      if (!journeyPatterns.contains(journeyPattern))
         journeyPatterns.add(journeyPattern);
   }

   /**
    * add a ptLinkId to list only if not already present
    * 
    * @param ptLinkId
    */
   public void addPTLinkId(String ptLinkId)
   {
      if (ptLinkIds == null)
         ptLinkIds = new ArrayList<String>();
      if (!ptLinkIds.contains(ptLinkId))
         ptLinkIds.add(ptLinkId);
   }

   /**
    * add a ptLink to list only if not already present
    * 
    * @param ptLink
    */
   public void addPTLink(PTLink ptLink)
   {
      if (ptLinks == null)
         ptLinks = new ArrayList<PTLink>();
      if (!ptLinks.contains(ptLink))
         ptLinks.add(ptLink);
   }

   /**
    * remove a journeyPatternId from list if present
    * 
    * @param journeyPatternId
    */
   public void removeJourneyPatternId(String journeyPatternId)
   {
      if (journeyPatternIds == null)
         journeyPatternIds = new ArrayList<String>();
      if (journeyPatternIds.contains(journeyPatternId))
         journeyPatternIds.remove(journeyPatternId);
   }

   /**
    * remove a journeyPattern from list if present
    * 
    * @param journeyPattern
    */
   public void removeJourneyPattern(JourneyPattern journeyPattern)
   {
      if (journeyPatterns == null)
         journeyPatterns = new ArrayList<JourneyPattern>();
      if (journeyPatterns.contains(journeyPattern))
         journeyPatterns.remove(journeyPattern);
   }

   /**
    * remove a ptLinkId from list if present
    * 
    * @param ptLinkId
    */
   public void removePTLinkId(String ptLinkId)
   {
      if (ptLinkIds == null)
         ptLinkIds = new ArrayList<String>();
      if (ptLinkIds.contains(ptLinkId))
         ptLinkIds.remove(ptLinkId);

   }

   /**
    * remove a ptLink from list if present
    * 
    * @param ptLink
    */
   public void removePTLink(PTLink ptLink)
   {
      if (ptLinks == null)
         ptLinks = new ArrayList<PTLink>();
      if (ptLinks.contains(ptLink))
         ptLinks.remove(ptLink);
   }

   /**
    * add a stopPoint at end of route sequence
    * 
    * @param stopPoint
    */
   public void addStopPoint(StopPoint stopPoint)
   {
      if (stopPoints == null)
         stopPoints = new ArrayList<StopPoint>();
      if (stopPoint != null && !stopPoints.contains(stopPoint))
      {
         int pos = stopPoints.size();
         stopPoints.add(stopPoint);
         stopPoint.setPosition(pos);
         rebuildPTLinks();
      }
   }

   /**
    * remove stoppoint for route
    * 
    * @param stopPoint
    *           stoppoint to be removed
    */
   public void removeStopPoint(StopPoint stopPoint)
   {
      if (stopPoint == null)
         return;
      if (stopPoints == null)
         stopPoints = new ArrayList<StopPoint>();
      if (stopPoints.contains(stopPoint))
      {
         int position = stopPoints.indexOf(stopPoint);
         removeJourneyPatternsStopPoint(stopPoint);
         stopPoints.remove(stopPoint);
         stopPoint.setRoute(null);
         repositionStopPoints(position);
         rebuildPTLinks();
      }
   }

   /**
    * remove stoppoint for route
    * 
    * @param position
    *           position (rank) of stoppoint to be removed
    */
   public void removeStopPointAt(int position)
   {
      if (stopPoints == null)
         stopPoints = new ArrayList<StopPoint>();
      if (stopPoints.size() > position)
      {
         removeStopPoint(stopPoints.get(position));
      }
      return;
   }

   /**
    * apply stoppoint deletion on journeyPatterns
    * 
    * @param stopPoint
    *           stopPoint to remove
    */
   private void removeJourneyPatternsStopPoint(StopPoint stopPoint)
   {
      if (getJourneyPatterns() == null)
         return;
      for (JourneyPattern journeyPattern : getJourneyPatterns())
      {
         if (journeyPattern.getStopPoints() != null)
         {
            List<StopPoint> jpPoints = journeyPattern.getStopPoints();

            if (jpPoints.contains(stopPoint))
            {
               for (VehicleJourney vehicleJourney : journeyPattern.getVehicleJourneys())
               {
                  vehicleJourney.removeStopPoint(stopPoint);
               }
            }
            jpPoints.remove(stopPoint);
         }
      }

   }

   /**
    * swap stoppoints position on route
    * 
    * @param stopPoint1
    *           first stoppoint to swap
    * @param stopPoint2
    *           second stoppoint to swap
    */
   public void swapStopPoints(StopPoint stopPoint1, StopPoint stopPoint2)
   {
      if (stopPoint1.equals(stopPoint2))
         return;
      if (stopPoints == null)
         stopPoints = new ArrayList<StopPoint>();
      int pos1 = stopPoints.indexOf(stopPoint1);
      int pos2 = stopPoints.indexOf(stopPoint2);
      if (pos1 >= 0 && pos2 >= 0)
      {
         stopPoints.set(pos1, stopPoint2);
         stopPoints.set(pos2, stopPoint1);
         stopPoint1.setPosition(pos2);
         stopPoint2.setPosition(pos1);
         rebuildPTLinks();
         refreshJourneyPatternsStopPoint(stopPoint1, stopPoint2);
      }
   }

   /**
    * swap stoppoints position on route
    * 
    * @param pos1
    *           first position to swap
    * @param pos2
    *           second position to swap
    */
   public void swapStopPoints(int pos1, int pos2)
   {
      if (stopPoints == null)
         stopPoints = new ArrayList<StopPoint>();
      if (pos1 >= 0 && pos2 >= 0 && pos1 < stopPoints.size() && pos2 < stopPoints.size() && pos1 != pos2)
      {
         StopPoint stopPoint1 = stopPoints.get(pos1);
         StopPoint stopPoint2 = stopPoints.get(pos2);
         swapStopPoints(stopPoint1, stopPoint2);
      }
   }

   /**
    * refresh order for every vehiclejourneys in journeyPattern concerned by
    * swaping stoppoint positions
    * 
    * @param stopPoints
    *           stoppoint previously swapped
    */
   private void refreshJourneyPatternsStopPoint(StopPoint... stopPoints)
   {
      if (getJourneyPatterns() == null)
         return;
      for (JourneyPattern journeyPattern : getJourneyPatterns())
      {
         if (journeyPattern.getStopPoints() != null)
         {
            List<StopPoint> jpPoints = journeyPattern.getStopPoints();

            for (StopPoint stopPoint : jpPoints)
            {
               if (jpPoints.contains(stopPoint))
               {
                  for (VehicleJourney vehicleJourney : journeyPattern.getVehicleJourneys())
                  {
                     vehicleJourney.sortVehicleJourneyAtStops();
                  }
                  break;
               }
            }
         }
      }

   }

   /**
    * insert a stopPoint in route at a specific position
    * 
    * @param position
    *           position for the stoppoint
    * @param stopPoint
    *           stpopoint to be inserted
    */
   public void addStopPointAt(int position, StopPoint stopPoint)
   {
      if (stopPoints == null)
         stopPoints = new ArrayList<StopPoint>();
      if (stopPoint == null || stopPoints.contains(stopPoint))
         return;
      if (position >= stopPoints.size())
      {
         stopPoint.setPosition(stopPoints.size());
         stopPoints.add(stopPoint);
      }
      else
      {
         stopPoint.setPosition(position);
         stopPoints.add(position, stopPoint);
         for (int i = position + 1; i < stopPoints.size(); i++)
         {
            StopPoint point = stopPoints.get(i);
            point.setPosition(i);
         }
      }
      repositionStopPoints(position);
      rebuildPTLinks();
   }

   /**
    * refresh position attribute for every stoppoint in route
    * 
    * @param start
    *           first position to refresh
    */
   private void repositionStopPoints(int start)
   {
      for (int i = start; i < stopPoints.size(); i++)
      {
         stopPoints.get(i).setPosition(i);
      }
   }

   /**
    * rebuild PTLink when stoppoints have changed (insert, delete or swap)
    */
   public void rebuildPTLinks()
   {
      if (ptLinks == null)
         ptLinks = new ArrayList<PTLink>();
      Map<String, PTLink> linkBySEId = new HashMap<String, PTLink>();
      for (PTLink link : ptLinks)
      {
         linkBySEId.put(link.getStartOfLink().getObjectId() + "@" + link.getEndOfLink().getObjectId(), link);
      }
      ptLinks.clear();
      String baseId = this.getObjectId().split(":")[0] + ":" + NeptuneIdentifiedObject.PTLINK_KEY + ":";
      for (int rank = 1; rank < stopPoints.size(); rank++)
      {
         StopPoint start = stopPoints.get(rank - 1);
         StopPoint end = stopPoints.get(rank);
         PTLink link = linkBySEId.remove(start.getObjectId() + "@" + end.getObjectId());
         if (link == null)
         {
            link = new PTLink();
            link.setStartOfLink(start);
            link.setEndOfLink(end);
            String startId = start.getObjectId().split(":")[2];
            String endId = end.getObjectId().split(":")[2];
            String objectId = baseId + startId + "A" + endId;
            link.setObjectId(objectId);
            link.setCreationTime(new Date());
            link.setRoute(this);
         }
         this.addPTLink(link);
      }
      for (PTLink link : linkBySEId.values())
      {
         link.setRoute(null); // for deletion
         link.setStartOfLink(null);
         link.setEndOfLink(null);
      }

   }

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#clean()
    */
   @Override
   public boolean clean()
   {
      if (journeyPatterns == null)
      {
         return false;
      }
      for (Iterator<JourneyPattern> iterator = journeyPatterns.iterator(); iterator.hasNext();)
      {
         JourneyPattern journeyPattern = iterator.next();
         if (journeyPattern == null || !journeyPattern.clean())
         {
            iterator.remove();
         }
      }
      if (journeyPatterns.isEmpty())
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
   public void complete()
   {
      if (isCompleted())
         return;
      super.complete();
      if (getId() != null)
      {
         wayBackRouteId=null;
         if (oppositeRouteId != null)
         {
            for (Route waybackRoute : line.getRoutes())
            {
               if (waybackRoute.getId().equals(oppositeRouteId))
               {
                  wayBackRouteId=waybackRoute.getObjectId();
                  break;
               }
            }
         }
      }

      List<StopPoint> stopPoints = getStopPoints();
      if (stopPoints != null && !stopPoints.isEmpty())
      {
         // generate PtLinks
         List<PTLink> ptLinks = getPtLinks();
         if (ptLinks == null || ptLinks.isEmpty())
         {
            rebuildPTLinks();
         }
         for (StopPoint stopPoint : stopPoints)
         {
            stopPoint.complete();
         }
      }

      List<PTLink> ptLinks = getPtLinks();
      if (ptLinks != null)
      {
         for (PTLink ptLink : ptLinks)
         {

            addPTLinkId(ptLink.getObjectId());
            ptLink.complete();
         }
      }

      List<JourneyPattern> jps = getJourneyPatterns();
      if (jps != null && !jps.isEmpty())
      {
         for (JourneyPattern journeyPattern : jps)
         {
            journeyPattern.complete();
         }
      }

   }
}
