package mobi.chouette.exchange.neptune.exporter.producer;

import mobi.chouette.exchange.neptune.JsonExtension;
import mobi.chouette.model.GroupOfLine;

import org.codehaus.jettison.json.JSONObject;
import org.trident.schema.trident.GroupOfLineType;

public class GroupOfLineProducer extends
      AbstractJaxbNeptuneProducer<GroupOfLineType, GroupOfLine> implements JsonExtension
{

   //@Override
   public GroupOfLineType produce(GroupOfLine bean, boolean addExtension)
   {
      GroupOfLineType jaxbGroupOfLine = tridentFactory.createGroupOfLineType();

      populateFromModel(jaxbGroupOfLine, bean);

      jaxbGroupOfLine.setName(bean.getName());
      jaxbGroupOfLine.setComment(buildComment(bean, addExtension));

      return jaxbGroupOfLine;
   }

	protected String buildComment(GroupOfLine bean, boolean addExtension) {
		if (!addExtension)
			return getNotEmptyString(bean.getComment());

		try {

			JSONObject jsonComment = new JSONObject();
			if (!isEmpty(bean.getRegistrationNumber())) {
				jsonComment.put(REGISTRATION_NUMBER, bean.getRegistrationNumber());
			}
			if (jsonComment.length() == 0) {
				return getNotEmptyString(bean.getComment());
			} else {
				if (!isEmpty(bean.getComment())) {
					jsonComment.put(COMMENT, bean.getComment().trim());
				}
			}
			return jsonComment.toString();
		} catch (Exception e) {
			return getNotEmptyString(bean.getComment());
		}
	}

}
