package fr.certu.chouette.model.neptune;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Neptune Journey Pattern : pattern for vehicle journeys in a route
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 */
public class JourneyPattern extends NeptuneIdentifiedObject
{
   private static final long    serialVersionUID = 7895941111990419404L;
   /**
    * Comment <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String               registrationNumber;
   /**
    * Comment <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String               comment;
   /**
    * Start StopPoint id <br/>
    * import/export usage <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String               origin;
   /**
    * End StopPoint id <br/>
    * import/export usage <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String               destination;
   /**
    * published journey pattern name <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String               publishedName;
   /**
    * ordered list of StopPoints <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private List<StopPoint>      stopPoints;
   /**
    * List of StpoPoint ObjectIds <br/>
    * import/export usage <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private List<String>         stopPointIds;
   /**
    * Line ObjectId <br/>
    * import/export usage <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String               lineIdShortcut;
   /**
    * Route ObjectId <br/>
    * import/export usage <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String               routeId;
   /**
    * Route <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private Route                route;
   /**
    * Vehicle journeys <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private List<VehicleJourney> vehicleJourneys;

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString(String indent, int level)
   {
      StringBuilder sb = new StringBuilder(super.toString(indent, level));
      sb.append("\n").append(indent).append("routeId = ").append(routeId);
      sb.append("\n").append(indent).append("publishedName = ").append(publishedName);
      sb.append("\n").append(indent).append("origin = ").append(origin);
      sb.append("\n").append(indent).append("destination = ").append(destination);
      sb.append("\n").append(indent).append("registrationNumber = ").append(registrationNumber);
      sb.append("\n").append(indent).append("comment = ").append(comment);

      if (stopPointIds != null)
      {
         sb.append("\n").append(indent).append(CHILD_ARROW).append("stopPointIds");
         for (String stopPointId : stopPointIds)
         {
            sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(stopPointId);
         }
      }
      if (level > 0)
      {
         int childLevel = level - 1;
         String childIndent = indent + CHILD_INDENT;

         childIndent = indent + CHILD_LIST_INDENT;
         if (stopPoints != null)
         {
            sb.append("\n").append(indent).append(CHILD_ARROW).append("stopPoints");
            for (StopPoint stopPoint : getStopPoints())
            {
               sb.append("\n").append(indent).append(CHILD_LIST_ARROW)
                     .append(stopPoint.toString(childIndent, childLevel));
            }
         }
         if (vehicleJourneys != null)
         {
            sb.append("\n").append(indent).append(CHILD_ARROW).append("vehicleJourneys");
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
         stopPoints.add(stopPoint);
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
         stopPoints.remove(stopPoint);
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
      if (!vehicleJourneys.contains(vehicleJourney))
         vehicleJourneys.add(vehicleJourney);
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
      for (Iterator<VehicleJourney> iterator = vehicleJourneys.iterator(); iterator.hasNext();)
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
         }
         else
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
}
