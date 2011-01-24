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
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.user.User;

/**
 * 
 */
public class PTNetworkManager extends AbstractNeptuneManager<PTNetwork>
{

   /* (non-Javadoc)
    * @see fr.certu.chouette.manager.INeptuneManager#getNewInstance(fr.certu.chouette.model.user.User)
    */
   @Override
   public PTNetwork getNewInstance(User user) throws ChouetteException
   {
      // TODO Auto-generated method stub
      return null;
   }


   /* (non-Javadoc)
    * @see fr.certu.chouette.manager.INeptuneManager#update(fr.certu.chouette.model.user.User, fr.certu.chouette.model.neptune.NeptuneObject)
    */
   @Override
   public void update(User user, PTNetwork bean) throws ChouetteException
   {
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
      // TODO Auto-generated method stub
      
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.manager.INeptuneManager#update(fr.certu.chouette.model.user.User, fr.certu.chouette.model.neptune.NeptuneObject, fr.certu.chouette.filter.DetailLevelEnum)
    */
   @Override
   public void update(User user, PTNetwork bean, DetailLevelEnum level) throws ChouetteException
   {
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
      // TODO Auto-generated method stub
      
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.manager.INeptuneManager#isRemovable(fr.certu.chouette.model.user.User, fr.certu.chouette.model.neptune.NeptuneObject)
    */
   @Override
   public boolean isRemovable(User user, PTNetwork bean) throws ChouetteException
   {
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
      // TODO Auto-generated method stub
      return false;
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.manager.INeptuneManager#remove(fr.certu.chouette.model.user.User, fr.certu.chouette.model.neptune.NeptuneObject)
    */
   @Override
   public void remove(User user, PTNetwork bean) throws ChouetteException
   {
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
      // TODO Auto-generated method stub
      
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.manager.INeptuneManager#removeAll(fr.certu.chouette.model.user.User, fr.certu.chouette.filter.Filter)
    */
   @Override
   public int removeAll(User user, Filter filter) throws ChouetteException
   {
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
      // TODO Auto-generated method stub
      return 0;
   }

 
}
