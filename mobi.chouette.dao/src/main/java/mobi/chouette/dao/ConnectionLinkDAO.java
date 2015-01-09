package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.ConnectionLink;

@Stateless
public class ConnectionLinkDAO extends GenericDAOImpl<ConnectionLink> {

	public ConnectionLinkDAO() {
		super(ConnectionLink.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

}
