package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.StopPoint;

@Stateless
public class StopPointDAOImpl extends GenericDAOImpl<StopPoint> implements StopPointDAO{

	public StopPointDAOImpl() {
		super(StopPoint.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

}
