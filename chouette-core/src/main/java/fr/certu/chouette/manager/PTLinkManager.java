/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.manager;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.core.CoreException;
import fr.certu.chouette.core.CoreExceptionCode;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.user.User;

/**
 * @author michel
 *
 */
public class PTLinkManager extends AbstractNeptuneManager<PTLink> {

	private static final Logger logger = Logger.getLogger(PTLinkManager.class);
	public PTLinkManager() 
	{
		super(PTLink.class,PTLink.PTLINK_KEY);
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}
	@Transactional
	@Override
	public int removeAll(User user, Filter filter) throws ChouetteException 
	{
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
		if (!filter.getType().equals(Filter.Type.EQUALS))
		{
			throw new CoreException(CoreExceptionCode.DELETE_IMPOSSIBLE,"unvalid filter");
		}
		int ret =  getDao().removeAll(filter);
		logger.debug(""+ret+" PTLinks deleted");
		return ret;
		
	}

}
