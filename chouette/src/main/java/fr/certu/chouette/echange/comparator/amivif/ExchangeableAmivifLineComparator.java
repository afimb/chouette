package fr.certu.chouette.echange.comparator.amivif;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import amivif.schema.RespPTLineStructTimetable;
import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.echange.comparator.ComparatorException;
import fr.certu.chouette.echange.comparator.ExchangeableLineComparator;
import fr.certu.chouette.service.amivif.IAmivifAdapter;
import fr.certu.chouette.service.amivif.ILecteurAmivifXML;
import fr.certu.chouette.service.xml.ILecteurEchangeXML;

/**
 * 
 * @author Dryade, Evelyne Zahn
 * Amivif line comparator Class,
 * specializes Exchangeable line* comparator
 * for Amivif specialized xml structure comparison
 */
public class ExchangeableAmivifLineComparator extends ExchangeableLineComparator {

	private static final Log logger = LogFactory.getLog(ExchangeableAmivifLineComparator.class);

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
			logger.error("RespPTLineStructTimetable-failure",e);
			throw new ComparatorException(ComparatorException.TYPE.UnbuildResource, "RespPTLineStructTimetable-failure");
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
			logger.error("ILecteurEchange-failure",e);
			throw new ComparatorException(ComparatorException.TYPE.UnbuildResource, "ILecteurEchange-failure");
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
			logger.error("AmivifExchangeableLineObjectIdMapper-failure",e);
			throw new ComparatorException(ComparatorException.TYPE.UnbuildResource, "AmivifExchangeableLineObjectIdMapper-failure");
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
		boolean amivifResult = true;

		if (commonResult)
		{
			//Compare added Amivif specificities		

			for (IAmivifDataComparator amivifDataComparator : amivifComparators)
			{
				logger.debug("starting "+amivifDataComparator.getMappingKey());
				if (! amivifDataComparator.compareData(this))
				{
					logger.debug(amivifDataComparator.getMappingKey() + " comparison failed");
					amivifResult = false;
					if (amivifDataComparator.mustStopOnFailure()) break;
				}
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
