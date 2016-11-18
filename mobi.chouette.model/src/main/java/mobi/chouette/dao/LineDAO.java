package mobi.chouette.dao;

import java.util.Collection;

import mobi.chouette.model.Line;

public interface LineDAO extends GenericDAO<Line> {

	void deleteByObjectId(Collection<String> objectIds);

}
