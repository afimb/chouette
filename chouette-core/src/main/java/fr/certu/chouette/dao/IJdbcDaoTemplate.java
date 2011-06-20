package fr.certu.chouette.dao;

import java.util.List;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.model.neptune.NeptuneObject;

/**
 * 
 * @author mamadou keira
 *
 * @param <T>
 */
public interface IJdbcDaoTemplate<T extends NeptuneObject>
{
	 List<T> getAll();
	 void removeAll(List<T> objects) throws  ChouetteException;
	 void saveOrUpdateAll(List<T> objects) throws  ChouetteException;
}
