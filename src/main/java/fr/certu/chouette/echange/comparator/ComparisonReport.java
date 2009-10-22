package fr.certu.chouette.echange.comparator;

import java.util.ArrayList;
import java.util.List;

public class ComparisonReport 
{	
	/** 
	 * Objects States List, true or false to indicate 
	 * the sub object comparison result
	 **/
	private List<ChouetteObjectState> objectStateList = new ArrayList<ChouetteObjectState>();
	
	public ComparisonReport(List<ChouetteObjectState> objectStateList) 
	{
		super();
		this.objectStateList = objectStateList;
	}
	
	public List<ReportItem> getAllItems()
	{
		List<ReportItem> items = new ArrayList<ReportItem>();
		for (ChouetteObjectState chouetteObjectState : objectStateList)
		{
			items.add(new ReportItem(chouetteObjectState, false));
		}
		return items;
	}
	
	public List<ReportItem> getErrorItems()
	{
		List<ReportItem> items = new ArrayList<ReportItem>();
		for (ChouetteObjectState chouetteObjectState : objectStateList) 
		{
			if (! chouetteObjectState.isIdentical())
			{	
				items.add(new ReportItem(chouetteObjectState, true));
			}
		}
		return items;
	}
		
		
	public List<ChouetteObjectState> getErrorsReport()
	{
		List<ChouetteObjectState> errorStates = new ArrayList<ChouetteObjectState>();
				
		for (ChouetteObjectState chouetteObjectState : objectStateList) 
		{
			if (! chouetteObjectState.isIdentical())
			{	
				errorStates.add(chouetteObjectState);
			}
		}
	
		return errorStates; 
	}
	

	class ReportItem
	{		
	    /**
	     * object type name
	     */
	    private String name;
	    
	    
	    private boolean state;
	    
	    /**
	     * Source side Trident id
	     */
	    private String sourceId;
	    /**
	     * Target side Trident id
	     */
	    private String targetId;
	    /**
	     * Attribute states
	     */
	    private List<DataState>  attributesStates;
	    	    	    
		public ReportItem(ChouetteObjectState chouetteObjectState, boolean filterValids)
		{
	    	this.setName(chouetteObjectState.getName());
	    	this.setSourceId(chouetteObjectState.getSourceId());
	    	this.setTargetId(chouetteObjectState.getTargetId());
	    	this.setState(chouetteObjectState.isIdentical());
	    	if (filterValids)
	    	{
	    		this.setAttributesStates(chouetteObjectState.getUnvalidAttributesStates());	    		
	    	}
	    	else
	    	{
	    		this.setAttributesStates(chouetteObjectState.getAttributeStates());
	    	}
		}


		public void setName(String name) 
		{
			this.name = name;
		}


		public String getName()
		{
			return name;
		}


		public void setSourceId(String sourceId)
		{
			this.sourceId = sourceId;
		}


		public String getSourceId() 
		{
			return sourceId;
		}


		public void setTargetId(String targetId) 
		{
			this.targetId = targetId;
		}


		public String getTargetId() {
			return targetId;
		}


		public void setAttributesStates(List<DataState> attributesStates) {
			this.attributesStates = attributesStates;
		}


		public List<DataState> getAttributesStates() 
		{
			return attributesStates;
		}


		public void setState(boolean state) {
			this.state = state;
		}


		public boolean getState() {
			return state;
		}
	}
	
}
