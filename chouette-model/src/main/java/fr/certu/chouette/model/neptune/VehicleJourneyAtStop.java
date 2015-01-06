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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import fr.certu.chouette.model.neptune.type.AlightingPossibilityEnum;
import fr.certu.chouette.model.neptune.type.BoardingAlightingPossibilityEnum;
import fr.certu.chouette.model.neptune.type.BoardingPossibilityEnum;

/**
 * Chouette VehicleJourneyAtStop : passing time on stops
 * <p/>
 * Neptune mapping : VehicleJourneyAtStop <br/>
 * Gtfs mapping : StopTime <br/>
 */

@Entity
@Table(name = "vehicle_journey_at_stops", uniqueConstraints = @UniqueConstraint(columnNames = { "vehicle_journey_id", "stop_point_id" }, name = "index_vehicle_journey_at_stops_on_stop_point_id"))
@NoArgsConstructor
public class VehicleJourneyAtStop extends NeptuneObject
{
   private static final long serialVersionUID = 194243517715939830L;

   /**
    * connecting Service Id
    * 
    * @param connectingServiceId
    *           New value
    * @return The actual value
    * 
    * @deprecated unused in Neptune 2014 specifications </br> 
    *             {will be removed in next version}
    */
   @Deprecated
   @Getter
   @Setter
   @Column(name = "connecting_service_id")
   private String connectingServiceId;

   /**
    * boarding alighting possibility
    * 
    * @param boardingAlightingPossibility
    *           New value
    * @return The actual value
    * 
    * @deprecated replaced by forBoarding and forAlighting</br>
    *             {will be removed in next version}
    * 
    */
   @Deprecated
   @Getter
   @Setter
   @Enumerated(EnumType.STRING)
   @Column(name = "boarding_alighting_possibility")
   private BoardingAlightingPossibilityEnum boardingAlightingPossibility;

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
    * arrival time
    * 
    * @param arrivalTime
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Column(name = "arrival_time")
   private Time arrivalTime;

   /**
    * departure time
    * 
    * @param departureTime
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Column(name = "departure_time")
   private Time departureTime;

   /**
    * waiting time
    * 
    * @param waitingTime
    *           New value
    * @return The actual value
    * 
    * @deprecated unused in Neptune 2014 specifications</br>
    *             {will be removed in next version}
    */
   @Deprecated
   @Getter
   @Setter
   @Column(name = "waiting_time")
   private Time waitingTime;

   /**
    * elapse duration <br/>
    * for vehicle journey with time slots<br/>
    * definition should change in next release
    * 
    * @param elapseDuration
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Column(name = "elapse_duration")
   private Time elapseDuration;

   /**
    * headway frequnecy <br/>
    * for vehicle journey with time slots<br/>
    * field should move to vehicleJourney in next release
    * 
    * @param headwayFrequency
    *           New value
    * @return The actual value
    * 
    * @deprecated field should move to vehicleJourney in next release
    */
   @Deprecated
   @Getter
   @Setter
   @Column(name = "headway_frequency")
   private Time headwayFrequency;

   /**
    * vehicle journey reference <br/>
    * 
    * @param vehicleJourney
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "vehicle_journey_id")
   private VehicleJourney vehicleJourney;

   /**
    * stop point reference <br/>
    * 
    * @param stopPoint
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "stop_point_id")
   private StopPoint stopPoint;

   /**
    * Neptune Id of associated StopPoint<br/>
    * (import/export purpose)
    * 
    * @param stopPointId
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Transient
   private String stopPointId;

   /**
    * Neptune Id of associated VehicleJourney<br/>
    * (import/export purpose)
    * 
    * @param vehicleJourneyId
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Transient
   private String vehicleJourneyId;

   /**
    * order in journeyPattern<br/>
    * (import/export purpose)
    * 
    * @param order
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Transient
   private long order;

   /**
    * departure<br/>
    * (import/export purpose)
    * 
    * @param departure
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Transient
   private boolean departure;

   /**
    * arrival<br/>
    * (import/export purpose)
    * 
    * @param arrival
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Transient
   private boolean arrival;

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.model.neptune.NeptuneObject#toString(java.lang.String,
    * int)
    */
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
               sb.append("\n").append(indent).append("stopPoint.name = ").append(stopPoint.getContainedInStopArea().getName());
         }
      }

      return sb.toString();
   }

   /**
    * convert time to string for toString purpose
    * 
    * @param date
    * @return string formated time
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

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.model.neptune.NeptuneObject#equals(java.lang.Object)
    */
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

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.model.neptune.NeptuneObject#compareAttributes(fr.certu
    * .chouette.model.neptune.NeptuneObject)
    */
   @Override
   public <T extends NeptuneObject> boolean compareAttributes(T anotherObject)
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
