package fr.certu.chouette.dao;

import java.util.List;

import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.NeptuneObject;

/**
 * interface for database access tool
 * <br/> a CRUD oriented database access must implement every methods except purge
 * <br/> a mass oriented database access must implement purge, saveOrUpdateAll and getAll
 * 
 * @author michel
 *
 * @param <T> NeptuneObject to access
 */
public interface IDaoTemplate <T extends NeptuneObject> 
{	
	/**
	 * return every objects of T type
	 * @return a list of objects
	 */
	List<T> getAll();
	/**
	 * return object with a specified database Id
	 * @param id database required id
	 * @return object if found
	 */
	T get(Long id);
	/**
	 * save object to database 
	 * 
	 * @param object object to save
	 */
	void save(T object);
	/**
	 * remove object from database 
	 * 
	 * @param id database required id
	 */
	void remove(Long id);
	/**
	 * remove every objects from a given list
	 * @param objects list of objects to be deleted
	 */
	void removeAll(List<T> objects);
	/**
	 * remove objects based on a request clause  
	 * @param clause clause for objects to be deleted
	 * @return
	 */
	int removeAll(Filter clause);
	/**
	 * update an object (
	 * @param object
	 */
	void update(T object);
	/**
	 * return object with a specified Neptune ObjectId
	 * @param objectId required objectId
	 * @return
	 */
	T getByObjectId( String objectId);
	/**
	 * return objects based on a request clause
	 * @param clause clause for objects to be returned
	 * @return
	 */
	List<T> select(Filter clause);
	/**
	 * check if an object exists in database
	 * 
	 * @param id checked id
	 * @return true if exists, false otherwise
	 */
	boolean exists(Long id);
	/**
	 * check if an object exists in database
	 * @param objectId checked ObjectId
	 * @return true if exists, false otherwise
	 */
	boolean exists(String objectId);
	/**
	 * save or update a list of objects
	 * @param objects objects to be saved or updates
	 */
	void saveOrUpdateAll(List<T> objects);
	/**
	 * purge unused or empty objects
	 * 
	 * @return purge effective count
	 */
	int purge();
	
	/**
	 * count objects saved in database
	 * 
	 * @return object count
	 */
	long count(Filter clause);
	
	/**
	 * detach objects from dao cache
	 * @param beans
	 */
	void detach(List<T> beans);
	
}
