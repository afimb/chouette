package fr.certu.chouette.echange.comparator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractChouetteDataComparator implements IChouetteDataComparator 
{

    protected IExchangeableLineComparator master;    
    private String mappingKey;
    
    protected List<String> convertToSourceId(List<String> targetList)
    {
        List<String> sourceList = new ArrayList<String>(targetList.size());
        for (String targetId : targetList)
        {
            String sourceId = master.getSourceId(targetId);
            if (sourceId == null) sourceId = "no match";
            sourceList.add(sourceId);
        }
        return sourceList;
    }

    public String getMappingKey()
    {
        return mappingKey;
    }

    public void setMappingKey(String mappingKey)
    {
        this.mappingKey = mappingKey;
    }

	public Map<String, ChouetteObjectState> getStateMap() {
		// TODO Auto-generated method stub
		return null;
	}

}
