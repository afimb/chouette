package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import java.util.List;

import org.trident.schema.trident.JourneyPatternType;

import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.plugin.exchange.SharedImportedData;
import fr.certu.chouette.plugin.exchange.UnsharedImportedData;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;

public class JourneyPatternProducer extends
      AbstractModelProducer<JourneyPattern, JourneyPatternType>
{

   @Override
   public JourneyPattern produce(String sourceFile,
         JourneyPatternType xmlJourneyPattern, ReportItem importReport,
         PhaseReportItem validationReport, SharedImportedData sharedData,
         UnsharedImportedData unshareableData)
   {
      JourneyPattern journeyPattern = new JourneyPattern();

      // objectId, objectVersion, creatorId, creationTime
      populateFromCastorNeptune(journeyPattern, xmlJourneyPattern, importReport);

      // Name optional
      journeyPattern.setName(getNonEmptyTrimedString(xmlJourneyPattern
            .getName()));

      // PublishedName optional
      journeyPattern.setPublishedName(getNonEmptyTrimedString(xmlJourneyPattern
            .getPublishedName()));

      // RouteId mandatory
      journeyPattern.setRouteId(getNonEmptyTrimedString(xmlJourneyPattern
            .getRouteId()));

      // Origin optional
      journeyPattern.setOrigin(getNonEmptyTrimedString(xmlJourneyPattern
            .getOrigin()));

      // Destination optional
      journeyPattern.setDestination(getNonEmptyTrimedString(xmlJourneyPattern
            .getDestination()));

      // StopPointIds [2..w]
      List<String> castorStopPointIds = xmlJourneyPattern.getStopPointList();
      for (String castorStopPointId : castorStopPointIds)
      {
         String stopPointId = getNonEmptyTrimedString(castorStopPointId);
         if (stopPointId == null)
         {
            // TODO tracer
         } else
         {
            journeyPattern.addStopPointId(stopPointId);
         }
      }

      // RegistrationNumber optional
      journeyPattern.setRegistrationNumber(getRegistrationNumber(
            xmlJourneyPattern.getRegistration(), importReport));

      // Comment optional
      journeyPattern.setComment(getNonEmptyTrimedString(xmlJourneyPattern
            .getComment()));

      // lineIdShortCut
      journeyPattern
            .setLineIdShortcut(getNonEmptyTrimedString(xmlJourneyPattern
                  .getLineIdShortcut()));

      return journeyPattern;
   }

}
