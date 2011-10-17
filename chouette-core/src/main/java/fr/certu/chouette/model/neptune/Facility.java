package fr.certu.chouette.model.neptune;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.model.neptune.type.FacilityLocation;
import fr.certu.chouette.model.neptune.type.facility.FacilityFeature;

/**
 * Neptune Facility : specific feature on different kind of Public Transport
 * elements
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 */
public class Facility extends NeptuneIdentifiedObject
{
   private static final long     serialVersionUID = -2150117548707325330L;

   /**
    * Attached StopArea ObjectId when Facility concern a StopArea <br/>
    * (Import/Export purpose) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String                stopAreaId;
   /**
    * Attached StopArea when Facility concern a StopArea <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private StopArea              stopArea;
   /**
    * Attached Line ObjectId when Facility concern a Line <br/>
    * (Import/Export purpose) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String                lineId;
   /**
    * Attached Line when Facility concern a Line <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private Line                  line;
   /**
    * Attached ConnectionLink ObjectId when Facility concern a ConnectionLink <br/>
    * (Import/Export purpose) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String                connectionLinkId;
   /**
    * Attached ConnectionLink when Facility concern a ConnectionLink <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private ConnectionLink        connectionLink;
   /**
    * Attached StopPoint ObjectId when Facility concern a StopPoint <br/>
    * (Import/Export purpose) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String                stopPointId;
   /**
    * Attached StopPoint when Facility concern a StopPoint <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private StopPoint             stopPoint;
   /**
    * description <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String                description;
   /**
    * Is the access restricted or authorised to everybody <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private Boolean               freeAccess;
   /**
    * Comment <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String                comment;
   /**
    * Location <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private FacilityLocation      facilityLocation;
   /**
    * Features available for this facility <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private List<FacilityFeature> facilityFeatures;

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

      stopAreaId = (stopArea != null) ? stopArea.getObjectId() : null;
      stopPointId = (stopPoint != null) ? stopPoint.getObjectId() : null;
      connectionLinkId = (connectionLink != null) ? connectionLink.getObjectId() : null;
      lineId = (line != null) ? line.getObjectId() : null;
   }
}
