/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.manager;

import org.apache.log4j.Logger;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.core.CoreException;
import fr.certu.chouette.core.CoreExceptionCode;
import fr.certu.chouette.filter.DetailLevelEnum;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.user.User;

/**
 * 
 */
public class LineManager extends AbstractNeptuneManager<Line>
{
	private static final Logger logger = Logger.getLogger(LineManager.class);

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.NeptuneBeanManager#getNewInstance(fr.certu.chouette.model.user.User)
	 */
	@Override
	public Line getNewInstance(User user) throws ChouetteException
	{
		// TODO : check user access
		return new Line();
	}



	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.NeptuneBeanManager#update(fr.certu.chouette.model.user.User, fr.certu.chouette.model.neptune.NeptuneBean)
	 */
	@Override
	public void update(User user, Line bean) throws ChouetteException
	{
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.NeptuneBeanManager#update(fr.certu.chouette.model.user.User, fr.certu.chouette.model.neptune.NeptuneBean, fr.certu.chouette.manager.NeptuneBeanManager.DETAIL_LEVEL)
	 */
	@Override
	public void update(User user, Line bean, DetailLevelEnum level)
	throws ChouetteException
	{
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.NeptuneBeanManager#isRemovable(fr.certu.chouette.model.user.User, fr.certu.chouette.model.neptune.NeptuneBean)
	 */
	@Override
	public boolean isRemovable(User user, Line bean) throws ChouetteException
	{
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.NeptuneBeanManager#remove(fr.certu.chouette.model.user.User, fr.certu.chouette.model.neptune.NeptuneBean)
	 */
	@Override
	public void remove(User user, Line bean) throws ChouetteException
	{
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.NeptuneBeanManager#removeAll(fr.certu.chouette.model.user.User, fr.certu.chouette.manager.Filter)
	 */
	@Override
	public int removeAll(User user, Filter filter) throws ChouetteException
	{
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
		// TODO Auto-generated method stub
		return 0;
	}


}
