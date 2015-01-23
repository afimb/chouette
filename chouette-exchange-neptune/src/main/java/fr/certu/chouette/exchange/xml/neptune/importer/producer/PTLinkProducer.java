package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import org.trident.schema.trident.PTLinkType;

import fr.certu.chouette.exchange.xml.neptune.importer.Context;
import fr.certu.chouette.model.neptune.PTLink;

@SuppressWarnings("deprecation")
public class PTLinkProducer extends AbstractModelProducer<PTLink, PTLinkType>
{

   @Override
   public PTLink produce(Context context, PTLinkType xmlPTLink)
   {
      PTLink ptLink = new PTLink();

      // objectId, objectVersion, creatorId, creationTime
      populateFromCastorNeptune(context, ptLink, xmlPTLink);

      // Name optional : unused and unchecked
      // ptLink.setName(getNonEmptyTrimedString(xmlPTLink.getName()));

      // Comment optional : unused and unchecked
      // ptLink.setComment(getNonEmptyTrimedString(xmlPTLink.getComment2()));

      // StartOfLink mandatory
      ptLink.setStartOfLinkId(getNonEmptyTrimedString(xmlPTLink
            .getStartOfLink()));

      // EndOfLink mandatory
      ptLink.setEndOfLinkId(getNonEmptyTrimedString(xmlPTLink.getEndOfLink()));

      // LinkDistance optional
      ptLink.setLinkDistance(xmlPTLink.getLinkDistance());

      // return null if in conflict with other files, else return object
      return checkUnsharedData(context, ptLink, xmlPTLink);
   }

}
