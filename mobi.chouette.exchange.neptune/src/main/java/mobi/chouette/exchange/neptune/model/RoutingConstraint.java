package mobi.chouette.exchange.neptune.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobi.chouette.model.Line;
import mobi.chouette.model.NeptuneIdentifiedObject;
import mobi.chouette.model.StopArea;

@NoArgsConstructor
public class RoutingConstraint extends NeptuneIdentifiedObject
{

   /**
	 * 
	 */
	private static final long serialVersionUID = 6775023762215687677L;

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
    * line
    * 
    * @param line
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   private Line line;

   /**
    * area
    * 
    * @param area
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   private StopArea area;

@Override
public Long getId() {
	// TODO Auto-generated method stub
	return null;
}



}
