package fr.certu.chouette.model.neptune;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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

/**
 * Neptune Journey Pattern : pattern for vehicle journeys in a route
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 */
@Entity
@Table(name = "journey_patterns")
@NoArgsConstructor
@Log4j
public class JourneyPattern extends NeptuneIdentifiedObject
{
   private static final long serialVersionUID = 7895941111990419404L;

   @Getter
   @Column(name = "name")
   private String name;

   @Getter
   @Column(name = "comment")
   private String comment;

   @Getter
   @Column(name = "registration_number")
   private String registrationNumber;

   @Getter
   @Setter
   @Column(name = "published_name")
   private String publishedName;

   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "route_id")
   private Route route;

   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "departure_stop_point_id")
   private StopPoint departureStopPoint;

   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "arrival_stop_point_id")
   private StopPoint arrivalStopPoint;

   @Getter
   @Setter
   @ManyToMany
   @JoinTable(name = "journey_patterns_stop_points", joinColumns = { @JoinColumn(name = "journey_pattern_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "stop_point_id", nullable = false, updatable = false) })
   private List<StopPoint> stopPoints = new ArrayList<StopPoint>(0);

   @Getter
   @Setter
   @OneToMany(mappedBy = "journeyPattern", cascade = { CascadeType.PERSIST,
         CascadeType.MERGE })
   private List<VehicleJourney> vehicleJourneys = new ArrayList<VehicleJourney>(
         0);

   /**
    * Start StopPoint id <br/>
    * import/export usage <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   @Transient
   private String origin;
   /**
    * End StopPoint id <br/>
    * import/export usage <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   @Transient
   private String destination;

   /**
    * List of StpoPoint ObjectIds <br/>
    * import/export usage <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   @Transient
   private List<String> stopPointIds;
   /**
    * Line ObjectId <br/>
    * import/export usage <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   @Transient
   private String lineIdShortcut;
   /**
    * Route ObjectId <br/>
    * import/export usage <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   @Transient
   private String routeId;

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

   @Override
   public String toString(String indent, int level)
   {
      StringBuilder sb = new StringBuilder(super.toString(indent, level));
      sb.append("\n").append(indent).append("routeId = ").append(routeId);
      sb.append("\n").append(indent).append("publishedName = ")
            .append(publishedName);
      sb.append("\n").append(indent).append("origin = ").append(origin);
      sb.append("\n").append(indent).append("destination = ")
            .append(destination);
      sb.append("\n").append(indent).append("registrationNumber = ")
            .append(registrationNumber);
      sb.append("\n").append(indent).append("comment = ").append(comment);

      if (stopPointIds != null)
      {
         sb.append("\n").append(indent).append(CHILD_ARROW)
               .append("stopPointIds");
         for (String stopPointId : stopPointIds)
         {
            sb.append("\n").append(indent).append(CHILD_LIST_ARROW)
                  .append(stopPointId);
         }
      }
      if (level > 0)
      {
         int childLevel = level - 1;
         String childIndent = indent + CHILD_INDENT;

         childIndent = indent + CHILD_LIST_INDENT;
         if (stopPoints != null)
         {
            sb.append("\n").append(indent).append(CHILD_ARROW)
                  .append("stopPoints");
            for (StopPoint stopPoint : getStopPoints())
            {
               sb.append("\n").append(indent).append(CHILD_LIST_ARROW)
                     .append(stopPoint.toString(childIndent, childLevel));
            }
         }
         if (vehicleJourneys != null)
         {
            sb.append("\n").append(indent).append(CHILD_ARROW)
                  .append("vehicleJourneys");
            for (VehicleJourney vehicleJourney : getVehicleJourneys())
            {
               sb.append("\n").append(indent).append(CHILD_LIST_ARROW)
                     .append(vehicleJourney.toString(childIndent, childLevel));
            }
         }
      }
      return sb.toString();
   }

   /**
    * add a stopPoint id if not already present
    * 
    * @param stopPointId
    */
   public void addStopPointId(String stopPointId)
   {
      if (stopPointIds == null)
         stopPointIds = new ArrayList<String>();
      if (!stopPointIds.contains(stopPointId))
         stopPointIds.add(stopPointId);
   }

   /**
    * add a stop point if not already present
    * 
    * @param stopPoint
    */
   public void addStopPoint(StopPoint stopPoint)
   {
      if (stopPoints == null)
         stopPoints = new ArrayList<StopPoint>();
      if (!stopPoints.contains(stopPoint))
      {
         stopPoints.add(stopPoint);
         refreshDepartureArrivals();
      }

   }

   /**
    * remove a stop point
    * 
    * @param stopPoint
    */
   public void removeStopPoint(StopPoint stopPoint)
   {
      if (stopPoints == null)
         stopPoints = new ArrayList<StopPoint>();
      if (stopPoints.contains(stopPoint))
      {
         stopPoints.remove(stopPoint);
         refreshDepartureArrivals();
      }
   }

   /**
    * add a vehicle journey if not already present
    * 
    * @param vehicleJourney
    */
   public void addVehicleJourney(VehicleJourney vehicleJourney)
   {
      if (vehicleJourneys == null)
         vehicleJourneys = new ArrayList<VehicleJourney>();
      if (vehicleJourney == null || vehicleJourneys.contains(vehicleJourney))
         return;
      vehicleJourneys.add(vehicleJourney);
      vehicleJourney.setJourneyPattern(this);
      if (route != null)
         vehicleJourney.setRoute(route);
   }

   /**
    * remove a vehicle journey if not already present
    * 
    * @param vehicleJourney
    */
   public void removeVehicleJourney(VehicleJourney vehicleJourney)
   {
      if (vehicleJourneys == null)
         vehicleJourneys = new ArrayList<VehicleJourney>();
      if (vehicleJourneys.contains(vehicleJourney))
         vehicleJourneys.remove(vehicleJourney);
   }

   /**
    * produce a unique key for the list of Stop points
    * 
    * @return
    */
   public String getStopPointsAsKey()
   {

      if (stopPoints != null)
      {
         StringBuffer buffer = new StringBuffer();
         for (StopPoint point : stopPoints)
         {
            buffer.append(point.getId());
            buffer.append(',');
         }
         return buffer.toString();
      }
      return "empty journeyPattern";
   }

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#clean()
    */
   @Override
   public boolean clean()
   {
      if (vehicleJourneys == null)
      {
         return false;
      }
      for (Iterator<VehicleJourney> iterator = vehicleJourneys.iterator(); iterator
            .hasNext();)
      {
         VehicleJourney vehicleJourney = iterator.next();
         if (vehicleJourney == null || !vehicleJourney.clean())
         {
            iterator.remove();
         }
      }
      if (vehicleJourneys.isEmpty())
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
      Route route = getRoute();
      if (route != null)
      {
         Line line = route.getLine();
         if (line != null)
            setLineIdShortcut(line.getObjectId());
      }
      List<StopPoint> stopPoints = getStopPoints();
      List<VehicleJourney> vjs = getVehicleJourneys();
      if (vjs != null && !vjs.isEmpty())
      {
         // complete StopPoints
         if (stopPoints == null || stopPoints.isEmpty())
         {
            VehicleJourney vj = vjs.get(0);
            for (VehicleJourneyAtStop vjas : vj.getVehicleJourneyAtStops())
            {
               addStopPoint(vjas.getStopPoint());
            }
         }
         // compute origin/destination
         if (stopPoints == null || stopPoints.isEmpty())
         {
            origin = null;
            destination = null;
         } else
         {
            origin = stopPoints.get(0).getObjectId();
            destination = stopPoints.get(stopPoints.size() - 1).getObjectId();
         }
         // complete VJ
         for (VehicleJourney vehicleJourney : vjs)
         {
            vehicleJourney.complete();
         }
      }
   }

   public void refreshDepartureArrivals()
   {
      if (stopPoints == null || stopPoints.isEmpty())
      {
         departureStopPoint = null;
         arrivalStopPoint = null;
      } else
      {
         departureStopPoint = stopPoints.get(0);
         arrivalStopPoint = stopPoints.get(stopPoints.size() - 1);
      }

   }

   @Override
   public <T extends NeptuneObject> boolean compareAttributes(T anotherObject)
   {
      if (anotherObject instanceof JourneyPattern)
      {
         JourneyPattern another = (JourneyPattern) anotherObject;
         if (!sameValue(this.getObjectId(), another.getObjectId()))
            return false;
         if (!sameValue(this.getObjectVersion(), another.getObjectVersion()))
            return false;
         if (!sameValue(this.getName(), another.getName()))
            return false;
         if (!sameValue(this.getComment(), another.getComment()))
            return false;
         if (!sameValue(this.getPublishedName(), another.getPublishedName()))
            return false;
         if (!sameValue(this.getRegistrationNumber(),
               another.getRegistrationNumber()))
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

      return getRoute().toURL() + "/journey_patterns/" + getId();
   }

}
