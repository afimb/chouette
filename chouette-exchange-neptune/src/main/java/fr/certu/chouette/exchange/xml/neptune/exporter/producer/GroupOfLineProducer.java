package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import fr.certu.chouette.model.neptune.GroupOfLine;

public class GroupOfLineProducer extends AbstractCastorNeptuneProducer<chouette.schema.GroupOfLine, GroupOfLine> {

   @Override
   public chouette.schema.GroupOfLine produce(GroupOfLine bean) 
   {
      chouette.schema.GroupOfLine castorGroupOfLine = new chouette.schema.GroupOfLine();

      populateFromModel(castorGroupOfLine, bean);

      castorGroupOfLine.setName(bean.getName());
      castorGroupOfLine.setComment(getNotEmptyString(bean.getComment()));
      // populated after with only one line
      //		for (String objectId : bean.getLineIds())
      //      {
      //		   castorGroupOfLine.addLineId(objectId);
      //      }

      return castorGroupOfLine;
   }

}
