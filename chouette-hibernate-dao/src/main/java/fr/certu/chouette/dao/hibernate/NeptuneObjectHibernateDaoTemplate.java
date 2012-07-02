package fr.certu.chouette.dao.hibernate;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.hibernate3.HibernateSystemException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import fr.certu.chouette.dao.IDaoTemplate;
import fr.certu.chouette.dao.hibernate.exception.HibernateDaoExceptionCode;
import fr.certu.chouette.dao.hibernate.exception.HibernateDaoRuntimeException;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.filter.FilterOrder;
import fr.certu.chouette.model.neptune.NeptuneObject;
import fr.certu.chouette.plugin.model.ExportLogMessage;
import fr.certu.chouette.plugin.model.FileValidationLogMessage;
import fr.certu.chouette.plugin.model.ImportLogMessage;

public class NeptuneObjectHibernateDaoTemplate<T extends NeptuneObject> extends HibernateDaoSupport implements IDaoTemplate<T>
{
   private static final Logger logger = Logger.getLogger(NeptuneObjectHibernateDaoTemplate.class);

   private Class<T> type;

   private NeptuneObjectHibernateDaoTemplate(Class<T> type) 
   {
      this.type = type;
   }

   public static NeptuneObjectHibernateDaoTemplate<ImportLogMessage> createImportLogMessageDao()
   {
      return new NeptuneObjectHibernateDaoTemplate<ImportLogMessage>( ImportLogMessage.class);
   }

   public static NeptuneObjectHibernateDaoTemplate<ExportLogMessage> createExportLogMessageDao()
   {
      return new NeptuneObjectHibernateDaoTemplate<ExportLogMessage>( ExportLogMessage.class);
   }
   
   public static NeptuneObjectHibernateDaoTemplate<FileValidationLogMessage> createFileValidationLogMessageDao()
   {
      return new NeptuneObjectHibernateDaoTemplate<FileValidationLogMessage>( FileValidationLogMessage.class);
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.dao.IDaoTemplate#get(java.lang.Long)
    */
   @SuppressWarnings("unchecked")
   public T get(Long id)
   {
      logger.debug("invoke get on "+type.getSimpleName());
      T object = ( T)getHibernateTemplate().get( type, id);
      if ( object==null)
      {
         throw new ObjectRetrievalFailureException( type, id);
      }
      return object;
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.dao.IDaoTemplate#select(fr.certu.chouette.filter.Filter)
    */
   @SuppressWarnings("unchecked")
   public List<T> select(final Filter filter) 
   {
      logger.debug("invoke select on "+type.getSimpleName());

      Session session = getSession();

      Criteria criteria = session.createCriteria(type);

      // DetachedCriteria criteria = DetachedCriteria.forClass(type);
      criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
      if (!filter.isEmpty())
      {
         FilterToHibernateClauseTranslator translator = new FilterToHibernateClauseTranslator();
         criteria.add(translator.translate(filter,criteria,getSessionFactory().getClassMetadata(type)));
      }
      if (filter.getOrderList()!= null)
      {
         for (FilterOrder order : filter.getOrderList())
         {
            switch (order.getType())
            {
            case ASC:
               criteria.addOrder(Order.asc(order.getAttribute()));
               break;
            case DESC:
               criteria.addOrder(Order.desc(order.getAttribute()));
               break;

            default:
               break;
            }
         }
      }
      // HibernateTemplate ht = getHibernateTemplate();
      List<T> beans = null; 
      if (filter.getLimit() > 0 || filter.getStart() > 0)
      {
         logger.debug("call with start and/or limit");
         criteria.setFirstResult(filter.getStart());
         criteria.setMaxResults(filter.getLimit());
         beans = criteria.list();// ht.findByCriteria(criteria,filter.getStart(),filter.getLimit());
      }
      else
      {
         beans = criteria.list(); // ht.findByCriteria(criteria);
      }
      logger.debug(type.getSimpleName()+" founds = "+beans.size());

      return beans;


   }

   @SuppressWarnings("unchecked")
   public List<T> select(final String hql, final List<Object> values)
   {
      Session session = getSession();
      if (values.isEmpty())
      {
         return session.createQuery(hql).list();
      }
      else
      {
         Query query = session.createQuery(hql);
         int pos = 0;
         for (Object value : values) 
         {
            query.setParameter(pos++, value);
         }
         logger.debug(query.getQueryString());
         return query.list();
      }
   }


   /* (non-Javadoc)
    * @see fr.certu.chouette.dao.IDaoTemplate#getAll()
    */
   public List<T> getAll() 
   {
      logger.debug("invoke getAll on "+type.getSimpleName());
      // return getHibernateTemplate().loadAll(type); 
      // wrong call, may contains duplicate entry if join clause
      Filter f = Filter.getNewEmptyFilter();
      return select(f);
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.dao.IDaoTemplate#remove(java.lang.Long)
    */
   public void remove(Long id)
   {
      logger.debug("invoke remove on "+type.getSimpleName());
      getHibernateTemplate().delete( get( id));
      getHibernateTemplate().flush();
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.dao.IDaoTemplate#save(fr.certu.chouette.model.neptune.NeptuneObject)
    */
   public void save(T object)
   {
      logger.debug("invoke save on "+type.getSimpleName());

      try
      {
         getHibernateTemplate().saveOrUpdate( object);
      }
      catch(HibernateSystemException hse)
      {
         if ( hse.getCause()!=null && 
               hse.getCause() instanceof NonUniqueObjectException)
            getHibernateTemplate().merge( object);
         else throw hse;
      }	
      //		T existing = getByObjectId(object.getObjectId());
      //		if (existing == null)
      //		{
      //			getHibernateTemplate().saveOrUpdate( object);
      //		}
      //		else
      //		{
      //			object.setId(existing.getId());
      //			getHibernateTemplate().merge( object);
      //		}
      // getHibernateTemplate().flush();
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.dao.IDaoTemplate#update(fr.certu.chouette.model.neptune.NeptuneObject)
    */
   public void update(T object)
   {
      logger.debug("invoke update on "+type.getSimpleName());

      try
      {
         getHibernateTemplate().saveOrUpdate( object);
      }
      catch(HibernateSystemException hse)
      {
         if ( hse.getCause()!=null && 
               hse.getCause() instanceof NonUniqueObjectException)
            getHibernateTemplate().merge( object);
         else throw hse;
      }	
      // getHibernateTemplate().flush();
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.dao.IDaoTemplate#exists(java.lang.Long)
    */
   @Override
   public boolean exists(Long id)
   {
      try
      {
         return (get(id) != null);
      }
      catch (ObjectRetrievalFailureException e) 
      {
         return false;
      }

   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.dao.IDaoTemplate#exists(java.lang.String)
    */
   @Override
   public boolean exists(String objectId)
   {
      try
      {
         return (getByObjectId(objectId) != null);
      }
      catch (ObjectRetrievalFailureException e) 
      {
         return false;
      }
   }

   @Override
   public void removeAll(List<T> objects) 
   {
      logger.debug("invoke removeAll on "+type.getSimpleName());

      getHibernateTemplate().deleteAll(objects);
      getHibernateTemplate().flush();
   }

   @Override
   public int removeAll(Filter clause) 
   {
      logger.debug("invoke removeAll on "+type.getSimpleName());

      Session session = getSession();
      FilterToHibernateClauseTranslator translator = new FilterToHibernateClauseTranslator();
      String hql = translator.translateToHQLDelete(clause, getSessionFactory().getClassMetadata(type));
      logger.debug("hql = "+hql);
      return session.createQuery(hql).executeUpdate();
   }

   @Override
   public void saveOrUpdateAll(List<T> objects) 
   {
      logger.debug("invoke saveOrUpdateAll on "+type.getSimpleName());
      for (T object : objects) 
      {
         T existing = get(object.getId());
         if (existing != null)
         {
            logger.debug("update object :"+object.getId());
            getHibernateTemplate().evict(existing);
            object.setId(existing.getId());
         }
         else
         {
            logger.debug("save object :"+object.getId());
         }
      }
      getHibernateTemplate().saveOrUpdateAll(objects);
      getHibernateTemplate().flush();
   }

   @Override
   public void detach(List<T> beans)
   {
      for (T bean : beans)
      {
         if (getSession().contains(bean))
            getSession().evict(bean);
      }
     
   }
   
   
   @Override
   public int purge() 
   {
      throw new HibernateDaoRuntimeException(HibernateDaoExceptionCode.NOT_YET_IMPLEMENTED, "purge");
   }

   @Override
   public long count(Filter clause) 
   {
      if (clause == null) clause = Filter.getNewEmptyFilter();
      Session session = getSession();
      FilterToHibernateClauseTranslator translator = new FilterToHibernateClauseTranslator();
      String hql = translator.translateToHQLCount(clause, getSessionFactory().getClassMetadata(type));
      logger.debug("hql = "+hql);
      if (translator.getValues().isEmpty())
         return ((Long) session.createQuery(hql).uniqueResult()).longValue();
      else
      {
         Query query = session.createQuery(hql);
         int pos = 0;
         for (Object value : translator.getValues()) 
         {
            query.setParameter(pos++, value);
         }
         return ((Long) query.uniqueResult()).longValue();
      }
   }

   @Override
   public T getByObjectId(String objectId)
   {
      // TODO Auto-generated method stub
      return null;
   }
}
