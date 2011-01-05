package fr.certu.chouette.dao.hibernate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.classic.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.hibernate3.HibernateSystemException;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import fr.certu.chouette.common.CodeIncident;
import fr.certu.chouette.common.ServiceException;
import fr.certu.chouette.dao.IDaoTemplate;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.filter.FilterOrder;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneObject;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;

public class HibernateDaoTemplate<T extends NeptuneObject> extends HibernateDaoSupport implements IDaoTemplate<T>
{
	private static final Logger logger = Logger.getLogger(HibernateDaoTemplate.class);

	private Class<T> type;

	private HibernateDaoTemplate(Class<T> type) 
	{
		this.type = type;
	}

	public static HibernateDaoTemplate<PTNetwork> createPTNetworkDao()
	{
		return new HibernateDaoTemplate<PTNetwork>( PTNetwork.class);
	}
	public static HibernateDaoTemplate<Line> createLineDao()
	{
		return new HibernateDaoTemplate<Line>( Line.class);
	}
	public static HibernateDaoTemplate<Company> createCompanyDao()
	{
		return new HibernateDaoTemplate<Company>( Company.class);
	}
	public static HibernateDaoTemplate<Route> createRouteDao()
	{
		return new HibernateDaoTemplate<Route>( Route.class);
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.dao.IDaoTemplate#get(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	public T get(Long id)
	{
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
	public List<T> select(final Filter filter) {
		DetachedCriteria criteria = DetachedCriteria.forClass(type);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		if (!filter.isEmpty())
		{
			logger.debug("build clause");
			FilterToHibernateClauseTranslator translator = new FilterToHibernateClauseTranslator();
			criteria.add(translator.translate(filter));
		}
		if (filter.getOrderList()!= null)
		{
			logger.debug("add order");
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
		HibernateTemplate ht = getHibernateTemplate();
		List<T> beans = null; 
		if (filter.getLimit() > 0)
		{
			logger.debug("call with limit");
			beans = ht.findByCriteria(criteria,1,filter.getLimit());
		}
		else
		{
			logger.debug("call without limit");
			beans = ht.findByCriteria(criteria);
		}
		logger.debug("beans founds = "+beans.size());
		return beans;


	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.dao.IDaoTemplate#getByObjectId(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public T getByObjectId( final String objectId)
	{
		if ( objectId==null || objectId.isEmpty()) return null;

		DetachedCriteria criteria = DetachedCriteria.forClass(type);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		criteria.add(Restrictions.eq("objectId", objectId));
		final List<T> list = getHibernateTemplate().findByCriteria(criteria);
		final int total = list.size();

		if ( total==0)
		{
			// TODO 
			throw new ObjectRetrievalFailureException( type, objectId);
		}
		else if ( total>1)
		{
			throw new ServiceException( CodeIncident.BASE_NON_INTEGRE, total + " tableaux de marche d'identifant " + objectId);
		}

		return list.get( 0);
	}	

	/* (non-Javadoc)
	 * @see fr.certu.chouette.dao.IDaoTemplate#getAll()
	 */
	@SuppressWarnings("unchecked")
	public List<T> getAll() 
	{
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
		getHibernateTemplate().delete( get( id));
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.dao.IDaoTemplate#save(fr.certu.chouette.model.neptune.NeptuneObject)
	 */
	public void save(T object)
	{
		getHibernateTemplate().save( object);
		getHibernateTemplate().flush();
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.dao.IDaoTemplate#update(fr.certu.chouette.model.neptune.NeptuneObject)
	 */
	public void update(T object)
	{
		try
		{
			getHibernateTemplate().saveOrUpdate( object);
		}
		catch(HibernateSystemException hse)
		{
			if ( hse.getCause()!=null && 
					hse.getCause() instanceof NonUniqueObjectException)
				getHibernateTemplate().merge( object);
		}	
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.dao.IDaoTemplate#exists(java.lang.Long)
	 */
	@Override
	public boolean exists(Long id)
	{

		return (get(id) != null);
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.dao.IDaoTemplate#exists(java.lang.String)
	 */
	@Override
	public boolean exists(String objectId)
	{

		return (getByObjectId(objectId) != null);
	}
}
