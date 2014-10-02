package fr.certu.chouette.model.neptune;

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
import lombok.extern.log4j.Log4j;
import fr.certu.chouette.model.neptune.type.facility.FacilityFeature;

/**
 * Neptune Facility : specific feature on different kind of Public Transport
 * elements
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 */
@Entity
@Table(name = "facilities")
@NoArgsConstructor
@Log4j
public class Facility extends NeptuneLocalizedObject
{
   private static final long serialVersionUID = -2150117548707325330L;

   @Getter
   @Column(name = "name")
   private String name;

   @Getter
   @Setter
   @Column(name = "comment")
   private String comment;

   @Getter
   @Setter
   @Column(name = "description")
   private String description;

   @Getter
   @Setter
   @Column(name = "free_access")
   private Boolean freeAccess;

   @Getter
   @Setter
   @Column(name = "contained_in")
   private String containedIn;

   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "stop_area_id")
   private StopArea stopArea;

   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "line_id")
   private Line line;

   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "connection_link_id")
   private ConnectionLink connectionLink;

   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "stop_point_id")
   private StopPoint stopPoint;

   @Getter
   @Setter
   private List<FacilityFeature> facilityFeatures;

   /**
    * Field containedInStopArea.
    */
   @Getter
   @Setter
   @Transient
   private StopArea containedInStopArea;

   /**
    * Attached StopArea ObjectId when Facility concern a StopArea <br/>
    * (Import/Export purpose) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String stopAreaId;

   /**
    * Attached Line ObjectId when Facility concern a Line <br/>
    * (Import/Export purpose) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String lineId;

   /**
    * Attached ConnectionLink ObjectId when Facility concern a ConnectionLink <br/>
    * (Import/Export purpose) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String connectionLinkId;

   /**
    * Attached StopPoint ObjectId when Facility concern a StopPoint <br/>
    * (Import/Export purpose) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String stopPointId;

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

   /**
    * add a new feature if not already present
    * 
    * @param facilityFeature
    *           teature to be added
    */
   public void addFacilityFeature(FacilityFeature facilityFeature)
   {
      if (facilityFeatures == null)
         facilityFeatures = new ArrayList<FacilityFeature>();
      if (!facilityFeatures.contains(facilityFeature))
         facilityFeatures.add(facilityFeature);
   }

   /**
    * remove a feature
    * 
    * @param facilityFeature
    *           feature to remove
    */
   public void removeFacilityFeature(FacilityFeature facilityFeature)
   {
      if (facilityFeatures == null)
         facilityFeatures = new ArrayList<FacilityFeature>();
      if (facilityFeatures.contains(facilityFeature))
         facilityFeatures.remove(facilityFeature);
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

      containedIn = (containedInStopArea != null) ? containedInStopArea
            .getObjectId() : null;

      stopAreaId = (stopArea != null) ? stopArea.getObjectId() : null;
      stopPointId = (stopPoint != null) ? stopPoint.getObjectId() : null;
      connectionLinkId = (connectionLink != null) ? connectionLink
            .getObjectId() : null;
      lineId = (line != null) ? line.getObjectId() : null;
   }

   @Override
   public <T extends NeptuneObject> boolean compareAttributes(T anotherObject)
   {
      if (anotherObject instanceof Facility)
      {
         Facility another = (Facility) anotherObject;
         if (!sameValue(this.getObjectId(), another.getObjectId()))
            return false;
         if (!sameValue(this.getObjectVersion(), another.getObjectVersion()))
            return false;
         if (!sameValue(this.getName(), another.getName()))
            return false;
         if (!sameValue(this.getComment(), another.getComment()))
            return false;
         if (!sameValue(this.getRegistrationNumber(),
               another.getRegistrationNumber()))
            return false;
         if (!sameValue(this.getCountryCode(), another.getCountryCode()))
            return false;
         if (!sameValue(this.getStreetName(), another.getStreetName()))
            return false;
         if (!sameValue(this.getLatitude(), another.getLatitude()))
            return false;
         if (!sameValue(this.getLongitude(), another.getLongitude()))
            return false;
         if (!sameValue(this.getLongLatType(), another.getLongLatType()))
            return false;
         if (!sameValue(this.getProjectionType(), another.getProjectionType()))
            return false;
         if (!sameValue(this.getX(), another.getX()))
            return false;
         if (!sameValue(this.getY(), another.getY()))
            return false;

         if (!sameValue(this.getDescription(), another.getDescription()))
            return false;
         if (!sameValue(this.getFreeAccess(), another.getFreeAccess()))
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
      return null;
   }

}
