package mobi.chouette.exchange.neptune.exporter.model;

import java.math.BigDecimal;

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
