package fr.certu.chouette.dao;

import java.util.List;

import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.NeptuneObject;

public interface IDaoTemplate <T extends NeptuneObject> 
{	
	   List<T> getAll();
	   T get(Long id);
	   void save(T object);
	   void remove(Long id);
//	   int removeAll(Filter clause);
	   void update(T object);
	   T getByObjectId( String objectId);
	   List<T> select(Filter clause);
	   boolean exists(Long id);
	   boolean exists(String objectId);
}
