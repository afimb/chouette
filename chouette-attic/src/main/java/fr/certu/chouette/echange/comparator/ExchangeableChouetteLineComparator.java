/**
 * 
 */
package fr.certu.chouette.echange.comparator;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import chouette.schema.ChouettePTNetworkTypeType;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.echange.comparator.amivif.AmivifExchangeableLineObjectIdMapper;
import fr.certu.chouette.service.xml.ILecteurEchangeXML;
import fr.certu.chouette.service.xml.ILecteurFichierXML;

/**
 * @author michel
 *
 */
public class ExchangeableChouetteLineComparator extends ExchangeableLineComparator 
{
	private static final Log logger = LogFactory.getLog(ExchangeableChouetteLineComparator.class);
	/** Spring instantiated resources */
	private ILecteurEchangeXML lecteurEchangeXML;
	
	private ILecteurFichierXML lecteurFichierXML;
	private ChouettePTNetworkTypeType sourceLineStruct;
	private ChouettePTNetworkTypeType targetLineStruct;

	/**
	 * 
	 */
	public ExchangeableChouetteLineComparator() 
	{
		super();
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.echange.comparator.IExchangeableLineComparator#doComparison(java.io.File, java.io.File)
	 */
	@Override
	public boolean doComparison(File source, File target) throws Exception 
	{
		//Init RespAmivifLineStruct
		initBuildMembers(source, target);

		//Launch common comparison
		return super.doComparison();
	}
	
	/** Check availability of resources **/
	private void checkRessources() 
	{		
		if (lecteurEchangeXML == null)
		{
			throw new ComparatorException(ComparatorException.TYPE.UnvailableResource, "lecteurEchangeXML");
		}
	}

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


		//Build lecteurEchanges
		try 
		{
			sourceLineStruct = lecteurFichierXML.lire(source.getAbsolutePath(),true);
			targetLineStruct = lecteurFichierXML.lire(target.getAbsolutePath(),true);
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
			ExchangeableLineObjectIdMapper sourceExchangeMap = new ExchangeableLineObjectIdMapper(getSource());
			ExchangeableLineObjectIdMapper targetExchangeMap = new ExchangeableLineObjectIdMapper(getTarget());
			setSourceExchangeMap(sourceExchangeMap);
			setTargetExchangeMap(targetExchangeMap);
		}
		catch (Exception e)
		{
			logger.error("ExchangeableLineObjectIdMapper-failure",e);
			throw new ComparatorException(ComparatorException.TYPE.UnbuildResource, "ExchangeableLineObjectIdMapper-failure");
		}
	}

	private ILectureEchange getExchangeableLine(ChouettePTNetworkTypeType lineStruct) 
	{
		// TODO Auto-generated method stub
		return lecteurEchangeXML.lire(lineStruct);
	}

	/**
	 * @return the lecteurEchangeXML
	 */
	public ILecteurEchangeXML getLecteurEchangeXML() 
	{
		return lecteurEchangeXML;
	}

	/**
	 * @param lecteurEchangeXML the lecteurEchangeXML to set
	 */
	public void setLecteurEchangeXML(ILecteurEchangeXML lecteurEchangeXML) 
	{
		this.lecteurEchangeXML = lecteurEchangeXML;
	}

	/**
	 * @return the lecteurFichierXML
	 */
	public ILecteurFichierXML getLecteurFichierXML() {
		return lecteurFichierXML;
	}

	/**
	 * @param lecteurFichierXML the lecteurFichierXML to set
	 */
	public void setLecteurFichierXML(ILecteurFichierXML lecteurFichierXML) {
		this.lecteurFichierXML = lecteurFichierXML;
	}

}
