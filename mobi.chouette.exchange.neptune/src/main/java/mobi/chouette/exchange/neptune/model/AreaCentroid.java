package mobi.chouette.exchange.neptune.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobi.chouette.model.NeptuneLocalizedObject;

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
public class AreaCentroid extends NeptuneLocalizedObject
{

   /**
	 * 
	 */
	private static final long serialVersionUID = -7188145272527483007L;

	@Getter @Setter
	private Long id;
	
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
    * containedInId
    * 
    * @param containedInId
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   private String containedInId;



}
