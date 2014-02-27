package fr.certu.chouette.model.neptune;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.type.BoardingAlightingPossibilityEnum;

/**
 * Neptune VehicleJourneyAtStop :
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 */

@Entity
@Table(name = "vehicle_journey_at_stops", uniqueConstraints = @UniqueConstraint(
      columnNames = { "vehicle_journey_id", "stop_point_id" },
      name = "index_vehicle_journey_at_stops_on_stop_point_id"))
@NoArgsConstructor
public class VehicleJourneyAtStop extends NeptuneObject
{
   private static final long serialVersionUID = 194243517715939830L;

   /**
    * name of stopPoint attribute for {@link Filter} attributeName construction
    */
   public static final String STOPPOINT = "stopPoint";

   @Getter
   @Setter
   @Column(name = "connecting_service_id")
   private String connectingServiceId;

   @Getter
   @Setter
   @Enumerated(EnumType.STRING)
   @Column(name = "boarding_alighting_possibility")
   private BoardingAlightingPossibilityEnum boardingAlightingPossibility;

   @Getter
   @Setter
   @Column(name = "arrival_time")
   private Time arrivalTime;

   @Getter
   @Setter
   @Column(name = "departure_time")
   private Time departureTime;

   @Getter
   @Setter
   @Column(name = "waiting_time")
   private Time waitingTime;

   @Getter
   @Setter
   @Column(name = "elapse_duration")
   private Time elapseDuration;

   @Getter
   @Setter
   @Column(name = "headway_frequency")
   private Time headwayFrequency;

   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "vehicle_journey_id")
   private VehicleJourney vehicleJourney;

   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "stop_point_id")
   private StopPoint stopPoint;

   /**
    * Trident Id of associated StopPoint
    */
   @Getter
   @Setter
   @Transient
   private String stopPointId;

   /**
    * Trident Id of associated VehicleJourney
    */
   @Getter
   @Setter
   @Transient
   private String vehicleJourneyId;

   /**
    * order in journeyPattern
    */
   @Getter
   @Setter
   @Transient
   private long order;

   @Getter
   @Setter
   @Transient
   private boolean departure;

   @Getter
   @Setter
   @Transient
   private boolean arrival;

   @Override
   public String toString(String indent, int level)
   {
      StringBuilder sb = new StringBuilder(super.toString(indent, level));
      sb.append("\n").append(indent).append("stopPointId = ").append(stopPointId);
      sb.append("\n").append(indent).append("vehicleJourneyId = ").append(vehicleJourneyId);
      sb.append("\n").append(indent).append("connectingServiceId = ").append(connectingServiceId);
      sb.append("\n").append(indent).append("boardingAlightingPossibility = ").append(boardingAlightingPossibility);
      sb.append("\n").append(indent).append("order = ").append(order);
      sb.append("\n").append(indent).append("arrivalTime = ").append(formatDate(arrivalTime));
      sb.append("\n").append(indent).append("departureTime = ").append(formatDate(departureTime));
      sb.append("\n").append(indent).append("waitingTime = ").append(formatDate(waitingTime));
      sb.append("\n").append(indent).append("elapseDuration = ").append(formatDate(elapseDuration));
      sb.append("\n").append(indent).append("headwayFrequency = ").append(formatDate(headwayFrequency));
      if (level >= 1)
      {
         if (stopPoint != null)
         {
            sb.append("\n").append(indent).append("stopPoint.id = ").append(stopPoint.getId());
            sb.append("\n").append(indent).append("stopPoint.objectId = ").append(stopPoint.getObjectId());
            if (stopPoint.getContainedInStopArea() != null)
               sb.append("\n").append(indent).append("stopPoint.name = ")
                     .append(stopPoint.getContainedInStopArea().getName());
         }
      }

      return sb.toString();
   }

   /**
    * convert time to string for toString purpose
    * 
    * @param date
    * @return
    */
   private String formatDate(Date date)
   {
      DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
      if (date != null)
      {
         return dateFormat.format(date);
      }
      else
      {
         return null;
      }
   }

   /**
    * check and return stoppoint if objectId match
    * 
    * @param objectId
    *           stopPoint objectid to check
    * @return stoppoint
    */
   public StopPoint getStopPointByObjectId(String objectId)
   {
      if (stopPoint != null)
      {
         if (stopPoint.getObjectId().equals(objectId))
            return stopPoint;
         else
            return null;
      }
      else
      {
         return null;
      }

   }

   @Override
   public boolean equals(Object obj)
   {
      if (obj instanceof VehicleJourneyAtStop)
      {
         VehicleJourneyAtStop vobj = (VehicleJourneyAtStop) obj;
         if (getId() == null && vobj.getId() == null)
         {
            if (getStopPoint() != null)
            {
               return getStopPoint().equals(vobj.getStopPoint());
            }
         }
      }
      return super.equals(obj);
   }

   @Override
   public <T extends NeptuneObject> boolean compareAttributes(
         T anotherObject)
   {
      if (anotherObject instanceof VehicleJourneyAtStop)
      {
         VehicleJourneyAtStop another = (VehicleJourneyAtStop) anotherObject;

         if (!sameValue(this.getArrivalTime(), another.getArrivalTime()))
            return false;
         if (!sameValue(this.getBoardingAlightingPossibility(), another.getBoardingAlightingPossibility()))
            return false;
         if (!sameValue(this.getConnectingServiceId(), another.getConnectingServiceId()))
            return false;
         if (!sameValue(this.getDepartureTime(), another.getDepartureTime()))
            return false;
         if (!sameValue(this.getElapseDuration(), another.getElapseDuration()))
            return false;
         if (!sameValue(this.getHeadwayFrequency(), another.getHeadwayFrequency()))
            return false;
         if (!sameValue(this.getOrder(), another.getOrder()))
            return false;
         if (!sameValue(this.getWaitingTime(), another.getWaitingTime()))
            return false;
         return true;
      }
      else
      {
         return false;
      }
   }

}
