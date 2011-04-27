package fr.certu.chouette.dao.hibernate;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.hibernate3.HibernateSystemException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import fr.certu.chouette.dao.IDaoTemplate;
import fr.certu.chouette.dao.hibernate.exception.HibernateDaoExceptionCode;
import fr.certu.chouette.dao.hibernate.exception.HibernateDaoRuntimeException;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.filter.FilterOrder;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneObject;
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.TimeSlot;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;

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
	public static HibernateDaoTemplate<JourneyPattern> createJourneyPatternDao()
	{
		return new HibernateDaoTemplate<JourneyPattern>( JourneyPattern.class);
	}
	public static HibernateDaoTemplate<PTLink> createPTLinkDao()
	{
		return new HibernateDaoTemplate<PTLink>( PTLink.class);
	}
	public static HibernateDaoTemplate<StopPoint> createStopPointDao()
	{
		return new HibernateDaoTemplate<StopPoint>( StopPoint.class);
	}
	public static HibernateDaoTemplate<StopArea> createStopAreaDao()
	{
		return new HibernateDaoTemplate<StopArea>( StopArea.class);
	}
	public static HibernateDaoTemplate<ConnectionLink> createConnectionLinkDao()
	{
		return new HibernateDaoTemplate<ConnectionLink>( ConnectionLink.class);
	}
	public static HibernateDaoTemplate<VehicleJourney> createVehicleJourneyDao()
	{
		return new HibernateDaoTemplate<VehicleJourney>( VehicleJourney.class);
	}
	public static HibernateDaoTemplate<Timetable> createTimetableDao()
	{
		return new HibernateDaoTemplate<Timetable>( Timetable.class);
	}
	public static HibernateDaoTemplate<TimeSlot> createTimeSlotDao()
	{
		return new HibernateDaoTemplate<TimeSlot>( TimeSlot.class);
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
		
		
		Session session = getSession();
		
		Criteria criteria = session.createCriteria(type);
		
		// DetachedCriteria criteria = DetachedCriteria.forClass(type);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		if (!filter.isEmpty())
		{
			logger.debug("build clause");
			FilterToHibernateClauseTranslator translator = new FilterToHibernateClauseTranslator();
			criteria.add(translator.translate(filter,criteria,getSessionFactory().getClassMetadata(type)));
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
		// HibernateTemplate ht = getHibernateTemplate();
		List<T> beans = null; 
		if (filter.getLimit() > 0 || filter.getStart() > 0)
		{
			logger.debug("call with limit");
			criteria.setFirstResult(filter.getStart());
			criteria.setMaxResults(filter.getLimit());
			beans = criteria.list();// ht.findByCriteria(criteria,filter.getStart(),filter.getLimit());
		}
		else
		{
			logger.debug("call without limit");
			beans = criteria.list(); // ht.findByCriteria(criteria);
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
			throw new HibernateDaoRuntimeException( HibernateDaoExceptionCode.DATABASE_INTEGRITY, total + " "+type.getName()+" id =" + objectId);
		}

		return list.get( 0);
	}	

	/* (non-Javadoc)
	 * @see fr.certu.chouette.dao.IDaoTemplate#getAll()
	 */
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
		getHibernateTemplate().flush();
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
			else throw hse;
		}	
		getHibernateTemplate().flush();
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
}
