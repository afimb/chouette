package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import org.trident.schema.trident.PTLinkType;

import fr.certu.chouette.model.neptune.PTLink;

public class PTLinkProducer extends
      AbstractJaxbNeptuneProducer<PTLinkType, PTLink>
{

   @Override
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
