package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.StopArea;

@Stateless
public class StopAreaDAOImpl extends GenericDAOImpl<StopArea> implements StopAreaDAO{
	public StopAreaDAOImpl() {
		super(StopArea.class);
	}

	@PersistenceContext(unitName = "public")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
}
