package fr.certu.chouette.model.neptune;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import fr.certu.chouette.model.neptune.type.AlightingPossibilityEnum;
import fr.certu.chouette.model.neptune.type.BoardingPossibilityEnum;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Chouette StopPoint : a StopPoint on a route
 * <p/>
 * Neptune mapping : StopPoint <br/>
 * Gtfs mapping : none
 */

@Entity
@Table(name = "stop_points")
@NoArgsConstructor
public class StopPoint extends NeptuneIdentifiedObject
{
   private static final long serialVersionUID = -4913573673645997423L;

   /**
    * position on the route
    * 
    * @param position
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Column(name = "position")
   private Integer position;

   /**
    * boarding possibility
    * 
    * @param forBoarding
    *           New value
    * @return The actual value
    * 
    * @since 2.5.2
    */
   @Getter
   @Setter
   @Enumerated(EnumType.STRING)
   @Column(name = "for_boarding")
   private BoardingPossibilityEnum forBoarding;

   /**
    * alighting possibility
    * 
    * @param forAlighting
    *           New value
    * @return The actual value
    * 
    * @since 2.5.2
    */
   @Getter
   @Setter
   @Enumerated(EnumType.STRING)
   @Column(name = "for_alighting")
   private AlightingPossibilityEnum forAlighting;

   /**
    * stop area container
    * 
    * @param containedInStopArea
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "stop_area_id")
   private StopArea containedInStopArea;

   /**
    * route
    * 
    * @param route
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "route_id")
   private Route route;

   /**
    * name<br/>
    * for import/export purpose
    * 
    * @param name
    *           New value
    * @return The actual value
    * 
    */
   @Getter
   @Setter
   @Transient
   private String name;

   /**
    * Neptune ObjectId for StopArea Container <br/>
    * for import/export purpose
    * 
    * @param containedInStopAreaId
    *           New value
    * @return The actual value
    * 
    */
   @Getter
   @Setter
   @Transient
   private String containedInStopAreaId;

   /**
    * Neptune ObjectId for line <br/>
    * for import/export purpose
    * 
    * @param lineIdShortcut
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Transient
   private String lineIdShortcut;
   /**
    * Line <br/>
    * for import/export purpose
    * 
    * @param line
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Transient
   private Line line;
   /**
    * Neptune ObjectId for PTNetwork <br/>
    * for import/export purpose
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
    * PTNetwork <br/>
    * for import/export purpose
    * 
    * @param ptNetwork
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Transient
   private PTNetwork ptNetwork;

   /**
    * Facility affected to this stopPoint <br/>
    * for import purpose
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

   /* (non-Javadoc)
    * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#toString(java.lang.String, int)
    */
   @Override
   public String toString(String indent, int level)
   {
      StringBuilder sb = new StringBuilder(super.toString(indent, level));

      // sb.append("\n").append(indent).append("  comment = ").append(comment);
      sb.append("\n").append(indent).append("  containedInStopAreaId = ").append(containedInStopAreaId);
      sb.append("\n").append(indent).append("  lineIdShortcut = ").append(lineIdShortcut);
      sb.append("\n").append(indent).append("  ptNetworkIdShortcut = ").append(ptNetworkIdShortcut);

      if (level > 0)
      {
         int childLevel = level - 1;
         String childIndent = indent + CHILD_INDENT;
         if (containedInStopArea != null)
         {
            sb.append("\n").append(indent).append(CHILD_ARROW).append(containedInStopArea.toString(childIndent, childLevel));
         }
      }

      return sb.toString();
   }

   /**
    * check relative position with another stop point<br/>
    * assume both stop points are on the same route (not checked)
    * 
    * @param another stop point
    * @return true if position if less than another position
    */
   public boolean before(StopPoint another)
   {
      return position < another.getPosition();
   }

   /**
    * check relative position with another stop point<br/>
    * assume both stop points are on the same route (not checked)
    * 
    * @param another stop point
    * @return true if position if greater than another position
    */
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
         setName(area.getName());
      }
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.model.neptune.NeptuneObject#compareAttributes(fr.certu.chouette.model.neptune.NeptuneObject)
    */
   @Override
   public <T extends NeptuneObject> boolean compareAttributes(T anotherObject)
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
         // if (!sameValue(this.getComment(), another.getComment()))
         // return false;
         if (!sameValue(this.getRegistrationNumber(), another.getRegistrationNumber()))
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

   /* (non-Javadoc)
    * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#toURL()
    */
   @Override
   public String toURL()
   {
      // no show page for stop points in ruby gui
      return null;
   }

}
