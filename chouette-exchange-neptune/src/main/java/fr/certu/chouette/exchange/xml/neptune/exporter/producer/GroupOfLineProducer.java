package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import org.trident.schema.trident.GroupOfLineType;

import fr.certu.chouette.model.neptune.GroupOfLine;

public class GroupOfLineProducer extends
      AbstractJaxbNeptuneProducer<GroupOfLineType, GroupOfLine>
{

   @Override
   public GroupOfLineType produce(GroupOfLine bean, boolean addExtension)
   {
      GroupOfLineType jaxbGroupOfLine = tridentFactory.createGroupOfLineType();

      populateFromModel(jaxbGroupOfLine, bean);

      jaxbGroupOfLine.setName(bean.getName());
      jaxbGroupOfLine.setComment(getNotEmptyString(bean.getComment()));
      // populated after with only one line
      // for (String objectId : bean.getLineIds())
      // {
      // castorGroupOfLine.addLineId(objectId);
      // }

      return jaxbGroupOfLine;
   }

}
