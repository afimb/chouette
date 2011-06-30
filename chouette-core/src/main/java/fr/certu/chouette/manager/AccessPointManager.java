package fr.certu.chouette.manager;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.AccessPoint;

/**
 * 
 * @author mamadou keira
 *
 */
public class AccessPointManager extends AbstractNeptuneManager<AccessPoint> 
{
    private static final Logger logger = Logger.getLogger(AccessPointManager.class);
	
	public AccessPointManager() {
		super(AccessPoint.class,AccessPoint.ACCESSPOINT_KEY);
	}

	@Override
	protected Logger getLogger() 
	{
		return logger;
	}
	
}
