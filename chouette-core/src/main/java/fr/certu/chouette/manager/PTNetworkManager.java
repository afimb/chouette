/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.manager;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.user.User;

/**
 * 
 */
@SuppressWarnings("unchecked")
public class PTNetworkManager extends AbstractNeptuneManager<PTNetwork>
{
	private static final Logger logger = Logger.getLogger(PTNetworkManager.class); 

	public PTNetworkManager() 
	{
		super(PTNetwork.class,PTNetwork.PTNETWORK_KEY);
	}
	@Transactional
	@Override
	public void remove(User user,PTNetwork ptNetwork,boolean propagate) throws ChouetteException
	{
		INeptuneManager<Line> lineManager = (INeptuneManager<Line>) getManager(Line.class);
		List<Line> lines = lineManager.getAll(user, Filter.getNewEqualsFilter("ptNetwork.id", ptNetwork.getId()));
		if(propagate)
			lineManager.removeAll(user,lines,propagate);
		else {
			for (Line line : lines) {
				line.setPtNetwork(null);
				lineManager.update(user, line);
			}
		}
		super.remove(user, ptNetwork,propagate);
	}

	@Override
	protected Logger getLogger() 
	{
		return logger;
	}
}
