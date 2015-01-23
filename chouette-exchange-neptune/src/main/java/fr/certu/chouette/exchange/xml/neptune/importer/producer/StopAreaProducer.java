package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;
import fr.certu.chouette.exchange.xml.neptune.importer.Context;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;

@Log4j
public class StopAreaProducer
      extends
      AbstractModelProducer<StopArea, org.trident.schema.trident.ChouettePTNetworkType.ChouetteArea.StopArea>
{
   @Override
   public StopArea produce(Context context,
         org.trident.schema.trident.ChouettePTNetworkType.ChouetteArea.StopArea xmlStopArea)
   {
      StopArea stopArea = new StopArea();

      // objectId, objectVersion, creatorId, creationTime
      populateFromCastorNeptune(context, stopArea, xmlStopArea);

      // AreaCentroid optional
      stopArea.setAreaCentroidId(getNonEmptyTrimedString(xmlStopArea
            .getCentroidOfArea()));

      // Name optional
      stopArea.setName(getNonEmptyTrimedString(xmlStopArea.getName()));

      // Comment optional
      stopArea.setComment(getNonEmptyTrimedString(xmlStopArea.getComment()));

      // BoundaryPoints [0..w] : out of Neptune scope

      // StopAreaExtension optional
      org.trident.schema.trident.StopAreaExtensionType xmlStopAreaExtension = xmlStopArea
            .getStopAreaExtension();
      if (xmlStopAreaExtension != null)
      {
         // AreaType mandatory
         if (xmlStopAreaExtension.getAreaType() != null)
         {
            try
            {
               stopArea.setAreaType(ChouetteAreaEnum
                     .valueOf(xmlStopAreaExtension.getAreaType().value()));
            } catch (IllegalArgumentException e)
            {
               // TODO: traiter le cas de non correspondance
               e.printStackTrace();
            }
         }

         // FareCode optional
         stopArea.setFareCode(xmlStopAreaExtension.getFareCode());

         // LiftAvailability optional
         if (xmlStopAreaExtension.isSetLiftAvailability())
            stopArea
                  .setLiftAvailable(xmlStopAreaExtension.isLiftAvailability());

         // MobilityRestrictedSuitability
         if (xmlStopAreaExtension.isSetMobilityRestrictedSuitability())
            stopArea.setMobilityRestrictedSuitable(xmlStopAreaExtension
                  .isMobilityRestrictedSuitability());

         // NearestTopicName optional
         stopArea
               .setNearestTopicName(getNonEmptyTrimedString(xmlStopAreaExtension
                     .getNearestTopicName()));

         // RegistrationNumber optional
         stopArea.setRegistrationNumber(getRegistrationNumber(context,
               xmlStopAreaExtension.getRegistration()));

         // StairsAvailability optional
         if (xmlStopAreaExtension.isSetStairsAvailability())
            stopArea.setStairsAvailable(xmlStopAreaExtension
                  .isStairsAvailability());

         if (xmlStopAreaExtension.getAccessibilitySuitabilityDetails() != null)
         {
            for (Object xmlAccessibilitySuitabilityDetailsItem : xmlStopAreaExtension
                  .getAccessibilitySuitabilityDetails()
                  .getMobilityNeedOrPsychosensoryNeedOrMedicalNeed())
            {
               try
               {
                  stopArea.addUserNeed(UserNeedEnum
                        .fromValue(xmlAccessibilitySuitabilityDetailsItem
                              .toString()));
               } catch (IllegalArgumentException e)
               {
                  log.error("unknown userneeds enum "
                        + xmlAccessibilitySuitabilityDetailsItem.toString());
                  ReportItem item = new ExchangeReportItem(
                        ExchangeReportItem.KEY.UNKNOWN_ENUM,
                        Report.STATE.ERROR, "UserNeed",
                        xmlAccessibilitySuitabilityDetailsItem.toString());
                  context.getImportReport().addItem(item);
               }

            }
         }
      }

      List<String> contains = new ArrayList<String>(xmlStopArea.getContains());
      xmlStopArea.getContains().clear();
      // remove unrelevant attributs
      xmlStopArea.unsetBoundaryPoint();

      StopArea sharedBean = getOrAddSharedData(context, stopArea, xmlStopArea);
      if (sharedBean != null)
         stopArea = sharedBean;

      xmlStopArea.getContains().addAll(contains);

      // ContainedStopIds [1..w]
      for (String containedStopId : contains)
      {
         stopArea.addContainedStopId(getNonEmptyTrimedString(containedStopId));
      }
      return stopArea;
   }

}
