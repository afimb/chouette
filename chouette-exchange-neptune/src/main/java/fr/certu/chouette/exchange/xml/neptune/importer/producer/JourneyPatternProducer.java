package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import java.util.List;

import org.trident.schema.trident.JourneyPatternType;

import fr.certu.chouette.exchange.xml.neptune.importer.Context;
import fr.certu.chouette.model.neptune.JourneyPattern;

public class JourneyPatternProducer extends
      AbstractModelProducer<JourneyPattern, JourneyPatternType>
{

   @Override
   public JourneyPattern produce(Context context,
         JourneyPatternType xmlJourneyPattern)
   {
      JourneyPattern journeyPattern = new JourneyPattern();

      // objectId, objectVersion, creatorId, creationTime
      populateFromCastorNeptune(context, journeyPattern, xmlJourneyPattern);

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
      journeyPattern.setRegistrationNumber(getRegistrationNumber(context,
            xmlJourneyPattern.getRegistration()));

      // Comment optional
      journeyPattern.setComment(getNonEmptyTrimedString(xmlJourneyPattern
            .getComment()));

      // lineIdShortCut
      journeyPattern
            .setLineIdShortcut(getNonEmptyTrimedString(xmlJourneyPattern
                  .getLineIdShortcut()));
      
      // return null if in conflict with other files, else return object
      return checkUnsharedData(context, journeyPattern, xmlJourneyPattern);
   }

}
