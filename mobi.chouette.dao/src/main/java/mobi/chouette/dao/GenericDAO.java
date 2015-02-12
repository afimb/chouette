package mobi.chouette.dao;

import java.util.Collection;
import java.util.List;

public interface GenericDAO<T> {

	T find(Object id);

	T findByObjectId(String id);

	List<T> findByObjectId(Collection<String> objectIds);

	List<T> findAll();

	List<T> find(String hql, List<Object> values);

	void create(T entity);

	T update(T entity);

	void delete(T entity);

	int deleteAll();

	void detach(T entity);

	void evictAll();

	List<T> load(Collection<T> list);

	void flush();

}
