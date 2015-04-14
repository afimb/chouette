package mobi.chouette.exchange.neptune.exporter.producer;

import java.util.Calendar;

import mobi.chouette.model.Network;
import mobi.chouette.model.type.PTNetworkSourceTypeEnum;

import org.trident.schema.trident.PTNetworkType;

public class PTNetworkProducer extends
      AbstractJaxbNeptuneProducer<PTNetworkType, Network>
{

   //@Override
   public PTNetworkType produce(Network ptNetwork, boolean addExtension)
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
