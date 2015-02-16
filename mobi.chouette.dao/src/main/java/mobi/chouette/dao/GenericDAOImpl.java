package mobi.chouette.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.NeptuneIdentifiedObject;

import org.hibernate.Session;
import org.hibernate.jpa.HibernateEntityManager;

@Log4j
public abstract class GenericDAOImpl<T> implements GenericDAO<T> {

	protected EntityManager em;

	protected Class<T> type;

	public GenericDAOImpl(Class<T> type) {
		this.type = type;
	}

	@Override
	public T find(final Object id) {
		return em.find(type, id);
	}

	@Override
	public List<T> find(final String hql, final List<Object> values) {
		List<T> result = null;
		if (values.isEmpty()) {
			TypedQuery<T> query = em.createQuery(hql, type);
			result = query.getResultList();
		} else {
			TypedQuery<T> query = em.createQuery(hql, type);
			int pos = 0;
			for (Object value : values) {
				query.setParameter(pos++, value);
			}
			result = query.getResultList();
		}
		return result;
	}

	@Override
	public List<T> findAll() {
		List<T> result = null;
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> criteria = builder.createQuery(type);
		Root<T> root = criteria.from(type);
		TypedQuery<T> query = em.createQuery(criteria);
		result = query.getResultList();
		return result;
	}

	@Override
	public T findByObjectId(final String objectId) {
		Session session = em.unwrap(Session.class);		
		T result = (T) session.bySimpleNaturalId(type).load(objectId);

		return result;
	}

	// public T findByObjectId(final String objectId) {
	// List<T> result = null;
	// CriteriaBuilder builder = em.getCriteriaBuilder();
	// CriteriaQuery<T> criteria = builder.createQuery(type);
	// Root<T> root = criteria.from(type);
	// Predicate predicate = builder.equal(root.get("objectId"), objectId);
	// criteria.where(predicate);
	// TypedQuery<T> query = em.createQuery(criteria);
	// result = query.getResultList();
	// return (result.size() == 0) ? null : result.get(0);
	// }

	@Override
	public List<T> load(Collection<T> list) {
		if (list.isEmpty()) {
			return (List<T>) list;
		} else {
			final Collection<String> objectIds = new ArrayList<String>();
			for (T o : list) {
				if (o instanceof NeptuneIdentifiedObject) {
					objectIds.add(((NeptuneIdentifiedObject) o).getObjectId());
				}
			}
			return findByObjectId(objectIds);
		}
	}

	@Override
	public List<T> findByObjectId(final Collection<String> objectIds) {
		List<T> result = null;
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> criteria = builder.createQuery(type);
		Root<T> root = criteria.from(type);
		Predicate predicate = builder.in(root.get("objectId")).value(objectIds);
		criteria.where(predicate);
		TypedQuery<T> query = em.createQuery(criteria);
		result = query.getResultList();
		return result;
	}

	@Override
	public void create(final T entity) {
		em.persist(entity);
	}

	@Override
	public T update(final T entity) {
		return em.merge(entity);
	}

	@Override
	public void delete(final T entity) {
		em.remove(entity);
	}

	@Override
	public void detach(final T entity) {
		em.detach(entity);
	}

	@Override
	public int deleteAll() {
		int result = 0;
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaDelete<T> criteria = builder.createCriteriaDelete(type);
		Root<T> root = criteria.from(type);
		Query query = em.createQuery(criteria);
		result = query.executeUpdate();
		return result;
	}

	@Override
	public void evictAll() {
		EntityManagerFactory factory = em.getEntityManagerFactory();
		Cache cache = factory.getCache();
		cache.evictAll();
	}

	@Override
	public void flush() {
//		Session session = em.unwrap(Session.class);	
//		session.flush();
		em.flush();
	}
}
