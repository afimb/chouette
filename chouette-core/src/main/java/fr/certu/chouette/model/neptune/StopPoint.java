package fr.certu.chouette.model.neptune;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.NoArgsConstructor;
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

@Entity
@Table(name = "stop_points")
@NoArgsConstructor
public class StopPoint extends NeptuneIdentifiedObject
{
   private static final long serialVersionUID = -4913573673645997423L;

   private static final Logger logger = Logger.getLogger(StopPoint.class);

   /**
    * name of comment attribute for {@link Filter} attributeName construction
    */
   public static final String COMMENT = "comment";

//   @Getter
//   @Setter
//   @Column(name = "comment")
//   private String comment;

   @Getter
   @Setter
   @Column(name = "position")
   private Integer position;

   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "stop_area_id")
   private StopArea containedInStopArea;

   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "route_id")
   private Route route;

   @Getter
   @Setter
   @Transient
   private String name;

   /**
    * postal Address <br/>
    * (import/export usage) based on parent StopArea's AreaCentroid <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   @Transient
   private Address address;
   /**
    * Spatial Referential Type (actually only WGS84 is valid) <br/>
    * (import/export usage) based on parent StopArea's AreaCentroid <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   @Transient
   private LongLatTypeEnum longLatType;
   /**
    * Latitude position of area <br/>
    * (import/export usage) based on parent StopArea's AreaCentroid <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   @Transient
   private BigDecimal latitude;
   /**
    * Longitude position of area <br/>
    * (import/export usage) based on parent StopArea's AreaCentroid <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   @Transient
   private BigDecimal longitude;
   /**
    * Optional other Spatial Referential position <br/>
    * (import/export usage) based on parent StopArea's AreaCentroid <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   @Transient
   private ProjectedPoint projectedPoint;

   /**
    * Neptune ObjectId for StopArea Container <br/>
    * (import/export usage) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   @Transient
   private String containedInStopAreaId;

   /**
    * Neptune ObjectId for line <br/>
    * (import/export usage) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   @Transient
   private String lineIdShortcut;
   /**
    * Line <br/>
    * (import/export usage) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   @Transient
   private Line line;
   /**
    * Neptune ObjectId for PTNetwork <br/>
    * (import/export usage) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   @Transient
   private String ptNetworkIdShortcut;
   /**
    * PTNetwork <br/>
    * (import/export usage) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   @Transient
   private PTNetwork ptNetwork;

   /**
    * Facility affected to this stopPoint <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   @Transient
   private List<Facility> facilities;

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

   //   sb.append("\n").append(indent).append("  comment = ").append(comment);
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

   public boolean before(StopPoint another)
   {
      return position < another.getPosition();
   }

   public boolean after(StopPoint another)
   {
      return position > another.getPosition();
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

   @Override
   public <T extends NeptuneObject> boolean compareAttributes(
         T anotherObject)
   {
      if (anotherObject instanceof StopPoint)
      {
         StopPoint another = (StopPoint) anotherObject;
         if (!sameValue(this.getObjectId(), another.getObjectId()))
            return false;
         if (!sameValue(this.getObjectVersion(), another.getObjectVersion()))
            return false;
         if (!sameValue(this.getName(), another.getName()))
            return false;
//         if (!sameValue(this.getComment(), another.getComment()))
//            return false;
         if (!sameValue(this.getRegistrationNumber(), another.getRegistrationNumber()))
            return false;
         if (!sameValue(this.getLatitude(), another.getLatitude()))
            return false;
         if (!sameValue(this.getLongitude(), another.getLongitude()))
            return false;
         if (!sameValue(this.getLongLatType(), another.getLongLatType()))
            return false;
         if (!sameValue(this.getAddress(), another.getAddress()))
            return false;
         if (!sameValue(this.getProjectedPoint(), another.getProjectedPoint()))
            return false;

         if (!sameValue(this.getPosition(), another.getPosition()))
            return false;
         return true;
      }
      else
      {
         return false;
      }
   }

   @Override
   public String toURL()
   {
      return null;
   }

}
