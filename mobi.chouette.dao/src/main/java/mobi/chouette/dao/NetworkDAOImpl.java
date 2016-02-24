package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.Network;

@Stateless
public class NetworkDAOImpl extends GenericDAOImpl<Network> implements NetworkDAO{

	public NetworkDAOImpl() {
		super(Network.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

}
