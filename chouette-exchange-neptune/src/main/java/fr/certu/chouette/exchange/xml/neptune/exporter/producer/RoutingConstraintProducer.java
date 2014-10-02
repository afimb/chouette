package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import org.trident.schema.trident.ITLType;

import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.StopArea;

public class RoutingConstraintProducer
{
   /**
    * @param line
    * @param routingConstraint
    * @return
    */
   public ITLType produceITL(Line line, StopArea routingConstraint)
   {
      ITLType jaxbITL = AbstractJaxbNeptuneProducer.tridentFactory
            .createITLType();

      jaxbITL.setName(routingConstraint.getName());
      jaxbITL.setLineIdShortCut(line.getObjectId());
      jaxbITL.setAreaId(routingConstraint.getObjectId());

      return jaxbITL;
   }

}
