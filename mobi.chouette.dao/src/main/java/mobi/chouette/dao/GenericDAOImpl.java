package mobi.chouette.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import com.google.common.collect.Iterables;

public abstract class GenericDAOImpl<T> implements GenericDAO<T> {

	protected EntityManager em;

	protected Class<T> type;

	public GenericDAOImpl(Class<T> type) {
		this.type = type;
	}

	
	public T find(final Object id) {
		return em.find(type, id);
	}

	
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

	
	public List<T> findAll(final Collection<Long> ids) {
		if (ids == null || ids.size() == 0){
			return Collections.emptyList();
		}
		List<T> result = null;
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> criteria = builder.createQuery(type);
		Root<T> root = criteria.from(type);
		Predicate predicate = builder.in(root.get("id")).value(ids);
		criteria.where(predicate);
		TypedQuery<T> query = em.createQuery(criteria);
		result = query.getResultList();
		return result;
	}

	
	public List<T> findAll() {
		List<T> result = null;
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> criteria = builder.createQuery(type);
		criteria.from(type);
		TypedQuery<T> query = em.createQuery(criteria);
		result = query.getResultList();
		return result;
	}

	@SuppressWarnings("unchecked")
	
	public T findByObjectId(final String objectId) {
		Session session = em.unwrap(Session.class);
		T result = (T) session.bySimpleNaturalId(type).load(objectId);

		return result;
	}

	
	public List<T> findByObjectId(final Collection<String> objectIds) {
		// System.out.println("GenericDAOImpl.findByObjectId() : " + objectIds);
		List<T> result = null;
		if (objectIds.isEmpty())
			return result;

		Iterable<List<String>> iterator = Iterables.partition(objectIds, 32000);
		for (List<String> ids : iterator) {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<T> criteria = builder.createQuery(type);
			Root<T> root = criteria.from(type);
			Predicate predicate = builder.in(root.get("objectId")).value(ids);
			criteria.where(predicate);
			TypedQuery<T> query = em.createQuery(criteria);
			if (result == null)
				result = query.getResultList();
			else
				result.addAll(query.getResultList());
		}
		return result;
	}

	
	public void create(final T entity) {
		em.persist(entity);
	}

	
	public T update(final T entity) {
		return em.merge(entity);
	}

	
	public void delete(final T entity) {
		em.remove(entity);
	}

	
	public void detach(final T entity) {
		em.detach(entity);
	}

	
	public int deleteAll() {
		int result = 0;
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaDelete<T> criteria = builder.createCriteriaDelete(type);
		criteria.from(type);
		Query query = em.createQuery(criteria);
		result = query.executeUpdate();
		return result;
	}

	
	public int truncate() {
		String query = new StringBuilder("TRUNCATE TABLE ").append(type.getAnnotation(Table.class).name())
				.append(" CASCADE").toString();
		return em.createNativeQuery(query).executeUpdate();
	}

	
	public void evictAll() {
		EntityManagerFactory factory = em.getEntityManagerFactory();
		Cache cache = factory.getCache();
		cache.evictAll();
	}

	
	public void flush() {
		em.flush();
	}

	@Override
	public void clear() {
		em.clear();
	}

	@Override
	public void detach(Collection<?> list) {
		for (Object object : list) {
			em.detach(object);
		}
	}

	
}
