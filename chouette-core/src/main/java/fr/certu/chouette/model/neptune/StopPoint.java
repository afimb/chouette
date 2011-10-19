package fr.certu.chouette.model.neptune;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;

/**
 * Neptune StopPoint : a StopPoint on a route
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 */
public class StopPoint extends NeptuneIdentifiedObject
{
   private static final long   serialVersionUID = -4913573673645997423L;

   private static final Logger logger           = Logger.getLogger(StopPoint.class);

   // TODO constant for persistence fields
   /**
    * name of comment attribute for {@link Filter} attributeName construction
    */
   public static final String    COMMENT                    = "comment";

   
   /**
    * postal Address <br/>
    * (import/export usage) based on parent StopArea's AreaCentroid <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private Address             address;
   /**
    * Spatial Referential Type (actually only WGS84 is valid) <br/>
    * (import/export usage) based on parent StopArea's AreaCentroid <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private LongLatTypeEnum     longLatType;
   /**
    * Latitude position of area <br/>
    * (import/export usage) based on parent StopArea's AreaCentroid <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private BigDecimal          latitude;
   /**
    * Longitude position of area <br/>
    * (import/export usage) based on parent StopArea's AreaCentroid <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private BigDecimal          longitude;
   /**
    * Optional other Spatial Referential position <br/>
    * (import/export usage) based on parent StopArea's AreaCentroid <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private ProjectedPoint      projectedPoint;
   /**
    * Comment <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String              comment;
   /**
    * Neptune ObjectId for StopArea Container <br/>
    * (import/export usage) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String              containedInStopAreaId;
   /**
    * StopArea Container <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private StopArea            containedInStopArea;
   /**
    * Neptune ObjectId for line <br/>
    * (import/export usage) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String              lineIdShortcut;
   /**
    * Line <br/>
    * (import/export usage) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private Line                line;
   /**
    * Neptune ObjectId for PTNetwork <br/>
    * (import/export usage) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String              ptNetworkIdShortcut;
   /**
    * PTNetwork <br/>
    * (import/export usage) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private PTNetwork           ptNetwork;
   /**
    * rank of stop point in route <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private int                 position;
   /**
    * Route <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private Route               route;
   /**
    * Facility affected to this stopPoint <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private List<Facility>      facilities;

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

      if (address != null)
      {
         sb.append("\n").append(indent).append("  address = ").append(address);
      }

      if (longLatType != null)
      {
         sb.append("\n").append(indent).append("  longLatType = ").append(longLatType);
      }

      sb.append("\n").append(indent).append("  latitude = ").append(latitude);
      sb.append("\n").append(indent).append("  longitude = ").append(longitude);

      if (projectedPoint != null)
      {
         sb.append("\n").append(indent).append("  projectedPoint = ").append(projectedPoint);
      }

      sb.append("\n").append(indent).append("  comment = ").append(comment);
      sb.append("\n").append(indent).append("  containedInStopAreaId = ").append(containedInStopAreaId);
      sb.append("\n").append(indent).append("  lineIdShortcut = ").append(lineIdShortcut);
      sb.append("\n").append(indent).append("  ptNetworkIdShortcut = ").append(ptNetworkIdShortcut);

      if (level > 0)
      {
         int childLevel = level - 1;
         String childIndent = indent + CHILD_INDENT;
         if (containedInStopArea != null)
         {
            sb.append("\n").append(indent).append(CHILD_ARROW)
                  .append(containedInStopArea.toString(childIndent, childLevel));
         }
      }

      return sb.toString();
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
      PTNetwork ptNetwork = getPtNetwork();
      if (ptNetwork != null)
         setPtNetworkIdShortcut(ptNetwork.getObjectId());
      Line line = getLine();
      if (line != null)
         setLineIdShortcut(line.getObjectId());
      StopArea area = getContainedInStopArea();
      if (area != null)
      {
         area.complete();
         AreaCentroid centroid = area.getAreaCentroid();
         if (centroid != null)
         {
            setLatitude(centroid.getLatitude());
            setLongitude(centroid.getLongitude());
            setLongLatType(centroid.getLongLatType());
            setProjectedPoint(centroid.getProjectedPoint());
            setAddress(centroid.getAddress());
         }
         else
         {
            logger.error("stopPoint " + getObjectId() + " has an area without centroid " + area.getObjectId());
         }
         setName(area.getName());
      }
   }
}
