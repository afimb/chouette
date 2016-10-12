package mobi.chouette.exchange.neptune.exporter.producer;

import mobi.chouette.model.Line;
import mobi.chouette.model.RoutingConstraint;

import org.trident.schema.trident.ITLType;

public class ITLProducer
{
   /**
    * @param line
    * @param routingConstraint
    * @return
    */
   public ITLType produce(Line line, RoutingConstraint routingConstraint)
   {
      ITLType jaxbITL = AbstractJaxbNeptuneProducer.tridentFactory
            .createITLType();

      jaxbITL.setName(routingConstraint.getName());
      jaxbITL.setLineIdShortCut(line.getObjectId());
      jaxbITL.setAreaId(routingConstraint.getObjectId());

      return jaxbITL;
   }

}
