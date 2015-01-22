package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.StopPoint;

@Stateless
public class StopPointDAO extends GenericDAOImpl<StopPoint> {

	public StopPointDAO() {
		super(StopPoint.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

}
