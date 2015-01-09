package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.PTNetwork;

@Stateless
public class PTNetworkDAO extends GenericDAOImpl<PTNetwork> {

	public PTNetworkDAO() {
		super(PTNetwork.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

}
