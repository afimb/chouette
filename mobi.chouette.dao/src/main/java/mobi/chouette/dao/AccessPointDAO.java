package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.AccessPoint;

@Stateless
public class AccessPointDAO extends GenericDAOImpl<AccessPoint> {

	public AccessPointDAO() {
		super(AccessPoint.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

}
