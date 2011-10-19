package fr.certu.chouette.model.neptune;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.manager.VehicleJourneyManager;
import fr.certu.chouette.model.neptune.type.ServiceStatusValueEnum;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;
import fr.certu.chouette.model.user.User;

/**
 * Neptune VehicleJourney
 * <p/>
 * <b>Note</b> VehicleJourney class contains method to manipulate
 * VehicleJourneyAtStop in logic with StopPoint's position on Route and
 * StopPoint list in JourneyPatterns <br/>
 * it is mandatory to respect instruction on each of these methods
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 * 
 */
public class VehicleJourney extends NeptuneIdentifiedObject
{
   private static final long          serialVersionUID             = 304336286208135064L;

   // constant for persistence fields
   /**
    * name of comment attribute for {@link Filter} attributeName construction
    */
   public static final String         COMMENT                      = "comment";
   /**
    * name of serviceStatusValue attribute for {@link Filter} attributeName
    * construction
    */
   public static final String         SERVICE_STATUS               = "serviceStatusValue";
   /**
    * name of transportMode attribute for {@link Filter} attributeName
    * construction
    */
   public static final String         TRANSPORT_MODE               = "transportMode";
   /**
    * name of publishedJourneyName attribute for {@link Filter} attributeName
    * construction
    */
   public static final String         PUBLISHED_JOURNEY_NAME       = "publishedJourneyName";
   /**
    * name of publishedJourneyIdentifier attribute for {@link Filter}
    * attributeName construction
    */
   public static final String         PUBLISHED_JOURNEY_IDENTIFIER = "publishedJourneyIdentifier";
   /**
    * name of serviceStatusValue attribute for {@link Filter} attributeName
    * construction
    */
   public static final String         FACILITY                     = "facility";
   /**
    * name of vehicleTypeIdentifier attribute for {@link Filter} attributeName
    * construction
    */
   public static final String         VEHICLE_TYPE_IDENTIFIER      = "vehicleTypeIdentifier";
   /**
    * name of number attribute for {@link Filter} attributeName construction
    */
   public static final String         NUMBER                       = "number";
   /**
    * name of route attribute for {@link Filter} attributeName construction
    */
   public static final String         ROUTE                        = "route";
   /**
    * name of journeyPattern attribute for {@link Filter} attributeName
    * construction
    */
   public static final String         JOURNEY_PATTERN              = "journeyPattern";
   /**
    * name of timeSlot attribute for {@link Filter} attributeName construction
    */
   public static final String         TIMESLOT                     = "timeSlot";
   /**
    * name of company attribute for {@link Filter} attributeName construction
    */
   public static final String         COMPANY                      = "company";
   /**
    * name of timetables attribute for {@link Filter} attributeName construction
    */
   public static final String         TIMETABLES                   = "timetables";
   /**
    * name of vehicleJourneyAtStops attribute for {@link Filter} attributeName
    * construction
    */
   public static final String         VEHICLE_JOURNEY_AT_STOPS     = "vehicleJourneyAtStops";

   /**
    * Service Status <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private ServiceStatusValueEnum     serviceStatusValue;
   /**
    * Transport Mode <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private TransportModeNameEnum      transportMode;
   /**
    * Comment <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String                     comment;
   /**
    * Facility <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String                     facility;
   /**
    * number <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private long                       number;
   /**
    * route objectId <br/>
    * <i>readable/writable</i>
    * <p/>
    * null if vehicleJourney is read from database; call
    * {@link VehicleJourneyManager#completeObject(User, VehicleJourney)} to
    * initialize
    */
   @Getter
   @Setter
   private String                     routeId;
   /**
    * route <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private Route                      route;
   /**
    * journeyPattern objectId <br/>
    * <i>readable/writable</i>
    * <p/>
    * null if vehicleJourney is read from database; call
    * {@link VehicleJourneyManager#completeObject(User, VehicleJourney)} to
    * initialize
    */
   @Getter
   @Setter
   private String                     journeyPatternId;
   /**
    * journey pattern <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private JourneyPattern             journeyPattern;
   /**
    * timeSlot objectId <br/>
    * <i>readable/writable</i>
    * <p/>
    * null if vehicleJourney is read from database; call
    * {@link VehicleJourneyManager#completeObject(User, VehicleJourney)} to
    * initialize
    */
   @Getter
   @Setter
   private String                     timeSlotId;
   /**
    * timeSlot <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private TimeSlot                   timeSlot;
   /**
    * publishedJourneyName <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String                     publishedJourneyName;
   /**
    * publishedJourneyIdentifier <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String                     publishedJourneyIdentifier;
   /**
    * vehicleTypeIdentifier <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String                     vehicleTypeIdentifier;
   /**
    * company objectId <br/>
    * <i>readable/writable</i>
    * <p/>
    * null if vehicleJourney is read from database; call
    * {@link VehicleJourneyManager#completeObject(User, VehicleJourney)} to
    * initialize
    */
   @Getter
   @Setter
   private String                     companyId;
   /**
    * company <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private Company                    company;
   /**
    * line objectId <br/>
    * <i>readable/writable</i>
    * <p/>
    * null if vehicleJourney is read from database; call
    * {@link VehicleJourneyManager#completeObject(User, VehicleJourney)} to
    * initialize
    */
   @Getter
   @Setter
   private String                     lineIdShortcut;
   /**
    * line <br/>
    * <i>readable/writable</i>
    * <p/>
    * null if vehicleJourney is read from database; call
    * {@link VehicleJourneyManager#completeObject(User, VehicleJourney)} to
    * initialize
    */
   @Getter
   @Setter
   private Line                       line;
   /**
    * vehicleJourneyAtStops <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private List<VehicleJourneyAtStop> vehicleJourneyAtStops        = new ArrayList<VehicleJourneyAtStop>();
   /**
    * timetables <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private List<Timetable>            timetables                   = new ArrayList<Timetable>();

   /**
    * add a VehicleJourneyAtStop if not already present
    * <p/>
    * adding won't sort VehicleJourneyAtStops in VehicleJourney <br/>
    * use {@link #sortVehicleJourneyAtStops()} to ordinate them <br/>
    * use {@link #checkJourneyPattern()} to link VehicleJourney on appropriate
    * JourneyPattern if necessary
    * <p/>
    * no <code>removeVehicleJourneyAtStop</code> is implemented, use
    * {@link #removeStopPoint(StopPoint)} instead
    * 
    * @param vehicleJourneyAtStop
    */
   public void addVehicleJourneyAtStop(VehicleJourneyAtStop vehicleJourneyAtStop)
   {
      if (vehicleJourneyAtStops == null)
         vehicleJourneyAtStops = new ArrayList<VehicleJourneyAtStop>();
      if (vehicleJourneyAtStop != null && !vehicleJourneyAtStops.contains(vehicleJourneyAtStop))
      {
         vehicleJourneyAtStops.add(vehicleJourneyAtStop);
      }
   }

   /**
    * add a collection of VehicleJourneyAtStops if not already presents
    * <p/>
    * adding won't sort VehicleJourneyAtStops in VehicleJourney <br/>
    * use {@link #sortVehicleJourneyAtStops()} to ordinate them <br/>
    * use {@link #checkJourneyPattern()} to link VehicleJourney on appropriate
    * JourneyPattern if necessary
    * 
    * @param vehicleJourneyAtStopCollection
    *           VehicleJourneyAtStops to add
    */
   public void addVehicleJourneyAtStops(Collection<VehicleJourneyAtStop> vehicleJourneyAtStopCollection)
   {
      if (vehicleJourneyAtStops == null)
         vehicleJourneyAtStops = new ArrayList<VehicleJourneyAtStop>();
      for (VehicleJourneyAtStop vehicleJourneyAtStop : vehicleJourneyAtStopCollection)
      {
         if (vehicleJourneyAtStop != null && !vehicleJourneyAtStops.contains(vehicleJourneyAtStop))
         {
            vehicleJourneyAtStops.add(vehicleJourneyAtStop);
         }
      }
   }

   /**
    * add a timeTable if not already presents
    * 
    * @param timetable
    *           to add
    */
   public void addTimetable(Timetable timetable)
   {
      if (timetables == null)
         timetables = new ArrayList<Timetable>();
      if (timetable != null && !timetables.contains(timetable))
         timetables.add(timetable);
   }

   /**
    * add a collection of timetables if not already presents
    * 
    * @param timetable
    *           to add
    */
   public void addTimetables(Collection<Timetable> timetableCollection)
   {
      if (timetables == null)
         timetables = new ArrayList<Timetable>();
      for (Timetable timetable : timetableCollection)
      {
         if (timetable != null && !timetables.contains(timetable))
            timetables.add(timetable);
      }
   }

   /**
    * remove a timetable if present
    * 
    * @param timetable
    */
   public void removeTimetable(Timetable timetable)
   {
      if (timetables == null)
         timetables = new ArrayList<Timetable>();
      if (timetable != null && timetables.contains(timetable))
         timetables.remove(timetable);
   }

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#clean()
    */
   @Override
   public boolean clean()
   {
      if (vehicleJourneyAtStops == null || vehicleJourneyAtStops.isEmpty())
      {
         return false;
      }
      if (timetables == null || timetables.isEmpty())
      {
         return false;
      }
      return true;
   }

   /**
    * remove the vehicleJourneyAtStop attached to a StopPoint
    * <p/>
    * vehicleJourneyAtStop's order will be recalculated <br/>
    * use {@link #checkJourneyPattern()} to link VehicleJourney on appropriate
    * JourneyPattern if necessary
    * 
    * @param stopPoint
    *           stopPoint to remove
    */
   public void removeStopPoint(StopPoint stopPoint)
   {
      if (vehicleJourneyAtStops != null)
      {
         List<VehicleJourneyAtStop> vjas = vehicleJourneyAtStops;
         boolean found = false;
         for (Iterator<VehicleJourneyAtStop> iterator = vjas.iterator(); iterator.hasNext();)
         {
            VehicleJourneyAtStop vehicleJourneyAtStop = iterator.next();
            if (stopPoint.equals(vehicleJourneyAtStop.getStopPoint()))
            {
               vehicleJourneyAtStop.setStopPoint(null);
               iterator.remove();
               found = true;
               break;
            }

         }
         if (found)
         {
            sortVehicleJourneyAtStops();
         }
      }
   }

   /**
    * order VehicleJourneyAtStops on StopPoint positions, calculate order and
    * departure/arrival flags
    */
   public void sortVehicleJourneyAtStops()
   {
      if (getVehicleJourneyAtStops() != null)
      {
         List<VehicleJourneyAtStop> vjass = getVehicleJourneyAtStops();
         Collections.sort(vjass, new VehicleJourneyAtStopComparator());
         int last = vjass.size() - 1;
         for (int i = 0; i < vjass.size(); i++)
         {
            VehicleJourneyAtStop vjas = vjass.get(i);
            vjas.setDeparture(i == 0);
            vjas.setOrder(i + 1);
            vjas.setArrival(i == last);
         }

      }

   }

   /**
    * check and rebuild journeyPattern if necessary
    * <p/>
    * <b>Warning</b> if true is returned and journeypattern.getId() is null or
    * journeyPattern.getObjectId() contains only a prefix the journeyPattern
    * must be first saved separately and, after, must be added to route and
    * route must be added to journeyPattern, therefore, route must be updated
    * (vehicleJourney will be updated in this action) <br/>
    * if true is returned and journeypattern.getId() is not null or
    * journeyPattern.getObjectId() contains a complete objectId only route must
    * be updated (vehicleJourney will be updated in this action)
    * 
    * @return true if journeyPattern has changed
    */
   public boolean checkJourneyPattern()
   {
      sortVehicleJourneyAtStops();
      String vjKey = getStopPointsAsKey();
      // check if actual journeyPattern is still valid
      if (journeyPattern != null)
      {
         String jpKey = journeyPattern.getStopPointsAsKey();
         if (jpKey.equals(vjKey))
            return false;
         journeyPattern.removeVehicleJourney(this);
         journeyPattern = null;
      }
      List<JourneyPattern> jps = getRoute().getJourneyPatterns();

      // try to find an existing journeyPattern which matches stopPoint sequence
      for (JourneyPattern jp : jps)
      {
         String jpKey = jp.getStopPointsAsKey();
         if (jpKey.equals(vjKey))
         {
            journeyPattern = jp;
            break;
         }
      }

      // no journeyPattern found
      if (journeyPattern == null)
      {
         // create a new JourneyPattern
         journeyPattern = new JourneyPattern();
         journeyPattern.setCreationTime(Calendar.getInstance().getTime());
         String prefix = route.getObjectId().split(":")[0];
         journeyPattern.setObjectId(prefix);
         for (VehicleJourneyAtStop vjas : vehicleJourneyAtStops)
         {
            journeyPattern.addStopPoint(vjas.getStopPoint());
         }
      }

      // put a copy of vehicleJourney in journeyPattern
      // VehicleJourney copy = copy();
      journeyPattern.addVehicleJourney(this);

      return true;
   }

   /**
    * build a unique key form vehicleJourney based on ordered stoppoint ids
    * 
    * <p/>
    * use to match journeyPattern stopPointKey
    * 
    * @return
    */
   private String getStopPointsAsKey()
   {

      if (vehicleJourneyAtStops != null)
      {
         StringBuffer buffer = new StringBuffer();
         for (VehicleJourneyAtStop vjas : vehicleJourneyAtStops)
         {
            buffer.append(vjas.getStopPoint().getId());
            buffer.append(',');
         }
         return buffer.toString();
      }
      return "empty vehicleJourney";
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString(String indent, int level)
   {
      StringBuilder sb = new StringBuilder(super.toString(indent, level));
      sb.append("\n").append(indent).append("serviceStatusValue = ").append(serviceStatusValue);
      sb.append("\n").append(indent).append("transportMode = ").append(transportMode);
      sb.append("\n").append(indent).append("comment = ").append(comment);
      sb.append("\n").append(indent).append("facility = ").append(facility);
      sb.append("\n").append(indent).append("number = ").append(number);
      sb.append("\n").append(indent).append("routeId = ").append(routeId);
      sb.append("\n").append(indent).append("journeyPatternId = ").append(journeyPatternId);
      sb.append("\n").append(indent).append("timeSlotId = ").append(timeSlotId);
      sb.append("\n").append(indent).append("publishedJourneyName = ").append(publishedJourneyName);
      sb.append("\n").append(indent).append("publishedJourneyIdentifier = ").append(publishedJourneyIdentifier);
      sb.append("\n").append(indent).append("vehicleTypeIdentifier = ").append(vehicleTypeIdentifier);
      sb.append("\n").append(indent).append("companyId = ").append(companyId);

      int childLevel = level - 1;

      String childIndent = indent + CHILD_LIST_INDENT;
      if (vehicleJourneyAtStops != null)
      {
         sb.append("\n").append(indent).append(CHILD_ARROW).append("vehicleJourneyAtStops");
         for (VehicleJourneyAtStop vehicleJourneyAtStop : getVehicleJourneyAtStops())
         {
            sb.append("\n").append(indent).append(CHILD_LIST_ARROW)
                  .append(vehicleJourneyAtStop.toString(childIndent, childLevel));
         }
      }
      if (timeSlot != null)
      {
         sb.append("\n").append(indent).append(CHILD_ARROW).append("timeSlot");
         sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(timeSlot.toString(childIndent, childLevel));
      }
      if (level > 0)
      {
         childIndent = indent + CHILD_INDENT;
         if (timeSlot != null)
         {
            sb.append("\n").append(indent).append(CHILD_ARROW).append(timeSlot.toString(childIndent, 0));
         }

         if (company != null)
         {
            sb.append("\n").append(indent).append(CHILD_ARROW).append(company.toString(childIndent, 0));
         }
         childIndent = indent + CHILD_LIST_INDENT;
         if (timetables != null)
         {
            sb.append("\n").append(indent).append(CHILD_ARROW).append("timetables");
            for (Timetable timetable : getTimetables())
            {
               sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(timetable.toString(childIndent, 0));
            }
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
      Line line = getLine();
      if (line != null)
         setLineIdShortcut(line.getObjectId());

      List<VehicleJourneyAtStop> vjass = getVehicleJourneyAtStops();
      for (int i = 0; i < vjass.size(); i++)
      {
         VehicleJourneyAtStop vjas = vjass.get(i);
         vjas.setVehicleJourney(this);
         vjas.setVehicleJourneyId(this.getObjectId());
      }
   }

   /**
    * compare VehicleJourneyAtStop on StoPoint position in route
    * 
    */
   public class VehicleJourneyAtStopComparator implements Comparator<VehicleJourneyAtStop>
   {

      /*
       * (non-Javadoc)
       * 
       * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
       */
      @Override
      public int compare(VehicleJourneyAtStop o1, VehicleJourneyAtStop o2)
      {
         StopPoint point1 = o1.getStopPoint();
         StopPoint point2 = o2.getStopPoint();
         if (point1 != null && point2 != null)
         {
            return point1.getPosition() - point2.getPosition();
         }
         return 0;
      }

   }

}
