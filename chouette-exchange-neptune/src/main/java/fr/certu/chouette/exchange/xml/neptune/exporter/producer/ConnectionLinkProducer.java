package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import java.util.ArrayList;
import java.util.List;

import org.trident.schema.trident.ConnectionLinkExtensionType;
import org.trident.schema.trident.ConnectionLinkTypeType;
import org.trident.schema.trident.ChouettePTNetworkType;
import org.trident.schema.trident.ConnectionLinkExtensionType.AccessibilitySuitabilityDetails;

import uk.org.ifopt.acsb.EncumbranceEnumeration;
import uk.org.ifopt.acsb.MedicalNeedEnumeration;
import uk.org.ifopt.acsb.MobilityEnumeration;
import uk.org.ifopt.acsb.PyschosensoryNeedEnumeration;
import uk.org.ifopt.acsb.UserNeedStructure;

import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.type.ConnectionLinkTypeEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;

public class ConnectionLinkProducer
      extends
      AbstractJaxbNeptuneProducer<ChouettePTNetworkType.ConnectionLink, ConnectionLink>
{

   @Override
   public ChouettePTNetworkType.ConnectionLink produce(
         ConnectionLink connectionLink)
   {
      ChouettePTNetworkType.ConnectionLink jaxbConnectionLink = tridentFactory
            .createChouettePTNetworkTypeConnectionLink();

      //
      populateFromModel(jaxbConnectionLink, connectionLink);

      jaxbConnectionLink.setComment(getNotEmptyString(connectionLink
            .getComment()));
      jaxbConnectionLink.setName(connectionLink.getName());
      jaxbConnectionLink.setStartOfLink(connectionLink.getStartOfLinkId());
      jaxbConnectionLink.setEndOfLink(connectionLink.getEndOfLinkId());
      jaxbConnectionLink.setLinkDistance(connectionLink.getLinkDistance());
      if (connectionLink.isMobilityRestrictedSuitable())
         jaxbConnectionLink.setMobilityRestrictedSuitability(true);
      if (connectionLink.isLiftAvailable())
         jaxbConnectionLink.setLiftAvailability(true);
      if (connectionLink.isStairsAvailable())
         jaxbConnectionLink.setStairsAvailability(true);
      if (connectionLink.getDefaultDuration() != null)
      {
         jaxbConnectionLink.setDefaultDuration(toDuration(connectionLink
               .getDefaultDuration()));
      }
      if (connectionLink.getFrequentTravellerDuration() != null)
      {
         jaxbConnectionLink
               .setFrequentTravellerDuration(toDuration(connectionLink
                     .getFrequentTravellerDuration()));
      }
      if (connectionLink.getOccasionalTravellerDuration() != null)
      {
         jaxbConnectionLink
               .setOccasionalTravellerDuration(toDuration(connectionLink
                     .getOccasionalTravellerDuration()));
      }
      if (connectionLink.getMobilityRestrictedTravellerDuration() != null)
      {
         jaxbConnectionLink
               .setMobilityRestrictedTravellerDuration(toDuration(connectionLink
                     .getMobilityRestrictedTravellerDuration()));
      }

      try
      {
         ConnectionLinkTypeEnum linkType = connectionLink.getLinkType();
         if (linkType != null)
         {
            jaxbConnectionLink.setLinkType(ConnectionLinkTypeType
                  .fromValue(linkType.name()));
         }
      } catch (IllegalArgumentException e)
      {
         // TODO generate report
      }

      ConnectionLinkExtensionType connectionLinkExtension = tridentFactory
            .createConnectionLinkExtensionType();
      AccessibilitySuitabilityDetails details = extractAccessibilitySuitabilityDetails(connectionLink
            .getUserNeeds());
      if (details != null)
      {
         connectionLinkExtension.setAccessibilitySuitabilityDetails(details);
         jaxbConnectionLink.setConnectionLinkExtension(connectionLinkExtension);
      }

      return jaxbConnectionLink;
   }

   protected AccessibilitySuitabilityDetails extractAccessibilitySuitabilityDetails(
         List<UserNeedEnum> userNeeds)
   {
      AccessibilitySuitabilityDetails details = new AccessibilitySuitabilityDetails();
      List<UserNeedStructure> detailsItems = new ArrayList<UserNeedStructure>();
      if (userNeeds != null)
      {
         for (UserNeedEnum userNeed : userNeeds)
         {
            if (userNeed != null)
            {
               UserNeedStructure userNeedGroup = new UserNeedStructure();

               switch (userNeed.category())
               {
               case ENCUMBRANCE:
                  userNeedGroup.setEncumbranceNeed(EncumbranceEnumeration
                        .fromValue(userNeed.value()));
                  break;
               case MEDICAL:
                  userNeedGroup.setMedicalNeed(MedicalNeedEnumeration
                        .fromValue(userNeed.value()));
                  break;
               case PSYCHOSENSORY:
                  userNeedGroup
                        .setPsychosensoryNeed(PyschosensoryNeedEnumeration
                              .fromValue(userNeed.value()));
                  break;
               case MOBILITY:
                  userNeedGroup.setMobilityNeed(MobilityEnumeration
                        .fromValue(userNeed.value()));
                  break;
               default:
                  throw new IllegalArgumentException("bad value of userNeed");
               }

               detailsItems.add(userNeedGroup);

            }
         }
      }

      if (detailsItems.isEmpty())
         return null;
      details.getMobilityNeedOrPsychosensoryNeedOrMedicalNeed().addAll(
            detailsItems);
      return details;
   }

}
