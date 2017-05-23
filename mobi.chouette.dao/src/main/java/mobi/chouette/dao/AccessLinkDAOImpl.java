package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.AccessLink;

@Stateless
public class AccessLinkDAOImpl extends GenericDAOImpl<AccessLink> implements AccessLinkDAO{

	public AccessLinkDAOImpl() {
		super(AccessLink.class);
	}

	@PersistenceContext(unitName = "public")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

}
