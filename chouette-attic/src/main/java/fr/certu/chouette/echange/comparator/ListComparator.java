package fr.certu.chouette.echange.comparator;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;

public class ListComparator extends AbstractChouetteDataComparator 
{
	private static final Log logger = LogFactory.getLog(ListComparator.class);

	private String listName;

	public boolean compareData(IExchangeableLineComparator master) throws ServiceException
	{
		this.master = master;
		ILectureEchange source = master.getSource();
		ILectureEchange target = master.getTarget();

		Ligne sourceLine = source.getLigne();
		Ligne targetLine = target.getLigne();

		// check the count 
		ChouetteObjectState objectState = new ChouetteObjectState(getMappingKey(), sourceLine.getObjectId(), targetLine.getObjectId());

		try
		{
			Method invokeMethod = source.getClass().getMethod("get"+listName);

			List<Object> sourceList = (List<Object>)invokeMethod.invoke(source);
			List<Object> targetList = (List<Object>)invokeMethod.invoke(target);

			// check the count of all the sublists 
			objectState.addAttributeState("count", sourceList, targetList);
			if (!objectState.isIdentical())
			{
				logger.debug(getMappingKey()+ ": source count = "+sourceList.size()+", target count ="+targetList.size());
			}

			master.addObjectState(objectState);
			return objectState.isIdentical();
		}
		catch (Exception ex)
		{
			logger.error(listName,ex);
			throw new ServiceException(CodeIncident.DONNEE_INVALIDE, CodeDetailIncident.DATA,ex,listName);
		}
	}


	@Override
	public Map<String, ChouetteObjectState> getStateMap()
	{
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * @param listName the listName to set
	 */
	public void setListName(String name) {
		this.listName = name;
	}


	/**
	 * @return the listName
	 */
	public String getListName() {
		return listName;
	}
}
