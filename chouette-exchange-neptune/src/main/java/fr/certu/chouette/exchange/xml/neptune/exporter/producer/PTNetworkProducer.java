package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import java.util.Calendar;

import org.trident.schema.trident.PTNetworkType;

import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.type.PTNetworkSourceTypeEnum;

public class PTNetworkProducer extends
      AbstractJaxbNeptuneProducer<PTNetworkType, PTNetwork>
{

   @Override
   public PTNetworkType produce(PTNetwork ptNetwork)
   {
      PTNetworkType jaxbPTNetwork = tridentFactory.createPTNetworkType();

      //
      populateFromModel(jaxbPTNetwork, ptNetwork);

      jaxbPTNetwork.setName(ptNetwork.getName());
      jaxbPTNetwork.setRegistration(getRegistration(ptNetwork
            .getRegistrationNumber()));

      jaxbPTNetwork
            .setDescription(getNotEmptyString(ptNetwork.getDescription()));
      jaxbPTNetwork.setSourceIdentifier(getNotEmptyString(ptNetwork
            .getSourceIdentifier()));
      jaxbPTNetwork.setSourceName(getNotEmptyString(ptNetwork.getSourceName()));
      jaxbPTNetwork.setComment(getNotEmptyString(ptNetwork.getComment()));
      // populated after with only one line
      // castorPTNetwork.setLineId(NeptuneIdentifiedObject.extractObjectIds(ptNetwork.getLines()));
      if (ptNetwork.getVersionDate() != null)
      {
         jaxbPTNetwork.setVersionDate(toCalendar(ptNetwork.getVersionDate()));
      } else
      {
         jaxbPTNetwork.setVersionDate(toCalendar(Calendar.getInstance()
               .getTime()));
      }

      try
      {
         PTNetworkSourceTypeEnum ptNetworkSourceType = ptNetwork
               .getSourceType();
         if (ptNetworkSourceType != null)
         {
            jaxbPTNetwork
                  .setSourceType(org.trident.schema.trident.SourceTypeType
                        .fromValue(ptNetworkSourceType.name()));
         }
      } catch (IllegalArgumentException e)
      {
         // TODO generate report
      }

      return jaxbPTNetwork;
   }

}
