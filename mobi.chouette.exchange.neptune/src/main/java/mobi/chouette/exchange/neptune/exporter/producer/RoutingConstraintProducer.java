package mobi.chouette.exchange.neptune.exporter.producer;

import mobi.chouette.model.Line;
import mobi.chouette.model.StopArea;

import org.trident.schema.trident.ITLType;

public class RoutingConstraintProducer
{
   /**
    * @param line
    * @param routingConstraint
    * @return
    */
   public ITLType produceITL(Line line, StopArea routingConstraint, boolean addExtension)
   {
      ITLType jaxbITL = AbstractJaxbNeptuneProducer.tridentFactory
            .createITLType();

      jaxbITL.setName(routingConstraint.getName());
      jaxbITL.setLineIdShortCut(line.getObjectId());
      jaxbITL.setAreaId(routingConstraint.getObjectId());

      return jaxbITL;
   }

}
