package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.StopArea;

@Stateless
public class StopAreaDAO extends GenericDAOImpl<StopArea> {
	public StopAreaDAO() {
		super(StopArea.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
}
