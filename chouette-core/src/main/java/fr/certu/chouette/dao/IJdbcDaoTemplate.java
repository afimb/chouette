package fr.certu.chouette.dao;

import java.util.List;

import fr.certu.chouette.model.neptune.NeptuneObject;
import fr.certu.chouette.model.neptune.PeerId;

/**
 * 
 * @author mamadou keira
 *
 * @param <T>
 */
public interface IJdbcDaoTemplate<T extends NeptuneObject> extends IDaoTemplate<T> 
{
	List<PeerId> get(List<String> objectids);
}
