package mobi.chouette.dao;

import java.util.Collection;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import mobi.chouette.model.Line;

@Stateless (name="LineDAO")
public class LineDAOImpl extends GenericDAOImpl<Line> implements LineDAO {

	private EntityManager deleteEntityManager;
	
	public LineDAOImpl() {
		super(Line.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
	
	@PersistenceContext(unitName = "referential_delete")
	public void setDeleteEntityManager(EntityManager em) {
		this.deleteEntityManager = em;
	}
	
	public void deleteByObjectId(Collection<String> objectIds) {
		if(objectIds == null || objectIds.size() == 0) {
			return;
		}
		Query deleteQuery = deleteEntityManager.createQuery("DELETE FROM Line where objectId IN :objectIds");
		deleteQuery.setParameter("objectIds", objectIds);
		deleteQuery.executeUpdate();
	}

}
