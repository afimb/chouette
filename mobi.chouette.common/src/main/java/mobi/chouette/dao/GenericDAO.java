package mobi.chouette.dao;

import java.util.Collection;
import java.util.List;

import javax.ejb.Local;

@Local
public interface GenericDAO<T> {

	T find(Object id);

	T findByObjectId(String id);

	List<T> findByObjectId(Collection<String> objectIds);

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
