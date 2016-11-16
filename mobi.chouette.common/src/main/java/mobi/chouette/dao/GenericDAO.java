package mobi.chouette.dao;

import java.util.Collection;
import java.util.List;

import javax.ejb.Local;

import mobi.chouette.common.Pair;

@Local
public interface GenericDAO<T> {

	T find(Object id);

	T findByChouetteId(String codeSpace, String objectId);

	List<T> findByChouetteId(String codeSpace, Collection<String> objectIds);

	List<T> findAll();

	List<T> findAll(Collection<Long> ids);

	List<T> find(String hql, List<Object> values);

	void create(T entity);

	T update(T entity);

	void delete(T entity);

	int deleteAll();

	int truncate();

	void detach(T entity);

	void evictAll();

	void flush();
	
	void clear();

	void detach(Collection<?> list);
	

}
