/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.Facility;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.user.User;

/**
 * @author michel
 *
 */
@SuppressWarnings("unchecked")
public class ConnectionLinkManager extends AbstractNeptuneManager<ConnectionLink> 
{

	private static final Logger logger = Logger.getLogger(ConnectionLinkManager.class); 
	public ConnectionLinkManager()
	{
		super(ConnectionLink.class,ConnectionLink.CONNECTIONLINK_KEY);
	}

	@Override
	protected Logger getLogger() 
	{
		return logger;
	}

	@Transactional
	@Override
	public void saveAll(User user, List<ConnectionLink> connectionLinks, boolean propagate,boolean fast) throws ChouetteException 
	{
		Set<String> areaIds = new HashSet<String>();
		for (ConnectionLink link : connectionLinks) 
		{
			if (link.getStartOfLink() == null)
				areaIds.add(link.getStartOfLinkId());
			if (link.getEndOfLink() == null)
				areaIds.add(link.getEndOfLinkId());
		}
		if (!areaIds.isEmpty())
		{
			areaIds.toArray();
			Filter filter = Filter.getNewInFilter("objectId", areaIds.toArray(new String[0])); 
			INeptuneManager<StopArea> areaManager = (INeptuneManager<StopArea>) getManager(StopArea.class);

			List<StopArea> stopareas = areaManager.getAll(user, filter);
			Map<String,StopArea> map = new HashMap<String, StopArea>();
			for (StopArea stopArea : stopareas) 
			{
				map.put(stopArea.getObjectId(), stopArea);
			}
			for (Iterator<ConnectionLink> iterator = connectionLinks.iterator(); iterator.hasNext();) 
			{
				ConnectionLink link = iterator.next();
				if (link.getStartOfLink() == null)
					link.setStartOfLink(map.get(link.getStartOfLinkId()));
				if (link.getEndOfLink() == null)
					link.setEndOfLink(map.get(link.getEndOfLinkId()));
				if (link.getStartOfLink() == null || link.getEndOfLink() == null) 
				{
					String id = null;
					if (link.getStartOfLink() == null) id = link.getStartOfLinkId();
					else id = link.getEndOfLinkId();
					logger.warn("ConnectionLink (objectId = "+link.getObjectId()+" cannot be saved : missing target stopArea "+id );
					iterator.remove();
				}
			}

		}

		super.saveAll(user, connectionLinks, propagate,fast);

		if(propagate)
		{
			INeptuneManager<Facility> facilityManager = (INeptuneManager<Facility>) getManager(Facility.class);
			List<Facility> facilities = new ArrayList<Facility>();
			for (ConnectionLink connectionLink : connectionLinks) 
			{
				mergeCollection(facilities,connectionLink.getFacilities());
			}

			if(!facilities.isEmpty())
				facilityManager.saveAll(user, facilities, propagate,fast);
		}
	}

}
