package mobi.chouette.exchange.neptune.exporter.producer;

import mobi.chouette.exchange.neptune.model.PTLink;

import org.trident.schema.trident.PTLinkType;

public class PTLinkProducer extends
      AbstractJaxbNeptuneProducer<PTLinkType, PTLink>
{

   //@Override
   public PTLinkType produce(PTLink ptLink, boolean addExtension)
   {
      PTLinkType jaxbPTLink = tridentFactory.createPTLinkType();

      //
      populateFromModel(jaxbPTLink, ptLink);

      jaxbPTLink.setName(ptLink.getName());
      jaxbPTLink.setStartOfLink(getNonEmptyObjectId(ptLink.getStartOfLink()));
      jaxbPTLink.setEndOfLink(getNonEmptyObjectId(ptLink.getEndOfLink()));
      jaxbPTLink.setLinkDistance(ptLink.getLinkDistance());

      return jaxbPTLink;
   }

}
