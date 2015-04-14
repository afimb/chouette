package mobi.chouette.exchange.neptune.exporter.producer;

import mobi.chouette.exchange.neptune.exporter.util.NeptuneObjectUtil;
import mobi.chouette.model.JourneyPattern;

import org.trident.schema.trident.JourneyPatternType;

public class JourneyPatternProducer extends
      AbstractJaxbNeptuneProducer<JourneyPatternType, JourneyPattern>
{

   //@Override
   public JourneyPatternType produce(JourneyPattern journeyPattern, boolean addExtension)
   {
      JourneyPatternType jaxbJourneyPattern = tridentFactory
            .createJourneyPatternType();

      //
      populateFromModel(jaxbJourneyPattern, journeyPattern);

      jaxbJourneyPattern.setComment(journeyPattern.getComment());
      jaxbJourneyPattern.setName(journeyPattern.getName());
      jaxbJourneyPattern.setPublishedName(journeyPattern.getPublishedName());
      jaxbJourneyPattern.setDestination(journeyPattern.getArrivalStopPoint().getObjectId());
      jaxbJourneyPattern.setOrigin(journeyPattern.getDepartureStopPoint().getObjectId());
      jaxbJourneyPattern.setRegistration(getRegistration(journeyPattern
            .getRegistrationNumber()));
      jaxbJourneyPattern.setRouteId(getNonEmptyObjectId(journeyPattern
            .getRoute()));
      jaxbJourneyPattern.getStopPointList().addAll(
            NeptuneObjectUtil.extractObjectIds(journeyPattern
                  .getStopPoints()));

      return jaxbJourneyPattern;
   }

}
