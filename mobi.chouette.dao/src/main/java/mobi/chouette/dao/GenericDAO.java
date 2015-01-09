package mobi.chouette.dao;

import java.util.List;

public interface GenericDAO<T> {

	T find(Object id);

	T findByObjectId(String id);

	List<T> findAll();

	List<T> find(String hql, List<Object> values);

	void create(T entity);

	T update(T entity);

	void delete(T entity);

	int deleteAll();

	void detach(T entity);

}
