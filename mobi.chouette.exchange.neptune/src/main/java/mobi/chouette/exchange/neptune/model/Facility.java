package mobi.chouette.exchange.neptune.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobi.chouette.exchange.neptune.model.facility.FacilityFeature;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.Line;
import mobi.chouette.model.NeptuneLocalizedObject;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;

/**
 * Chouette Facility : specific feature on different kind of Public Transport
 * elements
 * <br/>
 * Note: this object is only used for Neptune import and validation purpose
 * <p/>
 * Neptune mapping : ChouetteFacility <br/>
 * Gtfs mapping : none <br/>
 */
@NoArgsConstructor

public class Facility extends NeptuneLocalizedObject
{
   private static final long serialVersionUID = -2150117548707325330L;

   @Getter
	@Setter
	@GenericGenerator(name = "facilities_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", 
		parameters = {
			@Parameter(name = "sequence_name", value = "facilities_id_seq"),
			@Parameter(name = "increment_size", value = "100") })
	@Id
	@GeneratedValue(generator = "facilities_id_seq")
	@Column(name = "id", nullable = false)
	protected Long id;
   
   /**
    * name
    * 
    * @return The actual value
    */
   @Getter
   @Setter
   private String name;

   /**
    * comment
    * 
    * @return The actual value
    */
   @Getter
   @Setter
   private String comment;

   /**
    * description
    * 
    * @return The actual value
    */
   @Getter
   @Setter
   private String description;

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
   private Boolean freeAccess;

   /**
    * container : TBD
    * 
    * @return The actual value
    */
   @Getter
   @Setter
   private String containedIn;

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


}
