/**
 * 
 */
package fr.certu.chouette.echange.comparator;

import java.util.ArrayList;
import java.util.List;

/**
 * state of an elementary checked item
 * 
 * @author Michel Etienne
 *
 */
public class ChouetteObjectState 
{
    /**
     * object type name
     */
    private String name;
    /**
     * Source side Trident id
     */
    private String sourceId;
    /**
     * Target side Trident id
     */
    private String targetId;
    /**
     * Attribute states with valid status
     */
    private List<DataState> validAttributesStates;
    /**
     * get the valid attributes
     * 
     * @return
     */
    public List<DataState> getValidAttributesStates() 
    {
		return validAttributesStates;
	}

	/**
	 * Attribute states with invalid status
	 */
	private List<DataState> unvalidAttributesStates;    
    /**
     * get the invalid attributes
     * 
     * @return
     */
    public List<DataState> getUnvalidAttributesStates() 
    {
		return unvalidAttributesStates;
	}

	/**
     * status synthesis
     */
    private boolean identical = true;
    
    /**
     * ignore null couple of value in attribute states (default)
     */
    private boolean nullIgnored;

    /**
     * @param name
     * @param sourceId
     * @param targetId
     */
    public ChouetteObjectState(String name, String sourceId, String targetId) 
    {
       this(name,sourceId,targetId,true);
    }
    /**
     * @param name
     * @param sourceId
     * @param targetId
     */
    public ChouetteObjectState(String name, String sourceId, String targetId, boolean nullIgnored) 
    {
        this.name = name;
        setSourceId(sourceId);
        setTargetId(targetId);
        this.nullIgnored = nullIgnored;
        this.identical = (this.sourceId.length() > 0 && this.targetId.length() > 0);
        this.validAttributesStates = new ArrayList<DataState>();
        this.unvalidAttributesStates = new ArrayList<DataState>();
    }

    /**
     * add an attribute state
     * 
     * @param newState
     */
    private void addAttributeState(DataState newState)
    {
    	if (!newState.isIdentical())
    	{
            identical = false;
            unvalidAttributesStates.add(newState);
    	}
    	else
    	{
    		validAttributesStates.add(newState);
    	}
    }


    public String getName() 
    {
        return name;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getSourceId() 
    {
        return sourceId;
    }

    public void setSourceId(String sourceId) 
    {
        if (sourceId != null)
            this.sourceId = sourceId;
        else
            this.sourceId = "";
    }

    public String getTargetId() 
    {
        return targetId;
    }

    public void setTargetId(String targetId) 
    {
        if (targetId != null)
        {
            this.targetId = targetId;
        }
        else
        {
            this.targetId = "";
        }
    }

    public List<DataState> getAttributeStates() 
    {
    	List<DataState> attributes = validAttributesStates;
    	attributes.addAll(unvalidAttributesStates);
    	return attributes;    	
    }

    public boolean isIdentical() 
    {
        return identical;
    }

    private boolean addListAttributeState(String name, List<Object> sourceData, List<Object> targetData) 
    {
        boolean state = true;
        if (sourceData == null) 
        {
            state = (targetData == null);
            if (nullIgnored && state) return true;
        }
        else
        {
            state = sourceData.size() == targetData.size();
        }
        addAttributeState(new DataState(name, state));
        return state;
    }
    
    
    public boolean addAttributeState(String name, boolean sourceData, boolean targetData) 
    {
        boolean state = (sourceData == targetData);
        addAttributeState(new DataState(name, state));
        return state;
    }

    public boolean addAttributeState(String name, int sourceData, int targetData) 
    {
        boolean state = (sourceData == targetData);
        addAttributeState(new DataState(name, state));
        return state;
    }

    public boolean addAttributeState(String name, long sourceData, long targetData) 
    {
        boolean state = (sourceData == targetData);
        addAttributeState(new DataState(name, state));
        return state;
    }
        
    public boolean addAttributeState(String name, double sourceData, double targetData) 
    {
        boolean state = (sourceData == targetData);
        addAttributeState(new DataState(name, state));
        return state;
    }
        
    public boolean addAttributeState(String name, Object sourceData, Object targetData) 
    {
        boolean state = true;
        if (sourceData == null) 
        {
            state = (targetData == null);
            if (nullIgnored && state) return true;
        }
        else
        {
        	if (sourceData instanceof List)
        	{
        		return addListAttributeState(name, (List<Object>)sourceData, (List<Object>)targetData);
        	}
            state = sourceData.equals(targetData);
        }
        addAttributeState(new DataState(name, state));
        return state;
    }
}
