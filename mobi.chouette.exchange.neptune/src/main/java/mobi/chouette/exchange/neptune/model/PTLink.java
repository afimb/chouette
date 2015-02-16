package mobi.chouette.exchange.neptune.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobi.chouette.model.NeptuneIdentifiedObject;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopPoint;

/**
 * Chouette PTLink : a link between 2 successive StopPoints in a route
 * <br/>
 * Note: this object is only used for Neptune import, export and validation purpose
 * <p/>
 * <p/>
 * Neptune mapping : PtLink <br/>
 * Gtfs mapping : none <br/>
 * 
 */
@NoArgsConstructor
public class PTLink extends NeptuneIdentifiedObject
{
   private static final long serialVersionUID = -3089442100133439163L;

   @Getter
	@Setter
	@GenericGenerator(name = "pt_links_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", 
		parameters = {
			@Parameter(name = "sequence_name", value = "pt_links_id_seq"),
			@Parameter(name = "increment_size", value = "100") })
	@Id
	@GeneratedValue(generator = "pt_links_id_seq")
	@Column(name = "id", nullable = false)
	protected Long id;
   
   /**
    * name
    * 
    * @param name
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   private String name;

   /**
    * comment
    * 
    * @param comment
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   private String comment;

   /**
    * link length in meters
    * 
    * @param linkDistance
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   private BigDecimal linkDistance;

   /**
    * start of link
    * 
    * @param startOfLink
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   private StopPoint startOfLink;

   /**
    * end of link
    * 
    * @param endOfLink
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   private StopPoint endOfLink;

   /**
    * route
    * 
    * @param route
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   private Route route;


}
