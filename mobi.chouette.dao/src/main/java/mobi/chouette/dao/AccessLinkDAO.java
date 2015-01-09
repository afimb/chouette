package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.AccessLink;

@Stateless
public class AccessLinkDAO extends GenericDAOImpl<AccessLink> {

	public AccessLinkDAO() {
		super(AccessLink.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

}
