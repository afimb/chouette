package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import chouette.schema.ITL;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.StopArea;

public class RoutingConstraintProducer
{
   /**
    * @param line
    * @param routingConstraint
    * @return
    */
   public ITL produceITL(Line line, StopArea routingConstraint)
   {
      ITL castorITL = new ITL();

      castorITL.setName(routingConstraint.getName());
      castorITL.setLineIdShortCut(line.getObjectId());
      castorITL.setAreaId(routingConstraint.getObjectId());

      return castorITL;
   }

}
