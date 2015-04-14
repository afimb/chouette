package mobi.chouette.exchange.neptune.exporter.producer;

import mobi.chouette.model.GroupOfLine;

import org.trident.schema.trident.GroupOfLineType;

public class GroupOfLineProducer extends
      AbstractJaxbNeptuneProducer<GroupOfLineType, GroupOfLine>
{

   //@Override
   public GroupOfLineType produce(GroupOfLine bean, boolean addExtension)
   {
      GroupOfLineType jaxbGroupOfLine = tridentFactory.createGroupOfLineType();

      populateFromModel(jaxbGroupOfLine, bean);

      jaxbGroupOfLine.setName(bean.getName());
      jaxbGroupOfLine.setComment(getNotEmptyString(bean.getComment()));

      return jaxbGroupOfLine;
   }

}
