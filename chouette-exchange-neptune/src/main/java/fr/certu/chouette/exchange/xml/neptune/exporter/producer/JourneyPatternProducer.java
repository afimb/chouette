package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import org.trident.schema.trident.JourneyPatternType;

import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

public class JourneyPatternProducer extends
      AbstractJaxbNeptuneProducer<JourneyPatternType, JourneyPattern>
{

   @Override
   public JourneyPatternType produce(JourneyPattern journeyPattern)
   {
      JourneyPatternType jaxbJourneyPattern = tridentFactory
            .createJourneyPatternType();

      //
      populateFromModel(jaxbJourneyPattern, journeyPattern);

      jaxbJourneyPattern.setComment(journeyPattern.getComment());
      jaxbJourneyPattern.setName(journeyPattern.getName());
      jaxbJourneyPattern.setPublishedName(journeyPattern.getPublishedName());
      jaxbJourneyPattern.setDestination(journeyPattern.getDestination());
      jaxbJourneyPattern.setLineIdShortcut(journeyPattern.getLineIdShortcut()); // FIXME
      // why
      // not a
      // model
      // object
      // ???
      jaxbJourneyPattern.setOrigin(journeyPattern.getOrigin());
      jaxbJourneyPattern.setRegistration(getRegistration(journeyPattern
            .getRegistrationNumber()));
      jaxbJourneyPattern.setRouteId(getNonEmptyObjectId(journeyPattern
            .getRoute()));
      jaxbJourneyPattern.getStopPointList().addAll(
            NeptuneIdentifiedObject.extractObjectIds(journeyPattern
                  .getStopPoints()));

      return jaxbJourneyPattern;
   }

}
