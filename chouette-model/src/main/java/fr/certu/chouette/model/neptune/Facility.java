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
 * Chouette Facility : specific feature on different kind of Public Transport
 * elements
 * <br/>
 * Note: this object is only used for Neptune import and validation purpose
 * <p/>
 * Neptune mapping : ChouetteFacility <br/>
 * Gtfs mapping : none <br/>
 */
@Entity
@Table(name = "facilities")
@NoArgsConstructor
@Log4j
public class Facility extends NeptuneLocalizedObject
{
   private static final long serialVersionUID = -2150117548707325330L;

   /**
    * name
    * 
    * @return The actual value
    */
   @Getter
   @Column(name = "name")
   private String name;

   /**
    * set name <br/>
    * truncated to 255 characters if too long
    * 
    * @param value
    *           New value
    */
   public void setName(String value)
   {
      name = dataBaseSizeProtectedValue(value,"name",log);
   }

   /**
    * comment
    * 
    * @return The actual value
    */
   @Getter
   @Column(name = "comment")
   private String comment;
   /**
    * set comment <br/>
    * truncated to 255 characters if too long
    * 
    * @param value
    *           New value
    */
   public void setComment(String value)
   {
      comment = dataBaseSizeProtectedValue(value,"comment",log);
   }

   /**
    * description
    * 
    * @return The actual value
    */
   @Getter
   @Column(name = "description")
   private String description;
   /**
    * set description <br/>
    * truncated to 255 characters if too long
    * 
    * @param value
    *           New value
    */
   public void setDescription(String value)
   {
      description = dataBaseSizeProtectedValue(value,"description",log);
   }

   /**
    * free access : tell if this facility is available for anybody or not<br/>
    * description or comment should describe access rules if necessary
    * 
    * @param freeAccess
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Column(name = "free_access")
   private Boolean freeAccess;

   /**
    * container : TBD
    * 
    * @return The actual value
    */
   @Getter
   @Column(name = "contained_in")
   private String containedIn;
   /**
    * set container <br/>
    * truncated to 255 characters if too long
    * 
    * @param value
    *           New value
    */
   public void setContainedIn(String value)
   {
      containedIn = dataBaseSizeProtectedValue(value,"containedIn",log);
   }

   /**
    * attached stop area <br/>
    * exclusive relation <br/>
    * if set, line, connectionLink and stopPoint must be null
    * 
    * @param stopArea
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "stop_area_id")
   private StopArea stopArea;

   /**
    * attached line <br/>
    * exclusive relation <br/>
    * if set, stopArea, connectionLink and stopPoint must be null
    * 
    * @param line
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "line_id")
   private Line line;

   /**
    * attached connection link <br/>
    * exclusive relation <br/>
    * if set, line, stopArea and stopPoint must be null
    * 
    * @param connectionLink
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "connection_link_id")
   private ConnectionLink connectionLink;

   /**
    * attached stopPoint <br/>
    * exclusive relation <br/>
    * if set, line, stopArea and connectionLink must be null
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
    * facility features 
    * 
    * @param facilityFeatures
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   private List<FacilityFeature> facilityFeatures;

   /**
    * containedInStopArea : TBD
    * @param containedInStopArea
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Transient
   private StopArea containedInStopArea;

   /**
    * Attached StopArea ObjectId when Facility concern a StopArea <br/>
    * (Import/Export purpose) 
    * 
    * @param stopAreaId
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   private String stopAreaId;

   /**
    * Attached Line ObjectId when Facility concern a Line <br/>
    * (Import/Export purpose) 
    * 
    * @param lineId
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   private String lineId;

   /**
    * Attached ConnectionLink ObjectId when Facility concern a ConnectionLink <br/>
    * 
    * @param connectionLinkId
    *           New value
    * @return The actual value
    * (Import/Export purpose) 
    */
   @Getter
   @Setter
   private String connectionLinkId;

   /**
    * Attached StopPoint ObjectId when Facility concern a StopPoint <br/>
    * 
    * @param stopPointId
    *           New value
    * @return The actual value
    * (Import/Export purpose) 
    */
   @Getter
   @Setter
   private String stopPointId;

   /**
    * add a new feature if not already present
    * 
    * @param facilityFeature
    *           feature to be added
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

   /* (non-Javadoc)
    * @see fr.certu.chouette.model.neptune.NeptuneObject#compareAttributes(fr.certu.chouette.model.neptune.NeptuneObject)
    */
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

   /* (non-Javadoc)
    * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#toURL()
    */
   @Override
   public String toURL()
   {
      return null;
   }

}
