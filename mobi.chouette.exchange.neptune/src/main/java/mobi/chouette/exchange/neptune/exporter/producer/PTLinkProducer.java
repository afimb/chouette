package mobi.chouette.exchange.neptune.exporter.producer;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.model.PTLink;

import org.trident.schema.trident.PTLinkType;

public class PTLinkProducer extends
      AbstractJaxbNeptuneProducer<PTLinkType, PTLink>
{

   //@Override
   public PTLinkType produce(Context context, PTLink ptLink, boolean addExtension)
   {
      PTLinkType jaxbPTLink = tridentFactory.createPTLinkType();

      //
      populateFromModel(context, jaxbPTLink, ptLink);

      jaxbPTLink.setName(ptLink.getName());
      jaxbPTLink.setStartOfLink(getNonEmptyObjectId(context, ptLink.getStartOfLink()));
      jaxbPTLink.setEndOfLink(getNonEmptyObjectId(context, ptLink.getEndOfLink()));
      jaxbPTLink.setLinkDistance(ptLink.getLinkDistance());

      return jaxbPTLink;
   }

}
