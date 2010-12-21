package fr.certu.chouette.echange.comparator.amivif;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import amivif.schema.Line;
import amivif.schema.RespPTLineStructTimetable;
import fr.certu.chouette.echange.comparator.ChouetteObjectState;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;

public class AmivifListComparator extends AbstractAmivifDataComparator 
{
	private static final Log logger = LogFactory.getLog(AmivifListComparator.class);

	private String listName;

	public boolean compareData(ExchangeableAmivifLineComparator master) throws ServiceException
	{
		this.master = master;
    	RespPTLineStructTimetable source = master.getAmivifSource();
        RespPTLineStructTimetable target = master.getAmivifTarget();

		// check the count 
        Line sourceLine = source.getLine();
        Line targetLine = target.getLine();
		ChouetteObjectState objectState = new ChouetteObjectState(getMappingKey(), sourceLine.getObjectId(), targetLine.getObjectId());

		try
		{
			Method invokeMethod = source.getClass().getMethod("get"+listName+"Count");

			Integer sourceList = (Integer)invokeMethod.invoke(source);
			Integer targetList = (Integer)invokeMethod.invoke(target);

			// check the count of all the sublists 
			objectState.addAttributeState("count", sourceList.intValue(), targetList.intValue());
			if (!objectState.isIdentical())
			{
				logger.debug(getMappingKey()+ ": source count = "+sourceList+", target count ="+targetList);
			}

			master.addObjectState(objectState);
			return objectState.isIdentical();
		}
		catch (Exception ex)
		{
			logger.error(listName,ex);
			throw new ServiceException(CodeIncident.DONNEE_INVALIDE, ex, listName);
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
