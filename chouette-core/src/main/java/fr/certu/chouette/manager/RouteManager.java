/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.manager;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.core.CoreException;
import fr.certu.chouette.core.CoreExceptionCode;
import fr.certu.chouette.filter.DetailLevelEnum;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.user.User;

/**
 * @author michel
 *
 */
public class RouteManager extends AbstractNeptuneManager<Route> 
{
    public RouteManager() 
    {
		super(Route.class);
	}
	
	@Override
	public Route getNewInstance(User user) throws ChouetteException {
		// TODO Auto-generated method stub
		return new Route();
	}

	@Override
	public void update(User user, Route bean) throws ChouetteException {
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(User user, Route bean, DetailLevelEnum level)
			throws ChouetteException {
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isRemovable(User user, Route bean)
			throws ChouetteException {
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void remove(User user, Route bean) throws ChouetteException {
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
		// TODO Auto-generated method stub
		
	}

	@Override
	public int removeAll(User user, Filter filter) throws ChouetteException {
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
		// TODO Auto-generated method stub
		return 0;
	}

}
