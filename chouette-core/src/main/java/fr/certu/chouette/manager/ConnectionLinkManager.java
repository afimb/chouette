/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.Facility;
import fr.certu.chouette.model.user.User;

/**
 * @author michel
 *
 */
@SuppressWarnings("unchecked")
public class ConnectionLinkManager extends AbstractNeptuneManager<ConnectionLink> 
{

	private Logger logger = Logger.getLogger(ConnectionLink.class); 
    public ConnectionLinkManager()
	{
		super(ConnectionLink.class);
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}
    
	@Override
	public void saveAll(User user, List<ConnectionLink> connectionLinks, boolean propagate) throws ChouetteException 
	{
		super.saveAll(user, connectionLinks, propagate);
		
		if(propagate)
		{
			INeptuneManager<Facility> facilityManager = (INeptuneManager<Facility>) getManager(Facility.class);
			List<Facility> facilities = new ArrayList<Facility>();
			for (ConnectionLink connectionLink : connectionLinks) 
			{
				List<Facility> fList = connectionLink.getFacilities(); 
				if(fList != null && !facilities.containsAll(fList))
					facilities.addAll(fList);
			}
			
			if(!facilities.isEmpty())
				facilityManager.saveAll(user, facilities, propagate);
		}
	}

}
