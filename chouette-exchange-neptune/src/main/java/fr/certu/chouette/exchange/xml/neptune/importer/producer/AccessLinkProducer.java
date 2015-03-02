package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import org.trident.schema.trident.ConnectionLinkExtensionType;

import fr.certu.chouette.exchange.xml.neptune.importer.Context;
import fr.certu.chouette.model.neptune.AccessLink;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.type.ConnectionLinkTypeEnum;
import fr.certu.chouette.model.neptune.type.LinkOrientationEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;

/**
 * 
 * @author mamadou keira
 * 
 */
public class AccessLinkProducer
      extends
      AbstractModelProducer<AccessLink, org.trident.schema.trident.ChouettePTNetworkType.AccessLink>
{

   @Override
   public AccessLink produce(Context context,
         org.trident.schema.trident.ChouettePTNetworkType.AccessLink xmlAccessLink)
   {
      AccessLink accessLink = new AccessLink();
      // objectId, objectVersion, creatorId, creationTime
      populateFromCastorNeptune(context,accessLink, xmlAccessLink);
      // Name optional
      accessLink.setName(getNonEmptyTrimedString(xmlAccessLink.getName()));

      // Comment optional
      accessLink
            .setComment(getNonEmptyTrimedString(xmlAccessLink.getComment()));

      // StartOfLink mandatory
      accessLink.setStartOfLinkId(getNonEmptyTrimedString(xmlAccessLink
            .getStartOfLink()));

      // EndOfLink mandatory
      accessLink.setEndOfLinkId(getNonEmptyTrimedString(xmlAccessLink
            .getEndOfLink()));

      // LinkDistance optional
      accessLink.setLinkDistance(xmlAccessLink.getLinkDistance());

      // LiftAvailability optional
      if (xmlAccessLink.isSetLiftAvailability())
         accessLink.setLiftAvailable(xmlAccessLink.isLiftAvailability());

      // MobilityRestrictedSuitability optional
      if (xmlAccessLink.isSetMobilityRestrictedSuitability())
         accessLink.setMobilityRestrictedSuitable(xmlAccessLink
               .isMobilityRestrictedSuitability());

      // StairsAvailability optional
      if (xmlAccessLink.isSetStairsAvailability())
         accessLink.setStairsAvailable(xmlAccessLink.isStairsAvailability());

      // accessLinkExtension optional
      ConnectionLinkExtensionType xmlConnectionLinkExtension = xmlAccessLink
            .getConnectionLinkExtension();
      if (xmlConnectionLinkExtension != null)
      {
         if (xmlConnectionLinkExtension.getAccessibilitySuitabilityDetails() != null)
         {
            for (Object xmlAccessibilitySuitabilityDetailsItem : xmlConnectionLinkExtension
                  .getAccessibilitySuitabilityDetails()
                  .getMobilityNeedOrPsychosensoryNeedOrMedicalNeed())
            {
               try
               {
                  accessLink.addUserNeed(UserNeedEnum
                        .fromValue(xmlAccessibilitySuitabilityDetailsItem
                              .toString()));
               } catch (IllegalArgumentException e)
               {
                  // TODO: traiter le cas de non correspondance
               }

            }
         }
      }

      // DefaultDuration optional
      accessLink
            .setDefaultDuration(getTime(xmlAccessLink.getDefaultDuration()));

      // FrequentTravellerDuration optional
      accessLink.setFrequentTravellerDuration(getTime(xmlAccessLink
            .getFrequentTravellerDuration()));

      // OccasionalTravellerDuration optional
      accessLink.setOccasionalTravellerDuration(getTime(xmlAccessLink
            .getOccasionalTravellerDuration()));

      // MobilityRestrictedTravellerDuration optional
      accessLink.setMobilityRestrictedTravellerDuration(getTime(xmlAccessLink
            .getMobilityRestrictedTravellerDuration()));

      // LinkType optional
      if (xmlAccessLink.getLinkType() != null)
      {
         try
         {
            accessLink.setLinkType(ConnectionLinkTypeEnum.valueOf(xmlAccessLink
                  .getLinkType().value()));
         } catch (IllegalArgumentException e)
         {
            // TODO: traiter le cas de non correspondance
         }
      }

      // produce link orientation on startOflink type
      if (accessLink.getStartOfLinkId() != null
            && accessLink.getStartOfLinkId().contains(
                  ":" + AccessPoint.ACCESSPOINT_KEY + ":"))
      {
         accessLink
               .setLinkOrientation(LinkOrientationEnum.AccessPointToStopArea);
      } else
      {
         accessLink
               .setLinkOrientation(LinkOrientationEnum.StopAreaToAccessPoint);
      }
      
      AccessLink sharedBean = getOrAddSharedData(context, accessLink, xmlAccessLink);
      if (sharedBean != null)
         return sharedBean;

      return accessLink;
   }

}
