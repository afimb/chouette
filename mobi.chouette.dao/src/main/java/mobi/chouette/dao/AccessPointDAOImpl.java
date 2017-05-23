package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.AccessPoint;

@Stateless
public class AccessPointDAOImpl extends GenericDAOImpl<AccessPoint> implements AccessPointDAO{

	public AccessPointDAOImpl() {
		super(AccessPoint.class);
	}

	@PersistenceContext(unitName = "public")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

}
