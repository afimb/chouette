package fr.certu.chouette.echange.comparator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.certu.chouette.echange.ILectureEchange;


public abstract class ExchangeableLineComparator implements IExchangeableLineComparator
{	
	private static final Log logger = LogFactory.getLog(ExchangeableLineComparator.class);

	/** 
	 * SourceObjectIds<->targetObjectIds maps to link same schema objects
	 * as they don't necessary have the same id
	 * completed by data object comparators,
	 **/
	private Map<String, String> sourceTargetIdMap = new HashMap<String, String>();	
	private Map<String, String> targetSourceIdMap = new HashMap<String, String>();
	
	
	/** 
	 * Objects States List, true or false to indicate 
	 * the sub object comparison result
	 **/
	private List<ChouetteObjectState> objectStateList = new ArrayList<ChouetteObjectState>();
	private HashMap<String, Integer> countsStates = new HashMap<String, Integer>();
	
	/**
	 * Exchangeable source and target lines "LectureEchange",
	 * master compared Chouette line objects
	 * Build in daughter classes  
	 */
	private ILectureEchange sourceExchangeLine = null;
	private ILectureEchange targetExchangeLine = null;
	
	
	/** Map of id from LectureEchangeObjects */
    private ExchangeableLineObjectIdMapper sourceExchangeMap = null;
	private ExchangeableLineObjectIdMapper targetExchangeMap = null;
	
	/** DataComparators : ordered by activation sequence order */
	private List<IChouetteDataComparator> dataComparators; 
	
	
	public boolean doComparison() throws Exception
	{
    boolean globalResult = true;

    for (IChouetteDataComparator chouetteDataComparator : this.dataComparators)
    {
      logger.debug("starting " + chouetteDataComparator.getMappingKey());
      if (chouetteDataComparator.compareData(this))
        continue;
      logger.debug("comparison failed ");
      globalResult = false;
      if (chouetteDataComparator.mustStopOnFailure())
        break;
    }
    logger.debug("end " + globalResult);
    return globalResult;
	}
	
	/** 
	 * Basic Methods on members, as getters and setters 
	 */
	public void addMappingIds(String sourceId, String targetId) 
	{
		sourceTargetIdMap.put(sourceId, targetId);
		targetSourceIdMap.put(targetId, sourceId);
	}

	public void addObjectState(ChouetteObjectState objectState) 
	{
		objectStateList.add(objectState);
	}	
	
	public void setSource(ILectureEchange sourceExchangeLine) 
	{
		this.sourceExchangeLine = sourceExchangeLine;
	}
	
	public ILectureEchange getSource() 
	{
		return sourceExchangeLine;
	}
	
	public void setTarget(ILectureEchange targetExchangeLine) 
	{
		this.targetExchangeLine = targetExchangeLine;
	}
	
	public ILectureEchange getTarget() 
	{
		return targetExchangeLine;
	}
	
	public List<ChouetteObjectState> getObjectStateList()
	{
		return objectStateList;
	}
	
	public ExchangeableLineObjectIdMapper getSourceExchangeMap()
    {
        return sourceExchangeMap;
    }

    public ExchangeableLineObjectIdMapper getTargetExchangeMap()
    {
        return targetExchangeMap;
    }
    
	public void setSourceExchangeMap(ExchangeableLineObjectIdMapper sourceExchangeMap)
	{
		this.sourceExchangeMap = sourceExchangeMap;
	}
    
	public void setTargetExchangeMap(ExchangeableLineObjectIdMapper targetExchangeMap) 
	{
		this.targetExchangeMap = targetExchangeMap;
	}
	
	public String getSourceId(String targetId)
    {
        return targetSourceIdMap.get(targetId);
    }

	public String getTargetId(String sourceId)
	{
		return sourceTargetIdMap.get(sourceId);
	}	
	
	public void addCountState(String key, Integer state)
	{
		//should not occurs
		if (! countsStates.containsKey(key)) 
		{
			this.countsStates.put(key, state);
		}
	}

    public List<IChouetteDataComparator> getDataComparators()
    {
        return dataComparators;
    }

    public void setDataComparators(List<IChouetteDataComparator> dataComparators)
    {
        this.dataComparators = dataComparators;
    }
}
