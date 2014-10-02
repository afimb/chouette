package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import java.util.ArrayList;
import java.util.List;

import org.trident.schema.trident.ChouettePTNetworkType;
import org.trident.schema.trident.ConnectionLinkExtensionType;
import org.trident.schema.trident.ConnectionLinkExtensionType.AccessibilitySuitabilityDetails;
import org.trident.schema.trident.ConnectionLinkTypeType;

import uk.org.ifopt.acsb.EncumbranceEnumeration;
import uk.org.ifopt.acsb.MedicalNeedEnumeration;
import uk.org.ifopt.acsb.MobilityEnumeration;
import uk.org.ifopt.acsb.PyschosensoryNeedEnumeration;
import uk.org.ifopt.acsb.UserNeedStructure;

import fr.certu.chouette.model.neptune.AccessLink;
import fr.certu.chouette.model.neptune.type.ConnectionLinkTypeEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;

public class AccessLinkProducer extends
      AbstractJaxbNeptuneProducer<ChouettePTNetworkType.AccessLink, AccessLink>
{

   @Override
   public ChouettePTNetworkType.AccessLink produce(AccessLink accessLink)
   {
      ChouettePTNetworkType.AccessLink jaxbAccessLink = tridentFactory
            .createChouettePTNetworkTypeAccessLink();

      //
      populateFromModel(jaxbAccessLink, accessLink);

      jaxbAccessLink.setComment(getNotEmptyString(accessLink.getComment()));
      jaxbAccessLink.setName(accessLink.getName());
      jaxbAccessLink.setStartOfLink(accessLink.getStartOfLinkId());
      jaxbAccessLink.setEndOfLink(accessLink.getEndOfLinkId());
      jaxbAccessLink.setLinkDistance(accessLink.getLinkDistance());
      jaxbAccessLink.setMobilityRestrictedSuitability(accessLink
            .isMobilityRestrictedSuitable());
      jaxbAccessLink.setLiftAvailability(accessLink.isLiftAvailable());
      jaxbAccessLink.setStairsAvailability(accessLink.isStairsAvailable());
      if (accessLink.getDefaultDuration() != null)
      {
         jaxbAccessLink.setDefaultDuration(toDuration(accessLink
               .getDefaultDuration()));
      }
      if (accessLink.getFrequentTravellerDuration() != null)
      {
         jaxbAccessLink.setFrequentTravellerDuration(toDuration(accessLink
               .getFrequentTravellerDuration()));
      }
      if (accessLink.getOccasionalTravellerDuration() != null)
      {
         jaxbAccessLink.setOccasionalTravellerDuration(toDuration(accessLink
               .getOccasionalTravellerDuration()));
      }
      if (accessLink.getMobilityRestrictedTravellerDuration() != null)
      {
         jaxbAccessLink
               .setMobilityRestrictedTravellerDuration(toDuration(accessLink
                     .getMobilityRestrictedTravellerDuration()));
      }

      try
      {
         ConnectionLinkTypeEnum linkType = accessLink.getLinkType();
         if (linkType != null)
         {
            jaxbAccessLink.setLinkType(ConnectionLinkTypeType
                  .fromValue(linkType.name()));
         }
      } catch (IllegalArgumentException e)
      {
         // TODO generate report
      }

      ConnectionLinkExtensionType connectionLinkExtension = tridentFactory
            .createConnectionLinkExtensionType();
      AccessibilitySuitabilityDetails details = extractAccessibilitySuitabilityDetails(accessLink
            .getUserNeeds());
      if (details != null)
      {
         connectionLinkExtension.setAccessibilitySuitabilityDetails(details);
         jaxbAccessLink.setConnectionLinkExtension(connectionLinkExtension);
      }

      return jaxbAccessLink;
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
