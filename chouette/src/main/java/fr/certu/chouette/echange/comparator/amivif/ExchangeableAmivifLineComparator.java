package fr.certu.chouette.echange.comparator.amivif;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

import amivif.schema.RespPTLineStructTimetable;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.service.amivif.IAmivifAdapter;
import fr.certu.chouette.service.amivif.ILecteurAmivifXML;
import fr.certu.chouette.service.xml.ILecteurEchangeXML;
import fr.certu.chouette.echange.comparator.ComparatorException;
import fr.certu.chouette.echange.comparator.ExchangeableLineComparator;
import fr.certu.chouette.echange.comparator.ComparatorException.TYPE;
import fr.certu.chouette.echange.comparator.amivif.*;

/**
 * 
 * @author Dryade, Evelyne Zahn
 * Amivif line comparator Class,
 * specializes Exchangeable line* comparator
 * for Amivif specialized xml structure comparison
 */
public class ExchangeableAmivifLineComparator extends ExchangeableLineComparator {
	
	/** Spring instantiated resources */
	private ILecteurEchangeXML lecteurEchangeXML;
	private ILecteurAmivifXML lecteurAmivifXML;
	private IAmivifAdapter amivifAdapter;
		
	/** "LecteurAmivifXML" build Members */
	private  RespPTLineStructTimetable sourceLineStruct = null;
	private  RespPTLineStructTimetable targetLineStruct = null;    

	/** Comparators : ordered by sequence order */
	private List<IAmivifDataComparator> amivifComparators;
	/** 
	 * InitMembers, initiates : 
	 * - Amivif structure for Amifiv specific comparison adds
	 * - Super ILectureEchange (Import Export Exchangeable line Object)  
	 **/
	private void initBuildMembers(File source, File target)
	{			
		//check resources throws ComparatorException.TYPE.UnvailableResource
		checkRessources();
		
		//check arguments
		if (source == null || target == null) 
    	{
			throw new ComparatorException(ComparatorException.TYPE.UnvailableResource, "invalid-null-file(s)");
    	}
				
		//Build amivif structures
		try
		{
			this.sourceLineStruct = getAmivifLineStruct(source);
    		this.targetLineStruct = getAmivifLineStruct(target);
    	}
    	catch (Exception e)
    	{
    		throw new ComparatorException(ComparatorException.TYPE.UnbuildResource, "RespPTLineStructTimetable-failure", e);
    	}
    	
    	//Build lecteurEchanges
    	try 
    	{
    		ILectureEchange sourceExchangeLine = getExchangeableLine(sourceLineStruct);
    		ILectureEchange targetExchangeLine = getExchangeableLine(targetLineStruct);
    		super.setSource(sourceExchangeLine);
    		super.setTarget(targetExchangeLine);
    	}
    	catch(Exception e) 
    	{
    		throw new ComparatorException(ComparatorException.TYPE.UnbuildResource, "ILecteurEchange-failure", e);
    	}
    
    	//Build objectIds mapping
    	try
    	{
    		AmivifExchangeableLineObjectIdMapper sourceExchangeMap = new AmivifExchangeableLineObjectIdMapper(getAmivifSource(), super.getSource());
    	    AmivifExchangeableLineObjectIdMapper targetExchangeMap = new AmivifExchangeableLineObjectIdMapper(getAmivifTarget(), super.getTarget());
    	    super.setSourceExchangeMap(sourceExchangeMap);
    		super.setTargetExchangeMap(targetExchangeMap);
    	   
    	}
    	catch (Exception e)
    	{
    		throw new ComparatorException(ComparatorException.TYPE.UnbuildResource, "AmivifExchangeableLineObjectIdMapper-failure", e);
    	}
	}
	
	
	/** Check availability of resources **/
	private void checkRessources() 
	{		
		if (amivifAdapter == null)
		{
			throw new ComparatorException(ComparatorException.TYPE.UnvailableResource, "amivifAdapter");
		}
		if (lecteurAmivifXML == null) 
		{
			throw new ComparatorException(ComparatorException.TYPE.UnvailableResource, "lecteurAmivifXML");		
		}
		if (lecteurEchangeXML == null)
		{
			throw new ComparatorException(ComparatorException.TYPE.UnvailableResource, "lecteurEchangeXML");
		}
	}
	
	/** @return Amivif Structure Line **/
	private RespPTLineStructTimetable getAmivifLineStruct(File file) throws Exception
	{
		String canonicalPath = file.getCanonicalPath();
		return lecteurAmivifXML.lire(canonicalPath);
	}

	/** @return Amivif Exchangeable Line **/
	private ILectureEchange getExchangeableLine(RespPTLineStructTimetable amivifLine) throws Exception 
	{
		
		ILectureEchange exchangeableLine = lecteurEchangeXML.lire(amivifAdapter.getATC(amivifLine));
		return exchangeableLine;
	}

	public boolean doComparison(File source, File target) throws Exception
	{
		//Init RespAmivifLineStruct
		initBuildMembers(source, target);
		
		//Launch common comparison
		boolean commonResult = super.doComparison();
		
		//Compare added Amivif specificities		
		boolean amivifResult = true;
	
		for (IAmivifDataComparator AmivifDataComparator : amivifComparators)
		{
			if (! AmivifDataComparator.compareData(this))
			{
				amivifResult = false;
			}
		}
		return (commonResult && amivifResult);		
	}
    
    /** Basic methods such as getters and setters **/
    public IAmivifAdapter getAmivifAdapter()
	{
		return amivifAdapter;
	}

	public void setAmivifAdapter(IAmivifAdapter amivifAdapter)
	{
		this.amivifAdapter = amivifAdapter;
	}
		
	public ILecteurAmivifXML getLecteurAmivifXML()
	{
		return lecteurAmivifXML;
	}

	public void setLecteurAmivifXML(ILecteurAmivifXML lecteurAmivifXML)
	{
		this.lecteurAmivifXML = lecteurAmivifXML;
	}
	
	public ILecteurEchangeXML getLecteurEchangeXML()
	{
		return lecteurEchangeXML;
	}

	public void setLecteurEchangeXML(ILecteurEchangeXML lecteurEchangeXML)
	{
		this.lecteurEchangeXML = lecteurEchangeXML;
	}
	
	public RespPTLineStructTimetable getAmivifSource()
    {
        return this.sourceLineStruct;
    }
    
    public RespPTLineStructTimetable getAmivifTarget()
    {
        return this.targetLineStruct;
    }
    
    public AmivifExchangeableLineObjectIdMapper getSourceExchangeMap()
    {
    	return (AmivifExchangeableLineObjectIdMapper) super.getSourceExchangeMap();
    }
    
    public AmivifExchangeableLineObjectIdMapper getTargetExchangeMap()
    {
    	return (AmivifExchangeableLineObjectIdMapper) super.getTargetExchangeMap();
    }

    public List<IAmivifDataComparator> getAmivifComparators()
    {
        return amivifComparators;
    }

    public void setAmivifComparators(List<IAmivifDataComparator> amivifComparators)
    {
        this.amivifComparators = amivifComparators;
    }
}
