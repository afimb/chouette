package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import java.util.ArrayList;
import java.util.List;

import org.trident.schema.trident.ChouettePTNetworkType;
import org.trident.schema.trident.LineExtensionType;
import org.trident.schema.trident.TransportModeNameType;
import org.trident.schema.trident.LineExtensionType.AccessibilitySuitabilityDetails;

import uk.org.ifopt.acsb.EncumbranceEnumeration;
import uk.org.ifopt.acsb.MedicalNeedEnumeration;
import uk.org.ifopt.acsb.MobilityEnumeration;
import uk.org.ifopt.acsb.PyschosensoryNeedEnumeration;
import uk.org.ifopt.acsb.UserNeedStructure;

import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;

public class LineProducer
      extends
      AbstractJaxbNeptuneProducer<ChouettePTNetworkType.ChouetteLineDescription.Line, Line>
{

   @Override
   public ChouettePTNetworkType.ChouetteLineDescription.Line produce(Line line)
   {
      ChouettePTNetworkType.ChouetteLineDescription.Line jaxbLine = tridentFactory
            .createChouettePTNetworkTypeChouetteLineDescriptionLine();

      //
      populateFromModel(jaxbLine, line);

      jaxbLine.setComment(getNotEmptyString(line.getComment()));
      jaxbLine.setName(line.getName());
      jaxbLine.setNumber(line.getNumber());
      jaxbLine.setPublishedName(line.getPublishedName());
      jaxbLine.setPtNetworkIdShortcut(getNonEmptyObjectId(line.getPtNetwork()));

      try
      {
         TransportModeNameEnum transportModeName = line.getTransportModeName();
         if (transportModeName != null)
         {
            jaxbLine.setTransportModeName(TransportModeNameType
                  .fromValue(transportModeName.name()));
         }
      } catch (IllegalArgumentException e)
      {
         // TODO generate report
      }

      jaxbLine.setRegistration(getRegistration(line.getRegistrationNumber()));
      if (line.getLineEnds() != null)
      {
         jaxbLine.getLineEnd().addAll(line.getLineEnds());
      }
      jaxbLine.getRouteId().addAll(
            NeptuneIdentifiedObject.extractObjectIds(line.getRoutes()));

      boolean hasExtensions = false;
      LineExtensionType jaxbLineExtension = tridentFactory
            .createLineExtensionType();
      if (line.getUserNeeds() != null && !line.getUserNeeds().isEmpty())
      {
         jaxbLineExtension
               .setAccessibilitySuitabilityDetails(extractAccessibilitySuitabilityDetails(line
                     .getUserNeeds()));
         hasExtensions = true;
      }
      if (line.getMobilityRestrictedSuitable() != null
            && line.getMobilityRestrictedSuitable())
      {
         jaxbLineExtension.setMobilityRestrictedSuitability(line
               .getMobilityRestrictedSuitable());
         hasExtensions = true;
      }
      // jaxbLineExtension.setStableId(stableId); ???
      if (hasExtensions)
         jaxbLine.setLineExtension(jaxbLineExtension);

      return jaxbLine;
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
